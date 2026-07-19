package com.rcbd.auto;

import android.content.Context;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LicenseManager {

    private static final String ANDROID_ID_PERMITIDO =
            "31db07671355a14a";


    // ALTERAR ANTES DE COMPILAR PARA CADA CLIENTE
    private static final int DIAS_PLANO = 3;
    private static final int HORAS_PLANO = 0;
    private static final int MINUTOS_PLANO = 0;


    public static boolean verificar(Context context){

        String id =
                Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID
                );


        if(!ANDROID_ID_PERMITIDO.equals(id)){
            return false;
        }


        String licenca =
                LicenseStorage.ler();


        long agora =
                System.currentTimeMillis();



        // PRIMEIRA INSTALAÇÃO
        if(licenca == null){

            long validade =
                    (DIAS_PLANO * 86400000L)
                    +
                    (HORAS_PLANO * 3600000L)
                    +
                    (MINUTOS_PLANO * 60000L);


            long expiracao =
                    agora + validade;


            String nova =
                    "ANDROID_ID=" + id + "\n" +
                    "DATA_ATIVACAO=" + agora + "\n" +
                    "DATA_EXPIRACAO=" + expiracao + "\n" +
                    "ULTIMA_VERIFICACAO=" + agora;


            LicenseStorage.salvar(nova);


            return true;

        }



        try{

            long expiracao =
                    extrairLong(
                            licenca,
                            "DATA_EXPIRACAO="
                    );


            long ultima =
                    extrairLong(
                            licenca,
                            "ULTIMA_VERIFICACAO="
                    );


            // relógio voltou para trás
            if(agora < ultima){
                return false;
            }


            // expirou
            if(agora >= expiracao){
                return false;
            }


            // Atualiza a cada abertura
            String novo =
                    atualizarUltima(
                            licenca,
                            agora
                    );


            LicenseStorage.salvar(novo);


            return true;


        }catch(Exception e){

            return false;

        }

    }



    public static String getTempoRestante(Context context){

        try{

            String licenca =
                    LicenseStorage.ler();


            if(licenca == null){
                return "Sem licença";
            }


            long expiracao =
                    extrairLong(
                            licenca,
                            "DATA_EXPIRACAO="
                    );


            long diff =
                    expiracao -
                    System.currentTimeMillis();



            if(diff <= 0){
                return "Expirado";
            }


            long dias =
                    diff / 86400000L;


            long horas =
                    (diff % 86400000L)
                    /
                    3600000L;


            long minutos =
                    (diff % 3600000L)
                    /
                    60000L;


            long segundos =
                    (diff % 60000L)
                    /
                    1000L;


            return String.format(
                    Locale.getDefault(),
                    "%02dd %02dh %02dm %02ds",
                    dias,
                    horas,
                    minutos,
                    segundos
            );


        }catch(Exception e){

            return "Erro";

        }

    }



    private static long extrairLong(
            String texto,
            String chave
    ){

        String valor =
                texto.split(chave)[1]
                .split("\n")[0];


        return Long.parseLong(valor);

    }



    private static String atualizarUltima(
            String texto,
            long novaData
    ){

        return texto.replaceAll(
                "ULTIMA_VERIFICACAO=.*",
                "ULTIMA_VERIFICACAO=" + novaData
        );

    }


}