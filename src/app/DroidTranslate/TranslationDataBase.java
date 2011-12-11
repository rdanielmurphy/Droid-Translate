package app.DroidTranslate;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class TranslationDataBase extends ListActivity {

	private static final String DATABASE_NAME = "Translation.db";
	private static final String TRANSLATION_TABLE = "SavedTranslations";
	private static final int DATABASE_VERSION = 1;

	private static final String KEY_LANG_FROM = "langfrom";
	private static final String KEY_LANG_TO = "langto";
	private static final String KEY_ORIG = "orig";
	private static final String KEY_TRANSLATED = "translated";

	private static final String DATABASE_CREATE = "create table "
			+ TRANSLATION_TABLE + " (" + KEY_LANG_FROM + " text not null, " 
			+ KEY_LANG_TO
			+ " text not null, " + KEY_ORIG + " text not null, "
			+ KEY_TRANSLATED + " text not null);";

	private SQLiteDatabase db;
	private final Context context;
	private MyDbHelper myDbHelper;

	// constructor create the wrapper to open and close the db
	public TranslationDataBase(Context cxt) {
		context = cxt;
		myDbHelper = new MyDbHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public TranslationDataBase open() throws SQLException {

		db = myDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
		db.delete(TRANSLATION_TABLE, null, null);
	}

	public List<Translation> getEntries() {
		List<Translation> list = new ArrayList<Translation>();
		Translation tTranslation;

		Cursor c = db.query(TRANSLATION_TABLE, null, null,
				null, null, null, null);

		int iNumOfEntries = c.getCount();
		c.moveToFirst();
		for (int i = 0; i < iNumOfEntries; i++) {
			int columnLangFrom = c.getColumnIndex(KEY_LANG_FROM);
			int columnLangTo= c.getColumnIndex(KEY_LANG_TO);
			int columnOrig = c.getColumnIndex(KEY_ORIG);
			int columnTranslated = c.getColumnIndex(KEY_TRANSLATED);
			
			tTranslation = new Translation(c.getString(columnLangFrom), c
					.getString(columnLangTo), c.getString(columnOrig), c.getString(columnTranslated));

			list.add(tTranslation);
			
			c.moveToNext();
		}
		return list;
	}

	public int NumberOfEntries() {
		Cursor c = db.query(TRANSLATION_TABLE, null, null,
				null, null, null, null);

		return c.getCount();
	}
	
	public void AddTranslation(Translation tTranslation) {
		ContentValues cv = new ContentValues();

		cv.put(KEY_LANG_FROM, tTranslation.sFrom);
		cv.put(KEY_LANG_TO, tTranslation.sTo);
		cv.put(KEY_ORIG,tTranslation.sOrig);
		cv.put(KEY_TRANSLATED, tTranslation.sTranslated);
		db.insert(TRANSLATION_TABLE, null, cv);		
	}
	
	public void DeleteTranslation(Translation tTranslation) {
		String deletequery[] = {tTranslation.sFrom, tTranslation.sTo, tTranslation.sOrig};
		String whereClause = KEY_LANG_FROM + "=? AND " + KEY_LANG_TO + "=? AND " + KEY_ORIG + "=?";
		db.delete(TRANSLATION_TABLE,whereClause, deletequery);
	}

	private static class MyDbHelper extends SQLiteOpenHelper {
		public MyDbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		// Only gets called if the database does not exist on the phone
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}

		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// Drop old one
			_db.execSQL("DROP TABLE IF EXISTS " + TRANSLATION_TABLE);
			// Create new one
			onCreate(_db);
		}
		
		public void onOpen(SQLiteDatabase db){
			super.onOpen(db);
		}
	}
}