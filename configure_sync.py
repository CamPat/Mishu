#!/usr/bin/env python3
"""Embed the publisher-managed relay URL into Mishu builds."""
import os, pathlib, sys
url=os.environ.get('MISHU_SYNC_URL','').strip().rstrip('/')
if url and (not url.startswith('https://') or any(c in url for c in "'\"<> \n\r")):
    sys.exit('Invalid MISHU_SYNC_URL: expected HTTPS URL without spaces or quotes')
root=pathlib.Path(__file__).resolve().parent
for relative in ('web/index.html','app/src/main/assets/index.html'):
    p=root/relative
    s=p.read_text(encoding='utf-8')
    s=s.replace('__MISHU_SYNC_RELAY_URL__',url or '__MISHU_SYNC_RELAY_URL__')
    p.write_text(s,encoding='utf-8')
print('Relay embedded in web and native HTML' if url else 'Relay not configured; pairing will display a publisher setup message')
