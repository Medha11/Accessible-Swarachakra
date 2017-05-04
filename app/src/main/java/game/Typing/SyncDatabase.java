package game.Typing;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class SyncDatabase {

	private static final String DATABASE_NAME = "UT_Marathi";
	private static final String DATABASE_NAME_SESS = "UT_Marathi_main";
	public String[] escapeCharacters = {"'","\""};
	private static final int DATABASE_VERSION = 1;
	private final Context context;
	public String _id,u_fname,u_lname,u_school,u_standard,u_div,u_age,u_keyboard,u_imename,u_std_rating,u_photo,	u_sync_status,u_device_wifi_id;
	public String s_id,session_type,phrase_no,phrase_xml_id,f_timestamp,l_timestamp,phrase_typed,	edit_distance,attempt_number,ts,sync_status;
	public String ip_add;
	public String url;
	public SQLiteDatabase ourDatabase,ourUserDatabase;  //private
	public String Response,connectFail;
	ProgressDialog prgDialog1,prgDialog2,prgDialog3;
	private DbHelper ourHelper;
	private DbHelperUser ourHelperUser;

	public SyncDatabase(Context c) {
		context = c;
		ourHelperUser = new DbHelperUser(context);
		ourHelper = new DbHelper(context);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		ip_add = prefs.getString("ip_string", "192.168.1.130");

		url="http://"+ip_add+"/vkbPHP/";
	}

	public SyncDatabase open() {
		//ourHelper = new DbHelper(context);
		ourDatabase = ourHelper.getWritableDatabase();
		ourUserDatabase = ourHelperUser.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
		ourHelperUser.close();
	}

	public void onUpSync(int flag,ProgressDialog prDialog1){
		prgDialog1 = prDialog1;

		open();

		//String localres = null;
		String l_url = url+"upSync.php";
		Gson gson = new GsonBuilder().create();
		String returnResult = null;
		try{
			ArrayList<HashMap<String, String>> wordListUser;
			wordListUser = new ArrayList<HashMap<String, String>>();


			String selectUserQuery = "SELECT * FROM userdetails where synched = '0'";

			Cursor cursorUser = ourUserDatabase.rawQuery(selectUserQuery, null);

			int totalUsers=0, totalSessDetails = 0, totalSessTable =0;

			if(cursorUser.getCount()>0){
				if (cursorUser.moveToFirst()) {
					do {
						HashMap<String, String> map = new HashMap<String, String>();
						//map.put("userId", cursor.getString(0));
						map.put("_id", cursorUser.getString(0));
						map.put("u_fname", cursorUser.getString(1));	    		
						map.put("u_lname", cursorUser.getString(2));	    		
						map.put("u_school", cursorUser.getString(3));	
						map.put("u_standard", cursorUser.getString(4));
						map.put("u_div", cursorUser.getString(5));
						map.put("u_age", cursorUser.getString(6));
						map.put("u_keyboard", cursorUser.getString(7));
						map.put("u_imename", cursorUser.getString(8));
						map.put("u_std_rating", cursorUser.getString(9));
						map.put("synched", "1");
						map.put("action", "insert");
						map.put("tablename", "userdetails");


						wordListUser.add(map);
					} while (cursorUser.moveToNext());

					totalUsers=cursorUser.getCount();
				}

				//Use GSON to serialize Array List to JSON
				if(wordListUser.size()>0){
					String jsonUserTable = gson.toJson(wordListUser);
					//System.out.println("this is user json:" + jsonUserTable);
					postDataWithAsyncHttpAndUpdateResponse(l_url,jsonUserTable,1,totalUsers,flag,prgDialog1);
				}
				close();
				returnResult = "Sync in progress...";
				//prg.hide();

			}else{
				onUpSyncSessionTable(flag,prgDialog1);
				//close();
				//returnResult = "No records to sync";
				//prg.hide();
			}
		}catch(Exception ex){
			ex.printStackTrace();
			//prg.hide();
		} 
		//return returnResult;
	}
	//public SQLiteDatabase ourDatabase;  //private
	//private DbHelper ourHelper;

	//ProgressDialog prgDialog = null;
	//private String ip = "169.254.23.83";

	//get ip from the shared pref

	public void onUpSyncSessionTable(int flag,ProgressDialog prDialog1){
		prgDialog1 = prDialog1;



		open();

		//String localres = null;
		String l_url = url+"upSync.php";
		Gson gson = new GsonBuilder().create();
		String returnResult = null;
		try{
			ArrayList<HashMap<String, String>> wordListSessTable;

			wordListSessTable = new ArrayList<HashMap<String, String>>();			
			String selectSessionTableQuery = "SELECT * FROM session_table where synched = '0'";


			Cursor cursorSessTable = ourDatabase.rawQuery(selectSessionTableQuery, null);
			int totalSessTable =0;

			if(cursorSessTable.getCount()>0){				

				if (cursorSessTable.moveToFirst()) {

					do {
						HashMap<String, String> map1 = new HashMap<String, String>();
						//map.put("userId", cursor.getString(0));
						//Log.d("sessionTable","sessionTable array keys"+Arrays.toString(cursorSessTable.getColumnNames()));
						map1.put("u_id", cursorSessTable.getString(0));
						if(cursorSessTable.getString(1)==null)
							map1.put("s_id", "0");	
						else
							map1.put("s_id", cursorSessTable.getString(1));

						if(cursorSessTable.getString(2)==null)
							map1.put("session_type", cursorSessTable.getString(2));
						else
							map1.put("session_type", cursorSessTable.getString(2));


						map1.put("session_rating", cursorSessTable.getString(3));	
						map1.put("session_start", cursorSessTable.getString(4));
						map1.put("session_end", cursorSessTable.getString(5));
						map1.put("session_status", cursorSessTable.getString(6));
						map1.put(SessionDetailsTable.SESSION_CONTINUE_FROM, cursorSessTable.getString(7));
						map1.put("synched", "1");
						map1.put("action", "insert");
						map1.put("tablename", "session_table");
						//Log.d("sessionTable"," 1="+cursorSessTable.getString(0)+" 1="+cursorSessTable.getString(1)+" 2="+cursorSessTable.getString(2)+" 3="+cursorSessTable.getString(3)+" 4="+cursorSessTable.getString(4)+" 5="+cursorSessTable.getString(5)+" 6="+cursorSessTable.getString(6));

						wordListSessTable.add(map1);
					} while (cursorSessTable.moveToNext());
					totalSessTable=cursorSessTable.getCount();
				}

				//System.out.println("this is user json:" + jsonUserTable);
				//System.out.println("this is session details json:" + jsonSessDet);
				//System.out.println("this is session table json:" + jsonSessTable);
				//final int sc = 1;

				//Use GSON to serialize Array List to JSON

				if(wordListSessTable.size()>0){
					String jsonSessTable = gson.toJson(wordListSessTable);
					System.out.println("this is session table json:" + jsonSessTable);

					postDataWithAsyncHttpAndUpdateResponse(l_url,jsonSessTable,3,totalSessTable,flag,prgDialog1);
				}
				close();
				returnResult = "Sync in progress...";
				//prg.hide();

			}else{
				onUpSyncSessionDetails(flag,prgDialog1);
				//close();
				//returnResult = "No records to sync";
				//prg.hide();
			}
		}catch(Exception ex){
			ex.printStackTrace();
			//prg.hide();
		} 
		//return returnResult;
	}

	public void onUpSyncSessionDetails(int flag,ProgressDialog prDialog1){
		prgDialog1 = prDialog1;
		//prgDialog2 = prDialog2;
		//prgDialog3 = prDialog3;

		open();

		//String localres = null;
		String l_url = url+"upSync.php";
		Gson gson = new GsonBuilder().create();
		String returnResult = null;
		try{
			ArrayList<HashMap<String, String>> wordListSessDet;

			wordListSessDet = new ArrayList<HashMap<String, String>>();
			String selectSessionDetailsQuery = "SELECT * FROM session_details_table where synched = '0' limit 50";


			Cursor cursorSessDet = ourDatabase.rawQuery(selectSessionDetailsQuery, null);

			int totalSessDetails = 0;

			if(cursorSessDet.getCount()>0 ){
				if (cursorSessDet.moveToFirst()) {
					do {
						HashMap<String, String> map = new HashMap<String, String>();
						//map.put("userId", cursor.getString(0));
						map.put("u_id", cursorSessDet.getString(0));
						if(cursorSessDet.getString(1)==null)
							map.put("s_id", "0");
						else
							map.put("s_id", cursorSessDet.getString(1));

						if(cursorSessDet.getString(2)==null)
							map.put("session_type", "0");
						else
							map.put("session_type", cursorSessDet.getString(2));

						//map.put("session_type", cursorSessDet.getString(2));	    		
						map.put("phrase_no", cursorSessDet.getString(3));	
						map.put("phrase_xml_id", cursorSessDet.getString(4));
						//map.put("phrase_shown", cursorSessDet.getString(5));
						map.put("f_timestamp", cursorSessDet.getString(5));
						map.put("l_timestamp", cursorSessDet.getString(6));
						map.put("phrase_typed", escapeString(cursorSessDet.getString(7)));
						map.put(SessionDetailsTable.KEYSTROKE_LOG, escapeString(cursorSessDet.getString(8)));
						map.put("edit_distance", cursorSessDet.getString(9));
						map.put("attempt_number", cursorSessDet.getString(10));
						map.put("synched", "1");
						map.put("action", "insert");
						map.put("tablename", "session_details_table");


						wordListSessDet.add(map);
					} while (cursorSessDet.moveToNext());
					totalSessDetails=cursorSessDet.getCount();
				}

				//System.out.println("this is user json:" + jsonUserTable);
				//System.out.println("this is session details json:" + jsonSessDet);
				//System.out.println("this is session table json:" + jsonSessTable);
				//final int sc = 1;

				//Use GSON to serialize Array List to JSON

				if(wordListSessDet.size()>0){
					String jsonSessDet = gson.toJson(wordListSessDet);
					System.out.println("this is session details json:" + jsonSessDet);
					postDataWithAsyncHttpAndUpdateResponse(l_url,jsonSessDet,2,totalSessDetails,flag,prgDialog1);
				}

				close();
				returnResult = "Sync in progress...";
				//prg.hide();

			}else{

				close();
				returnResult = "No records to sync";
				Toast.makeText(context, returnResult, Toast.LENGTH_LONG).show();
				//prg.hide();
			}
		}catch(Exception ex){
			ex.printStackTrace();
			//prg.hide();
		} 

	}

	public void postDataWithAsyncHttpAndUpdateResponse(String uri,String json, final int func, final int count, final int flag,final ProgressDialog prg){
		//System.out.println("this is json:"+ json+">>this is uri:"+uri);
		final int DEFAULT_TIMEOUT = 30 * 1000;

		if(flag ==1){
			try{

				prg.show();

			}catch(Exception ex){

				ex.printStackTrace();

			}

		}


		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(DEFAULT_TIMEOUT);


		RequestParams params = new RequestParams();
		params.put("userJSON", json);

		client.post(context,uri,params ,new AsyncHttpResponseHandler() {

			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				System.out.println("response"+response);
				Boolean res;
				int f = func;
				System.out.println(func+"::this is response from the server:"+ response);
				if(f==1){
					Log.d("progressDialog", "User table");
					res = handlePostResponse(response,f,count);
					if(res){
						if(flag == 1)
							Toast.makeText(context, "Users Sync completed", Toast.LENGTH_SHORT).show();
					}else{

						Toast.makeText(context, "Users Sync Interrupted !!!", Toast.LENGTH_SHORT).show();
					}
					onUpSyncSessionTable(flag, prg);
				}else if(f==2){
					Log.d("progressDialog", "Sess det table");
					res = handlePostResponse2(response,f,count,prg);
					if(res){
						if(flag == 1)
							Toast.makeText(context, "Session Details Sync completed", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context, "Session Details Sync Interrupted !!!", Toast.LENGTH_SHORT).show();
					}
					
					/*if(prg.isShowing()){
						
						try{
						prg.hide();
						//prg.dismiss();
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
					}*/
					
				}else if(f==3){
					Log.d("progressDialog", "Session table");
					res = handlePostResponse3(response,f,count);
					if(res){
						if(flag == 1)
							Toast.makeText(context, "Session Table Sync completed", Toast.LENGTH_SHORT).show();								
					}else{
						Toast.makeText(context, "Session Table Sync Interrupted !!!", Toast.LENGTH_SHORT).show();								
					}
					onUpSyncSessionDetails(flag, prg);
				}

				Log.d("progressDialog", "Dismiss now");
				//if(flag ==1)
				//if(prg.isShowing())
				//prg.dismiss();
			}

			//@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				//System.out.println("Failure:::::"+ statusCode +" err:::"+error+" content::::" + responseBody.toString());
				Log.d("progressDialog", "Dismiss now");
				if(prg.isShowing()){
					prg.hide();
					//prg.dismiss();
				}

			}
			@Override
			public void onFinish() {
				// Completed the request (either success or failure)
				Log.d("progressDialog", "Dismiss now");

			}

		});

		client.getHttpClient().getConnectionManager().closeExpiredConnections();				

	}


	protected boolean handlePostResponse(String response, int f, int count) {
		// TODO Auto-generated method stub
		String[] status=null,ID=null,sess_id=null,sess_type=null,phr_no=null;
		int total = 0;
		//response = response.substring(response.indexOf('['));
		try {
			Log.v("system","array:"+response);
			response = response.substring(response.indexOf('['));
			JSONArray arr = new JSONArray(response);

			Log.v("system","array:"+arr.toString());
			status = new String[arr.length()];
			ID = new String[arr.length()];
			sess_id = new String[arr.length()];
			sess_type = new String[arr.length()];
			phr_no = new String[arr.length()];

			for(int i=0;i<arr.length();i++){

				JSONObject obj = (JSONObject)arr.get(i);
				status[i] = obj.get("status").toString();
				ID[i] = obj.get("id").toString();
				if(f==1){
					Log.d("users","userstable:::::Status:"+status[i].toString()+">>>>>>>>ID:"+ID[i]);
				}
				/*if(f==2){
						sess_id[i] = obj.get("s_id").toString();
						sess_type[i] = obj.get("session_type").toString();
						phr_no[i] = obj.get("phrase_no").toString();
					//Log.d("users","SessionDetails:::::Status:"+status[i].toString()+">>>>>>>>ID:"+ID[i]);
					}
					if(f==3){
						sess_id[i] = obj.get("s_id").toString();
						sess_type[i] = obj.get("session_type").toString();
					//Log.d("users","SessionTable:::::Status:"+status[i].toString()+">>>>>>>>>ID:"+ID[i]);
					}*/

			}



		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileOperations.write("Syncing exception:"+e.getMessage());
		}

		switch(f){
		case 1:

			for(int i=0;i<ID.length;i++){
				//Log.d("users","asyncHttpres users insert id :"+ ID[i].toString()+"<><>Status:"+status[i].toString());
				String stat = status[i].toString();
				if(stat.equals("yes")){
					//String updateQuery = "UPDATE userdetails SET synched = '1' where _id = '"+ID[i]+"'";
					//Log.d("users","query:"+updateQuery);
					open();
					ContentValues cv = new ContentValues();
					cv.put("synched", "1");
					String[] tempID = {String.valueOf(ID[i])};
					ourUserDatabase.update("userdetails",cv,"_id=?",tempID);
					total ++;
					tempID=null;
					cv.clear();
					close();
				}else{
					System.out.println("Did not insert in userdetails"+status.toString()+"::::ID:"+ID.toString());
				}
			}
			System.out.println("Output total user table:"+total);
			break;
			/*case 2:
				int total1 = 0;
				for(int i=0;i<ID.length;i++){
					//Log.d("users","asyncHttpres sessiondetails insert id :"+ ID[i].toString()+"<><>Status:"+status[i].toString());
					String stat = status[i].toString();
					if(stat.equals("yes")){
						//String updateQuery = "UPDATE userdetails SET synched = '1' where _id = '"+ID[i]+"'";
						//Log.d("users","query:"+updateQuery);
						open();
						ContentValues cv = new ContentValues();
						cv.put("synched", "1");
						String[] tempID = {String.valueOf(ID[i]),String.valueOf(sess_id[i]),String.valueOf(sess_type[i]),String.valueOf(phr_no[i])};
						ourDatabase.update("session_details_table",cv,"u_id=? AND s_id=? AND session_type=? AND phrase_no = ?",tempID);
						tempID=null;
						total1 ++;
						cv.clear();
						close();
					}else{
						System.out.println("Did not insert in session details table:"+status.toString()+"::::ID:"+ID.toString());
					}

				}
				System.out.println("Output total session details table:"+total1);
				state = 2;
				break;
			case 3:
				int total2 = 0;
				for(int i=0;i<ID.length;i++){
					//Log.d("users","asyncHttpres sessiontable insert id :"+ ID[i].toString()+"<><>Status:"+status[i].toString());
					String stat = status[i].toString();
					if(stat.equals("yes")){
						//String updateQuery = "UPDATE userdetails SET synched = '1' where _id = '"+ID[i]+"'";
						//Log.d("users","query:"+updateQuery);
						open();
						ContentValues cv = new ContentValues();
						cv.put("synched", "1");
						String[] tempID = {String.valueOf(ID[i]),String.valueOf(sess_id[i]),String.valueOf(sess_type[i])};
						ourDatabase.update("session_table",cv,"u_id=? AND s_id=? AND session_type=?",tempID);
						tempID=null;
						total2 ++;
						cv.clear();
						close();
					}else{
						System.out.println("Did not insert in session table:"+status.toString()+"::::ID:"+ID.toString());
					}

				}
				System.out.println("Output total session table rows:"+total2);
				state = 3;
				break;*/
		default:
			break;
		}

		if(total==count){
			total = 0;
			return true;
		}else{
			total = 0;
			return false;
		}
	}


	protected boolean handlePostResponse2(String response, int f, int count, ProgressDialog prg) {
		// TODO Auto-generated method stub
		String[] status=null,ID=null,sess_id=null,sess_type=null,phr_no=null,attempt_no=null;
		int total = 0;
		//response = response.substring(response.indexOf('['));
		try {
			response = response.substring(response.indexOf('['));
			JSONArray arr = new JSONArray(response);


			status = new String[arr.length()];
			ID = new String[arr.length()];
			sess_id = new String[arr.length()];
			sess_type = new String[arr.length()];
			phr_no = new String[arr.length()];
			attempt_no = new String[arr.length()];
			for(int i=0;i<arr.length();i++){

				JSONObject obj = (JSONObject)arr.get(i);
				status[i] = obj.get("status").toString();
				ID[i] = obj.get("id").toString();

				if(f==2){
					sess_id[i] = obj.get("s_id").toString();
					sess_type[i] = obj.get("session_type").toString();
					phr_no[i] = obj.get("phrase_no").toString();
					attempt_no[i] = obj.get("attempt_number").toString();
					//Log.d("users","SessionDetails:::::Status:"+status[i].toString()+">>>>>>>>ID:"+ID[i]);
				}


			}



		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileOperations.write("Syncing exception:"+e.getMessage());
		}

		switch(f){

		case 2:

			for(int i=0;i<ID.length;i++){
				//Log.d("users","asyncHttpres sessiondetails insert id :"+ ID[i].toString()+"<><>Status:"+status[i].toString());
				String stat = status[i].toString();
				if(stat.equals("yes")){
					//String updateQuery = "UPDATE userdetails SET synched = '1' where _id = '"+ID[i]+"'";
					//Log.d("users","query:"+updateQuery);
					open();
					ContentValues cv = new ContentValues();
					cv.put("synched", "1");
					String[] tempID = {String.valueOf(ID[i]),String.valueOf(sess_id[i]),String.valueOf(sess_type[i]),String.valueOf(phr_no[i]),String.valueOf(attempt_no[i])};
					ourDatabase.update("session_details_table",cv,"u_id=? AND s_id=? AND session_type=? AND phrase_no = ? AND attempt_number = ?",tempID);
					tempID=null;
					total ++;
					cv.clear();
					close();
				}else{
					System.out.println("Did not insert in session details table:"+status.toString()+"::::ID:"+ID.toString());
				}

			}
			System.out.println("Output total session details table:"+total);
			break;			
		default:
			break;
		}
		open();
		String selectSessionDetailsQuery = "SELECT * FROM session_details_table where synched = '0' limit 50";
		Cursor cursorSessDet = ourDatabase.rawQuery(selectSessionDetailsQuery, null);

		if(cursorSessDet.getCount()==0) {
			//Leaderboard message
			if(prg.isShowing())
				prg.dismiss();

		}else {

			onUpSyncSessionDetails(f, prg);
		}
		close();
		if(total==count){
			total = 0;
			return true;
		}else{
			total = 0;
			return false;
		}
	}

	protected boolean handlePostResponse3(String response, int f, int count) {
		// TODO Auto-generated method stub
		String[] status=null,ID=null,sess_id=null,sess_type=null,phr_no=null;
		int total = 0;
		//response = response.substring(response.indexOf('['));
		try {
			response = response.substring(response.indexOf('['));
			JSONArray arr = new JSONArray(response);


			status = new String[arr.length()];
			ID = new String[arr.length()];
			sess_id = new String[arr.length()];
			sess_type = new String[arr.length()];
			phr_no = new String[arr.length()];

			for(int i=0;i<arr.length();i++){

				JSONObject obj = (JSONObject)arr.get(i);
				status[i] = obj.get("status").toString();
				ID[i] = obj.get("id").toString();

				if(f==3){
					sess_id[i] = obj.get("s_id").toString();
					sess_type[i] = obj.get("session_type").toString();
					//Log.d("users","SessionTable:::::Status:"+status[i].toString()+">>>>>>>>>ID:"+ID[i]);
				}

			}



		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileOperations.write("Syncing exception:"+e.getMessage());
		}


		switch(f){			
		case 3:

			for(int i=0;i<ID.length;i++){
				//Log.d("users","asyncHttpres sessiontable insert id :"+ ID[i].toString()+"<><>Status:"+status[i].toString());
				String stat = status[i].toString();
				if(stat.equals("yes")){
					//String updateQuery = "UPDATE userdetails SET synched = '1' where _id = '"+ID[i]+"'";
					//Log.d("users","query:"+updateQuery);
					open();
					ContentValues cv = new ContentValues();
					cv.put("synched", "1");
					String[] tempID = {String.valueOf(ID[i]),String.valueOf(sess_id[i]),String.valueOf(sess_type[i])};
					ourDatabase.update("session_table",cv,"u_id=? AND s_id=? AND session_type=?",tempID);
					tempID=null;
					total ++;
					cv.clear();
					close();
				}else{
					System.out.println("Did not insert in session table:"+status.toString()+"::::ID:"+ID.toString());
				}

			}
			System.out.println("Output total session table rows:"+total);

			break;
		default:
			break;
		}

		if(total==count){
			total = 0;
			return true;
		}else{
			total = 0;
			return false;
		}
	}
	public void gotoActivity(String kbname, String imename){
		Context c = context;
		Intent i1 = new Intent (c, Session.class);
		context.startActivity(i1);

	}
	public void getDataWithAsyncHttpAndUpdateResponse(String uri,String json, final int func,final String userid, final ProgressDialog prg, final String kbname,final String imename ){
		//System.out.println("this is json:"+ json+">>this is uri:"+uri);
		final int DEFAULT_TIMEOUT = 300 * 1000;
		//Create AsycHttpClient object
		try{

			prg.show();

		}catch(Exception ex){

			ex.printStackTrace();

		}
		AsyncHttpClient client = new AsyncHttpClient();

		final ResponseContainer responseContainer = new ResponseContainer();
		client.setTimeout(DEFAULT_TIMEOUT);
		RequestParams params = new RequestParams();

		params.put("sendJSON", json);

		client.get(url+"getData.php",params,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				System.out.println(response);

				if(func==1){
					int res = handleGetResponse(response,func);
					if(res == 1){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "All Users Downloaded", Toast.LENGTH_SHORT).show();

					}else if(res == 2){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "Users Download Interupted!!!", Toast.LENGTH_SHORT).show();
					}else if (res == 0){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "Already in sync!!!", Toast.LENGTH_SHORT).show();
					}
				}else if(func==2){
					System.out.println(response);
					int res = handleGetResponse2(response,func);
					if(res == 1){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "All Session Details Downloaded", Toast.LENGTH_SHORT).show();

					}else if(res == 0){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "Session Details Download Interupted!!!", Toast.LENGTH_SHORT).show();
					}else if (res == 2){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "Session Details already in sync!!!", Toast.LENGTH_SHORT).show();
					}
					if(prg.isShowing()){
						try{
							prg.hide();
							prg.dismiss();

						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
					//Intent in = new Intent(SyncDatabase.c,"game.MarathiUT.SESSION");
					Intent intentShow = new Intent("game.MarathiUT.SESSION");
					intentShow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Bundle bundle = new Bundle();
					bundle.putInt("userid",Integer.valueOf(userid));
					bundle.putString("kbname", kbname);
					bundle.putString("imename", imename);
					intentShow.putExtras(bundle);
					context.startActivity(intentShow);

				}else if(func==3){
					int res = handleGetResponse3(response,func);
					if(res == 1){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "All Session Table Downloaded", Toast.LENGTH_SHORT).show();

					}else if(res == 0){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "Session Table Download Interupted!!!", Toast.LENGTH_SHORT).show();
					}else if (res == 2){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "Session Table already in sync!!!", Toast.LENGTH_SHORT).show();
					}
					loadSessionDetails(userid, prg, kbname,imename);
				}
				//prgDialog1.hide();
			}
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(prg.isShowing()){
					try{

						prg.hide();


					}catch(Exception ex){

						ex.printStackTrace();

					}
				}
				Intent intentShow = new Intent("game.MarathiUT.SESSION");
				intentShow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle bundle = new Bundle();
				bundle.putInt("userid",Integer.valueOf(userid));
				bundle.putString("kbname", kbname);
				bundle.putString("imename", imename);
				intentShow.putExtras(bundle);
				context.startActivity(intentShow);
				// TODO Auto-generated method stub
				if(statusCode == 404){
					System.out.println("Requested resource not found");
				}else if(statusCode == 500){
					System.out.println("Something went wrong at server end");
				}else{
					System.out.println("Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]");
				}
				//prgDialog1.hide();
			}
		});


		client.getHttpClient().getConnectionManager().closeExpiredConnections();
	}

	/*public String postDataOnlyHttp(String uri, String json,final int func){
		String localres = null;
		HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(uri);
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("userJSON", json));

	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        String res = EntityUtils.toString(response.getEntity());
	        System.out.println("this is actual out put "+res);
	        res = res.substring(res.indexOf('[')); 
	        handleGetResponse(res, func);
	        Log.d("tag12","this is the out put "+res);
	        localres = res;        

	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    	Log.d("tag12","CATCH ClientProtocolException error:"+e);
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	Log.d("tag12","CATCH IOEXCEPTION error:"+e);
	    } 
		return localres;
	}*/
	public boolean getSessionDetails(String user_id, String sess_id, String sess_type, String phr_no, String attempt_no ){

		String query = "select * from session_details_table where u_id='"+user_id+"' AND s_id='"+sess_id+"' AND session_type='"+sess_type+"' AND phrase_no='"+phr_no+"' AND attempt_number='"+attempt_no+"'";
		//String[] tempID = {user_id,sess_id,sess_type,phr_no};
		Cursor c = ourDatabase.rawQuery(query, null);
		int rows = c.getCount();

		if(rows > 0)
			return false;
		else
			return true;
	}

	public boolean getSessionTable(String user_id, String sess_id, String sess_type){

		String query = "select * from session_table where u_id='"+user_id+"' AND s_id='"+sess_id+"' AND session_type='"+sess_type+"'";
		//String[] tempID = {user_id,sess_id,sess_type};
		Cursor c = ourDatabase.rawQuery(query, null);
		int rows = c.getCount();

		if(rows > 0)
			return false;
		else
			return true;
	}

	public int handleGetResponse(String response, int f){
		// TODO Auto-generated method stub
		/*if(f==2){
			System.out.print("inside response "+response);
			Log.d("sTring","inside response "+response);
		}*/
		open();
		String[] _id=null,u_fname=null,u_school=null,u_standard=null,u_div=null,u_age=null,u_keyboard=null,u_std_rating=null;
		String[] u_imename=null,u_rows=null,idArr=null;
		//String ids=null;
		int totalinserts = 0;
		int checkNonInserts = 0;
		response = response.substring(response.indexOf('['));
		try {

			JSONArray arr = new JSONArray(response);
			/*String l_url = url+"getData.php";		
				ArrayList<HashMap<String, String>> wordListSessDet = new ArrayList<HashMap<String, String>>();
				ArrayList<HashMap<String, String>> wordListSessTable = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map = new HashMap<String, String>();
				HashMap<String, String> map2 = new HashMap<String, String>();*/
			//map.put("userId", cursor.getString(0));


			//Use GSON to serialize Array List to JSON
			/*Gson gson = new GsonBuilder().create();
			String jsonSessDetTable="",jsonSessTable=null;*/

			System.out.print("users data:::"+arr.toString());
			_id=new String[arr.length()];
			u_fname=new String[arr.length()];
			u_school=new String[arr.length()];
			u_standard=new String[arr.length()];
			u_div=new String[arr.length()];
			u_age=new String[arr.length()];
			u_keyboard=new String[arr.length()];
			u_imename=new String[arr.length()];
			u_std_rating=new String[arr.length()];
			u_rows=new String[arr.length()];

			Log.d("user_table","This is user table"+f);



			String selectQuery = "Select _id from userdetails order by _id";
			Cursor c = ourUserDatabase.rawQuery(selectQuery, null);
			idArr= new String[c.getCount()];
			if(c.moveToFirst()){

				int m=0;
				do{
					idArr[m] = c.getString(0);
					m++;
				}while(c.moveToNext());

			}




			for(int i=0;i<arr.length();i++){

				JSONObject obj = (JSONObject)arr.get(i);

				if(f==1){

					_id[i]=obj.get("_id").toString();
					u_fname[i]=obj.get("u_fname").toString();
					u_school[i]=obj.get("u_school").toString();
					u_standard[i]=obj.get("u_standard").toString();
					u_div[i]=obj.get("u_div").toString();
					u_age[i]=obj.get("u_age").toString();
					u_keyboard[i]=obj.get("u_keyboard").toString();
					u_imename[i]=obj.get("u_imename").toString();
					u_std_rating[i]=obj.get("u_std_rating").toString();
					u_rows[i]=obj.get("rows").toString();
					Log.d("users",">>>>>>>>>>>>>>>>>UID:"+_id[i]);


					/*map.clear();
						map2.clear();
						wordListSessDet.clear();
						wordListSessTable.clear();*/

					int k=0;
					for(int j=0;j<idArr.length;j++){
						if(_id[i].equals(idArr[j])){
							k++;
						}
					}
					if(k==0){

						ContentValues cv = new ContentValues();
						cv.put("_id",_id[i]);
						cv.put("u_fname",u_fname[i]);
						cv.put("u_lname","");
						cv.put("u_school",u_school[i]);
						cv.put("u_standard",u_standard[i]);
						cv.put("u_div",u_div[i]);
						cv.put("u_age",u_age[i]);
						cv.put("u_keyboard",u_keyboard[i]);
						cv.put("u_imename",u_imename[i]);
						cv.put("u_std_rating",u_std_rating[i]);
						cv.put("synched","1");

						long st = ourUserDatabase.insert("userdetails",null,cv);
						Log.d("insert","user id :"+_id[i]+" insert id"+st);
						totalinserts++;

						System.out.print("hello insert"+_id[i]+"\n");

					}else{
						k=0;
						System.out.print("no insert :'( "+_id[i]);
						totalinserts++;
						checkNonInserts++;
					}

				}

			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		close();
		Log.d("user_table","value of rows "+Integer.valueOf(u_rows[0]));
		if(Integer.valueOf(u_rows[0])== checkNonInserts){
			checkNonInserts=0;
			return 0;
		}else{
			if(totalinserts == Integer.valueOf(u_rows[0])){
				return 1;
			}else{
				return 2;
			}
		}



		/*switch(f){
			case 1:
				Log.d("user_table","This is user table"+f);
				String l_url = url+"getData.php";		
				ArrayList<HashMap<String, String>> wordListSessDet = new ArrayList<HashMap<String, String>>();
				ArrayList<HashMap<String, String>> wordListSessTable = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map = new HashMap<String, String>();
				HashMap<String, String> map2 = new HashMap<String, String>();
	        	//map.put("userId", cursor.getString(0));


	    		//Use GSON to serialize Array List to JSON
	    		Gson gson = new GsonBuilder().create();
	    		String jsonSessDetTable="",jsonSessTable=null;
				open();
				int total = 0;
				String selectQuery = "Select _id from userdetails order by _id";
				Cursor c = ourUserDatabase.rawQuery(selectQuery, null);
				String[] idArr= new String[c.getCount()];
				if(c.moveToFirst()){

					int m=0;
					do{
						idArr[m] = c.getString(0);
						m++;
					}while(c.moveToNext());

				}


				for(int i=0;i<_id.length;i++){
					map.clear();
					map2.clear();
					wordListSessDet.clear();
					wordListSessTable.clear();

					int k=0;
					for(int j=0;j<idArr.length;j++){
						if(_id[i].equals(idArr[j])){
							k++;
						}
					}
					if(k==0){

						ContentValues cv = new ContentValues();
							cv.put("_id",_id[i]);
							cv.put("u_fname",u_fname[i]);
							cv.put("u_lname","");
							cv.put("u_school",u_school[i]);
							cv.put("u_standard",u_standard[i]);
							cv.put("u_div",u_div[i]);
							cv.put("u_age",u_age[i]);
							cv.put("u_keyboard",u_keyboard[i]);
							cv.put("u_imename",u_imename[i]);
							cv.put("u_std_rating",u_std_rating[i]);
							cv.put("synched","1");

							long st = ourUserDatabase.insert("userdetails",null,cv);
							Log.d("insert","user id :"+_id[i]+" insert id"+st);
							if(st>0){
								map.put("action", "get");
								map.put("act", "insert");
					    		map.put("tablename", "session_details_table");
					    		map.put("u_id", _id[i]);
					    		wordListSessDet.add(map);				    		
					    		Log.d("map",map.toString());
					    		jsonSessDetTable = gson.toJson(wordListSessDet);
					    		//Log.d("sessionDetailsTable","json :"+jsonSessDetTable);
					    		getDataWithAsyncHttpAndUpdateResponse(l_url,jsonSessDetTable,2);


					    		map2.put("action", "get");
								map2.put("act", "insert");
					    		map2.put("tablename", "session_table");
					    		map2.put("u_id", _id[i]);
					    		wordListSessTable.add(map2);
					    		jsonSessTable = gson.toJson(wordListSessTable);
					    		//Log.d("sessionDetailsTable","json :"+jsonSessTable);
					    		getDataWithAsyncHttpAndUpdateResponse(l_url,jsonSessTable,3);
							}else{
								Log.d("user_table","user details not downloaded");
							}
							System.out.print("hello insert"+_id[i]+"\n");

					}else{
						k=0;
						System.out.print("no insert :'( "+_id[i]);
						map.put("action", "get");
						map.put("act", "update");
			    		map.put("tablename", "session_details_table");
			    		map.put("u_id", _id[i]);
			    		wordListSessDet.add(map);
			    		Log.d("map",map.toString());
			    		jsonSessDetTable = gson.toJson(wordListSessDet);
			    		//Log.d("sessionDetailsTable","json :"+jsonSessDetTable);
			    		getDataWithAsyncHttpAndUpdateResponse(l_url,jsonSessDetTable,2);

			    		map2.put("action", "get");
						map2.put("act", "update");
			    		map2.put("tablename", "session_table");
			    		map2.put("u_id", _id[i]);
			    		wordListSessTable.add(map2);
			    		jsonSessTable = gson.toJson(wordListSessTable);
			    		//Log.d("sessionDetailsTable","json :"+jsonSessTable);
			    		getDataWithAsyncHttpAndUpdateResponse(l_url,jsonSessTable,3);
					}

				}

				close();
				System.out.println("Output total affected:"+total);
				break;

			default:
				break;
		}*/
	}

	public int handleGetResponse2(String response, int f){
		// TODO Auto-generated method stub
		System.out.print("inside response "+response);
		/*if(f==2){
			System.out.print("inside response "+response);
			Log.d("sTring","inside response "+response);
		}*/open();

		int totalcalls = 0;
		String[] u_id=null,s_id=null,session_type=null,phrase_no=null,phrase_xml_id=null,phrase_shown = null,f_timestamp=null,l_timestamp=null,phrase_typed=null,keystroke_log=null,edit_distance=null,attempt_number=null;
		String[] act=null, rows=null;
		response = response.substring(response.indexOf('['));
		int arrExist = 0;
		try {

			JSONArray arr = new JSONArray(response);
			if(arr.length()>0){
				arrExist=1;
				if(f==2){
					System.out.print("sessionta details table data:::"+arr.toString());
					u_id=new String[arr.length()];
					s_id=new String[arr.length()];
					session_type=new String[arr.length()];
					phrase_no=new String[arr.length()];
					phrase_xml_id=new String[arr.length()];
					//phrase_shown=new String[arr.length()];
					f_timestamp=new String[arr.length()];
					l_timestamp=new String[arr.length()];
					phrase_typed=new String[arr.length()];
					keystroke_log=new String[arr.length()];
					edit_distance=new String[arr.length()];
					attempt_number=new String[arr.length()];
					act = new String[arr.length()];
					rows = new String[arr.length()];
					//Log.d("sTring","length::"+act.length+"sessiondetails data:::"+arr.toString());
				}

				for(int i=0;i<arr.length();i++){

					JSONObject obj = (JSONObject)arr.get(i);


					if(f==2){
						//Log.d("sTring","sTring::"+obj.get("phrase_typed").toString());
						u_id[i]=obj.get("u_id").toString();
						s_id[i]=obj.get("s_id").toString();
						session_type[i]=obj.get("session_type").toString();
						phrase_no[i]=obj.get("phrase_no").toString();
						phrase_xml_id[i]=obj.get("phrase_xml_id").toString();

						if(obj.get("f_timestamp").toString().equals("null"))
							f_timestamp[i]="";
						else
							f_timestamp[i]=obj.get("f_timestamp").toString();

						if(obj.get("l_timestamp").toString().equals("null"))
							l_timestamp[i]="";
						else
							l_timestamp[i]=obj.get("l_timestamp").toString();
						/*
						if(obj.get("phrase_shown").toString().equals("null"))
							phrase_shown[i]="";
						else
							phrase_shown[i]=obj.get("phrase_shown").toString();*/

						if(obj.get("phrase_typed").toString().equals("null"))
							phrase_typed[i]="";
						else
							phrase_typed[i]=obj.get("phrase_typed").toString();

						if(obj.get(SessionDetailsTable.KEYSTROKE_LOG).toString().equals("null"))
							keystroke_log[i]="";
						else
							keystroke_log[i]=obj.get(SessionDetailsTable.KEYSTROKE_LOG).toString();

						if(obj.get("edit_distance").toString().equals("null"))
							edit_distance[i]="";
						else
							edit_distance[i]=obj.get("edit_distance").toString();

						if(obj.get("attempt_number").toString().equals("null"))
							attempt_number[i]="";
						else
							attempt_number[i]=obj.get("attempt_number").toString();

						if(obj.has("act")){
							act[i]=obj.get("act").toString();
						}

						rows[i]=obj.get("rows").toString();

						Boolean result = getSessionDetails(u_id[i], s_id[i], session_type[i], phrase_no[i], attempt_number[i]);
						Log.d("sessionDetailsTable","sessionDetailsTable insert::"+u_id[i].toString()+"\n");
						if(result){
							System.out.print("session detail table i::"+act[i].toString());
							ContentValues cv = new ContentValues();
							cv.put("u_id",u_id[i]);
							cv.put("s_id",s_id[i]);
							cv.put("session_type",session_type[i]);
							cv.put("phrase_no",phrase_no[i]);
							cv.put("phrase_xml_id",phrase_xml_id[i]);
							//cv.put("phrase_shown",phrase_shown[i]);
							cv.put("f_timestamp",f_timestamp[i]);
							cv.put("l_timestamp",l_timestamp[i]);
							cv.put("phrase_typed",phrase_typed[i]);
							cv.put(SessionDetailsTable.KEYSTROKE_LOG,keystroke_log[i]);
							cv.put("edit_distance",edit_distance[i]);
							cv.put("attempt_number",attempt_number[i]);
							cv.put("synched","1");

							ourDatabase.insert("session_details_table",null,cv);
							totalcalls++;
							cv.clear();

						}else{
							Log.d("sessionDetailsTable","sessionDetailsTable update::"+u_id[i].toString()+"\n");
							ContentValues cv = new ContentValues();

							cv.put("phrase_xml_id",phrase_xml_id[i]);
							cv.put("f_timestamp",f_timestamp[i]);
							cv.put("l_timestamp",l_timestamp[i]);
							//cv.put("phrase_shown",phrase_shown[i]);
							cv.put("phrase_typed",phrase_typed[i]);
							cv.put(SessionDetailsTable.KEYSTROKE_LOG,keystroke_log[i]);
							cv.put("edit_distance",edit_distance[i]);
							cv.put("attempt_number",attempt_number[i]);
							cv.put("synched","1");

							String[] tempID = {String.valueOf(u_id[i]),String.valueOf(s_id[i]),String.valueOf(session_type[i]),String.valueOf(phrase_no[i]), String.valueOf(attempt_number[i])};
							ourDatabase.update("session_details_table",cv,"u_id=? AND s_id=? AND session_type=? AND phrase_no = ? AND attempt_number = ?",tempID);
							totalcalls++;
							cv.clear();
						}




					}



				}
			}else{
				arrExist = 2;
			}	

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(arrExist==1){
			Cursor ourCursor = ourDatabase.rawQuery("select * from session_details_table where u_id = '"+u_id[0]+"' order by s_id asc",null);
			//ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();
			ourCursor.moveToFirst();
			String[] result = new String[len];

			Log.d("sessionDetailsTable","rows count" + len);
			for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
				result[i] = "User "+ ourCursor.getString(0) + "," +ourCursor.getString(1) + "," +ourCursor.getString(2) + "," +ourCursor.getString(3) + "," +ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6)+ "," +ourCursor.getString(7);
				System.out.println(i+"--"+result[i]);
				Log.d("sessionDetailsTable","User-id: "+ ourCursor.getString(0) + "," +ourCursor.getString(1) + "," +ourCursor.getString(2) + "," +ourCursor.getString(3) + "," +ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7));
			}
			close();


			if(rows[0] !=null && totalcalls == Integer.valueOf(rows[0])){
				return 1;
			}else{
				return 0;
			}
		}else if(arrExist==2){
			return 2;
		}else {
			return 0;
		}
		/*switch(f){			
			case 2:
				Log.d("session_details_table","This is session details table"+f);
				int total1 = 0;
				//SessionDetailsTable sDT = new SessionDetailsTable(context);
				//sDT.getDetails();
				int exeFlag = 0 ;

				for(int i=0;i<u_id.length;i++){


				}
				if(exeFlag>0){
					exeFlag=0;
					//sDT.getDetails();
				}
				System.out.println("Output total affected:"+total1);
				break;

			default:
				break;
		}*/
	}

	public int handleGetResponse3(String response, int f){
		// TODO Auto-generated method stub
		/*if(f==2){
			System.out.print("inside response "+response);
			Log.d("sTring","inside response "+response);
		}*/
		open();
		String[] u_id=null,s_id=null,session_type=null;
		String[] rows=null,session_rating=null,session_start=null,session_end=null,session_status=null, act=null,session_continue_from=null;
		int arrExist=0;
		int total2 = 0;
		response = response.substring(response.indexOf('['));
		try {

			JSONArray arr = new JSONArray(response);
			if(arr.length()>0){
				arrExist=1;


				System.out.print("sessiontable data:::"+arr.toString());
				u_id=new String[arr.length()];
				s_id=new String[arr.length()];
				session_type=new String[arr.length()];
				session_rating=new String[arr.length()];
				session_start=new String[arr.length()];
				session_end=new String[arr.length()];
				session_continue_from=new String[arr.length()];
				session_status=new String[arr.length()];
				rows=new String[arr.length()];
				act = new String[arr.length()];

				for(int i=0;i<arr.length();i++){

					JSONObject obj = (JSONObject)arr.get(i);


					u_id[i]=obj.get("u_id").toString();
					s_id[i]=obj.get("s_id").toString();
					session_type[i]=obj.get("session_type").toString();
					session_rating[i]=obj.get("session_rating").toString();
					session_start[i]=obj.get("session_start").toString();
					session_end[i]=obj.get("session_end").toString();
					session_continue_from[i]=obj.get("continue_from").toString();
					session_status[i]=obj.get("session_status").toString();
					rows[i]=obj.get("rows").toString();
					if(obj.has("act")){
						act[i]=obj.get("act").toString();
					}

					Boolean result = getSessionTable(u_id[i], s_id[i], session_type[i]);
					if(result){
						Log.d("sessionTable","session table insert::"+u_id[i].toString()+"\n");
						ContentValues cv = new ContentValues();

						cv.put("u_id",u_id[i]);
						cv.put("s_id",s_id[i]);
						cv.put("session_type",session_type[i]);
						cv.put("session_rating",session_rating[i]);
						cv.put("session_start",session_start[i]);
						cv.put("session_end",session_end[i]);
						cv.put("continue_from", session_continue_from[i]);
						cv.put("session_status",session_status[i]);
						cv.put("synched","1");

						ourDatabase.insert("session_table",null,cv);
						cv.clear();

					}else{
						Log.d("sessionTable","session table update ::"+u_id[i].toString()+"\n");
						ContentValues cv = new ContentValues();
						cv.put("session_rating",session_rating[i]);
						cv.put("session_start",session_start[i]);
						cv.put("session_end",session_end[i]);
						cv.put("continue_from", session_continue_from[i]);
						cv.put("session_status",session_status[i]);
						cv.put("synched","1");

						String[] tempID = {String.valueOf(u_id[i]),String.valueOf(s_id[i]),String.valueOf(session_type[i])};
						ourDatabase.update("session_table",cv,"u_id=? AND s_id=? AND session_type=?",tempID);
						cv.clear();
					}


					total2++;


				}

			}else{
				arrExist = 2;
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(arrExist==1){
			Cursor ourCursor = ourDatabase.rawQuery("select * from session_table where u_id = '"+u_id[0]+"' order by s_id asc",null);
			//ourDatabase.rawQuery(query, null);
			int len = ourCursor.getCount();
			ourCursor.moveToFirst();
			String[] result = new String[len];

			Log.d("sessionTable","rows count" + len);
			for (int i = 0; i < len; i++, ourCursor.moveToNext()) {
				result[i] = "User "+ ourCursor.getString(0) + "," +ourCursor.getString(1) + "," +ourCursor.getString(2) + "," +ourCursor.getString(3) + "," +ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6)+ "," +ourCursor.getString(7)+ "," +ourCursor.getString(8);
				//System.out.println(i+"--"+result[i]);
				Log.d("sessionTable",i+"--User-id: "+ ourCursor.getString(0) + "," +ourCursor.getString(1) + "," +ourCursor.getString(2) + "," +ourCursor.getString(3) + "," +ourCursor.getString(4) + "," +ourCursor.getString(5) + "," +ourCursor.getString(6) + "," +ourCursor.getString(7)+ "," +ourCursor.getString(8));
			}

			close();

			if(total2 == Integer.valueOf(rows[0])){
				return 1;
			}else{
				return 0;
			}
		}else if(arrExist==2){
			return 2;
		}else {
			return 0;
		}
		/*switch(f){			
			case 3:
				Log.d("session_table","This is session table"+f);
				int total2 = 0;
				for(int i=0;i<u_id.length;i++){


				}
				System.out.println("Output total affected:"+total2);
				break;
			default:
				break;
		}*/
	}


	public String downSync(){
		//prgDialog1 = prg;
		//prgDialog1.show();
		/*RequestQueue mRequestQueue;

		// Instantiate the cache
		Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024*1024); // 1MB cap

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();*/
		open();

		String selectUserQuery = "SELECT _id FROM userdetails where synched = '0'";
		String selectSessionDetailsQuery = "SELECT u_id FROM session_details_table where synched = '0'";
		String selectSessionTableQuery = "SELECT u_id FROM session_table where synched = '0'";


		Cursor cursorUser = ourUserDatabase.rawQuery(selectUserQuery, null);
		Cursor cursorSessDet = ourDatabase.rawQuery(selectSessionDetailsQuery, null);
		Cursor cursorSessTable = ourDatabase.rawQuery(selectSessionTableQuery, null);

		if(cursorUser.getCount()>0 || cursorSessDet.getCount()>0 || cursorSessTable.getCount()>0){
			String message = context.getResources().getString(R.string.upsync_first);
			close();
			return message;

		}else{
			//String localres = null;
			String l_url = url+"getData.php";
			Gson gson = new GsonBuilder().create();
			//String returnResult = null;

			ArrayList<HashMap<String, String>> wordListUser;
			wordListUser = new ArrayList<HashMap<String, String>>();
			//wordListSessDet = new ArrayList<HashMap<String, String>>();
			//wordListSessTable = new ArrayList<HashMap<String, String>>();



			HashMap<String, String> map = new HashMap<String, String>();
			//map.put("userId", cursor.getString(0));

			map.put("synched", "1");
			map.put("action", "get");
			map.put("tablename", "userdetails");	    		

			wordListUser.add(map);


			//Use GSON to serialize Array List to JSON
			String jsonUserTable = gson.toJson(wordListUser);

			//getDataWithAsyncHttpAndUpdateResponse(l_url,jsonUserTable,1);

			String callurl1= l_url+"?sendJSON="+ jsonUserTable;



			//Moved to activity
			/*StringRequest stringRequest1 = new StringRequest(Request.Method.GET, callurl1,
		        new Response.Listener<String>() {
		    @Override
		    public void onResponse(String response) {
		        // Do something with the response
		    	int func = 1;
		    	if(func==1){
					int res = handleGetResponse(response,func);
					if(res == 1){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "All Users Downloaded", Toast.LENGTH_SHORT).show();

					}else if(res == 2){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "Users Download Interupted!!!", Toast.LENGTH_SHORT).show();
					}else if (res == 0){
						//responseContainer.resContainerResult=res;
						Toast.makeText(context, "Already in sync!!!", Toast.LENGTH_SHORT).show();
					}
				}
		    }
		},
		    new Response.ErrorListener() {
		       	@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub

				}
		});


		// Add the request to the RequestQueue.
		mRequestQueue.add(stringRequest1);*/


			close();
			//returnResult = "";


			//return "Downloading....";
			return callurl1;

		}

	}

	public String loadSessions(String userid, ProgressDialog prg,String kbname, String imename){
		String toastMessage="nothing";
		String l_url = url+"getData.php";
		Gson gson = new GsonBuilder().create();


		ArrayList<HashMap<String, String>> wordListSessTable = new ArrayList<HashMap<String, String>>();



		HashMap<String, String> map_st = new HashMap<String, String>();
		//map.put("userId", cursor.getString(0));

		map_st.put("action", "get");
		map_st.put("act", "insert");
		map_st.put("tablename", "session_table");
		map_st.put("u_id", userid);
		wordListSessTable.add(map_st);   		



		//Use GSON to serialize Array List to JSON

		String jsonSessTable = gson.toJson(wordListSessTable);

		getDataWithAsyncHttpAndUpdateResponse(l_url,jsonSessTable,3,userid, prg, kbname, imename);


		toastMessage = "Downloading....";

		return toastMessage;

	}


	public String loadSessionDetails(String userid, ProgressDialog prg, String kbname, String imename){
		String toastMessage="nothing";
		String l_url = url+"getData.php";
		Gson gson = new GsonBuilder().create();

		ArrayList<HashMap<String, String>> wordListSessDet = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();

		//map.put("userId", cursor.getString(0));

		map.put("action", "get");
		map.put("act", "insert");
		map.put("tablename", "session_details_table");
		map.put("u_id", userid);
		wordListSessDet.add(map); 


		//Use GSON to serialize Array List to JSON
		String jsonSessDetTable = gson.toJson(wordListSessDet);
		getDataWithAsyncHttpAndUpdateResponse(l_url,jsonSessDetTable,2,userid,prg, kbname, imename);

		toastMessage = "Downloading....";
		close();
		return toastMessage;

	}

	public boolean checkUnsychedSessions(int userid){
		open();
		ArrayList<HashMap<String, String>> wordListSessTable = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> wordListSessDetails = new ArrayList<HashMap<String, String>>();
		String selectSessionTableQuery = "SELECT * FROM session_table where u_id='"+userid+"' and synched = '0'";
		String selectSessionDetailsQuery = "SELECT * FROM session_details_table where u_id='"+userid+"' and synched = '0'";


		Cursor cursorSessTable = ourDatabase.rawQuery(selectSessionTableQuery, null);
		Cursor cursorSessDetTable = ourDatabase.rawQuery(selectSessionDetailsQuery, null);


		if(cursorSessTable.getCount()>0 || cursorSessDetTable.getCount()>0){
			close();
			return true;
		}else{
			close();
			return false;
		}
	}

	public static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME_SESS, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {


		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


		}

	}

	public static class DbHelperUser extends SQLiteOpenHelper {

		public DbHelperUser(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {


		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


		}

	}

	private class ResponseContainer {
		public boolean resContainerResult;
	}

	public String escapeString(String text){
		for(int i=0;i<escapeCharacters.length;i++){
			//TODO: throws a java.lang.NullPointerException on trying to sync
			if(text!=null && text.length()>0 && text.contains(escapeCharacters[i])){
				text = text.replace(escapeCharacters[i],"\\"+escapeCharacters[i]);
			}
		}
		return text;
	}


}
