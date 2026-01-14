package com.tugalsan.app.table;

import com.tugalsan.lib.rql.client.*;
import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.charset.client.*;
import com.tugalsan.api.file.client.*;
import com.tugalsan.api.file.html.client.*;
import com.tugalsan.api.gui.client.browser.*;
import com.tugalsan.api.gui.client.card.*;
import com.tugalsan.api.gui.client.click.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.tuple.client.TGS_Tuple3;
import com.tugalsan.lib.boot.client.*;
import com.tugalsan.lib.resource.client.*;
import com.tugalsan.lib.table.client.TGS_LibTableAppUtils;
import java.util.*;

public class AppModuleDefault extends TGC_LibBootModulePanel {

    final private static TGC_Log d = TGC_Log.of(AppModuleDefault.class);
    public static boolean showOptions = false;
    public static boolean showReadOnly = false;
    public static boolean showEditable = true;

    @Override
    public String getBrowserTitle() {
        return TGC_LibBootGUITitleUtils.browserTitle_domain_txt_user(
                App.domainCard, App.loginCard, App.route,
                AppStrings.TITLE_AppModuleDefault(),
                null
        );
    }

    @Override
    public String getBodyTitle() {
        return new TGS_FileHtmlText().setText(
                TGC_LibBootGUITitleUtils.bodyTitle_cloudlink_sub_user(
                        App.loginCard, App.route,
                        AppStrings.TITLE_AppModuleDefault(),
                        null
                )
        ).setBold(true).toString();
    }

    @Override
    public void run() {
        d.ci("run", "triggering", "loadParams");
        loadParams();
        d.ci("run", "triggering", "createWidgets");
        createWidgets();
        d.ci("run", "triggering", "createPops");
        createPops();
        d.ci("run", "triggering", "configInit");
        configInit();
        d.ci("run", "triggering", "configActions");
        configActions();
        d.ci("run", "triggering", "configFocus");
        configFocus();
        d.ci("run", "triggering", "configLayout");
        configLayout();
        d.ci("run", "triggering", "done");
//        TGC_FocusUtils.setFocusAfterGUIUpdate(taSend);
    }

    @Override
    public void loadParams() {
    }

    @Override
    public void createWidgets() {
        d.ci("createWidgets", "#100");
        tbRowCount = new TextBox();
        tbRowCount.setText(String.valueOf(AppModuleTable.DEFAULT_ROW_SIZE()));
        lblRowCount = new HTML("Satır sayısı:");
        cbShowReadOnly = TGC_CheckBoxUtils.createIcon(TGS_IconUtils.CLASS_FILTER(), "Okunabilir");
        cbShowEditable = TGC_CheckBoxUtils.createIcon(TGS_IconUtils.CLASS_FILTER(), "Güncellenbilir");
        if (App.loginCard.userAdmin) {
            cbShowOptions = TGC_CheckBoxUtils.createIcon(TGS_IconUtils.CLASS_HAMMER(), "Seçenekler");
        }

        search_png = TGS_LibResourceUtils.common.res.image.search.icon_png().toString();

        d.ci("createWidgets", "#200");
        //ADD USER APPS
        scroll = new ScrollPanel();
        var pack = pack(scroll, search_png);
        d.ci("createWidgets", "#300");
        tbInput = pack.value0;
        pnlCards = pack.value1;
        if (pnlCards.getWidgetCount() == 0) {
            d.ce("createWidgets", "HATA: Kullanıcıya tanımlı tablo bulunamadı! Kullanıcı çıkmış olabilir.");
        }
    }
    private TextBox tbInput;
    private FlowPanel pnlCards;
    private HTML lblRowCount;
    private TextBox tbRowCount;
    private ScrollPanel scroll;
    private CheckBox cbShowOptions;
    private CheckBox cbShowReadOnly;
    private CheckBox cbShowEditable;
    private String search_png;

