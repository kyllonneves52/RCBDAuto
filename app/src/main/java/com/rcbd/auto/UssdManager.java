package com.rcbd.auto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UssdManager {
    
    public static void iniciarEnvio(Context context){
        String numero = QueueManager.pegarPrimeiroNumero(context);
        if(numero == null){
            return;
        }
        
        // Reseta o serviço
        if(RCBDAccessibilityService.getInstancia() != null){
            RCBDAccessibilityService.getInstancia().resetarEtapa();
        }
        
        LogManager.registar(context, "Iniciando envio para: " + numero);
        
        // Abre o *162#
        String ussd = "*162#";
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Uri.encode(ussd)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}