package com.rcbd.auto;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class LicenseStorage {

    private static File getArquivo(Context context) {
        File pasta = new File(context.getFilesDir(), ".rcbd");
        if(!pasta.exists()){
            pasta.mkdirs();
        }
        return new File(pasta, "license.sys");
    }

    public static void criarSeNaoExistir(Context context, String conteudo) {
        File f = getArquivo(context);
        try{
            if(!f.exists()){
                FileWriter fw = new FileWriter(f);
                fw.write(conteudo);
                fw.close();
            }
        }catch(Exception e){ e.printStackTrace(); }
    }

    public static void atualizarTodos(Context context, String conteudo){
        File f = getArquivo(context);
        try{
            FileWriter fw = new FileWriter(f,false);
            fw.write(conteudo);
            fw.close();
        }catch(Exception e){ e.printStackTrace(); }
    }

    public static String lerLicenca(Context context){
        try{
            File f = getArquivo(context);
            if(!f.exists()){ return null; }
            FileInputStream fis = new FileInputStream(f);
            byte[] dados = new byte[(int)f.length()];
            fis.read(dados);
            fis.close();
            return new String(dados);
        }catch(Exception e){ return null; }
    }
}