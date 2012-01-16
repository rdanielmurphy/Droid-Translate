package app.DroidTranslate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

//To support new languages: add a public constant and add the languages' properties to the 
//languages list.  Then add it to the arrays.xml file.
public class LanguageValidator {
	// Private Constants
	private static final String _NAME = "name";
	private static final String _ABBR = "abbr";
	private static final String _LOCALE = "locale";
	private static final String _CONJ = "conjugation";
	private static final String _LANG_FROM = "language_orig";
	private static final String _LANG_TO = "language_trans";
	private static final String _IMAGE = "icon";

	// Internal Lists
	private static List<Map<String, Object>> _languages;
	private static Map<String, Map<String, ConjugatedVerb>> _conjugations;

	// Public Constants
	public static final String ENGLISH_LANG = "English";
	public static final String SPANISH_LANG = "Spanish";
	public static final String FRENCH_LANG = "French";
	public static final String ITALIAN_LANG = "Italian";
	public static final String ENGLISH_LANG_ABBR = "en";
	public static final String SPANISH_LANG_ABBR = "es";
	public static final String FRENCH_LANG_ABBR = "fr";
	public static final String ITALIAN_LANG_ABBR = "it";

	private static final String _INSERT_VERB = "#INSERT_VERB#";

	private static String _appId;

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
		language.put(_ABBR, ENGLISH_LANG_ABBR);
		language.put(_LOCALE, Locale.US);
		language.put(_CONJ, "http://conjugator.reverso.net/conjugation-english-verb-" + _INSERT_VERB + ".html");
		language.put(_IMAGE, R.drawable.eng);
		_languages.add(language);

		// Spanish
		language = new HashMap<String, Object>();
		language.put(_NAME, SPANISH_LANG);
		language.put(_ABBR, SPANISH_LANG_ABBR);
		language.put(_LOCALE, new Locale("spa", "ESP"));
		language.put(_CONJ, "http://www.wordreference.com/conj/EsVerbs.asp?v=" + _INSERT_VERB);
		language.put(_IMAGE, R.drawable.spa);
		_languages.add(language);

		// French
		language = new HashMap<String, Object>();
		language.put(_NAME, FRENCH_LANG);
		language.put(_ABBR, FRENCH_LANG_ABBR);
		language.put(_LOCALE, Locale.FRANCE);
		language.put(_CONJ, "http://www.wordreference.com/conj/FrVerbs.asp?v=" + _INSERT_VERB);
		language.put(_IMAGE, R.drawable.fre);
		_languages.add(language);

