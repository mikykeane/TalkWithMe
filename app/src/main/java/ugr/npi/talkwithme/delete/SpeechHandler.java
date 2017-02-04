package ugr.npi.talkwithme.delete;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Charlie on 04/02/2017.
 */

public class SpeechHandler implements RecognitionListener {
    private SpeechRecognizer speechR;
    String string_result = "A";
    Context ctx;

    SpeechHandler(Context ctx){
        PackageManager packManager = ctx.getPackageManager();
        List<ResolveInfo> intActivities = packManager.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        this.ctx=ctx;
        if (intActivities.size() != 0 || "generic".equals(Build.BRAND.toLowerCase(Locale.US))) {
            speechR = SpeechRecognizer.createSpeechRecognizer(ctx);
            speechR.setRecognitionListener(this);
        }
        else {
            speechR = null;
            Log.e("NO SPEECH", "SPEECH IS A LIE");
        }
    }


    public void listen(){

        String languageModel=RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;
        int maxResults=2;
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

            // Specify recognition language
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

            speechR.startListening(intent);

        }
    }

    public void stopListening(){
        speechR.stopListening();
    }


    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> nBestList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        float[] nBestConfidences = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        if(nBestList!=null){
            if(nBestList.size()>0){
                string_result=nBestList.get(0); //We will use the best result;
            }
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onError(int error) {

    }

}


