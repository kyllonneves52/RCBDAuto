package com.rcbd.auto;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

public class QueueManager {

    private static final String PREF = "RCBD_QUEUE";
    private static final String KEY = "pedidos";

    public static void adicionar(Context context, String mb, String numero) {

        try {
            SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

            String dados = sp.getString(KEY, "[]");

            JSONArray fila = new JSONArray(dados);

            JSONObject pedido = new JSONObject();

            pedido.put("mb", mb);
            pedido.put("numero", numero);
            pedido.put("estado", "aguardando");

            fila.put(pedido);

            sp.edit()
                    .putString(KEY, fila.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static JSONObject pegarProximo(Context context) {

        try {

            SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

            JSONArray fila = new JSONArray(
                    sp.getString(KEY, "[]")
            );

            if(fila.length() > 0){
                return fila.getJSONObject(0);
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }


    public static int quantidade(Context context){

        try{

            SharedPreferences sp =
                    context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

            JSONArray fila =
                    new JSONArray(sp.getString(KEY,"[]"));

            return fila.length();

        }catch(Exception e){
            return 0;
        }

    }
}
