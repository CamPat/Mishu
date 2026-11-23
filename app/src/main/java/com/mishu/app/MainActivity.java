package com.mishu.app;

import android.app.Activity;
import android.util.Log;
import android.os.Bundle;
import android.content.res.Configuration;
import android.view.WindowManager;
import android.print.PrintManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PageRange;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.widget.FrameLayout;
import android.view.ViewGroup;
import android.graphics.Color;
import android.provider.DocumentsContract;
import android.content.Context;
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
 private WebView pdfPrintWeb;
 private FrameLayout rootLayout;
 private String pendingPdfHtml;
 private String pendingPdfTitle;
 private double pendingPdfWidth=210,pendingPdfHeight=297;
 private static final int SAVE_PDF=32;
 private static final int SAVE_JSON=33;
 private String pendingJsonContent=null;
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
 @Override public void onCreate(Bundle state){super.onCreate(state);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);storage=new Storage(this);pendingExternalFile=readMishuIntent(getIntent());web=new WebView(this);rootLayout=new FrameLayout(this);rootLayout.addView(web,new FrameLayout.LayoutParams(-1,-1));setContentView(rootLayout);
  web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setAllowFileAccess(false);
  web.getSettings().setAllowContentAccess(true);web.getSettings().setMediaPlaybackRequiresUserGesture(false);web.addJavascriptInterface(new Bridge(),"MishuNative");
  web.setWebChromeClient(new WebChromeClient(){
   @Override public boolean onShowFileChooser(WebView view,ValueCallback<Uri[]> callback,FileChooserParams params){
    if(fileCallback!=null)fileCallback.onReceiveValue(null);fileCallback=callback;
    try{Intent intent=params.createIntent();startActivityForResult(intent,PICK_FILE);return true;}catch(Exception e){fileCallback=null;return false;}
   }
  });
  web.setWebViewClient(new WebViewClient(){
   @Override public void onPageFinished(WebView v,String url){String saved=storage.restore();if(saved!=null)web.evaluateJavascript("window.mishuNativeRestore("+JSONObject.quote(saved)+")",null);pageReady=true;deliverExternalFile();}
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
 @Override protected void onNewIntent(Intent intent){super.onNewIntent(intent);setIntent(intent);pendingExternalFile=readMishuIntent(intent);deliverExternalFile();}
 @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){super.onActivityResult(requestCode,resultCode,data);if(requestCode==PICK_FILE&&fileCallback!=null){Uri[] result=WebChromeClient.FileChooserParams.parseResult(resultCode,data);fileCallback.onReceiveValue(result);fileCallback=null;}
  if(requestCode==SAVE_PDF){if(resultCode==RESULT_OK&&data!=null&&data.getData()!=null){renderPdfToUri(data.getData());}else{notifyPdfResult(false,"Guardado cancelado / Save cancelled");pendingPdfHtml=null;}}
  if(requestCode==SAVE_JSON){if(resultCode==RESULT_OK&&data!=null&&data.getData()!=null){writeJsonToUri(data.getData());}else{pendingJsonContent=null;notifyJsonResult(false,"Guardado cancelado / Save cancelled");}}}
 private void saveJsonDocument(String json,String name){
  if(json==null||json.isEmpty()){notifyJsonResult(false,"JSON vacío / Empty JSON");return;}
  runOnUiThread(()->{
   if(pendingJsonContent!=null){notifyJsonResult(false,"Ya hay un archivo pendiente / Another save is pending");return;}
   try{
    pendingJsonContent=json;
    String safe=(name==null?"Mishu_backup.json":name).replaceAll("[\\\\/:*?\\\"<>|]","_");
    if(!safe.toLowerCase(java.util.Locale.ROOT).endsWith(".json"))safe+=".json";
    Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("application/json");
    intent.putExtra(Intent.EXTRA_TITLE,safe);
    startActivityForResult(intent,SAVE_JSON);
   }catch(Exception e){pendingJsonContent=null;Log.e("Mishu","JSON picker",e);notifyJsonResult(false,"No se pudo abrir Guardar como / Could not open save dialog");}
  });
 }
 private void notifyJsonResult(boolean success,String message){
  if(web!=null)runOnUiThread(()->web.evaluateJavascript("window.mishuJsonSaveResult&&window.mishuJsonSaveResult("+success+","+JSONObject.quote(message)+")",null));
 }
 private void writeJsonToUri(Uri uri){
  String json=pendingJsonContent;pendingJsonContent=null;
  if(json==null){notifyJsonResult(false,"No hay JSON pendiente");return;}
  new Thread(()->{
   try(OutputStream stream=getContentResolver().openOutputStream(uri,"wt")){
    if(stream==null)throw new IOException("No output stream");
    byte[] bytes=json.getBytes(StandardCharsets.UTF_8);
    stream.write(bytes);stream.flush();
    notifyJsonResult(true,"Archivo JSON guardado / JSON saved");
   }catch(Exception e){Log.e("Mishu","JSON write",e);notifyJsonResult(false,"Error guardando JSON: "+e.getMessage());}
  }).start();
 }
 private void notifyPdfResult(boolean success,String message){
  if(web!=null)runOnUiThread(()->web.evaluateJavascript("window.mishuPdfSaveResult&&window.mishuPdfSaveResult("+success+","+JSONObject.quote(message)+")",null));
 }
 private void printPdfHtml(String html,String title,String orientation){printPdfHtmlConfigured(html,title,"landscape".equals(orientation)?297:210,"landscape".equals(orientation)?210:297);}
 // Open Android's real system file-save picker. The chosen URI is writable without storage permissions.
 private void printPdfHtmlConfigured(String html,String title,double widthMm,double heightMm){
  if(html==null||html.trim().isEmpty()||html.length()>16*1024*1024){notifyPdfResult(false,"Documento vacío o demasiado grande / Empty or oversized document");return;}
  runOnUiThread(()->{
   pendingPdfHtml=html;pendingPdfTitle=(title==null||title.trim().isEmpty()?"Mishu":title).replaceAll("[\\/:*?\"<>|]","_");
   pendingPdfWidth=Math.max(70,Math.min(1200,widthMm));pendingPdfHeight=Math.max(70,Math.min(5000,heightMm));
   try{
    Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("application/pdf");
    intent.putExtra(Intent.EXTRA_TITLE,pendingPdfTitle+".pdf");
    startActivityForResult(intent,SAVE_PDF);
   }catch(Exception e){Log.e("Mishu","Cannot open PDF save picker",e);notifyPdfResult(false,"No se pudo abrir Guardar como PDF / Cannot open save picker");}
  });
 }
 private void renderPdfToUri(Uri target){
  final String html=pendingPdfHtml,title=pendingPdfTitle;
  pendingPdfHtml=null;
  if(html==null){notifyPdfResult(false,"No hay contenido PDF / No PDF content");return;}
  try{
   if(pdfPrintWeb!=null){rootLayout.removeView(pdfPrintWeb);pdfPrintWeb.destroy();pdfPrintWeb=null;}
   WebView view=new WebView(this);pdfPrintWeb=view;
   view.setBackgroundColor(Color.WHITE);
   view.getSettings().setJavaScriptEnabled(false);
   view.getSettings().setDomStorageEnabled(false);
   FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(Math.max(600,getResources().getDisplayMetrics().widthPixels),Math.max(800,getResources().getDisplayMetrics().heightPixels));
   rootLayout.addView(view,0,lp);
   view.setVisibility(android.view.View.VISIBLE);
   view.setAlpha(0.01f);
   view.setWebViewClient(new WebViewClient(){
    private boolean rendered=false;
    @Override public void onPageFinished(WebView w,String url){
     if(rendered)return;rendered=true;
     w.postDelayed(()->writePdfAdapter(w,target,title),900);
    }
   });
   view.loadDataWithBaseURL("https://appassets.androidplatform.net/",html,"text/html","UTF-8",null);
  }catch(Exception e){Log.e("Mishu","PDF render error",e);notifyPdfResult(false,"Error al preparar el PDF / PDF render error");}
 }
 private void writePdfAdapter(WebView view,Uri target,String title){
  try{
   int wm=(int)Math.round(pendingPdfWidth*1000/25.4),hm=(int)Math.round(pendingPdfHeight*1000/25.4);
   PrintAttributes.MediaSize paper=new PrintAttributes.MediaSize("MISHU_PDF","Mishu PDF",wm,hm);
   PrintAttributes attrs=new PrintAttributes.Builder().setMediaSize(paper).setResolution(new PrintAttributes.Resolution("pdf","PDF",300,300)).setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
   PrintDocumentAdapter adapter=view.createPrintDocumentAdapter(title);
   adapter.onStart();
   adapter.onLayout(null,attrs,new CancellationSignal(),new PrintDocumentAdapter.LayoutResultCallback(){
    @Override public void onLayoutFinished(PrintDocumentInfo info,boolean changed){
     try{
      ParcelFileDescriptor fd=getContentResolver().openFileDescriptor(target,"w");
      if(fd==null)throw new IOException("No writable PDF file");
      adapter.onWrite(new PageRange[]{PageRange.ALL_PAGES},fd,new CancellationSignal(),new PrintDocumentAdapter.WriteResultCallback(){
       @Override public void onWriteFinished(PageRange[] pages){try{fd.close();}catch(Exception ignored){}adapter.onFinish();notifyPdfResult(true,"PDF guardado / PDF saved");}
       @Override public void onWriteFailed(CharSequence error){try{fd.close();}catch(Exception ignored){}adapter.onFinish();notifyPdfResult(false,"Error al escribir PDF: "+error);}
       @Override public void onWriteCancelled(){try{fd.close();}catch(Exception ignored){}adapter.onFinish();notifyPdfResult(false,"PDF cancelado / PDF cancelled");}
      });
     }catch(Exception e){adapter.onFinish();Log.e("Mishu","PDF write",e);notifyPdfResult(false,"Error al guardar PDF / PDF save error");}
    }
    @Override public void onLayoutFailed(CharSequence error){adapter.onFinish();notifyPdfResult(false,"Error al preparar páginas: "+error);}
    @Override public void onLayoutCancelled(){adapter.onFinish();notifyPdfResult(false,"Diseño PDF cancelado / PDF layout cancelled");}
   },new Bundle());
  }catch(Exception e){Log.e("Mishu","PDF adapter",e);notifyPdfResult(false,"Error de exportación PDF / PDF export error");}
 }
 class Bridge{
  @JavascriptInterface public void save(String json){storage.save(json);}
  @JavascriptInterface public void saveJson(String json,String filename){saveJsonDocument(json,filename);}
  @JavascriptInterface public void printHtml(String html,String title,String orientation){printPdfHtml(html,title,orientation);}
  @JavascriptInterface public void printHtmlConfigured(String html,String title,double widthMm,double heightMm){printPdfHtmlConfigured(html,title,widthMm,heightMm);}
 }
 @Override public void onBackPressed(){web.evaluateJavascript("if(document.querySelector('dialog[open]'))closeModal();else back();",null);}
 @Override public void onConfigurationChanged(Configuration newConfig){super.onConfigurationChanged(newConfig); /* Keep the current WebView and note visible across rotation. */}
 @Override protected void onDestroy(){if(web!=null)web.destroy();if(pdfPrintWeb!=null)pdfPrintWeb.destroy();storage.close();super.onDestroy();}
}
