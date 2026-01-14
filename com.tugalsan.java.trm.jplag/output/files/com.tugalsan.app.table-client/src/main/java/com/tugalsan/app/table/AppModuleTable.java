package com.tugalsan.app.table;

import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.file.html.client.*;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTUEffectivelyFinal;
import com.tugalsan.api.gui.client.browser.*;
import com.tugalsan.api.gui.client.dom.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.theme.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.app.table.control.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.lib.boot.client.*;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.table.client.*;
import java.util.*;
import java.util.stream.*;

public class AppModuleTable extends TGC_LibBootModulePanel {

    final private static TGC_Log d = TGC_Log.of(false, AppModuleTable.class);

    public static int DEFAULT_ROW_SIZE() {
        return TGC_BrowserNavigatorUtils.mobile() ? 6 : 12;
    }

    public static int MAX_ROW_SIZE() {
        return 99;
    }

    @Override
    public String getBrowserTitle() {
        var title = curTable.nameReadable;
        return TGC_LibBootGUITitleUtils.browserTitle_domain_txt_user(
                App.domainCard, App.loginCard, App.route,
                App.loginCard.userAdmin ? (title + " (" + App.curTableName + ")") : title,
                null
        );
    }

    @Override
    public String getBodyTitle() {
        var titlePrefix = curTable.nameGroup;
        if (dbCfg.rev) {
            titlePrefix = "Rev.of";
        }
        if (dbCfg.txt) {
            titlePrefix = "Txt.of";
        }
        if (dbCfg.alw) {
            titlePrefix = "Alw.of";
        }
        if (dbCfg.rpt) {
            titlePrefix = "Rpt.of";
        }
        if (dbCfg.cfg) {
            titlePrefix = "Cfg.of";
        }
        var title = TGS_StringUtils.cmn().concat(
                titlePrefix,
                TGS_FileHtmlText.charSpace(), TGS_FileHtmlText.charRightArrow(), TGS_FileHtmlText.charSpace(),
                curTable.nameReadable
        );
        return new TGS_FileHtmlText().setText(
                TGC_LibBootGUITitleUtils.bodyTitle_cloud_app_sub_user(
                        App.loginCard, App.route,
                        AppStrings.TITLE_AppModuleDefault(),
                        App.loginCard.userAdmin ? (title + " (" + App.curTableName + ")") : title,
                        null
                )
        ).setBold(true).toString();
    }

    @Override
    public void run() {
        loadParams();
        if (!App.loginCard.userAdmin) {
            var tns = curTable.nameSql;
            if (tns.startsWith("login")) {
                d.ce("run", "security error");
                return;
            }
            if (tns.startsWith("common")) {
                d.ce("run", "security error");
                return;
            }
            if (tns.startsWith("config")) {
                d.ce("run", "security error");
                return;
            }
        }
        AppModuleTableUtils.initialize(() -> {
            d.ci("TMModuleTableModify", "initControllers...");
            initControllers();
            d.ci("TMModuleTableModify", "createWidgets...");
            createWidgets();
            d.ci("TMModuleTableModify", "createPops...");
            createPops();
            d.ci("TMModuleTableModify", "configInit...");
            configInit();
            d.ci("TMModuleTableModify", "configActions...");
            configActions();
            d.ci("TMModuleTableModify", "configFocus...");
            configFocus();
            d.ci("TMModuleTableModify", "configLayout...");
            configLayout();
            d.ci("TMModuleTableModify", "page.update...");
            page.update(() -> {
                d.ci("TMModuleTableModify", "page.update", "fin");
                AppModuleTableUtils.printKeyboardUsageIfNotMobile();
                TGC_FocusUtils.setFocusAfterGUIUpdate(filter.btn);
            }, false, true);
            d.ci("TMModuleTableModify", "fin");
        });
    }
    public TGS_LibRqlTbl curTable;

