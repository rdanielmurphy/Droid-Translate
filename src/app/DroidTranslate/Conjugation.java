package app.DroidTranslate;

import app.DroidTranslate.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Conjugation extends Activity implements View.OnClickListener {
	EditText txtBoxInput;
	Button btnOK;
	Button btnBack;
	Spinner cmbBoxLanguages;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.conjugator);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.my_simple_spinner_item, LanguageValidator
						.getListOfConjugationLanguagesSupported());
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cmbBoxLanguages = (Spinner) findViewById(R.id.cmbBoxConjLanguage);
		cmbBoxLanguages.setAdapter(adapter);
		cmbBoxLanguages.setSelection(adapter.getPosition(SettingsValidator
				.getDefaultConjLang(this)));
		txtBoxInput = (EditText) findViewById(R.id.txtBoxVerb);
		btnOK = (Button) findViewById(R.id.btnConjugate);
		btnOK.setOnClickListener(this);
		btnBack = (Button) findViewById(R.id.btnConjugateBack);
		btnBack.setOnClickListener(this);
	}

	public void onClick(View view) {
		if (view.equals(btnBack))
			finish();
		else if (view.equals(btnOK)) {
			if (txtBoxInput.getText().toString().length() > 0) {
				Intent intent = new Intent(this, WebHolder.class);
				intent.putExtra("verb", txtBoxInput.getText().toString());
				intent.putExtra("lang", cmbBoxLanguages.getSelectedItem()
						.toString());
				startActivity(intent);
			}
		}
	}
}
