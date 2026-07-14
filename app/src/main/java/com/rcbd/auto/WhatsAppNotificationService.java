package com.rcbd.auto;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.app.Notification;
import android.os.Bundle;


public class WhatsAppNotificationService
        extends NotificationListenerService {



    @Override
    public void onNotificationPosted(
            StatusBarNotification sbn
    ){


        String pacote =
                sbn.getPackageName();



        // Aceitar WhatsApp normal
        // e WhatsApp Business

        if(!pacote.equals("com.whatsapp")
                &&
           !pacote.equals("com.whatsapp.w4b")){


            return;

        }



        Notification notificacao =
                sbn.getNotification();



        if(notificacao == null){
            return;
        }



        Bundle extras =
                notificacao.extras;



        CharSequence texto =
                extras.getCharSequence(
                        Notification.EXTRA_TEXT
                );



        if(texto == null){
            return;
        }


        String mensagem =
                texto.toString();


String[] comando =
        CommandParser.analisarMandar(mensagem);


if(comando != null){


    String mb =
            comando[0];


    String numero =
            comando[1];


    QueueManager.adicionar(
            this,
            mb,
            numero
    );


    LogManager.registar(
            this,
            "Pedido recebido: "
            + mb
            + "MB -> "
            + numero
    );


UssdManager.iniciarEnvio(this);


}


        LogManager.registar(
                this,
                "WhatsApp: "
                + mensagem
        );


String[] dados =
        MessageParser.analisar(mensagem);


if(dados != null){

    String numero =
            dados[0];


    String valor =
            dados[1];


    LogManager.registar(
            this,
            "Extraído -> Número: "
            + numero
            + " Valor: "
            + valor
    );

}



LogManager.registar(
        this,
        "Extraído -> Número: "
        + numero
        + " Valor: "
        + valor
);


        // Próximo passo:
        // enviar para MessageParser


    }



    @Override
    public void onNotificationRemoved(
            StatusBarNotification sbn
    ){


    }


}
