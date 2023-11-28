import static org.junit.jupiter.api.Assertions.*;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.RandomAccessFileMode;
import org.junit.jupiter.api.*;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import help.AgentChicanery;
import help.EventSets;
import help.RandomGenStats;
import help.RandomGeneration;
import help.SystemGeneration;
import model.Manager;
import model.process.coobservability.Incremental;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;


public class TestFunctionality {

//---  Constants   ----------------------------------------------------------------------------

    private static final String RESULTS_FILE = "output.txt";

    private static final String ANALYSIS_FILE = "raw_num.txt";

//---  Instance Variables   -------------------------------------------------------------------

    private static Manager model;

    private static List<String> eventAtt = Arrays.asList(EventSets.EVENT_ATTR_LIST);

    private static String defaultWritePath;

    private static String writePath;

//---  Operations   ---------------------------------------------------------------------------

    @BeforeEach
    public void setup() {
        model = new Manager();
        SystemGeneration.assignManager(model);
    }

    public static void main(String[] args) throws IOException {
        FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_IMAGES);
        model = new Manager();

        File f = new File(FiniteStateMachine.ADDRESS_IMAGES);
        f = f.getParentFile();
        defaultWritePath = f.getAbsolutePath() + "/autogenerate";
        f = new File(defaultWritePath);
        FileUtils.forceMkdir(f);

        SystemGeneration.assignManager(model);

        //basicUStructCheck();
        //crushUStructCheck();
        //crushUStructCheck2();
        //crushUStructCheck3();
        //generateSystems();
        //runAllTests();

        //RandomGeneration.setupRandomFSMConditions(model, 1, 1, 1);
        //makeImageDisplay(RandomGeneration.generateRandomFSM("rand", model, 5, 3, 2, true), "Rand");

        //runAllCoobsTests();
        //runAllSBTests();

        //runAllTests();


        //runAllSBTests();
        //runAllIncrementalCoobsTests();

        //checkSystemDSBCoobservable();

        //runAllTests();

        /*
        SystemGeneration.generateSystemA("A");
        SystemGeneration.generateSystemB("B");
        List<String> use = new ArrayList<String>();
        use.add("A"); use.add("B");
        String nom = model.performParallelComposition(use);
        makeImageDisplay("A", "A");
        makeImageDisplay("B", "B");
        makeImageDisplay(nom, nom);
        */

        //autoTestHeuristicsIncremental(4, 4, 5, 2, 4, 2, .35, 2, 0, .4, .3);
        //autoTestNewRandomSystem(3, 3, 6, 2, 4, 2, .5, 2, 0, .4, .3);
        //while(true)
        //    autoTestHeuristicsIncremental(2, 2, 5, 2, 4, 2, .4, 2, 0, .4, .3);
        /*
        SystemGeneration.generateSystemExample1("Example M_L");
        SystemGeneration.generateSystemExample2("Example M_K");
        SystemGeneration.generateSystemExample3("Example M_L-M_K");
        SystemGeneration.generateSystemExample4("Example Observer M_L-M_K");
        makeImageDisplay("Example M_L", "Example M_L");
        makeImageDisplay("Example M_K", "Example M_K");
        makeImageDisplay("Example M_L-M_K", "Example M_L-M_K");
        makeImageDisplay("Example Observer M_L-M_K", "Example Observer M_L-M_K");
        */
        Incremental.assignIncrementalOptions(0, 1, 1);

        List<List<String>> names = SystemGeneration.generateSystemSetHISC();
        List<String> plant = names.get(0);
        List<String> spec = names.get(1);

        /*for(String s : spec) {
            makeImageDisplay(s, s);
        }*/

        Manager.assignEndAtFirstCounterexample(true);

        //printIncrementalLabel("System DTP", false);
        checkIncrementalCoobservable(plant, spec, AgentChicanery.generateAgentsHISC(3), false);
        System.out.println("Hey");
        //checkCoobservable(plant, spec, AgentChicanery.generateAgentsDTP(), false);
        //pullReserveDisplay();

        System.out.println("Done");

