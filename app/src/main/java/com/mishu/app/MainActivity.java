package com.mishu.app;

import android.app.Activity;
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
 @Override public void onCreate(Bundle state){super.onCreate(state);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);storage=new Storage(this);web=new WebView(this);setContentView(web);
  web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setAllowFileAccess(false);
  web.getSettings().setAllowContentAccess(true);web.getSettings().setMediaPlaybackRequiresUserGesture(false);web.addJavascriptInterface(new Bridge(),"MishuNative");
  web.setWebChromeClient(new WebChromeClient(){
   @Override public boolean onShowFileChooser(WebView view,ValueCallback<Uri[]> callback,FileChooserParams params){
    if(fileCallback!=null)fileCallback.onReceiveValue(null);fileCallback=callback;
    try{Intent intent=params.createIntent();startActivityForResult(intent,PICK_FILE);return true;}catch(Exception e){fileCallback=null;return false;}
   }
  });
  web.setWebViewClient(new WebViewClient(){
   @Override public void onPageFinished(WebView v,String url){String saved=storage.restore();if(saved!=null)web.evaluateJavascript("window.mishuNativeRestore("+JSONObject.quote(saved)+")",null);}
   @Override public boolean shouldOverrideUrlLoading(WebView view,android.webkit.WebResourceRequest request){Uri uri=request.getUrl();String scheme=uri.getScheme();if("file".equals(scheme)&&"android_asset".equals(uri.getHost()))return false;
    if("https".equals(scheme)||"http".equals(scheme)){try{startActivity(new Intent(Intent.ACTION_VIEW,uri));}catch(Exception e){Log.e("Mishu","External link",e);}return true;}return true;}
  });web.loadUrl("file:///android_asset/index.html");
 }
 @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){super.onActivityResult(requestCode,resultCode,data);if(requestCode==PICK_FILE&&fileCallback!=null){Uri[] result=WebChromeClient.FileChooserParams.parseResult(resultCode,data);fileCallback.onReceiveValue(result);fileCallback=null;}}
 class Bridge{@JavascriptInterface public void save(String json){storage.save(json);}}
 @Override public void onBackPressed(){web.evaluateJavascript("if(document.querySelector('dialog[open]'))closeModal();else back();",null);}
 @Override protected void onDestroy(){if(web!=null)web.destroy();storage.close();super.onDestroy();}
}
