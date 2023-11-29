package model.fsm.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Attribute {

//---  Instance Variables   -------------------------------------------------------------------

    private Map<String, Boolean> attributeMap;

//---  Constructors   -------------------------------------------------------------------------

    public Attribute() {
        this.attributeMap = new LinkedHashMap<String, Boolean>();
    }

    public Attribute(String inId) {
        this();
        setValue(inId, false);
    }

//---  Operations   ---------------------------------------------------------------------------

    public void addWrapper(String ref) {
        if (attributeMap.containsKey(ref))
            return;
        setValue(ref, false);
    }

//---  Setter Methods   -----------------------------------------------------------------------

    public void setAttributes(List<String> in) {
        if(in != null) {
            for (String id : in) {
                setValue(id, false);
            }
        }
    }

    public void setValue(String ref, boolean in) {
        attributeMap.put(ref, in);
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public Boolean getValue(String ref) {
        return attributeMap.get(ref);
    }

    public List<String> getAttributes(){
        return new ArrayList<>(attributeMap.keySet());
    }

}
