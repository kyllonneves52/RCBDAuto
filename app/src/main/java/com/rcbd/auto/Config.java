package com.rcbd.auto;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    private static final String PREF = "RCBD_CONFIG";

    private static final String MB = "mb_atual";
    private static final String NUMERO = "numero_atual";


    public static void salvarPedidoAtual(
            Context context,
            String mb,
            String numero
    ){

        SharedPreferences sp =
                context.getSharedPreferences(
                        PREF,
                        Context.MODE_PRIVATE
                );


        sp.edit()
                .putString(MB, mb)
                .putString(NUMERO, numero)
                .apply();
    }



    public static String getMB(Context context){

        SharedPreferences sp =
                context.getSharedPreferences(
                        PREF,
                        Context.MODE_PRIVATE
                );

        return sp.getString(MB, "");
    }



    public static String getNumero(Context context){

        SharedPreferences sp =
                context.getSharedPreferences(
                        PREF,
                        Context.MODE_PRIVATE
                );

        return sp.getString(NUMERO, "");
    }

}
