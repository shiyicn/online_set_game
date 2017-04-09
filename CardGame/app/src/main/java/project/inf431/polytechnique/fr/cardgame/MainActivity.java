package project.inf431.polytechnique.fr.cardgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main Activity";

    public static final String EXTRA_NUM_CARD = "project.inf431.polytechnique.fr.MESSAGE";
    public static final String EXTRA_LOGIN = "project.inf431.polytechnique.fr.LOGIN";
    public static final String EXTRA_CONNEXION_FLAG = "project.inf431.polytechnique.fr.CONNEXION";

    RadioGroup gameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        gameMode = (RadioGroup) findViewById(R.id.mode_choice_radio_group);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /** Called when the user taps the start button */
    public void startGame(View view) {
        int num = 12;
        int radioButtonID = gameMode.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) gameMode.findViewById(radioButtonID);
        String connexion = radioButton.getText().toString();
        EditText editText = (EditText) findViewById(R.id.editText);
        String my_login = editText.getText().toString();
        Log.v(TAG, my_login);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(EXTRA_NUM_CARD, num);
        intent.putExtra(EXTRA_LOGIN, my_login);
        intent.putExtra(EXTRA_CONNEXION_FLAG, connexion);
        startActivity(intent);
    }

}
