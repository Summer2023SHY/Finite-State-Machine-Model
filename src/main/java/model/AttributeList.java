package model;

import java.util.HashMap;

public class AttributeList {

//---  Constants   ----------------------------------------------------------------------------

    //false means you only need one, true means you need all (AON = "all or nothing")

    //-- State  -----------------------------------------------

    public static final String[] STATE_ATTRIBUTES = new String[] {AttributeList.ATTRIBUTE_INITIAL, AttributeList.ATTRIBUTE_MARKED, AttributeList.ATTRIBUTE_PRIVATE, AttributeList.ATTRIBUTE_BAD, AttributeList.ATTRIBUTE_GOOD};
    public static final String ATTRIBUTE_INITIAL = "Initial";
    public static final boolean ATTRIBUTE_AON_INITIAL = true;
    public static final String ATTRIBUTE_MARKED = "Marked";
    public static final boolean ATTRIBUTE_AON_MARKED = true;
    public static final String ATTRIBUTE_PRIVATE = "Private";
    public static final boolean ATTRIBUTE_AON_PRIVATE = true;
    public static final String ATTRIBUTE_BAD = "Bad";
    public static final boolean ATTRIBUTE_AON_BAD = false;
    public static final String ATTRIBUTE_GOOD = "Good";
    public static final boolean ATTRIBUTE_AON_GOOD = false;

    //-- Event  -----------------------------------------------

    public static final String[] EVENT_ATTRIBUTES = new String[] {AttributeList.ATTRIBUTE_OBSERVABLE, AttributeList.ATTRIBUTE_CONTROLLABLE, AttributeList.ATTRIBUTE_ATTACKER_OBSERVABLE};
    public static final String ATTRIBUTE_OBSERVABLE = "Observable";
    public static final boolean ATTRIBUTE_AON_OBSERVABLE = false;
    public static final String ATTRIBUTE_CONTROLLABLE = "Controllable";
    public static final boolean ATTRIBUTE_AON_CONTROLLABLE = false;
    public static final String ATTRIBUTE_ATTACKER_OBSERVABLE = "AttackerObservable";
    public static final boolean ATTRIBUTE_AON_ATTACKER_OBSERVABLE = true;

    //-- Transitions  -----------------------------------------

    public static final String[] TRANSITION_ATTRIBUTES = new String[] {AttributeList.ATTRIBUTE_BAD};

//---  Instance Variables   -------------------------------------------------------------------

    private static final HashMap<String, Boolean> map;
    static {
        map = new HashMap<String, Boolean>();
        map.put(ATTRIBUTE_INITIAL, ATTRIBUTE_AON_INITIAL);
        map.put(ATTRIBUTE_MARKED, ATTRIBUTE_AON_MARKED);
        map.put(ATTRIBUTE_PRIVATE, ATTRIBUTE_AON_PRIVATE);
        map.put(ATTRIBUTE_OBSERVABLE, ATTRIBUTE_AON_OBSERVABLE);
        map.put(ATTRIBUTE_CONTROLLABLE, ATTRIBUTE_AON_CONTROLLABLE);
        map.put(ATTRIBUTE_BAD, ATTRIBUTE_AON_BAD);
        map.put(ATTRIBUTE_ATTACKER_OBSERVABLE, ATTRIBUTE_AON_ATTACKER_OBSERVABLE);
        map.put(ATTRIBUTE_GOOD, ATTRIBUTE_AON_GOOD);
    }

    /** Private constructor */
    private AttributeList() {
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public static boolean getAON(String ref) {
        return map.get(ref);
    }

}
