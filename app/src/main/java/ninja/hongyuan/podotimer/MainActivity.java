package ninja.hongyuan.podotimer;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextClock mTextClock;
    private TextView mTextField;
    private CountDownTimer workCounter;
    private CountDownTimer breakCounter;
    private boolean onWork;
    private boolean onBreak;
    private boolean isDone;
    private Button toggleButton;
    private Vibrator vib;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // clock test
        mTextClock = (TextClock)findViewById(R.id.textClock);
        mTextClock.setFormat24Hour("yyyy-MM-dd hh:mm:ss");

        onWork = false;
        onBreak = false;
        isDone = false;

        // countdown test
        mTextField = (TextView)findViewById(R.id.textview);
        vib = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        toggleButton = (Button)findViewById(R.id.toggle_button);

        workCounter = counterInit(1500000);
        breakCounter = counterInit(300000);

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

    public CountDownTimer counterInit(int t) {
        CountDownTimer ctr = new CountDownTimer(t, 100) {
            public void onTick(long millisUntilFinished) {
                long min = millisUntilFinished / 60000;
                long sec = (millisUntilFinished % 60000) / 1000;
                mTextField.setText(min + ":" + sec);
            }

            public void onFinish() {
                this.cancel();
                vib.vibrate(500);
                if(onWork) toggleButton.setText("Break");
                if(onBreak) toggleButton.setText("Work");
                isDone = true;
                mTextField.setText("done!");
            }
        };

        return ctr;

    }

    public void toggle(View view) {
        if (!isDone && !onWork && !onBreak || isDone && onBreak) {
            start(workCounter);
            onWork = true;
            onBreak = false;
        }

        else if (isDone && onWork) {
            start(breakCounter);
            onWork = false;
            onBreak = true;
        }
        else cancel();

    }

    public void start(CountDownTimer ctr) {
        ctr.start();
        isDone = false;
        toggleButton.setText("Stop");
    }

    public void cancel() {
        workCounter.cancel();
        breakCounter.cancel();
        toggleButton.setText("Work");
        mTextField.setText("Stopped");
        onWork = false;
        onBreak = false;
        isDone = false;
    }
}
