package com.tugalsan.app.table;

import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTUUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.app.table.sg.cell.AppSGFCellGet;
import com.tugalsan.api.gui.client.browser.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.tuple.client.TGS_Tuple2;
import com.tugalsan.api.servlet.gwt.webapp.client.TGC_SGWTCalller;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.app.table.sg.init.*;
import com.tugalsan.lib.table.client.*;
import com.tugalsan.lib.rql.client.*;
import java.util.*;

public class AppModuleTableUtils {

    private AppModuleTableUtils() {

    }

    final private static TGC_Log d = TGC_Log.of(false, AppModuleTableUtils.class);

    public static void initialize(TGS_FuncMTU exe) {
        if (d.infoEnable) {
            d.ci("initialize", TGS_Time.toString_timeOnly_now(), "loading...");
        }
        var tblName = App.curTableName;
        if (d.infoEnable) {
            d.ci("initialize", TGS_Time.toString_timeOnly_now(), "TGC_SGWTCalller.async(new AppSGFInitPack(tblName)...)", "calling...");
        }
        TGC_SGWTCalller.async(new AppSGFInitPack(tblName), pack -> {
            if (d.infoEnable) {
                d.ci("initialize", TGS_Time.toString_timeOnly_now(), "TGC_SGWTCalller.async(new AppSGFInitPack(tblName)...)", "loading...");
            }
            App.tables = pack.getOutput_tables();
            App.userTableConfig = AppSGFInitPackUtils.toUserPacks(pack);
            if (d.infoEnable) {
                d.ci("initialize", TGS_Time.toString_timeOnly_now(), "TGC_SGWTCalller.async(new AppSGFInitPack(tblName)...)", "exe.run");
            }
            exe.run();
            if (d.infoEnable) {
                d.ci("initialize", TGS_Time.toString_timeOnly_now(), "TGC_SGWTCalller.async(new AppSGFInitPack(tblName)...)", "done");
            }
        });
        if (d.infoEnable) {
            d.ci("initialize", TGS_Time.toString_timeOnly_now(), "TGC_SGWTCalller.async(new AppSGFInitPack(tblName)...)", "post");
        }
    }

    public static boolean getAllowWrite(CharSequence userTableName) {
        var ti = App.tblUsrIdx(userTableName);
        if (ti == -1) {
            d.ce("getAllowWrite", "Tablename not found on user tables", userTableName);
            TGS_FuncMTUUtils.thrw(d.className(), "getAllowWrite", "ti == -1");
        }
        return App.userTableConfig.get(ti).allowFileWrite;
    }

    public static List<Integer> getHideIdxs(CharSequence userTableName) {
        var ti = App.tblUsrIdx(userTableName);
        if (ti == -1) {
            d.ce("getHideIdxs", "Tablename not found on user tables", userTableName);
            TGS_FuncMTUUtils.thrw(d.className(), "getHideIdxs", "ti == -1");
        }
        var tblColHideIdxsStr = App.userTableConfig.get(ti).colHideIdxes;
        List<Integer> colHideIdx = TGS_ListUtils.of();
        if (TGS_StringUtils.cmn().isNullOrEmpty(tblColHideIdxsStr)) {
            return colHideIdx;
        }
        var tblHideIdx_parsed = TGS_StringUtils.gwt().toList_spc(tblColHideIdxsStr);
        if (tblHideIdx_parsed.isEmpty()) {
            return colHideIdx;
        }
        var cs = App.tbl_mayThrow(userTableName).columns.size();
        tblHideIdx_parsed.forEach(idxs -> {
            var idx = TGS_CastUtils.toInteger(idxs).orElse(null);
            if (idx == null) {
                d.ce("getHideIdxs", "cannot parse hide idx", idxs);
                TGS_FuncMTUUtils.thrw(d.className(), "getHideIdxs", "idx == null");
            }
            if (idx < 0) {
                d.ce("getHideIdxs", "hide idx < 0", idxs);
                TGS_FuncMTUUtils.thrw(d.className(), "getHideIdxs", "idx < 0");
            }
            if (idx >= cs) {
                d.ce("getHideIdxs", "hide idx >= cs", idxs, cs);
                TGS_FuncMTUUtils.thrw(d.className(), "getHideIdxs", "idx >= cs");
            }
            colHideIdx.add(idx);
        });
        return colHideIdx;
    }

