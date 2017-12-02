package ru.evotor.app;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import net.glxn.qrgen.android.QRCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupSummary;
import ru.evotor.framework.users.User;
import ru.evotor.framework.users.UserApi;

public class PrePrintService extends IntegrationService {

    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(
                PrintExtraRequiredEvent.NAME_SELL_RECEIPT,
                new PrintExtraRequiredEventProcessor() {
                    @Override
                    public void call(String s, PrintExtraRequiredEvent printExtraRequiredEvent, Callback callback) {
                        List<SetPrintExtra> setPrintExtras = new ArrayList<>();
                        Receipt r = ReceiptApi.getReceipt(PrePrintService.this, Receipt.Type.SELL);
                        User user = UserApi.getAuthenticatedUser(getApplicationContext());
                        String link = "https://bot-maxim.com/feedback/" + user.getUuid() + "/" + Helper.md5(Helper.rand(0,99999) + "_" + Helper.getUnix());
                        Bitmap bitmap = createQRCode(link);
                        if (bitmap != null && r != null) {
                            for (Position p : r.getPositions()) {
                                setPrintExtras.add(new SetPrintExtra(
                                        new PrintExtraPlacePositionAllSubpositionsFooter(p.getUuid()),
                                        new IPrintable[]{
                                                new PrintableText("Оставь отзыв и получи скидку на 50 рублей на следующую покупку"),
                                                new PrintableImage(bitmap),
                                                new PrintableText("Отсканируй код или отправь фото чека в vk.com/feedback или t.me/feedback"),
                                                new PrintableText("\n\n"),
                                                new PrintableText(link)
                                        }
                                ));
                            }

                        }
                        try {
                            callback.onResult(new PrintExtraRequiredEventResult(setPrintExtras).toBundle());
                        } catch (Throwable ignored) {}
                    }
                }
        );
        return map;
    }

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
            return QRCode.from(link).withSize(415, 380).bitmap();
        } catch (Throwable e) {
            return null;
        }
    }
}
