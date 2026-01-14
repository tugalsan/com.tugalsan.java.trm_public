package com.tugalsan.app.table.control;

import com.tugalsan.api.cast.client.TGS_CastUtils;
import com.tugalsan.api.gui.client.dim.TGC_Dimension;
import com.tugalsan.api.gui.client.dom.TGC_DOMUtils;
import com.tugalsan.api.gui.client.pop.TGC_PopLblYesNoListBox;
import com.tugalsan.api.gui.client.pop.TGC_PopLblYesNoTextBoxListBox;
import com.tugalsan.api.gui.client.theme.TGC_PanelRed;
import com.tugalsan.api.gui.client.theme.TGC_PanelStyleUtils;
import com.tugalsan.api.gui.client.widget.TGC_ButtonUtils;
import com.tugalsan.api.gui.client.widget.table.TGC_TableHeaderStyled;
import com.tugalsan.api.icon.client.TGS_IconUtils;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.servlet.gwt.webapp.client.TGC_SGWTCalller;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.thread.client.TGC_ThreadUtils;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.app.table.App;
import com.tugalsan.app.table.AppModuleTable;
import com.tugalsan.app.table.pop.AppPopEditPopLINKBufferStar;
import com.tugalsan.app.table.sg.cell.AppSGFCellSearch;
import com.tugalsan.lib.rql.client.TGC_LibRqlTblBallWithSubBalls;
import java.util.*;

public class AppCtrlFilterJoin {

    final private static TGC_Log d = TGC_Log.of(AppCtrlFilterJoin.class);

    public AppCtrlFilterJoin(AppModuleTable tm) {
        this.tm = tm;
    }
    private final AppModuleTable tm;
    private long lastCTM = 0;

    public void cmd_annoint() {
        d.cr("createPops", "init clear...");
        d.cr("createPops", "fetch combination list...");
        if (curTableJoinCombinationsAll.isEmpty()) {
            d.cr("createPops", "pop", "join", "ball...");
            var ball = new TGC_LibRqlTblBallWithSubBalls(App.tables, App.curTableName);
            d.cr("createPops", "pop", "join", "curTableJoinCombinationsAll...");
            curTableJoinCombinationsAll.addAll(
                    ball.combinations()
            );
            d.cr("createPops", "pop", "join", "preapare  tm.input.popJoinTargetTable.listBox...");
            curTableJoinCombinationsAll.stream().map(str -> {
                var lst = TGS_StringUtils.gwt().toList_spc(str);
                if (lst.size() < 2) {
                    return "";
                }
                return lst.get(lst.size() - 1);
            }).filter(str -> TGS_StringUtils.cmn().isPresent(str)).distinct().sorted().forEach(tn -> {
                var tarTable = App.tables.stream().filter(t -> t.nameSql.equals(tn)).findAny().orElse(null);
                if (tarTable == null) {
                    d.ce("createPops", "pop", "join", "cannot find tablename, hence skipped", tn);
                    return;
                }
                curTableJoinTargetTables.add(tn);
                popJoinTargetTable.listBox.addItem(tarTable.nameReadable);
            });
        }
        d.cr("createPops", "ask popJoinTargetTable...");
        popJoinTargetTable.getPop().setVisible(true);
    }

    public void cmd_release() {
        d.cr("createPops", "popJoinTargetTable", "filitrede alt tablo ilişkisi iptal edildi");
        clearJoin();
    }

