package com.tugalsan.app.table.pop;

import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.log.client.TGS_Log;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.app.table.AppModuleTable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AppPopTableFilterMemProfiles {

    final private static TGC_Log d = TGC_Log.of(AppPopTableFilterMemProfiles.class);

    public AppPopTableFilterMemProfiles(String name, List<AppPopTableFilterMemProfile> profiles) {
        this.name = name;
        this.profiles = profiles;
    }
    public final String name;
    public final List<AppPopTableFilterMemProfile> profiles;

    private final static String TAG_name = "name ";

    public List<String> toLst() {
        List<String> lst = new ArrayList();
        profiles.forEach(p -> lst.addAll(p.toLst()));
        if (lst.isEmpty()) {
            return lst;
        }
        lst.add(0, TGS_StringUtils.cmn().concat(TAG_name, name));
        return lst;
    }

    public static List<AppPopTableFilterMemProfiles> ofLst(AppModuleTable tm, List<String> lst) {
        List<AppPopTableFilterMemProfiles> all = new ArrayList();
        AppPopTableFilterMemProfiles cur = null;
        if (lst.isEmpty()) {
            d.consoleOnly_ci_ifInfoEnable("ofLst", "lst.isEmpty()");
        }
        List<String> savedDataLines = new ArrayList();
        for (var line : lst) {
            if (line.contains(TAG_name)) {
                d.consoleOnly_ci_ifInfoEnable("ofLst", "line", "name#1", line);
                if (cur == null) {//CYCLE_FOR
                    if (!savedDataLines.isEmpty()) {
                        d.ce("ofLst", "cur == null", "#1.1");
                        savedDataLines.clear();
                    }
                } else {
                    if (savedDataLines.isEmpty()) {
                        d.ce("ofLst", "cur == null", "#1.2");
                        all.remove(all.size() - 1);
                    } else {
                        d.consoleOnly_ci_ifInfoEnable("ofLst", "loadAll", "BEGIN");
                        cur.profiles.forEach(p -> p.loadAll(savedDataLines));
                        d.consoleOnly_ci_ifInfoEnable("ofLst", "loadAll", "END");
                        savedDataLines.clear();
                    }
                    cur = null;
                }
                d.consoleOnly_ci_ifInfoEnable("ofLst", "line", "name#2", line);
                var name = line.substring(TAG_name.length());
                if (name.isEmpty()) {
                    name = "no_name " + System.currentTimeMillis();
                }
                var newProfiles = TGS_StreamUtils.toLst(
                        IntStream.range(0, tm.curTable.columns.size())
                                .mapToObj(ci -> new AppPopTableFilterMemProfile(ci))
                );
                cur = new AppPopTableFilterMemProfiles(name, newProfiles);
                d.consoleOnly_ci_ifInfoEnable("ofLst", "BEGIN printing inited memProfile");
                cur.toString_print();
                d.consoleOnly_ci_ifInfoEnable("ofLst", "END printing inited memProfile");
                all.add(cur);
                continue;
            }
            d.consoleOnly_ci_ifInfoEnable("ofLst", "line", "other", line);
            if (cur == null) {
                d.ce("ofLst", "cur == null", "#2");
                continue;
            }
            savedDataLines.add(line);
        }
        if (cur == null) {//CYCLE_TAIL
            if (!savedDataLines.isEmpty()) {
                d.ce("ofLst", "cur == null", "#3.1");
                savedDataLines.clear();
            }
        } else {
            if (savedDataLines.isEmpty()) {
                d.ce("ofLst", "cur == null", "#3.2");
                all.remove(all.size() - 1);
            } else {
                d.consoleOnly_ci_ifInfoEnable("ofLst", "loadAll", "BEGIN");
                cur.profiles.forEach(p -> p.loadAll(savedDataLines));
                d.consoleOnly_ci_ifInfoEnable("ofLst", "loadAll", "END");
                savedDataLines.clear();
            }
            cur = null;
        }
        if (d.infoEnable) {
            all.forEach(memProfiles -> memProfiles.toString_print());
        }
        return all;
    }

    public void toString_print() {
        d.consoleOnly_ci_ifInfoEnable("ofLst", "BEGIN printing loaded memProfiles");
        d.consoleOnly_ci_ifInfoEnable("ofLst", "name", name);
        profiles.forEach(memProfile -> {
            d.consoleOnly_ci_ifInfoEnable("ofLst", memProfile.toString());
        });
        d.consoleOnly_ci_ifInfoEnable("ofLst", "END printing loaded memProfiles");
    }
}
