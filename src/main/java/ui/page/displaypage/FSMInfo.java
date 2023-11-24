package ui.page.displaypage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FSMInfo {

//---  Instance Variables   -------------------------------------------------------------------

    private String fsmName;

    private String image;

    private List<String> fsmStateAttributes;

    private Map<String, List<Boolean>> fsmStateDetails;

    private List<String> fsmEventAttributes;

    private Map<String, List<Boolean>> fsmEventDetails;

    private List<String> fsmTransitionAttributes;

    private Map<String, List<Boolean>> fsmTransitionDetails;

//---  Constructors   -------------------------------------------------------------------------

    public FSMInfo(String name) {
        fsmName = name;
    }

//---  Setter Methods   -----------------------------------------------------------------------

    public void updateStateAttributes(List<String> statAttr) {
        fsmStateAttributes = statAttr;
    }

    public void updateStateDetails(Map<String, List<Boolean>> statDeta){
        fsmStateDetails = statDeta;
    }

    public void updateEventAttributes(List<String> evenAttr) {
        fsmEventAttributes = evenAttr;
    }

    public void updateEventDetails(Map<String, List<Boolean>> evenDeta){
        fsmEventDetails = evenDeta;
    }

    public void updateTransitionAttributes(List<String> transAttr) {
        fsmTransitionAttributes = transAttr;
    }

    public void updateTransitionDetails(Map<String, List<Boolean>> transDeta){
        fsmTransitionDetails = transDeta;
    }

    public void updateImage(String img) {
        image = img;
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public List<String> getStates(){
        return new ArrayList<String>(fsmStateDetails.keySet());
    }

    public List<String> getEvents(){
        return new ArrayList<String>(fsmEventDetails.keySet());
    }

    public List<String> getTransitions(){
        return new ArrayList<String>(fsmTransitionDetails.keySet());
    }

    public List<String> getStateAttributes(){
        return fsmStateAttributes;
    }

    public List<String> getEventAttributes(){
        return fsmEventAttributes;
    }

    public List<String> getTransitionAttributes(){
        return fsmTransitionAttributes;
    }

    public Map<String, List<Boolean>> getStateDetails(){
        return fsmStateDetails;
    }

    public Map<String, List<Boolean>> getEventDetails(){
        return fsmEventDetails;
    }

    public Map<String, List<Boolean>> getTransitionDetails(){
        return fsmTransitionDetails;
    }

    public String getName() {
        return fsmName;
    }

    public String getImage() {
        return image;
    }

}
