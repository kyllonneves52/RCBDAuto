package com.rcbd.auto;


public class CommandParser {


    public static String[] analisarMandar(String mensagem){


        mensagem = mensagem.trim();



        if(!mensagem.toLowerCase().startsWith(".mandar")){

            return null;

        }



        String[] partes =
                mensagem.split(" ");



        if(partes.length < 3){

            return null;

        }



        String mb =
                partes[1]
                .replace("mb","")
                .replace("MB","")
                .trim();



        String numero =
                partes[2]
                .trim();



        return new String[]{
                mb,
                numero
        };


    }


}