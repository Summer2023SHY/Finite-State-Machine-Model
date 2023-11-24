package model.fsm.component.transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Models all transitions in an FSM, storing states and a list of transitions as
 * key-value pairs.
 * 
 * <p>
 * This class is a part of the support package.
 * 
 * @author Ada Clevinger
 * @author Graeme Zinck
 */
public class TransitionFunction {

    // Instance Variables

    /**
     * A map of states to its respective transitions.
     */
    private Map<String, List<Transition>> transitions;

    private List<String> attributes;

    // --- Constructors
    // -------------------------------------------------------------------------

    /**
     * Constructs a new {@code TransitionFunction}.
     * 
     * @param defAttrib
     */
    public TransitionFunction(List<String> defAttrib) {
        transitions = new HashMap<String, List<Transition>>();
        attributes = defAttrib == null ? new ArrayList<String>() : defAttrib;
    }

    // Operations
    public void mergeTransitionFunctions(TransitionFunction in) {
        mergeTransitions(in);
    }

    public void mergeTransitions(TransitionFunction in) {
        for (String s : in.getStateNames()) {
            for (Transition t : in.getTransitions(s)) {
                String e = t.getEvent();
                for (String u : t.getStates()) {
                    this.addTransition(s, e, u);
                }
                for (String a : getAttributes()) {
                    this.setTransitionAttribute(s, e, a, in.getTransitionAttribute(s, e, a));
                }
            }
        }
    }

    public void renameEvent(String old, String newNom) {
        for (String s : getStateNames()) {
            Transition ol = getTransition(s, old);
            Transition noo = getTransition(s, newNom);
            if (noo != null) {
                for (String t : ol.getStates()) {
                    noo.addTransitionState(t);
                }
                removeTransition(s, old);
            } else {
                ol.setName(newNom);
            }
        }
    }

    // Adder Methods

    /**
     * Adds a new transition to this TransitionFunction.
     * 
     * @param inState  the initial state of the transition
     * @param event    the event that triggers the transition
     * @param outState the target state of the transition
     */
    public void addTransition(String inState, String event, String outState) {
        Transition t = getTransition(inState, event);
        if (t != null) {
            t.addTransitionState(outState);
        } else {
            if (transitions.get(inState) == null) {
                // if(inState.contains("trash"))
                // System.out.println(transitions.size() + " " + inState);
                transitions.put(inState, new ArrayList<Transition>());
            }
            transitions.get(inState).add(new Transition(event, outState));
            t = getTransition(inState, event);
        }
        List<String> use = new LinkedList<>(attributes);
        t.setAttributes(use);
    }

    /**
     * Adds transitions for a given state.
     * 
     * @param state         the state to store data for
     * @param inTransitions list of transitions to store
     */
    public void addTransitions(String state, List<Transition> inTransitions) {
        if (transitions.get(state) == null) {
            transitions.put(state, new ArrayList<Transition>());
        }
        for (Transition t : inTransitions) {
            Transition ref = getTransition(state, t.getEvent());
            if (ref == null) {
                transitions.get(state).add(t);
            } else {
                for (String s : t.getStates()) {
                    if (!ref.hasState(s)) {
                        ref.addTransitionState(s);
                    }
                }
            }
        }
    }

    public void addTransition(String state, String event, String target, TransitionFunction context) {
        if (transitions.get(state) == null) {
            transitions.put(state, new ArrayList<Transition>());
        }
        if (getTransition(state, event) == null) {
            transitions.get(state).add(context.getTransition(state, event).copy());
            getTransition(state, event).removeTargetStates();
            getTransition(state, event).addTransitionState(target);
        } else {
            getTransition(state, event).addTransitionState(target);
            getTransition(state, event).copyAttributes(context.getTransition(state, event));
        }
    }

    // Remover Methods

    /**
     * Removes all transitions for a given state.
     * 
     * @param state the state to remove data of
     */
    public void removeStateTransitions(String state) {
        transitions.remove(state);
        for (Map.Entry<String, List<Transition>> entry : transitions.entrySet()) {
            List<Transition> tToRemove = new ArrayList<Transition>();
            for (Transition transition : entry.getValue()) {
                transition.removeTransitionState(state);
                if (transition.isEmpty())
                    tToRemove.add(transition);
            }
            entry.getValue().removeAll(tToRemove);
        } // for every entry
    }

    public void removeEventTransitions(String event) {
        for (String s : transitions.keySet()) {
            Transition t = getTransition(s, event);
            if (t != null) {
                transitions.get(s).remove(t);
            }
        }
    }

