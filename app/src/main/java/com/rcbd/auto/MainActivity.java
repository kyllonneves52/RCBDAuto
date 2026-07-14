package com.rcbd.auto;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    EditText mb, numero;
    Button enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mb = findViewById(R.id.mb);
        numero = findViewById(R.id.numero);
        enviar = findViewById(R.id.enviar);

        enviar.setOnClickListener(v -> {

            String pacote = mb.getText().toString().trim();
            String tel = numero.getText().toString().trim();


            if(pacote.isEmpty() || tel.isEmpty()){

                Toast.makeText(
                        this,
                        "Preencha MB e número",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            // Guardar pedido na fila
            QueueManager.adicionar(
                    this,
                    pacote,
                    tel
            );


            int total = QueueManager.quantidade(this);


            Toast.makeText(
                    this,
                    "Pedido guardado: "
                    + pacote
                    + "MB para "
                    + tel
                    + "\nNa fila: "
                    + total,
                    Toast.LENGTH_LONG
            ).show();


            mb.setText("");
            numero.setText("");

        });

    }
}
