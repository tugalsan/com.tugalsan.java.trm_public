package com.tugalsan.app.table.rev;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.app.table.sg.rev.AppSGFRevRestoreRow;
import com.tugalsan.lib.boot.server.TS_LibBootUtils;
import javax.servlet.http.HttpServletRequest;
import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.tugalsan.api.servlet.gwt.webapp.server.TS_SGWTExecutor;
import com.tugalsan.api.servlet.gwt.webapp.server.TS_SGWTValidationResult;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.lib.rql.rev.server.TS_LibRqlRevRowUtils;
import com.tugalsan.lib.login.server.*;

public class AppSGERevRestoreRow extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGERevRestoreRow.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFRevRestoreRow) funcBase;
        var u_loginCard = TS_LibLoginCardUtils.get(servletKillTrigger,rq, f);
        if (u_loginCard.isExcuse()) {
            var msg = "loginCard==:" + u_loginCard.excuse().getMessage() + " -> " + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, null);
        }
        var loginCard = u_loginCard.value();
        if (!loginCard.userAdmin) {
            var msg = "!loginCard.userAdmin:" + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFRevRestoreRow.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
//        var loginCard = (TGS_LibLoginCard) vldRtn;
        var cp = TS_LibBootUtils.pck;
        var f = (AppSGFRevRestoreRow) funcBase;
        TS_LibRqlRevRowUtils.restore(cp.sqlAnc, f.getInput_data(), f.getInput_tableName(), f.getInput_overwriteId());
    }
}
