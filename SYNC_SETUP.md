# Mishu 4.16 — Publisher-only synchronization setup / Configuración exclusiva del desarrollador

**End users do not configure any URL, account, OAuth client, or relay.** The sync dialog contains only the two transfer choices.

**Important:** No Cloudflare Worker has been deployed by this package. A working relay URL cannot be fabricated. Until the publisher completes the steps below, the app displays a localized *sync not enabled* error. Do not claim synchronization is operational before testing.

## One-time publisher setup

1. Create a Cloudflare account. In Cloudflare, create a Workers KV namespace named `PAIRS` (or run `npx wrangler kv namespace create PAIRS`).
2. In the GitHub repository Settings → Secrets and variables → Actions, add secrets `CLOUDFLARE_API_TOKEN` (Workers/KV deployment permissions) and `CLOUDFLARE_ACCOUNT_ID`; add variable `MISHU_KV_NAMESPACE_ID` with the KV namespace ID.
3. Run the **Deploy Mishu Sync Relay** workflow. It deploys `sync-relay/worker.js` and prints the actual HTTPS Worker URL in the deployment logs.
4. Add GitHub Actions repository variable `MISHU_SYNC_URL` set to that exact HTTPS Worker URL (e.g. `https://mishu-device-sync.<your-subdomain>.workers.dev`).
5. Re-run **Publish Mishu Web**, **Build Mishu Android APK**, and **Build Mishu Windows EXE**. Each build automatically embeds the same endpoint. No user setup is needed.
6. Test on two devices, including sender approval, receiver preview, confirmation, recovery and error cases. Keep the sender window open.

## Safety / Seguridad

This remains a **prototype**, not verified production sync. The relay temporarily stores readable library JSON; there is **no end-to-end encryption**. Four-digit codes are guessable; approval is mandatory but not a substitute for cryptographic security. Do not transfer sensitive data. Worker uses Cloudflare KV eventual consistency; transfers can fail or race. The receiver replaces the full library, not merges it. Export a separate JSON backup first.
