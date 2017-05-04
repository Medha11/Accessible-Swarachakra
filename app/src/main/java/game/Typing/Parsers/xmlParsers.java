package game.Typing.Parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import game.Typing.FileOperations;
import game.Typing.R;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

public class xmlParsers {

	private  XmlPullParserFactory pullParserFactory;
	private  XmlPullParser parser;
	public  ArrayList<keyboards> keyboardList = null;
	public  HashMap<String,String> keyboardMap = null;
	public  HashMap<String,String> schoolMap = null;
	public  HashMap<String,String> alternativesMap = null;
	public  keyboards currentKB = null;
	//public  xmlParsers myinstance =null;

	public xmlParsers (){
		/*if(xmlParsers.myinstance == null)
			myinstance = this;
		if(pullParserFactory == null)
			pullParserFactory = XmlPullParserFactory.newInstance();*/
	}

	public HashMap<String,String> getSchoolNamesFromXML(Context context){

		//ArrayList<String> kbNames =null;
		try {
			//pullParserFactory = XmlPullParserFactory.newInstance();
			parser = context.getResources().getXml(R.xml.schoollist);

			//InputStream in_s = context.getResources().open("temp.xml");
			//parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			//parser.setInput(in_s, null);

			//parseXML(parser);

			
			int eventType = parser.getEventType();


			while (eventType != XmlPullParser.END_DOCUMENT){
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
					//kbNames = new ArrayList();
					schoolMap = new HashMap<String, String>();
					break;
				case XmlPullParser.START_TAG:                	
					name = parser.getName();
					//Log.d("kbz","parser name: "+name);

					if (name.equals("schools")){

						//parser.next();

					}else if (name.equals("school") ){

						currentKB = new keyboards();

					}else if(name.equals("name")){

						currentKB.name = parser.nextText();
						//Log.d("kbz","Name:"+currentKB.name);

					}else if(name.equals("location")){

						currentKB.servicename = parser.nextText();
						//Log.d("kbz","Location:"+currentKB.servicename);

					}	
					/*else if (currentKB != null){
                    }
                        if (name.equals("name")){
                        	currentKB.name = parser.nextText();
                        	Log.d("kbz","Name:"+currentKB.name);
                        } else if (name.equals("imename")){
                        	currentKB.servicename = parser.nextText();
                        	Log.d("kbz","Service name:"+currentKB.servicename);
                        } 
                    }*/
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("school") && currentKB != null){
						//kbNames.add(currentKB);
						schoolMap.put(currentKB.name, currentKB.servicename);
					} 
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {

			e.printStackTrace();
			FileOperations.write("School-list xml parsing problem. Exception: XmlPullParserException.");
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileOperations.write("School-list xml reading problem. Exception: IOException.");
			
		}catch (NotFoundException nfe){
			
			nfe.printStackTrace();
			FileOperations.write("School-list xml could not be loaded. Exception: NotFoundException.");
			
		}
		return schoolMap;
	}

	public HashMap<String,String> getKeyboardNamesFromXML(Context context){

		//ArrayList<String> kbNames =null;
		try {
			//pullParserFactory = XmlPullParserFactory.newInstance();
			parser = context.getResources().getXml(R.xml.keyboardlist);

			//InputStream in_s = context.getResources().open("temp.xml");
			//parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			//parser.setInput(in_s, null);

			//parseXML(parser);

			/*catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

			int eventType = parser.getEventType();


			while (eventType != XmlPullParser.END_DOCUMENT){
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
					//kbNames = new ArrayList();
					keyboardMap = new HashMap<String, String>();
					break;
				case XmlPullParser.START_TAG:                	
					name = parser.getName();
					//Log.d("kbz","parser name: "+name);

					if (name.equals("keyboards")){

						//parser.next();

					}else if (name.equals("keyboard") ){

						currentKB = new keyboards();

					}else if(name.equals("name")){

						currentKB.name = parser.nextText();
						//Log.d("kbz","Name:"+currentKB.name);

					}else if(name.equals("imename")){

						currentKB.servicename = parser.nextText();
						//Log.d("kbz","Service name:"+currentKB.servicename);

					}	
					/*else if (currentKB != null){
                    }
                        if (name.equals("name")){
                        	currentKB.name = parser.nextText();
                        	Log.d("kbz","Name:"+currentKB.name);
                        } else if (name.equals("imename")){
                        	currentKB.servicename = parser.nextText();
                        	Log.d("kbz","Service name:"+currentKB.servicename);
                        } 
                    }*/
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("keyboard") && currentKB != null){
						//kbNames.add(currentKB);
						keyboardMap.put(currentKB.name, currentKB.servicename);
					} 
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {

			e.printStackTrace();
			FileOperations.write("Keyboard list xml parsing problem. Exception: XmlPullParserException.");
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileOperations.write("Keyboard list xml reading problem. Exception: IOException.");
			
		}catch (NotFoundException nfe){
			
			nfe.printStackTrace();
			FileOperations.write("Keyboard list xml could not be loaded. Exception: NotFoundException.");
			
		}
		return keyboardMap;
	}

	public HashMap<String,String> getAlternativesFromXML(Context context){

		//ArrayList<String> kbNames =null;
		try {
			//pullParserFactory = XmlPullParserFactory.newInstance();
			parser = context.getResources().getXml(R.xml.alternatives);

			//InputStream in_s = context.getResources().open("temp.xml");
			//parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			//parser.setInput(in_s, null);

			//parseXML(parser);

			/*catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

			int eventType = parser.getEventType();


			while (eventType != XmlPullParser.END_DOCUMENT){
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
					//kbNames = new ArrayList();
					alternativesMap = new HashMap<String, String>();
					break;
					
				case XmlPullParser.START_TAG:                	
					name = parser.getName();
					//Log.d("kbz","parser name: "+name);

					if (name.equals("alternatives")){

						//parser.next();

					}else if (name.equals("alternative")){

						
						//currentKB.name = parser.getAttributeValue(null, "needle");
						//currentKB.servicename = parser.getAttributeValue(null, "replaceWith");
						
						alternativesMap.put(parser.getAttributeValue(null, "needle"), parser.getAttributeValue(null, "replaceWith"));
						//parser.next();

					}/*else if(name.equals("needle")){

						currentKB.name = parser.nextText();
						Log.d("kbz","Name:"+currentKB.name);

					}else if(name.equals("replaceWith")){

						currentKB.servicename = parser.nextText();
						Log.d("kbz","Service name:"+currentKB.servicename);

					}	*/
					/*else if (currentKB != null){
                    }
                        if (name.equals("name")){
                        	currentKB.name = parser.nextText();
                        	Log.d("kbz","Name:"+currentKB.name);
                        } else if (name.equals("imename")){
                        	currentKB.servicename = parser.nextText();
                        	Log.d("kbz","Service name:"+currentKB.servicename);
                        } 
                    }*/
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					/*if (name.equalsIgnoreCase("alternatives") && currentKB != null){
						//kbNames.add(currentKB);
						alternativesMap.put(currentKB.name, currentKB.servicename);
					}*/ 
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {

			e.printStackTrace();
			FileOperations.write("Alternatives list xml parsing problem. Exception: XmlPullParserException.");
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileOperations.write("Alternatives list xml reading problem. Exception: IOException.");
			
		}catch (NotFoundException nfe){
			
			nfe.printStackTrace();
			FileOperations.write("Alternatives list xml could not be loaded. Exception: NotFoundException.");
			
		}catch (NullPointerException npe){
			
			npe.printStackTrace();
			FileOperations.write("Alternatives list xml could not be loaded. Exception: NullPointerException.");
			
		}
		return alternativesMap;
	}
	
	public class keyboards{
		String name;
		String servicename;
	}
}

