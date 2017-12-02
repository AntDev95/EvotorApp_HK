package ru.evotor.app;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import ru.evotor.app.api.API;
import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.framework.core.IntegrationAppCompatActivity;

public class BaseActivity extends IntegrationAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceServiceConnector.startInitConnections(getApplicationContext());
        /* new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DeviceServiceConnector.getPrinterService().printDocument(
                            ru.evotor.devices.commons.Constants.DEFAULT_DEVICE_INDEX,
                            new PrinterDocument(
                                    new PrintableText("Ублюдок, мать твою, а ну иди сюда говно собачье, решил ко мне лезть? Ты, засранец вонючий, мать твою, а? Ну иди сюда, попробуй меня трахнуть, я тебя сам трахну ублюдок, онанист чертов, будь ты проклят, иди идиот, трахать тебя и всю семью, говно собачье, жлоб вонючий, дерьмо, сука, падла, иди сюда, мерзавец, негодяй, гад, иди сюда ты - говно, ЖОПА!")));
                } catch (Throwable e) {
                    Log.e("ErrorPrint", String.valueOf(e));
                }
            }
        }).start(); */
    }
}
