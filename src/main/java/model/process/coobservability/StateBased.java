package model.process.coobservability;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import model.fsm.TransitionSystem;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.StateSet;
import model.process.memory.ConcreteMemoryMeasure;

public class StateBased extends ConcreteMemoryMeasure {

//---  Instance Variables   -------------------------------------------------------------------

    private static String attributeInitialRef;
    private static String attributeObservableRef;
    private static String attributeControllableRef;

    private Map<String, Set<StateSet>> disable;
    private Map<String, Set<StateSet>> enable;

    private Map<StateSet, List<List<StateSet>>> pathTracing;

//---  Constructors   -------------------------------------------------------------------------

    public StateBased(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Agent> agents, boolean pathTrack) {
        super();
        disable = new HashMap<String, Set<StateSet>>();
        enable = new HashMap<String, Set<StateSet>>();
        if(pathTrack) {
            pathTracing = new HashMap<StateSet, List<List<StateSet>>>();
        }
        operate(plants, specs, attr, agents);
    }

    public static void assignAttributeReference(String init, String obs, String cont) {
        attributeInitialRef = init;
        attributeObservableRef = obs;
        attributeControllableRef = cont;
    }

//---  Operations   ---------------------------------------------------------------------------

    public boolean isSBCoobservable() {
        //printEnableDisableSets();
        for(String c : disable.keySet()) {
            if(!disable.get(c).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void operate(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Agent> agen) {
        Set<String> eventNamesHold = new HashSet<String>();
        Set<String> controllable = new HashSet<String>();

        for(Agent a : agen) {
            controllable.addAll(a.getEventsAttributeSet(attributeControllableRef, true));
        }

        for(TransitionSystem t : plants) {
            eventNamesHold.addAll(t.getEventNames());
        }

        List<String> eventNames = new ArrayList<>();
        eventNames.addAll(eventNamesHold);

        initializeEnableDisable(disable, enable, plants, specs, controllable);

        logMemoryUsage();

        boolean giveUp = false;

        for(String s : eventNames) {
            if(!controllable.contains(s) && disable.containsKey(s)) {
                giveUp = true;
            }
        }

        if(giveUp) {
            return;
        }

        //printEnableDisableSets();

        boolean pass = true;

        for(String e : controllable) {
            if(!disable.get(e).isEmpty()) {
                pass = false;
            }
        }

        if(pass) {
            return;    //false
        }

        for(Agent a : agen) {
            boolean skip = true;

            for(String e : controllable) {
                Boolean canControl = a.getEventAttribute(e, attributeControllableRef);
                if(canControl && !disable.get(e).isEmpty()) {
                    skip = false;
                }
            }

            if(skip) {
                continue;
            }

            Map<String, Set<StateSet>> tempDisable = observerConstructHiding(plants, specs, enable, disable, a.getEventsAttributeSet(attributeObservableRef, true), a.getEventsAttributeSet(attributeControllableRef, true), controllable);

            logMemoryUsage();

            pass = true;

            for(String c : controllable) {
                if(!tempDisable.get(c).isEmpty()) {
                    pass = false;
                }
            }
            disable = tempDisable;
            if(pass) {
                return;    //true
            }

        }
    }

    private void initializeEnableDisable(Map<String, Set<StateSet>> disable, Map<String, Set<StateSet>> enable, List<TransitionSystem> plants, List<TransitionSystem> specs, Set<String> controllable) {
        for(String c : controllable) {
            disable.put(c, new HashSet<StateSet>());
            enable.put(c, new HashSet<StateSet>());
        }
        StateSet.assignSizes(plants.size(), specs.size());

        Queue<StateSet> queue = new ArrayDeque<>();
        Set<StateSet> visited = new HashSet<>();
        StateSet first = initialStateSetPath(plants, specs);

        queue.add(first);
        while(!queue.isEmpty()) {
            StateSet curr = queue.poll();
            if(visited.contains(curr)) {
                continue;
            }
            visited.add(curr);
            boolean bail = true;
            for(String s : getAllEvents(plants)) {
                if(canProceed(plants, null, curr, s)) {
                    boolean specCan = canProceed(null, specs, curr, s);
                    if(specCan) {
                        StateSet next = stateSetStep(plants, specs, curr, s);
                        queue.add(next);
                    }
                    if(controllable.contains(s)) {
                        bail = false;
                        if(specCan) {
                            enable.get(s).add(curr);
                        }
                        else {
                            disable.get(s).add(curr);
                        }
                    }
                    else if(!specCan) {
                        //So what do we do here, when a transition is not in the specification but its event is not controllable.
                        //Causing a hard fail here should be fine? False negative won't contradict
                        //Except it only retains stuff it is confused about because it's an asinine system...
                        if(disable.get(s) == null) {
                            disable.put(s,  new HashSet<StateSet>());
                        }
                        disable.get(s).add(curr);
                    }
                }

            }
            if(pathTracing != null && bail) {
                pathTracing.remove(curr);
            }
        }
    }

    /*
     * Need a structure that maps each <state, event path> tuple consisting of a disablement decision state to a sequence of sets of states consisting of the enablement decision states that that controller confuses for
     * the original disablement decision state.
     * 
     * <state, event path> -> {{<state, event path>, <state, event path>}, {<state, event path>, <state, event path>}}
     * 
     * So anytime we see confusion between a disablement state and any enablement states, all versions of the disablement state need to map to all enablement states that controller also reached.
     * 
     * 
     * 
     */

    private Map<String, Set<StateSet>> observerConstructHiding(List<TransitionSystem> plants, List<TransitionSystem> specs, Map<String, Set<StateSet>> enable, Map<String, Set<StateSet>> disable, List<String> agentObs, List<String> agentCont, Set<String> controllable) {
        Map<String, Set<StateSet>> out = new HashMap<String, Set<StateSet>>();

        for(String c : controllable) {
            out.put(c, agentCont.contains(c) ? new HashSet<StateSet>() : disable.get(c));
        }

        Queue<Set<StateSet>> queue = new ArrayDeque<>();
        Set<Set<StateSet>> visited = new HashSet<>();

        Set<StateSet> initial = new HashSet<StateSet>();
        initial.add(initialStateSetPath(plants, specs));

        queue.add(reachableStateSetPaths(plants, specs, initial, agentObs));
        while(!queue.isEmpty()) {
            Set<StateSet> curr = queue.poll();
            if(visited.contains(curr)) {
                continue;
            }
            visited.add(curr);

            for(String e : agentObs) {
                Set<StateSet> reachable = new HashSet<StateSet>();
                for(StateSet s : curr) {
                    if(canProceed(plants, specs, s, e)) {
                        reachable.add(stateSetStep(plants, specs, s, e));
                    }
                }
                queue.add(reachableStateSetPaths(plants, specs, reachable, agentObs));
                logMemoryUsage();
            }
        }

        logMemoryUsage();
        for(Set<StateSet> group : visited) {
            for(String e : agentCont) {
                Set<StateSet> holdEna = intersection(group, enable.get(e));
                if(!holdEna.isEmpty()) {
                    Set<StateSet> holdDis = intersection(group, disable.get(e));
                    out.get(e).addAll(holdDis);
                }
            }
        }

        return out;
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public List<List<StateSet>> getEquivalentPaths(StateSet in){
        return pathTracing == null ? null : pathTracing.get(in);
    }

    private String getInitialState(TransitionSystem t) {
        return t.getStatesWithAttribute(attributeInitialRef).get(0);
    }

    private List<String> getAllEvents(List<TransitionSystem> plants){
        Set<String> hold = new HashSet<String>();

        for(TransitionSystem t : plants) {
            hold.addAll(t.getEventNames());
        }

        List<String> out = new ArrayList<String>();
        out.addAll(hold);
        return out;
    }

    public List<StateSet> getRemainingDisableStates(){
        List<StateSet> out = new ArrayList<StateSet>();
        Set<StateSet> use = new HashSet<StateSet>();
        for(String c : disable.keySet()) {
            use.addAll(disable.get(c));
        }
        out.addAll(use);
        return out;
    }

    public List<StateSet> getRemainingEnableStates(){
        List<StateSet> out = new ArrayList<>();
        Set<StateSet> use = new HashSet<>();
        for(String c : enable.keySet()) {
            use.addAll(enable.get(c));
        }
        out.addAll(use);
        return out;
    }

//---  Support Methods   ----------------------------------------------------------------------

    private StateSet initialStateSetPath(List<TransitionSystem> plants, List<TransitionSystem> specs) {
        String[] use = new String[plants.size() + specs.size()];
        for(int i = 0; i < plants.size(); i++) {
            use[i] = getInitialState(plants.get(i));
        }
        for(int i = 0; i < specs.size(); i++) {
            use[i + plants.size()] = getInitialState(specs.get(i));
        }
        return new StateSet(use);
    }

    private StateSet stateSetStep(List<TransitionSystem> plants, List<TransitionSystem> specs, StateSet curr, String event) {
        String[] out = new String[plants.size() + specs.size()];

        for(int i = 0; i < plants.size(); i++) {
            TransitionSystem t = plants.get(i);
            out[i] = knowsEvent(t, event) ? t.getStateEventTransitionStates(curr.getPlantState(i), event).get(0) : curr.getPlantState(i);
        }

        for(int i = 0; i < specs.size(); i++) {
            TransitionSystem t = specs.get(i);
            out[i + plants.size()] = canPerformEvent(t, curr.getSpecState(i), event) ? t.getStateEventTransitionStates(curr.getSpecState(i), event).get(0) : curr.getSpecState(i);
        }
        //System.out.println(curr.toString() + " " + event + " " + Arrays.toString(out) + " " + curr.getEventPath());
        StateSet use = new StateSet(out);
        return use;
    }

    private Set<StateSet> reachableStateSetPaths(List<TransitionSystem> plants, List<TransitionSystem> specs, Set<StateSet> initial, List<String> agentObs){
        Set<StateSet> out = new HashSet<>(initial);
        List<String> unobs = new ArrayList<>();
        for(String s : getAllEvents(plants)) {
            if(!agentObs.contains(s)) {
                unobs.add(s);
            }
        }

        Queue<StateSet> queue = new ArrayDeque<>();
        Set<StateSet> visited = new HashSet<>();
        queue.addAll(initial);

        while(!queue.isEmpty()) {
            StateSet curr = queue.poll();

            if(visited.contains(curr)) {
                continue;
            }
            visited.add(curr);

            for(String e : unobs) {
                if(canProceed(plants, specs, curr, e)) {
                    queue.add(stateSetStep(plants, specs, curr, e));
                }
            }

        }
        return visited;
    }

    private boolean knowsEvent(TransitionSystem system, String event) {
        return system.getEventNames().contains(event);
    }

    private boolean canPerformEvent(TransitionSystem system, String state, String event) {
        return knowsEvent(system, event) && system.getStateTransitionEvents(state).contains(event);
    }

    private boolean canProceed(List<TransitionSystem> plants, List<TransitionSystem> specs, StateSet curr, String event) {
        if(plants != null) {
            for(int i = 0; i < plants.size(); i++) {
                TransitionSystem t = plants.get(i);
                if(knowsEvent(t, event) && !canPerformEvent(t, curr.getPlantState(i), event)){
                    return false;
                }
            }
        }
        if(specs != null) {
            for(int i = 0; i < specs.size(); i++) {
                TransitionSystem t = specs.get(i);
                if(knowsEvent(t, event) && !canPerformEvent(t, curr.getSpecState(i), event)){
                    return false;
                }
            }
        }
        return true;
    }

    private void printEnableDisableSets() {
        System.out.println("Enable/Disable Sets:\nEnable: " + enable + "\nDisable: " + disable + "\n");
    }

    //-- SubsetConstructHiding  ---------------------------------------------------------------

    private Set<StateSet> intersection(Set<StateSet> conglom, Set<StateSet> check){
        Set<StateSet> out = new HashSet<>();
        for(StateSet s : check) {
            if(conglom.contains(s)) {
                out.add(s);
            }
        }
        return out;
    }

}
