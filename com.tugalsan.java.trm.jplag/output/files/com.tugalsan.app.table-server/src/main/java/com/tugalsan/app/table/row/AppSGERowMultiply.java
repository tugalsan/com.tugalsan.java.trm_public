package com.tugalsan.app.table.row;

import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTUEffectivelyFinal;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTUUtils;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.sql.conn.server.*;
import com.tugalsan.api.sql.max.server.*;
import com.tugalsan.api.sql.resultset.server.TS_SQLResultSet;
import com.tugalsan.api.sql.select.server.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.app.table.sg.row.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.login.client.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.rql.cfg.server.*;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.rql.report.server.*;
import com.tugalsan.lib.rql.rev.server.*;
import com.tugalsan.lib.rql.txt.server.*;
import com.tugalsan.lib.table.client.*;
import com.tugalsan.lib.table.server.*;
import com.tugalsan.api.time.client.*;
import java.util.*;
import java.util.stream.*;
import javax.servlet.http.*;

public class AppSGERowMultiply extends TS_SGWTExecutor {

    final public static TS_Log d = TS_Log.of(false, AppSGERowMultiply.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFRowMultiply) funcBase;
        var u_loginCard = TS_LibLoginCardUtils.get(servletKillTrigger, rq, f);
        if (u_loginCard.isExcuse()) {
            var msg = "loginCard==:" + u_loginCard.excuse().getMessage() + " -> " + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, null);
        }
        var loginCard = u_loginCard.value();
        if (f.getInput_dbCfg().isAny()) {
            if (!loginCard.userAdmin) {
                var err = "!loginCard.userAdmin:" + funcBase.getInput_url();
                f.setExceptionMessage(err);
                d.ce("validate", err);
                return new TS_SGWTValidationResult(false, loginCard);
            }
            return new TS_SGWTValidationResult(true, loginCard);
        }
        if (loginCard.userNone) {
            var err = "loginCard.userNone:" + funcBase.getInput_url();
            f.setExceptionMessage(err);
            d.ce("validate", err);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        var cp = TS_LibBootUtils.pck;
        var tn = f.getInput_table().nameSql;
        if (!TS_LibRqlAllowTblUtils.readAndWriteCheck(servletKillTrigger.newChild(d.className()).newChild("validate").newChild("TS_LibRqlAllowTblUtils.readAndWriteCheck"), TS_LibRqlBufferUtils.items, cp.sqlAnc, loginCard, f.getInput_table().nameSql, null)) {
            var err = "TS_LibTableUserUtils.isTableAllowedForReadAndWrite:" + loginCard.userName + ":" + tn;
            f.setExceptionMessage(err);
            d.ce("validate", err);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFRowMultiply.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        var cp = TS_LibBootUtils.pck;
        var f = (AppSGFRowMultiply) funcBase;

        var loginCard = (TGS_LibLoginCard) vldRtn;
        var username = loginCard.userName.toString();
        d.ci("run", "username", username);

        var anchor = TGS_FuncMTUEffectivelyFinal.of(TS_SQLConnAnchor.class).coronateAs(val -> {
            var anchor_pre = cp.sqlAnc;
            d.ci("run", "anchor_pre.config.dbName", anchor_pre.config.dbName);
            var anchor_pst = TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), anchor_pre);
            d.ci("run", "anchor_pst.config.dbName", anchor_pst.config.dbName);
            return anchor_pst;
        });

        var table = TGS_FuncMTUEffectivelyFinal.of(TGS_LibRqlTbl.class).coronateAs(val -> {
            var table_pre = f.getInput_table();
            d.ci("run", "table_pre.nameSql", table_pre.nameSql);
            var table_pst = TGS_LibTableDbSubUtils.toConvert(f.getInput_dbCfg(), f.getInput_table());
            f.setInput_table(table_pst);
            d.ci("run", "table_pst.nameSql", table_pst.nameSql);
            return table_pst;
        });
        var tableNameSql = f.getInput_table().nameSql;

        var nextDataRowId = table.autoIdDatedConfig
                ? TS_SQLMaxUtils.max(anchor, tableNameSql, 0).whereConditionNone().nextIdDated()
                : TS_SQLMaxUtils.max(anchor, tableNameSql, 0).whereConditionNone().nextId();
        d.ci("run", "nextDataRowId", nextDataRowId);

        TS_SQLSelectUtils.select(anchor, tableNameSql).columnsAll()
                .whereFirstColumnAsId(f.getInput_fromRowid())
                .walk(rs_empty -> {
                    var err = "record %L found on table %s".formatted(nextDataRowId, tableNameSql);
                    f.setExceptionMessage(err);
                    d.ce("run", err);
                }, rs -> {
                    if (rs.row.size() != 1) {
                        var err = "row size is not 1. it is %d".formatted(rs.row.size());
                        d.ce("run", err);
                        f.setExceptionMessage(err);
                        return;
                    }
                    rs.row.scrll(0);
                    IntStream.range(0, f.getInput_count()).forEachOrdered(countIdx -> {
                        var nextDataRowIdPlusCountIdx = nextDataRowId + countIdx;
                        d.ci("run", "un", username, "tn", tableNameSql, "nextDataRowId", nextDataRowId, "countIdx", countIdx, "nextDataRowIdPlusCountIdx", nextDataRowIdPlusCountIdx);
                        d.ci("run", "calling constructNewDataRow...");
                        var rowData = constructNewDataRow(anchor, username, f, rs, nextDataRowIdPlusCountIdx, f.getInput_emptyDatesAndHours(), f.getInput_emptyColumn_onRowClone());
                        d.ci("run", "calling AppSGERowAdd.run...");
                        AppSGERowAdd.run(username, new AppSGFRowAdd(f.getInput_dbCfg(), tableNameSql, rowData));
                        d.ci("run", "next");
                    });
                    d.ci("run", "setOutput_result.true");
                    f.setOutput_result(true);
                });
    }

    private static List<TGS_SQLCellAbstract> constructNewDataRow(TS_SQLConnAnchor anchor, String username, AppSGFRowMultiply f, TS_SQLResultSet rs, long nextDataRowIdPlusCountIdx, boolean cleanDatesAndHours, boolean emptyConfiguredColumn) {
        var rowDefault = TGS_LibRqlTblUtils.newRow(f.getInput_table(), 0);

        var BYTESSTR_PREFIX = TGS_SQLColTypedUtils.TYPE_BYTESSTR().concat(" ");
        d.ci("constructNewDataRow", "BYTESSTR_PREFIX", BYTESSTR_PREFIX);
        List<TGS_SQLCellAbstract> row = TGS_ListUtils.of();
        IntStream.range(0, f.getInput_table().columns.size()).forEachOrdered(ci -> {
            d.ci("constructNewDataRow", "ci", ci);
            if (ci == 0) {
                d.ci("constructNewDataRow", "ci==0", "addding id as", nextDataRowIdPlusCountIdx);
                row.add(new TGS_SQLCellLNG(nextDataRowIdPlusCountIdx));
                return;
            }
            var ct = f.getInput_table().columns.get(ci);
            d.ci("constructNewDataRow", "ct.getColumnName()", ct.getColumnName());

            if (emptyConfiguredColumn && f.getInput_table().getEmptyColumn_onRowClone().stream().anyMatch(item -> item == ci)) {
                d.ce("constructNewDataRow", "cleaning colum", ci);
                row.add(rowDefault.get(ci));
                return;
            }

            if (Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_LNG())
                    || Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_LNGDATE())
                    || Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_LNGDOUBLE())
                    || Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_LNGLINK())
                    || Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_LNGTIME())) {
                if (cleanDatesAndHours && Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_LNGDATE())) {
                    d.ci("constructNewDataRow", "adding default lng column for family lng -> cleanDate", ct.getColumnName());
                    row.add(new TGS_SQLCellLNG(TGS_TimeUtils.zeroDateLng()));
                    return;
                }
                if (cleanDatesAndHours && Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_LNGTIME())) {
                    d.ci("constructNewDataRow", "adding default lng column for family lng -> cleanTime", ct.getColumnName());
                    row.add(new TGS_SQLCellLNG(0L));
                    return;
                }
                d.ci("constructNewDataRow", "adding default lng column for family lng -> default", ct.getColumnName());
                row.add(new TGS_SQLCellLNG(rs.lng.get(ci)));
                return;
            }
            if (Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_STRFILE())
                    || Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_STRLINK())) {
                d.ci("constructNewDataRow", "adding default lng column for strfile or strlink", ct.getColumnName());
                row.add(new TGS_SQLCellSTR(rs.str.get(ci)));
                return;
            }
            if (Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_STR())) {
                d.ci("constructNewDataRow", "for type str", ct.getColumnName());
                var valueStr = rs.str.get(ci);
                d.ci("constructNewDataRow", "fetched valueStr", valueStr);
                if (!valueStr.startsWith(BYTESSTR_PREFIX)) {
                    d.ci("constructNewDataRow", "fetched valueStr not startsWith bytesstr", ct.getColumnName());
                    d.ci("constructNewDataRow", "adding str as", valueStr);
                    row.add(new TGS_SQLCellSTR(valueStr));
                    return;
                }
                d.ci("constructNewDataRow", "fetched valueStr startsWith bytesstr", ct.getColumnName());
                var valueStr_txtIdStr = valueStr.substring(BYTESSTR_PREFIX.length());
                d.ci("constructNewDataRow", "parsed valueStr_txtIdStr", valueStr);
                var valueStr_txtId = TGS_CastUtils.toLong(valueStr_txtIdStr).orElse(null);
                d.ci("constructNewDataRow", "parsed valueStr_txtId", valueStr);
                if (valueStr_txtId == null) {
                    d.ci("constructNewDataRow", "UNCOMPATIBLE: valueStr_txtId == null", "addding id str as (rollback)", valueStr);
                    row.add(new TGS_SQLCellSTR(valueStr));
                    return;
                }
                if (TS_LibRqlRevDBUtils.is(anchor) || TS_LibRqlTxtDBUtils.is(anchor) || TS_LibRqlAllowDBUtils.is(anchor) || TS_LibRqlReportDBUtils.is(anchor) || TS_LibRqlCfgDBUtils.is(anchor)) {
                    d.ce("constructNewDataRow", "UNCOMPATIBLE: BYTESTR redirect value on shadow db!", "addding id str as (rollback)", valueStr);
                    row.add(new TGS_SQLCellSTR(valueStr));
                    return;
                }
                d.ci("constructNewDataRow", "calling while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT...");
                var newValueId = while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT(
                        anchor, f.getInput_table().nameSql,
                        valueStr_txtId, f.getInput_fromRowid(),
                        nextDataRowIdPlusCountIdx, username
                );
                if (newValueId.isExcuse()) {
                    f.setExceptionMessage(newValueId.excuse().getMessage());
                    return;
                }
                var longTextNewValue = BYTESSTR_PREFIX + newValueId.value();
                d.ci("constructNewDataRow", "addding str as", longTextNewValue);
                row.add(new TGS_SQLCellSTR(longTextNewValue));
                return;
            }
            if (Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_BYTESSTR())) {
                d.ci("constructNewDataRow", "for type bytesstr", ct.getColumnName());
                d.ci("constructNewDataRow", "fetching bytes...");
                var valBytes = rs.bytes.get(ci);
                d.ci("constructNewDataRow", "coverting bytes to valString...");
                var valString = TGS_StringUtils.jre().toString(valBytes);
                d.ci("constructNewDataRow", "addding bytesstr as", valString);
                row.add(new TGS_SQLCellBYTESSTR(valString));
                return;
            }
            if (Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_BYTES())
                    || Objects.equals(ct.getType(), TGS_SQLColTypedUtils.TYPE_BYTESROW())) {
                d.ci("constructNewDataRow", "for type bytes or bytesRow", ct.getColumnName());
                d.ci("constructNewDataRow", "addding default bytes");
                row.add(new TGS_SQLCellBYTES());
                return;
            }
            TGS_FuncMTUUtils.thrw(d.className(), "constructNewDataRow", "ERROR: unrecognized column name: " + ct.getColumnName());
        });
        return row;
    }

    private static TGS_UnionExcuse<Long> while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT(
            TS_SQLConnAnchor anchor, String tableNameSql,
            long valueStr_txtId_src, long dataRowId,
            long nextDataRowIdPlusCountIdx, CharSequence username) {
        d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "tableNameSql", tableNameSql);
        d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "valueStr_txtId_src", valueStr_txtId_src);
        d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "dataRowId", dataRowId);
        d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "nextDataRowIdPlusCountIdx", nextDataRowIdPlusCountIdx);
        d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "username", username);

        d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "anchor.config.dbName", anchor.config.dbName);
        var anchortxt = TS_LibRqlTxtDBUtils.txt(anchor);
        d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "anchortxt.config.dbName", anchortxt.config.dbName);

        var newTxtId = TS_SQLMaxUtils.max(anchortxt, tableNameSql, 0).whereConditionNone().nextId();
        d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "newTxtId", newTxtId);

        var txt = new TGS_LibTableDbSub().txt();
        d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "txt", txt);

