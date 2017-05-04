package game.Typing;

import game.Typing.validation.AlternativesParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class Training extends FragmentActivity implements OnClickListener {

		Vibrator myVib;
	String[][] word_phrase;
	String value;
	TextView training,word_typed;
	TextToSpeech tts3;
	EditText typing;
	Button nexttext;
	String typed;
	int sessiontype;
	int j, repeated; //i, 
	int len;
	boolean leftflag = false,rightflag=false;
	long first_char_time,last_char_time,session_start_time,session_end_time;
	Bundle b;
	Database DB;
	SessionDetailsTable sDB;
	int userId, sessionId,continueFrom;
	String kbname, imename;
	File dir, file;
	String filePath;
	String log, oldText;
	ProgressDialog prgUserData;
	AlternativesParser alt ;

	DamerauLevenshteinAlgorithm editDistance = new DamerauLevenshteinAlgorithm(1,1,1,1);
	public static CustomKeyboard mCustomKeyboard;
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
//	@Override
//	public void onHoverEvent(MotionEvent event){
//
//	}



	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

			if (hasFocus) {
			this.getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);
		tts3 = new TextToSpeech(this, onInit);
		mCustomKeyboard= new CustomKeyboard(this, null,R.id.keyboardview, R.xml.hexkbd);
		mCustomKeyboard.registerEditText(R.id.editText1);
		this.addContentView(mCustomKeyboard, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		myVib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		FileOperations.write("##onCreate() in Training");

	}
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

	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		Log.d("logger", "Do nothing");
		mCustomKeyboard.hideCustomKeyboard();
		nexttext.setEnabled(true);
		FileOperations.write("##onBackPressed()  in Training");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		FileOperations.write("##onRestart() in Training");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		FileOperations.write("##onResume() in Training");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		FileOperations.write("##onPause() in Training");
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		FileOperations.write("##onStop() in Training");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FileOperations.write("##onDestroy() in Training");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		FileOperations.write("##onStart() in Training");
		this.setTitle(R.string.write_this_word);
		log = "";
		oldText = "";
		session_start_time =0;
		session_end_time = 0;
		first_char_time = 0;
		last_char_time = 0;
		
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
		
		DB = new Database(this);
		sDB = new SessionDetailsTable(this);
		alt = new AlternativesParser(this);

		nexttext = (Button) findViewById(R.id.nextText);
		training = (TextView) findViewById(R.id.textView1);
		word_typed =(TextView) findViewById(R.id.textView);
		word_typed.setTextColor(Color.DKGRAY);
		typing = (EditText) findViewById(R.id.editText1);
		typing.setLongClickable(false);
		typing.setText("");


		typing.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				// TODO Auto-generated method stub
				//typing.setError(null);
				//.//String str; 
				word_typed.setText(typing.getText().toString());
				if (session_start_time == 0) {
					session_start_time = System.currentTimeMillis();
					session_end_time = System.currentTimeMillis();
					sDB.updateTrainingEntry(userId, sessionId, 0, session_start_time, session_end_time, SessionDetailsTable.SESSION_STATUS_STARTED);
					FileOperations.write("SessionTable updated: uid-" + userId + " , session id-" + sessionId + " , session rating" + 0 + " , session start time-" + session_start_time + " , session end time-" + session_end_time + " , session status -" + SessionDetailsTable.SESSION_STATUS_STARTED);
				}


				if (first_char_time == 0) {

					//if we are about to begin typing a new word and 
					//if default KB is not the one assigned to the user don't let the session start
					//timings will not be affected

//					String currentDefaultKB = Settings.Secure.getString(
//							getContentResolver(),
//							Settings.Secure.DEFAULT_INPUT_METHOD
//							);

//					if(currentDefaultKB.equals(imename)==false)
//					{
//						//typing.setText("");
//						Toast.makeText(getApplicationContext(), "Default keyboard should be "+kbname, Toast.LENGTH_LONG).show();
//						getApplicationContext();
//						InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//						imm.showInputMethodPicker();
//
//						return;
//					}

					first_char_time = System.currentTimeMillis();

				}
				last_char_time = System.currentTimeMillis();

				if (oldText.compareToIgnoreCase(String.valueOf(s)) != 0) {
					//Log.d("detailedLogger", String.valueOf(s) + "starting from:" + start + ", prev length:" + before + ", replaced chars: "+count);


					//check what's new
					int oldLen = oldText.length();
					int newLen = s.length();

					if (before == 0) { //something added
						int diff = newLen - oldLen;
						Log.d("detailedLogger", "Added " + diff + " characters:" + String.valueOf(s.subSequence(start, start + diff)) + " at " + start + " after " + String.valueOf(s.subSequence(0, start)));
						log += System.currentTimeMillis() + ApplicationConstants.logFieldSeparator + ApplicationConstants.ADD + ApplicationConstants.logFieldSeparator + start + ApplicationConstants.logFieldSeparator + String.valueOf(s.subSequence(start, start + diff)) + ApplicationConstants.logRowSeparator;

					} else {
						int diff = oldLen - newLen;

						//Log.d("detailedLogger","Removed "+ before +" # characters:" +String.valueOf(oldText.subSequence(start, start+before)) + " and added " + String.valueOf(s).substring(start));

						if (String.valueOf(s.subSequence(start, start + count)).length() > 0) {
							Log.d("detailedLogger", "Removed " + before + " # characters:" + String.valueOf(oldText.subSequence(start, start + before)) + " and added " + s.subSequence(start, start + count));
							log += System.currentTimeMillis() + ApplicationConstants.logFieldSeparator + ApplicationConstants.REPLACE + ApplicationConstants.logFieldSeparator + start + ApplicationConstants.logFieldSeparator + s.subSequence(start, start + count) + ApplicationConstants.logRowSeparator;
						} else {
							Log.d("detailedLogger", "Removed " + before + " # characters:" + String.valueOf(oldText.subSequence(start, start + before)));
							log += System.currentTimeMillis() + ApplicationConstants.logFieldSeparator + ApplicationConstants.DELETED + ApplicationConstants.logFieldSeparator + start + ApplicationConstants.logFieldSeparator + s.subSequence(start, start + count) + ApplicationConstants.logRowSeparator;
						}

					}

					oldText = String.valueOf(s);
				}
				if (s.length() > 1)
					nexttext.setEnabled(false);

				//Log.d("logger:training","textChanged");

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub
				/*if(oldText.compareToIgnoreCase(String.valueOf(s))!=0){
					Log.d("detailedLogger:before", String.valueOf(s) + "start:" + start + ", how many:" + count);
					log += System.currentTimeMillis() + " ; " + String.valueOf(s)+"\n";
					oldText = String.valueOf(s);
				}*/
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}


		});

		typing.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					//sendMessage();
					Log.d("debug","txtbox says done");
					nexttext.performClick();
					handled = true;
				}
				return handled;
			}
			
		});

		/*typing.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
				// TODO Auto-generated method stub
				Log.d("detailedLogger:onkey", String.valueOf(keyCode) );
				return false;
			}
			
		});*/
		
		nexttext.setOnClickListener(this);
		Intent it = getIntent();
		b = it.getExtras();
		sessiontype = b.getInt("sessiontype");
		userId = b.getInt("userid");
		kbname = b.getString("kbname");
		imename = b.getString("imename");
		//continueFrom = b.getInt("continueFrom");
		sessionId = b.getInt("sessionid"); 
		//if(sessionId !=0)
		
		
		int[] sessionIdList = sDB.getIncompleteSessions(userId, ApplicationConstants.TRAINING_SESSIONTYPE);
		
		if(sessionIdList != null){
			
			if ( sessionIdList.length >1 ) {
				FileOperations.write("Illegal state in Training: More than one training session with status 'incomplete'");
				finish();
				return;
			}else
				sessionId = sessionIdList[0];
			
		}
		
		continueFrom = sDB.getLastSuccessfulPhrase(userId, sessionId); //resolve
		Log.d("kbz","continueFrom: "+continueFrom);	
		
		if(continueFrom <=0){				
			continueFrom = 1; 
			Log.d("kbz","continueFrom <=0 : reset to - "+continueFrom);	
		}
			
		
		//i = continueFrom - 1;
		Log.d("kbz","continue from: "+continueFrom);
		j = 0;
		repeated = 0;
		//Resources resources = this.getResources();
		//word_phrase = resources.getStringArray(R.array.T_words);
		word_phrase = sDB.getTrainingPhrases();

		if(word_phrase ==null){
			FileOperations.write("Illegal state:Phrase list for training empty.");
			finish();
			return;
		}else if(continueFrom > word_phrase.length){
			FileOperations.write("Illegal state:continueFrom exceeds number of phrases.");
			
			//update the sessionEntry to completed
			sDB.updateTrainingEntry(userId, sessionId, 0, 0, 0, SessionDetailsTable.SESSION_STATUS_DONE);
			FileOperations.write("SessionTable updated: uid-"+ userId + " , session id-" + sessionId+ " , session rating" + 0+ " , session start time-" + 0+ " , session end time-" + 0+ " , session status -" + SessionDetailsTable.SESSION_STATUS_DONE);
			//i=0;
			continueFrom = 1;
			sessionId = 0 ;
			
			if(sDB.countTrainings(userId) == ApplicationConstants.MAX_TRAININGS ){
				finish();
				return;
			}
			
			//finish();
			//return;
		}

		//i = continueFrom - 1;
		len = word_phrase.length;
		training.setText(word_phrase[continueFrom - 1][0].trim());

		Log.d("training_word", "a" + training.getText().toString());
