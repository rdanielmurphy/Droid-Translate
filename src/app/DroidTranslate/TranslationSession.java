package app.DroidTranslate;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class TranslationSession extends Activity implements
		View.OnClickListener {

	Button btnBack;
	Button btnTranslate;
	EditText txtBoxInput;
	Spinner cmbBoxTo;
	Spinner cmbBoxFrom;
	Button btnSwitch,btnSpeech;
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.translationsession);
		btnTranslate = (Button) findViewById(R.id.btnTranslate);
		btnTranslate.setOnClickListener(this);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		txtBoxInput = (EditText) findViewById(R.id.txtBoxText);
		txtBoxInput.setOnClickListener(this);
		cmbBoxTo = (Spinner) findViewById(R.id.cmbBoxTo);
		cmbBoxFrom = (Spinner) findViewById(R.id.cmbBoxFrom);
		btnSwitch = (Button) findViewById(R.id.btnSwitch);
		btnSwitch.setOnClickListener(this);
		btnSpeech = (Button) findViewById(R.id.btnSpeechToText);
		btnSpeech.setOnClickListener(this);

		ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
				R.array.languages, R.layout.my_simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cmbBoxTo.setAdapter(adapter);
		cmbBoxFrom.setAdapter(adapter);
		
		cmbBoxTo.setSelection(adapter.getPosition(SettingsValidator.getDefaultLangTo(this)));
		cmbBoxFrom.setSelection(adapter.getPosition(SettingsValidator.getDefaultLangFrom(this)));
		
        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
        	btnSpeech.setOnClickListener(this);
        } else {
        	btnSpeech.setEnabled(false);
            btnSpeech.setText("Disabled");
        }
        
		try {
			Translate.setHttpReferrer(InetAddress.getLocalHost().getHostName());
			Translate.validateReferrer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick(View view) {
		if (view.equals(btnBack))//exit Activity
			finish();
		else if (view.equals(btnSwitch)) {
            //Get index of selected item in the "to" Spinner, 
			//    set it as the index in the "from" Spinner
			//    and visa versa.			
			int iTo = cmbBoxTo.getSelectedItemPosition();
			int iFrom = cmbBoxFrom.getSelectedItemPosition();
			cmbBoxTo.setSelection(iFrom, true);
			cmbBoxFrom.setSelection(iTo, true);
		} 
		else if (view.equals(btnSpeech)) {
            startVoiceRecognitionActivity();
        }else if (view.equals(btnTranslate)) {
			try {
				String translatedText = "";
				Language to;
				Language from;

				//Set Language objects by inspecting strings selected in Spinners
				to = LanguageValidator.getLanguageObject(cmbBoxTo.getSelectedItem().toString());
				from = LanguageValidator.getLanguageObject(cmbBoxFrom.getSelectedItem().toString());

				//Call API function to translate text
				translatedText = Translate.execute(txtBoxInput.getText()
						.toString(), from, to);
					
				//Display
				Intent intent = new Intent(this, TranslatedItemView.class);
				intent.putExtra("origText", txtBoxInput.getText().toString());
				intent.putExtra("transText", translatedText);
				intent.putExtra("langFrom",cmbBoxFrom.getSelectedItem().toString());
				intent.putExtra("langTo",cmbBoxTo.getSelectedItem().toString());
				intent.putExtra("version", "new");
				startActivity(intent);				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
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
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            
            StringBuilder sb = new StringBuilder();
            if(txtBoxInput.getText().toString().length() > 0)
            	sb.append(" ");
            for(String s: matches){            	
            	sb.append(s);
            	sb.append(" ");
            }
            
            txtBoxInput.setText(txtBoxInput.getText().toString() + sb.toString().substring(0, sb.length()-1));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
