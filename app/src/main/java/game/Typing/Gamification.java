package game.Typing;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class Gamification extends Activity implements OnClickListener {
	TextView speed; //stars,
	RatingBar rating;
	Button endBtn;
	LinearLayout ll;
	int key,sessiontype;
	int correctCount, withHelpCount;

	double editDistance;
	double[] cpm;
	String[][] mistakes;
	String imename,kbname;

	double prevMaxCpm;
	int crackedScoreCount;
	boolean accurateSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gamification);


	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//stars = (TextView) findViewById(R.id.stars);
		speed =  (TextView) findViewById(R.id.speed);
		rating = (RatingBar) findViewById(R.id.stars);
		endBtn = (Button) findViewById(R.id.btnEnd);
		ll= (LinearLayout) findViewById(R.id.ll_gamification);


		//calculate the stars using errors made and assign it in the below variable
		//float starsVal = 4;
		editDistance = 0;


		endBtn.setOnClickListener(this);


		//collecting variable from previous intent
		//add or delete necessary variables
		Intent it = getIntent();
		Bundle b= it.getExtras();
		key = b.getInt("userid");
		sessiontype = b.getInt("sessiontype");
		cpm = b.getDoubleArray("cpm");
		editDistance = b.getDouble("editdistance");
		kbname= b.getString("kbname", kbname);
		imename=b.getString("imename", imename);

		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		/*ActionBar actionBar = getActionBar();
		actionBar.hide();*/

		//Prizes
		SessionDetailsTable sDB = new SessionDetailsTable(this);

		if(sessiontype !=ApplicationConstants.LTU_SESSION_START)
			prevMaxCpm = sDB.getHighScoreTillSession(key, sessiontype-1);
		else
			prevMaxCpm = 0;
		crackedScoreCount = sDB.getHighScoreCracks(key, sessiontype, prevMaxCpm);

		accurateSession = sDB.isSessionFullyAccurate(key, sessiontype);

		mistakes = sDB.getPresentedTypedPhrases(key, sessiontype);

		String prizeText = "";
		if(crackedScoreCount==1)
			prizeText = "\nYou get "+crackedScoreCount +" prize for beating your previous high score.";
		else if (crackedScoreCount>1)
			prizeText = "\nYou get "+crackedScoreCount +" prizes for beating your previous high score.";
		
		//int totalprizes = crackedScoreCount;

		if(accurateSession == true){
			prizeText = prizeText + "\n"+getResources().getString(R.string.gamification_accuracy_prize);
			//totalprizes = totalprizes +1;
		}

		/*
		if(totalprizes == 1)
			prizeText = "\nYou get "+totalprizes +" prize.";
		else if (totalprizes > 1){
			prizeText = "\nYou get "+totalprizes +" prizes. \n "+crackedScoreCount + " "+getResources().getString(R.string.gamification_cpm_prize);;
			
			if(accurateSession ==true)
				prizeText+="\n 1 "+getResources().getString(R.string.gamification_accuracy_prize);
			
		}
		*/
		if(sessiontype!=ApplicationConstants.FTU_SESSIONNO)
			speed.setText(""+String.format("%.2f", cpm [1]) +prizeText );
		else
			speed.setText(""+String.format("%.2f", cpm [1]));/*+ "characters per minute" +" \n 1* Avg cpm:"+String.format("%.2f", cpm [1]) + "characters per minute" 
				+" \n 2* Avg cpm:"+String.format("%.2f", cpm [2]) + "characters per minute"+" \n 3* Avg cpm:"+String.format("%.2f", cpm [3]) + "characters per minute"
				+" \n 4* Avg cpm:"+String.format("%.2f", cpm [4]) + "characters per minute" +" \n 5* Avg cpm:"+String.format("%.2f", cpm [5]) + "characters per minute");*/

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


		TextView label = new TextView(this);
		label.setTextColor(getResources().getColor(R.color.darkgrey2));
		label.setPadding(0, 20, 0, 0);
		label.setTextSize(16);

		label.setText(getResources().getText(R.string.sessionsummary));

		ll.addView(label,0);
		int index = 1;

		if(sessiontype >=ApplicationConstants.LTU_SESSION_START){
			for(int i=0;i<mistakes.length;i++){

				TextView presentedText = new TextView(this);
				presentedText.setTextColor(getResources().getColor(R.color.darkgrey));
				presentedText.setPadding(0, 10, 0, 0);
				presentedText.setText(mistakes[i][0]);

				TextView typedText = new TextView(this);
				typedText.setTextColor(getResources().getColor(R.color.blue25));
				typedText.setText(mistakes[i][1]);

				ll.addView(presentedText,index);
				ll.addView(typedText,(index+1));		
				index +=2;

			}
		}else if(sessiontype == ApplicationConstants.FTU_SESSIONNO)
		{

			Spannable tempword = new SpannableString(mistakes[18][0]);
			//int maxlength = tempword.length();
			//int maxlength = 9;
			
			LinearLayout llhorizontalx = new LinearLayout(this);
			llhorizontalx.setOrientation(LinearLayout.HORIZONTAL);
			TextView presentedTextx = new TextView(this);
			presentedTextx.setTextColor(getResources().getColor(R.color.black));
			presentedTextx.setPadding(20, 10, 0, 0);
			presentedTextx.setText(getResources().getString(R.string.word_shown));
			presentedTextx.setWidth(180);

			TextView typedTextx = new TextView(this);
			typedTextx.setTextColor(getResources().getColor(R.color.black));
			typedTextx.setText(getResources().getString(R.string.word_typed));

			llhorizontalx.addView(presentedTextx,0);
			llhorizontalx.addView(typedTextx,1);
			//ll.addView(presentedText,index);
			//ll.addView(typedText,(index+1));	
			ll.addView(llhorizontalx,index);
			index +=1;

			for(int i=0;i<mistakes.length;i++){

				/*TextView wordRow = new TextView(this);
				 Spannable word;
				 if(mistakes[i][0].length() == maxlength)
					 word = new SpannableString(mistakes[i][0]);    
				 else
					 word = new SpannableString(mistakes[i][0]+String.format("%" + (maxlength - mistakes[i][0].length()) + "s", "")); 

				 Log.d("padded","--"+mistakes[i][0]+String.format("%" + (maxlength - mistakes[i][0].length()) + "s", "")+"--");

				 word.setSpan(new ForegroundColorSpan(R.color.darkgrey), 0, maxlength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				 wordRow.setText(word);
				 wordRow.setText(" - ");
				 Spannable wordTwo = new SpannableString(mistakes[i][1]);        

				 wordTwo.setSpan(new ForegroundColorSpan(R.color.blue25), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				 wordRow.append(wordTwo);

				 ll.addView(wordRow,index);
				index +=1;
				 */

				LinearLayout llhorizontal = new LinearLayout(this);
				llhorizontal.setOrientation(LinearLayout.HORIZONTAL);
				//llhorizontal.setLayoutParams(new LayoutParams(, height))
				TextView presentedText = new TextView(this);
				presentedText.setTextColor(getResources().getColor(R.color.darkgrey));
				presentedText.setPadding(20, 10, 0, 0);
				presentedText.setText(mistakes[i][0]);
				presentedText.setWidth(180);

				TextView typedText = new TextView(this);
				typedText.setTextColor(getResources().getColor(R.color.blue25));
				typedText.setText(mistakes[i][1]);

				llhorizontal.addView(presentedText,0);
				llhorizontal.addView(typedText,1);
				//ll.addView(presentedText,index);
				//ll.addView(typedText,(index+1));	
				ll.addView(llhorizontal,index);
				index +=1;
				//index +=2;

			}
		}


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
