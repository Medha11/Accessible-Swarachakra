package game.Typing;


public class Phrases {
	String _editor;
	String _category;
	String _level;
	//String _text;
	//String _text1;
	String _message;
	String _alternatives;
	Integer _id;
	Integer _phraseID;
	Integer _wordCount;
	Integer _charCount;
	

	//Constructor
	public Phrases()
	{
		
	}
	
	
	// constructor with parameters
	public Phrases(String editor,String category,String level, String text,String text1,Integer id, String message,String alternatives, Integer phraseID,Integer wordCount,Integer charCount)
	{
		this._editor=editor;
		this._category=category;
		this._level=level;
		//this._text=text;
		//this._text1=text1;
		this._id=id;
		this._message=message;
		this._alternatives = alternatives;
		this._phraseID=phraseID;
		this._wordCount=wordCount;
		this._charCount=charCount;
	}
	
	
	//all set methods
	public void setEditor(String editor)
	{
		this._editor=editor;
	}
	
	public void setCategory(String category)
	{
		this._category=category;
	}
	
	public void setLevel(String level)
	{
		this._level=level;
	}
	
	/*public void setCategoryText(String text) {
		// TODO Auto-generated method stub
		this._text=text;
		
	}

	public void setLevelText(String text1) {
		// TODO Auto-generated method stub
		this._text1=text1;
	}*/

	public void setId(int id) {
		// TODO Auto-generated method stub
		this._id=id;
	}


	public void setMessage(String message) {
		// TODO Auto-generated method stub
		this._message=message;
	}
	
	public void setAlternatives(String alt) {
		// TODO Auto-generated method stub
		this._alternatives=alt;
	}
	
	public void setphraseID(Integer phraseID) {
		// TODO Auto-generated method stub
		this._phraseID=phraseID;
	}
	public void setwordCount(Integer wordCount) {
		// TODO Auto-generated method stub
		this._wordCount=wordCount;
	}
	public void setcharCount(Integer charCount) {
		// TODO Auto-generated method stub
		this._charCount=charCount;
	}

	//all get methods
	public String getEditor()
	{
		return this._editor;
	}
	public String getCategory()
	{
		return this._category;
	}
	
	public String setLevel()
	{
		return this._level;
	}
		
	/*public String gettext()
	{
		return this._text;
	}
	
	public String gettext1()
	{
		return this._text1;
	}*/
	
	public Integer id()
	{
		return this._id;
	}
	
	public String message()
	{
		return this._message;
	}
	public Integer phraseID()
	{
		return this._phraseID;
	}
	public Integer wordCount()
	{
		return this._wordCount;
	}
	public Integer charCount()
	{
		return this._charCount;
	}

	 @Override
	    public String toString() 
	 {		 
		 return "Category: "+_category+"\n"+"Level:  "+_level+"\n"+_id+"  "+_message+"\nPhrase ID:  "+_phraseID+"\nWord Count:  "+_wordCount+"\nCharCount:  "+_charCount;
//		 return "Message:"+_message;
	 }
	 
	
}
