package com.rcbd.auto;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class RCBDAccessibilityService extends AccessibilityService {
    private static RCBDAccessibilityService instancia;
    private String[] dados = new String[4];
    private int passo = 0;
    private boolean executando = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timeout;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instancia = this;
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        setServiceInfo(info);
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
            instancia.handler.postDelayed(() -> instancia.executarFluxo(), 5000);
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
        if(!executando || passo >= 4) return;

        String texto = dados[passo];
        colarApenas(texto, 2000, () -> {
            passo++;
            handler.postDelayed(() -> executarFluxo(), 2000);
        });
    }

    private void colarApenas(String texto, int delay, Runnable proximo){
        // 1. Coloca no clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("rcbd", texto));

        handler.postDelayed(() -> {
            // 2. Procura QUALQUER campo editável na tela e cola
            AccessibilityNodeInfo node = getRootInActiveWindow();
            if(node!= null){
                AccessibilityNodeInfo campo = encontrarCampoEditavel(node);
                if(campo!= null){
                    campo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    handler.postDelayed(() -> {
                        campo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                    }, 100);
                } else {
                    // Fallback: cola global
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_PASTE);
                }
            }

            handler.postDelayed(proximo, delay);

        }, 200);
    }

    private AccessibilityNodeInfo encontrarCampoEditavel(AccessibilityNodeInfo node){
        if(node == null) return null;

        // Se for editável retorna ele
        if(node.isEditable() && node.isEnabled() && node.isVisibleToUser()){
            return node;
        }

        // Senão procura nos filhos
        for(int i=0;i<node.getChildCount();i++){
            AccessibilityNodeInfo achado = encontrarCampoEditavel(node.getChild(i));
            if(achado!= null) return achado;
        }
        return null;
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt(){ pararExecucao(); }
    public static RCBDAccessibilityService getInstancia(){ return instancia; }
}
