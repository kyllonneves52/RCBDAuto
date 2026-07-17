package com.rcbd.auto;

public class MessageParser {

    public static String[] analisarMandar(String mensagem){
        mensagem = mensagem.trim().toLowerCase();

        if(!mensagem.startsWith(".mandar")){
            return null;
        }

        String[] partes = mensagem.split(" ");

        if(partes.length < 3){
            return null;
        }

        String mb = partes[1].replace("mb","").trim();
        String numero = partes[2].replace(" ", "").trim();

        return new String[]{ mb, numero };
    }
}
