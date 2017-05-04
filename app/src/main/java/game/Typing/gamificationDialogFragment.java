package game.Typing;
import java.io.IOException;

import game.Typing.validation.AlternativesParser;
import game.Typing.validation.Validator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class gamificationDialogFragment extends DialogFragment {

	private MediaPlayer player;
	private String TAG="audiofb";
	public Context c;
	//TextSwitcher mSwitcher;
	String typedtext,presentedtext;
	 boolean flag=false;
	 int displayWidth,displayHeight;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        double cpm;//,cpm2;
        int editDist, wordLength;   
       
        cpm = getArguments().getDouble("cpm");
        //cpm2 = getArguments().getDouble("cpm2");
        editDist = getArguments().getInt("editdistance");
        wordLength = getArguments().getInt("wordlength");
        typedtext = getArguments().getString("typedtext");
        presentedtext = getArguments().getString("presentedtext");
        displayWidth = getArguments().getInt("width");
        displayHeight = getArguments().getInt("height");
        
        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        

        //String stars = Validator.getEDToStars(editDist, wordLength);
        int starz = (int) Validator.getEDToStarVal(editDist, wordLength);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        /*// retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = c.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);*/
        
        /*Rect displayRectangle = new Rect();
        displayRectangle.set(0, 0, 1024, 1024);*/

        // Inflate and set the layout for the dialog
        View rl = (View) inflater.inflate(R.layout.gamification_screen1, null);
        rl.setBackgroundColor(getResources().getColor(R.color.blue26));
        rl.setMinimumWidth((int)(displayWidth * 0.99f));
        rl.setMinimumHeight((int)(displayHeight * 0.99f));
        
               
        //rl.setLayoutParams(new LinearLayout());
        //rl.animate();
        //rl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
       /* int width = c.getResources().getDisplayMetrics().widthPixels;
        int height = c.getResources().getDisplayMetrics().heightPixels;
        rl.setLayoutParams(new LinearLayout.LayoutParams(width, height));*/
        
        DamerauLevenshteinAlgorithm ed = new DamerauLevenshteinAlgorithm(1, 1, 1, 1);
        //AlternativesParser alt = new AlternativesParser(c);
        int edist=ed.execute(presentedtext, typedtext);
        
        
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rl)
        // Add action buttons
               .setPositiveButton(R.string.gamification_screen1_ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   
                       // dismiss the dialog box
                	   /*if(player.isPlaying())
                		   player.stop();*/
                	   try{
                		   player.stop();
                	   }catch(Exception ex){
                		   Log.d("debug","Player already stopped");
                	   }
                	   Log.d("debug","Player should be released");
                	   player.release();
                	   gamificationDialogFragment.this.getDialog().dismiss();
                	   //gamificationDialogFragment.this.mListener.onComplete();
                	   //gamificationDialogFragment.this.notify();                	   
                   }
               });
        
        
		
        /*TextView tv1 = (TextView) rl.findViewById(R.id.textView1);//(TextView)inflater.inflate(R.id.textView1, null);
        tv1.setText(stars);*/
        
        TextView tv3 = (TextView) rl.findViewById(R.id.textView3);
        //tv3.setText(getResources().getText(R.string.gamification_screen1_speed) + " : "+ String.format("%.0f", cpm) + " cpm, "+ String.format("%.0f", cpm2) + " cpm2");
        tv3.setText(getResources().getText(R.string.gamification_screen1_speed) + " : "+ String.format("%.0f", cpm) + " cpm ");
        
        TextView tv2 = (TextView) rl.findViewById(R.id.textView2);
        TextView pText = (TextView) rl.findViewById(R.id.presentedText);
        TextView tText = (TextView) rl.findViewById(R.id.typedText);
        //tText.setTextColor(getResources().getColor(R.color.blue25));
        
        ImageView imgv1 = (ImageView) rl.findViewById(R.id.imageView1);
        ImageView imgv2 = (ImageView) rl.findViewById(R.id.imageView2);
        ImageView imgv3 = (ImageView) rl.findViewById(R.id.imageView3);
        ImageView imgv4 = (ImageView) rl.findViewById(R.id.imageView4);
        ImageView imgv5 = (ImageView) rl.findViewById(R.id.imageView5);
        // Get our EditText object.
        //EditText vw = (EditText)rl.findViewById(R.id.richText);
        
        player=new MediaPlayer();
        int fileResrc=0;
		//player.create(getBaseContext(), resrc);
		
        //mSwitcher = (TextSwitcher) rl.findViewById(R.id.textSwitcher);
        
        Log.d("dialog","starz:"+starz);
        
        if(edist > 0 ){
        
        pText.setText(getResources().getString(R.string.word_shown)+" : "+presentedtext);
        tText.setText(getResources().getString(R.string.word_typed)+" : "+typedtext);
        
        }else{
        	
        	pText.setText("");
        	pText.setHeight(0);
            tText.setHeight(0);
            //vw.setHeight(0);
            tText.setText("");
        }


        int tmp = presentedtext.compareTo(typedtext);
        Log.d("kbz","tmp:"+tmp);
        // Create our span sections, and assign a format to each.
    
        switch(starz){   
        
        case 0:
        case 1:
        	tv2.setText(R.string.gamification_screen1_text_1star);
        	imgv1.setVisibility(View.VISIBLE);
        	
        	imgv2.setVisibility(View.INVISIBLE);
        	imgv3.setVisibility(View.INVISIBLE);
        	imgv4.setVisibility(View.INVISIBLE);
        	imgv5.setVisibility(View.INVISIBLE);
        	//imgv2.setAlpha(0);
        	fileResrc = R.raw.one;
        	
        	Log.d("dialog","case 1");
        	
        	break;
        	
        case 2:
        	tv2.setText(R.string.gamification_screen1_text_2star);
        	imgv1.setVisibility(View.VISIBLE);        	
        	imgv2.setVisibility(View.VISIBLE);
        	
        	imgv3.setVisibility(View.INVISIBLE);
        	imgv4.setVisibility(View.INVISIBLE);
        	imgv5.setVisibility(View.INVISIBLE);
        	fileResrc = R.raw.two;
        	Log.d("dialog","case 2");
        	break;
        	
        case 3:
        	tv2.setText(R.string.gamification_screen1_text_3star);
        	imgv1.setVisibility(View.VISIBLE);        	
        	imgv2.setVisibility(View.VISIBLE);
        	imgv3.setVisibility(View.VISIBLE);
        	
        	imgv4.setVisibility(View.INVISIBLE);
        	imgv5.setVisibility(View.INVISIBLE);
        	Log.d("dialog","case 3");
        	fileResrc = R.raw.three;
        	break;
        	
        case 4:
        	tv2.setText(R.string.gamification_screen1_text_4star);
        	imgv1.setVisibility(View.VISIBLE);
        	imgv2.setVisibility(View.VISIBLE);
        	imgv3.setVisibility(View.VISIBLE);        	
        	imgv4.setVisibility(View.VISIBLE);
        	
        	imgv5.setVisibility(View.INVISIBLE);
        	Log.d("dialog","case 4");
        	fileResrc = R.raw.four;
        	break;
        	
        case 5:
        	tv2.setText(R.string.gamification_screen1_text_5star);
        	imgv1.setVisibility(View.VISIBLE);
        	imgv2.setVisibility(View.VISIBLE);
        	imgv3.setVisibility(View.VISIBLE);        	
        	imgv4.setVisibility(View.VISIBLE);        	
        	imgv5.setVisibility(View.VISIBLE);
        	Log.d("dialog","case 5");
        	fileResrc = R.raw.five;
        	break;
        	
        }
        
        try{
		//player.setDataSource("android.resource://"+getPackageName()+"/"+resrc );
		//player.setDataSource(this, Uri.parse("android.resource://"+getPackageName()+"/"+resrc));
		
    	MediaPlayer player = new MediaPlayer(); 
    	player.setDataSource(c, Uri.parse("android.resource://"+c.getPackageName()+"/"+fileResrc));
		//player.setVolume(0.15f, 0.15f);
		player.setVolume(20.0f, 20.0f);
		
		//player.setDataSource(this, Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.center));
		//player.setDataSource("android.resource://"+getPackageName()+"/center");
		//Log.d(TAG, "datasrc set");
		player.prepare();
		//Log.d(TAG, "Player prepared");
		player.start();
		
		//Log.d(TAG, "Player started");
		}catch (IllegalArgumentException e) { Log.d("debug", "error playfile: " + e.getMessage(), e);   } 
		catch (IllegalStateException e) {Log.d("debug", "error in playfile: " + e.getMessage(), e); } 
		catch (IOException e) {Log.d("debug", "error playfile: " + e.getMessage(), e); } 
		catch (Exception e){    Log.d("debug", "error playfile: " + e.getMessage(), e);
		 }
        
        // Create the AlertDialog object and return it
        return builder.create();
    }

	/*public void show(FragmentManager fragmentManager, String string) {
		// TODO Auto-generated method stub
		
	}*/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
	}
	
	/*
	private Handler mHandler = new Handler();
    
      // Create a Runnable Instance
   Runnable r=new Runnable() {
       // Override the run Method
       public void run() {
           // TODO Auto-generated method stub
           try
           {
               // Update the TextSwitcher text
               updateTextSwitcherText();
               
           }
           finally
           {
               mHandler.postDelayed(this, 1000);
           }
       }
   };
// method to Update the TextSwitcher Text
 private void updateTextSwitcherText() 
   {
               
	   if(flag == false){
		   //mSwitcher.setText(typedtext);
		   flag=true;
	   }else{
		   //mSwitcher.setText(presentedtext);
		   flag=false;
	   }
   }*/
   
   /***
    * 
    * @param source
    * @param target
    * @return the index in str1 at which it differs from str2
    */
   
   private int compareStrings(String str1, String str2){
	   
	   str1 = "Monkey";
	   str2 = "Monkey";
	   
	   
	   return 0;
   }
   
}