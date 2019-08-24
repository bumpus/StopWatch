package us.bump;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import static android.os.SystemClock.elapsedRealtime;


public class MainActivity extends AppCompatActivity {

    private int lapCounter;
    private long swElapsedTime;
    private long swLapElapsedTime;

    private Button btnLap;
    private Button btnReset;
    private ToggleButton tbtnStartStop;
    private LinearLayout ll;

    private Chronometer sw;
    private Chronometer swLap;
    private TextView txtLap;
    private TextView currentLap;

    private Timer lockoutTimer;

    private TimerTask enableLapButton = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnLap.setEnabled(true);
                }
            });
        }
    };

    private View.OnClickListener mbtnClickListener = new View.OnClickListener() {
        public void onClick(View v){
            long currentTime = elapsedRealtime();
            switch (v.getId()) {
                case R.id.btnLap:
                    btnLap.setEnabled(false);
                    swLap.stop();

                    TextView currentLap = new TextView(getApplicationContext());
                    currentLap.setText(String.valueOf(lapCounter) + "     " + swLap.getText() + "     " + sw.getText());

                    swLap.setBase(currentTime);
                    swLapElapsedTime = 0;
                    swLap.start();

                    ll.addView(currentLap);

                    txtLap.setText(String.valueOf(++lapCounter));

                    lockoutTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnLap.setEnabled(true);
                                }
                            });
                        }
                    },500);

                    break;
                case R.id.btnReset:
                    txtLap.setText(String.valueOf(lapCounter=0));
                    swElapsedTime=0;
                    sw.setBase(currentTime);
                    swLapElapsedTime = 0;
                    swLap.setBase(currentTime);
                    ll.removeAllViewsInLayout();

                    txtLap.setText(String.valueOf(lapCounter = 0));
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mtglChangeListener = new CompoundButton.OnCheckedChangeListener(){
        public void onCheckedChanged(CompoundButton t, boolean isChecked) {
            long currentTime = elapsedRealtime();
            if (isChecked) {
                sw.setBase(currentTime - swElapsedTime);
                sw.start();
                swLap.setBase(currentTime - swElapsedTime);
                swLap.start();
                btnReset.setEnabled(false);
                btnLap.setEnabled(true);

                //If we're just starting, then set our lap # to 1
                if (0 == lapCounter) {
                    txtLap.setText(String.valueOf(++lapCounter));
                }
            } else {
                swElapsedTime = currentTime - sw.getBase();
                sw.stop();
                swLapElapsedTime = currentTime - swLap.getBase();
                swLap.stop();
                btnReset.setEnabled(true);
                btnLap.setEnabled(false);
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find my buttons in the content view
        btnLap = (Button) findViewById(R.id.btnLap);
        btnReset = (Button) findViewById(R.id.btnReset);
        tbtnStartStop = (ToggleButton) findViewById(R.id.tbtnStartStop);

        //Find the text areas in the content view
        sw = (Chronometer) findViewById(R.id.sw);
        swLap = (Chronometer) findViewById(R.id.swLap);
        txtLap = (TextView) findViewById(R.id.txtLap);
        //ScrollView scrViewLapCounter = (ScrollView) findViewById(R.id.scrViewLapCounter);
        ll = (LinearLayout) findViewById(R.id.linLayLapCounter);


        //Make Timer
        lockoutTimer = new Timer();

        //Assign listeners
        btnLap.setOnClickListener(mbtnClickListener);
        btnReset.setOnClickListener(mbtnClickListener);
        tbtnStartStop.setOnCheckedChangeListener(mtglChangeListener);
        tbtnStartStop.setOnClickListener(mbtnClickListener);

        // Initialize the lapcounter and display
        lapCounter = 0;
        txtLap.setText(String.valueOf(lapCounter));

        // Force the screen to stay on.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

}
