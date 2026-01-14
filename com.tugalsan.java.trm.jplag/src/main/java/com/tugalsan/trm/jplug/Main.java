package com.tugalsan.trm.jplug;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.ReportObjectFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        JavaLanguage language = new JavaLanguage();
        JPlagOptions options = new JPlagOptions(
                language,
                Set.of(new File("C:\\git\\app\\com.tugalsan.app.table")),
                Set.of()
        );
        try {
            JPlagResult result = JPlag.run(options);
            // Optional
            ReportObjectFactory reportObjectFactory = new ReportObjectFactory(new File("C:\\git\\trm\\com.tugalsan.trm.jplag\\output\\extract_me.zip"));
            reportObjectFactory.createAndSaveReport(result);
        } catch (ExitException e) {
            // error handling here
        } catch (FileNotFoundException e) {
            // handle IO exception here
        }
    }
}
