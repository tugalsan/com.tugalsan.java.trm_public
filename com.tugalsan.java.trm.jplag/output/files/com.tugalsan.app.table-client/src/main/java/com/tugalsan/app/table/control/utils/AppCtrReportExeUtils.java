package com.tugalsan.app.table.control.utils;

import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.rql.client.TGS_LibRqlReport;
import com.tugalsan.api.file.html.client.*;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTUUtils;
import com.tugalsan.api.gui.client.browser.*;
import com.tugalsan.api.gui.client.pop.TGC_PopFrame;
import com.tugalsan.api.gui.client.pop.options.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.thread.client.*;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.api.url.client.TGC_UrlRequestUtils;
import com.tugalsan.api.url.client.TGS_Url;
import com.tugalsan.api.url.client.parser.TGS_UrlParser;
import com.tugalsan.app.table.*;
import com.tugalsan.lib.acsrf.client.*;
import com.tugalsan.lib.boot.client.*;
import com.tugalsan.lib.file.client.*;
import com.tugalsan.lib.report.client.sgf.*;
import com.tugalsan.lib.report.client.sgf.TGS_LibRepSGFRun_Param;
import com.tugalsan.lib.route.client.*;
import java.util.*;

public class AppCtrReportExeUtils {
    
    private AppCtrReportExeUtils(){
        
    }

    final private static TGC_Log d = TGC_Log.of(AppCtrReportExeUtils.class);

    public static void decide(TGC_PopFrame pf, TGS_LibRqlTbl t, TGS_LibRqlReport r, List<String> fileTypes, Long id,
            List<TGS_LibRepSGFRun_Param> clientCodeParams) {
        if (TGS_CharSetCast.current().containsIgnoreCase(r.value, TGS_PopYesNoOptionCodes.EXE_REDIRECT())) {
            redirect(pf, t, r, id, clientCodeParams, false);
            return;
        }
        if (TGS_CharSetCast.current().containsIgnoreCase(r.value, TGS_PopYesNoOptionCodes.EXE_NEWTAB())) {
            redirect(pf, t, r, id, clientCodeParams, true);
            return;
        }
        d.ci("decide", "redirect not detected");
        servlet(t, r, fileTypes, id, clientCodeParams);
    }

    private static void redirect(TGC_PopFrame pf, TGS_LibRqlTbl t, TGS_LibRqlReport r, Long id,
            List<TGS_LibRepSGFRun_Param> clientCodeParams, boolean asNewTab) {
        var tn = t.nameSql;
        var redirectCmdPrefix = asNewTab ? TGS_PopYesNoOptionCodes.EXE_NEWTAB().concat(TGS_PopYesNoOptionCodes.DELIM()) : TGS_PopYesNoOptionCodes.EXE_REDIRECT().concat(TGS_PopYesNoOptionCodes.DELIM());
        var parsedReportCodes = TGS_StringUtils.gwt().toList(r.value, "\n");
        var redirectCmd = parsedReportCodes.stream()
                .map(line -> line.trim())
                .filter(line -> line.startsWith(redirectCmdPrefix))
                .findAny().orElse(null);
        var url = TGS_LibRoute
                .of(TGS_Url.of(redirectCmd.substring(redirectCmdPrefix.length())))
                .setParam(TGS_LibFileServletUrlUtilsReport.PARAM_FETCH_REPORT_CONFIG_TABLE(), tn)
                .setParam(TGS_LibFileServletUrlUtilsReport.PARAM_FETCH_REPORT_CONFIG_ID(), id)
                .setParam(TGS_LibAcsrfServletUtils.PARAM_ACSRF(), App.route.getAcsrf());
        for (var p : clientCodeParams) {
            url = url.setParam(p.name, p.value);
        }
        d.ci("redirect", "redirectUrl", url.toString());
        d.cr("redirect", "Rapor yönlendirildi.");
        pf.setUrl(url.toUrl());
        if (asNewTab) {
            TGC_BrowserWindowUtils.openNew(url.toUrl());
        } else {
            pf.getPop().setVisible(true);
        }
    }

