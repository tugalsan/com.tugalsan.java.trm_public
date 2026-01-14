package com.tugalsan.app.table.cooking;

@Deprecated
public class SE_ArduinoAddLog {//extends TS_SURLExecutor {
//
//    
//    @Override
//    public String name() {
//        return SE_ArduinoAddLog.class.getSimpleName();
//    }
//
//    final private static TS_Log d = TS_Log.of(SE_ArduinoAddLog.class);
//
//    @Override
//    public void run(TS_SURLHelper shw) {
//        var cp = TS_LibBootUtils.pck;
//        var so = new AppHelper(shw, TS_SURLHelper.CONTENT_TYPE_HTML);
//
//        boolean verbose = true;
//        if (verbose) {
//            so.println("WELCOME");
//        }
//
//        var preLog = TS_UrlServletRequestUtils.getParameterValue(shw.rq, "log", true);
//        var log = preLog == null ? "" : preLog;
//        if (verbose) {
//            so.println("FULL_LOG: " + log);
//        }
//        if (verbose) {
//            so.println("log.length(): " + log.length());
//        }
//        if (log.startsWith("sniff")) {
//            so.println("Sniffing2...");
//            var arduinoWebService = "https://" + log.substring("sniff".length());
//            so.println(arduinoWebService + ":");
//            String html = TS_UrlDownloadUtils.toText(TS_UrlUtils.toURLUnSafe_mayThrowException(arduinoWebService));
//            if (html == null) {
//                so.println("html null: " + arduinoWebService);
//                return;
//            }
//            so.println(html);
//
//            so.println("parsing html...");
//            var temperature = 0;
//            var humidity = 0;
//            var temperatureOffset = 0;
//            var humidityOffset = 0;
//            var ardWebSrvLines = TGS_StringUtils.jre().toList(html, "\n");
//            for (var j = 0; j < ardWebSrvLines.size(); j++) {
//                var ardWebSrvLineParts = TGS_StringUtils.jre().toList_spc(ardWebSrvLines.get(j));
//                if (ardWebSrvLineParts.size() != 2) {
//                    so.println("lineSkipped: size:" + ardWebSrvLineParts.size() + " : " + ardWebSrvLines.get(j));
//                    continue;
//                }
//                var value = ardWebSrvLineParts.get(1);
//                if (value.endsWith("<br/>")) {
//                    value = value.substring(0, value.length() - "<br/>".length());
//                }
//                var i = TGS_CastUtils.toInteger(value);
//                switch (ardWebSrvLineParts.get(0)) {
//                    case "temperatureX10:" -> {
//                        if (i != null) {
//                            temperature = i;
//                            so.println("parsed.temperatureX10: " + temperature);
//                        }
//                    }
//                    case "humidityX10:" -> {
//                        if (i != null) {
//                            humidity = i;
//                            so.println("parsed.humidityX10: " + humidity);
//                        }
//                    }
//                    case "temperatureOffsetX10:" -> {
//                        if (i != null) {
//                            temperatureOffset = i;
//                            so.println("parsed.temperatureOffsetX10: " + temperatureOffset);
//                        }
//                    }
//                    case "humidityOffsetX10:" -> {
//                        if (i != null) {
//                            humidityOffset = i;
//                            so.println("parsed.humidityOffsetX10: " + humidityOffset);
//                        }
//                    }
//                    default -> {
//                    }
//                }
//            }
//            if (temperature == 0) {
//                so.println("Op Skipped because temperature == 0");
//            } else {
//                var tnLog = "arduinolog";
//                var nextId = TS_SQLMaxUtils.max(cp.sqlAnc, tnLog, 0).whereConditionNone().nextId();
//                List<TGS_SQLCellAbstract> row = TGS_ListUtils.of();
//                row.add(new TGS_SQLCellLNG(nextId));
//                row.add(new TGS_SQLCellSTR(log));
//                row.add(new TGS_SQLCellLNG(TGS_Time.getCurrentDate()));
//                row.add(new TGS_SQLCellLNG(TGS_Time.getCurrentTime()));
//                row.add(new TGS_SQLCellLNG(temperature));
//                row.add(new TGS_SQLCellLNG(humidity));
//                row.add(new TGS_SQLCellLNG(temperatureOffset));
//                row.add(new TGS_SQLCellLNG(humidityOffset));
//                so.println("inserting row:" + row);
//                TS_SQLInsertUtils.insert(cp.sqlAnc, tnLog).valCell(row);
//            }
//            so.println("DONE");
//        } else {
//            var tnLog = "arduinolog";
//            var nextId = TS_SQLMaxUtils.max(cp.sqlAnc, tnLog, 0).whereConditionNone().nextId();
//            List<TGS_SQLCellAbstract> row = TGS_ListUtils.of();
//            row.add(new TGS_SQLCellLNG(nextId));
//            row.add(new TGS_SQLCellSTR(log));
//            row.add(new TGS_SQLCellLNG(TGS_Time.getCurrentDate()));
//            row.add(new TGS_SQLCellLNG(TGS_Time.getCurrentTime()));
//            row.add(new TGS_SQLCellLNG(0));
//            row.add(new TGS_SQLCellLNG(0));
//            row.add(new TGS_SQLCellLNG(0));
//            row.add(new TGS_SQLCellLNG(0));
//            TS_SQLInsertUtils.insert(cp.sqlAnc, tnLog).valCell(row);
//
//            var tnSetup = "arduinosetup";
//
//            var tokens = TGS_StringUtils.jre().toList_spc(log);
//            if (tokens.size() < 2) {
//                so.println("tokens.size()<2: " + tokens.size());
//                return;
//            }
//            var ARDX = tokens.get(0);
//            so.println("ARDX: " + ARDX);
//            var state = TGS_CastUtils.toInteger(tokens.get(1));
//            if (state == null) {
//                so.println("COUNTER not int: " + tokens.get(1));
//                return;
//            }
//            so.println("state: " + state);
//            var tblCommands = new TGS_ListTable();
//            {
//                TS_SQLResTblUtils.fill(
//                        TS_SQLSelectUtils.select(cp.sqlAnc, tnSetup).columnsAll()
//                                .whereConditionAnd(c -> c.strEq("STR254_ADI", ARDX)).groupNone()
//                                .orderAsc(0).rowIdxOffsetNone().rowSizeLimitNone(),
//                        tblCommands
//                );
//                if (tblCommands.isEmpty()) {
//                    so.println("HATA: " + tnSetup + " tablosu bos hatasi! for: " + ARDX);
//                    return;
//                }
//            }
//            Integer id = null;
//            var acted = false;
//            for (var i = 0; i < tblCommands.getRowSize(); i++) {
//                if (tblCommands.getValueAsString(i, 1).equals(ARDX)) {
//                    id = TGS_CastUtils.toInteger(tblCommands.getValueAsString(i, 0));
//                    if (id == null) {
//                        so.println("CANNOT cast 2 int table value 0: '" + tblCommands.getValueAsString(i, 0) + "'");
//                        return;
//                    }
//                    if (id <= state) {
//                        so.println("SKIPPING OLD CMD: " + id);
//                        continue;
//                    }
//                    var lines = TGS_StringUtils.jre().toList(tblCommands.getValueAsString(i, 2), "\n");
//                    for (var j = 0; j < lines.size(); j++) {
//                        List<String> codeParts = TGS_StringUtils.jre().toList_spc(lines.get(j));
//                        codeParts.add(0, String.valueOf(id));
//                        for (int k = 0; k < codeParts.size(); k++) {
//                            so.println("CO>" + codeParts.get(k));
//                        }
//                    }
//                    acted = true;
//                    break;
//                }
//            }
//            if (acted) {
//                final var fid = id;
//                final var facted = acted;
//                TS_SQLUpdateUtils.update(cp.sqlAnc, tnSetup).set(set -> set.add(new TGS_Tuple2("LNG_STATE", facted ? fid : state)))
//                        .whereConditionAnd(c -> c.strEq("STR254_ADI", ARDX));
//            }
//            TS_SQLDeleteUtils.delete(cp.sqlAnc, tnLog).whereConditionAnd(c -> {
//                c.lngEq("LNGFLOAT1_TEMPVAL", 0L);
//                c.lngEqNot("LNGDATE_ADI", TGS_Time.getCurrentDate());
//            });
//        }
//    }
}
