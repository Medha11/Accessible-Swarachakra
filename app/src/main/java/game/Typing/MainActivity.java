package game.Typing;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Intent intent = new Intent("game.MarathiUT.FIRSTSCREEN");
		startActivity(intent);
        }
    
    }