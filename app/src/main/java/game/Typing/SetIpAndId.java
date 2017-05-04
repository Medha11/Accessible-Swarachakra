package game.Typing;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SetIpAndId extends Activity implements OnClickListener{
	
	Button b1 ;
	EditText ip1, ip2, ip3,ip4,DeviceId;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.activity_setipandid);
		
		try{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		if (prefs.getBoolean("first_time", false)) {
			
			Intent intenta = new Intent("game.MarathiUT.SCHOOLSELECTION");
			intenta.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intenta);
			
		}
		
		
		b1 = (Button) findViewById(R.id.setBut);
		
		ip1 = (EditText) findViewById(R.id.ip1);
		ip2 = (EditText) findViewById(R.id.ip2);
		ip3 = (EditText) findViewById(R.id.ip3);
		ip4 = (EditText) findViewById(R.id.ip4);
		DeviceId = (EditText) findViewById(R.id.device_ID);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		b1.setOnClickListener(this);
		
		/*FontsOverride.setDefaultFont(this, "MONOSPACE", ApplicationConstants.customfontDev);
		FontsOverride.setDefaultFont(this, "SERIF", ApplicationConstants.customfontLatinSerif);
		FontsOverride.setDefaultFont(this, "SANS_SERIF", ApplicationConstants.customfontLatinSans);*/
		
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		/*ActionBar actionBar = getActionBar();
		actionBar.hide();*/
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		b1.setEnabled(false); //prevents double click
		String ip_string = ip1.getText()+"."+ip2.getText()+"."+ip3.getText()+"."+ip4.getText();
		int device_id = Integer.valueOf(DeviceId.getText().toString());
		int my_device_id = device_id;
		device_id = (device_id*1000)+1;
		
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = pre.edit();
		editor.putBoolean("first_time", true);
		editor.putString("ip_string", ip_string);
		editor.putInt("Device_ID", device_id);
		
		editor.putInt("my_device_id", my_device_id);
		editor.commit();
		
		Intent intent = new Intent("game.MarathiUT.SCHOOLSELECTION");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	

}
