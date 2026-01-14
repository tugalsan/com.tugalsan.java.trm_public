package com.tugalsan.app.table.control.utils;

import com.tugalsan.api.gui.client.dom.TGC_DOMUtils;
import com.tugalsan.api.gui.client.panel.TGC_PanelAbsoluteUtils;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.shape.client.TGS_ShapeRectangle;
import com.tugalsan.app.table.App;
import com.tugalsan.app.table.AppModuleTable;
import com.tugalsan.app.table.cell.AppCell_STR;
import com.tugalsan.lib.rql.cfg.client.TGS_LibRqlCfgUtils;
import com.tugalsan.lib.rql.client.TGS_LibRqlTblUtils;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AppCtrlCellLayoutUtils {

    final private static TGC_Log d = TGC_Log.of(false, AppCtrlCellLayoutUtils.class);

    private AppCtrlCellLayoutUtils() {

    }

    public static int XSPACE() {
        return 10;
    }

    public static int YSPACE() {
        return 10;
    }

    private static boolean isRenderable_do(AppModuleTable tm, int colIdx, boolean includeFileteredColumns) {
        if (tm.dbCfg.isAny()) {
            return true;
        }
        if (includeFileteredColumns && tm.filter.popMain != null) {
            if (tm.filter == null) {
                d.ce("isRenderable_do", "tm.filter == null");
            } else if (tm.filter.popMain == null) {
                d.ce("isRenderable_do", "tm.filter.popMain");
            } else if (tm.filter.popMain.profiles == null) {
                d.ce("isRenderable_do", "tm.filter.popMain.profiles");
            } else if (tm.filter.popMain.profiles.size() < colIdx) {
                d.ce("isRenderable_do", "tm.filter.popMain.profiles.size() < colIdx", tm.filter.popMain.profiles.size(), colIdx);
            } else if (tm.filter.popMain.profiles.get(colIdx).cbActive) {
                return true;
            }
        }
        return tm.curTable.renderConfig_colIdx.stream().anyMatch(renderIdx -> renderIdx == colIdx);
    }

    public static void isRenderable_clear() {
        isRenderable_buffer_includeFileteredColumns = null;
        isRenderable_buffer = null;
    }

    public static boolean isRenderable(AppModuleTable tm, int colIdx, boolean includeFileteredColumns) {
        if (includeFileteredColumns) {
            if (isRenderable_buffer_includeFileteredColumns == null) {
                isRenderable_buffer_includeFileteredColumns = IntStream.range(0, tm.curTable.columns.size())
                        .mapToObj(ci -> isRenderable_do(tm, ci, true))
                        .collect(Collectors.toList());//TO LIST: GWT WONT LIKE YOU
            }
            return isRenderable_buffer_includeFileteredColumns.get(colIdx);
        }
        if (isRenderable_buffer == null) {
            isRenderable_buffer = IntStream.range(0, tm.curTable.columns.size())
                    .mapToObj(ci -> isRenderable_do(tm, ci, false))
                    .collect(Collectors.toList());//TO LIST: GWT WONT LIKE YOU
        }
        return isRenderable_buffer.get(colIdx);
    }
    private static List<Boolean> isRenderable_buffer_includeFileteredColumns = null;
    private static List<Boolean> isRenderable_buffer = null;

    public static boolean isHidden(AppModuleTable tm, int colIdx) {
        if (isHidden_buffer == null) {
            if (tm.dbCfg.isAny()) {
                isHidden_buffer = IntStream.range(0, tm.curTable.columns.size())
                        .mapToObj(ci -> false)
                        .collect(Collectors.toList());//TO LIST: GWT WONT LIKE YOU
            } else {
                isHidden_buffer = IntStream.range(0, tm.curTable.columns.size())
                        .mapToObj(ci -> tm.cells.isColHidden[ci])
                        .collect(Collectors.toList());//TO LIST: GWT WONT LIKE YOU
            }
        }
        return isHidden_buffer.get(colIdx);
    }
    private static List<Boolean> isHidden_buffer = null;

    private static int getColOffsetX(AppModuleTable tm, int colIdx) {
        var colOffsetIdx = 0;
        for (; colOffsetIdx < tm.curTable.tableOrder.length; colOffsetIdx++) {
            if (tm.curTable.tableOrder[colOffsetIdx] == colIdx) {
                break;
            }
        }
        if (colOffsetIdx == tm.curTable.tableOrder.length) {
            return 0;
        }
        var colOffset = 0;
        for (var colOffsetI = 0; colOffsetI < colOffsetIdx; colOffsetI++) {
            var ci = tm.curTable.tableOrder[colOffsetI];
            if (isHidden(tm, ci) /*AppCtrlCellLayoutUtils.isHidden(tm, ci)*/) {
                continue;
            }
            if (!isRenderable(tm, ci, true) /*isRenderable(d.className() + ".getColOffsetX", tm, ci, true)*/) {
                continue;
            }
            colOffset += tm.cells.colWidths.get(tm.curTable.tableOrder[colOffsetI]);
        }
        return colOffset;
    }

    public static void cells_layout_refresh(String from, AppModuleTable tm) {
        d.ci("cells_layout_refresh", "Hücreler yeniden konumlandırıldı", from);
        var columnNames = TGS_LibRqlTblUtils.colNamesToList(tm.curTable);
        final var maxW = new AtomicInteger(TGC_DOMUtils.getWidth(tm.content.getElement()));
        final var maxH = new AtomicInteger(TGC_DOMUtils.getHeight(tm.content.getElement()));
        final var x = new AtomicInteger(TGS_LibRqlCfgUtils.DEFAULT_CELL_DIM().height);
        final var y = new AtomicInteger(TGS_LibRqlCfgUtils.DEFAULT_CELL_DIM().height);
        final var w = new AtomicInteger(TGS_LibRqlCfgUtils.DEFAULT_CELL_DIM().height);
        final var h = new AtomicInteger(TGS_LibRqlCfgUtils.DEFAULT_CELL_DIM().height);
        d.ci("cells_layout_refresh", "cnSize", columnNames.size());
        //PLACE HEADER CELLS IF NOT_HIDDEN
        IntStream.range(0, columnNames.size()).forEachOrdered(ci -> {
            var fw = tm.cells.headers.get(ci);
            TGC_PanelAbsoluteUtils.removeWidget(tm.content, fw);
        });
        IntStream.range(0, columnNames.size()).forEachOrdered(ci -> {
            var fw = tm.cells.headers.get(ci);
            if (AppCtrlCellLayoutUtils.isHidden(tm, ci)) {
                return;
            }
//            var colIsRenderable = tm.curTable.renderConfig_colIdx.stream().anyMatch(renderIdx -> renderIdx == ci);
//            if (colIsRenderable) {
//                TGC_PanelAbsoluteUtils.removeWidget(tm.content, fw);
//                return;
//            }
            ((AppCell_STR) fw).changeMaxChar(254);
            TGC_DOMUtils.setBorder(fw.getElement(), 1, true, "green");
            TGC_DOMUtils.setFontBold(fw.getElement(), true);
            TGC_DOMUtils.setTextAlignRight(fw.getElement());
            x.set(XSPACE() + getColOffsetX(tm, ci));
            y.set(YSPACE() + 0 * TGS_LibRqlCfgUtils.DEFAULT_CELL_DIM().height);
            w.set(tm.cells.colWidths.get(ci));
            if (App.loginCard.userAdmin) {
                fw.setTitle(columnNames.get(ci) + " [" + ci + "." + w + "]");
            }
            var rect = TGS_ShapeRectangle.of(x.get(), y.get(), w.get(), h.get());
            TGC_PanelAbsoluteUtils.setWidget(tm.content, fw, rect);
            maxW.set(maxW.get() < (x.get() + w.get()) ? (x.get() + w.get()) : maxW.get());
            maxH.set(maxH.get() < (y.get() + h.get()) ? (y.get() + h.get()) : maxH.get());
        });
        //PLACE DATA CELLS IF NOT_HIDDEN
        IntStream.range(0, tm.cellsRowSize).forEachOrdered(ri -> {
            var tableDataRow = tm.cells.rows.get(ri);
            IntStream.range(0, columnNames.size()).forEachOrdered(ci -> {
                var fw = tableDataRow.get(ci);
                if (AppCtrlCellLayoutUtils.isHidden(tm, ci)) {
                    return;
                }
//                var colIsRenderable = tm.curTable.renderConfig_colIdx.stream().anyMatch(renderIdx -> renderIdx == ci);
//                if (colIsRenderable) {
//                    TGC_PanelAbsoluteUtils.removeWidget(tm.content, fw);
//                    return;
//                }
                TGC_DOMUtils.setBorder(fw.getElement(), 1, true, "gray");
                x.set(XSPACE() + getColOffsetX(tm, ci));
                y.set(YSPACE() + (ri + 1) * TGS_LibRqlCfgUtils.DEFAULT_CELL_DIM().height);
                w.set(tm.cells.colWidths.get(ci));
                var rect = TGS_ShapeRectangle.of(x.get(), y.get(), w.get(), h.get());
                TGC_PanelAbsoluteUtils.setWidget(tm.content, fw, rect);
                maxW.set(maxW.get() < (x.get() + w.get()) ? (x.get() + w.get()) : maxW.get());
                maxH.set(maxH.get() < (y.get() + h.get()) ? (y.get() + h.get()) : maxH.get());
            });
        });
        TGC_DOMUtils.setSize(tm.content.getElement(), maxW.get() + XSPACE(), maxH.get() + YSPACE());
    }

    public static void cells_visibility_refresh(AppModuleTable tm, Integer optional_onEditMustShowRowIdx) {
        d.ci("cells_visibility_refresh", "init", "optional_onEditMustShowRowIdx", optional_onEditMustShowRowIdx);
        if (tm.curTable.columns.size() != tm.cells.headers.size()) {
            d.ce("cells_visibility_refresh", "tm.curTable.columns.size() != tm.cells.headers.size()", tm.curTable.columns.size(), tm.cells.headers.size());
            return;
        }
        var isAny = tm.dbCfg.isAny();
        IntStream.range(0, tm.curTable.columns.size()).forEach(ci -> {
            if (isAny) {
                tm.cells.headers.get(ci).setVisible(true);
                tm.cells.rows.forEach(row -> row.get(ci).setVisible(true));
                return;
            }
            if (isHidden(tm, ci)) {
                tm.cells.headers.get(ci).setVisible(false);
                tm.cells.rows.forEach(row -> row.get(ci).setVisible(false));
                return;
            }
            if (isRenderable(tm, ci, true)) {
                tm.cells.headers.get(ci).setVisible(true);
                tm.cells.rows.forEach(row -> row.get(ci).setVisible(true));
            } else {
                if (optional_onEditMustShowRowIdx == null) {
                    tm.cells.headers.get(ci).setVisible(false);
                    tm.cells.rows.forEach(row -> row.get(ci).setVisible(false));
                } else {
                    tm.cells.headers.get(ci).setVisible(true);
                    IntStream.range(0, tm.cells.rows.size())
                            .forEach(ri -> tm.cells.rows.get(ri).get(ci).setVisible(ri == optional_onEditMustShowRowIdx));
                }
            }
        });
    }

}
