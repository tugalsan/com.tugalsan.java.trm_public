package com.tugalsan.app.table.control;

import com.tugalsan.api.charset.client.TGS_CharSet;
import com.tugalsan.api.gui.client.key.TGC_KeyTriggerUtils;
import com.tugalsan.api.gui.client.theme.TGC_PanelStyleUtils;
import com.tugalsan.api.gui.client.widget.TGC_ButtonUtils;
import com.tugalsan.api.icon.client.TGS_IconUtils;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.AppCell_STR;
import com.tugalsan.app.table.control.utils.*;
import java.util.stream.IntStream;

public class AppCtrlShowKeys {

    final private static TGC_Log d = TGC_Log.of(AppCtrlShowKeys.class);

    private static String endMessage() {
        return ",<b>TAB:</b> sekme değiştir, <b>W:</b> sekme kapat";
    }
    public static boolean isShifed = false;
    public static boolean isControlled = false;
    public static boolean isControlShifed = false;

    public AppCtrlShowKeys(AppModuleTable tm) {
        this.tm = tm;
    }
    private final AppModuleTable tm;

    public void configActions() {
        TGC_KeyTriggerUtils.quickCtrlShift = () -> {
            if (!isControlShifed) {
                if (!tm.dbCfg.isAny()) {
                    AppCtrlCellHeaderUtils.tempHeaderTitles(tm, "Kolon adını değiştir", false);
                }
                isControlShifed = true;
            }
        };
        TGC_KeyTriggerUtils.quickCtrl = () -> {//TODO SHIFT EK ILISKI
            if (!isControlled) {
                if (!tm.filter.popMain.getPop().isVisible()) {
                    TGC_PanelStyleUtils.remove(tm.page.btnPrev, tm.page.btnNext);
                }
                if (tm.cells.isRowModifyVisible()) {
                    if (tm.dbCfg.isAny()) {
                        d.ce("onClick_showHeaderUpdate", "Hata: Mode açık iken render mode değiştirilemez!");
                        return;
                    }
                    tm.pageHeader.setTempTitle("<b>Sol/Sağ:</b> bölüm değiştir, <b>Yukarı/Aşağı:</b> kayıt değiştir" + endMessage());
                    AppCtrlCellLayoutUtils.isRenderable_clear();
                    IntStream.range(0, tm.cells.headers.size()).parallel().forEach(ci -> {
                        var colIsRenderable = AppCtrlCellLayoutUtils.isRenderable(tm, ci, false)
                                ? TGS_CharSet.cmn().UTF8_HOURGLASS() + "TABLODA GÖZÜKÜR"
                                : TGS_CharSet.cmn().UTF8_CROSS() + "TABLODA GİZLİ";
                        var cell = (AppCell_STR) tm.cells.headers.get(ci);
                        cell.setValueString(colIsRenderable);
                    });
                } else {
                    tm.pageHeader.setTempTitle("<b>SATIRLARDA Sol/Sağ:</b> sayfa değiştir, <b>YukarıOk':</b> yeni kayıt, <b>AşağıOk:</b> filitre" + endMessage());
                    AppCtrlCellHeaderUtils.tempHeaderTitles(tm, "KolonuSırala / HücreDeğiştir", false);
                    TGC_ButtonUtils.setIcon(tm.filter.btn, TGS_IconUtils.CLASS_FILTER(), "Kayıtlılar");
                }
                isControlled = true;
            }
        };
        TGC_KeyTriggerUtils.quickShift = () -> {
            if (!isShifed) {
                AppCtrlCellHeaderUtils.tempHeaderTitles(tm, "BağlıTabloyuAç / HücreÖnzile", true);
                isShifed = true;
            }
        };
        TGC_KeyTriggerUtils.quickNull = () -> {
            if (isControlled) {
                TGC_ButtonUtils.setIcon(tm.filter.btn, TGS_IconUtils.CLASS_FILTER(), "Süz");
                if (!tm.filter.popMain.getPop().isVisible()) {
                    tm.page.recolorLeftRightButtons();
                }
            }
            if (isControlled || isControlShifed) {
                tm.pageHeader.resetTitle();
                AppCtrlCellHeaderUtils.refreshHeaderTitles(tm, false);
            }
            if (isShifed) {
                AppCtrlCellHeaderUtils.refreshHeaderTitles(tm, true);
            }
            isControlShifed = false;
            isControlled = false;
            isShifed = false;
        };
    }

    public void configInit() {
        TGC_KeyTriggerUtils.add2Dom();
    }

}
