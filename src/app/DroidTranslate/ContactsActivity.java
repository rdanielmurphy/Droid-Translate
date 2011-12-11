package app.DroidTranslate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsActivity extends ListActivity {
	private List<Map<String, String>> phoneNumbers;
	private List<Map<String, String>> emails;
	public static final String NAME = "Name";
	public static final String NUMBER = "Number";
	public static final String EMAIL = "Email";
	public static final String TYPE = "Type";
	private Button btnClose;

	public static final Integer EMAIL_CONTACTS = 0;
	public static final Integer PHONE_CONTACTS = 1;
	private Integer type = PHONE_CONTACTS;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		try {
			setContentView(R.layout.dialog);

			btnClose = (Button) findViewById(R.id.btnCloseContacts);
			btnClose.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});

			Bundle extras = getIntent().getExtras();
			type = (Integer) extras.get("type");

			if (type != EMAIL_CONTACTS && type != PHONE_CONTACTS)
				type = PHONE_CONTACTS;

			phoneNumbers = new ArrayList<Map<String, String>>();
			emails = new ArrayList<Map<String, String>>();
			Cursor cursor = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);
			while (cursor.moveToNext()) {
				String contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));

				String hasPhone = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				
				if (hasPhone.equals("1")) {
					// You know have the number so now query it like this
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					if (phones.moveToFirst()) {
						do {
							Map<String, String> map = new HashMap<String, String>();
							String phoneNumber = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String name = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
							String type = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
							map.put(NUMBER, phoneNumber);
							map.put(NAME, name);
							map.put(TYPE, getPhoneType(type));
							phoneNumbers.add(map);
						} while (phones.moveToNext());
					}
					phones.close();

					Cursor emailCursor = getContentResolver()
							.query(
									ContactsContract.CommonDataKinds.Email.CONTENT_URI,
									new String[] {
											ContactsContract.CommonDataKinds.Email.DATA,
											ContactsContract.CommonDataKinds.Email.TYPE,
											ContactsContract.CommonDataKinds.Email.DISPLAY_NAME},
									ContactsContract.CommonDataKinds.Email.CONTACT_ID
											+ "='" + contactId + "'", null,
									null);
					while (emailCursor.moveToNext()) {
						String email = emailCursor
								.getString(emailCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						String emailType = emailCursor
								.getString(emailCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
						String name = emailCursor
						.getString(emailCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME));
						Map<String, String> map = new HashMap<String, String>();
						switch (Integer.parseInt(emailType)) {
						case 1:
							map.put(EMAIL, email);
							map.put(NAME, name);
							map.put(TYPE, "Home");
							break;
						case 2:
							map.put(EMAIL, email);
							map.put(NAME, name);
							map.put(TYPE, "Work");
							break;
						case 3:
							map.put(EMAIL, email);
							map.put(NAME, name);
							map.put(TYPE, "Other");
							break;
						case 4:
							map.put(EMAIL, email);
							map.put(NAME, name);
							map.put(TYPE, "Mobile");
							break;
						case 5:
							map.put(EMAIL, email);
							map.put(NAME, name);
							map.put(TYPE, "Custom");
							break;
						}
						emails.add(map);
					}
					emailCursor.close();
				}
			}
			cursor.close();

			if (type == PHONE_CONTACTS)
				setListAdapter(new IconicPhoneAdapter(this));
			else
				setListAdapter(new IconicEmailAdapter(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPhoneType(String type) {
		int iType = Integer.valueOf(type);
		if (iType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
			return "Mobile";
		if (iType == ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
			return "Home";
		if (iType == ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
			return "Work";
		else
			return "Other";
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (type == PHONE_CONTACTS) {
			Bundle bundle = new Bundle();

			bundle.putString(NAME, phoneNumbers.get(position).get(NAME));
			bundle.putString(NUMBER, phoneNumbers.get(position).get(NUMBER));
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);

			finish();
		} else {
			Bundle bundle = new Bundle();

			bundle.putString(NAME, emails.get(position).get(NAME));
			bundle.putString(EMAIL, emails.get(position).get(EMAIL));
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);

			finish();
		}
	}

	class IconicPhoneAdapter extends ArrayAdapter {
		Activity context;

		@SuppressWarnings("unchecked")
		IconicPhoneAdapter(Activity context) {
			super(context, R.layout.score, phoneNumbers);

			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = context.getLayoutInflater();

				row = inflater.inflate(R.layout.score, null);
			}

			TextView labelScore = (TextView) row.findViewById(R.id.lblScore);
			TextView labelUser = (TextView) row.findViewById(R.id.lblUser);
			TextView labelType = (TextView) row.findViewById(R.id.lblType);

			labelUser.setText(phoneNumbers.get(position).get(NAME));
			labelScore.setText(phoneNumbers.get(position).get(NUMBER));
			labelType.setText(phoneNumbers.get(position).get(TYPE));

			return (row);
		}
	}

	class IconicEmailAdapter extends ArrayAdapter {
		Activity context;

		@SuppressWarnings("unchecked")
		IconicEmailAdapter(Activity context) {
			super(context, R.layout.score, emails);

			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = context.getLayoutInflater();

				row = inflater.inflate(R.layout.score, null);
			}

			TextView labelScore = (TextView) row.findViewById(R.id.lblScore);
			TextView labelUser = (TextView) row.findViewById(R.id.lblUser);
			TextView labelType = (TextView) row.findViewById(R.id.lblType);

			labelUser.setText(emails.get(position).get(NAME));
			labelScore.setText(emails.get(position).get(EMAIL));
			labelType.setText(emails.get(position).get(TYPE));

			return (row);
		}
	}
}