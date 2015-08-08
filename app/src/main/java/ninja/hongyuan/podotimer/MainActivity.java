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

    // status:
    private final short PREPARE_TO_WORK = 0;
    private final short PREPARE_TO_BREAK = 1;
    private final short ON_WORK = 2;
    private final short ON_BREAK = 3;
    private short status = PREPARE_TO_WORK;

    private CountDownTimer cdt = counterInit(25);
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


        // countdown test
        mTextField = (TextView)findViewById(R.id.textview);
        vib = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        toggleButton = (Button)findViewById(R.id.toggle_button);

        // notification
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

    /*
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
    */

    // get a CountDownTimer with t minutes
    public CountDownTimer counterInit(int t) {
        t *= 60000; // millisecond to minute
        // count-down: t minutes; tick frequency: 100ms
        CountDownTimer ctr = new CountDownTimer(t, 100) {
            public void onTick(long millisUntilFinished) {
                long min = millisUntilFinished / 60000;
                long sec = (millisUntilFinished % 60000) / 1000;
                // update remaining time display
                mTextField.setText(min + ":" + sec);
                mBuilder.setContentText(min + ":" + sec);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }

            public void onFinish() {
                vib.vibrate(500);
                toggle();
                mTextField.setText("Done");
            }
        };

        return ctr;
    }

    // listen toggle button click
    public void onToggleButtonClick(View view) {
        toggle();
    }

    public void toggle() {
        // get user-defined time value
        EditText workTime = (EditText)findViewById(R.id.work_time);
        EditText breakTime = (EditText)findViewById(R.id.break_time);
        // default:
        // work: 25min
        // break: 5min
        int wt = workTime.getText().toString().equals("")?
                25 : Integer.parseInt(workTime.getText().toString());
        int bt = breakTime.getText().toString().equals("")?
                5 : Integer.parseInt(breakTime.getText().toString());

        switch (status) {
            case PREPARE_TO_WORK:
                status = ON_WORK;
                cdt = counterInit(wt);
                cdt.start();
                toggleButton.setText("Stop");
                break;
            case PREPARE_TO_BREAK:
                status = ON_BREAK;
                cdt = counterInit(bt);
                cdt.start();
                toggleButton.setText("Stop");
                break;
            case ON_WORK:
                status = PREPARE_TO_BREAK;
                cdt.cancel();
                toggleButton.setText("BREAK");
                mTextField.setText("Stopped");
                mNotifyMgr.cancel(mNotificationId);
                break;
            case ON_BREAK:
                status = PREPARE_TO_WORK;
                cdt.cancel();
                toggleButton.setText("WORK");
                mTextField.setText("Stopped");
                mNotifyMgr.cancel(mNotificationId);
                break;
        }
    }


}
