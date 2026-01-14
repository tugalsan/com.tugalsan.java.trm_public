package com.tugalsan.trm.console;

import com.tugalsan.api.console.client.TGS_ConsoleOption;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.os.server.TS_OsProcessUtils;
import java.nio.file.Path;

public class RunFile {

    final private static TS_Log d = TS_Log.of(true, RunFile.class);

    public static TGS_ConsoleOption run = TGS_ConsoleOption.of((cmd, restArgs) -> {
        if (restArgs.isEmpty()) {
            d.ce(cmd, "ERROR: restArgs.isEmpty()");
            return;
        }
        var tuple2 = TGS_ListUtils.sliceFirstToken(restArgs);
        var jarFilePathStr = tuple2.value0;
        var jarFileArguments = tuple2.value1;
        var jarFilePath = Path.of(jarFilePathStr.toString());
        if (!TS_FileUtils.isExistFile(jarFilePath)) {
            d.ce(cmd, "ERROR: file not exists @ '" + jarFilePathStr + "'");
            return;
        }
        TS_OsProcessUtils.runJar(jarFilePath, jarFileArguments);
    }, "r", "run");
}
