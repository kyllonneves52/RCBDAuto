package com.rcbd.auto;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


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
    TextView licenca;

    Spinner spinnerApp;


    String modoApp = "USSD";

    ArrayList<String> listaPacotes = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        // NOVO SISTEMA DE LICENÇA

        if(!LicenseManager.estaAtivado(this)){


            Intent i =
                    new Intent(
                            this,
                            ActivationActivity.class
                    );


            startActivity(i);


            finish();


            return;

        }



        setContentView(
                R.layout.activity_main
        );



        iniciarPermissoes();



        startService(
                new Intent(
                        this,
                        WhatsAppNotificationService.class
                )
        );



        ligarComponentes();


        iniciarContadorLicenca();


        iniciarVerificacaoLicenca();


        carregarListaApps();


        atualizarEstado();




        enviar.setOnClickListener(v -> {


            String pacoteMB =
                    mb.getText()
                    .toString()
                    .trim();



            String tel =
                    numero.getText()
                    .toString()
                    .trim()
                    .replace(" ","");



            if(pacoteMB.isEmpty() || tel.isEmpty()){


                Toast.makeText(
                        this,
                        "Preencha MB e número",
                        Toast.LENGTH_SHORT
                ).show();


                return;

            }



            QueueManager.adicionar(
                    this,
                    pacoteMB,
                    tel
            );



            RCBDAccessibilityService.setModo(
                    modoApp
            );



            UssdManager.iniciarEnvio(
                    this
            );



            log.setText(
                    "Pedido enviado\n\n"
                    + pacoteMB
                    + " MB\n"
                    + tel
            );



            mb.setText("");
            numero.setText("");

        });




        atualizar.setOnClickListener(v ->
                atualizarEstado()
        );




        diagnostico.setOnClickListener(v -> {


            status.setText(
                    StatusManager.verificar(this)
                    +
                    "\nModo: "
                    +
                    modoApp
            );


        });




        permissoes.setOnClickListener(v ->

                PermissionManager.abrirAcessibilidade(this)

        );


    }






    private void ligarComponentes(){


        mb=findViewById(R.id.mb);

        numero=findViewById(R.id.numero);


        enviar=findViewById(R.id.enviar);

        atualizar=findViewById(R.id.atualizar);

        diagnostico=findViewById(R.id.diagnostico);

        permissoes=findViewById(R.id.permissoes);



        status=findViewById(R.id.status);

        fila=findViewById(R.id.fila);

        log=findViewById(R.id.log);

        licenca=findViewById(R.id.licenca);


        spinnerApp=findViewById(R.id.spinnerApp);


    }






    private void iniciarContadorLicenca(){



        Timer timer = new Timer();



        timer.scheduleAtFixedRate(

                new TimerTask(){


                    @Override
                    public void run(){


                        runOnUiThread(() -> {


                            licenca.setText(

                                    "Validade: "
                                    +
                                    LicenseManager.tempoRestante(
                                            MainActivity.this
                                    )

                            );


                        });


                    }


                },

                0,

                60000

        );


    }







    private void iniciarPermissoes(){


        if(Build.VERSION.SDK_INT >= 23){


            if(checkSelfPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED){



                requestPermissions(

                        new String[]{

                                android.Manifest.permission.READ_EXTERNAL_STORAGE,

                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE

                        },

                        200

                );

            }

        }

    }






    private void carregarListaApps(){


        ArrayList<String> nomes =
                new ArrayList<>();


        nomes.add("USSD Direto");


        listaPacotes.add("USSD");



        PackageManager pm =
                getPackageManager();



        Intent intent =
                new Intent(
                        Intent.ACTION_SEND
                );


        intent.setType(
                "text/plain"
        );



        List<ResolveInfo> apps =
                pm.queryIntentActivities(
                        intent,
                        0
                );



        for(ResolveInfo info: apps){


            String nome =
                    info.loadLabel(pm)
                    .toString();



            String pacote =
                    info.activityInfo.packageName;



            if(!listaPacotes.contains(pacote)){


                nomes.add(nome);

                listaPacotes.add(pacote);


            }


        }




        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(

                        this,

                        android.R.layout.simple_spinner_item,

                        nomes

                );



        adapter.setDropDownViewResource(

                android.R.layout.simple_spinner_dropdown_item

        );



        spinnerApp.setAdapter(adapter);




        spinnerApp.setOnItemSelectedListener(

                new AdapterView.OnItemSelectedListener(){



                    public void onItemSelected(

                            AdapterView<?> p,

                            View v,

                            int pos,

                            long id

                    ){


                        modoApp =
                                listaPacotes.get(pos);


                    }




                    public void onNothingSelected(

                            AdapterView<?> p){}



                }


        );


    }







    private void atualizarEstado(){



        status.setText(

                StatusManager.verificar(this)

                +

                "\nModo: "

                +

                modoApp

        );



        fila.setText(

                "Pedidos: "

                +

                QueueManager.quantidade(this)

        );



        log.setText(

                "RCBDAuto iniciado"

        );


    }

private void iniciarVerificacaoLicenca(){

    Timer timer = new Timer();

    TimerTask tarefa = new TimerTask(){

        @Override
        public void run(){

            runOnUiThread(() -> {

                if(!LicenseManager.estaAtivado(MainActivity.this)){

                    QueueManager.limpar(MainActivity.this);

                    stopService(
                            new Intent(
                                    MainActivity.this,
                                    WhatsAppNotificationService.class
                            )
                    );

                    Toast.makeText(
                            MainActivity.this,
                            "Licença expirada",
                            Toast.LENGTH_LONG
                    ).show();


                    // Cancela a verificação
                    tarefa.cancel();


                    Intent i = new Intent(
                            MainActivity.this,
                            ActivationActivity.class
                    );

                    i.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                            |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(i);

                    finish();

                }

            });

        }

    };


    timer.scheduleAtFixedRate(
            tarefa,
            60000,
            60000
    );

}

}