package com.tugalsan.app.table.cell;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
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

public class AppSGECellGetList extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGECellGetList.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFCellGetList) funcBase;
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
        return AppSGFCellGetList.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        var cp = TS_LibBootUtils.pck;
        var f = (AppSGFCellGetList) funcBase;
        d.cr("run", "ids", f.getInput_ids());
        TS_ThreadSyncLst<TS_LibRqlLinkUtils.GetBufferItem> scopedBuffer = TS_ThreadSyncLst.ofSlowWrite();
        var output_lst = f.getInput_ids().stream().map(id -> {
            if (id == null) {
                return null;
            }
            return TS_LibRqlLinkUtils.get(
                    servletKillTrigger, TS_LibRqlBufferUtils.items,
                    TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), cp.sqlAnc),
                    f.getInput_lookFromTablename(), TS_LibBootUtils.pck.sqlBCfg.tableName,
                    f.getInput_tableName(), id, scopedBuffer
            );
        }).toList();
        f.setOutput_errTexts(TGS_StreamUtils.toLst(output_lst.stream().map(o -> o.errTxt)));
        f.setOutput_linkTexts(TGS_StreamUtils.toLst(output_lst.stream().map(o -> o.linkText)));
        f.setOutput_linkText_WPrefixIds(TGS_StreamUtils.toLst(output_lst.stream().map(o -> o.linkText_WPrefixId)));
        d.cr("run", "output_lst", f.getOutput_linkText_WPrefixIds());
    }
}
