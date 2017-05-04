package game.Typing;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {

	Button login;
	Spinner School;
	Spinner Name;
	String[][] userSet;
	String[] schools, users;
	String userSchool = "";
	Database DB = new Database(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);


		login = (Button) findViewById(R.id.button1);
		School = (Spinner) findViewById(R.id.spinner1);
		Name = (Spinner) findViewById(R.id.spinner4);

		//DB.open();
		schools = DB.getSchoolName();
		//DB.close();
		ArrayAdapter<String> schoolAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, schools);
		School.setAdapter(schoolAdapter);
		
		School.setOnItemSelectedListener(new OnItemSelectedListener(){

			@SuppressWarnings("null")
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				userSchool = School.getSelectedItem().toString();
				//DB.open();
				userSet = DB.getUsers(userSchool);
				String userName[] = new String[userSet.length];
				//DB.close();
				Log.d("TAG","here it is " + userSet.length + "----len");
				for(int i=0;i<userSet.length;i++){
					String u = "";
					/*for(int j=0;j<7;j++){
						
						u += userSet[i][j];
						u +="\n";
						//users[i] = u.toString();
						//Log.d("TAG","here it is " + u + users[i]);
					}*/
					u = userSet[i][1].toString();
					Log.d("TAG","here it is " + u);
					userName[i]= u;
				}
				
				ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(Login.this,
						android.R.layout.simple_spinner_dropdown_item, userName);
				Name.setAdapter(nameAdapter);	
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		login.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		Bundle bundle = new Bundle();

		switch (v.getId()) {

		case R.id.button1:
			int user = Name.getSelectedItemPosition();
			Intent intent = new Intent("game.MarathiUT.SESSION");
			bundle.putString("UserId", userSet[user][0]);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		
	}
}