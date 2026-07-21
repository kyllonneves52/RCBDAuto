package com.rcbd.auto;

import android.content.Context;

import java.util.Locale;


public class LicenseManager {


    // COLOCA O BUILD.ID DO TEU TELEFONE AQUI
    private static final String BUILD_PERMITIDO =
            "SP1A.210812.017";


    private static final String ID_LICENCA =
            "DF7A2792";


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



            String buildAtual =
                    android.os.Build.ID
                    .toUpperCase(Locale.ROOT);



            String buildGuardado =
                    pegar(dados,"BUILD_ID");



            if(!buildAtual.equals(buildGuardado)){
                return false;
            }



            long dataFinal =
                    Long.parseLong(
                            pegar(dados,"DATA_FINAL")
                    );



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


            // PRIMEIRA VERIFICAÇÃO: BUILD

            String buildTelefone =
                    android.os.Build.ID
                    .toUpperCase(Locale.ROOT);



            LogManager.registar(
                    context,
                    "BUILD telefone: "
                    + buildTelefone
            );



            if(!buildTelefone.equals(BUILD_PERMITIDO)){


                LogManager.registar(
                        context,
                        "Falhou: BUILD diferente"
                );


                return false;

            }




            // NÃO ACEITA LICENÇA REPETIDA


            if(LicenseStorage.existe()){


                String dados =
                        LicenseStorage.ler();



                if(dados != null){


                    String chaveSalva =
                            pegar(dados,"CHAVE");



                    if(chave.equalsIgnoreCase(chaveSalva)){

                        return false;

                    }

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
                    partes[2]
                    .replace("D","");



            String codigo =
                    partes[3];




            if(!id.equals(ID_LICENCA)){


                LogManager.registar(
                        context,
                        "Falhou ID"
                );


                return false;

            }




            if(!dias.equals(
                    String.valueOf(DIAS_PLANO)
            )){


                LogManager.registar(
                        context,
                        "Falhou dias"
                );


                return false;

            }




            if(!codigo.equals(CODIGO)){


                LogManager.registar(
                        context,
                        "Falhou codigo"
                );


                return false;

            }




            long inicio =
                    System.currentTimeMillis();



            long finalizacao =
                    inicio +
                    (10 * 60 * 1000L);




            String dados =

                    "CHAVE="+chave+"\n"+
                    "BUILD_ID="+buildTelefone+"\n"+
                    "DATA_INICIO="+inicio+"\n"+
                    "DATA_FINAL="+finalizacao;




            LicenseStorage.salvar(dados);



            return true;



        }catch(Exception e){


            LogManager.registar(
                    context,
                    "Erro licença: "+e.getMessage()
            );


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


}