    @Override
    public void loadParams() {
        filterIdStart = App.route.getParamStr(TGS_LibTableAppUtils.PARAM_FILTER_ID_START());
        filterIdEnd = App.route.getParamStr(TGS_LibTableAppUtils.PARAM_FILTER_ID_END());

        cellsRowSize = App.route.getParamInt(TGS_LibTableAppUtils.PARAM_CELLS_ROW_SIZE());
        if (TGS_StringUtils.cmn().isNullOrEmpty(App.curTableName)) {
            d.ce("loadParams", "HATA: Tablo adı parametresi boş olamaz!");
        }
        if (cellsRowSize < 1) {
            cellsRowSize = DEFAULT_ROW_SIZE();
            d.ce("loadParams", "HATA: Satır parametresi 1 den büyük tam sayı olmalıydı!", App.route.getParamStr(TGS_LibTableAppUtils.PARAM_CELLS_ROW_SIZE()), "Parametre tekrar ayarlandı", cellsRowSize);
        } else if (cellsRowSize > MAX_ROW_SIZE()) {
            cellsRowSize = MAX_ROW_SIZE();
            d.ce("loadParams", "HATA: Satır parametresi limit dışında!", App.route.getParamStr(TGS_LibTableAppUtils.PARAM_CELLS_ROW_SIZE()), "Parametre tekrar ayarlandı", cellsRowSize);
        }

        var mode = App.route.getParamStr(TGS_LibTableAppUtils.PARAM_MODE());
        dbCfg = new TGS_LibTableDbSub(
                App.loginCard.userAdmin ? Objects.equals(mode, "rev") : false,
                App.loginCard.userAdmin ? Objects.equals(mode, "txt") : false,
                App.loginCard.userAdmin ? Objects.equals(mode, "alw") : false,
                App.loginCard.userAdmin ? Objects.equals(mode, "rpt") : false,
                App.loginCard.userAdmin ? Objects.equals(mode, "cfg") : false
        );
        if (dbCfg.isAny()) {
            d.ci("loadParams", dbCfg, "converting...");
            IntStream.range(0, App.tables.size()).forEach(i -> {
                App.tables.set(i, TGS_LibTableDbSubUtils.toConvert(
                        dbCfg, App.tables.get(i)
                ));
            });
            IntStream.range(0, App.userTableConfig.size()).forEach(i -> {
                var cfg = App.userTableConfig.get(i);
                cfg.table = TGS_LibTableDbSubUtils.toConvert(dbCfg, cfg.table);
            });
        } else {
            d.ci("loadParams", dbCfg);
        }
        curTable = App.tbl_mayThrow(App.curTableName);
    }
    private String filterIdStart;
    private String filterIdEnd;
    public int cellsRowSize;
    public TGS_LibTableDbSub dbCfg;

  

    public void initControllers() {
        d.ci("initControllers", "title...");
        d.ci("initControllers", "content...");
        content = TGC_PanelAbsoluteUtils.create(null);//BEFORE ABS WIDGETS, FOR NOW!
        d.ci("initControllers", "filter...");
        filter = new AppCtrlFilter(this);
        d.ci("initControllers", "page...");
        page = new AppCtrlPage(this);
        d.ci("initControllers", "add...");
        add = new AppCtrlRowAdd(this);
        d.ci("initControllers", "op...");
        operations = new AppCtrlOperation(this);
        d.ci("initControllers", "settings...");
        settings = new AppCtrlSettings(this);
        d.ci("initControllers", "report...");
        report = new AppCtrlReport(this);
        d.ci("initControllers", "input...");
        input = new AppCtrlInput(this);
        d.ci("initControllers", "subRecord...");
        subRecord = new AppCtrlSubRecord(this);
        d.ci("initControllers", "showKeys...");
        showKeys = new AppCtrlShowKeys(this);
        d.ci("initControllers", "pageHeader...");
        pageHeader = new AppCtrlPageHeader(this);
        d.ci("initControllers", "fin");
    }
    public AbsolutePanel content;
    public AppCtrlFilter filter;
    public AppCtrlPage page;
    public AppCtrlRowAdd add;
    public AppCtrlOperation operations;
    public AppCtrlSettings settings;
    public AppCtrlReport report;
    public AppCtrlInput input;
    public AppCtrlSubRecord subRecord;
    public AppCtrlShowKeys showKeys;
    public AppCtrlPageHeader pageHeader;
//    public Integer rowSize;

