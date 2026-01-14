package com.tugalsan.app.table.control;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.tugalsan.api.cast.client.TGS_CastUtils;
import com.tugalsan.api.function.client.maythrowexceptions.checked.*;
import com.tugalsan.api.gui.client.click.TGC_ClickUtils;
import com.tugalsan.api.gui.client.dim.TGC_Dimension;
import com.tugalsan.api.gui.client.focus.TGC_FocusUtils;
import com.tugalsan.api.gui.client.focus.TGS_FocusSides4;
import com.tugalsan.api.gui.client.key.TGC_KeyUtils;
import com.tugalsan.api.gui.client.pop.TGC_PopLblYesNoListBox;
import com.tugalsan.api.icon.client.TGS_IconUtils;
import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.app.table.AppModuleTable;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.widget.TGC_ButtonUtils;
import com.tugalsan.lib.boot.client.TGC_LibBootGUIBody;
import com.tugalsan.app.table.sg.cell.*;

public class AppCtrlSettings {

    final private static TGC_Log d = TGC_Log.of(AppCtrlSettings.class);

    public AppCtrlSettings(AppModuleTable tm) {
        this.tm = tm;
    }
    private final AppModuleTable tm;
    public PushButton btn;
    public TGC_PopLblYesNoListBox pop;
    public int searchMaxSecs = AppSGFCellSearch.MAX_SECS();
    public int searchMaxItem = AppSGFCellSearch.MAX_SIZE();