//        var f = new AppSGFRowMultiply(txt, table, valueStr_txtId_src, 1);
        TGS_Tuple1<TGS_UnionExcuse<Long>> result = TGS_Tuple1.of(TGS_UnionExcuse.of(newTxtId));
        TS_SQLSelectUtils.select(anchortxt, tableNameSql).columnsAll().whereFirstColumnAsId(valueStr_txtId_src)
                .walk(rs0 -> {
                    var err = "valueStr_txtId_src %L cannot be found on table %s on db [err_emptyRow]".formatted(valueStr_txtId_src, tableNameSql, anchortxt.config.dbName);
                    d.ce("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", err);
                    result.value0 = TGS_UnionExcuse.ofExcuse(d.className(), "while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", err);
                }, rs -> {
                    List<TGS_SQLCellAbstract> rowTxt = TGS_ListUtils.of();
                    rs.walkCells(rs00 -> {
                        var err = "valueStr_txtId_src %L cannot be found on table %s on db [err_emptyCell]".formatted(valueStr_txtId_src, tableNameSql, anchortxt.config.dbName);
                        d.ce("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", err);
                        result.value0 = TGS_UnionExcuse.ofExcuse(d.className(), "while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", err);
                    }, (ri, ci) -> {
                        d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "ci", ci);
                        if (ci == 0) {
                            d.ci("while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "ci==0", "addding id as", nextDataRowIdPlusCountIdx);
                            rowTxt.add(new TGS_SQLCellLNG(newTxtId));
                            return;
                        }
                        var cn = rs.col.name(ci);
                        d.ci("constructNewDataRow", "cn", cn);
                        var tc = TGS_LibRqlColUtils.toSqlCol(cn);
                        if (tc.familyStr()) {
                            var valueStr = rs.str.get(ci);
                            valueStr = valueStr.replace("." + dataRowId, "." + nextDataRowIdPlusCountIdx);
                            rowTxt.add(new TGS_SQLCellSTR(valueStr));
                            return;
                        }
                        if (tc.typeBytesStr()) {
                            rowTxt.add(new TGS_SQLCellBYTESSTR(rs.bytesStr.get(ci)));
                            return;
                        }
                        TGS_FuncMTUUtils.thrw(d.className(), "while_constructingNewDataRow_IfTypeStrHasExtension_duplicateTXT", "ERROR: unrecognized column name: " + cn);
                    });
                    AppSGERowAdd.run(username, new AppSGFRowAdd(txt, tableNameSql, rowTxt));
                });
        return result.value0;
    }
}
