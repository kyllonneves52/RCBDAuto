package com.rcbd.auto;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class LicenseManager {

    // COLOCAR ID DO CLIENTE ANTES DE COMPILAR
    private static final String ANDROID_ID_PERMITIDO = "COLOQUE_ID";

    // PLANO COMPRADO - AGORA TEM HORAS E MINUTOS PRA TESTAR
    private static final int DIAS_PLANO = 0; // 0 pra teste
    private static final int HORAS_PLANO = 0; // 0 pra teste
    private static final int MINUTOS_PLANO = 30; // 30 MINUTOS PRA TESTAR

    private static final String CHAVE = "RCBD@CHAVE2026BR"; // Chave pra criptografar

    public static boolean verificar(Context context){
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if(!ANDROID_ID_PERMITIDO.equals(id)){
            return false;
        }

        String licencaCrip = LicenseStorage.lerLicenca();
        long agoraMillis = System.currentTimeMillis();

        if(licencaCrip == null){
            // PRIMEIRA VEZ: CRIA O ARQUIVO CRIPTOGRAFADO
            long tempoTotal = (DIAS_PLANO * 86400000L) + (HORAS_PLANO * 3600000L) + (MINUTOS_PLANO * 60000L);
            long dataFinal = agoraMillis + tempoTotal;

            String nova = "ANDROID_ID="+id+"\n"+
                          "DATA_FINAL="+dataFinal+"\n"+
                          "ULTIMA_DATA="+agoraMillis;

            LicenseStorage.criarSeNaoExistir(criptografar(nova));
            return true;
        }

        try {
            // DESCRIPTOGRAFA PRA LER
            String licenca = descriptografar(licencaCrip);

            long dataFinal = extrairLong(licenca, "DATA_FINAL=");
            long ultimaData = extrairLong(licenca, "ULTIMA_DATA=");

            // ANTI BURLO: Se voltou a data do celular
            if(agoraMillis < ultimaData){
                return false;
            }

            if(agoraMillis >= dataFinal){
                return false; // Expirou
            }

            // ATUALIZA E CRIPTOGRAFA DE NOVO
            String nova = "ANDROID_ID="+id+"\n"+
                          "DATA_FINAL="+dataFinal+"\n"+
                          "ULTIMA_DATA="+agoraMillis;
            LicenseStorage.atualizarTodos(criptografar(nova));
            return true;

        } catch(Exception e){
            return false; // Arquivo foi editado
        }
    }

    // PRA MOSTRAR NA TELA: 00d 00h 04m 32s
    public static String getTempoRestante(){
        try {
            String licencaCrip = LicenseStorage.lerLicenca();
            if(licencaCrip == null) return "Sem licença";

            String licenca = descriptografar(licencaCrip);
            long dataFinal = extrairLong(licenca, "DATA_FINAL=");
            long diff = dataFinal - System.currentTimeMillis();

            if(diff <= 0) return "Expirado";

            long dias = diff / 86400000;
            long horas = (diff % 86400000) / 3600000;
            long min = (diff % 3600000) / 60000;
            long seg = (diff % 60000) / 1000;

            return String.format(Locale.getDefault(), "%02dd %02dh %02dm %02ds", dias, horas, min, seg);

        } catch(Exception e){ return "Erro"; }
    }

    // PRA MOSTRAR DATA FINAL
    public static String getDataValidade(){
        try {
            String licencaCrip = LicenseStorage.lerLicenca();
            String licenca = descriptografar(licencaCrip);
            long dataFinal = extrairLong(licenca, "DATA_FINAL=");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date(dataFinal));
        } catch(Exception e){ return "Erro"; }
    }

    private static String criptografar(String data) {
        try {
            SecretKeySpec skey = new SecretKeySpec(CHAVE.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            return Base64.encodeToString(cipher.doFinal(data.getBytes()), Base64.DEFAULT);
        } catch(Exception e){ return ""; }
    }

    private static String descriptografar(String data) throws Exception {
        SecretKeySpec skey = new SecretKeySpec(CHAVE.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skey);
        return new String(cipher.doFinal(Base64.decode(data, Base64.DEFAULT)));
    }

    private static long extrairLong(String texto, String chave){
        try{
            String valor = texto.split(chave)[1].split("\n")[0];
            return Long.parseLong(valor);
        }catch(Exception e){ return 0; }
    }
}