package com.rcbd.auto;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;

public class RCBDAccessibilityService extends AccessibilityService {
    private static RCBDAccessibilityService instancia;
    private int passo = 0;
    private String[] dados = new String[4];
    private boolean executando = false;
    private Handler handler = new Handler(Looper.getMainLooper());
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
            instancia.handler.postDelayed(() -> instancia.executarFluxo(), 5000); // 5s igual teu macro
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
        clicar(540, 1850); // Posição do botão CONFIRMAR
        LogManager.registar(this, "RCBD executado: " + dados[2] + "MB → " + dados[3]);
        QueueManager.removerPrimeiro(this);
        pararExecucao();
        handler.postDelayed(() -> UssdManager.iniciarEnvio(this), 3000);
        });});});
    }

    private void colarEEnter(String texto, int delay, Runnable proximo){
        // 1. Coloca no clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("rcbd", texto));

        // 2. Toque longo no campo do discador pra aparecer "Colar"
        handler.postDelayed(() -> {
            toqueLongo(540, 1500); // Centro da tela onde fica o campo

            // 3. Espera e clica em "Colar"
            handler.postDelayed(() -> {
                clicar(540, 1600); // Posição do botão "Colar"

                // 4. Espera colar e clica em Enviar/Ligar
                handler.postDelayed(() -> {
                    clicar(540, 1900); // Botão de ligar/enviar
                    handler.postDelayed(proximo, delay);
                }, 500);

            }, 400);

        }, 200);
    }

    private void clicar(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 100));
        dispatchGesture(builder.build(), null, null);
    }

    private void toqueLongo(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 500)); // 500ms = toque longo
        dispatchGesture(builder.build(), null, null);
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt(){ pararExecucao(); }
    public static RCBDAccessibilityService getInstancia(){ return instancia; }
}
