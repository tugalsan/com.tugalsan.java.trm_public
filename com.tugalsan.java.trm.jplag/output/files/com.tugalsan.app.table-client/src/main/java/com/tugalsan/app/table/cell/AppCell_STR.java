package com.tugalsan.app.table.cell;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.gui.client.browser.TGC_BrowserClipboardUtils;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.app.table.sg.query.*;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.table.client.*;

public class AppCell_STR extends AppCell_Abstract {

    final private static TGC_Log d = TGC_Log.of(AppCell_STR.class);

    private static String DEFAULT_VALUE() {
        return (String) TGS_LibRqlColUtils.getDefaultValue(TGS_SQLColTypedUtils.TYPE_STR());
    }
    private int maxChar;
    private String valueString;//LONG CONTENT
    public Long extendedId = null;

    protected void setMaxChar(int newMaxChar) {
        maxChar = newMaxChar;
    }

    public int getMaxChar() {
        return maxChar;
    }

    public void changeMaxChar(int maxChar) {
        this.maxChar = maxChar;
    }

    @Override
    public String getLog() {
        return valueString;
    }

    public String getValueString() {
        return valueString;
    }

    public final void setValueString(String text) {
        if (text == null) {
            text = DEFAULT_VALUE();
        }
        valueString = text;
        setText(text);
    }

    public AppCell_STR(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
        valueString = DEFAULT_VALUE();
        setText(valueString);
        this.maxChar = ct.getDataInt_STRFamilyMaxCharSize_or_LNGDOUBLEPrecision();
    }

    @Override
    public void reset() {
        setValueString(DEFAULT_VALUE());
        extendedId = null;
    }

    @Override
    protected void onClick_copyToClipboard() {
        //PRECHECK CUSTOM LOAD
        preCheckExtendedId();

        //IF NOT EXTENSION
        if (extendedId == null) {
            var s = getText();
            TGC_BrowserClipboardUtils.copy(s);
            d.cr("AppCell_STR.onClick_copyToClipboard", "Hücre Bilgisi", s);
            d.cr("AppCell_STR.onClick_copyToClipboard", "İşlem: Hücre bilgisi, başka biryere yapıştırabilmeniz için kopyalandı.");
            return;
        }

        //WITH EXTENSION
        d.ci("AppCell_STR.onClick_copyToClipboard", "PopCall_getTableQuery...");
        var tn = tm.curTable.nameSql;
        var txt = new TGS_LibTableDbSub().txt();
        d.cr("onClick_copyToClipboard", "Hücre okunuyor...");
        TGC_SGWTCalller.async(new AppSGFQueryPage(txt, tn, null, null, tn + ".LNG_ID = " + extendedId, null, null, null), r -> {
            var columnValues = r.getOutput_column_values();
            if (columnValues.isEmpty()) {
                d.ce("AppCell_STR.onClick_copyToClipboard", "HATA: TXT sub table record not found!");
                return;
            }
            d.ci("AppCell_STR.onClick_copyToClipboard", "PopCall_getTableQuery", "afterSuccessOperations", "resultSet1.size:" + columnValues.size());
            var cell = columnValues.get(2).get(0);//col 2, row 0
            var val = ((TGS_SQLCellBYTESSTR) cell).getValueString();
            d.cr("onClick_copyToClipboard", "Hücre okundu.");
            TGC_BrowserClipboardUtils.copy(val);
//            d.cr("AppCell_STR.onClick_copyToClipboard", "Hücre Bilgisi:");
//            d.cr("AppCell_STR.onClick_copyToClipboard", val);
            d.cr("AppCell_STR.onClick_copyToClipboard", "İşlem: Hücre bilgisi içeriği, başka biryere yapıştırabilmeniz için kopyalandı.");;
            onFocused();
//            var rowId = AppCtrlCellRowUtils.getRowIdByIdx(tm.cells, rowIdx);
//            var tnv = tm.curTable.nameReadable;
//            var cnv = ((AppCell_Abstract) tm.cells.headers.get(colIdx)).ct.getColumnNameVisible();
//            tm.input.showArea(val,
//                    null,
//                    this,
//                    getMaxChar(),
//                    TGS_StringUtils.cmn().concat("<b>Girdi:</b> ", tnv, " > id:", String.valueOf(rowId), " -> " + cnv, ":")
//            );
        });
    }

    private void preCheckExtendedId() {
        if (extendedId == null) {
            var val = getValueString();
            if (val.length() < "BYTESSTR 999999999999999".length()) {
                if (val.startsWith("BYTESSTR")) {
                    var parsedData = TGS_StringUtils.gwt().toList_spc(val);
                    if (parsedData.size() == 2) {
                        var id = TGS_CastUtils.toLong(parsedData.get(1)).orElse(null);
                        if (id != null) {
                            extendedId = id;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        var val = getValueString();
        var rowId = AppCtrlCellRowUtils.getRowIdByIdx(tm.cells, rowIdx);

        //PRECHECK CUSTOM LOAD
        preCheckExtendedId();

        //CUSTOM LOAD
        if (extendedId == null) {
            tm.input.setAreaEditable(true);
        } else {
            tm.input.setAreaEditable(false);
            d.ci("AppCell_STR.onClick_showCellUpdate", "PopCall_getTableQuery...");
            var tn = tm.curTable.nameSql;
            var txt = new TGS_LibTableDbSub().txt();
            d.cr("onClick_showCellUpdate", "Hücre güncelleniyor...");
            TGC_SGWTCalller.async(new AppSGFQueryPage(txt, tn, null, null, tn + ".LNG_ID = " + extendedId, null, null, null), r -> {
                var columnValues = r.getOutput_column_values();
                if (columnValues.isEmpty()) {
                    d.ci("AppCell_STR.onClick_showCellUpdate", "HATA: TXT sub table record not found!");
                    if (App.loginCard.userAdmin) {
                        tm.input.setAreaEditable(true);
                    }
                    return;
                }
                d.ci("AppCell_STR.onClick_showCellUpdate", "PopCall_getTableQuery", "afterSuccessOperations", "resultSet1.size:" + columnValues.size());
                var cell = columnValues.get(2).get(0);//col 2, row 0
                var s = ((TGS_SQLCellBYTESSTR) cell).getValueString();
                d.ci("AppCell_STR.onClick_showCellUpdate", "PopCall_getTableQuery", "afterSuccessOperations", "s" + s);
                tm.input.setAreaText(s);
                tm.input.setAreaEditable(true);
                d.cr("onClick_showCellUpdate", "Hücre güncellendi.");
            });
        }

        //MAIN OP
        onFocused();
        var tnv = tm.curTable.nameReadable;
        var cnv = ((AppCell_Abstract) tm.cells.headers.get(colIdx)).ct.getColumnNameVisible();
        tm.input.showArea(val,
                inputText -> AppCtrlCellUpdateUtils.text(tm, inputText),
                this,
                getMaxChar(),
                TGS_StringUtils.cmn().concat("<b>Girdi:</b> ", tnv, " > id:", String.valueOf(rowId), " -> " + cnv, ":")
        );
    }
}
