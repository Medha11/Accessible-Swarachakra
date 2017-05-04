package game.Typing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class SessionFeedback extends FragmentActivity implements OnClickListener {
	RatingBar sessionRating;
	RatingBar stdTaskRating;
	Button resultBtn;
	//String key;//,remark;
	//int correctCount, withHelpCount;
	int userid,sessiontype,width,height;
	double editDistance,cpm,cpm2, editdist;
	double [] cpmArray;
	long sessionStartTS, sessionEndTS;
	Database db;
	SessionDetailsTable sDB;
	String imename,kbname, presentedText,typedText;
	ProgressDialog prgUserData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_feedback);

		
	}
/*	
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
		
		stdTaskRating = (RatingBar)findViewById(R.id.normalRating);
		sessionRating = (RatingBar) findViewById(R.id.sessionRating);
		resultBtn = (Button) findViewById(R.id.resBtn);
		db = new Database(getApplicationContext());
		sDB = new SessionDetailsTable(getApplicationContext());
		//collecting variable from previous intent
		//check if you want all the variables
		Intent it = getIntent();
		Bundle b= it.getExtras();
		userid = b.getInt("userid");
		cpm = b.getDouble("lastcpm");
		//cpm2 = b.getDouble("lastcpm2");
		cpmArray = b.getDoubleArray("cpm");
		editDistance = b.getDouble("editdistance");
		editdist = b.getDouble("editdist");
		sessiontype = b.getInt("sessiontype");
		sessionStartTS = b.getLong("sessionStartTS");
		sessionEndTS = b.getLong("sessionEndTS");
		kbname= b.getString("kbname");
		imename=b.getString("imename");
		presentedText = b.getString("presentedtext");
		typedText = b.getString("typedtext");
		width = b.getInt("width");
		height = b.getInt("height");
		
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
		
		//get the rating from db and put it in the below variable
		int rating =db.getRating(userid);
		if(rating == -1)
		{
			Toast.makeText(this, "Strange: std task rating not found for userid:"+userid, Toast.LENGTH_LONG).show();
			rating = 0;
		}
		//String stdTaskVal = String.valueOf(rating);
		//stdTaskRating.setText(stdTaskVal);
		stdTaskRating.setRating(rating);
		resultBtn.setOnClickListener(this);
		
		Bundle b1 = new Bundle();
		b1.putInt("editdistance", (int)editdist);
		b1.putInt("wordlength", presentedText.length());
		b1.putDouble("cpm",cpm);
		//b1.putDouble("cpm2",cpm2);
		b1.putString("presentedtext",presentedText);
		b1.putString("typedtext",typedText);
		b1.putInt("width", width);
		b1.putInt("height", height);
		//Toast.makeText(this, "star:", Toast.LENGTH_SHORT).show();
		Log.d("debug","ed:"+editdist);
		
		gamificationDialogFragment gdf =new gamificationDialogFragment();
		gdf.setArguments(b1);
		gdf.c = getApplicationContext();
		gdf.show(getSupportFragmentManager(), "feedback");
				
	}
	@Override
	public void onClick(View v) {
		int sessRat = (int) sessionRating.getRating();
		
		
		int status = sDB.updateSessionEntry(userid, sessiontype, sessRat,sessionStartTS, sessionEndTS, SessionDetailsTable.SESSION_STATUS_DONE);
		Log.d("logger:sessionrating",userid +" , "+ sessiontype +" , "+ sessRat +" , "+ SessionDetailsTable.SESSION_STATUS_DONE);
		//Get session details
		
		//sDB.logSession(userid, sessiontype);
		//sDB.logSessionDetails(userid, sessiontype);
		//FileOp
		
		if(status ==1)
		{
			Toast.makeText(this, "LT:Session details updated", Toast.LENGTH_LONG).show();
		}else
			Toast.makeText(this, "LT:Failed to update session rating and session times", Toast.LENGTH_LONG).show();
		
		if(status>0){
			SyncDatabase syncDB = new SyncDatabase(getApplicationContext());
			syncDB.onUpSync(1,prgUserData);
		}
		//update the db with the sessions rating here 		
		//Log.d("kbz","Session FB: cpm-"+cpm + ", session error stars - "+editDistance);
		//jumping to next screen
		Intent intent = new Intent("game.MarathiUT.GAMIFICATION");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bd = new Bundle();
		bd.putInt("userid", userid);
		bd.putInt("sessiontype", sessiontype);
		//bd.putDouble("cpm", cpm);
		bd.putDoubleArray("cpm", cpmArray);
		bd.putDouble("editdistance", editDistance);
		bd.putString("kbname", kbname);
		bd.putString("imename", imename);
		intent.putExtras(bd);
		startActivity(intent);
		
	}	
	
}