    @Override
    public void createWidgets() {
        d.ci("createWidgets", "#0");
        filter.createWidgets();
        d.ci("createWidgets", "#1");
        page.createWidgets();
        d.ci("createWidgets", "#2");
        add.createWidgets();
        d.ci("createWidgets", "#3");
        settings.createWidgets();
        d.ci("createWidgets", "#5");
        operations.createWidgets();
        d.ci("createWidgets", "#4");
        report.createWidgets();
        d.ci("createWidgets", "#6");
        AppCtrlCellCreateUtils.createWidgets(this);
        d.ci("createWidgets", "#7");
    }
    public AppCtrlCell cells;

    @Override
    public void createPops() {
        filter.createPops();
        settings.createPops();
        operations.createPops();
        report.createPops();
        AppCtrlCellCreateUtils.createPops(this);
        input.createPops();
        subRecord.createPops();
    }

    @Override
    public void configInit() {
        d.ci("configInit", "filter.configInit...");
        filter.configInit(filterIdStart, filterIdEnd);
        d.ci("configInit", "page.configInit...");
        page.configInit(filterIdStart, filterIdEnd);
        d.ci("configInit", "settings.configInit...");
        settings.configInit();
        d.ci("configInit", "operations.configInit...");
        operations.configInit();
        d.ci("configInit", "report.configInit...");
        report.configInit();
        d.ci("configInit", "input.configInit...");
        input.configInit();
        d.ci("configInit", "subRecord.configInit...");
        subRecord.configInit();
        d.ci("configInit", "showKeys.configInit...");
        showKeys.configInit();
        d.ci("configInit", "AppCtrlCellConfigUtils.configInit...");
        AppCtrlCellConfigUtils.configInit(this);
    }

    @Override
    public void configActions() {
        filter.configActions();
        page.configActions();
        add.configActions();
        settings.configActions();
        operations.configActions();
        report.configActions();
        showKeys.configActions();
        AppCtrlCellActionUtils.configActions(this);
    }

    @Override
    public void configFocus() {
        filter.configFocus();
        page.configFocus();
        add.configFocus();
        settings.configFocus();
        operations.configFocus();
        report.configFocus();
        AppCtrlCellFocusUtils.configActions(this);
    }

    @Override
    public void configLayout() {
        //content = TGC_PanelAbsoluteUtils.create(null);//ALREADY DONE!
        content.setStyleName(TGC_Panel.class.getSimpleName());

        AppCtrlCellLayoutUtils.cells_visibility_refresh(this, null);

        filter.configLayout(TGC_LibBootGUIBody.buttonHolderLeft);
        page.configLayout(TGC_LibBootGUIBody.buttonHolderLeft);
        add.configLayout(TGC_LibBootGUIBody.buttonHolderLeft);
        settings.configLayout(TGC_LibBootGUIBody.buttonHolderLeft);
        operations.configLayout(TGC_LibBootGUIBody.buttonHolderLeft);
        report.configLayout(TGC_LibBootGUIBody.buttonHolderLeft);

        var scroll = (ScrollPanel) widget;
        scroll.setWidget(content);
        var maxWidth = TGS_FuncMTCUtils.call(() -> {
            var scrollWidth = TGC_DOMUtils.getWidth(scroll.getElement());//problem on scroll.getElement().getStyle()
            var contentWidth = TGC_DOMUtils.getWidth(content.getElement());
            return TGS_FuncMTUEffectivelyFinal.of(Integer.class)
                    .anointAndCoronateIf(val -> scrollWidth == null, val -> contentWidth)
                    .anointAndCoronateIf(val -> contentWidth == null, val -> scrollWidth)
                    .coronateAs(val -> Math.max(scrollWidth, scrollWidth));
        }, e -> TGC_DOMUtils.getWidth(content.getElement()));
        scroll.addStyleName(maxWidth > 1000 ? "AppModule_configLayout2" : "AppModule_configLayout");
        TGC_ScrollUtils.addScrollToTop(scroll);
    }
    private final Widget widget = new ScrollPanel();

    @Override
    public Widget getWidget() {
        return widget;
    }
}
