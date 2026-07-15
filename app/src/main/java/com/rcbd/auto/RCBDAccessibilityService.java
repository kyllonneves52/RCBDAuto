package com.rcbd.auto;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class RCBDAccessibilityService extends AccessibilityService {

    private static RCBDAccessibilityService instancia;
    private int passo = 0; // agora é só contador
    private Handler handler = new Handler();
    private String[] dados = new String[4];

    @Override
    protected void onServiceConnected(){
        super.onServiceConnected();
        instancia = this;
        Toast.makeText(this, "RCBDAuto Ativo - Modo Direto", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root == null) return;

        // Pega os dados 1 vez só
        if(dados[0] == null){
            dados[0] = "8"; // Menu Internet
            dados[1] = "2"; // Menu Transferir
            dados[2] = Config.getMB(this); // Qtd
            dados[3] = Config.getNumero(this); // Numero
        }

        // Se já enviou os 4, confirma e sai
        if(passo >= 4){
            clicarEnviar(root);
            handler.postDelayed(() -> {
                clicarConfirmar(root);
                LogManager.registar(this, "Envio concluido");
                QueueManager.removerPrimeiro(this);
                passo = 0;
                dados[0] = null;
                handler.postDelayed(() -> UssdManager.iniciarEnvio(this), 3000);
            }, 2000);
            return;
        }

        // Só digita
        digitarPasso(root, dados[passo]);
        passo++;
    }

    private void digitarPasso(AccessibilityNodeInfo root, String texto){
        handler.postDelayed(() -> {
            LogManager.registar(this, "Digitando: " + texto);
            clicarCampo(root);
            handler.postDelayed(() -> {
                enviarTexto(root, texto);
                handler.postDelayed(() -> clicarEnviar(root), 1000);
            }, 500);
        }, 2000);
    }

    private void clicarCampo(AccessibilityNodeInfo node){
        if(node == null) return;
        if(node.isEditable() || node.isClickable()){
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
        for(int i=0;i<node.getChildCount();i++) clicarCampo(node.getChild(i));
    }

    private void enviarTexto(AccessibilityNodeInfo root, String texto){
        if(root == null) return;
        if(root.isEditable()){
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, texto);
            root.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
            return;
        }
        for(int i=0;i<root.getChildCount();i++) enviarTexto(root.getChild(i), texto);
    }

    private void clicarEnviar(AccessibilityNodeInfo node){
        if(node == null) return;
        String texto = node.getText()!=null? node.getText().toString().toLowerCase() : "";
        if(texto.contains("enviar") || texto.contains("ok") || texto.contains("confirmar") || texto.contains("responder")){
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
        for(int i=0;i<node.getChildCount();i++) clicarEnviar(node.getChild(i));
    }

    private void clicarConfirmar(AccessibilityNodeInfo node){
        if(node == null) return;
        String texto = node.getText()!=null? node.getText().toString().toLowerCase() : "";
        if(texto.contains("1") || texto.contains("sim") || texto.contains("confirm")){
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
        for(int i=0;i<node.getChildCount();i++) clicarConfirmar(node.getChild(i));
    }

    @Override
    public void onInterrupt(){}

    public void resetarEtapa(){
        passo = 0;
        dados[0] = null;
    }

    public static RCBDAccessibilityService getInstancia(){ 
        return instancia; 
    }
}