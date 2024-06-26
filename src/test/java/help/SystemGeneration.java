package help;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import model.AttributeList;
import model.Manager;
import model.fsm.TransitionSystem;

public class SystemGeneration {

//---  Instance Variables   -------------------------------------------------------------------

    private static Manager model;

//---  Static Assignments   -------------------------------------------------------------------

    public static void assignManager(Manager man) {
        model = man;
    }

//---  Operations   ---------------------------------------------------------------------------

    //-- Solo System  -----------------------------------------

    public static void generateSystemExample1(String name) {
        generateSystemDefault(name);

        model.addStates(name, 7);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_A);

        model.addTransition(name, "0", "a_{1}", "1");
        model.addTransition(name, "0", "a_{2}", "2");

        model.addTransition(name, "1", "b_{1}", "3");
        model.addTransition(name, "1", "b_{2}", "4");
        model.addTransition(name, "2", "b_{1}", "5");
        model.addTransition(name, "2", "b_{2}", "6");

        model.addTransition(name, "3", "c", "3");
        model.addTransition(name, "4", "c", "4");
        model.addTransition(name, "5", "c", "5");
        model.addTransition(name, "6", "c", "6");


    }

    public static void generateSystemExample2(String name) {
        generateSystemDefault(name);

        model.addStates(name, 7);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_A);

        model.addTransition(name, "0", "a_{1}", "1");
        model.addTransition(name, "0", "a_{2}", "2");

        model.addTransition(name, "1", "b_{1}", "3");
        model.addTransition(name, "1", "b_{2}", "4");
        model.addTransition(name, "2", "b_{1}", "5");
        model.addTransition(name, "2", "b_{2}", "6");

        model.addTransition(name, "3", "c", "3");
    }

    public static void generateSystemExample3(String name) {
        generateSystemDefault(name);

        model.addStates(name, 7);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_A);

        model.addTransition(name, "0", "a_{1}", "1");
        model.addTransition(name, "0", "a_{2}", "2");

        model.addTransition(name, "1", "b_{1}", "3");
        model.addTransition(name, "1", "b_{2}", "4");
        model.addTransition(name, "2", "b_{1}", "5");
        model.addTransition(name, "2", "b_{2}", "6");

        model.addTransition(name, "3", "c", "3");
        model.addTransition(name, "4", "c", "4");
        model.addTransition(name, "5", "c", "5");
        model.addTransition(name, "6", "c", "6");

        setBadTransitions(name,"4","c","5","c","6","c");
    }

    public static void generateSystemExample4(String name) {
        generateSystemDefault(name);

        model.addState(name, "(0, 1, 2)");
        model.addState(name, "(3, 5)");
        model.addState(name, "(4, 6)");

        initialState(name, "(0, 1, 2)");

        initiateEvents(name, EventSets.EVENT_LIST_A);

        model.addTransition(name, "(0, 1, 2)", "b_{1}", "(3, 5)");
        model.addTransition(name, "(0, 1, 2)", "b_{2}", "(4, 6)");
        model.addTransition(name, "(4, 6)", "c", "(4, 6)");
        model.addTransition(name, "(3, 5)", "c", "(3, 5)");

    }

    public static void generateSystemA(String name) {
        generateSystemDefault(name);

        model.addStates(name, 8);
        model.removeState(name, "0");

        model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);

        initiateEvents(name, EventSets.EVENT_LIST_A, "c");

        model.addTransition(name, "1", "a_{1}", "2");
        model.addTransition(name, "1", "a_{2}", "3");
        model.addTransition(name, "2", "b_{1}", "4");
        model.addTransition(name, "2", "b_{2}", "5");
        model.addTransition(name, "3", "b_{1}", "6");
        model.addTransition(name, "3", "b_{2}", "7");

        model.addTransition(name, "4", "c", "4");
        model.addTransition(name, "5", "c", "5");
        model.addTransition(name, "6", "c", "6");
        model.addTransition(name, "7", "c", "7");

        setBadTransitions(name, "5", "c", "6", "c");
    }

    public static void generateSystemB(String name) {
        generateSystemDefault(name);

        model.addStates(name, 6);

        model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);

        initiateEvents(name, EventSets.EVENT_LIST_B, "s");

        model.setEventAttribute(name, "c", AttributeList.ATTRIBUTE_OBSERVABLE, false);

        model.addTransition(name, "0", "a", "1");
        model.addTransition(name, "0", "b", "2");
        model.addTransition(name, "1", "b", "3");
        model.addTransition(name, "1", "c", "2");
        model.addTransition(name, "2", "b", "3");
        model.addTransition(name, "2", "d", "5");
        model.addTransition(name, "3", "d", "4");
        model.addTransition(name, "4", "s", "4");
        model.addTransition(name, "5", "s", "5");

        setBadTransitions(name, "5", "s");
    }

    public static void generateSystemBAlt(String name) {
        generateSystemDefault(name);

        model.addStates(name, 6);

        model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);

        initiateEvents(name, EventSets.EVENT_LIST_B, "s");

        model.setEventAttribute(name, "c", AttributeList.ATTRIBUTE_OBSERVABLE, false);

        model.addTransition(name, "0", "a", "1");
        model.addTransition(name, "0", "b", "2");
        model.addTransition(name, "1", "b", "3");
        model.addTransition(name, "1", "c", "2");
        model.addTransition(name, "2", "b", "3");
        model.addTransition(name, "2", "a", "3");
        model.addTransition(name, "2", "d", "5");
        model.addTransition(name, "3", "d", "4");
        model.addTransition(name, "4", "s", "4");
        model.addTransition(name, "5", "s", "5");

        setBadTransitions(name, "5", "s");
    }

    public static void generateSystemC(String name) {
        generateSystemDefault(name);

        model.addStates(name, 5);

        model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);

        initiateEvents(name, EventSets.EVENT_LIST_C, "c");

        model.addTransition(name, "0", "a_{1}", "1");
        model.addTransition(name, "0", "b_{1}", "1");
        model.addTransition(name, "0", "a_{2}", "2");
        model.addTransition(name, "0", "b_{2}", "2");
        model.addTransition(name, "1", "c", "3");
        model.addTransition(name, "2", "d", "3");
        model.addTransition(name, "2", "c", "4");

        setBadTransitions(name, "2", "c");
    }

    public static void generateSystemD(String name) {
        generateSystemDefault(name);

        model.addStates(name, 7);
        model.removeState(name, "0");

        model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);

        initiateEvents(name, EventSets.EVENT_LIST_D, "b", "c");

        model.addTransition(name, "1", "a", "3");
        model.addTransition(name, "1", "b", "2");
        model.addTransition(name, "2", "c", "4");
        model.addTransition(name, "2", "b", "5");
        model.addTransition(name, "3", "b", "5");
        model.addTransition(name, "5", "c", "6");

        setBadTransitions(name, "2", "b", "5", "c");
    }

    public static void generateSystemE(String name) {
        generateSystemDefault(name);

        model.addStates(name, 12);
        model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);

        initiateEvents(name, EventSets.EVENT_LIST_E, "s");

        model.addTransition(name, "0", "c1", "1");
        model.addTransition(name, "0", "a_{1}", "2");
        model.addTransition(name, "0", "c2", "3");
        model.addTransition(name, "0", "a_{2}", "4");
        model.addTransition(name, "0", "c3", "5");

        model.addTransition(name, "1", "a_{2}", "6");
        model.addTransition(name, "1", "b_{2}", "6");

        model.addTransition(name, "2", "b_{1}", "6");
        model.addTransition(name, "2", "b_{2}", "7");

        model.addTransition(name, "3", "a_{2}", "7");
        model.addTransition(name, "3", "b_{1}", "7");

        model.addTransition(name, "4", "b_{1}", "8");

        model.addTransition(name, "5", "a_{1}", "8");
        model.addTransition(name, "5", "b_{2}", "8");

        model.addTransition(name, "6", "d", "9");
        model.addTransition(name, "7", "d", "10");
        model.addTransition(name, "8", "d", "11");

        model.addTransition(name, "9", "s", "9");
        model.addTransition(name, "10", "s", "10");
        model.addTransition(name, "11", "s", "11");

        setBadTransitions(name, "10", "s", "11", "s");
    }

    public static void generateSystemFinn(String name) {
        generateSystemDefault(name);

        model.addStates(name, 19);
        //model.removeState(name, "0");

        model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);

        initiateEvents(name, EventSets.EVENT_LIST_FINN5, "s");

        model.addTransition(name, "0", "a_{1}", "1");
        model.addTransition(name, "0", "a_{2}", "2");
        model.addTransition(name, "0", "a_{3}", "3");
        model.addTransition(name, "0", "a_{4}", "4");
        model.addTransition(name, "0", "a_{5}", "5");
        model.addTransition(name, "0", "a_{6}", "6");
        model.addTransition(name, "1", "b_{1}", "7");
        model.addTransition(name, "1", "b_{2}", "8");
        model.addTransition(name, "2", "b_{2}", "9");
        model.addTransition(name, "2", "b_{3}", "10");
        model.addTransition(name, "3", "b_{3}", "11");
        model.addTransition(name, "3", "b_{4}", "12");
        model.addTransition(name, "4", "b_{4}", "13");
        model.addTransition(name, "4", "b_{5}", "14");
        model.addTransition(name, "5", "b_{5}", "15");
        model.addTransition(name, "5", "b_{6}", "16");
        model.addTransition(name, "6", "b_{6}", "17");
        model.addTransition(name, "6", "b_{1}", "18");

        model.addTransition(name, "7", "s", "7");
        model.addTransition(name, "8", "s", "8");
        model.addTransition(name, "9", "s", "9");
        model.addTransition(name, "10", "s", "10");
        model.addTransition(name, "11", "s", "11");
        model.addTransition(name, "12", "s", "12");
        model.addTransition(name, "13", "s", "13");
        model.addTransition(name, "14", "s", "14");
        model.addTransition(name, "15", "s", "15");
        model.addTransition(name, "16", "s", "16");
        model.addTransition(name, "17", "s", "17");
        model.addTransition(name, "18", "s", "18");

        setBadTransitions(name, "8", "s", "10", "s","12", "s","14", "s","16", "s","17", "s");
    }

    public static void generateSystemSigmaStarion(String name, List<String> events) {
        generateSystemDefault(name);

        model.addStates(name, 1);

        initialState(name, "0");

        String[] use = events.toArray(ArrayUtils.EMPTY_STRING_ARRAY);

        initiateEvents(name, use);

        for(String e : events) {
            model.addTransition(name, "0", e, "0");
        }
    }

    public static void generateSystemSpecPrimeTestPlant(String name) {
        generateSystemDefault(name);

        model.addStates(name, 5);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_SPEC_PRIME);

        addTransitions(name, "a", "0", "1");
        addTransitions(name, "b", "0", "1", "1", "3");
        addTransitions(name, "g", "1", "2", "3", "4");
    }

    public static void generateSystemSpecPrimeTestSpec(String name) {
        generateSystemDefault(name);

        model.addStates(name, 5);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_SPEC_PRIME);

        addTransitions(name, "a", "0", "2");
        addTransitions(name, "b", "0", "1", "2", "4");
        addTransitions(name, "g", "1", "3");
    }

    //-- Poly System  -----------------------------------------

    public static void generateSystemSetA(List<String> name) {
        generateLiuG1(name.get(0));
        generateLiuH1(name.get(1));
    }

    public static void generateSystemSetB(List<String> name) {
        generateLiuG3(name.get(0));
        generateLiuG4(name.get(1));
        generateLiuH1(name.get(2));
    }

    private static void generateLiuG1(String name) {
        generateSystemDefault(name);

        model.addStates(name, 5);
        model.removeState(name, "0");

        model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);

        initiateEvents(name, EventSets.EVENT_LIST_LIU_ONE, "g");

        model.addTransition(name, "1", "a", "2");
        model.addTransition(name, "1", "b", "2");
        model.addTransition(name, "2", "a", "3");
        model.addTransition(name, "2", "b", "3");
        model.addTransition(name, "3", "g", "4");
    }

    private static void generateLiuG2(String name) {
        generateSystemDefault(name);

        model.addStates(name, 7);
        model.removeState(name, "0");

        model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);

        initiateEvents(name, EventSets.EVENT_LIST_LIU_ONE, "g");

        addTransitions(name, "a", "1", "2", "2", "4", "3", "5");
        addTransitions(name, "b", "1", "3", "3", "4", "2", "5");
        addTransitions(name, "g", "5", "6");
    }

    private static void generateLiuH1(String name) {
        generateSystemDefault(name);

        model.addStates(name, 6);
        model.removeState(name, "0");

        initialState(name, "1");

        initiateEvents(name, EventSets.EVENT_LIST_LIU_ONE, "g");

        addTransitions(name, "a", "1", "2", "3", "4");
        addTransitions(name, "b", "1", "3", "2", "4");
        addTransitions(name, "g", "4", "5");
    }

    private static void generateLiuG3(String name) {
        generateSystemDefault(name);

        model.addStates(name, 9);
        model.removeState(name, "0");

        initialState(name, "1");

        initiateEvents(name, EventSets.EVENT_LIST_LIU_ONE, "g");

        addTransitions(name, "a", "1", "2", "2", "4", "3", "5");
        addTransitions(name, "b", "1", "3", "3", "6", "2", "5");
        addTransitions(name, "g", "4", "7", "5", "8");
    }

    private static void generateLiuG4(String name) {
        generateSystemDefault(name);

        model.addStates(name, 9);
        model.removeState(name, "0");

        initialState(name, "1");

        initiateEvents(name, EventSets.EVENT_LIST_LIU_ONE, "g");

        addTransitions(name, "a", "1", "2", "2", "4", "3", "5");
        addTransitions(name, "b", "1", "3", "3", "6", "2", "5");
        addTransitions(name, "g", "6", "7", "5", "8");
    }

    private static void generateLiuH2(String name) {
        generateSystemDefault(name);
    }

    public static void generateSystemSetUrvashi(String plant, String spec) {
        generateUrvashiPlant(plant);
        generateUrvashiSpec(spec);
    }

    private static void generateUrvashiPlant(String name) {
        generateSystemDefault(name);

        model.addStates(name, 7);


        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_D, "c");

        addTransitions(name, "a", "0", "1", "2", "4");
        addTransitions(name, "b", "0", "2", "1", "3");
        addTransitions(name, "c", "3", "5", "4", "6");
    }

    private static void generateUrvashiSpec(String name) {
        generateSystemDefault(name);

        model.addStates(name, 7);

        model.removeState(name, "5");

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_D, "c");

        addTransitions(name, "a", "0", "1", "2", "4");
        addTransitions(name, "b", "0", "2", "1", "3");
        addTransitions(name, "c", "4", "6");
    }

    public static List<String> generateSystemSetDTP() {
        List<String> use = new ArrayList<String>();
        use.add("Sender");
        use.add("Receiver");
        use.add("Channel");
        use.add("SpecOne");
        use.add("SpecTwo");
        use.add("SpecThree");
        generateDTPSender(use.get(0));
        generateDTPReceiver(use.get(1));
        generateDTPChannel(use.get(2));
        generateDTPSpecOne(use.get(3));
        generateDTPSpecTwo(use.get(4));
        generateDTPSpecThree(use.get(5));
        return use;
    }

    private static void generateDTPSender(String name) {
        generateSystemDefault(name);

        model.addStates(name, 5);

        model.removeState(name,  "0");

        initialState(name, "1");

        initiateEvents(name, EventSets.EVENT_LIST_DTP_SENDER);

        addTransitions(name, "getFrame", "1", "2", "4", "2");
        addTransitions(name, "loss", "3", "2");
        addTransitions(name, "send_0", "2", "3", "4", "3");
        addTransitions(name, "send_1", "2", "3", "4", "3");
        addTransitions(name, "rcvAck_0", "3", "4");
        addTransitions(name, "rcvAck_1", "3", "4");
    }

    private static void generateDTPReceiver(String name) {
        generateSystemDefault(name);

        model.addStates(name, 6);

        model.removeState(name,  "0");

        initialState(name, "1");

        initiateEvents(name, EventSets.EVENT_LIST_DTP_RECEIVER);

        addTransitions(name, "rcv_0", "1", "2", "4", "5");
        addTransitions(name, "rcv_1", "1", "2", "4", "5");
        addTransitions(name, "passToHost", "2", "3", "5", "3");
        addTransitions(name, "sendAck_0", "3", "4", "5", "4");
        addTransitions(name, "sendAck_1", "3", "4", "5", "4");
    }

    private static void generateDTPChannel(String name) {
        generateSystemDefault(name);

        model.addStates(name, 5);

        model.removeState(name,  "0");

        initialState(name, "1");

        initiateEvents(name, EventSets.EVENT_LIST_DTP_CHANNEL);

        addTransitions(name, "rcv_0", "2", "1");
        addTransitions(name, "rcvAck_1", "3", "1");
        addTransitions(name, "rcv_1", "4", "1");
        addTransitions(name, "rcvAck_0", "5", "1");

        addTransitions(name, "send_0", "1", "2");
        addTransitions(name, "send_1", "1", "4");
        addTransitions(name, "sendAck_0", "1", "5");
        addTransitions(name, "sendAck_1", "1", "3");

        addTransitions(name, "loss", "2", "1", "4", "1", "5", "1", "3", "1");
    }

    private static void generateDTPSpecOne(String name) {
        generateSystemDefault(name);

        model.addStates(name, 3);

        model.removeState(name,  "0");

        initialState(name, "1");

        initiateEvents(name, EventSets.EVENT_LIST_DTP_SPEC_ONE);

        addTransitions(name, "getFrame", "1", "2");
        addTransitions(name, "passToHost", "2", "1");

        for(String s : EventSets.EVENT_LIST_DTP) {
            if(!(s.equals("getFrame") || s.equals("passToHost"))) {
                addTransitions(name, s, "1", "1", "2", "2");
            }
        }
    }

    private static void generateDTPSpecTwo(String name) {
        generateSystemDefault(name);

        model.addStates(name, 7);

        model.removeState(name,  "0");

        initialState(name, "1");

        initiateEvents(name, EventSets.EVENT_LIST_DTP_SPEC_TWO);

        addTransitions(name, "getFrame", "1", "2", "4", "5");
        addTransitions(name, "loss", "3", "2", "6", "5");

        addTransitions(name, "rcvAck_0", "3", "4", "6", "5");
        addTransitions(name, "rcvAck_1", "3", "2", "6", "1");

        addTransitions(name, "send_0", "2", "3");
        addTransitions(name, "send_1", "5", "6");
    }

    private static void generateDTPSpecThree(String name) {
        generateSystemDefault(name);

        model.addStates(name, 7);

        model.removeState(name,  "0");

        initialState(name, "1");

        initiateEvents(name, EventSets.EVENT_LIST_DTP_SPEC_THREE);

        addTransitions(name, "rcv_0", "1", "2", "4", "3");
        addTransitions(name, "rcv_1", "4", "5", "1", "6");

        addTransitions(name, "passToHost", "2", "3", "5", "6");

        addTransitions(name, "sendAck_0", "3", "4");
        addTransitions(name, "sendAck_1", "6", "1");
    }

    public static List<List<String>> generateSystemSetHISC(){
        List<List<String>> out = new ArrayList<List<String>>();

        List<String> plant = new ArrayList<String>();
        plant.add("Packaging");
        plant.add("Source");
        plant.add("Sink");
        plant.add("Test");

        generateHISCPackaging(plant.get(0));
        generateHISCSource(plant.get(1));
        generateHISCSink(plant.get(2));
        generateHISCTest(plant.get(3));

        plant.addAll(generateHISCPath("Path", 3));
        plant.addAll(generateHISCDefine("Define", 3));
        plant.addAll(generateHISCPolishPart("Polish Part", 3));
        plant.addAll(generateHISCAttachCase("Attach Case", 3));
        plant.addAll(generateHISCAttachPart("Attach Part", 3));


        List<String> spec = new ArrayList<String>();
        spec.add("In Buffer");
        spec.add("Out Buffer");
        spec.add("Package");
        spec.add("Ensure");

        generateHISCInBuff(spec.get(0));
        generateHISCOutBuff(spec.get(1));
        generateHISCPackage(spec.get(2));
        generateHISCEnsure(spec.get(3));

        spec.addAll(generateHISCMoves("Moves", 3));
        spec.addAll(generateHISCPolishSequence("Polish Sequence", 3));
        spec.addAll(generateHISCAffix("Affix", 3));
        spec.addAll(generateHISCG("G", 3));
        spec.addAll(generateHISCSequence("Sequence", 3));

        out.add(plant);
        out.add(spec);

        return out;
    }

    public static List<List<String>> generateSystemSetHISCHighLevel(){
        List<List<String>> out = new ArrayList<List<String>>();

        List<String> plant = new ArrayList<String>();
        plant.add("Packaging");
        plant.add("Source");
        plant.add("Sink");
        plant.add("Test");

        generateHISCPackaging(plant.get(0));
        generateHISCSource(plant.get(1));
        generateHISCSink(plant.get(2));
        generateHISCTest(plant.get(3));

        List<String> spec = new ArrayList<String>();
        spec.add("In Buffer");
        spec.add("Out Buffer");
        spec.add("Package");
        spec.add("Ensure");

        generateHISCInBuff(spec.get(0));
        generateHISCOutBuff(spec.get(1));
        generateHISCPackage(spec.get(2));
        generateHISCEnsure(spec.get(3));

        out.add(plant);
        out.add(spec);

        return out;
    }

    public static List<List<String>> generateSystemSetHISCLowLevel(){
        List<List<String>> out = new ArrayList<List<String>>();

        List<String> plant = new ArrayList<String>();

        plant.addAll(generateHISCPath("Path", 1));
        plant.addAll(generateHISCDefine("Define", 1));
        plant.addAll(generateHISCPolishPart("Polish Part", 1));
        plant.addAll(generateHISCAttachCase("Attach Case", 1));
        plant.addAll(generateHISCAttachPart("Attach Part", 1));


        List<String> spec = new ArrayList<String>();

        //spec.addAll(generateHISCMoves("Moves", 1));
        spec.addAll(generateHISCPolishSequence("Polish Sequence", 1));
        spec.addAll(generateHISCAffix("Affix", 1));
        spec.addAll(generateHISCG("G", 1));
        spec.addAll(generateHISCSequence("Sequence", 1));

        out.add(plant);
        out.add(spec);

        return out;
    }

    private static String[] appendControlNumberEventSet(String[] in, int num) {
        String[] out = new String[in.length];

        for(int i = 0; i < out.length; i++) {
            out[i] = appendControlNumber(in[i], num);
        }
        return out;
    }

    private static String appendControlNumber(String in, int num) {
        return in + StringUtils.repeat('I', num);
    }

    //-- Plant Parts  -----------------------------------------

    public static void generateHISCPackaging(String name) {
        generateSystemDefault(name);

        model.addStates(name, 3);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_HISC_PACK_SYS);

        addTransitions(name, "take_item", "0", "1");
        addTransitions(name, "package", "1", "2");
        addTransitions(name, "allow_exit", "2", "0");
    }

    public static void generateHISCSource(String name) {
        generateSystemDefault(name);

        model.addStates(name, 1);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_HISC_SOURCE);

        addTransitions(name, "new_part", "0", "0");
    }

    public static void generateHISCSink(String name) {
        generateSystemDefault(name);

        model.addStates(name, 1);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_HISC_SINK);

        addTransitions(name, "allow_exit", "0", "0");
    }

    public static void generateHISCTest(String name) {
        generateSystemDefault(name);

        model.addStates(name, 4);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_HISC_TEST);

        addTransitions(name, "part_f_obuff", "0", "1");
        addTransitions(name, "part_passes", "1", "2");
        addTransitions(name, "part_fails", "1", "3");
        addTransitions(name, "ret_inbuff", "3", "0");
        addTransitions(name, "deposit_part", "2", "0");
    }

        //-- J Parts  -----------------------------------------

    public static List<String> generateHISCPath(String nameIn, int num) {
        List<String> out = new ArrayList<String>();
        for(int i = 1; i < num + 1; i++) {
            String name = nameIn + "_" + i;
            out.add(name);

            generateSystemDefault(name);

            model.addStates(name, 10);

            initialState(name, "0");

            initiateEvents(name, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_PATH, i));

            addTransitions(name, appendControlNumber("part_ent-", i), "0", "1");
            addTransitions(name, appendControlNumber("part_arr1-", i), "1", "2");
            addTransitions(name, appendControlNumber("part_lv1-", i), "2", "3");
            addTransitions(name, appendControlNumber("str_exit-", i), "3", "4");
            addTransitions(name, appendControlNumber("fin_exit-", i), "4", "0");
            addTransitions(name, appendControlNumber("partLvExit-", i), "3", "5");

            addTransitions(name, appendControlNumber("part_arr2-", i), "5", "6");
            addTransitions(name, appendControlNumber("recog_A-", i), "6", "7");
            addTransitions(name, appendControlNumber("recog_B-", i), "6", "7");
            addTransitions(name, appendControlNumber("part_lv2-", i), "7", "8");
            addTransitions(name, appendControlNumber("part_arr3-", i), "8", "9");
            addTransitions(name, appendControlNumber("part_lv3-", i), "9", "1");
        }
        return out;
    }

    public static List<String> generateHISCDefine(String nameIn, int num) {
        List<String> out = new ArrayList<String>();
        for(int i = 1; i < num + 1; i++) {
            String name = nameIn + "_" + i;
            out.add(name);

            generateSystemDefault(name);

            model.addStates(name, 1);

            initialState(name, "0");

            initiateEvents(name, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_DEFINE, i));

            addTransitions(name, appendControlNumber("attch_ptA-", i), "0", "0");
            addTransitions(name, appendControlNumber("attch_ptB-", i), "0", "0");
            addTransitions(name, appendControlNumber("finA_attch-", i), "0", "0");
            addTransitions(name, appendControlNumber("finB_attch-", i), "0", "0");
        }
        return out;
    }

    public static List<String> generateHISCPolishPart(String nameIn, int num) {
        List<String> out = new ArrayList<String>();
        for(int i = 1; i < num + 1; i++) {
            String name = nameIn + "_" + i;
            out.add(name);

            generateSystemDefault(name);

            model.addStates(name, 3);

            initialState(name, "0");

            initiateEvents(name, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_POLISH_PART, i));

            addTransitions(name, appendControlNumber("start_pol-", i), "0", "1");
            addTransitions(name, appendControlNumber("dip_acid-", i), "1", "1");
            addTransitions(name, appendControlNumber("polish-", i), "1", "1");
            addTransitions(name, appendControlNumber("str_rlse-", i), "1", "2");
            addTransitions(name, appendControlNumber("compl_pol-", i), "2", "0");
        }
        return out;
    }

    public static List<String> generateHISCAttachCase(String nameIn, int num) {
        List<String> out = new ArrayList<String>();
        for(int i = 1; i < num + 1; i++) {
            String name = nameIn + "_" + i;
            out.add(name);

            generateSystemDefault(name);

            model.addStates(name, 3);

            initialState(name, "0");

            initiateEvents(name, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_ATTACH_CASE, i));

            addTransitions(name, appendControlNumber("start_case-", i), "0", "1");
            addTransitions(name, appendControlNumber("attch_case-", i), "1", "2");
            addTransitions(name, appendControlNumber("compl_case-", i), "2", "0");
        }
        return out;
    }

    public static List<String> generateHISCAttachPart(String nameIn, int num) {
        List<String> out = new ArrayList<String>();
        for(int i = 1; i < num + 1; i++) {
            String name = nameIn + "_" + i;
            out.add(name);

            generateSystemDefault(name);

            model.addStates(name, 5);

            initialState(name, "0");

            initiateEvents(name, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_ATTACH_PART, i));

            addTransitions(name, appendControlNumber("take_pt-", i), "0", "1");
            addTransitions(name, appendControlNumber("str_ptA-", i), "1", "2");
            addTransitions(name, appendControlNumber("str_ptB-", i), "1", "3");
            addTransitions(name, appendControlNumber("cmpl_A-", i), "2", "4");
            addTransitions(name, appendControlNumber("cmpl_B-", i), "3", "4");
            addTransitions(name, appendControlNumber("ret_pt-", i), "4", "0");
        }
        return out;
    }

    //-- Spec Parts  ------------------------------------------

    public static void generateHISCInBuff(String name) {
        generateSystemDefault(name);

        model.addStates(name, 5);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_HISC_INBUFF);

        addTransitions(name, "ret_inbuff", "0", "1", "1", "2", "2", "3", "3", "4");
        addTransitions(name, "new_part", "0", "1", "1", "2", "2", "3", "3", "4");

        addTransitions(name, "part_ent-I", "4", "3", "3", "2", "2", "1", "1", "0");
        addTransitions(name, "part_ent-II", "4", "3", "3", "2", "2", "1", "1", "0");
        addTransitions(name, "part_ent-III", "4", "3", "3", "2", "2", "1", "1", "0");

    }

    public static void generateHISCOutBuff(String name) {
        generateSystemDefault(name);

        model.addStates(name, 5);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_HISC_OUTBUFF);

        addTransitions(name, "part_ent-I", "0", "0", "1", "1");
        addTransitions(name, "part_ent-II", "0", "0", "1", "1");
        addTransitions(name, "part_ent-III", "0", "0", "1", "1");

        addTransitions(name, "fin_exit-I", "0", "1", "1", "2", "2", "3", "3", "4");
        addTransitions(name, "fin_exit-II", "0", "1", "1", "2", "2", "3", "3", "4");
        addTransitions(name, "fin_exit-III", "0", "1", "1", "2", "2", "3", "3", "4");

        addTransitions(name, "part_f_obuff", "4", "3", "3", "2", "2", "1", "1", "0");

    }

    public static void generateHISCPackage(String name) {
        generateSystemDefault(name);

        model.addStates(name, 5);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_HISC_PACKBUFF);

        addTransitions(name, "deposit_part", "0", "1", "1", "2", "2", "3", "3", "4");
        addTransitions(name, "take_item", "4", "3", "3", "2", "2", "1", "1", "0");
    }

    public static void generateHISCEnsure(String name) {

        generateSystemDefault(name);

        model.addStates(name, 5);

        initialState(name, "0");

        initiateEvents(name, EventSets.EVENT_LIST_HISC_ENSURE);

        addTransitions(name, "new_part", "0", "1", "1", "2", "2", "3", "3", "4");
        addTransitions(name, "part_passes", "4", "3", "3", "2", "2", "1", "1", "0");
    }

    //-- J Parts  -----------------------------------------

    public static List<String> generateHISCMoves(String nameIn, int num) {
        List<String> out = new ArrayList<String>();

        for(int i = 1; i < num + 1; i++) {
            String name = nameIn + "_" + i;
            out.add(name);

            generateSystemDefault(name);

            model.addStates(name, 2);

            initialState(name, "0");

            initiateEvents(name, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_MOVE, i));

            addTransitions(name, appendControlNumber("part_ent-", i), "0", "1");
            addTransitions(name, appendControlNumber("fin_exit-", i), "1", "0");
        }
        return out;

    }

    public static List<String> generateHISCPolishSequence(String nameIn, int num) {
        List<String> out = new ArrayList<String>();
        for(int i = 1; i < num + 1; i++) {
            String name = nameIn + "_" + i;
            out.add(name);

            generateSystemDefault(name);

            model.addStates(name, 6);

            initialState(name, "0");

            initiateEvents(name, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_POLISH_SEQUENCE, i));

            addTransitions(name, appendControlNumber("start_pol-", i), "0", "1");
            addTransitions(name, appendControlNumber("dip_acid-", i), "1", "2", "3", "4");
            addTransitions(name, appendControlNumber("polish-", i), "2", "3", "4", "5");
            addTransitions(name, appendControlNumber("str_rlse-", i), "5", "0");
        }
        return out;
    }

    public static List<String> generateHISCAffix(String nameIn, int num) {
        List<String> out = new ArrayList<String>();
        for(int i = 1; i < num + 1; i++) {
            String name = nameIn + "_" + i;
            out.add(name);

            generateSystemDefault(name);

            model.addStates(name, 11);

            initialState(name, "0");

            initiateEvents(name, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_AFFIX, i));

            addTransitions(name, appendControlNumber("str_ptA-", i), "7", "8");
            addTransitions(name, appendControlNumber("str_ptB-", i), "2", "3");
            addTransitions(name, appendControlNumber("attch_ptA-", i), "0", "6");
            addTransitions(name, appendControlNumber("attch_ptB-", i), "0", "1");
            addTransitions(name, appendControlNumber("cmpl_A-", i), "8", "9");
            addTransitions(name, appendControlNumber("cmpl_B-", i), "3", "4");
            addTransitions(name, appendControlNumber("finA_attch-", i), "10", "0");
            addTransitions(name, appendControlNumber("finB_attch-", i), "5", "0");
            addTransitions(name, appendControlNumber("take_pt-", i), "1", "2", "6", "7");
            addTransitions(name, appendControlNumber("ret_pt-", i), "4", "5", "9", "10");
        }
        return out;
    }

    public static List<String> generateHISCG(String nameIn, int num) {
        List<String> out = new ArrayList<String>();
        for(int i = 1; i < num + 1; i++) {
            String name = nameIn + "_" + i;
            out.add(name);

            generateSystemDefault(name);

            model.addStates(name, 5);

            initialState(name, "0");

            initiateEvents(name, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_G, i));

            addTransitions(name, appendControlNumber("start_pol-", i), "0", "1");
            addTransitions(name, appendControlNumber("compl_pol-", i), "1", "0");
            addTransitions(name, appendControlNumber("attch_ptA-", i), "0", "2");
            addTransitions(name, appendControlNumber("finA_attch-", i), "2", "0");
            addTransitions(name, appendControlNumber("attch_ptB-", i), "0", "3");
            addTransitions(name, appendControlNumber("finB_attch-", i), "3", "0");
            addTransitions(name, appendControlNumber("start_case-", i), "0", "4");
            addTransitions(name, appendControlNumber("compl_case-", i), "4", "0");
        }
        return out;
    }

    public static List<String> generateHISCSequence(String nameIn, int num) {
        List<String> out = new ArrayList<String>();
        for(int i = 1; i < num + 1; i++) {
            String name = nameIn + "_" + i;
            out.add(name);

            generateSystemDefault(name);

            model.addStates(name, 17);

            initialState(name, "0");

            initiateEvents(name, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_SEQUENCE, i));

            addTransitions(name, appendControlNumber("start_pol-", i), "2", "3");
            addTransitions(name, appendControlNumber("compl_pol-", i), "3", "4");
            addTransitions(name, appendControlNumber("start_case-", i), "12", "13");
            addTransitions(name, appendControlNumber("compl_case-", i), "13", "14");
            addTransitions(name, appendControlNumber("attch_ptA-", i), "6", "8");
            addTransitions(name, appendControlNumber("attch_ptB-", i), "7", "9");
            addTransitions(name, appendControlNumber("finA_attch-", i), "8", "10");
            addTransitions(name, appendControlNumber("finB_attch-", i), "9", "10");

            addTransitions(name, appendControlNumber("fin_exit-", i), "0", "0");
            addTransitions(name, appendControlNumber("part_ent-", i), "0", "1");

            addTransitions(name, appendControlNumber("recog_A-", i), "5", "6");
            addTransitions(name, appendControlNumber("recog_B-", i), "5", "7");

            addTransitions(name, appendControlNumber("part_arr1-", i), "1", "2", "15", "16");
            addTransitions(name, appendControlNumber("part_arr2-", i), "5", "5");
            addTransitions(name, appendControlNumber("part_arr3-", i), "11", "12");

            addTransitions(name, appendControlNumber("partLvExit-", i), "5", "5");
            addTransitions(name, appendControlNumber("part_lv1-", i), "4", "5", "16", "16");
            addTransitions(name, appendControlNumber("part_lv2-", i), "10", "11");
            addTransitions(name, appendControlNumber("part_lv3-", i), "14", "15");

            addTransitions(name, appendControlNumber("str_exit-", i), "16", "0");
        }
        return out;
    }

