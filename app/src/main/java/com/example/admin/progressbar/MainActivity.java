package com.example.admin.progressbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private final int PROGRESSION = 1;
    private final int RESET_PROGRESSION = 2;
    private final int TIMER_PROGRESSION = 10;
    private final int TIMER_RESET = 1000;

    private final String MESSAGE_RESET = "Reset";
    private final String MESSAGE_PROGRESSION = "Progress";


    private ProgressBar bar ;
    private TextView compteur;

    private Bundle messageBundle = new Bundle();
    private Message message;

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bar = (ProgressBar) findViewById(R.id.progress);
        compteur = (TextView) findViewById(R.id.compteur);

        compteur.setText("0");
    }

    @Override
    protected void onStart() {
        super.onStart();

        //On lance le traitement du Thread
        traitement.start();
    }

    Handler handler = new Handler (){
        private int chiffreCompteur = 0;
        public void handleMessage(Message msg) {

            //On modifie la ProgressBar avec la progression reçu
            int progress = msg.getData().getInt(MESSAGE_PROGRESSION);
            bar.incrementProgressBy(progress);

            //Si la progression est au max on remet à 0 en doublant la taille
            if(msg.getData().getBoolean(MESSAGE_RESET))
            {
                bar.setMax(bar.getMax() * RESET_PROGRESSION );
                bar.setProgress(0);
                chiffreCompteur++;
                compteur.setText(chiffreCompteur + "");
            }
        }
    };

    Thread traitement = new Thread() {
        @Override
        public void run() {
            while(isRunning.get())
            {
                //On met en pause le thread un petit moment pour éviter les remplissages instantané
                try {
                    Thread.sleep(TIMER_PROGRESSION);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //On envois au Handler la progression
                message = handler.obtainMessage();
                messageBundle.putInt(MESSAGE_PROGRESSION, PROGRESSION);

                //On vérifie a chaque passage la progression de la barre pour la remettre à 0 si besoin
                if(bar.getMax() <= bar.getProgress()){
                    try {
                        Thread.sleep(TIMER_RESET);

                        messageBundle.putBoolean(MESSAGE_RESET, true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    messageBundle.putBoolean(MESSAGE_RESET, false);
                }

                message.setData(messageBundle);
                handler.sendMessage(message);
            }
        }
    };
}
