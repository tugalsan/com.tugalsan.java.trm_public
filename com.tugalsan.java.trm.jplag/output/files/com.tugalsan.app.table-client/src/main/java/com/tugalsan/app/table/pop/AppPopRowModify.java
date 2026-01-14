package com.tugalsan.app.table.pop;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.gui.client.click.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.dom.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.key.*;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.theme.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.gui.client.widget.abs.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.shape.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.control.utils.AppCtrlCellLayoutUtils;
import com.tugalsan.app.table.sg.row.*;
import com.tugalsan.lib.boot.client.*;
import java.util.*;
import java.util.stream.*;

public class AppPopRowModify implements TGC_PopInterface {

    final private static TGC_Log d = TGC_Log.of(AppPopRowModify.class);
    public boolean rememberLastBolum = false;

    @Override
    public void createWidgets() {
        tpContent = new TGC_TabPanel();
        btnCancel = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CANCEL_CIRCLE(), "Kapat");
        btnSubRecords = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_LINK(), "Alt Kayıtları");
        btnRowPrev = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_POINT_UP(), "Yukarı");
        btnRowNext = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_POINT_DOWN(), "Aşağı");
        btnDelete = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_BIN(), "Sil");
        image = new Image();
    }
    public TGC_TabPanel tpContent;
    public PushButton btnCancel, btnRowPrev, btnRowNext, btnSubRecords, btnDelete;
    private Image image;

    @Override
    public void createPops() {
        popDelete = new TGC_PopLblYesNoTextArea(
                new TGC_Dimension(400, 400, true),
                "<b>Silme kontrolü:</b>", "Sil", "İptal",
                p -> TGC_SGWTCalller.async(new AppSGFRowRemove(tm.dbCfg, tm.curTable, tm.cells.getActiveRowId()), r -> {
                    p.getPop().setVisible(false);
                    onClose.run(false);
                    tm.page.update(null, false, true);
                    if (!r.getOutput_result()) {
                        d.ce("createPops", "HATA: " + tm.cells.getActiveRowId() + " silme işlemi başarısız");
                        return;
                    }
                    d.cr("createPops", "id=" + tm.cells.getActiveRowId() + " silindi");
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnDelete);
                }),
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnDelete);
                },
                null
        );
        popDelete.getPop().setVisible_focus = popDelete.btnEsc;

        //TGC_PanelLayoutUtils.MAX_GRID_WIDTH
        popSubRecords = new TGC_PopLblYesNoListBox(
                new TGC_Dimension(null, 400, true),
                TGS_ListUtils.of()/*needed there!*/,
                "<b>Bağlı Tabloda, Kayıt Önizleme</b>", "Şeçili Kaydı Düzenle", "Kapat",
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnSubRecords);
                    var si = p.listBox.getSelectedIndex();
                    if (si == -1) {
                        d.ce("popSubRecords.OK", "HATA: Listeden bir kayıt seçmeliydiniz!");
                        return;
                    }
                    var itemText = p.listBoxContent.get(si).trim();
                    var subRecords_validItemPrefixTrimmed = subRecords_validItemPrefix.trim();
                    if (!itemText.startsWith(subRecords_validItemPrefixTrimmed)) {
                        d.ce("popSubRecords.OK1", "HATA: Listeden UYGUN bir kayıt seçiniz!", itemText, "BEKLENTİ", subRecords_validItemPrefixTrimmed);
                        return;
                    }
                    var text_phase1 = itemText.substring(subRecords_validItemPrefixTrimmed.length());
                    var delimIdx = text_phase1.indexOf("}");
                    if (delimIdx == -1) {
                        d.ce("popSubRecords.OK2", "HATA: delimIdx != -1", itemText, text_phase1, delimIdx);
                        return;
                    }
                    var text_phase2 = text_phase1.substring(0, delimIdx);
                    var delimIdx2 = text_phase1.indexOf("/");
                    if (delimIdx2 == -1) {
                        d.ce("popSubRecords.OK3", "HATA: delimIdx2 != -1", text_phase2);
                        return;
                    }
                    var tablename = text_phase2.substring(0, delimIdx2);
                    var id = text_phase2.substring(delimIdx2 + 1);

                    var pack = AppModuleTableUtils.getTableIfAllowed_returnErrorAndTable(tablename);
                    if (pack.value0 != null) {
                        d.ce("popSubRecords.OK4", "HATA: " + pack.value0);
                        return;
                    }
                    AppModuleTableUtils.openNewTableModify(pack.value1, id);
                },
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnSubRecords);
                },
                p -> {
                    var idx = p.listBox.getSelectedIndex();
                    var si = p.listBox.getSelectedIndex();
                    if (si == -1) {
                        d.ce("createPops.popSubRecords", "HATA: Satır seçilmedi", "si", si);
                        return;
                    }
                    var val = p.listBox.getSelectedItemText();
                    if (!(val.startsWith(subRecords_validItemPrefix) || (" " + val).startsWith(subRecords_validItemPrefix))) {
                        d.ci("createPops.popSubRecords", "HATA: Satır verisi hatası", subRecords_validItemPrefix, val);
                        return;
                    }
                    var idx_dotdot = val.indexOf(":");
                    var idx_slash = val.indexOf("/", idx_dotdot + 1);
                    var idx_close = val.indexOf("}", idx_slash + 1);
                    var tn = val.substring(idx_dotdot + 1, idx_slash);
                    var idStr = val.substring(idx_slash + 1, idx_close);
                    if (!TGS_CastUtils.isLong(idStr)) {
                        d.ce("createPops.popSubRecords", "HATA: id tam sayıya dönüştürülemiyor.", idStr);
                        return;
                    }
                    var id = TGS_CastUtils.toLong(idStr).orElseThrow();
                    var table = App.tbl_mayThrow(tn);
                    var tnvn = table == null ? "tablenameNotFound" : table.nameReadable;
                    var curTableName = tm.curTable.nameSql;
                    var pe = AppModuleTableUtils.getSQLLinkIfAllowed(tm, curTableName, tn, id, reply -> {
                        var errText = reply.getOutput_cell().errTxt;
                        var linkText = reply.getOutput_cell().linkText;
                        if (errText != null) {
                            d.ce("getSQLLinkIfAllowed", "HATA: Satır hatalı çekildi hatası", errText, tn, id);
                            return;
                        }
                        popSubRecords.listBox.setItemText(idx, " - " + tnvn + " - #" + id + " - " + linkText);
                    });
                },
                null
        );
        popSubRecords.getPop().setVisible_focus = popSubRecords.btnEsc;
    }
    private TGC_PopLblYesNoTextArea popDelete;
    private TGC_PopLblYesNoListBox popSubRecords;

    @Override
    public void configInit() {
        image.setStyleName(TGC_Widget.class.getSimpleName());
        image.addStyleName("AppModule_Image_BorderRadiusSmall");
        imageHandler = new AppPopImageHandler(image);
        cols = tm.curTable.columns.size();
        buttonsWidgetsBackup = TGS_ListUtils.of();
        rowWidgetsRectBackup = TGS_ListUtils.of();
        headersWidgetsRectBackup = TGS_ListUtils.of();

        popDelete.setEditable(false);

        var cfg = App.userTableConfig.stream()
                .filter(_cfg -> _cfg.table.nameSql.equals(App.curTableName))
                .findAny().orElse(null);
        if (cfg == null) {
            d.ce("configInit", "cfg == null");
            btnDelete.setEnabled(false);
            return;
        }
        if (cfg.editableDays == 0) {
            btnDelete.setEnabled(false);
        }
    }
    public AppPopImageHandler imageHandler;
    private int cols;
    private List<Widget> buttonsWidgetsBackup;
    private List<TGS_ShapeRectangle<Integer>> rowWidgetsRectBackup;
    private List<TGS_ShapeRectangle<Integer>> headersWidgetsRectBackup;

    @Override
    public void configActions() {
        TGC_ClickUtils.add(btnCancel, () -> onClose.run(true));
        TGC_ClickUtils.add(btnRowPrev, onPrevClick);
        TGC_ClickUtils.add(btnRowNext, onNextClick);
        TGC_ClickUtils.add(btnSubRecords, onSubRecords);
        TGC_ClickUtils.add(btnDelete, onDelete);

        TGC_KeyUtils.add(btnCancel, () -> onClose.run(true), () -> onClose.run(true));
        //tm.del.btn -> see TMControllerRowDelete
        //tm.op.btn -> see TMControllerTableOperation
        //tm.report.btn -> see TMControllerReport
        TGC_KeyUtils.add(btnSubRecords, onSubRecords, () -> onClose.run(true));
        TGC_KeyUtils.add(btnRowPrev, onPrevClick, () -> onClose.run(true));
        TGC_KeyUtils.add(btnRowNext, onNextClick, () -> onClose.run(true));
        TGC_KeyUtils.add(btnDelete, onDelete, () -> onClose.run(true));

        TGC_KeyUtils.addDel(onDelete, btnCancel, btnDelete, tm.operations.btn, tm.report.btn, btnRowPrev, btnRowNext);
        TGC_KeyUtils.addCtrlEnter(() -> onClose.run(true), btnCancel, btnDelete, tm.operations.btn, tm.report.btn, btnRowPrev, btnRowNext);
        TGC_KeyUtils.addCtrlUp(onPrevClick, btnCancel, btnDelete, tm.operations.btn, tm.report.btn, btnRowPrev, btnRowNext);
        TGC_KeyUtils.addCtrlDown(onNextClick, btnCancel, btnDelete, tm.operations.btn, tm.report.btn, btnRowPrev, btnRowNext);
        TGC_KeyUtils.addCtrlLeft(() -> onFocusPrevTab(), btnCancel, btnDelete, tm.operations.btn, tm.report.btn, btnRowPrev, btnRowNext);
        TGC_KeyUtils.addCtrlRight(() -> onFocusNextTab(), btnCancel, btnDelete, tm.operations.btn, tm.report.btn, btnRowPrev, btnRowNext);
    }

    @Override
    public void configFocus() {
        TGC_FocusUtils.addKeyDown(btnCancel, nativeKeyCode -> {
            var focusSides = new TGS_FocusSides4(btnCancel, btnDelete, btnSubRecords, getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btnCancel, focusSides, nativeKeyCode);
        });
        TGC_FocusUtils.addKeyDown(btnSubRecords, nativeKeyCode -> {
            var focusSides = new TGS_FocusSides4(btnDelete, tm.operations.btn, btnSubRecords, getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btnSubRecords, focusSides, nativeKeyCode);
        });
        TGC_FocusUtils.addKeyDown(btnDelete, nativeKeyCode -> {
            if (AppPopRowModify.this.getPop().isVisible()) {
                var delFocusSides = new TGS_FocusSides4(btnCancel, btnSubRecords, null, getPreferredActiveCell());
                TGC_FocusUtils.focusSide(btnDelete, delFocusSides, nativeKeyCode);
            } else {
                if (nativeKeyCode == KeyCodes.KEY_DOWN) {
                    TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getPreferredActiveCell());
                }
            }
        });
        TGC_FocusUtils.addKeyDown(tm.operations.btn, nativeKeyCode -> {
            if (AppPopRowModify.this.getPop().isVisible()) {
                var opFocusSides = new TGS_FocusSides4(btnDelete, tm.report.btn, null, getPreferredActiveCell());
                TGC_FocusUtils.focusSide(tm.operations.btn, opFocusSides, nativeKeyCode);
            } else {
                if (nativeKeyCode == KeyCodes.KEY_DOWN) {
                    TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getPreferredActiveCell());
                }
            }
        });
        TGC_FocusUtils.addKeyDown(tm.report.btn, nativeKeyCode -> {
            if (AppPopRowModify.this.getPop().isVisible()) {
                var reportFocusSides = new TGS_FocusSides4(tm.operations.btn, btnRowPrev, null, getPreferredActiveCell());
                TGC_FocusUtils.focusSide(tm.report.btn, reportFocusSides, nativeKeyCode);
            } else {
                if (nativeKeyCode == KeyCodes.KEY_DOWN) {
                    TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getPreferredActiveCell());
                }
            }
        });
        TGC_FocusUtils.addKeyDown(btnRowPrev, nativeKeyCode -> {
            var prevFocusSides = new TGS_FocusSides4(tm.report.btn, btnRowNext, null, getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btnRowPrev, prevFocusSides, nativeKeyCode);
        });
        TGC_FocusUtils.addKeyDown(btnRowNext, nativeKeyCode -> {
            var nextFocusSides = new TGS_FocusSides4(btnRowPrev, null, null, getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btnRowNext, nextFocusSides, nativeKeyCode);
        });
    }

    public AppCell_Abstract getPreferredActiveCell() {
        var tabIndex = tpContent.getSelectedTabIndex();
        d.ci("getPreferredActiveCell", "tabIndex", tabIndex);
        if (tabIndex == -1) {
            return null;
        }
        var tabItems = onShowRow_tabsOrder.get(tabIndex).value1;
        var preferred = tabItems.isEmpty() ? null : (AppCell_Abstract) tabItems.get(0).value1;
        if (preferred == null) {
            d.ci("getPreferredActiveCell", "as", preferred);
        } else {
            d.ci("getPreferredActiveCell", "as", preferred.getText(), preferred.rowIdx, preferred.colIdx);
        }
        return preferred;
    }

    @Override
    public void configLayout() {
        btnCancel.addStyleName("AppModuleTable_btn");
        btnDelete.addStyleName("AppModuleTable_btn");
        btnSubRecords.addStyleName("AppModuleTable_btn");
        btnRowPrev.addStyleName("AppModuleTable_btn");
        btnRowNext.addStyleName("AppModuleTable_btn");

        var dim = new TGC_Dimension(990, 500, false);
        content = TGC_PanelAbsoluteUtils.create(dim);//#101010
        var height = 30;
        TGC_PanelAbsoluteUtils.setWidget(content, btnCancel, TGS_ShapeRectangle.of(10, 10, 100, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnDelete, TGS_ShapeRectangle.of(120, 10, 100, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSubRecords, TGS_ShapeRectangle.of(230, 10, 100, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnRowPrev, TGS_ShapeRectangle.of(820, 10, 78, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnRowNext, TGS_ShapeRectangle.of(908, 10, 77, height));
        TGC_PanelAbsoluteUtils.setWidget(content, tpContent.getWidget(), TGS_ShapeRectangle.of(10, 40, 977, 455));

        if (tm.dbCfg.isAny()) {
            btnSubRecords.setVisible(false);
        }

        //ADD LAST
        TGC_PanelAbsoluteUtils.setWidget(content, image, TGS_ShapeRectangle.of(721, 1, 96, 72));

        panelPopup = new TGC_Pop(
                content,
                dim, null
        );
    }
    private AbsolutePanel content;
    private TGC_Pop panelPopup;

    @Override
    public TGC_Pop getPop() {
        return panelPopup;
    }

    public AppPopRowModify(AppModuleTable tm) {
        this.tm = tm;
        createWidgets();
        createPops();
        configInit();
        configActions();
        configFocus();
        configLayout();
    }
    private AppModuleTable tm;

    final public TGS_FuncMTU_In1<Integer> onreloadImageByRow = rowIdx -> {
        var cell = ((AppCell_Abstract) tm.cells.rows.get(rowIdx).get(0));
        var valueLong = TGS_CastUtils.toLong(cell.getText()).orElse(null);
        if (valueLong == null) {
            d.ci("reloadImageByRow", valueLong == null, cell.getText());
            return;
        }
        var curTableName = tm.curTable.nameSql;
        d.ci("reloadImageByRow", "row", rowIdx, "tm.curTable", curTableName, "valueLong as id", valueLong);
        if (!tm.dbCfg.isAny()) {
            imageHandler.reloadImageById(curTableName, valueLong);
        }
    };

    private List<FocusWidget> rowWidgets;
    private List<FocusWidget> headersWidgets;
    private List<TGS_Tuple2<AbsolutePanel, TGC_ScrollPanel>> tabs;

    public void onFocusNextTab() {
        var selectedTabIndex = tpContent.getSelectedTabIndex();
        var tabIndexSize = tabs.size();
        if (selectedTabIndex < tabIndexSize - 1) {
            tpContent.show(selectedTabIndex + 1);
            var order = onShowRow_tabsOrder.get(selectedTabIndex + 1).value1;
            if (order.isEmpty()) {
                return;
            }
            TGC_FocusUtils.setFocusAfterGUIUpdate(order.get(0).value1);
        }
    }

    public void onFocusPrevTab() {
        var selectedTabIndex = tpContent.getSelectedTabIndex();
        if (selectedTabIndex > 0) {
            tpContent.show(selectedTabIndex - 1);
            var order = onShowRow_tabsOrder.get(selectedTabIndex - 1).value1;
            if (order.isEmpty()) {
                return;
            }
            TGC_FocusUtils.setFocusAfterGUIUpdate(order.get(0).value1);
        }
    }

    private void onShowRow_fill_buttonsWidgetsBackup() {
        d.ci("onShowRow_fill_buttonsWidgetsBackup", "backup buttons...");
        buttonsWidgetsBackup.clear();
        buttonsWidgetsBackup.add(tm.operations.btn);
        buttonsWidgetsBackup.add(tm.report.btn);
    }

    private void onShowRow_fill_rowWidgetsRectBackup() {
        d.ci("onShowRow_fill_rowWidgetsRectBackup", "backup row...");
        rowWidgetsRectBackup.clear();
        IntStream.range(0, cols).forEachOrdered(ci -> {
            if (AppCtrlCellLayoutUtils.isHidden(tm, ci)) {
                rowWidgetsRectBackup.add(rectNull);
                return;
            }
            rowWidgetsRectBackup.add(TGC_DOMUtils.getRect(rowWidgets.get(ci)));
        });
    }

    private void onShowRow_fill_headersWidgetsRectBackup() {
        d.ci("onShowRow_fill_headersWidgetsRectBackup", "backup header...");
        headersWidgetsRectBackup.clear();
        IntStream.range(0, cols).forEachOrdered(ci -> {
            if (AppCtrlCellLayoutUtils.isHidden(tm, ci)) {
                headersWidgetsRectBackup.add(rectNull);
                return;
            }
            headersWidgetsRectBackup.add(TGC_DOMUtils.getRect(headersWidgets.get(ci)));
        });
    }

    private void onShowRow_place2pop_buttonsWidgets() {
        d.ci("onShowRow_place2pop_buttonsWidgets", "setting buttons...");
        var buttonsWidgetPop_gap = 5;
        var buttonsWidgetPop_x = TGC_DOMUtils.getLeft(btnSubRecords.getElement()) + TGC_DOMUtils.getWidth(btnSubRecords.getElement()) + buttonsWidgetPop_gap;
        var buttonsWidgetPop_y = 10;
        var buttonsWidgetPop_width = 100;
        var buttonsWidgetPop_height = 30;
        d.ci("onShowRow_place2panel_buttonsWidget", "setting buttons.for");
        for (var i = 0; i < buttonsWidgetsBackup.size(); i++) {
            var widget = buttonsWidgetsBackup.get(i);
            d.ci("onShowRow_place2panel_buttonsWidget", "setting buttons.for", i, "setWidget");
            TGC_LibBootGUIBody.buttonHolderLeft.add(widget);
            TGC_PanelAbsoluteUtils.setWidget(content, widget, TGS_ShapeRectangle.of(buttonsWidgetPop_x, buttonsWidgetPop_y, buttonsWidgetPop_width, buttonsWidgetPop_height));
            d.ci("onShowRow_place2panel_buttonsWidget", "setting buttons.for", i, "calc");
            buttonsWidgetPop_x += buttonsWidgetPop_width + buttonsWidgetPop_gap;
        }
    }

    private void onShowRow_place2pop_headerAndRowWidgets_placeId() {
        TGC_PanelAbsoluteUtils.setWidget(content, headersWidgets.get(tm.curTable.tableOrder[0]), TGS_ShapeRectangle.of(820, 40, 95, 32));
        TGC_PanelAbsoluteUtils.setWidget(content, rowWidgets.get(tm.curTable.tableOrder[0]), TGS_ShapeRectangle.of(915, 40, 70, 32));
    }

    private void onShowRow_place2pop_headerAndRowWidgets_tabsOrder() {
        d.ci("onShowRow_place2pop_headerAndRowWidgets_planOrder", "init");
        onShowRow_tabsOrder.clear();
        String currentGroupName = null;
        List<TGS_Tuple2<AppCell_Abstract, AppCell_Abstract>> currentTabWidgets = null;
        for (var i = 1; i < tm.curTable.tableOrder.length; i++) {
            if (AppCtrlCellLayoutUtils.isHidden(tm, tm.curTable.tableOrder[i])) {
                continue;
            }
            var widgetGroupName = tm.curTable.tableOrderGroups[i];
            if (!Objects.equals(currentGroupName, widgetGroupName)) {
                currentGroupName = widgetGroupName;
                currentTabWidgets = TGS_ListUtils.of();
                onShowRow_tabsOrder.add(new TGS_Tuple2(currentGroupName, currentTabWidgets));
            }
            var headerWidget = (AppCell_Abstract) headersWidgets.get(tm.curTable.tableOrder[i]);
            var rowWidget = (AppCell_Abstract) rowWidgets.get(tm.curTable.tableOrder[i]);
            currentTabWidgets.add(new TGS_Tuple2(headerWidget, rowWidget));
        }
    }
    final public List<TGS_Tuple2<String, List<TGS_Tuple2<AppCell_Abstract, AppCell_Abstract>>>> onShowRow_tabsOrder = TGS_ListUtils.of();

    private void onShowRow_place2pop_headerAndRowWidgets_prepareTabs() {
        if (tabs == null) {
            tabs = TGS_ListUtils.of();
        }
        tabs.clear();
        tpContent.clear();
        onShowRow_tabsOrder.stream().forEachOrdered(tabOrder -> {
            var tab_scroll = new TGC_ScrollPanel(dimScrollPanel);
            var tab = TGC_PanelAbsoluteUtils.wrap(tab_scroll);
            tpContent.add(tab, tabOrder.value0, null);
            tabs.add(new TGS_Tuple2(tab, tab_scroll));
        });
        IntStream.range(0, tabs.size()).forEachOrdered(i -> {
            tpContent.widget.getTabBar().getTab(i).addKeyUpHandler(ke -> {
                var nativeKeyCode = ke.getNativeKeyCode();
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_LEFT:
                        onFocusPrevTab();
                        break;
                    case KeyCodes.KEY_RIGHT:
                        onFocusNextTab();
                        break;
                    case KeyCodes.KEY_UP:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(btnCancel);
                        break;
                    case KeyCodes.KEY_DOWN:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(getPreferredActiveCell());
                        break;
                }
            });
        });
    }
    private TGC_Dimension dimScrollPanel = new TGC_Dimension(970, 418, false);

    private void onShowRow_place2pop_headerAndRowWidgets_fillTabs() {
        var heightGap = 1;
        var heightStep_large = 48;
        var heightStep_small = 24;
        var headerWidgetsPop_x = 0;
        var headerWidgetsPop_width = 238;
        var rowWidgetsPop_x = headerWidgetsPop_width;
        var rowWidgetsPop_width_large = 720;
        var rowWidgetsPop_width_small = 81;
        var colGap = headerWidgetsPop_width + rowWidgetsPop_width_small;
        for (var tabI = 0; tabI < onShowRow_tabsOrder.size(); tabI++) {
            var heightOffset = 0;
            var tabOrder = onShowRow_tabsOrder.get(tabI).value1;
            var tab_scroll = tabs.get(tabI).value1;
            var colNrFull = new boolean[]{false, false, false};
            for (var rowWidgetsI = 0; rowWidgetsI < tabOrder.size(); rowWidgetsI++) {
                var headerWidget = tabOrder.get(rowWidgetsI).value0;
                var rowWidget = tabOrder.get(rowWidgetsI).value1;
                if (rowWidget.isTextAlignLeft() && !Objects.equals(rowWidget.ct.getType(), TGS_SQLColTypedUtils.TYPE_STRFILE())) {
                    if (colNrFull[0] && colNrFull[1] && colNrFull[2]) {
                    } else if (!colNrFull[0] && !colNrFull[1] && !colNrFull[2]) {
                    } else {
                        heightOffset += heightStep_small;
                    }
                    tab_scroll.addWidget_enlargeContentSize(headerWidget, headerWidgetsPop_x, heightGap + heightOffset, headerWidgetsPop_width, heightStep_large);
                    tab_scroll.addWidget_enlargeContentSize(rowWidget, rowWidgetsPop_x, heightGap + heightOffset, rowWidgetsPop_width_large, heightStep_large);
                    IntStream.range(0, 3).forEachOrdered(i -> colNrFull[i] = true);
                    heightOffset += heightStep_large;
                } else {
                    if (!colNrFull[0] && !colNrFull[1] && !colNrFull[2]) {
                        var rowWidgetsPop_x_byCol = rowWidgetsPop_x;
                        var headerWidgetsPop_x_byCol = headerWidgetsPop_x;
                        tab_scroll.addWidget_enlargeContentSize(headerWidget, headerWidgetsPop_x_byCol, heightGap + heightOffset, headerWidgetsPop_width, heightStep_small);
                        tab_scroll.addWidget_enlargeContentSize(rowWidget, rowWidgetsPop_x_byCol, heightGap + heightOffset, rowWidgetsPop_width_small, heightStep_small);
                        IntStream.range(0, 3).forEachOrdered(i -> colNrFull[i] = i == 0);
                    } else if (colNrFull[0] && !colNrFull[1] && !colNrFull[2]) {
                        var rowWidgetsPop_x_byCol = rowWidgetsPop_x + colGap;
                        var headerWidgetsPop_x_byCol = headerWidgetsPop_x + colGap;
                        tab_scroll.addWidget_enlargeContentSize(headerWidget, headerWidgetsPop_x_byCol, heightGap + heightOffset, headerWidgetsPop_width, heightStep_small);
                        tab_scroll.addWidget_enlargeContentSize(rowWidget, rowWidgetsPop_x_byCol, heightGap + heightOffset, rowWidgetsPop_width_small, heightStep_small);
                        IntStream.range(0, 3).forEachOrdered(i -> colNrFull[i] = i != 2);
                    } else if (colNrFull[0] && colNrFull[1] && !colNrFull[2]) {
                        var rowWidgetsPop_x_byCol = rowWidgetsPop_x + colGap * 2;
                        var headerWidgetsPop_x_byCol = headerWidgetsPop_x + colGap * 2;
                        tab_scroll.addWidget_enlargeContentSize(headerWidget, headerWidgetsPop_x_byCol, heightGap + heightOffset, headerWidgetsPop_width, heightStep_small);
                        tab_scroll.addWidget_enlargeContentSize(rowWidget, rowWidgetsPop_x_byCol, heightGap + heightOffset, rowWidgetsPop_width_small, heightStep_small);
                        IntStream.range(0, 3).forEachOrdered(i -> colNrFull[i] = true);
                        heightOffset += heightStep_small;
                    } else if (colNrFull[0] && colNrFull[1] && colNrFull[2]) {
                        var rowWidgetsPop_x_byCol = rowWidgetsPop_x;
                        var headerWidgetsPop_x_byCol = headerWidgetsPop_x;
                        tab_scroll.addWidget_enlargeContentSize(headerWidget, headerWidgetsPop_x_byCol, heightGap + heightOffset, headerWidgetsPop_width, heightStep_small);
                        tab_scroll.addWidget_enlargeContentSize(rowWidget, rowWidgetsPop_x_byCol, heightGap + heightOffset, rowWidgetsPop_width_small, heightStep_small);
                        IntStream.range(0, 3).forEachOrdered(i -> colNrFull[i] = i == 0);
                    }
                }
            }
        }
    }

    final public void onShowRow(int rowIdx, boolean focus) {
        AppCtrlCellLayoutUtils.cells_visibility_refresh(tm, rowIdx);
        onShowRow_activeRowIdx = rowIdx;
        if (rowIdx == -1) {
            d.ce("setVisible", "ERROR: row==-1 detected");
            return;
        }
        rowWidgets = tm.cells.rows.get(rowIdx);
        headersWidgets = tm.cells.headers;

        onShowRow_fill_buttonsWidgetsBackup();
        onShowRow_fill_rowWidgetsRectBackup();
        onShowRow_fill_headersWidgetsRectBackup();
        onShowRow_place2pop_buttonsWidgets();
        onShowRow_place2pop_headerAndRowWidgets_placeId();
        onShowRow_place2pop_headerAndRowWidgets_tabsOrder();
        onShowRow_place2pop_headerAndRowWidgets_prepareTabs();
        onShowRow_place2pop_headerAndRowWidgets_fillTabs();

        if (rememberLastBolum) {
            tpContent.show(onClose_lastTabIndex == -1 ? 0 : onClose_lastTabIndex);
        } else {
            tpContent.show(0);
        }

        d.ci("onShowRow", "setVisibleTrue...");
        getPop().setVisible(true);

        d.ci("onShowRow", "onreloadImageByRow...");
        onreloadImageByRow.run(rowIdx);

        if (focus) {
            d.ci("onShowRow", "setFocusAfterGUIUpdate...");
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnCancel);
        }
    }
    final private TGS_ShapeRectangle rectNull = TGS_ShapeRectangle.of();

    public int get_onShowRow_activeRowIdx() {
        return onShowRow_activeRowIdx;
    }
    private int onShowRow_activeRowIdx = -1;

    private int onClose_lastTabIndex = -1;
    final public TGS_FuncMTU_In1<Boolean> onClose = focus -> {
//        AppCtrlCellLayoutUtils.cells_visibility_refresh(tm, null);
//        AppCtrlCellLayoutUtils.cells_layout_refresh(tm);
        if (!getPop().isVisible()) {
            return;
        }
        d.ci("onClose", "getting tab idx...");
        onClose_lastTabIndex = tpContent.getSelectedTabIndex();
        d.ci("onClose", "onClose_lastTabIndex", onClose_lastTabIndex);

        buttonsWidgetsBackup.forEach(w -> {
            TGC_LibBootGUIBody.buttonHolderLeft.add(w);
        });
        d.ci("closeWindow", "forEveryCols", "isColumnHidden");
        IntStream.range(0, cols).forEach(ci -> {
            if (AppCtrlCellLayoutUtils.isHidden(tm, ci)) {
                return;
            }
            TGC_PanelAbsoluteUtils.setWidget(tm.content, headersWidgets.get(ci), headersWidgetsRectBackup.get(ci));
            TGC_PanelAbsoluteUtils.setWidget(tm.content, rowWidgets.get(ci), rowWidgetsRectBackup.get(ci));
        });
        d.ci("onClose", "hiding pop...");
        getPop().setVisible(false);
        AppCtrlCellLayoutUtils.cells_visibility_refresh(tm, null);
        AppCtrlCellLayoutUtils.cells_layout_refresh("onClose", tm);
        if (focus) {
            d.ci("onClose", "focusing preffered cell");
            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getPreferredLeftMostCell());
        }
        d.ci("onClose", "fin");
    };

    final public TGS_FuncMTU onPrevClick = () -> {
        if (onShowRow_activeRowIdx == 0) {//ROW 0
            if (tm.page.isFirst()) {//PAGEFIRST
                d.ce("btnPrevClick", "UYARI: Zaten ilk kayıttasınız.");
                onClose.run(true);
                return;
            } else {//PAGEELSE
                tm.page.onPagePrev();
                onClose.run(false);
                tm.cells.setActiveCell((AppCell_Abstract) tm.cells.rows.get(tm.cells.getRowSize() - 1).get(1), false);
                onShowRow(tm.cells.getActiveRowIdx(), false);
                TGC_FocusUtils.setFocusAfterGUIUpdate(btnRowPrev);
                return;
            }
        }
        {//ROWSAFE
            onClose.run(false);
            tm.cells.setActiveCell((AppCell_Abstract) tm.cells.rows.get(onShowRow_activeRowIdx - 1).get(1), false);
            onShowRow(tm.cells.getActiveRowIdx(), false);
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnRowPrev);
        }
    };

    final public TGS_FuncMTU onNextClick = () -> {
        if (onShowRow_activeRowIdx == tm.cells.getRowSize() - 1) {//ROWMAX
            if (tm.page.isLast()) {//PAGEMAX
                d.ce("btnNextClick", "UYARI: Zaten son kayıttasınız.");
                onClose.run(true);
                return;
            } else {//PAGEELSE
                tm.page.onPageNext();
                onClose.run(false);
                tm.cells.setActiveCell((AppCell_Abstract) tm.cells.rows.get(0).get(1), false);
                onShowRow(tm.cells.getActiveRowIdx(), false);
                TGC_FocusUtils.setFocusAfterGUIUpdate(btnRowNext);
                return;
            }
        }
        {//ROWSAFE
            var nextRowIdx = onShowRow_activeRowIdx + 1;
            if (!tm.cells.isRowEnabled(nextRowIdx)) {
                d.ce("btnNextClick", "UYARI: Zaten son kayıttasınız.");
                onClose.run(true);
                return;
            }//CELLSAFE
            onClose.run(false);
            tm.cells.setActiveCell((AppCell_Abstract) tm.cells.rows.get(nextRowIdx).get(1), false);
            onShowRow(tm.cells.getActiveRowIdx(), false);
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnRowNext);
        }
    };

    public TGS_FuncMTU onDelete = () -> {
        if (tm.cells.getActiveCell() == null || tm.cells.getActiveColIdx() < 0) {
            d.ce("onDelete", "HATA: Önce silmek istediğiniz satırdan bir hücre seçiniz!");
            return;
        }
        if (tm.cells.getActiveRowId() < 1L) {
            d.ce("onDelete", "HATA: Satır id 1 den küçük olamaz hatası");
            return;
        }
        popDelete.btnExe.setVisible(false);
        popDelete.label.setText("id=" + tm.cells.getActiveRowId() + " satırını silmek istediğinizden emin misiniz?");
        popDelete.textArea.setText("Silme kontrolü başlatıldı, lütfen bekleyiniz...\n\nBu mesaj uzun süre takılı kalırsa, kullanıcı algılanmamış olabilir:\n- İptal'e basın; tekrar Sil'e basın.\n- Çalışmaz ise, kullanıcı girişinizi kontrol ediniz.");
        popDelete.getPop().setVisible_beCeneteredAt(btnDelete);
        var curTableName = tm.curTable.nameSql;
        if (tm.dbCfg.isAny()) {
            popDelete.btnExe.setVisible(true);
            popDelete.setTextArea("TAMAM: Silme kontrol tamamlandı. Konu satır başka hiçbir tabloda kullanılmamış. Rahatlıkla silebilirsiniz.");
            return;
        }
        TGC_SGWTCalller.async(new AppSGFRowUsage(curTableName, tm.cells.getActiveRowId()), r -> {
//            d.ci("onDelete", "returnedAs", r.toString());
            if (r.getOutput_used()) {
                popDelete.append("DIKKAT: Aşağıdaki bilgilere göre silmeden önce kontrol yapmanız önerilir!\n\n" + r.getOutput_summary());
            } else {
                popDelete.btnExe.setVisible(true);
                popDelete.setTextArea("TAMAM: Silme kontrol tamamlandı. Konu satır başka hiçbir tabloda kullanılmamış. Rahatlıkla silebilirsiniz.");
            }
        });
    };

    private String subRecords_validItemPrefix = " - {tn/id:";
    public TGS_FuncMTU onSubRecords = () -> {
        if (tm.cells.getActiveCell() == null || tm.cells.getActiveColIdx() < 0) {
            d.ce("onDelete", "HATA: Önce silmek istediğiniz satırdan bir hücre seçiniz!");
            return;
        }
        if (tm.cells.getActiveRowId() < 1L) {
            d.ce("onDelete", "HATA: Satır id 1 den küçük olamaz hatası");
            return;
        }
        popSubRecords.label.setText("id=" + tm.cells.getActiveRowId() + "'ın alt kayıtarı:");
        popSubRecords.listBox.clear();
        popSubRecords.listBoxContent.clear();
        popSubRecords.listBox.addItem(" - {BEKLEYİN...}");
        popSubRecords.getPop().setVisible_beCeneteredAt(btnSubRecords);
        var curTableName = tm.curTable.nameSql;
        TGC_SGWTCalller.async(new AppSGFRowUsage(curTableName, tm.cells.getActiveRowId()), response -> {
            if (response.getOutput_used()) {
                List<TGS_Tuple2<String, Long>> tablename_id_visibleName = TGS_ListUtils.of();
                response.getOutput_tableSQLNames().forEach(p -> p.value1.forEach(id -> tablename_id_visibleName.add(new TGS_Tuple2(p.value0, id))));

                var suffixWait = " Kayıt tablosunu açmaya yetkili iseniz, satıra tıkladığınızda detaylanacak...";

                popSubRecords.listBox.clear();
                popSubRecords.listBoxContent.clear();
                IntStream.range(0, tablename_id_visibleName.size()).forEachOrdered(idx -> {
                    var pack3 = tablename_id_visibleName.get(idx);
                    var tn = pack3.value0;
                    var id = pack3.value1;
                    var config = subRecords_validItemPrefix + tn + "/" + id + "}";
                    popSubRecords.listBoxContent.add(config);
                    popSubRecords.listBox.addItem(config + suffixWait);
                });
            } else {
                popSubRecords.listBox.clear();
                popSubRecords.listBoxContent.clear();
                popSubRecords.listBox.addItem(" - {BOŞ}");
            }
        });
    };
}
