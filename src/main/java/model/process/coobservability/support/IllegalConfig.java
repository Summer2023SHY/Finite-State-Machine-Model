package model.process.coobservability.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;

public class IllegalConfig {

    private AgentStates stateSet;
    /** For each agent, it is their visible version of the true event sequence from the AgentStates object. Does NOT include the plant view, that is in the AgentStates stateSet object*/
    private List<List<String>> observedPaths;

    private String event;

    public IllegalConfig(AgentStates inStates, List<List<String>> inPaths, String inEvent) {
        stateSet = inStates;
        observedPaths = inPaths;
        event = inEvent;
    }

    public AgentStates getStateSet() {
        return stateSet;
    }

    public List<String> getEventPath() {
        return new ArrayList<String>(stateSet.getEventPath());
    }

    public List<List<String>> getObservedPaths() {
        List<List<String>> out = new ArrayList<>();
        for(List<String> s : observedPaths) {
            out.add(new ArrayList<String>(s));
        }
        return out;
    }

    public int getEventPathLength() {
        return getEventPath().size();
    }

    public int getNumberDistinctEvents() {
        HashSet<String> chars = new HashSet<>();
        for(String c : getEventPath()) {
            chars.add(c);
        }
        return chars.size();
    }

    public String getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return stateSet.toString() + ", " + stateSet.getEventPath() + ", " + event + StringUtils.LF + observedPaths;
    }

}
