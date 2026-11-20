package com.mishu.app;

import android.app.Activity;
import android.app.PendingIntent;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;
import android.util.Log;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.PermissionRequest;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.content.Intent;
import android.net.Uri;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.DocumentsContract;
import android.util.Log;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class MainActivity extends Activity {
 private WebView web;
 private Storage storage;
 private ValueCallback<Uri[]> fileCallback;
 private String pendingExternalFile=null;
 private boolean pageReady=false;
 private static final int PICK_FILE=31;
 private static class Storage extends SQLiteOpenHelper {
  Storage(Activity activity){super(activity,"mishu.sqlite",null,1);}
  @Override public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE IF NOT EXISTS snapshots(seq INTEGER PRIMARY KEY, payload TEXT NOT NULL, created INTEGER NOT NULL)");}
  @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){}
  synchronized String restore(){
   try(SQLiteDatabase db=getReadableDatabase();Cursor c=db.rawQuery("SELECT payload FROM snapshots ORDER BY seq DESC LIMIT 10",null)){
    while(c.moveToNext()){String s=c.getString(0);if(isValid(s))return s;}
   }catch(Exception e){Log.e("Mishu","Restore error",e);}return null;
  }
  synchronized boolean save(String json){if(!isValid(json))return false;
   try{JSONObject o=new JSONObject(json);long seq=o.getLong("seq");SQLiteDatabase db=getWritableDatabase();db.beginTransaction();try{
    ContentValues values=new ContentValues();values.put("seq",seq);values.put("payload",json);values.put("created",System.currentTimeMillis());
    db.insertWithOnConflict("snapshots",null,values,SQLiteDatabase.CONFLICT_REPLACE);
    db.execSQL("DELETE FROM snapshots WHERE seq NOT IN (SELECT seq FROM snapshots ORDER BY seq DESC LIMIT 12)");
    db.setTransactionSuccessful();return true;
   }finally{db.endTransaction();}}
   catch(Exception e){Log.e("Mishu","Save failed",e);return false;}
  }
  static boolean isValid(String s){try{JSONObject o=new JSONObject(s);if(o.optInt("schema")!=1||o.optLong("seq",-1)<0)return false;
   for(String k:new String[]{"collections","folders","notes","trash","templates"})if(o.optJSONArray(k)==null)return false;
   return true;}catch(Exception e){return false;}}
 }
 private AuthorizationService authService;
 private void googleLogin(){
  String clientId=BuildConfig.GOOGLE_OAUTH_CLIENT_ID;
  if(clientId.isEmpty()){web.evaluateJavascript("window.mishuGoogleError('Google OAuth Android client ID not configured')",null);return;}
  AuthorizationServiceConfiguration service=new AuthorizationServiceConfiguration(
    Uri.parse("https://accounts.google.com/o/oauth2/v2/auth"),Uri.parse("https://oauth2.googleapis.com/token"));
  AuthorizationRequest req=new AuthorizationRequest.Builder(service,clientId,ResponseTypeValues.CODE,
    Uri.parse(BuildConfig.GOOGLE_REDIRECT_URI))
    .setScope("https://www.googleapis.com/auth/drive.file")
    .build();
  Intent callback=new Intent(this,MainActivity.class).setAction("com.mishu.app.GOOGLE_OAUTH");
  callback.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
  PendingIntent pending=PendingIntent.getActivity(this,31415,callback,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
  authService.performAuthorizationRequest(req,pending);
 }
 private void completeGoogleLogin(Intent intent){
  AuthorizationResponse response=AuthorizationResponse.fromIntent(intent);
  AuthorizationException exception=AuthorizationException.fromIntent(intent);
  if(response==null){String error=exception==null?"Google sign-in cancelled":exception.getMessage();web.evaluateJavascript("window.mishuGoogleError("+JSONObject.quote(error)+")",null);return;}
  authService.performTokenRequest(response.createTokenExchangeRequest(),(tokenResponse,tokenException)->runOnUiThread(()->{
   if(tokenResponse!=null&&tokenResponse.accessToken!=null){
    web.evaluateJavascript("window.mishuGoogleToken("+JSONObject.quote(tokenResponse.accessToken)+")",null);
   }else{String error=tokenException==null?"Token exchange failed":tokenException.getMessage();web.evaluateJavascript("window.mishuGoogleError("+JSONObject.quote(error)+")",null);}
  }));
 }
 @Override public void onCreate(Bundle state){super.onCreate(state);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);storage=new Storage(this);authService=new AuthorizationService(this);pendingExternalFile=readMishuIntent(getIntent());web=new WebView(this);setContentView(web);
  web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setAllowFileAccess(false);
  web.getSettings().setAllowContentAccess(true);web.getSettings().setMediaPlaybackRequiresUserGesture(false);web.addJavascriptInterface(new Bridge(),"MishuNative");
  web.setWebChromeClient(new WebChromeClient(){
   @Override public boolean onShowFileChooser(WebView view,ValueCallback<Uri[]> callback,FileChooserParams params){
    if(fileCallback!=null)fileCallback.onReceiveValue(null);fileCallback=callback;
    try{Intent intent=params.createIntent();startActivityForResult(intent,PICK_FILE);return true;}catch(Exception e){fileCallback=null;return false;}
   }
  });
  web.setWebViewClient(new WebViewClient(){
   @Override public void onPageFinished(WebView v,String url){String saved=storage.restore();if(saved!=null)web.evaluateJavascript("window.mishuNativeRestore("+JSONObject.quote(saved)+")",null);pageReady=true;deliverExternalFile();if("com.mishu.app.GOOGLE_OAUTH".equals(getIntent().getAction()))completeGoogleLogin(getIntent());}
   @Override public boolean shouldOverrideUrlLoading(WebView view,android.webkit.WebResourceRequest request){Uri uri=request.getUrl();String scheme=uri.getScheme();if("file".equals(scheme)&&"android_asset".equals(uri.getHost()))return false;
    if("https".equals(scheme)||"http".equals(scheme)){try{startActivity(new Intent(Intent.ACTION_VIEW,uri));}catch(Exception e){Log.e("Mishu","External link",e);}return true;}return true;}
  });web.loadUrl("file:///android_asset/index.html");
 }
 private String readMishuIntent(Intent intent){
  if(intent==null||!Intent.ACTION_VIEW.equals(intent.getAction()))return null;
  Uri uri=intent.getData();if(uri==null)return null;
  String filename=uri.getLastPathSegment();String type=intent.getType();
  if((filename==null||!filename.toLowerCase(java.util.Locale.ROOT).endsWith(".mishu"))&&!("application/vnd.mishu+json".equals(type)))return null;
  try(InputStream in=getContentResolver().openInputStream(uri);ByteArrayOutputStream out=new ByteArrayOutputStream()){
   if(in==null)return null;byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1){out.write(b,0,n);if(out.size()>20*1024*1024)throw new IOException("File too large");}
   return out.toString("UTF-8");
  }catch(Exception e){Log.e("Mishu","Cannot open .mishu",e);return null;}
 }
 private void deliverExternalFile(){if(!pageReady||pendingExternalFile==null)return;String data=pendingExternalFile;pendingExternalFile=null;web.evaluateJavascript("window.mishuOpenExternal("+JSONObject.quote(data)+")",null);}
 @Override protected void onNewIntent(Intent intent){super.onNewIntent(intent);setIntent(intent);if("com.mishu.app.GOOGLE_OAUTH".equals(intent.getAction())){completeGoogleLogin(intent);return;}pendingExternalFile=readMishuIntent(intent);deliverExternalFile();}
 @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){super.onActivityResult(requestCode,resultCode,data);if(requestCode==PICK_FILE&&fileCallback!=null){Uri[] result=WebChromeClient.FileChooserParams.parseResult(resultCode,data);fileCallback.onReceiveValue(result);fileCallback=null;}}
 class Bridge{@JavascriptInterface public void save(String json){storage.save(json);}@JavascriptInterface public void googleLogin(){runOnUiThread(()->MainActivity.this.googleLogin());}}
 @Override public void onBackPressed(){web.evaluateJavascript("if(document.querySelector('dialog[open]'))closeModal();else back();",null);}
 @Override protected void onDestroy(){if(web!=null)web.destroy();if(authService!=null)authService.dispose();storage.close();super.onDestroy();}
}
