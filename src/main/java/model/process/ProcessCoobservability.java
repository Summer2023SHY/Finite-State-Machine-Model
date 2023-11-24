package model.process;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.fsm.TransitionSystem;
import model.process.coobservability.Incremental;
import model.process.coobservability.StateBased;
import model.process.coobservability.UStructure;
import model.process.coobservability.deciding.DecideCoobs;
import model.process.coobservability.deciding.DecideInfCoobs;
import model.process.coobservability.deciding.DecideSBCoobs;
import model.process.coobservability.support.Agent;
import model.process.memory.ReceiveMemoryMeasure;

public class ProcessCoobservability {

//---  Instance Variables   -------------------------------------------------------------------

    private static String badTransRef;
    private static ReceiveMemoryMeasure memoryRecipient;

//---  Meta   ---------------------------------------------------------------------------------

    public static void assignReferences(ReceiveMemoryMeasure rmm, String cont, String obs, String init, String badTrans, String goodThing) {
        memoryRecipient = rmm;
        badTransRef = badTrans;
        UStructure.assignAttributeReferences(init, obs, cont, badTrans, goodThing);
        StateBased.assignAttributeReference(init, obs, cont);
        Incremental.assignAttributeReference(obs, init, badTrans);
        DecideCoobs.assignAttributeReferences(init, cont, obs, badTrans);
        DecideSBCoobs.assignAttributeReferences(obs);
    }

    public static void assignIncrementalOptions(int a, int b, int c) {
        Incremental.assignIncrementalOptions(a, b, c);
    }

    public static void assignEndAtFirstCounterexample(boolean in) {
        DecideCoobs.assignEndAtFirstCounterexample(in);
    }

//---  Operations   ---------------------------------------------------------------------------

    //-- Coobservable  ----------------------------------------

    public static boolean isCoobservableUStruct(TransitionSystem plant, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        DecideCoobs dC = new DecideCoobs(plant, attr, constructAgents(plant.getEventNames(), attr, agents));
        boolean out = dC.decideCondition();
        memoryRecipient.assignMemoryMeasure(dC.produceMemoryMeasure());
        return out;
    }

    public static boolean isCoobservableUStruct(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        List<Agent> age = constructAgents(getAllEvents(plants, specs), attr, agents);
        DecideCoobs dC = new DecideCoobs(plants, specs, attr, age);
        boolean out = dC.decideCondition();
        memoryRecipient.assignMemoryMeasure(dC.produceMemoryMeasure());
        return out;
    }

    public static boolean isInferenceCoobservableUStruct(TransitionSystem plant, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        DecideInfCoobs dC = new DecideInfCoobs(plant, attr, constructAgents(plant.getEventNames(), attr, agents));
        boolean out = dC.decideCondition();
        memoryRecipient.assignMemoryMeasure(dC.produceMemoryMeasure());
        return out;
    }

    public static boolean isInferenceCoobservableUStruct(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        List<Agent> age = constructAgents(getAllEvents(plants, specs), attr, agents);
        DecideInfCoobs dC = new DecideInfCoobs(plants, specs, attr, age);
        boolean out = dC.decideCondition();
        memoryRecipient.assignMemoryMeasure(dC.produceMemoryMeasure());
        return out;
    }

    //-- SB Coobservable  -------------------------------------

    public static boolean isSBCoobservable(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        List<Agent> age = constructAgents(getAllEvents(plants, specs), attr, agents);
        DecideSBCoobs dC = new DecideSBCoobs(plants, specs, attr, age);
        boolean out = dC.decideCondition();
        memoryRecipient.assignMemoryMeasure(dC.produceMemoryMeasure());
        return out;
    }

    //-- Incremental  -----------------------------------------

    /**
     * 
     * TODO: Technically would want an inferencing and non-inferencing version of this down the line
     * 
     * @param plants
     * @param specs
     * @param attr
     * @param agents
     * @return
     */

    public static boolean isIncrementalCoobservable(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        List<Agent> age = constructAgents(getAllEvents(plants, specs), attr, agents);
        Incremental inc = new Incremental(new DecideCoobs());
        memoryRecipient.assignMemoryMeasure(inc);
        return inc.decideIncrementalCondition(plants, specs, attr, age);
    }

    public static boolean isIncrementalInferenceCoobservable(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        List<Agent> age = constructAgents(getAllEvents(plants, specs), attr, agents);
        Incremental inc = new Incremental(new DecideInfCoobs());
        memoryRecipient.assignMemoryMeasure(inc);
        return inc.decideIncrementalCondition(plants, specs, attr, age);
    }

    public static boolean isIncrementalSBCoobservable(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        List<Agent> age = constructAgents(getAllEvents(plants, specs), attr, agents);
        Incremental inc = new Incremental(new DecideSBCoobs(true));
        memoryRecipient.assignMemoryMeasure(inc);
        return inc.decideIncrementalCondition(plants, specs, attr, age);
    }

    //-- Support  ---------------------------------------------

    public static TransitionSystem convertSoloPlantSpec(TransitionSystem plant) {
        TransitionSystem out = plant.copy();

        for(String s : plant.getStateNames()) {
            for(String e : plant.getStateTransitionEvents(s)) {
                if(plant.getTransitionAttribute(s, e, badTransRef)) {
                    for(String t : plant.getStateEventTransitionStates(s, e)) {
                        out.removeTransition(s, e, t);
                    }
                }
            }
        }
        return out;
    }

    public static UStructure constructUStruct(TransitionSystem plant, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        UStructure u = new UStructure(plant, attr, constructAgents(plant.getEventNames(), attr, agents));
        memoryRecipient.assignMemoryMeasure(u);
        return u;
    }

    public static UStructure constructUStruct(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        List<Agent> age = constructAgents(getAllEvents(plants, specs), attr, agents);
        DecideCoobs dC = new DecideCoobs(plants, specs, attr, age);
        dC.decideCondition();
        memoryRecipient.assignMemoryMeasure(dC.produceMemoryMeasure());
        return dC.getUStructure();
    }

    public static UStructure constructUStructRaw(TransitionSystem plant, List<String> attr, List<Agent> agents) {
        UStructure u = new UStructure(plant, attr, agents);
        memoryRecipient.assignMemoryMeasure(u);
        return u;
    }

    //-- Helper  ----------------------------------------------

    private static List<String> getAllEvents(List<TransitionSystem> plants, List<TransitionSystem> specs){
        List<String> out = new ArrayList<String>();
        Set<String> hold = new HashSet<String>();
        for(TransitionSystem aT : plants) {
            hold.addAll(aT.getEventNames());
        }
        for(TransitionSystem aT : specs) {
            hold.addAll(aT.getEventNames());
        }
        out.addAll(hold);
        return out;
    }

//---  Support Methods   ----------------------------------------------------------------------

    private static List<Agent> constructAgents(List<String> event, List<String> attr, List<Map<String, List<Boolean>>> agents){
        List<Agent> agen = new ArrayList<Agent>();

        for(Map<String, List<Boolean>> h : agents) {
            Agent a = new Agent(attr, event);
            for(String s : event) {
                if(h.get(s) == null) {
                    continue;
                }
                for(int i = 0; i < attr.size(); i++) {
                    Boolean b = h.get(s).get(i);
                    if(b)
                        a.setAttribute(attr.get(i), s, true);
                }
            }
            agen.add(a);
        }
        return agen;
    }

}
