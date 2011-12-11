package app.DroidTranslate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TranslatedItemView extends Activity implements
		View.OnClickListener, OnInitListener {
	private TextView tvOld;
	private TextView tvNew;
	private String from;
	private String to;
	private String version;
	private Button btnBack;
	private Button btnSaveDelete;
	private Button btnSendSMS,btnSendEmail,btnCopy;
	private TranslationDataBaseInterface db;
	private TextToSpeech mTts;

	/** Called when the activity is first created. */
	// @Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.translateditemview);
		tvOld = (TextView) findViewById(R.id.lblOrigText);
		tvOld.setOnClickListener(this);
		tvNew = (TextView) findViewById(R.id.lblTransText);
		tvNew.setOnClickListener(this);
		btnSaveDelete = (Button) findViewById(R.id.btnSave);
		btnSaveDelete.setOnClickListener(this);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnSendSMS = (Button) findViewById(R.id.btnText);
		btnSendSMS.setOnClickListener(this);
		btnSendEmail = (Button) findViewById(R.id.btnEmail);
		btnSendEmail.setOnClickListener(this);
		btnCopy = (Button) findViewById(R.id.btnCopy);
		btnCopy.setOnClickListener(this);
		
		mTts = new TextToSpeech(this, this);
		try {
			Bundle extras = getIntent().getExtras();
			tvOld.setText(extras.getString("origText"));
			tvNew.setText(extras.getString("transText"));
			from = extras.getString("langFrom");
			to = extras.getString("langTo");
			version = extras.getString("version");
			if (version.equals("saved"))
				btnSaveDelete.setText("Delete");
			else if (version.equals("new"))
				btnSaveDelete.setText("Save");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onClick(View view) {
		if (view.equals(btnBack))
			finish();
		else if (view.equals(btnSaveDelete)
				&& btnSaveDelete.getText().equals("Save")) {
			db = new TranslationDataBaseInterface(this.getApplicationContext());
			Translation t = new Translation(from, to, tvOld.getText()
					.toString(), tvNew.getText().toString());
			db.AddTranslation(t);
			Toast toast = new Toast(TranslatedItemView.this);
			toast = Toast.makeText(TranslatedItemView.this, "Translation Saved", Toast.LENGTH_SHORT);
			toast.show();
		} else if (view.equals(btnSaveDelete)
				&& btnSaveDelete.getText().equals("Delete")) {
			db = new TranslationDataBaseInterface(this.getApplicationContext());
			Translation t = new Translation(from, to, tvOld.getText()
					.toString(), tvNew.getText().toString());
			db.DeleteTranslation(t);
			Toast toast = new Toast(TranslatedItemView.this);
			toast = Toast.makeText(TranslatedItemView.this, "Translation Deleted", Toast.LENGTH_SHORT);
			toast.show();
			finish();
		} else if (view.equals(tvOld)) {
			try {
				mTts.setLanguage(LanguageValidator.getLocale(from));

				mTts.speak(tvOld.getText().toString(),
						TextToSpeech.QUEUE_FLUSH, null);
			} catch (Exception e) {
				new AlertDialog.Builder(this).setMessage(e.getMessage())
						.setNeutralButton("Close",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dlg,
											int sumthin) {
									}
								}).show();
			}
		} else if (view.equals(tvNew)) {
			try {
				mTts.setLanguage(LanguageValidator.getLocale(to));

				mTts.speak(tvNew.getText().toString(),
						TextToSpeech.QUEUE_FLUSH, null);
			} catch (Exception e) {
				new AlertDialog.Builder(this).setMessage(e.getMessage())
						.setNeutralButton("Close",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dlg,
											int sumthin) {
									}
								}).show();
			}
		} else if (view.equals(btnSendSMS)) {
			try {			
				Intent intent = new Intent(this, SendTranslationSMS.class);
				intent.putExtra("transText", tvNew.getText());
				startActivity(intent);	
			} catch (Exception e) {
				new AlertDialog.Builder(this).setMessage(e.getMessage())
						.setNeutralButton("Close",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dlg,
											int sumthin) {
									}
								}).show();
			}
		} else if (view.equals(btnSendEmail)) {
			try {			
				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,	"");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, tvNew.getText());
				
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			} catch (Exception e) {
				new AlertDialog.Builder(this).setMessage(e.getMessage())
						.setNeutralButton("Close",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dlg,
											int sumthin) {
									}
								}).show();
			}
		}
		if (view.equals(btnCopy)) { 
			ClipboardManager ClipMan = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			ClipMan.setText(tvNew.getText());
		}
	}

	// @Override
	public void onInit(int status) {
		// TODO Auto-generated method stub

	}
}
