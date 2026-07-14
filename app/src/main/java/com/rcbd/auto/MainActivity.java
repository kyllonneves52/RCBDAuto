package com.rcbd.auto;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {


    EditText mb;
    EditText numero;


    Button enviar;
    Button atualizar;
    Button diagnostico;
    Button permissoes;


    TextView status;
    TextView fila;
    TextView log;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        mb = findViewById(R.id.mb);
        numero = findViewById(R.id.numero);


        enviar = findViewById(R.id.enviar);
        atualizar = findViewById(R.id.atualizar);
        diagnostico = findViewById(R.id.diagnostico);
        permissoes = findViewById(R.id.permissoes);


        status = findViewById(R.id.status);
        fila = findViewById(R.id.fila);
        log = findViewById(R.id.log);



        atualizarEstado();



        enviar.setOnClickListener(v -> {


            String pacote =
                    mb.getText()
                    .toString()
                    .trim();


            String tel =
                    numero.getText()
                    .toString()
                    .trim();



            if(pacote.isEmpty() || tel.isEmpty()){


                Toast.makeText(
                        this,
                        "Preencha MB e número",
                        Toast.LENGTH_SHORT
                ).show();


                return;

            }



            QueueManager.adicionar(
                    this,
                    pacote,
                    tel
            );



            int total =
                    QueueManager.quantidade(this);



            fila.setText(
                    "Pedidos: "
                    + total
            );



            log.setText(
                    "Pedido criado:\n"
                    + pacote
                    + " MB\nNúmero: "
                    + tel
            );



            Toast.makeText(
                    this,
                    "Pedido colocado na fila",
                    Toast.LENGTH_LONG
            ).show();



            UssdManager.iniciarEnvio(this);



            mb.setText("");
            numero.setText("");

        });





        atualizar.setOnClickListener(v -> {


            atualizarEstado();


        });





        diagnostico.setOnClickListener(v -> {


            log.setText(
                    "Diagnóstico iniciado..."
            );


            status.setText(
                    StatusManager.verificar(this)
            );


            Toast.makeText(
                    this,
                    "Teste RCBDAuto",
                    Toast.LENGTH_SHORT
            ).show();


        });





        permissoes.setOnClickListener(v -> {


            PermissionManager.abrirAcessibilidade(this);


        });


    }






    private void atualizarEstado(){


        status.setText(
                StatusManager.verificar(this)
        );



        fila.setText(
                "Pedidos: "
                + QueueManager.quantidade(this)
        );



        log.setText(
                "RCBDAuto iniciado"
        );


    }


}