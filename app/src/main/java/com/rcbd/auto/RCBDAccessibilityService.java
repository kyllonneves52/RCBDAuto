package com.rcbd.auto;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class RCBDAccessibilityService extends AccessibilityService {
    private static RCBDAccessibilityService instancia;
    private static String modo = "USSD"; // Guarda o pacote do app selecionado
    private String[] dados = new String[4];
    private int passo = 0;
    private boolean executando = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timeout;

    public static void setModo(String m){ modo = m; }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instancia = this;
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
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
            instancia.handler.postDelayed(() -> instancia.executarFluxo(), 3000);
        }
    }

    private void iniciarTimeout(){
        if(timeout!= null) handler.removeCallbacks(timeout);
        timeout = () -> pararExecucao();
        handler.postDelayed(timeout, 40000);
    }

    private void pararExecucao(){
        executando = false;
        passo = 0;
        for(int i=0; i<4; i++) dados[i] = null;
        if(timeout!= null) handler.removeCallbacks(timeout);
    }

    private void executarFluxo(){
        if(!executando) return;

        // MUDANÇA: Se não for USSD, abre qualquer app selecionado
        if(!modo.equals("USSD")){
            abrirAppEColar();
            return;
        }

        // FLUXO USSD ANTIGO - MANTIDO
        if(passo < 4){
            int delay = 2000;
            if(passo == 1) delay = 5000;
            if(passo == 2) delay = 5000;
            if(passo == 3) delay = 4000;

            String texto = dados[passo];
            colarEEnter(texto, delay, () -> {
                passo++;
                handler.postDelayed(() -> executarFluxo(), 500);
            });
        } else {
            handler.postDelayed(() -> {
                clicarPorTexto("OK", "Enviar", "Ligar", "Próximo", "→", "Next", "Send", "CONFIRMAR");
                handler.postDelayed(() -> apertarEnter(), 200);
                pararExecucao();
            }, 300);
        }
    }

    // MUDANÇA: METODO AGORA ABRE QUALQUER APP PELO PACOTE
    private void abrirAppEColar(){
        String pacote = modo;
        String mensagem = "MB: " + dados[2] + " | Numero: " + dados[3];

        Intent intent = getPackageManager().getLaunchIntentForPackage(pacote);
        if(intent == null){
            LogManager.registar(this, "ERRO: App nao encontrado: " + pacote);
            pararExecucao();
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        handler.postDelayed(() -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("rcbd", mensagem));
            handler.postDelayed(() -> {
                toqueLongoCentro();
                handler.postDelayed(() -> clicarPorTexto("COLAR", "PASTE", "Colar"), 500);
                handler.postDelayed(() -> clicarPorTexto("ENVIAR", "SEND", "→", "Enviar"), 1200);
                pararExecucao();
            }, 1000);
        }, 4000);
    }

    private void colarEEnter(String texto, int delay, Runnable proximo){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("rcbd", texto));

        handler.postDelayed(() -> {
            AccessibilityNodeInfo node = getRootInActiveWindow();
            if(node!= null){
                AccessibilityNodeInfo campo = encontrarCampoEditavel(node);
                if(campo!= null){
                    campo.performAction(AccessibilityServiceInfo.ACTION_FOCUS);
                    handler.postDelayed(() -> {
                        campo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                        handler.postDelayed(() -> {
                            clicarPorTexto("OK", "Enviar", "Ligar", "Próximo", "→");
                            handler.postDelayed(() -> apertarEnter(), 200);
                        }, 300);
                    }, 100);
                } else {
                    toqueLongoCentro();
                    handler.postDelayed(() -> clicarPorTexto("COLAR","PASTE", "Colar"), 400);
                }
            }
            handler.postDelayed(proximo, delay);
        }, 200);
    }

    private void apertarEnter(){
        Path path = new Path();
        path.moveTo(540, 1800);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 100));
        dispatchGesture(builder.build(), null, null);
    }

    private AccessibilityNodeInfo encontrarCampoEditavel(AccessibilityNodeInfo node){
        if(node == null) return null;
        if(node.isEditable() && node.isEnabled() && node.isVisibleToUser()){
            return node;
        }
        for(int i=0;i<node.getChildCount();i++){
            AccessibilityNodeInfo achado = encontrarCampoEditavel(node.getChild(i));
            if(achado!= null) return achado;
        }
        return null;
    }

    private void clicarPorTexto(String... textos){
        AccessibilityNodeInfo node = getRootInActiveWindow();
        if(node == null) return;
        for(String t : textos){
            if(clicarRecursivo(node, t.toLowerCase())) break;
        }
    }

    private boolean clicarRecursivo(AccessibilityNodeInfo node, String texto){
        if(node == null) return false;
        String txt = node.getText()!=null? node.getText().toString().toLowerCase() : "";
        String desc = node.getContentDescription()!=null? node.getContentDescription().toString().toLowerCase() : "";
        if(txt.contains(texto) || desc.contains(texto)){
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        }
        for(int i=0;i<node.getChildCount();i++){
            if(clicarRecursivo(node.getChild(i), texto)) return true;
        }
        return false;
    }

    private void toqueLongoCentro(){
        Path path = new Path();
        path.moveTo(600, 1000);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 500));
        dispatchGesture(builder.build(), null, null);
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt(){ pararExecucao(); }
    public static RCBDAccessibilityService getInstancia(){ return instancia; }
}