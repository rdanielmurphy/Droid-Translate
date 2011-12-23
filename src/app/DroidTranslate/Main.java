package app.DroidTranslate;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class Main extends Activity implements View.OnClickListener {
	private ImageButton btnSettings;
	private ImageButton btnConjugation;
	private ImageButton btnHistory;
	private ImageButton btnSpeechToSpeech;
	private Button btnTranslate, btnSwitch, btnSpeech;
	private EditText txtBoxInput;
	private Spinner cmbBoxTo, cmbBoxFrom;

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	/** Called when the activity is first created. */
	// @Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentBasedOnLayout();

		btnConjugation = (ImageButton) findViewById(R.id.btnConjugator);
		btnConjugation.setOnClickListener(this);
		btnSettings = (ImageButton) findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(this);
		btnHistory = (ImageButton) findViewById(R.id.btnHistory);
		btnHistory.setOnClickListener(this);
		btnSpeechToSpeech = (ImageButton) findViewById(R.id.btnSpeechToSpeech);
		btnSpeechToSpeech.setOnClickListener(this);

		btnTranslate = (Button) findViewById(R.id.btnTranslate);
		btnTranslate.setOnClickListener(this);
		txtBoxInput = (EditText) findViewById(R.id.txtBoxText);
		txtBoxInput.setOnClickListener(this);
		cmbBoxTo = (Spinner) findViewById(R.id.cmbBoxTo);
		cmbBoxFrom = (Spinner) findViewById(R.id.cmbBoxFrom);
		btnSwitch = (Button) findViewById(R.id.btnSwitch);
		btnSwitch.setOnClickListener(this);
		btnSpeech = (Button) findViewById(R.id.btnSpeechToText);
		btnSpeech.setOnClickListener(this);

		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.languages, R.layout.my_simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cmbBoxTo.setAdapter(adapter);
		cmbBoxFrom.setAdapter(adapter);

		cmbBoxTo.setSelection(adapter.getPosition(SettingsValidator.getDefaultLangTo(this)));
		cmbBoxFrom.setSelection(adapter.getPosition(SettingsValidator.getDefaultLangFrom(this)));

		// Check to see if a recognition activity is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
			btnSpeech.setOnClickListener(this);
		} else {
			btnSpeech.setEnabled(false);
			btnSpeech.setText("Disabled");
		}
	}

	
	/**
	 * 
	 */
	private void setContentBasedOnLayout() {
		WindowManager winMan = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);

		if (winMan != null) {
			int orientation = winMan.getDefaultDisplay().getOrientation();

			if (orientation == 0) {
				// Portrait
				setContentView(R.layout.new_main_portrait);
			} else if (orientation == 1) {
				// Landscape
				setContentView(R.layout.new_main_landscape);
			}
		}
	}

	/**
	 * Fire an intent to start the speech recognition activity.
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			StringBuilder sb = new StringBuilder();
			if (txtBoxInput.getText().toString().length() > 0)
				sb.append(" ");
			for (String s : matches) {
				sb.append(s);
				sb.append(" ");
			}

			txtBoxInput.setText(txtBoxInput.getText().toString() + sb.toString().substring(0, sb.length() - 1));
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onClick(View view) {
		if (view.equals(btnSettings)) {
			Intent intent = new Intent(this, TranslationSettings.class);
			startActivity(intent);
		} else if (view.equals(btnConjugation)) {
			Intent intent = new Intent(this, Conjugation.class);
			startActivity(intent);
		} else if (view.equals(btnHistory)) {
			Intent intent = new Intent(this, SavedTranslationsList.class);
			startActivity(intent);
		} else if (view.equals(btnSpeechToSpeech)) {
			Intent intent = new Intent(this, SpeechToSpeechSession.class);
			startActivity(intent);
		} else if (view.equals(btnSwitch)) {
			// Get index of selected item in the "to" Spinner,
			// set it as the index in the "from" Spinner
			// and visa versa.
			int iTo = cmbBoxTo.getSelectedItemPosition();
			int iFrom = cmbBoxFrom.getSelectedItemPosition();
			cmbBoxTo.setSelection(iFrom, true);
			cmbBoxFrom.setSelection(iTo, true);
		} else if (view.equals(btnSpeech)) {
			startVoiceRecognitionActivity();
		} else if (view.equals(btnTranslate)) {
			try {
				String translatedText = "";
				String to;
				String from;

				// Set Language objects by inspecting strings selected in Spinners
				to = LanguageValidator.getLanguageString(cmbBoxTo.getSelectedItem().toString());
				from = LanguageValidator.getLanguageString(cmbBoxFrom.getSelectedItem().toString());

				// Call API function to translate text
				translatedText = LanguageValidator.translateString(from, to, txtBoxInput.getText().toString());

				// Display
				Intent intent = new Intent(this, TranslatedItemView.class);
				intent.putExtra("origText", txtBoxInput.getText().toString());
				intent.putExtra("transText", translatedText);
				intent.putExtra("langFrom", cmbBoxFrom.getSelectedItem().toString());
				intent.putExtra("langTo", cmbBoxTo.getSelectedItem().toString());
				intent.putExtra("version", "new");
				startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(this.getApplicationContext(), "Could not run translation.  See log.", Toast.LENGTH_LONG);
				Log.e("failed translation", e.getMessage());
			}
		}
	}
}