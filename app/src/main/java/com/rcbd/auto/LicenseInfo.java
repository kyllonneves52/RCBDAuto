package com.rcbd.auto;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LicenseInfo {

    public static String mostrar(Context context){
        try {
            String licencaCrip = LicenseStorage.lerLicenca(context);

            if(licencaCrip == null){
                return "LICENÇA NÃO ENCONTRADA\nAtive o app para gerar 1h";
            }

            // Descriptografa
            String licenca = Crypto.descriptografar(licencaCrip);

            long dataFinal = extrairLong(licenca, "DATA_FINAL=");
            long ultimaData = extrairLong(licenca, "ULTIMA_DATA=");

            // Calcula tempo restante
            long diff = dataFinal - System.currentTimeMillis();

            String tempoRestante;
            if(diff <= 0){
                tempoRestante = "EXPIRADO";
            } else {
                long horas = (diff % 86400000) / 3600000;
                long min = (diff % 3600000) / 60000;
                long seg = (diff % 60000) / 1000;
                tempoRestante = String.format(Locale.getDefault(), "%02dh %02dm %02ds", horas, min, seg);
            }

            // Formata data final
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String dataFinalFormatada = sdf.format(new Date(dataFinal));

            String ultimaAtualizacao = sdf.format(new Date(ultimaData));

            return "LICENÇA ATIVA\n" +
                   "Tempo restante: " + tempoRestante + "\n" +
                   "Válido até: " + dataFinalFormatada + "\n" +
                   "Última verificação: " + ultimaAtualizacao;

        } catch(Exception e){
            return "ERRO AO LER LICENÇA\n\nArquivo corrompido";
        }
    }

    private static long extrairLong(String texto, String chave){
        try{
            String valor = texto.split(chave)[1].split("\n")[0];
            return Long.parseLong(valor);
        }catch(Exception e){ return 0; }
    }
}