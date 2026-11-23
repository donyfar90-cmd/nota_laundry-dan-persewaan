package id.kasirnota.pro;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.util.Base64;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_CHOOSER = 501;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        webView = new WebView(this);
        setContentView(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setLoadsImagesAutomatically(true);
        CookieManager.getInstance().setAcceptCookie(true);
        webView.addJavascriptInterface(new AppBridge(), "KasirAndroid");
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(Uri.parse(url));
            }
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Route browser printing and JSON backup downloads to Android native services.
                String js = "window.printNota=function(){renderNota();KasirAndroid.printPage();};" +
                    "window.exportBackupJSON=function(){var payload={exportedAt:new Date().toISOString(),config:config,history:getHistory()};var b=btoa(unescape(encodeURIComponent(JSON.stringify(payload,null,2))));KasirAndroid.saveBackup('kasirnota-backup-'+toDateInputValue(new Date())+'.json',b);};";
                view.evaluateJavascript(js, null);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                Intent intent = params.createIntent();
                try { startActivityForResult(intent, FILE_CHOOSER); }
                catch (ActivityNotFoundException e) { fileCallback = null; Toast.makeText(MainActivity.this, "Pemilih file tidak tersedia", Toast.LENGTH_SHORT).show(); return false; }
                return true;
            }
        });
        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
            catch (Exception e) { Toast.makeText(this, "Tidak dapat membuka unduhan", Toast.LENGTH_SHORT).show(); }
        });
        webView.loadUrl("file:///android_asset/index.html");
    }

    private boolean handleUrl(Uri uri) {
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase();
        if (scheme.equals("file") || scheme.equals("about") || scheme.equals("data") || scheme.equals("blob")) return false;
        if (scheme.equals("http") || scheme.equals("https")) return false;
        try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
        catch (Exception e) { Toast.makeText(this, "Aplikasi untuk tautan ini tidak ditemukan", Toast.LENGTH_SHORT).show(); }
        return true;
    }

    private class AppBridge {
        @JavascriptInterface public void saveBackup(String name, String encoded) {
            runOnUiThread(() -> {
                try {
                    byte[] bytes = Base64.decode(encoded, Base64.DEFAULT);
                    File file = new File(getExternalFilesDir(null), name);
                    try (FileOutputStream out = new FileOutputStream(file)) { out.write(bytes); }
                    Toast.makeText(MainActivity.this, "Backup tersimpan: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (Exception e) { Toast.makeText(MainActivity.this, "Gagal menyimpan backup", Toast.LENGTH_SHORT).show(); }
            });
        }
        @JavascriptInterface public void printPage() { runOnUiThread(() -> {
            try {
                PrintManager manager = (PrintManager) getSystemService(PRINT_SERVICE);
                String jobName = "Nota KasirNota Pro";
                manager.print(jobName, webView.createPrintDocumentAdapter(jobName), new PrintAttributes.Builder().build());
            } catch (Exception e) { Toast.makeText(MainActivity.this, "Fitur cetak tidak tersedia", Toast.LENGTH_SHORT).show(); }
        }); }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER && fileCallback != null) {
            Uri[] results = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            fileCallback.onReceiveValue(results);
            fileCallback = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
}
