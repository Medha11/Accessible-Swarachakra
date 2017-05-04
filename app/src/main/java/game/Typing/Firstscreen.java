package game.Typing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Firstscreen extends ActionBarActivity implements OnClickListener,OnItemClickListener {

	public String[] titles;
	public Integer[] images;
	public Integer[] next;
	Button login,dump;
	Button register;
	Button exit;

	Button upsync,downsync;

	Button startSession;
	Button sessionDetails;
	Spinner School;
	Spinner Name;
	String[][] userSet;
	/*public static final String[] titles = new String[] { "Strawberry",
		"Banana", "Orange", "Mixed" };

	public static final String[] descriptions = new String[] {
		"It is an aggregate accessory fruit",
		"It is the largest herbaceous flowering plant", "Citrus Fruit",
	"Mixed Fruits" };

	public static final Integer[] images = { R.drawable.straw,
		R.drawable.banana, R.drawable.orange, R.drawable.mixed };

	public static final Integer[] next = { R.drawable.ic_action_next_item,
		R.drawable.ic_action_next_item, R.drawable.ic_action_next_item, R.drawable.ic_action_next_item };*/
	String[] schools, users;
	String userSchool = "", userName ="";
	ProgressDialog prgDialog;
	ListView listView;
	List<RowItem> rowItems;

	/*	ListView usersList;
	ArrayAdapter<String> adaptor;*/

	Database DB;
	SessionDetailsTable sDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firstscreen);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		/*ActionBar actionBar = getActionBar();
		actionBar.hide();*/
		
		prgDialog = new ProgressDialog(this);
		prgDialog.setMessage(getString(R.string.please_wait_downloading_session_details));
		prgDialog.setCancelable(false);

		FileOperations.enableLogging(true); //later can be optional
		Log.d("logger","in onStart()");

		/*login = (Button) findViewById(R.id.editUser);
		register = (Button) findViewById(R.id.newUser);*/
		exit = (Button) findViewById(R.id.button3);
		/*upsync = (Button) findViewById(R.id.upsync);
		downsync = (Button) findViewById(R.id.downsync);
		dump = (Button) findViewById(R.id.dump);*/

		startSession = (Button) findViewById(R.id.startsession);
		sessionDetails = (Button) findViewById(R.id.showSession);

		//School = (Spinner) findViewById(R.id.school);
		//Name = (Spinner) findViewById(R.id.user);
		listView = (ListView)findViewById(R.id.user);

		/*usersList = (ListView) findViewById(R.id.userlist);*/

		DB = new Database(this);
		sDB = new SessionDetailsTable(this);


		Intent it = getIntent();
		Bundle b= it.getExtras();
		userSchool = b.getString("schoolname");
		setTitle("School: "+userSchool);
		
		//schools = DB.getSchoolName();

		//DB.close();
		//if(schools.length>=1){
		//		if(schools != null){

		//Log.d("logger", "schools count:"+schools.length);

		/*ArrayAdapter<String> schoolAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, schools);
		School.setAdapter(schoolAdapter);*/

		//userSchool = School.getSelectedItem().toString();
		//DB.open();
		userSet = DB.getUsers(userSchool);
		System.out.println("userSet"+userSet.length+userSet.toString());
		String userName[] = new String[userSet.length];
		images = new Integer[userSet.length];
		next = new Integer[userSet.length];
		
		for(int i=0;i<userSet.length;i++){
			String u = "";
			/*for(int j=0;j<7;j++){

				u += userSet[i][j];
				u +="\n";
				//users[i] = u.toString();
				//Log.d("TAG","here it is " + u + users[i]);
			}*/
			u = userSet[i][1].toString();
			Log.d("TAG","here it is " + u);
			userName[i]= u;	
			
			try{
				images[i] = Integer.parseInt(userSet[i][9]);
			}catch(IndexOutOfBoundsException iob){
				//images[i] = R.drawable.girl;
			}catch(NumberFormatException nfe){
				//images[i] = R.drawable.girl;
			}
			next[i] = R.drawable.ic_action_next_item;
		}

		/*ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(Firstscreen.this,
				android.R.layout.simple_spinner_dropdown_item, userName);
		Name.setAdapter(nameAdapter);	*/
		/*rowItems = new ArrayList<RowItem>();
		for (int i = 0; i < userName.length; i++) {
			RowItem item = new RowItem(images[i], userName[i], next[i]);
			rowItems.add(item);

		}*/
		/*CustomListViewAdapter adapter = new CustomListViewAdapter(this,
				R.layout.list_item, rowItems);
		listView.setAdapter(adapter);*/
		CustomArrayAdapter adapter = new CustomArrayAdapter(this,userName); 
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);		

		/*School.setOnItemSelectedListener(new OnItemSelectedListener(){

			@SuppressWarnings("null")
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				userSchool = School.getSelectedItem().toString();
				//DB.open();
				userSet = DB.getUsers(userSchool);
				String userName[] = new String[userSet.length];
				//DB.close();
				Log.d("TAG","here it is " + userSet.length + "----len");
				for(int i=0;i<userSet.length;i++){
					String u = "";
					u = userSet[i][1].toString();
					Log.d("TAG","here it is " + u);
					userName[i]= u;
				}

				ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(Firstscreen.this,
						android.R.layout.simple_spinner_dropdown_item, userName);
				Name.setAdapter(nameAdapter);	
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});*/
		startSession.setEnabled(true);
		sessionDetails.setEnabled(true);
		//}
		startSession.setOnClickListener(this);
		sessionDetails.setOnClickListener(this);

		//login.setOnClickListener(this);
		//register.setOnClickListener(this);
		exit.setOnClickListener(this);
		//upsync.setOnClickListener(this);
		//downsync.setOnClickListener(this);
		//dump.setOnClickListener(this);
		/*if(sDB.isPhraseTablePopulated() == false)
			sDB.populatePhrasesTable();	
		else
			Log.d("xml","phrase table exists");
		int c = sDB.getTotalSessionCount();
*/
	}

	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub

		final Bundle bundle = new Bundle();
		CustomArrayAdapter clva = (CustomArrayAdapter) listView.getAdapter();
		

		switch (v.getId()) {
		case R.id.editUser:
			//Intent intentLogin = new Intent("game.MarathiUT.LOGIN");
			//startActivity(intentLogin);
			break;
		/*case R.id.newUser:
			Intent intentReg = new Intent("game.MarathiUT.REGISTER");
			startActivity(intentReg);
			break;*/
		case R.id.button3:
			Intent intentHome = new Intent("game.MarathiUT.SCHOOLSELECTION");
			bundle.putString("school",userSchool);
			intentHome.putExtras(bundle);
			startActivity(intentHome);
			break;
		case R.id.showSession:
			
			int user = clva.getSelectedIndex(); //Name.getSelectedItemPosition();
			
			if(user<0)
			{	
				String toastMessge = "Select a participant from the list";
				Toast.makeText(getApplicationContext(), toastMessge, Toast.LENGTH_LONG).show();
				return;
			}
			
			Log.d("debug", "user set ength:" + userSet.length + "selected pos:"+user);
			Log.d("logger","Show sessions details of: "+ userSet[user][0]);

			Intent intentShow = new Intent("game.MarathiUT.sessiondetails");
			bundle.putInt("userid", Integer.parseInt(userSet[user][0]));

			intentShow.putExtras(bundle);
			startActivity(intentShow);
			break;

		case R.id.startsession:
			
			int userselected = clva.getSelectedIndex(); //Name.getSelectedItemPosition();
			if(userselected<0)
			{	
				String toastMessge = "Select a participant from the list";
				Toast.makeText(getApplicationContext(), toastMessge, Toast.LENGTH_LONG).show();
				return;
			}
			/*Intent intent = new Intent("game.MarathiUT.SESSION");
			Log.d("logger","Show session screen for:"+userSet[userselected][0]);
			bundle.putInt("userid", Integer.parseInt(userSet[userselected][0]));
			bundle.putString("kbname", userSet[userselected][7]);
			bundle.putString("imename", userSet[userselected][8]);*/
			final String kbname = userSet[userselected][7];
			final String imename = userSet[userselected][8];
			//intent.putExtras(bundle);
			//startActivity(intent);
			final int userid = Integer.parseInt(userSet[userselected][0]);
			SyncDatabase syncDBSess = new SyncDatabase(getApplicationContext());
			if(syncDBSess.checkUnsychedSessions(userid)){
				Intent intentSessStart = new Intent("game.MarathiUT.SESSION");
				bundle.putInt("userid",userid);
				bundle.putString("kbname", kbname);
				bundle.putString("imename", imename);
				intentSessStart.putExtras(bundle);
				startActivity(intentSessStart);
				
			}else{
				
				String toastMessge = syncDBSess.loadSessions(String.valueOf(userSet[userselected][0]),prgDialog,kbname,imename);
				Toast.makeText(getApplicationContext(), toastMessge, Toast.LENGTH_LONG).show();
			}
			//int dur = Toast.makeText(getApplicationContext(), toastMessge, Toast.LENGTH_LONG).getDuration();

			/*final ScheduledExecutorService worker = 
					Executors.newSingleThreadScheduledExecutor();


			Runnable task = new Runnable() {
				public void run() {
					 Do something
					startSession.setEnabled(true);
					Intent intentShow = new Intent("game.MarathiUT.SESSION");
					bundle.putInt("userid",userid);
					bundle.putString("kbname", kbname);
					bundle.putString("imename", imename);
					intentShow.putExtras(bundle);
					startActivity(intentShow);
				}
			};
			worker.schedule(task, 10, TimeUnit.SECONDS);*/

			// startSession.setEnabled(false);
			break;
		/*case R.id.upsync:
			SyncDatabase syncDB = new SyncDatabase(getApplicationContext());
			String status = syncDB.onUpSync(1);
			Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();

			final ScheduledExecutorService work = 
					Executors.newSingleThreadScheduledExecutor();


			Runnable run = new Runnable() {
				public void run() {
					 Do something
					Intent intentShow = new Intent("game.MarathiUT.FIRSTSCREEN");							
					startActivity(intentShow);
				}
			};
			work.schedule(run, 5, TimeUnit.SECONDS);
			break;

		case R.id.downsync:
			SyncDatabase syncDB1 = new SyncDatabase(getApplicationContext());
			String status1 = syncDB1.downSync();
			Toast.makeText(getApplicationContext(), status1, Toast.LENGTH_LONG).show();
			final ScheduledExecutorService worker1 = 
					Executors.newSingleThreadScheduledExecutor();


			Runnable task1 = new Runnable() {
				public void run() {
					 Do something
					Intent intentShow = new Intent("game.MarathiUT.FIRSTSCREEN");							
					startActivity(intentShow);
				}
			};
			worker1.schedule(task1, 5, TimeUnit.SECONDS);
			break;*/
		case R.id.dump:
			//Toast.makeText(this, "DB Exported!", Toast.LENGTH_SHORT).show();
			exportDB();
			break;
		}
	}


	public void exportDB(){
		/*String path = Environment.getExternalStorageDirectory().getPath() + "/vkb_database";
		File fPath = new File(path);
		fPath.mkdirs();*/
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		userName =(String) listView.getItemAtPosition(position);
		//userlist.setEnabled(true);
		//upsync.setEnabled(true);
		//downsync.setEnabled(true);
		//dump.setEnabled(true);
		Log.d("debug","Item selected from participant list");
		//School.setSelection(position);
		CustomArrayAdapter clva = (CustomArrayAdapter) listView.getAdapter();
		clva.setSelectedIndex(position);
		
	}

}


