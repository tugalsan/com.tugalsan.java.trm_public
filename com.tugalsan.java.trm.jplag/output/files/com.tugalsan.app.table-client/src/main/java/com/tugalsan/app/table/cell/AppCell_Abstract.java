package com.tugalsan.app.table.cell;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.gui.client.dom.*;
import com.tugalsan.api.list.client.TGS_ListSortUtils;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.TGC_SGWTCalller;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.sg.*;
import com.tugalsan.app.table.control.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.lib.rql.cfg.client.TGS_LibRqlCfgUtils;
import com.tugalsan.lib.rql.client.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;

abstract public class AppCell_Abstract extends Button {

    final private static TGC_Log d = TGC_Log.of(AppCell_Abstract.class);

    public TGS_LibRqlCol ct;
    public int rowIdx, colIdx;
    public AppModuleTable tm;

    public AppCell_Abstract(AppModuleTable tm, int rowIdx, int colIdx, TGS_LibRqlCol ct) {
        TGC_DOMUtils.setOverflowHidden(getElement());
        this.rowIdx = rowIdx;
        this.colIdx = colIdx;
        this.ct = ct;
        this.tm = tm;
        TGC_DOMUtils.setTextAlignLeft(getElement());
        addClickHandler(e -> {
            onClick(e.isShiftKeyDown(), e.isAltKeyDown(), e.isControlKeyDown(), e.isMetaKeyDown());
        });
        addKeyUpHandler(e -> {//TODO HOW SPACE ALSO WORKS WITHOUT DEFINING IT?
            if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                onClick(e.isShiftKeyDown(), e.isAltKeyDown(), e.isControlKeyDown(), e.isMetaKeyDown());
            }
        });
        addFocusHandler(e -> onFocused());
        addBlurHandler(e -> onLostFocus());
    }

    final public void setTextAlignLeft() {
        TGC_DOMUtils.setTextAlignLeft(getElement());
        isTextAllignLeft = true;
        isTextAllignRight = false;
        isTextAllignCenter = false;
    }

    public boolean isTextAlignLeft() {
        return isTextAllignLeft;
    }
    private boolean isTextAllignLeft = true;

    final public void setTextAlignRight() {
        TGC_DOMUtils.setTextAlignRight(getElement());
        isTextAllignLeft = false;
        isTextAllignRight = true;
        isTextAllignCenter = false;
    }

    public boolean isTextAlignRight() {
        return isTextAllignRight;
    }
    private boolean isTextAllignRight = false;

    public void setTextAlignCenter() {
        TGC_DOMUtils.setTextAlignCenter(getElement());
        isTextAllignLeft = false;
        isTextAllignCenter = true;
        isTextAllignRight = false;
    }

    public boolean isTextAlignCenter() {
        return isTextAllignCenter;
    }
    private boolean isTextAllignCenter = false;

    public void setHTML(CharSequence html) {
        super.setHTML(html.toString());
    }

    public void setText(CharSequence text) {
        setText(text.toString());
    }

    @Override//NEED STRING
    public void setText(String text) {//TODO WHY NOT ALWAYS ON HOVER
        d.ci("setText", text);
        super.setText(text);
        super.setTitle(text);//TOOLTIP
    }

    public boolean isHeader() {
        return rowIdx == AppCtrlCell.HEADER_ROW_IDX;
    }

    public boolean isRow() {
        return !isHeader();
    }

    final public void onClick(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        onFocused();
        if (isHeader()) {
            if (controlDown && shiftDown) {
                if (App.loginCard.userAdmin) {
                    onClick_showHeaderUpdate(shiftDown, altDown, controlDown, metaDown);
                } else {
                    d.ce("onClick", "HATA: Başlık düzenlemek için yetkili değilsiniz!");
                }
            } else if (controlDown) {
                if (tm.cells.isRowModifyVisible()) {
                    onClick_renderColumnsUpdate(shiftDown, altDown, controlDown, metaDown);
                } else {
                    onClick_sortTable(shiftDown, altDown, controlDown, metaDown);
                }
            } else if (shiftDown) {
                onClick_openSubTable();
            } else {
                onClick_showFilter(shiftDown, altDown, controlDown, metaDown);
            }
        } else {//isRow()
            if (tm.cells.isRowModifyVisible()) {//STEP 2 -> on PopUp
                if (controlDown && shiftDown) {
                    d.ce("onClick", "HATA: Satır düzenleme açık iken, Ctrl ve Shift combinasyonu için aksiyon tanımlanmamış!");
                } else if (controlDown) {
                    d.ce("onClick", "HATA: Satır düzenleme açık iken, Ctrl tuşu için aksiyon tanımlanmamış!");
                } else if (shiftDown) {
                    onClick_showSubTable();
                } else {
                    if (!App.loginCard.userAdmin && colIdx == 0) {
                        onClick_copyToClipboard();
                    } else {
                        _onClick_showCellUpdate(shiftDown, altDown, controlDown, metaDown);
                    }
                }
            } else {//STEP 1 -> showPanelRowModify
                if (controlDown && shiftDown) {
                    onClick_showRowMultiply(shiftDown, altDown, controlDown, metaDown);
                } else if (controlDown) {
                    if (!App.loginCard.userAdmin && colIdx == 0) {
                        onClick_copyToClipboard();
                    } else {
                        _onClick_showCellUpdate(shiftDown, altDown, controlDown, metaDown);
                    }
                } else if (shiftDown) {
                    onClick_showSubTable();
                } else {
                    onClick_showRowModify(shiftDown, altDown, controlDown, metaDown);
                }
            }
        }
    }

    private void onClick_renderColumnsUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        if (colIdx == 0) {
            d.ce("onClick_renderColumnsUpdate", "Görüntüleme değişikliği ilk kolon için yapılamaz!");
            return;
        }
        AppCtrlCellLayoutUtils.isRenderable_clear();
        var isRenderable = AppCtrlCellLayoutUtils.isRenderable(tm, colIdx, false);
        List<Integer> newRenderList = new ArrayList();
        newRenderList.addAll(tm.curTable.renderConfig_colIdx);
        if (isRenderable) {
            newRenderList.remove(Integer.valueOf(colIdx));//OBJECTIFY IS A MUST
        } else {
            newRenderList.add(colIdx);
        }
        TGS_ListSortUtils.sortInt(newRenderList);
        var curTableName = tm.curTable.nameSql;
        var val = TGS_StringUtils.cmn().toString(newRenderList, " ");
        var ref = TGS_LibRqlCfgUtils.PARAM_RENDER_CONFIG();
        TGC_SGWTCalller.async(new AppSGFConfigValueSet(curTableName, ref, val), r -> {
            if (r.getOutput_id() == null) {
                d.ce("onClick_renderColumnsUpdate", "HATA: Görüntüleme değişikliği yapılırken bir hata oluştu!");
                return;
            }
            tm.curTable.renderConfig_colIdx = newRenderList;
            AppCtrlCellHeaderUtils.refreshHeaderTitles(tm, false);
            d.cr("onClick_renderColumnsUpdate", "Görüntüleme değişikliği yapıldı. config.id = " + r.getOutput_id());
            d.cr("onClick_renderColumnsUpdate", "Ayarlar 2 dk sonra yeni sekmelerde de gözükebilecek.");
            tm.cells.popRowModify.getPop().setVisible(false);
            AppCtrlCellLayoutUtils.cells_visibility_refresh(tm, null);
            AppCtrlCellLayoutUtils.cells_layout_refresh("onClick_renderColumnsUpdate", tm);
        });
    }

    private void _onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
