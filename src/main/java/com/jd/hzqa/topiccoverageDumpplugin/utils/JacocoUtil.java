package com.jd.hzqa.topiccoverageDumpplugin.utils;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.html.HTMLFormatter;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by qqs on 15/5/27.
 */
public class JacocoUtil {
    private final static Logger LOGGER = Logger.getLogger(JacocoUtil.class.getName());

    private final String title;
    private final File executionDataFile;
    private final File classesDirectory;
    private final File sourceDirectory;
    private static File reportDirectory;
    private ExecFileLoader execFileLoader;

    /**
     * Create a new generator based for the given project.
     *
     * @param projectDirectory
     */
    //
    //    [INFO] [18:53:13.088] Base dir: /Users/qqs/GitHub/wycds_web
    //    [INFO] [18:53:13.088] Working dir: /Users/qqs/GitHub/wycds_web/target/sonar
    //    [INFO] [18:53:13.088] Source dirs: /Users/qqs/GitHub/wycds_web/src/main/java
    //    [INFO] [18:53:13.088] Test dirs: /Users/qqs/GitHub/wycds_web/src/test/java
    //    [INFO] [18:53:13.088] Binary dirs: /Users/qqs/GitHub/wycds_web/target/classes
    //    [INFO] [18:53:13.089] Source encoding: UTF-8, default locale: zh_CN
    public JacocoUtil(final File projectDirectory) {
        this.title = projectDirectory.getName();
        this.executionDataFile = new File(projectDirectory, "target/jacoco-client.exec");
        this.classesDirectory = new File(projectDirectory, "target/classes");
        this.sourceDirectory = new File(projectDirectory, "src/main/java");
        this.reportDirectory = new File(projectDirectory, "target/coveragereport");

    }

    /**
     * Starts the execution data request.
     *
     * @throws java.io.IOException
     */

    public static boolean dumpIsOk(String DumpFile, String ADDRESS, int PORT) {
        boolean dumpSuccess = false;
        String DESTFILE = DumpFile + "/target/jacoco-client.exec";
        FileOutputStream localFile = null;
        Socket socket = null;
        try {
            localFile = new FileOutputStream(DESTFILE);
            ExecutionDataWriter localWriter = new ExecutionDataWriter(
                    localFile);
            // Open a socket to the coverage agent:
            socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
            RemoteControlWriter writer = new RemoteControlWriter(
                    socket.getOutputStream());
            RemoteControlReader reader = new RemoteControlReader(
                    socket.getInputStream());
            reader.setSessionInfoVisitor(localWriter);
            reader.setExecutionDataVisitor(localWriter);

            // Send a dump command and read the response:
            writer.visitDumpCommand(true, false);

            if (reader.read()) {
                System.out.println("dump 成功");
                dumpToHTML(DumpFile);
                dumpSuccess = true;
            } else {
                dumpSuccess = false;
                System.out.println("从 agent 获取 dump 文件失败");

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            dumpSuccess = false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            dumpSuccess = false;
        } catch (IOException e) {
            e.printStackTrace();
            dumpSuccess = false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (localFile != null) {
                try {
                    localFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dumpSuccess;
    }

    /**
     * Create the report.
     *
     * @throws java.io.IOException
     */
    public void create() throws IOException {

        // Read the jacoco.exec file. Multiple data files could be merged
        // at this point
        loadExecutionData();

        // Run the structure analyzer on a single class folder to build up
        // the coverage model. The process would be similar if your classes
        // were in a jar file. Typically you would create a bundle for each
        // class folder and each jar you want in your report. If you have
        // more than one bundle you will need to add a grouping node to your
        // report
        final IBundleCoverage bundleCoverage = analyzeStructure();

        createReport(bundleCoverage);

    }

    private void createReport(final IBundleCoverage bundleCoverage)
            throws IOException {

        // Create a concrete report visitor based on some supplied
        // configuration. In this case we use the defaults
        final HTMLFormatter htmlFormatter = new HTMLFormatter();
        final IReportVisitor visitor = htmlFormatter
                .createVisitor(new FileMultiReportOutput(reportDirectory));

        // Initialize the report with all of the execution and session
        // information. At this point the report doesn't know about the
        // structure of the report being created
        visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(),
                execFileLoader.getExecutionDataStore().getContents());

        // Populate the report structure with the bundle coverage information.
        // Call visitGroup if you need groups in your report.
        visitor.visitBundle(bundleCoverage, new DirectorySourceFileLocator(
                sourceDirectory, "utf-8", 4));

        // Signal end of structure information to allow report to write all
        // information out
        visitor.visitEnd();

    }

    private void loadExecutionData() throws IOException {
        execFileLoader = new ExecFileLoader();
        execFileLoader.load(executionDataFile);
    }

    private IBundleCoverage analyzeStructure() throws IOException {
        final CoverageBuilder coverageBuilder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(
                execFileLoader.getExecutionDataStore(), coverageBuilder);

        analyzer.analyzeAll(classesDirectory);

        return coverageBuilder.getBundle(title);
    }

    /**
     * @throws java.io.IOException
     */
    private static void dumpToHTML(String DumpFile) throws IOException {
        final JacocoUtil generator = new JacocoUtil(new File(
                DumpFile));
        try {
            generator.create();
            LOGGER.log(Level.INFO, "处理完毕,请打开 " + reportDirectory + "/index.html 查看报告");
        } catch (IOException e) {
            throw e;
        }

    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }
}
