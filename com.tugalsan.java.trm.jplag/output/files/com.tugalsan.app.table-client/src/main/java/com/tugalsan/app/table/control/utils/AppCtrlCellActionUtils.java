package com.tugalsan.app.table.control.utils;

import com.google.gwt.event.dom.client.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.thread.client.*;
import com.tugalsan.app.table.*;
import java.util.*;
import java.util.stream.*;
import com.tugalsan.app.table.cell.*;

public class AppCtrlCellActionUtils {

    private AppCtrlCellActionUtils() {

    }

    final private static TGC_Log d = TGC_Log.of(AppCtrlCellActionUtils.class);
    final private static boolean PARALLEL = true;

    public static void configActions(AppModuleTable tm) {
        addMouseOverHandlers(tm);
        addKeyHandlers_tableHeaderCells(tm);
        addKeyUpHandlers_tableRowCells(tm);
    }

    private static void addKeyHandlers_tableHeaderCells(AppModuleTable tm) {
        KeyUpHandler kdh = e -> {
            var cell = (AppCell_Abstract) e.getSource();
            if (tm.cells.isRowModifyVisible()) {
                if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                    tm.cells.popRowModify.onClose.run(true);
                    return;
                }
                var tabIndex = tm.cells.popRowModify.tpContent.getSelectedTabIndex();
                var tabOrder = tm.cells.popRowModify.onShowRow_tabsOrder.get(tabIndex).value1;
                var popCellRowIndex = IntStream.range(0, tabOrder.size()).filter(i -> Objects.equals(tabOrder.get(i).value0, cell)).findAny().orElse(-1);
                if (e.getNativeKeyCode() == KeyCodes.KEY_DELETE) {
                    if (popCellRowIndex != -1) {
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tabOrder.get(popCellRowIndex).value1);
                        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> tm.cells.popRowModify.onDelete.run(), 1);
                    }
                } else if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    if (e.isControlKeyDown()) {
                        tm.cells.popRowModify.onClose.run(true);
                    }
                }
            }
        };
        IntStream.range(0, tm.cells.headers.size()).parallel().forEach(ci -> {
            var w = tm.cells.headers.get(ci);
            w.addKeyUpHandler(kdh);
        });
    }

    private static void addKeyUpHandlers_tableRowCells(AppModuleTable tm) {
        KeyUpHandler kdh = e -> {
            if (e.getNativeKeyCode() == KeyCodes.KEY_DELETE) {
                tm.cells.popRowModify.onDelete.run();
                return;
            }
            if (tm.cells.isRowModifyVisible()) {
                if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                    tm.cells.popRowModify.onClose.run(true);
                    return;
                }
                if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    if (e.isControlKeyDown()) {
                        tm.cells.popRowModify.onClose.run(true);
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

    private static void addMouseOverHandlers(AppModuleTable tm) {
        var curTableName = tm.curTable.nameSql;
        var stream = PARALLEL ? tm.cells.rows.parallelStream() : tm.cells.rows.stream();
        stream.forEachOrdered(row -> {
            row.stream().forEachOrdered(cell -> {
                var btn = (AppCell_Abstract) cell;
                btn.addMouseOverHandler(e -> {
                    if (tm.cells.isRowModifyVisible()) {
                        var ct = tm.curTable.columns.get(btn.colIdx);
                        if (ct.getType().equals(TGS_SQLColTypedUtils.TYPE_STRFILE())) {
                            if (ct.getDataString1_LnkTargetTableName().equals("JPG")) {
                                if (!tm.dbCfg.isAny()) {
                                    tm.cells.popRowModify.imageHandler.reloadImageById(curTableName, ((AppCell_LNG) tm.cells.rows.get(btn.rowIdx).get(0)).getValueLong());
                                }
                            }
                        } else if (ct.getType().equals(TGS_SQLColTypedUtils.TYPE_LNGLINK())) {
                            var id = ((AppCell_LNGLINK) tm.cells.rows.get(btn.rowIdx).get(btn.colIdx)).getValueLong();
                            var table = App.tbl_canReturnNull(ct.getDataString1_LnkTargetTableName());
                            if (table == null) {
                                d.ce("SKIPPING: tooltipIMG (table == null)");
                                return;
                            }
                            var tableName = table.nameSql;
                            if (!tm.dbCfg.isAny()) {
                                tm.cells.popRowModify.imageHandler.reloadImageById(tableName, id);
                            }
                        }
                    }
                });
                btn.addMouseOutHandler(e -> {
                    if (tm.cells.isRowModifyVisible()) {
                        var ct = tm.curTable.columns.get(btn.colIdx);
                        if (ct.getType().equals(TGS_SQLColTypedUtils.TYPE_STRFILE())) {
                            if (ct.getDataString1_LnkTargetTableName().equals("JPG")) {
                                tm.cells.popRowModify.onreloadImageByRow.run(btn.rowIdx);
                            }
                        } else if (ct.getType().equals(TGS_SQLColTypedUtils.TYPE_LNGLINK())) {
                            tm.cells.popRowModify.onreloadImageByRow.run(btn.rowIdx);
                        }
                    }
                });
            });
        });
    }
}
