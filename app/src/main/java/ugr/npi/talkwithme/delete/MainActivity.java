package ugr.npi.talkwithme.delete;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TTSHandler tts;
    SpeechHandler speech;
    EditText ttsText;
    TextView speechText;
    Button ttsButton, speechButton, resultsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        setContentView(R.layout.activity_main);
        ttsText =  (EditText) findViewById(R.id.ttsText);
        speechText = (TextView) findViewById(R.id.speechText);
        ttsButton = (Button)  findViewById(R.id.ttsButton);
        ttsButton.setOnClickListener(this);
        speechButton = (Button)  findViewById(R.id.speechButton);
        speechButton.setOnClickListener(this);
        resultsButton = (Button)  findViewById(R.id.resultButton);
        */
        resultsButton.setOnClickListener(this);
        tts = new TTSHandler(this);
        speech = new SpeechHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if(v==ttsButton){
            tts.speak(ttsText.getText().toString());
        }
        if(v==speechButton){
            speechText.setText("RECONOCIENDO");
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Request the permission.
                    ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.RECORD_AUDIO},
                            22); //Callback in "onRequestPermissionResult"
                }
                speech.listen();

        }
        if(v==resultsButton){
            speech.stopListening();
            speechText.setText(speech.string_result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == 22) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("", "Record audio permission granted");
            } else {
                Log.i("", "Record audio permission denied");
                //onRecordAudioPermissionDenied();
            }
        }
    }
}