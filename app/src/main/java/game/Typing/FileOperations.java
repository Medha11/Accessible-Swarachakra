package game.Typing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class FileOperations {
	 public FileOperations() {
		 
	    } 

	 private static boolean log;
	 
	 public static boolean isLoggingEnabled(){
		 
		 return log;
	 }
	 
	 public static void enableLogging(boolean state){
		 
		 log = state ; 
	 }
	 
	 public static Boolean write(String fcontent){ //String fname, 
			try {
				 
				
				//String f = "/sdcard/"+fname+".txt";
				
				String fileName = DateFormat.format("dd_MM_yyyy",new Date(System.currentTimeMillis())).toString() +".txt";
				//String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/utevaluationtool/";
				String path = Environment.getExternalStorageDirectory().getPath() + "/utevaluationtool";
				File fPath = new File(path);
				fPath.mkdirs();
					 
				File file = new File(fPath,fileName);
	 
				// If file does not exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
	 
				FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(DateFormat.format("dd-MM-yyyy hh:mm:ssa",new Date(System.currentTimeMillis())).toString()+":::"+fcontent);
				bw.newLine();
				bw.close();
	 
				Log.d("Success","Success");
				return true;
	 
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
	 }
	 
	 public static Boolean writeLog(String fcontent, int userId){ //String fname, 
			try {
				 
				
				//String f = "/sdcard/"+fname+".txt";
				
				String fileName = userId +".txt";
				//String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/utevaluationtool/";
				String path = Environment.getExternalStorageDirectory().getPath() + "/utevaluationtool";
				File fPath = new File(path);
				fPath.mkdirs();
					 
				File file = new File(fPath,fileName);
	 
				// If file does not exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
	 
				FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(fcontent);
				//bw.newLine();
				bw.close();
	 
				Log.d("Success","Success");
				return true;
	 
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
	 }
	 
}
