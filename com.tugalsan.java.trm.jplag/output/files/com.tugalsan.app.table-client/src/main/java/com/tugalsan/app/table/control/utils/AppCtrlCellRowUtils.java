package com.tugalsan.app.table.control.utils;

import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.lib.rql.client.*;
import java.util.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.control.*;
import com.tugalsan.app.table.sg.query.*;
import com.tugalsan.app.table.sg.row.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.lib.table.client.*;

public class AppCtrlCellRowUtils {

    private AppCtrlCellRowUtils() {

    }

    final private static TGC_Log d = TGC_Log.of(AppCtrlCellRowUtils.class);

    public static Integer getRowIdxById(AppCtrlCell ch, long rowId) {
        int ri;
        AppCell_LNG cellId;
        for (ri = 0; ri < ch.rows.size(); ri++) {
            cellId = (AppCell_LNG) ch.rows.get(ri).get(0);
            if (cellId.getValueLong() == rowId) {
                break;
            }
        }
        return ri == ch.rows.size() ? null : ri;
    }

    public static Long getRowIdByIdx(AppCtrlCell tableWidgets, int rowIdx) {
        if (rowIdx < 0) {
            return null;
        }
        var cell = (AppCell_LNG) tableWidgets.rows.get(rowIdx).get(0);
        return cell.getValueLong();
    }

    public static void insertNewRow(AppModuleTable tm, List<TGS_SQLCellAbstract> row, TGS_FuncMTU_In1<Long> exeAfter) {
        var curTblName = tm.curTable.nameSql;
        TGC_SGWTCalller.async(new AppSGFRowAdd(tm.dbCfg, curTblName, row), r -> {
            if (!r.getOutput_result()) {
                d.ce("insertNewRow", "HATA Ekleme başarısız!");
                return;
            }
            var id = ((TGS_SQLCellLNG) row.get(0)).getValueLong();
            d.cr("insertNewRow", " id=" + id + " başarıyla eklendi");
            if (exeAfter != null) {
                exeAfter.run(id);
            }
        });
    }

    public static void createNewRow(TGS_LibTableDbSub dbCfg, TGS_LibRqlTbl curTable, TGS_FuncMTU_In1<List<TGS_SQLCellAbstract>> exeAfter) {
        createNewRowId(dbCfg, curTable, (Long newId) -> {
            exeAfter.run(TGS_LibRqlTblUtils.newRow(
                    TGS_LibTableDbSubUtils.toConvert(dbCfg, curTable), newId
            ));
        });
    }

    private static void createNewRowId(TGS_LibTableDbSub dbCfg, TGS_LibRqlTbl curTable, TGS_FuncMTU_In1<Long> exeAfter) {
        d.ci("createNewRowId", "init");
        var columnName = "LNG_ID";
        String where = null;
        if (curTable.autoIdDatedConfig && !dbCfg.isAny()) {
            where = TGS_StringUtils.cmn().concat(columnName, " BETWEEN ", String.valueOf(createNewRowId_getFromDate(true)), " AND ", String.valueOf(createNewRowId_getToDate(true)));
        }
        d.ci("constructor", "where", where);
        var tn = curTable.nameSql;
        TGC_SGWTCalller.async(new AppSGFQueryMaxId(dbCfg, tn, columnName, where), r -> {
            var maxId = r.getOutput_id();
            d.ci("constructor", "maxId.0", maxId);
            if (curTable.autoIdDatedConfig && maxId == 0L) {
                maxId = createNewRowId_getFromDate(true);
            }
            d.ci("constructor", "maxId.1", maxId);
            exeAfter.run(maxId + 1L);
        });
    }

    private static long createNewRowId_getFromDate(boolean slimToZeroDateYear_defultTrue) {
        var now = TGS_Time.of();
        var year = now.getYear();
        if (slimToZeroDateYear_defultTrue) {
            year = TGS_TimeUtils.slimToZeroDateYear_OnlyIf2XXX(year);
        }
        return (year * 1000000L) + (now.getMonth() * 10000L);
    }

    private static long createNewRowId_getToDate(boolean slimToZeroDateYear_defultTrue) {
        var now = TGS_Time.of();
        var year = now.getYear();
        if (slimToZeroDateYear_defultTrue) {
            year = TGS_TimeUtils.slimToZeroDateYear_OnlyIf2XXX(year);
        }
        return (year * 1000000L) + ((now.getMonth() + 1) * 10000L);
    }
}
