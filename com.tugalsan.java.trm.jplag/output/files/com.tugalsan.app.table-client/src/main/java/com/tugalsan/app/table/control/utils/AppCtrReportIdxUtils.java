package com.tugalsan.app.table.control.utils;

import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.rql.client.TGS_LibRqlReport;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.lib.report.client.sgf.TGS_LibRepSGFRun_Param;
import java.util.*;
import java.util.stream.*;

public class AppCtrReportIdxUtils {
    
    private AppCtrReportIdxUtils(){
        
    }

    final private static TGC_Log d = TGC_Log.of(AppCtrReportIdxUtils.class);

    public static void compile(TGC_PopFrame pf, TGS_LibRqlTbl t, TGS_LibRqlReport r, List<String> fileTypes, Long id,
            List<TGS_LibRepSGFRun_Param> clientCodeParams, int clientCodeIdx,
            String code, List<String> codeTags, String cmd) {
        //        var IDX0_CMD = 0;
        var IDX1_PARAM_NAME = 1;
        var IDX2_DEFAULT = 2;
        var IDX3_CHOICES_OFFSET = 3;
        d.ci("compile", "cmd", "detected as list");
        if (codeTags.size() < 3) {
            d.ce("compile", "ERROR: ClienCode", "tags.size() < 3", code);
            return;
        }
        var paramName = codeTags.get(IDX1_PARAM_NAME);
        var defStr = codeTags.get(IDX2_DEFAULT);
        var def = TGS_CastUtils.toInteger(defStr).orElse(null);
        if (def == null){
            d.ce("compile", "ERROR: ClienCode", "defStr is not integer", defStr);
            return;
        }
        List<String> choices = TGS_ListUtils.of();
        IntStream.range(IDX3_CHOICES_OFFSET, codeTags.size()).forEachOrdered(i -> {
            choices.add(codeTags.get(i));
        });
        var pop = new TGC_PopLblYesNoListBox(
                new TGC_Dimension(TGC_PanelLayoutUtils.MAX_GRID_WIDTH(), null, true),
                null,
                "Bir seçenek seçin:", "Uygula", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    d.ci("compile", "cmd", "processing list input...");
                    var val = p.listBox.getSelectedIndex();
                    if (val == -1) {
                        d.ce("compile", "HATA: Listeden seçim yapılmadığı için işlem sonlandırıldı.");
                        return;
                    }
                    clientCodeParams.add(TGS_LibRepSGFRun_Param.of(paramName, String.valueOf(val)));
                    AppCtrReportMainUtils.line(pf, t, r, fileTypes, id, clientCodeParams, clientCodeIdx + 1);
                },
                p -> {
                    p.getPop().setVisible(false);
                    d.cr("compile", "BİLGİ: İşlem kullanıcı tarafından sonlandırıldı.");
                },
                null, null
        );
        d.ci("compile", "cmd", "requesting list input...");
        pop.listBox.clear();
        choices.forEach(c -> pop.listBox.addItem(c));
        pop.listBox.setSelectedIndex(def);
        TGC_ButtonUtils.setIcon(pop.btnExe, TGS_IconUtils.CLASS_CHECKMARK(), "Seç");
        TGC_ButtonUtils.setIcon(pop.btnEsc, TGS_IconUtils.CLASS_CROSS(), "İptal");
        pop.getPop().setVisible(true);
    }
}
