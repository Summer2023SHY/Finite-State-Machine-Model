package datagathering;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.RandomAccessFileMode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.*;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import help.AgentChicanery;
import help.EventSets;
import help.RandomGenStats;
import help.RandomGeneration;
import help.SystemGeneration;
import model.Manager;
import model.process.coobservability.Incremental;

/**
 *
 *
 * Pick out a subsystem to test of the HISC example
 *
 *
 *
 * Integrate specific examples from papers, make sure run heuristics on them
 *
 * 2 or 3 max transitions in the random generation? May want to define that differently based on size.
 *
 *
 *
 * Heuristics incremental tests should also compare against 100 true-random heuristic examples averaged

 * MemoryMeasure should probably be a Map instead of synchronized lists; when writing to file map each key to an index to associate value properly; use dummy value to denote it's missing

 * Heuristic: Alternate choosing Plants or Specs

 * Possibility on skipping redoing a complete test to not note its result for comparison to other actively calculated results. Need to retrieve result somehow.
 *  - A logic error could slip through if a later test finished after a preliminary crash and prior test results aren't in memory
 *
 * Retain some progress data for incremental by assigning memory measure object before test begins, then save output of that when marking unfinished?
 *  - Decide how much data we want; could track every sub-system along the way and not just the final one before resetting, but may be messy/hard to interpret.

 * @author aclevinger
 *
 */

public class DataGathering {

//---  Constants   ----------------------------------------------------------------------------

    private final String RESULTS_FILE = "output.txt";

    private final String ANALYSIS_FILE = "raw_num";

    private final String PROCESS_FILE = "analysis";

    private final String RAW_DATA_FILE = "raw_data";

    private final String TEXT_EXTENSION = ".txt";

    private final String TEST_NAME = "test";

    private final String VERIFY_COMPLETE_TEST = "!!~~Verified Complete and Done!~~!!";

    private final String DECLARE_MEMORY_ERROR = "!!~~Possible Memory Exception~~!!";

    private final String VERIFY_MEMORY_ERROR = "!!~~Verified Memory Exception~~!!";

    private final String VERIFY_COMPLETE_CHECKPOINT = "!!~~Verified Subtest Complete~~!!";

    private final int MINIMUM_TRUE_RESULTS = 25;

    private final int NUMBER_REPEAT_TEST = 3;

    private final int NUMBER_EXISTING_TEST_RUNS = 100;

    private final int TEST_ALL = 0;
    private final int TEST_BASIC = 1;
    private final int TEST_INC = 2;
    private final int TEST_HEUR = 3;
    private final int TEST_SB_INC = 4;

    private final int TYPE_COOBS = 0;
    private final int TYPE_SB = 1;
    private final int TYPE_INC_COOBS = 2;
    private final int TYPE_INC_SB = 3;
    private final String ANALYSIS_COOBS = "_coobs";
    private final String ANALYSIS_SB = "_sb";
    private final String ANALYSIS_INC_COOBS = "_inc_coobs";
    private final String ANALYSIS_INC_SB = "_inc_sb";

    private final String[] ANALYSIS_TYPES = new String[] {ANALYSIS_COOBS, ANALYSIS_SB, ANALYSIS_INC_COOBS, ANALYSIS_INC_SB};

    private final String[] TEST_NAMES = new String[] {"/Test Batch Random Basic 1",                 //0
                                                             "/Test Batch Random Basic 2",
                                                             "/Test Batch Random Basic 3",
                                                             "/Test Batch Random Basic 4",
                                                             "/Test Batch Random Inc 1",            //4
                                                             "/Test Batch Random Inc 2",
                                                             "/Test Batch Random Inc 3",
                                                             "/Test Batch Random Heuristic 1",      //7
                                                             "/Test Batch Random Heuristic 2",
                                                             "/Test Batch Basic DTP",               //9
                                                             "/Test Basic Batch HISC High",         //10
                                                             "/Test Basic Batch HISC Low",          //11
                                                             "/Test Batch Incremental DTP",         //12
                                                             "/Test Batch Incremental HISC High",   //13
                                                             "/Test Batch Incremental HISC Low",    //14
                                                             "/Test Batch Incremental HISC"};       //15

    private final int[] TEST_SIZES = new int[] {       150,
                                                       100,
                                                       100,
                                                       100,
                                                       150,
                                                       125,
                                                       75,
                                                       200,
                                                       200,
                                                       NUMBER_EXISTING_TEST_RUNS,
                                                       NUMBER_EXISTING_TEST_RUNS,
                                                       NUMBER_EXISTING_TEST_RUNS,
                                                       NUMBER_EXISTING_TEST_RUNS,
                                                       NUMBER_EXISTING_TEST_RUNS,
                                                       NUMBER_EXISTING_TEST_RUNS,
                                                       NUMBER_EXISTING_TEST_RUNS,
                                                       NUMBER_EXISTING_TEST_RUNS
                                                       };

    private static Logger logger = LogManager.getLogger();

//---  Instance Variables   -------------------------------------------------------------------

    private Manager model;

    private List<String> eventAtt;

    private String defaultWritePath;

    private String writePath;

    private String analysisSubtype;

    private TestReset clock;

    private boolean finished;

    private boolean heuristics;

//---  Constructors   -------------------------------------------------------------------------

    public DataGathering(TestReset inClock) {
        clock = inClock;
    }

    public File initializeDataGathering() {
        FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_IMAGES);
        model = new Manager();

        File f = new File(FiniteStateMachine.ADDRESS_IMAGES);
        f = f.getParentFile();

        f = new File(f.getAbsolutePath() + "/TestBatches/");
        f.mkdir();

        SystemGeneration.assignManager(model);

        eventAtt = new ArrayList<String>();
        for(String s : EventSets.EVENT_ATTR_LIST) {
            eventAtt.add(s);
        }

        defaultWritePath = f.getAbsolutePath();

        return f;
    }

    private void initializeTestFolder(File f, String in) {
        File g = new File(f, in);
        g.mkdir();
    }

