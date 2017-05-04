package game.Typing;

import game.Typing.Parsers.xmlParsers;

import java.util.HashMap;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.Type;

public class Session extends Activity implements OnClickListener{

	Button Training;
	Button FTU;
	Button Longitudinal, Explore;
	TextView Details;
	TextView SessionNo;
	Database DB = new Database(this);
	SessionDetailsTable sDB = new SessionDetailsTable(this);

	int trainingsDone,FTUdone, LTUDone,XploreDone;
	double ExploreTime;
	int userid,continueFromT,continueFromFTU,continueFromLTU,sessionId,sessionType;//,remark;
	String kbname,imename;
	boolean trainingIncmp,FTUIncmp,LTUIncmp;
	String[] details;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session);

	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Training = (Button) findViewById(R.id.button1);
		FTU = (Button) findViewById(R.id.button2);
		Longitudinal = (Button) findViewById(R.id.button3);
		Explore = (Button) findViewById(R.id.explore);

		Details = (TextView) findViewById(R.id.textView1);
		//SessionNo = (TextView) findViewById(R.id.textView2);

		Training.setOnClickListener(this);
		FTU.setOnClickListener(this);
		Longitudinal.setOnClickListener(this);
		Explore.setOnClickListener(this);
		
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		/*ActionBar actionBar = getActionBar();
		actionBar.hide();*/

		Intent it = getIntent();
		Bundle b= it.getExtras();
		userid = b.getInt("userid");
		kbname = b.getString("kbname");
		imename = b.getString("imename");
		Log.d("kbz","read ime:"+imename);
		
		continueFromT = 1;
		continueFromFTU = 1;
		continueFromLTU = 1;
		
		sessionId = 0;

		/*int success0=0;
		int sucess1=0;*/

		/*if(b.containsKey("remark"))
			remark = b.getString("remark");
		else
			remark="";

		if(b.containsKey("successCount"))
			success0 = b.getInt("successCount");

		if(b.containsKey("withHelpCount"))
			sucess1 = b.getInt("withHelpCount");*/

		//chk for interrupted training session
		//get training sessions with status 'not complete'
		/*sDB.logSession(userid, SessionDetailsTable.TRAINING_SESSIONTYPE);
		sDB.logSessionDetails(userid, SessionDetailsTable.TRAINING_SESSIONTYPE);*/
		
		
		trainingsDone = sDB.countTrainings(userid);
		FTUdone = sDB.ftuDone(userid);
		LTUDone = sDB.ltuDone(userid);
		XploreDone = sDB.getExplorationCount(userid);
		ExploreTime = sDB.getExplorationTime(userid);
		
		
		int[] sessionIdList = sDB.getIncompleteSessions(userid, ApplicationConstants.TRAINING_SESSIONTYPE);
		
		if(sessionIdList !=null){ //should not happen
			
			if(sessionIdList.length>1)
				FileOperations.write("Illegal state: More than one training session with status 'incomplete'");
			else
				FileOperations.write("One training session with status 'incomplete'");
			//chk
			for(int i=0 ; i<sessionIdList.length ; i++){
				FileOperations.write("For user-"+ userid+" incomplete training session id: "+sessionIdList[i]);

				//check if its an interrupted session, or one yet to be begun
				//if(sDB.isInterrupted(userid, sessionIdList[i])==true){
					
					trainingIncmp = true;
					//get last phrase attempted
					continueFromT =sDB.getLastSuccessfulPhrase(userid, sessionIdList[i]);
					
					if(continueFromT == -1){
						
						continueFromT = 1;
						FileOperations.write("Illegal state: Could not get continueFrom for Training from db for sessionId:" + sessionIdList[i]);
						//sDB.updateTrainingEntry(userid, sessionIdList[i], 0, 0, 0, SessionDetailsTable.SESSION_STATUS_NOTSTARTED); //reset session not yet started
						
					}else if(continueFromT == 1){
						
						FileOperations.write("Handle state: Session started, but interrupted before first phrase was completed.");
						sDB.updateTrainingEntry(userid, sessionIdList[i], 0, 0, 0, SessionDetailsTable.SESSION_STATUS_NOTSTARTED); //reset session not yet started
						
					}
					sessionId = sessionIdList[i];
					//break;
					
				//}
			}
			if(continueFromT ==1)
				trainingIncmp =false;

		}else{
			trainingIncmp = false;
		}

		//chk for interrupted FTU session
		sessionIdList = sDB.getIncompleteSessions(userid, ApplicationConstants.FTU_SESSIONNO);

		if(sessionIdList !=null){

			if(sessionIdList.length>1){
				FileOperations.write("Illegal state: More than one FTU session with status 'incomplete'");
				Log.d("debug","Illegal state: More than one FTU session with status 'incomplete'");
			}
			//chk
			for(int i=0 ; i<sessionIdList.length ; i++){

				//if(sDB.isInterrupted(userid, sessionIdList[i])==true ){
					FTUIncmp = true;
					continueFromFTU =sDB.getLastSuccessfulPhrase(userid, sessionIdList[i]);
					
					if(continueFromFTU == -1){
						
						continueFromFTU = 1;
						FileOperations.write("Illegal state: Could not get continueFrom entry for FTU from db for sessionId:" + sessionIdList[i]);
						Log.d("debug","Illegal state: Could not get continueFrom entry for FTU from db for sessionId:" + sessionIdList[i]);
						//sDB.updateSessionEntry(userid, SessionDetailsTable.FTU_SESSIONNO, 0, 0, 0, SessionDetailsTable.SESSION_STATUS_NOTSTARTED);
					}else if(continueFromFTU == 1){
						
						FileOperations.write("Handle state:FTU Session started, but interrupted before first phrase was completed.");
						Log.d("debug","Handle state:FTU Session started, but interrupted before first phrase was completed.");
						sDB.updateSessionEntry(userid, ApplicationConstants.FTU_SESSIONNO, 0, 0, 0, SessionDetailsTable.SESSION_STATUS_NOTSTARTED);
						
					}
					sessionId = sessionIdList[i];
					//break;
				//}
					
			}
			if(continueFromFTU ==1)
				FTUIncmp =false;

		}else{
			FTUIncmp = false;
			Log.d("debug","No incomplete session found");
		}

		//chk for interrupted LT session
		int[] sessionTypeList = sDB.getIncompleteLTSession(userid, ApplicationConstants.LTU_SESSION_START); //here session numbers are returned, not session-id

		if(sessionTypeList !=null){

			/*if(sessionIdList.length>1)
				FileOperations.write("Illegal state: More than one LTU session with status 'incomplete'");*/
			//chk
			for(int i=0 ; i<sessionTypeList.length ; i++){

				//if(sDB.isLTInterrupted(userid, sessionIdList[i])==true ){
					LTUIncmp = true;
					continueFromLTU =sDB.getLTLastSuccessfulPhrase(userid, sessionTypeList[i]);
					sessionType = sessionTypeList[i];
					
					if(continueFromLTU == -1){
						continueFromLTU = 1;
						FileOperations.write("Illegal state: Could not get continueFrom entry for LTU last from db for sessionId:" + sessionTypeList[i]);
						//sDB.updateSessionEntry(userid, sessionIdList[i], 0, 0, 0, SessionDetailsTable.SESSION_STATUS_NOTSTARTED);
					}else if(continueFromLTU == 1){
						
						FileOperations.write("Handle state:LTU Session started, but interrupted before first phrase was completed.");
						sDB.updateSessionEntry(userid, sessionTypeList[i], 0, 0, 0, SessionDetailsTable.SESSION_STATUS_NOTSTARTED);
					}
					
					Log.d("kbz","continuefrom: "+ continueFromLTU);
					//break;
				//}
			}
			if(continueFromLTU ==1)
				LTUIncmp =false;

		}else{
			LTUIncmp = false;
			//get next session to do
			sessionType = sDB.getNextLTSession(userid);
			
		}

		FileOperations.write("Session to be started: "+sessionType);

		//DB.open();
		//Details.setText("Hi "+DB.getuserDetails(key)+"\n"+remark+ "\n Without help successes" + success0 + ", with help: "+sucess1);
		String name = DB.getuserDetails(userid);
		//Details.setText("Participant: "+name);


		if(name.length()<1){
			Toast t = Toast.makeText(this, "Strangely, user name for userid " + userid + " not found.", Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();

		}
		
		
		/*trainingsDone = sDB.countTrainings(userid);
		FTUdone = sDB.ftuDone(userid);
		LTUDone = sDB.ltuDone(userid);
		XploreDone = sDB.getExplorationCount(userid);
		ExploreTime = sDB.getExplorationTime(userid);
		
		*/
		double highScore ;
		String highScoreText="";
		if( LTUDone >=1 && sessionType >ApplicationConstants.LTU_SESSION_START)
		{
			highScore = sDB.getHighScoreTillSession(userid, sessionType - 1);
			
			if(highScore>0)
				highScoreText = "\n Max cpm: "+String.format("%.0f", highScore);
			
		}
		
		
		Details.setText("Participant: "+name+"\nTrained "+trainingsDone + " times" + "\n" + LTUDone + " LTU sessions completed" + "\n" + XploreDone + " Self-Explore sessions: "+String.format("%.3f",ExploreTime) + "mins in total" + "\n\n" + kbname +highScoreText );
		//sno=DB.getSession(key);
		//sno="0";
		//DB.close();

		//SessionNo.setText(sno);
		int totalLTUSessionCount = sDB.getTotalSessionCount() - 1;
		
		if(trainingsDone >= ApplicationConstants.MAX_TRAININGS){

//			Training.setEnabled(false);
			//Log.d("kbz","max trainings reached");

		}else{
			if( LTUDone < (totalLTUSessionCount) ){
				//Log.d("kbz","All LTUs yet to be done");
				Training.setEnabled(true); 
			}
			else{
//				Training.setEnabled(false); //Disable if all LT sessions completed.
				//Log.d("kbz","All LTUs done");
			}
		}

		if(FTUdone == 0 && trainingsDone >= 1 ){
			//Log.d("kbz","FTU done, atleast one training done");
			FTU.setEnabled(true);
		}
		else{
//			FTU.setEnabled(false);
			///Log.d("kbz","either FTU already done, or no training done");
		}

		if(FTUdone == 1 && trainingsDone >= 1 && LTUDone < (totalLTUSessionCount)){
			Longitudinal.setEnabled(true);
			Log.d("kbz","LT ");
		}
		else{
//			Longitudinal.setEnabled(false);
			Log.d("kbz","LT2");
		}

		if(trainingsDone > 0){
			Explore.setEnabled(true);
		}
		//Longitudinal.setEnabled(true); //comment
		
		Log.d("kbz","trainingIncmp|FTUIncmp|LTUIncmp : "+trainingIncmp +"|"+FTUIncmp +"|" +LTUIncmp);
		FileOperations.write("trainingIncmp|FTUIncmp|LTUIncmp : "+trainingIncmp +"|"+FTUIncmp +"|" +LTUIncmp);
		//override above 
		int countFlagsSet=0;
		if(trainingIncmp ==true ){
			Log.d("kbz","Train ing incmp");
			FileOperations.write("Train ing incmp");
			Training.setEnabled(true);
//			FTU.setEnabled(false);
//			Longitudinal.setEnabled(false);
//			Explore.setEnabled(false);
			countFlagsSet ++;
		}else if(FTUIncmp ==true ){
			Log.d("kbz","FTU incmp");
			FileOperations.write("FTU incmp");
//			Training.setEnabled(false);
			FTU.setEnabled(true);
//			Longitudinal.setEnabled(false);
//			Explore.setEnabled(false);
			countFlagsSet ++;
			
		}else if(LTUIncmp ==true){
			Log.d("kbz","LTU incmp");
			FileOperations.write("LTU incmp");
//			Training.setEnabled(false);
//			FTU.setEnabled(false);
			Longitudinal.setEnabled(true);
//			Explore.setEnabled(false);
			countFlagsSet ++;
			
		}
		if(countFlagsSet >1){
			FileOperations.write("Illegal state: More than one incomplete session. TrainingIncmp:" +trainingIncmp+",FTUIncmp:"+FTUIncmp+", LTUIncmp:"+LTUIncmp);
			Toast t= Toast.makeText(this, "Illegal state: More than one incomplete session. TrainingIncmp:" +trainingIncmp+",FTUIncmp:"+FTUIncmp+", LTUIncmp:"+LTUIncmp,Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
			finish();
			return;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		Bundle bundle = new Bundle();

//		String currentDefaultKB = Settings.Secure.getString(
//				getContentResolver(),
//				Settings.Secure.DEFAULT_INPUT_METHOD
//				);

		//currentDefaultKB = currentDefaultKB.replaceAll("/", "").trim();
		//imename = imename.replaceAll("/", "");
		//String tmp = imename;
		//imename = "com.klye.ime.latin/.LatinIME";

		//Log.d("kbz","curr default:'"+currentDefaultKB + "', expected: '"+imename+"/'" +tmp+" = "+currentDefaultKB.equalsIgnoreCase(tmp) +currentDefaultKB.contains(imename)+imename.compareTo(currentDefaultKB));
//		if(currentDefaultKB.equalsIgnoreCase(imename.trim()) == false)
//		{
//			Toast t = Toast.makeText(this, "Default keyboard should be "+kbname, Toast.LENGTH_LONG);
//			t.setGravity(Gravity.CENTER, 0, 0);
//			t.show();
//			Log.d("kbz","currentKB:--" + currentDefaultKB + "--\nexpected kb:--"+imename+"--");
//			InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showInputMethodPicker();
//			return;
//		}

		switch(v.getId()){

		case R.id.button1:

			Intent intent_training = new Intent("game.MarathiUT.TRAINING");
			bundle.putInt("userid", userid);
			bundle.putInt("sessiontype", ApplicationConstants.TRAINING_SESSIONTYPE );
			bundle.putString("kbname", kbname);
			bundle.putString("imename", imename);
			bundle.putInt("continueFrom", continueFromT);
			bundle.putInt("sessionid", sessionId);  // has a meaningful value only if there's a interrupted session
			intent_training.putExtras(bundle);
			startActivity(intent_training);
			break;

		case R.id.button2:	

			Intent intent_FTU = new Intent("game.MarathiUT.FTU");
			bundle.putInt("userid", userid);
			bundle.putInt("sessiontype", ApplicationConstants.FTU_SESSIONNO);
			bundle.putString("kbname", kbname);
			bundle.putString("imename", imename);
			bundle.putInt("continueFrom", continueFromFTU);
			intent_FTU.putExtras(bundle);
			startActivity(intent_FTU);
			break;

		case R.id.button3:

			Intent intent_Longitudinal = new Intent("game.MarathiUT.LONGITUDINAL");
			bundle.putInt("userid", userid);
			//bundle.putInt("sessiontype",(LTUDone + 1));
			bundle.putInt("sessiontype",sessionType);
			bundle.putString("kbname", kbname);
			bundle.putString("imename", imename);
			bundle.putInt("continueFrom", continueFromLTU);
			intent_Longitudinal.putExtras(bundle);
			startActivity(intent_Longitudinal);
			break;
		case R.id.explore:

			Intent intent_explore = new Intent("game.MarathiUT.EXPLORE");
			bundle.putInt("userid", userid);
			bundle.putInt("sessiontype",sDB.getTotalSessionCount() +1);  //(FTUdone + 1)
			bundle.putString("kbname", kbname);
			bundle.putString("imename", imename);
			intent_explore.putExtras(bundle);
			startActivity(intent_explore);
			break;
		}

	}
}