    private String getPngName(String tng, String tns) {
        var tnsAsPngName = Arrays.asList(
                "tamanishopconfig", "tamanishopproducts", "tburetim", "yburetim"
        ).stream().anyMatch(s -> Objects.equals(tns, s));
        if (tnsAsPngName) {
            return tns;
        }
//        d.ce("getPngName", "notPresent", tns);
        var defChar = '-';
        var imgFg = TGS_FileUtilsTur.toSafe(TGS_CharSetCast.current().toLowerCase(tng), defChar);
        d.ci("createWidgets", "imgFg", "#0", imgFg, tns);
        if (imgFg.length() > 5) {
            imgFg = imgFg.substring(0, 5);
        }
        d.ci("createWidgets", "imgFg", "#1", imgFg);
        imgFg = imgFg.replace(' ', defChar);
        d.ci("createWidgets", "imgFg", "#2", imgFg);
        if (Objects.equals(imgFg, "ky-i-")) {//becasue of TGS_FileUtilsTur.toSafe
            imgFg = "ky-in";
        }
        if (imgFg.endsWith("g-tb")) {//becasue of it was ÜG TB/YB originally
            imgFg = "üg-tb-yb";
        }
        d.ci("createWidgets", "imgFg", "#3", imgFg);
        return imgFg;
    }

    @Override
    public void createPops() {
    }

    @Override
    public void configInit() {
        if (App.loginCard.userAdmin) {
            cbShowOptions.setValue(showOptions);
        }
        cbShowReadOnly.setValue(showReadOnly);
        cbShowEditable.setValue(showEditable);
    }

    @Override
    public void configActions() {
        TGC_TextBoxUtils.onChange(tbRowCount, () -> {
            var val = TGS_CastUtils.toInteger(tbRowCount.getText()).orElse(null);
            if (val == null) {
                d.ce("configActions", "HATA: Satır parametresi bir tam sayı olmalı!");
                tbRowCount.setText(String.valueOf(AppModuleTable.DEFAULT_ROW_SIZE()));
                return;
            }
            if (val < 1) {
                d.ce("configActions", "HATA: Satır parametresi 1'den büyük olmalı!");
                tbRowCount.setText("1");
                return;
            }
            if (val > AppModuleTable.MAX_ROW_SIZE()) {
                d.ce("configActions", "HATA: Satır parametresi limit dışı!");
                tbRowCount.setText(String.valueOf(AppModuleTable.MAX_ROW_SIZE()));
                return;
            }
        });
        if (App.loginCard.userAdmin) {
            TGC_ClickUtils.add(cbShowOptions, () -> {
                showOptions = !showOptions;
                d.cr("configActions", "Seçenekler", showOptions ? "açık" : "kapalı");
            });
        }
        TGC_ClickUtils.add(cbShowReadOnly, () -> {
            showReadOnly = !showReadOnly;
            d.cr("configActions", "Okunabilir", showReadOnly ? "açık" : "kapalı");
            refreshCards(scroll, search_png);
        });
        TGC_ClickUtils.add(cbShowEditable, () -> {
            showEditable = !showEditable;
            d.cr("configActions", "Güncellenebilir", showEditable ? "açık" : "kapalı");
            refreshCards(scroll, search_png);
        });
    }

    @Override
    public void configFocus() {
    }

    @Override
    public void configLayout() {
        if (App.loginCard.userAdmin) {
            cbShowOptions.addStyleName("AppModuleTable_btn2");
            TGC_LibBootGUIBody.buttonHolderCenter.add(cbShowOptions);
        } else {
            cbShowReadOnly.addStyleName("AppModuleTable_btn2");
            TGC_LibBootGUIBody.buttonHolderCenter.add(cbShowReadOnly);
        }

        cbShowEditable.addStyleName("AppModuleTable_btn2");
        TGC_LibBootGUIBody.buttonHolderCenter.add(cbShowEditable);

        lblRowCount.addStyleName("AppModuleTable_lbl");
        TGC_LibBootGUIBody.buttonHolderCenter.add(lblRowCount);

        TGC_TextBoxUtils.setTypeNumber(tbRowCount, 1, null, AppModuleTable.MAX_ROW_SIZE());
        tbRowCount.addStyleName("AppModuleTable_tbInt");
        TGC_LibBootGUIBody.buttonHolderCenter.add(tbRowCount);

        styleTbInput();
        TGC_LibBootGUIBody.buttonHolderCenter.add(tbInput);

        scroll.setWidget(pnlCards);
        TGC_ScrollUtils.addScrollToTop(scroll);

        widget = scroll;
    }
    public Widget widget;

    @Override
    public Widget getWidget() {
        return widget;
    }

    //----------------------------- UTILS -------------------------------------
    private void styleTbInput() {
        tbInput.addStyleName("AppModuleTable_input");
    }

