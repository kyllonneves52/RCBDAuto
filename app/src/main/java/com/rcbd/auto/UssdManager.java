package com.rcbd.auto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONObject;

public class UssdManager {


    public static void iniciarEnvio(Context context){


        try{


            JSONObject pedido =
                    QueueManager.pegarProximo(context);



            if(pedido == null){


                Toast.makeText(
                        context,
                        "Fila vazia",
                        Toast.LENGTH_SHORT
                ).show();


                return;

            }



            String mb =
                    pedido.getString("mb");


            String numero =
                    pedido.getString("numero");



            Config.salvarPedidoAtual(
                    context,
                    mb,
                    numero
            );



            RCBDAccessibilityService service =
                    RCBDAccessibilityService.getInstancia();



            if(service != null){

                // preparar primeira etapa
                service.resetarEtapa();

            }




            String ussd =
                    Uri.encode("*162#");



            Intent intent =
                    new Intent(
                            Intent.ACTION_CALL,
                            Uri.parse("tel:" + ussd)
                    );



            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );


            context.startActivity(intent);



        }catch(Exception e){


            e.printStackTrace();


        }

    }

}
