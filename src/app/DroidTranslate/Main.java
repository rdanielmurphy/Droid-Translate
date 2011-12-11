package app.DroidTranslate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Main extends Activity implements View.OnClickListener{
	Button btnNewTrans;
	Button btnConjugation;
	Button btnSavedTrans, btnSpeechToSpeech;
	Button btnExit;
	
    /** Called when the activity is first created. */
    //@Override
    public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);
        setContentView(R.layout.main);

        btnNewTrans = (Button)findViewById(R.id.btnNewTrans);
        btnNewTrans.setOnClickListener(this);
        btnConjugation = (Button)findViewById(R.id.btnConjugate);
        btnConjugation.setOnClickListener(this);
        btnSavedTrans = (Button)findViewById(R.id.btnSavedTrans);
        btnSavedTrans.setOnClickListener(this);
        btnSpeechToSpeech = (Button)findViewById(R.id.btnSpeechToSpeech);
        btnSpeechToSpeech.setOnClickListener(this);
        btnExit = (Button)findViewById(R.id.btnExit);
        btnExit.setOnClickListener(this);
        
        registerForContextMenu(new View(this.getApplicationContext()));
        
        
		try {	        

		} catch (Exception e) {
			// TODO Auto-generated catch block
 			e.printStackTrace();
		}
    }
	public void onClick(View view){
		if(view.equals(btnExit))
			finish();
		else if(view.equals(btnNewTrans)){
			Intent intent = new Intent(this, TranslationSession.class);
			startActivity(intent);			
		}
		else if(view.equals(btnConjugation)){
			Intent intent = new Intent(this, Conjugation.class);
			startActivity(intent);			
		}
		else if(view.equals(btnSavedTrans)){
			Intent intent = new Intent(this, SavedTranslationsList.class);
			startActivity(intent);			
		}		
		else if(view.equals(btnSpeechToSpeech)){
			Intent intent = new Intent(this, SpeechToSpeechSession.class);
			startActivity(intent);			
		}		
	}
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add("Settings");
    	
    	return(super.onCreateOptionsMenu(menu));
    }
    public boolean onOptionsItemSelected(MenuItem item){
		Intent intent = new Intent(this, TranslationSettings.class);
		startActivity(intent);
		
    	return true;
    }
}