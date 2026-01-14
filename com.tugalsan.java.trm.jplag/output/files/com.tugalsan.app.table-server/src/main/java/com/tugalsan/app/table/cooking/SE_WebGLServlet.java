package com.tugalsan.app.table.cooking;
public class SE_WebGLServlet {//extends TS_SURLExecutor {

//
//    @Override
//    public String name() {
//        return SE_WebGLServlet.class.getSimpleName();
//    }
//
//    final private static TS_Log d = TS_Log.of(SE_WebGLServlet.class);
//
//    @Override
//    public void run(TS_SURLHelper shw) {
//        var cp = TS_LibBootUtils.pck;
//        var so = new AppHelper(shw, TS_SURLHelper.CONTENT_TYPE_HTML);
//
//        var webglTXT = TS_LibRqlBufferUtils.get("webglTXT");
//        if (webglTXT == null) {
//            so.throwError("ERROR: table WEBGLTXT_IS_NULL");
//        }
//
//        if (!webglTXT.nameSql.equalsIgnoreCase(so.tableName)) {
//            so.throwError(TGS_StringUtils.cmn().concat("ERROR: UNKNOWN_TABLENAME [", so.tableName, "] expected [", webglTXT.nameSql, "]"));
//        }
//
//        var tokens = TGS_StringUtils.jre().toList_spc(so.selectedIdStr, "_");
//        if (tokens.isEmpty()) {
//            so.throwError("ERROR: TokenSize == 0");
//        }
//
//        TGS_Tuple2<String, String> results = null;
//        for (var i = 0; i < tokens.size(); i++) {
//            var valId = TGS_CastUtils.toLong(tokens.get(i)).orElse(null);
//            if (valId == null) {
//                so.throwError(TGS_StringUtils.cmn().concat("ERROR: CANNOT_PARSE_ID [", tokens.get(i), "]"));
//            }
//            results = getTXT(cp.sqlAnc, so, webglTXT, valId);
//            if (results == null) {
//                so.throwError("ERROR: results == null for id: " + valId);
//            }
//            tokens.set(i, results.value1);
//        }
//
//        var sb = new StringBuilder();
//        sb.append("valId:{<BR>\n");
//        sb.append(so.selectedIdStr);
//        sb.append("<BR>\n}<BR>\n<BR>\n");
//        sb.append("valName:{<BR>\n");
//        sb.append(results == null ? String.valueOf(results) : results.value0);
//        sb.append("<BR>\n}<BR>\n<BR>\n");
//        sb.append("valCode:{<BR>\n");
//        for (var i = 0; i < tokens.size(); i++) {
//            sb.append(tokens.get(i));
//        }
//        sb.append("<BR>\n}<BR>\n<BR>\n");
//        so.println(sb);
//    }
//
//    public static TGS_Tuple2<String, String> getTXT(TS_SQLConnAnchor anchor, AppHelper so, TGS_LibRqlTbl webglTXT, long id) {
//        TGS_Tuple2<String, String> results = new TGS_Tuple2();
//        var colNameId = "LNG_ID";
//        var colNameName = "STR254_ADI";
//        var colNameCode = "BYTESSTR_TXT";
//        TS_SQLSelectUtils.select(anchor, webglTXT.nameSql).columns(colNameName, colNameCode)
//                .whereConditionAnd(con -> con.lngEq(colNameId, id))
//                .groupNone().orderNone().rowIdxOffsetNone().rowSizeLimitNone().walk(rs -> {
//                    if (rs.row.isEmpty()) {
//                        so.throwError(TGS_StringUtils.cmn().concat("ERROR: CANNOT_FIND_ID  = [", String.valueOf(id), "]"));
//                    }
//                    rs.walkRows(ri -> {
//                        results.value0 = rs.str.get(colNameName);
//                        results.value1 = rs.bytesStr.get(colNameCode);
//                    });
//                });
//        return results.value1 == null ? null : results;
//    }
}
