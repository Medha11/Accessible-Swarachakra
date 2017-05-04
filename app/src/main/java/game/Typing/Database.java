package game.Typing;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class Database {

	public static final String USER_ID = "_id";
	public static final String USER_FNAME = "u_fname";
	public static final String USER_LNAME = "u_lname";
	public static final String USER_SCHOOL = "u_school";
	//public static final String USER_ROLL = "RollNumber";
	public static final String USER_CLASS = "u_standard";
	public static final String USER_DIV = "u_div";
	public static final String USER_AGE = "u_age";
	public static final String USER_KEYBOARD = "u_keyboard";
	public static final String USER_KEYBOARD_SERVICE = "u_imename";
	public static final String USER_STD_RATING = "u_std_rating";
	public static final String USER_SYNCHED = "synched";
	
	public static final String USER_PHOTO = "u_photo";

	//public static final String USER_TRAININGS = "Trainings";
	//public static final String USER_SESSION = "Session";


	public static final String TABLE_USER_DETAILS = "userdetails";
	private static final String DATABASE_NAME = "UT_Marathi";
	private static final int DATABASE_VERSION = 1;

	private final Context context;
	private SQLiteDatabase ourDatabase,ourRDatabase;
	private DbHelper ourHelper;

	public static class DbHelper extends SQLiteOpenHelper {

		private static DbHelper sInstance;
		
		private DbHelper(Context context) {
			
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		public static DbHelper getInstance(Context context) {

		    // Use the application context, which will ensure that you 
		    // don't accidentally leak an Activity's context.
		    // See this article for more information: http://bit.ly/6LRzfx
		    if (sInstance == null) {
		      sInstance = new DbHelper(context);
		    }
		    return sInstance;
		  }
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + TABLE_USER_DETAILS + " (" + USER_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_FNAME
					+ " TEXT NOT NULL, " + USER_LNAME + " TEXT NOT NULL, "
					+ USER_SCHOOL + " TEXT NOT NULL, "
					+ USER_CLASS + " TEXT NOT NULL, " + USER_DIV
					+ " TEXT NOT NULL, " + USER_AGE + " int NOT NULL, "
					+ USER_KEYBOARD + " TEXT NOT NULL, "
					+ USER_KEYBOARD_SERVICE + " TEXT NOT NULL, "
					+ USER_STD_RATING + " INTEGER NOT NULL, "
					+ USER_PHOTO + " INTEGER , "
					+ USER_SYNCHED+ " INTEGER DEFAULT 0);");
			
			Log.d("logger","tried to create table: "+"CREATE TABLE " + TABLE_USER_DETAILS + " (" + USER_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_FNAME
					+ " TEXT NOT NULL, " + USER_LNAME + " TEXT NOT NULL, "
					+ USER_SCHOOL + " TEXT NOT NULL, "
					+ USER_CLASS + " TEXT NOT NULL, " + USER_DIV
					+ " TEXT NOT NULL, " + USER_AGE + " int NOT NULL, "
					+ USER_KEYBOARD + " TEXT NOT NULL, " 
					+ USER_STD_RATING + " INTEGER NOT NULL);");
			
			Cursor cc = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='table_name'", null);
			if(cc.getCount()>0)
			{	
				cc.moveToFirst();
				Log.d("logger" , " table metadata " + cc.getColumnName(0));
				cc.close();
				/*Log.d("logger" , " table metadata " + cc.getColumnName(1));
				Log.d("logger" , " table metadata " + cc.getColumnName(2));
				Log.d("logger" , " table metadata " + cc.getColumnName(3));
				Log.d("logger" , " table metadata " + cc.getColumnName(4));
				Log.d("logger" , " table metadata " + cc.getColumnName(5));
				Log.d("logger" , " table metadata " + cc.getColumnName(6));*/
			}else
			{
				Log.d("logger","table creation probab didnt work");
				
			}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DETAILS);
			onCreate(db);
		}

	}

	public Database(Context c) {
		context = c;
		ourHelper = DbHelper.getInstance(context);
	}

	public Database open() {
		//ourHelper = new DbHelper(context);
		ourDatabase = ourHelper.getWritableDatabase();
		
		return this;
	}

	public void close() {
		ourHelper.close();
	}
	
	public String[] getDetails() { //int userID
		// TODO Auto-generated method stub
		open();
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" WHERE " + USER_ID + " = "+ userID+" ORDER BY " + USER_SESSION_NO + " , " + PHRASE_NO);
		String query = ("SELECT * FROM " +TABLE_USER_DETAILS + " ORDER BY " + USER_ID );
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" ORDER BY " + USER_SESSION_NO + " , " + PHRASE_NO);
		
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int len = ourCursor.getCount();
		ourCursor.moveToFirst();
		String[] result = new String[len];
		
		Log.d("dbQuery","User count" + len);
		for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
			result[i] = "User "+ ourCursor.getString(0) + "," +ourCursor.getString(1) + "," +ourCursor.getString(2) + "," +ourCursor.getString(3) + "," +ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6)+ "," +ourCursor.getString(7);
			
			Log.d("dbQuery","User-id: "+ ourCursor.getString(0) + "," +ourCursor.getString(1) + "," +ourCursor.getString(2) + "," +ourCursor.getString(3) + "," +ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7));
		}
		close();
		return result;
	}

	public String[] getName() { //int userID
		// TODO Auto-generated method stub
		open();
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" WHERE " + USER_ID + " = "+ userID+" ORDER BY " + USER_SESSION_NO + " , " + PHRASE_NO);
		String query = ("SELECT * FROM " +TABLE_USER_DETAILS + " ORDER BY " + USER_ID );
		//String query = ("SELECT * FROM " +TABLE_SESSION_PHRASE_MAP +" ORDER BY " + USER_SESSION_NO + " , " + PHRASE_NO);
		
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int len = ourCursor.getCount();
		ourCursor.moveToFirst();
		String[] result = new String[len];
		
		Log.d("dbQuery","User count" + len);
		for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
			result[i] = "User "+ ourCursor.getString(0) + "," +ourCursor.getString(1) + "," +ourCursor.getString(2) + "," +ourCursor.getString(3) + "," +ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7);
			
			Log.d("dbQuery","User-id: "+ ourCursor.getString(0) + "," +ourCursor.getString(1) + "," +ourCursor.getString(2) + "," +ourCursor.getString(3) + "," +ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7));
		}
		close();
		return result;
	}
	public int insertUser(String userFName, String userLName, String userSchool, String userClass,
			String userDiv,  String uAge, String userKeyboard, String userKeyboardService,String userRating, int userPhoto) { //, String userPhoto
		// TODO Auto-generated method stub 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int genID = prefs.getInt("Device_ID",0);
		int myDeviceId = prefs.getInt("my_device_id", 0);
		int min = (myDeviceId*1000)+1;
		int max = (myDeviceId*1000)+999;
		//String tId = genID+"1";
		
		open();
		String sql = "Select "+ USER_ID +" from "+TABLE_USER_DETAILS+" where "+ USER_ID +">='"+min+"' AND "+ USER_ID +"<'"+max+"' order by _id DESC";
		FileOperations.write("Create genID for user: "+sql);
		
		Cursor idCur = ourDatabase.rawQuery(sql, null);
		int len = idCur.getCount();
		if(len > 0){
			idCur.moveToFirst();
			int idRes = idCur.getInt(0);
			//int id = Integer.valueOf(idRes);
			genID = idRes+1;
		}		
		
		//int uID = Integer.valueOf(tId);
		ContentValues cv = new ContentValues();
		cv.put(USER_ID,genID);
		cv.put(USER_FNAME, userFName);
		cv.put(USER_LNAME, userLName);
		cv.put(USER_SCHOOL, userSchool);
		cv.put(USER_CLASS, userClass);
		cv.put(USER_DIV, userDiv);
		cv.put(USER_AGE, uAge);
		cv.put(USER_KEYBOARD, userKeyboard);
		cv.put(USER_KEYBOARD_SERVICE, userKeyboardService);
		cv.put(USER_STD_RATING, userRating );
		cv.put(USER_PHOTO, userPhoto);
		cv.put(USER_SYNCHED, "0");

		//open();
		
		long status = ourDatabase.insert(TABLE_USER_DETAILS, null, cv);
		Log.d("users","This is the inserted user id:"+status+"---This is genID:"+genID);
		close();
		
		Editor editor = prefs.edit();
		int newID = genID+1;
		editor.putInt("Device_ID",newID );
		editor.commit();
		return genID;
		
		/*String insertQuery = "Insert into " + TABLE_USER_DETAILS + "('" + USER_FNAME + "', '" + USER_LNAME + "', '" + USER_SCHOOL + "', '" 
		+ USER_CLASS + "' , '" + USER_DIV + "' , '" + USER_AGE + "' , '" + USER_KEYBOARD + "') values ('" 
			+ userFName + "','" + userLName + "','" + userSchool + "','" + userClass + "','" + userDiv + "','" + uAge + "','" + userKeyboard + "')";
		
		Log.d("logger" , "query: "+insertQuery);
		
		Cursor c = ourDatabase.rawQuery(insertQuery, null);
		
		int len=c.getCount();
		close();
		
		if(len==0){
			close();
			return -1;
		}else{
			return len;
		}
		*/
		
		//return ourDatabase.insert(TABLE_USER_DETAILS, null, cv);
	}

	public String[] getSchoolName() {
		// TODO Auto-generated method stub
		open();
		Log.d("logger:school" , "db"+ourDatabase);
		
		String query = ("SELECT DISTINCT " + USER_SCHOOL + " FROM " + TABLE_USER_DETAILS + " ORDER BY " + USER_SCHOOL);
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int len = ourCursor.getCount();
		Log.d("logger","No of schools:"+len);
		if(len==0)
		{
			return null;
		}
		ourCursor.moveToFirst();
		String[] result = new String[len];
		
		for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
			result[i] = ourCursor.getString(0);
			Log.d("logger","Schools:" + result[i]);
		}
		
		close();
		return result;
	}

	public String[] getName(String myschool) {
		// TODO Auto-generated method stub
		String query = ("SELECT CONCAT( " + USER_FNAME + ",' '," + USER_LNAME + " ) FROM " + TABLE_USER_DETAILS + " WHERE " + USER_SCHOOL + " ='"
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
	
	/**
	 * Gets rating of the standard task by the given user
	 * @param userId user-id to fetch rating for
	 * @return rating value if given user found in database table else returns -1
	 */
	public int getRating(int userId) {
		// TODO Auto-generated method stub
		open();
		int result =-1;
		String query = "SELECT " + USER_STD_RATING + " FROM " + TABLE_USER_DETAILS + " WHERE " + USER_ID + " ='"
				+ userId + "'";
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int len = ourCursor.getCount();
		
		if(len<=0){
			close();
			return -1;
		}
		
		if(ourCursor.moveToFirst()==false){
			close();
			return -1;
		}
		
		try{
			
			result = Integer.parseInt(ourCursor.getString(0));
		
		}catch(Exception ex){
			
			ex.printStackTrace();
			//return -1;
		}
		close();
		return result;
	}
	
	public String getId(String userSchool, String userFName, String userLName, String userAge,
			String userClass, String userDiv, String userKeyboard) {
		// TODO Auto-generated method stub
		String query = ("SELECT  * FROM '" + TABLE_USER_DETAILS + "' WHERE '" + USER_SCHOOL + "' = '"
				+ userSchool + "' AND '" + USER_FNAME + "' = '" + userFName + "' AND '" + USER_AGE + "' = '"
				+ userAge + "' AND '" + USER_CLASS + "' = '" + userClass + "' AND '" + USER_DIV + "' = '"
				+ userDiv + "' AND '" + USER_KEYBOARD + "' = '" + userKeyboard + "'"); //AND '"+ USER_LNAME + "' = '" + userLName + "'
		Log.d("logger", query);
		open();
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int len = ourCursor.getCount();
		if(len==0)
		{
			close();
			Log.d("logger", "id not found");
			return "";
		}
		ourCursor.moveToFirst();
		String result;
		result = ourCursor.getString(0);
		close();
		Log.d("logger", "id found: "+ result);
		return result;
	}
	
	public String[] getDetails(String userId) {
		// TODO Auto-generated method stub
		String query = ("SELECT * FROM " + TABLE_USER_DETAILS + " WHERE " + USER_ID + " = '"+ userId+"'");
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		if(ourCursor!=null){
		int len = ourCursor.getCount();
		
			ourCursor.moveToFirst();
			String[] result = new String[len];
			for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
				result[i] = ourCursor.getString(0);
			}
			return result;
		}else{
			return null;
		}
	}
	
	public String getuserDetails(int userId) {
		// TODO Auto-generated method stub
		open();
		String query = ("SELECT " + USER_FNAME + "," + USER_CLASS + "," + USER_DIV  + " FROM " + TABLE_USER_DETAILS + " WHERE " + USER_ID + " = '"+ userId+"'");
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int len = ourCursor.getCount();
		
		if(len==0){
			Log.d("logger" , "This should not be happening: userid "+userId);
			FileOperations.write("Illegal state: User name for user id:" + userId + " not found.");
			close();
			return "";
		}
			
		ourCursor.moveToFirst();
		String result = "";
		
		result = ourCursor.getString(0) +" ( Std "+ ourCursor.getString(1) + "-" + ourCursor.getString(2) + ")";
		/*for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
			result = result.concat(ourCursor.getString(0));
			result = result.concat("\n");
		}*/
		close();
		return result;
	}
	
	/*
	 * Fiedls removed :MJ
	 * public String getTrainings(String userId) {
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
	public void countTrain(String userId) {
		// TODO Auto-generated method stub
	/*	ContentValues cv = new ContentValues();
		String query = ("SELECT Trainings FROM userdetails WHERE _id ='"
				+ userId + "'");
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		ourCursor.moveToFirst();
		ourCursor.moveToFirst();
		String c= ourCursor.getString(0);
		
		
		
		cv.put(USER_TRAININGS,);
		ourDatabase.update(TABLE_USER_DETAILS, Trainings, _id, cv);
		
		*/
		
	}
	
	
	//update the number of sessions i.e. increase the session number after the session;
	public void countSess(String userId) {
		// TODO Auto-generated method stub
		
		
	}
	
	/*public String[][] getUsers(String userSchool) {
		// TODO Auto-generated method stub
		
		String[] columns = new String[]{USER_ID, USER_FNAME, USER_LNAME, USER_SCHOOL, USER_CLASS, USER_DIV, USER_AGE, USER_KEYBOARD};
		Cursor ourCursor = ourDatabase.query(TABLE_USER_DETAILS,columns, USER_SCHOOL+"='"+ userSchool+"'", null, null, null, null);
		int lenr=ourCursor.getCount();
		int lenc=ourCursor.getColumnCount();
		
		int iRow=ourCursor.getColumnIndex(USER_ID);
		int ifName=ourCursor.getColumnIndex(USER_FNAME);
		int ilName=ourCursor.getColumnIndex(USER_LNAME);
		//int iRoll=ourCursor.getColumnIndex(USER_ROLL);
		int iSchool=ourCursor.getColumnIndex(USER_SCHOOL);
		int iClass=ourCursor.getColumnIndex(USER_CLASS);
		int iDiv=ourCursor.getColumnIndex(USER_DIV);
		int iAge=ourCursor.getColumnIndex(USER_AGE);
		int iKeyboard=ourCursor.getColumnIndex(USER_KEYBOARD);
		
		String[][] result = new String [lenr][lenc];
		ourCursor.moveToFirst();
		for (int i=0;!ourCursor.isAfterLast();i++, ourCursor.moveToNext()) {
			
			result[i][0]=ourCursor.getString(iRow);
			result[i][1]=ourCursor.getString(ifName);
			result[i][2]=ourCursor.getString(ilName);
			result[i][3]=ourCursor.getString(iSchool);
			result[i][4]=ourCursor.getString(iClass);
			result[i][5]=ourCursor.getString(iDiv);
			result[i][6]=ourCursor.getString(iAge);			
			result[i][7]=ourCursor.getString(iKeyboard);
			
			Log.d("TAG",result[i][0]+ "---db");
		}
	
		return result;
	}*/
	
	public String[][] getUsers(String userSchool) {
		// TODO Auto-generated method stub
		
		open();
		String query = "Select * from "+ TABLE_USER_DETAILS + " where " + USER_SCHOOL +" = '" + userSchool +"' order by " + USER_FNAME;
		//String[] columns = new String[]{USER_ID, USER_FNAME, USER_LNAME, USER_SCHOOL, USER_CLASS, USER_DIV, USER_AGE, USER_KEYBOARD};
		//Cursor ourCursor = ourDatabase.query(TABLE_USER_DETAILS,columns, USER_SCHOOL+"='"+ userSchool+"'", null, null, null, null);
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int lenr=ourCursor.getCount();
		int lenc=ourCursor.getColumnCount();
		
		int iRow=ourCursor.getColumnIndex(USER_ID);
		int ifName=ourCursor.getColumnIndex(USER_FNAME);
		int ilName=ourCursor.getColumnIndex(USER_LNAME);
		//int iRoll=ourCursor.getColumnIndex(USER_ROLL);
		int iSchool=ourCursor.getColumnIndex(USER_SCHOOL);
		int iClass=ourCursor.getColumnIndex(USER_CLASS);
		int iDiv=ourCursor.getColumnIndex(USER_DIV);
		int iAge=ourCursor.getColumnIndex(USER_AGE);
		int iKeyboard=ourCursor.getColumnIndex(USER_KEYBOARD);
		int iKeyboardService=ourCursor.getColumnIndex(USER_KEYBOARD_SERVICE);
		int iPhoto = ourCursor.getColumnIndex(USER_PHOTO);
		
		String[][] result = new String [lenr][lenc];
		ourCursor.moveToFirst();
		for (int i=0;!ourCursor.isAfterLast();i++, ourCursor.moveToNext()) {
			
			result[i][0]=ourCursor.getString(iRow);
			result[i][1]=ourCursor.getString(ifName);
			result[i][2]=ourCursor.getString(ilName);
			result[i][3]=ourCursor.getString(iSchool);
			result[i][4]=ourCursor.getString(iClass);
			result[i][5]=ourCursor.getString(iDiv);
			result[i][6]=ourCursor.getString(iAge);			
			result[i][7]=ourCursor.getString(iKeyboard);
			result[i][8]=ourCursor.getString(iKeyboardService);
			result[i][9]=ourCursor.getString(iPhoto);
			
			Log.d("TAG",result[i][0]+ "---db");
		}
		close();
	
		return result;
	}
	public String[] getUserIds(String userSchool) {
		// TODO Auto-generated method stub
		
		open();
		//String query = "Select " + USER_ID +" from "+ TABLE_USER_DETAILS + " where " + USER_SCHOOL +" = '" + userSchool +"' order by " + USER_ID;
		String query = "Select " + USER_ID +" from "+ TABLE_USER_DETAILS +" order by " + USER_ID;
		//String[] columns = new String[]{USER_ID, USER_FNAME, USER_LNAME, USER_SCHOOL, USER_CLASS, USER_DIV, USER_AGE, USER_KEYBOARD};
		//Cursor ourCursor = ourDatabase.query(TABLE_USER_DETAILS,columns, USER_SCHOOL+"='"+ userSchool+"'", null, null, null, null);
		Cursor ourCursor = ourDatabase.rawQuery(query, null);
		int lenr=ourCursor.getCount();
		//int lenc=ourCursor.getColumnCount();
		
		int iRow=ourCursor.getColumnIndex(USER_ID);
				
		String[] result = new String [lenr];
		ourCursor.moveToFirst();
		for (int i=0;!ourCursor.isAfterLast();i++, ourCursor.moveToNext()) {
			
			result[i]=ourCursor.getString(iRow);
			
			
			Log.d("TAG",result[i]+ "---db");
		}
		close();
	
		return result;
	}
	
}