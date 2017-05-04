package game.Typing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class sessiondetails extends Activity {

	SessionDetailsTable sDB;
	
	GridView gridView;
	ArrayList<String> gridArray = new ArrayList<String>();
	
	public static int itemCount;
	
	public sessiondetails() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_details);
		int userid;
		
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		/*ActionBar actionBar = getActionBar();
		actionBar.hide();*/
		
		Bundle b= getIntent().getExtras();
		userid=b.getInt("userid");
		sDB = new SessionDetailsTable(this);
		
		final GridView grid = (GridView) findViewById(R.id.gridview);
        final ArrayList<String> items = new ArrayList<String>();

       
		/*if(b.containsKey("userid")==true)
			u=b.getInt("User");
		else
			u=0;*/
		//TextView t = (TextView) findViewById(R.id.username);
		//t.setText("Session details for "+userid);
		String [][] trainingDetails = sDB.getDetails(userid, ApplicationConstants.TRAINING_SESSIONTYPE);
		String [][] exploreDetails = sDB.getDetails(userid, sDB.getTotalSessionCount() + 1);
		String [][] ftuDetails = sDB.getDetails(userid, ApplicationConstants.FTU_SESSIONNO);
		
		String [][] ltuDetails = sDB.getLTDetails(userid, ApplicationConstants.LTU_SESSION_START);
		//String [][] sessionDetails = new String[trainingDetails.length+exploreDetails.length+sDB.getTotalSessionCount()][8];
		
		double cpm =0.0, errors=0.0;
		double[] cpmArray;
		int sessionId,index=0; //
		
		//items.add("Type,SId,SNo,Rating,Date,cpm,Status");
		items.add("Type,Rating,Date,CPM,Errors,Status");
		//get training details
		for(int i=0; i<trainingDetails.length;i++){
			
			sessionId=Integer.parseInt(trainingDetails[i][0]); 
			//Log.d("sessionDetails","Sessionid:"+sessionId);
			
			int sessionStatus = Integer.parseInt(trainingDetails[i][5]);
			if(sessionStatus == SessionDetailsTable.SESSION_STATUS_NOTSTARTED )
				continue;
			else if(sessionStatus == SessionDetailsTable.SESSION_STATUS_STARTED )
			{
				//Log.d("sessionDetails","Session status incomplete");
				
				/*if(sDB.isInterrupted(userid,sessionId) == false){
					
					//Log.d("sessionDetails","Session status: invalid");
					continue;
				}*/
				//else{
					//Log.d("sessionDetails","Session status: interrupted");
					trainingDetails[i][5] = "Incmp";
					trainingDetails[i][2] = "NA";
				//}
					
				
			}else if(sessionStatus == SessionDetailsTable.SESSION_STATUS_DONE){
				//Log.d("sessionDetails","Session status: completed");
				trainingDetails[i][5] = "Done";
				cpmArray = sDB.getCPMNew(userid, sessionId,ApplicationConstants.TRAINING_SESSIONTYPE);
				cpm = cpmArray[0];
				errors = sDB.getED(userid, sessionId);
			}
				
				
			long sessionDuration=0;
			String sessionDate="";
			
			if( Long.parseLong(trainingDetails[i][4]) == 0 || Long.parseLong(trainingDetails[i][3]) == 0 )
				sessionDuration = 0;
			else
				sessionDuration = (Long.parseLong(trainingDetails[i][4]) - Long.parseLong(trainingDetails[i][3]))/1000;
			
			if(Long.parseLong(trainingDetails[i][4]) != 0){
				@SuppressWarnings("deprecation")
				long ts = Long.parseLong(trainingDetails[i][4]);
							
				sessionDate =DateFormat.format("dd-MM hh:mma", ts).toString(); //dd-MMMM-yyyy hh:mm a
				//Log.d("sessionDetails","c:"+System.currentTimeMillis()+", past"+ sessionDate);
			}

			Log.d("sessionDetails","Training: "+ trainingDetails[i][0] + "," +trainingDetails[i][1] + "," +trainingDetails[i][2] + "," +sessionDate + "," + sessionDuration + "," +trainingDetails[i][5] + "," + cpm);
			/*sessionDetails[index][0]="Training";
			sessionDetails[index][1]=trainingDetails[i][0];
			sessionDetails[index][2]=trainingDetails[i][1];
			sessionDetails[index][3]=trainingDetails[i][2];
			sessionDetails[index][4]= sessionDate;
			sessionDetails[index][5]= String.valueOf(sessionDuration);
			sessionDetails[index][6]=trainingDetails[i][5];
			sessionDetails[index][7]=String.valueOf(cpm);*/
			//items.add("Tr,"+ trainingDetails[i][0] + "," +trainingDetails[i][1] + "," +trainingDetails[i][2] + "," +sessionDate + "," + sessionDuration + "," +trainingDetails[i][5] + "," + String.format("%.2f",cpm));
			
			//items.add("Tr,"+ trainingDetails[i][0] + "," +trainingDetails[i][1] + "," +trainingDetails[i][2] + "," +sessionDate + "," + String.format("%.2f",cpm) + "," +trainingDetails[i][5] );
			items.add("Tr,"+ trainingDetails[i][2] + "," +sessionDate + "," + String.format("%.2f",cpm) + "," + String.format("%.2f",errors) + "," +trainingDetails[i][5] );
			index++;
		}
		
		//get exploration session details
		for(int i=0; i<exploreDetails.length;i++){
			
			sessionId=Integer.parseInt(exploreDetails[i][0]);
			//Log.d("sessionDetails","Sessionid:"+sessionId);
			
			int sessionStatus = Integer.parseInt(exploreDetails[i][5]);
			
			if(sessionStatus == SessionDetailsTable.SESSION_STATUS_NOTSTARTED )
				continue;
			else if(sessionStatus == SessionDetailsTable.SESSION_STATUS_STARTED)
			{
				//Log.d("sessionDetails","Session status incomplete");
				
				/*if(sDB.isInterrupted(userid, sessionId) == false){
					
					//Log.d("sessionDetails","Session status: invalid");
					continue;
				}
				else{*/
					//Log.d("sessionDetails","Session status: interrupted");
					exploreDetails[i][5] = "Incmp";
					exploreDetails[i][2] = "NA";
				//}
					
				
			}else if(sessionStatus == SessionDetailsTable.SESSION_STATUS_DONE){
				//Log.d("sessionDetails","Session status: completed");
				exploreDetails[i][5] = "Done";
				//cpm = sDB.getCPM(sessionId);
			}
				
			long sessionDuration=0;
			String sessionDate="";
			cpm =0;
			errors=0;
			if( Long.parseLong(exploreDetails[i][4]) == 0 || Long.parseLong(exploreDetails[i][3]) == 0 )
				sessionDuration = 0;
			else
				sessionDuration = (Long.parseLong(exploreDetails[i][4]) - Long.parseLong(exploreDetails[i][3]))/1000;
			
			if(Long.parseLong(exploreDetails[i][4]) != 0){
				@SuppressWarnings("deprecation")
				long ts = Long.parseLong(exploreDetails[i][4]);
							
				sessionDate =DateFormat.format("dd-MM hh:mma", ts).toString();
				//Log.d("sessionDetails","c:"+System.currentTimeMillis()+", past"+ sessionDate);
			}
			
			Log.d("sessionDetails","Self-exploration: "+ exploreDetails[i][0] + "," +exploreDetails[i][1] + "," +exploreDetails[i][2] + "," +sessionDate + "," + sessionDuration + "," +exploreDetails[i][5] + "," + cpm);
			/*sessionDetails[index][0]="Self-exploration";
			sessionDetails[index][1]=exploreDetails[i][0];
			sessionDetails[index][2]=exploreDetails[i][1];
			sessionDetails[index][3]=exploreDetails[i][2];
			sessionDetails[index][4]= sessionDate;
			sessionDetails[index][5]= String.valueOf(sessionDuration);
			sessionDetails[index][6]=exploreDetails[i][5];
			sessionDetails[index][7]=String.valueOf(cpm);*/
			index++;
			//items.add("Xplr,"+ exploreDetails[i][0] + "," +exploreDetails[i][1] + "," +exploreDetails[i][2] + "," +sessionDate + "," + sessionDuration + "," +exploreDetails[i][5] + "," + String.format("%.2f",cpm));
			items.add("Xplr,"+ exploreDetails[i][2] + "," +sessionDate+ "," + String.format("%.2f",cpm)+"," + String.format("%.2f",errors) + "," +exploreDetails[i][5] );
			
		}
		
		//items.add("1 , Hello11 , Hello12");
	    //items.add("2 , Hello21 , Hello22");

		cpm=0;
		errors=0;
		
		//get FTU details
		for(int i=0; i<ftuDetails.length;i++){
			
			sessionId=Integer.parseInt(ftuDetails[i][0]);
			//Log.d("sessionDetails","Sessionid:"+sessionId);
			int sessionStatus = Integer.parseInt(ftuDetails[i][5]);
			
			if(sessionStatus == SessionDetailsTable.SESSION_STATUS_NOTSTARTED )
				continue;
			else if(sessionStatus == SessionDetailsTable.SESSION_STATUS_STARTED)
			{
				//Log.d("sessionDetails","Session status incomplete");
				
				/*if(sDB.isInterrupted(userid, sessionId) == false){
					
					//Log.d("sessionDetails","Session status: invalid");
					continue;
				}
				else{*/
					//Log.d("sessionDetails","Session status: interrupted");
					ftuDetails[i][5] = "Incmp";
					ftuDetails[i][2] = "NA";
				//}
					
				
			}else if(sessionStatus == SessionDetailsTable.SESSION_STATUS_DONE){
				//Log.d("sessionDetails","Session status: completed");
				ftuDetails[i][5] = "Done";
				cpmArray = sDB.getCPMNew(userid, sessionId,ApplicationConstants.FTU_SESSIONNO);
				cpm = cpmArray[0];
				errors = sDB.getED(userid, sessionId);
			}
				
			long sessionDuration=0;
			String sessionDate="";
			
			if( Long.parseLong(ftuDetails[i][4]) == 0 || Long.parseLong(ftuDetails[i][3]) == 0 )
				sessionDuration = 0;
			else
				sessionDuration = (Long.parseLong(ftuDetails[i][4]) - Long.parseLong(ftuDetails[i][3]))/1000;
			
			if(Long.parseLong(ftuDetails[i][4]) != 0){
				@SuppressWarnings("deprecation")
				long ts = Long.parseLong(ftuDetails[i][4]);
							
				sessionDate =DateFormat.format("dd-MM hh:mma", ts).toString();
				//Log.d("sessionDetails","c:"+System.currentTimeMillis()+", past"+ sessionDate);
			}

			Log.d("sessionDetails","FTU: "+ ftuDetails[i][0] + "," +ftuDetails[i][1] + "," +ftuDetails[i][2] + "," +sessionDate + "," + sessionDuration + "," +ftuDetails[i][5] + "," + cpm);
			/*sessionDetails[index][0]="FTU";
			sessionDetails[index][1]=ftuDetails[i][0];
			sessionDetails[index][2]=ftuDetails[i][1];
			sessionDetails[index][3]=ftuDetails[i][2];
			sessionDetails[index][4]= sessionDate;
			sessionDetails[index][5]= String.valueOf(sessionDuration);
			sessionDetails[index][6]=ftuDetails[i][5];
			sessionDetails[index][7]=String.valueOf(cpm);*/
			//items.add("Tr,"+ ftuDetails[i][0] + "," +ftuDetails[i][1] + "," +ftuDetails[i][2] + "," +sessionDate + "," + sessionDuration + "," +ftuDetails[i][5] + "," + String.format("%.2f",cpm));
			items.add("FTU,"+ ftuDetails[i][2] + "," +sessionDate + "," + String.format("%.2f",cpm) + "," + String.format("%.2f",errors) + "," +ftuDetails[i][5] );
			index++;
		}
		
		cpm=0;
		errors=0;
		
		//get LT details
		for(int i=0; i<ltuDetails.length;i++){
			
			sessionId=Integer.parseInt(ltuDetails[i][0]);
			//Log.d("sessionDetails","Sessionid:"+sessionId);
			int sessionStatus = Integer.parseInt(ltuDetails[i][5]);
			
			if(sessionStatus == SessionDetailsTable.SESSION_STATUS_NOTSTARTED )
				continue;
			else if(sessionStatus == SessionDetailsTable.SESSION_STATUS_STARTED)
			{
				//Log.d("sessionDetails","Session status incomplete");
				
				/*if(sDB.isLTInterrupted(userid, sessionId) == false){
					
					//Log.d("sessionDetails","Session status: invalid");
					continue;
				}*/
				//else{
					//Log.d("sessionDetails","Session status: interrupted");
					ltuDetails[i][5] = "Incmp";
					ltuDetails[i][2] = "NA";
				//}
					
				
			}else if(sessionStatus == SessionDetailsTable.SESSION_STATUS_DONE){
				//Log.d("sessionDetails","Session status: completed");
				ltuDetails[i][5] = "Done";
				cpmArray = sDB.getCPMNew(userid, sessionId,sessionId);
				cpm = cpmArray[0];
				errors = sDB.getED(userid, sessionId);
			}
				
			long sessionDuration=0;
			String sessionDate="";
			
			if( Long.parseLong(ltuDetails[i][4]) == 0 || Long.parseLong(ltuDetails[i][3]) == 0 )
				sessionDuration = 0;
			else
				sessionDuration = (Long.parseLong(ltuDetails[i][4]) - Long.parseLong(ltuDetails[i][3]))/1000;
			
			if(Long.parseLong(ltuDetails[i][4]) != 0){
				@SuppressWarnings("deprecation")
				long ts = Long.parseLong(ltuDetails[i][4]);
							
				sessionDate =DateFormat.format("dd-MM hh:mma", ts).toString();
				//Log.d("sessionDetails","c:"+System.currentTimeMillis()+", past"+ sessionDate);
			}

			Log.d("sessionDetails","LT: "+ ltuDetails[i][0] + "," +ltuDetails[i][1] + "," +ltuDetails[i][2] + "," +sessionDate + "," + sessionDuration + "," +ltuDetails[i][5] + "," + cpm);
			/*sessionDetails[index][0]="LT";
			sessionDetails[index][1]=ltuDetails[i][0];
			sessionDetails[index][2]=ltuDetails[i][1];
			sessionDetails[index][3]=ltuDetails[i][2];
			sessionDetails[index][4]= sessionDate;
			sessionDetails[index][5]= String.valueOf(sessionDuration);
			sessionDetails[index][6]=ltuDetails[i][5];
			sessionDetails[index][7]=String.valueOf(cpm);*/
			//items.add("Tr,"+ ltuDetails[i][0] + "," +ltuDetails[i][1] + "," +ltuDetails[i][2] + "," +sessionDate + "," + sessionDuration + "," +ltuDetails[i][5] + "," + String.format("%.2f",cpm));
			items.add("LT,"+ltuDetails[i][2] + "," +sessionDate + "," + String.format("%.2f",cpm)+ "," + String.format("%.2f",errors) + "," +ltuDetails[i][5] );
			index++;
		}
		itemCount = 6;
		
	    grid.setAdapter(new GridAdapter(items));
		
		/*for(int i=1 ; i<randomizer.total_sessions;i++){
			
			
			
			
		}*/
	}
	
	private static final class GridAdapter extends BaseAdapter {

        final ArrayList<String> mItems;
        final int mCount;

        /**
         * Default constructor
         * @param items to fill data to
         */
        private GridAdapter(final ArrayList<String> items) {

            mCount = items.size() * itemCount;
            mItems = new ArrayList<String>(mCount);

            Log.d("kbz","mCount: "+mCount);
            // for small size of items it's ok to do it here, sync way
            for (String item : items) {
                // get separate string parts, divided by ,
                final String[] parts = item.split(",");

                // remove spaces from parts
                for (String part : parts) {
                    part.replace(" ", "");
                    mItems.add(part);
                }
            }
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(final int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {

            TextView view = (TextView)convertView;

            if (view == null) {
                //view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            	view = new TextView(parent.getContext());
            	view.setLayoutParams(new GridView.LayoutParams(70, 40));
            	view.setTextSize(11);
            	view.setPadding(1, 1, 1, 1);
            }

            /*final TextView text = (TextView) view.findViewById(android.R.id.text1);

            text.setText(mItems.get(position));*/
            view.setText(mItems.get(position));

            return view;
        }

		/*@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}*/
    }

}
