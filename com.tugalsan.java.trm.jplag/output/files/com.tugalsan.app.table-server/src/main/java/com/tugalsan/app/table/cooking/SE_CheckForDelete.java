package com.tugalsan.app.table.cooking;

public class SE_CheckForDelete {//extends TS_SURLExecutor {

//
//    @Override
//    public String name() {
//        return SE_CheckForDelete.class.getSimpleName();
//    }
//
//    final private static TS_Log d = TS_Log.of(SE_CheckForDelete.class);
//
//    @Override
//    public void run(TS_SURLHelper shw) {
//        var cp = TS_LibBootUtils.pck;
//        var so = new AppHelper(shw, TS_SURLHelper.CONTENT_TYPE_HTML);
//        var tn = shw.getParameter("tn", true);
//        var id = shw.getParameterInteger("tn", true);
//
//        //FINDING RELATED TABLE.COLUMNS
//        final List<TGS_Tuple2<TGS_LibRqlTbl, TGS_LibRqlCol>> foundTableColumnIdx = TGS_ListUtils.of();
//        TS_LibRqlBufferUtils.items.stream().forEachOrdered(table -> {
//            table.columns.stream().forEachOrdered(ct -> {
//                var tc = TGS_LibRqlColUtils.toConnCol(ct);
//                if (tc.groupLnk()) {
//                    if (ct.getDataString1_LnkTargetTableName().equalsIgnoreCase(tn)) {
//                        foundTableColumnIdx.add(new TGS_Tuple2(table, ct));
//                    }
//                }
//            });
//        });
//
//        var sb = new StringBuilder();
//        for (var i = 0; i < foundTableColumnIdx.size(); i++) {
//            var table = foundTableColumnIdx.get(i).value0;
//            var ct = foundTableColumnIdx.get(i).value1;
//            var tb = table.nameSql;
//            var cn = ct.getColumnName();
//            var vtb = table.nameReadable;
//
//            var pack = TS_LibRqlConfigValueUtils.get(cp.sqlAnc, tb, TGS_LibRqlCfgUtils.PrecolName  + cn);
//            if (pack.value0 == null) {
//                so.println("ERROR: kolon başlığı çekilemedi hatası: " + tb + "." + cn);
//                return;
//            }
//            var vcn = pack.value1;
//
//            TS_SQLSelectUtils.select(cp.sqlAnc, tb).columns(0).whereConditionAnd(c -> c.lngEq(cn, id))
//                    .groupNone().orderNone().rowIdxOffsetNone().rowSizeLimitNone().walk(rs -> {
//                        sb.append(vtb).append(" tablosunun, ").append(vcn).append(" kolonunda, kullanılan idler:");
//                        IntStream.range(0, rs.row.size()).forEachOrdered(ri -> {
//                            sb.append(" ").append(rs.str.get(0));
//                        });
//                        sb.append("\n");
//                    });
//        }
//        so.println(sb);
//    }
}
