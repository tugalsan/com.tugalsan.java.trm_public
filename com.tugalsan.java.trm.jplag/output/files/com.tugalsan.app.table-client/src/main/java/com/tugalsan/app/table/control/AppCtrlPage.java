package com.tugalsan.app.table.control;

import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_OutBool_In1;
import com.tugalsan.api.gui.client.browser.TGC_BrowserTabUtils;
import com.tugalsan.api.gui.client.click.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.key.*;
import com.tugalsan.api.gui.client.theme.TGC_PanelStyleUtils;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.thread.client.*;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.control.utils.AppCtrlCellRowUtils;
import com.tugalsan.app.table.sg.query.*;
import com.tugalsan.lib.boot.client.*;
import com.tugalsan.lib.rql.client.*;
import java.util.*;
import java.util.stream.*;

public class AppCtrlPage {

    final private static TGC_Log d = TGC_Log.of(false, AppCtrlPage.class);
    public int pageNrCurrent = 1;
    public int pageNrMax = 1;

    public AppCtrlPage(AppModuleTable tm) {
        this.tm = tm;
    }
    final private AppModuleTable tm;

    public void createWidgets() {
        btnFirst = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_FIRST());
        btnPrev = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_POINT_LEFT());
        btnNr = new PushButton(TGS_IconUtils.CLASS_PAGE_BREAK() + " Yenile");
        btnNext = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_POINT_RIGHT());
        btnLast = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_LAST());
    }
    public PushButton btnFirst, btnPrev, btnNr, btnNext, btnLast;

    public void configFocus() {
        TGC_FocusUtils.addKeyDown(btnFirst, nativeKeyCode -> {
            var filterFocusSides = new TGS_FocusSides4(tm.filter.btn, btnPrev, TGC_LibBootGUIBody.windowOperatorButton, tm.cells.getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btnFirst, filterFocusSides, nativeKeyCode);
        });
        TGC_FocusUtils.addKeyDown(btnPrev, nativeKeyCode -> {
            var filterFocusSides = new TGS_FocusSides4(btnFirst, btnNr, TGC_LibBootGUIBody.windowOperatorButton, tm.cells.getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btnPrev, filterFocusSides, nativeKeyCode);
        });
        TGC_FocusUtils.addKeyDown(btnNr, nativeKeyCode -> {
            var filterFocusSides = new TGS_FocusSides4(btnPrev, btnNext, TGC_LibBootGUIBody.windowOperatorButton, tm.cells.getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btnNr, filterFocusSides, nativeKeyCode);
        });
        TGC_FocusUtils.addKeyDown(btnNext, nativeKeyCode -> {
            var filterFocusSides = new TGS_FocusSides4(btnNr, btnLast, TGC_LibBootGUIBody.windowOperatorButton, tm.cells.getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btnNext, filterFocusSides, nativeKeyCode);
        });
        TGC_FocusUtils.addKeyDown(btnLast, nativeKeyCode -> {
            var filterFocusSides = new TGS_FocusSides4(btnNext, tm.add.btn, TGC_LibBootGUIBody.windowOperatorButton, tm.cells.getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btnLast, filterFocusSides, nativeKeyCode);
        });
    }

    public void configActions() {
        TGC_ClickUtils.add(btnFirst, () -> {
            onPageFirst();
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnFirst);
        });
        TGC_ClickUtils.add(btnPrev, () -> {
            onPagePrev();
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnPrev);
        });
        TGC_ClickUtils.add(btnNr, () -> onPageNr());
        TGC_ClickUtils.add(btnNext, () -> {
            onPageNext();
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnNext);
        });
        TGC_ClickUtils.add(btnLast, () -> {
            onPageLast();
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnLast);
        });

        TGC_KeyUtils.add(btnFirst, () -> {
            onPageFirst();
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnFirst);
        }, null);
        TGC_KeyUtils.add(btnPrev, () -> {
            onPagePrev();
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnPrev);
        }, null);
        TGC_KeyUtils.add(btnNr, () -> onPageNr(), null);
        TGC_KeyUtils.add(btnNext, () -> {
            onPageNext();
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnNext);
        }, null);
        TGC_KeyUtils.add(btnLast, () -> {
            onPageLast();
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnLast);
        }, null);
    }

    public void configLayout(HorizontalPanel p) {
        p.add(btnFirst);
        btnFirst.addStyleName(AppModuleTable.class.getSimpleName() + "_btnSmall");
        p.add(btnPrev);
        btnPrev.addStyleName(AppModuleTable.class.getSimpleName() + "_btnSmall");
        p.add(btnNr);
        btnNr.addStyleName(AppModuleTable.class.getSimpleName() + "_btn");
        p.add(btnNext);
        btnNext.addStyleName(AppModuleTable.class.getSimpleName() + "_btnSmall");
        p.add(btnLast);
        btnLast.addStyleName(AppModuleTable.class.getSimpleName() + "_btnSmall");
        recolorLeftRightButtons();
    }

    public void onPageLast() {
        var useBufferIfPossible = false;
        var forceSyncClear = false;
        onPage(pageNrMax, useBufferIfPossible, forceSyncClear);
        recolorLeftRightButtons();
    }

    public void onPageNext() {
        var useBufferIfPossible = !AppCtrlShowKeys.isControlled;
        var forceSyncClear = AppCtrlShowKeys.isControlled;
        onPage(pageNrCurrent + 1, useBufferIfPossible, forceSyncClear);
        recolorLeftRightButtons();
    }

    private void onPageNr() {
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPageNr", "#0");
        }
        tm.input.setCustomBoxEscFocusWidget = btnNr;
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPageNr", "#1");
        }
        tm.input.setPopBoxOkText("Göster");
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPageNr", "#2");
        }
        tm.input.showBox(String.valueOf(pageNrCurrent),
                onPageAction,
                btnNr,
                "<b>Sayfa Nr:</b>",
                TGS_IconUtils.CLASS_FIRST(), "İlk", "1",
                TGS_IconUtils.CLASS_LAST(), "Son", String.valueOf(pageNrMax)
        );
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPageNr", "#3");
        }
        recolorLeftRightButtons();
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPageNr", "#4");
        }
    }

    public void onPageFirst() {
        var useBufferIfPossible = false;
        var forceSyncClear = false;
        onPage(1, useBufferIfPossible, forceSyncClear);
        recolorLeftRightButtons();
    }

    public void onPagePrev() {
        var useBufferIfPossible = !AppCtrlShowKeys.isControlled;
        var forceSyncClear = AppCtrlShowKeys.isControlled;
        onPage(pageNrCurrent - 1, useBufferIfPossible, forceSyncClear);
    }

    public void onPage(int pageNrNew, boolean useBufferIfPossible, boolean forceSyncClear) {
        if (pageNrNew < 1) {
            pageNrNew = 1;
        }
        if (pageNrNew > pageNrMax) {
            pageNrNew = pageNrMax;
        }
        pageNrCurrent = pageNrNew;
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPage", "update", "begin");
        }
        update(null, useBufferIfPossible, forceSyncClear);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPage", "update", "end");
        }
    }

    public boolean isLast() {
        return Objects.equals(pageNrCurrent, pageNrMax);
    }

    public boolean isFirst() {
        return pageNrCurrent == 1;
    }

    private void update_queryPage_pageNrMax() {
        var tn = tm.curTable.nameSql;
        var where = tm.filter.getWhereStmt();
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage_pageNrMax", "tn", tn, "where", where);
        }
        TGC_SGWTCalller.async(true, new AppSGFQueryCount(tm.dbCfg, tn, where, tm.filter.join.aramaJoinConfig, tm.filter.join.aramaJoinValue), resp -> {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "refreshPageNrMax", "response.getOutput_count()", resp.getOutput_count());
            }
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "refreshPageNrMax", "tm.tableWidgets.getRowSize()", tm.cells.getRowSize());
            }
            pageNrMax = (int) Math.ceil((double) resp.getOutput_count() / tm.cells.getRowSize());
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "refreshPageNrMax", "pageNrMax", pageNrMax);
            }
            if (pageNrMax == 0) {
                pageNrMax = 1;
            }
            TGC_ButtonUtils.setIcon(btnNr, TGS_IconUtils.CLASS_PAGE_BREAK(), pageNrCurrent + "/" + pageNrMax);
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "refreshPageNrMax", "fin");
            }
            recolorLeftRightButtons();
        });
    }

    public void updatePage_IfIdIsNotShown_WarnUser(long id) {
        update(() -> {
            TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                if (AppCtrlCellRowUtils.getRowIdxById(tm.cells, id) == null) {
                    d.ce("insertNewRow", " Ancak sayfada görünmüyor! Sayfayı değiştirmeyi veya Filitreyi kaldırmayı deyebilirsiniz.");
                }
            }, 3);
        }, false, true);
    }

    public TGS_FuncMTU_In1<String> onPageAction = inputText -> {
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPageAction", "#0");
        }
        var pageNrToGo = TGS_CastUtils.toInteger(inputText).orElse(null);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPageAction", "#1");
        }
        if (pageNrToGo == null) {
            d.ce("onPageAction", "Girdi no hatası");
            return;
        }
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPageAction", "#2");
        }
        onPage(pageNrToGo, false, true);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPageAction", "#3");
        }
        TGC_FocusUtils.setFocusAfterGUIUpdate(btnNr);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "onPageAction", "#4");
        }
    };

    public void configInit(String filterIdStart, String filterIdEnd) {
        if (Objects.nonNull(filterIdStart) && Objects.equals(filterIdStart, filterIdEnd)) {
            update_runOnceAfterwards = () -> {
                var cellAbstract = (AppCell_Abstract) tm.cells.rows.get(0).get(1);
                cellAbstract.onFocused();
                tm.cells.popRowModify.onShowRow(0, true);
            };
        }
    }

    public void update(TGS_FuncMTU exe, boolean useBufferIfPossible, boolean forceSyncClear) {
        var start = TGS_Time.of();
        var dbConfig = tm.dbCfg;
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update", "dbConfig", dbConfig);
        }
        var tn = tm.curTable.nameSql;
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update", "tm.curTable", tn);
        }
        var pageIdxCur = pageNrCurrent - 1;
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update", "pageIdx", pageIdxCur);
        }
        var rowIdxStart = (pageNrCurrent - 1) * tm.cells.getRowSize();
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update", "rowIdxStart", rowIdxStart);
        }
        var rowIdxSize = tm.cells.getRowSize();
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update", "rowIdxSize", rowIdxSize);
        }
        var wherestmt = tm.filter.getWhereStmt();
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update", "wherestmt", wherestmt);
        }
        var orderbystmt = tm.filter.getOrderByStmt();
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update", "orderbystmt", orderbystmt);
        }
        d.cr("update", TGS_Time.toString_now(), "Tablo güncelleniyor...");
        TGC_SGWTCalller.async(new AppSGFQueryPage(dbConfig, tn, rowIdxStart, rowIdxSize, wherestmt, orderbystmt, tm.filter.join.aramaJoinConfig, tm.filter.join.aramaJoinValue), reply -> {
            var end = TGS_Time.of();
            var serverDifference = start.getSecondsDifference(end);
            if (reply == null) {
                d.ce("update", "reply == null");
                return;
            }
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update", "Taze veri ile yavaş gösterim sağlanıyor...");
            }
            recolorLeftRightButtons();
            update_queryPage(reply, serverDifference);
            if (exe != null) {
                exe.run();
            }
            if (update_runOnceAfterwards != null) {
                update_runOnceAfterwards.run();
                update_runOnceAfterwards = null;
            }
        });
    }
    public TGS_FuncMTU update_runOnceAfterwards = null;

    public void recolorLeftRightButtons() {
        var wherestmt = tm.filter.getWhereStmt();
        var orderbystmt = tm.filter.getOrderByStmt();
        var pageIdxMin = 0;
        var pageIdxCur = pageNrCurrent - 1;
        var pageIdxMax = pageNrMax - 1;
        //NEXT PAGE
        var pageIdxNext = pageIdxCur + 1;
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "recolorLeftRightButtons", "---------", "pageIdxCur", pageIdxCur);
        }
        if (pageIdxNext > pageIdxMax) {//IF MAX
            btnNext.setEnabled(false);
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "recolorLeftRightButtons", "pageIdxNext > pageIdxMax", pageIdxNext, pageIdxMax);
            }
            TGC_PanelStyleUtils.remove(btnNext);
        } else {//ELSE
            btnNext.setEnabled(true);
        }
        //PREV PAGE
        var pageIdxPrev = pageIdxCur - 1;
        if (pageIdxPrev < pageIdxMin) {
            btnPrev.setEnabled(false);
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "recolorLeftRightButtons", "pageIdxPrev < pageIdxMin", pageIdxPrev, pageIdxMin);
            }
            TGC_PanelStyleUtils.remove(btnPrev);
        } else {//FIRST
            btnPrev.setEnabled(true);
        }
    }

    private void update_queryPage(AppSGFQueryPage resp, long serverDifference) {
        var start = TGS_Time.of();
        if (resp == null) {
            d.ce("update_queryPage", "resp== null");
            return;
        }
        if (resp.getOutput_column_values() == null) {
            d.ce("update_queryPage", "resp.getOutput_column_values() == null");
            return;
        }
        tm.cells.setEnableDataTable(false);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "tm.cells.setEnableDataTable(false)");
        }
        var queryColSize = resp.getOutput_column_values().size();
        var queryRowSize = queryColSize == 0 ? 0 : resp.getOutput_column_values().get(0).size();
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "queryRowSize", queryRowSize, "queryColSize", queryColSize);
        }
        var tableGUIRowSize = tm.cells.getRowSize();
        var tableSQLColSize = tm.curTable.columns.size();
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "tableGUIRowSize", tableGUIRowSize, "tableSQLColSize", tableSQLColSize);
        }
        update_queryPage_for(resp, queryRowSize, tableGUIRowSize, tableSQLColSize);
        var end = TGS_Time.of();
        var updateDifference = start.getSecondsDifference(end);
