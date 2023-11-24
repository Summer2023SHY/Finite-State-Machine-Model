package ui.popups;

import java.util.ArrayList;
import java.util.List;

public class EventRep {

//---  Instance Variables   -------------------------------------------------------------------

    private String event;
    private List<Boolean> setVals;

//---  Constructors   -------------------------------------------------------------------------

    public EventRep(String inEve, int i) {
        setVals = new ArrayList<Boolean>();
        event = inEve;
        for(int a = 0; a < i; a++) {
            setVals.add(false);
        }
    }

//---  Operations   ---------------------------------------------------------------------------

    public void toggle(int i) {
        setVals.set(i, !setVals.get(i));
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public List<Boolean> getValues(){
        return setVals;
    }

    public boolean getValue(int i) {
        return setVals.get(i);
    }

    public String getName() {
        return event;
    }

}