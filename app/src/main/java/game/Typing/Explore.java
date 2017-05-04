package game.Typing;

import java.security.acl.LastOwnerException;
import java.text.MessageFormat;

import javax.security.auth.callback.Callback;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Explore extends Activity implements OnClickListener{

	/*public Explore() {
		// TODO Auto-generated constructor stub
	}*/
	
	int userid, sid, sessionType;
	int explorationTime=5; //mins
	Button explore;
	EditText type;
	TextView tv;
	Handler h;
	SessionDetailsTable  sDB;
	long fchar,lchar;
	String kbname,imename;
	String log, oldText;
	ProgressDialog prgUserData;
	
	CustomKeyboard mCustomKeyboard;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.explore);
		mCustomKeyboard= new CustomKeyboard(this, null,R.id.keyboardview, R.xml.hexkbd);
		mCustomKeyboard.registerEditText(R.id.editText1);
		this.addContentView(mCustomKeyboard, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

		FileOperations.write("##onCreate() in Explore");
	}
	
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		Log.d("logger","Do nothing");
		FileOperations.write("##onBackPressed()  in Explore");
	}	
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		FileOperations.write("##onRestart() in Explore");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		FileOperations.write("##onResume() in Explore");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		FileOperations.write("##onPause() in Explore");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		FileOperations.write("##onStop() in Explore");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FileOperations.write("##onDestroy() in Explore");
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		FileOperations.write("##onStart() in Explore");
		this.setTitle(R.string.write_explore);
		
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
		
		Bundle b = getIntent().getExtras();
		userid = b.getInt("userid");
		kbname = b.getString("kbname");
		imename = b.getString("imename");
		sessionType = b.getInt("sessiontype");
		explore = (Button) findViewById(R.id.nextText);
		explore.setOnClickListener(this);
		type = (EditText) findViewById(R.id.editText1);
		tv = (TextView) findViewById(R.id.timer);
		fchar=0;
		lchar=0;
		log = "";
		oldText = "";
		
		sDB = new SessionDetailsTable(this);
		
		type.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(fchar==0){
					
					//if we are about to begin typing a new word and 
					//if default KB is not the one assigned to the user don't let the session start
					//timings will not be affected
					
//					String currentDefaultKB = Settings.Secure.getString(
//							   getContentResolver(),
//							   Settings.Secure.DEFAULT_INPUT_METHOD
//							);
//
//					if(currentDefaultKB.equals(imename)==false)
//					{
//							Toast.makeText(getApplicationContext(), "Default keyboard should be "+kbname, Toast.LENGTH_LONG).show();
//							InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
//							imm.showInputMethodPicker();
//							return;
//					}
					fchar=System.currentTimeMillis();
//					sDB.updateSessionEntry(userid, sessionType, 0, fchar, fchar, SessionDetailsTable.SESSION_STATUS_STARTED);
				}
				lchar = System.currentTimeMillis();
				
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
				
				if(type.getText().toString().length() > 1)
				{
					explore.setEnabled(true);
				}
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
		
		android.os.Handler.Callback callback = new android.os.Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				Log.d("explore",msg.obj+"--");
				
				lchar = System.currentTimeMillis();
				endActivity();
				return false;
			}
		};
		h= new Handler(callback);
		
		Message m= Message.obtain(h);
		m.obj="test";
		h.sendMessageDelayed(m, explorationTime*60*1000);
		//explore.setOnClickListener(this);
		
		sid=(int)sDB.addTrainingEntry(userid, sessionType);
		FileOperations.write("SessionTable added entry for: uid-"+ userid + " , session type-" + sessionType+ " , with sessionId-" + sid);
		
		new CountDownTimer(explorationTime*60*1000, 1000) {

		     public void onTick(long millisUntilFinished) {
		         //tv.setText("seconds remaining: " + millisUntilFinished / 1000);
		         tv.setText(String.format("%02d:%02d", (( millisUntilFinished / 1000)%3600)/60, (( millisUntilFinished / 1000)%60) ));
		     }

		     public void onFinish() {
		         //mTextField.setText("done!");
		     }
		  }.start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId()==R.id.nextText)
		{
			lchar = System.currentTimeMillis();
			/*try{
				Looper l = h.getLooper();
				l.quitSafely();
			}catch(Exception ex){
				ex.printStackTrace();
			}*/
			h.removeCallbacksAndMessages(null);
			endActivity();
		}
	}
	private void endActivity(){
		
		explore.setEnabled(false);
		Bundle bundle = new Bundle();
		Intent intent = new Intent("game.MarathiUT.SESSION");
		bundle.putInt("userid", userid);
		bundle.putInt("timespent",(int)(lchar-fchar)/60000);
		bundle.putString("kbname", kbname);
		bundle.putString("imename", imename);
		intent.putExtras(bundle);
		
		sDB.addTrainingDetails(userid, sid, sessionType, 0, 0, fchar, lchar, type.getText().toString(),log, 0, 1);
		FileOperations.writeLog(log, userid);
		FileOperations.write("Session Table updated: uid-"+ userid + " , session id-" + sid + " , session type-" + sessionType+ " , phrase number-" + 0 + " , phrase id-" + 0+ " , first char time-" + fchar+ " , last char time-" + lchar+ " , text typed -" + type.getText().toString() + " , edit distance -" + 0 + " , attempt - 1 ");
		log="";
		
		long status = sDB.updateTrainingEntry(userid, sid, 0,fchar, lchar, SessionDetailsTable.SESSION_STATUS_DONE);
		FileOperations.write("SessionTable updated: uid-"+ userid + " , session id-" + sid+ " , session rating" + 0+ " , first char time-" + fchar+ " , last char time-" + lchar+ " , session status -" + SessionDetailsTable.SESSION_STATUS_DONE);
		
		float d = (lchar - fchar)/1000;
		Log.d("logger","explored for: "+ d +" sec");
		if(status>0){
			SyncDatabase syncDB = new SyncDatabase(getApplicationContext());
			syncDB.onUpSync(0,prgUserData);
		}
		startActivity(intent);
		finish();
	}

}
