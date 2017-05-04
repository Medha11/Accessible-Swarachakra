package game.Typing;

import game.Typing.Parsers.xmlParsers;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import game.Typing.Parsers.xmlParsers;

public class Register extends Activity implements OnClickListener {

	Button register;
	EditText Class, Division;
	EditText Name;
	EditText Age;
	//EditText Keyboard;
	Spinner Keyboard;
	RatingBar Rating;
	Spinner School;
	int UserId;
	int UserPhoto;
	HashMap<String, String> KBlist ;
	HashMap<String, String> Schoollist;
	ProgressDialog prgUserData;

	/*String[] schools = {"बालमोहन विद्यामंदिर" , "शारदाश्रम विद्यालय", "साने गुरुजी विद्यालय", "सरस्वती विद्यालय", "साधना विद्यालय" }, classes = { "4", "5", "6", "7" }, divisions = { "A", "B",
			"C", "D", "E", "F", "G", "H", "I" }, keyboards = { "CDAC GIST Marathi Keyboard",
			"MultiLing Keyboard", "Samsung keyboard" , "Swarachakra Marathi", "Swype"};*/
	String[] schools, classes, divisions, keyboards;
	// String schools;

	Database DB;
	SessionDetailsTable sDB;
	//SessionTable stDB;

	// saving the fields in the database??

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		register = (Button) findViewById(R.id.button1);
		Class = (EditText) findViewById(R.id.std);
		Division = (EditText) findViewById(R.id.division);
		Name = (EditText) findViewById(R.id.editText1);
		Age = (EditText) findViewById(R.id.editText2);
		School = (Spinner) findViewById(R.id.SchoolName);
		Keyboard = (Spinner) findViewById(R.id.keyboard);
		Rating = (RatingBar) findViewById(R.id.ratingBar);
		
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		/*ActionBar actionBar = getActionBar();
		actionBar.hide();*/

		UserId = 0;

		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(Register.this,
				android.R.layout.simple_spinner_dropdown_item, classes);
		Class.setAdapter(adapter);*/

		/*ArrayAdapter<String> divisionadapter = new ArrayAdapter<String>(
				Register.this, android.R.layout.simple_spinner_dropdown_item,
				divisions);
		Division.setAdapter(divisionadapter);*/
		prgUserData = new ProgressDialog(this);
 	    prgUserData.setMessage(getString(R.string.please_wait_uploading));
		prgUserData.setCancelable(false);
		
		//get keyboard list from xml
		xmlParsers xpp = new xmlParsers();
		KBlist =xpp.getKeyboardNamesFromXML(this);
		Schoollist =xpp.getSchoolNamesFromXML(this);
		DB = new Database(this);
		sDB = new SessionDetailsTable(this);

		try{
			keyboards = new String[KBlist.size()];
			schools = new String[Schoollist.size()];
			keyboards = KBlist.keySet().toArray(keyboards);
			
			schools = Schoollist.keySet().toArray(schools);

			ArrayAdapter<String> schoolAdapter = new ArrayAdapter<String>(
					Register.this, android.R.layout.simple_spinner_dropdown_item,
					schools);
			School.setAdapter(schoolAdapter);

			//if(DB ==null)
			//DB = new Database(this);
			//if(sDB ==null)
			//sDB = new SessionDetailsTable(this);
			//stDB = new SessionTable(this);

			//DB.open();
			//schools = DB.getSchoolName();
			//DB.close();

			/*ArrayAdapter<String> schoolAdapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_dropdown_item, schools);
				School.setAdapter(schoolAdapter);*/

			ArrayAdapter<String> keyadapter = new ArrayAdapter<String>(
					Register.this, android.R.layout.simple_spinner_dropdown_item,
					keyboards);
			Keyboard.setAdapter(keyadapter);
			int randomIndex = randomizer.randInt(0, keyboards.length-1);
			Keyboard.setSelection(randomIndex);
			//Keyboard.setText(keyboards[randomizer.randInt(0, keyboards.length-1)]);

		}catch(Exception ex){
			ex.printStackTrace();
			FileOperations.write("Exception:"+ex.getMessage());
		}

