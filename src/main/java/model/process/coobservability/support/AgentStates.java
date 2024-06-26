package model.process.coobservability.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgentStates implements Comparable<AgentStates>{

//---  Instance Variables   -------------------------------------------------------------------

    private String[] currentStates;

    private List<List<String>> eventPath;

//---  Constructors   -------------------------------------------------------------------------

    public AgentStates(String[] states, List<String> inPath) {
        currentStates = states;
        eventPath = new ArrayList<List<String>>();
        for(int i = 0; i < states.length; i++) {
            List<String> use = new ArrayList<>(inPath);
            eventPath.add(use);
        }
    }

    public AgentStates(String[] states) {
        currentStates = states;
        eventPath = new ArrayList<List<String>>();
        for(int i = 0; i < states.length; i++) {
            List<String> use = new ArrayList<>();
            eventPath.add(use);
        }
    }

//---  Operations   ---------------------------------------------------------------------------

    /**
     * 
     * Where index = 0 is the true plant and the first controller view is index = 1
     * 
     * @param index
     * @param s
     */

    public void addGuess(int index, String s) {
        eventPath.get(index).add(s);
    }

    public AgentStates deriveChild(String[] newStates, boolean[] canAct, String s) {
        AgentStates out = new AgentStates(newStates);
        for(int i = 0; i < newStates.length; i++) {
            List<String> use = new ArrayList<String>();
            for(String t : eventPath.get(i)) {
                use.add(t);
            }
            if(canAct[i] && s != null) {
                use.add(s);
            }
            out.setObservedPath(i, use);
        }
        return out;
    }

    public AgentStates deriveChild(String[] newStates, int index, String s) {
        AgentStates out = new AgentStates(newStates);
        for(int i = 0; i < newStates.length; i++) {
            List<String> use = new ArrayList<String>();
            for(String t : eventPath.get(i)) {
                use.add(t);
            }
            if(i == index) {
                use.add(s);
            }
            out.setObservedPath(i, use);
        }
        return out;
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public String[] getStates() {
        return currentStates;
    }

    public List<String> getEventPath() {
        return new ArrayList<String>(eventPath.get(0));
    }

    /**
     * 
     * Where index = 0 is the true plant and the first controller view is index = 1
     * 
     * @param index
     * @return
     */

    public List<String> getObservedPath(int index){
        return eventPath.get(index);
    }

    public String getCompositeName() {
        StringBuilder out = new StringBuilder();
        out.append("(");
        for(int i = 0; i < currentStates.length; i++) {
            out.append(currentStates[i] + (i + 1 < currentStates.length ? ", " : ")"));
        }
        return out.toString();
    }

//---  Setter Methods   -----------------------------------------------------------------------

    protected void setObservedPath(int index, List<String> inPath) {
        eventPath.set(index, inPath);
    }

//---  Mechanics   ----------------------------------------------------------------------------

    @Override
    public int compareTo(AgentStates o) {
        boolean fail = false;
        for(int i = 0; i < this.getStates().length; i++)
            if(!this.getStates()[i].equals(o.getStates()[i]))
                fail = true;
        if(!fail)
            return 0;
        else
            return -1;
    }

    @Override
    public boolean equals(Object o1) {
        if (this == o1)
            return true;
        else if (o1 instanceof AgentStates)
            return this.getCompositeName().equals(((AgentStates)o1).getCompositeName());
        else
            return false;
    }

    @Override
    public String toString() {
        return Arrays.toString(currentStates);
    }

}