//        d.cr("_onClick_showCellUpdate", "#1");
        var cfg = App.userTableConfig.stream()
                .filter(_cfg -> _cfg.table.nameSql.equals(App.curTableName))
                .findAny().orElse(null);
//        d.cr("_onClick_showCellUpdate", "#2");
        if (cfg == null) {
            d.ce("_onClick_showCellUpdate", "cfg == null");
            return;
        }
//        d.cr("_onClick_showCellUpdate", "#3");
        if (cfg.editableDays == 0) {
            var tmp = d.infoEnable;
            d.infoEnable = true;
            d.ce("_onClick_showCellUpdate", "UYARI: Sayfa salt okunur.");
            d.infoEnable = tmp;
            onClick_copyToClipboard();
            return;
        }
//        d.cr("_onClick_showCellUpdate", "#4");
        onClick_showCellUpdate(shiftDown, altDown, controlDown, metaDown);
//        d.cr("_onClick_showCellUpdate", "#5");
    }

    abstract protected void onClick_copyToClipboard();

    private void onClick_openSubTable() {
        tm.subRecord.onClick_TableLinkOpen(this);
    }

    private void onClick_showSubTable() {
        tm.subRecord.onClick_TableLinkSniff(this);
    }

    abstract public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown);

    final public void onClick_showHeaderUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        if (this instanceof AppCell_STR cellStr) {
            if (tm.dbCfg.isAny()) {
                d.ce("onClick_showHeaderUpdate", "Hata: Mode açık iken başlık değiştirilemez!");
                return;
            }
            AppCtrlCellHeaderUtils.refreshHeaderTitle(tm, colIdx);
            tm.input.showBox(cellStr.getValueString(),
                    inputText -> AppCtrlCellHeaderUtils.executeHeaderUpdate(tm, inputText),
                    cellStr,
                    "<b>Kolon Başlığı:</b>"
            );
            return;
        }
        d.ce("onClick_showHeaderUpdate", "Hata: Not implemented!");
    }

    final public void onClick_showRowModify(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        tm.cells.popRowModify.onShowRow(rowIdx, true);
    }

    final public void onClick_showRowMultiply(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        tm.cells.setActiveCell(this, false);
        tm.operations.multiplyRow();
    }

    final public void onClick_showFilter(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        tm.filter.popMain.onSetVisibleCol.run(colIdx);
    }

    final public void onClick_sortTable(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        var profiles = tm.filter.popMain.profiles;
        var currentSortColIdx = IntStream.range(0, profiles.size()).filter(i -> profiles.get(i).rbSortBy).findAny().orElse(-1);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "currentSortColIdx", currentSortColIdx, "colIdx", colIdx);
        }
        if (currentSortColIdx == colIdx) {
            profiles.get(colIdx).cbAscending = !profiles.get(colIdx).cbAscending;
        } else {
            if (currentSortColIdx != -1) {
                profiles.get(currentSortColIdx).rbSortBy = false;
            }
            profiles.get(colIdx).rbSortBy = true;
        }
        tm.filter.popMain.onCloseAndApply.run();
    }

    public void onFocused() {
        tm.cells.setActiveCell(this, false);
    }

    public void onLostFocus() {
    }

    abstract public String getLog();

    abstract public void reset();
}
