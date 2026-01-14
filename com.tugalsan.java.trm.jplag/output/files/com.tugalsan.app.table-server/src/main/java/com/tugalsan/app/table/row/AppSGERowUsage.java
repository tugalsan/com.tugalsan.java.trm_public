package com.tugalsan.app.table.row;

import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.sql.select.server.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.app.table.sg.row.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.client.*;
import javax.servlet.http.*;

public class AppSGERowUsage extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGERowUsage.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFRowUsage) funcBase;
        var u_loginCard = TS_LibLoginCardUtils.get(servletKillTrigger, rq, f);
        if (u_loginCard.isExcuse()) {
            var msg = "loginCard==:" + u_loginCard.excuse().getMessage() + " -> " + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, null);
        }
        var loginCard = u_loginCard.value();
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
        return AppSGFRowUsage.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
//        var loginCard = (TGS_LibLoginCard) vldRtn;
        var f = (AppSGFRowUsage) funcBase;
        var cp = TS_LibBootUtils.pck;

        var tn = f.getInput_tableName();
        var id = f.getInput_id();
        f.setOutput_tableSQLNames(TGS_ListUtils.of());

        var sb = new StringBuilder();
        {//CHECK FOR LONG TEXT COLUMN
            var disableLongTextOnDelRow = true;//DO NOT MAKE IT FALSE, because backup is not enabled for txt table yet
            var error254Found = false;
            var table = TS_LibRqlBufferUtils.get(tn);
            if (table == null) {
                f.setOutput_summary("ERROR: curTable == null");
                return;
            }
            for (var ct : table.columns) {
                if (servletKillTrigger.hasTriggered()) {
                    return;
                }
                var tc = TGS_LibRqlColUtils.toSqlCol(ct);
                if (!tc.typeStr()) {
                    continue;
                }
                var r = TS_SQLSelectUtils.select(cp.sqlAnc, tn).columns(ct.getColumnName()).whereConditionAnd(conditions -> {
                    conditions.lngEq("LNG_ID", id);
                }).groupNone().orderNone().rowIdxOffsetNone().rowSizeLimitNone().getStr();
                if (r == null) {
                    f.setOutput_summary("ERROR: ServletCheckForDelete.funcVersion..r == null");
                    return;
                } else if (disableLongTextOnDelRow && r.startsWith("BYTESSTR ")) {
                    var value = r.substring("BYTESSTR ".length());
                    var valueLong = TGS_CastUtils.toLong(value).orElse(null);
                    if (valueLong == null) {
                        f.setOutput_summary("ERROR: ServletCheckForDelete.funcVersion.valueLong == null");
                        return;
                    }
                    if (!error254Found) {
                        error254Found = true;
                        sb.append("ERROR: Silme işlemine devam edebilmek için aşağıdaki kolonlardaki hücreleri 254 karaterin altına indiriniz.\n");
                    }
                    sb.append("  - ").append(ct.getColumnNameVisible()).append("\n");
                } else {
                    //SKIP
                }
            }
            if (error254Found) {
                sb.append("\n\n");
            }
        }

        //CHECK FOR LNG_LINK
        TS_LibRqlBufferUtils.items.forEach(false, t -> {
            if (servletKillTrigger.hasTriggered()) {
                return;
            }
            var tName = t.nameSql;
            for (var ct : t.columns) {
                var tc = TGS_LibRqlColUtils.toSqlCol(ct);
                if (!tc.typeLngLnk()) {
                    continue;
                }
                if (!TGS_CharSetCast.current().equalsIgnoreCase(ct.getDataString1_LnkTargetTableName(), tn)) {
                    continue;
                }
                var subIds = TS_SQLSelectUtils.select(cp.sqlAnc, tName).columns(0).whereConditionOr(c -> {
                    c.lngEq(ct.getColumnName(), id);
                }).groupNone().orderNone().rowIdxOffsetNone().rowSizeLimitNone().getLngLst();
                if (!subIds.isEmpty()) {
                    f.getOutput_tableSQLNames().add(new TGS_Tuple2(tName, subIds));
                    sb.append(t.nameReadable).append(" tablosunun,\n")
                            .append(ct.getColumnNameVisible()).append(" kolonunda,\n")
                            .append(" kullanilan idler:");
                    subIds.forEach(subId -> sb.append(" ").append(subId));
                    sb.append("\n\n");
                }
            }
        });
        TS_LibRqlBufferUtils.items.forEach(false, t -> {
            if (servletKillTrigger.hasTriggered()) {
                return;
            }
            var tName = t.nameSql;
            for (var ct : t.columns) {
                if (servletKillTrigger.hasTriggered()) {
                    return;
                }
                var tc = TGS_LibRqlColUtils.toSqlCol(ct);
                if (!tc.typeStrLnk()) {
                    continue;
                }
                if (!TGS_CharSetCast.current().equalsIgnoreCase(ct.getDataString1_LnkTargetTableName(), tn)) {
                    continue;
                }
                var subIds = TS_SQLSelectUtils.select(cp.sqlAnc, tName).columns(0).whereConditionOr(c -> {
                    var sId = String.valueOf(id);
                    c.strLike(ct.getColumnName(), sId, " ");
                }).groupNone().orderNone().rowIdxOffsetNone().rowSizeLimitNone().getLngLst();
                if (!subIds.isEmpty()) {
                    f.getOutput_tableSQLNames().add(new TGS_Tuple2(tName, subIds));
                    sb.append(t.nameReadable).append(" tablosunun,\n")
                            .append(ct.getColumnNameVisible()).append(" kolonunda,\n")
                            .append(" kullanilan idler:");
                    subIds.forEach(subId -> sb.append(" ").append(subId));
                    sb.append("\n\n");
                }
            }
        });
        f.setOutput_summary(sb.toString());
    }
}
