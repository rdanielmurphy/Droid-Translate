package app.DroidTranslate;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendTranslationSMS extends Activity implements
		View.OnClickListener {

	private EditText txtBoxMessage;
	private EditText txtBoxContacts;
	private Button btnConctacts;
	private Button btnBack;
	private Button btnSend;
	private Button btnAddNumber, btnReset;
	private EditText txtBoxNumber;

	private List<Contact> contacts;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.send_sms);
		contacts = new ArrayList<Contact>();
		try {
			txtBoxMessage = (EditText) findViewById(R.id.txtBoxMessage);
			txtBoxMessage.setOnClickListener(this);
			txtBoxContacts = (EditText) findViewById(R.id.txtBoxTo);
			txtBoxContacts.setText("");
			btnConctacts = (Button) findViewById(R.id.btnContacts);
			btnConctacts.setOnClickListener(this);
			btnBack = (Button) findViewById(R.id.btnCancelSMS);
			btnBack.setOnClickListener(this);
			btnSend = (Button) findViewById(R.id.btnSendSMS);
			btnSend.setOnClickListener(this);
			btnAddNumber = (Button) findViewById(R.id.btnAddNumber);
			btnAddNumber.setOnClickListener(this);
			btnReset = (Button) findViewById(R.id.btnResetRecievers);
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
			intent.putExtra("type",ContactsActivity.PHONE_CONTACTS);
			startActivityForResult(intent, 0);
		} else if (view.equals(btnAddNumber)) {
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.submitscore);
			dialog.setTitle("Enter Number");
			dialog.show();
			Button buttonOK = (Button) dialog.findViewById(R.id.btnSubmit);
			buttonOK.setOnClickListener(new OKListener(dialog));
			Button buttonCancel = (Button) dialog.findViewById(R.id.btnCancel);
			buttonCancel.setOnClickListener(new CancelListener(dialog));
			txtBoxNumber = (EditText) dialog.findViewById(R.id.txtBoxName);
		} else if (view.equals(btnReset)) {
			txtBoxContacts.setText("");
			contacts.clear();
		} else if (view.equals(btnSend)) {
			BroadcastReceiver rcvMsgSent = null;

			final SmsManager sms = SmsManager.getDefault();

			// prepare listening intents
			Intent msgSent = new Intent("ACTION_MSG_SENT");
			Intent msgReceipt = new Intent("ACTION_MSG_RECEIPT");

			final PendingIntent pendingMsgSent = PendingIntent.getBroadcast(
					this, 0, msgSent, 0);
			final PendingIntent pendingMsgReceipt = PendingIntent.getBroadcast(
					this, 0, msgReceipt, 0);

			for (final Contact c : contacts) {
				sms.sendTextMessage(c.getPhone(), null, txtBoxMessage.getText()
						.toString(), pendingMsgSent, pendingMsgReceipt);

				if (rcvMsgSent != null) {
					unregisterReceiver(rcvMsgSent);
					rcvMsgSent = null;
				}

				// listen for status notifications
				rcvMsgSent = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						int result = getResultCode();
						if (result != Activity.RESULT_OK) {
							Log.e("telephony", "SMS send failed code = "
									+ result);
							Toast t = new Toast(SendTranslationSMS.this);
							t = Toast.makeText(SendTranslationSMS.this, "SMS send failed code = "
								+ result + "to " + c._name, Toast.LENGTH_SHORT);
			                t.show();
							pendingMsgReceipt.cancel();
						} else {
							Toast t = new Toast(SendTranslationSMS.this);
			                t = Toast.makeText(SendTranslationSMS.this, "SMS message sent! to " + c._name, Toast.LENGTH_SHORT);
			                t.show();
						}
					}
				};
				registerReceiver(rcvMsgSent,
						new IntentFilter("ACTION_MSG_SENT"));
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK) {
			Bundle extras = intent.getExtras();
			String name = extras.getString(ContactsActivity.NAME);
			String phone = extras.getString(ContactsActivity.NUMBER);

			Contact c = new Contact(name, phone, "");
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
			txtBoxContacts.append(txtBoxNumber.getText().toString() + ";");
			Contact c = new Contact(txtBoxNumber.getText().toString(),
					txtBoxNumber.getText().toString(), "");
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
