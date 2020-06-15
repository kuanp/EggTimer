package ex.example.eggtimer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView timeDisplay;
    private SeekBar timerInitSeekBar;
    private Button controlButton;
    private boolean alreadyCountingDown;
    private CountDownTimer countDownTimer;

    private static final int MAX_TIME_SECONDS = 300;
    private static final int DEFAULT_START_SECONDS = MAX_TIME_SECONDS / 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeDisplay = findViewById(R.id.timeDisplay);
        timerInitSeekBar = findViewById(R.id.timerInitSeekBar);
        controlButton = findViewById(R.id.controlButton);
        alreadyCountingDown = false;

        setupTimerInitSeekBar();
        updateDisplayText(timerInitSeekBar.getProgress());
        updateControlButton();
    }

    private void setupTimerInitSeekBar() {
        timerInitSeekBar.setMax(MAX_TIME_SECONDS);
        timerInitSeekBar.setProgress(DEFAULT_START_SECONDS);
        timerInitSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!alreadyCountingDown) {
                    updateDisplayText(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        timerInitSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (alreadyCountingDown) {
                    Toast.makeText(getApplicationContext(),
                            "Count down already started, press stop to start a new timer.",
                            Toast.LENGTH_SHORT).show();
                }

                // returning true here means we don't propagate the event to the seekBar itself,
                // effectively disabling it.
                return alreadyCountingDown;
            }
        });
    }

    private void updateDisplayText(int seconds) {
        int minutes = seconds / 60;
        seconds = seconds % 60;

        timeDisplay.setText(String.format("%d:%02d", minutes, seconds));
    }

    private void updateControlButton() {
        if (alreadyCountingDown) {
            controlButton.setText("Stop!");
            controlButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   onStopButtonClicked(v);
                }
            });
        } else {
            controlButton.setText("Go!");
            controlButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGoButtonClicked(v);
                }
            });
        }
    }

    private void onGoButtonClicked(View view) {
        alreadyCountingDown = true;
        countDownTimer = new CountDownTimer(timerInitSeekBar.getProgress() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int currentTickSeconds = timerInitSeekBar.getProgress() - 1;
                if (currentTickSeconds < 0) {
                    throw new RuntimeException("Um");
                }

                updateDisplayText(currentTickSeconds);
                timerInitSeekBar.setProgress(currentTickSeconds);
            }

            @Override
            public void onFinish() {
                playFinishingSound();
                timerInitSeekBar.setProgress(DEFAULT_START_SECONDS);
                onStopButtonClicked(null);
            }
        };
        countDownTimer.start();
        updateControlButton();
    }

    private void onStopButtonClicked(View view) {
        alreadyCountingDown = false;
        countDownTimer.cancel();
        updateControlButton();
        updateDisplayText(timerInitSeekBar.getProgress());
    }

    private void playFinishingSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.airhorn);
        mediaPlayer.start();
    }
}
