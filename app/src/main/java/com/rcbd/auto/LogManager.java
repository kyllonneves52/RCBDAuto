package com.rcbd.auto;

import android.content.Context;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class LogManager {


    private static final String ARQUIVO =
            "rcbd_logs.txt";



    public static void registar(
            Context context,
            String mensagem
    ){


        try{


            String hora =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss",
                            Locale.getDefault()
                    ).format(
                            new Date()
                    );



            String linha =
                    hora
                    + " - "
                    + mensagem
                    + "\n";



            FileOutputStream fos =
                    context.openFileOutput(
                            ARQUIVO,
                            Context.MODE_APPEND
                    );



            fos.write(
                    linha.getBytes()
            );


            fos.close();



        }catch(Exception e){


            e.printStackTrace();


        }


    }





    public static String ler(
            Context context
    ){


        try{


            java.io.FileInputStream fis =
                    context.openFileInput(
                            ARQUIVO
                    );


            byte[] dados =
                    new byte[
                            fis.available()
                    ];



            fis.read(dados);

            fis.close();



            return new String(dados);



        }catch(Exception e){


            return "Sem logs";


        }


    }


}
