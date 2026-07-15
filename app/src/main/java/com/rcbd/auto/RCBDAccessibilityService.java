package com.rcbd.auto;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class RCBDAccessibilityService extends AccessibilityService {

    private static RCBDAccessibilityService instancia;
    private int etapa = 0;
    private Handler handler = new Handler();

    @Override
    protected void onServiceConnected(){
        super.onServiceConnected();
        instancia = this;
        Toast.makeText(this, "RCBDAuto Acessibilidade ativa", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root == null){
            return;
        }
        String tela = lerTela(root);
        if(tela.isEmpty()){
            return;
        }
        controlarUSSD(tela.toLowerCase(), root);
    }

    private void controlarUSSD(String texto, AccessibilityNodeInfo root){
        if(etapa == 0){
            esperarEnviar(root, "8");
            etapa = 1;
        }
        else if(etapa == 1 && (texto.contains("menu") || texto.contains("transfer") || texto.contains("opção"))){
            esperarEnviar(root, "2");
            etapa = 2;
        }
        else if(etapa == 2){
            esperarEnviar(root, Config.getMB(this));
            etapa = 3;
        }
        else if(etapa == 3){
            esperarEnviar(root, Config.getNumero(this));
            etapa = 4;
        }
        else if(etapa == 4 && (texto.contains("confirm") || texto.contains("sim") || texto.contains("enviar"))){
            esperarEnviar(root, "1");
            etapa = 5;
        }
        else if(etapa == 5){
            LogManager.registar(this, "Envio concluido");
            QueueManager.removerPrimeiro(this);
            etapa = 0;
            handler.postDelayed(() -> {
                UssdManager.iniciarEnvio(this);
            }, 3000);
        }
    }

    private void esperarEnviar(AccessibilityNodeInfo root, String texto){
        handler.postDelayed(() -> {
            LogManager.registar(this, "Enviar etapa: " + texto);
            enviarTexto(root, texto);
            handler.postDelayed(() -> {
                clicarBotao(root);
            },3000);
        },4000);
    }

    private void enviarTexto(AccessibilityNodeInfo root, String texto){
        if(root == null){
            return;
        }
        if(root.isEditable()){
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, texto);
            root.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
            return;
        }
        for(int i=0;i<root.getChildCount();i++){
            enviarTexto(root.getChild(i), texto);
        }
    }

    private void clicarBotao(AccessibilityNodeInfo node){
        if(node == null){
            return;
        }
        String texto = "";
        if(node.getText()!=null){
            texto = node.getText().toString().toLowerCase();
        }
        if(texto.contains("ok") || texto.contains("enviar") || texto.contains("confirmar") || 
           texto.contains("continuar") || texto.contains("enter") || texto.contains("aceitar") || 
           texto.contains("responder") || texto.contains("sim") || texto.contains("prosseguir")){
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
        for(int i=0;i<node.getChildCount();i++){
            clicarBotao(node.getChild(i));
        }
    }

    private String lerTela(AccessibilityNodeInfo node){
        StringBuilder texto = new StringBuilder();
        if(node == null){
            return "";
        }
        if(node.getText()!=null){
            texto.append(node.getText());
        }
        for(int i=0;i<node.getChildCount();i++){
            texto.append(lerTela(node.getChild(i)));
        }
        return texto.toString();
    }

    @Override
    public void onInterrupt(){}

    public void resetarEtapa(){
        etapa = 0;
    }

    public static RCBDAccessibilityService getInstancia(){
        return instancia;
    }
}