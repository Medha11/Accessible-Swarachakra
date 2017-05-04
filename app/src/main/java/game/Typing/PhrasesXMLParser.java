package game.Typing;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class PhrasesXMLParser {

	// names of the XML tags

	public String CATEGORY = "category";
	public String TEXT = "text";
	public String LEVEL = "level";
	public String ID = "id";
	public String MESSAGE = "message";
	public String EDITOR = "editor";
	public String COUNT = "count";
	public String PHRASEID="phraseID";
	public String WORDCOUNT="wordCount";
	public String CHARCOUNT="charCount";
	public String ALTERNATIVES = "alternatives";
		
	ArrayList<Phrases> phrasesList = null;
	private Phrases currentPhrase = null;
															
	private boolean done = false;
	private String currentTag = null;
	private String currentCategory = null;
	private String currentLevelText = null;
	private String currentLevelCount = null;
	private int currentId = 0;
	private int currentPhraseId = 0;
	private int currentWordCount = 0;
	private int currentCharCount = 0;
	private String currentAlternatives = null;
	

	public ArrayList<Phrases> parse(Context context)

	{

		XmlPullParser parser = context.getResources().getXml(R.xml.marathi);
		try {

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT && !done) 
			{
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					phrasesList = new ArrayList<Phrases>();
					break;
				case XmlPullParser.START_TAG:
					currentTag = parser.getName();
					if (currentTag.equalsIgnoreCase(EDITOR)) 
					{

					} 
					else if (currentTag.equalsIgnoreCase(CATEGORY)) 
					{
						currentCategory = parser.getAttributeValue(null, TEXT);
					} 
					else if (currentTag.equalsIgnoreCase(LEVEL)) 
					{
						currentLevelText = parser.getAttributeValue(null, TEXT);
						currentLevelCount = parser.getAttributeValue(null,COUNT);
					} 
					else if (currentTag.equalsIgnoreCase(MESSAGE)) 
					{
						currentId = Integer.parseInt(parser.getAttributeValue(null, ID));
						currentPhraseId=Integer.parseInt(parser.getAttributeValue(null, PHRASEID));
						currentWordCount=Integer.parseInt(parser.getAttributeValue(null, WORDCOUNT));
						currentCharCount=Integer.parseInt(parser.getAttributeValue(null, CHARCOUNT));
						currentAlternatives = parser.getAttributeValue(null, ALTERNATIVES);

						//currentPhrase = new Phrases("", currentCategory, "",currentCategory, currentLevelText, currentId,parser.nextText(),currentPhraseId,currentWordCount,currentCharCount);
						currentPhrase = new Phrases("", currentCategory, currentLevelText,"", "", currentId,parser.nextText(), currentAlternatives, currentPhraseId,currentWordCount,currentCharCount);
						phrasesList.add(currentPhrase);
					}
					break;
				case XmlPullParser.END_TAG:
					
					break;
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return phrasesList;
	}

}
