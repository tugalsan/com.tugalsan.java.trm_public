package com.tugalsan.app.table.control.utils;

import com.google.gwt.event.dom.client.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.thread.client.*;
import com.tugalsan.app.table.*;
import java.util.*;
import java.util.stream.*;
import com.tugalsan.app.table.cell.*;

public class AppCtrlCellFocusUtils {

    private AppCtrlCellFocusUtils() {

    }

//    final private static TGC_Log d = TGC_Log.of(AppCtrlCellFocusUtils.class);
    public static void configActions(AppModuleTable tm) {
        addOnFocusHandlers(tm);
        addKeyHandlers_tableHeaderCells(tm);
        addKeyHandlers_tableRowCells(tm);
    }

    private static void addOnFocusHandlers(AppModuleTable tm) {
        FocusHandler fh = e -> ((AppCell_Abstract) e.getSource()).onFocused();
        tm.cells.rows.forEach(row -> row.forEach(cell -> cell.addFocusHandler(fh)));
    }

    private static void addKeyHandlers_tableHeaderCells(AppModuleTable tm) {
        KeyUpHandler kdh = e -> {
            var cell = (AppCell_Abstract) e.getSource();
            if (tm.cells.isRowModifyVisible()) {
                var tabIndex = tm.cells.popRowModify.tpContent.getSelectedTabIndex();
                var tabOrder = tm.cells.popRowModify.onShowRow_tabsOrder.get(tabIndex).value1;
                var popCellRowIndex = IntStream.range(0, tabOrder.size()).filter(i -> Objects.equals(tabOrder.get(i).value0, cell)).findAny().orElse(-1);
                if (e.getNativeKeyCode() == KeyCodes.KEY_LEFT || (e.getNativeKeyCode() == KeyCodes.KEY_TAB && e.isShiftKeyDown())) {
                    if (e.isControlKeyDown()) {
                        addKeyHandlers_tableHeaderCells_onRowEdit_Ctrlleft(tm);
                    } else {
                        addKeyHandlers_tableHeaderCells_onRowEdit_left();
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_RIGHT || (e.getNativeKeyCode() == KeyCodes.KEY_TAB && !e.isShiftKeyDown())) {
                    if (e.isControlKeyDown()) {
                        addKeyHandlers_tableHeaderCells_onRowEdit_Ctrlright(tm);
                    } else {
                        addKeyHandlers_tableHeaderCells_onRowEdit_right(popCellRowIndex, tabOrder);
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_UP) {
                    if (e.isControlKeyDown()) {
                        addKeyHandlers_tableHeaderCells_onRowEdit_Ctrlup(tm);
                    } else {
                        addKeyHandlers_tableHeaderCells_onRowEdit_up(tm, popCellRowIndex, tabOrder);
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
                    if (e.isControlKeyDown()) {
                        tm.cells.popRowModify.onNextClick.run();
                    } else {
                        if (popCellRowIndex > tabOrder.size() - 2) {
                            //DO NOTHING
                        } else {
                            if (popCellRowIndex != -1) {
                                TGC_FocusUtils.setFocusAfterGUIUpdate(tabOrder.get(popCellRowIndex + 1).value0);
                            }
                        }
                    }
                }
            } else {
                var ci = cell.colIdx;
                if (e.getNativeKeyCode() == KeyCodes.KEY_LEFT || (e.getNativeKeyCode() == KeyCodes.KEY_TAB && e.isShiftKeyDown())) {
                    if (e.isControlKeyDown()) {
                        tm.page.onPagePrev();
                        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getPreferredActiveCell());
                        }, 0.5f);
                    } else {
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.headers.get(AppCtrlCellFocusCalcUtils.getShownOrderedLeftColumn(tm, ci)));
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_RIGHT || (e.getNativeKeyCode() == KeyCodes.KEY_TAB && !e.isShiftKeyDown())) {
                    if (e.isControlKeyDown()) {
                        tm.page.onPageNext();
                        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getPreferredActiveCell());
                        }, 0.5f);
                    } else {
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.headers.get(AppCtrlCellFocusCalcUtils.getShownOrderedRightColumn(tm, ci)));
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_UP) {
                    if (e.isControlKeyDown()) {
                        tm.add.onAdd();
                    } else {
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tm.filter.btn);
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
                    if (e.isControlKeyDown()) {
                        tm.filter.showSth_Acc2CtrlKey();
                    } else {
                        if (!tm.cells.rows.isEmpty()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.rows.get(0).get(ci));
                        }
                    }
                }
            }
        };
        IntStream.range(0, tm.cells.headers.size()).parallel().forEach(ci -> {
            var w = tm.cells.headers.get(ci);
            w.addKeyUpHandler(kdh);
        });
    }

    private static void addKeyHandlers_tableHeaderCells_onRowEdit_Ctrlleft(AppModuleTable tm) {
        tm.cells.popRowModify.onFocusPrevTab();
    }

    private static void addKeyHandlers_tableHeaderCells_onRowEdit_Ctrlright(AppModuleTable tm) {
        tm.cells.popRowModify.onFocusNextTab();
    }

    private static void addKeyHandlers_tableHeaderCells_onRowEdit_left() {
        //DO NOTHING
    }

    private static void addKeyHandlers_tableHeaderCells_onRowEdit_right(int popCellRowIndex, List<TGS_Tuple2<AppCell_Abstract, AppCell_Abstract>> tabOrder) {
        if (popCellRowIndex != -1) {
            TGC_FocusUtils.setFocusAfterGUIUpdate(tabOrder.get(popCellRowIndex).value1);
        }
    }

    private static void addKeyHandlers_tableHeaderCells_onRowEdit_up(AppModuleTable tm, int popCellRowIndex, List<TGS_Tuple2<AppCell_Abstract, AppCell_Abstract>> tabOrder) {
        if (popCellRowIndex < 1) {
            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.popRowModify.btnCancel);
        } else {
            if (popCellRowIndex != -1) {
                TGC_FocusUtils.setFocusAfterGUIUpdate(tabOrder.get(popCellRowIndex - 1).value0);
            }
        }
    }

    private static void addKeyHandlers_tableHeaderCells_onRowEdit_Ctrlup(AppModuleTable tm) {
        tm.cells.popRowModify.onPrevClick.run();
    }

    private static void addKeyHandlers_tableRowCells(AppModuleTable tm) {
        KeyUpHandler kdh = e -> {
            var cell = (AppCell_Abstract) e.getSource();
            var ri = cell.rowIdx;
            var ci = cell.colIdx;
            if (tm.cells.isRowModifyVisible()) {
                var tabIndex = tm.cells.popRowModify.tpContent.getSelectedTabIndex();
                var tabOrder = tm.cells.popRowModify.onShowRow_tabsOrder.get(tabIndex).value1;
                var popCellRowIndex = IntStream.range(0, tabOrder.size()).filter(i -> Objects.equals(tabOrder.get(i).value1, cell)).findAny().orElse(-1);
                if (e.getNativeKeyCode() == KeyCodes.KEY_LEFT || (e.getNativeKeyCode() == KeyCodes.KEY_TAB && e.isShiftKeyDown())) {
                    if (e.isControlKeyDown()) {
                        tm.cells.popRowModify.onFocusPrevTab();
                    } else {
                        if (popCellRowIndex != -1) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tabOrder.get(popCellRowIndex).value0);
                        }
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_RIGHT || (e.getNativeKeyCode() == KeyCodes.KEY_TAB && !e.isShiftKeyDown())) {
                    if (e.isControlKeyDown()) {
                        tm.cells.popRowModify.onFocusNextTab();
                    } else {
                        //DO NOTHING
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_UP) {
                    if (e.isControlKeyDown()) {
                        tm.cells.popRowModify.onPrevClick.run();
                    } else {
                        if (popCellRowIndex < 1) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.popRowModify.btnCancel);
                        } else {
                            if (popCellRowIndex != -1) {
                                TGC_FocusUtils.setFocusAfterGUIUpdate(tabOrder.get(popCellRowIndex - 1).value1);
                            }
                        }
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
                    if (e.isControlKeyDown()) {
                        tm.cells.popRowModify.onNextClick.run();
                    } else {
                        if (popCellRowIndex > tabOrder.size() - 2) {
                            //DO NOTHING
                        } else {
                            if (popCellRowIndex != -1) {
                                TGC_FocusUtils.setFocusAfterGUIUpdate(tabOrder.get(popCellRowIndex + 1).value1);
                            }
                        }
                    }
                }
            } else {
                if (e.getNativeKeyCode() == KeyCodes.KEY_LEFT || (e.getNativeKeyCode() == KeyCodes.KEY_TAB && e.isShiftKeyDown())) {
                    if (e.isControlKeyDown()) {
                        tm.page.onPagePrev();
                        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getPreferredActiveCell());
                        }, 0.5f);
                    } else {
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.rows.get(ri).get(AppCtrlCellFocusCalcUtils.getShownOrderedLeftColumn(tm, ci)));
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_RIGHT || (e.getNativeKeyCode() == KeyCodes.KEY_TAB && !e.isShiftKeyDown())) {
                    if (e.isControlKeyDown()) {
                        tm.page.onPageNext();
                        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getPreferredActiveCell());
                        }, 0.5f);
                    } else {
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.rows.get(ri).get(AppCtrlCellFocusCalcUtils.getShownOrderedRightColumn(tm, ci)));
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_UP) {
                    if (e.isControlKeyDown()) {
                        tm.add.onAdd();
                    } else {
                        if (ri - 1 > -1) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.rows.get(ri - 1).get(ci));
                        } else {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.headers.get(ci));
                        }
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
                    if (e.isControlKeyDown()) {
                        tm.filter.showSth_Acc2CtrlKey();
                    } else {
                        if (ri + 1 < tm.cells.rows.size()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.rows.get(ri + 1).get(ci));
                        }
                    }
                }
            }
        };
        IntStream.range(0, tm.cells.rows.size()).parallel().forEach(ri -> {
            IntStream.range(0, tm.cells.rows.get(ri).size()).parallel().forEach(ci -> {
                var w = tm.cells.rows.get(ri).get(ci);
                w.addKeyUpHandler(kdh);
            });
        });
    }
}