//---  Operations   ---------------------------------------------------------------------------

    public void allInOneRunTests() {
        finished = false;
        try {
            runTests(initializeDataGathering());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        finished = true;
    }

    public void runTests(File f) throws IOException {
        heuristics = false;

        boolean runThrough = false;         //set true if you want to force it to go through and rewrite the analysis files/check for gaps

        //resetDataGathered(f);


        //Existing Tests

        Manager.assignEndAtFirstCounterexample(false);

    //  clock.setTimeOutLong();

        Incremental.assignIncrementalOptions(0, 1, 1);
/*
        initializeTestFolder(f, TEST_NAMES[15]);
        if(!testsCompletedNonRandom(TEST_NAMES[15], ANALYSIS_COOBS, TEST_SIZES[15]) || runThrough) {
            testIncrementalConfigHISC();
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
        }*/

        runBasicTests(f, runThrough);
        clock.resetClock();
        runIncrementalTests(f, runThrough);
        clock.resetClock();
        runHeuristicTests(f, runThrough);
        clock.resetClock();
        runBasicExamples(f, runThrough);
        clock.resetClock();
        runIncrementalExamples(f, runThrough);

    }

    private void runBasicTests(File f, boolean runThrough) throws IOException {
        clock.setTimeOutShort();
        //Coobs and SB
        int testNum = 0;
        initializeTestFolder(f, TEST_NAMES[testNum]);
        if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]) || runThrough) {
            testBasicConfigOne(TEST_SIZES[testNum]);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
        }
        testNum = 1;
        initializeTestFolder(f, TEST_NAMES[testNum]);
        if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]) || runThrough) {
            testBasicConfigTwo(TEST_SIZES[testNum]);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
        }
        testNum = 2;
        initializeTestFolder(f, TEST_NAMES[testNum]);
        if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]) || runThrough) {
            testBasicConfigThree(TEST_SIZES[testNum]);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
        }
        testNum = 3;
        initializeTestFolder(f, TEST_NAMES[testNum]);
        if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]) || runThrough) {
            testBasicConfigFour(TEST_SIZES[testNum]);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
        }
    }

    private void runIncrementalTests(File f, boolean runThrough) throws IOException {
        clock.setTimeOutShort();
        // SB and Inc
        int testNum = 4;
        initializeTestFolder(f, TEST_NAMES[testNum]);
        if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_INC_COOBS, TEST_SIZES[testNum]) || runThrough) {
            testIncConfigOne(TEST_SIZES[testNum]);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
        }
        testNum = 5;
        initializeTestFolder(f, TEST_NAMES[testNum]);
        if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_INC_COOBS, TEST_SIZES[testNum]) || runThrough) {
            testIncConfigTwo(TEST_SIZES[testNum]);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
        }
        testNum = 6;
        initializeTestFolder(f, TEST_NAMES[testNum]);
        if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_INC_COOBS, TEST_SIZES[testNum]) || runThrough) {
            testIncConfigThree(TEST_SIZES[testNum]);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
        }

    }

    private void runHeuristicTests(File f, boolean runThrough) throws IOException {
        heuristics = true;
        //Heuristics Test (SB Inc and Coobs Inc)
        int testNum = 7;
        initializeTestFolder(f, TEST_NAMES[testNum]);
        if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_INC_COOBS, TEST_SIZES[testNum]) || runThrough) {
            testHeuristicConfigOne(TEST_SIZES[testNum]);
            interpretTestBatchDataHeuristics(defaultWritePath, ANALYSIS_INC_COOBS);
            interpretTestBatchDataHeuristics(defaultWritePath, ANALYSIS_INC_SB);
        }
        testNum = 8;
        initializeTestFolder(f, TEST_NAMES[testNum]);
        if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_INC_COOBS, TEST_SIZES[testNum]) || runThrough) {
            testHeuristicConfigTwo(TEST_SIZES[testNum]);
            interpretTestBatchDataHeuristics(defaultWritePath, ANALYSIS_INC_COOBS);
            interpretTestBatchDataHeuristics(defaultWritePath, ANALYSIS_INC_SB);
        }

        heuristics = false;
    }

    private void runBasicExamples(File f, boolean runThrough)  {
        initializeTestFolder(f, TEST_NAMES[9]);
        if(!testsCompletedNonRandom(TEST_NAMES[9], ANALYSIS_COOBS, TEST_SIZES[9]) || runThrough) {
            testBasicConfigDTP();
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
        }

        initializeTestFolder(f, TEST_NAMES[10]);
        if(!testsCompletedNonRandom(TEST_NAMES[10], ANALYSIS_COOBS, TEST_SIZES[10]) || runThrough) {
            testBasicConfigHISCHigh();
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
        }


        initializeTestFolder(f, TEST_NAMES[11]);
        if(!testsCompletedNonRandom(TEST_NAMES[11], ANALYSIS_COOBS, TEST_SIZES[11]) || runThrough) {
            testBasicConfigHISCLow();
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
        }
    }

    private void runIncrementalExamples(File f, boolean runThrough)  {

        initializeTestFolder(f, TEST_NAMES[12]);
        if(!testsCompletedNonRandom(TEST_NAMES[12], ANALYSIS_COOBS, TEST_SIZES[12]) || runThrough) {
            testIncrementalConfigDTP();
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
        }
        clock.resetClock();

        initializeTestFolder(f, TEST_NAMES[13]);
        if(!testsCompletedNonRandom(TEST_NAMES[13], ANALYSIS_COOBS, TEST_SIZES[13]) || runThrough) {
            testIncrementalConfigHISCHigh();
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
        }
        clock.resetClock();

        initializeTestFolder(f, TEST_NAMES[14]);
        if(!testsCompletedNonRandom(TEST_NAMES[14], ANALYSIS_COOBS, TEST_SIZES[14]) || runThrough) {
            testIncrementalConfigHISCLow();
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
        }
        clock.resetClock();

        clock.setTimeOutLong();

        initializeTestFolder(f, TEST_NAMES[15]);
        if(!testsCompletedNonRandom(TEST_NAMES[15], ANALYSIS_COOBS, TEST_SIZES[15]) || runThrough) {
            testIncrementalConfigHISC();
            interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
            interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
        }
    }

    private void resetDataGathered(File f) {
        //Deletes all the result files but keeps the generated systems in place for a re-run

        for(String s : TEST_NAMES) {
            int counter = 0;

            String path = f.getAbsolutePath() + File.separator + s + File.separator;

            for(String t : ANALYSIS_TYPES) {
                new File(path + RAW_DATA_FILE, t + ".txt").delete();
            }

            if(!(new File(path).exists())) {
                continue;
            }

            int size = new File(path).list().length;

            while(counter < size + 1) {

                String outputFile = path + TEST_NAME + "_" + counter + File.separator + RESULTS_FILE;
                new File(outputFile).delete();
                System.out.println(outputFile);

                for(String t : ANALYSIS_TYPES) {
                    outputFile = path + TEST_NAME + "_" + counter + File.separator + ANALYSIS_FILE + t + ".txt";
                    new File(outputFile).delete();
                }

                counter++;
            }

        }

    }

    //-- Test Data Interpretation Simple  ---------------------

    private void interpretTestBatchDataSimple(String path, String type) {
        generateRawDataFileSimple(path, type);
        InterpretData hold = generateInterpretDataSimple(path, type);
        outputInterpretDataSimple(hold, path, type, StringUtils.EMPTY);
        hold.setColumnFilter(2);
        hold.setFilterValue(1.0);
        outputInterpretDataSimple(hold, path, type, BooleanUtils.TRUE);
        hold.setFilterValue(0.0);
        outputInterpretDataSimple(hold, path, type, BooleanUtils.FALSE);
    }

    private void generateRawDataFileSimple(String path, String type) {
        int counter = 0;

        int size = new File(path).list().length;
        String[] attributes = null;

        File f = new File(path, TEST_NAME + "_" + counter++ + File.separator + ANALYSIS_FILE + type + ".txt");
        File g = new File(path, RAW_DATA_FILE + type + ".txt");

        f.delete();
        g.delete();

        try (RandomAccessFile rag = RandomAccessFileMode.READ_WRITE.create(g)) {
            RandomAccessFile raf;
            while(counter <= size+1) {

                if(f.exists()) {
                    raf = RandomAccessFileMode.READ_WRITE.create(f);
                    boolean skip = false;
                    if(attributes == null) {
                        String line = raf.readLine();
                        if(line != null) {
                            attributes = line.split(", ");
                            rag.writeBytes(line + StringUtils.LF);
                        }
                        else
                            skip = true;
                    }
                    else {
                        raf.readLine();
                    }
                    InterpretData hold = new InterpretData();
                    if(!skip) {
                        String line = raf.readLine();
                        String[] values = line.split(", ");
                        if(values[values.length - 1].isEmpty()) {
                            values = Arrays.copyOf(values, values.length - 2);
                        }
                        while(values != null) {
                            for(int i = 0; i < values.length; i++) {
                                values[i] = values[i].trim();
                            }
                            if(!values[0].isEmpty())
                                hold.addDataRow(values);
                            String next = raf.readLine();
                            values = next == null ? null : next.split(", ");
                        }
                        List<Double> aver = hold.calculateAverages();
                        //System.out.println(hold.calculateInterquartileRange() + " " + hold.calculateFirstQuartile() + " " + hold.calculateThirdQuartile());
                        for(int i = 0; i < aver.size(); i++) {
                            rag.writeBytes(Double.toString(threeSig(aver.get(i))) + (i + 1 == aver.size() ? StringUtils.EMPTY : ", "));
                        }
                        rag.writeBytes(StringUtils.LF);
                    }
                    raf.close();
                }
                f = new File(path, TEST_NAME + "_" + counter++ + File.separator + ANALYSIS_FILE + type + ".txt");
            }
        }
        catch(IOException e) {
            System.out.println(f.getAbsolutePath());
            logger.catching(e);
        }
    }

    private InterpretData generateInterpretDataSimple(String path, String type) {
        InterpretData hold = new InterpretData();
        hold.assignTotalNumberTests(getTotalNumberTests(path));

        String[] attributes = null;
        File f = new File(path, RAW_DATA_FILE + type + ".txt");

        try {
            RandomAccessFile raf;
            if(f.exists()) {
                raf = RandomAccessFileMode.READ_WRITE.create(f);
                String line = raf.readLine();
                if(line != null) {
                    attributes = line.split(", ");
                    hold.assignAttributes(attributes);
                }

                line = raf.readLine();

                while(line != null) {
                    String[] values = line.split(", ");
                    for(int i = 0; i < values.length; i++) {
                        values[i] = values[i].trim();
                    }
                    hold.addDataRow(values);
                    line = raf.readLine();
                }

                raf.close();
            }
        }
        catch (IOException e) {
            System.out.println(f.getAbsolutePath());
            logger.catching(e);
        }
        return hold;
    }

    private void outputInterpretDataSimple(InterpretData hold, String path, String type, String suffix) {
        File f = new File(path, PROCESS_FILE + type + (suffix.isEmpty() ? StringUtils.EMPTY : "_") + suffix + ".txt");
        f.delete();
        try (RandomAccessFile raf = RandomAccessFileMode.READ_WRITE.create(f)) {
            fileWriteInterpretDataGeneral(hold, raf);
            raf.writeBytes("\n\n");
            fileWriteInterpretDataOverleafTableGeneral(hold, raf);
        }
        catch (IOException e) {
            logger.catching(e);
        }
    }

    private void fileWriteInterpretDataGeneral(InterpretData hold, RandomAccessFile raf) throws IOException {
        String[] attributes = hold.getAttributes();

        raf.writeBytes("\t\t\t\t\t\t");
        for(String s : attributes) {
            raf.writeBytes(s + ", ");
        }
        raf.writeBytes("\nAverage: \t\t\t\t");
        for(double v : hold.calculateAverages()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nMinimum: \t\t\t\t");
        for(double v : hold.calculateMinimums()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nMaximum: \t\t\t\t");
        for(double v : hold.calculateMaximums()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nMedian: \t\t\t\t");
        for(double v : hold.calculateMedians()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nFirst Quartile:\t\t\t");
        for(double v : hold.calculateFirstQuartile()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nThird Quartile:\t\t\t");
        for(double v : hold.calculateThirdQuartile()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nInter Quart Range:\t\t");
        for(double v : hold.calculateInterquartileRange()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nNumber Valid Tests:\t\t");
        for(Integer v : hold.getColumnSizes()) {
            raf.writeBytes(v + ", \t\t");
        }
        raf.writeBytes("\n\nNumber True Results:\t" + hold.getNumberTrueResults());
        raf.writeBytes("\nNumber False Results:\t" + hold.getNumberFalseResults());
        raf.writeBytes("\nNumber of DNFs:\t\t\t" + hold.getNumberDNF());
    }

    //-- Test Data Interpretation Incremental  ----------------

    private void interpretTestBatchDataIncremental(String path, String type) {
        generateRawDataFileIncremental(path, type);
        InterpretDataNested hold = generateInterpretDataIncremental(path, type);
        outputInterpretDataIncremental(hold, path, type, StringUtils.EMPTY);
        hold.setColumnFilter(2);
        hold.setFilterValue(1.0);
        outputInterpretDataIncremental(hold, path, type, BooleanUtils.TRUE);
        hold.setFilterValue(0.0);
        outputInterpretDataIncremental(hold, path, type, BooleanUtils.FALSE);
    }

    private void generateRawDataFileIncremental(String path, String type) {
        int counter = 0;

        int size = new File(path).list().length;
        String[] attributes = null;

        File f = new File(path, TEST_NAME + "_" + counter++ + File.separator + ANALYSIS_FILE + type + ".txt");
        File g = new File(path, RAW_DATA_FILE + type + ".txt");

        f.delete();
        g.delete();

        try (RandomAccessFile rag = RandomAccessFileMode.READ_WRITE.create(g)) {
            RandomAccessFile raf;
            while(counter <= size+1) {

                if(f.exists()) {
                    raf = RandomAccessFileMode.READ_WRITE.create(f);
                    boolean skip = false;
                    if(attributes == null) {
                        String line = raf.readLine();
                        if(line != null) {
                            attributes = line.split(", ");
                            rag.writeBytes(line + StringUtils.LF);
                        }
                        else
                            skip = true;
                    }
                    else {
                        raf.readLine();
                    }
                    InterpretData holdOverall = new InterpretData();
                    InterpretData holdSub = new InterpretData();
                    if(!skip) {
                        String line = raf.readLine();
                        String[] values = line.split(", ");
                        if(values[values.length - 1].isEmpty()) {
                            values = Arrays.copyOf(values, values.length - 2);
                        }
                        while(values != null) {
                            for(int i = 0; i < values.length; i++) {
                                values[i] = values[i].trim();
                            }
                            if(!values[0].isEmpty()) {
                                holdOverall.addDataRow(values);
                            }
                            else {
                                values = Arrays.copyOfRange(values, 2, values.length - 1);
                                holdSub.addDataRow(values);
                            }
                            String next = raf.readLine();
                            values = next == null ? null : next.split(", ");
                        }
                        List<Double> averOverall = holdOverall.calculateAverages();
                        List<Double> averSub = holdSub.calculateAverages();
                        //System.out.println(hold.calculateInterquartileRange() + " " + hold.calculateFirstQuartile() + " " + hold.calculateThirdQuartile());
                        for(int i = 0; i < averOverall.size(); i++) {
                            rag.writeBytes(Double.toString(threeSig(averOverall.get(i))) + (i + 1 == averOverall.size() ? StringUtils.EMPTY : ", "));
                        }
                        rag.writeBytes("\n, , ");
                        for(int i = 0; i < averSub.size(); i++) {
                            rag.writeBytes(Double.toString(threeSig(averSub.get(i))) + (i + 1 == averSub.size() ? StringUtils.EMPTY : ", "));
                        }
                        rag.writeBytes(StringUtils.LF);
                    }
                    raf.close();
                }
                f = new File(path, TEST_NAME + "_" + counter++ + File.separator + ANALYSIS_FILE + type + ".txt");
            }
        }
        catch(IOException e) {
            System.out.println(f.getAbsolutePath());
            logger.catching(e);
        }
    }

    private InterpretDataNested generateInterpretDataIncremental(String path, String type) {
        InterpretDataNested hold = new InterpretDataNested();
        hold.assignTotalNumberTests(getTotalNumberTests(path));

        String[] attributes = null;
        File f = new File(path, RAW_DATA_FILE + type + ".txt");
        int counter = 0;
        try {
            RandomAccessFile raf;
            if(f.exists()) {
                raf = RandomAccessFileMode.READ_WRITE.create(f);
                String line = raf.readLine();
                attributes = line.split(", ");
                hold.assignAttributes(attributes);

                line = raf.readLine();

                String[] values;

                while(line != null) {
                    values = line.split(", ");
                    for(int i = 0; i < values.length; i++) {
                        values[i] = values[i].trim();
                    }
                    if(!values[0].isEmpty()) {
                        hold.addDataRow(values);
                    }
                    else {
                        hold.addAssociatedDataRow(values);
                        counter++;
                    }
                    line = raf.readLine();
                }

                raf.close();
            }
        }
        catch(IOException e) {
            System.out.println(f.getAbsolutePath());
            logger.catching(e);
        }
        hold.assignTotalNumberTestsNested(counter);
        return hold;
    }

    private void outputInterpretDataIncremental(InterpretDataNested hold, String path, String type, String suffix) {
        File f = new File(path + File.separator + PROCESS_FILE + type + "_" + suffix + ".txt");
        f.delete();
        try (RandomAccessFile raf = RandomAccessFileMode.READ_WRITE.create(f)) {
            fileWriteInterpretDataGeneral(hold, raf);
            raf.writeBytes("\n\nAnalysis of Incremental Subsystems\n");
            fileWriteInterpretDataIncremental(hold, raf);

            raf.writeBytes("\n\n");
            fileWriteInterpretDataOverleafTableGeneral(hold, raf);
        }
        catch(IOException e) {
            logger.catching(e);
        }
    }

    private void fileWriteInterpretDataIncremental(InterpretDataNested holdIn, RandomAccessFile raf) throws IOException {
        String[] attributes = holdIn.getAttributes();

        raf.writeBytes("\t\t\t\t\t\t");
        for(int i = 3; i < attributes.length; i++) {
            raf.writeBytes(attributes[i] + ", ");
        }
        InterpretData hold = holdIn.getAssociatedData().copy();
        int numTrue = hold.getNumberTrueResults();
        int numFalse = hold.getNumberFalseResults();
        int numDNF = hold.getNumberDNF();
        hold.deleteFirstValue();
        hold.deleteFirstValue();
        hold.deleteFirstValue();
        raf.writeBytes("\nAverage: \t\t\t\t");
        for(double v : hold.calculateAverages()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nMinimum: \t\t\t\t");
        for(double v : hold.calculateMinimums()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nMaximum: \t\t\t\t");
        for(double v : hold.calculateMaximums()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nMedian: \t\t\t\t");
        for(double v : hold.calculateMedians()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nFirst Quartile:\t\t\t");
        for(double v : hold.calculateFirstQuartile()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nThird Quartile:\t\t\t");
        for(double v : hold.calculateThirdQuartile()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nInter Quart Range:\t\t");
        for(double v : hold.calculateInterquartileRange()) {
            raf.writeBytes(threeSig(v) + ", \t\t");
        }
        raf.writeBytes("\nNumber Total Subsys:\t");
        for(Integer v : hold.getColumnSizes()) {
            raf.writeBytes(v + ", \t\t");
        }

        raf.writeBytes("\n\nNumber True Results:\t" + numTrue);
        raf.writeBytes("\nNumber False Results:\t" + numFalse);
        raf.writeBytes("\nNumber of DNFs:\t\t\t" + numDNF);
    }

    private void fileWriteInterpretDataOverleafTableGeneral(InterpretData holdIn, RandomAccessFile raf) throws IOException {
        String[] attributes = holdIn.getAttributes();

        StringBuilder part = new StringBuilder("{|");

        for(int i = 0; i < attributes.length; i++) {
            part.append("c|");
        }

        part.append("}");

        raf.writeBytes("\\begin{table}[H]\n"
                + "\\begin{center}\n"
                + "\\begin{footnotesize}\n"
                + "    \\begin{tabular}" + part.toString() + "\n"
                + "    \\hline\n\t");
        for(String s : attributes) {
            raf.writeBytes(" & " + s);
        }
        raf.writeBytes("\\\\\n\t\\hline\n\tAverage");
        for(double v : holdIn.calculateAverages()) {
            raf.writeBytes(" & " + threeSig(v));
        }
        raf.writeBytes("\\\\\n\t\\hline\n\tMinimum");
        for(double v : holdIn.calculateMinimums()) {
            raf.writeBytes(" & " + threeSig(v));
        }
        raf.writeBytes("\\\\\n\t\\hline\n\tMaximum");
        for(double v : holdIn.calculateMaximums()) {
            raf.writeBytes(" & " + threeSig(v));
        }
        raf.writeBytes("\\\\\n\t\\hline\n\tMedian");
        for(double v : holdIn.calculateMedians()) {
            raf.writeBytes(" & " + threeSig(v));
        }
        raf.writeBytes("\\\\\n\t\\hline\n\t$Q_1$");
        for(double v : holdIn.calculateFirstQuartile()) {
            raf.writeBytes(" & " + threeSig(v));
        }
        raf.writeBytes("\\\\\n\t\\hline\n\t$Q_3$");
        for(double v : holdIn.calculateThirdQuartile()) {
            raf.writeBytes(" & " + threeSig(v));
        }
        raf.writeBytes("\\\\\n\t\\hline\n\tIQR");
        for(double v : holdIn.calculateInterquartileRange()) {
            raf.writeBytes(" & " + threeSig(v));
        }

        raf.writeBytes("\\\\\n\t\\hline\n"
                + "    \\end{tabular}\n"
                + "\\end{footnotesize}\n"
                + "\\caption{Test results for ...}\n"
                + "\\end{center}\n"
                + "\\end{table}");
    }

    //-- Test Data Interpretation Heuristics  -----------------

    private void interpretTestBatchDataHeuristics(String path, String type) {

    }

    //-- Existing Tests  --------------------------------------

    abstract class BatchSetup {

        public abstract void setUpSystem();

    }

    private void testBasicConfigDTP()  {

        List<String> names = SystemGeneration.generateSystemSetDTP();

        List<String> plant = new ArrayList<String>(names.subList(0, 3));
        List<String> spec = new ArrayList<String>(names.subList(3, 6));

        testExistingSystem("Test Basic Config DTP", plant, spec, AgentChicanery.generateAgentsDTP(), new BatchSetup(){
            public void setUpSystem() {
                SystemGeneration.generateSystemSetDTP();

        }
        }, TEST_BASIC);
    }

    private void testBasicConfigHISCHigh()  {

        List<List<String>> names = SystemGeneration.generateSystemSetHISCHighLevel();

        testExistingSystem("Test Basic Config HISC High Level", names.get(0), names.get(1), AgentChicanery.generateAgentsHISC(0), new BatchSetup() {

            public void setUpSystem() {
                SystemGeneration.generateSystemSetHISCHighLevel();
            }

        }, TEST_BASIC);

    }

    private void testBasicConfigHISCLow()  {

        List<List<String>> names = SystemGeneration.generateSystemSetHISCLowLevel();

        testExistingSystem("Test Basic Config HISC Low Level", names.get(0), names.get(1), AgentChicanery.generateAgentsHISC(1), new BatchSetup() {

            public void setUpSystem() {
                SystemGeneration.generateSystemSetHISCLowLevel();
            }

        }, TEST_BASIC);

    }

    private void testIncrementalConfigDTP()  {

        List<String> names = SystemGeneration.generateSystemSetDTP();

        List<String> plant = new ArrayList<String>(names.subList(0, 3));
        List<String> spec = new ArrayList<String>(names.subList(3, 6));

        testExistingSystem("Test Incremental Config DTP", plant, spec, AgentChicanery.generateAgentsDTP(), new BatchSetup(){
            public void setUpSystem() {
                SystemGeneration.generateSystemSetDTP();

        }
        }, TEST_SB_INC);

    }

    private void testIncrementalConfigHISC()  {

        List<List<String>> names = SystemGeneration.generateSystemSetHISC();

        testExistingSystem("Test Incremental Config HISC", names.get(0), names.get(1), AgentChicanery.generateAgentsHISC(3), new BatchSetup() {

            public void setUpSystem() {
                SystemGeneration.generateSystemSetHISC();
            }

        }, TEST_INC);

    }

    private void testIncrementalConfigHISCHigh()  {

        List<List<String>> names = SystemGeneration.generateSystemSetHISCHighLevel();

        testExistingSystem("Test Incremental Config HISC High Level", names.get(0), names.get(1), AgentChicanery.generateAgentsHISC(0), new BatchSetup() {

            public void setUpSystem() {
                SystemGeneration.generateSystemSetHISCHighLevel();
            }

        }, TEST_SB_INC);

    }

    private void testIncrementalConfigHISCLow()  {

        List<List<String>> names = SystemGeneration.generateSystemSetHISCLowLevel();

        testExistingSystem("Test Incremental Config HISC Low Level", names.get(0), names.get(1), AgentChicanery.generateAgentsHISC(1), new BatchSetup() {

            public void setUpSystem() {
                SystemGeneration.generateSystemSetHISCLowLevel();
            }

        }, TEST_SB_INC);

    }

    private void testExistingSystem(String testBatch, List<String> plants, List<String> specs, List<Map<String, List<Boolean>>> agents, BatchSetup systemStart, int testChoice)  {
        int counter = 0;

        while(counter <= NUMBER_EXISTING_TEST_RUNS) {
            System.out.println(testBatch + ": " + counter);

            String testName = TEST_NAME + "_" + counter;

            File f;
            f = new File(defaultWritePath, testName);
            f.mkdir();

            writePath = defaultWritePath + File.separator + testName;

            int finished = checkTestNumberVerifiedComplete(writePath);

            while(finished < NUMBER_REPEAT_TEST) {
                systemStart.setUpSystem();

                List<String> completeTests = checkTestsCompleted(writePath);
                List<String> memoryError = checkTestTypesVerifiedMemoryError(writePath);


                switch(testChoice) {
                    case TEST_ALL:
                        autoTestSystemFull(testName, plants, specs, agents);
                        break;
                    case TEST_BASIC:
                        autoTestSystemCoobsSB(testName, plants, specs, agents, finished, completeTests, memoryError);
                        break;
                    case TEST_INC:
                        autoTestSystemIncr(testName, plants, specs, agents, finished, completeTests, memoryError);
                        break;
                    case TEST_HEUR:
                        autoTestHeuristics(testName, plants, specs, agents, completeTests, memoryError);
                        break;
                    case TEST_SB_INC:
                        autoTestSystemSBIncr(testName, plants, specs, agents, finished, completeTests, memoryError);
                        break;
                    default:
                        break;
                }

                confirmComplete();
                resetModel();

                finished = checkTestNumberVerifiedComplete(writePath);
                System.out.println(finished);
            }

            counter++;
        }
    }

    //-- Random Tests  ----------------------------------------

      //- Basic ---------------------------

    private void testBasicConfigOne(int count) throws IOException{
        Incremental.assignIncrementalOptions(0, 1, 1);

        int numPlants = 2;
        int numSpecs = 2;

        int numStates = 4;
        int numStateVar = 2;
        int numEvents = 3;
        int numEventsVar = 2;

        double eventShareRate = .4;

        int numControllers = 2;
        int numControllersVar = 0;
        double controllerObserveRate = .4;
        double controllerControlRate = .3;

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

        runBasicTest(info, count, "Test Basic Config One");
    }

    private void testBasicConfigTwo(int count) throws IOException {
        Incremental.assignIncrementalOptions(0, 1, 1);

        int numPlants = 3;
        int numSpecs = 3;

        int numStates = 4;
        int numStateVar = 2;
        int numEvents = 3;
        int numEventsVar = 2;

        double eventShareRate = .4;

        int numControllers = 2;
        int numControllersVar = 0;
        double controllerObserveRate = .4;
        double controllerControlRate = .3;

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

        runBasicTest(info, count, "Test Basic Config Two");
    }

    private void testBasicConfigThree(int count) throws IOException {
        Incremental.assignIncrementalOptions(0, 1, 1);

        int numPlants = 1;
        int numSpecs = 1;

        int numStates = 20;
        int numStateVar = 4;
        int numEvents = 7;
        int numEventsVar = 2;

        double eventShareRate = .4;

        int numControllers = 3;
        int numControllersVar = 0;
        double controllerObserveRate = .4;
        double controllerControlRate = .3;

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

        runBasicTest(info, count, "Test Basic Config Three");
    }

    private void testBasicConfigFour(int count) throws IOException {
        Incremental.assignIncrementalOptions(0, 1, 1);

        int numPlants = 1;
        int numSpecs = 1;

        int numStates = 50;
        int numStateVar = 15;
        int numEvents = 8;
        int numEventsVar = 2;

        double eventShareRate = .4;

        int numControllers = 5;
        int numControllersVar = 0;
        double controllerObserveRate = .4;
        double controllerControlRate = .3;

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

        runBasicTest(info, count, "Test Basic Config Four");
    }

    private void runBasicTest(RandomGenStats info, int count, String testBatch) throws IOException {
        int trueResults = generateInterpretDataSimple(defaultWritePath, ANALYSIS_COOBS).getNumberTrueResults();

        int counter = 0;

        while(counter < count || trueResults < MINIMUM_TRUE_RESULTS) {
            System.out.println(testBatch + ": " + counter + ", " + trueResults + "/" + MINIMUM_TRUE_RESULTS);
            if(autoTestRandomSystem(counter + 1, info, TEST_BASIC))
                trueResults++;
            resetModel();
            counter++;
        }
    }

      //- Incremental ---------------------

    private void testIncConfigOne(int count) throws IOException {
        Incremental.assignIncrementalOptions(0, 1, 1);

        int numPlants = 2;
        int numSpecs = 2;

        int numStates = 4;
        int numStateVar = 2;
        int numEvents = 3;
        int numEventsVar = 2;

        double eventShareRate = .4;

        int numControllers = 2;
        int numControllersVar = 0;
        double controllerObserveRate = .4;
        double controllerControlRate = .3;

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

        runIncTest(info, count, "Test Inc Config One", true);
    }

    private void testIncConfigTwo(int count) throws IOException {
        Incremental.assignIncrementalOptions(0, 1, 1);

        int numPlants = 3;
        int numSpecs = 3;

        int numStates = 4;
        int numStateVar = 2;
        int numEvents = 3;
        int numEventsVar = 2;

        double eventShareRate = .4;

        int numControllers = 2;
        int numControllersVar = 0;
        double controllerObserveRate = .4;
        double controllerControlRate = .3;

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

        runIncTest(info, count, "Test Inc Config Two", true);
    }

    private void testIncConfigThree(int count) throws IOException {
        Incremental.assignIncrementalOptions(0, 1, 1);

        int numPlants = 5;
        int numSpecs = 5;

        int numStates = 4;
        int numStateVar = 2;
        int numEvents = 3;
        int numEventsVar = 2;

        double eventShareRate = .4;

        int numControllers = 3;
        int numControllersVar = 0;
        double controllerObserveRate = .4;
        double controllerControlRate = .3;

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

        runIncTest(info, count, "Test Inc Config Three", true);
    }

    private void runIncTest(RandomGenStats info, int count, String testBatch, boolean sb) throws IOException {
        int trueResults = generateInterpretDataSimple(defaultWritePath, ANALYSIS_COOBS).getNumberTrueResults();

        int counter = 0;

        while(counter < count || trueResults < MINIMUM_TRUE_RESULTS) {
            System.out.println(testBatch + ": " + counter);
            if(autoTestRandomSystem(counter + 1, info, sb ? TEST_SB_INC : TEST_INC))
                trueResults++;
            resetModel();
            counter++;
        }
    }

      //- Heuristics ----------------------

    private void testHeuristicConfigOne(int count) throws IOException {
        Incremental.assignIncrementalOptions(0, 1, 1);

        int numPlants = 2;
        int numSpecs = 2;

        int numStates = 4;
        int numStateVar = 2;
        int numEvents = 3;
        int numEventsVar = 2;

        double eventShareRate = .4;

        int numControllers = 2;
        int numControllersVar = 0;
        double controllerObserveRate = .4;
        double controllerControlRate = .3;

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

        int counter = 0;

        while(counter < count) {
            System.out.println("Test Heuristic Config One: " + counter);
            autoTestRandomSystem(counter + 1, info, TEST_HEUR);
            resetModel();
            counter++;

        }
    }

    private void testHeuristicConfigTwo(int count) throws IOException {
        Incremental.assignIncrementalOptions(0, 1, 1);

        int numPlants = 5;
        int numSpecs = 5;

        int numStates = 4;
        int numStateVar = 2;
        int numEvents = 3;
        int numEventsVar = 2;

        double eventShareRate = .4;

        int numControllers = 4;
        int numControllersVar = 0;
        double controllerObserveRate = .4;
        double controllerControlRate = .3;

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

        int counter = 0;

        while(counter < count) {
            System.out.println("Test Heuristic Config Two: " + counter);
            autoTestRandomSystem(counter + 1, info, TEST_HEUR);
            resetModel();
            counter++;

        }
    }

//---  System Testing   -----------------------------------------------------------------------

    public void markTestUnfinished() {
        if(!heuristics) {
            if(checkTestDeclaredTypeMemoryError(writePath)) {
                printOut(VERIFY_MEMORY_ERROR + analysisSubtype);
            }
            else{
                printOut(DECLARE_MEMORY_ERROR + analysisSubtype);
            }

            if(model != null && model.getLastProcessData() != null) {
                printOut(model.getLastProcessData().produceOutputLog());
            }
        }
        else {
            printOut(VERIFY_MEMORY_ERROR + analysisSubtype + retrieveHeuristicsPostscript());
        }
    }

    private boolean autoTestRandomSystem(int count, RandomGenStats info, int testChoice) throws IOException {
        String testName = TEST_NAME + "_" +  count;
        File f;
        f = new File(defaultWritePath, testName);

        writePath = defaultWritePath + File.separator + testName;

        boolean inMem = false;

        if(!f.exists()) {
            autoGenerateNewRandomSystem(count, info);
            inMem = true;
        }

        Boolean out = null;



        int finished = checkTestNumberVerifiedComplete(writePath);

        while(finished < (heuristics ? 1 : NUMBER_REPEAT_TEST)) {

            List<String> completeTests = checkTestsCompleted(writePath);
            List<String> memoryError = checkTestTypesVerifiedMemoryError(writePath);

            if(!inMem)
                readInOldSystem(testName);

            List<String> plants = getPlants(testName);
            List<String> specs = getSpecs(testName);
            List<Map<String, List<Boolean>>> agents = getAgents(testName);


            switch(testChoice) {
                case TEST_ALL:
                    out = autoTestSystemFull(testName, plants, specs, agents);
                    break;
                case TEST_BASIC:
                    out = autoTestSystemCoobsSB(testName, plants, specs, agents, finished, completeTests, memoryError);
                    break;
                case TEST_INC:
                    out = autoTestSystemIncr(testName, plants, specs, agents, finished, completeTests, memoryError);
                    break;
                case TEST_HEUR:
                    out = autoTestHeuristics(testName, plants, specs, agents, completeTests, memoryError);
                    break;
                case TEST_SB_INC:
                    out = autoTestSystemSBIncr(testName, plants, specs, agents, finished, completeTests, memoryError);
                    break;
                default:
                    break;
            }
            confirmComplete();
            finished = checkTestNumberVerifiedComplete(writePath);
        }

        return out == null ? false : out;
    }

    private void readInOldSystem(String prefixNom) throws FileNotFoundException {
        String path = writePath;
        List<String> plants = new ArrayList<String>();
        int counter = 0;
        String hold = pullSourceData(path + File.separator + prefixNom + "_p_" + counter++ + ".txt");
        while(hold != null) {
            plants.add(model.readInFSM(hold));
            hold = pullSourceData(path + File.separator + prefixNom + "_p_" + counter++ + ".txt");
        }

        List<String> specs = new ArrayList<String>();
        counter = 0;
        hold = pullSourceData(path + File.separator + prefixNom + "_s_" + counter++ + ".txt");
        while(hold != null) {
            specs.add(model.readInFSM(hold));
            hold = pullSourceData(path + File.separator + prefixNom + "_s_" + counter++ + ".txt");
        }
    }

    private void autoGenerateNewRandomSystem(int count, RandomGenStats info) throws IOException {
        String testName = TEST_NAME + "_" +  count;
        File f;
        f = new File(defaultWritePath, testName);

        writePath = defaultWritePath + File.separator + testName;

        f.mkdir();

        //System.out.println("This test: " + testName);
        printOut(testName + ", " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n---------------------------------------------\n");


        printOut("Test Configuration: Full Suite");
        printOut("Randomizer Parameters: ");
        printOut(" " + info.toString());
        printOut(" " + info.shortToString() + StringUtils.LF);
        printOut("---------------------------------------------\n");

        List<String> events = RandomGeneration.generateRandomSystemSet(testName, model, info);
        List<String> names = RandomGeneration.getComponentNames(testName, info.getNumPlants(), info.getNumSpecs());

        List<Map<String, List<Boolean>>> agents = RandomGeneration.generateRandomAgents(events, info);

        f = new File(writePath, testName + "_agents.txt");
        try (RandomAccessFile raf = RandomAccessFileMode.READ_WRITE.create(f)) {
            raf.writeBytes(model.exportAgents(testName + "_agents", agents, eventAtt));
        }

        printOut("Agent Information: \n" + agents.toString().replace("},", "},\n").replaceAll("[\\[\\]]", " "));
        printOut("\n---------------------------------------------\n");

        for(String s : names) {
            //makeImageDisplay(s, s);
            f = new File(writePath, s + ".txt");
            try (RandomAccessFile raf = RandomAccessFileMode.READ_WRITE.create(f)) {
                raf.writeBytes(model.exportFSM(s));
            }
            Files.move(new File(FormatConversion.createImgFromFSM(model.generateFSMDot(s), s)).toPath(), new File(writePath, s + ".png").toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private boolean autoTestSystemFull(String prefixNom, List<String> plantNames, List<String> specNames, List<Map<String, List<Boolean>>> agents) {
        printCoobsLabel(prefixNom, false);
        boolean coobs = checkCoobservable(plantNames, specNames, agents, false);

        printIncrementalLabel(prefixNom, false);
        boolean icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);

        printSBCoobsLabel(prefixNom);
        boolean sbCoobs = checkSBCoobservable(plantNames, specNames, agents);
        printIncrementalSBLabel(prefixNom);
        boolean icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);

        if(coobs && !sbCoobs) {
            printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
        }

        boolean error = false;

        if(coobs != icCoobs) {
            printOut("~~~\nError!!! : Incremental Algo. did not return same as Coobs. Algo.\n~~~");
            error = true;
        }
        if(sbCoobs != icSbCoobs) {
            printOut("~~~\nError!!! : Incremental SB Algo. did not return same as SB Algo.\n~~~");
            error = true;
        }

        if(sbCoobs && !coobs) {
            printOut("~~~\nError!!! : State Based Algo. claimed True while Coobs. Algo. claimed False\n~~~");
            error = true;
        }

        if(error) {
            throw new RuntimeException("Logic Conflict in Data Output");
        }
        resetModel();
        return coobs;
    }

    private Boolean autoTestSystemCoobsSB(String prefixNom, List<String> plantNames, List<String> specNames, List<Map<String, List<Boolean>>> agents, int finishCount, List<String> completedTests, List<String> memoryError) {
        Boolean coobs = null;
        if(contains(completedTests, ANALYSIS_COOBS) == finishCount && !memoryError.contains(ANALYSIS_COOBS)) {
            printCoobsLabel(prefixNom, false);
            coobs = checkCoobservable(plantNames, specNames, agents, false);
        }

        Boolean sbCoobs = null;
        if(contains(completedTests, ANALYSIS_SB) == finishCount && !memoryError.contains(ANALYSIS_SB)) {
            printSBCoobsLabel(prefixNom);
            sbCoobs = checkSBCoobservable(plantNames, specNames, agents);
        }

        if(memoryError.isEmpty() && coobs != null && sbCoobs != null && (coobs && !sbCoobs)) {
            printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
        }

        boolean error = false;

        if(memoryError.isEmpty() && coobs != null && sbCoobs != null && (sbCoobs && !coobs)) {
            printOut("~~~\nError!!! : State Based Algo. claimed True while Coobs. Algo. claimed False\n~~~");
            error = true;
        }
        if(error) {
            throw new RuntimeException("Logic Conflict in Data Output: " + prefixNom);
        }
        resetModel();
        return coobs;
    }

    private boolean autoTestSystemIncr(String prefixNom, List<String> plantNames, List<String> specNames, List<Map<String, List<Boolean>>> agents, int finishCount, List<String> completedTests, List<String> memoryError) {
        Boolean icCoobs = null;
        if(contains(completedTests, ANALYSIS_INC_COOBS) == finishCount && !memoryError.contains(ANALYSIS_INC_COOBS)) {
            printIncrementalLabel(prefixNom, false);
            icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);
        }

        Boolean icSbCoobs = null;
        if(contains(completedTests, ANALYSIS_INC_SB) == finishCount && !memoryError.contains(ANALYSIS_INC_SB)) {
            printIncrementalSBLabel(prefixNom);
            icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);
        }

        if((icCoobs != null && icSbCoobs != null) && icCoobs && !icSbCoobs) {
            printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
        }

        boolean error = false;

        if((icCoobs != null && icSbCoobs != null) && !icCoobs && icSbCoobs) {
            printOut("~~~\nError!!! : Incremental SB Algo. did not return same as Incremental Algo.\n~~~");
            error = true;
        }

        if(error) {
            throw new RuntimeException("Logic Conflict in Data Output: " + prefixNom);
        }
        resetModel();
        return icCoobs == null ? (icSbCoobs == null ? false : icSbCoobs) : icCoobs;
    }

    private boolean autoTestSystemSBIncr(String prefixNom, List<String> plantNames, List<String> specNames, List<Map<String, List<Boolean>>> agents, int finishCount, List<String> completedTests, List<String> memoryError) {
        Boolean icCoobs = null;
        if(contains(completedTests, ANALYSIS_INC_COOBS) == finishCount && !memoryError.contains(ANALYSIS_INC_COOBS)) {
            printIncrementalLabel(prefixNom, false);
            icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);
        }

        Boolean sbCoobs = null;
        if(contains(completedTests, ANALYSIS_SB) == finishCount && !memoryError.contains(ANALYSIS_SB)) {
            printSBCoobsLabel(prefixNom);
            sbCoobs = checkSBCoobservable(plantNames, specNames, agents);
        }

        Boolean icSbCoobs = null;
        if(contains(completedTests, ANALYSIS_INC_SB) == finishCount && !memoryError.contains(ANALYSIS_INC_SB)) {
            printIncrementalSBLabel(prefixNom);
            icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);
        }

        if((icCoobs != null && sbCoobs != null) && icCoobs && !sbCoobs) {
            printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
        }

        boolean error = false;

        if((sbCoobs != null && icSbCoobs != null) && sbCoobs != icSbCoobs) {
            printOut("~~~\nError!!! : Incremental SB Algo. did not return same as SB Algo.\n~~~");
            error = true;
        }
        if((sbCoobs != null && icCoobs != null) && (sbCoobs && !icCoobs)) {
            printOut("~~~\nError!!! : State Based Algo. claimed True while Coobs. Algo. claimed False\n~~~");
            error = true;
        }
        if(error) {
            throw new RuntimeException("Logic Conflict in Data Output: " + prefixNom);
        }
        resetModel();
        return icCoobs == null ? (sbCoobs == null ? (icSbCoobs == null ? false : icSbCoobs) : sbCoobs) : icCoobs;
    }

    private Boolean autoTestHeuristics(String prefixNom, List<String> plantNames, List<String> specNames, List<Map<String, List<Boolean>>> agents, List<String> completedTests, List<String> memoryError) {
        Boolean expectedCoob = null;
        Boolean expectedSB = null;

        for(int i = 0; i < Incremental.NUM_A_HEURISTICS; i++) {
            for(int j = 0; j < Incremental.NUM_B_HEURISTICS; j++) {
                //for(int k = 0; k < Incremental.NUM_C_HEURISTICS; k++) {
                    Incremental.assignIncrementalOptions(i, j, 1);
                    String post = generateHeuristicsPostscript(i, j, 1);
                    Boolean icCoobs = null;
                    if(!completedTests.contains(ANALYSIS_INC_COOBS + post) && !memoryError.contains(ANALYSIS_INC_COOBS + post)) {
                        printIncrementalLabel(prefixNom + post, false);
                        icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);
                    }

                    if(expectedCoob == null) {
                        expectedCoob = icCoobs;
                    }

                    Boolean icSbCoobs = null;
                    if(ArrayUtils.contains(Incremental.INCREMENTAL_B_NO_REJECT, j) && (!completedTests.contains(ANALYSIS_INC_SB + post) && !memoryError.contains(ANALYSIS_INC_SB + post))) {
                        printIncrementalSBLabel(prefixNom + post);
                        icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);
                    }

                    if(expectedSB == null) {
                        expectedSB = icSbCoobs;
                    }

                    if((expectedCoob != null && icCoobs != null && expectedCoob != icCoobs) || (expectedSB != null && icSbCoobs != null && expectedSB != icSbCoobs)) {
                        throw new RuntimeException("Change in Heuristics caused difference result: " + post);
                    }
                //}
            }
        }
        resetModel();
        if((expectedCoob != null && expectedSB != null) && (expectedCoob && !expectedSB)) {
            printOut("Of interest, incremental co-observability returned True and incremental SB returned False overall");
        }
        else if((expectedCoob != null && expectedSB != null) && (!expectedCoob && expectedSB)) {
            throw new RuntimeException("Logic Error: Incremental co-observability found False while incremental SB returned True overall");
        }
        return expectedCoob;
    }

    //-- Coobservability Testing  -----------------------------

    private boolean checkCoobservable(List<String> plants, List<String> specs, List<Map<String, List<Boolean>>> agents, boolean inf) {
        long t = System.currentTimeMillis();
        long hold = getCurrentMemoryUsage();
        assignAnalysisSubtype(TYPE_COOBS);
        Boolean result = inf ? model.isInferenceCoobservableUStruct(plants, specs, eventAtt, agents) : model.isCoobservableUStruct(plants, specs, eventAtt, agents);
        handleOutData(t, hold);
        printOut("\t\t\t\t" + (inf ? "Inferencing " : StringUtils.EMPTY ) + "Coobservable: " + result);
        
        return result;
    }

    private void printCoobsLabel(String system, boolean type) {
        printOut(system + StringUtils.SPACE + (type ? "Inference Coobservability:" : "Coobservability:") + " \t");
    }

    //-- SB Coobservability Testing  --------------------------

    private boolean checkSBCoobservable(List<String> plants, List<String> specs, List<Map<String, List<Boolean>>> agents) {
        long t = System.currentTimeMillis();
        long hold = getCurrentMemoryUsage();
        assignAnalysisSubtype(TYPE_SB);
        boolean result = model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents);
        handleOutData(t, hold);
        printOut("\t\t\t\tSB-Coobservable: " + result);
        
        return result;
    }

    private void printSBCoobsLabel(String system) {
        printOut(system + " SB Coobservability: \t");
    }

    //-- Incremental Testing  ---------------------------------

    private boolean checkIncrementalCoobservable(List<String> plants, List<String> specs, List<Map<String, List<Boolean>>> agents, boolean inf) {
        long t = System.currentTimeMillis();
        long hold = getCurrentMemoryUsage();
        assignAnalysisSubtype(TYPE_INC_COOBS);
        boolean result = inf ? model.isIncrementalInferenceCoobservable(plants, specs, eventAtt, agents) : model.isIncrementalCoobservable(plants, specs, eventAtt, agents);
        handleOutData(t, hold);
        printOut("\t\t\t\tIncremental" + (inf ? " Inference" : StringUtils.EMPTY) + " Coobservable: " + result);
        
        return result;
    }

    private boolean checkIncrementalSBCoobservable(List<String> plants, List<String> specs, List<Map<String, List<Boolean>>> agents) {
        long t = System.currentTimeMillis();
        long hold = getCurrentMemoryUsage();
        assignAnalysisSubtype(TYPE_INC_SB);
        boolean result = model.isIncrementalSBCoobservable(plants, specs, eventAtt, agents);
        handleOutData(t, hold);
        printOut("\t\t\t\tIncremental SB Coobservable: " + result);
        
        return result;
    }

    private void printIncrementalLabel(String system, boolean inf) {
        printOut(system + " Incremental" + (inf ? " Inference" : StringUtils.EMPTY) + " Coobservability: \t");
    }

    private void printIncrementalSBLabel(String system) {
        printOut(system + " Incremental SB Coobservability: \t");
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public boolean getFinished() {
        return finished;
    }

//---  Support Methods   ----------------------------------------------------------------------

    private int contains(List<String> list, String find) {
        int out = 0;
        for(String s : list) {
            out += (s.equals(find) ? 1 : 0);
        }
        return out;
    }

    private void assignAnalysisSubtype(int in) {
        switch(in) {
            case TYPE_COOBS:
                analysisSubtype = ANALYSIS_COOBS;
                break;
            case TYPE_SB:
                analysisSubtype = ANALYSIS_SB;
                break;
            case TYPE_INC_COOBS:
                analysisSubtype = ANALYSIS_INC_COOBS;
                break;
            case TYPE_INC_SB:
                analysisSubtype = ANALYSIS_INC_SB;
                break;
            default:
                analysisSubtype = StringUtils.EMPTY;
        }
    }

    private String retrieveHeuristicsPostscript() {
        int[] hold = Incremental.retrieveIncrementalOptions();
        return "_" + hold[0] + "_" + hold[1] + "_" + hold[2];
    }

    private String generateHeuristicsPostscript(int a, int b, int c) {
        return "_" + a + "_" + b + "_" + c;
    }

    //-- Batch Progress Analysis  -----------------------------

    private int checkTestNumberVerifiedComplete(String path) {
        return checkForTermLinePositions(path + File.separator + RESULTS_FILE, VERIFY_COMPLETE_TEST).size();
    }

    private boolean checkTestDeclaredTypeMemoryError(String path) {
        return checkForTerm(path + File.separator + RESULTS_FILE, DECLARE_MEMORY_ERROR + analysisSubtype);
    }

    private List<String> checkTestTypesVerifiedMemoryError(String path){
        List<String> out = new ArrayList<String>();
        if(!heuristics) {
            for(String s : ANALYSIS_TYPES) {
                if(checkForTerm(path + File.separator + RESULTS_FILE, VERIFY_MEMORY_ERROR + s)) {
                    out.add(s);
                }
            }
        }
        else {
            for(int i = 0; i < Incremental.NUM_A_HEURISTICS; i++) {
                for(int j = 0; j < Incremental.NUM_B_HEURISTICS; j++) {
                    for(int k = 0; k < Incremental.NUM_C_HEURISTICS; k++) {
                        String post = generateHeuristicsPostscript(i, j, k);
                        if(checkForTerm(path + File.separator + RESULTS_FILE, VERIFY_MEMORY_ERROR + ANALYSIS_INC_SB + post)) {
                            out.add(ANALYSIS_INC_SB + post);
                        }
                        if(checkForTerm(path + File.separator + RESULTS_FILE, VERIFY_MEMORY_ERROR + ANALYSIS_INC_COOBS + post)) {
                            out.add(ANALYSIS_INC_COOBS + post);
                        }
                    }
                }
            }
        }
        return out;
    }

    private List<String> checkTestsCompleted(String path){
        List<String> out = new ArrayList<String>();
        if(!heuristics) {
            for(String s : ANALYSIS_TYPES) {
                if(checkForTerm(path + File.separator + RESULTS_FILE, VERIFY_COMPLETE_CHECKPOINT + s)) {
                    for(int i = 0; i < checkForTermLinePositions(path + File.separator + RESULTS_FILE, VERIFY_COMPLETE_CHECKPOINT + s).size(); i++)
                        out.add(s);
                }
            }
        }
        else {
            for(int i = 0; i < Incremental.NUM_A_HEURISTICS; i++) {
                for(int j = 0; j < Incremental.NUM_B_HEURISTICS; j++) {
                    for(int k = 0; k < Incremental.NUM_C_HEURISTICS; k++) {
                        String post = generateHeuristicsPostscript(i, j, k);
                        if(checkForTerm(path + File.separator + RESULTS_FILE, VERIFY_COMPLETE_CHECKPOINT + ANALYSIS_INC_SB + post)) {
                            out.add(ANALYSIS_INC_SB + post);
                        }
                        if(checkForTerm(path + File.separator + RESULTS_FILE, VERIFY_COMPLETE_CHECKPOINT + ANALYSIS_INC_COOBS + post)) {
                            out.add(ANALYSIS_INC_COOBS + post);
                        }
                    }
                }
            }
        }
        return out;
    }

    private int getTotalNumberTests(String path) {
        int validTests = 0;
        int counter = 0;

        int size = new File(path).list().length;
        while(counter <= size + 5) {
            File g = new File(path, TEST_NAME + "_" + counter++ + File.separator + RESULTS_FILE);
            if(g.exists()) {
                validTests++;
            }
        }
        return validTests;
    }

    private boolean testsCompleted(String testBatch, String type, int maxSize) {
        generateRawDataFileSimple(defaultWritePath, type);
        InterpretData d = generateInterpretDataSimple(defaultWritePath, type);
        int totalTests = d.getTotalNumberTests();
        int trueResults = d.getNumberTrueResults();
        if(!(trueResults >= MINIMUM_TRUE_RESULTS)) {
            System.out.println(testBatch + " in progress at: " + trueResults + " true outcomes of " + totalTests + " total tests.");
            return false;
        }
        if(checkTestNumberVerifiedComplete(defaultWritePath + File.separator + TEST_NAME + "_" + maxSize) != NUMBER_REPEAT_TEST) {
            System.out.println(testBatch + " in progress at: " + trueResults + " true outcomes of " + totalTests + " total tests.");
            return false;
        }
        System.out.println(testBatch + " already complete at: " + totalTests + " tests");
        System.out.println(testBatch + " satisfies minimum true results: " + trueResults);
        return true;
    }

    private boolean testsCompletedNonRandom(String testBatch, String type, int maxSize) {
        InterpretData d = generateInterpretDataSimple(defaultWritePath, type);
        int totalTests = d.getTotalNumberTests();
        if(checkTestNumberVerifiedComplete(defaultWritePath + File.separator + TEST_NAME + "_" + maxSize) != NUMBER_REPEAT_TEST) {
            System.out.println(checkTestNumberVerifiedComplete(defaultWritePath + File.separator + TEST_NAME + "_" + maxSize));
            System.out.println(defaultWritePath + File.separator + TEST_NAME + "_" + maxSize);
            return false;
        }
        System.out.println(testBatch + " already complete at: " + totalTests + " tests");
        return true;
    }

    //-- Data Output Gathering  -------------------------------

    private void handleOutData(long t, long hold) {
        printOut(VERIFY_COMPLETE_CHECKPOINT + analysisSubtype + (heuristics ? retrieveHeuristicsPostscript() : StringUtils.EMPTY));
        printOut(model.getLastProcessData().produceOutputLog());
        long res = (System.currentTimeMillis() - t);
        printTimeTook(res);
        double val = inMB(getCurrentMemoryUsage() - hold);
        val = val < 0 ? 0 : val;
        printMemoryUsage(val);
        List<Double> data = model.getLastProcessData().getStoredData();
        List<String> use = model.getLastProcessData().getOutputGuide();

        use.add(0, "Total Time (ms)");
        use.add(1, "Overall Memory Usage (Mb)");

        printEquivalentResults(use, res, val, data);
    }

    private void printTimeTook(long t) {
        printOut("\t\t\t\tTook " + t + " ms");
    }

    private void printMemoryUsage(double reduction) {
        printOut("\t\t\t\tUsing " + threeSig(reduction) + " Mb");
        
    }

    private long getCurrentMemoryUsage() {
        Runtime r = Runtime.getRuntime();
        return ((r.totalMemory() - r.freeMemory()));
    }

    private double inMB(long in) {
        return (double)in / 1000000;
    }

    private double threeSig(double in) {
        String use = (in < 0 ? 0 : in)+"0000";
        int posit = use.indexOf('.') + 4;
        try {
            return Double.parseDouble(use.substring(0, posit));
        }
        catch(NumberFormatException e) {
            System.out.println(in);
            return 0d;
        }
    }

    private void confirmComplete() {
        printOut(StringUtils.LF + VERIFY_COMPLETE_TEST + StringUtils.LF);
        clock.resetClock();
    }

    //-- File Output  -----------------------------------------

    private void printOut(String text) {
        if(writePath != null) {
            File f = new File(writePath, RESULTS_FILE);
            try (RandomAccessFile raf = RandomAccessFileMode.READ_WRITE.create(f)) {
                raf.seek(raf.length());
                raf.writeBytes(text + StringUtils.LF);
            }
            catch(IOException e) {
                logger.catching(e);
            }
        }
    }

    private void printEquivalentResults(List<String> guide, long time, double overallMem, List<Double> vals) {
        if(writePath != null) {
            File f = new File(writePath, ANALYSIS_FILE + analysisSubtype + TEXT_EXTENSION);
            try (RandomAccessFile raf = RandomAccessFileMode.READ_WRITE.create(f)) {
                raf.seek(raf.length());
                if(raf.length() == 0) {
                    for(String s : guide)
                        raf.writeBytes(s + ", ");
                    raf.writeBytes(StringUtils.LF);
                }
                raf.writeBytes(time + ", \t" + threeSig(overallMem) + ", \t");
                for(int i = 0; i < vals.size(); i++){
                    Double d = vals.get(i);
                    if(d != null)
                        raf.writeBytes(threeSig(d) + (i + 1 < vals.size() ? ", \t" : StringUtils.EMPTY));
                    else {
                        raf.writeBytes("\n, , \t\t\t");
                    }
                }
                raf.writeBytes(StringUtils.LF);
            }
            catch(IOException e) {
                logger.catching(e);
            }
        }
    }

    //-- Data Input  ------------------------------------------

    private String pullSourceData(String path) throws FileNotFoundException {
        File f = new File(path);
        if(f.exists()) {
            StringBuilder sb = new StringBuilder();
            try (Scanner sc = new Scanner(f)) {
                while(sc.hasNextLine()) {
                    sb.append(sc.nextLine() + StringUtils.LF);
                }
            }
            return sb.toString();
        }
        return null;
    }

    private boolean checkForTerm(String path, String phrase) {
        File g = new File(path);
        if(!g.exists()) {
            return false;
        }
        try (RandomAccessFile raf = RandomAccessFileMode.READ_ONLY.create(g)) {
            String line = raf.readLine();
            while(line != null && !line.equals(phrase)) {
                line = raf.readLine();
            }
            return line != null && line.equals(phrase);
        }
        catch (IOException e) {
            logger.catching(e);
        }
        return false;
    }

    private List<Integer> checkForTermLinePositions(String path, String phrase){
        List<Integer> out = new ArrayList<Integer>();
        File g = new File(path);
        if(!g.exists()) {
            return out;
        }
        try (RandomAccessFile raf = RandomAccessFileMode.READ_ONLY.create(g)) {
            String line = raf.readLine();
            int counter = 1;
            while(line != null) {
                if(line.equals(phrase)) {
                    out.add(counter);
                }
                counter++;
                line = raf.readLine();
            }
            return out;
        }
        catch (IOException e) {
            logger.catching(e);
        }
        return new ArrayList<Integer>();
    }

    private List<String> getPlants(String prefix) throws IOException {
        List<String> plants = new ArrayList<String>();
        int counter = 0;
        String hold = pullSourceData(writePath + File.separator + prefix + "_p_" + counter++ + ".txt");
        while(hold != null) {
            plants.add(model.readInFSM(hold));
            hold = pullSourceData(writePath + File.separator + prefix + "_p_" + counter++ + ".txt");
        }
        return plants;
    }

    private List<String> getSpecs(String prefix) throws IOException {
        List<String> plants = new ArrayList<String>();
        int counter = 0;
        String hold = pullSourceData(writePath + File.separator + prefix + "_s_" + counter++ + ".txt");
        while(hold != null) {
            plants.add(model.readInFSM(hold));
            hold = pullSourceData(writePath + File.separator + prefix + "_s_" + counter++ + ".txt");
        }
        return plants;
    }

    private List<Map<String, List<Boolean>>> getAgents(String prefix) throws IOException {
        String hold = pullSourceData(writePath + File.separator + prefix + "_agents.txt");
        return model.readInAgents(hold);
    }

    //-- Model Management  ------------------------------------

    private void resetModel() {
        model.flushFSMs();
        
    }

}
