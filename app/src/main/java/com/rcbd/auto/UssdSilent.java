package com.rcbd.auto;

import android.content.Context;
import android.os.Handler;
import android.telephony.TelephonyManager;
import java.lang.reflect.Method;

public class UssdSilent {

    public static boolean sendUssd(Context context, String ussdCode) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            
            Class<?> c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            Object telephonyService = m.invoke(tm);

            Class<?> iTelephonyClass = Class.forName("com.android.internal.telephony.ITelephony");
            Method sendUssd = iTelephonyClass.getDeclaredMethod("sendUSSD", String.class, Handler.class);
            sendUssd.invoke(telephonyService, ussdCode, null);
            
            LogManager.registar(context, "USSD Enviado em 2º plano: " + ussdCode);
            return true;

        } catch (Exception e) {
            LogManager.registar(context, "ERRO USSD Silencioso: " + e.getMessage());
            return false;
        }
    }
}
