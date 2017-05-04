package game.Typing;

public class SpeedCalculator {
	
	private long firstCharInputTimestamp;
	private long lastCharInputTimestamp;
	//private long phraseFirstShownTimestamp;
	private int textLength;
	private int wordCount;
	private double avgWordLength;

	public SpeedCalculator() {
		// TODO Auto-generated constructor stub
		
	}
	public SpeedCalculator(int avgWordLength) {
		// TODO Auto-generated constructor stub
		this.avgWordLength = avgWordLength;
	}
	
	public void setFirstCharTime(long fCharTime){
		firstCharInputTimestamp = fCharTime;
	}
	
	public void setLastCharTime(long lCharTime){
		lastCharInputTimestamp = lCharTime;
	}
	public void setTextLength(int length){
		textLength = length ;
	}

	public void setWordCount(int c){
		wordCount = c ;
	}
	public void setAvgWordLength(double length){
		avgWordLength = length ;
	}
	
	/*
	public void setPhraseShownTS(long phraseShownTS){
		phraseFirstShownTimestamp = phraseShownTS;
	}*/
	
	/***
	 * 
	 * @return returns cpm if textLength not zero and diff between firstCharInputTimestamp and lastCharInputTimestamp is not zero,
	 * else returns -1
	 */
	public double getCPM(){
		double timeTakenMin = getTimeTaken()/60.0;
		
		if(textLength !=0 && timeTakenMin >0)
			return (textLength-1) / timeTakenMin;
		else
			return -1;
	}
	
	/*
	public double getCPM2(){
		double timeTakenMin2 = getTimeTaken2()/60.0;
		
		if(textLength !=0 && timeTakenMin2 >0)
			return (textLength-1) / timeTakenMin2;
		else
			return -1;
	}
	*/
	public double getWPM(){
		double timeTakenMin = getTimeTaken()/60.0;
		
		if(wordCount !=0 && timeTakenMin !=0)
			return wordCount / timeTakenMin;
		else
			return -1;
	}
	
	public double getNormalizedWPM(){
		double timeTakenSec = (lastCharInputTimestamp - firstCharInputTimestamp)/1000.0;
		
		if(textLength !=0 && timeTakenSec !=0)
			return ((textLength-1) / timeTakenSec) * (60 /avgWordLength);
		else
			return -1;
	}
	
	public double getTimeTaken(){
		double timeTakenSec = (lastCharInputTimestamp - firstCharInputTimestamp)/1000.0;
		
		return timeTakenSec;
	}
	
	/*public double getTimeTaken2(){
		double timeTakenSec = (lastCharInputTimestamp - phraseFirstShownTimestamp)/1000.0;
		
		return timeTakenSec;
	}*/

	/*public double getTimeTakenMin(){
		double timeTakenMin = (lastCharInputTimestamp - firstCharInputTimestamp)/1000.0;
		
		return timeTakenMin;
	}*/
	}
