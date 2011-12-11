package app.DroidTranslate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.api.translate.Language;

//To support new languages: add a public constant and add the languages' properties to the 
//languages list.  Then add it to the arrays.xml file.
public class LanguageValidator {
	// Private Constants
	private static final String _NAME = "name";
	private static final String _ABBR = "abbr";
	private static final String _LOCALE = "locale";
	private static final String _LANG = "language";
	private static final String _CONJ = "conjugation";
	private static final String _LANG_FROM = "language_orig";
	private static final String _LANG_TO = "language_trans";
	private static final String _IMAGE = "icon";

	// Internal Lists
	private static List<Map<String, Object>> _languages;
	private static List<Map<String, Object>> _icons;

	// Public Constants
	public static final String ENGLISH_LANG = "English";
	public static final String SPANISH_LANG = "Spanish";
	public static final String FRENCH_LANG = "French";
	public static final String ITALIAN_LANG = "Italian";

	private static final String _INSERT_VERB = "#INSERT_VERB#";

	// Private Constructor. Do not call.
	private LanguageValidator() {
	}

	// Initialize Internal Language List
	private static void initLanguageList() {
		_languages = new ArrayList<Map<String, Object>>();

		Map<String, Object> language;
		// English
		language = new HashMap<String, Object>();
		language.put(_NAME, ENGLISH_LANG);
		language.put(_ABBR, "en");
		language.put(_LOCALE, Locale.US);
		language.put(_LANG, Language.ENGLISH);
		language.put(_CONJ,
				"http://conjugator.reverso.net/conjugation-english-verb-"
						+ _INSERT_VERB + ".html");
		language.put(_IMAGE, R.drawable.eng);
		_languages.add(language);

		// Spanish
		language = new HashMap<String, Object>();
		language.put(_NAME, SPANISH_LANG);
		language.put(_ABBR, "es");
		language.put(_LOCALE, new Locale("spa", "ESP"));
		language.put(_LANG, Language.SPANISH);
		language.put(_CONJ, "http://www.wordreference.com/conj/EsVerbs.asp?v=" + _INSERT_VERB);
		language.put(_IMAGE, R.drawable.spa);
		_languages.add(language);

		// French
		language = new HashMap<String, Object>();
		language.put(_NAME, FRENCH_LANG);
		language.put(_ABBR, "fr");
		language.put(_LOCALE, Locale.FRANCE);
		language.put(_LANG, Language.FRENCH);
		language.put(_CONJ, "http://www.wordreference.com/conj/FrVerbs.asp?v=" + _INSERT_VERB);
		language.put(_IMAGE, R.drawable.fre);
		_languages.add(language);

		// French
		language = new HashMap<String, Object>();
		language.put(_NAME, ITALIAN_LANG);
		language.put(_ABBR, "it");
		language.put(_LOCALE, Locale.ITALIAN);
		language.put(_LANG, Language.ITALIAN);
		language.put(_CONJ, "http://www.wordreference.com/conj/ITverbs.asp?v=" + _INSERT_VERB);
		language.put(_IMAGE, R.drawable.ital);
		_languages.add(language);
	}

	// Initialize Internal Icon List
	private static void initIconList() {
		_icons = new ArrayList<Map<String, Object>>();

		Map<String, Object> icon;
		// English to Spanish
		icon = new HashMap<String, Object>();
		icon.put(_LANG_FROM, ENGLISH_LANG);
		icon.put(_LANG_TO, SPANISH_LANG);
		icon.put(_IMAGE, R.drawable.eng_to_span);
		_icons.add(icon);

		// Spanish to English
		icon = new HashMap<String, Object>();
		icon.put(_LANG_FROM, SPANISH_LANG);
		icon.put(_LANG_TO, ENGLISH_LANG);
		icon.put(_IMAGE, R.drawable.span_to_eng);
		_icons.add(icon);

		// English to French
		icon = new HashMap<String, Object>();
		icon.put(_LANG_FROM, ENGLISH_LANG);
		icon.put(_LANG_TO, FRENCH_LANG);
		icon.put(_IMAGE, R.drawable.eng_to_fren);
		_icons.add(icon);

		// French to English
		icon = new HashMap<String, Object>();
		icon.put(_LANG_FROM, FRENCH_LANG);
		icon.put(_LANG_TO, ENGLISH_LANG);
		icon.put(_IMAGE, R.drawable.fren_to_eng);
		_icons.add(icon);

		// Spanish to French
		icon = new HashMap<String, Object>();
		icon.put(_LANG_FROM, SPANISH_LANG);
		icon.put(_LANG_TO, FRENCH_LANG);
		icon.put(_IMAGE, R.drawable.span_to_fren);
		_icons.add(icon);

		// French to Spanish
		icon = new HashMap<String, Object>();
		icon.put(_LANG_FROM, FRENCH_LANG);
		icon.put(_LANG_TO, SPANISH_LANG);
		icon.put(_IMAGE, R.drawable.span_to_eng);
		_icons.add(icon);
	}

	public static Locale getLocale(String language) {
		if (_languages == null)
			initLanguageList();
		for (Map<String, Object> entry : _languages) {
			if (entry.get(_NAME).equals(language)
					|| entry.get(_ABBR).equals(language))
				return (Locale) entry.get(_LOCALE);
		}
		return null;
	}

	public static Language getLanguageObject(String language) {
		if (_languages == null)
			initLanguageList();
		for (Map<String, Object> entry : _languages) {
			if (entry.get(_NAME).equals(language)
					|| entry.get(_ABBR).equals(language))
				return (Language) entry.get(_LANG);
		}
		return null;
	}

	public static String getWebSite(String language, String verb){
		if (_languages == null)
			initLanguageList();
		String site = "";
		for(Map<String,Object> entry :_languages){
			if(entry.get(_NAME).equals(language)  || entry.get(_ABBR).equals(language))
				site = (String)entry.get(_CONJ);
		}
		
		if(site.contains(_INSERT_VERB))
			site = site.replace(_INSERT_VERB, verb);
		
		return site;
	}

	public static List<String> getListOfTranslationLanguagesSupported() {
		if (_languages == null)
			initLanguageList();
		List<String> languages = new ArrayList<String>();
		for (Map<String, Object> entry : _languages) {
			languages.add((String) entry.get(_NAME));
		}

		return languages;
	}

	public static List<String> getListOfConjugationLanguagesSupported() {
		if (_languages == null)
			initLanguageList();
		List<String> languages = new ArrayList<String>();
		for (Map<String, Object> entry : _languages) {
			if (!entry.get(_CONJ).equals(""))
				languages.add((String) entry.get(_NAME));
		}

		return languages;
	}

	public static int getLanguageIcon(String language) {
		if (_languages == null)
			initLanguageList();
		for (Map<String, Object> entry : _languages) {
			if (entry.get(_NAME).equals(language))
				return (Integer) entry.get(_IMAGE);
		}
		return R.drawable.icon;
	}
}
