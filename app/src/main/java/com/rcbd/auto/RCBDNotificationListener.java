package com.rcbd.auto;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.content.Context;

public class RCBDNotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pacote = sbn.getPackageName();

        // Só lê WhatsApp e WhatsApp Business
        if(pacote.equals("com.whatsapp") || pacote.equals("com.whatsapp.w4b")){
            String titulo = sbn.getNotification().extras.getString("android.title");
            String texto = sbn.getNotification().extras.getString("android.text");

            if(texto!= null && texto.toLowerCase().startsWith(".mandar")){
                LogManager.registar(this, "Msg recebida: " + texto);
                WhatsAppNotificationService.processarMensagem(this, texto);
            }
        }
    }
}
