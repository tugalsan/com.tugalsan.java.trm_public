package com.tugalsan.trm.console;

import com.tugalsan.api.console.server.TS_ConsoleUtils;
import com.tugalsan.api.file.pom.server.TS_FilePomConfig;
import com.tugalsan.api.list.client.TGS_ListUtils;
import java.util.List;

public class Main {

//    final private static TS_Log d = TS_Log.of(true, Main.class);
    final public static TS_FilePomConfig pomCfg = TS_FilePomConfig.of();

    //cd C:\me\codes\com.tugalsan\trm\com.tugalsan.trm.trm
    //java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.trm.trm-1.0-SNAPSHOT-jar-with-dependencies.jar
    public static void main(String... args) {
        TS_ConsoleUtils.mainLoop(
                List.of("q", "quit"), List.of("cls", "clear"),
                TGS_ListUtils.of(
                        RunFile.run,
                        RunMavenCodes.run,
                        RunMavenRepository.run,
                        RunMavenRepositoryProjects.run,
                        RunProjectGroupId.run,
                        RunFind.run
                )
        );
    }
}
