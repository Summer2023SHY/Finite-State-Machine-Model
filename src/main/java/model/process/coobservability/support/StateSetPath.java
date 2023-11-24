package model.process.coobservability.support;

import java.util.ArrayList;
import java.util.List;

public class StateSetPath extends StateSet implements Comparable<StateSetPath>{

    private StateSetPath parent;

    private List<String> eventPath;

    private String problemEvent;

    public StateSetPath(String[] in, StateSetPath inParent) {
        super(in);
        parent = inParent;
        if(!parent.search(new StateSet(in))) {
            eventPath = new ArrayList<String>();
            for(String s : parent.getEventPath()) {
                eventPath.add(s);
            }
        }
    }

    public void setProblemEvent(String in) {
        problemEvent = in;
    }

    protected boolean search(StateSet check) {
        return getPairName().equals(check.getPairName()) ? true : parent == null ? false : parent.search(check);
    }

    public boolean isNew() {
        return eventPath != null;
    }

    public StateSetPath(String[] in) {
        super(in);
        eventPath = new ArrayList<String>();
        parent = null;
    }

    public void addEvent(String s) {
        eventPath.add(s);
    }


    public List<String> getEventPath(){
        return eventPath == null ? new ArrayList<>() : new ArrayList<>(eventPath);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return toString().equals(other.toString());
    }

    @Override
    public String toString() {
        return eventPath == null ? parent.toString() : (super.toString() + ", " + eventPath);
    }

    @Override
    public int compareTo(StateSetPath o) {
        return toString().compareTo(o.toString());
    }

}
