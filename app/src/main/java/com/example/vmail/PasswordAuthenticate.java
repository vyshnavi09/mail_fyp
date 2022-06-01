package com.example.vmail;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Store;
import javax.mail.search.FlagTerm;


//Class is extending AsyncTask because this class is going to perform a networking operation
public class PasswordAuthenticate extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;


    private TextToSpeech t1;

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;


    //Class Constructor
    public PasswordAuthenticate(Context context) {
        //Initializing variables
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context, "Authenticating Password", "Please wait...", false, false);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        progressDialog.dismiss();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        try {
            Session session = Session.getDefaultInstance(new Properties());
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", Config.EMAIL, Config.PASSWORD);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Intent intent = new Intent(context, SecondActivity.class);
            context.startActivity(intent);

        } catch (Exception mex) {
            mex.printStackTrace();
            t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        t1.speak("Incorecct Password", TextToSpeech.QUEUE_ADD, null, null);
                    }
                }
            });
        }
        return null;
    }

}