package ugr.npi.talkwithme.voiceinterface;

import android.app.Activity;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Miguel on 02/02/2017.
 */

public abstract class VoiceActivity extends AppCompatActivity implements RecognitionListener, OnInitListener {
    private TextToSpeech tts;
    private SpeechRecognizer speech;

    public void init(){


    }

    public void listen(){

    }
    public void speak(){

    }

    public String getSpeechResult(){
        return null;
    }

    public abstract void getSpeechError();
    public abstract void getTTSError();
    public abstract void onTTSDone(String uttId);
    public abstract void onTTSError(String uttId);
    public abstract void onTTSStrart(String uttId);
    public abstract boolean requestPermissions();

}