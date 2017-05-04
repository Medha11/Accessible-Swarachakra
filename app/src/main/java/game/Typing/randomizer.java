package game.Typing;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.util.Log;

public class randomizer {

	private static int easy_max , easy=0;
	private static int medium_max , medium = 0 ;
	private static int hard_max , hard = 0;
	//private static int spSessions = 5;
	public static int total_sessions = 0;
	//private static int phrasesPerSession = 10;
	private static int trainingPhraseCount;
	private static int FTUPhraseCount;
	
	private static ArrayList<Integer> phraseList;
	private static String logtext;
	private static SessionDetailsTable mysdb;
	private static ArrayList<String> randomizedEntries;
	private static ArrayList<String> trainingSessions;
	private static ArrayList<String> FTUSessions;
	
	
	public randomizer(Context c) {
		// TODO Auto-generated constructor stub
		mysdb = new SessionDetailsTable(c);
		
		
		//Log.d("sDebug:randomizer",mysdb.ourDatabase.getPath());
		phraseList = new ArrayList<Integer>();
		randomizedEntries = new ArrayList<String>();
		trainingSessions = new ArrayList<String>();
		FTUSessions = new ArrayList<String>();
		
		//init phrase details
		easy_max = mysdb.getPhraseCount(SessionDetailsTable.PHRASE_CATEGORY_SENTENCES, SessionDetailsTable.PHRASE_LEVEL_EASY);
		//easy=0;
		medium_max =  mysdb.getPhraseCount(SessionDetailsTable.PHRASE_CATEGORY_SENTENCES, SessionDetailsTable.PHRASE_LEVEL_MEDIUM);
		//medium = 0 ;
		hard_max = mysdb.getPhraseCount(SessionDetailsTable.PHRASE_CATEGORY_SENTENCES, SessionDetailsTable.PHRASE_LEVEL_DIFFICULT);
		//hard = 0;
		
		trainingPhraseCount = mysdb.getPhraseCount(SessionDetailsTable.PHRASE_CATEGORY_WORDS, SessionDetailsTable.PHRASE_LEVEL_PRACTICE);
		FTUPhraseCount = mysdb.getPhraseCount(SessionDetailsTable.PHRASE_CATEGORY_WORDS, SessionDetailsTable.PHRASE_LEVEL_MAIN);
		
		Log.d("xml" ,"xml values extracted: easymax="+easy_max +",mediummax:"+medium_max + ", difficultmax: "+hard_max);
		Log.d("xml" ,"xml values extracted: trainingWordCount="+trainingPhraseCount +", ftu word count: " + FTUPhraseCount);
		
		/*//private static int spSessions = 5;
		public static int total_sessions = 11;
		private static int phrasesPerSession = 10;
		private static int trainingPhraseCount = 8;
		private static int FTUPhraseCount = 20;*/
		//total_sessions = ((easy_max + medium_max + hard_max)/10)+1;
		total_sessions = mysdb.getTotalSessionCount();
		//SessionDetailsTable.TOTAL_SESSIONS = total_sessions;
		
	}

	public static ArrayList<String> generateTrainingSequence(long userID){
		
		int phraseid = ApplicationConstants.TRAINING_PHRASE_XMLID_START;
		
		for(int trainingPhraseNo = 1; trainingPhraseNo <= trainingPhraseCount; trainingPhraseNo ++ ){
			trainingSessions.add(userID + ";" + ApplicationConstants.TRAINING_SESSIONTYPE + ";" + trainingPhraseNo + ";" + phraseid);
			Log.d("logger:training", userID + ";" + ApplicationConstants.TRAINING_SESSIONTYPE + ";" + trainingPhraseNo + ";" + phraseid);
			phraseid++;
		}
		return trainingSessions;
	}
	
	public static ArrayList<String> generateFTUSequence(long userID){
		
		int phraseid = ApplicationConstants.FTU_PHRASE_XMLID_START;
		FTUSessions.clear();
		
		for(int ftuPhraseNo = 1; ftuPhraseNo <= FTUPhraseCount; ftuPhraseNo ++ ){
			
			int c=ApplicationConstants.FTU_WORD_REPEAT_COUNT;
			
			while(c>0){
				FTUSessions.add(userID + ";" + ApplicationConstants.FTU_SESSIONNO + ";" + ftuPhraseNo + ";" + phraseid +";"+c);
				c--;
			}
			//Log.d("logger:ftu", userID + "; sno: " + SessionDetailsTable.FTU_SESSIONNO + "; phno:" + ftuPhraseNo + ";phid:" + phraseid);
			phraseid++;
		}
		return FTUSessions;
		
	}
	
