"""Mishu desktop host with transactional SQLite snapshots.
Install: pip install pywebview
Run: python windows/Mishu.py
"""
import json
import sqlite3
import threading
from pathlib import Path
import os
import sys
import webview
import tempfile
from playwright.sync_api import sync_playwright


APP_DIR = Path(os.environ.get('LOCALAPPDATA') or (Path.home() / '.local' / 'share')) / 'Mishu'
APP_DIR.mkdir(parents=True, exist_ok=True)
DB_PATH = APP_DIR / 'mishu.sqlite'
lock = threading.RLock()

class API:
    def __init__(self):
        with self.connect() as conn:
            conn.execute('CREATE TABLE IF NOT EXISTS snapshots (seq INTEGER PRIMARY KEY, payload TEXT NOT NULL, created TEXT DEFAULT CURRENT_TIMESTAMP)')

    def destination(self, name, extension):
        result = window.create_file_dialog(webview.FileDialog.SAVE, save_filename=name, file_types=(f'{extension.upper()} (*.{extension})',))
        if not result:
            return None
        path = Path(result if isinstance(result, str) else result[0])
        return path if path.suffix.lower() == '.' + extension else path.with_suffix('.' + extension)

    def save_json(self, payload, name):
        try:
            json.loads(payload)
            path = self.destination(name, 'json')
            if path is None: return {'cancelled': True}
            path.write_text(payload, encoding='utf-8')
            return {'ok': True}
        except Exception as e:
            return {'error': str(e)}

    def save_pdf(self, html, name, width, height):
        try:
            path = self.destination(name + '.pdf', 'pdf')
            if path is None: return {'cancelled': True}
            with sync_playwright() as pw:
                browser = pw.chromium.launch(channel='msedge', headless=True)
                try:
                    page = browser.new_page()
                    page.set_content(html, wait_until='load', timeout=30000)
                    page.evaluate('Promise.race([document.fonts.ready, new Promise(r=>setTimeout(r,3000))])')
                    data = page.pdf(width=f'{float(width)}mm', height=f'{float(height)}mm', prefer_css_page_size=True, print_background=True)
                    if not data.startswith(b'%PDF-'): raise ValueError('Invalid PDF')
                    path.write_bytes(data)
                finally:
                    browser.close()
            return {'ok': True}
        except Exception as e:
            return {'error': str(e)}

    def connect(self):
        conn = sqlite3.connect(DB_PATH, timeout=15)
        conn.execute('PRAGMA journal_mode=WAL')
        conn.execute('PRAGMA synchronous=FULL')
        return conn

    @staticmethod
    def valid(value):
        if not isinstance(value, dict) or value.get('schema') != 1 or not isinstance(value.get('seq'), int) or value['seq'] < 0:
            return False
        return all(isinstance(value.get(k), list) for k in ('collections','folders','notes','trash','templates'))

    def save(self, payload):
        value = json.loads(payload)
        if not self.valid(value):
            raise ValueError('Invalid Mishu snapshot')
        with lock, self.connect() as conn:
            conn.execute('INSERT OR REPLACE INTO snapshots (seq,payload) VALUES (?,?)',(value['seq'],payload))
            conn.execute('DELETE FROM snapshots WHERE seq NOT IN (SELECT seq FROM snapshots ORDER BY seq DESC LIMIT 12)')
        return True

    def restore(self):
        with lock, self.connect() as conn:
            rows = conn.execute('SELECT payload FROM snapshots ORDER BY seq DESC LIMIT 12').fetchall()
        for (payload,) in rows:
            try:
                if self.valid(json.loads(payload)):
                    return payload
            except (ValueError, TypeError):
                continue
        return None

if __name__ == '__main__':
    api = API()
    bundle_root = Path(getattr(sys, '_MEIPASS', Path(__file__).resolve().parents[1]))
    html = bundle_root / 'app' / 'src' / 'main' / 'assets' / 'index.html'
    window = webview.create_window('Mishu 🐱', html.as_uri(), js_api=api, width=1100, height=780)
    def open_arg():
        if len(sys.argv) < 2:
            return
        file = Path(sys.argv[1])
        if file.suffix.lower() != '.json' or not file.is_file() or file.stat().st_size > 20*1024*1024:
            return
        payload = file.read_text(encoding='utf-8')
        window.evaluate_js('window.mishuOpenExternal(' + json.dumps(payload, ensure_ascii=True) + ')')
    window.events.loaded += open_arg
    webview.start(gui="edgechromium", private_mode=False, storage_path=str(APP_DIR / "webview"))
