package com.rcbd.auto;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class WhatsAppNotificationService extends Service {

    private static final String CHANNEL_ID = "RCBDAutoService";

    @Override
    public void onCreate() {
        super.onCreate();
        criarCanal();
        
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                 .setContentTitle("RCBDAuto Rodando")
                 .setContentText("Monitorando WhatsApp")
                 .setSmallIcon(android.R.drawable.ic_dialog_info)
                 .build();
        } else {
            notification = new Notification.Builder(this)
                 .setContentTitle("RCBDAuto Rodando")
                 .setContentText("Monitorando WhatsApp")
                 .setSmallIcon(android.R.drawable.ic_dialog_info)
                 .build();
        }
        
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public static void processarMensagem(Context context, String mensagem) {
        String[] dados = MessageParser.analisarMandar(mensagem);
        boolean sucesso = dados!= null && dados.length >= 3;
        if(sucesso){
            String pacote = dados[0];
            String mb = dados[1];
            String numero = dados[2];
            QueueManager.adicionar(context, mb, numero);
            RCBDAccessibilityService.setModo(pacote);
            UssdManager.iniciarEnvio(context);
        }
    }

    private void criarCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "RCBDAuto Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
