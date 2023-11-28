package help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import model.AttributeList;
import model.Manager;

public class RandomGeneration {

//---  Constant Values   ----------------------------------------------------------------------

    private static final int TRANSITION_NUMBER_DEFAULT = 3;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

//---  Static Preparation   -------------------------------------------------------------------

    public static void setupRandomFSMConditions(Manager model, int numObsEve, int numContrEve, int badTran) {
        List<String> stateAtt = new ArrayList<>();
        List<String> eventAtt = new ArrayList<>();
        List<String> transAtt = new ArrayList<>();

        stateAtt.add(AttributeList.ATTRIBUTE_INITIAL);
        for(String s : EventSets.EVENT_ATTR_LIST) {
            eventAtt.add(s);
        }
        transAtt.add(AttributeList.ATTRIBUTE_BAD);

        List<Integer> stat = new ArrayList<>();

        stat.add(1);

        model.assignRandomFSMStateConfiguration(stateAtt, stat);

        List<Integer> even = new ArrayList<>();

        even.add(numObsEve);
        even.add(numContrEve);

        model.assignRandomFSMEventConfiguration(eventAtt, even);

        List<Integer> tran = new ArrayList<>();

        tran.add(badTran);

        model.assignRandomFSMTransitionConfiguration(transAtt, tran);
    }

    public static void setupRandomFSMDefaultEvents(Manager model, List<String> defaultEvents) {
        model.assignRandomFSMDefaultEventSet(defaultEvents);
    }

//---  Operations   ---------------------------------------------------------------------------

    /**
     *
     * @param nom
     * @param model
     * @param numStates
     * @param numEvents
     * @param numTransition
     * @param numObsEve
     * @param numContrEve
     * @param badTran
     * @return
     */

    public static String generateRandomFSM(String nom, Manager model, int numStates, int numEvents, int numTransition, boolean accessible)  {
        String out;
        do {
            model.removeFSM(nom);
            out = model.readInFSM(model.generateRandomFSM(nom, numStates, numEvents, numTransition, true));
        }while(!accessible || !model.isAccessible(out));
        return out;
    }

    /**
     *
     * Total event set size will be 1/2 # of plants + specs * eventSizeAverage
     *
     * EventShareRate is proportional to how many other components already contain an event? Or for how many events for a component are shared by others tempered by the prevalence of its events. Hmm...
     *
     * OR: each component has x number of events, and during its setup it decides whether to pull events from other existing components.
     *
     * Can eventually integrate 'trends' where some components share a lot of events? Specs need to only have events found in plants, with high share-rate.
     *
     * Given conditions, create the plants and specs
     *  - States and transitions are independent of other components, but need intentional overlap of events
     *  - Specs should only use alphabet defined in plants; true random or share events with specific plant components?
     * Make Agents wrt the events previously generated
     *
     * TODO: Output results and image whenever we run a test with this, don't want to lose it
     *
     * @param prefixNom
     * @param model
     * @param numPlants
     * @param numSpecs
     * @param stateSizeAverage
     * @param stateVariance
     * @param eventSizeAverage
     * @param eventVariance
     * @param eventShareRate
     */
    public static List<String> generateRandomSystemSet(String prefixNom, Manager model, RandomGenStats info)  {
        Random rand = new Random();
        Map<String, List<String>> plantEvents = new HashMap<>();

        setupRandomFSMConditions(model, 0, 0, 0);

        List<String> out = new ArrayList<String>();

        int numPlants = info.getNumPlants();
        int stateSizeAverage = info.getNumStates();
        int stateVariance = info.getNumStatesVar();
        int eventSizeAverage = info.getNumEvents();
        int eventVariance = info.getNumEventsVar();
        double eventShareRate = info.getEventShareRate();
        int numSpecs = info.getNumSpecs();

        for(int i = 0; i < numPlants; i++) {
            int numStates = stateSizeAverage + (rand.nextInt(stateVariance * 2 + 1) - (stateVariance));
            int numEvents = eventSizeAverage + (rand.nextInt(eventVariance * 2 + 1) - (eventVariance));
            int numTransitions = TRANSITION_NUMBER_DEFAULT;

            int numBorrowed = 0;
            for(int j = 0; j < numEvents - 1; j++) {
                if(!plantEvents.keySet().isEmpty() && rand.nextDouble() < eventShareRate)
                    numBorrowed++;
            }
            numBorrowed = numBorrowed > out.size() ? out.size() : numBorrowed;

            List<String> events = getPlantEvents(numEvents - numBorrowed, String.valueOf(ALPHABET.charAt(i)), configureName(prefixNom, i, true));
            plantEvents.put(configureName(prefixNom, i, true), (List<String>) new ArrayList<>(events));
            out.addAll(events);

            for(int j = 0; j < numBorrowed; j++) {
                int select = rand.nextInt(i);
                List<String> choices = plantEvents.get(configureName(prefixNom, select, true));
                String choice = choices.get(rand.nextInt(choices.size()));
                if(!events.contains(choice)) {
                    events.add(choice);
                }
                else {
                    j--;
                }
            }

            setupRandomFSMConditions(model, events.size(), 0, 0);
            setupRandomFSMDefaultEvents(model, events);
            generateRandomFSM(configureName(prefixNom, i, true), model, numStates, numEvents, numTransitions, true);
        }

        for(int i = 0; i < numSpecs; i++) {
            int numStates = stateSizeAverage + (rand.nextInt(stateVariance * 2 + 1) - (stateVariance));
            int numEvents = eventSizeAverage + (rand.nextInt(eventVariance * 2 + 1) - (eventVariance));
            int numTransitions = TRANSITION_NUMBER_DEFAULT;

            List<String> events = new ArrayList<String>();
            while(events.size() < numEvents && events.size() != out.size()) {
                List<String> pull = plantEvents.get(configureName(prefixNom, rand.nextInt(numPlants), true));
                String even = pull.get(rand.nextInt(pull.size()));
                if(!events.contains(even))
                    events.add(even);
            }
            setupRandomFSMConditions(model, events.size(), 0, 0);
            setupRandomFSMDefaultEvents(model, events);

            generateRandomFSM(configureName(prefixNom, i, false), model, numStates, numEvents, numTransitions, true);
        }

        return out;
    }

