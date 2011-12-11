package app.DroidTranslate;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class TranslationDataBaseInterface {
	private TranslationDataBase db;

	public TranslationDataBaseInterface(Context context) {
		try {
			db = new TranslationDataBase(context);
			db.open();
		} catch (Exception e) {
			new AlertDialog.Builder(context).setTitle("ERROR").setMessage(
					e.getMessage()).setNeutralButton("Close",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dlg, int sumthin) {
						}
					}).show();
		}
	}

	public List<Translation> GetTranslations() {
		return db.getEntries();
	}

	public void DeleteTranslation(Translation tTranslation) {
		db.DeleteTranslation(tTranslation);
	}

	public void AddTranslation(Translation tTranslation) {
		db.AddTranslation(tTranslation);
	}
	
	public int GetNumberOfTranslations(){
		return db.NumberOfEntries();
	}
}