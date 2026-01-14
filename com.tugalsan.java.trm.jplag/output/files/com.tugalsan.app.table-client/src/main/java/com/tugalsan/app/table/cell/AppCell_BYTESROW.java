package com.tugalsan.app.table.cell;

import com.tugalsan.app.table.*;
import com.tugalsan.lib.rql.client.*;


public class AppCell_BYTESROW extends AppCell_BYTES {

//    final private static TGC_Log d = TGC_Log.of(AppCell_BYTESROW.class);

    public AppCell_BYTESROW(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
    }

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        tm.operations.showBackupInfo();
    }
}
