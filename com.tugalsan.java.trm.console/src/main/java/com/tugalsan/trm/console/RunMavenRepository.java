package com.tugalsan.trm.console;

import com.tugalsan.api.cast.server.TS_CastUtils;
import com.tugalsan.api.console.client.TGS_ConsoleOption;
import com.tugalsan.api.file.pom.server.TS_FilePomConfig;
import com.tugalsan.api.file.server.TS_DirectoryUtils;
import com.tugalsan.api.log.server.TS_Log;
import static com.tugalsan.trm.console.Main.pomCfg;
import java.util.List;
import java.util.Objects;

public class RunMavenRepository {

    final private static TS_Log d = TS_Log.of(true, RunMavenRepository.class);
    public static TGS_ConsoleOption run = TGS_ConsoleOption.of((cmd, restArgs) -> {
        if (restArgs.isEmpty()) {
            d.ce(cmd, "restArgs.isEmpty()");
            return;
        }
        if (Objects.equals(restArgs.get(0), "get")) {
            restArgs.remove(0);
            mavenRepository_get(pomCfg, restArgs);
            return;
        }
        if (Objects.equals(restArgs.get(0), "set")) {
            restArgs.remove(0);
            mavenRepository_set(pomCfg, restArgs);
            return;
        }
        d.ce(cmd, "next token not recognized");
    }, "mr", "mavenRepository");

    private static void mavenRepository_get(TS_FilePomConfig pomCfg, List<CharSequence> restArgs) {
        if (!restArgs.isEmpty()) {
            d.ce("mavenRepository_get", "!restArgs.isEmpty()");
            return;
        }
        d.cr("mavenRepository_get", pomCfg.mavenRepository);
    }

    private static void mavenRepository_set(TS_FilePomConfig pomCfg, List<CharSequence> restArgs) {
        if (restArgs.size() != 1) {
            d.ce("mavenRepository_set", "restArgs.size() != 1");
            return;
        }
        var pathStr = restArgs.get(0);
        var path = TS_CastUtils.toPath(pathStr).orElse(null);
        if (path == null) {
            d.ce("mavenRepository_set", "path == null", pathStr);
            return;
        }
        if (!TS_DirectoryUtils.isExistDirectory(path)) {
            d.ce("mavenRepository_set", "!TS_DirectoryUtils.isExistDirectory(path)", pathStr);
            return;
        }
        pomCfg.mavenRepository = path;
        d.cr("mavenRepository_set", pomCfg.mavenRepository);
    }
}
