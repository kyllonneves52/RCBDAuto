package com.rcbd.auto;

import android.content.Context;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class LicenseManager {


    // ALTERAR ANTES DE COMPILAR PARA CADA CLIENTE

    private static final String ID_LICENCA =
            "31DB0767";

    private static final int DIAS_PLANO =
            3;

    private static final String CODIGO =
            "AB92";



    public static boolean estaAtivado(Context context){


        if(!LicenseStorage.existe()){
            return false;
        }


        try{


            String dados =
                    LicenseStorage.ler();


            String idAndroid =
                    Settings.Secure.getString(
                            context.getContentResolver(),
                            Settings.Secure.ANDROID_ID
                    );



            String idGuardado =
                    pegar(dados,"ID_ANDROID");

            long dataFinal =
                    Long.parseLong(
                            pegar(dados,"DATA_FINAL")
                    );



            // Verifica se é o mesmo aparelho

            if(!idAndroid.equals(idGuardado)){
                return false;
            }



            // Verifica validade

            if(System.currentTimeMillis() >= dataFinal){

                return false;

            }



            return true;



        }catch(Exception e){

            return false;

        }

    }



    public static boolean ativar(
        Context context,
        String chave
){

    try{

        // VERIFICA SE JÁ EXISTE UMA LICENÇA

        if (LicenseStorage.existe()) {

            try {

                String dados = LicenseStorage.ler();

                if(dados != null){

                    String chaveSalva =
                            pegar(dados, "CHAVE");

                    if(chave.equalsIgnoreCase(chaveSalva)){
                        return false;
                    }

                }

            } catch (Exception e) {

                e.printStackTrace();

            }

        }


        String[] partes =
                chave.split("-");


        if(partes.length != 4){
            return false;
        }


        if(!partes[0].equals("RCBD")){
            return false;
        }


        String id =
                partes[1];


        String dias =
                partes[2].replace("D","");


        String codigo =
                partes[3];



        String idAndroid =
                Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID
                );


        String idGerado =
                gerarIdLicenca(idAndroid);

// TESTE TEMPORÁRIO
LogManager.registar(context, "ID da chave: " + id);
LogManager.registar(context, "ID esperado: " + ID_LICENCA);
LogManager.registar(context, "ANDROID_ID: " + idAndroid);
LogManager.registar(context, "ID gerado: " + idGerado);


        // Verifica ID da chave com ID do APK

        if(!id.equals(ID_LICENCA)){
            return false;
        }



        // Verifica ID do aparelho

        if(!idGerado.equals(ID_LICENCA)){
            return false;
        }



        if(!dias.equals(
                String.valueOf(DIAS_PLANO)
        )){
            return false;
        }



        if(!codigo.equals(CODIGO)){
            return false;
        }



        long inicio =
                System.currentTimeMillis();



        long finalizacao =
                inicio +
                (10 * 60 * 1000L);



        String dados =

                "CHAVE="+chave+"\n"+
                "ID_ANDROID="+idAndroid+"\n"+
                "DATA_INICIO="+inicio+"\n"+
                "DATA_FINAL="+finalizacao;



        LicenseStorage.salvar(dados);



        return true;



    }catch(Exception e){

        return false;

    }

}




    public static String tempoRestante(Context context){


        try{


            String dados =
                    LicenseStorage.ler();


            long fim =
                    Long.parseLong(
                            pegar(dados,"DATA_FINAL")
                    );



            long restante =
                    fim -
                    System.currentTimeMillis();



            if(restante <= 0){

                return "EXPIRADO";

            }



            long dias =
                    restante / 86400000;


            long horas =
                    (restante % 86400000) / 3600000;


            long minutos =
                    (restante % 3600000) / 60000;



            return dias+" dias "
                    +horas+" horas "
                    +minutos+" minutos";



        }catch(Exception e){

            return "ERRO";

        }


    }




    private static String pegar(
            String texto,
            String chave
    ){


        for(String linha :
                texto.split("\n")){


            if(linha.startsWith(chave)){

                return linha
                        .replace(chave+"=","");

            }

        }


        return "";

    }

private static String gerarIdLicenca(String androidId) {

    if(androidId == null || androidId.length() < 8){
        return "";
    }

    return androidId
            .substring(0, 8)
            .toUpperCase(Locale.ROOT);
}


}