        /*
        String prefix = "C:\\Users\\SirBo\\Documents\\GitHub\\Finite-State-Machine-Model\\Finite State Machine Model\\sources\\";

        String hold = "observer_example_0";

        makeImage(prefix, hold);

        makeImage(prefix, "spec_example_0");
        makeImage(prefix, "spec_example_1");
        makeImage(prefix, "spec_example_2");
        makeImage(prefix, "plant_example_0");
        makeImage(prefix, "plant_example_1");
        makeImage(prefix, "plant_example_2");
        makeImage(prefix, "marked_example_1");
        makeImage(prefix, "observer_example_0");

        //basicUStructCheck();

        //generateSystems();

        //checkSystemUrvashiSBCoobservable();

        /*
        checkSystemLiuOneIncrementalCoobservable();
        model.flushFSMs();
        checkSystemLiuTwoIncrementalCoobservable();

        model.flushFSMs();

        checkSystemLiuOneCoobservable();
        model.flushFSMs();
        checkSystemLiuTwoCoobservable();
        */

        //checkSystemBCoobservable();
        //checkSystemBAltCoobservable();
        //checkSystemFinnCoobservable();
    }

    private static void makeSpecPrime(String in) {

        for(String s : model.getFSMStateList(in)) {
            for(String e : model.getFSMEventList(in)) {
                if(!model.transitionExists(in, s, e))
                    model.addTransition(in, s, e, "trash");
            }
        }

    }

    @Test
    public void confirmSpecPrimeWorks() {
        SystemGeneration.generateSystemSpecPrimeTestPlant("plant");
        SystemGeneration.generateSystemSpecPrimeTestSpec("spec");

        makeImageDisplay("plant", "plant");
        makeImageDisplay("spec", "spec");

        makeSpecPrime("spec");

        makeImageDisplay("spec", "spec");

        List<String> use = new ArrayList<String>();
        use.add("plant");
        use.add("spec");

        String nom = model.performParallelComposition(use);

        makeImageDisplay(nom, nom);
    }

