package com.rcbd.auto;

import android.content.Context;

public class CommandParser {

    public static boolean processar(Context context, String mensagem) {

        try {
            mensagem = mensagem.trim().toLowerCase();

            String[] partes = mensagem.split(" ");

            if(partes.length < 3){
                return false;
            }

            String comando = partes[0];

            // ACEITA.mandar
            if(!comando.equals(".mandar")){
                return false;
            }

            // Pega MB e tira o "mb" se tiver
            String mb = partes[1].replace("mb", "").replace("MB", "").trim();
            String numero = partes[2].replace(" ", "").trim();

            QueueManager.adicionar(
                    context,
                    mb,
                    numero
            );

            LogManager.registar(context, "Comando adicionado: " + mb + "MB para " + numero);
            return true;

        } catch(Exception e){
            e.printStackTrace();
            LogManager.registar(context, "Erro CommandParser: " + e.getMessage());
            return false;
        }
    }
}