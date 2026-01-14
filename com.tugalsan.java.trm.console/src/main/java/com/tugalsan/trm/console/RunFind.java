package com.tugalsan.trm.console;

import com.tugalsan.api.console.client.TGS_ConsoleOption;
import com.tugalsan.api.file.server.TS_DirectoryUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.log.server.TS_Log;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RunFind {

    final private static TS_Log d = TS_Log.of(true, RunFind.class);

    public static TGS_ConsoleOption run = TGS_ConsoleOption.of((cmd, restArgs) -> {
        if (Objects.equals(restArgs.get(0), "pom")) {
            restArgs.remove(0);
            find_pom(restArgs);
            return;
        }
        d.ce(cmd, "next token not recognized");
    }, "f", "find");

    private static void find_pom(List<CharSequence> restArgs) {
        if (restArgs.size() != 2) {
            d.ce("find_pom", "restArgs.size() != 2");
            return;
        }
        TS_Consol
        var groupId = restArgs.get(0);
        var artifactId = restArgs.get(1);
        List<TS_FilePomDeprecated> buffer = new ArrayList();
        find_pom_recursive(buffer, WORK_SPACE, groupId, artifactId);
    }

    private static void find_pom_recursive(List<TS_FilePomDeprecated> buffer, Path parent, CharSequence groupId, CharSequence artifactId) {
        var pathPom = parent.resolve("pom.xml");
        if (TS_FileUtils.isExistFile(pathPom)) {
            var objPom = TS_FilePomDeprecated.of(pathPom);
            if (Objects.equals(objPom.groupId, groupId) && Objects.equals(objPom.articactId, artifactId)) {
                buffer.add(objPom);
            }
            return;
        }
        TS_DirectoryUtils.subDirectories(parent, true, false)
                .forEach(subDir -> find_pom_recursive(buffer, subDir, groupId, artifactId));
    }
//    private static void dep(List<CharSequence> restArgs) {
//        var com_tugalsan = TS_OsJavaUtils.getJarPath().getParent().getParent().getParent().getParent();
//        if (restArgs.size() != 2) {
//            d.ce("dep", "restArgs.size() != 2");
//            return;
//        }
//        var subDirName = restArgs.get(0);
//        var prjDirName = restArgs.get(1);
//        var subDir = com_tugalsan.resolve(subDirName.toString());
//        var prjDir = subDir.resolve(prjDirName.toString());
//        var prjPom = prjDir.resolve("pom.xml");
//        var pom = TS_FilePomDeprecated.of(prjPom);
//        if (!pom.isLoadedSuccessfully) {
//            d.cr("dep", "!pom.isLoadedSuccessfully @ " + pom.articactId);
//            return;
//        }
//        pom.dependenciesFull.forEach(dep -> {
//            d.cr("dep", "-", dep.articactId);
//        });
//    }
//
//    private static void lst(List<CharSequence> restArgs) {
//        var com_tugalsan = TS_OsJavaUtils.getJarPath().getParent().getParent().getParent().getParent();
//        if (restArgs.isEmpty()) {
//            lst_printSubDirs(com_tugalsan);
//            return;
//        }
//        var subDirName = restArgs.get(0);
//        var subDir = com_tugalsan.resolve(subDirName.toString());
//        if (!TS_DirectoryUtils.isExistDirectory(subDir)) {
//            d.ce("lst", "ERROR: subDir not exists @ " + subDir);
//            return;
//        }
//        lst_printSubDirs(subDir);
//    }
//
//    private static void lst_printSubDirs(Path parentDir) {
//        d.cr("lst_printSubDirs", TS_DirectoryUtils.getName(parentDir) + ":");
//        TS_DirectoryUtils.subDirectories(parentDir, true, false).forEach(subDir -> {
//            d.cr("lst_printSubDirs", "- " + TS_DirectoryUtils.getName(subDir));
//        });
//    }
}
