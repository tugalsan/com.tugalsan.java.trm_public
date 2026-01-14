package com.tugalsan.trm.console;

import com.tugalsan.api.console.client.TGS_ConsoleOption;
import com.tugalsan.api.file.pom.server.TS_FilePomConfig;
import com.tugalsan.api.log.server.TS_Log;
import static com.tugalsan.trm.console.Main.pomCfg;
import java.util.List;
import java.util.Objects;

public class RunProjectGroupId {

    final private static TS_Log d = TS_Log.of(true, RunProjectGroupId.class);

    public static TGS_ConsoleOption run = TGS_ConsoleOption.of((cmd, restArgs) -> {
        if (restArgs.isEmpty()) {
            d.ce(cmd, "restArgs.isEmpty()");
            return;
        }
        if (Objects.equals(restArgs.get(0), "lst")) {
            restArgs.remove(0);
            projectGroupId_lst(pomCfg, restArgs);
            return;
        }
        if (Objects.equals(restArgs.get(0), "clr")) {
            restArgs.remove(0);
            projectGroupId_clr(pomCfg, restArgs);
            return;
        }
        if (Objects.equals(restArgs.get(0), "add")) {
            restArgs.remove(0);
            projectGroupId_add(pomCfg, restArgs);
            return;
        }
        if (Objects.equals(restArgs.get(0), "del")) {
            restArgs.remove(0);
            projectGroupId_del(pomCfg, restArgs);
            return;
        }
        d.ce(cmd, "next token not recognized");
    }, "pgi", "projectGroupId");

    private static void projectGroupId_lst(TS_FilePomConfig pomCfg, List<CharSequence> restArgs) {
        if (!restArgs.isEmpty()) {
            d.ce("projectGroupId_lst", "ERROR: !restArgs.isEmpty()");
            return;
        }
        d.cr("projectGroupId_lst", pomCfg.projectGroupIds);
    }

    private static void projectGroupId_clr(TS_FilePomConfig pomCfg, List<CharSequence> restArgs) {
        if (!restArgs.isEmpty()) {
            d.ce("projectGroupId_clr", "ERROR: !restArgs.isEmpty()");
            return;
        }
        pomCfg.projectGroupIds.clear();
    }

    private static void projectGroupId_add(TS_FilePomConfig pomCfg, List<CharSequence> restArgs) {
        if (restArgs.isEmpty()) {
            d.ce("projectGroupId_add", "ERROR: restArgs.isEmpty()");
            return;
        }
        restArgs.forEach(newGroupId -> pomCfg.projectGroupIds.add(newGroupId));
    }

    private static void projectGroupId_del(TS_FilePomConfig pomCfg, List<CharSequence> restArgs) {
        if (restArgs.isEmpty()) {
            d.ce("projectGroupId_del", "ERROR: restArgs.isEmpty()");
            return;
        }
        restArgs.forEach(newGroupId -> pomCfg.projectGroupIds.remove(newGroupId));
    }
}
