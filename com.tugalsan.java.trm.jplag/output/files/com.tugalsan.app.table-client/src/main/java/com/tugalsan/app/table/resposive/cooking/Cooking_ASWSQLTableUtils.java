package com.tugalsan.app.table.resposive.cooking;

public class Cooking_ASWSQLTableUtils {

//    final private static TGC_Log d = TGC_Log.of(Cooking_ASWSQLTableUtils.class);
//
//    public static void initialize(TGS_LibRqlTbl curTable, TGS_FuncMTU afterExe) {
//        d.ci("initialize", "initializeTableCommonSettings...");
//        initializeTableCommonSettings(curTable);
//        d.ci("initialize", "initializeHiddenServerOrder...");
//        initializeHiddenServerOrder(curTable);
//        d.ci("initialize", "initializeHiddenUserOrder...");
//        initializeHiddenUserOrder();
//        d.ci("initialize", "initializeGetColumnOrderSettings...");
//        initializeGetColumnOrderSettings(curTable, r -> {
//            d.ci("initialize", "initializeColumnServerOrder...");
//            initializeColumnServerOrder(r);
//            d.ci("initialize", "initializeHiddenAndColumnClientOrder...");
//            initializeHiddenAndColumnClientOrder();
//            d.ci("initialize", "afterExe...");
//            afterExe.run();
//        });
//    }
//
//    public static Integer findIndexOnUserTableOpen(String tableName) {
//        var o = IntStream.range(0, ASWPackUtils.pack.getOutput_userTables().size()).filter(i -> Objects.equals(ASWPackUtils.pack.getOutput_userTables().get(i).nameSql, tableName)).findAny();
//        return o.isPresent() ? o.getAsInt() : null;
//    }
//
//    private static void initializeTableCommonSettings(TGS_LibRqlTbl curTable) {
//        colSizeFull = curTable.getColumns().size();
//        tableOpenIdx = findIndexOnUserTableOpen(curTable.nameSql);
//        if (tableOpenIdx == null) {
//            TGS_FuncUtils.catch("ASWSQLTableUtils.initialize->ERROR: tableOpenIdx returns null for " + curTable.nameSql);
//        }
//    }
//    public static int colSizeFull;
//    public static Integer tableOpenIdx;
//
//    private static void initializeGetColumnOrderSettings(TGS_LibRqlTbl curTable, TGS_FuncMTU_In1<TGS_FConfigValueGet> afterExe) {
//        TGC_SGWTCalller.async(new TGS_FConfigValueGet(TGS_LibRqlTbl.SQL_TABLE_PREFIX + curTable.nameSql, ".columnOrder"), r -> {
//            if (r.getOutput_id() == null) {
//                TGS_FuncUtils.catch("ASWSQLTableUtils.initializeGetColumnOrderSettings->ERROR: columnOrder returns null, please recreateTables for " + curTable.nameSql);
//            }
//            afterExe.run(r);
//        });
//    }
//
//    private static void initializeHiddenServerOrder(TGS_LibRqlTbl curTable) {
//        colSizeVisible = colSizeFull - curTable.columnHiddenIdxs.size();
//        columnHiddenServerOrder = new boolean[colSizeFull];
//        Arrays.fill(columnHiddenServerOrder, false);
//        curTable.columnHiddenIdxs.parallelStream().forEach(ci -> columnHiddenServerOrder[ci] = true);
//        d.ci("initializeHiddenServerOrder", "columnHiddenServerOrder#1", Arrays.toString(columnHiddenServerOrder));
//    }
//    private static int colSizeVisible;//WHY WOULD YOU WANNA USE IT?
//    public static boolean[] columnHiddenServerOrder;
//
//    private static void initializeHiddenUserOrder() {
//        var allowUserColumns = ASWPackUtils.pack.getOutput_allowColumnHideIdxs().get(tableOpenIdx);
//        if (allowUserColumns.contains("*")) {
//            Arrays.fill(columnHiddenServerOrder, false);
//            d.ci("initializeHiddenUserOrder", "columnHiddenServerOrder#2*", Arrays.toString(columnHiddenServerOrder));
//            return;
//        }
//        var parsedData = TGS_StringUtils.gwt().toList_spc(allowUserColumns);
//        parsedData.stream().forEachOrdered(colAllowSI -> {
//            var colAllowI = TGS_CastUtils.toInteger(colAllowSI);
//            if (colAllowI == null) {
//                d.ce("initializeHiddenUserOrder", "Error skipping non integer columnAllow", colAllowSI);
//                return;
//            }
//            if (colAllowI > columnHiddenServerOrder.length - 1) {
//                d.ce("initializeHiddenUserOrder", "Error skipping max limit exceed integer columnAllow" + colAllowSI);
//                return;
//            }
//            if (colAllowI < 0) {
//                d.ce("initializeHiddenUserOrder", "Error skipping min limit exceed integer columnAllow: " + colAllowSI);
//                return;
//            }
//            if (columnHiddenServerOrder[colAllowI] == false) {
//                d.ce("initializeHiddenUserOrder", "Error skipping unnecessary columnAllow: " + colAllowSI);
//                return;
//            }
//            columnHiddenServerOrder[colAllowI] = false;
//        });
//        d.ci("initializeHiddenUserOrder", "columnHiddenServerOrder#2", Arrays.toString(columnHiddenServerOrder));
//    }
//
//    private static void initializeColumnServerOrder(TGS_FConfigValueGet r) {
//        columnGroupsServerOrder = new String[colSizeFull];
//        columnIdxsServerOrder = new int[colSizeFull];
//        var parsedData = TGS_StringUtils.gwt().toList_spc(r.getOutput_value());
//        TGS_Tuple1<Integer> fillerOffetColIdx = new TGS_Tuple1(0);
//        TGS_Tuple1<String> futureGroupTag = new TGS_Tuple1("Genel");
//        Arrays.fill(columnIdxsServerOrder, -1);
//        parsedData.stream().forEachOrdered(colOrderSI -> {
//            var colOrderI = TGS_CastUtils.toInteger(colOrderSI);
//            if (colOrderI == null) {
//                futureGroupTag.value0 = colOrderSI.trim();
//                return;
//            }
//            if (colOrderI > columnIdxsServerOrder.length - 1) {
//                d.ce("initializeColumnServerOrder", "Error skipping max limit exceed integer column order token", colOrderSI);
//                return;
//            }
//            if (colOrderI < 0) {
//                d.ce("initializeColumnServerOrder", "Error skipping min limit exceed integer column order token", colOrderSI);
//                return;
//            }
//            if (columnIdxsServerOrder[colOrderI] != -1) {
//                d.ce("initializeColumnServerOrder", "Error skipping repetation of integer column order token", colOrderSI);
//                return;
//            }
//            if (fillerOffetColIdx.value0 == 0 && colOrderI != 0) {
//                d.ce("initializeColumnServerOrder", "Error skipping column order first token (should start with 0)", colOrderSI);
//                colOrderI = 0;
//            }
//            columnGroupsServerOrder[colOrderI] = futureGroupTag.value0;
//            columnIdxsServerOrder[colOrderI] = fillerOffetColIdx.value0;
//            fillerOffetColIdx.value0++;
//        });
//        IntStream.range(0, columnIdxsServerOrder.length).forEachOrdered(i -> {
//            if (columnIdxsServerOrder[i] == -1) {
//                columnGroupsServerOrder[i] = futureGroupTag.value0;
//                columnIdxsServerOrder[i] = fillerOffetColIdx.value0;
//                fillerOffetColIdx.value0++;
//            }
//        });
//        d.ci("initializeColumnServerOrder", "columnGroupsServerOrder", Arrays.toString(columnGroupsServerOrder));
//        d.ci("initializeColumnServerOrder", "columnIdxsServerOrder", Arrays.toString(columnIdxsServerOrder));
//    }
//    public static String[] columnGroupsServerOrder;
//    public static int[] columnIdxsServerOrder;
//
//    private static void initializeHiddenAndColumnClientOrder() {
//        columnGroupsClientOrder = new String[colSizeFull];
//        columnIdxsClientOrder = new int[colSizeFull];
//        columnHiddenClientOrder = new boolean[colSizeFull];
//        IntStream.range(0, columnIdxsClientOrder.length).parallel().forEach(cci -> {
//            IntStream.range(0, columnIdxsServerOrder.length).parallel().forEach(csi -> {
//                if (columnIdxsServerOrder[csi] == cci) {
//                    columnIdxsClientOrder[cci] = csi;
//                    columnGroupsClientOrder[cci] = columnGroupsServerOrder[csi];
//                    columnHiddenClientOrder[cci] = columnHiddenServerOrder[csi];
//                }
//            });
//        });
//        d.ci("initializeHiddenAndColumnClientOrder", "columnGroupsClientOrder", Arrays.toString(columnGroupsClientOrder));
//        d.ci("initializeHiddenAndColumnClientOrder", "columnIdxsClientOrder", Arrays.toString(columnIdxsClientOrder));
//        d.ci("initializeHiddenAndColumnClientOrder", "columnHiddenClientOrder", Arrays.toString(columnHiddenClientOrder));
//    }
//    public static String[] columnGroupsClientOrder;
//    public static int[] columnIdxsClientOrder;
//    public static boolean[] columnHiddenClientOrder;
}
