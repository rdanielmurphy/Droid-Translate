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
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class SpeechToSpeechSession extends Activity implements
View.OnClickListener, OnInitListener {

	Button btnBack;
	TextView lblTo, lblFrom;
	Spinner cmbBoxTo;
	Button btnSpeech;
	private String lastLang = "English";
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private TextToSpeech mTts;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mTts = new TextToSpeech(this, this);
		
		setContentView(R.layout.speech_to_speech);
		btnBack = (Button) findViewById(R.id.btnExitSpeechToSpeech);
		btnBack.setOnClickListener(this);
		lblTo = (TextView) findViewById(R.id.txtViewTranslatedSpeech);
		lblTo.setOnClickListener(this);
		lblFrom = (TextView) findViewById(R.id.txtViewDecodedSpeech);
		lblFrom.setOnClickListener(this);
		cmbBoxTo = (Spinner) findViewById(R.id.cmbBoxSpeechToSpeechLang);
		btnSpeech = (Button) findViewById(R.id.btnGoSpeechToSpeech);
		btnSpeech.setOnClickListener(this);

		ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
				R.array.languages, R.layout.my_simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cmbBoxTo.setAdapter(adapter);
		
		cmbBoxTo.setSelection(adapter.getPosition(SettingsValidator.getDefaultLangTo(this)));
		
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
		else if (view.equals(btnSpeech)) {
            startVoiceRecognitionActivity();
        }
		else if (view.equals(lblTo)) {
			mTts.setLanguage(LanguageValidator.getLocale(lastLang));
			mTts.speak(lblTo.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
        }
		else if (view.equals(lblFrom)) {
			mTts.setLanguage(LanguageValidator.getLocale(LanguageValidator.ENGLISH_LANG));
			mTts.speak(lblFrom.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
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
            for(String s: matches){            	
            	sb.append(s);
            	sb.append(" ");
            }
            
            lblFrom.setText(sb.toString());
            
            //translate
			String translatedText = "";
			Language to;
			Language from;

			//Set Language objects by inspecting strings selected in Spinners
			to = LanguageValidator.getLanguageObject(cmbBoxTo.getSelectedItem().toString());
			from = LanguageValidator.getLanguageObject(LanguageValidator.ENGLISH_LANG);
			try {
				//Call API function to translate text
				translatedText = Translate.execute(lblFrom.getText().toString(), from, to);
				lblTo.setText(translatedText);
				
	            //speak
				mTts.setLanguage(LanguageValidator.getLocale(cmbBoxTo.getSelectedItem().toString()));
				mTts.speak(translatedText,TextToSpeech.QUEUE_FLUSH, null);
				
				lastLang = cmbBoxTo.getSelectedItem().toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

	public void onInit(int arg0) {
		// TODO Auto-generated method stub
		
	}	
}
