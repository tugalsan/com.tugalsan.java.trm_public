package com.tugalsan.app.table.query;

import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.sql.conn.server.TS_SQLConnAnchor;
import com.tugalsan.api.sql.select.server.*;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.app.table.sg.query.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.login.client.TGS_LibLoginCard;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.rql.link.server.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.table.client.*;
import com.tugalsan.lib.table.server.*;
import java.util.*;
import java.util.stream.*;
import javax.servlet.http.*;

public class AppSGEQueryPage extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(false, AppSGEQueryPage.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        d.ci("validate", "welcome");
        var f = (AppSGFQueryPage) funcBase;
        var u_loginCard = TS_LibLoginCardUtils.get(servletKillTrigger, rq, f);
        if (u_loginCard.isExcuse()) {
            var msg = "loginCard==:" + u_loginCard.excuse().getMessage() + " -> " + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, null);
        }
        var loginCard = u_loginCard.value();
        if (f.getInput_dbCfg().isAny() && !f.getInput_dbCfg().isTxt()) {
            if (!loginCard.userAdmin) {
                var msg = "!loginCard.userAdmin:" + funcBase.getInput_url();
                f.setExceptionMessage(msg);
                d.ce("validate", msg);
                return new TS_SGWTValidationResult(false, loginCard);
            }
            return new TS_SGWTValidationResult(true, loginCard);
        }
        if (loginCard.userNone) {
            var msg = "loginCard.userNone:" + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        var cp = TS_LibBootUtils.pck;
        var table = TS_LibRqlBufferUtils.get(f.getInput_tableName());
        var tn = table.nameSql;
        if (!TS_LibRqlAllowTblUtils.readCheck(servletKillTrigger, TS_LibRqlBufferUtils.items, cp.sqlAnc, loginCard, tn)) {
            var msg = "TS_LibTableUserUtils.isTableAllowedForReadAndWrite:" + loginCard.userName + ":" + tn;
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFQueryPage.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        d.ci("run", "welcome");
        var loginCard = (TGS_LibLoginCard) vldRtn;
        var f = (AppSGFQueryPage) funcBase;
        AppSGEQueryPage.executes(servletKillTrigger, f, loginCard.userName);
    }

    public static void executes(TS_ThreadSyncTrigger servletKillTrigger, AppSGFQueryPage f, CharSequence username) {
        if (servletKillTrigger.hasTriggered()) {
            return;
        }

        //BUFFER
        TS_ThreadSyncLst<TS_LibRqlLinkUtils.GetBufferItem> scopedBuffer = TS_ThreadSyncLst.ofSlowWrite();

        var anchor = TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), TS_LibBootUtils.pck.sqlAnc);

        var tn = f.getInput_tableName();
        var table = TGS_LibTableDbSubUtils.toConvert(f.getInput_dbCfg(), TS_LibRqlBufferUtils.get(tn));

        f.setOutput_column_idsIfValueTypeIsLngLink(TGS_ListUtils.of());

        var rowStart = f.getInput_rowStart();
        var rowSize = f.getInput_rowSize();
        var wherestmt = f.getInput_wherestmt();
        var orderbystmt = f.getInput_orderbystmt();
        var sql = getSQL(anchor, null, table, rowStart, rowSize, wherestmt, orderbystmt, f.getInput_aramaJoinConfig(), f.getInput_aramaJoinValue());
        if (sql.startsWith("ERROR")) {
            d.ce("run", sql);
            servletKillTrigger.trigger(sql + " > early kill col");
        }
        if (servletKillTrigger.hasTriggered()) {
            return;
        }
        TS_SQLSelectStmtUtils.select(anchor, sql, rs -> {
            if (rs.row.isEmpty()) {
                f.setOutput_column_values(TGS_ListUtils.of());
                f.setOutput_column_idsIfValueTypeIsLngLink(TGS_ListUtils.of());
                return;
            }
            if (servletKillTrigger.hasTriggered()) {
                return;
            }
            var tableData = rs.table.get(servletKillTrigger, true);
            if (servletKillTrigger.hasTriggered()) {
                return;
            }

            d.ci("executes", "UPDATE LINKTEXT AND SET LINKID");
            List<List<TGS_SQLCellLNG>> transposedTableLngLinkId = TGS_ListUtils.of();
            d.ci("executes", "rs.rs/cs", rs.row.size(), rs.col.size());

            IntStream.range(0, rs.col.size()).forEachOrdered(ci -> {
                if (servletKillTrigger.hasTriggered()) {
                    return;
                }
                List<TGS_SQLCellLNG> colLngLinkId = TGS_ListUtils.of();
                var ct = table.columns.get(ci);
                var cn = ct.getColumnName();
                d.ci("executes", "cn", cn);
                var ct_typed = TGS_SQLColTyped.of(cn);
                if (!ct_typed.typeLngLnk()) {
                    IntStream.range(0, rs.row.size()).forEachOrdered(ri -> colLngLinkId.add(null));
                    transposedTableLngLinkId.add(colLngLinkId);
                    return;
                }
                if (servletKillTrigger.hasTriggered()) {
                    return;
                }
                var idLinks = IntStream.range(0, rs.row.size()).mapToLong(ri -> {
//                    d.ci("executes", "SET LINK ID");
                    var tableDataRow = tableData.get(ri);
                    var idLink = ((TGS_SQLCellLNG) tableDataRow.get(ci)).getValueLong();
                    colLngLinkId.add(new TGS_SQLCellLNG(idLink));
                    return idLink;
                }).boxed().toList();
                if (servletKillTrigger.hasTriggered()) {
                    return;
                }
//                var stream = processIfPossibleOrLatestIsImportant
//                        ? IntStream.range(0, rs.row.size()).sequential()
//                        : IntStream.range(0, rs.row.size()).parallel();
                var stream = IntStream.range(0, rs.row.size()).sequential();
                stream.forEach(ri -> {//TODO: THIS IS TAKING SO MUCH TIME!
                    if (servletKillTrigger.hasTriggered()) {
                        return;
                    }
                    var tableDataRow = tableData.get(ri);
//                    d.ci("executes", "SET LINK ID");
                    var idLink = idLinks.get(ri);
//                    d.ci("executes", "SNIFF LINK TEXT");
                    var link = TS_LibRqlLinkUtils.get(
                            servletKillTrigger,
                            TS_LibRqlBufferUtils.items,
                            anchor,
                            tn, TS_LibBootUtils.pck.sqlBCfg.tableName,
                            ct.getDataString1_LnkTargetTableName(),
                            idLink,
                            scopedBuffer
                    );
                    if (servletKillTrigger.hasTriggered()) {
                        return;
                    }
                    if (link.errTxt != null) {
                        d.ce("executes", "link.errTxt", link.errTxt, "tn", tn, "ci", ci,
                                "ci,ct.getColumnName()", ct.getColumnName(),
                                "ct.getDataString1_LnkTargetTableName()", ct.getDataString1_LnkTargetTableName(),
                                "idLink", idLink
                        );
                        tableDataRow.set(ci, new TGS_SQLCellSTR("HATA: " + link.errTxt));
                        return;
                    }
                    tableDataRow.set(ci, new TGS_SQLCellSTR(link.linkText));
                });
                transposedTableLngLinkId.add(colLngLinkId);
            });
            f.setOutput_column_idsIfValueTypeIsLngLink(transposedTableLngLinkId);
            var tableDataTransposed = TGS_ListMatrixUtils.transpose(tableData);
            f.setOutput_column_values(tableDataTransposed);
        });
    }

    private static String getSQL(TS_SQLConnAnchor anchor, List<String> columnNamesAsList, TGS_LibRqlTbl table, Integer rowStart, Integer rowSize, CharSequence wherestmt, CharSequence orderbystmt, CharSequence joinConfig, CharSequence joinValue) {
        var tn = table.nameSql;

        var joinAllowed = true;
        var joinRequested = joinConfig != null && joinValue != null;
        var whereRequested = wherestmt != null && !wherestmt.isEmpty();

        //FIX columnNamesAsList
        if (columnNamesAsList == null || columnNamesAsList.isEmpty()) {
            columnNamesAsList = TGS_StreamUtils.toLst(table.columns.stream().map(c -> c.getColumnName()));
        }
        if (joinAllowed && joinRequested) {
            IntStream.range(0, columnNamesAsList.size()).parallel().forEach(i -> {

            });
            for (var i = 0; i < columnNamesAsList.size(); i++) {
                var cn = columnNamesAsList.get(i);
                if (cn.contains(".")) {
                    continue;
                }
                columnNamesAsList.set(i, tn + "." + cn);
            }
        }

        //CREATE columnNamesComma
        var columnNamesComma = columnNamesAsList.stream().collect(Collectors.joining(", "));

        //CONSTRUCT SQL
        var sb = new StringBuilder();
        sb.append(anchor.tagSelectAndSpace()).append(columnNamesComma).append(" FROM ").append(tn);
        if (joinAllowed && joinRequested) {
            var joinConfigs = TGS_StringUtils.jre().toList_spc(joinConfig);
            if (joinConfigs.size() % 2 != 0) {
                return "ERROR: joinConfigs.size() % 2 != 0 + hence join skipped: " + joinConfig;
            }
            List<Integer> joinConfigFatherColumnsIdxs = new ArrayList();
            List<TGS_LibRqlTbl> joinConfigTables = new ArrayList();
            for (var i = 0; i < joinConfigs.size(); i += 2) {
                var joinConfigFatherColumnsIdxStr = joinConfigs.get(i);
                var joinConfigFatherColumnsIdx = TGS_CastUtils.toInteger(joinConfigFatherColumnsIdxStr).orElse(null);
                if (joinConfigFatherColumnsIdx == null) {
                    return "ERROR: joinConfigs " + joinConfigFatherColumnsIdxStr + " is not int + hence join skipped: " + joinConfig;
                }
                joinConfigFatherColumnsIdxs.add(joinConfigFatherColumnsIdx);
                var targetTableName = joinConfigs.get(i + 1);
                var targetTabe = TS_LibRqlBufferUtils.get(targetTableName);
                if (targetTabe == null) {
                    return "ERROR: joinConfigs " + targetTabe + " not found + hence join skipped: " + joinConfig;
                }
                joinConfigTables.add(targetTabe);
            }
            List<TGS_LibRqlCol> joinConfigFatherColumns = new ArrayList();
            IntStream.range(0, joinConfigTables.size()).forEach(i -> {
                var konuTable = i == 0 ? table : joinConfigTables.get(i - 1);
                var konuColumn = konuTable.columns.get(
                        joinConfigFatherColumnsIdxs.get(i)
                );
                joinConfigFatherColumns.add(konuColumn);
            });
            for (var i = 0; i < joinConfigTables.size(); i++) {
                var fatherTable = (i == 0 ? table : joinConfigTables.get(i - 1)).nameSql;
                var fatherColumnObj = joinConfigFatherColumns.get(i);
                var fatherColumn = fatherColumnObj.getColumnName();
                var childTable = joinConfigTables.get(i).nameSql;
                if (fatherColumnObj.getColType().typeLngLnk()) {
                    sb.append(" INNER JOIN ").append(childTable).append(" ON ").append(fatherTable).append(".").append(fatherColumn).append(" = ").append(childTable).append(".LNG_ID");
                } else {
                    return "ERROR: column type unknown [" + fatherColumn + "] + hence join skipped: " + joinConfig;
                }
            }
            sb.append(" WHERE ");
            sb.append(joinConfigTables.getLast().nameSql).append(".LNG_ID = ").append(joinValue);
        }
        if (whereRequested) {
            sb.append(joinAllowed && joinRequested ? " AND " : " WHERE ").append(wherestmt);
        }
        if (orderbystmt != null && !orderbystmt.isEmpty()) {
            sb.append(" ORDER BY ").append(orderbystmt);
        }
        if (rowStart != null && rowSize != null) {
            sb.append(" LIMIT ").append(rowStart).append(", ").append(rowSize);
        }
        var sql = sb.toString();
        d.cr("getQuary", "joinConfig", joinConfig, "joinValue", joinValue, "sql", sql);
        return sql;
    }
}
