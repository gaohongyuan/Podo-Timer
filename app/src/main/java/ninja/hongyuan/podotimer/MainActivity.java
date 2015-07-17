package ninja.hongyuan.podotimer;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextClock mTextClock;
    private TextView mTextField;
    private CountDownTimer counter;
    private boolean isStart;
    private Button startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // clock test
        mTextClock = (TextClock)findViewById(R.id.textClock);
        mTextClock.setFormat24Hour("yyyy-MM-dd hh:mm:ss");

        // countdown test
        mTextField = (TextView)findViewById(R.id.textview);
        counter = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mTextField.setText("done!");
            }
        };

        isStart = false;
        startButton = (Button)findViewById(R.id.toggle_button);

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

    public void toggle(View view) {
        if (isStart) {
            counter.cancel();
            startButton.setText("Start");
            mTextField.setText("Stopped");
            isStart = false;
        }

        else {
            counter.start();
            startButton.setText("Cancel");
            isStart = true;
        }

    }
}
