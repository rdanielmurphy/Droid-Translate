package app.DroidTranslate;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;


public class Translation {
	public String sFrom;
	public String sTo;
	public String sOrig;
	public String sTranslated;
	
	public Translation(String from, String to, String orig,
					String translated){
			sFrom = from;
			sTo = to;
			sOrig = orig;
			sTranslated = translated;
	}
}
