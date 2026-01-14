package com.tugalsan.app.table.control;

import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.gui.client.click.TGC_ClickUtils;
import com.tugalsan.api.gui.client.dim.TGC_Dimension;
import com.tugalsan.api.gui.client.dom.TGC_DOMUtils;
import com.tugalsan.api.gui.client.focus.TGC_FocusUtils;
import com.tugalsan.api.gui.client.focus.TGS_FocusSides4;
import com.tugalsan.api.gui.client.key.TGC_KeyUtils;
import com.tugalsan.api.gui.client.panel.TGC_PanelLayoutUtils;
import com.tugalsan.api.gui.client.pop.TGC_PopLblYesNoListBox;
import com.tugalsan.api.gui.client.widget.TGC_ButtonUtils;
import com.tugalsan.api.icon.client.TGS_IconUtils;
import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.log.client.TGS_Log;
import com.tugalsan.api.servlet.gwt.webapp.client.TGC_SGWTCalller;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.lib.rql.cfg.client.TGS_LibRqlCfgUtils;
import com.tugalsan.app.table.sg.AppSGFConfigValueSet;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.utils.AppCtrlCellLayoutUtils;
import com.tugalsan.app.table.pop.*;
import com.tugalsan.lib.boot.client.*;
import java.util.Collections;
import java.util.StringJoiner;

public class AppCtrlFilter {

    final private static TGC_Log d = TGC_Log.of(AppCtrlFilter.class);
    public PushButton btn;
    public AppPopTableFilter popMain;
    public TGC_PopLblYesNoListBox popMem;
    public AppCtrlFilterJoin join;

    public AppCtrlFilter(AppModuleTable tm) {
        this.tm = tm;
        join = new AppCtrlFilterJoin(tm);
    }
    private final AppModuleTable tm;

    public void configInit(String filterIdStart, String filterIdEnd) {
        TGC_DOMUtils.setListBoxItemEnableAt(popMem.listBox, 2, false);
        TGC_DOMUtils.setListBoxItemEnableAt(popMem.listBox, 3, false);
        popMem.listBox.setSelectedIndex(0);
        if (filterIdStart == null && filterIdEnd == null) {
            d.ci("configInit", "DEBUG: PreFilter skipped -> filterIdStart == null && filterIdEnd == null");
            return;
        }
        popMain.profiles.get(0).cbActive = true;
        popMain.profiles.get(0).btnMin = filterIdStart == null ? "0" : filterIdStart;
        popMain.profiles.get(0).btnMax = filterIdEnd == null ? String.valueOf(Long.MAX_VALUE) : filterIdEnd;
        d.ci("configInit", "DEBUG: PreFiltered -> filterIdStart/filterIdEnd", filterIdStart, filterIdEnd);
    }

    public void configLayout(HorizontalPanel p) {
        p.add(btn);
        btn.addStyleName(AppModuleTable.class.getSimpleName() + "_btn");
    }

    public void configActions() {
        TGC_ClickUtils.add(btn, () -> showSth_Acc2CtrlKey());
        TGC_KeyUtils.add(btn, () -> showSth_Acc2CtrlKey(), null);
    }

    public void configFocus() {
        TGC_FocusUtils.addKeyUp(btn, nativeKeyCode -> {
            var filterFocusSides = new TGS_FocusSides4(null, tm.page.btnFirst, TGC_LibBootGUIBody.windowOperatorButton, tm.cells.getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btn, filterFocusSides, nativeKeyCode);
        });
        TGC_FocusUtils.addKeyUp(TGC_LibBootGUIBody.windowOperatorButton, new TGS_FocusSides4(null, null, null, btn));
    }

