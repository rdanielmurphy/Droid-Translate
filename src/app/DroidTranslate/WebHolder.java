package app.DroidTranslate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebHolder extends Activity {
	private WebView wv;
	private String url, verb, lang;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		wv = new WebView(this);
		setContentView(wv);

		wv.getSettings().setJavaScriptEnabled(true);

		final Activity activity = this;
		wv.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				activity.setProgress(progress * 1000);
			}
		});

		Bundle extras = getIntent().getExtras();
		verb = extras.getString("verb");
		lang = extras.getString("lang");

		url = LanguageValidator.getWebSite(lang, verb);

		wv.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
			}
		});
		wv.loadUrl(url);
	}
}
