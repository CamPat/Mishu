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
import base64
import hashlib
import secrets
import urllib.parse
import urllib.request
import http.server
import socketserver
import concurrent.futures
import webbrowser


APP_DIR = Path(os.environ.get('LOCALAPPDATA') or (Path.home() / '.local' / 'share')) / 'Mishu'
APP_DIR.mkdir(parents=True, exist_ok=True)
DB_PATH = APP_DIR / 'mishu.sqlite'
lock = threading.RLock()

class API:
    def __init__(self):
        with self.connect() as conn:
            conn.execute('CREATE TABLE IF NOT EXISTS snapshots (seq INTEGER PRIMARY KEY, payload TEXT NOT NULL, created TEXT DEFAULT CURRENT_TIMESTAMP)')

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

    def google_login(self, client_id):
        if not isinstance(client_id,str) or not client_id.endswith('.apps.googleusercontent.com'):
            raise ValueError('Configure a Google Desktop OAuth client ID')
        # Google installed-app authorization: system browser + loopback redirect + PKCE.
        verifier=secrets.token_urlsafe(64)
        challenge=base64.urlsafe_b64encode(hashlib.sha256(verifier.encode()).digest()).rstrip(b'=').decode()
        state=secrets.token_urlsafe(24)
        future=concurrent.futures.Future()
        class Callback(http.server.BaseHTTPRequestHandler):
            def do_GET(self):
                query=urllib.parse.parse_qs(urllib.parse.urlsplit(self.path).query)
                if query.get('state',[''])[0]!=state:future.set_exception(ValueError('OAuth state mismatch'))
                elif 'error' in query:future.set_exception(ValueError(query['error'][0]))
                elif 'code' in query:future.set_result(query['code'][0])
                else:future.set_exception(ValueError('Missing OAuth code'))
                self.send_response(200);self.send_header('Content-Type','text/html; charset=utf-8');self.end_headers()
                self.wfile.write(b'<html><body>Google authorization finished. Return to Mishu.</body></html>')
            def log_message(self,*args):pass
        with socketserver.TCPServer(('127.0.0.1',0),Callback) as server:
            server.timeout=1
            redirect=f'http://127.0.0.1:{server.server_address[1]}/'
            params={'client_id':client_id,'redirect_uri':redirect,'response_type':'code','scope':'https://www.googleapis.com/auth/drive.file','code_challenge':challenge,'code_challenge_method':'S256','state':state,'access_type':'online'}
            webbrowser.open('https://accounts.google.com/o/oauth2/v2/auth?'+urllib.parse.urlencode(params))
            import time
            deadline=time.monotonic()+180
            while not future.done() and time.monotonic()<deadline:server.handle_request()
            if not future.done():raise TimeoutError('Google sign-in timed out')
            code=future.result()
        data=urllib.parse.urlencode({'client_id':client_id,'code':code,'code_verifier':verifier,'redirect_uri':redirect,'grant_type':'authorization_code'}).encode()
        req=urllib.request.Request('https://oauth2.googleapis.com/token',data=data,headers={'Content-Type':'application/x-www-form-urlencoded'})
        with urllib.request.urlopen(req,timeout=20) as response:result=json.load(response)
        return result['access_token']

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
    webview.start(private_mode=False)
