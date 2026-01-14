package com.tugalsan.app.table.control.utils;

import com.tugalsan.lib.rql.client.*;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.pop.options.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.lib.report.client.sgf.*;
import java.util.*;

public class AppCtrReportIntUtils {

    final private static TGC_Log d = TGC_Log.of(true, AppCtrReportBoolUtils.class);

    private AppCtrReportIntUtils() {

    }

    public static void compile(TGC_PopFrame pf, TGS_LibRqlTbl t, TGS_LibRqlReport r, List<String> fileTypes, Long id,
            List<TGS_LibRepSGFRun_Param> clientCodeParams, int clientCodeIdx,
            String code, List<String> codeTags, String cmd) {
//        var IDX0_CMD = 0;
        var IDX1_PARAM_NAME = 1;
        var IDX2_DEFAULT = 2;
        var IDX3_MIN = 3;
        var IDX4_MAX = 4;
        var IDX5_QUESTION = 5;
        d.consoleOnly_ci_ifInfoEnable("compile", "cmd", "detected as integer");
        if (codeTags.size() < 5) {
            d.ce("compile", "ERROR: ClienCode", "tags.size() < 5", code);
            return;
        }
        var paramName = codeTags.get(IDX1_PARAM_NAME);
        d.consoleOnly_ci_ifInfoEnable("compile", "paramName", paramName);
        var defStr = codeTags.get(IDX2_DEFAULT);
        d.consoleOnly_ci_ifInfoEnable("compile", "defStr", defStr);
        var def = TGS_CastUtils.toLong(defStr).orElse(null);
        d.consoleOnly_ci_ifInfoEnable("compile", "def", def);
//        if (def == null) {
//            d.ce("compile", "ERROR: ClienCode", "defStr is not integer", defStr);
//            return;
//        }
        var minStr = codeTags.get(IDX3_MIN);
        d.consoleOnly_ci_ifInfoEnable("compile", "minStr", minStr);
        var min = TGS_CastUtils.toLong(minStr).orElse(null);
        d.consoleOnly_ci_ifInfoEnable("compile", "min", min);
//        if (min == null) {
//            d.ce("compile", "ERROR: ClienCode", "minStr is not integer", minStr);
//            return;
//        }
        var maxStr = codeTags.get(IDX4_MAX);
        d.consoleOnly_ci_ifInfoEnable("compile", "maxStr", maxStr);
        var max = TGS_CastUtils.toLong(maxStr).orElse(null);
        d.consoleOnly_ci_ifInfoEnable("compile", "max", max);
//        if (max == null) {
//            d.ce("compile", "ERROR: ClienCode", "maxStr is not integer", maxStr);
//            return;
//        }
        var que = codeTags.get(IDX5_QUESTION);
        d.consoleOnly_ci_ifInfoEnable("compile", "que", que);
        if (def == null) {
            d.consoleOnly_ci_ifInfoEnable("compile", "def == null");
            var defText = codeTags.get(IDX2_DEFAULT);
            d.consoleOnly_ci_ifInfoEnable("compile", "defText", defText);
            if (Objects.equals(defText, TGS_PopYesNoOptionCodes.VAL_YEAR_CUR())) {
                def = (long) TGS_Time.getCurrentYear();
            } else if (Objects.equals(defText, TGS_PopYesNoOptionCodes.VAL_YEAR_PREV())) {
                def = (long) TGS_Time.getCurrentYear() - 1;
            } else if (Objects.equals(defText, TGS_PopYesNoOptionCodes.VAL_YEAR_NEXT())) {
                def = (long) TGS_Time.getCurrentYear() + 1;
            } else if (Objects.equals(defText, TGS_PopYesNoOptionCodes.VAL_TODAY())) {
                def = TGS_Time.getCurrentDate();
            } else if (min != null) {
                def = min;
            }
            d.consoleOnly_ci_ifInfoEnable("compile", "def", def);
        }
        d.consoleOnly_ci_ifInfoEnable("compile", "pop...");
        var pop = new TGC_PopLblYesNoTextBoxExtraBtn2(
                new TGC_Dimension(TGC_PanelLayoutUtils.MAX_GRID_WIDTH(), null, true),
                "Bir seçenek seçin:", "Uygula", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    d.consoleOnly_ci_ifInfoEnable("compile", "cmd", "processing integer input...");
                    var val = TGS_CastUtils.toInteger(p.textBox.getText()).orElse(null);
                    if (val == null) {
                        d.ce("compile", "HATA: Tam sayı tanımlanamadığı için işlem sonlandırıldı.", p.textBox.getText());
                        return;
                    }
                    clientCodeParams.add(TGS_LibRepSGFRun_Param.of(paramName, String.valueOf(val)));
                    AppCtrReportMainUtils.line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
                },
                p -> {
                    p.getPop().setVisible(false);
                    d.cr("compile", "BİLGİ: İşlem kullanıcı tarafından sonlandırıldı.");
                },
                null
        );
        d.consoleOnly_ci_ifInfoEnable("compile", "cmd", "requesting integer input...");
        d.consoleOnly_ci_ifInfoEnable("compile", "pop.label...");
        pop.label.setText(que);
        d.consoleOnly_ci_ifInfoEnable("compile", "pop.textBox...");
        pop.textBox.setText(def == null ? "" : String.valueOf(def));
        d.consoleOnly_ci_ifInfoEnable("compile", "pop.btnAddShowAs...");
        pop.btnAddShowAs(TGS_IconUtils.CLASS_FIRST(), "En Az", TGS_IconUtils.CLASS_LAST(), "En Çok");
        d.consoleOnly_ci_ifInfoEnable("compile", "pop.btnAddHide...");
        pop.btnAddHide(min == null, max == null);
        d.consoleOnly_ci_ifInfoEnable("compile", "pop.btnAddSet...");
        pop.btnAddSet(btnMin -> {
            if (min == null) {
                d.ce("btnMin", "ERROR: Nothing to do", "min limit not set");
                return;
            }
            pop.textBox.setText(String.valueOf(min));
        }, btnMax -> {
            if (max == null) {
                d.ce("btnMax", "ERROR: Nothing to do", "max limit not set");
                return;
            }
            pop.textBox.setText(String.valueOf(max));
        });
        d.consoleOnly_ci_ifInfoEnable("compile", "pop.setVisible...");
        pop.getPop().setVisible(true);
        d.consoleOnly_ci_ifInfoEnable("compile", "pop.end");
    }
}
