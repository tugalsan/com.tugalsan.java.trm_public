package com.tugalsan.app.table.cell;

import com.tugalsan.app.table.AppModuleTable;
import com.tugalsan.lib.rql.client.TGS_LibRqlCol;

public class AppCell_STRFILE extends AppCell_STR {

//    final private static TGC_Log d = TGC_Log.of(AppCell_STRFILE.class);
    public AppCell_STRFILE(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
        super.setText("DOSYA." + ct.getDataString1_LnkTargetTableName());
    }

    @Override
    public void setText(String text) {
    }

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        var h = (AppCell_Abstract) tm.cells.headers.get(colIdx);
        var rowId = tm.cells.getActiveRowId();
        tm.cells.popFileOperations.label.setText(tm.curTable.nameReadable + " > id:" + rowId + " > " + h.getText() + " > Dosya İşlemleri: ");
        tm.cells.popFileOperations.getPop().setVisible(true);
    }
}
