package com.tugalsan.app.table.pop;

import java.util.*;
import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.app.table.sg.path.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.gui.client.theme.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.thread.client.*;

import com.tugalsan.lib.resource.client.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class AppPopImageHandler {

    final private static TGC_Log d = TGC_Log.of(AppPopImageHandler.class);

    private static final LinkedList<AppPopImageBufferItem> buffer = new LinkedList();//CLIENT SIDE! USES REMOVE FIRST!
    final private AtomicInteger SYNC = new AtomicInteger(Integer.MIN_VALUE);

    private static int MAX_BUFFER_SIZE() {
        return 255;
    }

    public AppPopImageHandler(Image image) {
        this.image = image;
        image.setStyleName(TGC_Widget.class.getSimpleName());
        image.addStyleName("AppModule_Image_BorderRadiusSmall");
        resetImage();
    }
    final private Image image;

    private void setImageUrl(String url) {
        d.ci("setImageUrl", url);
        image.setUrl(url);
    }

    final public void resetImage() {
        var resetImage = TGS_LibResourceUtils.common.res.def._0_jpg().toString();
        d.ci("resetImage", resetImage);
        setImageUrl(resetImage);
    }

    public void reloadImageById(String tablename, final Long id) {
        d.ci("reloadImageById", "tablename", tablename, "id", id);
        if (id == null) {
            resetImage();
            return;
        }
        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
            var localAttemptId = SYNC.incrementAndGet();
            TGS_FuncMTCUtils.run(() -> {
                if (tablename == null) {
                    return;
                }
                d.ci("reloadImageById", "tablename", tablename, "id", id, "run", "localAttemptId", localAttemptId);

                if (bufferLoad_returnTrueIfFromBuffer(tablename, id)) {
                    return;
                }

                TGC_SGWTCalller.async(new AppSGFPathHttpInboxImageUrls(tablename, id, true), r -> {
                    if (r == null) {
                        d.ce("reloadImageById", "tablename", tablename, "id", id, "async", localAttemptId, "getHttpInbox_ImageLoc",
                                "filenames.getData() == null", "BİLGİ: Silinmiş kayıt olabilir.");
                        if (localAttemptId == SYNC.get()) {
                            resetImage();
                        }
                        return;
                    }
                    if (r.getOutput_imageUrls() == null) {
                        d.ce("reloadImageById", "tablename", tablename, "id", id, "async", localAttemptId, "getHttpInbox_ImageLoc",
                                "filenames.getData() == null", "ÖNERİ: Tabloya resim dosyası ayarı yapılabilir.");
                        if (localAttemptId == SYNC.get()) {
                            resetImage();
                        }
                        return;
                    }
                    if (r.getOutput_imageUrls().isEmpty()) {
                        d.ci("reloadImageById", "tablename", tablename, "id", id, "async", localAttemptId, "getHttpInbox_ImageLoc",
                                "filenames.getData().isEmpty()");
                        if (localAttemptId == SYNC.get()) {
                            resetImage();
                        }
                        return;
                    }
                    var newImagePath = r.getOutput_imageUrls().get(r.getOutput_imageUrls().size() - 1);
                    d.ci("reloadImageById", "tablename", tablename, "id", id, "async", localAttemptId, "getHttpInbox_ImageLoc",
                            "newImagePath", newImagePath);
                    if (localAttemptId == SYNC.get()) {//SET IMAGE
                        d.ci("reloadImageById", "tablename", tablename, "id", id, "async", localAttemptId, "getHttpInbox_ImageLoc",
                                "localAttemptId == globalAttemptId)");
                        setImageUrl(newImagePath);
                        bufferSet(tablename, id, newImagePath);
                    } else {
                        d.ci("reloadImageById", "tablename", tablename, "id", id, "async", localAttemptId, "getHttpInbox_ImageLoc",
                                "LATE!");
                    }
                });
            }, e -> {
                if (localAttemptId == SYNC.get()) {
                    resetImage();
                }
                d.ce("reloadImageById", "tablename", tablename, "id", id, "async", "FAILED", e);
            });
        }, 0.1f);
    }

    //BUFFER------------------------------------------------------------------------
    private void bufferSet(String tablename, long id, String newImagePath) {
        d.ci("bufferSet", "tablename", tablename, "id", id, "newImagePath", newImagePath);
        for (var b : buffer) {
            if (b.id == id) {
                if (b.tablename.equals(tablename)) {
                    b.imageLoc = newImagePath;
                    return;
                }
            }
        }
        buffer.add(new AppPopImageBufferItem(tablename, id, newImagePath));
        while (buffer.size() > MAX_BUFFER_SIZE()) {
            buffer.removeFirst();
        }
    }

    private boolean bufferLoad_returnTrueIfFromBuffer(String tablename, long id) {
        d.ci("bufferLoad_returnTrueIfFromBuffer", "tablename", tablename, "id", id);
        for (var b : buffer) {
            if (b.id == id) {
                if (b.tablename.equals(tablename)) {
                    setImageUrl(b.imageLoc);
                    return true;
                }
            }
        }
        resetImage();
        return false;
    }

    public void bufferUnSet(String tablename, long id) {
        d.ci("bufferUnSet", "tablename", tablename, "id", id);
        for (var b : buffer) {
            if (b.id == id) {
                if (b.tablename.equals(tablename)) {
                    buffer.remove(b);
                    break;
                }
            }
        }
    }
}
