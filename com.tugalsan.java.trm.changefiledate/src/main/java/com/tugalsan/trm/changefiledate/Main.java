package com.tugalsan.trm.changefiledate;

import com.tugalsan.api.charset.client.TGS_CharSetLocaleTypes;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.os.server.TS_OsCpuUtils;
import com.tugalsan.api.random.server.*;
import java.nio.file.*;
import java.util.stream.IntStream;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\trm\com.tugalsan.trm.changefiledate
//java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.trm.changefiledate-1.0-SNAPSHOT-jar-with-dependencies.jar    
public class Main {

    final private static TS_Log d = TS_Log.of(true, Main.class);

    public static void main(String... args) {
        d.cr("jvm",TS_OsCpuUtils.getLoad_jvm());
        d.cr("pro",TS_OsCpuUtils.getLoad_process());
        d.cr("sys",TS_OsCpuUtils.getLoad_system());
        d.cr("osh",TS_OsCpuUtils.getLoad_percent_oshi(500));
        
        if (true){
            return;
        }
        
        var a = TGS_Time.of();

        IntStream.range(0, 14).forEach(i -> {
            a.incrementDay(1);
            d.cr("main", a.toString_dateOnly(), a.dayOfWeek_returns_1_to_7(), a.isDayWork(), a.isDayWeekend(), a.getDayOfWeekName(TGS_CharSetLocaleTypes.TURKISH));
        });

        if (true) {
            return;
        }

//        var dirPrefix = "\\\\10.0.0.222\\";
        var dirPrefix = "\\\\192.168.7.1\\";
//        var dir = Path.of(dirPrefix + "kalite_destek\\1-Alt Yapı Yönetimi\\KY AY FR 9 - Alarm Listesi Formu");
//        var dir = Path.of(dirPrefix + "kalite_destek\\2-Bilgi Güvenliği Yönetimi\\KY BG FR 1 - Bilgi Güvenliği Kontrol Formu");
        var dir = Path.of(dirPrefix + "kalite_destek\\11-Dokümante Bilgi\\SY DB FR 4 - Dokümante Edilmiş Bilgi Kontrolü Formu");
        dir(
                dir, "-",
                18, 24,
                0, 59,
                0, 59
        );
    }

    private static void dir(Path directory, String delim, int hourMin, int hourMax, int minMin, int minMax, int secMin, int secMax) {
        if (!TS_DirectoryUtils.isExistDirectory(directory)) {
            d.ce("run", "dir not exists");
            return;
        }
        TS_DirectoryUtils.subFiles(directory, "*.*", true, true).forEach(file -> {
            file(file, delim,
                    TS_RandomUtils.nextInt(hourMin, hourMax),
                    TS_RandomUtils.nextInt(minMin, minMax),
                    TS_RandomUtils.nextInt(secMin, secMax)
            );
        });
    }

    private static void file(Path file, String delim, int hour, int min, int sec) {
        d.cr("file", file);
        var fileLabel = TS_FileUtils.getNameLabel(file);
        var split = fileLabel.split(delim);
        if (split.length < 2) {//FILENAME XX-XX-XXXX
            d.ce("file", "fileLabel not proper", fileLabel, "skipped");
            return;
        }
        var dateLabel = TGS_Time.ofDate_YYYY_MM_DD(split[0]);
        if (dateLabel == null) {
            d.ce("file", "dateLabel NOT detected", "skipped");
            return;
        } else {
            d.cr("file", "dateLabel detected", dateLabel.toString_dateOnly(), "continuing...");
        }
        var dateCreate = TS_FileUtils.getTimeCreationTime(file);
        var dateModified = TS_FileUtils.getTimeLastModified(file);
        if (dateCreate.hasEqualDateWith(dateLabel) && dateModified.hasEqualDateWith(dateLabel)) {
            d.ce("file", "dateLabel skipped as dateCreate and dateModified is proper", "EARLY SKIPPED...");
            return;
        }
        TS_FileUtils.setTimeTimes(file, dateLabel.setHour(hour).setMinute(min).setSecond(sec));
        d.cr("file", "done", TS_FileUtils.getTimeCreationTime(file).toString_dateOnly());
    }
}
