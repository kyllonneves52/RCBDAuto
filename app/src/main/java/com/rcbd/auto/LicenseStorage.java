package com.rcbd.auto;

import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;

public class LicenseStorage {


    private static File getArquivo(){

    File arquivo = new File(
            Environment.getExternalStorageDirectory(),
            "Android/license.sys"
    );


    File pasta = arquivo.getParentFile();


    if(pasta != null && !pasta.exists()){
        pasta.mkdirs();
    }


    return arquivo;

}


    public static boolean existe(){

        return getArquivo().exists();

    }



    public static void salvar(String dados){

        try{

            FileWriter fw =
                    new FileWriter(
                            getArquivo(),
                            false
                    );

            fw.write(dados);

            fw.close();


        }catch(Exception e){

            e.printStackTrace();

        }

    }



    public static String ler(){

        try{

            File f = getArquivo();

            FileInputStream fis =
                    new FileInputStream(f);


            byte[] dados =
                    new byte[(int)f.length()];


            fis.read(dados);

            fis.close();


            return new String(dados);


        }catch(Exception e){

            return null;

        }

    }

}