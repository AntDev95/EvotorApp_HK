package ru.evotor.app;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONObject;

import ru.evotor.app.api.API;
import ru.evotor.app.api.Params;
import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.framework.core.IntegrationAppCompatActivity;

public class BaseActivity extends IntegrationAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceServiceConnector.startInitConnections(getApplicationContext());
        API.getInstance().startCrutch();
    }

}
