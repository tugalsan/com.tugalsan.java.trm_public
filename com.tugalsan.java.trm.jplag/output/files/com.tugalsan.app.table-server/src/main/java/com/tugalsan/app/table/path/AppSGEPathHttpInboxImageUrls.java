package com.tugalsan.app.table.path;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.thread.server.sync.*;
import com.tugalsan.api.url.client.TGS_Url;
import com.tugalsan.app.table.sg.path.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.login.client.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.table.server.*;
import javax.servlet.http.*;

public class AppSGEPathHttpInboxImageUrls extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGEPathHttpInboxImageUrls.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFPathHttpInboxImageUrls) funcBase;
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
        var tn = f.getInput_tablename();
        if (!TS_LibRqlAllowTblUtils.readCheck_or_parentAllowes(servletKillTrigger.newChild(d.className()).newChild("validate").newChild("TS_LibRqlAllowTblUtils.readCheck_or_parentAllowes"), TS_LibRqlBufferUtils.items, cp.sqlAnc, loginCard, tn)) {
            var msg = "TS_LibTableUserUtils.isTableAllowedForRead:" + loginCard.userName + ":" + tn;
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFPathHttpInboxImageUrls.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        var loginCard = (TGS_LibLoginCard) vldRtn;
        var cp = TS_LibBootUtils.pck;
        var f = (AppSGFPathHttpInboxImageUrls) funcBase;

        var tablename = f.getInput_tablename();
        var id = f.getInput_id();
        var copyTempl = f.getInput_copyTemplateFileIfNeeded();
        var username = loginCard.userName.toString();
        d.ci("run", "tablename", tablename);
        d.ci("run", "id", id);
        d.ci("run", "copyTempl", copyTempl);
        d.ci("run", "username", username);

        var result = TS_LibTableFileUrlImgUtils.getDataIn_ImageLoc(
                servletKillTrigger, true,
                cp.sqlAnc, username,
                TGS_Url.of(f.getInput_url()),
                tablename, id, copyTempl
        );
        if (result == null) {
            return;
        }

        f.setOutput_imageUrls(result);
        if (d.infoEnable) {
            d.ci("run", "f.getOutput_imageUrls().size:", f.getOutput_imageUrls().size());
            f.getOutput_imageUrls().forEach(item -> {
                d.ci("run", "", item);
            });
        }
    }
}
