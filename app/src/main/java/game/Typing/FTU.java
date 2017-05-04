package game.Typing;

import game.Typing.validation.AlternativesParser;
import game.Typing.validation.Validator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;


public class FTU extends FragmentActivity implements OnClickListener{
	boolean leftflag = false,rightflag=false;
	String[][] word_phrase;
	String value;
	TextView training,secondAttempt,word_typed;
	EditText typing;
	Button nexttext;
	String typed;
	int session_type,userId;
	int j,attemptNo; //i, 
	int len,continueFrom;
	Bundle b;
	Database DB;
	SessionDetailsTable sDB;
	AlternativesParser alt ;
	ProgressDialog prgUserData;
	Vibrator myVib;
	TextToSpeech tts3;
	//varaiables used for edit distance
	DamerauLevenshteinAlgorithm editDistance = new DamerauLevenshteinAlgorithm(1,1,1,1);
	File dir, file;
	String filePath;
	/*	int correctCount;
	int withHelpCount;*/
	long firstchartime,currentTime,session_start_time,session_end_time;
	int Arraylenght = 0;
	Double  cpm;
	//static float cpm1;
	static int array1;
	int sessionId;
	long total;
	String ctyped;
	double totalcpm = 0;
	//int totalEDistance = 0;

	String kbname,imename, log, oldText;

	SpeedCalculator speed;
	RelativeLayout rl;
	
	Rect displayRectangle;
	public static CustomKeyboard mCustomKeyboard;
	int f[]=new int[2];

	/**
	 * Touch Event Used for detecting
	 * 1.Top left corner touch
	 * 2.Top right corner touch
	 * */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("TouchEvent", "true");
		int maskedAction = event.getActionMasked();
		float x1,y1;
		x1 = event.getX();
		y1 = event.getY();
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		Log.d("dimensions",height+","+width);

