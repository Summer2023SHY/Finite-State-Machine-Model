package model.fsm.component.transition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.fsm.component.Entity;

/**
 * Representation of a transition.
 * 
 * This interface is part of the support.transition package.
 * 
 * @author Ada Clevinger
 * @author Graeme Zinck
 */

public class Transition extends Entity implements Comparable<Transition> {

    // Instance Variables

    /** Holds all states associated with this transition. */
    private List<String> states;

    // Constructors

    public Transition(String inEvent, String state) {
        super(inEvent);
        states = new ArrayList<String>();
        states.add(state);
    }

    /**
     * Constructs a new {@code Transition} assigning a single String object and
     * a list of States which the event can lead to.
     * 
     * @param inString string representation of the event that leads to the
     *                 associated transition states
     * @param inStates list of states led to by this transition
     */

    public Transition(String inString, List<String> inStates) {
        super(inString);
        states = inStates;
    }

    protected Transition(Entity base) {
        super(base.getName());
        copyAttributes(base);
        states = new ArrayList<String>();
    }

    // Operations

    @Override
    public Transition copy() {
        Transition out = new Transition(this);
        for (String s : states) {
            out.addTransitionState(s);
        }
        return out;
    }

    // Setter Methods

    /**
     * Sets the transition states for this transition.
     * 
     * @param in list of states led to by this transition
     */
    public void setTransitionStates(List<String> in) {
        states = in;
    }

    public void setTransitionEvent(String in) {
        setName(in);
    }

    // Getter Methods

    public String getEvent() {
        return getName();
    }

    public List<String> getStates() {
        return states;
    }

    public boolean hasState(String stateName) {
        return states.contains(stateName);
    }

    public boolean isEmpty() {
        return states.isEmpty();
    }

    // Adder Methods

    public void addTransitionState(String in) {
        if (!states.contains(in))
            states.add(in);
    }

    // Remover Methods

    public void removeTransitionState(String stateName) {
        states.remove(stateName);
    }

    public void removeTargetStates() {
        states.clear();
    }

    // Mechanics

    @Override
    public int compareTo(Transition o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName() + " goes to the states: ");
        Iterator<String> itr = states.iterator();
        while (itr.hasNext()) {
            sb.append(itr.next());
            if (itr.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }

}
