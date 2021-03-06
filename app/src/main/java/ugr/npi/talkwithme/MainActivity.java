package ugr.npi.talkwithme;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ugr.npi.talkwithme.voiceinterface.VoiceActivity;


public class MainActivity extends VoiceActivity implements View.OnClickListener{

    private static final String FRAGMENT_DIALOG_LOG_TAG = "BrainLoggerDialog";
    private static int PERMISSIONS_REQUEST_ID=362;

    private static String ID_AFFIRMATIVE = "AFF";	//Id chosen to identify the prompts that involve posing questions to the user
    private static String ID_OOB = "OOB";	//Id chosen to identify the prompts that involve only informing the user

    private ListView chatListView;
    private static ChatArrayAdapter adapter;

    String last_oob="";

    private Button mic;
    private BrainLoggerDialog dialog;

    boolean recording;
    boolean introduced=false;

    String introduction_string;

    //the one we use for Text to Speech
    private ResponseReceiver mMessageReceiver;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getFragmentManager();
        if(!initVoice(this)){
            Log.e("ERROR", "Couldn't initialize Voice Recognition");
        }


        // Se crea un dialog que muestra las cosas que han cargado.

        if (savedInstanceState == null) {
            Log.d("MainActivity", "onCreate savedInstanceState null");
            adapter = new ChatArrayAdapter(getApplicationContext());

            dialog = new BrainLoggerDialog();
            if (!ChatBotApplication.isBrainLoaded()) {
                dialog.show(fm, FRAGMENT_DIALOG_LOG_TAG);
            } else {
                dialog.setPositiveButtonEnabled(true);
            }
        } else {
            Log.d("MainActivity", "onCreate savedInstanceState NOT null");
            dialog = (BrainLoggerDialog) fm.findFragmentByTag(FRAGMENT_DIALOG_LOG_TAG);
        }
        introduction_string=getResources().getString(R.string.introduction);


        chatListView = (ListView) findViewById(R.id.chat_listView);
        chatListView.setAdapter(adapter);

        mic = (Button) findViewById((R.id.mic));
        recording=false;
        activateMicButton();

        //hide keyboard

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.

