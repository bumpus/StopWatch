package us.bump.stopwatch;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;


import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import static android.os.SystemClock.elapsedRealtime;


public class MainActivity extends AppCompatActivity {

    private int lapCounter;
    private long swElapsedTime;
    private long lapStartTime;

    private Button btnLap;
    private Button btnReset;
    private ToggleButton tbtnStartStop;

    private Chronometer sw;
    private TextView txtLap;
    private TextView txtLapTimes;

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
            switch (v.getId()) {
                case R.id.btnLap:
                    btnLap.setEnabled(false);
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
                    txtLap.setText(String.valueOf(++lapCounter));
                    txtLapTimes.setText(
                            txtLapTimes.getText() +
                            "\n"+ String.valueOf(lapCounter)+ " This Lap " + sw.getText());
                    break;
                case R.id.btnReset:
                    txtLap.setText(String.valueOf(lapCounter=0));
                    swElapsedTime=0;
                    sw.setBase(elapsedRealtime());
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mtglChangeListener = new CompoundButton.OnCheckedChangeListener(){
        public void onCheckedChanged(CompoundButton t, boolean isChecked) {
            if (isChecked) {
                sw.setBase(elapsedRealtime()-swElapsedTime);
                sw.start();
                btnReset.setEnabled(false);
                btnLap.setEnabled(true);
            } else {
                swElapsedTime= elapsedRealtime()-sw.getBase();
                sw.stop();
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
        txtLap = (TextView) findViewById(R.id.txtLap);
        txtLapTimes = (TextView) findViewById(R.id.txtLapTimes);

        //Make Timer
        lockoutTimer = new Timer();

        //Assign listeners
        btnLap.setOnClickListener(mbtnClickListener);
        btnReset.setOnClickListener(mbtnClickListener);
        tbtnStartStop.setOnCheckedChangeListener(mtglChangeListener);
        tbtnStartStop.setOnClickListener(mbtnClickListener);


        lapCounter = 0;

        txtLap.setText(String.valueOf(lapCounter));

    }

}
