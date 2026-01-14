package com.tugalsan.app.table;

import com.tugalsan.api.charset.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.file.xlsx.server.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.math.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.thread.server.sync.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.url.client.TGS_Url;
import com.tugalsan.app.table.query.*;
import com.tugalsan.app.table.sg.*;
import com.tugalsan.app.table.sg.query.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.file.server.*;
import com.tugalsan.lib.login.client.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.table.server.*;
import java.nio.file.*;
import java.util.stream.*;
import javax.servlet.http.*;

public class AppSGEExportExcel extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(true, AppSGEExportExcel.class);

    @Override
    public int timeout_seconds() {
        return 60 * 10;
    }

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFExportExcel) funcBase;
        d.ci("validate", "#0");
        var u_loginCard = TS_LibLoginCardUtils.get(servletKillTrigger, rq, f);
        if (u_loginCard.isExcuse()) {
            var msg = "loginCard==:" + u_loginCard.excuse().getMessage() + " -> " + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, null);
        }
        d.ci("validate", "#1");
        var loginCard = u_loginCard.value();
        if (f.getInput_dbCfg().isAny()) {
            if (!loginCard.userAdmin) {
                var msg = "!loginCard.userAdmin:" + funcBase.getInput_url();
                f.setExceptionMessage(msg);
                d.ce("validate", msg);
                return new TS_SGWTValidationResult(false, loginCard);
            }
            return new TS_SGWTValidationResult(true, loginCard);
        }
        d.ci("validate", "#2");
        if (loginCard.userNone) {
            var msg = "loginCard.userNone:" + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        d.ci("validate", "#3");
        var cp = TS_LibBootUtils.pck;
        d.ci("validate", "#4");
        var tn = f.getInput_table().nameSql;
        d.ci("validate", "#5");
        if (!TS_LibRqlAllowTblUtils.readAndWriteCheck(servletKillTrigger, TS_LibRqlBufferUtils.items, cp.sqlAnc, loginCard, tn, null)) {
            var msg = "TS_LibTableUserUtils.isTableAllowedForReadAndWrite:" + loginCard.userName + ":" + tn;
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        d.ci("validate", "#6");
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFExportExcel.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        d.ci("run", "#0");
        var loginCard = (TGS_LibLoginCard) vldRtn;
        var cp = TS_LibBootUtils.pck;
        var f = (AppSGFExportExcel) funcBase;
        var un = loginCard.userName.toString();

        d.ci("run", "#1");
        var lf = doExcelOutput_returnLocalFileName(servletKillTrigger, cp.dirDAT, TGS_Url.of(f.getInput_url()), f, un);
        d.ci("run", "#2");
        if (servletKillTrigger.hasTriggered()) {
            d.ce("run", "servletKillTrigger.hasTriggered()");
            return;
        }

        //CLEANUP
        var path = TS_LibFilePathUtils.datUsrNameTmp(cp.dirDAT, un);
        var files = TS_DirectoryUtils.subFiles(path, null, false, false);
        files.stream().filter(file -> !TGS_CharSetCast.current().containsIgnoreCase(file.toString(), lf))
                .forEach(file -> TS_FileUtils.deleteFileIfExists(file));
        d.ci("run", "#3");
    }

    private static String doExcelOutput_returnLocalFileName(TS_ThreadSyncTrigger servletKillTrigger,
            Path dirDat, TGS_Url url, AppSGFExportExcel f2, String username) {
        if (servletKillTrigger.hasTriggered()) {
            d.ce("run", "servletKillTrigger.hasTriggered()", "#0");
            return "";
        }

        var tableOrder = f2.getInput_tableOrder();
        var isColumnHidden = f2.getInput_isColumnHidden();
        var table = f2.getInput_table();
        var tableName = table.nameSql;
        var wherestmt = f2.getInput_wherestmt();
        var orderbystmt = f2.getInput_orderbystmt();

        var filename = System.currentTimeMillis() + ".xlsx";
        d.ci("doExcelOutput_returnLocalFileName", "filename", filename);
        var localXlsFile = TS_LibFilePathUtils.datUsrNameTmp(dirDat, username).resolve(filename);
        d.ci("doExcelOutput_returnLocalFileName", "localXlsFile", localXlsFile);
        var remoteXlsFile = TS_LibTableFileGetUtils.urlUsrTmp(true, dirDat, url, username, filename).toString();
        d.ci("doExcelOutput_returnLocalFileName", "remoteXlsFile", remoteXlsFile);

        var f = new AppSGFQueryPage( f2.getInput_dbCfg(), tableName, null, null, wherestmt, orderbystmt, f2.getInput_aramaJoinConfig(), f2.getInput_aramaJoinValue());
        AppSGEQueryPage.executes(servletKillTrigger, f, username);
        var columnValues = f.getOutput_column_values();
        if (columnValues == null) {
            TGS_FuncMTUUtils.thrw(d.className(), "doExcelOutput_returnLocalFileName", "columnValues == null");
        }

        //DEBUG
        var sizeRow = columnValues.get(0).size();
        var sizeColQuery = columnValues.size();
        d.ci("doExcelOutput_returnLocalFileName", "query.rowSize/colSize", sizeRow, sizeColQuery);

        var tableAsis = TS_FileXlsxTable.ofXlsx();
        IntStream.range(0, sizeRow).forEachOrdered(ri -> {
            if (servletKillTrigger.hasTriggered()) {
                d.ce("run", "servletKillTrigger.hasTriggered()", "#1");
                return;
            }
            IntStream.range(0, sizeColQuery).forEachOrdered(ci -> {
                var ct = table.columns.get(ci);
                if (TGS_SQLColTypedUtils.familyLng(ct.getColumnName())) {
                    var cellAbs = columnValues.get(ci).get(ri);
                    if (TGS_SQLColTypedUtils.typeLngLnk(ct.getColumnName())) {
                        tableAsis.setValue(ri, ci,
                                cellAbs instanceof TGS_SQLCellSTR ins
                                        ? ins.getValueString()
                                        : "Error at doExcelOutput_returnLocalFileName.TYPE_LNGLINK: " + cellAbs.getClass().getSimpleName()
                        );
                        return;
                    }
                    var val = ((TGS_SQLCellLNG) cellAbs).getValueLong();
                    if (TGS_SQLColTypedUtils.typeLngDate(ct.getColumnName())) {
                        tableAsis.setValue(ri, ci, TGS_Time.ofDate(val).toString_dateOnly());
                        return;
                    }
                    if (TGS_SQLColTypedUtils.typeLngTime(ct.getColumnName())) {
                        tableAsis.setValue(ri, ci, TGS_Time.ofTime(val).toString_timeOnly_simplified());
                        return;
                    }
                    if (TGS_SQLColTypedUtils.typeLngDbl(ct.getColumnName())) {
                        var dbl = TGS_MathUtils.long2Double(val, ct.getDataInt_STRFamilyMaxCharSize_or_LNGDOUBLEPrecision());
                        tableAsis.setValue(ri, ci, TGS_StringUtils.cmn().toString(dbl));
                        return;
                    }
                    tableAsis.setValue(ri, ci, val);
                    return;
                }
                if (TGS_SQLColTypedUtils.familyStr(ct.getColumnName())) {
                    var val = ((TGS_SQLCellSTR) columnValues.get(ci).get(ri)).getValueString();
                    tableAsis.setValue(ri, ci, val);
                    return;
                }
                if (TGS_SQLColTypedUtils.familyBytes(ct.getColumnName())) {
                    if (TGS_SQLColTypedUtils.typeBytesStr(ct.getColumnName())) {
                        var val = TGS_StringUtils.cmn().toEmptyIfNull(((TGS_SQLCellBYTESSTR) columnValues.get(ci).get(ri)).getValueString());
                        tableAsis.setValue(ri, ci, val);
                        return;
                    }
                    tableAsis.setValue(ri, ci, "BYTES");
                    return;
                }
                tableAsis.setValue(ri, ci, "Unknown column type: " + ct);
            });
        });
        if (servletKillTrigger.hasTriggered()) {
            d.ce("run", "servletKillTrigger.hasTriggered()", "#2");
            return "";
        }

        d.ci("doExcelOutput_returnLocalFileName", "ADD HEADER YTO COLS");
        tableAsis.insertEmptyRow(0);
        IntStream.range(0, sizeColQuery).forEach(ci -> {
            tableAsis.setValue(0, ci, table.columns.get(ci).getColumnNameVisible());
        });
        if (servletKillTrigger.hasTriggered()) {
            d.ce("run", "servletKillTrigger.hasTriggered()", "#3");
            return "";
        }

        d.ci("doExcelOutput_returnLocalFileName", "CLEAR HIDDEN COLS");
        if (isColumnHidden != null) {
            IntStream.range(0, isColumnHidden.length).parallel().forEach(ci -> {
                if (isColumnHidden[ci]) {
                    tableAsis.clearColumn(ci);
                }
            });
        }
        if (servletKillTrigger.hasTriggered()) {
            d.ce("run", "servletKillTrigger.hasTriggered()", "#4");
            return "";
        }

        d.ci("doExcelOutput_returnLocalFileName", "DEBUG");
        var sizeColTbl = tableAsis.getColumnSize(0);//fast colsize
        sizeRow = tableAsis.getRowSize();
        d.ci("doExcelOutput_returnLocalFileName", "tableAsis.rowSize/colSize", sizeRow, sizeColTbl);

        d.ci("doExcelOutput_returnLocalFileName", "ORDER COLS");
        var tableOrdered = TS_FileXlsxTable.ofXlsx();
        var columnUsed = new boolean[sizeColTbl];
        var ciOffset = 0;
        for (var coi = 0; coi < tableOrder.length && coi < sizeColTbl; coi++) {//INC
            var ci = tableOrder[coi];
            if (ci < sizeColTbl && !columnUsed[ci]) {
                final var fciOffset = ciOffset;
                IntStream.range(0, sizeRow).forEachOrdered(ri -> tableOrdered.setValue(ri, fciOffset, tableAsis.getValueAsObject(ri, ci)));
                columnUsed[ci] = true;
                ciOffset++;
            }
        }
        IntStream.range(0, sizeRow).parallel().forEach(ri -> {
            IntStream.range(0, sizeColTbl).parallel().forEach(ci -> {
                for (var ui = 0; ui < columnUsed.length; ui++) {
                    if (!columnUsed[ui]) {
                        tableOrdered.setValue(ri, ci, tableAsis.getValueAsObject(ri, ui));
                        columnUsed[ui] = true;
                        break;
                    }
                }
            });
        });
        if (servletKillTrigger.hasTriggered()) {
            d.ce("run", "servletKillTrigger.hasTriggered()", "#5");
            return "";
        }

        d.ci("doExcelOutput_returnLocalFileName", "DELETE CLEARED COLS");
        if (isColumnHidden != null) {
            TGS_StreamUtils.reverse(0, tableOrdered.getColumnSize(0))
                    .filter(ci -> tableOrdered.isValueEmpty(0, ci))
                    .forEachOrdered(ci -> tableOrdered.deleteColumn(ci));
        }
        if (servletKillTrigger.hasTriggered()) {
            d.ce("run", "servletKillTrigger.hasTriggered()", "#6");
            return "";
        }

        //SAVE FILE
        d.ci("doExcelOutput_returnLocalFileName", "SAVE FILE");
        var u_xlsx = tableOrdered.toFile(localXlsFile);
        if (u_xlsx.isVoid()) {
            d.cr("doExcelOutput_returnLocalFileName", "u_xlsx.isVoid()");
        }
        if (u_xlsx.isExcuseInterrupt()) {
            d.ce("doExcelOutput_returnLocalFileName", "u_xlsx.isExcuseInterrupt()");
        }
        if (u_xlsx.isExcuseTimeout()) {
            d.ce("doExcelOutput_returnLocalFileName", "u_xlsx.isExcuseTimeout()");
        }
        if (u_xlsx.isExcuse()) {
            d.ce("doExcelOutput_returnLocalFileName", "u_xlsx.isExcuse()");
            d.ce("doExcelOutput_returnLocalFileName", u_xlsx.excuse());
            TGS_FuncMTUUtils.thrw(d.className(), "doExcelOutput_returnLocalFileName", u_xlsx.excuse());
        }
        d.ci("doExcelOutput_returnLocalFileName", "u_xlsx.isAlive...");

        d.ci("doExcelOutput_returnLocalFileName", url.toString(), "remoteXlsFile", remoteXlsFile);
        f2.setOutput_url(remoteXlsFile);
        return filename;
    }
}
