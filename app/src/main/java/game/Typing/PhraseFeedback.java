package game.Typing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class PhraseFeedback extends Activity implements OnClickListener {
	TextView speed; //stars,
	RatingBar rating;
	Button endBtn;
	int key;
	int correctCount, withHelpCount;
	
	double editDistance;
	double[] cpm;
	String imename,kbname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speedometer);

		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//stars = (TextView) findViewById(R.id.stars);
				speed =  (TextView) findViewById(R.id.speed);
				rating = (RatingBar) findViewById(R.id.stars);
				endBtn = (Button) findViewById(R.id.btnEnd);
				
				//calculate the stars using errors made and assign it in the below variable
				//float starsVal = 4;
				editDistance = 0;
				
				View decorView = getWindow().getDecorView();
				// Hide the status bar.
				int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
				decorView.setSystemUiVisibility(uiOptions);
				// Remember that you should never show the action bar if the
				// status bar is hidden, so hide that too if necessary.
				/*ActionBar actionBar = getActionBar();
				actionBar.hide();*/
				
				endBtn.setOnClickListener(this);
				
				
				//collecting variable from previous intent
				//add or delete necessary variables
				Intent it = getIntent();
				Bundle b= it.getExtras();
				key = b.getInt("userid");
				cpm = b.getDoubleArray("cpm");
				editDistance = b.getDouble("editdistance");
				kbname= b.getString("kbname", kbname);
				imename=b.getString("imename", imename);
				speed.setText("Avg cpm:"+String.format("%.2f", cpm [0]) + "characters per minute" +" \n 1* Avg cpm:"+String.format("%.2f", cpm [1]) + "characters per minute" 
				+" \n 2* Avg cpm:"+String.format("%.2f", cpm [2]) + "characters per minute"+" \n 3* Avg cpm:"+String.format("%.2f", cpm [3]) + "characters per minute"
				+" \n 4* Avg cpm:"+String.format("%.2f", cpm [4]) + "characters per minute" +" \n 5* Avg cpm:"+String.format("%.2f", cpm [5]) + "characters per minute");
				
				//Log.d("kbz","FTU: cpm-"+cpm + ", session error stars - "+editDistance);
				/*if( editDistance <= 5){
					starsVal = 5;
				
				}else if(editDistance > 5 && editDistance <= 10){
					starsVal = 4;
				}else if(editDistance > 10 && editDistance <= 15){
					starsVal = 3;
				}else if(editDistance > 15 && editDistance <= 20){
					starsVal = 2;
				}else
					starsVal = 1;*/
				
				
				//stars.setText(starsVal);	
				//rating.setRating(starsVal);
				rating.setRating((float)editDistance);
		
	}

	@Override
	public void onClick(View v) {
		
		//update the db with session completed status. and then next
		
		
		Intent intent = new Intent("game.MarathiUT.SESSION");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bd = new Bundle();
		bd.putInt("userid", key);
		bd.putString("kbname", kbname);
		bd.putString("imename", imename);
		intent.putExtras(bd);
		startActivity(intent);
		
		
		
		
		
	}	
	
	public String getEDToStars(int editDistance, int wordLength){
		String starsVal = "";
		double correctnessRatio = (editDistance *1.0 / wordLength) * 100;
		
		/*if(editDistance ==0)
			correctnessRatio = 100;*/
		
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
}