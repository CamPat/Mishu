# Mishu 4.15 — Device-to-device pairing (English / Español)

## Important / Importante
A four-digit code **cannot transfer data across the internet on its own**. A temporary relay service is necessary for web, Android, and Windows on different networks. This implementation uses a Cloudflare Worker + KV as a **temporary connection server**, NOT Google Drive/Dropbox/GitHub storage. You must deploy it once and enter its HTTPS URL in Mishu on both devices. There is no OAuth client ID or user account login inside Mishu.

Un código de cuatro dígitos **no puede transmitir información por Internet por sí solo**. Se necesita un servidor temporal. El proyecto incluye un Cloudflare Worker con KV que actúa como intermediario, **no como almacenamiento permanente**. Debes publicarlo una vez y pegar su URL HTTPS en Mishu en ambos dispositivos. No necesitas configurar OAuth de Google.

## Deployment / Publicación
1. Create a Cloudflare account and install Node.js locally.
2. Open `sync-relay/` and run `npx wrangler login`.
3. Run `npx wrangler kv namespace create PAIRS` and copy the namespace ID into `sync-relay/wrangler.toml`.
4. Run `npx wrangler deploy` inside `sync-relay/`.
5. Copy the HTTPS `*.workers.dev` URL into the Mishu synchronization dialog on **both** devices.
6. Open Mishu on device A, select **Sync another device from this one**, and note the four-digit code.
7. On device B, select **Sync this device from another**, enter the code.
8. Approve the request on A; inspect the before/after counts on B; confirm replacement on B.

## Security and limitations / Seguridad y limitaciones
- The code expires after **10 minutes**, and the sender must approve the request.
- The sender must remain online and keep the synchronization dialog open.
- This is **one-way full library replacement**, not a merge, and is NOT automatic sync.
- A local recovery backup is created on the receiver before replacement. Export a manual JSON backup as well.
- The relay **temporarily holds the library in readable JSON**. Do not deploy it under a shared/untrusted account for sensitive notes. Cloudflare KV can retain data beyond TTL in internal infrastructure; this is not end-to-end encrypted.
- Four-digit codes have only 10,000 combinations. Sender approval mitigates but does not eliminate abuse. Do not approve unexpected requests. For stronger security, a longer pairing code and end-to-end encryption should be added before production use.
- KV has eventual consistency and does not provide atomic code reservation. Simultaneous requests and code collisions are possible under heavy use. This is a prototype and requires live security and cross-platform testing.
- Worker request size is limited to ~3.5MB of JSON. Very large libraries will need a chunked transfer design.
- The application no longer asks for Google OAuth credentials. **Deploying a relay service is still required.**
- Do not treat this prototype as a verified production-ready synchronization system.
