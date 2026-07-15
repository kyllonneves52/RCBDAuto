package com.rcbd.auto;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class RCBDAccessibilityService extends AccessibilityService {
    private static RCBDAccessibilityService instancia;
    private int passo = 0;
    private String[] dados = new String[4]; // 0=8 1=2 2=MB 3=Numero
    private boolean executando = false;
    private Handler handler = new Handler();
    private Runnable timeout;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instancia = this;
        Toast.makeText(this, "RCBDAuto Ativo - Seguro", Toast.LENGTH_SHORT).show();
    }

    public static void iniciarExecucao(String mb, String numero){
        if(instancia!= null){
            instancia.dados[0] = "8";
            instancia.dados[1] = "2";
            instancia.dados[2] = mb;
            instancia.dados[3] = numero;
            instancia.executando = true;
            instancia.passo = 0;
            instancia.iniciarTimeout();
        }
    }

    private void iniciarTimeout(){
        if(timeout!= null) handler.removeCallbacks(timeout);
        timeout = () -> {
            LogManager.registar(this, "Timeout 25s: parando");
            pararExecucao();
        };
        handler.postDelayed(timeout, 25000);
    }

    private void pararExecucao(){
        executando = false;
        passo = 0;
        for(int i=0; i<4; i++) dados[i] = null;
        if(timeout!= null) handler.removeCallbacks(timeout);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!executando) return; // FICA PARADO SE NÃO MANDAR
        if (event == null || event.getPackageName() == null) return;

        String pkg = event.getPackageName().toString();
        if (!pkg.contains("com.android.phone")) return;

        AccessibilityNodeInfo node = getRootInActiveWindow();
        if (node == null) return;

        if (passo < 4) {
            digitarOuClicar(node, dados[passo]);
            passo++;
        }

        if (passo >= 4) {
            LogManager.registar(this, "Ciclo finalizado");
            QueueManager.removerPrimeiro(this);
            pararExecucao();
            handler.postDelayed(() -> UssdManager.iniciarEnvio(this), 3000);
        }
    }

    private void digitarOuClicar(AccessibilityNodeInfo node, String texto){
        if(node.isEditable()){
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, texto);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
            handler.postDelayed(() -> clicarEnviar(node), 800);
        } else {
            clicarEnviar(node);
        }
    }

    private void clicarEnviar(AccessibilityNodeInfo node){
        if(node == null) return;
        String txt = node.getText()!=null? node.getText().toString().toLowerCase() : "";
        if(txt.contains("enviar") || txt.contains("ok") || txt.contains("1")){
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
        for(int i=0;i<node.getChildCount();i++) clicarEnviar(node.getChild(i));
    }

    @Override
    public void onInterrupt(){
        pararExecucao();
    }

    public static RCBDAccessibilityService getInstancia(){ return instancia; }
}