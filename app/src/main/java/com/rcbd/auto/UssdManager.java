package com.rcbd.auto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;


public class UssdManager {


    private static UssdStateMachine maquina =
            new UssdStateMachine();



    public static void iniciarEnvio(Context context){


        try{


            org.json.JSONObject pedido =
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


            if(mb == null || numero == null){


                Toast.makeText(
                        context,
                        "Dados do envio vazios",
                        Toast.LENGTH_SHORT
                ).show();


                return;

            }


RCBDAccessibilityService service =
        RCBDAccessibilityService.getInstancia();


if(service != null){

    service.resetarEtapa();

}


            maquina.iniciar();



            LogManager.registar(
                    context,
                    "Iniciando USSD"
            );



            abrirUSSD(context);



        }catch(Exception e){


            maquina.erro();


            LogManager.registar(
                    context,
                    "Erro USSD: "
                    + e.getMessage()
            );


        }


    }





    private static void abrirUSSD(Context context){


        String codigo =
                Uri.encode("*162#");



        Intent intent =
                new Intent(
                        Intent.ACTION_CALL,
                        Uri.parse(
                                "tel:"
                                + codigo
                        )
                );



        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        );



        context.startActivity(intent);



        maquina.proximo();



    }





    public static UssdStateMachine.Estado estadoAtual(){


        return maquina.getEstado();


    }





    public static void avancar(){


        maquina.proximo();


    }



}