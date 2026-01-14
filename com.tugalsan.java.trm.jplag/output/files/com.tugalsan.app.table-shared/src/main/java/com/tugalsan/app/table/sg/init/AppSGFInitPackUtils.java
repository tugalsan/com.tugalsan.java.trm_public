package com.tugalsan.app.table.sg.init;

import com.tugalsan.lib.rql.client.TGS_LibRqlTbl;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AppSGFInitPackUtils {

    public static AppSGFInitPack toSGFPack(CharSequence input_tableName, List<TGS_LibRqlTbl> output_tables, List<AppSGFInitPack_ConfigTableUser> userPacks) {
        var sgfPack = new AppSGFInitPack(input_tableName);
        sgfPack.setOutput_tables(output_tables);
        sgfPack.setOutput_userAllowFileWrite(new ArrayList());
        sgfPack.setOutput_userColHideIdxes(new ArrayList());
        sgfPack.setOutput_userEditableDays(new ArrayList());
        sgfPack.setOutput_userTables(new ArrayList());
        userPacks.forEach(cfg -> {
            sgfPack.getOutput_userAllowFileWrite().add(cfg.allowFileWrite);
            sgfPack.getOutput_userColHideIdxes().add(cfg.colHideIdxes);
            sgfPack.getOutput_userEditableDays().add(cfg.editableDays);
            sgfPack.getOutput_userTables().add(cfg.table);
        });
        return sgfPack;
    }

    public static List<AppSGFInitPack_ConfigTableUser> toUserPacks(AppSGFInitPack sgfPack) {
        List<AppSGFInitPack_ConfigTableUser> userPack = new ArrayList();
        IntStream.range(0, sgfPack.getOutput_userTables().size()).forEachOrdered(i -> {
            userPack.add(AppSGFInitPack_ConfigTableUser.of(
                    sgfPack.getOutput_userTables().get(i),
                    sgfPack.getOutput_userColHideIdxes().get(i),
                    sgfPack.getOutput_userAllowFileWrite().get(i),
                    sgfPack.getOutput_userEditableDays().get(i)
            ));
        });
        return userPack;
    }
}
