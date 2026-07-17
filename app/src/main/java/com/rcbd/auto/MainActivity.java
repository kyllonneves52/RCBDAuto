package com.rcbd.auto;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

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
    Spinner spinnerApp;
    String modoApp = "USSD";
    ArrayList<String> listaPacotes = new ArrayList<>(); // NOVO: guarda os pacotes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, WhatsAppNotificationService.class));

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 100);
        }

        mb = findViewById(R.id.mb);
        numero = findViewById(R.id.numero);
        enviar = findViewById(R.id.enviar);
        atualizar = findViewById(R.id.atualizar);
        diagnostico = findViewById(R.id.diagnostico);
        permissoes = findViewById(R.id.permissoes);
        status = findViewById(R.id.status);
        fila = findViewById(R.id.fila);
        log = findViewById(R.id.log);
        spinnerApp = findViewById(R.id.spinnerApp);

        // NOVO: CARREGA TODOS OS APPS QUE ACEITAM TEXTO
        carregarListaApps();

        enviar.setEnabled(true);

        atualizarEstado();

        enviar.setOnClickListener(v -> {
            String pacoteMB = mb.getText().toString().trim();
            String tel = numero.getText().toString().trim().replace(" ", "");

            if(pacoteMB.isEmpty() || tel.isEmpty()){
                Toast.makeText(this, "Preencha MB e número", Toast.LENGTH_SHORT).show();
                return;
            }

            QueueManager.adicionar(this, pacoteMB, tel);
            fila.setText("Pedidos: " + QueueManager.quantidade(this));
            log.setText("Pedido criado:\n" + pacoteMB + " MB\nNúmero: " + tel + "\nModo: " + modoApp);
            Toast.makeText(this, "Pedido na fila - Modo: " + modoApp, Toast.LENGTH_LONG).show();

            RCBDAccessibilityService.setModo(modoApp);
            UssdManager.iniciarEnvio(this);
            mb.setText("");
            numero.setText("");
        });

        atualizar.setOnClickListener(v -> atualizarEstado());
        diagnostico.setOnClickListener(v -> {
            log.setText("Diagnóstico iniciado...");
 status.setText(StatusManager.verificar(this) + "
Modo: " + modoApp);
        });
        permissoes.setOnClickListener(v -> PermissionManager.abrirAcessibilidade(this));
    }

    // NOVO: FUNCAO QUE BUSCA TODOS OS APPS
    private void carregarListaApps(){
        ArrayList<String> listaApps = new ArrayList<>();

        listaApps.add("USSD Direto");
        listaPacotes.add("USSD");

        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);

        for(ResolveInfo info : apps){
            String nome = info.loadLabel(pm).toString();
            String pacote = info.activityInfo.packageName;
            if(!listaPacotes.contains(pacote)){
                listaApps.add(nome);
                listaPacotes.add(pacote);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaApps);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerApp.setAdapter(adapter);

        spinnerApp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modoApp = listaPacotes.get(position); // Guarda o pacote direto
                atualizarEstado();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void atualizarEstado(){
 status.setText(StatusManager.verificar(this) + "
Modo: " + modoApp);
        fila.setText("Pedidos: " + QueueManager.quantidade(this));
        log.setText("RCBDAuto iniciado - Modo: " + modoApp);
    }
}