    public static void printKeyboardUsageIfNotMobile() {
        if (!TGC_BrowserNavigatorUtils.mobile()) {
            var infoEnable = d.infoEnable;
            d.infoEnable = true;
            d.ci("printKeyboardUsageIfNotMobile", "<i>İpUcu 1: Panellerde 'Esc' iptal eder; 'Ctrl ve Enter' değişikliği uygular; 'Del' satır siler</i>");
            d.ci("printKeyboardUsageIfNotMobile", "<i>İpUcu 2: Kolon başlığına sol fare tuşu veya üzerimde 'Enter', filitre açar.");
            d.ci("printKeyboardUsageIfNotMobile", "<i>İpUcu 3: 'Ctrl veya Shift'e basılı tutmak, diğer seçenekleri gösterir.");
            d.infoEnable = infoEnable;
        }
    }

    public static void openNewTableModify(TGS_LibRqlTbl table, String optional_idStr) {
        var tn = table.nameSql;
        var tnv = table.nameReadable;
        var newRoute = App.route.setParam(TGS_LibTableAppUtils.PARAM_CURRENT_TABLE_NAME(), tn);
        if (TGS_StringUtils.cmn().isPresent(optional_idStr)) {
            newRoute = newRoute.setParam(TGS_LibTableAppUtils.PARAM_FILTER_ID_START(), optional_idStr);
            newRoute = newRoute.setParam(TGS_LibTableAppUtils.PARAM_FILTER_ID_END(), optional_idStr);
        }
        TGC_BrowserWindowUtils.openNew(newRoute.toUrl());
        d.cr("onClick_OpenNewTableModify", "Tablo görüntüleniyor...", tnv);
    }

    public static TGS_Tuple2<String, TGS_LibRqlTbl> getTableIfAllowed_returnErrorAndTable(String targetTableName) {
        TGS_Tuple2<String, TGS_LibRqlTbl> pack = new TGS_Tuple2();

        if (App.loginCard.userNone) {
            pack.value0 = "HATA: Kullanıcı çıkışı algılandı!";
            return pack;
        }

        if (App.loginCard.userAdmin) {
            pack.value1 = App.tables.stream()
                    .filter(t -> Objects.equals(targetTableName, t.nameSql))
                    .findAny().orElse(null);
            return pack;
        }

        pack.value1 = App.userTableConfig.stream()
                .map(cfg -> cfg.table)
                .filter(t -> Objects.equals(targetTableName, t.nameSql))
                .findAny().orElse(null);
        if (pack.value1 != null) {
            return pack;
        }

        var tmp = App.tables.stream()
                .filter(t -> Objects.equals(targetTableName, t.nameSql))
                .findAny().orElse(null);
        if (tmp == null) {
            pack.value0 = "HATA: Tablo bulunamıyor hatası! " + targetTableName;
        } else {
            pack.value0 = "HATA: Kullanıcı'nın bu tabloyu açma yetkisi yok! " + targetTableName;
        }
        return pack;
    }

    //DONT FORCE IT!
    @Deprecated
    public static TGS_Tuple2<String, TGS_LibRqlTbl> getSQLLinkIfAllowed(AppModuleTable tm, String optionalSourceTable, String targetTableName, long id, TGS_FuncMTU_In1<AppSGFCellGet> exe) {
        var pack = AppModuleTableUtils.getTableIfAllowed_returnErrorAndTable(targetTableName);
        if (pack.value0 == null) {
            TGC_SGWTCalller.async(new AppSGFCellGet(tm.dbCfg, optionalSourceTable, targetTableName, id), r -> exe.run(r));
        }
        return pack;
    }

}
