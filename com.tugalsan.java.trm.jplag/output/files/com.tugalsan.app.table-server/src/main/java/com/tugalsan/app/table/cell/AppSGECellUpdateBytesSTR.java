package com.tugalsan.app.table.cell;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.sql.basic.server.TS_SQLBasicUtils;
import com.tugalsan.api.sql.select.server.TS_SQLSelectUtils;
import com.tugalsan.api.sql.update.server.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.app.table.sg.cell.*;
import com.tugalsan.lib.boot.server.*;
import static com.tugalsan.lib.boot.server.TS_LibBootUtils.pck;
import com.tugalsan.lib.login.client.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.rql.rev.server.*;
import com.tugalsan.lib.table.client.*;
import com.tugalsan.lib.table.server.*;
import java.util.Objects;
import javax.servlet.http.*;

public class AppSGECellUpdateBytesSTR extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGECellUpdateBytesSTR.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFCellUpdateBytesSTR) funcBase;
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
        if (!TS_LibRqlAllowTblUtils.readAndWriteCheck_Loosely(servletKillTrigger.newChild(d.className()).newChild("validate").newChild("TS_LibRqlAllowTblUtils.readAndWriteCheck_Loosely"), TS_LibRqlBufferUtils.items, cp.sqlAnc, loginCard, tn)) {
            var msg = "TS_LibTableUserUtils.readAndWriteCheck_Loosely:" + loginCard.userName + ":" + tn;
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, loginCard);
        } else {
            d.ce("validate", "WARNING: usage of TS_LibRqlAllowTblUtils.readAndWriteCheck_Loosely");
        }
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFCellUpdateBytesSTR.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        var loginCard = (TGS_LibLoginCard) vldRtn;
        var cp = TS_LibBootUtils.pck;
        var f = (AppSGFCellUpdateBytesSTR) funcBase;
        var un = loginCard.userName.toString();
        var tn = f.getInput_table().nameSql;

        //CREATE ANCHOR
        f.setInput_table(TGS_LibTableDbSubUtils.toConvert(f.getInput_dbCfg(), f.getInput_table()));
        var anchorConverted = TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), cp.sqlAnc);

        //CHECK SAME VALUE
        var prevVal = TS_SQLSelectUtils.select(anchorConverted, tn)
                .columns(f.getInput_columnname())
                .whereFirstColumnAsId(f.getInput_id())
                .getStr();
        var curVal = f.getInput_value() == null ? "" : f.getInput_value();
        if (Objects.equals(prevVal, curVal)) {
            d.ci("run", "same found, will skip", prevVal, curVal);
            f.setOutput_result(true);
            return;
        } else {
            d.ci("run", "not same, will continue", prevVal, curVal);
        }

        //LOG CHANGE IF ACHOR-NORMAL
        if (!f.getInput_dbCfg().isAny()) {
            var u_add = TS_LibRqlRevRowUtils.add(cp.sqlAnc, un, tn, f.getInput_id(), TS_LibRqlRevRowUtils.PARAM_ACT_MODIFY_1(), null, true);
            if (u_add.isExcuse()) {
                d.ce("run", "ERROR: sqlUpdateCellBytesSTR.backup == false", f, u_add.excuse().getMessage());
                f.setOutput_result(false);
                return;
            }
        }

        //UPDATE
        var bytes = TGS_FuncMTCUtils.call(() -> TGS_StringUtils.jre().toByte(curVal), e -> {
            d.ce("run", "ERROR sqlUpdateCellBytesSTR.bytes: " + e.getMessage());
            f.setOutput_result(false);
            return null;
        });
        if (bytes == null) {
            d.ce("run", "ERROR sqlUpdateCellBytesSTR.bytes başarısız oldu");
        }
        var res = TS_SQLUpdateUtils.update(anchorConverted, tn).set(set -> {
            set.add(new TGS_Tuple2(f.getInput_columnname(), bytes));
        }).whereFirstColumnAsId(f.getInput_id());
        f.setOutput_result(res.affectedRowCount == 1);

        //SET FAST CFG UPDATE TRIGGER FLAG
        if (f.getInput_dbCfg().isCfg()) {
            var lstAsStrln = TS_LibRqlBufferFastUpdateUtils.add(pck.sqlAnc, pck.sqlBCfg, tn);
            d.cr("run", TS_SQLBasicUtils.class.getSimpleName(), "strList_addItm", tn, lstAsStrln);
        }
    }
}
