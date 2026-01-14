package com.tugalsan.trm.recorder;

import com.tugalsan.api.input.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import java.nio.file.*;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
public class Main {

    final private static TS_Log d = TS_Log.of(Main.class);
    final private static TS_ThreadSyncTrigger killTrigger = TS_ThreadSyncTrigger.of("main");

    //cd C:\me\codes\com.tugalsan\trm\com.tugalsan.trm.recorder
    //java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.trm.recorder-1.0-SNAPSHOT-jar-with-dependencies.jar
    public static void main(String[] args) {
        System.out.println("0");
        var soundRecorder = TS_InputSound.of(killTrigger, Path.of("D:\\as\\a.wav"));
        System.out.println("1");
        TS_Thread.seconds(d.className, killTrigger, 20f);
        System.out.println("3");
        soundRecorder.kill();
        System.out.println("5");
        soundRecorder.saveToFile();
        System.out.println("6");
    }
}
