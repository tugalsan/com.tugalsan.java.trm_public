package com.tugalsan.app.table.pop;

import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.thread.client.TGC_ThreadUtils;
import com.tugalsan.app.table.AppModuleTable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppPopEditPopLINKBufferStar {

    final private static TGC_Log d = TGC_Log.of(false, AppPopEditPopLINKBufferStar.class);

    private AppPopEditPopLINKBufferStar(String targetTable, List<String> output) {
        this.targetTable = targetTable;
        this.output = output;
    }
    private final String targetTable;
    private final List<String> output;

    public String getTargetTable() {
        return targetTable;
    }

    public List<String> getOutput() {
        return output;
    }

    public static List<AppPopEditPopLINKBufferStar> LST_INSTANCE = new ArrayList();

    public static void add(String targetTable, List<String> output) {
        LST_INSTANCE.add(new AppPopEditPopLINKBufferStar(targetTable, output));
    }

    public static Optional<List<String>> findAny(String targetTable) {
        return LST_INSTANCE.stream()
                .filter(item -> item.targetTable.equals(targetTable))
                .map(item -> item.output)
                .findAny();
    }

    public static void printInfo() {
        if (true) {
            return;
        }
        if (LST_INSTANCE.isEmpty()) {
            d.ce("printInfo", "onRefreshLb_buffer.isEmpty()", "onRefreshLb_buffer is empty");
        } else {
            d.ce("printInfo", "onRefreshLb_buffer.isPresent()", "Arama hafızada bulunamadı");
            LST_INSTANCE.forEach(item -> {
                d.ce("printInfo", "onRefreshLb_buffer.isPresent()", "onRefreshLb_buffer", item);
            });
        }
    }

    public static void clear(AppModuleTable tm) {
        TGC_ThreadUtils.run_afterGUIUpdate(() -> {
            LST_INSTANCE.clear();
            d.cr("onRefreshLb_buffer_clear", "BİLGİ: hafızadaki liste temizlendi");
            tm.cells.popEditCellLNGLINK.clear_buffer();
            tm.cells.popEditCellSTRLINK.clear_buffer();
            tm.filter.popMain.popFilterLINKBySelected.clear_buffer();
        });
    }

}
