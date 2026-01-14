package com.tugalsan.app.table.resposive.cooking;

public class Cooking_ASWModuleTableModify {//implements TGC_PanelInterface, TGS_FuncMTU {

//    final private static TGC_Log d = TGC_Log.of(Cooking_ASWModuleTableModify);
//
//    @Override
//    public String getBrowserTitle() {
//        return curTable == null ? "curTable == null" : curTable.nameReadable;
//    }
//
//    @Override
//    public void run() {
//        d.ce("run", "loadCookie...");
//        loadCookie();
//        d.ce("run", "initControllers...");
//        initControllers(() -> {
//            d.ce("run", "createWidgets...");
//            createWidgets();
//            d.ce("run", "createPops...");
//            createPops();
//            d.ce("run", "configInit...");
//            configInit();
//            d.ce("run", "configActions...");
//            configActions();
//            d.ce("run", "configFocus...");
//            configFocus();
//            d.ce("run", "configLayout...");
//            configLayout();
//            d.ce("run", "setFocusAfterGUIUpdate...");
//            TGC_FocusUtils.setFocusAfterGUIUpdate(btnFilter);
//            d.ce("run", "fin");
//        });
//    }
//
//    @Override
//    public void loadCookie() {
//        TGC_LibBootModuleUtils.pop(pd -> {
//            d.ci("module", pd);
//            curTableName = pd.find(PARAM_CURRENT_TABLE_NAME);
//            var rs = pd.find(PARAM_ROW_SIZE);
//            rowSize = TGS_CastUtils.toInteger(rs);
//            d.ci("module", "rowSize", rowSize);
//        });
//    }
//    final public static String PARAM_CURRENT_TABLE_NAME = "curTableName";
//    final public static String PARAM_ROW_SIZE = "rowSize";
//    private String curTableName;
//    private Integer rowSize;
//
//    public void initControllers(TGS_FuncMTU afterExe) {
//        curTable = ASWPackUtils.getTable(curTableName);
//        d.ci("initControllers", "curTable", curTable);
//        Cooking_ASWSQLTableUtils.initialize(curTable, afterExe);
//        d.ci("initControllers", "fin");
//    }
//    public TGS_LibRqlTbl curTable;
//
//    @Override
//    public void createWidgets() {
//        btnFilter = new PushButton("Süz");
//        btnPageFirst = new PushButton("|<");
//        btnPagePrev = new PushButton("<");
//        btnRefresh = new PushButton("Sayfa 1/1");
//        btnPageNext = new PushButton(">");
//        btnPageLast = new PushButton(">|");
//        btnAdd = new PushButton("Ekle");
//        btnDelete = new PushButton("Sil");
//        menu = new TGC_MenuPC();
//        table = TGC_TableUtils.createTable(rowSize, curTable.getColumnNames_getClone(), colTitle -> d.cr("colTitleClick", colTitle));
//    }
//    public PushButton btnFilter, btnPageFirst, btnPagePrev, btnRefresh, btnPageNext, btnPageLast, btnAdd, btnDelete;
//    public TGS_Tuple2<CellTable<TGC_TableRow>, List<TGC_TableRow>> table;
//    public TGC_MenuPC menu;
//
//    @Override
//    public void createPops() {
//    }
//
//    @Override
//    public void configInit() {
//        var colWidthPx = new int[Cooking_ASWSQLTableUtils.colSizeFull];
//        Arrays.fill(colWidthPx, 100);
//        IntStream.range(0, Cooking_ASWSQLTableUtils.colSizeFull).parallel().filter(i -> Cooking_ASWSQLTableUtils.columnHiddenClientOrder[i]).forEach(i -> colWidthPx[i] = 0);
//        TGC_TableUtils.setColumnWidthsPx(table.value0, colWidthPx);
//        IntStream.range(0, rowSize).parallel().forEach(ri -> IntStream.range(0, Cooking_ASWSQLTableUtils.colSizeFull).parallel().forEach(ci -> table.value1.get(ri).cells[ci] = TGS_LoremIpsum.getWords(3, 10).toString()));
//        TGC_TableUtils.refreshData(table.value0, table.value1);
//    }
//
//    @Override
//    public void configActions() {
//        TGC_TableUtils.addOnSelectAction(table.value0, () -> d.cr("onSelect", TGC_TableUtils.getSelectedRow(table.value0).rowIdx));
//    }
//
//    @Override
//    public void configFocus() {
//    }
//
//    @Override
//    public void configLayout() {
//        Widget[] btnListOp = {btnFilter, btnPageFirst, btnPagePrev, btnRefresh, btnPageNext, btnPageLast, btnAdd, btnDelete, menu.widget};
//        widget = TGC_PanelLayoutUtils.createDockNorth(
//                1,
//                new ScrollPanel(TGC_PanelLayoutUtils.createHorizontal(null, btnListOp)),
//                new ScrollPanel(table.value0)
//        );
//        TGC_LibBootGUIBody.setToCenter(widget);//DONT DELETE, LAZY UPDATE
//    }
//
//    @Override
//    public Widget getWidget() {
//        return widget;
//    }
//    public DockLayoutPanel widget;
}
