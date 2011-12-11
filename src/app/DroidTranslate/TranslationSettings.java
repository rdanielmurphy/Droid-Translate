package app.DroidTranslate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class TranslationSettings extends Activity implements View.OnClickListener{
	
	//public static final String PREFS_NAME = "MyPrefsFile";
	
	Button btnBack;
	Button btnSave;
	Spinner cmbBoxTo;
	Spinner cmbBoxFrom;
	Spinner cmbBoxConj;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		btnSave = (Button) findViewById(R.id.btnSaveSettings);
		btnSave.setOnClickListener(this);
		btnBack = (Button) findViewById(R.id.btnExitSetings);
		btnBack.setOnClickListener(this);
		cmbBoxTo = (Spinner) findViewById(R.id.cmbBoxDefaultLangTo);
		cmbBoxFrom = (Spinner) findViewById(R.id.cmbBoxDefaultLangFrom);
		cmbBoxConj = (Spinner) findViewById(R.id.cmbBoxDefaultConjLang);

		ArrayAdapter<String> conjAdapter = new ArrayAdapter<String>(this, R.layout.my_simple_spinner_item, 
				LanguageValidator.getListOfConjugationLanguagesSupported());         
		conjAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<String> transAdapter = new ArrayAdapter<String>(this, R.layout.my_simple_spinner_item, 
				LanguageValidator.getListOfTranslationLanguagesSupported());         
		transAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		cmbBoxTo.setAdapter(transAdapter);
		cmbBoxFrom.setAdapter(transAdapter);
		cmbBoxConj.setAdapter(conjAdapter);
		
	    setCombBox(cmbBoxFrom,SettingsValidator.getDefaultLangFrom(this));
	    setCombBox(cmbBoxTo,SettingsValidator.getDefaultLangTo(this));
	    setCombBox(cmbBoxConj,SettingsValidator.getDefaultConjLang(this));
	}

	public void onClick(View v) {	
		if (v.equals(btnBack))//exit Activity
			finish();
		else if (v.equals(btnSave)) {			
			 SettingsValidator.setDefaultLangFrom(cmbBoxFrom.getSelectedItem().toString(), this);
			 SettingsValidator.setDefaultLangTo(cmbBoxTo.getSelectedItem().toString(), this);
			 SettingsValidator.setDefaultConjLang(cmbBoxConj.getSelectedItem().toString(), this);
			 
		     finish();
		}
	}
	
	private void setCombBox(Spinner spinner, String value){
		for(int i = 0; i < spinner.getCount(); i++){
			if(spinner.getItemAtPosition(i).equals(value))
				spinner.setSelection(i);
		}
	}
}
