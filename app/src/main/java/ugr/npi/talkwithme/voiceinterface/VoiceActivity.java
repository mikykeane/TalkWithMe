package ugr.npi.talkwithme.voiceinterface;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Miguel on 02/02/2017.
 */

public abstract class VoiceActivity extends Activity implements RecognitionListener, OnInitListener {
    private TextToSpeech tts;
    private SpeechRecognizer speech;
    private final int PERMISSIONS_REQUEST = 362;
    private final String VOICE_LOG_TAG = "VOICE ACTIVITY";

    public void init(Context ctx){


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

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onResults(Bundle results) {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onInit(int status) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }


}
