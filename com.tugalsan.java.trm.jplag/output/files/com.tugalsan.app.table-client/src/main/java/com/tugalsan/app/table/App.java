package com.tugalsan.app.table;

import com.tugalsan.app.table.sg.init.*;
import java.util.*;
import com.tugalsan.api.file.common.client.TGS_FileCommonFavIcon;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.api.file.html.client.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTUUtils;
import com.tugalsan.api.gui.client.browser.*;
import com.tugalsan.api.gui.client.click.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.time.client.*;
import com.tugalsan.lib.boot.client.*;
import com.tugalsan.lib.domain.client.*;
import com.tugalsan.lib.login.client.*;
import com.tugalsan.lib.route.client.*;
import com.tugalsan.lib.scale.client.*;
import com.google.gwt.core.client.*;
import com.tugalsan.lib.table.client.TGS_LibTableAppUtils;

// *!* Dont change my hardcoded classpath com.tugalsan.app.table.App *!*
public class App implements EntryPoint {

    final public static TGS_FileCommonFavIcon favIconBug = TGS_FileCommonFavIcon.ofTxt("🐞", null, false);
    final public static TGS_FileCommonFavIcon favIconReport = TGS_FileCommonFavIcon.ofTxt("📈", null, false);
    final public static TGS_FileCommonFavIcon favIconDocument = TGS_FileCommonFavIcon.ofTxt("📜", null, false);

    final private static TGC_Log d = TGC_Log.of(false, App.class);
    final public static TGS_LibRoute route = TGC_LibRouteUtils.of();
    final public static CharSequence curTableName = route.getParamStr(TGS_LibTableAppUtils.PARAM_CURRENT_TABLE_NAME());
    public static TGS_Time warVersion;
    public static TGS_LibDomainCard domainCard;
    public static TGS_LibLoginCard loginCard;
    public static List<TGS_LibRqlTbl> tables;
    public static List<AppSGFInitPack_ConfigTableUser> userTableConfig;

    @Override
    public void onModuleLoad() {
        if (d.infoEnable) {
            d.ci("onModuleLoad", TGS_Time.toString_timeOnly_now(), "init");
        }
        TGC_LibBootUtils.onModuleLoad(route, (_warVersion, _domainCard, runAcc2Url) -> {
            App.warVersion = _warVersion;
            App.domainCard = _domainCard;
            if (d.infoEnable) {
                d.ci("onModuleLoad", TGS_Time.toString_timeOnly_now(), "TGC_LibBootUtils.onModuleLoad", "init");
            }
            TGC_LibLoginCardUtils.async(_loginCard -> {
                App.loginCard = _loginCard;
                if (d.infoEnable) {
                    d.ci("onModuleLoad", TGS_Time.toString_timeOnly_now(), "TGC_LibLoginCardUtils.async", "init");
                }
                if (loginCard.userNone) {
                    if (d.infoEnable) {
                        d.ci("onModuleLoad", TGS_Time.toString_timeOnly_now(), "TGC_LibLoginCardUtils.async", "loginCard.userNone");
                    }
                    var err = new TGS_FileHtmlText().setBold(true).setHexcolor("FF0000")
                            .setText("HATA: Kullanıcı algılanamadı:<br>- Geri dönmek için tıklayınız.");
                    var btn = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_BUG(), err.toString());
                    TGC_ClickUtils.add(btn, () -> TGC_BrowserWindowUtils.openSame(
                            route.setRouteApp(TGS_LibScaleMemUtils.cloud()).delMdl().toUrl())
                    );
                    TGC_LibBootGUIBody.setToCenter(btn);
                    d.ce("onModuleLoad", "loginCard", err);
                    return;
                }
                if (d.infoEnable) {
                    d.ci("onModuleLoad", TGS_Time.toString_timeOnly_now(), "TGC_LibLoginCardUtils.async", "loginCard", loginCard.userName);
                }
                AppModuleTableUtils.initialize(() -> {
                    if (runAcc2Url == null) {
                        d.ce("onModuleLoad", "TGC_LibLoginCardUtils.async", "AppModuleTableUtils.initialize", "HATA: runAcc2Url == null");
                        return;
                    }
                    if (d.infoEnable) {
                        d.ci("onModuleLoad", TGS_Time.toString_timeOnly_now(), "TGC_LibLoginCardUtils.async", "AppModuleTableUtils.initialize", "runAcc2Url.run()...");
                    }
                    runAcc2Url.run();
                });
                if (d.infoEnable) {
                    d.ci("onModuleLoad", TGS_Time.toString_timeOnly_now(), "TGC_LibLoginCardUtils.async", "AppModuleTableUtils.initialize", "post");
                }
            });
            if (d.infoEnable) {
                d.ci("onModuleLoad", TGS_Time.toString_timeOnly_now(), "TGC_LibLoginCardUtils.async", "pst");
            }
        },
                new AppModuleDefault(),
                new AppModuleTable(),
                new TGC_LibBootModuleFile()
        );
        if (d.infoEnable) {
            d.ci("onModuleLoad", TGS_Time.toString_timeOnly_now(), "TGC_LibBootUtils.onModuleLoad", "pst");
        }
    }

    public static TGS_LibRqlTbl tbl_canReturnNull(CharSequence tableName) {
        if (tableName == null || App.tables == null) {
            return null;
        }
        return TGS_LibRqlTblUtils.tbl(App.tables, tableName);
    }

    public static TGS_LibRqlTbl tbl_mayThrow(CharSequence tableName) {
        if (tableName == null) {
            d.ce("tbl", "Tablename not found on tables", "tableName == null");
            TGS_FuncMTUUtils.thrw(d.className(), "tbl", "tableName == null @tableName:" + tableName);
        }
        if (App.tables == null) {
            d.ce("tbl", "Tablename not found on tables", "App.tables == null", tableName);
            TGS_FuncMTUUtils.thrw(d.className(), "tbl", "App.tables == null @tableName:" + tableName);
        }
        return TGS_LibRqlTblUtils.tbl(App.tables, tableName);
    }

    public static int tblUsrIdx(CharSequence tableName) {
        if (tableName == null) {
            d.ce("tblUsrIdx", "Tablename not found on user tables", "tableName == null");
            TGS_FuncMTUUtils.thrw(d.className(), "tblUsrIdx", "tableName == null @tableName:" + tableName);
        }
        if (App.userTableConfig == null) {
            d.ce("tblUsrIdx", "Tablename not found on user tables", "App.userTables == null", tableName);
            TGS_FuncMTUUtils.thrw(d.className(), "tblUsrIdx", "App.userTables == null @tableName:" + tableName);
        }
        return TGS_LibRqlTblUtils.tblIdx(TGS_StreamUtils.toLst(App.userTableConfig.stream().map(cfg -> cfg.table)), tableName);
    }
}
