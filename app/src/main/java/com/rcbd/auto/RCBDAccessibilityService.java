package com.rcbd.auto;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.os.Handler;
import android.widget.Toast;

public class RCBDAccessibilityService extends AccessibilityService {
    private static RCBDAccessibilityService instancia;
    private int passo = 0;
    private String[] dados = new String[4];
    private boolean executando = false;
    private Handler handler = new Handler();
    private Runnable timeout;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instancia = this;
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
            instancia.handler.postDelayed(() -> instancia.executarFluxo(), 5000);// Pause 5s igual teu macro depois do *162#
        }
    }

    private void iniciarTimeout(){
        if(timeout!= null) handler.removeCallbacks(timeout);
        timeout = () -> pararExecucao();
        handler.postDelayed(timeout, 25000);
    }

    private void pararExecucao(){
        executando = false;
        passo = 0;
        for(int i=0; i<4; i++) dados[i] = null;
        if(timeout!= null) handler.removeCallbacks(timeout);
    }

    private void executarFluxo(){
        if(!executando) return;

        // PASSO 1: COLAR 8 + ENTER + PAUSE 2s
        colarEEnter(dados[0], 2000, () -> {
        // PASSO 2: COLAR 2 + ENTER + PAUSE 5s
        colarEEnter(dados[1], 5000, () -> {
        // PASSO 3: COLAR MB + ENTER + PAUSE 5s
        colarEEnter(dados[2], 5000, () -> {
        // PASSO 4: COLAR NUMERO + ENTER + PAUSE 4s
        colarEEnter(dados[3], 4000, () -> {
        // PASSO 5: CLICAR CONFIRMAR
        AccessibilityNodeInfo node = getRootInActiveWindow();
        clicarBotao(node, "confirmar");
        LogManager.registar(this, "RCBD executado: " + dados[2] + "MB → " + dados[3]);
        QueueManager.removerPrimeiro(this);
        pararExecucao();
        handler.postDelayed(() -> UssdManager.iniciarEnvio(this), 3000);
        });});});});
    }

    private void colarEEnter(String texto, int delay, Runnable proximo){
        AccessibilityNodeInfo node = getRootInActiveWindow();
        colarNoCampo(node, texto);
        handler.postDelayed(() -> {
            AccessibilityNodeInfo n2 = getRootInActiveWindow();
            clicarBotao(n2, "enviar");
            handler.postDelayed(proximo, delay);
        }, 500);
    }

    private void colarNoCampo(AccessibilityNodeInfo node, String texto){
        if(node == null ||!node.isEditable()) return;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("rcbd", texto);
        clipboard.setPrimaryClip(clip);
        node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        handler.postDelayed(() -> node.performAction(AccessibilityNodeInfo.ACTION_PASTE), 200);
    }

    private void clicarBotao(AccessibilityNodeInfo node, String texto){
        if(node == null) return;
        String txt = node.getText()!=null? node.getText().toString().toLowerCase() : "";
        String desc = node.getContentDescription()!=null? node.getContentDescription().toString().toLowerCase() : "";
        if(txt.contains(texto) || desc.contains(texto)){
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
        for(int i=0;i<node.getChildCount();i++) clicarBotao(node.getChild(i), texto);
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt(){ pararExecucao(); }
    public static RCBDAccessibilityService getInstancia(){ return instancia; }
}