package com.tugalsan.app.table.cell;

import com.tugalsan.api.gui.client.browser.TGC_BrowserClipboardUtils;
import com.tugalsan.api.log.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.lib.rql.client.*;

public class AppCell_BYTES extends AppCell_Abstract {

    final private static TGC_Log d = TGC_Log.of(AppCell_BYTES.class);

    public static String getDEFAULT_VALUE_EMPTY() {
        return "";
    }

    public static String getDEFAULT_VALUE_NOTEMPTY() {
        return "BYTES";
    }

    public int rowSize = 1;

    @Override
    public String getLog() {
        return getText();
    }

    public AppCell_BYTES(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
        reset();
    }

    @Deprecated
    @Override
    public final void setText(String text) {
    }

    public final void superSetText(String text) {
        super.setText(text);
    }

    public void setEmpty(boolean empty) {
        superSetText(empty ? getDEFAULT_VALUE_EMPTY() : getDEFAULT_VALUE_NOTEMPTY());
    }

    @Override
    public void reset() {
        setEmpty(false);
    }

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        d.ce("AppCell_BYTES.onClick_showCellUpdate", "Hata: Not implemented!");
    }
    
    @Override
    protected void onClick_copyToClipboard() {
        var s = getText();
        TGC_BrowserClipboardUtils.copy(s);
        d.cr("AppCell_BYTES.onClick_copyToClipboard", "Hücre Bilgisi", s);
        d.cr("AppCell_BYTES.onClick_copyToClipboard", "İşlem: Hücre bilgisi, başka biryere yapıştırabilmeniz için kopyalandı.");
    }
}
