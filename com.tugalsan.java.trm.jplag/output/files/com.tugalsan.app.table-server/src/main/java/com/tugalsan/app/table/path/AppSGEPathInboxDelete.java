package com.tugalsan.app.table.path;

import com.tugalsan.api.file.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.app.table.sg.path.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.table.server.*;
import javax.servlet.http.*;

public class AppSGEPathInboxDelete extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGEPathInboxDelete.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFPathInboxDelete) funcBase;
        var u_loginCard = TS_LibLoginCardUtils.get(servletKillTrigger, rq, f);
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
        return AppSGFPathInboxDelete.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
//        var loginCard = (TGS_LibLoginCard) vldRtn;
        var f = (AppSGFPathInboxDelete) funcBase;
        var tablename = f.getInput_tablename();
        var columnname = f.getInput_columnname();
        var filename = f.getInput_filename();
        d.ci("run", "tablename", tablename);
        d.ci("run", "columnname", columnname);
        d.ci("run", "filename", filename);

        var path = TS_LibTableFileDirUtils.datTblTblnameColname(
                TS_LibBootUtils.pck.dirDAT, tablename, columnname
        ).resolve(filename);
        d.ci("run", "path", path);

        TS_FileUtils.deleteFileIfExists(path);
        f.setOutput_result(!TS_FileUtils.isExistFile(path));
    }
}
