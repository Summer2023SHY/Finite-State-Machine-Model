package model.fsm.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is a wrapper for a HashMap, allowing the user to search for a Entity object using its corresponding name.
 * It also permits convenient interfacing with Entity objects.
 * 
 * This class is a part of the support package.
 * 
 * @author Ada Clevinger and Graeme Zinck
 */

public class StateMap {

//--- Instance Variables   --------------------------------------------------------------------

    /** HashMap<<r>String, <<r>S extends Entity>> object that maps the String object names of States to their Entity objects.*/
    private Map<String, Entity> states;
    /** HashMap<<r>S, ArrayList<<r>S>> object that maps a Entity extending object to a list of Entity extending objects which compose it.*/
    private Map<String, List<String>> composition;

    private List<String> attributes;

//---  Constructors   -------------------------------------------------------------------------

    /**
     * Constructor for a StateMap object that initializes the state HashMap<<r>String, <<r>S extends Entity>> object.
     * 
     * @param inClass - The class of Entity the map will hold, used for instantiation.
     */

    public StateMap(List<String> defAttrib) {
        states = new HashMap<String, Entity>();
        composition = new HashMap<String, List<String>>();
        attributes = defAttrib == null ? new ArrayList<String>() : defAttrib;
    }

//---  Operations   ---------------------------------------------------------------------------

    public void mergeStateMaps(StateMap in) {
        mergeStates(in);
        mergeStateCompositions(in);
    }

    public void mergeStateCompositions(StateMap in) {
        for(String s : in.getStateCompositions().keySet()) {
            List<String> use = new ArrayList<String>(in.getStateCompositions().get(s));
            addStateComposition(s, use);
        }
    }

    public void mergeStates(StateMap in) {
        for(String s : in.getStates().keySet()) {
            addState(s, in);
        }
    }

    /**
     * This method renames a state from its former String oldName to a provided String newName.
     * 
     * @param state - String object representing the Entity that needs to be renamed.
     * @param newName - String object representing the Entity's new name.
     * @return - Returns a boolean value; true if the Entity was successfully renamed, false otherwise.
     */

    public boolean renameState(String oldName, String newName) {
        if(oldName == null || newName == null)
            return false;
        Entity state = states.get(oldName);
        state.setName(newName);
        states.remove(oldName);
        states.put(newName, state);
        return true;
    }

    /**
     * This method renames all the states in the set of states in the FSM so that states are named sequentially ("0", "1", "2"...).
     *
     */

    public void renameStates() {
        Entity[] stateArr = states.values().toArray(new Entity[states.size()]);
        for(int i = 0; i < states.size(); i++)
            renameState(stateArr[i].getName(), Integer.toString(i));
        composition = null;
    }

//---  Adder Methods   ------------------------------------------------------------------------

    /**
     * This method adds a new Entity with the given name to the HashMap<String, Entity> mapping.
     * If a state with the same id already exists, nothing is changed and the corresponding
     * pre-existing Entity object is returned.
     * 
     * @param stateName - String object representing the Entity's name.
     * @return - Returns a Entity object representing the object added to the mapping (or the one that already existed in the mapping).
     */

    public void addState(String stateName) {
        states.put(stateName, new Entity(stateName));
        List<String> use = new LinkedList<String>();
        use.addAll(attributes);
        states.get(stateName).setAttributes(use);
    }

    public void addState(String stateName, StateMap context) {
        states.put(stateName, context.getState(stateName).copy());
    }

    /**
     * Setter method that assigns a list of Entity extending objects to be designated as the States which have
     * composed the provided Entity in some operation that aggregated the values in that list to produce the
     * singular Entity.
     * 
     * @param keyState - Entity extending object whose entry in the set of States and their composing States will be adjusted.
     * @param composition - ArrayList<<r>Entity> object containing the Entity extending objects which compose the designated Entity extending object.
     */

    public void addStateComposition(String keyState, List<String> composedStates) {
        composition.put(keyState, composedStates);
    }

    public void addAttributeToState(String stateName, String attribute, boolean set) {
        states.get(stateName).addAttribute(attribute, set);
    }

//---  Remover Methods   ----------------------------------------------------------------------

