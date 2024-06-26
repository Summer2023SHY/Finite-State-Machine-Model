package model.fsm.component;

import java.util.Collections;
import java.util.List;

/**
 * This class models an Event in an FSM, storing information about the Event's name and
 * its status as Observable, Controllable, and whatever other features may be implemented
 * in the future.
 * 
 * This class is a part of the support.event package
 * 
 * TODO: Clean up the functions between this and Attribute, they're clunky and a bit weird
 * 
 * @author Ada Clevinger and Graeme Zinck
 */

public class Entity {

//---  Instance Variables   -------------------------------------------------------------------

    /** String instance variable object representing the name of the Event*/
    private String id;
    private Attribute wrap;

//---  Constructors   -------------------------------------------------------------------------

    /**
     * Constructor for an Event object that assigns a defined String object to be its name,
     * defaulting its status as Controllability and Observability to be true.
     *
     * @param eventName - String object that represents the name of the Event object.
     */

    public Entity(String name) {
        id = name;
    }

//---  Operations   ---------------------------------------------------------------------------

    public Entity copy() {
        Entity out = new Entity(getName());
        out.setAttributes(getAttributes());
        for(String s : getAttributes()) {
            out.setAttributeValue(s, getAttributeValue(s));
        }
        return out;
    }

    public void wipeAttributes() {
        wrap = null;
    }

    public void copyAttributes(Entity ot) {
        setAttributes(ot.getAttributes());
        for(String s : getAttributes()) {
            setAttributeValue(s, ot.getAttributeValue(s));
        }
    }

    public void addAttribute(String attr, boolean set) {
        if(wrap == null) {
            wrap = new Attribute(attr);
            wrap.setValue(attr, set);
        }
        else {
            wrap.setValue(attr, set);
        }
    }

    public void addAttributes(List<String> refs) {
        if(refs != null && !refs.isEmpty()) {
            for(String s : refs) {
                addAttribute(s, false);
            }
        }
    }

//---  Setter Methods   -----------------------------------------------------------------------

    /**
     * Setter method that assigns the provided String value to be the new name of this Event object
     * 
     * @param in - String object provided as the new value for this Event object's name
     */

    public void setName(String in) {
        id = in;
    }

    public void setAttributeValue(String ref, boolean val) {
        if(wrap == null) {
            wrap = new Attribute(ref);
        }
        wrap.setValue(ref, val);
    }

    public void setAttributes(List<String> refs) {
        Attribute hold = wrap;
        wipeAttributes();
        for(String s : refs) {
            addAttribute(s, hold == null || hold.getValue(s) == null ? false : hold.getValue(s));
        }
    }

//---  Getter Methods   -----------------------------------------------------------------------

    /**
     * Getter method that requests the current name of the Event object
     * 
     * @return - Returns a String object representing the name of this Event object
     */

    public String getName() {
        return id;
    }

    public Boolean getAttributeValue(String ref) {
        if(wrap == null) {
            return null;
        }
        return wrap.getValue(ref);
    }

    public List<String> getAttributes(){
        return wrap == null ? Collections.emptyList() : wrap.getAttributes();
    }

//---  Miscellaneous   ------------------------------------------------------------------------

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        else if (other instanceof Entity) {
            Entity ot = (Entity)other;
            boolean result = getName().equals(ot.getName());
            result = result && (getAttributes().equals(ot.getAttributes()));
            for(String s : getAttributes()) {
                result = result && (getAttributeValue(s) == ot.getAttributeValue(s));
            }
            return result;
        } else
            return false;
    }

}