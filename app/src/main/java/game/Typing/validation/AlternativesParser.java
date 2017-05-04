package game.Typing.validation;

import game.Typing.Parsers.xmlParsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//import org.apache.http.cookie.SetCookie2;

import android.content.Context;
import android.util.Log;

public class AlternativesParser {

	HashMap<String,String> alternatives;
	xmlParsers parser;
	Context context;
	
	public AlternativesParser(Context c) {
		// TODO Auto-generated constructor stub
		context = c;
		alternatives = new HashMap<String, String>();
		parser = new xmlParsers();
		alternatives = parser.getAlternativesFromXML(context);
		Log.d("alt","XML called: "+alternatives.size());
		
		Set<Entry<String,String>> mapEntry= alternatives.entrySet();
		Iterator<Entry<String,String>> iterator = mapEntry.iterator();
		
		while (iterator.hasNext()) {
			Entry e= iterator.next();
			String key = (String) e.getKey();
			String value = (String) e.getValue();
			
			Log.d("alt","key: "+key + " value: "+value);
			
		}
		
		
		/*alternatives.put("\u0930\u094d\u200d", "\u0931\u094d"); 
		alternatives.put("\u0931\u094d\u200d", "\u0931\u094d"); 
		alternatives.put("\u0931\u094d", "\u0931\u094d"); 
		alternatives.put("\u0930\u093c\u094d\u200d", "\u0931\u094d"); 
		alternatives.put("\u0930\u093c\u094d", "\u0931\u094d"); 
		alternatives.put("\u0930\u094d\u093c", "\u0931\u094d"); 

		alternatives.put("\u0020\u0020\u0020", "\u0020");
		alternatives.put("\u0020\u0020", "\u0020");
		
		alternatives.put("\u0901", "\u0945");
		
		alternatives.put("\u0945\u093e", "\u0949");
		alternatives.put("\u093e\u0945", "\u0949");
		
		alternatives.put("\u0947\u093e", "\u094b");
		alternatives.put("\u093e\u0947", "\u094b");
		
		alternatives.put("\u0948\u093e", "\u094c");
		alternatives.put("\u093e\u0948", "\u094c");*/
	}
	
	public String replaceAlternatives(String alt){
		alt =alt.trim();
		if(alt == null || alt == "" || alt.length()==0)
			return alt;
		//Log.d("alt-before",alt);
		Set<Entry<String,String>> mapEntry= alternatives.entrySet();
		Iterator<Entry<String,String>> iterator = mapEntry.iterator();
		
		while (iterator.hasNext()) {
			Entry e= iterator.next();
			String key = (String) e.getKey();
			String value = (String) e.getValue();
			
			if(alt.contains(key))
			{
				alt=alt.replaceAll(key, value);
			}
			
		}
		//Log.d("alt-after",alt);
		return alt;
	}
	
	
}