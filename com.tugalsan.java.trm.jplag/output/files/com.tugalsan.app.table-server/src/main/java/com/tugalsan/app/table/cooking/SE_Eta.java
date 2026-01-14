package com.tugalsan.app.table.cooking;

public class SE_Eta {//extends TS_SURLExecutor {
//
//
//    @Override
//    public String name() {
//        return SE_Eta.class.getSimpleName();
//    }
//
//    final private static TS_Log d = TS_Log.of(SE_Eta.class);
//
//    @Override
//    public void run(TS_SURLHelper shw) {
//        var so = new AppHelper(shw, TS_SURLHelper.CONTENT_TYPE_HTML);
//        var cp = TS_LibBootUtils.pck;
//        var domain = TS_LibDomainUtils.getPackBuffered(shw.url);
//        if (domain == null) {
//            so.addHTML_BodyBegin(SE_Eta.class.getSimpleName(), true, false, 5, 5, null);
//            so.addHTML_Error("domian pack == null for url:" + shw.url, true);
//            return;
//        }
//
//        String favIcon;
//        if (Objects.equals(domain.firmaNameParam, "mesametal")) {
//            favIcon = TGS_LibResourceUtils.other.res.mesametal_com.favicon.mesametal_dark_16x16_png();
//        } else if (Objects.equals(domain.firmaNameParam, "mebosa")) {
//            favIcon = TGS_LibResourceUtils.other.res.mebosa_com.favicon.mebosa_com_dark_16x16_png();
//        } else {
//            favIcon = TGS_LibResourceUtils.common.res.favicon.default_dark_16x16_png();
//        }
//
//        so.println(TGS_FileHtmlUtils.beginLines("ETA", false, true, 5, 5, favIcon,optionalCustomDomain));
//
//        var mode_sniffAll = "sniffAll";
//        var mode = shw.getParameter("mode", true);
//        var db = shw.getParameter("db", true);
//        var param = shw.getParameter("param", false);
//        so.println("DEBUG: param is '" + param + "'");
//        if (param == null) {
//            so.println("param is null");
//        } else {
//            param = param.trim();
//            if (param.isEmpty()) {
//                so.println("param is empty");
//            }
//            var parsedParam = TGS_StringUtils.jre().toList_spc(param, "_");
//            if (parsedParam == null) {
//                so.println("parsedParam is null");
//            } else if (parsedParam.isEmpty()) {
//                so.println("parsedParam is empty");
//            } else {
//                so.println("parsedParam is " + TGS_StringUtils.cmn().toString_ln(parsedParam));
//            }
//        }
//
//        var config = new TS_SQLConnConfig(db);
//        config.dbPort = 1433;
//        config.dbUser = "sa";
//        config.method = TS_SQLConnMethodUtils.METHOD_SQLSERVER;
//        var anchor = new TS_SQLConnAnchor(config);
//
//        if (mode.equals(mode_sniffAll)) {
//            String firmaTableName = "FIRMA";
//            List<List<String>> firmaTable = TGS_ListUtils.of();
//
//            List<String> row = TGS_ListUtils.of();
////                sql = "SELECT CARKOD, CARUNVAN               FROM CARKART ORDER BY CARKOD, CARUNVAN";
//            TS_SQLSelectUtils.select(anchor, firmaTableName).columns("LNG_ID", "STR254_KOD", "STR254_ADI")
//                    .whereConditionNone().groupNone().orderAsc("STR254_KOD", "STR254_ADI", "LNG_ID")
//                    .rowIdxOffsetNone().rowSizeLimitNone().walkRows((rs, ri) -> {
//                        row.add(rs.str.get("LNG_ID"));
//                        row.add(rs.str.get("STR254_KOD"));
//                        row.add(rs.str.get("STR254_ADI"));
//                    });
//
////            String columnMUHKOD = "MUHKOD";
////            String columnMUHADI1 = "MUHADI1";
////            String tableMUHHESAP = "MUHHESAP";
////            String sqlStart = "SELECT MUHHARTAR AS 'Tarih', MUHHARMUHKOD AS 'Muh Kod', MUHHARNO AS 'Fis No', MUHHARACIKLAMA AS 'Aciklama', 'T' = CASE WHEN MUHHARBATIPI=1 THEN 'B' ELSE 'A' END, MUHHARTUTAR AS 'Tutar', MUHFTKOD AS 'Fis Cinsi' FROM MUHHAR WITH(NOLOCK) LEFT JOIN MUHFISTIP WITH(NOLOCK) ON MUHHAR.MUHHARCINSI=MUHFISTIP.MUHFTNO";// WHERE MUHHARMUHKOD LIKE '" + fMUHKOD + "%'";
////            String sqlMiddle = "";
//            //            	} else if (bilanco) {
//            //		html += " (ay : 1 - " + sci + ") <font><br>";
//            //		if (sci < 13) {
//            //			sqlMiddle = " AND ((MONTH(MUHHARTAR) <= "  + sci + " AND YEAR(MUHHARTAR) = " + year + ") OR (YEAR(MUHHARTAR) < " + year+"))";
//            //		} else {
//            //			sci-=12;
//            //			sqlMiddle = " AND ((MONTH(MUHHARTAR) <= "  + sci + " AND YEAR(MUHHARTAR) = " + (year+1) + ") OR (YEAR(MUHHARTAR) < " + (year+1)+"))";
//            //		}
//            //	} else {
//            //		html += " (ay : " + sci + ") <font><br>";
//            //		if (sci < 13) {
//            //			sqlMiddle = " AND MONTH(MUHHARTAR) = "  + sci + " AND YEAR(MUHHARTAR) = " + year;
//            //		} else {
//            //			sci-=12;
//            //			sqlMiddle = " AND MONTH(MUHHARTAR) = "  + sci + " AND YEAR(MUHHARTAR) = " + (year+1);
//            //		}
////            String sqlEnd = " ORDER BY MUHHARTAR DESC, MUHHARREFNO, MUHHARSIRANO";
//            so.println("<table>");
//            so.println("<tr><td>skipped...</td><td></td></tr>");
//
//            TS_SQLSelectUtils.select(cp.sqlAnc, "CARKART").columns("CARKOD", "CARUNVAN")
//                    .whereConditionNone().groupNone().orderAsc("CARKOD", "CARUNVAN")
//                    .rowIdxOffsetNone().rowSizeLimitNone().walk(rs -> {
//                        boolean found;
//                        for (var qi = 0; qi < rs.row.size(); qi++) {
//                            rs.row.scrll(qi);
//                            var etaKod = rs.str.get("CARKOD");
//                            var etaAd = rs.str.get("CARUNVAN");
//                            found = false;
//                            for (List<String> r : firmaTable) {
//                                so.println("['" + r.get(1) + "','" + etaKod + "']");
//                                if (r.get(1).equals(etaKod)) {
//                                    firmaTable.remove(r);
//                                    found = true;
//                                    break;
//                                }
//                            }
//                            if (!found) {
//                                so.println("<tr>");
//                                so.println("<td>");
//                                so.println(etaAd);
//                                so.println("</td>");
//                                so.println("<td>");
//                                so.println(etaKod);
//                                so.println("</td>");
//                                so.println("</tr>");
//                            }
//                            if (qi > 0) {
//                                break;
//                            }
//                        }
//                    });
//            so.println("<tr><td>excessive...</td><td></td></tr>");
//            for (var ii = 0; ii < firmaTable.size(); ii++) {
//                so.println("<tr>");
//                so.println("<td>");
//                so.println(firmaTable.get(ii).get(1));
//                so.println("</td>");
//                so.println("<td>");
//                so.println(firmaTable.get(ii).get(0));
//                so.println(" ");
//                so.println(firmaTable.get(ii).get(2));
//                so.println("</td>");
//                so.println("</tr>");
//            }
//        } else {
//            so.println("ERROR: unknown funcName: " + mode);
//        }
//
//        so.println(TGS_FileHtmlUtils.endLines());
//    }
}
