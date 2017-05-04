package game.Typing;

import game.Typing.validation.Validator;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SessionDetailsTable {

	//sessiondetails table
	public static final String USER_ID = "u_id";
	public static final String SESSION_ID = "s_id";
	public static final String USER_SESSION_TYPE = "session_type";
	public static final String PHRASE_NO = "phrase_no";	
	public static final String PHRASE_XML_ID = "phrase_xml_id ";

	public static final String FIRST_CHAR_TIMESTAMP = "f_timestamp";
	public static final String LAST_CHAR_TIMESTAMP = "l_timestamp";
	public static final String PHRASE_TYPED = "phrase_typed";
	//public static final String PHRASE_CHAR_COUNT = "phrase_char_count";
	public static final String EDIT_DISTANCE = "edit_distance";
	public static final String ATTEMPT_NUMBER= "attempt_number";

	public static final String TABLE_SESSION_PHRASE_MAP = "session_details_table";

	//public static final String SESSIONDONE = "sessionstatus";

	//sessiontable
	//public static final String USER_ID = "u_id";
	//public static final String SESSION_ID = "s_id";
	public static final String SESSION_TYPE = "session_type";
	//public static final String PHRASE_SHOWN = "phrase_shown";
	public static final String SESSION_RATING = "session_rating";
	public static final String SESSION_STATUS= "session_status";
	public static final String SESSION_START = "session_start";
	public static final String SESSION_END = "session_end";
	public static final String SESSION_CONTINUE_FROM = "continue_from";
	public static final int SESSION_STATUS_DONE = 2;
	public static final int SESSION_STATUS_STARTED = 1;
	public static final int SESSION_STATUS_NOTSTARTED = 0;
	public static final String KEYSTROKE_LOG = "keystroke_logs";
	//public static final String SESSIONDONE = "sessionstatus";

	public static final String SESSION_TABLE = "session_table";

	/*private static final String DATABASE_NAME = "UT_Marathi_main2";
	private static final int DATABASE_VERSION = 1;

	public static int TRAINING_SESSIONID = 0;
	public static int FTU_SESSIONID = 1;
	public static int TRAINING_PHRASE_XMLID_START = 401;
	public static int FTU_PHRASE_XMLID_START = 501;*/


	/*private static final String DATABASE_NAME = "UT_Marathi_main";
	private static final int DATABASE_VERSION = 1;*/

	/*public static int TRAINING_SESSIONTYPE = 0;
	public static int FTU_SESSIONNO = 1;
	public static int LTU_SESSION_START = 2;
	//public static int TOTAL_SESSIONS = 31;
	public static int TRAINING_PHRASE_XMLID_START = 401;
	public static int FTU_PHRASE_XMLID_START = 501;
	public static int MAX_TRAININGS = 3;
	public static int MAX_FTU = 1;
	public static int MAX_PHRASES_PER_SESSION = 10;
	public static int TRAINING_WORD_REPEAT_COUNT = 2;*/
	public static String SYNCED = "synched";
	public static int NOTSYNCED = 0;


	//Phrases table
	public static String PHRASE_TABLE = "phraseTable";
	public static String PHRASE_ID = "phrase_id";
	public static String PHRASE_CATEGORY = "phrase_category";
	public static String PHRASE_LEVEL = "phrase_level";
	public static String PHRASE_SEQUENCE_NO = "phrase_seq_no";
	public static String PHRASE = "phrase";
	public static String PHRASE_ALTERNATIVE = "phrase_alternative";
	public static String PHRASE_WORD_COUNT = "phrase_word_count";
	public static String PHRASE_CHAR_COUNT = "phrase_char_count";

	public static String PHRASE_CATEGORY_WORDS = "words";
	public static String PHRASE_CATEGORY_SENTENCES = "sentence";

	public static String PHRASE_LEVEL_PRACTICE = "practice";
	public static String PHRASE_LEVEL_MAIN = "main";

	public static String PHRASE_LEVEL_EASY = "easy";
	public static String PHRASE_LEVEL_MEDIUM = "medium";
	public static String PHRASE_LEVEL_DIFFICULT = "hard";


	private final Context context;
	public SQLiteDatabase ourDatabase;  //private
	private DbHelper ourHelper;
	public List<Phrases> editor;

	public static boolean phraseTableFlag;

	public static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, ApplicationConstants.DATABASE_NAME, null, ApplicationConstants.DATABASE_VERSION);

			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			try{
				db.execSQL("CREATE TABLE " + TABLE_SESSION_PHRASE_MAP + " (" 
						+ USER_ID + " INTEGER NOT NULL, " 
						+ SESSION_ID + " INTEGER, " 
						+ USER_SESSION_TYPE + " INTEGER NOT NULL, " 
						+ PHRASE_NO + " INTEGER NOT NULL, "
						+ PHRASE_XML_ID + " INTEGER NOT NULL, " 
						/*+ PHRASE_SHOWN + " INTEGER DEFAULT 0, "*/
						+ FIRST_CHAR_TIMESTAMP + " INTEGER DEFAULT 0, " 
						+ LAST_CHAR_TIMESTAMP + " INTEGER DEFAULT 0, " 
						+ PHRASE_TYPED + " TEXT DEFAULT null, "
						+ KEYSTROKE_LOG + " TEXT DEFAULT null, "
						+ EDIT_DISTANCE + " INTEGER DEFAULT 0, "
						+ ATTEMPT_NUMBER + " INTEGER DEFAULT 1, "
						+ SYNCED + " INTEGER DEFAULT 0);");  //, PRIMARY KEY( " + USER_ID + ", " + USER_SESSION_TYPE + ", " + PHRASE_NO + " ) + PHRASE_CHAR_COUNT + " INTEGER , " 
				Log.d("logger",TABLE_SESSION_PHRASE_MAP + " created");

			}catch(SQLException sqlex){
				sqlex.printStackTrace();
				Log.d("logger",TABLE_SESSION_PHRASE_MAP+" creation failed");

			}

			try{
				db.execSQL("CREATE TABLE " + SESSION_TABLE + " (" 
						+ USER_ID + " INTEGER NOT NULL, " 
						+ SESSION_ID + " INTEGER, "  // PRIMARY KEY AUTOINCREMENT
						+ SESSION_TYPE + " INTEGER, " 
						+ SESSION_RATING + " INTEGER DEFAULT 0, "
						+ SESSION_START + " INTEGER DEFAULT 0, "  
						+ SESSION_END + " INTEGER DEFAULT 0, " 
						+ SESSION_STATUS + " INTEGER default 0, "
						+ SESSION_CONTINUE_FROM + " INTEGER default 1, "
						+ SYNCED + " INTEGER DEFAULT 0);");//, PRIMARY KEY( " + USER_ID + ", " + USER_SESSION_TYPE + ", " + PHRASE_NO + " )
				Log.d("logger",SESSION_TABLE +" created");

			}catch(SQLException sqlex){
				sqlex.printStackTrace();
				Log.d("logger",SESSION_TABLE+" creation failed");					
			}

			try{				
				db.execSQL("CREATE TABLE " + PHRASE_TABLE + " (" 
						+ PHRASE_ID + " INTEGER NOT NULL PRIMARY KEY, " 
						+ PHRASE_CATEGORY + " TEXT, " 
						+ PHRASE_LEVEL + " TEXT, " 
						+ PHRASE_SEQUENCE_NO + " INTEGER, " 
						+ PHRASE + " TEXT, "
						+ PHRASE_ALTERNATIVE + " TEXT, "
						+ PHRASE_WORD_COUNT + " INTEGER, "
						+ PHRASE_CHAR_COUNT + " INTEGER);");  //, PRIMARY KEY( " + USER_ID + ", " + USER_SESSION_TYPE + ", " + PHRASE_NO + " )
				Log.d("logger",PHRASE_TABLE + " created");
				phraseTableFlag = false;

			}catch(SQLException sqlex){

				sqlex.printStackTrace();
				Log.d("logger","sdb2database failed");					
			}

			//
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION_PHRASE_MAP);
			db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE);
			onCreate(db);
		}

	}

	public SessionDetailsTable(Context c) {
		context = c;
		ourHelper = new DbHelper(context);		
		editor = null;
	}

	public SessionDetailsTable open() {
		//ourHelper = new DbHelper(context);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public void populatePhrasesTable() {
		//open();
		PhrasesXMLParser parser = new PhrasesXMLParser();
		editor = parser.parse(context);

		for(int i = 0; i < editor.size(); i++){
			Phrases p = editor.get(i);
			//Log.d("xml", p._phraseID+","+p._category+","+p._level+","+p._id+","+p._message+","+p._wordCount+","+p._charCount+","+p._message.length());
			addPhrase(p._phraseID,p._category,p._level,p._id,p._message,p._alternatives,p._wordCount,p._charCount);
		}
		phraseTableFlag = true;

		//close();
	}

	private long addPhrase(int phraseId, String category, String level, int sequenceNo, String phrase, String alternatives, int wordCount, int charCount ){
		open();

		ContentValues cv = new ContentValues();
		cv.put(PHRASE_ID, phraseId);
		cv.put(PHRASE_CATEGORY, category);
		cv.put(PHRASE_LEVEL, level);
		cv.put(PHRASE_SEQUENCE_NO, sequenceNo);
		cv.put(PHRASE, phrase);
		cv.put(PHRASE_ALTERNATIVE, alternatives);
		cv.put(PHRASE_WORD_COUNT, wordCount);
		cv.put(PHRASE_CHAR_COUNT, charCount);

		long status = ourDatabase.insertWithOnConflict(PHRASE_TABLE, null, cv,SQLiteDatabase.CONFLICT_IGNORE);
		//long status = ourDatabase.insertWithOnConflict(PHRASE_TABLE, null, cv, );
		close();

		return status;

	}

	public boolean isPhraseTablePopulated(){

		if(phraseTableFlag == true )
			return phraseTableFlag;

		String query ="Select count(*) from " + PHRASE_TABLE;
		long status = 0;

		open();
		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			//int len = ourCursor.getCount();
			if(ourCursor.moveToFirst() == true){
				status = ourCursor.getInt(0);
				if(status>1)
					phraseTableFlag = true;
				else
					phraseTableFlag = false;
			}
			else{
				phraseTableFlag = false;
			}

		}catch(Exception ex){

			ex.printStackTrace();
			phraseTableFlag = false;
		}

		close();
		return phraseTableFlag;

	}

	public int getPhraseCount(String category, String level){

		String query ="Select count(*) from " + PHRASE_TABLE + " where " + PHRASE_CATEGORY + " = '" + category +"' AND "+ PHRASE_LEVEL + " = '"+ level +"'";
		Log.d("randomizer", query);
		int status = 0;

		open();
		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();

			if(ourCursor.moveToFirst() == true)
				status = ourCursor.getInt(0);
			//String[] result = new String[len];

			Log.d("xml",query);
			Log.d("xml", category + "," + level + ": " + status);


			/*for (int i = 0; i < len; ourCursor.moveToNext()) {

		}*/
			//close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		close();
		return status;
	}

	public String[] getPhrase(int phraseId){
		String query ="Select " +PHRASE  +","+ PHRASE_LEVEL+" from " + PHRASE_TABLE + " where " + PHRASE_ID + " = " + phraseId;
		String phrase[] = new String[2];

		//open();
		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();

			if(len == 0)
				FileOperations.write("Illegal state: Phrase for the phrase ID -"+phraseId+" not found.");

			if(ourCursor.moveToFirst() == true){
				phrase[0] = ourCursor.getString(0);
				phrase[1] = ourCursor.getString(1);

			}else
				FileOperations.write("Illegal state: Problem retrieving phrase for the phrase ID -"+phraseId + ".");
			//String[] result = new String[len];

			//Log.d("xml","resultlength" + len);


			/*for (int i = 0; i < len; ourCursor.moveToNext()) {

		}*/
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//close();
		return phrase;		
	}

	public String[][] getTrainingPhrases(){

		String[][] phrases =null;
		//String query ="Select " +PHRASE +" from " + PHRASE_TABLE + " where " + PHRASE_CATEGORY + " = '" + PHRASE_CATEGORY_WORDS +"' AND "+PHRASE_LEVEL + " = '" +PHRASE_LEVEL_PRACTICE +"' ORDER by "+PHRASE_ID;
		String query ="Select " +PHRASE +"," + PHRASE_ALTERNATIVE+ " from " + PHRASE_TABLE + " where " + PHRASE_CATEGORY + " = '" + PHRASE_CATEGORY_WORDS +"' AND "+PHRASE_LEVEL + " = '" +PHRASE_LEVEL_PRACTICE +"' ORDER by "+PHRASE_ID;
		//String phrase = "";


		open();
		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();
			phrases = new String[len][2];
			//int index=0;
			//ourCursor.moveToFirst();

			for (int i = 0; ourCursor.moveToNext(); i++) {
				phrases[i][0] = ourCursor.getString(0);
				phrases[i][1] = ourCursor.getString(1);

				Log.d("xml","phrase" + ourCursor.getString(0));
				//index++;
				//String[] result = new String[len];
			}
			Log.d("xml","resultlength" + len);

		}catch(Exception ex){
			ex.printStackTrace();
		}
		close();
		return phrases;	

	}

	public String[][] getFTUPhrases(){
		String[][] phrases =null;
		String query ="Select " +PHRASE +" , " + PHRASE_ALTERNATIVE+ " from " + PHRASE_TABLE + " where " + PHRASE_CATEGORY + " = '" + PHRASE_CATEGORY_WORDS +"' AND "+PHRASE_LEVEL + " = '" +PHRASE_LEVEL_MAIN +"' ORDER by "+PHRASE_ID;
		//String phrase = "";


		open();
		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();
			phrases = new String[len][2];
			//int index=0;
			//ourCursor.moveToFirst();

			for (int i = 0; ourCursor.moveToNext(); i++) {
				phrases[i][0] = ourCursor.getString(0);
				phrases[i][1] = ourCursor.getString(1);

				Log.d("xml","phrase" + ourCursor.getString(0));
				//index++;
				//String[] result = new String[len];
			}
			Log.d("xml","resultlength" + len);



		}catch(Exception ex){
			ex.printStackTrace();
		}
		close();
		return phrases;	

	}

	public String[][] getPhrases(int userId,int sessionNumber){
		String[][] phrases =null;
		int[] phraseids = null;
		//String query ="Select " +PHRASE_XML_ID +" from " + TABLE_SESSION_PHRASE_MAP + " where " + USER_ID + " = " + userId +" AND "+SESSION_TYPE + " = " +sessionNumber +" ORDER by "+PHRASE_NO;
		String query ="Select " +PHRASE_XML_ID +" from " + TABLE_SESSION_PHRASE_MAP + " where " + USER_ID + " = " + userId +" AND "+SESSION_TYPE + " = " +sessionNumber +" AND "+ SESSION_TYPE +" = " + SESSION_ID+" ORDER by "+PHRASE_NO;
		
		//String phrase = "";


		open();
		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();
			phrases = new String[len][2];
			phraseids = new int[len];
			FileOperations.write("# of phrases for the session: "+len);
			//int index=0;
			//ourCursor.moveToFirst();

			for (int i = 0; ourCursor.moveToNext(); i++) {
				phraseids[i] = ourCursor.getInt(0);
				Log.d("xml","phrase" + ourCursor.getInt(0));
				//index++;
				//String[] result = new String[len];
			}
			Log.d("xml","phrases found in phrases table" + len);

			for (int i = 0; i<len; i++) {
				String[] phTmp = getPhrase(phraseids[i]);
				phrases[i][0] = phTmp[0];
				phrases[i][1] = phTmp[1];


				Log.d("xml","phrase:" + phrases[i][0] +" level: "+ phrases[i][1]);
				//index++;
				//String[] result = new String[len];
			}

			/*for (int i = 0; i < len; ourCursor.moveToNext()) {

			}*/
		}catch(Exception ex){
			ex.printStackTrace();
		}
		close();
		return phrases;	

	}


	public String[][] getPresentedTypedPhrases(int uid,int stype){
		
		String[][] phrases =null;
		String query ;
		
		if(stype == ApplicationConstants.FTU_SESSIONNO)
			query ="Select " +PHRASE +" , " + PHRASE_TYPED+ " from " + PHRASE_TABLE + " INNER JOIN  " + TABLE_SESSION_PHRASE_MAP + " ON "+ PHRASE_TABLE + "."+PHRASE_ID + " = "+ TABLE_SESSION_PHRASE_MAP + "."+ PHRASE_XML_ID  + " where "+ USER_ID+ "=" + uid +" AND  "+ SESSION_TYPE + "=" + stype +" AND ( ( " +ATTEMPT_NUMBER+ "=1 AND "+ EDIT_DISTANCE  +" =0 AND length("+ PHRASE_TYPED +")>0) || (length("+ PHRASE_TYPED +") AND "+ATTEMPT_NUMBER  +"=2)) ORDER by "+PHRASE_NO;
		else
			query ="Select " +PHRASE +" , " + PHRASE_TYPED+ " from " + PHRASE_TABLE + " INNER JOIN  " + TABLE_SESSION_PHRASE_MAP + " ON "+ PHRASE_TABLE + "."+PHRASE_ID + " = "+ TABLE_SESSION_PHRASE_MAP + "."+ PHRASE_XML_ID  + " where "+ USER_ID+ "=" + uid +" AND  "+ SESSION_TYPE + "=" + stype +" ORDER by "+PHRASE_NO;
		//String phrase = "";
		
		Log.d("test","get list:"+query);

		open();
		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();
			phrases = new String[len][2];
			//int index=0;
			//ourCursor.moveToFirst();

			for (int i = 0; ourCursor.moveToNext(); i++) {
				phrases[i][0] = ourCursor.getString(0);
				phrases[i][1] = ourCursor.getString(1);

				Log.d("xml","phrase" + ourCursor.getString(0)+", "+ourCursor.getString(1));
				//index++;
				//String[] result = new String[len];
			}
			Log.d("xml","resultlength" + len);

		}catch(Exception ex){
			ex.printStackTrace();
		}
		close();
		return phrases;	

	}
	
	public long addSessionDetails(int userID, int sessionType, int userPhraseNo,
			int userPhraseID, int attemptNo) {
		// TODO Auto-generated method stub


		/*if(userMappingExists(userID, sessionType, userPhraseNo) ==true){
			Log.d("sDebug" , "User mapping exists");
			return -1;
		}*/

		long sessionId = addSessionEntry(userID, sessionType);

		open();
		if( sessionId == -1 )
			return -1;

		ContentValues cv = new ContentValues();
		cv.put(USER_ID, userID);
		cv.put(SESSION_ID, sessionId);
		cv.put(USER_SESSION_TYPE, sessionType);
		cv.put(PHRASE_NO, userPhraseNo);
		cv.put(PHRASE_XML_ID, userPhraseID);
		cv.put(ATTEMPT_NUMBER, attemptNo);

		//Log.d("logger","Create new entry: u - " + userID + ", sessionId - " + sessionId + ", sessiontype - " +sessionType + ", phraseNo - " +userPhraseNo + " userPhraseId - " +userPhraseID );
		//FileOperations.write("Create new entry: u - " + userID + ", sessionId - " + sessionId + ", sessiontype - " +sessionType + ", phraseNo - " +userPhraseNo + " userPhraseId - " +userPhraseID);

		long status = ourDatabase.insert(TABLE_SESSION_PHRASE_MAP, null, cv);
		close();
		return status;
	}
	/*
	public long updatePhraseShownTS(int userID, int session_id, int sessionType, int userPhraseNo,
			int attemptNo, long sTS) {
		// TODO Auto-generated method stub

		//int status = 0;

		open();
		
		ContentValues cv = new ContentValues();
		cv.put(PHRASE_SHOWN, sTS);
		cv.put(SYNCED, 0);
		//cv.put(USER_SESSION_TYPE, sessionType);
		//cv.put(PHRASE_NO, userPhraseNo);
		//cv.put(PHRASE_XML_ID, userPhraseID);
		//cv.put(ATTEMPT_NUMBER, attemptNo);
		

		Log.d("test","Update: u - " + USER_ID + "=" + userID + " AND " + SESSION_TYPE + "=" + sessionType + " AND " + SESSION_ID + "=" + session_id + " AND " + PHRASE_NO + "=" + userPhraseNo  + " AND " + ATTEMPT_NUMBER + " = " + attemptNo +" AND "+ PHRASE_SHOWN + " = 0 " + " set PTS="+sTS );
		//FileOperations.write("Create new entry: u - " + userID + ", sessionId - " + sessionId + ", sessiontype - " +sessionType + ", phraseNo - " +userPhraseNo + " userPhraseId - " +userPhraseID);

		int status = ourDatabase.update(TABLE_SESSION_PHRASE_MAP, cv, USER_ID + "=" + userID + " AND " + SESSION_TYPE + "=" + sessionType + " AND " + SESSION_ID + "=" + session_id + " AND " + PHRASE_NO + "=" + userPhraseNo  + " AND " + ATTEMPT_NUMBER + " = " + attemptNo +" AND "+ PHRASE_SHOWN + " = 0"  ,null);
		
		
		
		//ourDatabase.execSQL(sql);
		//ourDatabase.
		
		//if(status>0 ) 
		Log.d("test","Rows affected : "+ ": " + status);
		close();
		return status;
	}
	
	public long getPhraseShownTS(int userID, int session_id, int sessionType, int userPhraseNo,
			int attemptNo) {
		// TODO Auto-generated method stub

		long ts = 0;

		open();
		
		//Log.d("test","Update: u - " + USER_ID + "=" + userID + " AND " + SESSION_TYPE + "=" + sessionType + " AND " + SESSION_ID + "=" + session_id + " AND " + PHRASE_NO + "=" + userPhraseNo  + " AND " + ATTEMPT_NUMBER + " = " + attemptNo);
		//FileOperations.write("Create new entry: u - " + userID + ", sessionId - " + sessionId + ", sessiontype - " +sessionType + ", phraseNo - " +userPhraseNo + " userPhraseId - " +userPhraseID);

		String sql = "SELECT  " +PHRASE_SHOWN + " FROM "+TABLE_SESSION_PHRASE_MAP + " where " + USER_ID + "=" + userID + " AND " + SESSION_TYPE 
				+ "=" + sessionType + " AND " + SESSION_ID 
				+ "=" + session_id + " AND " + PHRASE_NO + "=" + userPhraseNo  + " AND " + ATTEMPT_NUMBER + " = " + attemptNo;
		
		Cursor ourCursor  = ourDatabase.rawQuery(sql, null);
		int rowCount = ourCursor.getCount();
		if(rowCount==0)
			return -1;

		ourCursor.moveToFirst();
		ts = ourCursor.getLong(0);
		close();
		
		Log.d("test","PhraseShown TS:"+ts);
		return ts;
	}*/


	public int getPhraseXMLID(int userID, int sessionType, int userPhraseNo){
		open();
		String query = ("SELECT " + PHRASE_XML_ID + " FROM " +TABLE_SESSION_PHRASE_MAP 
				+ " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_TYPE + " = "+ sessionType +" AND " + PHRASE_NO +"=" + userPhraseNo+ " LIMIT 0,1");
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);

		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int rowCount = ourCursor.getCount();
		if(rowCount==0)
			return -1;

		ourCursor.moveToFirst();
		int phraseId = ourCursor.getInt(0);
		close();
		return phraseId;
	}

	public int updateSessionDetails(int userID, int sessionType, int userPhraseNo,
			long startTS, long endTS, String textTyped, String log, int editDist, int attempt){

		open();
		ContentValues cv = new ContentValues();
		cv.put(FIRST_CHAR_TIMESTAMP, startTS);
		cv.put(LAST_CHAR_TIMESTAMP, endTS);
		cv.put(PHRASE_TYPED, textTyped);
		cv.put(KEYSTROKE_LOG, log);
		cv.put(EDIT_DISTANCE, editDist);
		cv.put(ATTEMPT_NUMBER, attempt);
		cv.put(SYNCED, NOTSYNCED);

		//int status = ourDatabase.update(TABLE_SESSION_PHRASE_MAP, cv, USER_ID + "=" + userID + " AND " + SESSION_TYPE + "=" + sessionType + " AND " + PHRASE_NO +" = " +userPhraseNo + " AND (" + ATTEMPT_NUMBER +" =0 OR " + ATTEMPT_NUMBER + " = " + attempt +")", null);
		int status = ourDatabase.update(TABLE_SESSION_PHRASE_MAP, cv, USER_ID + "=" + userID + " AND " + SESSION_TYPE 
				+ "=" + sessionType + " AND " + PHRASE_NO +" = " +userPhraseNo + " AND " + ATTEMPT_NUMBER + " = " + attempt , null);
		//long status = ourDatabase.insert(SESSION_TABLE, null, cv);

		close();
		return status;
	}

	public long addTrainingDetails(int userID, int sessionId, int sessionType, int userPhraseNo,
			int userPhraseID, long startTS, long endTS, String phraseTyped, String log, int editDist, int attempt) {
		// TODO Auto-generated method stub

		//long sessionId = addSessionEntry(userID, sessionType);

		open();
		if( sessionId == -1 ){
			FileOperations.write("Illegal state: Session ID -1");
			return -1;
		}

		ContentValues cv = new ContentValues();
		cv.put(USER_ID, userID);
		cv.put(SESSION_ID, sessionId);
		cv.put(USER_SESSION_TYPE, sessionType);
		cv.put(PHRASE_NO, userPhraseNo);
		cv.put(PHRASE_XML_ID, userPhraseID);
		cv.put(FIRST_CHAR_TIMESTAMP, startTS);
		cv.put(LAST_CHAR_TIMESTAMP, endTS);
		cv.put(PHRASE_TYPED, phraseTyped);
		cv.put(KEYSTROKE_LOG, log);
		cv.put(EDIT_DISTANCE, editDist);
		cv.put(ATTEMPT_NUMBER, attempt);

		long status = ourDatabase.insert(TABLE_SESSION_PHRASE_MAP, null, cv);
		close();

		return status;
	}

	public void logSession(int userID, int sessionNo) { //int userID
		// TODO Auto-generated method stub
		open();
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" WHERE " + USER_ID + " = "+ userID+" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);
		//String query = ("SELECT " + USER_ID+ "|| ',' ||" + SESSION_ID+ "|| ',' ||"+ SESSION_TYPE +"|| ',' ||"+ SESSION_RATING + "|| ',' ||"+ SESSION_START + "|| ',' ||"+ SESSION_END + "|| ',' ||"+ SESSION_STATUS + "|| ',' ||" + SESSION_CONTINUE_FROM + "|| ',' ||" + SYNCED+  " FROM " +SESSION_TABLE  +" ORDER BY " + SESSION_START); //+ " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_NUMBER + " = "+ sessionNo
		String query = ("SELECT " + USER_ID+ " , " + SESSION_ID+ " , "+ SESSION_TYPE +" , "+ SESSION_RATING + " , "+ SESSION_START + " , "+ SESSION_END + " , "+ SESSION_STATUS + " , " + SESSION_CONTINUE_FROM + " , " + SYNCED+  " FROM " +SESSION_TABLE  +" ORDER BY " + SESSION_START); //+ " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_NUMBER + " = "+ sessionNo
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);

		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int rowCount = ourCursor.getCount();

		if(rowCount <=0){
			//ourCursor.moveToFirst();
			FileOperations.write("Session table entry: not found.");

		}/*else
		{
			FileOperations.write("Session table entry: not found.");
		}*/
		//ourCursor.moveToFirst();
		while(ourCursor.moveToNext()) {

			//FileOperations.write("Session table entry : "+ourCursor.getString(0));
			FileOperations.write("Session table entry : "+ourCursor.getString(0) + ", " +ourCursor.getString(1)+ ", " +ourCursor.getString(2)+ ", " +ourCursor.getString(3)+ ", " +ourCursor.getString(4)+ ", " +ourCursor.getString(5) + ", " +ourCursor.getString(6)+ ", " +ourCursor.getString(7)+ ", " +ourCursor.getString(8));

			//index++;
			//String[] result = new String[len];
		}

		close();
		//return result;
	}

	public void logSessionDetails(int userID, int sessionNo) { //int userID
		// TODO Auto-generated method stub
		open();
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" WHERE " + USER_ID + " = "+ userID+" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);
		//String query = ("SELECT " + USER_ID+ "|| ',' ||" + SESSION_ID+ "|| ',' ||"+ SESSION_TYPE +"|| ',' ||"+ PHRASE_NO +"|| ',' ||"+ PHRASE_XML_ID + "|| ',' ||"+ FIRST_CHAR_TIMESTAMP + "|| ',' ||"+ LAST_CHAR_TIMESTAMP + "|| ',' ||"+ PHRASE_TYPED +"|| ',' ||"+ EDIT_DISTANCE +"|| ',' ||"+ ATTEMPT_NUMBER+ "|| ',' ||" + SYNCED+  " FROM " +TABLE_SESSION_PHRASE_MAP   +" ORDER BY " + FIRST_CHAR_TIMESTAMP); //+ " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_NUMBER + " = "+ sessionNo
		String query = ("SELECT " + USER_ID+ " , " + SESSION_ID+ " , "+ SESSION_TYPE +" , "+ PHRASE_NO +" , "+ PHRASE_XML_ID + " , "+ FIRST_CHAR_TIMESTAMP + " , "+ LAST_CHAR_TIMESTAMP + " , "+ PHRASE_TYPED +" , "+ EDIT_DISTANCE +" , "+ ATTEMPT_NUMBER+ " , " + SYNCED+  " FROM " +TABLE_SESSION_PHRASE_MAP   +" ORDER BY " + FIRST_CHAR_TIMESTAMP); //+ " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_NUMBER + " = "+ sessionNo
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);

		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		/*int rowCount = ourCursor.getCount();
		if(rowCount >0){
			ourCursor.moveToFirst();
			FileOperations.write("Session details table entry: "+ourCursor.getString(0));

		}else
		{
			FileOperations.write("Session details table entry: not found.");
		}*/

		while(ourCursor.moveToNext()) {

			//FileOperations.write("Session details table entry : "+ourCursor.getString(0));
			FileOperations.write("Session details table entry : "+ourCursor.getString(0) + ", " +ourCursor.getString(1)+ ", " +ourCursor.getString(2)+ ", " +ourCursor.getString(3)+ ", " +ourCursor.getString(4)+ ", " +ourCursor.getString(5) + ", " +ourCursor.getString(6)+ ", " +ourCursor.getString(7)+ ", " +ourCursor.getString(8)+ ", " +ourCursor.getString(9)+ ", " +ourCursor.getString(10));
			//index++;
			//String[] result = new String[len];
		}


		close();
		//return result;
	}

	public String[][] getDetails(int userID, int sessionNo) { //int userID
		// TODO Auto-generated method stub
		open();
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" WHERE " + USER_ID + " = "+ userID+" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);
		String query = ("SELECT " + SESSION_ID+ ","+ SESSION_TYPE +","+ SESSION_RATING + ","+ SESSION_START + ","+ SESSION_END + ","+ SESSION_STATUS + " FROM " +SESSION_TABLE + " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_TYPE + " = "+ sessionNo +" ORDER BY " + SESSION_START);
		//String query = ("SELECT " + SESSION_RATING + ","+ SESSION_START + ","+ SESSION_END + ","+ SESSION_STATUS + " FROM " +SESSION_TABLE + " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_TYPE + " = "+ sessionNo +" ORDER BY " + SESSION_START);
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);

		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int rowCount = ourCursor.getCount();
		ourCursor.moveToFirst();
		int columnCount = ourCursor.getColumnCount();
		String[][] result = new String[rowCount][columnCount];

		//Log.d("dbQuery","Row count" + rowCount);
		for (int i = 0; i < rowCount; ourCursor.moveToNext()) {
			//result[i] = "User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_NUMBER + ":" +ourCursor.getString(2) + "," +PHRASE_NO + ":" +ourCursor.getString(3) + "," +PHRASE_XML_ID + ":"+ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9) ;

			result[i][0] = ourCursor.getString(0);
			result[i][1] = ourCursor.getString(1);
			result[i][2] = ourCursor.getString(2);
			result[i][3] = ourCursor.getString(3);
			result[i][4] = ourCursor.getString(4);
			result[i][5] = ourCursor.getString(5);

			//Log.d("dbQuery","User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_NUMBER + ":" +ourCursor.getString(2) + "," +PHRASE_NO + ":" +ourCursor.getString(3) + "," +PHRASE_XML_ID + ":"+ourCursor.getString(4) + "," +ourCursor.getString(5)  ); //+ "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9)
			i++;
		}
		close();
		return result;
	}

	public int[] getIncompleteSessions(int userID, int sessionNo) { //int userID
		// TODO Auto-generated method stub
		int[] result =null;
		open();
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" WHERE " + USER_ID + " = "+ userID+" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);
		String query = ("SELECT " + SESSION_ID +" FROM " +SESSION_TABLE + " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_TYPE + " = "+ sessionNo +" AND "+ SESSION_STATUS+" = "+SESSION_STATUS_STARTED +" ORDER BY " + SESSION_ID +" desc");
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);

		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int rowCount = ourCursor.getCount();


		Log.d("dbQuery",query);
		Log.d("dbQuery","incmp:rowCount: "+ rowCount);

		if(rowCount <=0){
			close();
			return result;
		}

		ourCursor.moveToFirst();
		result = new int[rowCount];


		//Log.d("dbQuery","Row count" + rowCount);
		for (int i = 0; !ourCursor.isAfterLast(); ourCursor.moveToNext()) {
			//result[i] = "User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_NUMBER + ":" +ourCursor.getString(2) + "," +PHRASE_NO + ":" +ourCursor.getString(3) + "," +PHRASE_XML_ID + ":"+ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9) ;

			result[i] = ourCursor.getInt(0);
			Log.d("dbQuery","incmp:User: "+ ourCursor.getInt(0)); //+ "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9)
			i++;
		}
		close();
		return result;
	}

	public int[] getIncompleteLTSession(int userID, int sessionNo) { //int userID
		// TODO Auto-generated method stub
		int[] result =null;
		int sessionCount = getTotalSessionCount();
		open();
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" WHERE " + USER_ID + " = "+ userID+" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);
		//String query = ("SELECT " + SESSION_ID +" FROM " +SESSION_TABLE + " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_NUMBER + " >= "+ sessionNo +" AND "+ SESSION_NUMBER + " <= "+ TOTAL_SESSIONS +" AND " +SESSION_STATUS+" = "+SESSION_STATUS_STARTED +" ORDER BY " + SESSION_ID);
		String query = ("SELECT " + SESSION_TYPE +" FROM " +SESSION_TABLE + " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_TYPE + " >= "+ sessionNo +" AND "+ SESSION_TYPE + " <= "+ sessionCount +" AND " +SESSION_STATUS+" = "+SESSION_STATUS_STARTED +" ORDER BY " + SESSION_TYPE);
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);

		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int rowCount = ourCursor.getCount();

		Log.d("dbQuery",query);
		Log.d("dbQuery","incmp:rowCount: "+ rowCount);

		if(rowCount <=0){
			close();
			return result;
		}

		ourCursor.moveToFirst();
		result = new int[rowCount];

		//Log.d("dbQuery","Row count" + rowCount);
		for (int i = 0; !ourCursor.isAfterLast(); ourCursor.moveToNext()) {
			//result[i] = "User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_NUMBER + ":" +ourCursor.getString(2) + "," +PHRASE_NO + ":" +ourCursor.getString(3) + "," +PHRASE_XML_ID + ":"+ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9) ;

			result[i] = ourCursor.getInt(0);
			Log.d("dbQuery","incmp:User: "+ ourCursor.getInt(0)); //+ "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9)
			//Log.d("dbQuery","User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_NUMBER + ":" +ourCursor.getString(2) + "," +PHRASE_NO + ":" +ourCursor.getString(3) + "," +PHRASE_XML_ID + ":"+ourCursor.getString(4) + "," +ourCursor.getString(5)  ); //+ "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9)
			i++;
		}
		close();
		return result;
	}

	public int getNextLTSession(int userID) { //int userID
		// TODO Auto-generated method stub
		//int[] result =null;
		open();
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" WHERE " + USER_ID + " = "+ userID+" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);
		//String query = ("SELECT " + SESSION_ID +" FROM " +SESSION_TABLE + " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_NUMBER + " >= "+ sessionNo +" AND "+ SESSION_NUMBER + " <= "+ TOTAL_SESSIONS +" AND " +SESSION_STATUS+" = "+SESSION_STATUS_STARTED +" ORDER BY " + SESSION_ID);
		String query = ("SELECT min(" + SESSION_TYPE +") FROM " +SESSION_TABLE + " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_TYPE + " >= "+ ApplicationConstants.LTU_SESSION_START  +" AND " +SESSION_STATUS+" = "+SESSION_STATUS_NOTSTARTED ); //+" ORDER BY " + SESSION_TYPE +" LIMIT 0,1"
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);

		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int rowCount = ourCursor.getCount();

		Log.d("dbQuery",query);
		Log.d("dbQuery","incmp:rowCount: "+ rowCount);

		if(rowCount <=0){
			close();
			return -1;
		}

		ourCursor.moveToFirst();
		int result = ourCursor.getInt(0);
		//result = new int[rowCount];

		//Log.d("dbQuery","Row count" + rowCount);
		/*for (int i = 0; !ourCursor.isAfterLast(); ourCursor.moveToNext()) {
			//result[i] = "User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_NUMBER + ":" +ourCursor.getString(2) + "," +PHRASE_NO + ":" +ourCursor.getString(3) + "," +PHRASE_XML_ID + ":"+ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9) ;

			result[i] = ourCursor.getInt(0);
			Log.d("dbQuery","incmp:User: "+ ourCursor.getInt(0)); //+ "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9)
			//Log.d("dbQuery","User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_NUMBER + ":" +ourCursor.getString(2) + "," +PHRASE_NO + ":" +ourCursor.getString(3) + "," +PHRASE_XML_ID + ":"+ourCursor.getString(4) + "," +ourCursor.getString(5)  ); //+ "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9)
			i++;
		}*/
		close();
		return result;
	}
	public String[][] getLTDetails(int userID, int sessionNo) { //int userID
		// TODO Auto-generated method stub
		int sessionCount = getTotalSessionCount();
		open();
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" WHERE " + USER_ID + " = "+ userID+" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);
		String query = ("SELECT " + SESSION_ID+ ","+ SESSION_TYPE +","+ SESSION_RATING + ","+ SESSION_START + ","+ SESSION_END + ","+ SESSION_STATUS 
				+ " FROM " +SESSION_TABLE + " WHERE "+ USER_ID+ "="+ userID +" AND "+ SESSION_TYPE + " >= "+ sessionNo +" AND "+ SESSION_TYPE + "<="+ sessionCount +" ORDER BY " + SESSION_START);
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" ORDER BY " + USER_SESSION_TYPE + " , " + PHRASE_NO);

		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int rowCount = ourCursor.getCount();
		ourCursor.moveToFirst();
		int columnCount = ourCursor.getColumnCount();
		String[][] result = new String[rowCount][columnCount];

		//Log.d("dbQuery","Row count" + rowCount);
		for (int i = 0; i < rowCount; ourCursor.moveToNext()) {
			//result[i] = "User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_NUMBER + ":" +ourCursor.getString(2) + "," +PHRASE_NO + ":" +ourCursor.getString(3) + "," +PHRASE_XML_ID + ":"+ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9) ;

			result[i][0] = ourCursor.getString(0);
			result[i][1] = ourCursor.getString(1);
			result[i][2] = ourCursor.getString(2);
			result[i][3] = ourCursor.getString(3);
			result[i][4] = ourCursor.getString(4);
			result[i][5] = ourCursor.getString(5);

			//Log.d("dbQuery","User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_NUMBER + ":" +ourCursor.getString(2) + "," +PHRASE_NO + ":" +ourCursor.getString(3) + "," +PHRASE_XML_ID + ":"+ourCursor.getString(4) + "," +ourCursor.getString(5)  ); //+ "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9)
			i++;
		}
		close();
		return result;
	}

	/*public String[] getSchoolName() {
		// TODO Auto-generated method stub
		String query = ("SELECT DISTINCT School FROM userdetails;");
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int len = ourCursor.getCount();
		ourCursor.moveToFirst();
		String[] result = new String[len];
		for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
			result[i] = ourCursor.getString(0);
		}
		return result;
	}

	public String[] getName(String myschool) {
		// TODO Auto-generated method stub
		String query = ("SELECT Name FROM userdetails WHERE School ='"
				+ myschool + "'");
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int len = ourCursor.getCount();
		ourCursor.moveToFirst();
		String[] result = new String[len];
		for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
			result[i] = ourCursor.getString(0);
		}
		return result;
	}

	public String getId(String userSchool, String userName, String userRoll,
			String userClass, String userDiv, String userKeyboard) {
		// TODO Auto-generated method stub
		String query = ("SELECT  _id FROM userdetails WHERE School = '"
				+ userSchool + "' AND Name = '" + userName + "' AND RollNumber = '"
				+ userRoll + "' AND Class = '" + userClass + "' AND Division = '"
				+ userDiv + "' AND Keyboard = '" + userKeyboard + "'");
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		ourCursor.moveToFirst();
		String result;
		result = ourCursor.getString(0);
		return result;
	}

	public String[] getDetails(String userId) {
		// TODO Auto-generated method stub
		String query = ("SELECT * FROM userdetails WHERE _id = '"+ userId+"'");
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int len = ourCursor.getCount();
		ourCursor.moveToFirst();
		String[] result = new String[len];
		for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
			result[i] = ourCursor.getString(0);
		}
		return result;
	}

	public String getuserDetails(String userId) {
		// TODO Auto-generated method stub
		String query = ("SELECT * FROM userdetails WHERE _id = '"+ userId+"'");
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int len = ourCursor.getCount();
		ourCursor.moveToFirst();
		String result = "";
		for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
			result = result.concat(ourCursor.getString(0));
			result = result.concat("\n");
		}
		return result;
	}

	public String getTrainings(String userId) {
		// TODO Auto-generated method stub
		String query = ("SELECT Trainings FROM userdetails WHERE _id ='"
				+ userId + "'");
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		ourCursor.moveToFirst();
		return ourCursor.getString(0);
	}

	public String getSession(String userId) {
		// TODO Auto-generated method stub
		String query = ("SELECT Session FROM userdetails WHERE _id ='"
				+ userId + "'");
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		ourCursor.moveToFirst();
		return ourCursor.getString(0);
	}*/


	//update the number of trainings i.e. increase the trainings after the session;



	//update the number of sessions i.e. increase the session number after the session;
	public void countSess(String userId) {
		// TODO Auto-generated method stub


	}

	//Sessiontable functions

	public long addSessionEntry(int userID, Integer sessionType) {
		// TODO Auto-generated method stub


		/*if(userMappingExists(userID, sessionType) ==true){
			Log.d("sDebug" , "User mapping exists");
			return -1;
		}*/

		long sessionId = userSessionDetailsExist(userID, sessionType);

		if(sessionId !=-1)
			return sessionId;

		sessionId = getUniqueSessionId(userID);
		open();
		ContentValues cv = new ContentValues();
		cv.put(USER_ID, userID);
		cv.put(SESSION_ID, sessionId);
		cv.put(SESSION_TYPE, sessionType);

		if(ourDatabase.insert(SESSION_TABLE, null, cv) != -1)
			Log.d("logger", "Session entry added successfully");
		close();
		return sessionId;
	}

	public long addTrainingEntry(Integer userID, Integer sessionType) {
		// TODO Auto-generated method stub
		long sessionId=-1;
		sessionId = getUniqueSessionId(userID);
		open();
		ContentValues cv = new ContentValues();
		cv.put(USER_ID, userID);
		cv.put(SESSION_ID, sessionId);
		Log.d("logger","Sessionid"+sessionId);
		cv.put(SESSION_TYPE, sessionType);
		cv.put(SESSION_STATUS, SESSION_STATUS_STARTED);

		long status = ourDatabase.insert(SESSION_TABLE, null, cv);
		if(status ==-1){
			Log.d("severe","Could not make a new training session entry for user:"+userID+" sessionId:"+sessionId);
			FileOperations.write("Could not make a new training session entry for user:"+userID+" sessionId:"+sessionId);			
		}
		close();
		return sessionId;
	}

	public int updateTrainingEntry(Integer userId, Integer sessionId, Integer rating, long sessionStartTS, long sessionEndTS,  Integer sessionstatus) {
		// TODO Auto-generated method stub

		open();
		/*if(userMappingExists(userID, sessionType) ==true){
			Log.d("sDebug" , "User mapping exists");
			return -1;
		}*/

		ContentValues cv = new ContentValues();
		cv.put(SESSION_RATING, rating);
		cv.put(SESSION_START, sessionStartTS);
		cv.put(SESSION_END, sessionEndTS);
		cv.put(SESSION_STATUS, sessionstatus);
		cv.put(SYNCED, NOTSYNCED);

		int status = ourDatabase.update(SESSION_TABLE, cv, USER_ID + "='" + userId + "' AND " + SESSION_ID + "=" + sessionId, null);
		//long status = ourDatabase.insert(SESSION_TABLE, null, cv);

		close();
		return status;
	}

	public float getExplorationTime(int userid){

		
		float duration = 0;
		int sessionType = getTotalSessionCount() + 1;
		open();
		try{
			//String query="Select sum("+SESSION_RATING+") from "+ SESSION_TABLE + " where " + USER_ID + "= "+ userid + " AND " + SESSION_NUMBER + ">" +TOTAL_SESSIONS + " AND "+SESSION_STATUS + "= "+SESSION_STATUS_DONE;
			String query="Select (SUM(" + LAST_CHAR_TIMESTAMP + "-" + FIRST_CHAR_TIMESTAMP + "))/60000.0 from "	+ TABLE_SESSION_PHRASE_MAP + " where " + USER_ID + "= "+ userid + " AND " + SESSION_TYPE + "=" +sessionType ;//+ " AND "+SESSION_STATUS + "= "+SESSION_STATUS_DONE;
			Cursor ourCursor =  ourDatabase.rawQuery(query, null);

			int len = ourCursor.getCount();

			if(len==0){

				if(ourDatabase.isOpen())
					close();
				FileOperations.write("Illegal state: Could not get duration of explorations done for user-id:"+userid+". Query returned no result");
				return duration;
			}

			if(ourCursor.moveToFirst()==false){			
				close();
				return duration;
			}

			duration = ourCursor.getFloat(0);
			if(ourDatabase.isOpen())
				close();
			Log.d("logger","explore time: "+duration);
			return duration;
			/*if(ourCursor.getInt(0) > 0)
			return ourCursor.getInt(0);
		else 
			return false;
			 */
		}catch(Exception e){
			e.printStackTrace();
			if(ourDatabase.isOpen())
				close();
		}

		Log.d("logger","explore time: "+duration);
		return duration;
	}

	public int getTotalSessionCount(){
		int sessionCount = -1;
		open();

		try{
			String query="Select count(*) from "+ PHRASE_TABLE + " where " + PHRASE_CATEGORY + "= '"+ PHRASE_CATEGORY_SENTENCES +"'";
			Cursor ourCursor =  ourDatabase.rawQuery(query, null);

			if(ourCursor.moveToFirst()){
				sessionCount = ourCursor.getInt(0);
				sessionCount = (sessionCount)/ApplicationConstants.MAX_PHRASES_PER_SESSION + ApplicationConstants.MAX_FTU;
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}

		close();

		return sessionCount;
	}

	public int getExplorationCount(int userid){

		int sessionCount = getTotalSessionCount();
		open();
		int trainings = 0;
		try{

			String query="Select count(*) from "+ SESSION_TABLE + " where " + USER_ID + "= "+ userid + " AND " + SESSION_TYPE + ">" + sessionCount+ " AND "+SESSION_STATUS + "= "+SESSION_STATUS_DONE;
			//String query="Select SUM(" + LAST_CHAR_TIMESTAMP + "-" + FIRST_CHAR_TIMESTAMP + ")/60000 from "	+ SESSION_TABLE + " where " + USER_ID + "= "+ userid + " AND " + SESSION_NUMBER + ">" +TOTAL_SESSIONS + " AND "+SESSION_STATUS + "= "+SESSION_STATUS_DONE;
			Cursor ourCursor =  ourDatabase.rawQuery(query, null);

			int len = ourCursor.getCount();

			if(len==0){
				close();
				FileOperations.write("Illegal state: Could not get count of Explorations done for user-id:"+userid+". Query returned no result");
				return trainings;
			}

			if(ourCursor.moveToFirst()==false){			
				close();
				return trainings;
			}

			trainings = ourCursor.getInt(0);
			close();
			return trainings;
			/*if(ourCursor.getInt(0) > 0)
			return ourCursor.getInt(0);
		else 
			return false;
			 */
		}catch(Exception e){
			e.printStackTrace();
		}
		close();
		return trainings;

	}
	//get session entry by id

	public void getSessionEntry(Integer sessionId){

		String query ="Select * from " + SESSION_TABLE + " where"  + SESSION_ID + " = " + sessionId;

		open();
		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();
			ourCursor.moveToFirst();
			//String[] result = new String[len];

			Log.d("logger:sessionentry","getSessionEntry resultlength" + len);

			for (int i = 0; i < len; ourCursor.moveToNext()) {
				//result[i] = "User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_NUMBER + ":" +ourCursor.getString(2) + "," +PHRASE_NO + ":" +ourCursor.getString(3) + "," +PHRASE_XML_ID + ":"+ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7) + "," +ourCursor.getString(8) + "," +ourCursor.getString(9) ;

				Log.d("logger:sessionentry","User: "+ ourCursor.getString(0) + "," +SESSION_ID + ":" +ourCursor.getString(1) + "," + SESSION_TYPE + ":" +ourCursor.getString(2) + "," + SESSION_RATING + ":" +ourCursor.getString(3) + "," +SESSION_STATUS + ":"+ourCursor.getString(4) );
				i++;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		close();
		//return result;
	}

	/***
	 * Update the rating and status
	 * 
	 * @param userId
	 * @param sessionType
	 * @param rating
	 * @param sessionStartTS
	 * @param sessionEndTS
	 * @param sessionstatus
	 * @return returns number of rows affected by the update
	 */
	public int updateSessionEntry(int userId, int sessionType, int rating, long sessionStartTS, long sessionEndTS, int sessionstatus) {
		// TODO Auto-generated method stub

		open();
		/*if(userMappingExists(userID, sessionType) ==true){
			Log.d("sDebug" , "User mapping exists");
			return -1;
		}*/

		ContentValues cv = new ContentValues();

		if(rating !=0)
			cv.put(SESSION_RATING, rating);
		if(sessionStartTS !=0)
			cv.put(SESSION_START, sessionStartTS);
		if(sessionEndTS !=0)
			cv.put(SESSION_END, sessionEndTS);
		//if(sessionstatus !=0)
		cv.put(SESSION_STATUS, sessionstatus);
		cv.put(SYNCED, NOTSYNCED);

		int status =0;
		if(cv.size()>0)
			status= ourDatabase.update(SESSION_TABLE, cv, USER_ID + "='" + userId + "' AND " + SESSION_TYPE + " = " + sessionType, null);
		//long status = ourDatabase.insert(SESSION_TABLE, null, cv);

		close();
		return status;
	}

	public void setContinueFrom(int userId, int sessionId) {
		// TODO Auto-generated method stub

		open();
		/*if(userMappingExists(userID, sessionType) ==true){
			Log.d("sDebug" , "User mapping exists");
			return -1;
		}*/

		/*ContentValues cv = new ContentValues();
		cv.put(SESSION_CONTINUE_FROM, sessionContinueFrom);

		int status = ourDatabase.update(SESSION_TABLE, cv, USER_ID + "='" + userId + "' AND " + SESSION_TYPE + " = " + sessionType, null);*/
		//long status = ourDatabase.insert(SESSION_TABLE, null, cv);


		open();
		String query="Update " + SESSION_TABLE + " set "+ SESSION_CONTINUE_FROM + " = " + SESSION_CONTINUE_FROM + " +1, " + SYNCED + " = "+ NOTSYNCED + " where " + USER_ID + " = "+ userId + " AND " + SESSION_ID + " = " +sessionId;
		Log.d("logger:continuefrom", query);

		ourDatabase.execSQL(query);
		FileOperations.write("Continue from updated");

		close();
		//return status;
	}

	/*****
	 * Check if the user is already allocated phrases
	 * @param userID
	 * @param sessionType
	 * @return  sessionID for the session if entry found, else return -1
	 */

	public long userSessionDetailsExist(int userID, Integer sessionType){
		try{
			open();
			String query="Select distinct " + SESSION_ID + " from "+ SESSION_TABLE + " where " + USER_ID + "= "+ userID + " AND " + SESSION_TYPE + " = " +sessionType;
			Cursor ourCursor =  ourDatabase.rawQuery(query, null);

			int len = ourCursor.getCount();		

			if(len==0){
				close();
				//FileOperations.write("Illegal state: User should already have been allocated phrase. Sessionid should exist");
				Log.d("kbz","Illegal state: User should already have been allocated phrase. Sessionid should exist");
				return -1;
			}

			if(ourCursor.moveToFirst()==false){
				//FileOperations.write("Illegal state: User should already have been allocated phrase. Sessionid should exist");
				Log.d("kbz","Illegal state: User should already have been allocated phrase. Sessionid should exist");
				close();
				return -1;
			}

			close();
			Log.d("kbz","SID:"+ourCursor.getInt(0));
			return ourCursor.getInt(0);
			/*if(ourCursor.getInt(0) > 0)
			return ourCursor.getInt(0);
		else 
			return false;
			 */
		}catch(Exception e){
			e.printStackTrace();
		}
		close();
		FileOperations.write("Illegal state: Uer should already have been allocated phrase. Sessionid should exist");
		return -1;

	}
	/*****
	 * Generates a unique session id for the given user
	 * @param userID 
	 * @return returns the unique session id
	 */
	public long getUniqueSessionId(int userID){
		long id = 0;

		open();
		String query="Select distinct " + SESSION_ID + " from "+ SESSION_TABLE + " where " + USER_ID + "= "+ userID +" order by " + SESSION_ID + " desc LIMIT 0,1";
		Cursor ourCursor =  ourDatabase.rawQuery(query, null);

		int len = ourCursor.getCount();		

		if(len==0){ //no sessions created for user yet, start at 1
			close();
			FileOperations.write("Illegal state: There should more than 1 entry in the session table for the user. Should not need to create new ones.");
			return 1;
		}

		ourCursor.moveToFirst();
		id = ourCursor.getLong(0); //get the current highest sessionid for the user, and add 1 to it
		close();

		return id+1;
	}

	public boolean isInterrupted(int userId, int sessionId){ //verify logic

		open();
		String query = ("SELECT COUNT(*)" + " FROM " +TABLE_SESSION_PHRASE_MAP + " WHERE "+ USER_ID + "= "+ userId +" AND " + SESSION_ID+ "="+ sessionId + " AND " + PHRASE_TYPED +" != 'null'");//
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		ourCursor.moveToFirst();

		int count = ourCursor.getInt(0);
		close();

		Log.d("kbz","No interrupted session for "+sessionId);
		if(count == 0)
			return false;
		else
			return true; //Select (sum(l_timestamp - f_timestamp))/1000.0 from  Session_details_table where s_id = 56

	}

	//verify logic
	public boolean isLTInterrupted(int userId,int sessionId){

		open();
		String query = ("SELECT COUNT(*)" + " FROM " +TABLE_SESSION_PHRASE_MAP + " WHERE "+ USER_ID + "= "+ userId +" AND " + SESSION_ID+ "="+ sessionId + " AND " + PHRASE_TYPED +" != 'null'");//
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		ourCursor.moveToFirst();

		int count = ourCursor.getInt(0);
		close();

		if(count == 0)
			return false;
		else
			return true; //Select (sum(l_timestamp - f_timestamp))/1000.0 from  Session_details_table where s_id = 56

	}

	public int getLastSuccessfulPhrase(int userId,int sessionId){

		/*open();
		//get the highest phrase no for the session id for the user, where
		//the phrase was typed correctly in first attempt or has been attempted twice.

		String query = ("SELECT "+ PHRASE_NO + " FROM " +TABLE_SESSION_PHRASE_MAP + " WHERE "+ USER_ID + "= "+ userId +" AND " + SESSION_ID+ "="+ sessionId 
				+ " AND ( ( "+ ATTEMPT_NUMBER +" = 2 ) || ("+ ATTEMPT_NUMBER +" = 1 AND " + EDIT_DISTANCE + " = 0) ) ORDER BY " + PHRASE_NO + " desc LIMIT 0,1");//
		Cursor ourCursor = ourDatabase.rawQuery(query, null);

		if(ourCursor.getCount()==0)
			return -1;

		ourCursor.moveToFirst();		

		int phraseNo = ourCursor.getInt(0);
		close();

		return phraseNo; */


		open();
		String query = ("SELECT "+ SESSION_CONTINUE_FROM + " FROM " +SESSION_TABLE + " WHERE "+ USER_ID + "= "+ userId +" AND " + SESSION_ID+ "="+ sessionId); //resolve
		Cursor ourCursor = ourDatabase.rawQuery(query, null);

		if(ourCursor.getCount()==0)
			return -1;

		ourCursor.moveToFirst();		

		int phraseNo = ourCursor.getInt(0);
		close();

		return phraseNo;
	}

	public int getLTLastSuccessfulPhrase(int userId,int sessionId){

		/*open();
		//get the highest phrase no for the session id for the user, where
		//the phrase was typed correctly in first attempt or has been attempted twice.

		String query = ("SELECT "+ PHRASE_NO + " FROM " +TABLE_SESSION_PHRASE_MAP + " WHERE "+ USER_ID + "= "+ userId +" AND " + SESSION_TYPE+ "="+ sessionType 
				+ " AND "+ ATTEMPT_NUMBER +" >= 1 ORDER BY " + PHRASE_NO + " desc  LIMIT 0,1");//
		Log.d("kbz","Query:"+query);
		Cursor ourCursor = ourDatabase.rawQuery(query, null);

		if(ourCursor.getCount()==0)
			return -1;

		ourCursor.moveToFirst();		

		int phraseNo = ourCursor.getInt(0);
		close();

		return phraseNo; */

		open();
		String query = ("SELECT "+ SESSION_CONTINUE_FROM + " FROM " +SESSION_TABLE + " WHERE "+ USER_ID + "= "+ userId +" AND " + SESSION_ID+ "="+ sessionId);//
		Cursor ourCursor = ourDatabase.rawQuery(query, null);

		if(ourCursor.getCount()==0)
			return -1;

		ourCursor.moveToFirst();		

		int phraseNo = ourCursor.getInt(0);
		close();

		return phraseNo;

	}

	/*public double getCPM(int userId,int sessionId){

		open();
		String query = ("Select "+ TABLE_SESSION_PHRASE_MAP +"."+FIRST_CHAR_TIMESTAMP +","+ TABLE_SESSION_PHRASE_MAP +"."+LAST_CHAR_TIMESTAMP +","+ PHRASE_TABLE +"."+PHRASE_CHAR_COUNT 
				+" from "+ TABLE_SESSION_PHRASE_MAP +" INNER JOIN "+ PHRASE_TABLE+" ON "+ TABLE_SESSION_PHRASE_MAP +"."+PHRASE_XML_ID +" = "+ PHRASE_TABLE +"."+PHRASE_ID +" where "+ USER_ID + "= "+ userId +" AND " + TABLE_SESSION_PHRASE_MAP +"."+SESSION_ID +" = " +sessionId);
		long start;
		long end;
		double charCount;
		double total_cpm = 0.0,cpm=0.0;

		//Log.d("sessionDetails",query);


		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();	
			ourCursor.moveToFirst();

			for (int i = 0; !ourCursor.isAfterLast(); ourCursor.moveToNext()) {
				start = ourCursor.getLong(0);
				end = ourCursor.getLong(1);
				charCount = ourCursor.getDouble(2);
				double diff =(end - start)/60000.0;

				cpm = charCount / diff;
				//Log.d("sessionDetails","Values:"+start+","+end + ","+charCount +","+cpm);

				if(total_cpm ==0)
				total_cpm = cpm;
			else
				total_cpm = (total_cpm + cpm)/2;
				total_cpm +=cpm;

			}
			total_cpm =total_cpm/len;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		close();

		return total_cpm;		
	}*/

	public double[] getCPMNew(int userId,int sessionId,int sessionType){

		
		String query="";
		
		if(sessionType == ApplicationConstants.FTU_SESSIONNO){
			
			query = "Select "+ TABLE_SESSION_PHRASE_MAP +"."+FIRST_CHAR_TIMESTAMP +","+ TABLE_SESSION_PHRASE_MAP +"."+LAST_CHAR_TIMESTAMP +","+ TABLE_SESSION_PHRASE_MAP +"."+EDIT_DISTANCE +",length("+ TABLE_SESSION_PHRASE_MAP +"."+PHRASE_TYPED +"),"+"length("+ PHRASE_TABLE +"."+PHRASE +")"  
				+" from "+ TABLE_SESSION_PHRASE_MAP +" INNER JOIN "+ PHRASE_TABLE+" ON "+ TABLE_SESSION_PHRASE_MAP +"."+PHRASE_XML_ID +" = "+ PHRASE_TABLE +"."+PHRASE_ID +" where "+ USER_ID + "= "+ userId +" AND " + TABLE_SESSION_PHRASE_MAP +"."+SESSION_ID +" = " +sessionId +" AND " +"("+FIRST_CHAR_TIMESTAMP+"!=0 AND "+ KEYSTROKE_LOG + " != \"\") AND (("+ATTEMPT_NUMBER+" =1 AND " + EDIT_DISTANCE + "=0) ||(" + ATTEMPT_NUMBER + " = 2))";
		}
		else if(sessionType >= ApplicationConstants.LTU_SESSION_START && sessionType <= (ApplicationConstants.LTU_SESSION_START + getTotalSessionCount()) ){
			
		
			query = "Select "+ TABLE_SESSION_PHRASE_MAP +"."+FIRST_CHAR_TIMESTAMP +","+ TABLE_SESSION_PHRASE_MAP +"."+LAST_CHAR_TIMESTAMP +","+ TABLE_SESSION_PHRASE_MAP +"."+EDIT_DISTANCE +",length("+ TABLE_SESSION_PHRASE_MAP +"."+PHRASE_TYPED +"),"+"length("+ PHRASE_TABLE +"."+PHRASE +")" 
				+" from "+ TABLE_SESSION_PHRASE_MAP +" INNER JOIN "+ PHRASE_TABLE+" ON "+ TABLE_SESSION_PHRASE_MAP +"."+PHRASE_XML_ID +" = "+ PHRASE_TABLE +"."+PHRASE_ID +" where "+ USER_ID + "= "+ userId +" AND " + TABLE_SESSION_PHRASE_MAP +"."+SESSION_ID +" = " +sessionId +" AND " +""+FIRST_CHAR_TIMESTAMP+"!=0 ";
		}
		
		/*long start;
		long end;
		int charCount;
		int editDistance =0;*/
		double total_cpm = 0.0,cpm=0.0;
		double[] cpms = new double[6];
		Arrays.fill(cpms, 0.0); //initialize

		Log.d("kbz",query);
		long [][] result=null;
		open();

		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			int rowCount = ourCursor.getCount();	
			result = new long[rowCount][ourCursor.getColumnCount()];
			ourCursor.moveToFirst();

			for (int i = 0; !ourCursor.isAfterLast(); ourCursor.moveToNext()) {
				/*start = ourCursor.getLong(0);
				end = ourCursor.getLong(1);
				editDistance = ourCursor.getInt(2);
				charCount = ourCursor.getInt(3);*/

				result[i][0] =ourCursor.getLong(0); //fTS
				result[i][1] =ourCursor.getLong(1); //lTS
				result[i][2] =ourCursor.getLong(2); //ED
				result[i][3] =ourCursor.getLong(3); //tcCount
				result[i][4] =ourCursor.getLong(4); //cCount
				
				Log.d("kbz-data",result[i][0]+","+result[i][1]+","+result[i][2]+","+result[i][3] + ","+result[i][4]);
				
				i++;
				/*double diff =(end - start)/60000.0;

				cpm = charCount / diff;

				total_cpm +=cpm;*/

			}
			close();
			//total_cpm =total_cpm/len;
		}catch(Exception ex){
			ex.printStackTrace();
			close();
		}

		if(result ==null)
			return cpms;

		SpeedCalculator speed =new SpeedCalculator();
		int fiveStarCount=0,fourStarCount=0,threeStarCount=0,twoStarCount=0,oneStarCount=0;

		for (int i = 0 ; i<result.length ;i++){
			float starVal = Validator.getEDToStarVal((int)result[i][2], (int)result[i][4]);

			speed.setFirstCharTime(result[i][0]);
			speed.setLastCharTime(result[i][1]);
			speed.setTextLength((int)result[i][3]);
			cpm = speed.getCPM();

			/*switch(starVal){
				case 5:
					fiveStarCount++;
					cpms[starVal-1] +=cpm;
					break;
				case 4:
					fourStarCount++;
					cpms[starVal-1] +=cpm;
					break;
				case 3:
					threeStarCount++;
					cpms[starVal-1] +=cpm;
					break;
				case 2:
					twoStarCount++;
					cpms[starVal-1] +=cpm;
					break;
				case 1:
					oneStarCount++;
					cpms[starVal-1] +=cpm;
					break;					
			}*/
			if(starVal >=5)
			{
				fiveStarCount++;
				cpms[5] +=cpm;
			}
			if(starVal >= 4){
				fourStarCount++;
				cpms[4] +=cpm;
			}
			if(starVal >= 3){
				threeStarCount++;
				cpms[3] +=cpm;
			}
			if(starVal >= 2){
				twoStarCount++;
				cpms[2] +=cpm;
			}
			if(starVal >= 1){
				oneStarCount++;
				cpms[1] +=cpm;
			}

			cpms[0] +=cpm; //flat avg should be same as cpms[1]
			FileOperations.write("starVal" +starVal+", avg:"+String.format("%.2f", cpms[0]) + "-"+oneStarCount + ", 2* avg:"+String.format("%.2f", cpms[2]) + "-"+twoStarCount + ", 3* avg:"+String.format("%.2f", cpms[3]) + "-"+threeStarCount + ", 4* avg:"+String.format("%.2f", cpms[4]) + "-"+fourStarCount + ", 5* avg:"+String.format("%.2f", cpms[5]) + "-"+fiveStarCount);
			Log.d("kbz", "starVal" +starVal+"[ "+result[i][2]+","+result[i][4]+" ],avg:"+String.format("%.2f", cpms[0]) + "-"+oneStarCount + ", 2* avg:"+String.format("%.2f", cpms[2]) + "-"+twoStarCount + ", 3* avg:"+String.format("%.2f", cpms[3]) + "-"+threeStarCount + ", 4* avg:"+String.format("%.2f", cpms[4]) + "-"+fourStarCount + ", 5* avg:"+String.format("%.2f", cpms[5]) + "-"+fiveStarCount);
		}

		Log.d("kbz", "Total = avg:"+String.format("%.2f", cpms[0]) + "-"+oneStarCount + ", 2* avg:"+String.format("%.2f", cpms[2]) + "-"+twoStarCount + ", 3* avg:"+String.format("%.2f", cpms[3]) + "-"+threeStarCount + ", 4* avg:"+String.format("%.2f", cpms[4]) + "-"+fourStarCount + ", 5* avg:"+String.format("%.2f", cpms[5]) + "-"+fiveStarCount);

		cpms[0] = cpms[0]/result.length;
		cpms[1] = cpms[1]/oneStarCount;

		if(twoStarCount !=0)
			cpms[2] = cpms[2]/twoStarCount;
		else
			cpms[2] = 0;

		if(threeStarCount !=0)
			cpms[3] = cpms[3]/threeStarCount;
		else
			cpms[3] = 0;

		if(fourStarCount !=0)
			cpms[4] = cpms[4]/fourStarCount;
		else
			cpms[4] = 0;

		if(fiveStarCount !=0)
			cpms[5] = cpms[5]/fiveStarCount;
		else
			cpms[5] = 0;

		Log.d("kbz", "Final = avg:"+String.format("%.2f", cpms[0]) + "-"+oneStarCount + ", 2* avg:"+String.format("%.2f", cpms[2]) + "-"+twoStarCount + ", 3* avg:"+String.format("%.2f", cpms[3]) + "-"+threeStarCount + ", 4* avg:"+String.format("%.2f", cpms[4]) + "-"+fourStarCount + ", 5* avg:"+String.format("%.2f", cpms[5]) + "-"+fiveStarCount);

		return cpms;		
	}

	public double getED(int userId,int sessionId){

		open();
		String query = ("Select "+TABLE_SESSION_PHRASE_MAP+"."+EDIT_DISTANCE +", "+ PHRASE_TABLE +"."+PHRASE_CHAR_COUNT + " from "+
				TABLE_SESSION_PHRASE_MAP +" INNER JOIN " + PHRASE_TABLE +" ON " + TABLE_SESSION_PHRASE_MAP +"."+ PHRASE_XML_ID +"="+ PHRASE_TABLE+ "."+PHRASE_ID +" where "+ USER_ID + "= "+ userId +" AND " +SESSION_ID +" = " +sessionId);
		//String query = ("Select "+TABLE_SESSION_PHRASE_MAP+"."+EDIT_DISTANCE +", length("+ PHRASE_TABLE +"."+PHRASE +")"+ " from "+TABLE_SESSION_PHRASE_MAP +" INNER JOIN " + PHRASE_TABLE +" ON " + TABLE_SESSION_PHRASE_MAP +"."+ PHRASE_XML_ID +"="+ PHRASE_TABLE+ "."+PHRASE_ID +" where "+ USER_ID + "= "+ userId +" AND " +SESSION_ID +" = " +sessionId);
		int editdist;
		int phrase_length;
		double correctnessRatio = 0.0;

		//Log.d("sessionDetails",query);


		try{
			Cursor ourCursor = ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();	

			if(len ==0)
			{
				FileOperations.write("Exception.No entries for ED found.");
				return correctnessRatio;
			}

			int maxStars = len * 5;
			ourCursor.moveToFirst();

			for (int i = 0; !ourCursor.isAfterLast(); ourCursor.moveToNext()) {
				editdist = ourCursor.getInt(0);
				phrase_length = ourCursor.getInt(1);


				//Log.d("sessionDetails","Values:"+start+","+end + ","+charCount +","+cpm);

				/*if(total_cpm ==0)
				total_cpm = cpm;
				else
				total_cpm = (total_cpm + cpm)/2;*/
				correctnessRatio += Validator.getEDToStarVal(editdist, phrase_length);

			}
			correctnessRatio = Validator.getSessionStar(correctnessRatio / maxStars); //avg


		}catch(Exception ex){
			ex.printStackTrace();
		}
		close();

		return correctnessRatio;		
	}

	public int countTrainings(int userId) {
		// TODO Auto-generated method stub
		open();
		int trainings = 0;
		try{
			String query="Select count( " + SESSION_ID + ") from "+ SESSION_TABLE + " where " + USER_ID + "= "+ userId + " AND " + SESSION_TYPE + "=" +ApplicationConstants.TRAINING_SESSIONTYPE + " AND "+SESSION_STATUS + "= "+SESSION_STATUS_DONE;
			Cursor ourCursor =  ourDatabase.rawQuery(query, null);

			int len = ourCursor.getCount();

			if(len==0){
				close();
				FileOperations.write("Illegal state: Could not get count of trainings done for user-id:"+userId+". Query returned no result");
				return trainings;
			}

			if(ourCursor.moveToFirst()==false){			
				close();
				return trainings;
			}

			trainings = ourCursor.getInt(0);
			close();
			return trainings;
			/*if(ourCursor.getInt(0) > 0)
			return ourCursor.getInt(0);
		else 
			return false;
			 */
		}catch(Exception e){
			e.printStackTrace();
		}
		close();
		return trainings;

	}

	public int ftuDone(int userId) {
		// TODO Auto-generated method stub
		open();
		int ftu = 0;
		try{
			String query="Select count( " + SESSION_ID + ") from "+ SESSION_TABLE + " where " + USER_ID + "= "+ userId + " AND " + SESSION_TYPE + "=" +ApplicationConstants.FTU_SESSIONNO + " AND " + SESSION_STATUS + "=" + SESSION_STATUS_DONE;
			Cursor ourCursor =  ourDatabase.rawQuery(query, null);

			int len = ourCursor.getCount();

			if(len==0){
				close();
				FileOperations.write("Illegal state: Could not get count of FTU done for user-id:"+userId+". Query returned no result");
				return ftu;
			}

			if(ourCursor.moveToFirst()==false){			
				close();
				return ftu;
			}

			ftu = ourCursor.getInt(0);
			close();
			return ftu;
			/*if(ourCursor.getInt(0) > 0)
			return ourCursor.getInt(0);
		else 
			return false;
			 */
		}catch(Exception e){
			e.printStackTrace();
		}
		close();
		return ftu;

	}

	public int ltuDone(int userId) {
		// TODO Auto-generated method stub
		int sessionCount = getTotalSessionCount();
		open();
		int ltu = 0;
		//int exploreSession = SessionDetailsTable.TOTAL_SESSIONS;
		try{
			String query="Select count( " + SESSION_ID + ") from "+ SESSION_TABLE + " where " + USER_ID + "= "+ userId + " AND " + SESSION_TYPE + ">=" +ApplicationConstants.LTU_SESSION_START + " AND " + SESSION_TYPE + "<="+ sessionCount + " AND " + SESSION_STATUS + "=" + SESSION_STATUS_DONE;
			Cursor ourCursor =  ourDatabase.rawQuery(query, null);

			int len = ourCursor.getCount();

			if(len==0){
				close();
				FileOperations.write("Illegal state: 1. Could not get count of LTUs done for user-id:"+userId+". Query returned no result");
				return ltu;
			}

			if(ourCursor.moveToFirst()==false){			
				close();
				FileOperations.write("Illegal state: 2. Could not get count of LTUs done for user-id:"+userId+". Query returned no result");
				return ltu;
			}

			ltu = ourCursor.getInt(0);
			close();
			return ltu;
			/*if(ourCursor.getInt(0) > 0)
			return ourCursor.getInt(0);
		else 
			return false;
			 */
		}catch(Exception e){
			e.printStackTrace();
		}
		close();
		return ltu;

	}
	
	public boolean isSessionFullyAccurate(int userID, int sessionID ){
		open();
		int count = 0;
		try{
			
			String query="SELECT count(*) FROM "+ TABLE_SESSION_PHRASE_MAP + " where " + USER_ID + "= "+ userID + " AND " + SESSION_ID + " = " +sessionID + " AND " + EDIT_DISTANCE + " = 0";
			Cursor ourCursor =  ourDatabase.rawQuery(query, null);

			int len = ourCursor.getCount();

			if(len==0){
				close();
				FileOperations.write("Illegal state: Could not get count of correct phrases for user-id:"+userID+". Query returned no result");
				return false;
			}

			if(ourCursor.moveToFirst()==false){			
				close();
				return false;
			}

			count = ourCursor.getInt(0);
			close();
			if(count == ApplicationConstants.MAX_PHRASES_PER_SESSION)
				return true;
			else 
				return false;
			
			/*if(ourCursor.getInt(0) > 0)
			return ourCursor.getInt(0);
		else 
			return false;
			 */
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		//close();
		//return ftu;

	}
	
	public double getHighScoreTillSession(int userID, int sessionID ){
		open();
		int count = 0;
		double highscore = 0.0;
		int  editdist =0, phrase_length, typed_char_length;
		long fTS,lTS;
		double cpm;
		SpeedCalculator speed;
		speed = new SpeedCalculator();
		try{
			
			String query="SELECT  " + userID +" , "+ FIRST_CHAR_TIMESTAMP +" , "+ LAST_CHAR_TIMESTAMP+" ," + PHRASE_TYPED +" , "+ EDIT_DISTANCE +", "+ PHRASE_CHAR_COUNT+" FROM "+ TABLE_SESSION_PHRASE_MAP +", "+PHRASE_TABLE +" WHERE "+USER_ID +" = "+ userID +" and "+ SESSION_TYPE +" >= "+ ApplicationConstants.LTU_SESSION_START+" AND "+ SESSION_TYPE+" <= "+ sessionID+" and "+ PHRASE_ID+" = "+ PHRASE_XML_ID ;
			Cursor ourCursor =  ourDatabase.rawQuery(query, null);

			int len = ourCursor.getCount();

			if(len==0){
				close();
				FileOperations.write("Illegal state: Could not get high scores for user-id:"+userID+". Query returned no result");
				return highscore;
			}
         
			ourCursor.moveToFirst();

			for (int i = 0; !ourCursor.isAfterLast(); ourCursor.moveToNext()) {
				editdist = ourCursor.getInt(4);
				phrase_length = ourCursor.getInt(5);
				
				int starz = (int) Validator.getEDToStarVal(editdist, phrase_length);
				
				if(starz >=4)
				{
					fTS = ourCursor.getInt(1);
					lTS = ourCursor.getInt(2);
					typed_char_length = ourCursor.getString(3).length();
					
					speed.setFirstCharTime(fTS);
					speed.setLastCharTime(lTS);
					speed.setTextLength(typed_char_length);
					cpm = speed.getCPM();
					
					if(cpm > highscore)
						highscore = cpm;
					
				}

			}
			return highscore;
			
		}catch(Exception e){
			e.printStackTrace();
			return highscore;
		}
		//close();
		//return ftu;

	}
	
	public int getHighScoreCracks(int userID, int sessionID, double prevMaxCPM ){
		open();
		int crackCount = 0;
		double crackedScore = prevMaxCPM;
		
		int  editdist =0, phrase_length, typed_char_length;
		long fTS,lTS;
		double cpm;
		
		SpeedCalculator speed;
		speed = new SpeedCalculator();
		try{
			
			String query="SELECT  " + userID +" , "+ FIRST_CHAR_TIMESTAMP +" , "+ LAST_CHAR_TIMESTAMP+" ,  "+ PHRASE_TYPED +" , "+ EDIT_DISTANCE +", "+ PHRASE_CHAR_COUNT+" FROM "+ TABLE_SESSION_PHRASE_MAP +", "+PHRASE_TABLE +" WHERE "+USER_ID +" = "+ userID +" and "+ SESSION_TYPE +" = "+ sessionID+" and "+ PHRASE_ID+" = "+ PHRASE_XML_ID ;
			Cursor ourCursor =  ourDatabase.rawQuery(query, null);

			int len = ourCursor.getCount();

			if(len==0){
				close();
				FileOperations.write("Illegal state: Could not get high scores for user-id:"+userID+". Query returned no result");
				return crackCount;
			}
         
			ourCursor.moveToFirst();

			for (int i = 0; !ourCursor.isAfterLast(); ourCursor.moveToNext()) {
				editdist = ourCursor.getInt(4);
				phrase_length = ourCursor.getInt(5);
				
				int starz = (int) Validator.getEDToStarVal(editdist, phrase_length);
				
				if(starz >=4)
				{
					fTS = ourCursor.getInt(1);
					lTS = ourCursor.getInt(2);
					typed_char_length =  ourCursor.getString(3).length();
					
					speed.setFirstCharTime(fTS);
					speed.setLastCharTime(lTS);
					speed.setTextLength(typed_char_length);
					cpm = speed.getCPM();
					
					if(cpm > crackedScore){
						crackedScore = cpm;
						crackCount ++;
					}
					
				}

			}
			return crackCount;
			
		}catch(Exception e){
			e.printStackTrace();
			return crackCount;
		}
		//close();
		//return ftu;

	}

}