		switch(maskedAction){
			case MotionEvent.ACTION_DOWN:
				if(y1<height/	6){
					//top left corner
					if(x1<width/3){
//						getActionBar().hide();
						if(f[0]==0){

							String text = training.getText().toString();
							speakOut_training(text);
							f[0]=1;
							f[1]=0;
							myVib.vibrate(50);
						}

					}
					//top right corner
					else if(x1 >2*width/3 && x1 <width){
						if(f[1]==0){
							String text = typing.getText().toString();
							speakOut_training(text);
							f[1]=1;
							f[0]=0;
							myVib.vibrate(50);
						}

					}

				}
				break;
			case MotionEvent.ACTION_MOVE:
				if(y1<height/6){
					//top left corner
					if(x1<width/3){
//						getActionBar().hide();
						if(f[0]==0){
							String text = training.getText().toString();
							speakOut_training(text);
							f[0]=1;
							f[1]=0;
							myVib.vibrate(50);
						}

					}
					//top right corner
					else if(x1 >2*width/3 && x1 <width){
						if(f[1]==0){
							String text = typing.getText().toString();
							speakOut_training(text);
							f[1]=1;
							f[0]=0;
							myVib.vibrate(50);
						}

					}

				}

				break;
			case MotionEvent.ACTION_UP:
				f[0]=0;
				f[1]=0;
				break;
		}
		return false;
	}
	//Text to Speeches intialisation
	TextToSpeech.OnInitListener onInit = new TextToSpeech.OnInitListener() {
		@Override
		public void onInit(int status) {
			if (status == TextToSpeech.SUCCESS) {
				final Locale loc = new Locale("hin", "IND");
				int result = tts3.setLanguage(loc);
				if (result == TextToSpeech.LANG_MISSING_DATA
						|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
					Log.e("TTS", "This Language is not supported");
				} else {

//                    speakOut(keyCodelabel);
				}

			} else {
				Log.e("TTS", "Initilization Failed!");
			}
		}
	};
	public void speakOut_training(String keyCodelabel) {
//        if (!isAccessibilitySettingsOn(mHostActivity.getApplicationContext())) {
		tts3.setPitch(1);
		tts3.speak(keyCodelabel, TextToSpeech.QUEUE_FLUSH, null);
//        }
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tts3 = new TextToSpeech(this, onInit);
		setContentView(R.layout.activity_ftu);
		myVib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		//Log.d("logger","##onCreate()");
		mCustomKeyboard= new CustomKeyboard(this, null,R.id.keyboardview, R.xml.hexkbd);
		mCustomKeyboard.registerEditText(R.id.editText1);
		this.addContentView(mCustomKeyboard, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));


		FileOperations.write("##onCreate()  in FTU");
	}

	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		//Log.d("logger","##onBackPressed()");
		mCustomKeyboard.hideCustomKeyboard();
		nexttext.setEnabled(true);
		FileOperations.write("##onBackPressed()  in FTU");
	}	

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		//Log.d("logger","##onRestart()");
		FileOperations.write("##onRestart() in FTU");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//Log.d("logger","##onResume()");
		FileOperations.write("##onResume() in FTU");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//Log.d("logger","##onPause()");
		FileOperations.write("##onPause() in FTU");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//Log.d("logger","##onStop()");
		FileOperations.write("##onStop() in FTU");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//Log.d("logger","##onDestroy()");
		FileOperations.write("##onDestroy() in FTU");
	}

	/*
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		Log.d("logger","##hasfocus()");
	}*/
	
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//Log.d("logger","##onStart()");
		FileOperations.write("##onStart() in FTU");
		
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
		
		this.setTitle(R.string.write_this_word);
		speed = new SpeedCalculator();
		oldText = "";
		log ="";
		session_start_time =0;
		session_end_time = 0;
		firstchartime = 0;
		currentTime = 0;
		nexttext = (Button) findViewById(R.id.nextText);
		training = (TextView) findViewById(R.id.textView1);
		typing = (EditText) findViewById(R.id.editText1);
		word_typed =(TextView) findViewById(R.id.textView4);
		word_typed.setTextColor(Color.DKGRAY);
		typing.setText("");
		rl = (RelativeLayout) findViewById(R.id.ftu);
		secondAttempt = (TextView) findViewById(R.id.secondAttempt);

		DB = new Database(this);
		sDB = new SessionDetailsTable(this);
		alt = new AlternativesParser(this);
		
		displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

		Intent it = getIntent();
		Bundle b= it.getExtras();
		userId = b.getInt("userid");
		session_type = b.getInt("sessiontype");
		kbname = b.getString("kbname");
		imename = b.getString("imename");
		//continueFrom = b.getInt("continueFrom");
		continueFrom = sDB.getLastSuccessfulPhrase(userId, session_type);

		totalcpm = 0;

		typing.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				word_typed.setText(typing.getText().toString());
				if(session_start_time == 0){
					session_start_time = System.currentTimeMillis();
					session_end_time = System.currentTimeMillis();
					sDB.updateSessionEntry(userId, session_type, 0, session_start_time, session_end_time, SessionDetailsTable.SESSION_STATUS_STARTED);
					FileOperations.write("SessionTable updated: uid-"+ userId + " , session type-" + session_type+ " , session rating" + 0+ " , session start time-" + session_start_time+ " , session end time-" + session_end_time+ " , session status -" + SessionDetailsTable.SESSION_STATUS_STARTED);
				}

				if(firstchartime==0){		

					//if we are about to begin typing a new word and 
					//if default KB is not the one assigned to the user don't let the session start
					//timings will not be affected
//
//					String currentDefaultKB = Settings.Secure.getString(
//							getContentResolver(),
//							Settings.Secure.DEFAULT_INPUT_METHOD
//							);
//
//					if(currentDefaultKB.equals(imename)==false)
//					{
//						Toast.makeText(getApplicationContext(), "Default keyboard should be "+kbname, Toast.LENGTH_LONG).show();
//						InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
//						imm.showInputMethodPicker();
//						return;
//					}
					//this will remain constant;
					firstchartime = System.currentTimeMillis();
					speed.setFirstCharTime(firstchartime);
					speed.setAvgWordLength(ApplicationConstants.MARATHI_AVG_WORD_LEN);

				}

				if(oldText.compareToIgnoreCase(String.valueOf(s))!=0){
					//Log.d("detailedLogger", String.valueOf(s) + "starting from:" + start + ", prev length:" + before + ", replaced chars: "+count);


					//check what's new
					int oldLen=oldText.length();
					int newLen = s.length();

					if(before == 0){ //something added
						int diff =newLen - oldLen;
						Log.d("detailedLogger","Added "+ diff +" characters:" +String.valueOf(s.subSequence(start, start+diff)) + " at "+start + " after " + String.valueOf(s.subSequence(0, start)));
						log += System.currentTimeMillis() + ApplicationConstants.logFieldSeparator + ApplicationConstants.ADD + ApplicationConstants.logFieldSeparator + start + ApplicationConstants.logFieldSeparator +String.valueOf(s.subSequence(start, start+diff)) +ApplicationConstants.logRowSeparator;

					}else{
						//int diff =oldLen - newLen;

						//Log.d("detailedLogger","Removed "+ before +" # characters:" +String.valueOf(oldText.subSequence(start, start+before)) + " and added " + String.valueOf(s).substring(start));

						if(String.valueOf(s.subSequence(start, start + count)).length()>0){
							Log.d("detailedLogger","Removed "+ before +" # characters:" +String.valueOf(oldText.subSequence(start, start+before)) + " and added " + s.subSequence(start, start + count));
							log += System.currentTimeMillis() + ApplicationConstants.logFieldSeparator + ApplicationConstants.REPLACE + ApplicationConstants.logFieldSeparator + start + ApplicationConstants.logFieldSeparator +s.subSequence(start, start + count) +ApplicationConstants.logRowSeparator;
						}
						else{
							Log.d("detailedLogger","Removed "+ before +" # characters:" +String.valueOf(oldText.subSequence(start, start+before)));
							log += System.currentTimeMillis() + ApplicationConstants.logFieldSeparator + ApplicationConstants.DELETED + ApplicationConstants.logFieldSeparator + start + ApplicationConstants.logFieldSeparator +s.subSequence(start, start + count) + ApplicationConstants.logRowSeparator;
						}

					}

					oldText = String.valueOf(s);
				}

				//this is the current character time
				currentTime = System.currentTimeMillis();
				//float diff =(float) (currentTime - firstchartime );
				//Log.d("logger","firstcharT: "+ firstchartime + ", last chartime:" + currentTime + ", diff: "+diff);

				if(s.length()>=2){
					typing.setError(null);
//					nexttext.setEnabled(true);

				}else
					nexttext.setEnabled(false);
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

		nexttext.setOnClickListener(this);

		//if (session_type.equals("-1")) {
		//i = continueFrom;
		Log.d("kbz","continue from: "+continueFrom);
		j = 0;
		attemptNo=0;
		/*		correctCount = 0;
		withHelpCount = 0;*/
		//Resources resources = this.getResources();

		//word_phrase = resources.getStringArray(R.array.FTU_words);

		word_phrase = sDB.getFTUPhrases();
		if(word_phrase ==null){

			FileOperations.write("Illegal state:Phrase list for FTU empty.");			
			finish();
			return;

		}else if(continueFrom > word_phrase.length){

			continueFrom = 1;
			//sessionId = 0;
			FileOperations.write("Illegal state:continueFrom exceeds number of phrases.");
			sDB.updateSessionEntry(userId, session_type, 0, 0, 0, SessionDetailsTable.SESSION_STATUS_DONE);
			FileOperations.write("SessionTable updated: uid-"+ userId + " , session type-" + session_type+ " , session rating" + 0+ " , session start time-" + 0+ " , session end time-" + 0+ " , session status -" + SessionDetailsTable.SESSION_STATUS_DONE);

			//if(sDB.ftuDone(userId) == 1){
			finish();
			return;
			//}
			//finish();			

		}

		//i = continueFrom - 1;		
		len = word_phrase.length;
		training.setText(word_phrase[continueFrom -1][0].trim());
		typed = typing.getText().toString().trim();

		//get sessionId
		sessionId =  (int) sDB.addSessionEntry(userId, session_type );	

		sDB.updateSessionEntry(userId, session_type, 0, 0, 0, SessionDetailsTable.SESSION_STATUS_STARTED);
		//onComplete();
		FileOperations.write("SessionTable updated: uid-"+ userId + " , session type-" + session_type+ " , session rating" + 0+ " , session start time-" + 0+ " , session end time-" + 0+ " , session status -" + SessionDetailsTable.SESSION_STATUS_STARTED);

		int rating =DB.getRating(userId);

		if(rating == -1)
		{
			Toast.makeText(this, "Strange: std task rating not found for userid:"+userId, Toast.LENGTH_LONG).show();
			FileOperations.write("Illegal state: Std rating not found for user "+userId);
			//rating = 0;
		}else{
			Toast.makeText(this, "Std task rating for userid:" + userId + " is "+rating, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//1.

		//4.
		/*if(typing.getText().length() == 0){
			typing.setError(getResources().getString(R.string.write_something));
			return;
		}*/
		int index = continueFrom -1;
		//nexttext.setEnabled(false);
		
		session_end_time = System.currentTimeMillis();
		if (firstchartime ==0) {
			Toast.makeText(this, "Warning:First character typed timestamp is 0", Toast.LENGTH_LONG).show();
			FileOperations.write("Warning:First character typed timestamp is 0");
		}
		String typedText = typing.getText().toString().trim();
		String presentedText = word_phrase[index][0].trim();

		/*if(typedText.isEmpty() || typedText.length() ==0){
			nexttext.setEnabled(false);
			return;
		}*/		

		attemptNo++;

		//String typedText = typing.getText().toString().trim();
		//String presentedText = training.getText().toString();

		boolean altMatch = false;

		/*try{
			if(word_phrase[index][1] == null || word_phrase[index][1].length() ==0){

				Log.d("logger", "Alt val "+word_phrase[index][1]);

			}else{

				String[] tmp = word_phrase[index][1].split(",");

				for(int i=0 ; i<tmp.length ; i++){
					
					Log.d("logger", "Alt chk:"+tmp[i].trim());
					
					if(tmp[i].trim().equalsIgnoreCase(typedText)== true){
						
						altMatch = true;
						Log.d("logger", "Alt match found");
						break;
						
					}else
						Log.d("logger", "Nope not a match");

				}
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}*/

		if (!(alt.replaceAlternatives(alt.replaceAlternatives(presentedText.trim()))).equals(alt.replaceAlternatives(alt.replaceAlternatives(typedText))) ) //Did not match the phrase or the alternative versions
		{

			if(attemptNo<2){

				int phraseno = (index+1);
				//float cpm = typing.getText().toString().length() / ( (float)(currentTime-firstchartime) /(float)60000); 


				//repeated++;  //moved up

				//String presentedText = training.getText().toString();
				//String typedText = typing.getText().toString().trim();

				speed.setLastCharTime(currentTime);
				speed.setTextLength(typedText.length());
				speed.setWordCount(typedText.split(" ").length);

				//double cpm= speed.getCPM();
				//float timetaken=speed.getTimeTaken();

				int editdis = editDistance.execute(alt.replaceAlternatives(alt.replaceAlternatives(presentedText)), alt.replaceAlternatives(alt.replaceAlternatives(typedText)));
				//Toast.makeText(getApplicationContext(), "Edist: "+editdis, Toast.LENGTH_LONG).show();
				//int timetaken=(int) (currentTime-firstchartime) /1000;
				//Toast.makeText(getApplicationContext(), "(TLen,time)(" +typing.getText().toString().length()+ ","+timetaken+", cpm: "+cpm +", Edist: "+editdis, Toast.LENGTH_LONG).show();
				//this.setTitle(typing.getText().toString().length()+ " chars in "+timetaken+" sec; cpm: "+String.format("%.2f", cpm) +"; Edist: "+editdis);

				//this.setTitle(String.format("%.0f", cpm) +" | "+editdis+" | "+ Validator.getEDToStars( editdis, word_phrase[i][0].length()));
				//latest:this.setTitle(String.format("%.0f", cpm) +" | "+editdis+" | "+ Validator.getEDToStars( editdis,presentedText.length()) +" | "+ typedText.length() + " | "+ speed.getTimeTaken());

				speakOut_training(typing.getText()+"  गलत हैं ! पुनः प्रयास करें");
				secondAttempt.setTextColor(getResources().getColor(R.color.red));
				secondAttempt.setText(typing.getText());

				//TODO: record first attempt
				int updateStatus = sDB.updateSessionDetails(userId , session_type, phraseno, firstchartime, currentTime, typedText, log, editdis, attemptNo);
				FileOperations.writeLog(log, userId);
				FileOperations.write("Session Table updated: uid-"+ userId + " , session type-" + session_type+ " , phrase number-" + phraseno+ " , first char time-" + firstchartime+ " , last char time-" + currentTime+ " , text typed -" + typedText + " , edit distance -" + editdis + " , attempt -" + attemptNo);

				if(updateStatus ==1)
				{
					FileOperations.write("Update successful");
					//Toast.makeText(this, "LT:Session details updated", Toast.LENGTH_LONG).show();
				}else{
					//Toast.makeText(this, "LT:Failed to update session details", Toast.LENGTH_LONG).show();
					FileOperations.write("Update failed: # of rows updated -"+updateStatus);
				}
				//Log.d("logger:ftu",userId +" , "+ session_type+" , " +  phraseno +" , " + String.format("%.2f", cpm) +" cpm , " + typing.getText().toString()+" , " +editdis +" , " +repeated);

				//totalcpm =( (currentTime-firstchartime)/ + totalcpm )/2;
				//totalEDistance += editdis;
				//restart counter
			

				typing.setText("");
				log = "";
				firstchartime = 0;

				nexttext.setEnabled(false);
				return;

			} 
			else{
				//tbd
			}
		}

		int exeflag =0;
		//int phraseno = i+1;

		speed.setLastCharTime(currentTime);
		speed.setTextLength(typedText.length());
		speed.setWordCount(typedText.split(" ").length);
		//speed.setPhraseShownTS(sDB.getPhraseShownTS(userId, sessionId, session_type, index+1, 1));

		double cpm= speed.getCPM();	
		//double cpm2 = speed.getCPM2();
		//double timetaken=speed.getTimeTaken();

		int editdis=0;
		if(altMatch == true)
			editdis = 0;
		else
			editdis = editDistance.execute(alt.replaceAlternatives(alt.replaceAlternatives(presentedText)), alt.replaceAlternatives(alt.replaceAlternatives(typedText)));
		
		//Toast.makeText(getApplicationContext(), "Edist: "+editdis, Toast.LENGTH_LONG).show();
		//latest:this.setTitle(String.format("%.0f", cpm) +" | "+editdis+" | "+ Validator.getEDToStars( editdis,presentedText.length()) +" | "+ typedText.length() + " | "+ speed.getTimeTaken());
		//AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Bundle b = new Bundle();
		b.putInt("editdistance", editdis);
		//Log.d("debug","ed being sent FTU:" + editDistance.execute(alt.replaceAlternatives(alt.replaceAlternatives(presentedText)), alt.replaceAlternatives(alt.replaceAlternatives(typedText))) + ", " + editdis);
		b.putInt("wordlength", presentedText.length());
		b.putDouble("cpm",cpm);
		//b.putDouble("cpm2",cpm2);
	
		b.putString("presentedtext",alt.replaceAlternatives(alt.replaceAlternatives(presentedText)));
		b.putString("typedtext",alt.replaceAlternatives(alt.replaceAlternatives(typedText)));
		b.putInt("width", displayRectangle.width());
		b.putInt("height", displayRectangle.height());
		
		gamificationDialogFragment gdf =new gamificationDialogFragment();
		gdf.setArguments(b);
		gdf.c = getApplicationContext();

		gdf.show(getSupportFragmentManager(), "feedback");
		

		/*if(attemptNo >1){//this is second attempt, add another entry
			int xmlId = sDB.getPhraseXMLID(userId, session_type, phraseno);
			sDB.addSessionDetails(userId, session_type, phraseno,xmlId );
			FileOperations.write("Add to Session Table: uid-"+ userId + " , session type-" + session_type+ " , phrase number-" + phraseno+ " , phrase xml id -" + xmlId);
			Log.d("logger","Repeated greater than 1");
			FileOperations.write("Repeated greater than 1");
		}*/

		int updateStatus = sDB.updateSessionDetails(userId , session_type, continueFrom, firstchartime, currentTime, typedText,log, editdis, attemptNo);
		FileOperations.writeLog(log, userId);
		FileOperations.write("Session Details Table updated: uid-"+ userId + " , session type-" + session_type+ " , phrase number-" + continueFrom+ " , first char time-" + firstchartime+ " , last char time-" + currentTime+ " , text typed -" + typedText + " , edit distance -" + editdis + " , attempt -" + attemptNo);
		//Log.d("test","Session Details Table updated: uid-"+ userId + " , session type-" + session_type+ " , phrase number-" + continueFrom+ " , first char time-" + firstchartime+ " , last char time-" + currentTime+ " , text typed -" + typedText + " , edit distance -" + editdis + " , attempt -" + attemptNo);

		sDB.setContinueFrom(userId, session_type);
		FileOperations.write("Update continueFrom in Session Table: uid-"+ userId + " , session type-" + session_type +",continufrom: "+sDB.getLastSuccessfulPhrase(userId, session_type));

		if(updateStatus ==1)
		{
			FileOperations.write("Update successful");
			//Toast.makeText(this, "LT:Session details updated", Toast.LENGTH_LONG).show();
		}else{
			//Toast.makeText(this, "LT:Failed to update session details", Toast.LENGTH_LONG).show();
			FileOperations.write("Update failed: # of rows updated -"+updateStatus);
		}

		if (index < len - 1) {



			//++i;
			continueFrom = sDB.getLastSuccessfulPhrase(userId, session_type);
			index = continueFrom -1;
			typing.setText("");
			nexttext.setEnabled(false);
			log = "";
			firstchartime = 0;
			//Log.v("TAG","i ==== "+i);
			training.setText(word_phrase[index][0].trim());
			secondAttempt.setText("");

			typing.setError(null);
			attemptNo=0;

			exeflag++;

		}else{


			//totalEDistance += Validator.normalizedEditDistance(editdis, typedText.length());
			updateStatus = sDB.updateSessionEntry(userId, session_type, 0, session_start_time, session_end_time, SessionDetailsTable.SESSION_STATUS_DONE);
			FileOperations.write("SessionTable updated: uid-"+ userId + " , session type-" + session_type+ " , session rating" + 0+ " , session start time-" + session_start_time+ " , session end time-" + session_end_time+ " , session status -" + SessionDetailsTable.SESSION_STATUS_DONE);

			if(updateStatus ==1)
			{
				FileOperations.write("Update successful");
				//Toast.makeText(this, "LT:Session details updated", Toast.LENGTH_LONG).show();
			}else{
				//Toast.makeText(this, "LT:Failed to update session details", Toast.LENGTH_LONG).show();
				FileOperations.write("Update failed: # of rows updated -"+updateStatus);
			}


			//sDB.logSession(userId, session_type);
			//sDB.logSessionDetails(userId, session_type);
			/*try{
				wait(5000);
			}catch(Exception ex){
				ex.printStackTrace();
			}*/

			Intent intent = new Intent("game.MarathiUT.SESSIONFEEDBACK");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //?

			double[] cpmArray = sDB.getCPMNew(userId, sessionId,session_type);
			double errorIndicator = sDB.getED(userId, sessionId);

			Log.d("kbz","FTU: cpm-"+totalcpm + ", session error stars - "+errorIndicator);
			//sDB.logSession(userId, session_type);
			//sDB.logSessionDetails(userId, session_type);

			Bundle bd = new Bundle();
			bd.putInt("userid", userId);
			bd.putInt("sessiontype", session_type);
			bd.putDouble("lastcpm", cpm);
			//bd.putDouble("lastcpm2", cpm2);
			bd.putDoubleArray("cpm", cpmArray);
			bd.putDouble("editdistance", errorIndicator);
			bd.putDouble("editdist", editDistance.execute(alt.replaceAlternatives(alt.replaceAlternatives(presentedText)), alt.replaceAlternatives(alt.replaceAlternatives(typedText))));
			bd.putLong("sessionStartTS", session_start_time);
			bd.putLong("sessionEndTS", session_end_time);
			bd.putString("kbname", kbname);
			bd.putString("imename", imename);
			bd.putString("presentedtext", presentedText);
			bd.putString("typedtext",typedText);
			bd.putInt("width", displayRectangle.width());
			bd.putInt("height", displayRectangle.height());
			//bd.putInt("withHelpCount", withHelpCount);
			intent.putExtras(bd);
			startActivity(intent);

			exeflag++;


		}//else
		if(exeflag>0){
			exeflag=0;
			SyncDatabase syncDB = new SyncDatabase(getApplicationContext());
			syncDB.onUpSync(0,prgUserData);
		}

	}



}
