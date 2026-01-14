package com.tugalsan.app.table.control.utils;

import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.app.table.*;

public class AppCtrlCellFocusCalcUtils {

    private AppCtrlCellFocusCalcUtils() {

    }

    final private static TGC_Log d = TGC_Log.of(AppCtrlCellFocusCalcUtils.class);

    public static Integer getShownOrderedLeftColumn(AppModuleTable tm, int ci) {
        d.ci("getShownOrderedLeftColumn");
        var ordered_ci = getOrderedLeftColumn(tm, ci);
        while (true) {
            d.ci("getShownOrderedLeftColumn", "getOrderedLeftColumn(tableOrder, ci)", ordered_ci);
            if (!AppCtrlCellLayoutUtils.isHidden(tm, ci)) {
                return ordered_ci;
            }
            if (!AppCtrlCellLayoutUtils.isRenderable(tm, ci, true)) {
                return ordered_ci;
            }
            ci--;
            if (ci < 0) {
                return null;
            }
            ordered_ci = getOrderedLeftColumn(tm, ci);
        }
    }

    public static Integer getShownOrderedRightColumn(AppModuleTable tm, int ci) {
        d.ci("getShownOrderedRightColumn");
        var ordered_ci = getOrderedRightColumn(tm, ci);
        while (true) {
            d.ci("getShownOrderedLeftColumn", "getShownOrderedRightColumn(tableOrder, c,)", ordered_ci);
            if (!AppCtrlCellLayoutUtils.isHidden(tm, ci)) {
                return ordered_ci;
            }
            if (!AppCtrlCellLayoutUtils.isRenderable(tm, ci, true)) {
                return ordered_ci;
            }
            ci++;
            if (ci >= tm.cells.headers.size()) {
                return null;
            }
            ordered_ci = getOrderedRightColumn(tm, ci);
        }
    }

    public static int getOrderedRightColumn(AppModuleTable tm, int ci) {
        int i;
        for (i = 0; i < tm.curTable.tableOrder.length; i++) {
            if (tm.curTable.tableOrder[i] == ci) {
                break;
            }
        }
        if (i == tm.curTable.tableOrder.length - 1) {
            return ci;
        }
        if (i < tm.curTable.tableOrder.length - 1) {
            return tm.curTable.tableOrder[i + 1];
        }
        return 0;
    }

    public static int getOrderedLeftColumn(AppModuleTable tm, int ci) {
        int i;
        for (i = 0; i < tm.curTable.tableOrder.length; i++) {
            if (tm.curTable.tableOrder[i] == ci) {
                break;
            }
        }
        if (i == 0) {
            return ci;
        }
        if (i > 0) {
            return tm.curTable.tableOrder[i - 1];
        }
        return 0;
    }
}
