package game.Typing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Type extends Activity implements OnClickListener {

	String[] word_phrase;
	String value;
	TextView training;
	EditText typing;
	Button nexttext;
	String typed, key;
	int i, j;
	int len;
	Bundle b;
	Database DB = new Database(this);

	File dir, file;
	String filePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_type);

		nexttext = (Button) findViewById(R.id.nextText);
		training = (TextView) findViewById(R.id.textView1);
		typing = (EditText) findViewById(R.id.editText1);

		filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/keyLogData";
		dir = new File(filePath);

		if (!dir.exists()) {
			dir.mkdirs();
		}
		file = new File(dir, "keyboard_log.txt");

		typing.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				// Log.e("test",""+s.charAt(s.length()-1));

				// Log.e("test",""+s.length());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				/*
				 * Log.e("test",""+s.charAt(s.length()-1));
				 * 
				 * Log.e("test",""+s.length());
				 */
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				/*char c = s.charAt(s.length() - 1);
				Log.e("test", "" + (int) c);
				try {

					String key1 = "Pressed Key Code:" + (int) c
							+ " Current Time:" + System.currentTimeMillis();

					FileOutputStream fOut = new FileOutputStream(file, true);
					OutputStreamWriter myOutWriter = new OutputStreamWriter(
							fOut);
					myOutWriter.write("\n" + key1);
					myOutWriter.close();
					fOut.close();
				} catch (Exception e) {

				}*/
			}
		});

		nexttext.setOnClickListener(this);
		Intent it = getIntent();
		b = it.getExtras();
		key = b.getString("session");

		if (key.equals("-1")) {
			i = 0;
			j = 0;
			Resources resources = this.getResources();
			//word_phrase = resources.getStringArray(R.array.T_words);
			len = word_phrase.length;
			training.setText(word_phrase[i]);
			typed = typing.getText().toString();

		} else if (key.equals("0")) {
			i = 0;
			j = 0;
			Resources resources = this.getResources();
			//word_phrase = resources.getStringArray(R.array.FTU_words);
			len = word_phrase.length;
			training.setText(word_phrase[i]);
			typed = typing.getText().toString();

		} else {
			// to be changed to session wise phrases and also add randomisation
			i = 0;
			j = 0;
			Resources resources = this.getResources();
			//word_phrase = resources.getStringArray(R.array.Longitudinal_phrases_easy);
			len = word_phrase.length;
			training.setText(word_phrase[i]);
			typed = typing.getText().toString();

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.v("TAG","i="+i+"J="+j+"word phrase"+word_phrase.toString()+"");
		if (i == word_phrase.length) {
			Log.v("TAG","i="+i+"J="+j+"word phrase"+word_phrase.toString()+"--if stmt");
			Log.e("test", "" + typed);
			try {

				String key1 = "Pressed Key Code:" + typed + " Current Time:"
						+ System.currentTimeMillis();

				FileOutputStream fOut = new FileOutputStream(file, true);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
				myOutWriter.write("\n" + key1);
				myOutWriter.close();
				fOut.close();
			} catch (Exception e) {

			}

			if (key.equals("-1")) {

				DB.open();
				DB.countTrain(b.getString("UserId"));
				DB.close();

			}
			
			else {
				
				DB.open();
				DB.countSess(b.getString("UserId"));
				DB.close();
			
			}

			Intent intent = new Intent("game.MarathiUT.SESSION");
			Bundle bd = new Bundle();
			bd.putString("UserId", b.getString("UserId"));
			intent.putExtras(bd);
			startActivity(intent);

		} else if ((!word_phrase[i].equals(typing.getText().toString()))
				&& ((key.equals("-1") || ((j == 0) && (key.equals("0")))))) {
			Log.v("TAG","i="+i+"J="+j+"word phrase"+word_phrase.toString()+"---else if");
			if (key.equals("0"))
				j++;

			Dialog d = new Dialog(this);
			TextView tv = new TextView(this);
			tv.setText("Type again");
			d.setTitle("Typed Wrong!!!");
			d.setContentView(tv);
			d.show();
			i = 0;
			typing.setText("");
		} else {
			Log.v("TAG","i="+i+"J="+j+"word phrase"+word_phrase.toString()+"-----else");
			Dialog d = new Dialog(this);
			TextView tv = new TextView(this);
			tv.setText("Type the next word");
			d.setTitle("Yipee!!!");
			d.setContentView(tv);
			d.show();
			if (i < len - 1) {
				++i;
				typing.setText("");
				training.setText(word_phrase[i]);
			}
		}
	}
}
