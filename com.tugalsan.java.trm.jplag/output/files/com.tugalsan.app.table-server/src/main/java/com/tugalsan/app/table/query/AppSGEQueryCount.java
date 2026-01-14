package com.tugalsan.app.table.query;

import com.tugalsan.api.cast.client.TGS_CastUtils;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.sql.conn.server.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.app.table.sg.query.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.rql.client.TGS_LibRqlCol;
import com.tugalsan.lib.rql.client.TGS_LibRqlTbl;
import com.tugalsan.lib.table.client.TGS_LibTableDbSubUtils;
import com.tugalsan.lib.table.server.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.servlet.http.*;

public class AppSGEQueryCount extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGEQueryCount.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFQueryCount) funcBase;
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
        var tn = f.getInput_tableName();
        if (!TS_LibRqlAllowTblUtils.readCheck(servletKillTrigger.newChild(d.className()).newChild("validate").newChild("TS_LibRqlAllowTblUtils.readCheck"), TS_LibRqlBufferUtils.items, cp.sqlAnc, loginCard, tn)) {
            var msg = "TS_LibTableUserUtils.isTableAllowedForRead:" + loginCard.userName + ":" + tn;
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFQueryCount.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
//        var loginCard = (TGS_LibLoginCard) vldRtn;
        var f = (AppSGFQueryCount) funcBase;

        var anchor = TS_LibBootUtils.pck.sqlAnc;//TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), TS_LibBootUtils.pck.sqlAnc);
        var tn = f.getInput_tableName();
        var table = TGS_LibTableDbSubUtils.toConvert(f.getInput_dbCfg(), TS_LibRqlBufferUtils.get(tn));

        var sql = getSQL(anchor, table, f.getInput_where(), f.getInput_aramaJoinConfig(), f.getInput_aramaJoinValue());
        if (sql.startsWith("ERROR")) {
            d.ce("run", sql);
            servletKillTrigger.trigger(sql + " > early kill col");
        }

        TS_SQLConnWalkUtils.query(
                TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), anchor),
                sql,
                fillStmt -> {
                },
                rs -> f.setOutput_count(rs.lng.get(0, 0))
        );
    }

    private String getSQL(TS_SQLConnAnchor anchor, TGS_LibRqlTbl table, CharSequence where, CharSequence joinConfig, CharSequence joinValue) {
        var tn = table.nameSql;

        var joinAllowed = true;
        var joinRequested = joinConfig != null && joinValue != null;
        var whereRequested = where != null && !where.isEmpty();

        //CONSTRUCT SQL
        var sb = new StringBuilder();
        sb.append(anchor.tagSelectAndSpace()).append("COUNT(*)").append(" FROM ").append(tn);
        if (joinAllowed && joinRequested) {
            if (d.infoEnable) {
                d.cr("getQuary", "joinConfig", joinConfig);
            }
            var joinConfigs = TGS_StringUtils.jre().toList_spc(joinConfig);
            if (joinConfigs.size() % 2 != 0) {
                return "ERROR: joinConfigs.size() % 2 != 0 + hence join skipped: " + joinConfig;
            }
            if (d.infoEnable) {
                d.cr("getQuary", "joinConfigs", joinConfigs);
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
            if (d.infoEnable) {
                d.cr("getQuary", "joinConfigFatherColumnsIdxs", joinConfigFatherColumnsIdxs);
                d.cr("getQuary", "joinConfigTables", joinConfigTables.stream().map(o -> o.nameSql).toList());
            }
            List<TGS_LibRqlCol> joinConfigFatherColumns = new ArrayList();
            IntStream.range(0, joinConfigTables.size()).forEach(i -> {
                var konuTable = i == 0 ? table : joinConfigTables.get(i - 1);
                var konuColumn = konuTable.columns.get(
                        joinConfigFatherColumnsIdxs.get(i)
                );
                joinConfigFatherColumns.add(konuColumn);
            });
            if (d.infoEnable) {
                d.cr("getQuary", "joinConfigFatherColumns", joinConfigFatherColumns.stream().map(o -> o.getColumnName()).toList());
            }
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
            if (d.infoEnable) {
                d.cr("getQuary", "joinValue", joinValue);
            }
            sb.append(" WHERE ");
            sb.append(joinConfigTables.getLast().nameSql).append(".LNG_ID = ").append(joinValue);
        }
        if (whereRequested) {
            sb.append(joinAllowed && joinRequested ? " AND " : " WHERE ").append(where);
        }
        return sb.toString();
    }
}
