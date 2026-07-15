package com.rcbd.auto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import org.json.JSONObject;
import org.json.JSONException;

public class UssdManager {
    
    public static void iniciarEnvio(Context context){
        JSONObject obj = QueueManager.pegarProximo(context);
        if(obj == null){
            LogManager.registar(context, "Fila vazia");
            return;
        }
        
        try {
            String numero = obj.getString("numero");
            String mb = obj.getString("mb"); // PEGA O MB TAMBEM
            LogManager.registar(context, "Iniciando envio para: " + numero);
            
            String ussd = "*162#";
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + Uri.encode(ussd)));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            
            // ESPERA 2s E MANDA PRO ACCESSIBILITY FAZER OS CLIQUES
            new android.os.Handler().postDelayed(() -> {
                RCBDAccessibilityService.iniciarExecucao(mb, numero);
            }, 2000);
            
        } catch (JSONException e) {
            LogManager.registar(context, "Erro ao ler dados: " + e.getMessage());
        }
    }
}