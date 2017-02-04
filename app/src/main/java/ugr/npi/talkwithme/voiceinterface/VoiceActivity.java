package ugr.npi.talkwithme.voiceinterface;

import android.app.Activity;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ugr.npi.talkwithme.R;


public abstract class VoiceActivity extends AppCompatActivity implements RecognitionListener, TextToSpeech.OnInitListener{

    private RecognitionListener RL;
    private TextToSpeech T2S;
    Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);


    }
}
