Mishu 4.17 — Direct WebRTC transfer (no cloud)

1. Open Mishu on both devices.
2. On sender: Sync > Link another device. Copy invitation.
3. On receiver: Sync > Link this device. Paste invitation, generate response.
4. Copy response back to sender, press Continue and paste response.
5. When linked, sender chooses Send my library.
6. Receiver reviews counts and confirms full replacement.
7. Unlink on either device or close the application to end session.

Limitations: GitHub Pages is static; without signaling infrastructure manual exchange is necessary. WebRTC can fail behind restrictive NAT/firewalls; WebView/pywebview WebRTC support must be tested. No persistent linking, automatic sync, remote storage or four-digit pairing. STUN server is used only for connectivity discovery, not library storage. Do not transfer sensitive information before independent security review.
