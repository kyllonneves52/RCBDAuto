package com.rcbd.auto;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LicenseStorage {

    private static final String PASTA = "RCBDAuto";
    private static final String ARQUIVO = "license.sys";


    private static File getArquivo(){

        File pasta = new File(
                Environment.getExternalStorageDirectory(),
                PASTA
        );

        if(!pasta.exists()){
            pasta.mkdirs();
        }

        return new File(pasta, ARQUIVO);
    }


    public static boolean existe(){

        return getArquivo().exists();

    }


    public static void salvar(String conteudo){

        try{

            FileOutputStream fos =
                    new FileOutputStream(
                            getArquivo(),
                            false
                    );

            fos.write(
                    conteudo.getBytes("UTF-8")
            );

            fos.close();


        }catch(Exception e){

            e.printStackTrace();

        }

    }


    public static String ler(){

        try{

            File arquivo = getArquivo();

            if(!arquivo.exists()){
                return null;
            }


            FileInputStream fis =
                    new FileInputStream(
                            arquivo
                    );


            byte[] dados =
                    new byte[(int)arquivo.length()];


            fis.read(dados);

            fis.close();


            return new String(
                    dados,
                    "UTF-8"
            );


        }catch(Exception e){

            return null;

        }

    }

}