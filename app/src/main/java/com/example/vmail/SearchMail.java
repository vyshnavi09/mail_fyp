package com.example.vmail;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

public class SearchMail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;
    private String search;

    private TextToSpeech t1;

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;


    //Class Constructor
    public SearchMail(Context context, String search) {
        //Initializing variables
        this.context = context;
        this.search = search;
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

            Message[] messages = inbox.getMessages();


            ArrayList<String> Emailno_arr = new ArrayList<>();
            ArrayList<String> Subject_arr = new ArrayList<>();
            ArrayList<String> From_arr = new ArrayList<>();

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                String s = message.getFrom()[0].toString();
                System.out.println(s);
                String f = message.getSubject();
                System.out.println(f);
                if(s.equals(search) || f.indexOf(search) !=-1) {
                    String emailno = "Email Number " + String.valueOf(i+1);
                    System.out.println(emailno);
                    Emailno_arr.add(emailno);
                    String subject = "Subject " + message.getSubject();
                    System.out.println(subject);
                    Subject_arr.add(subject);
                    String from = "From " + message.getFrom()[0];
                    From_arr.add(from);
                    System.out.println(from);
                }
            }

            if(Emailno_arr.size() == 0) {
                t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            t1.speak("No such Emails", TextToSpeech.QUEUE_ADD, null, null);
                        }

                    }
                });
            }

            t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        for(int j = 0;j < Emailno_arr.size();j++) {
                            t1.speak(Emailno_arr.get(j) + "\n" + Subject_arr.get(j) + " " + From_arr.get(j), TextToSpeech.QUEUE_ADD, null, null);
                        }
                    }
                }
            });

        } catch (Exception mex) {
            mex.printStackTrace();
        }
        return null;
    }

}