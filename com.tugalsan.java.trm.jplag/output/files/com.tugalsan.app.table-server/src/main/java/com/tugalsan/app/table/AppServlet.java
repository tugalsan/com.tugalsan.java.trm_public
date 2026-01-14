package com.tugalsan.app.table;

import com.tugalsan.api.cast.client.TGS_CastUtils;
import com.tugalsan.lib.report.server.sge.TS_LibRepSGERun;
import com.tugalsan.lib.report.server.sge.TS_LibRepSGEProgress;
import javax.servlet.*;
import javax.servlet.annotation.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.path.*;
import com.tugalsan.app.table.query.*;
import com.tugalsan.app.table.rev.*;
import com.tugalsan.app.table.row.*;
import com.tugalsan.lib.boot.server.*;
import java.time.Duration;
import com.tugalsan.api.file.common.client.TGS_FileCommonFavIcon;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.sql.basic.server.TS_SQLBasicUtils;
import com.tugalsan.lib.login.server.TS_LibLoginUsernameUtils;
import com.tugalsan.lib.rql.allow.server.TS_LibRqlAllowTblUtils;
import com.tugalsan.lib.rql.buffer.server.TS_LibRqlBufferCreateUtils;

@WebListener
public class AppServlet implements ServletContextListener {

    final private static TS_Log d = TS_Log.of(AppServlet.class);
    final public static TGS_FileCommonFavIcon favIconSpi = TGS_FileCommonFavIcon.ofTxt("𝄜", null, false);
    final public static TGS_FileCommonFavIcon favIconBug = TGS_FileCommonFavIcon.ofTxt("🐛", null, false);

    public static String APP_NAME;

    @Override
    public void contextInitialized(ServletContextEvent evt) {
//        TS_SQLConnAnchor.use_sema = StableValue.supplier(() -> new TS_ThreadSyncSemaphore(32));
        APP_NAME = TS_LibBootUtils.warmUp_setLogPrefix_createKillTriggers_returnAppName_forApp(evt);
        TS_LibBootUtils.contextInitializedAsyncRun(
                Duration.ofMinutes(10),
                evt, "autosqlweb", "ASW", favIconSpi, favIconBug,
                kt -> {
                    TS_SGWTExecutorList.add(
                            new AppSGEConfigValueSet(),
                            new AppSGEExportExcel(),
                            new AppSGEInitPack(),
                            new AppSGECellGet(),
                            new AppSGECellGetList(),
                            new AppSGECellSearch(),
                            new AppSGECellUpdateBytesSTR(),
                            new AppSGECellUpdateLNG(),
                            new AppSGECellUpdateSTR(),
                            new AppSGEPathHttpInboxFileUrl(),
                            new AppSGEPathHttpInboxImageUrls(),
                            new AppSGEPathInboxDelete(),
                            new AppSGEPathInboxGetFileNames(),
                            new AppSGEQueryCount(),
                            new AppSGEQueryMaxId(),
                            new AppSGEQueryMinMaxId(),
                            new AppSGEQueryPage(),
                            new TS_LibRepSGERun(),
                            new TS_LibRepSGEProgress(),
                            new AppSGERevGetRowData(),
                            new AppSGERevRestoreRow(),
                            new AppSGERowAdd(),
                            new AppSGERowExists(),
                            new AppSGERowMultiply(),
                            new AppSGERowRemove(),
                            new AppSGERowUsage()
                    );
//                    TS_ThreadAsyncScheduled.everyDays_whenHourShow(TS_SURLWebServlet.killTrigger, Duration.ofMinutes(10), true, 1, 6, ___ -> {
//                        AppSGEInitPack.SYNC_USER_DATA.clear();
//                    });
                    var usernames = TS_LibLoginUsernameUtils.getUsernames(TS_LibBootUtils.pck.sqlAnc);
                    AppSGEInitPack.warmUp(TS_LibBootUtils.pck.sqlAnc, usernames);
                    var PARALLEL_THRESHOLD_MB_STR = TS_SQLBasicUtils.getStr(TS_LibBootUtils.pck.sqlAnc, TS_LibBootUtils.pck.sqlBCfg, "PARALLEL_THRESHOLD_MB", String.valueOf(TS_LibRqlBufferCreateUtils.PARALLEL_THRESHOLD_MB));
                    var PARALLEL_THRESHOLD_MB_INT = TGS_CastUtils.toInteger(PARALLEL_THRESHOLD_MB_STR).orElse(null);
                    if (PARALLEL_THRESHOLD_MB_INT == null) {
                        d.ce("contextInitialized", "PARALLEL_THRESHOLD_MB_STR", "WARNING: Cannot convert to int!");
                    } else {
                        TS_LibRqlBufferCreateUtils.PARALLEL_THRESHOLD_MB = PARALLEL_THRESHOLD_MB_INT;
                        d.cr("contextInitialized", "PARALLEL_THRESHOLD_MB", PARALLEL_THRESHOLD_MB_STR);
                    }
                }
        );
        TS_LibRqlAllowTblUtils.FULLYEDITABLETABLES_WHENDATEISNULL.add("personeltakip");
    }

    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        TS_LibBootUtils.contextDestroyed(evt);
    }
}
