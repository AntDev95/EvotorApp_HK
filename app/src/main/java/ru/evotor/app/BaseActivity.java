package ru.evotor.app;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import ru.evotor.app.api.API;
import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.framework.core.IntegrationAppCompatActivity;

public class BaseActivity extends IntegrationAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceServiceConnector.startInitConnections(getApplicationContext());
        API.getInstance().startCrutch();
        FrameLayout layout = new FrameLayout(this);
        TextView desc = new TextView(this);
        desc.setTextColor(Color.WHITE);
        desc.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        desc.setGravity(Gravity.CENTER);
        desc.setText("fdfdsfds");
        layout.addView(desc);
        setContentView(layout);
    }

}