//		speakOut_training("टाइप करें" + training.getText().toString() + " " + "टाइप करें" + training.getText().toString() + " " + "टाइप करें" + training.getText().toString());
		typed = typing.getText().toString();

		if(continueFrom == 1 && sessionId ==0){ //start a fresh training session, sessionId ==0 chk needed for a training session interrupted even before first phrase training completed
			sessionId =  (int) sDB.addTrainingEntry(userId, Integer.valueOf(sessiontype) );	
			FileOperations.write("SessionTable add entry: uid-"+ userId + " , session type-" + sessiontype + ", sessionId associated-" + sessionId);
			Log.d("db:training entry", userId + "," + Integer.valueOf(sessiontype) + " added with id "+sessionId);
		}
		

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		session_end_time = System.currentTimeMillis();
		//String typedText = typing.getText().toString().trim();
		boolean altMatch = false;
		int index = continueFrom -1;
		String typedText = typing.getText().toString().trim();
		String presentedText = word_phrase[index][0].trim();

		

		/*Support for alternatives
		 * try{
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

		//if (!(word_phrase[index][0].trim()).equals(typedText) &&  !altMatch) // incorrectly typed text
		if (editDistance.execute(alt.replaceAlternatives(alt.replaceAlternatives(presentedText)), alt.replaceAlternatives(alt.replaceAlternatives(typedText))) !=0 )
		{
			MediaPlayer mp = MediaPlayer.create(this, R.raw.one);
			mp.start();
			myVib.vibrate(50);
			Log.d("training_word", "error" + training.getText().toString());
			//typing.setText("");
		}else{
			//Log.v("TAG","i="+i+"J="+j+"word phrase"+word_phrase.toString()+"-----else");

			typing.setError(null);
			MediaPlayer mp = MediaPlayer.create(this, R.raw.two);
			mp.start();
			Log.d("training_word", "b" + training.getText().toString());
//			speakOut_training("टाइप करें" + training.getText().toString() + " " + "टाइप करें" + training.getText().toString() + " " + "टाइप करें" + training.getText().toString());

			repeated++;

			if(repeated< ApplicationConstants.TRAINING_WORD_REPEAT_COUNT){       //type correctly once more
				typing.setText(""); 
				first_char_time = 0;
				nexttext.setEnabled(false);

				return;
			}

			int exeflag =0;

			nexttext.setEnabled(false);
			int phraseid = (ApplicationConstants.TRAINING_PHRASE_XMLID_START + index);  //cleanup
			int timetaken = (int)((last_char_time - first_char_time)/1000) ; // in sec
			//int phraseNo = i+1;
			
			//record session details
			long status = sDB.addTrainingDetails(userId, sessionId, Integer.valueOf(sessiontype), continueFrom, phraseid, first_char_time, last_char_time, typedText, log, 0, 1);
			FileOperations.writeLog(log, userId);
			
			/*FileOperations.write("Session Details Table updated: uid-"+ userId + " , session id-" + sessionId + " , session type-" + sessiontype + " , phrase number-" + continueFrom+ " , first char time-" + first_char_time+ " , last char time-" + last_char_time+ " , text typed -" + typedText + " , edit distance -" + 0 + " , attempt -" + 1);
			Bundle b = new Bundle();
			b.putInt("editdistance", editdis);
			b.putInt("wordlength", presentedText.length());
			b.putDouble("cpm",cpm);
			
			gamificationDialogFragment gdf =new gamificationDialogFragment();
			gdf.setArguments(b);
			gdf.show(getSupportFragmentManager(), "feedback");*/
			
			sDB.setContinueFrom(userId, sessionId);
			FileOperations.write("Update continueFrom in Session Table: uid-"+ userId + " , session id-" + sessionId +",continufrom: "+sDB.getLastSuccessfulPhrase(userId, sessionId));
			
			if(status ==-1)
			{
				FileOperations.write("Could not add trained phrase entry to "+SessionDetailsTable.TABLE_SESSION_PHRASE_MAP+" for phrase no:" + continueFrom + typedText);
				Toast.makeText(this, "Could not add trained phrase entry to "+SessionDetailsTable.TABLE_SESSION_PHRASE_MAP+" for phrase no:" + continueFrom + typedText , Toast.LENGTH_LONG).show();
			}
			Log.d("logger:training",userId +" , " + sessionId+" , " + Integer.valueOf(sessiontype)+" , " + continueFrom +" , " + phraseid+" , " + timetaken+" sec , " + typedText+" , " + 0 +" , " + 1);

			if (index < len - 1) { //if more words to be trained on remain
				//++i;
				continueFrom = sDB.getLastSuccessfulPhrase(userId, sessionId);
				index = continueFrom - 1;
				//nexttext.setEnabled(false);
				/*int phraseid = (SessionDetailsTable.TRAINING_PHRASE_XMLID_START + i);
				int timetaken = (int)((last_char_time - first_char_time)/1000) ; // in sec

				++i;
				//record session details
				sDB.addTrainingDetails(userId, sessionId, Integer.valueOf(sessiontype),i, phraseid, first_char_time, last_char_time, typing.getText().toString(), 0, 1);
				Log.d("logger:training",userId +" , " + sessionId+" , " + Integer.valueOf(sessiontype)+" , " + i +" , " + phraseid+" , " + timetaken+" sec , " + typing.getText().toString()+" , " + 0 +" , " + 1);*/

				typing.setText("");	
				log = "";

				//Log.v("TAG","i ==== "+i);
				training.setText(word_phrase[index][0].trim());
				Log.d("training_word", "d" + training.getText().toString());
//				speakOut_training("टाइप करें" + training.getText().toString() + " " + "टाइप करें" + training.getText().toString() + " " + "टाइप करें" + training.getText().toString());
				repeated=0;
				first_char_time = 0 ;
				//nexttext.setEnabled(false);
				exeflag++;
			}else{ 
				//All words done
				//record session details
				//nexttext.setEnabled(false);
				/*int phraseid = (SessionDetailsTable.TRAINING_PHRASE_XMLID_START + i);
				int timetaken = (int)((last_char_time - first_char_time)/1000) ; // in sec
				++i;
				
				sDB.addTrainingDetails(userId, sessionId, Integer.valueOf(sessiontype),i, phraseid, first_char_time, last_char_time, typing.getText().toString(), 0, 1);

				Log.d("logger:training-last",userId +" , " + sessionId+" , " + Integer.valueOf(sessiontype)+" , " + i +" , " + phraseid+" , " + timetaken+" sec , " + typing.getText().toString()+" , " + 0 +" , " + 1);*/
				//++i;
				//update session table to set status to completed
				status = sDB.updateTrainingEntry(userId, sessionId, 0, session_start_time, session_end_time, SessionDetailsTable.SESSION_STATUS_DONE );
				FileOperations.write("SessionTable updated: uid-"+ userId + " , session id-" + sessionId+ " , session rating" + 0+ " , session start time-" + session_start_time+ " , session end time-" + session_end_time+ " , session status -" + SessionDetailsTable.SESSION_STATUS_DONE);
				
				sDB.setContinueFrom(userId, sessionId);
				FileOperations.write("Update continueFrom in Session Table: uid-"+ userId + " , session id-" + sessionId +",continufrom: "+sDB.getLastSuccessfulPhrase(userId, sessionId));
				
				if(status <= 0){
					Log.d("logger:training-last","Could not update");
					FileOperations.write("Could not add trained phrase entry to "+SessionDetailsTable.TABLE_SESSION_PHRASE_MAP+" for phrase no:" + continueFrom + typedText);
					Toast.makeText(this, "Could not update training completed to "+SessionDetailsTable.SESSION_TABLE , Toast.LENGTH_LONG).show();
				}
				else
					Log.d("logger:training-last","Updated");

				//sDB.getSessionEntry(userId);

				Intent intent = new Intent("game.MarathiUT.SESSION");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bd = new Bundle();
				bd.putInt("userid", userId );
//				bd.putString("kbname", kbname);
//				bd.putString("imename", imename);
				/*bd.putString("remark", "Well done! All set for the next challenge?");*/
				intent.putExtras(bd);
				startActivity(intent);

				exeflag++;
			}

			if(exeflag>0){
				exeflag=0;
				SyncDatabase syncDB = new SyncDatabase(getApplicationContext());
				syncDB.onUpSync(0,prgUserData);
			}
		}
	}

}