    /**
     * Removes a transition from this transition function.
     * 
     * @param inState  the initial state of the transition
     * @param event    the event that triggers the transition
     * @param outState the target state of the transition
     */
    public void removeTransition(String stateFrom, String event, String stateTo) {
        List<Transition> thisTransitions = transitions.get(stateFrom);
        for (int i = 0; i < thisTransitions.size(); i++) {
            Transition transition = thisTransitions.get(i);
            if (transition.getEvent().equals(event)) {
                if (transition.hasState(stateTo)) {
                    transition.removeTransitionState(stateTo);
                    if (transition.isEmpty())
                        thisTransitions.remove(transition);
                }
            }
        }
    }

    public void removeTransition(String stateFrom, String event) {
        for (int i = 0; i < getTransitions(stateFrom).size(); i++) {
            Transition t = getTransitions(stateFrom).get(i);
            if (t.getEvent().equals(event)) {
                getTransitions(stateFrom).remove(i);
                i--;
            }
        }
    }

    // Setter Methods

    public void setAttributes(List<String> attrib) {
        attributes = attrib;
        for (String s : getStateNames()) {
            for (Transition e : getTransitions(s)) {
                List<String> use = new LinkedList<String>();
                use.addAll(attributes);
                e.setAttributes(use);
            }
        }

    }

    public void setTransitionAttribute(String state, String event, String ref, boolean val) {
        getTransition(state, event).setAttributeValue(ref, val);
    }

    // Getter Methods

    public List<String> getTransitionsWithAttribute(String attrib) {
        Set<String> out = new HashSet<>();
        for (String s : getStateNames()) {
            for (String e : getStateEvents(s)) {
                if (getTransitionAttribute(s, e, attrib)) {
                    out.add(s);
                }
            }
        }
        List<String> use = new ArrayList<>(out);
        return use;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public Boolean getTransitionAttribute(String state, String event, String ref) {
        return getTransition(state, event).getAttributeValue(ref);
    }

    /**
     * Returns the states stored in this transition function.
     * 
     * @return the states stored in this transition function
     */
    public List<String> getStateNames() {
        return new ArrayList<>(transitions.keySet());
    }

    public List<String> getStateEvents(String state) {
        List<String> out = new ArrayList<>();
        if (transitions.get(state) == null) {
            return out;
        }
        for (Transition t : transitions.get(state)) {
            if (!out.contains(t.getEvent()))
                out.add(t.getEvent());
        }
        return out;
    }

    /**
     * Retrieves the transition states at a designated state that
     * correspond to the provided event.
     * 
     * @param state the state whose transitions are searched through
     * @param event the event provided to denote which State Transitions to
     *              return corresponding to the provided State object
     * @return list of transition states that the provided
     *         state leads to, or {@code null} if there are none.
     */
    public List<String> getTransitionStates(String state, String event) {
        List<Transition> trans = transitions.get(state);
        if (trans != null)
            for (Transition transition : trans)
                if (transition.getEvent().equals(event)) {
                    List<String> out = new ArrayList<>();
                    for (String s : transition.getStates()) {
                        if (!out.contains(s))
                            out.add(s);
                    }
                    return out;
                }
        return null;
    }

    /**
     * Checks whether a certain event exists at a certain state.
     * 
     * @param state the state in whose transitions to search for the specified event
     * @param event the event object to search for
     * @return {@code true} if the state has the provided event in one of its
     *         transitions
     */
    public boolean eventExists(String state, String event) {
        List<Transition> thisTransitions = transitions.get(state);
        if (thisTransitions != null) {
            // System.out.println("In state " + state.getStateName() + ", we're looking for
            // " + event.getEventName() + " in " + thisTransitions.toString());
            for (Transition Transition : thisTransitions)
                if (Transition.getEvent().equals(event))
                    return true;
        }
        return false;
    }

    /**
     * Returns the mapping of states to their transitions.
     * 
     * @return the map
     */
    protected Map<String, List<Transition>> getTransitions() {
        return transitions;
    }

    protected List<Transition> getTransitions(String state) {
        return transitions.get(state);
    }

    protected Transition getTransition(String state, String event) {
        if (transitions.get(state) == null) {
            return null;
        }
        for (Transition t : transitions.get(state)) {
            if (t.getEvent().equals(event)) {
                return t;
            }
        }
        return null;
    }

    // Mechanics

    /**
     * Searches the presence of the specified transition.
     * 
     * @param reference  the state to search through
     * @param transition the transition to search for
     * @return {@code true} if the transition exists
     */
    public boolean contains(String reference, Transition transition) {
        for (Transition t : transitions.get(reference)) {
            if (t.equals(transition))
                return true;
        }
        return false;
    }

}
