package app.DroidTranslate;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebHolder extends Activity {
	WebView wv;
	String url;
	String verb;
	String lang;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		wv = new WebView(this);
		setContentView(wv);

		try {
			Bundle extras = getIntent().getExtras();
			verb = extras.getString("verb");
			lang = extras.getString("lang");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		url = LanguageValidator.getWebSite(lang,verb);
		wv.loadUrl(url);
	}
}