    private void refreshCards(ScrollPanel scroll, CharSequence search_png) {
        d.ci("refreshCards", "#300");
        TGC_LibBootGUIBody.buttonHolderCenter.remove(tbInput);
        var pack = pack(scroll, search_png);
        tbInput = pack.value0;
        styleTbInput();
        TGC_LibBootGUIBody.buttonHolderCenter.add(tbInput);
        pnlCards = pack.value1;
        scroll.setWidget(pnlCards);
    }

    final public TGS_Tuple3<TextBox, FlowPanel, List<TGC_Card>> pack(ScrollPanel scroll, CharSequence search_png) {
        return TGC_CardUtils.create(scroll, null, search_png, cards -> {
            d.ci("create", "#200", "card", "#init");
            var spc = TGS_FileHtmlText.charSpace();
            var preAdmin = spc + TGS_IconUtils.createSpan(TGS_IconUtils.CLASS_TERMINAL()) + spc;
            var preGroup = spc + TGS_IconUtils.createSpan(TGS_IconUtils.CLASS_PUSHPIN()) + spc;
            var preTable = spc + TGS_IconUtils.createSpan(TGS_IconUtils.CLASS_TABLE2()) + spc;
            var ln = TGS_FileHtmlText.charLn();
            d.ci("create", "#200", "card", "#forEach");
            getGroupsOfOpenTables().forEach(gn -> {
                d.ci("create", "#200", "card", "#forEach", "cardAdd");
                cards.add(new TGC_Card(
                        TGS_LibResourceUtils.png("group"),
                        null,
                        new TGS_FileHtmlText().setText(preGroup + gn).setBold(true).toString()
                ));
                d.ci("create", "#200", "card", "#forEach", "forEach");
                App.userTableConfig.stream().forEach(cfg -> {
                    var tableReadOnly = cfg.editableDays == null || cfg.editableDays == 0;
                    var tableEditable = cfg.editableDays != null && cfg.editableDays != 0;

                    if (showReadOnly && showEditable) {
                        //DO NOTHING
                    } else if (showReadOnly) {
                        if (!App.loginCard.userAdmin && !tableReadOnly) {
                            return;
                        }
                    } else if (showEditable) {
                        if (!App.loginCard.userAdmin && !tableEditable) {
                            return;
                        }
                    } else {
                        return;
                    }

                    var t = cfg.table;
                    String tns = t.nameSql;
                    var tng = t.nameGroup;
                    var tnv = t.nameReadable;
                    if (!Objects.equals(tng, gn)) {
                        return;
                    }
                    if (!App.loginCard.userAdmin) {
                        if (tns.startsWith("login")) {
                            return;
                        }
                        if (tns.startsWith("common")) {
                            return;
                        }
                        if (tns.startsWith("config")) {
                            return;
                        }
                    }
                    cards.add(new TGC_Card(
                            TGS_LibResourceUtils.png(getPngName(tng, tns)),
                            null,
                            new TGS_FileHtmlText().setText(
                                    App.loginCard.userAdmin
                                            ? preAdmin + tns + ln + preGroup + tng + ln + preTable + tnv
                                            : preGroup + tng + ln + preTable + tnv
                            ).setBold(true).toString(),
                            cardAction(t)
                    ));
                });
            });
        });
    }

    private List<String> getGroupsOfOpenTables() {
        List<String> groups = TGS_ListUtils.of();
        App.userTableConfig.stream()
                .map(cfg -> cfg.table)
                .forEachOrdered(t -> {//Fill Groups
                    var groupCandidate = t.nameGroup;
                    var find = groups.stream().filter(g -> Objects.equals(g, groupCandidate)).findAny();
                    if (!find.isPresent()) {
                        groups.add(groupCandidate);
                    }
                });
        return groups;
    }

