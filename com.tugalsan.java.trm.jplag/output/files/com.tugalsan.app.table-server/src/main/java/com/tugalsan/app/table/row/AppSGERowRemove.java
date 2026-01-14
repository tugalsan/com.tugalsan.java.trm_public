package com.tugalsan.app.table.row;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.sql.basic.server.TS_SQLBasicUtils;
import com.tugalsan.api.sql.delete.server.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.app.table.sg.row.*;
import com.tugalsan.lib.boot.server.*;
import static com.tugalsan.lib.boot.server.TS_LibBootUtils.pck;
import com.tugalsan.lib.login.client.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.rql.rev.server.*;
import com.tugalsan.lib.table.client.*;
import com.tugalsan.lib.table.server.*;
import javax.servlet.http.*;

public class AppSGERowRemove extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGERowRemove.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFRowRemove) funcBase;
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
        var tn = f.getInput_table().nameSql;
        if (f.getInput_dbCfg().isTxt()) {
            if (!TS_LibRqlAllowTblUtils.readAndWriteCheck_Loosely(servletKillTrigger.newChild(d.className()).newChild("validate").newChild("TS_LibRqlAllowTblUtils.readAndWriteCheck_Loosely"), TS_LibRqlBufferUtils.items, cp.sqlAnc, loginCard, tn)) {
                var msg = "TS_LibTableUserUtils.readAndWriteCheck_Loosely:" + loginCard.userName + ":" + tn;
                f.setExceptionMessage(msg);
                d.ce("validate", msg);
                return new TS_SGWTValidationResult(false, loginCard);
            } else {
                d.ce("validate", "WARNING: usage of TS_LibRqlAllowTblUtils.readAndWriteCheck_Loosely");
            }
        } else {
            if (!TS_LibRqlAllowTblUtils.readAndWriteCheck(servletKillTrigger.newChild(d.className()).newChild("validate").newChild("TS_LibRqlAllowTblUtils.readAndWriteCheck"), TS_LibRqlBufferUtils.items, cp.sqlAnc, loginCard, tn, f.getInput_Id())) {
                var msg = "TS_LibTableUserUtils.isTableAllowedForReadAndWrite:" + loginCard.userName + ":" + tn;
                f.setExceptionMessage(msg);
                d.ce("validate", msg);
                return new TS_SGWTValidationResult(false, loginCard);
            }
        }
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFRowRemove.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        var loginCard = (TGS_LibLoginCard) vldRtn;
        var cp = TS_LibBootUtils.pck;
        var f = (AppSGFRowRemove) funcBase;

        f.setInput_table(TGS_LibTableDbSubUtils.toConvert(f.getInput_dbCfg(), f.getInput_table()));
        var anchor = TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), cp.sqlAnc);

        var un = loginCard.userName.toString();
        var tn = f.getInput_table().nameSql;

        d.ci("run", anchor.config.dbName, f.getInput_table(), f.getInput_Id());

        if (f.getInput_dbCfg().isAny()) {
            TS_SQLDeleteUtils.delete(anchor, tn).whereFirstColumnAsId(f.getInput_Id());
            f.setOutput_result(true);
            if (f.getInput_dbCfg().isCfg()) {
                var lstAsStrln = TS_LibRqlBufferFastUpdateUtils.add(pck.sqlAnc, pck.sqlBCfg, tn);
                d.cr("run", TS_SQLBasicUtils.class.getSimpleName(), "strList_addItm", tn, lstAsStrln);
            }
            return;
        }

        var u_add = TS_LibRqlRevRowUtils.add(cp.sqlAnc, un, tn, f.getInput_Id(), TS_LibRqlRevRowUtils.PARAM_ACT_DELETE_2(), null, true);
        if (u_add.isExcuse()) {
            f.setOutput_result(false);
            d.ce("process", f, "ERROR: backUp.getData() == false", u_add.excuse().getMessage());
        }

        TS_SQLDeleteUtils.delete(anchor, tn).whereFirstColumnAsId(f.getInput_Id());
        f.setOutput_result(true);
    }
}
