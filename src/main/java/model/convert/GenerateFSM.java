package model.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * This class is used to generate files readable as Finite State Machines to the constructors
 * that accept file input from the classes in the fsm package. It does so randomly with no
 * oversight to handle strange productions.
 * 
 * The class creates a file in a location defined by a class constant and named howsoever the
 * user of this class wants; any usage of the class' production is then done by accessing that
 * location in your file system, given as a String returned by this method or by directly accessing
 * your file system yourself.
 * 
 * Important note: Must write to file the following:
 * # of special types
 * # of elements of special type 'n'
 *     - the elements
 *  - repeat for all special types
 * All transitions (State, State, Event)
 * 
 * This class is a part of the support package.
 * 
 * @author Ada Clevinger and Graeme Zinck
 *
 */

public class GenerateFSM {

//--- Constant Values  ------------------------------------------------------------------------

    /** String constant referenced for consistent naming practices of States*/
    private static final String ALPHABET_STATE = "0123456789";
    /** String constants referenced for consistent naming practices of Events*/
    private static final String ALPHABET_EVENT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static int MAX_PERCENTAGE_VALUE = 100;

    private static String SEPARATOR;
    private static String REGION_SEPARATOR;
    private static String TRUE_SYMBOL;
    private static String FALSE_SYMBOL;

//---  Instance Variables   -------------------------------------------------------------------

    private static List<String> stateAttributes;
    private static List<Integer> stateNumbers;
    private static List<String> eventAttributes;
    private static List<Integer> eventNumbers;
    private static List<String> transitionAttributes;
    private static List<Integer> transitionNumbers;

    private static List<String> defaultStateSet;
    private static List<String> defaultEventSet;

    /** Private constructor */
    private GenerateFSM() {
    }

//---  Static Assignment   --------------------------------------------------------------------

    public static void assignStateAttributes(List<String> in, List<Integer> amounts) {
        stateAttributes = in;
        stateNumbers = amounts;
    }

    public static void assignEventAttributes(List<String> in, List<Integer> amounts) {
        eventAttributes = in;
        eventNumbers = amounts;
    }

    public static void assignTransitionAttributes(List<String> in, List<Integer> amounts) {
        transitionAttributes = in;
        transitionNumbers = amounts;
    }

    public static void assignDefaultStateSet(List<String> in) {
        defaultStateSet = in;
    }

    public static void wipeDefaultStateSet() {
        defaultStateSet = null;
    }

    public static void assignDefaultEventSet(List<String> in) {
        defaultEventSet = in;
    }

    public static void wipeDefaultEventSet() {
        defaultEventSet = null;
    }

//---  Operations   ---------------------------------------------------------------------------

    /**
     * The produced FSM object will be randomized according to the bounds described by the provided arguments.
     * 
     * Arguments for sizeStates and sizeEvents are ignored in the case that default sets have been assigned to the GenerateFSM class.
     * 
     * @param name
     * @param sizeStates
     * @param sizeEvents
     * @param sizeTrans
     * @param nonDet
     * @return
     */

    public static String createNewFSM(String name, int sizeStates, int sizeEvents, int sizeTrans, boolean det) {
        if(stateAttributes == null || eventAttributes == null || transitionAttributes == null) {
            throw new IllegalStateException("Error: FSM Attribute Component Not Defined; Check State/Event/Transition Attribute Assignment");
        }
        StringBuilder out = new StringBuilder();
        out.append(name + StringUtils.LF);
        out.append(REGION_SEPARATOR + StringUtils.LF);
        writeAttribute(out, stateAttributes);
        writeAttribute(out, eventAttributes);
        writeAttribute(out, transitionAttributes);
        out.append(REGION_SEPARATOR + StringUtils.LF);

        Random rand = new Random();

        Map<Integer, String> stateNames = defaultStateSet == null ? writeComponentGenerative(out, rand, sizeStates, stateAttributes, stateNumbers, true) : writeComponentDefaultSet(out, rand, defaultStateSet, stateAttributes, stateNumbers);

        Map<Integer, String> eventNames = defaultEventSet == null ? writeComponentGenerative(out, rand, sizeEvents, eventAttributes, eventNumbers, false) : writeComponentDefaultSet(out, rand, defaultEventSet, eventAttributes, eventNumbers);

        writeTransitions(out, rand, stateNames, eventNames, sizeTrans, det);
        return out.toString();
    }

    private static Map<Integer, String> writeComponentGenerative(StringBuilder out, Random rand, int sizeComponent, List<String> attributes, List<Integer> numbers, boolean stateNames) {
        List<Integer> track = new ArrayList<>();
        for(Integer i : numbers) {
            track.add(0);
        }
        Map<Integer, String> nameMapping = new HashMap<>();
        for(int i = 0; i < sizeComponent; i++) {
            String name = generateName(i, stateNames);
            nameMapping.put(i, name);
            String line = name + writeAttributes(getRandomValue(rand), sizeComponent, i, attributes, numbers, track);
            out.append(line + StringUtils.LF);
        }
        out.append(REGION_SEPARATOR + StringUtils.LF);
        return nameMapping;
    }