		// Italian
		language = new HashMap<String, Object>();
		language.put(_NAME, ITALIAN_LANG);
		language.put(_ABBR, ITALIAN_LANG_ABBR);
		language.put(_LOCALE, Locale.ITALIAN);
		language.put(_CONJ, "http://www.wordreference.com/conj/ITverbs.asp?v=" + _INSERT_VERB);
		language.put(_IMAGE, R.drawable.ital);
		_languages.add(language);
	}

	public static Locale getLocale(String language) {
		if (_languages == null)
			initLanguageList();
		for (Map<String, Object> entry : _languages) {
			if (entry.get(_NAME).equals(language) || entry.get(_ABBR).equals(language))
				return (Locale) entry.get(_LOCALE);
		}
		return null;
	}

	public static String getLanguageString(String language) {
		if (_languages == null)
			initLanguageList();
		for (Map<String, Object> entry : _languages) {
			if (entry.get(_NAME).equals(language) || entry.get(_ABBR).equals(language))
				return entry.get(_ABBR).toString();
		}
		return null;
	}

	public static String getWebSite(String language, String verb) {
		if (_languages == null)
			initLanguageList();
		String site = "";
		for (Map<String, Object> entry : _languages) {
			if (entry.get(_NAME).equals(language) || entry.get(_ABBR).equals(language))
				site = (String) entry.get(_CONJ);
		}

		if (site.contains(_INSERT_VERB))
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

	private static void readConjugationFile(InputStream stream) {
		InputStreamReader streamReader = new InputStreamReader(stream);

		CSVReader reader = null;
		try {
			reader = new CSVReader(streamReader);
			String[] nextLine = reader.readNext();// skip column names
			while ((nextLine = reader.readNext()) != null) {
				System.out.println(nextLine[0]);
				ConjugatedVerb verb = new ConjugatedVerb(nextLine[0]);
				verb.setTense(ConjugatedVerb.IndicativePresent, nextLine);
				verb.setTense(ConjugatedVerb.IndicativeFuture, reader.readNext());
				verb.setTense(ConjugatedVerb.IndicativeImperfect, reader.readNext());
				verb.setTense(ConjugatedVerb.IndicativePreterite, reader.readNext());
				verb.setTense(ConjugatedVerb.IndicativeConditional, reader.readNext());
				verb.setTense(ConjugatedVerb.IndicativePresentPerfect, reader.readNext());
				verb.setTense(ConjugatedVerb.IndicativeFuturePerfect, reader.readNext());
				verb.setTense(ConjugatedVerb.IndicativePastPerfect, reader.readNext());
				verb.setTense(ConjugatedVerb.PreteriteArchaic, reader.readNext());
				verb.setTense(ConjugatedVerb.IndicativeConditionalPerfect, reader.readNext());
				verb.setTense(ConjugatedVerb.SubjunctivePresent, reader.readNext());
				verb.setTense(ConjugatedVerb.SubjunctiveImperfect, reader.readNext());
				verb.setTense(ConjugatedVerb.SubjunctiveFuture, reader.readNext());
				verb.setTense(ConjugatedVerb.SubjunctivePresentPerfect, reader.readNext());
				verb.setTense(ConjugatedVerb.SubjunctiveFuturePerfect, reader.readNext());
				verb.setTense(ConjugatedVerb.SubjunctivePastPerfect, reader.readNext());
				verb.setTense(ConjugatedVerb.ImperativeAffirmativePresent, reader.readNext());
				verb.setTense(ConjugatedVerb.ImperativeNegativePresent, reader.readNext());
				verb.setVerbDefinition(nextLine[1]);
				_conjugations.get(SPANISH_LANG_ABBR).put(nextLine[0], verb);
			}
		} catch (Exception e) {
			Log.e("Could not read Spanish verb db", e.getMessage());
		} finally {

		}
	}

	private static void buildConjugationMap(Context c) {
		if (_conjugations == null) {
			_conjugations = new HashMap<String, Map<String, ConjugatedVerb>>();

			// spanish verbs
			_conjugations.put(SPANISH_LANG_ABBR, new HashMap<String, ConjugatedVerb>());
			readConjugationFile(c.getResources().openRawResource(R.raw.spanish_verb_db1));
			readConjugationFile(c.getResources().openRawResource(R.raw.spanish_verb_db2));
			readConjugationFile(c.getResources().openRawResource(R.raw.spanish_verb_db3));
		}
	}

	private static String httpGet(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();
		return sb.toString();
	}

	private static String parseXML(String xml) throws Exception {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
		NodeList translations = doc.getElementsByTagName("string");
		String item = translations.item(0).getTextContent().toString();

		return item;
	}

	public static String translateString(String languageFrom, String languageTo, String text) throws Exception {
		String uri = "http://api.microsofttranslator.com/v2/Http.svc/Translate?appId=" + _appId + "&text=" + java.net.URLEncoder.encode(text) + "&from="
				+ languageFrom + "&to=" + languageTo;

		return parseXML(httpGet(uri));
	}

	public static void initializeLanguageEngine(Context c) {
		buildConjugationMap(c);
		initializeBingAPI(c);
	}

	private static void initializeBingAPI(Context c) {
		InputStreamReader streamReader = new InputStreamReader(c.getResources().openRawResource(R.raw.app_id));
		BufferedReader reader = new BufferedReader(streamReader);

		try {
			_appId = reader.readLine();
		} catch (Exception e) {
			//won't happen
		}
	}

	public static ConjugatedVerb conjugateVerb(String language, String verb) {
		String abbr = getLanguageString(language);
		if (_conjugations.containsKey(abbr) && _conjugations.get(abbr).containsKey(verb))
			return _conjugations.get(abbr).get(verb);

		return null;
	}
}
