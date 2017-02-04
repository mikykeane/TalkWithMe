package ugr.npi.talkwithme.delete;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Charlie on 04/02/2017.
 */

public class TTSHandler implements TextToSpeech.OnInitListener{
    private static final String ID_PROMPT_INFO = "2";
    private TextToSpeech tts;

    TTSHandler(Context context){
        tts= new TextToSpeech(context, this);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
        {
            @Override
            public void onDone(String utteranceId) //TTS finished synthesizing
            {
                onTTSDone(utteranceId);
            }

            @Override
            public void onError(String utteranceId) //TTS encountered an error while synthesizing
            {
                onTTSError(utteranceId);
            }

            @Override
            public void onStart(String utteranceId) //TTS has started synthesizing
            {
                onTTSStart(utteranceId);
            }
        });
    }

    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR){
            Log.d("TTS","LOADED");
            tts.setLanguage(Locale.ENGLISH);
        }
    }

    public void speak(String txt){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, ID_PROMPT_INFO);
        tts.speak(txt, TextToSpeech.QUEUE_ADD, params);

    }

    /**
     * Invoked when the TTS has finished synthesizing.
     *
     * In this case, it starts recognizing if the message that has just been synthesized corresponds to a question (its id is ID_PROMPT_QUERY),
     * and does nothing otherwise.
     *
     * @param uttId identifier of the prompt that has just been synthesized (the id is indicated in the speak method when the text is sent
     * to the TTS engine)
     */
    public void onTTSDone(String uttId) {
    }

    /**
     * Invoked when the TTS encounters an error.
     *
     * In this case it just writes in the log.
     */
    public void onTTSError(String uttId) {
        Log.e("L", "TTS error");
    }

    /**
     * Invoked when the TTS starts synthesizing
     *
     * In this case it just writes in the log.
     */
    public void onTTSStart(String uttId) {

        Log.e("L", "TTS starts speaking");
    }



}