    private static void servlet(TGS_LibRqlTbl t, TGS_LibRqlReport r, List<String> fileTypes, Long id,
            List<TGS_LibRepSGFRun_Param> clientCodeParams) {
        var start = TGS_Time.of();
        var acfSafe = App.route.parser.quary.getParameterByName(TGS_LibAcsrfServletUtils.PARAM_ACSRF()).valueSafe;
        TGS_Tuple1<Boolean> onRun = new TGS_Tuple1(true);
        TGS_FuncMTCUtils.run(() -> {

            var tn = t.nameSql;
            var delayInSec = 2;
            TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(thread -> {
                if (onRun.value0) {
                    TGC_SGWTCalller.async(new TGS_LibRepSGFProgress(tn), resp -> {
                        if (resp.getOutput_progress() == null) {
                            d.ci("servlet", "BİLGİ: Rapor yapım yüzdesi çekilmedi uyarısı.");
                            return;
                        }
                        TGC_LibBootGUIBody.progress.getRange().set(0, 100);
                        TGC_LibBootGUIBody.progress.update(resp.getOutput_progress());
                    });
                    thread.run_afterSeconds(delayInSec);
                    return;
                }
                TGC_LibBootGUIBody.progress.getRange().set(0, 100);
                TGC_LibBootGUIBody.progress.update(100);
            }, delayInSec);

            d.cr("servlet", start.toString() + " Rapor oluşturuluyor...");

//            if (!fileTypes.contains(TGS_FileTmcrTypes.FILE_TYPE_HTML())) {
//                fileTypes.add(TGS_FileTmcrTypes.FILE_TYPE_HTML());
//            }
            d.ci("servlet", "pre-call...");
            var reportRun = new TGS_LibRepSGFRun(tn, r.param, id, fileTypes, clientCodeParams);
            d.ci("servlet", "reportRun", reportRun);
            TGC_SGWTCalller.async(reportRun, resp -> {
                onRun.value0 = false;
                d.ci("servlet", "onRun.value0 set as false");

                if (resp == null) {
                    d.ce("servlet", "HATA: İletişim hatası");
                    return;
                }

                if (resp.getOutput_result() == null) {
                    d.ce("servlet", "HATA: Rapor dosyası oluşturma bilgisi dönmedi; modül güncelleniyor olabilir; 1 dakika bekleyip tekrar deneyin");
                    d.ce("servlet", "ERROR: resp.getOutput_result() == null. TRY#1: Try to open url, on server side console error, open it in text mode to see it pretty. If TRY#1 output is halfway through, try to open tomcat logs to catch stackoverflow like errors.");
                    return;
                }

                if (!resp.getOutput_result()) {
                    d.ce("servlet", "HATA: Rapor dosyası oluşturulamadı.");
                    return;
                }
                d.ci("servlet", "sound.play...");
                TGC_LibBootGUISoundStatic.play();
                var end = TGS_Time.of();
                var secondDifference = start.getSecondsDifference(end);
                var serverSecondsText = "server: " + secondDifference + " sn";

                var newRoute = App.route
                        .setMdl(TGC_LibBootModuleFile.class.getSimpleName())
                        .setParam(TGC_LibBootModuleFile.PARAM_BROWSERTITLE(),
                                TGS_StringUtils.cmn().concat(
                                        App.domainCard.firmaNameShort,
                                        " > ",
                                        r.param
                                )
                        ).setParam(TGC_LibBootModuleFile.PARAM_BODYTITLE(),
                                TGS_StringUtils.cmn().concat(
                                        "<b>",
                                        "Bulut",
                                        TGS_FileHtmlText.charSpace(), TGS_FileHtmlText.charRightArrow(), TGS_FileHtmlText.charSpace(),
                                        "Tablo",
                                        TGS_FileHtmlText.charSpace(), TGS_FileHtmlText.charRightArrow(), TGS_FileHtmlText.charSpace(),
                                        t.nameGroup,
                                        TGS_FileHtmlText.charSpace(), TGS_FileHtmlText.charRightArrow(), TGS_FileHtmlText.charSpace(),
                                        t.nameReadable,
                                        TGS_FileHtmlText.charSpace(), TGS_FileHtmlText.charRightArrow(), TGS_FileHtmlText.charSpace(),
                                        r.param,
                                        "</b>"
                                )
                        ).setParam(TGC_LibBootModuleFile.PARAM_HIDE_BAR(), false);
                var urlFile = resp.getOutput_remoteFile_content();
                var contentPresent_justDownloadFile = TGS_StringUtils.cmn().isPresent(resp.getOutput_remoteFile_content());
                if (contentPresent_justDownloadFile) {
                    newRoute = newRoute.setParam(TGC_LibBootModuleFile.PARAM_CONTENT(), urlFile);
                }
                urlFile = resp.getOutput_remoteFile_macro();
                if (TGS_StringUtils.cmn().isPresent(urlFile)) {
                    if (!contentPresent_justDownloadFile) {
                        var urlFileWithAcf = TGS_UrlParser.of(TGS_Url.of(urlFile)).quary.setParameterValueUrlSafe(TGS_LibAcsrfServletUtils.PARAM_ACSRF(), acfSafe).toUrl();
                        TGC_BrowserWindowUtils.openNew(urlFileWithAcf);
                        d.cl("servlet", start.toString() + " Rapor indirildi. " + serverSecondsText, urlFileWithAcf.toString());
                        return;
                    }
                    newRoute = newRoute.setParam(TGC_LibBootModuleFile.PARAM_TMCR(), urlFile);
                }
                urlFile = resp.getOutput_remoteFile_htm();
                if (TGS_StringUtils.cmn().isPresent(urlFile)) {
                    if (!contentPresent_justDownloadFile) {
                        var urlFileWithAcf = TGS_UrlParser.of(TGS_Url.of(urlFile)).quary.setParameterValueUrlSafe(TGS_LibAcsrfServletUtils.PARAM_ACSRF(), acfSafe).toUrl();
                        TGC_BrowserWindowUtils.openNew(urlFileWithAcf);
                        d.cl("servlet", start.toString() + " Rapor indirildi. " + serverSecondsText, urlFileWithAcf.toString());
                        return;
                    }
                    newRoute = newRoute.setParam(TGC_LibBootModuleFile.PARAM_HTM(), urlFile);
                }
                urlFile = resp.getOutput_remoteFile_pdf();
                if (TGS_StringUtils.cmn().isPresent(urlFile)) {
                    if (!contentPresent_justDownloadFile) {
                        var urlFileWithAcf = TGS_UrlParser.of(TGS_Url.of(urlFile)).quary.setParameterValueUrlSafe(TGS_LibAcsrfServletUtils.PARAM_ACSRF(), acfSafe).toUrl();
                        TGC_BrowserWindowUtils.openNew(urlFileWithAcf);
                        d.cl("servlet", start.toString() + " Rapor indirildi. " + serverSecondsText, urlFileWithAcf.toString());
                        return;
                    }
                    newRoute = newRoute.setParam(TGC_LibBootModuleFile.PARAM_PDF(), urlFile);
                }
                urlFile = resp.getOutput_remoteFile_xlsx();
                if (TGS_StringUtils.cmn().isPresent(urlFile)) {
                    if (!contentPresent_justDownloadFile) {
                        var urlFileWithAcf = TGS_UrlParser.of(TGS_Url.of(urlFile)).quary.setParameterValueUrlSafe(TGS_LibAcsrfServletUtils.PARAM_ACSRF(), acfSafe).toUrl();
                        TGC_BrowserWindowUtils.openNew(urlFileWithAcf);
                        d.cl("servlet", start.toString() + " Rapor indirildi. " + serverSecondsText, urlFileWithAcf.toString());
                        return;
                    }
                    newRoute = newRoute.setParam(TGC_LibBootModuleFile.PARAM_XLSX(), urlFile);
                }
                urlFile = resp.getOutput_remoteFile_docx();
                if (TGS_StringUtils.cmn().isPresent(urlFile)) {
                    if (!contentPresent_justDownloadFile) {
                        var urlFileWithAcf = TGS_UrlParser.of(TGS_Url.of(urlFile)).quary.setParameterValueUrlSafe(TGS_LibAcsrfServletUtils.PARAM_ACSRF(), acfSafe).toUrl();
                        TGC_BrowserWindowUtils.openNew(urlFileWithAcf);
                        d.cr("servlet", start.toString() + " Rapor indirildi.", serverSecondsText, urlFileWithAcf);
                        return;
                    }
                    newRoute = newRoute.setParam(TGC_LibBootModuleFile.PARAM_DOCX(), urlFile);
                }
                urlFile = resp.getOutput_remoteFile_zip();
                if (TGS_StringUtils.cmn().isPresent(urlFile)) {
                    if (!contentPresent_justDownloadFile) {
                        var urlFileWithAcf = TGS_UrlParser.of(TGS_Url.of(urlFile)).quary.setParameterValueUrlSafe(TGS_LibAcsrfServletUtils.PARAM_ACSRF(), acfSafe).toUrl();
                        TGC_BrowserWindowUtils.openNew(urlFileWithAcf);
                        d.cl("servlet", start.toString() + " Rapor indirildi. " + serverSecondsText, urlFileWithAcf.toString());
                        return;
                    }
                    newRoute = newRoute.setParam(TGC_LibBootModuleFile.PARAM_ZIP(), urlFile);
                }
                var disableRouteExitsCheck = true;
                if (disableRouteExitsCheck) {
                    TGC_BrowserWindowUtils.openNew(newRoute.toUrl());
                    d.cl("servlet", start.toString() + " Rapor oluşturuldu. " + serverSecondsText, newRoute.toString());
                    return;
                }
                var finalNewRoute = newRoute;
                TGC_UrlRequestUtils.async_get(finalNewRoute.toUrl(), onResponse -> {
                    if (onResponse.getStatusCode() != TGC_UrlRequestUtils.getStatusCodeOk()) {
                        var waitSecs = 5;
                        d.cr("servlet", start.toString() + " Rapor oluşturuldu", serverSecondsText, "Rapor için bekleniyor...", waitSecs);
                        TGC_ThreadUtils.run_afterSeconds(exe -> {
                            TGC_BrowserWindowUtils.openNew(finalNewRoute.toUrl());
                            d.cl("servlet", start.toString() + " Rapor gecikmeli oluşturuldu. " + serverSecondsText, finalNewRoute.toString());
                            d.infoEnable = true;
                            d.ci("servlet", "YAMA: Açılacak sayfayı yenileyin veya daha az dosya tipi seçmeyi deneyin");
                            d.infoEnable = false;
                        }, waitSecs);
                        return;
                    }
                    TGC_BrowserWindowUtils.openNew(finalNewRoute.toUrl());
                    d.cl("servlet", start.toString() + " Rapor oluşturuldu. " + serverSecondsText, finalNewRoute.toString());
                });
            });
        }, e -> {
            onRun.value0 = false;
            d.ce("servlet", "ERROR", e.getMessage());
            TGS_FuncMTUUtils.thrw(e);
        });
    }
}
