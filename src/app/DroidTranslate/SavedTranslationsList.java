package app.DroidTranslate;

import java.util.ArrayList;
import java.util.List;

import app.DroidTranslate.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SavedTranslationsList extends ListActivity implements
		View.OnKeyListener {
	TranslationDataBaseInterface db;
	Button btnDelete;
	EditText textView;
	String matchText;
	List<Translation> list;
	List<Translation> fList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_translations_main);

		// get translations from db
		db = new TranslationDataBaseInterface(this.getApplicationContext());
		list = db.GetTranslations();

		textView = (EditText) findViewById(R.id.txtBoxSearch);
		textView.setText("");
		matchText = textView.getText().toString().toLowerCase();
		textView.setOnKeyListener(this);
		
		setListAdapter(new IconicAdapter(this));
	}

	//@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		matchText = textView.getText().toString().toLowerCase();
		setListAdapter(new IconicAdapter(this));
		return false;
	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
		Intent intent = new Intent(this, TranslatedItemView.class);
		intent.putExtra("origText", fList.get(position).sOrig);
		intent.putExtra("transText", fList.get(position).sTranslated);
		intent.putExtra("langFrom", fList.get(position).sFrom);
		intent.putExtra("langTo", fList.get(position).sTo);
		intent.putExtra("version", "saved");
		startActivityForResult(intent, 0);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// /TODO: only refresh if item deleted
		list = db.GetTranslations();
		setListAdapter(new IconicAdapter(this));
	}
	
	public List<Translation> filterList() {
		if(!matchText.equals("")){
			fList = new ArrayList<Translation>();			
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).sOrig.toLowerCase().contains(matchText))
					fList.add(list.get(i));
			}
		}
		else{
			fList = new ArrayList<Translation>();			
			for (int i = 0; i < list.size(); i++) {
					fList.add(list.get(i));
			}
		}
		return fList;
	}

	class IconicAdapter extends ArrayAdapter {
		Activity context;

		@SuppressWarnings("unchecked")
		IconicAdapter(Activity context) {
			super(context, R.layout.saved_translations, filterList());

			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = context.getLayoutInflater();

				row = inflater.inflate(R.layout.saved_translations, null);
			}
			TextView label = (TextView) row
					.findViewById(R.id.lblSavedTranslation);

			label.setText(fList.get(position).sOrig);
			ImageView iconTo = (ImageView) row.findViewById(R.id.iconTo);
			ImageView iconFrom = (ImageView) row.findViewById(R.id.iconFrom);
			
			iconTo.setImageResource(LanguageValidator.getLanguageIcon(fList.get(position).sTo));
			iconFrom.setImageResource(LanguageValidator.getLanguageIcon(fList.get(position).sFrom));

			return (row);
		}
	}
}
