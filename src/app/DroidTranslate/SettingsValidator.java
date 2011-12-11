package app.DroidTranslate;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsValidator {
	private static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREFS_LANG_FROM = "langFrom";
	private static final String PREFS_LANG_TO = "langTo";
	private static final String PREFS_CONJ_LANG = "langConj";
		
	private SettingsValidator(){
	}
		
	public static void setDefaultLangFrom(String value, Context c){
		SharedPreferences settings;
		SharedPreferences.Editor editor;
		settings = c.getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		editor.putString(PREFS_LANG_FROM, value);	
		editor.commit();
	}

	public static void setDefaultLangTo(String value, Context c){
		SharedPreferences settings;
		SharedPreferences.Editor editor;
		settings = c.getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		editor.putString(PREFS_LANG_TO, value);
		editor.commit();
	}
	
	public static void setDefaultConjLang(String value, Context c){
		SharedPreferences settings;
		SharedPreferences.Editor editor;
		settings = c.getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		editor.putString(PREFS_CONJ_LANG, value);
		editor.commit();
	}

	public static String getDefaultLangFrom(Context c){
		SharedPreferences settings;
		settings = c.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREFS_LANG_FROM, LanguageValidator.getListOfTranslationLanguagesSupported().get(0));
	}

	public static String getDefaultLangTo(Context c){
		SharedPreferences settings;
		settings = c.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREFS_LANG_TO, LanguageValidator.getListOfTranslationLanguagesSupported().get(0));
	}
	
	public static String getDefaultConjLang(Context c){
		SharedPreferences settings;
		settings = c.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREFS_CONJ_LANG, LanguageValidator.getListOfConjugationLanguagesSupported().get(0));	
	}
}

