package com.rcbd.auto;

import android.content.Context;
import android.provider.Settings;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LicenseManager {

    // COLOCAR ID DO CLIENTE ANTES DE COMPILAR
    private static final String ANDROID_ID_PERMITIDO = "31db07671355a14a";

    // PLANO DE 1 HORA
    private static final int DIAS_PLANO = 0;
    private static final int HORAS_PLANO = 1;
    private static final int MINUTOS_PLANO = 0;

    public static boolean verificar(Context context){
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // 1. CHECA ID
        if(!ANDROID_ID_PERMITIDO.equals(id)){
            return false;
        }

        String licencaCrip = LicenseStorage.lerLicenca(context);
        long agoraMillis = System.currentTimeMillis();

        // 2. SE NÃO TEM ARQUIVO, CRIA E LIBERA
        if(licencaCrip == null){
            long tempoTotal = (DIAS_PLANO * 86400000L) + (HORAS_PLANO * 3600000L) + (MINUTOS_PLANO * 60000L);
            long dataFinal = agoraMillis + tempoTotal;

            String nova = "ANDROID_ID="+id+"\n"+
                          "DATA_FINAL="+dataFinal+"\n"+
                          "ULTIMA_DATA="+agoraMillis;

            LicenseStorage.criarSeNaoExistir(context, Crypto.criptografar(nova));
            return true;
        }

        try {
            // 3. DESCRIPTOGRAFA E CHECA
            String licenca = Crypto.descriptografar(licencaCrip);
            long dataFinal = extrairLong(licenca, "DATA_FINAL=");
            long ultimaData = extrairLong(licenca, "ULTIMA_DATA=");

            // ANTI BURLO: Se voltou a data do celular
            if(agoraMillis < ultimaData){
                return false;
            }

            if(agoraMillis >= dataFinal){
                return false; // Expirou
            }

            // 4. ATUALIZA E CRIPTOGRAFA DE NOVO
            String nova = "ANDROID_ID="+id+"\n"+
                          "DATA_FINAL="+dataFinal+"\n"+
                          "ULTIMA_DATA="+agoraMillis;
            LicenseStorage.atualizarTodos(context, Crypto.criptografar(nova));
            return true;

        } catch(Exception e){
            return false; // Arquivo foi editado
        }
    }

    // PRA MOSTRAR NA TELA: 00d 00h 04m 32s
    public static String getTempoRestante(Context context){
        try {
            String licencaCrip = LicenseStorage.lerLicenca(context);
            if(licencaCrip == null) return "Ativando...";

            String licenca = Crypto.descriptografar(licencaCrip);
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
    public static String getDataValidade(Context context){
        try {
            String licencaCrip = LicenseStorage.lerLicenca(context);
            String licenca = Crypto.descriptografar(licencaCrip);
            long dataFinal = extrairLong(licenca, "DATA_FINAL=");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(new Date(dataFinal));
        } catch(Exception e){ return "Erro"; }
    }

    private static long extrairLong(String texto, String chave){
        try{
            String valor = texto.split(chave)[1].split("\n")[0];
            return Long.parseLong(valor);
        }catch(Exception e){ return 0; }
    }
}