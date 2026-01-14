package com.tugalsan.app.table.row;

import com.tugalsan.api.file.obj.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.sql.insert.server.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.app.table.sg.row.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.login.client.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.rql.rev.server.*;
import com.tugalsan.lib.table.server.*;
import javax.servlet.http.*;

public class AppSGERowAdd extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(false, AppSGERowAdd.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFRowAdd) funcBase;
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
        var tn = f.getInput_tableName();
        if (!TS_LibRqlAllowTblUtils.readAndWriteCheck(servletKillTrigger.newChild(d.className()).newChild("validate").newChild("TS_LibRqlAllowTblUtils.readAndWriteCheck"), TS_LibRqlBufferUtils.items, cp.sqlAnc, loginCard, tn, null)) {
            var msg = "TS_LibTableUserUtils.isTableAllowedForReadAndWrite:" + loginCard.userName + ":" + tn;
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFRowAdd.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        var loginCard = (TGS_LibLoginCard) vldRtn;
        var f = (AppSGFRowAdd) funcBase;
        run(loginCard.userName, f);
    }

    public static void run(CharSequence un, AppSGFRowAdd f) {
        //d.infoEnable = true;
        if (d.infoEnable) {
            var id = ((TGS_SQLCellLNG) f.getInput_row().get(0)).getValueLong();
            f.getInput_row().stream().forEachOrdered(cell -> {
                d.ci("run", "cell", cell);
            });
            d.ci("run", "id", id);
        }
        var cp = TS_LibBootUtils.pck;
        var anchor = TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), cp.sqlAnc);

        d.ci("run", anchor.config.dbName, f.getInput_tableName(), f.getInput_row());

        //ONLY FOR REV CONTAINS BYTES
        if (f.getInput_dbCfg().isRev()) {
            d.ci("run", "mode rev");
            var bytes = (TGS_SQLCellBYTES) f.getInput_row().get(f.getInput_row().size() - 1);
            if (bytes.getValueBytes() == null) {
                bytes.imitateValueBytes(TS_FileObjUtils.toBytes(new Object[0]).orElse(e -> new byte[0]));
            }
            TS_SQLInsertUtils.insert(anchor, f.getInput_tableName()).valCell(f.getInput_row());
            f.setOutput_result(true);
            return;
        }

        //ALL ELSE
        d.ci("run", "mode normal");
        var lngId = (TGS_SQLCellLNG) f.getInput_row().get(0);
        d.ci("run", "lngId", lngId.getValueLong());
        var u_add = TS_LibRqlRevRowUtils.add(cp.sqlAnc, un, f.getInput_tableName(), lngId.getValueLong(), TS_LibRqlRevRowUtils.PARAM_ACT_CREATE_0(), f.getInput_row(), true);
        if (u_add.isExcuse()) {
            f.setOutput_result(false);
            d.ce("process", f, "ERROR: backUp.getData() == false", u_add.excuse().getMessage());
            d.ct("run", u_add.excuse());
            return;
        }
        d.ci("process", f, "after backup");
        d.ci("run", "lngId", f.getInput_row().get(0));
        var r = TS_SQLInsertUtils.insert(anchor, f.getInput_tableName()).valCell(f.getInput_row());
        d.ci("run", "r.affectedRowCount", r.affectedRowCount);
        d.ci("process", f, "after insert");
        f.setOutput_result(true);
    }
}
