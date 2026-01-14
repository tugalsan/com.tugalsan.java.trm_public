package com.tugalsan.app.table;

import javax.servlet.http.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.app.table.sg.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.sql.basic.server.TS_SQLBasicUtils;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.buffer.server.TS_LibRqlBufferFastUpdateUtils;
import com.tugalsan.lib.rql.cfg.client.TGS_LibRqlCfgUtils;
import com.tugalsan.lib.rql.cfg.server.*;
import java.util.Objects;

public class AppSGEConfigValueSet extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(AppSGEConfigValueSet.class);

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFConfigValueSet) funcBase;
        var u_loginCard = TS_LibLoginCardUtils.get(servletKillTrigger, rq, f);
        if (u_loginCard.isExcuse()) {
            var msg = "loginCard==:" + u_loginCard.excuse().getMessage() + " -> " + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, null);
        }
        var loginCard = u_loginCard.value();
        if (Objects.equals(f.getInput_parameter(), TGS_LibRqlCfgUtils.PARAM_RENDER_CONFIG()) || Objects.equals(f.getInput_parameter(), TGS_LibRqlCfgUtils.PARAM_FILTER_CONFIG())) {
            if (loginCard.userNone) {
                var msg = "loginCard.userNone:" + funcBase.getInput_url();
                f.setExceptionMessage(msg);
                d.ce("validate", msg);
                return new TS_SGWTValidationResult(false, loginCard);
            }
        } else {
            if (!loginCard.userAdmin) {
                var msg = "!loginCard.userAdmin:" + funcBase.getInput_url();
                f.setExceptionMessage(msg);
                d.ce("validate", msg);
                return new TS_SGWTValidationResult(false, loginCard);
            }
        }
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFConfigValueSet.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
//        var loginCard = (TGS_LibLoginCard) vldRtn;
        var pck = TS_LibBootUtils.pck;
        var f = (AppSGFConfigValueSet) funcBase;
        f.setOutput_id(TS_LibRqlCfgRowUtils.set(pck.sqlAnc,
                f.getInput_targetObject(),
                f.getInput_parameter(),
                f.getInput_value()
        ));
        var tn = f.getInput_targetObject();
        var lstAsStrln = TS_LibRqlBufferFastUpdateUtils.add(pck.sqlAnc, pck.sqlBCfg, tn);
        d.cr("run", TS_SQLBasicUtils.class.getSimpleName(), "strList_addItm", tn, lstAsStrln);
    }
}
