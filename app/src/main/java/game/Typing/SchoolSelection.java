package game.Typing;

import game.Typing.validation.AlternativesParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
//import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class SchoolSelection extends ActionBarActivity implements
OnItemClickListener,OnClickListener {


	Button userlist, register;
	Button upsync,downsync,dump;

	//Spinner School;
	ListView School;

	String[] schools;
	String userSchool;

	Database DB;
	SessionDetailsTable sDB;
	ProgressDialog prgUserData;
	static int downloadCounter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_school_selection);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		prgUserData = new ProgressDialog(this);
		prgUserData.setMessage(getString(R.string.please_wait_uploading));
		prgUserData.setCancelable(false);

		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		/*ActionBar actionBar = getActionBar();
		actionBar.hide();*/

		FileOperations.enableLogging(true); //later can be optional

		Intent it = getIntent();
		Bundle b= it.getExtras();
		try{

			userSchool = b.getString("schoolname");

		}catch(Exception ex){

			Log.d("debug","School name not available");
			userSchool = "";

		}

		userlist = (Button) findViewById(R.id.user_list);
		upsync = (Button) findViewById(R.id.upsync);
		downsync = (Button) findViewById(R.id.downsync);
		dump = (Button) findViewById(R.id.dump);
		register = (Button) findViewById(R.id.newUser);

		School = (ListView) findViewById(R.id.school_list);

		DB = new Database(this);
		sDB = new SessionDetailsTable(this);


		schools = DB.getSchoolName();

		if(schools ==null){
			schools = new String[1];
			schools[0]=getResources().getString(R.string.no_users_in_db);		

		}	
		Log.d("debug","School list not null");

		CustomArrayAdapter adapter = new CustomArrayAdapter(this,schools); 
		School.setAdapter(adapter);


		//Update view when data changes
		((CustomArrayAdapter) School.getAdapter()).notifyDataSetChanged(); 

		School.setOnItemClickListener(this);
		/*new OnItemClickListener(){

			@SuppressWarnings("null")
			@Override

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				userSchool = School.getSelectedItem().toString();
				userlist.setEnabled(true);
				upsync.setEnabled(true);
				downsync.setEnabled(true);
				dump.setEnabled(true);
				Log.d("debug","Item selected from School list");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				Log.d("debug","No item selected from School list");
			}

		});*/

		//}

		userlist.setOnClickListener(this);
		upsync.setOnClickListener(this);
		downsync.setOnClickListener(this);
		dump.setOnClickListener(this);
		register.setOnClickListener(this);

		if(sDB.isPhraseTablePopulated() == false)
			sDB.populatePhrasesTable();	
		else
			Log.d("xml","phrase table exists");
		//int c = sDB.getTotalSessionCount();
		if(userSchool!=null){

			int i;
			for(i=0; i<schools.length;i++){
				if(userSchool.compareToIgnoreCase(schools[i])==0)
					break;
			}

			if(i <schools.length){
				CustomArrayAdapter clva = (CustomArrayAdapter) School.getAdapter();
				clva.setSelectedIndex(i);
			}

		}


	}

	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub

		final Bundle bundle = new Bundle();

		switch (v.getId()) {

		case R.id.newUser:
			Intent intentReg = new Intent("game.MarathiUT.REGISTER");
			startActivity(intentReg);
			break;
		case R.id.user_list:
			if(userSchool.compareToIgnoreCase(getResources().getString(R.string.no_users_in_db))==0)
				return;
			Intent intentShow = new Intent("game.MarathiUT.FIRSTSCREEN");
			bundle.putString("schoolname",userSchool);
			intentShow.putExtras(bundle);
			startActivity(intentShow);

			break;

		case R.id.upsync:

			/*register.setEnabled(false);
				userlist.setEnabled(false);
				upsync.setEnabled(false);
				downsync.setEnabled(false);
				dump.setEnabled(false);
				School.setEnabled(false);*/

			SyncDatabase syncDB = new SyncDatabase(getApplicationContext());
			syncDB.onUpSync(1,prgUserData);
			//Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();

			/*if (status.compareToIgnoreCase("No records to sync")==0){
					register.setEnabled(true);
					userlist.setEnabled(true);
					upsync.setEnabled(true);
					downsync.setEnabled(true);
					dump.setEnabled(true);
					School.setEnabled(true);
				}*/
			/*final ScheduledExecutorService work = 
						  Executors.newSingleThreadScheduledExecutor();


						  Runnable run = new Runnable() {
						    public void run() {
						       Do something… 
					    	 	//Intent intentShow = new Intent("game.MarathiUT.FIRSTSCREEN");							
								//startActivity(intentShow);

						    	register.setEnabled(true);
								userlist.setEnabled(true);
								upsync.setEnabled(true);
								downsync.setEnabled(true);
								dump.setEnabled(true);
								School.setEnabled(true);
						    	Toast.makeText(getApplicationContext(), "Upload complete", Toast.LENGTH_SHORT).show();
						    }
						  };
						  work.schedule(run, 5, TimeUnit.SECONDS);*/
			break;

		case R.id.downsync:

			AlternativesParser alt = new AlternativesParser(this);
			DamerauLevenshteinAlgorithm editDistance = new DamerauLevenshteinAlgorithm(1,1,1,1);

			//Log.d("alt","\u0905\u0930\u094d\u200d\u0925");
			//alt.replaceAlternatives("\u0905\u0930\u094d\u200d\u0925");

			/*Log.d("alt","two spaces:" + editDistance.execute(getString(R.string.test_2spaces),alt.replaceAlternatives(getString(R.string.test_2spaces_mod))));
				Log.d("alt","spaces:" + editDistance.execute( getString(R.string.test_spaces),alt.replaceAlternatives( getString(R.string.test_spaces_mod) ) ) );*/

			/*Log.d("alt","two spaces:" + editDistance.execute("\u0905 \u0915 \u0930 \u0931", alt.replaceAlternatives(alt.replaceAlternatives("\u0905 \u0915 \u0930 \u0931")) ));
				Log.d("alt","three spaces:" + editDistance.execute("\u0905 \u0915 \u0930 \u0931", alt.replaceAlternatives(alt.replaceAlternatives(" \u0905   \u0915   \u0930   \u0931")) ));
				Log.d("alt","four spaces:" + editDistance.execute("\u0905 \u0915 \u0930 \u0931", alt.replaceAlternatives(alt.replaceAlternatives(" \u0905    \u0915    \u0930    \u0931")) ));
				Log.d("alt","five spaces:" + editDistance.execute("\u0905 \u0915 \u0930 \u0931", alt.replaceAlternatives(alt.replaceAlternatives(" \u0905     \u0915     \u0930     \u0931")) ));
				Log.d("alt","six spaces:" + editDistance.execute("\u0905 \u0915 \u0930 \u0931", alt.replaceAlternatives(alt.replaceAlternatives(" \u0905      \u0915      \u0930      \u0931")) ));
				Log.d("alt","mix spaces:" + editDistance.execute("\u0905 \u0915 \u0930 \u0931", alt.replaceAlternatives(alt.replaceAlternatives(" \u0905 \u0915   \u0930    \u0931")) ));*/
			//Log.d("alt","spaces:" + editDistance.execute( "","" ) );
			/*Log.d("alt","eyelash1:" + editDistance.execute(getString(R.string.test_spaces),alt.replaceAlternatives(getString(R.string.test_spaces_mod))));
				Log.d("alt","eyelash2:" + editDistance.execute(getString(R.string.test_spaces),alt.replaceAlternatives(getString(R.string.test_spaces_mod))));
				Log.d("alt","spaces:" + editDistance.execute(getString(R.string.test_spaces),alt.replaceAlternatives(getString(R.string.test_spaces_mod))));
				Log.d("alt","spaces:" + editDistance.execute(getString(R.string.test_spaces),alt.replaceAlternatives(getString(R.string.test_spaces_mod))));*/

			//Log.d("alt",alt.replaceAlternatives("\u0905\u0930\u094d\u200d\u0925")); 

			/*register.setEnabled(false);
				userlist.setEnabled(false);
				upsync.setEnabled(false);
				downsync.setEnabled(false);
				dump.setEnabled(false);
				School.setEnabled(false);*/

			/*SyncDatabase syncDB1 = new SyncDatabase(getApplicationContext());
				String status1 = syncDB1.downSync();
				Toast.makeText(getApplicationContext(), status1, Toast.LENGTH_LONG).show();
				final ScheduledExecutorService worker1 = 
						  Executors.newSingleThreadScheduledExecutor();


						  Runnable task1 = new Runnable() {
						    public void run() {
						       Do something… 
					    	 	Intent intentShow = new Intent("game.MarathiUT.FIRSTSCREEN");							
								startActivity(intentShow);
						    	register.setEnabled(true);
								userlist.setEnabled(true);
								upsync.setEnabled(true);
								downsync.setEnabled(true);
								dump.setEnabled(true);
								School.setEnabled(true);
						    	Toast.makeText(getApplicationContext(), "Download complete", Toast.LENGTH_SHORT).show();
						    }
						  };
						  worker1.schedule(task1, 5, TimeUnit.SECONDS);*/
			//if(userSchool!=null || userSchool.length() >0)
			downloadAll("");
			break;
		case R.id.dump:
			//Toast.makeText(this, "DB Exported!", Toast.LENGTH_SHORT).show();
			exportDB();
			break;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		userSchool =(String) School.getItemAtPosition(position);
		if(userSchool.compareToIgnoreCase(getResources().getString(R.string.no_users_in_db))==0){
			userSchool = "";
			return;
		}
		userlist.setEnabled(true);
		/*userlist.setEnabled(true);
			upsync.setEnabled(true);
			downsync.setEnabled(true);*/
		//dump.setEnabled(true);
		Log.d("debug","Item selected from School list");
		//School.setSelection(position);
		CustomArrayAdapter clva = (CustomArrayAdapter) School.getAdapter();
		clva.setSelectedIndex(position);

	}

	public void exportDB(){
		/*String path = Environment.getExternalStorageDirectory().getPath() + "/vkb_database";
			File fPath = new File(path);
			fPath.mkdirs();*/
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		prgUserData.setMessage(getResources().getText(R.string.please_wait_data_dump) );
		prgUserData.show();

		int myDeviceId = prefs.getInt("my_device_id", 0);
		// String currentDateTimeString = DateFormat.getDateTimeInstance().format("dd-MM-yyyyhh:mm:ssa");
		File sd =new File(Environment.getExternalStorageDirectory().getPath() + "/vkb_database");
		sd.mkdirs();
		File data = Environment.getDataDirectory();
		Log.d("dump1","file sd:"+sd);
		Log.d("dump1","file data:"+data);
		FileChannel source=null,sourceSess=null;
		FileChannel destination=null,destinationSess=null;

		String currentDBPath = "/data/game.Typing/databases/UT_Marathi";
		String currentDBSessPath = "/data/game.Typing/databases/"+ApplicationConstants.DATABASE_NAME;

		String backupDBPath = myDeviceId+"_UT_Users"+DateFormat.format("dd_MM_yyyy_hh_mm_ssa",new Date(System.currentTimeMillis())).toString();
		String backupDBSessPath = myDeviceId+"_UT_Sessions"+DateFormat.format("dd_MM_yyyy_hh_mm_ssa",new Date(System.currentTimeMillis())).toString();

		File currentDB = new File(data, currentDBPath);
		File currentSessDB = new File(data, currentDBSessPath);

		File backupDB = new File(sd, backupDBPath);
		File backupDBSess = new File(sd, backupDBSessPath);

		try {
			source = new FileInputStream(currentDB).getChannel();
			sourceSess = new FileInputStream(currentSessDB).getChannel();

			destination = new FileOutputStream(backupDB).getChannel();
			destinationSess = new FileOutputStream(backupDBSess).getChannel();

			destination.transferFrom(source, 0, source.size());
			destinationSess.transferFrom(sourceSess, 0, sourceSess.size());

			source.close();
			sourceSess.close();

			destination.close();
			destinationSess.close();

			Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
		} catch(IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "DB Exception!", Toast.LENGTH_LONG).show();
		}
		try{
			prgUserData.hide();

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void downloadAll(String userSchool){

		final SyncDatabase syncDBSess = new SyncDatabase(getApplicationContext());
		String url = syncDBSess.downSync();

		if(url.compareToIgnoreCase(getResources().getString(R.string.upsync_first))==0){
			Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
			return;
		}
		prgUserData.setMessage(getResources().getText(R.string.please_wait_downloading_user) );
		try{
		prgUserData.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}

		RequestQueue mRequestQueue;

		// Instantiate the cache
		Cache cache = new DiskBasedCache(this.getCacheDir(), 1024 * 1024*1024); // 1MB cap

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();


		StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// Do something with the response
				int func = 1;
				if(func==1){
					int res = syncDBSess.handleGetResponse(response,func);

					if(res == 1){

						//responseContainer.resContainerResult=res;
						Toast.makeText(getApplicationContext(), "All users' details downloaded", Toast.LENGTH_SHORT).show();
						//prgUserData.setMessage("All users' details downloaded");

					}else if(res == 2){

						//responseContainer.resContainerResult=res;
						Toast.makeText(getApplicationContext(), "Users details download Interrupted!!!", Toast.LENGTH_SHORT).show();
						//prgUserData.setMessage("Users details download Interrupted!!!");

					}else if (res == 0){

						//responseContainer.resContainerResult=res;
						Toast.makeText(getApplicationContext(), "User details already in sync!!!", Toast.LENGTH_SHORT).show();
						//prgUserData.setMessage("User details already in sync!!!");
					}
				}
				try{
					prgUserData.hide();

				}catch(Exception ex){
					ex.printStackTrace();
				}

				finish();
				Intent intentShow = new Intent("game.MarathiUT.SCHOOLSELECTION");
				intentShow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentShow);
			}
		},
		new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Error downsyncing!!!", Toast.LENGTH_SHORT).show();
				try{
					prgUserData.hide();

				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});


		// Add the request to the RequestQueue.
		mRequestQueue.add(stringRequest1);
		//final long downloadDuration = 7;


		/*	String[] userSet = DB.getUserIds(userSchool);
			RequestQueue mRequestQueue;

			// Instantiate the cache
			Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024* 1024 *1024); // 1MB cap - 1024*1024

			// Set up the network to use HttpURLConnection as the HTTP client.
			Network network = new BasicNetwork(new HurlStack());

			// Instantiate the RequestQueue with the cache and network.
			mRequestQueue = new RequestQueue(cache, network);

			// Start the queue
			mRequestQueue.start();

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String ip_add = prefs.getString("ip_string", "192.168.1.130");

			String url="http://"+ip_add+"/vkbPHP/getData.php";
			//String url ="http://www.myurl.com";
			Gson gson = new GsonBuilder().create();

			ArrayList<HashMap<String, String>> wordListSessDet = new ArrayList<HashMap<String, String>>();
			ArrayList<HashMap<String, String>> wordListSessTable = new ArrayList<HashMap<String, String>>();


			HashMap<String, String> map = new HashMap<String, String>();
			HashMap<String, String> map_st = new HashMap<String, String>();
			//map.put("userId", cursor.getString(0));


			for(int i=0;i<userSet.length;i++){
			// Formulate the request and handle the response.

				map.put("action", "get");
				map.put("act", "insert");
				map.put("tablename", "session_details_table");
				map.put("u_id", userSet[i]);
				wordListSessDet.add(map); 

				map_st.put("action", "get");
				map_st.put("act", "insert");
				map_st.put("tablename", "session_table");
				map_st.put("u_id", userSet[i]);
				wordListSessTable.add(map_st);   		



				//Use GSON to serialize Array List to JSON
				String jsonSessDetTable = gson.toJson(wordListSessDet);
				String jsonSessTable = gson.toJson(wordListSessTable);
				map.clear();
				map_st.clear();

				String callurl1= url+"?sendJSON="+ jsonSessTable;
				String callurl2= url+"?sendJSON="+ jsonSessDetTable;

				StringRequest stringRequest1 = new StringRequest(Request.Method.GET, callurl1,
			        new Response.Listener<String>() {
			    @Override
			    public void onResponse(String response) {
			        // Do something with the response
			    	//Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
			    	int func = 3;


			    	if(func==1){
						int res = syncDBSess.handleGetResponse(response,func);
						if(res == 1){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "All Users Downloaded", Toast.LENGTH_SHORT).show();

						}else if(res == 2){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Users Download Interupted!!!", Toast.LENGTH_SHORT).show();
						}else if (res == 0){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Already in sync!!!", Toast.LENGTH_SHORT).show();
						}
					}else if(func==2){
						int res = syncDBSess.handleGetResponse2(response,func);
						if(res == 1){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "All Session Details Downloaded", Toast.LENGTH_SHORT).show();

						}else if(res == 0){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Session Details Download Interupted!!!", Toast.LENGTH_SHORT).show();
						}else if (res == 2){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Session Details already in sync!!!", Toast.LENGTH_SHORT).show();
						}

					}else if(func==3){
						int res = syncDBSess.handleGetResponse3(response,func);
						if(res == 1){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "All Session Table Downloaded", Toast.LENGTH_SHORT).show();

						}else if(res == 0){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Session Table Download Interupted!!!", Toast.LENGTH_SHORT).show();
						}else if (res == 2){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Session Table already in sync!!!", Toast.LENGTH_SHORT).show();
						}
					}
			    }
			},
			    new Response.ErrorListener() {
			       	@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
			       		Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
					}
			});

			StringRequest stringRequest2 = new StringRequest(Request.Method.GET, callurl2,
			        new Response.Listener<String>() {
			    @Override
			    public void onResponse(String response) {
			        // Do something with the response
			    	//Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
			    	int func = 2;


			    	if(func==1){
						int res = syncDBSess.handleGetResponse(response,func);
						if(res == 1){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "All Users Downloaded", Toast.LENGTH_SHORT).show();

						}else if(res == 2){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Users Download Interupted!!!", Toast.LENGTH_SHORT).show();
						}else if (res == 0){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Already in sync!!!", Toast.LENGTH_SHORT).show();
						}
					}else if(func==2){
						int res = syncDBSess.handleGetResponse2(response,func);
						if(res == 1){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "All Session Details Downloaded", Toast.LENGTH_SHORT).show();

						}else if(res == 0){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Session Details Download Interupted!!!", Toast.LENGTH_SHORT).show();
						}else if (res == 2){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Session Details already in sync!!!", Toast.LENGTH_SHORT).show();
						}

					}else if(func==3){
						int res = syncDBSess.handleGetResponse3(response,func);
						if(res == 1){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "All Session Table Downloaded", Toast.LENGTH_SHORT).show();

						}else if(res == 0){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Session Table Download Interupted!!!", Toast.LENGTH_SHORT).show();
						}else if (res == 2){
							//responseContainer.resContainerResult=res;
							Toast.makeText(getApplicationContext(), "Session Table already in sync!!!", Toast.LENGTH_SHORT).show();
						}
					}
			    }
			},
			    new Response.ErrorListener() {
			       	@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
			       		Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
					}
			});

			// Add the request to the RequestQueue.
			mRequestQueue.add(stringRequest1);
			mRequestQueue.add(stringRequest2);
			}*/
	}






}