    public void createWidgets() {
        btn = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_WRENCH(), "Ayarlar");
    }

    public void configLayout(HorizontalPanel p) {
        p.add(btn);
        btn.addStyleName(AppModuleTable.class.getSimpleName() + "_btn");
    }

    public void configActions() {
        TGC_ClickUtils.add(btn, () -> onSettings());
        TGC_KeyUtils.add(btn, () -> onSettings(), () -> {
            if (tm.cells.isRowModifyVisible()) {
                tm.cells.popRowModify.onClose.run(true);
            }
        });
    }

    public void onSettings() {
        pop.getPop().setVisible(true);
    }

    public void configInit() {

    }

    public void configFocus() {
        TGC_FocusUtils.addKeyDown(btn, nativeKeyCode -> {
            var opFocusSides = new TGS_FocusSides4(tm.add.btn, tm.operations.btn, TGC_LibBootGUIBody.windowOperatorButton, tm.cells.getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btn, opFocusSides, nativeKeyCode);
        });
    }

    public void createPops() {
        pop = new TGC_PopLblYesNoListBox(
                new TGC_Dimension(TGC_PanelLayoutUtils.MAX_GRID_WIDTH(), null, true),
                TGS_ListUtils.of(
                        " Satır değiştiğinde, en son açık bölümü hatırla/hatırlama",//selectedIndex=0
                        " Arama max saniye limitini değiştir",//selectedIndex=1
                        " Arama max kalem limitini değiştir"//selectedIndex=2
                ),
                "Tablo İşlemleri:", "Çalıştır", "İptal",
                p -> {
                    d.ci("createPops", "pop", "exe", "init");
                    p.getPop().setVisible(false);
                    var si = p.listBox.getSelectedIndex();
                    d.ci("createPops", "pop", "exe", "si", si);
                    d.ci("createPops", "pop", "exe", "tm==null", tm == null);
                    d.ci("createPops", "pop", "exe", "tm.cells==null", tm.cells == null);
                    d.ci("createPops", "pop", "exe", "tm.cells.popRowModify==null", tm.cells.popRowModify == null);
                    d.ci("createPops", "pop", "exe", "tm.cells.popRowModify.getPop()==null", tm.cells.popRowModify.getPop() == null);
                    d.ci("createPops", "pop", "exe", "tm.cells.popRowModify.getPop().isVisible()", tm.cells.popRowModify.getPop().isVisible());
                    d.ci("createPops", "pop", "exe", "tm.cells.popRowModify.onClose==null", tm.cells.popRowModify.onClose == null);
                    if (tm.cells.isRowModifyVisible()) {
                        tm.cells.popRowModify.onClose.run(true);
                    }
                    d.ci("createPops", "pop", "exe", "swich_init");
                    switch (si) {
                        case -1:
                            d.ce("Listeden bir işlem seçilmedi hatası!");
                            break;
                        case 0:
                            tm.cells.popRowModify.rememberLastBolum = !tm.cells.popRowModify.rememberLastBolum;
                            d.cr("createPops", "AYAR DEĞİŞİKLİĞİ: Satır değiştiğinde en son açık olan bölüm " + (tm.cells.popRowModify.rememberLastBolum ? "HATIRLANACAK" : "HATIRLANMAYACAK") + "!");
                            break;
                        case 1://Arama max saniye limitini değiştir
                            //                            var ac4 = tm.cells.getActiveCell();
//                            if (ac4 == null) {
//                                d.ce("saniyeLimiti", "HATA: Önce bir hücre seçiniz!");
//                                return;
//                            }
                            tm.input.showBox(
                                    String.valueOf(searchMaxSecs),
                                    inputText -> actionAfter_searchMaxSecs(inputText),
                                    null,//ac4,
                                    "Arama Maksimum Saniye Limiti",
                                    TGS_IconUtils.CLASS_FIRST(), "Min", String.valueOf(1),
                                    TGS_IconUtils.CLASS_LAST(), "Mak", String.valueOf(AppSGFCellSearch.THRESHOLD_MAX_SECS)
                            );
                            break;
                        case 2://Arama max kalem limitini değiştir
//                            var ac5 = tm.cells.getActiveCell();
//                            if (ac5 == null) {
//                                d.ce("kalemLimiti", "HATA: Önce bir hücre seçiniz!");
//                                return;
//                            }
                            tm.input.showBox(
                                    String.valueOf(searchMaxItem),
                                    inputText -> actionAfter_searchMaxItem(inputText),
                                    null,//ac4,
                                    "Arama Maksimum Kalem Limiti",
                                    TGS_IconUtils.CLASS_FIRST(), "Min", String.valueOf(1),
                                    TGS_IconUtils.CLASS_LAST(), "Mak", String.valueOf(Integer.MAX_VALUE)
                            );
                            break;
                        default:
                            d.ce("init", "Listeden seçilen işlem bulunamadı hatası! (" + si + ")");
                    }
                    d.ci("createPops", "pop", "exe", "swich_end");
                },
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btn);
                },
                null, () -> TGC_FocusUtils.setFocusAfterGUIUpdate(pop.btnEsc)
        );
    }

    private void actionAfter_searchMaxSecs(String inputText) {
        TGS_FuncMTCUtils.run(() -> {
            var val = TGS_CastUtils.toInteger(inputText).orElse(null);
            if (val == null) {
                d.ce("actionAfter_searchMaxSecs", "HATA: girdi bir tam sayı olmalı!", inputText);
                return;
            }
            if (val < 1) {
                d.ce("actionAfter_searchMaxSecs", "HATA: girdi 0 dan büyük olmalı!", val);
                return;
            }
            if (val > AppSGFCellSearch.THRESHOLD_MAX_SECS) {
                d.ce("actionAfter_searchMaxSecs", "HATA: girdi uygulanabilir max saniye dan büyük!", val, AppSGFCellSearch.THRESHOLD_MAX_SECS);
                return;
            }
            searchMaxSecs = val;
            tm.cells.focusActiveCell();
            d.cr("actionAfter_searchMaxSecs", "BİLGİ: Arama maksimum saniye limiti ayarlandı", val);
        }, e -> d.ct("actionAfter_searchMaxSecs", e));
    }

    private void actionAfter_searchMaxItem(String inputText) {
        TGS_FuncMTCUtils.run(() -> {
            var val = TGS_CastUtils.toInteger(inputText).orElse(null);
            if (val == null) {
                d.ce("actionAfter_searchMaxItem", "HATA: girdi bir tam sayı olmalı!");
                return;
            }
            if (val < 1) {
                d.ce("actionAfter_searchMaxItem", "HATA: girdi 0 dan büyük olmalı!");
                return;
            }
            searchMaxItem = val;
            tm.cells.focusActiveCell();
            d.cr("actionAfter_searchMaxItem", "BİLGİ: Arama maksimum kalem limiti ayarlandı", val);
        }, e -> d.ct("actionAfter_searchMaxItem", e));
    }
}
