package com.tugalsan.trm.ddosattacker;

import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.thread.server.async.TS_ThreadAsync;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.url.client.*;
import com.tugalsan.api.url.server.*;
import java.util.stream.*;

public class Main {
    //cd C:\me\codes\com.tugalsan\trm\com.tugalsan.trm.ddosattacker
    //java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.trm.ddosattacker-1.0-SNAPSHOT-jar-with-dependencies.jar

    final private static TS_Log d = TS_Log.of(Main.class);
    final private static TS_ThreadSyncTrigger killTrigger = TS_ThreadSyncTrigger.of("main");

    public static void main(String... args) {
        var url = TGS_Url.of("https://localhost:8081/hello1");
        IntStream.range(0, 2000000).forEach(i -> TS_ThreadAsync.now(killTrigger, kt -> {
            while (true) {
                TS_UrlDDosUtils.attack(url);
                System.out.println(".");
            }
        }));
        TS_ThreadWait.minutes(d.className, killTrigger, 1);
    }
}
