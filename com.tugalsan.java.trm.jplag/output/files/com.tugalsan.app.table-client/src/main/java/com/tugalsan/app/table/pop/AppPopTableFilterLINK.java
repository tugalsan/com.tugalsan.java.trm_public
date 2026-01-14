package com.tugalsan.app.table.pop;

import com.tugalsan.lib.rql.link.client.*;
import com.tugalsan.lib.rql.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In2;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.gui.client.click.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.key.*;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.theme.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.shape.client.*;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.thread.client.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.tuple.client.TGS_Tuple2;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.sg.cell.*;
import com.tugalsan.app.table.sg.query.*;
import java.util.*;
import java.util.stream.*;

public class AppPopTableFilterLINK implements TGC_PopInterface {

    final private static TGC_Log d = TGC_Log.of(AppPopTableFilterLINK.class);
    private AppPopTableFilterProfile profile;
    private TGS_LibRqlTbl tarTable;
    private long lastCTM = 0L;

    public void clear_buffer() {//TODO NO add or get
        onFillTbInputRange_buffer_colIdx_data.removeIf(p -> p.value0.equals(profile.ci));
        onRefreshLb_buffer_colIdx_data.removeIf(p -> p.value0.equals(profile.ci));
    }

    @Override
    public void createWidgets() {
        btnSearchAnd = TGC_ButtonUtils.createIconToggle(TGS_IconUtils.CLASS_BOOK(), "Toplu Ara");
        btnLinkMemoryEnabled = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_HAMMER());
        btnLinkMemoryEnabled.setTitle("Hafızadaki Listeyi Sil");
        btnSearch = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_SEARCH(), "Ara");
        btnSearchAll = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CHECKMARK(), "Hepsin Göster");
        btnTableTargetOpen = TGC_ButtonUtils.createIcon(btnTableTargetOpenInit[0], btnTableTargetOpenInit[1]);
        btnCancel = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CROSS(), "İptal");
        btnSave = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CHECKMARK(), "Uygula");
        btnMinMax = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_FILTER(), "Min&Max");
        btnSelectAllSearch = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CHECKBOX_CHECKED(), null);
        btnSelectNoneSearch = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CHECKBOX_UNCHECKED(), null);
        btnSelectAllValues = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CHECKBOX_CHECKED(), null);
        btnSelectNoneValues = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CHECKBOX_UNCHECKED(), null);
        btnNone = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CHECKBOX_UNCHECKED(), "Listeyi Boşalt");
        btnAdd = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_PLUS(), "Ekle");
        btnRemove = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_BIN(), "Kaldır");
        lb = TGC_ListBoxUtils.create(false);
        lbValues = TGC_ListBoxUtils.create(false);
        tbInput = new TextBox();
        tbInputMin = new TextBox();
        tbInputMax = new TextBox();
        lblHTML = new HTML();
    }
    private String[] btnTableTargetOpenInit = new String[]{TGS_IconUtils.CLASS_TABLE2(), "Bağlı Tabloyu Aç"};
    private String[] btnTableTargetOpenWait = new String[]{TGS_IconUtils.CLASS_HOUR_GLASS(), "Bağlı Tabloyu Açılıyor..."};
    private String[] btnTableTargetOpenFailed = new String[]{TGS_IconUtils.CLASS_QUESTION(), "Yetkin var mı?"};
    private ToggleButton btnSearchAnd;
    private PushButton btnSearch, btnSearchAll, btnTableTargetOpen, btnCancel, btnSave;
    private PushButton btnSelectAllSearch, btnSelectNoneSearch;
    private PushButton btnSelectAllValues, btnSelectNoneValues;
    private PushButton btnMinMax, btnNone, btnAdd, btnRemove, btnLinkMemoryEnabled;
    private ListBox lb, lbValues;
    private TextBox tbInput, tbInputMin, tbInputMax;
    private HTML lblHTML;

    @Override
    public void createPops() {
    }

    @Override
    public void configInit() {
        btnSearchAnd.setDown(true);
        onSearchAnd.run(btnSearchAnd.isDown());
        values = TGS_ListUtils.of();
        lbValues.setMultipleSelect(true);
        lb.setMultipleSelect(true);
    }
    private List<String> values;

    @Override
    public void configActions() {
        TGC_ClickUtils.add(btnSearchAnd, isDown -> onSearchAnd.run(isDown));
        TGC_ClickUtils.add(btnLinkMemoryEnabled, () -> AppPopEditPopLINKBufferStar.clear(tm));
        TGC_ClickUtils.add(btnSearch, () -> onRefreshLb_useBufferIfPossible_showAllIfUnderMinSize.run(true, false));
        TGC_ClickUtils.add(btnCancel, onCancel);
        TGC_ClickUtils.add(btnSave, onSetCell);
        TGC_ClickUtils.add(btnSearchAll, onSetSearchAll);
        TGC_ClickUtils.add(btnMinMax, () -> onFillTbInputRange_useBufferIfPossible_year.run(false, null));
        TGC_ClickUtils.add(btnSelectAllSearch, () -> onClickSearch.run(true));
        TGC_ClickUtils.add(btnSelectAllValues, () -> onClickValue.run(true));
        TGC_ClickUtils.add(btnSelectNoneSearch, () -> onClickSearch.run(false));
        TGC_ClickUtils.add(btnSelectNoneValues, () -> onClickValue.run(false));
        TGC_ClickUtils.add(btnNone, onSetValuesNone);
        TGC_ClickUtils.add(btnAdd, onAdd);
        TGC_ClickUtils.add(btnRemove, onRemove);
        TGC_ClickUtils.add(lb, onChangeSearch, onAdd);
        TGC_ClickUtils.add(lbValues, onChangeValues, onRemove);
        TGC_ClickUtils.add(btnTableTargetOpen, onTableLinkOpen);

        TGC_KeyUtils.add(btnLinkMemoryEnabled, () -> AppPopEditPopLINKBufferStar.clear(tm), onCancel);
        TGC_KeyUtils.add(btnSearch, () -> onRefreshLb_useBufferIfPossible_showAllIfUnderMinSize.run(true, false), onCancel);
        TGC_KeyUtils.add(btnCancel, onCancel, onCancel);
        TGC_KeyUtils.add(btnSave, onSetCell, onCancel);
        TGC_KeyUtils.add(btnSearchAll, onSetSearchAll, onCancel);
        TGC_KeyUtils.add(btnMinMax, () -> onFillTbInputRange_useBufferIfPossible_year.run(false, null), onCancel);
        TGC_KeyUtils.add(btnSelectAllSearch, () -> onClickSearch.run(true), onCancel);
        TGC_KeyUtils.add(btnSelectAllValues, () -> onClickValue.run(true), onCancel);
        TGC_KeyUtils.add(btnSelectNoneSearch, () -> onClickSearch.run(false), onCancel);
        TGC_KeyUtils.add(btnSelectNoneValues, () -> onClickValue.run(false), onCancel);
        TGC_KeyUtils.add(btnNone, onSetValuesNone, onCancel);
        TGC_KeyUtils.add(btnAdd, onAdd, onCancel);
        TGC_KeyUtils.add(btnRemove, onRemove, onCancel);
        TGC_KeyUtils.add(lb, onAdd, onCancel, onChangeSearch, onChangeSearch);
        TGC_KeyUtils.add(lbValues, onRemove, onCancel, onChangeValues, onChangeValues);
        TGC_KeyUtils.add(tbInput, () -> onRefreshLb_useBufferIfPossible_showAllIfUnderMinSize.run(true, false), onCancel);
        TGC_KeyUtils.add(btnTableTargetOpen, onTableLinkOpen, onCancel);

        TGC_KeyUtils.addCtrlEnter(onSetCell,
                btnSearch, btnCancel, btnSave, btnSearchAll, btnMinMax, btnSelectAllSearch, btnSelectAllValues,
                btnSelectNoneSearch, btnSelectNoneValues, btnNone, btnAdd, btnRemove, lb, lbValues, tbInput
        );
    }

    @Override
    public void configFocus() {
        TGC_FocusUtils.addKeyUp(btnCancel, new TGS_FocusSides4(null, btnSave, null, null));
        TGC_FocusUtils.addKeyUp(btnCancel, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_DOWN:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tbInput);
                        tbInput.selectAll();
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(btnSave, new TGS_FocusSides4(btnCancel, null, null, null));
        TGC_FocusUtils.addKeyUp(btnSave, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_RIGHT:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tbInputMin);
                        tbInputMin.selectAll();
                        break;
                    case KeyCodes.KEY_DOWN:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tbInput);
                        tbInput.selectAll();
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(tbInputMin, new TGS_FocusSides4(null, null, null, btnSearch));
        TGC_FocusUtils.addKeyUp(tbInputMin, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_LEFT:
                        if (tbInputMin.getCursorPos() == 0) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSave);
                        }
                        break;
                    case KeyCodes.KEY_RIGHT:
                        if (tbInputMin.getCursorPos() == tbInputMin.getText().length()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tbInputMax);
                            tbInputMax.selectAll();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(tbInputMax, new TGS_FocusSides4(null, null, null, btnMinMax));
        TGC_FocusUtils.addKeyUp(tbInputMax, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_LEFT:
                        if (tbInputMax.getCursorPos() == 0) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tbInputMin);
                            tbInputMin.selectAll();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(tbInput, new TGS_FocusSides4(null, null, btnCancel, lb));
        TGC_FocusUtils.addKeyUp(tbInput, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_RIGHT:
                        if (tbInput.getCursorPos() == tbInput.getText().length()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSearch);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(btnSearch, new TGS_FocusSides4(null, btnMinMax, null, lbValues));
        TGC_FocusUtils.addKeyUp(btnSearch, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_LEFT:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tbInput);
                        tbInput.selectAll();
                        break;
                    case KeyCodes.KEY_UP:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tbInputMin);
                        tbInputMin.selectAll();
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(btnMinMax, new TGS_FocusSides4(btnSearch, null, null, lbValues));
        TGC_FocusUtils.addKeyUp(btnMinMax, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_UP:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tbInputMax);
                        tbInputMax.selectAll();
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(lb, new TGS_FocusSides4(null, btnAdd, tbInput, btnSearchAll), null);
        TGC_FocusUtils.addKeyUp(btnSearchAll, new TGS_FocusSides4(null, btnTableTargetOpen, lb, null));
        TGC_FocusUtils.addKeyUp(btnTableTargetOpen, new TGS_FocusSides4(btnSearchAll, btnNone, lb, null));

        TGC_FocusUtils.addKeyUp(lbValues, new TGS_FocusSides4(btnAdd, null, null, null));
        TGC_FocusUtils.addKeyUp(lbValues, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_UP:
                        if (lbValues.getItemCount() == 0 || lbValues.getSelectedIndex() == 0) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(tbInput);
                            tbInput.selectAll();
                        }
                        break;
                    case KeyCodes.KEY_DOWN:
                        if (lb.getSelectedIndex() == lb.getItemCount() - 1) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnNone);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(btnNone, new TGS_FocusSides4(btnTableTargetOpen, null, lbValues, null));

        TGC_FocusUtils.addKeyUp(btnAdd, new TGS_FocusSides4(lb, lbValues, tbInput, btnRemove));
        TGC_FocusUtils.addKeyUp(btnRemove, new TGS_FocusSides4(lb, lbValues, btnAdd, null));
        TGC_FocusUtils.addKeyUp(btnRemove, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_DOWN:
                        if (btnSelectAllSearch.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectAllSearch);
                        } else if (btnSelectNoneSearch.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectNoneSearch);
                        } else if (btnSelectAllValues.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectAllValues);
                        } else if (btnSelectNoneValues.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectNoneValues);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        TGC_FocusUtils.addKeyUp(btnSelectAllSearch, new TGS_FocusSides4(lb, null, btnRemove, null));
        TGC_FocusUtils.addKeyUp(btnSelectAllSearch, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_RIGHT:
                        if (btnSelectAllValues.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectAllValues);
                        } else if (btnSelectNoneValues.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectNoneValues);
                        } else {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(lbValues);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(btnSelectNoneSearch, new TGS_FocusSides4(lb, null, btnRemove, null));
        TGC_FocusUtils.addKeyUp(btnSelectNoneSearch, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_RIGHT:
                        if (btnSelectAllValues.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectAllValues);
                        } else if (btnSelectNoneValues.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectNoneValues);
                        } else {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(lbValues);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        TGC_FocusUtils.addKeyUp(btnSelectAllValues, new TGS_FocusSides4(null, lbValues, btnRemove, null));
        TGC_FocusUtils.addKeyUp(btnSelectAllValues, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_LEFT:
                        if (btnSelectAllSearch.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectAllSearch);
                        } else if (btnSelectNoneSearch.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectNoneSearch);
                        } else {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(lb);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(btnSelectNoneValues, new TGS_FocusSides4(null, lbValues, btnRemove, null));
        TGC_FocusUtils.addKeyUp(btnSelectNoneValues, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_LEFT:
                        if (btnSelectAllSearch.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectAllSearch);
                        } else if (btnSelectNoneSearch.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnSelectNoneSearch);
                        } else {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(lb);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void configLayout() {
        var dim = new TGC_Dimension(990, 500, false);
        content = TGC_PanelAbsoluteUtils.create(dim);//#101010

        var noPictureOffset = 90;

        var width = 100;
        var height = 24;
        var y0 = 10;
        var y1 = 40;
        TGC_PanelAbsoluteUtils.setWidget(content, btnCancel, TGS_ShapeRectangle.of(10, y0, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSave, TGS_ShapeRectangle.of(120, y0, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSearchAll, TGS_ShapeRectangle.of(10, 450, 195, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnTableTargetOpen, TGS_ShapeRectangle.of(215, 450, 195, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSearchAnd, TGS_ShapeRectangle.of(215 + 195 + 10, 450, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnLinkMemoryEnabled, TGS_ShapeRectangle.of(525, 450, 40, height));
        TGC_PanelAbsoluteUtils.setWidget(content, lblHTML, TGS_ShapeRectangle.of(230, y0, 440, height));
        TGC_PanelAbsoluteUtils.setWidget(content, tbInput, TGS_ShapeRectangle.of(10, y1, 660 + noPictureOffset, height));

        var x1 = 680 + noPictureOffset;
        var x2 = 790 + noPictureOffset;
        TGC_PanelAbsoluteUtils.setWidget(content, tbInputMin, TGS_ShapeRectangle.of(x1, y0, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, tbInputMax, TGS_ShapeRectangle.of(x2, y0, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSearch, TGS_ShapeRectangle.of(x1, y1, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnMinMax, TGS_ShapeRectangle.of(x2, y1, width, height));

        TGC_PanelAbsoluteUtils.setWidget(content, btnSelectAllSearch, TGS_ShapeRectangle.of(420, 330, 60, 24));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSelectAllValues, TGS_ShapeRectangle.of(510, 330, 60, 24));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSelectNoneSearch, TGS_ShapeRectangle.of(420, 330, 60, 24));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSelectNoneValues, TGS_ShapeRectangle.of(510, 330, 60, 24));
        TGC_PanelAbsoluteUtils.setWidget(content, btnNone, TGS_ShapeRectangle.of(785, 450, 195, 24));
        TGC_PanelAbsoluteUtils.setWidget(content, btnAdd, TGS_ShapeRectangle.of(420, 230, 150, 24));
        TGC_PanelAbsoluteUtils.setWidget(content, btnRemove, TGS_ShapeRectangle.of(420, 280, 150, 24));
        TGC_PanelAbsoluteUtils.setWidget(content, lb, TGS_ShapeRectangle.of(10, 80, 400, 360));
        TGC_PanelAbsoluteUtils.setWidget(content, lbValues, TGS_ShapeRectangle.of(580, 80, 400, 360));
//        TGC_PanelAbsoluteUtils.setWidget(content, cbDistinct, TGS_ShapeRectangle.of(420, 200, 150, 24));
//        TGC_PanelAbsoluteUtils.setWidget(content, btnSearchAll, TGS_ShapeRectangle.of(580, 450, 195, 24));

        panelPopup = new TGC_Pop(content, dim, null);
    }
    private AbsolutePanel content;
    private TGC_Pop panelPopup;

    @Override
    public TGC_Pop getPop() {
        return panelPopup;
    }

    @SuppressWarnings("deprecation")
    public AppPopTableFilterLINK(AppModuleTable tm, FocusWidget onCloseFocus) {
        this.tm = tm;
        this.onCloseFocus = onCloseFocus;
        createWidgets();
        createPops();
        configInit();
        configActions();
        configFocus();
        configLayout();
    }
    private AppModuleTable tm;
    private FocusWidget onCloseFocus;

    private TGS_FuncMTU onTableLinkOpen = () -> {
        btnTableTargetOpen.setEnabled(false);
        TGC_ButtonUtils.setIcon(btnTableTargetOpen, btnTableTargetOpenWait[0], btnTableTargetOpenWait[1]);
        if (!tm.subRecord.onClick_TableLinkOpen(profile.ct, null)) {
            TGC_ButtonUtils.setIcon(btnTableTargetOpen, btnTableTargetOpenFailed[0], btnTableTargetOpenFailed[1]);
        }
        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
            btnTableTargetOpen.setEnabled(true);
            TGC_ButtonUtils.setIcon(btnTableTargetOpen, btnTableTargetOpenInit[0], btnTableTargetOpenInit[1]);
        }, 5);
    };

    private TGS_FuncMTU remove_TheAllTag = () -> {
        TGS_ListCleanUtils.deleteIf(i -> values.get(i).equals(TGS_LibRqlLink.STAR_TAG()), values, i -> lbValues.removeItem(i));
    };
    private TGS_FuncMTU onChangeValues = () -> {
        TGC_ThreadUtils.run_afterGUIUpdate(() -> {
            var selectAll = TGC_ListBoxUtils.isSelectedAll(lbValues);
            if (selectAll == null) {//empty
                btnSelectAllValues.setVisible(false);
                btnSelectNoneValues.setVisible(false);
                d.ci("onChangeValues", "onChangeValues", "isEmpty");
                return;
            }
            btnSelectAllValues.setVisible(!selectAll);
            btnSelectNoneValues.setVisible(selectAll);
            d.ci("onChangeValues", "onChangeValues", "btnSelectAllValues", btnSelectAllValues.isVisible());
        });
    };

    private TGS_FuncMTU onAdd = () -> {
        d.ci("onAdd", "init");
        var si = TGC_ListBoxUtils.getSelectedIndexes(lb);
        if (si.isEmpty()) {
            TGC_PanelStyleUtils.warn(lb, 1);
            d.ce("onAdd", "HATA: Eklemeden önce sol taraftaki listeden birkaç satır seçiniz.");
            return;
        }
        for (var i = 0; i < si.size(); i++) {
            var text = lb.getItemText(si.get(i));
            var parsedRow = TGS_StringUtils.gwt().toList_spc(text);
            if (parsedRow.isEmpty()) {
                TGC_PanelStyleUtils.warn(lb, 1);
                d.ce("onAdd", "HATA: Solda seçilen satırı parçalama hatası.");
                continue;
            }
            var id = TGS_CastUtils.toInteger(parsedRow.get(0)).orElse(null);
            if (id == null) {
                TGC_PanelStyleUtils.warn(lb, 1);
                d.ce("onAdd", "HATA: Solda seçilen satırda ilk kelime id olmalıydı hatası.");
                continue;
            }
            remove_TheAllTag.run();
            var ids = String.valueOf(id);
            var alreadyExists = false;
            for (var s : values) {
                if (s.equals(ids)) {
                    alreadyExists = true;
                    TGC_PanelStyleUtils.warn(lbValues, 1);
                    d.ce("onAdd", "UYARI: Zaten var!");
                    break;
                }
            }
            if (alreadyExists) {
                continue;
            }
            values.add(ids);
            lbValues.addItem(text);
        }
        onChangeValues.run();
    };

    private TGS_FuncMTU onRemove = () -> {
        d.ci("onRemove", "init");
        var si = TGC_ListBoxUtils.getSelectedIndexes(lbValues);
        if (si.isEmpty()) {
            TGC_PanelStyleUtils.warn(lbValues, 1);
            d.ce("onRemove", "HATA: Kaldırmadan önce sağ taraftaki listeden birkaç satır seçiniz.");
            return;
        }
        TGS_ListSortUtils.sortReversed(si);
        for (var i = 0; i < si.size(); i++) {
            lbValues.removeItem(si.get(i));
            values.remove(si.get(i).intValue());
            if (!values.isEmpty()) {
                TGC_ListBoxUtils.selectOnlyLastItem(lbValues);
            }
        }
        onChangeValues.run();
    };

    private TGS_FuncMTU onCancel = () -> {
        getPop().setVisible(false);
        TGC_FocusUtils.setFocusAfterGUIUpdate(onCloseFocus);
    };

    private TGS_FuncMTU onSetValuesNone = () -> {
        values.clear();
        lbValues.clear();
        onChangeValues.run();
    };

    private TGS_FuncMTU_In1<Boolean> onClickValue = selectAll -> {
        d.ci("onClickValue", "onClickValue", "selectAll", selectAll);
        TGC_ThreadUtils.run_afterGUIUpdate(() -> {
            TGC_ListBoxUtils.selectAll(lbValues, selectAll);
            onChangeValues.run();
        });
    };

    private TGS_FuncMTU onChangeSearch = () -> {
        TGC_ThreadUtils.run_afterGUIUpdate(() -> {
            var selectAll = TGC_ListBoxUtils.isSelectedAll(lb);
            if (selectAll == null) {//empty
                btnSelectAllSearch.setVisible(false);
                btnSelectNoneSearch.setVisible(false);
                d.ci("onChangeSearch", "onChangeSearch", "isEmpty");
                return;
            }
            btnSelectAllSearch.setVisible(!selectAll);
            btnSelectNoneSearch.setVisible(selectAll);
            d.ci("onChangeSearch", "onChangeSearch", "btnSelectAllSearch", btnSelectAllSearch.isVisible());
        });
    };

    private TGS_FuncMTU_In1<Boolean> onClickSearch = selectAll -> {
        d.ci("onClickSearch", "onClickSearch", "selectAll", selectAll);
        TGC_ThreadUtils.run_afterGUIUpdate(() -> {
            TGC_ListBoxUtils.selectAll(lb, selectAll);
            onChangeSearch.run();
        });
    };

    private TGS_FuncMTU onSetCell = () -> {
        var inputText = TGS_StringUtils.cmn().toString(values, " ");
        profile.btnBySelected = inputText;
        profile.cbActive = !inputText.trim().isEmpty();
        profile.renderPopProfile_and_lb();
        getPop().setVisible(false);
        TGC_FocusUtils.setFocusAfterGUIUpdate(onCloseFocus);
    };

    //DEFAULT IS FALSE
    private TGS_FuncMTU_In2<Boolean, Boolean> onRefreshLb_useBufferIfPossible_showAllIfUnderMinSize = (useBufferIfPossible, showAllIfUnderMinSize) -> {
        TGS_FuncMTCUtils.run(() -> {
            if (!useBufferIfPossible) {
                onRefreshLb_buffer_colIdx_data.removeIf(p -> p.value0.equals(profile.ci));
            }
            d.ci("refreshLb", "init");
            lb.clear();
            var min = TGS_CastUtils.toLong(tbInputMin.getText()).orElse(null);
            var max = TGS_CastUtils.toLong(tbInputMax.getText()).orElse(null);
            var düzeltme = " Sağ üst köşede bulunan MİN ve MAX kutucuklarının dolmasını bekleyin veya MinMax tuşuna basarak tekrar doldurulmasını isteyin.";
            if (min == null) {
                d.ce("refreshLb", "HATA: min sayıya çevirilemiyor hatası!" + düzeltme);
                return;
            }
            if (max == null) {
                d.ce("refreshLb", "HATA: max sayıya çevirilemiyor hatası!" + düzeltme);
                return;
            }

            var curTableName = tm.curTable.nameSql;
            var tarTableName = tarTable.nameSql;

            var existsOnBuffer = AppPopEditPopLINKBufferStar.findAny(tarTableName).orElse(null);
//            var inputTags = TGS_StringUtils.gwt().toList_spc(tbInput.getText());
//            if (!inputTags.isEmpty() && TGS_CastUtils.toLong(inputTags).isEmpty()) {
//                existsOnBuffer = null;
//            }
            if (existsOnBuffer == null) {
                AppPopEditPopLINKBufferStar.printInfo();
            } else {
                d.cr("refreshLb", "Arama hafızadan çekildi");
                lb.clear();
                existsOnBuffer.forEach(s -> {
                    lb.addItem(s);
                });
                return;
            }

            var searchFuncNew = new AppSGFCellSearch(
                    tm.dbCfg, min, max, curTableName, tarTableName,
                    showAllIfUnderMinSize ? AppSGFCellSearch.MIN_SIZE() : AppSGFCellSearch.MIN_SIZE_DISABLE(),
                    tm.settings.searchMaxItem, tm.settings.searchMaxSecs, tbInput.getText(), btnSearchAnd.isDown()
            );
            d.cr("refreshLb", "searchFuncNew", searchFuncNew);

            TGC_PanelStyleUtils.red(lb, tbInput, tbInputMin, tbInputMax);
            btnSearch.setEnabled(false);
            TGC_SGWTCalller.async(searchFuncNew, resp -> {
                TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> TGC_PanelStyleUtils.remove(lb, tbInput, tbInputMin, tbInputMax), 1);
                btnSearch.setEnabled(true);
                //                TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                TGC_PanelStyleUtils.remove(lb, tbInput, tbInputMin, tbInputMax);
//                }, 1);
                if (resp.getOutput_ctm() == null) {
                    d.ce("refreshLb", "Liste yenilenirken bir hata oluştu (resp.getOutput_ctm() == null)");
                    return;
                }
                if (lastCTM > resp.getOutput_ctm()) {
                    d.ci("refreshLb", "Liste atlandi: + lastCTM:" + lastCTM);
                    return;
                }
                lastCTM = resp.getOutput_ctm();

                if (resp.getOutput_list() == null) {
                    d.ce("refreshLb", "Liste yenilenirken bir hata oluştu (r.getOutput_list() == null)");
                    d.ce("refreshLb", resp.getOutput_status());
                    return;
                }
                if (resp.getOutput_list().isEmpty()) {
                    d.ce("refreshLb", resp.getOutput_status());
                    d.ce("refreshLb", "Liste boş döndü hatsı!");
                    return;
                }

                lb.clear();
                resp.getOutput_list().forEach(s -> {
                    lb.addItem(s);
                    d.ci("refreshLb", "prgGetLinkLbData.s[i]", s);
                });

                if (resp.getOutput_isProcessedAsStar() && !resp.getOutput_list().isEmpty()) {
                    AppPopEditPopLINKBufferStar.add(resp.getInput_tarTablename(), resp.getOutput_list());
                    d.cr("refreshLb", "Arama bitti. " + TGS_Time.toString_now(), "hafızaya eklendi");
                } else {
                    d.cr("refreshLb", "Arama bitti. " + TGS_Time.toString_now());
                }

                onChangeSearch.run();
            });
        }, thr -> {
            TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                TGC_PanelStyleUtils.remove(lb, tbInput, tbInputMin, tbInputMax);
                btnSearch.setEnabled(true);
            }, 1);
        });
    };
    private static List<TGS_Tuple2<Integer, AppSGFCellSearch>> onRefreshLb_buffer_colIdx_data = new ArrayList();

    private TGS_FuncMTU onSetSearchAll = () -> {
        tbInput.setText("*");
        onRefreshLb_useBufferIfPossible_showAllIfUnderMinSize.run(true, false);
    };

    private TGS_FuncMTU_In2<Boolean, Integer> onFillTbInputRange_useBufferIfPossible_year = (useBufferIfPossible, year) -> {
        if (tarTable == null) {
            return;
        }
        if (!useBufferIfPossible) {
            onFillTbInputRange_buffer_colIdx_data.removeIf(p -> p.value0.equals(profile.ci));
        }
        var tarTableName = tarTable.nameSql;
        if (year == null) {
            TGC_SGWTCalller.async(new AppSGFQueryMinMaxId(tm.dbCfg, tarTableName, "LNG_ID"), (response) -> {
                tbInputMin.setText(String.valueOf(response.getOutput_minId()));
                tbInputMax.setText(String.valueOf(response.getOutput_maxId()));
                d.ci("fillTbInputRange", "Min-Max ayarlandı. tn:" + tarTableName);
                TGC_SGWTCalller.async(new AppSGFQueryCount(tm.dbCfg, tarTableName, null, null, null), resp -> {
                    if (resp.getOutput_count() > AppSGFCellSearch.MIN_SIZE()) {
                        return;
                    }
                    onRefreshLb_useBufferIfPossible_showAllIfUnderMinSize.run(useBufferIfPossible, true);
                    d.ci("fillTbInputRange", "Liste otomatik güncellendi");
                });
            });
        } else {
            var yearPrefix_minus1 = String.valueOf(year - 1).substring(2, 4);
            var yearPrefix = String.valueOf(year).substring(2, 4);
            tbInputMin.setText(yearPrefix_minus1 + "000000");
            tbInputMax.setText(yearPrefix + "999999");
            d.ci("fillTbInputRange", "Min-Max bu yıla ayarlandı. tn:" + tarTableName);
            var where = "LNG_ID BETWEEN " + tbInputMin.getText() + " AND " + tbInputMax.getText();
            TGC_SGWTCalller.async(new AppSGFQueryCount(tm.dbCfg, tarTableName, where, null, null), resp -> {
                if (resp.getOutput_count() > AppSGFCellSearch.MIN_SIZE()) {
                    return;
                }
                onRefreshLb_useBufferIfPossible_showAllIfUnderMinSize.run(useBufferIfPossible, true);
                d.ci("fillTbInputRange", "Liste otomatik güncellendi");
            });
        }
    };
    private static List<TGS_Tuple2<Integer, AppSGFQueryMinMaxId>> onFillTbInputRange_buffer_colIdx_data = new ArrayList();

    private static class IDX_ID {

        public IDX_ID(int idx, long id) {
            this.idx = idx;
            this.id = id;
        }
        public int idx;
        public long id;
    }
    public TGS_FuncMTU_In1<AppPopTableFilterProfile> onSetVisible = profile -> {
        this.profile = profile;
        lblHTML.setHTML(profile.htmlColumnNameTitle);
        tarTable = App.tbl_canReturnNull(profile.ct.getDataString1_LnkTargetTableName());
        if (tarTable == null) {
            tbInputMin.setText("");
            tbInputMax.setText("");
        } else {
            onFillTbInputRange_useBufferIfPossible_year.run(true, tarTable.autoIdDatedConfig ? TGS_Time.getCurrentYear() : null);
        }
        tbInput.setText(profile.btnBySelected);

        values.clear();
        lbValues.clear();
        values = TGS_StringUtils.gwt().toList_spc(profile.btnBySelected);

        List<IDX_ID> lst_IDX_ID = new ArrayList();
        IntStream.range(0, values.size()).forEachOrdered(idx -> {
            var id = TGS_CastUtils.toLong(values.get(idx)).orElse(null);
            if (id == null) {
                if (values.get(idx).isEmpty()) {
                    //skip
                } else if (values.get(idx).equals(TGS_LibRqlLink.STAR_TAG())) {
                    lbValues.addItem(values.get(idx) + " HEPSİ");
                } else {
                    lbValues.addItem(values.get(idx) + " HATA");
                    d.ce("setVisible", "ERROR: values.get", String.valueOf(idx), String.valueOf(values.get(idx)), "nor integer and nor '*'");
                }
            } else {
                lbValues.addItem(values.get(idx));
                lst_IDX_ID.add(new IDX_ID(idx, id));
            }
        });

        var curTableName = tm.curTable.nameSql;
        var tarTableName = tarTable.nameSql;
        TGC_SGWTCalller.async(new AppSGFCellGetList(tm.dbCfg, curTableName, tarTableName, TGS_StreamUtils.toLst(lst_IDX_ID.stream().map(o -> o.id))), r -> {
            if (r.getOutput_errTexts() == null) {
                d.ce("onSetVisible", "HATA: errTexts == null");
                return;
            }
            if (r.getOutput_errTexts().size() != lst_IDX_ID.size()) {
                d.ce("onSetVisible", "HATA: r.getOutput_errTexts().size() != lst_IDX_ID.size()", r.getOutput_errTexts().size(), lst_IDX_ID.size());
                return;
            }
            if (r.getOutput_linkTexts() == null) {
                d.ce("onSetVisible", "HATA: linkTexts == null");
                return;
            }
            if (r.getOutput_linkTexts().size() != lst_IDX_ID.size()) {
                d.ce("onSetVisible", "HATA: r.getOutput_linkTexts().size() != lst_IDX_ID.size()", r.getOutput_linkTexts().size(), lst_IDX_ID.size());
                return;
            }
            if (r.getOutput_linkText_WPrefixIds() == null) {
                d.ce("onSetVisible", "HATA: linkText_WPrefixIds == null");
                return;
            }
            if (r.getOutput_linkText_WPrefixIds().size() != lst_IDX_ID.size()) {
                d.ce("onSetVisible", "HATA: r.getOutput_linkText_WPrefixIds().size() != lst_IDX_ID.size()", r.getOutput_linkText_WPrefixIds().size(), lst_IDX_ID.size());
                return;
            }
            IntStream.range(0, lst_IDX_ID.size()).forEachOrdered(i -> {
                var errText = r.getOutput_errTexts().get(i);
                var linkText_WPrefixId = r.getOutput_linkText_WPrefixIds().get(i);
                if (errText == null) {
                    lbValues.setItemText(lst_IDX_ID.get(i).idx, linkText_WPrefixId);
                    d.ci("onSetVisible", "callBack", "setItem(i, s)", lst_IDX_ID.get(i).idx, linkText_WPrefixId);
                } else {
                    d.ce("onSetVisible", "HATA: getErrorText: " + errText);
                }
            });
        });

        getPop().setVisible(true);
        tbInput.setFocus(true);
        tbInput.selectAll();
        lb.clear();
        onChangeSearch.run();
        onChangeValues.run();
    };

    private final TGS_FuncMTU_In1<Boolean> onSearchAnd = isDown -> {
        if (isDown) {
            TGC_ButtonUtils.setIcon(btnSearchAnd, TGS_IconUtils.CLASS_BOOK(), "Toplu Ara");
        } else {
            TGC_ButtonUtils.setIcon(btnSearchAnd, TGS_IconUtils.CLASS_BOOKS(), "Parçalı Ara");
        }
    };
}
