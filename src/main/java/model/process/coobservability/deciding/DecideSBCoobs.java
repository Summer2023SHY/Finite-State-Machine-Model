package model.process.coobservability.deciding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.fsm.TransitionSystem;
import model.process.ProcessDES;
import model.process.coobservability.StateBased;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.AgentStates;
import model.process.coobservability.support.IllegalConfig;
import model.process.coobservability.support.StateSet;
// import model.process.coobservability.support.StateSetPath;
import model.process.memory.MemoryMeasure;

public class DecideSBCoobs implements DecideCondition{

//---  Instance Variables   -------------------------------------------------------------------

    private static String attributeObservableRef;

    private List<TransitionSystem> plants;

    private List<TransitionSystem> specs;

    private List<String> attributes;

    private List<Agent> agents;

    private Set<String> events;

    private StateBased sbStructure;

    private boolean pathKnowledge;

//---  Constructors   -------------------------------------------------------------------------

    public DecideSBCoobs(boolean pathIn) {
        assignPathKnowledge(pathIn);
    }

    public DecideSBCoobs(List<String> eventsIn, TransitionSystem specStart, List<String> attr, List<Agent> agentsIn) {
        events = new HashSet<String>();
        events.addAll(eventsIn);
        plants = new ArrayList<TransitionSystem>();
        plants.add(generateSigmaStarion(specStart));
        specs = new ArrayList<TransitionSystem>();
        specs.add(specStart);
        attributes = attr;
        agents = agentsIn;
    }

    public DecideSBCoobs(List<TransitionSystem> inPlants, List<TransitionSystem> inSpecs, List<String> attrIn, List<Agent> agentsIn) {
        plants = inPlants;
        specs = inSpecs;
        attributes = attrIn;
        agents = agentsIn;
    }

//---  Static Assignments   -------------------------------------------------------------------

    public static void assignAttributeReferences(String obs) {
        attributeObservableRef = obs;
    }

//---  Operations   ---------------------------------------------------------------------------

    @Override
    public boolean decideCondition() {
        sbStructure = new StateBased(plants, specs, attributes, agents, getPathKnowledge());
        boolean out = sbStructure.isSBCoobservable();
        sbStructure.assignTestResult(out);
        return out;
    }

    @Override
    public DecideCondition constructDeciderCoobs(List<String> events, TransitionSystem specStart, List<String> attr, List<Agent> agentsIn) {
        DecideSBCoobs out = new DecideSBCoobs(events, specStart, attr, agentsIn);
        out.assignPathKnowledge(getPathKnowledge());
        return out;
    }

    @Override
    public void addComponent(TransitionSystem next, boolean plant) {
        if(plant) {
            plants.add(next);
        }
        else {
            specs.add(next);
        }
    }

    @Override
    public MemoryMeasure produceMemoryMeasure() {
        return sbStructure == null ? StateBased.produceBlank() : sbStructure;
    }

    private TransitionSystem generateSigmaStarion(TransitionSystem spec) {
        TransitionSystem sigmaStar = spec.copy();
        sigmaStar.setId("sigma_starion_" + spec.getId());

        for(String s : sigmaStar.getStateNames()) {
            for(String t : events) {
                sigmaStar.setEventAttribute(t, attributeObservableRef, true);
                if(!sigmaStar.getStateTransitionEvents(s).contains(t)) {
                    sigmaStar.addTransition(s, t, s);
                }
            }
        }

        return sigmaStar;
    }

//---  Setter Methods   -----------------------------------------------------------------------

    protected void assignPathKnowledge(boolean in) {
        pathKnowledge = in;
    }

//---  Getter Methods   -----------------------------------------------------------------------

    private boolean getPathKnowledge() {
        return pathKnowledge;
    }

    @Override
    public Set<IllegalConfig> getCounterExamples() {

        return null;
        /*
        Set<IllegalConfig> out = new HashSet<>();
        if(sbStructure == null) {
            return out;
        }
        for(StateSet s : sbStructure.getRemainingDisableStates()) {
            AgentStates aS = new AgentStates(s.getStates(), sbStructure.getStateSetPath(s));
            for(String t : sbStructure.getStateSetPathEvents(s)) {
                getSequences(0, aS, sbStructure.getEquivalentPaths(s), new ArrayList<List<String>>(), t, out);
            }
        }
        System.out.println("~~~\n~~~\n" + out);
        return out;
        */
    }

//---  Support Methods   ----------------------------------------------------------------------

    private void getSequences(int index, AgentStates aS, List<List<StateSet>> paths, List<List<String>> use, String s, Set<IllegalConfig> out){
        if(index >= paths.size()) {
            out.add(new IllegalConfig(aS, copy(use), s));
        }
        else {
            for(StateSet st : paths.get(index)) {
                //use.add(st.getEventPath());
                getSequences(index + 1, aS, paths, use, s, out);
                use.remove(use.size() - 1);
            }
        }
    }

    private List<List<String>> copy(List<List<String>> in){
        List<List<String>> out = new ArrayList<>();
        for(List<String> t : in) {
            out.add(new ArrayList<>(t));
        }
        return out;
    }

}