//        d.cr("update_queryPage", "Sayfa güncelleme saniyesi:", updateDifference);

        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(exe -> {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "#5");
            }
            update_queryPage_pageNrMax();
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "#6");
            }
            var now = TGS_Time.of();
            var maxDifference = start.getSecondsDifference(now);
            d.cr("update_queryPage", now.toString(), "Tablo güncellendi.", "server: " + serverDifference + " sn", "client: " + updateDifference + "~" + maxDifference + " sn");
        }, 0.1f);
    }

    private void update_queryPage_for(AppSGFQueryPage resp, int queryRowSize, int tableGUIRowSize, int tableSQLColSize) {
        var enableFunctionDebug = false;
        TGS_StreamUtils.forEachOptions(IntStream.range(0, tableGUIRowSize), (ri, forOptionsRows) -> {
            if (enableFunctionDebug) {
                if (d.infoEnable) {
                    d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "start");
                }
            }
            TGS_StreamUtils.forEachOptions(IntStream.range(0, tableSQLColSize), (ci, forOptionsCols) -> {
                update_queryPage_for_ri_ci(resp, queryRowSize, ri, forOptionsRows, ci, forOptionsCols);
            });
            if (enableFunctionDebug) {
                if (d.infoEnable) {
                    d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "end");
                }
            }
        });
    }

    private void update_queryPage_for_ri_ci(AppSGFQueryPage resp, int queryRowSize, int ri, TGS_StreamUtils.Options forOptionsRows, int ci, TGS_StreamUtils.Options forOptionsCols) {
        var enableFunctionDebug = false;
        if (enableFunctionDebug) {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "start");
            }
        }
        var ct = tm.curTable.columns.get(ci);
        var tc = TGS_LibRqlColUtils.toSqlCol(ct);
        if (enableFunctionDebug) {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "#1");
            }
        }
        var tableCellAbstract = (AppCell_Abstract) tm.cells.rows.get(ri).get(ci);
        if (enableFunctionDebug) {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "getSimpleName", tableCellAbstract.getClass().getSimpleName());
            }
        }
        if (ri >= queryRowSize) {
            tableCellAbstract.reset();
            return;
        }
        if (enableFunctionDebug) {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "#4");
            }
        }
        tm.cells.setEnableDataRow(ri, true);
        if (enableFunctionDebug) {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "#3");
            }
        }
        var respCellAbstract = resp.getOutput_column_values().get(ci).get(ri);
        if (tc.typeLngLnk()) {
            if (enableFunctionDebug) {
                if (d.infoEnable) {
                    d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "typeLngLnk", tc.columnName);
                }
            }
            var tableCellLngLink = (AppCell_LNGLINK) tableCellAbstract;
            if (enableFunctionDebug) {
                if (d.infoEnable) {
                    d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "typeLngLnk", "#1");
                }
            }

            if (resp.getOutput_column_idsIfValueTypeIsLngLink().size() - 1 < ci) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "Error: tc.typeLngLnk() > resp.getOutput_column_idsIfValueTypeIsLngLink().size() - 1 <= ci");
                forOptionsCols.stop();
                forOptionsRows.stop();
                return;
            }
            if (resp.getOutput_column_idsIfValueTypeIsLngLink().get(ci).size() - 1 < ri) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "Error: tc.typeLngLnk() > resp.getOutput_column_idsIfValueTypeIsLngLink().get(ci).size() - 1 <= ri");
                forOptionsCols.stop();
                forOptionsRows.stop();
                return;
            }
            var respId_SqlCellLng = resp.getOutput_column_idsIfValueTypeIsLngLink().get(ci).get(ri);
            if (enableFunctionDebug) {
                if (d.infoEnable) {
                    d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "typeLngLnk", "#2");
                }
            }
            if (respCellAbstract instanceof TGS_SQLCellSTR respCell_SqlCellLngLnkStr) {
                if (enableFunctionDebug) {
                    if (d.infoEnable) {
                        d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "typeLngLnk", "#4");
                    }
                }
                tableCellLngLink.setValueLongAndText(respId_SqlCellLng.getValueLong(), respCell_SqlCellLngLnkStr.getValueString());
                if (enableFunctionDebug) {
                    if (d.infoEnable) {
                        d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "typeLngLnk", "#5");
                    }
                }
            } else {
                d.ce("Error: respLNGLINK_STR not instanceof TGS_SQLResultSetValueSTR");
                forOptionsCols.stop();
                forOptionsRows.stop();
            }
            return;
        }
        if (enableFunctionDebug) {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "#5");
            }
        }
        if (tc.familyLng()) {//covers float date and time
            if (enableFunctionDebug) {
                if (d.infoEnable) {
                    d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "familyLng", tc.columnName);
                }
            }
            var tableCellLng = (AppCell_LNG) tableCellAbstract;
            if (respCellAbstract instanceof TGS_SQLCellLNG resp_SqlCellLng) {
                tableCellLng.setValueLong(resp_SqlCellLng.getValueLong());
            } else {
                d.ce("update_queryPage", "Error: respLNG not instanceof TGS_SQLResultSetValueLNG)");
                forOptionsCols.stop();
                forOptionsRows.stop();
            }
            return;
        }
        if (enableFunctionDebug) {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "#6");
            }
        }
        if (tc.familyStr()) {
            if (enableFunctionDebug) {
                if (d.infoEnable) {
                    d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "typeStr", tc.columnName);
                }
            }
            var tableCellStr = (AppCell_STR) tableCellAbstract;
            tableCellStr.extendedId = null;
            var resp_SQlCellStr = (TGS_SQLCellSTR) respCellAbstract;
            tableCellStr.setValueString(resp_SQlCellStr.getValueString());
            return;
        }
        if (enableFunctionDebug) {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "#7");
            }
        }
        if (tc.typeBytesStr()) {
            if (enableFunctionDebug) {
                if (d.infoEnable) {
                    d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "typeBytesStr", tc.columnName);
                }
            }
            var tableCellBytesStr = (AppCell_BYTESSTR) tableCellAbstract;
            var resp_SqlCellBytesStr = (TGS_SQLCellBYTESSTR) respCellAbstract;
            tableCellBytesStr.setValueString(resp_SqlCellBytesStr.getValueString());
            return;
        }
        if (enableFunctionDebug) {
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "#8");
            }
        }
        if (tc.typeBytesRow()) {
            if (enableFunctionDebug) {
                if (d.infoEnable) {
                    d.debug(TGS_Log.TYPE_INF(), d.className(), "update_queryPage", "ri", ri, "ci", ci, "typeBytesRow", tc.columnName);
                }
            }
            return;
        }
        d.ce("handleQueryPage", "ERROR: colType not detected", ct.getColumnName());
    }
}
