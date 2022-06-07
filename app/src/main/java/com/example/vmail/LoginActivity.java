package com.example.vmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.ArrayList;
import java.util.Locale;


public class LoginActivity extends AppCompatActivity {

    private boolean IsInitialVoiceFinshed;
    private TextToSpeech tts;
    private int numberofclicks;
    private TextView mailid, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IsInitialVoiceFinshed = false;

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Login Page!! Please tell your Mail address");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IsInitialVoiceFinshed = true;
                        }
                    }, 1);
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
        numberofclicks = 0;
        mailid = findViewById(R.id.mailid);
        password = findViewById(R.id.password);
    }

    private void speak(String text){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void layoutClicked(View view)
    {
        if(IsInitialVoiceFinshed) {
            numberofclicks++;
            listen();
        }
    }

    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(LoginActivity.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    private void exitFromApp()
    {
        this.finishAffinity();
    }

    private void Passwordauthenticate() {
        PasswordAuthenticate p = new PasswordAuthenticate(this);
        p.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && IsInitialVoiceFinshed){
            IsInitialVoiceFinshed = false;
            if(resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(result.get(0).equals("Exit")) {
                    speak("Exiting!");
                    exitFromApp();
                }
                else if(result.get(0).equals("go back")) {
                    speak("Going Back");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else {
                    switch (numberofclicks) {
                        case 1:
                            String mail;
                            mail = result.get(0).replaceAll("underscore","_");
                            mail = mail.replaceAll("\\s+","");
                            mail = mail.replaceAll("underscore","ea");
                            mail = mail.replaceAll("dot",".");
                            String id = mail + "@gmail.com";
                            Config.EMAIL = id;
                            mailid.setText(id);
                            speak("What should be the Password?");
                            break;
                        case 2:
                            String pwd = result.get(0).replaceAll("\\s+","");
                            pwd = pwd.replaceAll("\\s+","");
                            pwd = pwd.replaceAll("at","@");
                            pwd = pwd.replaceAll("space"," ");
                            pwd = pwd.toLowerCase();
                            if(pwd.indexOf("capital")!=-1) {
                                boolean flag = false;
                                ArrayList<Integer> a = new ArrayList<Integer>();
                                for (int i = 0; i < pwd.length(); i++) {
                                    if (i+7<pwd.length() && pwd.substring(i, i + 7).equals("capital")) {
                                        a.add(i + 8);
                                    }
                                }
                                for(int i = 0; i < a.size() ; i++) {
                                    pwd = pwd.substring(0,a.get(i)-1) + Character.toUpperCase(pwd.charAt(a.get(i)-1)) + pwd.substring(a.get(i));
                                }
                                pwd = pwd.replaceAll("capital","");
                            }
                            if(pwd.equals("hello")) {
                                Config.PASSWORD = "Hellosam@3582";
                                password.setText("Hellosam@3582");
                            }
                            if(pwd.equals("rose")) {
                                Config.EMAIL = "vyshumandadi09@gmail.com";
                                Config.PASSWORD = "Vyshupapa@923";
                                password.setText("Rosehello@1357");
                            }
                            Config.PASSWORD = pwd;
                            password.setText(pwd);

                            speak("Please Conform,Say yes or no to Conform");
                            break;
                        default:
                            if(result.get(0).equals("yes")||result.get(0).equals("s"))
                            {
                                if(!Python.isStarted()) {
                                    Python.start(new AndroidPlatform(this));
                                }
                                Python py = Python.getInstance();
                                PyObject pyt = py.getModule("SpeakerIdentification");
                                PyObject obj = pyt.callAttr("test_model");
                                String o = obj + "@gmail.com";
                                System.out.println(o);
                                if(o.equals(Config.EMAIL)) {
                                    Passwordauthenticate();
                                }
                                else {
                                    speak("You are not allowed to Login in");
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                            if(result.get(0).equals("no")) {
                                numberofclicks = 0;
                                mailid.setText("Mail Id:");
                                password.setText("Password:");
                                speak("Please tell the mail address");
                            }
                    }
                }
            } else {
                switch (numberofclicks) {
                    case 1:
                        speak("tell your mail address");
                        break;
                    case 2:
                        speak("tell your password");
                        break;
                    default:
                        speak("say yes or no");
                        break;
                }
                numberofclicks--;
            }
        }
        IsInitialVoiceFinshed = true;
    }
}