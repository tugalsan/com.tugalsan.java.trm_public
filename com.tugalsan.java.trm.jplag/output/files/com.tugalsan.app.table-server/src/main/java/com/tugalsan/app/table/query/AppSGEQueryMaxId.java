package com.tugalsan.app.table.query;

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
import com.tugalsan.lib.table.server.*;
import javax.servlet.http.*;

public class AppSGEQueryMaxId extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGEQueryMaxId.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFQueryMaxId) funcBase;
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
                var msg = "!isAny(" + f.getInput_dbCfg().toString() + ").loginCard.userAdmin:" + funcBase.getInput_url();
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
        return AppSGFQueryMaxId.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
//        var loginCard = (TGS_LibLoginCard) vldRtn;
        var anchor = TS_LibBootUtils.pck.sqlAnc;
        var f = (AppSGFQueryMaxId) funcBase;

        var sql = anchor.tagSelectAndSpace() + "MAX(" + f.getInput_columnName() + ") FROM " + f.getInput_tableName();
        var where = TGS_StringUtils.cmn().toNullIfEmpty(f.getInput_where());
        if (where != null) {
            sql += " WHERE " + f.getInput_where();
        }
        TS_SQLConnWalkUtils.query(
                TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), anchor),
                sql,
                fillStmt -> {
                },
                rs -> f.setOutput_id(rs.lng.get(0, 0))
        );
    }
}
