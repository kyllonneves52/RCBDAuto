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

            String pacote = mb.getText().toString();
            String tel = numero.getText().toString();

            if(pacote.isEmpty() || tel.isEmpty()){
                Toast.makeText(
                    this,
                    "Preencha MB e número",
                    Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Toast.makeText(
                this,
                "Teste enviado: "+pacote+"MB para "+tel,
                Toast.LENGTH_LONG
            ).show();

        });

    }
}
