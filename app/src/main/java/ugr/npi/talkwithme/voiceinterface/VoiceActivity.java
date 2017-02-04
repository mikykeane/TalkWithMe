package ugr.npi.talkwithme.voiceinterface;

import android.app.Activity;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

/**
 * Created by Miguel on 02/02/2017.
 */

public abstract class VoiceActivity implements RecognitionListener, OnInitListener {

    private SpeechRecognizer SR;
    private TextToSpeech T2S;
    Activity act;

    

}
