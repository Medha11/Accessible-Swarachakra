package game.Typing.validation;

import game.Typing.FileOperations;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class Validator {

	/***
	 * 
	 * @param c application context
	 * @param expectedKB keyboard that is expected to be the default
	 * @return returns true if the default keyboard is infact currentKB else returns false  
	 */
	
	public static double oneStarLowerBoundInclusive = 0.0; 
	public static double oneStarUpperBoundExclusive = 0.4;
	public static double twoStarLowerBoundInclusive = 0.4; 
	public static double twoStarUpperBoundExclusive = 0.6;
	public static double threeStarLowerBoundInclusive = 0.6; 
	public static double threeStarUpperBoundExclusive = 0.8; 
	public static double fourStarLowerBoundInclusive = 0.8; 
	public static double fourStarUpperBoundExclusive = 1.0; 
	public static double fiveStarLowerBoundInclusive = 1.0; 
	public static double fiveStarUpperBoundExclusive = 1.0001; 
	
	public static double oneStarLowerBoundInclusiveCompleteSession = -1.0; 
	public static double oneStarUpperBoundExclusiveCompleteSession = 0.2;
	public static double twoStarLowerBoundInclusiveCompleteSession = 0.2; 
	public static double twoStarUpperBoundExclusiveCompleteSession = 0.5;
	public static double threeStarLowerBoundInclusiveCompleteSession = 0.5; 
	public static double threeStarUpperBoundExclusiveCompleteSession = 0.75; 
	public static double fourStarLowerBoundInclusiveCompleteSession = 0.75; 
	public static double fourStarUpperBoundExclusiveCompleteSession = 1.0; 
	public static double fiveStarLowerBoundInclusiveCompleteSession = 1.0; 
	public static double fiveStarUpperBoundExclusiveCompleteSession = 1.0001; 
	
	public static boolean isCorrectKeyboardSet(Context c, String expectedKB){
		
		String currentKB = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
		if(currentKB.equalsIgnoreCase(expectedKB))
			return true;
		else
			return false;
	}
	
	public static String getEDToStars(int editDistance, int wordLength){
		String starsVal = "";
		//double correctnessRatio = (editDistance *1.0 / wordLength) * 100;
		double correctnessRatio = normalizedEditDistance(editDistance, wordLength);
		
		/*if(editDistance ==0)
			correctnessRatio = 100;*/
		
		/*if( correctnessRatio == 0){
			starsVal = "*****";
		
		}else if(correctnessRatio > 0 && correctnessRatio <= 20){
			starsVal = "****";
		}else if(correctnessRatio > 20 && correctnessRatio <= 40){
			starsVal = "***";
		}else if(correctnessRatio > 40 && correctnessRatio <= 60){
			starsVal = "**";
		}else
			starsVal = "*";*/
		
		if( correctnessRatio >= fiveStarLowerBoundInclusive && correctnessRatio < fiveStarUpperBoundExclusive){
			starsVal = "* * * * *";
		
		}else if(correctnessRatio >=fourStarLowerBoundInclusive && correctnessRatio < fourStarUpperBoundExclusive){
			starsVal = "* * * *";
		}else if(correctnessRatio >= threeStarLowerBoundInclusive && correctnessRatio < threeStarUpperBoundExclusive){
			starsVal = "* * *";
		}else if(correctnessRatio >= twoStarLowerBoundInclusive && correctnessRatio < twoStarUpperBoundExclusive){
			starsVal = "* *";
		}else if(correctnessRatio >= oneStarLowerBoundInclusive && correctnessRatio < oneStarUpperBoundExclusive)
			starsVal = "*";
		
		Log.d("ed","ED: "+editDistance + ", Wordlength : "+wordLength);
		Log.d("ed","Correctness ratio: "+String.format("%.2f", correctnessRatio) + ", stars : "+starsVal);
		return starsVal;
	}
	
	public static float getEDToStarVal(int editDistance, int wordLength){
		float starsVal = 0;
		//double correctnessRatio = (editDistance *1.0 / wordLength) * 100;
		double correctnessRatio = normalizedEditDistance(editDistance, wordLength);
		
		/*if(editDistance ==0)
			correctnessRatio = 100;*/
		
		/*if( correctnessRatio == 0){
			starsVal = 5;
		
		}else if(correctnessRatio > 0 && correctnessRatio <= 20){
			starsVal = 4;
		}else if(correctnessRatio > 20 && correctnessRatio <= 40){
			starsVal = 3;
		}else if(correctnessRatio > 40 && correctnessRatio <= 60){
			starsVal = 2;
		}else
			starsVal = 1;*/
		
		if( correctnessRatio >= fiveStarLowerBoundInclusive && correctnessRatio < fiveStarUpperBoundExclusive){
			starsVal = 5;
		
		}else if(correctnessRatio >=fourStarLowerBoundInclusive && correctnessRatio < fourStarUpperBoundExclusive){
			starsVal = 4;
		}else if(correctnessRatio >= threeStarLowerBoundInclusive && correctnessRatio < threeStarUpperBoundExclusive){
			starsVal = 3;
		}else if(correctnessRatio >= twoStarLowerBoundInclusive && correctnessRatio < twoStarUpperBoundExclusive){
			starsVal = 2;
		}else if(correctnessRatio >= oneStarLowerBoundInclusive && correctnessRatio < oneStarUpperBoundExclusive)
			starsVal = 1;
		
		//Log.d("ed","ED: "+editDistance + ", Wordlength : "+wordLength);
		//Log.d("ed","Correctness ratio: "+String.format("%.2f", correctnessRatio) + ", stars : "+starsVal);
		return starsVal;
	}
	
	public static double normalizedEditDistance(int editDistance, int wordLength){
		
		double correctnessRatio =0;
		if(wordLength!=0)
			correctnessRatio = 1 - (editDistance *1.0 / wordLength); //correctnessRatio =  (editDistance *1.0 / wordLength) * 100;
		else
			FileOperations.write("Length of word typed in FTU is 0 ");
		return correctnessRatio;
	}
	
	public static double getSessionStar(double correctnessRatio){
		double starsVal=0;
		
		if( correctnessRatio >= fiveStarLowerBoundInclusiveCompleteSession && correctnessRatio < fiveStarUpperBoundExclusiveCompleteSession){
			starsVal = 5;
		
		}else if(correctnessRatio >=fourStarLowerBoundInclusiveCompleteSession && correctnessRatio < fourStarUpperBoundExclusiveCompleteSession){
			starsVal = 4;
		}else if(correctnessRatio >= threeStarLowerBoundInclusiveCompleteSession && correctnessRatio < threeStarUpperBoundExclusiveCompleteSession){
			starsVal = 3;
		}else if(correctnessRatio >= twoStarLowerBoundInclusiveCompleteSession && correctnessRatio < twoStarUpperBoundExclusiveCompleteSession){
			starsVal = 2;
		}else if(correctnessRatio >= oneStarLowerBoundInclusiveCompleteSession && correctnessRatio < oneStarUpperBoundExclusiveCompleteSession)
			starsVal = 1;
		
		return starsVal;
	}
}
