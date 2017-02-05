package ugr.npi.talkwithme.voiceinterface;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Miguel on 02/02/2017.
 */

public abstract class VoiceActivity extends Activity implements RecognitionListener, OnInitListener {
    private TextToSpeech tts;
    private SpeechRecognizer speech;
    private final int PERMISSIONS_REQUEST = 362;
    private final String VOICE_LOG_TAG = "VOICE ACTIVITY";
    private Context ctx;

    final Locale language=Locale.ENGLISH;

    public boolean initVoice(Context ctx){
        boolean result;
        this.ctx=ctx;
        PackageManager packManager = ctx.getPackageManager();

        //Assign TTS
        tts = new TextToSpeech(ctx, this);
        tts.setLanguage(language);

        //TODO utterance

        /*Utterance things
        *
        * */

        //Assign Speech
        // Find out whether speech recognition is supported
        List<ResolveInfo> intActivities = packManager.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (intActivities.size() != 0 || "generic".equals(Build.BRAND.toLowerCase(Locale.US))) {
            speech = SpeechRecognizer.createSpeechRecognizer(ctx);
            speech.setRecognitionListener(this);
            result=true;
        }
        else {
            speech = null;
            result=false;
        }
        return result;

    }

    public void end(){
        tts.stop();
        tts.shutdown();
        tts=null;

        speech.stopListening();
        speech.destroy();
        speech=null;
    }


    //languageModel=RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    //maxResults=integer
@TargetApi(21)
    public void listen(String languageModel, int maxResults){
        requestPermissions();
            if((languageModel.equals(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM) || languageModel.equals(RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)) && (maxResults>=0))
            {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                // Specify the calling package to identify the application
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, ctx.getPackageName());
                //Caution: be careful not to use: getClass().getPackage().getName());

                // Specify language model
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, languageModel);

                // Specify how many results to receive. Results listed in order of confidence
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults);

                // Specify English as recognition language
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");

                Log.d("LISTEN", "STARTS LISTENING ");

                speech.startListening(intent);



            }


    }

    //TODO quitar esto?


    public void requestPermissions(){
        Log.d("PERMISSIONS", "try to give permissions ");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission.
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.RECORD_AUDIO}, 362); //Callback in "onRequestPermissionResult"
            Log.d("PERMISSIONS", "Permissions given");
        }

    }


    //TODO quitar deprecated?
    public void speak(String text){
        //DEPRECATED
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        tts.setLanguage(language);
        tts.speak(text, TextToSpeech.QUEUE_ADD, params);

        //API>21
        //Bundle params = new Bundle();
        //tts.speak(text,TextToSpeech.QUEUE_ADD, params,"");

    }

    public abstract void processSpeechResult(String result);

    public abstract void onSpeechError(int code);
    public abstract void onTTSDone(String uttId);
    public abstract void onTTSError(String uttId);
    public abstract void onTTSStart(String uttId);
    //public abstract void requestPermissions();

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
        if(results!=null){
            ArrayList<String> resultsArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if(resultsArray.size()>0) {
                String text_result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                processSpeechResult(text_result);
            }else{
                onSpeechError(SpeechRecognizer.ERROR_NO_MATCH);
            }

        }
        else {
            onSpeechError(SpeechRecognizer.ERROR_NO_MATCH);
        }

    }

    @Override
    public void onError(int error) {
        onSpeechError(error);
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR){
            Log.d(VOICE_LOG_TAG,"TTS created");
        }
        else
        {
            Log.d(VOICE_LOG_TAG,"Error creating the TTS");
        }

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }


}
