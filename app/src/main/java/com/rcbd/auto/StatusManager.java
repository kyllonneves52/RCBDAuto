package com.rcbd.auto;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public class StatusManager {


    public static String verificar(Context context){


        StringBuilder estado =
                new StringBuilder();



        // Aplicação

        estado.append(
                "🟢 Aplicação ativa\n\n"
        );



        // Accessibility

        if(accessibilidadeAtiva(context)){


            estado.append(
                    "🟢 Accessibility ligada\n"
            );


        }else{


            estado.append(
                    "🔴 Accessibility desligada\n"
            );

        }



        // CALL PHONE

        if(context.checkSelfPermission(
                "android.permission.CALL_PHONE"
        ) == PackageManager.PERMISSION_GRANTED){


            estado.append(
                    "🟢 CALL_PHONE permitido\n"
            );


        }else{


            estado.append(
                    "🔴 CALL_PHONE negado\n"
            );

        }




        // WhatsApp

        if(instalado(
                context,
                "com.whatsapp"
        )){


            estado.append(
                    "🟢 WhatsApp instalado\n"
            );


        }else{


            estado.append(
                    "🔴 WhatsApp não encontrado\n"
            );


        }




        // WhatsApp Business

        if(instalado(
                context,
                "com.whatsapp.w4b"
        )){


            estado.append(
                    "🟢 WhatsApp Business instalado\n"
            );


        }else{


            estado.append(
                    "🔴 WhatsApp Business não encontrado\n"
            );

        }



        estado.append(
                "🟡 USSD: pronto para teste\n"
        );



        estado.append(
                "🟡 Notification Listener: próximo módulo\n"
        );



        estado.append(
                "🟡 Boot: próximo módulo\n"
        );



        return estado.toString();

    }






    private static boolean instalado(
            Context context,
            String pacote
    ){

        try{


            context.getPackageManager()
                    .getPackageInfo(
                            pacote,
                            0
                    );


            return true;


        }catch(Exception e){


            return false;

        }

    }





    private static boolean accessibilidadeAtiva(
            Context context
    ){


        AccessibilityManager manager =
                (AccessibilityManager)
                context.getSystemService(
                        Context.ACCESSIBILITY_SERVICE
                );



        if(manager == null){
            return false;
        }



        List<AccessibilityServiceInfo> lista =
                manager.getEnabledAccessibilityServiceList(
                        AccessibilityServiceInfo.FEEDBACK_ALL_MASK
                );



        for(AccessibilityServiceInfo info: lista){


            if(info.getResolveInfo()
                    .serviceInfo
                    .packageName
                    .equals(
                            context.getPackageName()
                    )){


                return true;

            }

        }


        return false;

    }


}
