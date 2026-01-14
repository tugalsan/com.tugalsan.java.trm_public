package com.tugalsan.app.table.cell;

import com.tugalsan.api.gui.client.browser.TGC_BrowserClipboardUtils;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.string.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.lib.rql.client.*;

public class AppCell_BYTESSTR extends AppCell_BYTES {

    final private static TGC_Log d = TGC_Log.of(AppCell_BYTESSTR.class);

    public static String getDEFAULT_VALUE_HASTEXT() {
        return "UZUN YAZI";
    }

    private String valueString;

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = TGS_StringUtils.cmn().toEmptyIfNull(valueString);
        setEmpty(TGS_StringUtils.cmn().isNullOrEmpty(valueString));
    }

    @Override
    public final void reset() {
        super.reset();
        setEmpty(true);
        setValueString("");
    }

    public AppCell_BYTESSTR(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
        reset();
    }

    @Override
    public void setEmpty(boolean empty) {
        super.setEmpty(empty);
        if (!empty) {
            superSetText(getDEFAULT_VALUE_HASTEXT());
        }
    }

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        var tableVisibleName = tm.curTable.nameReadable;
        var colNameVisible = ((AppCell_Abstract) tm.cells.headers.get(colIdx)).ct.getColumnNameVisible();
        var rowIdStr = String.valueOf(AppCtrlCellRowUtils.getRowIdByIdx(tm.cells, rowIdx));
        tm.input.showArea(getValueString(),
                inputText -> AppCtrlCellUpdateUtils.noval(tm, inputText),
                this, //null if fullscreen
                TGC_PopLblYesNoTextArea.MAX_CHAR_SQL_BLOB(),
                TGS_StringUtils.cmn().concat("<b>Girdi:</b> ", tableVisibleName, " > id:", rowIdStr, " -> ", colNameVisible, ":")
        );
    }

    @Override
    protected void onClick_copyToClipboard() {
        var val = getValueString();
        TGC_BrowserClipboardUtils.copy(val);
//        d.cr("AppCell_BYTESSTR.onClick_copyToClipboard", "Hücre Bilgisi", val);
        d.cr("AppCell_BYTESSTR.onClick_copyToClipboard", "İşlem: Hücre bilgisi, başka biryere yapıştırabilmeniz için kopyalandı.");
        onFocused();
//        var rowId = AppCtrlCellRowUtils.getRowIdByIdx(tm.cells, rowIdx);
//        var tnv = tm.curTable.nameReadable;
//        var cnv = ((AppCell_Abstract) tm.cells.headers.get(colIdx)).ct.getColumnNameVisible();
//        tm.input.showArea(val,
//                null,
//                this,
//                TGC_PopLblYesNoTextArea.MAX_CHAR_SQL_BLOB(),
//                TGS_StringUtils.cmn().concat("<b>Girdi:</b> ", tnv, " > id:", String.valueOf(rowId), " -> " + cnv, ":")
//        );
    }
}
