package com.tugalsan.app.table.cell;

import com.tugalsan.api.log.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.app.table.pop.*;
import com.tugalsan.app.table.sg.cell.*;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.rql.link.client.*;


public class AppCell_LNGLINK extends AppCell_LNG {

    final private static TGC_Log d = TGC_Log.of(AppCell_LNGLINK.class);

    public TGS_LibRqlTbl getTargetTable() {
        return targetTable;
    }

    public AppCell_LNGLINK(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
        targetTable = App.tbl_mayThrow(ct.getDataString1_LnkTargetTableName());
        setTextAlignLeft();
    }
    private final TGS_LibRqlTbl targetTable;

    public void setValueLongAndText(long valueLong, String text) {
        super.setValueLong(valueLong);
        setText(text);
    }

    @Override
    public void setValueLong(long valueLong) {
        setValueLongAndText(valueLong, String.valueOf(valueLong));
        if (isEnabled()) {
            refreshTextContent(false);
        }
    }

    public void refreshTextContent(boolean forceRefresh) {
        d.ci("refreshTextContent", "forceRefresh", forceRefresh, "getValueLong()", getValueLong(), "preRefreshedValueLong", preRefreshedValueLong);
        if (forceRefresh || preRefreshedValueLong == null || getValueLong() != preRefreshedValueLong) {
            d.ci("refreshTextContent", "init");
            preRefreshedValueLong = getValueLong();
            var curTableName = tm.curTable.nameSql;
            var tarTableName = targetTable.nameSql;
            TGC_SGWTCalller.async(new AppSGFCellGet(tm.dbCfg, curTableName, tarTableName, getValueLong()), r -> {
                if (r.getOutput_cell() == null) {
                    d.ce("HATA: TK_GWTTableCellLNGLINK.setValueLong." + getValueLong() + " -> o == null");
                } else {
                    tmp = r.getOutput_cell();
                    if (tmp.errTxt == null) {
                        var s = tmp.linkText;
                        setText(s);
                        d.ci("refreshTextContent", "s", s);
                    } else {
                        d.ce("HATA: TK_GWTTableCellLNGLINK.setValueLong." + getValueLong() + " -> getErrorText: " + tmp.errTxt);
                    }
                }

            });
        } else {
            d.ci("refreshTextContent", "skipped");
        }
    }
    private TGS_LibRqlLink tmp;
    private Long preRefreshedValueLong = null;

    public void setGui(AppPopEditCellLNGLINK gui) {
        this.gui = gui;
    }
    private AppPopEditCellLNGLINK gui;

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        if (gui == null) {
            d.ce("AppCell_LNGLINK.onClick_showCellUpdate", "Hata: gui == null!");
        } else {
            var tableVisibleName = tm.curTable.nameReadable;
            var colNameVisible = ((AppCell_Abstract) tm.cells.headers.get(colIdx)).ct.getColumnNameVisible();
            var rowIdStr = String.valueOf(AppCtrlCellRowUtils.getRowIdByIdx(tm.cells, rowIdx));
            var lblHtmlString = TGS_StringUtils.cmn().concat("<b>Girdi:</b> ", tableVisibleName, " > id:", rowIdStr, " -> ", colNameVisible, ":");
            gui.onSetVisible.run(true, lblHtmlString);
        }
    }
}