//---  Support Methods   ----------------------------------------------------------------------

    private static void generateSystemDefault(String name) {
        model.generateEmptyFSM(name);

        List<String> stateAtt = new ArrayList<>();
        List<String> eventAtt = new ArrayList<>();
        List<String> transAtt = new ArrayList<>();

        stateAtt.add(AttributeList.ATTRIBUTE_INITIAL);
        for(String s : EventSets.EVENT_ATTR_LIST) {
            eventAtt.add(s);
        }
        transAtt.add(AttributeList.ATTRIBUTE_BAD);

        model.assignStateAttributes(name, stateAtt);
        model.assignEventAttributes(name, eventAtt);
        model.assignTransitionAttributes(name, transAtt);

    }

    private static void initiateEvents(String name, String[] eventList, String ... controllables) {
        for(String s : eventList) {
            model.addEvent(name, s);
            model.setEventAttribute(name, s, AttributeList.ATTRIBUTE_OBSERVABLE, true);
        }
        for(String s : controllables) {
            model.setEventAttribute(name, s, AttributeList.ATTRIBUTE_CONTROLLABLE, true);
        }
    }

    private static void setBadTransitions(String name, String ... pairTrans) {
        for(int i = 0; i < pairTrans.length; i += 2) {
            model.setTransitionAttribute(name, pairTrans[i], pairTrans[i+1], AttributeList.ATTRIBUTE_BAD, true);
        }
    }

    private static void addTransitions(String name, String event, String ... statePairs) {
        for(int i = 0; i < statePairs.length; i += 2) {
            model.addTransition(name, statePairs[i], event, statePairs[i+1]);
        }
    }

    private static void initialState(String name, String state) {
        model.setStateAttribute(name, state, AttributeList.ATTRIBUTE_INITIAL, true);
    }

}