    public void createPops() {
        popJoinTargetTable = new TGC_PopLblYesNoListBox(
                TGC_Dimension.FULLSCREEN, null,
                "<b>Hedef Tablo:</b>", "Değiştir", "İptal",
                p -> {
                    if (p.listBox.getSelectedIndex() == -1) {
                        d.ce("createPops", "popJoinTargetTable", "HATA: Hedef tablo seçilmedi hatası");
                        return;
                    }
                    var selectedTargetTable = curTableJoinTargetTables.get(popJoinTargetTable.listBox.getSelectedIndex());
                    d.cr("createPops", "popJoinTargetTable", "prepare popJoinTargetCombination...", selectedTargetTable);
                    popJoinTargetCombination.listBox.clear();
                    curTableTargetTableCombinations(selectedTargetTable).forEach(combination -> {
                        var lstConfig = TGS_StringUtils.gwt().toList_spc(combination);
                        var sb = new StringBuilder();
//                        for (var i = 0; i < lstConfig.size() - 2; i += 2) {//remove last
//                            if (i != 0) {
//                                sb.append(" > ");
//                            }
//                            var tn = lstConfig.get(i + 1);
//                            var tnv = App.tbl_mayThrow(tn).nameReadable;
//                            sb.append(tnv);
//                        }
                        for (var i = 0; i < lstConfig.size(); i += 2) {
                            var sourceTable = i == 0 ? tm.curTable : App.tbl_mayThrow(lstConfig.get(i - 1));
                            var sourceColumn = sourceTable.columns.get(TGS_CastUtils.toInteger(lstConfig.get(i)).orElseThrow());
                            if (i == 0) {
                                sb.append(sourceTable.nameReadable).append(" > ").append(sourceColumn.getColumnNameVisible());
                            } else {
                                sb.append(" > ").append(sourceColumn.getColumnNameVisible());
                            }
                        }
                        popJoinTargetCombination.listBox.addItem(sb.toString());
                    });
                    d.cr("createPops", "popJoinTargetTable", "ask popJoinTargetCombination...");
                    popJoinTargetValue.listBox.clear();
                    TGC_PanelStyleUtils.remove(popJoinTargetValue.listBox, popJoinTargetValue.tb);
                    popJoinTargetValue.btnSearch.setEnabled(true);
                    p.getPop().setVisible(false);
                    popJoinTargetValue.getPop().setVisible(true);
                    //popJoinTargetCombination.getPop().setVisible(true);
                },
                p -> {
                    p.getPop().setVisible(false);
                    d.cr("createPops", "popJoinTargetTable", "filitrede alt tablo ilişkisi iptal edildi");
                    clearJoin();
                },
                null, null
        );
        popJoinTargetValue = new TGC_PopLblYesNoTextBoxListBox(
                TGC_Dimension.FULLSCREEN, null,
                "Değer:",
                "",
                "Arama sonuçları:",
                "Uygula",
                "İptal",
                "Ara",
                p -> {
                    if (p.listBox.getSelectedIndex() == -1) {
                        d.ce("createPops", "popJoinTargetValue", "HATA: Listeden bir veri seçilmedi hatası");
                        return;
                    }
                    p.getPop().setVisible(false);
                    popJoinTargetCombination.getPop().setVisible(true);
                },
                p -> {
                    p.getPop().setVisible(false);
                    d.cr("createPops", "popJoinTargetValue", "filitrede alt tablo ilişkisi iptal edildi");
                    clearJoin();
                },
                p -> {
                    var searchTag = p.tb.getText().trim();
                    if (searchTag.isEmpty()) {
                        p.tb.setText("*");
                        searchTag = "*";
                    }
                    d.cr("createPops", "popJoinTargetValue", "packing search config...");
                    var selectedTargetTable = curTableJoinTargetTables.get(popJoinTargetTable.listBox.getSelectedIndex());
                    d.cr("createPops", "popJoinTargetValue", "prepare popJoinTargetCombination...", selectedTargetTable);
                    var searchFuncNew = new AppSGFCellSearch(
                            tm.dbCfg, null, null, tm.curTable.nameSql, selectedTargetTable,
                            AppSGFCellSearch.MIN_SIZE_DISABLE(),
                            tm.settings.searchMaxItem, tm.settings.searchMaxSecs, searchTag, true);
                    d.cr("createPops", "popJoinTargetValue", searchFuncNew);
                    d.cr("createPops", "popJoinTargetValue", "packing searching...");
                    TGC_PanelStyleUtils.red(p.listBox, p.tb);
                    p.btnSearch.setEnabled(false);
                    TGC_SGWTCalller.async(searchFuncNew, resp -> {
//                TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                        TGC_PanelStyleUtils.remove(p.listBox, p.tb);
                        p.btnSearch.setEnabled(true);
//                }, 1);
                        if (resp.getOutput_ctm() == null) {
                            d.ce("createPops.refreshLb", "Liste yenilenirken bir hata oluştu (resp.getOutput_ctm() == null)");
                            return;
                        }
                        if (lastCTM > resp.getOutput_ctm()) {
                            d.ci("createPops.refreshLb", "Liste atlandi: + lastCTM:" + lastCTM);
                            return;
                        }
                        lastCTM = resp.getOutput_ctm();

                        if (resp.getOutput_list() == null) {
                            d.ce("createPops.refreshLb", "Liste yenilenirken bir hata oluştu (r.getOutput_list() == null)");
                            d.ce("createPops.refreshLb", resp.getOutput_status());
                            return;
                        }
                        if (resp.getOutput_list().isEmpty()) {
                            d.ce("createPops.refreshLb", resp.getOutput_status());
                            d.ce("createPops.refreshLb", "Liste boş döndü hatası!");
                            return;
                        }

                        p.listBox.clear();
                        resp.getOutput_list().forEach(s -> {
                            p.listBox.addItem(s);
                            d.ci("createPops.refreshLb", "prgGetLinkLbData", "r.getDataArrayString(i)", s);
                        });

                        if (resp.getOutput_isProcessedAsStar() && !resp.getOutput_list().isEmpty()) {
                            AppPopEditPopLINKBufferStar.add(resp.getInput_tarTablename(), resp.getOutput_list());
                            d.cr("createPops.refreshLb", "Arama bitti. " + TGS_Time.toString_now(), "hafızaya eklendi");
                        } else {
                            d.cr("createPops.refreshLb", "Arama bitti. " + TGS_Time.toString_now());
                        }
                    }, thr -> {
                        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                            TGC_PanelStyleUtils.remove(p.listBox, p.tb);
                            p.btnSearch.setEnabled(true);
                        }, 1);
                    });
                },
                null, null
        );
        popJoinTargetCombination = new TGC_PopLblYesNoListBox(
                TGC_Dimension.FULLSCREEN, null,
                "<b>Kombinasyon:</b>", "Değiştir", "İptal",
                p -> {
                    if (p.listBox.getSelectedIndex() == -1) {
                        d.ce("createPops", "popJoinTargetCombination", "Hedef kombinasyon seçilmedi hatası");
                        return;
                    }
                    p.getPop().setVisible(false);
                    var inputText = popJoinTargetValue.listBox.getSelectedItemText();
                    var inputText_ID = TGS_StringUtils.gwt().toList_spc(inputText).get(0);
                    if (!TGS_CastUtils.isLong(inputText_ID)) {
                        d.cr("createPops", "popJoinTargetTable", "id (ilk kelime) tam sayı olmalı", inputText);
                        clearJoin();
                        return;
                    }
                    var selectedTargetTable = curTableJoinTargetTables.get(popJoinTargetTable.listBox.getSelectedIndex());
                    d.cr("createPops", "popJoinTargetValue", "prepare popJoinTargetCombination...", selectedTargetTable);
                    aramaJoinConfig = curTableTargetTableCombinations(selectedTargetTable).get(popJoinTargetCombination.listBox.getSelectedIndex());
                    aramaJoinValue = inputText_ID;
                    d.cr("actionAfter_join", "BİLGİ: Filitrede alt tablo ilişkisi ayarlandı", aramaJoinConfig, aramaJoinValue);
                    TGC_ButtonUtils.setIcon(tm.filter.popMain.btnJoin, TGS_IconUtils.CLASS_LINK(), "Ek İlişki Kaldır");
                    TGC_DOMUtils.setForegroundColorText(tm.filter.btn.getElement(), "maroon");
                    TGC_DOMUtils.setForegroundColorText(tm.filter.popMain.btnJoin.getElement(), "maroon");
                    tm.cells.focusActiveCell();
                    tm.page.onPageFirst();
//                    tm.input.showBox(
//                            "VALUE",
//                            inputText -> actionAfter_join(inputText),
//                            null,//ac4,
//                            "Filitreye join eklemek için Eşitlik ifadesi"
//                    );
                },
                p -> {
                    p.getPop().setVisible(false);
                    d.cr("createPops", "popJoinTargetTable", "filitrede alt tablo ilişkisi iptal edildi");
                    clearJoin();
                },
                null,
                () -> {
                    if (popJoinTargetCombination.listBox.getItemCount() == 1) {
                        popJoinTargetCombination.listBox.setSelectedIndex(0);
                        popJoinTargetCombination.onExe.run(popJoinTargetCombination);
                    }
                }
        );
    }

    public boolean isJoin() {
        return aramaJoinConfig != null;
    }

    private void clearJoin() {
        TGC_DOMUtils.setForegroundColorText(tm.filter.btn.getElement(), "black");
        TGC_DOMUtils.setForegroundColorText(tm.filter.popMain.btnJoin.getElement(), "black");
        aramaJoinConfig = null;
        aramaJoinValue = null;
        tm.cells.focusActiveCell();
        tm.page.onPageFirst();
    }
    public volatile String aramaJoinConfig = null;
    public volatile String aramaJoinValue = null;

    private TGC_PopLblYesNoListBox popJoinTargetTable;
    private TGC_PopLblYesNoTextBoxListBox popJoinTargetValue;
    private TGC_PopLblYesNoListBox popJoinTargetCombination;

    private List<String> curTableTargetTableCombinations(CharSequence targetTable) {
        return TGS_StreamUtils.toLst(
                curTableJoinCombinationsAll.stream().filter(str -> {
                    var lst = TGS_StringUtils.gwt().toList_spc(str);
                    if (lst.size() < 2) {
                        return false;
                    }
                    return lst.get(lst.size() - 1).equals(targetTable);
                })
        );
    }
    private final List<String> curTableJoinCombinationsAll = new ArrayList();
    private final List<String> curTableJoinTargetTables = new ArrayList();

}
