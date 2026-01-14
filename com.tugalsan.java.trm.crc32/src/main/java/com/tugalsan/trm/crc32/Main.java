package com.tugalsan.trm.crc32;

import com.tugalsan.api.desktop.server.TS_DesktopDialogInfoUtils;
import com.tugalsan.api.desktop.server.TS_DesktopPathUtils;
import com.tugalsan.api.file.csv.server.TS_FileCsvUtils;
import com.tugalsan.api.file.server.TS_DirectoryUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.list.client.TGS_ListTable;
import java.nio.file.Path;
import java.util.Optional;

public class Main {

    public static void main(String... args) {
        var folder = TS_DesktopPathUtils.chooseDirectory("", Optional.of(Path.of("C:\\Program Files\\SOLIDWORKS Corp")));
        if (folder.isEmpty()) {
            TS_DesktopDialogInfoUtils.show("", "folder == null -> exit;");
            System.exit(1);
            return;
        }

        var output = Path.of("crc32.xlsx");

        var table = TGS_ListTable.of(true);
        TS_DirectoryUtils
                .subFiles(folder.get(), "*.*", true, true)
                .forEach(file -> {
                    var create = TS_FileUtils.getTimeCreationTime(file);
                    var modified = TS_FileUtils.getTimeLastModified(file);
                    table.setValues(
                            table.getRowSize(),
                            String.valueOf(TS_FileUtils.getChecksumHex(file)),
                            create == null ? "null" : create.toString_dateOnly(),
                            modified == null ? "null" : modified.toString_dateOnly(),
                            file.toString()
                    );
                });
        TS_FileCsvUtils.toFile(table, output, true);

        TS_DesktopDialogInfoUtils.show("", "See " + output.toString());
        System.exit(0);
    }
}