		register.setOnClickListener(this);

		Name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length()>=2)
					Name.setError(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		Age.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length()>=1)
					Age.setError(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		
		//UserPhoto = R.drawable.girl;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		boolean isEmpty=false;  //set if any of the text fields are empty
		
		switch (v.getId()) {
		case R.id.button1:

			String userName = Name.getText().toString();
			String userAge = Age.getText().toString();
			String userClass = Class.getText().toString();
			//int userClass = Integer.parseInt(Class.getText().toString());
			//String userDiv = Division.getSelectedItem().toString();
			String userDiv = Division.getText().toString();
			//String userSchool = School.getText().toString();
			String userSchool = School.getSelectedItem().toString();
			String userKeyboard = Keyboard.getSelectedItem().toString();
			String userIMEName = KBlist.get(userKeyboard);

			int rating = (int)Rating.getRating();
			String userRating = String.valueOf(rating);

			if( !userClass.isEmpty() && userName.length() != 0 ){

				if( Integer.parseInt(userClass) > 7 || Integer.parseInt(userClass) < 4){

					Toast.makeText(this, "Please confirm that the standard of the participant is correct.", Toast.LENGTH_SHORT).show();
				}
			}
			if (userName.length() == 0 || userName.isEmpty()) {

				Name.setError("Mandatory field");
				isEmpty = true;
				//return;

			}if (userAge.length() == 0 || userAge.isEmpty()) {
				Age.setError("Mandatory field");
				isEmpty = true;
				//return;
			}if (userDiv.length() == 0 || userDiv.isEmpty()) {
				Division.setError("Mandatory field");
				isEmpty = true;
				//return;
			}if(rating == 0){
				Toast.makeText(this, "Please set the standard task rating.", Toast.LENGTH_LONG).show();
				isEmpty = true;
			}
			
			if(isEmpty ==true)
				return;
			
			//else {
			v.setEnabled(false);
				String[] nameArray = new String[2];
				String[] tmpArray = userName.split(" ");

				if(tmpArray.length < 2)
				{
					nameArray[0] = userName.trim();
					nameArray[1] = "";
				}else
				{
					nameArray[0] = tmpArray[0];
					nameArray[1] = tmpArray[1];
				}

				//nameArray[0], nameArray[1]
				//DB.open();

				//v.setEnabled(false); //prevent re-registering while the allocation table populates.

				UserId = DB.insertUser(userName.trim(),"", userSchool, String.valueOf(userClass), userDiv, userAge, userKeyboard,userIMEName, userRating,R.drawable.ic_launcher);
				
				//DB.close();

				/*if( UserId != -1){
					//Log.d("logger" , "User added successfully:" + userName.trim() + ""+ userSchool + userClass + userDiv + userAge + userKeyboard + userIMEName + userRating);
				}
				else{
					//Log.d("logger" , "Could not add user:" + userName.trim() + ""+ userSchool + userClass + userDiv + userAge + userKeyboard + userIMEName + userRating);
					return;
				}*/

				//UserId = userid;
				/*Dialog d = new Dialog(this);
						TextView tv = new TextView(this);
						tv.setText(userName);
						d.setTitle("Registration Successfull.");
						d.setContentView(tv);
						d.show();*/

				Handler h = new Handler();
				Runnable r = new Runnable() {


					@Override
					public void run() {
						// TODO Auto-generated method stub

						/*String userName = Name.getText().toString();
						//String[] nameArray = userName.split(" ");

						String userAge = Age.getText().toString();
						String userClass = Class.getSelectedItem().toString();
						String userDiv = Division.getSelectedItem().toString();
						//String userSchool = School.getText().toString();
						String userSchool = School.getSelectedItem().toString();
						String userKeyboard = Keyboard.getText().toString();//Keyboard.getSelectedItem().toString();
						 */						
						/*String[] nameArray2 = new String[2];
						String[] tmpArray = userName.split(" ");

						if(tmpArray.length < 2)
						{
							nameArray2[0] = userName.trim();
							nameArray2[1] = "";
						}else
						{
							nameArray2[0] = tmpArray[0];
							nameArray2[1] = tmpArray[1];
						}*/

						//DB.open();
						String userKeyboard = Keyboard.getSelectedItem().toString();
						String userKBName = KBlist.get(userKeyboard);

						//DB.getDetails();
						//String user = DB.getId( userSchool, userName, "", userAge, userClass, userDiv, userKeyboard);
						Log.d("logger" , "ID of new user: "+ UserId);
						//DB.close();

						randomizer r = new randomizer(getApplicationContext());				

						//create FTU session entries for the user
						String tmp;
						String[] tmpArray;
						
						ArrayList<String> list= randomizer.generateFTUSequence(UserId);
						if(list.isEmpty())
							FileOperations.write("Populating FTU entries in "+ SessionDetailsTable.SESSION_TABLE+ " table and " + SessionDetailsTable.TABLE_SESSION_PHRASE_MAP+ " table failed. Randomizer list empty.");
						else{
							for(int i=0;i<list.size() ; i++){
								tmp=list.get(i);
	
								tmpArray=tmp.split(";");
	
								long status = sDB.addSessionDetails(UserId, Integer.parseInt(tmpArray[1].trim()), Integer.parseInt(tmpArray[2].trim()), Integer.parseInt(tmpArray[3].trim()), Integer.parseInt(tmpArray[4].trim()));
								if(status ==-1){
									Log.d("logger","Could not add " + list.get(0) + " to sessions table");
									FileOperations.write("FTU allocation. Could not add FTU entry for user:"+UserId +" with phraseNo:"+Integer.parseInt(tmpArray[2].trim()));
								}
								else{
									//Log.d("logger","Insert successful");
								}
							}
						}

						//Allocate phrases randomly to users in accordance with protocol
						//sessionID = stDB.addSessionEntry((int)UserId, SessionTable.SESSION_ID);

						/*if(sessionID == -1)
							return;

						 */
						if(list.isEmpty() == false)
							list.clear();
						
						tmpArray=null;
						list= randomizer.generatePhraseSequence(UserId);					
						
						if(list.isEmpty())
							FileOperations.write("Populating LTU entries in "+ SessionDetailsTable.SESSION_TABLE+ " table and " + SessionDetailsTable.TABLE_SESSION_PHRASE_MAP+ " table failed. Randomizer list empty.");
						else{
							for(int i=0;i<list.size() ; i++){
								tmp=list.get(i);
	
								tmpArray=tmp.split(";");
	
								long status = sDB.addSessionDetails(UserId, Integer.parseInt(tmpArray[1].trim()), Integer.parseInt(tmpArray[2].trim()), Integer.parseInt(tmpArray[3].trim()) , Integer.parseInt(tmpArray[4].trim()));
								if(status ==-1){
									Log.d("logger","Insert failed " + tmp);
									FileOperations.write("LTU allocation. Could not add LTU entry for user:"+UserId +" with phraseNo:"+Integer.parseInt(tmpArray[2].trim()));
								}
								else{
									//Log.d("logger","Insert successful:" + tmp);
								}
							}
						}
						
						//see if the alloc table done
						//String[][] res = sDB.getDetails(UserId, 1);
						
						//if(res.length>0){
						SyncDatabase syncDB = new SyncDatabase(getApplicationContext());
						syncDB.onUpSync(0,prgUserData);
						//}
						
						Intent intent = new Intent("game.MarathiUT.SESSION");
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Bundle bd = new Bundle();
						bd.putInt("userid", UserId );
						bd.putString("kbname", userKeyboard);
						bd.putString("imename", userKBName);

						//Toast.makeText(getApplicationContext(), "Userid: " + UserId + ". being passed to session.", Toast.LENGTH_LONG).show();
						intent.putExtras(bd);
						register.setEnabled(true);
						startActivity(intent);
					}
				};
				h.postDelayed(r, 750); //verify:


			//}//else
		}		
	}
}