    public static List<Map<String, List<Boolean>>> generateRandomAgents(List<String> events, RandomGenStats info){
        Random rand = new Random();
        int agentSizeVariance = info.getNumControllersVar();
        int numAgents = info.getNumControllers() + (agentSizeVariance == 0 ? 0 : (rand.nextInt(agentSizeVariance * 2 + 1) - (agentSizeVariance)));
        boolean[][][] agentInfo = new boolean[numAgents][events.size()][2];
        for(int i = 0; i < numAgents; i++) {
            boolean hasControl = false;
            for(int j = 0; j < events.size(); j++) {
                agentInfo[i][j][0] = rand.nextDouble() < info.getControllerObserveRate();
                agentInfo[i][j][1] = rand.nextDouble() < info.getControllerControlRate();
                if(agentInfo[i][j][1]) {
                    hasControl = true;
                }
            }
            if(!hasControl) {
                agentInfo[i][rand.nextInt(events.size())][1] = true;
            }
        }
        String[] evens = events.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        return AgentChicanery.generateAgentSet(agentInfo, evens);
    }

//---  Getter Functions   ---------------------------------------------------------------------

    public static List<String> getComponentNames(String prefixNom, int numPlants, int numSpecs){
        List<String> out = new ArrayList<>();
        out.addAll(getPlantNames(prefixNom, numPlants));
        out.addAll(getSpecNames(prefixNom, numSpecs));
        return out;
    }

    public static List<String> getPlantNames(String prefixNom, int numPlants){
        List<String> out = new ArrayList<>();
        for(int i = 0; i < numPlants; i++) {
            out.add(configureName(prefixNom, i, true));
        }
        return out;
    }

    public static List<String> getSpecNames(String prefixNom, int numSpecs){
        List<String> out = new ArrayList<>();
        for(int i = 0; i < numSpecs; i++) {
            out.add(configureName(prefixNom, i, false));
        }
        return out;
    }

//---  Helper Functions   ---------------------------------------------------------------------

    private static List<String> getPlantEvents(int numEvents, String eventChar, String plantName){
        List<String> events = new ArrayList<>();
        for(int j = 0; j < numEvents; j++) {
            events.add(eventChar + "_{" + j + "}");
        }
        return events;
    }

    private static String configureName(String prefix, int num, boolean plant) {
        return prefix + (plant ? "_p_" : "_s_") + num;
    }

}