        IntentFilter intentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION_BRAIN_STATUS);
        intentFilter.addAction(Constants.BROADCAST_ACTION_BRAIN_ANSWER);
        intentFilter.addAction(Constants.BROADCAST_ACTION_LOGGER);

        mMessageReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, intentFilter);

        if (dialog != null && ChatBotApplication.isBrainLoaded()) {
            dialog.loadLog();
            dialog.setPositiveButtonEnabled(true);
        }

    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_log) {
            FragmentManager fm = getFragmentManager();
            dialog = new BrainLoggerDialog();
            dialog.show(fm, FRAGMENT_DIALOG_LOG_TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == mic) {
            Log.d("MIC", "MIC ACTIVATED");
            if(!recording) {
                startListening();
            }else{
                stopListening();
            }

        }
    }


    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_STATUS)) {

                int status = intent.getIntExtra(Constants.EXTRA_BRAIN_STATUS, 0);
                switch (status) {

                    case Constants.STATUS_BRAIN_LOADING:
                        Toast.makeText(MainActivity.this, "brain loading", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            dialog.show(getFragmentManager(), FRAGMENT_DIALOG_LOG_TAG);
                        }
                        break;

                    case Constants.STATUS_BRAIN_LOADED:
                        Toast.makeText(MainActivity.this, "brain loaded", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            dialog.setPositiveButtonEnabled(true);
                        }
                        break;

                }
            }

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_ANSWER)) {
                String answer = intent.getStringExtra(Constants.EXTRA_BRAIN_ANSWER);
                String uttId=answer.contains("<oob>")?ID_OOB:ID_AFFIRMATIVE;
                String answerTTS=answer;
                if(uttId.equals(ID_OOB)){
                    int start=answer.indexOf("<oob>")+5;
                    int end=answer.indexOf("</oob>");
                    last_oob=answer.substring(start,end);
                    Log.d(ID_OOB,"<oob> found between "+start+" and "+end+". OOB: "+last_oob);
                    answerTTS=answer.substring(0,start-5)+answer.substring(end+6);
                }
                speak(answerTTS, uttId);
                adapter.add(new ChatMessage(true, answer));
                adapter.notifyDataSetChanged();
            }

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_LOGGER)) {

                String info = intent.getStringExtra(Constants.EXTENDED_LOGGER_INFO);
                if (info != null) {
                    Log.i("EXTENDED_LOGGER_INFO", info);
                    if (dialog != null) {
                        dialog.addLine(info);
                    }
                }
            }
        }
    }


    /*---------------------------------------------------
    *
    *
    *           WE IMPLEMENT OUR ABSTRACT METHODS
    *
    *
    * ---------------------------------------------------
    */

    @Override
    public void processSpeechResult(String result){

        Log.d("RESULT", "IT WORKS! " + result);

        adapter.add(new ChatMessage(false, result));

        Intent brainIntent = new Intent(MainActivity.this, BrainService.class);
        brainIntent.setAction(BrainService.ACTION_QUESTION);
        brainIntent.putExtra(BrainService.EXTRA_QUESTION, result);
        startService(brainIntent);
    }

    @Override
    public void onSpeechError(int errorCode){
        recording=false;
        activateMicButton();
        Log.e("ERROR", "SPEECH ERROR ");

        String errorMsg = "";
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorMsg = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorMsg = "Unknown client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMsg = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMsg = "Network related error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorMsg = "Network operation timed out";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMsg = "No recognition result matched";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMsg = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMsg = "Server sends error status";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMsg = "No speech input";
                break;
            default:
                errorMsg = ""; //Another frequent error that is not really due to the ASR, we will ignore it
        }
        Toast.makeText(getApplicationContext(), errorMsg , Toast.LENGTH_LONG).show();

        Log.e("ERROR", errorMsg);



    }

    void startListening(){
        recording=true;
        activateMicButton();
        Log.d("MIC","RED");
        mic.setBackgroundResource(android.R.drawable.presence_audio_busy);
        listen(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,20);
    }
    @Override
    public void stopListening(){
        super.stopListening();
        recording=false;
        activateMicButton();
    }
    void processOBB(){
        if(last_oob.contains("<search>")){
            Log.d("SEARCH","DETECTED");
            int start=last_oob.indexOf("<search>")+8;
            int end=last_oob.indexOf("</search>");
            String query=last_oob.substring(start,end);
            Log.d("SEARCHING",query);
            Uri uri = Uri.parse("http://www.google.com/#q="+query);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }else if(last_oob.contains("<url>")){
            Log.d("SEARCH","DETECTED");
            int start=last_oob.indexOf("<url>")+5;
            int end=last_oob.indexOf("</url>");
            String url=last_oob.substring(start,end);
            Log.d("URL",url);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public void onTTSDone(String uttId){
        Log.d("TTS DONE","UTTERANCE "+uttId);
        if(uttId.equals(ID_OOB)){
            Log.d("TTS DONE", "OBB BEING PROCESSED");
            processOBB();
        }else{
            Log.d("TTS DONE", "NO OBB PROCESSED");
        }
        this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   activateMicButton();
                               }
                           });
    }


    @Override
    public void onTTSError(String uttId){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activateMicButton();
            }
        });
        Log.e("TTS ERROR","UTTERANCE: "+ uttId);
    }
    @Override
    public void onTTSStart(String uttId){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deactivateMicButton();
            }
        });

    }


    void activateMicButton(){
        mic.setOnClickListener(this);
        recording=false;
        Log.d("MIC","GREEN");
        mic.setBackgroundResource(android.R.drawable.presence_audio_online);
    }
    void deactivateMicButton(){
        recording=false;
        mic.setOnClickListener(null);
        Log.d("MIC","GREY");
        mic.setBackgroundResource(android.R.drawable.presence_audio_away);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("PERMISSIONS", "CHECK " + requestCode);
        if(requestCode == PERMISSIONS_REQUEST_ID) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("", "Record audio permission granted");
            } else {
                Log.i("", "Record audio permission denied");
                //onRecordAudioPermissionDenied();
            }
        }
    }

    public void introduction(){
        try{
            //Thread.sleep(1);
            adapter.add(new ChatMessage(true, introduction_string));
            adapter.notifyDataSetChanged();
            introduced=true;
            speak(introduction_string, ID_AFFIRMATIVE);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
