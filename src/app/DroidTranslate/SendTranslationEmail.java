package app.DroidTranslate;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendTranslationEmail extends Activity implements
		View.OnClickListener {

	private EditText txtBoxMessage;
	private EditText txtBoxContacts;
	private Button btnConctacts;
	private Button btnBack;
	private Button btnSend;
	private Button btnAddNumber, btnReset;
	private EditText txtBoxEmail;

	private List<Contact> contacts;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.send_email);
		contacts = new ArrayList<Contact>();
		try {
			txtBoxMessage = (EditText) findViewById(R.id.txtBoxEmail);
			txtBoxMessage.setOnClickListener(this);
			txtBoxContacts = (EditText) findViewById(R.id.txtBoxEmailTo);
			txtBoxContacts.setText("");
			btnConctacts = (Button) findViewById(R.id.btnEmailContacts);
			btnConctacts.setOnClickListener(this);
			btnBack = (Button) findViewById(R.id.btnCancelEmail);
			btnBack.setOnClickListener(this);
			btnSend = (Button) findViewById(R.id.btnSendEmail);
			btnSend.setOnClickListener(this);
			btnAddNumber = (Button) findViewById(R.id.btnAddEmail);
			btnAddNumber.setOnClickListener(this);
			btnReset = (Button) findViewById(R.id.btnResetRecepients);
			btnReset.setOnClickListener(this);

			txtBoxContacts.setEnabled(false);
			Bundle extras = getIntent().getExtras();
			txtBoxMessage.setText(extras.getString("transText"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick(View view) {
		if (view.equals(btnBack))
			finish();
		else if (view.equals(btnConctacts)) {
			Intent intent = new Intent(this, ContactsActivity.class);
			intent.putExtra("type", ContactsActivity.EMAIL_CONTACTS);
			startActivityForResult(intent, 0);
		} else if (view.equals(btnAddNumber)) {
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.submitscore);
			dialog.setTitle("Enter Name");
			dialog.show();
			Button buttonOK = (Button) dialog.findViewById(R.id.btnSubmit);
			buttonOK.setOnClickListener(new OKListener(dialog));
			Button buttonCancel = (Button) dialog.findViewById(R.id.btnCancel);
			buttonCancel.setOnClickListener(new CancelListener(dialog));
			txtBoxEmail = (EditText) dialog.findViewById(R.id.txtBoxName);
		} else if (view.equals(btnReset)) {
			txtBoxContacts.setText("");
			contacts.clear();
		} else if (view.equals(btnSend)) {
			final Intent emailIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			
			String [] recepients = new String [contacts.size()];
			int i = 0;
			
			for (Contact c : contacts) {
				recepients[i++] = c.getEmail();
			}
			
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recepients);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,	"No Subject");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, txtBoxMessage.getText().toString());
			
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK) {
			Bundle extras = intent.getExtras();
			String name = extras.getString(ContactsActivity.NAME);
			String email = extras.getString(ContactsActivity.EMAIL);

			Contact c = new Contact(name, "", email);
			if (!listContainsContact(c)) {
				txtBoxContacts.append(name + ";");
				contacts.add(c);
			}
		}
	}

	private class Contact {
		private String _name = "";
		private String _phone = "";
		private String _email = "";

		Contact(String name, String phone, String email) {
			_name = name;
			_phone = phone;
			_email = email;
		}

		public String getName() {
			return _name;
		}

		public String getPhone() {
			return _phone;
		}

		public String getEmail() {
			return _email;
		}

		@Override
		public String toString() {
			return _name + ": " + _phone + "; " + _email;
		}

		@Override
		public int hashCode() {
			String t = this.toString();
			return t.hashCode();
		}
	}

	public boolean listContainsContact(Contact contact) {
		for (Contact c : contacts) {
			if (contact.hashCode() == c.hashCode())
				return true;
		}
		return false;
	}

	protected class OKListener implements OnClickListener {

		private Dialog dialog;

		public OKListener(Dialog dialog) {
			this.dialog = dialog;
		}

		public void onClick(View v) {
			txtBoxContacts.append(txtBoxEmail.getText().toString() + ";");
			Contact c = new Contact(txtBoxEmail.getText().toString(),
					"", txtBoxEmail.getText().toString());
			contacts.add(c);
			dialog.dismiss();
		}
	}

	protected class CancelListener implements OnClickListener {

		private Dialog dialog;

		public CancelListener(Dialog dialog) {
			this.dialog = dialog;
		}

		public void onClick(View v) {
			dialog.dismiss();
		}
	}
}
