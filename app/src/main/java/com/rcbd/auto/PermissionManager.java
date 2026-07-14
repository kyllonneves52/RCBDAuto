package com.rcbd.auto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;


public class PermissionManager {


    public static void abrirAcessibilidade(Context context){

        Intent intent =
                new Intent(
                        Settings.ACTION_ACCESSIBILITY_SETTINGS
                );

        context.startActivity(intent);

    }



    public static void abrirNotificacoes(Context context){

        Intent intent =
                new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
                );

        context.startActivity(intent);

    }



    public static boolean temCallPhone(Context context){

        return context.checkSelfPermission(
                "android.permission.CALL_PHONE"
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED;

    }


}
