package com.tugalsan.app.table.control;

import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.api.gui.client.click.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.key.*;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.pop.options.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.lib.boot.client.*;
import com.tugalsan.lib.file.tmcr.client.*;
import java.util.*;

public class AppCtrlReport {

    final private static TGC_Log d = TGC_Log.of(false, AppCtrlReport.class);

    public static String PREFIX_LINES() {
        return "- ";
    }

    public AppCtrlReport(AppModuleTable tm) {
        this.tm = tm;
    }
    private final AppModuleTable tm;

    public void createWidgets() {
        btn = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_STATS_DOTS(), "Raporlar");
    }
    public PushButton btn;

    public void createPops() {
        var dim = new TGC_Dimension(TGC_PanelLayoutUtils.MAX_GRID_WIDTH(), null, true);
        popFrame = new TGC_PopFrame(TGC_Dimension.FULLSCREEN, null, "Kapat", "Yeni Sekme'de Aç", p -> popFrame.getPop().setVisible(false), null);
        popOperations = new TGC_PopLblYesNoCheckListBox(
                dim, null,
                TGS_ListUtils.of(
                        new TGS_Tuple2(TGS_IconUtils.CLASS_FILE_ZIP(), "Zip dosyası"),
                        new TGS_Tuple2(TGS_IconUtils.CLASS_TERMINAL(), "Macro dosyası"),
                        new TGS_Tuple2(TGS_IconUtils.CLASS_EARTH(), "Web dosyası"),
                        new TGS_Tuple2(TGS_IconUtils.CLASS_FILE_PDF(), "Pdf dosyası"),
                        new TGS_Tuple2(TGS_IconUtils.CLASS_FILE_EXCEL(), "Excel dosyası"),
                        new TGS_Tuple2(TGS_IconUtils.CLASS_FILE_WORD(), "Word dosyası")
                ),
                "Rapor İşlemleri:", "Dosya Tipleri:", "Çalıştır", "İptal",
                p -> {
                    d.ci("createPops", "popOperations", "onExe", "Debug#0");
                    p.getPop().setVisible(false);
                    if (tm.cells.isRowModifyVisible()) {
                        tm.cells.popRowModify.onClose.run(false);
                    }
                    d.ci("createPops", "popOperations", "onExe", "Debug#1");
                    var si = p.listBox.getSelectedIndex();
                    if (si == -1) {
                        d.ce("onMainMenuAct", "HATA: Listeden bir işlem seçilmedi hatası!");
                        return;
                    }
                    d.ci("createPops", "popOperations", "onExe", "Debug#2");
                    List<String> fileTypes = TGS_ListUtils.of();
                    if (p.checkBoxes.get(0).getValue()) {
                        fileTypes.add(TGS_LibFileTmcrTypes.FILE_TYPE_ZIP());
                    }
                    d.ci("createPops", "popOperations", "onExe", "Debug#2.0");
                    if (p.checkBoxes.get(1).getValue()) {
                        fileTypes.add(TGS_LibFileTmcrTypes.FILE_TYPE_TMCR());
                    }
                    d.ci("createPops", "popOperations", "onExe", "Debug#2.1");
                    if (p.checkBoxes.get(2).getValue()) {
                        fileTypes.add(TGS_LibFileTmcrTypes.FILE_TYPE_HTM());
                    }
                    d.ci("createPops", "popOperations", "onExe", "Debug#2.2");
                    if (p.checkBoxes.get(3).getValue()) {
                        fileTypes.add(TGS_LibFileTmcrTypes.FILE_TYPE_PDF());
                    }
                    d.ci("createPops", "popOperations", "onExe", "Debug#2.3");
                    if (p.checkBoxes.get(4).getValue()) {
                        fileTypes.add(TGS_LibFileTmcrTypes.FILE_TYPE_XLSX());
                    }
                    d.ci("createPops", "popOperations", "onExe", "Debug#2.4");
                    if (p.checkBoxes.get(5).getValue()) {
                        fileTypes.add(TGS_LibFileTmcrTypes.FILE_TYPE_DOCX());
                    }
                    d.ci("createPops", "popOperations", "onExe", "Debug#3");
                    if (fileTypes.isEmpty()) {
                        d.ce("createPops", "popOperations", "fileTypes", "HATA: En az bir dosya tipi seçilmeli!");
                        return;
                    }
                    d.ci("createPops", "popOperations", "onExe", "Debug#4");
                    AppCtrReportMainUtils.init(tm, si, fileTypes);
                    d.ci("createPops", "popOperations", "onExe", "Debug#5");
                },
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btn);
                },
                null, () -> TGC_FocusUtils.setFocusAfterGUIUpdate(popOperations.btnEsc)
        );
    }
    public TGC_PopLblYesNoCheckListBox popOperations;
    public TGC_PopFrame popFrame;

    public void configInit() {
        popOperations.checkBoxes.get(1).setEnabled(App.loginCard.userAdmin);
        popOperations.checkBoxes.get(2).setValue(true);
        popOperations.checkBoxes.get(3).setValue(true);
        TGC_ListBoxUtils.selectNone(popOperations.listBox);
        if (tm.curTable.reports == null) {
            return;
        }
        Arrays.stream(tm.curTable.reports).forEach(r -> {
            var listName = PREFIX_LINES() + r.param;
            d.ci("configInit", "report_id/nm/vl", r.id, r.param, r.value);
            popOperations.listBox.addItem(listName);
        });
    }

    public void configActions() {
        TGC_ClickUtils.add(btn, () -> onReport());
        TGC_KeyUtils.add(btn, () -> onReport(), () -> {
            if (tm.cells.isRowModifyVisible()) {
                tm.cells.popRowModify.onClose.run(true);
            }
        });
        var onListBox = onListBoxSelect();
        TGC_ClickUtils.add(popOperations.listBox, onListBox, onListBox);
    }

    public void configFocus() {
        TGC_FocusUtils.addKeyDown(btn, nativeKeyCode -> {
            TGS_FocusSides4 reportFocusSides;
            if (tm.cells.isRowModifyVisible()) {
                reportFocusSides = new TGS_FocusSides4(tm.operations.btn, tm.cells.popRowModify.btnRowPrev, null, tm.cells.getPreferredActiveCell());
            } else {
                reportFocusSides = new TGS_FocusSides4(tm.operations.btn, null, TGC_LibBootGUIBody.windowOperatorButton, tm.cells.getPreferredActiveCell());
            }
            TGC_FocusUtils.focusSide(btn, reportFocusSides, nativeKeyCode);
        });
    }

    public void configLayout(HorizontalPanel p) {
        p.add(btn);
        btn.addStyleName(AppModuleTable.class.getSimpleName() + "_btn");
        if (tm.dbCfg.isAny()) {
            btn.setVisible(false);
        }
    }

    public TGS_FuncMTU onListBoxSelect() {
        return () -> {
            var si = popOperations.listBox.getSelectedIndex();
            if (si == -1) {
                return;
            }
            var htmlOnly = TGS_CharSetCast.current().containsIgnoreCase(
                    tm.curTable.reports[si].value,
                    TGS_PopYesNoOptionCodes.EXE_REDIRECT().concat(TGS_PopYesNoOptionCodes.DELIM())
            ) || TGS_CharSetCast.current().containsIgnoreCase(
                    tm.curTable.reports[si].value,
                    TGS_PopYesNoOptionCodes.EXE_NEWTAB().concat(TGS_PopYesNoOptionCodes.DELIM())
            );
            popOperations.checkBoxes.forEach(cb -> {
                cb.setVisible(!htmlOnly);
            });
            popOperations.lblCheckBox.setVisible(!htmlOnly);
        };
    }

    public void onReport() {
        if (popOperations.listBox.getItemCount() == 0) {
            d.ce("onReport", "Hata: Konu tabloya rapor tanımlanmamış!");
            return;
        }
        popOperations.getPop().setVisible(true);
    }
}