	public static ArrayList<String> generatePhraseSequence(long userID){
		
		//Log.d("dbQuery","DB to be"+mysdb);
		
		ArrayList<Integer> easyDone =new ArrayList<Integer>();
		ArrayList<Integer> mediumDone =new ArrayList<Integer>();
		ArrayList<Integer> hardDone =new ArrayList<Integer>();
		
		int randomPhraseId=0;
		int phraseNumber = 0;
		total_sessions = mysdb.getTotalSessionCount();
		
		for(int sessionNo = 2; sessionNo <= total_sessions ; sessionNo++){
			
			switch(sessionNo){
			
			case 2:
				
				easy=8;
				medium = 0;
				hard = 0;
				
				for(int j=1;j<=easy;j++){
					
					randomPhraseId = randInt( 1, easy_max);

					
					while(easyDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, easy_max);
						
					}
					easyDone.add(randomPhraseId);
					phraseList.add(randomPhraseId);
					//Log.d("logger","\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+j+", easy phrase-no: "+ temp4compare + " , phrase-id: " + temp4compare);						
					//logtext+= "\nUserID: " + userID + ", Session: "+ i + ", phrase number: "+j+", easy phrase-no: "+ temp4compare + " , phrase-id: " + temp4compare;
					logtext+= "\nUserID: " + userID + ", Session: "+ sessionNo + ", phrase number: "+ j + " , phrase-id: " + randomPhraseId;
					
					randomizedEntries.add(userID + ";" + sessionNo + ";" + j +";"+ randomPhraseId + "; 1"); //only one attempt in LTU
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId + "; 1");
					/*if(mysdb.insertUser((int)userID, i, j, temp4compare)!= -1){
						Log.d("logger","insert succ.");
					}else
						Log.d("logger","insert failed");*/
						
					
				}
				
				break;
			
			case 3:
				//Log.d("logger","case "+i);
				
				easy=6;
				medium = 2;
				hard = 0;
				phraseNumber = 1;
				
				for(int j=1;j<=easy;j++){
					
					randomPhraseId = randInt( 1, easy_max);
					
					while(easyDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, easy_max);
						
					}
					easyDone.add(randomPhraseId);
					phraseList.add(randomPhraseId);
					//Log.d("logger","\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", easy phrase-no: "+ temp4compare + " , phrase-id: " + temp4compare );		
					//logtext+="\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", easy phrase-no: "+ temp4compare + " , phrase-id: " + temp4compare;
					logtext+="\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ phraseNumber + " , phrase-id: " + randomPhraseId;
					randomizedEntries.add(userID + ";" + sessionNo + ";" + phraseNumber +";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId + "; 1");
					
					/*if(mysdb.insertUser((int)userID, i, j, temp4compare)!= -1){
						Log.d("logger","insert succ.");
					}else
						Log.d("logger","insert failed");*/
					phraseNumber++;
					
				}
				
				for(int j=1;j<=medium;j++){
					
					randomPhraseId = randInt( 1, medium_max);
					
					while(mediumDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, medium_max);
						
					}
					mediumDone.add(randomPhraseId);
					phraseList.add(randomPhraseId+ easy_max);
					//Log.d("logger","\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", medium phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + easy_max  ));	
					//logtext+= "\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", medium phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + easy_max  ); 
					logtext+= "\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ phraseNumber +" , phrase-id: " + (randomPhraseId + easy_max  ); 
					
					randomPhraseId+= easy_max;
					randomizedEntries.add(userID + ";" + sessionNo + ";" + phraseNumber+";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId + "; 1");
					/*if(mysdb.insertUser((int)userID, i, phraseNumber, (temp4compare+ easy_max) )!= -1){
						Log.d("logger","insert succ.");
					}else
						Log.d("logger","insert failed");*/
					phraseNumber++;
					
				}
				
				break;
				
			case 4:
				//Log.d("logger","case "+i);
				
				easy=4;
				medium = 4;
				hard = 0;
				
				phraseNumber = 1;
				
				for(int j=1;j<=easy;j++){
					
					randomPhraseId = randInt( 1, easy_max);
					
					while(easyDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, easy_max);
						
					}
					easyDone.add(randomPhraseId);
					phraseList.add(randomPhraseId);
					//Log.d("logger","UserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", easy phrase-no: "+ temp4compare + " , phrase-id: " + temp4compare );		
					
					//logtext+= "\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", easy phrase-no: "+ temp4compare + " , phrase-id: " + temp4compare ;
					logtext+= "\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ phraseNumber + " , phrase-id: " + randomPhraseId ;
					
					/*if(mysdb.insertUser((int)userID, i, phraseNumber, (temp4compare) )!= -1){
						Log.d("logger","insert succ.");
					}else
						Log.d("logger","insert failed");*/
					

					randomizedEntries.add(userID + ";" + sessionNo + ";" + phraseNumber+";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId + "; 1");
					
					phraseNumber++;
					
				}
				
				for(int j=1;j<=medium;j++){
					
					randomPhraseId = randInt( 1, medium_max);
					
					while(mediumDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, medium_max);
						
					}
					mediumDone.add(randomPhraseId);
					phraseList.add(randomPhraseId+ easy_max);
					//Log.d("logger","UserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", medium phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + easy_max ));
					//logtext+= "\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", medium phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + easy_max ) ;
					logtext+= "\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ phraseNumber +" , phrase-id: " + (randomPhraseId + easy_max ) ;

					/*if(mysdb.insertUser((int)userID, i, phraseNumber, (temp4compare+ easy_max) )!= -1){
						Log.d("logger","insert succ.");
					}else
						Log.d("logger","insert failed");*/
					
					randomPhraseId+= easy_max;
					randomizedEntries.add(userID + ";" + sessionNo + ";" + phraseNumber+";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId + "; 1");
					
					phraseNumber++;
					
				}
				
				break;
				
			case 5:
				//Log.d("logger","case "+i);
				
				easy = 3;
				medium = 3;
				hard = 2;
				
				phraseNumber = 1;
				
				for(int j=1;j<=easy;j++){
					
					randomPhraseId = randInt( 1, easy_max);
					
					while(easyDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, easy_max);
						
					}
					easyDone.add(randomPhraseId);
					phraseList.add(randomPhraseId);
					//Log.d("logger","UserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", easy phrase-no: "+ temp4compare + " , phrase-id: " + temp4compare );		
					//logtext+= "\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", easy phrase-no: "+ temp4compare + " , phrase-id: " + temp4compare;
					logtext+= "\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ phraseNumber + " , phrase-id: " + randomPhraseId;


					randomizedEntries.add(userID + ";" + sessionNo + ";" + phraseNumber+";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId + "; 1");
					
					phraseNumber++;
					
				}
				
				for(int j=1;j<=medium;j++){
					
					randomPhraseId = randInt( 1, medium_max);
					
					while(mediumDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, medium_max);
						
					}
					mediumDone.add(randomPhraseId);
					phraseList.add(randomPhraseId+ easy_max);
					//Log.d("logger","UserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", medium phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + easy_max ));	
					//logtext+= "\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", medium phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + easy_max );
					logtext+= "\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ phraseNumber +" , phrase-id: " + (randomPhraseId + easy_max );

		
					randomPhraseId+= easy_max;
					randomizedEntries.add(userID + ";" + sessionNo + ";" + phraseNumber+";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId + "; 1");
					
					phraseNumber++;
					
				}
				
				for(int j=1;j<=hard;j++){
					
					randomPhraseId = randInt( 1, hard_max);
					
					while(hardDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, hard_max);
						
					}
					hardDone.add(randomPhraseId);
					phraseList.add(randomPhraseId+ easy_max +medium_max);
					//Log.d("logger","UserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", hard phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + ( medium_max + easy_max )  ));
					//logtext+= "\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", medium phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + easy_max );
					logtext+= "\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ phraseNumber +" , phrase-id: " + (randomPhraseId + medium_max +easy_max );

//					if(mysdb.insertUser((int)userID, i, phraseNumber, (temp4compare+ medium_max +easy_max) )!= -1){
//						Log.d("logger","insert succ.");
//					}else
//						Log.d("logger","insert failed");
					
					randomPhraseId+= easy_max + medium_max;
					randomizedEntries.add(userID + ";" + sessionNo + ";" + phraseNumber+";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId);
					
					phraseNumber++;
					
				}
				
				break;
				
			/*case 6:
				//Log.d("logger","case "+i);
				
				easy=  4 ;
				medium = 4 ;
				hard = 2 ;
				
				phraseNumber = 1;
				
				for(int j=1;j<=easy;j++){
					
					randomPhraseId = randInt( 1, easy_max);
					
					while(easyDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, easy_max);
						
					}
					easyDone.add(randomPhraseId);
					phraseList.add(randomPhraseId);
					//Log.d("logger","UserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", easy phrase-no: "+ temp4compare + " , phrase-id: " + temp4compare );	
					//logtext+= "\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", easy phrase-no: "+ temp4compare + " , phrase-id: " + temp4compare;
					logtext+= "\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ phraseNumber + " , phrase-id: " + randomPhraseId;

//					if(mysdb.insertUser((int)userID, i, phraseNumber, (temp4compare) )!= -1){
//						Log.d("logger","insert succ.");
//					}else
//						Log.d("logger","insert failed");
					
					
					randomizedEntries.add(userID + ";" + sessionNo + ";" + phraseNumber+";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId);
					
					phraseNumber++;
					
				}
				
				for(int j=1;j<=medium;j++){
					
					randomPhraseId = randInt( 1, medium_max);
					
					while(mediumDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, medium_max);
						
					}
					mediumDone.add(randomPhraseId);
					phraseList.add(randomPhraseId+ easy_max);
					//Log.d("logger","UserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", medium phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + easy_max ));	
					//logtext+= "\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", medium phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + easy_max );
					logtext+= "\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ phraseNumber +" , phrase-id: " + (randomPhraseId + easy_max );

//					if(mysdb.insertUser((int)userID, i, phraseNumber, (temp4compare+ easy_max) )!= -1){
//						Log.d("logger","insert succ.");
//					}else
//						Log.d("logger","insert failed");
					randomPhraseId+= easy_max;
					randomizedEntries.add(userID + ";" + sessionNo + ";" + phraseNumber+";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId);
					
					phraseNumber++;
					
				}
				
				for(int j=1;j<=hard;j++){
					
					randomPhraseId = randInt( 1, hard_max);
					
					while(hardDone.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, hard_max);
						
					}
					hardDone.add(randomPhraseId);
					phraseList.add(randomPhraseId+ easy_max +medium_max);
					//Log.d("logger","UserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", hard phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + ( medium_max + easy_max )  ));
					//logtext+= "\nUserID: " + userID + ",  Session: "+ i + ", phrase number: "+ phraseNumber +", medium phrase-no: "+ temp4compare +" , phrase-id: " + (temp4compare + easy_max );
					logtext+= "\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ phraseNumber +" , phrase-id: " + (randomPhraseId + medium_max +easy_max );

//					if(mysdb.insertUser((int)userID, i, phraseNumber, (temp4compare+ medium_max +easy_max) )!= -1){
//						Log.d("logger","insert succ.");
//					}else
//						Log.d("logger","insert failed");
					
					randomPhraseId+= easy_max + medium_max;
					randomizedEntries.add(userID + ";" + sessionNo + ";" + phraseNumber+";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno:" + j + ";phid:" + randomPhraseId);
					
					phraseNumber++;
					
				}
				break;*/
				
						
			default:
				//Log.d("logger","default case ");
				
				int tempPhCount = (total_sessions -1)* ApplicationConstants.MAX_PHRASES_PER_SESSION;
				//Log.d("logger","default case: " +i);
				//phraseNumber = 1;
				
				for(int j = 1; j<= ApplicationConstants.MAX_PHRASES_PER_SESSION ;j++){
					
					randomPhraseId = randInt( 1, tempPhCount);
					
					while(phraseList.contains(Integer.valueOf(randomPhraseId ))){
						
						//Log.d("logger","Do over");
						randomPhraseId = randInt( 1, tempPhCount);
						
					}
					phraseList.add(randomPhraseId);
					//Log.d("logger","UserID: " + userID + ",  Session: "+ i + ", phrase number: "+ j +" , phrase-id: " + temp4compare );	
					logtext+= "\nUserID: " + userID + ",  Session: "+ sessionNo + ", phrase number: "+ j +", phrase-id: " + randomPhraseId;
					
					/*if(mysdb.insertUser((int)userID, i, j, (temp4compare) )!= -1){
						Log.d("logger","insert succ.");
					}else
						Log.d("logger","insert failed");*/
					
					//temp4compare+= easy_max;
					randomizedEntries.add(userID + ";" + sessionNo + ";" + j+";"+ randomPhraseId + "; 1");
					Log.d("logger:ltu", userID + "; sno: " + sessionNo + "; phno: " + j + ";phid: " + randomPhraseId);
					//phraseNumber++;
					
				}
				
				
				
			
			}
			
			
		}
		
		try{
		phraseList.clear();
		
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return randomizedEntries;
	}

public static int randInt(int min, int max) {

    // NOTE: Usually this should be a field rather than a method
    // variable so that it is not re-seeded every call.
    Random rand = new Random();

    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    int randomNum = rand.nextInt((max - min) + 1) + min;

    return randomNum;
}
}