    private static Map<Integer, String> writeComponentDefaultSet(StringBuilder out, Random rand, List<String> components, List<String> attributes, List<Integer> numbers){
        List<Integer> track = new ArrayList<Integer>();
        for(Integer i : numbers) {
            track.add(0);
        }
        Map<Integer, String> nameMapping = new HashMap<Integer, String>();
        for(int i = 0; i < components.size(); i++) {
            String name = components.get(i);
            nameMapping.put(i, name);
            String line = name + writeAttributes(getRandomValue(rand), components.size(), i, attributes, numbers, track);
            out.append(line + StringUtils.LF);
        }
        out.append(REGION_SEPARATOR + StringUtils.LF);
        return nameMapping;
    }

    private static StringBuilder writeTransitions(StringBuilder out, Random rand, Map<Integer, String> stateNames, Map<Integer, String> eventNames, int sizeTrans, boolean isDet) {
        List<Integer> track = new ArrayList<>();
        for(Integer i : transitionNumbers) {
            track.add(0);
        }
        int sizeStates = stateNames.keySet().size();
        int sizeEvents = eventNames.keySet().size();
        List<Integer> numTransPerState = new ArrayList<>();
        for(int i = 0; i < sizeStates; i++) {
            numTransPerState.add(rand.nextInt(sizeTrans) + 1);
        }
        Set<Integer> usedEvents = new HashSet<>();
        Map<Integer, Set<Integer>> detMap = new HashMap<>();
        for(int i = 0; i < sizeStates; i++) {
            detMap.put(i, new HashSet<Integer>());
        }
        for(int i = 0; i < sizeStates; i++) {
            int numTr = numTransPerState.get(i);
            for(int j = 0; j < numTr; j++) {
                int state1 = i;
                int state2 = rand.nextInt(sizeStates);
                int event = rand.nextInt(sizeEvents);
                int count = 0;
                boolean order = rand.nextBoolean();
                while(isDet && detMap.get(order ? state1 : state2).contains(event) && detMap.get(order ? state1 : state2).size() < sizeEvents && count < (10 * sizeEvents)) {
                    event = rand.nextInt(sizeEvents);
                    count++;
                }
                if(isDet && detMap.get(order ? state1 : state2).contains(event)) {
                    continue;
                }
                detMap.get(order ? state1 : state2).add(event);
                usedEvents.add(event);
                String line = stateNames.get(order ? state1 : state2) + SEPARATOR + eventNames.get(event) + SEPARATOR + stateNames.get(order ? state2 : state1);
                //TODO: Examine use of sizeTrans here for proportion of transition attributes
                line += writeAttributes(getRandomValue(rand), numTransPerState.size(), i, transitionAttributes, transitionNumbers, track);
                out.append(line + StringUtils.LF);
            }
        }
        //System.out.println(eventNames);
        while(usedEvents.size() < eventNames.size()) {
            int event = 0;
            while(usedEvents.contains(event)) {
                event++;
            }
            int state1 = rand.nextInt(sizeStates);
            int state2 = rand.nextInt(sizeStates);
            String line = stateNames.get(state1) + SEPARATOR + eventNames.get(event) + SEPARATOR + stateNames.get(state2);
            line += writeAttributes(getRandomValue(rand), numTransPerState.size(), state1, transitionAttributes, transitionNumbers, track);
            out.append(line + StringUtils.LF);
            usedEvents.add(event);
        }
        return out;
    }

//---  Setter Methods   -----------------------------------------------------------------------

    public static void assignConstants(String separator, String regionSeparator, String trueSymbol, String falseSymbol) {
        SEPARATOR = separator;
        REGION_SEPARATOR = regionSeparator;
        TRUE_SYMBOL = trueSymbol;
        FALSE_SYMBOL = falseSymbol;
    }

//---  Support Methods   ----------------------------------------------------------------------

    private static String generateName(int i, boolean character) {
        String out = "";
        String language = character ? ALPHABET_STATE : ALPHABET_EVENT;
        do {
            int use = i % (language.length());
            out = language.charAt(use) + out;
            i /= language.length();
        }while(i > 0);

        return out;
    }

    private static int getRandomValue(Random rand) {
        return rand.nextInt(MAX_PERCENTAGE_VALUE);
    }

    private static String writeAttributes(int rand, int size, int index, List<String> attri, List<Integer> numbers, List<Integer> track) {
        String line = "";
        for(int j = 0; j < attri.size(); j++) {
            int prop = MAX_PERCENTAGE_VALUE * numbers.get(j) / size;
            boolean result = (track.get(j) < numbers.get(j) && (rand <= prop || size - index == numbers.get(j) - track.get(j)));
            line += SEPARATOR + (result ? TRUE_SYMBOL : FALSE_SYMBOL);
            track.set(j, track.get(j) + (result ? 1 : 0));
        }
        return line;
    }

    private static void writeAttribute(StringBuilder out, List<String> attri) {
        for(int i = 0; i < attri.size(); i++) {
            out.append(attri.get(i) + (i + 1 < attri.size() ? SEPARATOR : StringUtils.EMPTY));
        }
        out.append(SEPARATOR + StringUtils.LF);
    }

}