    /**
     * This method removes a Entity from the HashMap<<r>String, <<r>S extends Entity>> using its corresponding String name.
     * 
     * @param stateName - String object representing the name of the state to remove.
     */

    public void removeState(String stateName) {
        states.remove(stateName);
        composition.remove(stateName);
        for(String s : composition.keySet()) {
            composition.get(s).remove(stateName);
        }
    }

//---  Setter Methods   -----------------------------------------------------------------------

    public void setAttributes(List<String> attrib) {
        attributes = attrib;
        for(Entity e : states.values()) {
            List<String> use = new LinkedList<String>(attributes);
            e.setAttributes(use);
        }
    }

    public void setStateAttribute(String nom, String ref, boolean val) {
        states.get(nom).setAttributeValue(ref, val);
    }

    /**
     * Setter method that assigns the provided HashMap<<r>String, S> object to this object's corresponding instance variable.
     * 
     * @param inHash - HashMap<<r>String, S> object that represents a matched set of Strings leading to Entity objects.
     */

    public void setStates(StateMap in) {
        states = in.getStates();
    }

    public void setStateComposition(String state, List<String> comp) {
        composition.put(state, comp);
    }

    /**
     * Setter method that assigns a new set of Entity extending objects to individual lists of Entity extending objects
     * which represent the States that have been aggregated to compose the key Entity in <<r>Entity, ArrayList<<r>Entity>> pairs.
     * 
     * @param newComposed - HashMap<Entity, ArrayList<Entity>> object representing the new set of States and their composing States.
     */

    public void setStateCompositions(StateMap in) {
        composition = in.getStateCompositions();
    }

//---  Getter Methods   -----------------------------------------------------------------------

    protected Entity getState(String stateName) {
        return states.get(stateName).copy();
    }

    public List<String> getStatesWithAttribute(String attrib){
        List<String> out = new ArrayList<>();
        for(String s : states.keySet()) {
            if(getStateAttribute(s, attrib)) {
                out.add(s);
            }
        }
        return out;
    }

    public List<String> getAttributes(){
        return attributes;
    }

    public List<String> getNames(){
        return new ArrayList<>(states.keySet());
    }

    /**
     * Getter method that returns a list of States which compose the provided Entity. (Through operations
     * such as Determinization or generating the Observer View.) In the event that the provided Entity is
     * not found, but there is a mapping of some States to a list of States, then return a single-entry
     * list containing the querying Entity. If there is no mapping, return null.
     * 
     * @param provided - Entity extending object provided to request a specific Entity's set of composing States.
     * @return - Returns an ArrayList<<r>Entity> representing all the States that are designated as composing the provided Entity.
     */

    public List<String> getStateComposition(String provided){
        List<String> out = new ArrayList<String>();
        if(composition == null || composition.get(provided) == null) {
            out.add(provided);
            return out;
        }
        else {
            for(String s : composition.get(provided)) {
                out.add(s);
            }
            return out;
        }
    }

    /**
     * Getter method that requests the status of whether or not an entry for a given Entity's name exists.
     * 
     * @param stateName - String object representing the Entity's name.
     * @return - Returns a boolean value; true if the String object has an entry, false otherwise.
     */

    public boolean stateExists(String stateName) {
        return states.get(stateName) != null;
    }

    public Boolean getStateAttribute(String nom, String ref) {
        return states.get(nom).getAttributeValue(ref);
    }

    //-- Internal  --------------------------------------------

    protected Map<String, Entity> getStates(){
        return states;
    }

    /**
     * Getter method that returns a HashMap<<r>Entity, ArrayList<<r>Entity>> representing the full set of States
     * and the list of States which compose each one after operations that aggregate States together.
     * 
     * @return - Returns a HashMap<<r>Entity, ArrayList<<r>Entity>> object holding paired States and lists of composing States.
     */

    protected Map<String, List<String>> getStateCompositions(){
        return composition;
    }

//---  Mechanics   ----------------------------------------------------------------------------

    @Override
    public String toString() {
        return states.toString();
    }
}
