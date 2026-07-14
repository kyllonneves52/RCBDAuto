package com.rcbd.auto;

import android.content.Context;

public class CommandParser {

    public static boolean processar(Context context, String mensagem) {

        try {

            mensagem = mensagem.trim();

            String[] partes = mensagem.split(" ");

            if(partes.length < 3){
                return false;
            }

            String comando = partes[0].toLowerCase();

            if(!comando.equals("rcbd.mandar")){
                return false;
            }

            String mb = partes[1];
            String numero = partes[2];

            QueueManager.adicionar(
                    context,
                    mb,
                    numero
            );

            return true;

        } catch(Exception e){

            e.printStackTrace();
            return false;
        }
    }
}
