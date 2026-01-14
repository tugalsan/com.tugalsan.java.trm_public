package com.tugalsan.trm.console;

import com.tugalsan.api.cast.server.TS_CastUtils;
import com.tugalsan.api.console.client.TGS_ConsoleOption;
import com.tugalsan.api.file.pom.server.TS_FilePomConfig;
import com.tugalsan.api.file.server.TS_DirectoryUtils;
import com.tugalsan.api.log.server.TS_Log;
import static com.tugalsan.trm.console.Main.pomCfg;
import java.util.List;
import java.util.Objects;

public class RunMavenCodes {

    final private static TS_Log d = TS_Log.of(true, RunMavenCodes.class);

    public static TGS_ConsoleOption run = TGS_ConsoleOption.of((cmd, restArgs) -> {
        if (restArgs.isEmpty()) {
            d.ce(cmd, "restArgs.isEmpty()");
            return;
        }
        if (Objects.equals(restArgs.get(0), "get")) {
            restArgs.remove(0);
            mavenCodes_get(pomCfg, restArgs);
            return;
        }
        if (Objects.equals(restArgs.get(0), "set")) {
            restArgs.remove(0);
            mavenCodes_set(pomCfg, restArgs);
            return;
        }
        d.ce(cmd, "next token not recognized");
    }, "mc", "mavenCodes");

    private static void mavenCodes_get(TS_FilePomConfig pomCfg, List<CharSequence> restArgs) {
        if (!restArgs.isEmpty()) {
            d.ce("mavenCodes_get", "!restArgs.isEmpty()");
            return;
        }
        d.cr("mavenCodes_get", pomCfg.mavenCodes);
    }

    private static void mavenCodes_set(TS_FilePomConfig pomCfg, List<CharSequence> restArgs) {
        if (restArgs.size() != 1) {
            d.ce("mavenCodes_set", "restArgs.size() != 1");
            return;
        }
        var pathStr = restArgs.get(0);
        var path = TS_CastUtils.toPath(pathStr).orElse(null);
        if (path == null) {
            d.ce("mavenCodes_set", "path == null", pathStr);
            return;
        }
        if (!TS_DirectoryUtils.isExistDirectory(path)) {
            d.ce("mavenCodes_set", "!TS_DirectoryUtils.isExistDirectory(path)", pathStr);
            return;
        }
        pomCfg.mavenCodes = path;
        d.cr("mavenCodes_set", pomCfg.mavenCodes);
    }
}
