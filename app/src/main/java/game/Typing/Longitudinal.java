package game.Typing;

import game.Typing.validation.AlternativesParser;
import game.Typing.validation.Validator;

import java.io.File;
import java.security.acl.LastOwnerException;
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
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Longitudinal extends FragmentActivity implements OnClickListener {
	boolean leftflag = false,rightflag=false;
	Vibrator myVib;
	TextToSpeech tts3;
	String[][] word_phrase;
	String value;
	TextView training, progress,word_typed;
	EditText typing;
	Button nexttext;
	String typed;
	int sessiontype,userid,sid;
	//long sessionId;
	int  j; //i,
	int len;
	Bundle b;
	Database DB;
	SessionDetailsTable sDB;
	AlternativesParser alt ;
	private static final String TAG="LTU";
	int editdis;//,totalEditance;
	double cpm, totalcpm;
	//int LTUDone;
	SpeedCalculator speed;
	String kbname,imename;
	int continueFrom;
	String log, oldText;
	ProgressDialog prgUserData;
	
	Rect displayRectangle;
	//ProgressBar prog;
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
							f[0]=1;
							f[1]=0;
							Log.d("abba","x");
							String text = training.getText().toString();
							speakOut_training(text);
							myVib.vibrate(50);
						}

					}
					//top right corner
					else if(x1 >2*width/3 && x1 <width){
						if(f[1]==0){
							String text = typing.getText().toString();
							speakOut_training(text);
							f[0]=0;
							f[1]=1;	Log.d("a","x");

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
							Log.d("a","x");
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
							Log.d("a","x");
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
	DamerauLevenshteinAlgorithm editDistance = new DamerauLevenshteinAlgorithm(1,1,1,1);
	long firstchartime,currentTime,session_start_time,session_end_time;
	public static CustomKeyboard mCustomKeyboard;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tts3 = new TextToSpeech(this, onInit);
		setContentView(R.layout.activity_longitudinal);
		myVib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		mCustomKeyboard= new CustomKeyboard(this, null,R.id.keyboardview, R.xml.hexkbd);
		mCustomKeyboard.registerEditText(R.id.editText1);
		this.addContentView(mCustomKeyboard, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

		FileOperations.write("##onCreate()  in LTU");
	}

	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		Log.d("logger","Do nothing");
		mCustomKeyboard.hideCustomKeyboard();
		nexttext.setEnabled(true);
		FileOperations.write("##onBackPressed()  in LTU");
	}	
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		FileOperations.write("##onRestart() in LTU");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		FileOperations.write("##onResume() in LTU");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		FileOperations.write("##onPause() in LTU");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		FileOperations.write("##onStop() in LTU");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FileOperations.write("##onDestroy() in LTU");
	}
	
	/*
	@Override
	public void onComplete() {
		
		// TODO Auto-generated method stub
		long ts = System.currentTimeMillis();
		Log.d("test","Now update the phrase-shown timestamp for: "+word_phrase[continueFrom - 1][0] + " TS: "+ts);
		sid = (int)sDB.userSessionDetailsExist(userid,sessiontype);
		sDB.updatePhraseShownTS(userid, sid, sessiontype, continueFrom, 1, ts);
		
	}*/
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		FileOperations.write("##onStart() in LTU");
		this.setTitle(R.string.write_this_phrase_ltu);
		
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
		
		speed = new SpeedCalculator();
		cpm = 0;
		totalcpm = 0;
		editdis = 0;
		session_start_time =0;
		session_end_time = 0;
		firstchartime = 0;
		currentTime = 0;
		//totalEditance = 0;
		//continueFrom = 1;
		log = "";
		oldText = "";

		nexttext = (Button) findViewById(R.id.nextText);
		training = (TextView) findViewById(R.id.textView1);
		word_typed =(TextView) findViewById(R.id.textView5);
		word_typed.setTextColor(Color.DKGRAY);

		progress = (TextView) findViewById(R.id.textView2);
		typing = (EditText) findViewById(R.id.editText1);
		typing.setText("");

		DB = new Database(this);
		sDB = new SessionDetailsTable(this);
		alt = new AlternativesParser(this);
		
		displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		
		typing.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				word_typed.setText(typing.getText().toString());
				if(session_start_time == 0){
					session_start_time = System.currentTimeMillis();
					session_end_time = System.currentTimeMillis();
					sDB.updateSessionEntry(userid, sessiontype, 0, session_start_time, session_end_time, SessionDetailsTable.SESSION_STATUS_STARTED);
					FileOperations.write("SessionTable updated: uid-"+ userid + " , session type-" + sessiontype+ " , session rating" + 0+ " , session start time-" + session_start_time+ " , session end time-" + session_end_time+ " , session status -" + SessionDetailsTable.SESSION_STATUS_STARTED);
				}
				
				if(firstchartime ==0){
					
					//if we are about to begin typing a new word and 
					//if default KB is not the one assigned to the user don't let the session start
					//timings will not be affected
					
//					String currentDefaultKB = Settings.Secure.getString(
//							   getContentResolver(),
//							   Settings.Secure.DEFAULT_INPUT_METHOD
//							);
//
//					Log.d("kbz","curr default:"+currentDefaultKB + "expected: "+imename);
//					if(currentDefaultKB.equals(imename)==false)
//					{
//							Toast.makeText(getApplicationContext(), "Default keyboard should be "+kbname, Toast.LENGTH_LONG).show();
//							InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
//							imm.showInputMethodPicker();
//							return;
//					}
					
					firstchartime = System.currentTimeMillis();
					speed.setFirstCharTime(firstchartime);
					speed.setAvgWordLength(ApplicationConstants.MARATHI_AVG_WORD_LEN);
				}
				currentTime = System.currentTimeMillis();
				
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
						int diff =oldLen - newLen;
											
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

				if(s.length()>=2){
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
		Intent it = getIntent();
		b = it.getExtras();
		sessiontype = b.getInt("sessiontype");
		userid = b.getInt("userid");
		kbname = b.getString("kbname");
		imename = b.getString("imename");
		//continueFrom = b.getInt("continueFrom");
		continueFrom = sDB.getLTLastSuccessfulPhrase(userid, sessiontype);
		
		FileOperations.write("#4 LTU started for user:" + userid + "." + " Data sent from Session activity: kb-"+kbname + ", imename - "+imename+ " ,continueFrom - "+continueFrom + " ,sessionType - "+sessiontype);
		Log.d("kbz","continue from: "+continueFrom);
		//if (key.equals("-1")) {
		//i = continueFrom;
		j = 0;
		//Resources resources = this.getResources();
		//word_phrase = resources.getStringArray(R.array.Longitudinal_phrases_easy);
		//LTUDone = sDB.ltuDone(userid);
		//FileOperations.write("# of LTUs completed by user:" +LTUDone);
				
		//word_phrase = sDB.getPhrases(userid, SessionDetailsTable.LTU_SESSION_START + LTUDone ); //change
		word_phrase = sDB.getPhrases(userid, sessiontype ); //change
		
		/*if(LTUDone <0 || LTUDone > SessionDetailsTable.TOTAL_SESSIONS)
		{
			FileOperations.write("Illegal state:LTUDone value exceeds total number of sessions or is an invalid value" );
			finish();
			return;
		}*/
		
		if(word_phrase ==null){
			
			FileOperations.write("Illegal state:Phrase list for LTU empty.");
			finish();
			return;
			
		}else if(continueFrom > word_phrase.length){
			
			FileOperations.write("Illegal state:continueFrom exceeds number of phrases. Maybe the session is done but completion status wasnt updated correctly.");
			//finish();
			continueFrom = 1;
			sDB.updateSessionEntry(userid, sessiontype, 0, 0, 0, SessionDetailsTable.SESSION_STATUS_DONE);
			FileOperations.write("SessionTable updated: uid-"+ userid + " , session type-" + sessiontype+ " , session rating" + 0+ " , session start time-" + 0+ " , session end time-" + 0+ " , session status -" + SessionDetailsTable.SESSION_STATUS_DONE);
			//if( sDB.ltuDone(userid) == (SessionDetailsTable.TOTAL_SESSIONS - 1) ){
				finish();
				return;
			//}
			
		}
		
		Log.d("kbz","wordphrase: "+word_phrase);
		len = word_phrase.length;
		Log.d("kbz","wordphrase len: "+word_phrase.length);
		
		//i = continueFrom - 1;
		//i = sDB.getLTLastSuccessfulPhrase(userid, sessiontype) - 1;
		
		training.setText(word_phrase[continueFrom - 1][0]);
		typed = typing.getText().toString();
		//int tmp = i + 1;
		progress.setText(continueFrom + "/" + word_phrase.length);
		//sessionId = sDB.userSessionDetailsExist(userid, sessiontype);
		
		/*if(sessionId == -1)
			FileOperations.write("Illegal state: Could not get session id");*/
		int updateStatus = sDB.updateSessionEntry(userid, sessiontype, 0, 0, 0, SessionDetailsTable.SESSION_STATUS_STARTED);
		//onComplete();
		FileOperations.write("SessionTable updated: uid-"+ userid + " , session type-" + sessiontype+ " , session rating" + 0+ " , session start time-" + 0+ " , session end time-" + 0+ " , session status -" + SessionDetailsTable.SESSION_STATUS_STARTED);
		
		if(updateStatus ==1)
		{
			FileOperations.write("Update successful");
			//Toast.makeText(this, "LT:Session details updated", Toast.LENGTH_LONG).show();
		}else{
			//Toast.makeText(this, "LT:Failed to update session details", Toast.LENGTH_LONG).show();
			FileOperations.write("Update failed: # of rows updated -"+updateStatus);
		}
	}

	/*public String getEDToStars(int editDistance, int wordLength){
		String starsVal = "";
		//double correctnessRatio = (editDistance *1.0 / wordLength) * 100;
		double correctnessRatio = normalizedEditDistance(editDistance, wordLength);
		
		if(editDistance ==0)
			correctnessRatio = 100;
		
		if( correctnessRatio == 0){
			starsVal = "*****";
		
		}else if(correctnessRatio > 0 && correctnessRatio <= 20){
			starsVal = "****";
		}else if(correctnessRatio > 20 && correctnessRatio <= 40){
			starsVal = "***";
		}else if(correctnessRatio > 40 && correctnessRatio <= 60){
			starsVal = "**";
		}else
			starsVal = "*";
		
		Log.d("ed","ED: "+editDistance + ", Wordlength : "+wordLength);
		Log.d("ed","Correctness ratio: "+String.format("%.2f", correctnessRatio) + ", stars : "+starsVal);
		return starsVal;
	}
	
	public double normalizedEditDistance(int editDistance, int wordLength){
		
		double correctnessRatio =0;
		if(wordLength!=0)
			correctnessRatio =  (editDistance *1.0 / wordLength) * 100;
		else
			FileOperations.write("Length of word typed in LTU is 0 for "+word_phrase[i][0] +", user: "+userid);
		return correctnessRatio;
	}*/
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int index = continueFrom - 1;
		nexttext.setEnabled(false);
		
		session_end_time = System.currentTimeMillis();
		if (firstchartime ==0) {
			Toast.makeText(this, "First character typed timestamp is 0", Toast.LENGTH_LONG).show();
			FileOperations.write("First character typed timestamp is 0");
		}
		/*
		if ((!word_phrase[i].equals(typing.getText().toString())))
		{
			Dialog d = new Dialog(this);
			TextView tv = new TextView(this);
			tv.setText("Type again");
			d.setTitle("Typed Wrong!!!");
			d.setContentView(tv);
			d.show();			
			typing.setText("");
		} else {
			//Log.v("TAG","i="+i+"J="+j+"word phrase"+word_phrase.toString()+"-----else");
			Dialog d = new Dialog(this);
			TextView tv = new TextView(this);
			tv.setText("Type the next word");
			d.setTitle("Yipee!!!");
			d.setContentView(tv);
			d.show();*/
		int exeflag=0;
		String typedText = typing.getText().toString().trim();
		String presentedText = word_phrase[index][0].trim();
		
		//Log.d("replace","presented:"+alt.replaceAlternatives(alt.replaceAlternatives(presentedText))+". typed: "+alt.replaceAlternatives(alt.replaceAlternatives(typedText)));
		//int phraseno= continueFrom;
		
		speed.setLastCharTime(currentTime);
		speed.setTextLength(typedText.length());
		speed.setWordCount(typedText.split(" ").length);
		//speed.setPhraseShownTS(sDB.getPhraseShownTS(userid, sid, sessiontype, index+1, 1));
		
		//float cpm = typing.getText().toString().trim().length() / ( (currentTime-firstchartime) /(float)60000); 
		double cpm = speed.getCPM();
		//double cpm2 = speed.getCPM2();
		int editdis =  editDistance.execute(alt.replaceAlternatives(alt.replaceAlternatives(presentedText)), alt.replaceAlternatives(alt.replaceAlternatives(typedText)));

		//int timetaken=(int) (currentTime-firstchartime) /1000;
		//float timetaken = speed.getTimeTaken();
		//Toast.makeText(getApplicationContext(), "(TLen,time)(" +typing.getText().toString().length()+ ","+timetaken+", cpm: "+cpm +", Edist: "+editdis, Toast.LENGTH_LONG).show();
		//this.setTitle(typedText.length()+ " chars in "+timetaken+" s; cpm:"+String.format("%.2f", cpm) +"; ed:"+editdis);
		//latest:this.setTitle(String.format("%.0f", cpm) +" | "+editdis+" | "+ Validator.getEDToStars( editdis, presentedText.length()) +" | "+ word_phrase[index][1] +" | "+ typedText.length() + " | "+ speed.getTimeTaken());
		
		Bundle b = new Bundle();
		b.putInt("editdistance", editdis);
		Log.d("debug","ed being sent LTU:" + editDistance.execute(alt.replaceAlternatives(alt.replaceAlternatives(presentedText)), alt.replaceAlternatives(alt.replaceAlternatives(typedText))) + ", " + editdis);
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
		
		int updateStatus = sDB.updateSessionDetails(userid , sessiontype, continueFrom, firstchartime, currentTime, typedText,log, editdis, 1);
		FileOperations.write("Session Table updated: uid-"+ userid + " , session type-" + sessiontype+ " , phrase number-" + continueFrom+ " , first char time-" + firstchartime+ " , last char time-" + currentTime+ " , text typed -" + typedText + " , edit distance -" + editdis + " , attempt -" + 1);
		FileOperations.writeLog(log, userid);
		
		sDB.setContinueFrom(userid, sessiontype);
		FileOperations.write("Update continueFrom in Session Table: uid-"+ userid + " , session type-" + sessiontype +",continufrom: "+sDB.getLastSuccessfulPhrase(userid, sessiontype));
		
		/*if(updateStatus ==1)
		{
			Toast.makeText(this, "LTU:Session details updated", Toast.LENGTH_LONG).show();
		}else
			Toast.makeText(this, "LTU:Failed to update session details", Toast.LENGTH_LONG).show();*/
		if(updateStatus ==1)
		{
			FileOperations.write("Update successful");
			//Toast.makeText(this, "LT:Session details updated", Toast.LENGTH_LONG).show();
		}else{
			//Toast.makeText(this, "LT:Failed to update session details", Toast.LENGTH_LONG).show();
			FileOperations.write("Update failed: # of rows updated -"+updateStatus);
		}
		
		Log.d(TAG,userid +" , "+ sessiontype +" , " +  index +" , " + cpm +" cpm , " + typing.getText().toString()+" , " +editdis +" , " +1);
		
		if (index < len - 1) {

//			String typedText= typing.getText().toString();
//			
//			speed.setLastCharTime(currentTime);
//			speed.setTextLength(typedText.length());
//			speed.setWordCount(typedText.split(" ").length);
//			
//			//float cpm = typing.getText().toString().trim().length() / ( (currentTime-firstchartime) /(float)60000); 
//			double cpm = speed.getCPM();
//			int editdis =  editDistance.execute(word_phrase[i][0], typedText);
//
//			//int timetaken=(int) (currentTime-firstchartime) /1000;
//			//float timetaken = speed.getTimeTaken();
//			//Toast.makeText(getApplicationContext(), "(TLen,time)(" +typing.getText().toString().length()+ ","+timetaken+", cpm: "+cpm +", Edist: "+editdis, Toast.LENGTH_LONG).show();
//			//this.setTitle(typedText.length()+ " chars in "+timetaken+" s; cpm:"+String.format("%.2f", cpm) +"; ed:"+editdis);
//			this.setTitle(String.format("%.0f", cpm) +" | "+editdis+" | "+ Validator.getEDToStars( editdis, word_phrase[i][0].length()) +" | "+ word_phrase[i][1]);
			continueFrom = sDB.getLTLastSuccessfulPhrase(userid, sessiontype);
			index = continueFrom -1;
			//continueFrom++;
			//record the session details

			//log session
//			sDB.updateSessionDetails(userid , SessionDetailsTable.LTU_SESSION_START + LTUDone, i, firstchartime, currentTime, typedText, editdis, 1);
//			Log.d(TAG,userid +" , "+ SessionDetailsTable.LTU_SESSION_START + LTUDone+" , " +  i +" , " + cpm +" cpm , " + typing.getText().toString()+" , " +editdis +" , " +1);
			
			//int tmp =i+1;
			progress.setText(continueFrom + "/" + word_phrase.length);

			/*if(totalcpm == 0)
				totalcpm =cpm;
			else
				totalcpm =( cpm + totalcpm )/2;*/

			//totalEditance +=editdis;
			typing.setText("");
			log = "";
			firstchartime=0;
			//Log.v(TAG,"i ==== "+index);
			training.setText(word_phrase[index][0].trim());
			exeflag++;
		}else{

			//log session
			/*String typedText= typing.getText().toString();
			
			speed.setLastCharTime(currentTime);
			speed.setTextLength(typedText.length());
			speed.setWordCount(typedText.split(" ").length);
			
			//float cpm = typing.getText().toString().trim().length() / ( (currentTime-firstchartime) /(float)60000); 
			double cpm = speed.getCPM();
			int editdis =  editDistance.execute(word_phrase[i][0], typedText);

			//int timetaken=(int) (currentTime-firstchartime) /1000;
			//float timetaken = speed.getTimeTaken();
			//this.setTitle(typedText.length()+ " chars in "+timetaken+" s; cpm:"+String.format("%.2f", cpm) +"; ed:"+editdis);
			this.setTitle(String.format("%.0f", cpm) +" | "+editdis+" | "+ Validator.getEDToStars( editdis, word_phrase[i][0].length()) +" | "+ word_phrase[i][1]);*/
			
			//float cpm = typing.getText().toString().trim().length() / ( (currentTime-firstchartime) /(float)60000); 
			//int editdis =  editDistance.execute(word_phrase[i], typing.getText().toString());
			
			//int timetaken=(int) (currentTime-firstchartime) /1000;
			//Toast.makeText(getApplicationContext(), "(TLen,time)(" +typing.getText().toString().length()+ ","+timetaken+", cpm: "+cpm +", Edist: "+editdis, Toast.LENGTH_LONG).show();
			//record the session details

			//log session
			//sDB.updateSessionDetails(userid , SessionDetailsTable.LTU_SESSION_START + LTUDone, phraseno, firstchartime, currentTime, typing.getText().toString(), editdis, 1);
			//Log.d(TAG,userid +" , "+ SessionDetailsTable.LTU_SESSION_START + LTUDone+" , " +  phraseno +" , " + cpm +" cpm , " + typing.getText().toString()+" , " +editdis +" , " +1);
			
			/*if(totalcpm == 0)
				totalcpm =cpm;
			else
				totalcpm =( cpm + totalcpm )/2;*/

			//totalEditance +=editdis;

			double[] cpmArray = sDB.getCPMNew(userid, sessiontype, sessiontype);
			double errorIndicator = sDB.getED(userid, sessiontype);
			
			updateStatus = sDB.updateSessionEntry(userid, sessiontype, 0, session_start_time, session_end_time, SessionDetailsTable.SESSION_STATUS_DONE);
			FileOperations.write("SessionTable updated: uid-"+ userid + " , session type-" + sessiontype+ " , session rating" + 0+ " , session start time-" + session_start_time+ " , session end time-" + session_end_time+ " , session status -" + SessionDetailsTable.SESSION_STATUS_DONE);
			
			if(updateStatus ==1)
			{
				FileOperations.write("Update successful");
				//Toast.makeText(this, "LT:Session details updated", Toast.LENGTH_LONG).show();
			}else{
				//Toast.makeText(this, "LT:Failed to update session details", Toast.LENGTH_LONG).show();
				FileOperations.write("Update failed: # of rows updated -"+updateStatus);
			}
			
			
			/*sDB.logSession(userid, sessiontype);
			sDB.logSessionDetails(userid, sessiontype);*/
			/*try {
			    Thread.sleep(25000);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}*/
			// TODO Auto-generated method stub
			Intent intent = new Intent("game.MarathiUT.SESSIONFEEDBACK");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //?
			
			Bundle bd = new Bundle();
			bd.putInt("userid", userid);
			bd.putInt("sessiontype", sessiontype);
			bd.putDouble("lastcpm", cpm);
			//b.putDouble("lastcpm2",cpm2);
			bd.putDoubleArray("cpm", cpmArray);
			bd.putDouble("editdistance", errorIndicator);
			bd.putDouble("editdist", editDistance.execute(alt.replaceAlternatives(alt.replaceAlternatives(presentedText)), alt.replaceAlternatives(alt.replaceAlternatives(typedText))));
			bd.putLong("sessionStartTS", session_start_time);
			Log.d("kbz","passing ime:"+imename);
			bd.putLong("sessionEndTS", session_end_time);
			bd.putString("kbname", kbname);
			bd.putString("imename", imename);
			bd.putString("presentedtext", alt.replaceAlternatives(alt.replaceAlternatives(presentedText)));
			bd.putString("typedtext", alt.replaceAlternatives(alt.replaceAlternatives(typedText)));
			bd.putInt("width", displayRectangle.width());
			bd.putInt("height", displayRectangle.height());
			intent.putExtras(bd);
			startActivity(intent);


			exeflag++;
		}
		if(exeflag>0){
			exeflag=0;
			SyncDatabase syncDB = new SyncDatabase(getApplicationContext());
			syncDB.onUpSync(0,prgUserData);
		}
		//}
	}

	}
