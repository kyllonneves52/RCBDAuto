package com.rcbd.auto;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ActivationActivity extends Activity {


    EditText chave;
    Button ativar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_activation);



        chave = findViewById(R.id.chave);
        ativar = findViewById(R.id.ativar);



        ativar.setOnClickListener(v -> {


            String codigo =
                    chave.getText()
                    .toString()
                    .trim()
                    .toUpperCase();



            boolean resultado =
                    LicenseManager.ativar(
                            this,
                            codigo
                    );



            if(resultado){


                Toast.makeText(
                        this,
                        "Licença ativada com sucesso",
                        Toast.LENGTH_LONG
                ).show();



                Intent i =
                        new Intent(
                                this,
                                MainActivity.class
                        );


                startActivity(i);

                finish();



            }else{


                Toast.makeText(
                        this,
                        "Chave inválida",
                        Toast.LENGTH_LONG
                ).show();


            }



        });


    }


}
