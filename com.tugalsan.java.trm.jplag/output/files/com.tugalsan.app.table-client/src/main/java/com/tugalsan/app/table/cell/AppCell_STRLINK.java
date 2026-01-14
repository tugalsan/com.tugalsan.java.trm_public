package com.tugalsan.app.table.cell;

import com.tugalsan.api.log.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.app.table.pop.*;
import com.tugalsan.lib.rql.client.*;

public class AppCell_STRLINK extends AppCell_STR {

    final private static TGC_Log d = TGC_Log.of(AppCell_STRLINK.class);

    public boolean isInProcess() {
        return inProcess;
    }

    public TGS_LibRqlTbl getTargetTable() {
        return targetTable;
    }

    public AppCell_STRLINK(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
        targetTable = App.tbl_mayThrow(ct.getDataString1_LnkTargetTableName());
        inProcess = false;
    }
    private final TGS_LibRqlTbl targetTable;
    private final boolean inProcess;

    public AppPopEditCellSTRLINK getGui() {
        return gui;
    }
    private AppPopEditCellSTRLINK gui;

    public void setGui(AppPopEditCellSTRLINK gui) {
        this.gui = gui;
    }

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        if (gui == null) {
            d.ce("AppCell_STRLINK.onClick_showCellUpdate", "Hata: gui == null!");
        } else {
            var tableVisibleName = tm.curTable.nameReadable;
            var colNameVisible = ((AppCell_Abstract) tm.cells.headers.get(colIdx)).ct.getColumnNameVisible();
            var rowIdStr = String.valueOf(AppCtrlCellRowUtils.getRowIdByIdx(tm.cells, rowIdx));
            var lblHtmlString = TGS_StringUtils.cmn().concat("<b>Girdi:</b> ", tableVisibleName, " > id:", rowIdStr, " -> ", colNameVisible, ":");
            gui.onSetVisible.run(true, lblHtmlString);
        }
    }
}
