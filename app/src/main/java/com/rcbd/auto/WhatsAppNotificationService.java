package com.rcbd.auto;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.app.Notification;
import android.os.Bundle;

public class WhatsAppNotificationService
        extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){

        String pacote = sbn.getPackageName();

        // Aceitar WhatsApp normal e Business
        if(!pacote.equals("com.whatsapp") &&!pacote.equals("com.whatsapp.w4b")){
            return;
        }

        Notification notificacao = sbn.getNotification();
        if(notificacao == null){
            return;
        }

        Bundle extras = notificacao.extras;

        CharSequence texto = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence titulo = extras.getCharSequence(Notification.EXTRA_TITLE);

        if(texto == null){
            return;
        }

        // Junta titulo + texto pra pegar "Fulano:.mandar 100mb 848395255"
        String mensagem = (titulo!= null? titulo.toString() + ": " : "") + texto.toString();

        LogManager.registar(this, "Notificacao recebida: " + mensagem);

        // USA O COMMANDPARSER PRA LER.mandar 100mb 848395255
        boolean sucesso = MessageParser.analisarMandar(mensagem);

        if(sucesso){
            // Pega o ultimo adicionado na fila
            RCBDAccessibilityService.setModo("USSD"); // Comando via zap sempre usa USSD
            UssdManager.iniciarEnvio(this);
            LogManager.registar(this, "Comando executado via WhatsApp");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){}
}