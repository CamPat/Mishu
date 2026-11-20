// Mishu temporary relay. Cloudflare KV stores snapshots for at most ten minutes.
// Source device approval is required. Not intended as permanent backup storage.
const json=(body,status=200)=>new Response(JSON.stringify(body),{status,headers:{'Content-Type':'application/json','Access-Control-Allow-Origin':'*','Cache-Control':'no-store'}});
const fail=(error,status=400)=>json({ok:false,error},status);
const randomCode=()=>{const a=new Uint32Array(1);crypto.getRandomValues(a);return String(a[0]%10000).padStart(4,'0')};
const validCode=s=>typeof s==='string'&&/^\d{4}$/.test(s);
const safeToken=s=>typeof s==='string'&&/^[a-z0-9-]{20,100}$/i.test(s);
const key=c=>'pair:'+c;
async function read(env,code){return JSON.parse(await env.PAIRS.get(key(code))||'null')}
async function write(env,code,record){await env.PAIRS.put(key(code),JSON.stringify(record),{expirationTtl:600})}
export default {async fetch(request,env){
 if(request.method==='OPTIONS')return new Response(null,{headers:{'Access-Control-Allow-Origin':'*','Access-Control-Allow-Methods':'POST, OPTIONS','Access-Control-Allow-Headers':'Content-Type','Access-Control-Max-Age':'86400'}});
 if(request.method!=='POST')return fail('Method not allowed',405);
 let body;try{if(Number(request.headers.get('content-length')||0)>4000000)return fail('Library too large',413);body=await request.json()}catch{return fail('Invalid JSON')}
 const path=new URL(request.url).pathname;
 try{
 if(path==='/create'){
  if(!safeToken(body.secret)||!body.data||!Array.isArray(body.data.notes))return fail('Invalid request');
  const raw=JSON.stringify(body.data);if(raw.length>3500000)return fail('Library too large for relay',413);
  for(let i=0;i<20;i++){const code=randomCode();if(await read(env,code))continue;
   await write(env,code,{secret:body.secret,data:body.data,status:'waiting',created:Date.now(),attempts:0});return json({ok:true,code})}
  return fail('No free codes. Try again.',503)
 }
 if(!validCode(body.code))return fail('Invalid code');
 const rec=await read(env,body.code);if(!rec||Date.now()-rec.created>600000)return fail('Code expired or not found',404);
 if(path==='/sender-status'){
  if(body.secret!==rec.secret)return fail('Not authorized',403);
  return json({ok:true,status:rec.status,requestId:rec.requestId||null})
 }
 if(path==='/request'){
  if(!safeToken(body.token))return fail('Invalid token');
  if(rec.status!=='waiting')return fail('Code already used or pending',409);
  // Limit request attempts per code. Only the source can authorize a transfer.
  if(rec.attempts>=3)return fail('Too many attempts',429);
  rec.attempts++;rec.token=body.token;rec.requestId=crypto.randomUUID();rec.status='requested';await write(env,body.code,rec);
  return json({ok:true,status:'requested'})
 }
 if(path==='/approve'||path==='/reject'){
  if(body.secret!==rec.secret||body.requestId!==rec.requestId||rec.status!=='requested')return fail('Not authorized',403);
  rec.status=path==='/approve'?'approved':'rejected';await write(env,body.code,rec);return json({ok:true})
 }
 if(path==='/receive'){
  if(body.token!==rec.token||!safeToken(body.token))return fail('Not authorized',403);
  if(rec.status==='approved')return json({ok:true,status:'approved',data:rec.data});
  return json({ok:true,status:rec.status})
 }
 if(path==='/complete'){
  if(body.token!==rec.token||rec.status!=='approved')return fail('Not authorized',403);
  rec.status='complete';delete rec.data;await write(env,body.code,rec);return json({ok:true})
 }
 return fail('Unknown endpoint',404)
 }catch(e){return fail('Server error: '+e.message,500)}
 }};
