package ninja.hongyuan.podotimer;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private NotificationManager mNotifyMgr;
    private NotificationManager timeupManager;
    private Notification.Builder mBuilder;
    private int mNotificationId = 12345;


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

        workCounter = counterInit(0);
        breakCounter = counterInit(0);

        timeupManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.nficon)
                .setContentTitle("Podo Timer")
                .setContentText("Hello World!")
                .setAutoCancel(true);

        Intent resultIntent = new Intent(this, MainActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        // Gets an instance of the NotificationManager service
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.


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
        t *= 1000; // millisecond to second
        CountDownTimer ctr = new CountDownTimer(t, 100) {
            public void onTick(long millisUntilFinished) {
                long min = millisUntilFinished / 60000;
                long sec = (millisUntilFinished % 60000) / 1000;
                mTextField.setText(min + ":" + sec);
                mBuilder.setContentText(min + ":" + sec);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }

            public void onFinish() {
                this.cancel();
                vib.vibrate(500);
                if(onWork) {
                    toggleButton.setText("Break");
                    mBuilder.setContentText("Start a break?");
                }
                if(onBreak) {
                    toggleButton.setText("Work");
                    mBuilder.setContentText("Go back to Work?");
                }
                isDone = true;
                mTextField.setText("done!");
            }
        };

        return ctr;

    }

    public void toggle(View view) {
        if (!isDone && !onWork && !onBreak || isDone && onBreak) {
            EditText workTime = (EditText)findViewById(R.id.work_time);

            int wt;
            if(workTime.getText().toString().equals(""))
                wt = 1500;
            else
                wt = Integer.parseInt(workTime.getText().toString());

            workCounter = counterInit(wt);
            start(workCounter);
            onWork = true;
            onBreak = false;
        }

        else if (isDone && onWork) {
            EditText breakTime = (EditText)findViewById(R.id.break_time);

            int bt;
            if(breakTime.getText().toString().equals(""))
                bt = 300;
            else
                bt = Integer.parseInt(breakTime.getText().toString());

            breakCounter = counterInit(bt);
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
        mNotifyMgr.cancel(mNotificationId);
        onWork = false;
        onBreak = false;
        isDone = false;
    }


}
