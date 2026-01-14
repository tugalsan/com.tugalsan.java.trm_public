package com.tugalsan.app.table.cell;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.network.server.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.thread.server.async.await.TS_ThreadAsyncAwait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.app.table.sg.cell.*;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.lib.rql.link.server.*;
import com.tugalsan.lib.table.server.*;
import java.time.Duration;
import javax.servlet.http.*;

public class AppSGECellSearch extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(false, AppSGECellSearch.class);

    @Override
    public int timeout_seconds() {
        return AppSGFCellSearch.MAX_SECS();
    }

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFCellSearch) funcBase;
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
        var tn = f.getInput_tarTablename();
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
        return AppSGFCellSearch.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        var cp = TS_LibBootUtils.pck;
        var f = (AppSGFCellSearch) funcBase;
        TS_ThreadSyncLst<TS_LibRqlLinkUtils.GetBufferItem> scopedBuffer = TS_ThreadSyncLst.ofSlowWrite();
        d.ci("run", "#0");
        var u_ip = TS_NetworkIPUtils.getIPClient(rq);
        d.ci("run", "#1");
        if (u_ip.isExcuse()) {
            d.ci("run", "#2");
            f.setOutput_ctm(System.currentTimeMillis());
            f.setOutput_list(null);
            f.setOutput_status("HATA: " + u_ip.excuse().getMessage());
            return;
        }
        d.ci("run", "#3");
        var ip = u_ip.value();
        d.ci("run", "#4", ip , f);
        var call = TS_ThreadAsyncAwait.callSingle(
                servletKillTrigger.newChild(d.className()).newChild("TS_LibRqlLink.run"),
                Duration.ofSeconds(f.getInput_maxSecs()),
                ktt -> TS_LibRqlLinkUtils.run(
                        ktt,
                        TS_LibRqlBufferUtils.items,
                        TS_LibTableDbSubUtils.convert(f.getInput_dbCfg(), cp.sqlAnc),
                        ip, f.getInput_minId(), f.getInput_maxId(),
                        f.getInput_lookFromTablename(), f.getInput_tarTablename(),
                        TS_LibBootUtils.pck.sqlBCfg.tableName,
                        f.getInput_minSize(), f.getInput_maxSize(),
                        f.getInput_spacedTags(), f.getInput_isKeyWordsAnd(),
                        scopedBuffer
                )
        );
        d.ci("run", "#5");
        if (servletKillTrigger.hasTriggered()) {
            d.ci("run", "#6");
            return;
        }
        d.ci("run", "#7");
        if (call.hasError()) {
            d.ci("run", "call.exceptionIfFailed().isPresent()");
            var e = call.exceptionIfFailed().orElseThrow();
            d.ce(ip, e);
            d.ci("run", "#9");
            f.setOutput_ctm(System.currentTimeMillis());
            f.setOutput_list(null);
            d.ci("run", "#10");
            if (call.timeout()) {
                d.ci("run", "#11");
                f.setOutput_status("HATA: Zaman aşımına uğradı, arama süre limitini arttırmayı deneyin.");
            } else {
                d.ci("run", "#12");
                f.setOutput_status("HATA: " + e.getMessage());
            }
            d.ci("run", "#13");
            return;
        }
        d.ci("run", "#14");
        var result = call.result().get();
        f.setOutput_ctm(result.value1_ctm());
        f.setOutput_list(result.value2_list());
        f.setOutput_status(result.value3_status());
        f.setOutput_isProcessedAsStar(result.value4_processedAsStar());
        d.ci("run", "#18");
    }
}
