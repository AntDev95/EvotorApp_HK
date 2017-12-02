package ru.evotor.app;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.widget.Toast;

import net.glxn.qrgen.android.QRCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.evotor.app.api.API;
import ru.evotor.devices.commons.printer.printable.IPrintable;
import ru.evotor.devices.commons.printer.printable.PrintableImage;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionAllSubpositionsFooter;
import ru.evotor.framework.users.User;
import ru.evotor.framework.users.UserApi;


public class PrePrintService extends IntegrationService {

    private Bitmap getBitmapFromAsset(String fileName) {
        AssetManager assetManager = getAssets();
        InputStream stream = null;
        try {
            stream = assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(stream);
    }

    private Bitmap createQRCode(String link) {
        try {
            return QRCode.from(link).withSize(415, 400).bitmap();
        } catch (Throwable e) {
            return null;
        }
    }

    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        final User user = UserApi.getAuthenticatedUser(getApplicationContext());
        if (user == null) {
            return new HashMap<>();
        }
        final String link = "https://bot-maxim.com/f/?user_id=" + user.getUuid() + "&shop_id=" + API.getInstance().shop_id;
        API.getInstance().shorMethod(link);
        final String md5 = Helper.md5(link);
        final String shortLink = "https://bot-maxim.com/s/" + md5.substring(0, 5);
        PrintExtraRequiredEventProcessor processor = new PrintExtraRequiredEventProcessor() {
            @Override
            public void call(String s, PrintExtraRequiredEvent printExtraRequiredEvent, Callback callback) {
                List<SetPrintExtra> setPrintExtras = new ArrayList<>();
                Receipt r = ReceiptApi.getReceipt(PrePrintService.this, Receipt.Type.SELL);
                Bitmap bitmap = createQRCode(link);
                if (bitmap != null && r != null) {
                    for (Position p : r.getPositions()) {
                        setPrintExtras.add(new SetPrintExtra(
                                new PrintExtraPlacePositionAllSubpositionsFooter(p.getUuid()),
                                new IPrintable[]{
                                        new PrintableText("Оставь отзыв и получи скидку на 50 рублей на следующую покупку"),
                                        new PrintableImage(bitmap),
                                        new PrintableText("Отсканируй код или отправь фото чека в vk.com/feedback или t.me/feedback"),
                                        new PrintableText("\n\r"),
                                        new PrintableText(shortLink)
                                }
                        ));
                    }

                }
                try {
                    callback.onResult(new PrintExtraRequiredEventResult(setPrintExtras).toBundle());
                } catch (Throwable ignored) {}
            }
        };
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(PrintExtraRequiredEvent.NAME_SELL_RECEIPT, processor);
        return map;
    }
}