//---  Automated Testing   --------------------------------------------------------------------

    @Nested
    @Disabled
    class AllTests {

        @DisplayName("Testing System A")
        @Test
        public void testSystemA() {
            checkSystemACoobservable(false);
            checkSystemACoobservable(true);
            checkSystemASBCoobservable();
        }

        @DisplayName("Testing System B")
        @Test
        public void testSystemB() {
            checkSystemBCoobservable(false);
            checkSystemBCoobservable(true);
            checkSystemBSBCoobservable();
        }

        @DisplayName("Testing System C")
        @Test
        public void testSystemC() {
            checkSystemCCoobservable(false);
            checkSystemCCoobservable(true);
            checkSystemCSBCoobservable();
        }

        @DisplayName("Testing System D")
        @Test
        public void testSystemD() {
            checkSystemDCoobservable(false);
            checkSystemDCoobservable(true);
            checkSystemDSBCoobservable();
        }

        @DisplayName("Testing System E")
        @Test
        public void testSystemE() {
            checkSystemECoobservable(false);
            checkSystemECoobservable(true);
            checkSystemESBCoobservable();
        }

        @DisplayName("Testing System Finn")
        @Test
        public void testSystemFinn() {
            checkSystemFinnCoobservable(false);
            checkSystemFinnCoobservable(true);
        }

        @DisplayName("Testing System Urvashi")
        @Test
        public void testSystemUrvashi(){
            checkSystemUrvashiSBCoobservable();
        }

        @DisplayName("Testing System Liu One")
        @Test
        public void testSystemLiuOne(){
            checkSystemLiuOneCoobservable(false);
            checkSystemLiuOneCoobservable(true);
            checkSystemLiuOneSBCoobservable();
            checkSystemLiuOneIncrementalCoobservable();
        }

        @DisplayName("Testing System Liu Two")
        @Test
        public void testSystemLiuTwo() {
            checkSystemLiuTwoCoobservable(false);
            checkSystemLiuTwoCoobservable(true);
            checkSystemLiuTwoSBCoobservable();
            checkSystemLiuTwoIncrementalCoobservable();
        }
    }

    @Test
    public void runAllCoobsTests() {
        checkSystemACoobservable(false);
        checkSystemBCoobservable(false);
        checkSystemCCoobservable(false);
        checkSystemDCoobservable(false);
        checkSystemECoobservable(false);
        checkSystemFinnCoobservable(false);
        checkSystemLiuOneCoobservable(false);
        checkSystemLiuTwoCoobservable(false);
    }

    @Test
    public void runAllInfCoobsTests(){
        checkSystemACoobservable(true);
        checkSystemBCoobservable(true);
        checkSystemCCoobservable(true);
        checkSystemDCoobservable(true);
        checkSystemECoobservable(true);
        checkSystemFinnCoobservable(true);
        checkSystemLiuOneCoobservable(true);
        checkSystemLiuTwoCoobservable(true);
    }

    @Test
    public void runAllSBTests() {
        checkSystemASBCoobservable();
        checkSystemBSBCoobservable();
        checkSystemCSBCoobservable();
        checkSystemDSBCoobservable();
        checkSystemESBCoobservable();
        checkSystemUrvashiSBCoobservable();
        checkSystemLiuOneSBCoobservable();
        checkSystemLiuTwoSBCoobservable();
    }

    @Test
    public void runAllIncrementalCoobsTests() {
        checkSystemLiuOneIncrementalCoobservable();
        checkSystemLiuTwoIncrementalCoobservable();
    }

    private static void generateSystems() {
        String SystemA = "Example 1";
        SystemGeneration.generateSystemA(SystemA);
        makeImageDisplay(SystemA, "Example 1");

        String SystemB = "Example 2";
        SystemGeneration.generateSystemB(SystemB);
        makeImageDisplay(SystemB, "Example 2");

        String SystemC = "Example 3";
        SystemGeneration.generateSystemC(SystemC);
        makeImageDisplay(SystemC, "Example 3");

        String SystemD = "Example 4";
        SystemGeneration.generateSystemD(SystemD);
        makeImageDisplay(SystemD, "Example 4");

        String SystemE = "Example 5";
        SystemGeneration.generateSystemE(SystemE);
        makeImageDisplay(SystemE, SystemE);
    }

    @Test
    public void basicUStructCheck() {
        String SystemA = "Example 1";
        SystemGeneration.generateSystemA(SystemA);
        // makeImageDisplay(SystemA, "Example 1");

        String ustruct = model.buildUStructure(SystemA, eventAtt, AgentChicanery.generateAgentsA());
        System.out.println(model.getLastProcessData().produceOutputLog());
        for(String s : model.getFSMStateList(ustruct)) {
            System.out.println(s);
        }
        for(String s : model.getFSMEventList(ustruct)) {
            System.out.println(s);
        }
        for(String s : model.getFSMTransitionList(ustruct)) {
            System.out.println(s);
        }
        // makeImageDisplay(ustruct, "Example 1 UStruct");
    }

    @Test
    public void crushUStructCheck() {
        String SystemA = "Example 1";
        SystemGeneration.generateSystemA(SystemA);
        makeImageDisplay(SystemA, "Example 1");

        List<String> ustruct = model.buildUStructureCrush(SystemA, eventAtt, AgentChicanery.generateAgentsA());
        for(String s : ustruct)
            makeImageDisplay(s, s);
    }

    @Test
    public void crushUStructCheck2() {
        String SystemB = "Example 2";
        SystemGeneration.generateSystemB(SystemB);
        makeImageDisplay(SystemB, "Example 2");

        List<String> ustruct = model.buildUStructureCrush(SystemB, eventAtt, AgentChicanery.generateAgentsB2());
        for(String s : ustruct) {
            makeImageDisplay(s, s);
            model.exportFSM(s);
        }
    }

    @Test
    public void crushUStructCheck3() {
        String SystemE = "Example 5";
        SystemGeneration.generateSystemE(SystemE);
        makeImageDisplay(SystemE, "Example 5");

        List<String> ustruct = model.buildUStructureCrush(SystemE, eventAtt, AgentChicanery.generateAgentsE());
        for(String s : ustruct) {
            /* Disabled due to engine timeout */
            // makeSVGImage(s, s);
            model.exportFSM(s);
        }
    }

    @Test
    public void crushUStructCheckFinn() {
        String SystemFinn = "Example Finn";
        SystemGeneration.generateSystemFinn(SystemFinn);
        makeImageDisplay(SystemFinn, SystemFinn);
        List<String> ustruct = model.buildUStructureCrush(SystemFinn, eventAtt, AgentChicanery.generateAgentsFinn5());
        for(String s : ustruct) {
            makeImageDisplay(s, s);
            model.exportFSM(s);
        }
    }

    private static void autoTestNewRandomSystem(int numPlants, int numSpecs, int numStates, int numStateVar, int numEve, int numEveVar, double shareRate, int numAgents, int numAgentVar, double obsRate, double ctrRate) throws IOException {
        String testName = "test_full_suite_";
        int count = -1;
        File f;
        do {
            f = new File(defaultWritePath + File.separator + testName + ++count);
        }while(f.exists());

        f.mkdir();
        testName += count+"";
        writePath = defaultWritePath + File.separator + testName;
        //System.out.println("This test: " + testName);
        System.out.println(testName + ", " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n---------------------------------------------\n");


        System.out.println("Test Configuration: Full Suite");
        System.out.println("Randomizer Parameters:");
        System.out.println(" Plants: " + numPlants + ", Specs: " + numSpecs + ", # States Average: " + numStates + ", State Variance: " + numStateVar + ", # Events Average: " + numEve + ", Event Variance: " + numEveVar +
        ", Event Share Rate: " + shareRate + ", # Agents: " + numAgents + ", Agent Variance: " + numAgentVar + ", Agent Obs. Event Rate: " + obsRate + ", Agent Ctr. Event Rate: " + ctrRate);
        System.out.println(" " + numPlants + ", " + numSpecs + ", " + numStates + ", " + numStateVar + ", " + numEve + ", " + numEveVar + ", " + shareRate + ", " + numAgents + ", " + numAgentVar + ", " + obsRate + ", " + ctrRate + "\n");
        System.out.println("---------------------------------------------\n");

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEve, numEveVar, shareRate, numAgents, numAgentVar, obsRate, ctrRate);
        List<String> events = RandomGeneration.generateRandomSystemSet(testName, model, info);
        List<String> names = RandomGeneration.getComponentNames(testName, numPlants, numSpecs);

        List<Map<String, List<Boolean>>> agents = RandomGeneration.generateRandomAgents(events, info);

        f = new File(writePath + File.separator + (testName + "_agents.txt"));
        try (RandomAccessFile raf = RandomAccessFileMode.READ_WRITE.create(f)) {
            raf.writeBytes(model.exportAgents(testName + "_agents", agents, eventAtt));
        }

        System.out.println("Agent Information: \n" + agents.toString().replace("},", "},\n").replaceAll("[\\[\\]]", " "));
        System.out.println("\n---------------------------------------------\n");

        for(String s : names) {
            //makeImageDisplay(s, s);
            f = new File(writePath + File.separator + s + ".txt");
            try (RandomAccessFile raf = RandomAccessFileMode.READ_WRITE.create(f)) {
                raf.writeBytes(model.exportFSM(s));
            }
            Files.move(new File(FormatConversion.createImgFromFSM(model.generateFSMDot(s), s)).toPath(), new File(writePath + File.separator + s + ".png").toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        autoTestSystemFull(testName, RandomGeneration.getPlantNames(testName, numPlants), RandomGeneration.getSpecNames(testName, numSpecs), agents, false);
    }

    private static void autoTestOldSystem(String prefixNom) throws IOException {
        String path = defaultWritePath + File.separator + prefixNom;
        List<String> plants = new ArrayList<String>();
        int counter = 0;
        List<String> hold = pullSourceData(path + File.separator + prefixNom + "_p_" + counter++ + ".txt");
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

        hold = pullSourceData(path + File.separator + prefixNom + "_agents.txt");

        List<Map<String, List<Boolean>>> agents = model.readInAgents(hold);

        System.out.println("Agent Information: \n" + agents.toString().replace("},", "},\n").replaceAll("[\\[\\]]", " "));

        String hold2 = writePath;
        writePath = null;

        autoTestSystemFull(prefixNom, plants, specs, agents, true);
        writePath = hold2;
    }

    private static void pullReserveDisplay() {
        String nom = model.storeProcessHoldSystem();
        if(nom != null) {
            makeImageDisplay(nom, nom + "_coobs");
        }
    }

    private static void autoTestSystemFull(String prefixNom, List<String> plantNames, List<String> specNames, List<Map<String, List<Boolean>>> agents, boolean displays) {
        printCoobsLabel(prefixNom, false);
        boolean coobs = checkCoobservable(plantNames, specNames, agents, false);

        if(displays)
            pullReserveDisplay();

        printIncrementalLabel(prefixNom, false);
        boolean icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);

        if(displays)
            pullReserveDisplay();

        printSBCoobsLabel(prefixNom);
        boolean sbCoobs = checkSBCoobservable(plantNames, specNames, agents);
        printIncrementalSBLabel(prefixNom);
        boolean icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);
        /*
        printCoobsLabel(prefixNom, true);
        boolean infCoobs = checkCoobservable(plantNames, specNames, agents, true);
        printIncrementalLabel(prefixNom, true);
        boolean icIfCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, true);
        */

        if(coobs && !sbCoobs) {
            System.out.println("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
        }

        boolean error = false;

        if(coobs != icCoobs) {
            fail("Incremental Algo. did not return same as Coobs. Algo.\n~~~");
            error = true;
        }
        if(sbCoobs != icSbCoobs) {
            fail("Incremental SB Algo. did not return same as SB Algo.\n~~~");
            error = true;
        }
/*        if(infCoobs != icIfCoobs) {
            fail("Incremental Inferencing Algo. did not return same as Inferencing Algo.");
        }*/
        if(sbCoobs && !coobs) {
            fail("State Based Algo. claimed True while Coobs. Algo. claimed False");
            error = true;
        }
/*        if(coobs && !infCoobs) {
            fail("Coobs. Algo. claimed True while Infer. Coobs. Algo. claimed False");
        }*/
        if(error) {
            try (Scanner sc = new Scanner(System.in)) {
                sc.nextLine();
            }
        }
    }

    private static void autoTestHeuristicsIncremental(int numPlants, int numSpecs, int numStates, int numStateVar, int numEve, int numEveVar, double shareRate, int numAgents, int numAgentVar, double obsRate, double ctrRate) throws IOException {
        String testName = "test_heu_inc_";
        int count = -1;
        File f;
        do {
            f = new File(defaultWritePath + File.separator + testName + ++count);
        }while(f.exists());

        f.mkdir();
        testName += Integer.toString(count);
        writePath = defaultWritePath + File.separator + testName;
        System.out.println("This test: " + testName);
        System.out.println(testName + ", " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n---------------------------------------------\n");


        System.out.println("Test Configuration: Incremental Heuristics");
        System.out.println("Randomizer Parameters:");
        System.out.println(" Plants: " + numPlants + ", Specs: " + numSpecs + ", # States Average: " + numStates + ", State Variance: " + numStateVar + ", # Events Average: " + numEve + ", Event Variance: " + numEveVar +
        ", Event Share Rate: " + shareRate + ", # Agents: " + numAgents + ", Agent Variance: " + numAgentVar + ", Agent Obs. Event Rate: " + obsRate + ", Agent Ctr. Event Rate: " + ctrRate);
        System.out.println(" " + numPlants + ", " + numSpecs + ", " + numStates + ", " + numStateVar + ", " + numEve + ", " + numEveVar + ", " + shareRate + ", " + numAgents + ", " + numAgentVar + ", " + obsRate + ", " + ctrRate + "\n");
        System.out.println("---------------------------------------------\n");

        RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEve, numEveVar, shareRate, numAgents, numAgentVar, obsRate, ctrRate);
        List<String> events = RandomGeneration.generateRandomSystemSet(testName, model, info);
        List<String> names = RandomGeneration.getComponentNames(testName, numPlants, numSpecs);

        List<Map<String, List<Boolean>>> agents = RandomGeneration.generateRandomAgents(events, info);

        System.out.println("Agent Information: \n" + agents.toString().replace("},", "},\n").replaceAll("[\\[\\]]", " "));
        System.out.println("\n---------------------------------------------\n");

        for(String s : names) {
            //makeImageDisplay(s, s);
            f = new File(writePath + File.separator + s + ".txt");
            try (RandomAccessFile raf = RandomAccessFileMode.READ_WRITE.create(f)) {
                raf.writeBytes(model.exportFSM(s));
            }
            Files.move(new File(FormatConversion.createImgFromFSM(model.generateFSMDot(s), s)).toPath(), new File(writePath + File.separator + s + ".png").toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        printIncrementalLabel(testName, false);
        Boolean result = null;
        for(int i = 0; i < Incremental.NUM_A_HEURISTICS; i++) {
            for(int j = 0; j < Incremental.NUM_B_HEURISTICS; j++) {
                for(int k = 0; k < Incremental.NUM_C_HEURISTICS; k++) {
                    Incremental.assignIncrementalOptions(i, j, k);
                    System.out.println("\nTest Config: " + i + ", " + j + ", " + k);

                    boolean hold = checkIncrementalCoobservable(RandomGeneration.getPlantNames(testName, numPlants), RandomGeneration.getSpecNames(testName, numSpecs), agents, false);

                    if(result == null) {
                        result = hold;
                    }
                    if(hold != result) {
                        fail("This heuristic configuration caused an improper result.");
                    }
                }
            }
        }

    }

    //-- Coobservable  ------------------------------------------------------------------------

    private static boolean checkCoobservable(String name, List<Map<String, List<Boolean>>> agents, boolean inf) {
        boolean result = inf ? model.isInferenceCoobservableUStruct(name, eventAtt, agents) : model.isCoobservableUStruct(name, eventAtt, agents);
        handleOutData();
        System.out.println("\t\t\t\t" + (inf ? "Inferencing " : "" ) + "Coobservable: " + result);

        return result;
    }

    private static boolean checkCoobservable(List<String> plants, List<String> specs, List<Map<String, List<Boolean>>> agents, boolean inf) {
        boolean result = inf ? model.isInferenceCoobservableUStruct(plants, specs, eventAtt, agents) : model.isCoobservableUStruct(plants, specs, eventAtt, agents);
        handleOutData();
        System.out.println("\t\t\t\t" + (inf ? "Inferencing " : "" ) + "Coobservable: " + result);

        return result;
    }

    private static void printCoobsLabel(String system, boolean type) {
        System.out.println(system + " " + (type ? "Inference Coobservability:" : "Coobservability:") + " \t");
    }

    private static void checkSystemACoobservable(boolean inf){
        String SystemA = "Example 1";
        SystemGeneration.generateSystemA(SystemA);
        printCoobsLabel(SystemA, inf);
        checkCoobservable(SystemA, AgentChicanery.generateAgentsA(), inf);
    }

    private static void checkSystemBCoobservable(boolean inf) {
        String SystemB = "Example B 1";
        SystemGeneration.generateSystemB(SystemB);
        printCoobsLabel(SystemB, inf);
        checkCoobservable(SystemB, AgentChicanery.generateAgentsB(), inf);
    }

    private static void checkSystemBAltCoobservable(boolean inf) {
        String SystemB = "Example B Alt";
        SystemGeneration.generateSystemBAlt(SystemB);
        makeImageDisplay(SystemB, SystemB);
        printCoobsLabel(SystemB, inf);
        checkCoobservable(SystemB, AgentChicanery.generateAgentsB(), inf);
        List<String> ustruct = model.buildUStructureCrush(SystemB, eventAtt, AgentChicanery.generateAgentsB());
        for(String s : ustruct) {
            makeImageDisplay(s, s);
            model.exportFSM(s);
        }
    }

    private static void checkSystemCCoobservable(boolean inf) {
        String SystemC = "Example C";
        SystemGeneration.generateSystemC(SystemC);
        printCoobsLabel(SystemC, inf);
        checkCoobservable(SystemC, AgentChicanery.generateAgentsC(), inf);
    }

    private static void checkSystemDCoobservable(boolean inf) {
        String SystemD = "Example D 1";
        SystemGeneration.generateSystemD(SystemD);
        printCoobsLabel(SystemD, inf);
        checkCoobservable(SystemD, AgentChicanery.generateAgentsD(), inf);
    }

    private static void checkSystemECoobservable(boolean inf) {
        String SystemE = "Example E";
        SystemGeneration.generateSystemE(SystemE);
        printCoobsLabel(SystemE, inf);
        checkCoobservable(SystemE, AgentChicanery.generateAgentsE(), inf);
    }

    private static void checkSystemFinnCoobservable(boolean inf){
        String SystemFinn = "System Example Finn";
        SystemGeneration.generateSystemFinn(SystemFinn);
        printCoobsLabel(SystemFinn, inf);
        checkCoobservable(SystemFinn, AgentChicanery.generateAgentsFinn5(), inf);
    }

    private static void checkSystemLiuOneCoobservable(boolean inf) {
        List<String> names = new ArrayList<String>();
        List<String> plant = new ArrayList<String>();
        List<String> spec = new ArrayList<String>();
        names.add("G1");
        plant.add("G1");
        names.add("H1");
        spec.add("H1");
        SystemGeneration.generateSystemSetA(names);
        printCoobsLabel("System Liu One", inf);
        checkCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne(), inf);
    }

    private static void checkSystemLiuTwoCoobservable(boolean inf) {
        List<String> names = new ArrayList<String>();
        List<String> plant = new ArrayList<String>();
        List<String> spec = new ArrayList<String>();
        names.add("G3");
        plant.add("G3");
        names.add("G4");
        plant.add("G4");
        names.add("H1");
        spec.add("H1");
        SystemGeneration.generateSystemSetB(names);
        printCoobsLabel("System Liu Two", inf);
        checkCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne(), inf);
    }

    //-- SBCoobservable  ----------------------------------------------------------------------

    private static boolean checkSBCoobservable(String name, List<Map<String, List<Boolean>>> agents) {
        boolean result = prepSoloSpecRunSB(name, agents);
        handleOutData();
        System.out.println("\t\t\t\tSB-Coobservable: " + result);

        return result;
    }

    private static boolean checkSBCoobservable(List<String> plants, List<String> specs, List<Map<String, List<Boolean>>> agents) {
        boolean result = model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents);
        handleOutData();
        System.out.println("\t\t\t\tSB-Coobservable: " + result);

        return result;
    }

    private static boolean prepSoloSpecRunSB(String name, List<Map<String, List<Boolean>>> agents) {
        String b = name + "_spec";
        generateSoloSpecPlant(name, b);
        List<String> plants = new ArrayList<String>();
        List<String> specs = new ArrayList<String>();
        plants.add(name);
        specs.add(b);
        return model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents);
    }

    private static void printSBCoobsLabel(String system) {
        System.out.println(system + " SB Coobservability: \t");
    }

    private static void checkSystemASBCoobservable() {
        String a = "Ex1";
        SystemGeneration.generateSystemA(a);
        printSBCoobsLabel(a);
        checkSBCoobservable(a, AgentChicanery.generateAgentsA());
    }

    private static void checkSystemBSBCoobservable() {
        String ex1 = "Ex B 1";
        SystemGeneration.generateSystemB(ex1);
        printSBCoobsLabel(ex1);
        checkSBCoobservable(ex1, AgentChicanery.generateAgentsB());
    }

    private static void checkSystemCSBCoobservable() {
        String ex1 = "Ex C 1";
        SystemGeneration.generateSystemC(ex1);
        printSBCoobsLabel(ex1);
        checkSBCoobservable(ex1, AgentChicanery.generateAgentsC());
    }

    private static void checkSystemDSBCoobservable() {
        String ex1 = "Ex D 1";
        SystemGeneration.generateSystemD(ex1);
        printSBCoobsLabel(ex1);
        checkSBCoobservable(ex1, AgentChicanery.generateAgentsD());
    }

    private static void checkSystemESBCoobservable() {
        String SystemE = "Example E 1";
        SystemGeneration.generateSystemE(SystemE);
        printSBCoobsLabel(SystemE);
        checkSBCoobservable(SystemE, AgentChicanery.generateAgentsE());
    }

    private static void checkSystemUrvashiSBCoobservable() {
        List<String> plant = new ArrayList<String>();
        plant.add("plant");
        List<String> spec = new ArrayList<String>();
        spec.add("spec");
        SystemGeneration.generateSystemSetUrvashi(plant.get(0), spec.get(0));
        printSBCoobsLabel("System Urvashi");
        checkSBCoobservable(plant, spec, AgentChicanery.generateAgentsUrvashi());
    }

    private static void checkSystemLiuOneSBCoobservable() {
        List<String> names = new ArrayList<String>();
        List<String> plant = new ArrayList<String>();
        List<String> spec = new ArrayList<String>();
        names.add("G1");
        plant.add("G1");
        names.add("H1");
        spec.add("H1");
        SystemGeneration.generateSystemSetA(names);
        printSBCoobsLabel("System Liu One");
        checkSBCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne());
    }

    private static void checkSystemLiuTwoSBCoobservable() {
        List<String> names = new ArrayList<String>();
        List<String> plant = new ArrayList<String>();
        List<String> spec = new ArrayList<String>();
        names.add("G3");
        plant.add("G3");
        names.add("G4");
        plant.add("G4");
        names.add("H1");
        spec.add("H1");
        SystemGeneration.generateSystemSetB(names);
        printSBCoobsLabel("System Liu Two");
        checkSBCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne());
    }

    //-- Incremental Coobservable  ------------------------------------------------------------

    private static boolean checkIncrementalCoobservable(List<String> plants, List<String> specs, List<Map<String, List<Boolean>>> agents, boolean inf) {
        boolean result = inf ? model.isIncrementalInferenceCoobservable(plants, specs, eventAtt, agents) : model.isIncrementalCoobservable(plants, specs, eventAtt, agents);
        handleOutData();
        System.out.println("\t\t\t\tIncremental" + (inf ? " Inference" : "") + " Coobservable: " + result);

        return result;
    }

    private static boolean checkIncrementalSBCoobservable(List<String> plants, List<String> specs, List<Map<String, List<Boolean>>> agents) {
        boolean result = model.isIncrementalSBCoobservable(plants, specs, eventAtt, agents);
        handleOutData();
        System.out.println("\t\t\t\tIncremental SB Coobservable: " + result);

        return result;
    }

    private static void printIncrementalLabel(String system, boolean inf) {
        System.out.println(system + " Incremental" + (inf ? " Inference" : "") + " Coobservability: \t");
    }

    private static void printIncrementalSBLabel(String system) {
        System.out.println(system + " Incremental SB Coobservability: \t");
    }

    private static void checkSystemLiuOneIncrementalCoobservable() {
        List<String> names = new ArrayList<String>();
        List<String> plant = new ArrayList<String>();
        List<String> spec = new ArrayList<String>();
        names.add("G1");
        plant.add("G1");
        names.add("H1");
        spec.add("H1");
        SystemGeneration.generateSystemSetA(names);
        printIncrementalLabel("System Liu One", false);
        checkIncrementalCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne(), false);
    }

    private static void checkSystemLiuTwoIncrementalCoobservable() {
        List<String> names = new ArrayList<String>();
        List<String> plant = new ArrayList<String>();
        List<String> spec = new ArrayList<String>();
        names.add("G3");
        plant.add("G3");
        names.add("G4");
        plant.add("G4");
        names.add("H1");
        spec.add("H1");
        SystemGeneration.generateSystemSetB(names);
        printIncrementalLabel("System Liu Two", false);
        checkIncrementalCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne(), false);
    }

