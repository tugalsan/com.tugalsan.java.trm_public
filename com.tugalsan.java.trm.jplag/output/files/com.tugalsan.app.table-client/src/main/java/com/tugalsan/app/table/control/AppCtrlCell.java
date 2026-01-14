package com.tugalsan.app.table.control;

import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.gui.client.dom.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.app.table.pop.*;
import com.tugalsan.lib.file.client.*;
import java.util.*;
import java.util.stream.*;

public class AppCtrlCell {

    public AppCtrlCell(AppModuleTable tm) {
        this.tm = tm;
    }
    final private AppModuleTable tm;

    final private static TGC_Log d = TGC_Log.of(AppCtrlCell.class);
    final public static int HEADER_ROW_IDX = -1;

    public static int FILE_MENU_IDX_NONE() {
        return -1;
    }

    public static int FILE_MENU_IDX0_TMPL_SHOWLAST() {
        return 0;
    }

    public static int FILE_MENU_IDX1_TMPL_UPLOAD() {
        return 1;
    }

    public static int FILE_MENU_IDX2_TMPL_SHOWREV() {
        return 2;
    }

    public static int FILE_MENU_IDX3_SPC() {
        return 3;
    }

    public static int FILE_MENU_IDX4_FILE_SHOWLAST() {
        return 4;
    }

    public static int FILE_MENU_IDX5_FILE_UPLOAD() {
        return 5;
    }

    public static int FILE_MENU_IDX6_FILE_SHOWREV() {
        return 6;
    }

    public static int FILE_MENU_IDX7_FILE_DELREV() {
        return 7;
    }

    public int popFileOperationsIdx = AppCtrlCell.FILE_MENU_IDX_NONE();

    public List<TGS_SQLCellAbstract> restoreData;
    public long restoreRefId;

    private AppCell_Abstract active = null;
    public List<FocusWidget> headers;
    public List<List<FocusWidget>> rows;
    public List<Integer> colWidths;
    public boolean[] isColHidden = null;
    public AppPopEditCellLNGLINK popEditCellLNGLINK;
    public AppPopEditCellSTRLINK popEditCellSTRLINK;
    public TGC_PopLblYesNoListBox popFileOperations, popFileChooser, popRestoreConfirm;
    public TGC_PopLblYesNoTextArea popRowRestoreOperations;
    public TGC_LibFileUploadPop popFileUpload;
    public AppPopRowModify popRowModify;

    public void focusActiveCell() {
        TGC_FocusUtils.setFocusAfterGUIUpdate(getActiveCell());
    }

    public AppCell_Abstract getActiveCell() {
        return active;
    }

    public void setActiveCell(AppCell_Abstract cell, boolean focus) {
        if (active != null) {
            TGC_DOMUtils.setBorder(active.getElement(), 1, true, "gray");
            active = null;
        }
        if (cell == null) {
            return;
        }
        this.active = cell;
        TGC_DOMUtils.setBorder(active.getElement(), 1, false, "green");
        if (focus) {
            TGC_FocusUtils.setFocusAfterGUIUpdate(active);
        }
        d.ci("setActiveCell", active.getText());
    }

    public Integer getActiveColIdx() {
        if (active == null) {
            return null;
        }
        return active.colIdx;
    }

    public Integer getActiveRowIdx() {
        if (active == null) {
            return null;
        }
        return active.rowIdx;
    }

    public boolean isRowEnabled(int rowIdx) {
        if (rowIdx == -1) {
            return true;
        }
        return rows.get(rowIdx).get(1).isEnabled();
    }

    public Long getActiveRowId() {
        var rowIdx = getActiveRowIdx();
        if (rowIdx == null) {
            return null;
        }
        return AppCtrlCellRowUtils.getRowIdByIdx(this, rowIdx);
    }

    public int getRowSize() {
        return rows.size();
    }

    public int getColumnSize() {
        return headers.size();
    }

    public void setEnableDataCell(int ri, int ci, boolean enable) {
        rows.get(ri).get(ci).setEnabled(enable);
    }

    public void setEnableDataColumn(int ci, boolean enable) {
        IntStream.range(0, rows.size()).parallel().forEach(ri -> setEnableDataCell(ri, ci, enable));
    }

    public void setEnableDataRow(int ri, boolean enable) {
        IntStream.range(0, headers.size()).parallel().forEach(ci -> setEnableDataCell(ri, ci, enable));
    }

    public void setEnableDataTable(boolean enable) {
        IntStream.range(0, headers.size()).parallel().forEach(ci -> setEnableDataColumn(ci, enable));
    }

    public void setEnableHeader(int c, boolean enable) {
        headers.get(c).setEnabled(enable);
    }

    public void setEnableHeaders(boolean enable) {
        IntStream.range(0, headers.size()).parallel().forEach(ci -> setEnableHeader(ci, enable));
    }

    public AppCell_Abstract getPreferredLeftMostCell() {
        return (AppCell_Abstract) getPreferredActiveRow().get(getPreferredLeftMostCellColIdx());
    }

    public List<FocusWidget> getPreferredActiveRow() {
        var preferredCell = getPreferredActiveCell();
        if (preferredCell.rowIdx == -1) {
            return headers;
        }
        return rows.get(preferredCell.rowIdx);
    }

    public AppCell_Abstract getPreferredActiveCell() {
        var getPreferredLeftMostCellColIdx = getPreferredLeftMostCellColIdx();
        d.ci("getPreferredActiveCell", "active == null", active == null);
        d.ci("getPreferredActiveCell", "getPreferredLeftMostCellColIdx", getPreferredLeftMostCellColIdx);
        d.ci("getPreferredActiveCell", "headers.size", headers.size());
        return active == null ? (AppCell_Abstract) headers.get(getPreferredLeftMostCellColIdx) : active;
    }

    private int getPreferredLeftMostCellColIdx() {
        if (preferredLeftMostCellIdx != -1) {
            return preferredLeftMostCellIdx;
        }
        if (headers.size() == 1) {
            preferredLeftMostCellIdx = 0;
            return preferredLeftMostCellIdx;
        }
        TGS_ListSortedDistinct2<Integer, Integer> map = new TGS_ListSortedDistinct2();

        headers.forEach(fw -> {
            var cell = (AppCell_Abstract) fw;
            if (isColHidden != null && isColHidden[cell.colIdx]) {
                return;
            }
            var left = TGC_DOMUtils.getLeft(cell.getElement());
//            if (left == null) {//JUST CRASH
//                return;
//            }
            map.add(left, cell.colIdx);
        });
        preferredLeftMostCellIdx = map.getValue(1);
        return preferredLeftMostCellIdx;
    }
    private int preferredLeftMostCellIdx = -1;

    public boolean isRowModifyVisible() {
        return tm.cells.popRowModify.getPop().isVisible();
    }
}
