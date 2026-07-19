package com.rcbd.auto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class LicenseStorage {


    private static File[] obterArquivos() {

        String base =
                android.os.Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();


        return new File[]{

                new File(base + "/Android/media/.rcbd/license.sys"),

                new File(base + "/Download/.rcbd/license.sys"),

                new File(base + "/.rcbd/license.sys")

        };

    }



    public static void criarSeNaoExistir(String conteudo) {

        File[] arquivos = obterArquivos();


        for(File f : arquivos){

            try{

                if(!f.getParentFile().exists()){

                    f.getParentFile().mkdirs();

                }


                if(!f.exists()){

                    FileWriter fw =
                            new FileWriter(f);

                    fw.write(conteudo);

                    fw.close();

                }


            }catch(Exception e){

                e.printStackTrace();

            }

        }

    }




    public static void atualizarTodos(String conteudo){

        File[] arquivos = obterArquivos();


        for(File f : arquivos){

            try{

                if(!f.getParentFile().exists()){

                    f.getParentFile().mkdirs();

                }


                FileWriter fw =
                        new FileWriter(f,false);

                fw.write(conteudo);

                fw.close();


            }catch(Exception e){

                e.printStackTrace();

            }

        }

    }





    public static String lerLicenca(){

        try{

            File[] arquivos = obterArquivos();


            File encontrada = null;


            for(File f : arquivos){

                if(f.exists()){

                    encontrada = f;
                    break;

                }

            }



            if(encontrada == null){

                return null;

            }



            FileInputStream fis =
                    new FileInputStream(encontrada);


            byte[] dados =
                    new byte[(int)encontrada.length()];


            fis.read(dados);

            fis.close();


            return new String(dados);


        }catch(Exception e){

            return null;

        }

    }


}