//---  Support Methods   ----------------------------------------------------------------------

    private static List<String> pullSourceData(String path) throws IOException {
        File f = new File(path);
        List<String> lines = null;
        if(f.exists()) {
            try (Reader reader = IOUtils.buffer(new FileReader(f))) {
                lines = IOUtils.readLines(reader);
            }
        }
        return lines;
    }

    private static void makeImage(String path, String name) throws IOException {
        List<String> look = pullSourceData(path + name + ".txt");

        model.readInFSM(look);

        makeImageDisplay(name, name);
    }

    private static void handleOutData() {
        System.out.println(model.getLastProcessData().produceOutputLog());
        List<Double> data = model.getLastProcessData().getStoredData();
        List<String> use = model.getLastProcessData().getOutputGuide();

        printEquivalentResults(use, data);
    }

    private static void printEquivalentResults(List<String> guide, List<Double> vals) {
        if(writePath != null) {
            File f = new File(writePath + File.separator + ANALYSIS_FILE);
            try (RandomAccessFile raf = RandomAccessFileMode.READ_WRITE.create(f)) {
                raf.seek(raf.length());
                if(raf.length() == 0) {
                    for(String s : guide)
                        raf.writeBytes(s + ", ");
                    raf.writeBytes("\n");
                }
                for(Double d : vals) {
                    raf.writeBytes(threeSig(d) + ", \t");
                }
                raf.writeBytes("\n");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterEach
    public void resetModel() {
        model.flushFSMs();
    }

    private static Double threeSig(double in) {
        String use = in+"0000";
        int posit = use.indexOf(".") + 4;
        return Double.parseDouble(use.substring(0, posit));
    }

    private static void generateSoloSpecPlant(String plant, String spec) {
        model.convertSoloPlantSpec(plant, spec);
    }

    private static void makeImageDisplay(String in, String nom) {
        try {
            String path = FormatConversion.createImgFromFSM(model.generateFSMDot(in), nom);
            //System.out.println(path);
            WindowFrame fram = new WindowFrame(800, 800) {
                @Override
                public void reactToResize() {
                }
            };
            fram.reserveWindow("Main");
            fram.setName("Test Functionality: " + nom);
            fram.showActiveWindow("Main");
            ElementPanel p = new ElementPanel(0, 0, 800, 800);
            ImageDisplay iD = new ImageDisplay(path, p);
            p.setEventReceiver(iD.generateEventReceiver());
            fram.addPanelToWindow("Main", "pan", p);
            iD.refresh();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (HeadlessException e) {
        }
    }

    private static void makeSVGImage(String in, String nom) {
        try {
            String path = FormatConversion.createSVGFromFSM(model.generateFSMDot(in), nom);
            System.out.println(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
