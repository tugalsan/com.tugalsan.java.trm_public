package com.tugalsan.app.table.pop;

import com.tugalsan.lib.rql.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In2;
import com.tugalsan.api.gui.client.click.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.key.*;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.theme.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.shape.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.thread.client.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.tuple.client.TGS_Tuple2;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.app.table.sg.cell.*;
import com.tugalsan.app.table.sg.query.*;
import java.util.ArrayList;
import java.util.List;

public class AppPopEditCellLNGLINK implements TGC_PopInterface {

    final private static TGC_Log d = TGC_Log.of(AppPopEditCellLNGLINK.class);
    private TGS_LibRqlTbl tarTable;
    private AppCell_LNGLINK cell;
    private long lastCTM = 0;

    public void clear_buffer() {//TODO NO add or get
        onFillTbInputRange_buffer_colIdx_data.removeIf(p -> p.value0.equals(cell.colIdx));
        onRefreshLb_buffer_colIdx_data.removeIf(p -> p.value0.equals(cell.colIdx));
    }

    @Override
    public void createWidgets() {
        btnSearchAnd = TGC_ButtonUtils.createIconToggle(TGS_IconUtils.CLASS_BOOK(), "Toplu Ara");
        btnLinkMemoryEnabled = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_HAMMER());
        btnLinkMemoryEnabled.setTitle("Hafızadaki Listeyi Sil");
        btnSearch = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_SEARCH(), "Ara");
        btnCancel = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CROSS(), "İptal");
        btnSave = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CHECKMARK(), "Değiştir");
        btnZero = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_PACMAN());
        btnZero.setTitle("Temizle");
        btnSearchAll = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CHECKMARK(), "Hepsin Göster");
        btnTableTargetOpen = TGC_ButtonUtils.createIcon(btnTableTargetOpenInit[0], btnTableTargetOpenInit[1]);
        btnTableTargetOpen2 = TGC_ButtonUtils.createIcon(btnTableTargetOpenInit2[0], btnTableTargetOpenInit2[1]);
        btnTableTargetOpen3 = TGC_ButtonUtils.createIcon(btnTableTargetOpenInit3[0], btnTableTargetOpenInit3[1]);
        btnMinMax = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_FILTER(), "Min&Max");
        lb = TGC_ListBoxUtils.create(false);
        tbInput = new TextBox();
        tbInputMin = new TextBox();
        tbInputMax = new TextBox();
        image = new Image();
        lblHTML = new HTML();
    }
    private String[] btnTableTargetOpenInit = new String[]{TGS_IconUtils.CLASS_TABLE2(), "Bağlı Tabloyu Aç"};
    private String[] btnTableTargetOpenInit2 = new String[]{TGS_IconUtils.CLASS_INSERT_TEMPLATE(), "Bağlı Tabloda Kaydı Aç"};
    private String[] btnTableTargetOpenInit3 = new String[]{TGS_IconUtils.CLASS_EYE(), "Bağlı Kaydı Ön-İzle"};
    private String[] btnTableTargetOpenWait = new String[]{TGS_IconUtils.CLASS_HOUR_GLASS(), "Bağlı Tabloyu Açılıyor..."};
    private String[] btnTableTargetOpenWait2 = new String[]{TGS_IconUtils.CLASS_HOUR_GLASS(), "Bağlı Tabloda Kayıt Açılıyor..."};
    private String[] btnTableTargetOpenFailed = new String[]{TGS_IconUtils.CLASS_QUESTION(), "Yetkin var mı?"};
    private String[] btnTableTargetOpenFailed3 = new String[]{TGS_IconUtils.CLASS_WARNING(), "Id çekilemedi hatası?"};
    private ToggleButton btnSearchAnd;
    private PushButton btnSearch, btnCancel, btnSave, btnZero, btnSearchAll,
            btnTableTargetOpen, btnTableTargetOpen2, btnTableTargetOpen3, btnMinMax, btnLinkMemoryEnabled;
    private TextBox tbInput, tbInputMin, tbInputMax;
    private Image image;
    private ListBox lb;
    private HTML lblHTML;

    @Override
    public void createPops() {
    }

    @Override
    public void configInit() {
        btnSearchAnd.setDown(true);
        lb.addItem("");
        imageHandler = new AppPopImageHandler(image);
        picEnableStatus = false;
    }
    private AppPopImageHandler imageHandler;
    private boolean picEnableStatus;

    @Override
    public void configActions() {
        TGC_ClickUtils.add(btnSearchAnd, isDown -> onSearchAnd.run(isDown));
        TGC_ClickUtils.add(btnLinkMemoryEnabled, () -> AppPopEditPopLINKBufferStar.clear(tm));
        TGC_ClickUtils.add(btnSearch, onSearch);
        TGC_ClickUtils.add(btnCancel, onCancel);
        TGC_ClickUtils.add(btnSave, onSaveBtn);
        TGC_ClickUtils.add(btnZero, onZero);
        TGC_ClickUtils.add(btnSearchAll, onSearchAll);
        TGC_ClickUtils.add(btnMinMax, onMinMax);
        TGC_ClickUtils.add(lb, onReloadImage, onSaveLb);
        TGC_ClickUtils.add(image, onImageClick);
        TGC_ClickUtils.add(btnTableTargetOpen, onTableLinkOpen);
        TGC_ClickUtils.add(btnTableTargetOpen2, onTableLinkOpen2);
        TGC_ClickUtils.add(btnTableTargetOpen3, onTableLinkOpen3);

        TGC_KeyUtils.add(btnLinkMemoryEnabled, () -> AppPopEditPopLINKBufferStar.clear(tm), onCancel);
        TGC_KeyUtils.add(btnSearch, onSearch, onCancel);
        TGC_KeyUtils.add(btnCancel, onCancel, onCancel);
        TGC_KeyUtils.add(btnSave, onSaveBtn, onCancel);
        TGC_KeyUtils.add(btnZero, onZero, onCancel);
        TGC_KeyUtils.add(btnSearchAll, onSearchAll, onCancel);
        TGC_KeyUtils.add(btnMinMax, onMinMax, onCancel);
        TGC_KeyUtils.add(lb, onSaveLb, onCancel, onReloadImage, onReloadImage);
        TGC_KeyUtils.add(tbInput, onSearch, onCancel);
        TGC_KeyUtils.add(btnTableTargetOpen, onTableLinkOpen, onCancel);
        TGC_KeyUtils.add(btnTableTargetOpen2, onTableLinkOpen2, onCancel);
        TGC_KeyUtils.add(btnTableTargetOpen3, onTableLinkOpen3, onCancel);

        TGC_KeyUtils.addCtrlEnter(() -> onClick.run(tbInput.getText()),
                btnSearch, btnCancel, btnSave, btnSearchAll, btnMinMax, lb, tbInput
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
        TGC_FocusUtils.addKeyUp(btnZero, new TGS_FocusSides4(null, null, btnCancel, lb));
        TGC_FocusUtils.addKeyUp(btnZero, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_RIGHT:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(tbInput);
                        tbInput.selectAll();
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
                    case KeyCodes.KEY_LEFT:
                        if (tbInput.getCursorPos() == 0) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(btnZero);
                        }
                        break;
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
        TGC_FocusUtils.addKeyUp(btnSearch, new TGS_FocusSides4(null, btnMinMax, null, lb));
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
        TGC_FocusUtils.addKeyUp(btnMinMax, new TGS_FocusSides4(btnSearch, null, null, lb));
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
        TGC_FocusUtils.addKeyUp(lb, new TGS_FocusSides4(null, null, tbInput, btnSearchAll), sid -> {
            var tags = TGS_StringUtils.gwt().toList_spc(lb.getSelectedValue());
            if (tags.isEmpty()) {
                return;
            }
            var idStr = tags.get(0);
            var id = TGS_CastUtils.toLong(idStr).orElse(null);
            if (id == null) {
                return;
            }
            tbInput.setText(idStr);
        });
        TGC_FocusUtils.addKeyUp(btnSearchAll, new TGS_FocusSides4(null, btnTableTargetOpen, lb, null));
        TGC_FocusUtils.addKeyUp(btnTableTargetOpen, new TGS_FocusSides4(btnSearchAll, btnTableTargetOpen2, lb, null));
        TGC_FocusUtils.addKeyUp(btnTableTargetOpen2, new TGS_FocusSides4(btnTableTargetOpen, btnTableTargetOpen3, lb, null));
    }

    @Override
    public void configLayout() {
        var dim = new TGC_Dimension(990, 500, false);
        content = TGC_PanelAbsoluteUtils.create(dim);

        var width = 100;
        var height = 24;
        var y0 = 10;
        var y1 = 40;
        TGC_PanelAbsoluteUtils.setWidget(content, btnCancel, TGS_ShapeRectangle.of(10, y0, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSave, TGS_ShapeRectangle.of(120, y0, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSearchAll, TGS_ShapeRectangle.of(10, 450, 195, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnTableTargetOpen, TGS_ShapeRectangle.of(215, 450, 195, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnTableTargetOpen2, TGS_ShapeRectangle.of(420, 450, 195, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnTableTargetOpen3, TGS_ShapeRectangle.of(625, 450, 195, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSearchAnd, TGS_ShapeRectangle.of(625 + 195 + 10, 450, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnLinkMemoryEnabled, TGS_ShapeRectangle.of(940, 450, 40, height));
        TGC_PanelAbsoluteUtils.setWidget(content, lblHTML, TGS_ShapeRectangle.of(230, y0, 440, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnZero, TGS_ShapeRectangle.of(10, y1, 30, height));
        TGC_PanelAbsoluteUtils.setWidget(content, tbInput, TGS_ShapeRectangle.of(50, y1, 620, height));

        var x1 = 680;
        var x2 = 790;
        TGC_PanelAbsoluteUtils.setWidget(content, tbInputMin, TGS_ShapeRectangle.of(x1, y0, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, tbInputMax, TGS_ShapeRectangle.of(x2, y0, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnSearch, TGS_ShapeRectangle.of(x1, y1, width, height));
        TGC_PanelAbsoluteUtils.setWidget(content, btnMinMax, TGS_ShapeRectangle.of(x2, y1, width, height));
        panelPopup = new TGC_Pop(content, dim, null);
    }
    private AbsolutePanel content;
    private TGC_Pop panelPopup;

    @Override
    public TGC_Pop getPop() {
        return panelPopup;
    }

    public AppPopEditCellLNGLINK(AppModuleTable tm) {
        this.tm = tm;
        createWidgets();
        createPops();
        configInit();
        configActions();
        configFocus();
        configLayout();
        TGC_ThreadUtils.run_afterGUIUpdate(() -> onReloadMiddleSection.run(picEnableStatus));
    }
    private AppModuleTable tm;

    private TGS_FuncMTU onTableLinkOpen = () -> {
        btnTableTargetOpen.setEnabled(false);
        TGC_ButtonUtils.setIcon(btnTableTargetOpen, btnTableTargetOpenWait[0], btnTableTargetOpenWait[1]);
        if (!tm.subRecord.onClick_TableLinkOpen(cell.ct, null)) {
            TGC_ButtonUtils.setIcon(btnTableTargetOpen, btnTableTargetOpenFailed[0], btnTableTargetOpenFailed[1]);
        }
        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
            btnTableTargetOpen.setEnabled(true);
            TGC_ButtonUtils.setIcon(btnTableTargetOpen, btnTableTargetOpenInit[0], btnTableTargetOpenInit[1]);
        }, 5);
    };
    private TGS_FuncMTU onTableLinkOpen2 = () -> {
        String idStr = null;
        var tags = TGS_StringUtils.gwt().toList_spc(tbInput.getText());
        if (!tags.isEmpty()) {
            var tag = tags.get(0);
            var id = TGS_CastUtils.toLong(tag).orElse(null);
            if (id != null) {
                idStr = tag;
            }
        }

        btnTableTargetOpen2.setEnabled(false);
        TGC_ButtonUtils.setIcon(btnTableTargetOpen2, btnTableTargetOpenWait2[0], btnTableTargetOpenWait2[1]);
        if (!tm.subRecord.onClick_TableLinkOpen(cell.ct, idStr)) {
            TGC_ButtonUtils.setIcon(btnTableTargetOpen2, btnTableTargetOpenFailed[0], btnTableTargetOpenFailed[1]);
        }
        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
            btnTableTargetOpen2.setEnabled(true);
            TGC_ButtonUtils.setIcon(btnTableTargetOpen2, btnTableTargetOpenInit2[0], btnTableTargetOpenInit2[1]);
        }, 5);
    };
    private TGS_FuncMTU onTableLinkOpen3 = () -> {
        Long id = null;
        var tags = TGS_StringUtils.gwt().toList_spc(tbInput.getText());
        if (!tags.isEmpty()) {
            var tag = tags.get(0);
            id = TGS_CastUtils.toLong(tag).orElse(null);
        }

        btnTableTargetOpen3.setEnabled(false);
        TGC_ButtonUtils.setIcon(btnTableTargetOpen3, btnTableTargetOpenWait2[0], btnTableTargetOpenWait2[1]);
        if (id == null) {
            TGC_ButtonUtils.setIcon(btnTableTargetOpen3, btnTableTargetOpenFailed3[0], btnTableTargetOpenFailed3[1]);
        } else {
            tm.subRecord.onClick_TableLinkSniff(btnTableTargetOpen3, cell.ct, id.longValue());
        }
        TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
            btnTableTargetOpen3.setEnabled(true);
            TGC_ButtonUtils.setIcon(btnTableTargetOpen3, btnTableTargetOpenInit3[0], btnTableTargetOpenInit3[1]);
        }, 1);

    };

    private TGS_ShapeRectangle dimlbMin = TGS_ShapeRectangle.of(10, 80, 413, 360);
    private TGS_ShapeRectangle dimlbMax = TGS_ShapeRectangle.of(10, 80, 970, 360);
    private TGS_ShapeRectangle dimimageMax = TGS_ShapeRectangle.of(433, 80, 547, 410);
    private TGS_ShapeRectangle dimimageMin = TGS_ShapeRectangle.of(900, 10, 80, 60);
    private TGS_FuncMTU_In1<Boolean> onReloadMiddleSection = picEnable -> {
        TGC_PanelAbsoluteUtils.setWidget(content, lb, picEnable ? dimlbMin : dimlbMax);
        TGC_PanelAbsoluteUtils.setWidget(content, image, picEnable ? dimimageMax : dimimageMin);
    };

    private TGS_FuncMTU onImageClick = () -> {
        picEnableStatus = !picEnableStatus;
        onReloadMiddleSection.run(picEnableStatus);
    };

    private TGS_FuncMTU onReloadImage = () -> {
        d.ci("reloadImage", "#1");
        if (lb.getSelectedIndex() != -1) {
            tbInput.setText(lb.getItemText(lb.getSelectedIndex()));
        }
        d.ci("reloadImage", "#2");
        var parsedData = TGS_StringUtils.gwt().toList_spc(tbInput.getText());
        d.ci("reloadImage", "#3");
        if (parsedData.isEmpty()) {
            d.ci("reloadImage", "#4.skip");
            return;
        }
        d.ci("reloadImage", "#4", "parsedData.get(0)", parsedData.get(0));
        var valueLong = TGS_CastUtils.toLong(parsedData.get(0)).orElse(null);
        if (valueLong == null) {
            d.ci("reloadImage", "#4", "valueLong == null", parsedData.get(0));
            return;
        }
        d.ci("reloadImage", "#5");
        var tarTableName = tarTable.nameSql;
        if (!tm.dbCfg.isAny()) {
            imageHandler.reloadImageById(tarTableName, valueLong);
        }
        d.ci("reloadImage", "#6");
    };

    //DEFAULT IS FALSE
    private TGS_FuncMTU_In2<Boolean, Boolean> onRefreshLb_useBufferIfPossible_showAllIfUnderMinSize = (useBufferIfPossible, showAllIfUnderMinSize) -> {
        TGS_FuncMTCUtils.run(() -> {
            if (!useBufferIfPossible) {
                onRefreshLb_buffer_colIdx_data.removeIf(p -> p.value0.equals(cell.colIdx));
            }
            d.ci("refreshLb", "Yeni arama yapılıyor...");
            lb.clear();
            d.ci("refreshLb", "Liste güncelleniyor...");
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
                    tm.settings.searchMaxItem, tm.settings.searchMaxSecs, tbInput.getText(), btnSearchAnd.isDown());
            d.ci("refreshLb", "searchFuncNew", searchFuncNew);

            TGC_PanelStyleUtils.red(lb, tbInput, tbInputMin, tbInputMax);
            btnSearch.setEnabled(false);
            TGC_SGWTCalller.async(searchFuncNew, resp -> {
//                TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                TGC_PanelStyleUtils.remove(lb, tbInput, tbInputMin, tbInputMax);
                btnSearch.setEnabled(true);
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
                    d.ce("refreshLb", "Liste boş döndü hatası!");
                    return;
                }

                lb.clear();
                resp.getOutput_list().forEach(s -> {
                    lb.addItem(s);
                    d.ci("refreshLb", "prgGetLinkLbData", "r.getDataArrayString(i)", s);
                });

                if (resp.getOutput_isProcessedAsStar() && !resp.getOutput_list().isEmpty()) {
                    AppPopEditPopLINKBufferStar.add(resp.getInput_tarTablename(), resp.getOutput_list());
                    d.cr("refreshLb", "Arama bitti. " + TGS_Time.toString_now(), "hafızaya eklendi");
                } else {
                    d.cr("refreshLb", "Arama bitti. " + TGS_Time.toString_now());
                }
            }, thr -> {
                TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t -> {
                    TGC_PanelStyleUtils.remove(lb, tbInput, tbInputMin, tbInputMax);
                    btnSearch.setEnabled(true);
                }, 1);
            });
            d.ci("refreshLb", "#9");
        }, e -> {
            d.ct("refreshLb", e);
            d.ce("Liste yenilenirken bir hata oluştu.");
        });
        d.ci("refreshLb", "#10");
        onReloadImage.run();
        d.ci("refreshLb", "#11");
    };
    private static List<TGS_Tuple2<Integer, AppSGFCellSearch>> onRefreshLb_buffer_colIdx_data = new ArrayList();

    private TGS_FuncMTU onSearchAll = () -> {
        tbInput.setText("*");
        onRefreshLb_useBufferIfPossible_showAllIfUnderMinSize.run(false, false);
    };

    private TGS_FuncMTU_In2<Boolean, Integer> onFillTbInputRange_useBufferIfPossible_year = (useBufferIfPossible, year) -> {
        if (tarTable == null) {
            return;
        }
        if (!useBufferIfPossible) {
            onFillTbInputRange_buffer_colIdx_data.removeIf(p -> p.value0.equals(cell.colIdx));
        }
        var tarTableName = tarTable.nameSql;
        if (year == null) {
            TGC_SGWTCalller.async(new AppSGFQueryMinMaxId(tm.dbCfg, tarTableName, "LNG_ID"), response -> {
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

    public TGS_FuncMTU_In2<Boolean, String> onSetVisible = (enable, lblHtmlString) -> {
        d.ci("setVisible", enable);
        if (enable) {
            cell = (AppCell_LNGLINK) tm.cells.getActiveCell();
            lblHTML.setHTML(lblHtmlString);
            tarTable = cell.getTargetTable();
            if (tarTable == null) {
                tbInputMin.setText("");
                tbInputMax.setText("");
            } else {
                onFillTbInputRange_useBufferIfPossible_year.run(true, tarTable.autoIdDatedConfig ? TGS_Time.getCurrentYear() : null);
            }
            getPop().setVisible(true);
            lb.clear();
            tbInput.setText(cell.getValueLong() + " " + cell.getText());
            tbInput.selectAll();
            tbInput.setFocus(true);
        } else {//setVisibleDisable
            getPop().setVisible(false);
            if (cell != null) {
                TGC_FocusUtils.setFocusAfterGUIUpdate(cell);
            }
        }
        d.ci("setVisible", "fin");
    };

    private TGS_FuncMTU onCancel = () -> {
        onSetVisible.run(false, null);
    };

    private TGS_FuncMTU_In1<Long> onSetCell = valueLong -> {
        var id = AppCtrlCellRowUtils.getRowIdByIdx(tm.cells, cell.rowIdx);
        var columnname = tm.curTable.columns.get(cell.colIdx).getColumnName();
        TGC_SGWTCalller.async(new AppSGFCellUpdateLNG(tm.dbCfg, tm.curTable, id, columnname, valueLong), r -> {
            if (r.getOutput_result()) {
                d.cr("setCell", "Değişiklik yapıldı.");
                cell.setValueLong(valueLong);
            } else {
                d.ce("setCell", "HATA: bağlantı değişirken hata oluştu! (TK_GSF_SQLCellUpdateLNG)");
            }
            TGC_FocusUtils.setFocusAfterGUIUpdate(cell);
        });
        onSetVisible.run(false, null);
    };

    private TGS_FuncMTU_In1<String> onClick = inputText -> {
        d.ci("onClick", "init");
        if (inputText.isEmpty()) {
            d.ce("onClick", "UYARI: Hücre temizlendi!");
            onSetVisible.run(false, null);
            onSetCell.run(0L);
            return;
        }
        var parsedData = TGS_StringUtils.gwt().toList_spc(inputText);
        var valueLong = TGS_CastUtils.toLong(parsedData.get(0)).orElse(null);
        if (valueLong == null) {
            d.ce("onClick", "HATA: İlk kelime ID sayısı olmalıydı hatası!");
        } else {
            onSetCell.run(valueLong);
        }
        onSetVisible.run(false, null);
    };

    private final TGS_FuncMTU onZero = () -> {
        tbInput.setText("0");
        lb.clear();
    };
    //SHORCUTS
    private final TGS_FuncMTU onSaveLb = () -> {
        d.ci("onSaveLb", "init");
        onClick.run(lb.getItemText(lb.getSelectedIndex()));
    };
    private final TGS_FuncMTU onMinMax = () -> {
        onFillTbInputRange_useBufferIfPossible_year.run(false, null);
    };
    private final TGS_FuncMTU onSaveBtn = () -> {
        onClick.run(tbInput.getText().trim());
    };
    private final TGS_FuncMTU onSearch = () -> {
        onRefreshLb_useBufferIfPossible_showAllIfUnderMinSize.run(true, false);
    };

    private final TGS_FuncMTU_In1<Boolean> onSearchAnd = isDown -> {
        if (isDown) {
            TGC_ButtonUtils.setIcon(btnSearchAnd, TGS_IconUtils.CLASS_BOOK(), "Toplu Ara");
        } else {
            TGC_ButtonUtils.setIcon(btnSearchAnd, TGS_IconUtils.CLASS_BOOKS(), "Parçalı Ara");
        }
    };
}
