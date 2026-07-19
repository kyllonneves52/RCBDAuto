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
import android.view.AdapterView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Build;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

        // 1. VERIFICA LICENÇA AQUI PRIMEIRO
        if(!LicenseManager.verificar(this)){
            Toast.makeText(this, "Licença expirada ou dispositivo não autorizado", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        startService(new Intent(this, WhatsAppNotificationService.class));

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 100);
        }

        licenca = findViewById(R.id.licenca);
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
        
        checkAndRequestPermissions(); // <- 2. PEDE PERMISSÃO CORRETA

        carregarListaApps();
        enviar.setEnabled(true);
        atualizarEstado();

        licenca.setText("Validade: " + LicenseManager.getTempoRestante());

        // 3. ATUALIZA O CONTADOR A CADA 1 SEGUNDO
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                runOnUiThread(() -> {
                    licenca.setText("Validade: " + LicenseManager.getTempoRestante());
                });
            }
        }, 0, 1000);

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
            status.setText(StatusManager.verificar(this) + "\nModo: " + modoApp);
        });
        permissoes.setOnClickListener(v -> PermissionManager.abrirAcessibilidade(this));
    } // fecha onCreate

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        } else {
            // Android 10-
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            }
        }
    }

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
                modoApp = listaPacotes.get(position);
                atualizarEstado();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void atualizarEstado(){
        status.setText(StatusManager.verificar(this) + "\nModo: " + modoApp);
        fila.setText("Pedidos: " + QueueManager.quantidade(this));
        log.setText("RCBDAuto iniciado - Modo: " + modoApp);
    }
}