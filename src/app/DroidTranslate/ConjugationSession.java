package app.DroidTranslate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ConjugationSession extends Activity implements View.OnClickListener {
	private EditText txtBoxInput;
	private Button btnOK, btnBack;
	private Spinner cmbBoxLanguages;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.conjugator);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_simple_spinner_item,
				LanguageValidator.getListOfConjugationLanguagesSupported());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cmbBoxLanguages = (Spinner) findViewById(R.id.cmbBoxConjLanguage);
		cmbBoxLanguages.setAdapter(adapter);
		cmbBoxLanguages.setSelection(adapter.getPosition(SettingsValidator.getDefaultConjLang(this)));
		txtBoxInput = (EditText) findViewById(R.id.txtBoxVerb);
		btnOK = (Button) findViewById(R.id.btnConjugate);
		btnOK.setOnClickListener(this);
		btnOK.setEnabled(false);
		btnBack = (Button) findViewById(R.id.btnConjugateBack);
		btnBack.setOnClickListener(this);
		final TextView progressText = (TextView) findViewById(R.id.progressText);

		final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
		// Start lengthy operation in a background thread
		new Thread(new Runnable() {
			public void run() {
				try {
					progress.setVisibility(ProgressBar.VISIBLE);

					runOnUiThread(new Runnable() {
						public void run() {
							try {
								LanguageValidator.initializeLanguageEngine(ConjugationSession.this);

								progress.setVisibility(ProgressBar.GONE);
								progressText.setVisibility(TextView.INVISIBLE);
								btnOK.setEnabled(true);
							} catch (Exception e1) {
								Toast.makeText(getApplicationContext(), "Error...could not read Spanish verb DB. See log.", Toast.LENGTH_LONG).show();
								Log.e("Spanish DB exception", e1.getMessage());
							}
						}
					});
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Error...could not read Spanish verb DB. See log.", Toast.LENGTH_LONG).show();
					Log.e("Spanish DB exception", e.getMessage());
				} finally {

				}
			}
		}).start();
	}

	public void onClick(View view) {
		try {
			if (view.equals(btnBack))
				finish();
			else if (view.equals(btnOK)) {
				if (txtBoxInput.getText().toString().length() > 0) {
					Intent intent = null;
					if (LanguageValidator.conjugateVerb(cmbBoxLanguages.getSelectedItem().toString(), txtBoxInput.getText().toString()) != null) {
						if (LanguageValidator.getLanguageString(cmbBoxLanguages.getSelectedItem().toString()).equals(LanguageValidator.SPANISH_LANG_ABBR))
							intent = new Intent(this, SpanishConjugationView.class);
					} else
						intent = new Intent(this, WebHolder.class);

					intent.putExtra("verb", txtBoxInput.getText().toString());
					intent.putExtra("lang", cmbBoxLanguages.getSelectedItem().toString());
					startActivity(intent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
