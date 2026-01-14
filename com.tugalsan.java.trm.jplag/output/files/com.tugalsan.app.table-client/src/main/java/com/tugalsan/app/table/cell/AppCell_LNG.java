package com.tugalsan.app.table.cell;

import com.tugalsan.api.gui.client.browser.TGC_BrowserClipboardUtils;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.lib.rql.client.*;


public class AppCell_LNG extends AppCell_Abstract {

    final private static TGC_Log d = TGC_Log.of(AppCell_LNG.class);

    private static Long DEFAULT_VALUE() {
        return (Long) TGS_LibRqlColUtils.getDefaultValue(TGS_SQLColTypedUtils.TYPE_LNG());
    }
    private long valueLong;

    @Override
    public String getLog() {
        return getText();
    }

    final public long getValueLong() {
        return valueLong;
    }

    public void setValueLong(long valueLong) {
        this.valueLong = valueLong;
        setText(String.valueOf(valueLong));
    }

    public AppCell_LNG(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
        valueLong = DEFAULT_VALUE();
        setText(String.valueOf(valueLong));
        setTextAlignRight();
    }

    @Override
    public void reset() {
        setValueLong(DEFAULT_VALUE());
    }

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        tm.input.showBox(getText(),
                inputText -> AppCtrlCellUpdateUtils.number(tm, inputText),
                this,
                "<b>Tam Sayı Girdi:</b>"
        );
    }
    
    @Override
    protected void onClick_copyToClipboard() {
        var s = getText();
        TGC_BrowserClipboardUtils.copy(s);
        d.cr("AppCell_LNG.onClick_copyToClipboard", "Hücre Bilgisi", s);
        d.cr("AppCell_LNG.onClick_copyToClipboard", "İşlem: Hücre bilgisi, başka biryere yapıştırabilmeniz için kopyalandı.");
    }
}
