package com.tugalsan.app.table.control.utils;

import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.rql.client.TGS_LibRqlReport;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.lib.report.client.sgf.TGS_LibRepSGFRun_Param;
import java.util.*;

public class AppCtrReportBoolUtils {
    
    private AppCtrReportBoolUtils(){
        
    }

    final private static TGC_Log d = TGC_Log.of(AppCtrReportBoolUtils.class);

    public static void compile(TGC_PopFrame pf, TGS_LibRqlTbl t, TGS_LibRqlReport r, List<String> fileTypes, Long id,
            List<TGS_LibRepSGFRun_Param> clientCodeParams, int clientCodeIdx,
            String code, List<String> codeTags, String cmd) {
//        var IDX0_CMD = 0;
        var IDX1_PARAM_NAME = 1;
        var IDX2_DEFAULT = 2;
        var IDX3_TRUE = 3;
        var IDX4_FALSE = 4;
        d.ci("compile", "cmd", "detected as boolean");
        if (codeTags.size() != 5) {
            d.ce("compile", "ERROR: ClienCode", "tags.size() != 4", code);
            return;
        }
        var paramName = codeTags.get(IDX1_PARAM_NAME);
        var doNottoggleValueStr = codeTags.get(IDX2_DEFAULT);
        var doNottoggleValue = TGS_CastUtils.toBoolean(doNottoggleValueStr).orElse(null);
        if (doNottoggleValue == null) {
            d.ce("compile", "ERROR: ClienCode", "defStr is not boolean", doNottoggleValueStr);
            return;
        }
        d.ci("compile", "defStr", doNottoggleValueStr);
        var textTrue = codeTags.get(IDX3_TRUE);
        var textFalse = codeTags.get(IDX4_FALSE);
        var pop = new TGC_PopLblYesNo(
                new TGC_Dimension(TGC_PanelLayoutUtils.MAX_GRID_WIDTH(), null, true),
                "Bir seçenek seçin:", "Uygula", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    d.ci("compile", "cmd", "processing boolean input...");
                    clientCodeParams.add(TGS_LibRepSGFRun_Param.of(paramName, String.valueOf(doNottoggleValue ? true : false)));
                    AppCtrReportMainUtils.line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
                },
                p -> {
                    p.getPop().setVisible(false);
                    d.ci("compile", "cmd", "processing boolean input...");
                    clientCodeParams.add(TGS_LibRepSGFRun_Param.of(paramName, String.valueOf(doNottoggleValue ? false : true)));
                    AppCtrReportMainUtils.line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
                },
                null
        );
        pop.getPop().setVisible_focus = pop.btnExe;
        d.ci("compile", "cmd", "requesting boolean input...");
        TGC_ButtonUtils.setIcon(pop.btnExe, TGS_IconUtils.CLASS_CHECKMARK(), textTrue);
        TGC_ButtonUtils.setIcon(pop.btnEsc, TGS_IconUtils.CLASS_CROSS(), textFalse);
        pop.getPop().setVisible(true);
    }
}
