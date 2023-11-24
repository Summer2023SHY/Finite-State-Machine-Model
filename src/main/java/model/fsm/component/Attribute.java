package model.fsm.component;

import java.util.LinkedList;
import java.util.List;

public class Attribute {

//---  Instance Variables   -------------------------------------------------------------------

    private String id;
    private boolean value;
    private Attribute wrap;

//---  Constructors   -------------------------------------------------------------------------

    public Attribute(String inId) {
        id = inId;
        value = false;
    }

//---  Operations   ---------------------------------------------------------------------------

    public void addWrapper(String ref) {
        if(id.equals(ref)) {
            return;
        }
        if(wrap != null) {
            wrap.addWrapper(ref);
        }
        else {
            wrap = new Attribute(ref);
        }
    }

//---  Setter Methods   -----------------------------------------------------------------------

    public void setAttributes(List<String> in) {
        if(in != null && !in.isEmpty()) {
            wrap = new Attribute(in.remove(0));
            wrap.setAttributes(in);
        }
    }

    public void setValue(String ref, boolean in) {
        if(id.equals(ref)) {
            value = in;
        }
        else {
            if(wrap == null) {
                wrap = new Attribute(ref);
            }
            wrap.setValue(ref, in);
        }
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public Boolean getValue(String ref) {
        if(id.equals(ref)) {
            return value;
        }
        if(wrap != null) {
            return wrap.getValue(ref);
        }
        return null;
    }

    public List<String> getAttributes(){
        List<String> out = new LinkedList<String>();
        out.add(id);
        if(wrap != null) {
            out.addAll(wrap.getAttributes());
        }
        return out;
    }

}
