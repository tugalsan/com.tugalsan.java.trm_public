package com.tugalsan.app.table.cell;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.app.table.sg.cell.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.rql.link.server.*;
import com.tugalsan.lib.table.server.*;
import javax.servlet.http.*;

public class AppSGECellGet extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGECellGet.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFCellGet) funcBase;
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
        var tn = f.getInput_tableName();
        var cp = TS_LibBootUtils.pck;
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
        return AppSGFCellGet.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        var cp = TS_LibBootUtils.pck;
        var f = (AppSGFCellGet) funcBase;
        d.ce("run", "DONT ABUSE ME", "id", f.getInput_id());
        TS_ThreadSyncLst<TS_LibRqlLinkUtils.GetBufferItem> scopedBuffer = TS_ThreadSyncLst.ofSlowWrite();
        var output_item = TS_LibRqlLinkUtils.get(
                servletKillTrigger,
                TS_LibRqlBufferUtils.items,
                TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), cp.sqlAnc),
                f.getInput_lookFromTablename(), TS_LibBootUtils.pck.sqlBCfg.tableName,
                f.getInput_tableName(),
                f.getInput_id(),
                scopedBuffer
        );
        f.setOutput_cell(output_item);
        d.ce("run", "output_item", f.getOutput_cell().linkText_WPrefixId);
    }
}