    private TGS_FuncMTU cardAction(TGS_LibRqlTbl t) {
        return () -> {
            var tn = t.nameSql;
            if (!showOptions) {
                TGC_BrowserWindowUtils.openNew(
                        App.route.setMdl(AppModuleTable.class.getSimpleName())
                                .setParam(TGS_LibTableAppUtils.PARAM_CURRENT_TABLE_NAME(), tn)
                                .setParam(TGS_LibTableAppUtils.PARAM_CELLS_ROW_SIZE(), tbRowCount.getText())
                                .setParam(TGS_LibTableAppUtils.PARAM_MODE(), "")
                                .toUrl()
                );
                return;
            }
            new TGC_PopLblYesNoListBox(TGC_Dimension.FULLSCREEN,
                    TGS_ListUtils.of(
                            "Tabloyu aç",//0
                            "REV: Revizyonları aç",//1
                            "TXT: Uzun yazıları aç",//2
                            "ALW: İzinleri aç",//3
                            "RPT: Raporları aç",//4
                            "CFG: Ayarları aç"//5
                    ),
                    "Aksiyon", "Seç", "İptal",
                    p -> {
                        d.ci("createCard", "ok", "begin");
                        p.getPop().setVisible(false);
                        var si = p.listBox.getSelectedIndex();
                        if (si == -1) {
                            d.ce("createCard", "Seçim yapılmadı.", si);
                            return;
                        }
                        d.ci("createCard", "si", si);
                        if (si == 0) {
                            TGC_BrowserWindowUtils.openNew(
                                    App.route.setMdl(AppModuleTable.class.getSimpleName())
                                            .setParam(TGS_LibTableAppUtils.PARAM_CURRENT_TABLE_NAME(), tn)
                                            .setParam(TGS_LibTableAppUtils.PARAM_CELLS_ROW_SIZE(), tbRowCount.getText())
                                            .setParam(TGS_LibTableAppUtils.PARAM_MODE(), "")
                                            .toUrl()
                            );
                            return;
                        }
                        if (si == 1) {
                            TGC_BrowserWindowUtils.openNew(
                                    App.route.setMdl(AppModuleTable.class.getSimpleName())
                                            .setParam(TGS_LibTableAppUtils.PARAM_CURRENT_TABLE_NAME(), tn)
                                            .setParam(TGS_LibTableAppUtils.PARAM_CELLS_ROW_SIZE(), tbRowCount.getText())
                                            .setParam(TGS_LibTableAppUtils.PARAM_MODE(), "rev")
                                            .toUrl()
                            );
                            return;
                        }
                        if (si == 2) {
                            TGC_BrowserWindowUtils.openNew(
                                    App.route.setMdl(AppModuleTable.class.getSimpleName())
                                            .setParam(TGS_LibTableAppUtils.PARAM_CURRENT_TABLE_NAME(), tn)
                                            .setParam(TGS_LibTableAppUtils.PARAM_CELLS_ROW_SIZE(), tbRowCount.getText())
                                            .setParam(TGS_LibTableAppUtils.PARAM_MODE(), "txt")
                                            .toUrl()
                            );
                            return;
                        }
                        if (si == 3) {
                            TGC_BrowserWindowUtils.openNew(
                                    App.route.setMdl(AppModuleTable.class.getSimpleName())
                                            .setParam(TGS_LibTableAppUtils.PARAM_CURRENT_TABLE_NAME(), tn)
                                            .setParam(TGS_LibTableAppUtils.PARAM_CELLS_ROW_SIZE(), tbRowCount.getText())
                                            .setParam(TGS_LibTableAppUtils.PARAM_MODE(), "alw")
                                            .toUrl()
                            );
                            return;
                        }
                        if (si == 4) {
                            TGC_BrowserWindowUtils.openNew(
                                    App.route.setMdl(AppModuleTable.class.getSimpleName())
                                            .setParam(TGS_LibTableAppUtils.PARAM_CURRENT_TABLE_NAME(), tn)
                                            .setParam(TGS_LibTableAppUtils.PARAM_CELLS_ROW_SIZE(), tbRowCount.getText())
                                            .setParam(TGS_LibTableAppUtils.PARAM_MODE(), "rpt")
                                            .toUrl()
                            );
                            return;
                        }
                        if (si == 5) {
                            TGC_BrowserWindowUtils.openNew(
                                    App.route.setMdl(AppModuleTable.class.getSimpleName())
                                            .setParam(TGS_LibTableAppUtils.PARAM_CURRENT_TABLE_NAME(), tn)
                                            .setParam(TGS_LibTableAppUtils.PARAM_CELLS_ROW_SIZE(), tbRowCount.getText())
                                            .setParam(TGS_LibTableAppUtils.PARAM_MODE(), "cfg")
                                            .toUrl()
                            );
                            return;
                        }
                        d.ce("cardAction", "HATA: Listenden seçim yapılmalı!");
                    },
                    p -> p.getPop().setVisible(false),
                    null, null
            ).getPop().setVisible(true);
        };
    }
}