    public void createPops() {
        join.createPops();
        popMain = new AppPopTableFilter(tm);
        popMem = new TGC_PopLblYesNoListBox(
                new TGC_Dimension(TGC_PanelLayoutUtils.MAX_GRID_WIDTH(), null, true),
                TGS_ListUtils.of(
                        " Başlangıç Ayarları",//selectedIndex=0
                        " Geçerli Filtreyi Aşağıya Ekle",//selectedIndex=1
                        " Kaldırmak için üzerinde DEL'e basınız",//selectedIndex=2
                        " ------------------"//selectedIndex=3
                ),
                "Kayıtlı Süzgeçler (Silmek için DEL'e bas):", "Çalıştır", "İptal",
                p -> {
                    d.ci("createPops", "popLst", "exe", "init");
                    p.getPop().setVisible(false);
                    var si = p.listBox.getSelectedIndex();
                    d.ci("createPops", "popLst", "exe", "si", si);
                    switch (si) {
                        case -1 ->
                            d.ce("createPops", "popLst", "HATA: Listeden bir işlem seçilmedi hatası!");
                        case 0 ->
                            popMain.onResetFromPopLst.run();
                        case 1 -> {//CUSTOM ADD
                            tm.input.setPopBoxOkText("Kaydet");
                            tm.input.showBox("Yeni Süzgeç " + TGS_Time.of().toString_YYYY_MM_DD_HH_MM_SS(),
                                    name -> {
                                        name = name.trim();
                                        if (TGS_StringUtils.cmn().isNullOrEmpty(name)) {
                                            d.ce("createPops", "popLst", "add", "HATA: Süzgeç adı boş olamaz");
                                            return;
                                        }
                                        if (!popMain.mem_saveCurrentAsNew(name)) {
                                            d.ce("mem_saveCurrentAsNew", "HATA: Filtre boş olduğu için kayıt edilmedi", name);
                                            return;
                                        }
                                        refreshPopLstItems();
                                        mem_saveToServer("customAdd");
                                    },
                                    null,
                                    "Yeni Süzgeç Adı:"
                            );
                        }
                        case 2 ->
                            d.ce("createPops", "popLst", "HATA: Seçilebilir olmayan kalem hatası!", si);
                        case 3 ->
                            d.ce("createPops", "popLst", "HATA: Seçilebilir olmayan kalem hatası!", si);
                        default -> {
                            popMain.mem_loadAsCurrentFrom(si - 4);
                            tm.filter.popMain.getPop().setVisible(false);
                            AppCtrlCellLayoutUtils.isRenderable_clear();
                            AppCtrlCellLayoutUtils.cells_visibility_refresh(tm, null);
                            AppCtrlCellLayoutUtils.cells_layout_refresh(d.className() + "createPops.popMem.default", tm);
                            if (!popMain.getPop().isVisible()) {
                                tm.page.onPageFirst();
                            }
                        }

                    }
                    d.ci("createPops", "popLst", "exe", "swich_end");
                },
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btn);
                },
                null, TGS_FuncMTU.empty
        );
        TGC_KeyUtils.addDel(() -> {//CUSTOM DEL
            if (popMain.profilesListCustom.isEmpty()) {
                d.ce("createPops", "TGC_KeyUtils.addDel", "popMain.profilesListCustom.isEmpty", "HATA: Özel-Filtre-Listesi boş hatası!");
                return;
            }
            var si = popMem.listBox.getSelectedIndex() - 4;
            if (si >= popMain.profilesListCustom.size()) {
                d.ce("createPops", "TGC_KeyUtils.addDel", "popMain.profilesListCustom.isEmpty", "HATA: Özel-Filtre-Listesi bu kadar büyük değil hatası!");
                return;
            }
            popMain.profilesListCustom.remove(si);
            refreshPopLstItems();
            mem_saveToServer("customDel");
        }, popMem.listBox);
        initPopLstItems();
    }

    public void mem_saveToServer(String reason) {
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "mem_saveToServer", "INFO: mem_saveToServer", "reason", reason);
        }
        var curTableName = tm.curTable.nameSql;
        var ref = TGS_LibRqlCfgUtils.PARAM_FILTER_CONFIG();
        var inputText = new StringJoiner("\n");
        tm.filter.popMain.profilesListCustom.forEach(item -> {
            var lst = item.toLst();
            if (lst.isEmpty()) {
                d.ce("mem_saveToServer", "UYARI: Boş bir filitre kaydedilmesi atlandı", item.name);
                return;
            }
            lst.forEach(line -> inputText.add(line));
        });
        TGC_SGWTCalller.async(new AppSGFConfigValueSet(curTableName, ref, inputText.toString()), r -> {
            if (r.getOutput_id() == null) {
                d.ce("executeHeaderUpdate", "HATA: Filitreler server'a kaydedilemedi!");
                return;
            }
            d.cr("executeHeaderUpdate", "Filitreler server'a kaydedildi");
        });
    }

    public void initPopLstItems() {
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "initPopLstItems", "loading...");
        }
        AppPopTableFilterMemProfiles.ofLst(tm, tm.curTable.filterConfig).forEach(item -> {
            tm.filter.popMain.profilesListCustom.add(item);
        });
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "initPopLstItems", "fin");
        }
        refreshPopLstItems();
    }

    public void refreshPopLstItems() {
        while (popMem.listBoxContent.size() > 4) {
            popMem.listBox.removeItem(4);
            popMem.listBoxContent.remove(4);
        }
        Collections.sort(popMain.profilesListCustom, (item1, item2) -> {
            return item1.name.compareTo(item2.name);
        });
        popMain.profilesListCustom.forEach(item -> {
            popMem.listBoxContent.add(item.name);
            popMem.listBox.addItem(item.name);
        });
    }

    public void createWidgets() {
        btn = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_FILTER(), "Süz");
    }

    public void showPopMemLst() {
        tm.filter.popMem.getPop().setVisible(true);
    }

    public void showPopMain() {
        popMain.onSetVisibleCol.run(tm.cells.getActiveColIdx());
    }

    public void showSth_Acc2CtrlKey() {
        if (AppCtrlShowKeys.isControlled) {
            showPopMemLst();
        } else {
            showPopMain();
        }
    }

    public String getWhereStmt() {
        var sj = new StringJoiner(" AND ");
        popMain.profiles.stream().forEachOrdered(p -> {
            var where = p.getWhereStmt();
            if (where != null) {
                sj.add(where);
            }
        });
        return sj.toString();
    }

    public boolean isActiveWhere() {
        return !TGS_StringUtils.cmn().isNullOrEmpty(getWhereStmt());
    }

    public boolean isActiveOrder() {
        return !TGS_StringUtils.cmn().isNullOrEmpty(getWhereStmt());
    }

    public boolean isActive() {
        return isActiveOrder() || isActiveWhere();
    }

    public String getOrderByStmt() {
        for (var profile : popMain.profiles) {
            if (profile.rbSortBy) {
                return TGS_StringUtils.cmn().concat(
                        profile.pop.tm.curTable.nameSql,
                        ".",
                        profile.ct.getColumnName(),
                        (profile.cbAscending ? " ASC" : " DESC")
                );
            }
        }
        return null;
    }

}
