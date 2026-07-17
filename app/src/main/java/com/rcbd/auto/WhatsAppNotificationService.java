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
                .setContentText("Monitorando.mandar")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();
        } else {
            notification = new Notification.Builder(this)
                .setContentTitle("RCBDAuto Rodando")
                .setContentText("Monitorando.mandar")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();
        }

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    // CHAMADO PELO RCBDNotificationListener
    public static void processarMensagem(Context context, String mensagem) {
        String[] dados = MessageParser.analisarMandar(mensagem);
        boolean sucesso = dados!= null && dados.length == 2; // CORRIGIDO

        if(sucesso){
            String mb = dados[0]; // CORRIGIDO
            String numero = dados[1]; // CORRIGIDO
            QueueManager.adicionar(context, mb, numero);
            LogManager.registar(context, "Comando WhatsApp: " + mb + "MB para " + numero);
            RCBDAccessibilityService.setModo("USSD"); // Força USSD
            UssdManager.iniciarEnvio(context);
        } else {
            LogManager.registar(context, "Comando invalido: " + mensagem);
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
