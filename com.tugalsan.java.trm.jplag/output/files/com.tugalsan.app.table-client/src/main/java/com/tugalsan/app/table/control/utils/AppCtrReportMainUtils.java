package com.tugalsan.app.table.control.utils;

import com.tugalsan.api.gui.client.pop.TGC_PopFrame;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.rql.client.TGS_LibRqlReport;
import com.tugalsan.api.gui.client.pop.options.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.lib.report.client.sgf.TGS_LibRepSGFRun_Param;
import java.util.*;
import java.util.stream.*;

public class AppCtrReportMainUtils {

    private AppCtrReportMainUtils() {

    }

    final private static TGC_Log d = TGC_Log.of(AppCtrReportMainUtils.class);

    public static void init(AppModuleTable tm, int reportIdx, List<String> fileTypes) {
        var report = tm.curTable.reports[reportIdx];
        var id = tm.cells.getActiveRowId();
        List<TGS_LibRepSGFRun_Param> clientCodeParams = TGS_ListUtils.of();
        AppCtrReportMainUtils.line(tm.report.popFrame, tm.curTable, report, fileTypes, id, clientCodeParams, 0);
    }

    public static void line(TGC_PopFrame pf, TGS_LibRqlTbl t, TGS_LibRqlReport r, List<String> fileTypes, Long id,
            List<TGS_LibRepSGFRun_Param> clientCodeParams, int clientCodeIdx) {
        var parsedCodes = TGS_StringUtils.gwt().toList(r.value, "\n");
        if (clientCodeIdx >= parsedCodes.size()) {
            if (d.infoEnable) {
                d.ci("compile", "clientCodes decided as");
                clientCodeParams.forEach(param -> {
                    d.ci("compile", "-", param.name, param.value);
                });
            }
            AppCtrReportExeUtils.decide(pf, t, r, fileTypes, id, clientCodeParams);
            return;
        }
        var code = parsedCodes.get(clientCodeIdx);
        d.ci("compile", "handling client code", code);
        var codeTags = TGS_StringUtils.gwt().toList(code, TGS_PopYesNoOptionCodes.DELIM());
        if (codeTags.isEmpty()) {
            line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
            return;
        }
        var cmd = codeTags.get(0);
        if (d.infoEnable) {
            IntStream.range(0, codeTags.size()).forEachOrdered(i -> d.ci("compile", "tags", i, codeTags.get(i)));
            d.ci("compile", "cmd", cmd);
        }
        if (Objects.equals(cmd, TGS_PopYesNoOptionCodes.REQ_ID())) {
            if (id == null) {
                d.ce("start", "HATA: Tablodan satır seçilmedi!");
                return;
            }
            d.ci("start", "Bilgi: Satır seçildiği algılandı");
            AppCtrReportMainUtils.line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
            return;
        }
        if (Objects.equals(cmd, TGS_PopYesNoOptionCodes.REQ_ID_IF())) {
            var IDX1_PARAM_NAME = 1;
            var IDX2_PARAM_VALUE_TRIGGER = 2;
            var paramName = codeTags.get(IDX1_PARAM_NAME);
            var paramValue = codeTags.get(IDX2_PARAM_VALUE_TRIGGER);
            var triggerFound = clientCodeParams.stream()
                    .anyMatch(p -> Objects.equals(paramName, p.name) && Objects.equals(paramValue, p.value));
            if (!triggerFound) {
                AppCtrReportMainUtils.line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
                return;
            }
            if (id == null) {
                d.ce("start", "HATA: Tablodan satır seçilmedi!");
                return;
            }
            d.ci("start", "Bilgi: Satır seçildiği algılandı");
            AppCtrReportMainUtils.line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
            return;
        }
        if (Objects.equals(cmd, TGS_PopYesNoOptionCodes.REQ_IDX())) {
            AppCtrReportIdxUtils.compile(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx, code, codeTags, cmd);
            return;
        }
        if (Objects.equals(cmd, TGS_PopYesNoOptionCodes.REQ_BOOL())) {
            AppCtrReportBoolUtils.compile(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx, code, codeTags, cmd);
            return;
        }
        if (Objects.equals(cmd, TGS_PopYesNoOptionCodes.REQ_INT())) {
            AppCtrReportIntUtils.compile(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx, code, codeTags, cmd);
            return;
        }
        if (Objects.equals(cmd, TGS_PopYesNoOptionCodes.EXE_REDIRECT()) || Objects.equals(cmd, TGS_PopYesNoOptionCodes.EXE_NEWTAB())) {
            //NOTHING TO COMPILE
            AppCtrReportMainUtils.line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
            return;
        }
        if (Objects.equals(cmd, TGS_PopYesNoOptionCodes.EXE_SERVLET())) {
            //NOTHING TO COMPILE
            AppCtrReportMainUtils.line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
            return;
        }
        d.ce("compile", "cmd", "not compile-able client side code!");
        AppCtrReportMainUtils.line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
    }
}
