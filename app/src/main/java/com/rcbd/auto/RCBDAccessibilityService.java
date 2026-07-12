package com.rcbd.auto;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class RCBDAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    public void teste(){
        Toast.makeText(
            this,
            "RCBDAuto Acessibilidade ativa",
            Toast.LENGTH_SHORT
        ).show();
    }
}
