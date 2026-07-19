package com.rcbd.auto;

public class LicenseInfo {


    public static int pegarDias(String texto){

        try{

            String linha =
                    texto.split("DIAS=")[1]
                    .split("\n")[0];

            return Integer.parseInt(linha);


        }catch(Exception e){

            return 0;

        }

    }



    public static String pegarData(String texto){

        try{

            return texto.split(
                    "ULTIMA_DATA=")[1]
                    .trim();


        }catch(Exception e){

            return "Sem data";

        }

    }



    public static String mostrar(){

        String licenca =
                LicenseStorage.lerLicenca();


        if(licenca == null){

            return "Licença não encontrada";

        }


        int dias =
                pegarDias(licenca);


        String data =
                pegarData(licenca);



        return
        "LICENÇA ATIVA\n\n"+
        "Dias restantes: "+dias+"\n"+
        "Última atualização: "+data;


    }


}
