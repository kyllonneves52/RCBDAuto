package com.rcbd.auto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import org.json.JSONObject;

public class UssdManager {
    
    public static void iniciarEnvio(Context context){
        JSONObject obj = QueueManager.pegarProximo(context);
        if(obj == null){
            return;
        }
        
        String numero = obj.getString("numero");
        LogManager.registar(context, "Iniciando envio para: " + numero);
        
        String ussd = "*162#";
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Uri.encode(ussd)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}