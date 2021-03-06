package com.example.vmail;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;

import android.app.ProgressDialog;
import android.content.Context;
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
public class ReadMail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;


    private TextToSpeech t1;

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;


    //Class Constructor
    public ReadMail(Context context) {
        //Initializing variables
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context, "Reading message", "Please wait...", false, false);

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

            Message[] messages = inbox.search(
                    new FlagTerm(new Flags(Flags.Flag.SEEN), false));


            ArrayList<String> Emailno_arr = new ArrayList<>();
            ArrayList<String> Subject_arr = new ArrayList<>();
            ArrayList<String> From_arr = new ArrayList<>();
//            ArrayList<String> Content_arr = new ArrayList<>();

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
//                Multipart mp = (Multipart)message.getContent();
//                BodyPart bp = mp.getBodyPart(0);
                String emailno = "Email Number " + String.valueOf(i+1);
                System.out.println(emailno);
                Emailno_arr.add(emailno);
                String subject = "Subject " + message.getSubject();
                System.out.println(subject);
                Subject_arr.add(subject);
                String from = "From " + message.getFrom()[0];
                From_arr.add(from);
                System.out.println(from);
//                String c = bp.getContent().toString();
//                Content_arr.add(c);
//                System.out.println(bp.getContent().toString());
            }
            System.out.println(Emailno_arr.size());

            t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        if(Emailno_arr.size() == 0) {
                            t1.speak("No unread Mails",TextToSpeech.QUEUE_ADD, null, null);
                        }
                        for(int j = 0;j < Emailno_arr.size();j++) {
                            t1.speak(Emailno_arr.get(j) + "\n" + Subject_arr.get(j) + " \n" + From_arr.get(j) + "\n" , TextToSpeech.QUEUE_ADD, null, null);
                        }
                        t1.speak("Completed reading all unread mails",TextToSpeech.QUEUE_ADD, null, null);
                    }
                }
            });

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