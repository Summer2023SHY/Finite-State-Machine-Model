package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import model.convert.GenerateDot;
import model.convert.GenerateFSM;
import model.convert.ReadWrite;
import model.fsm.TransitionSystem;
import model.process.ProcessDES;
import model.process.memory.MemoryMeasure;
import model.process.memory.ReceiveMemoryMeasure;

public class Manager implements ReceiveMemoryMeasure{

//---  Constants   ----------------------------------------------------------------------------

    private static final String SEPARATOR = ";,;;,;";
    private static final String REGION_SEPARATOR = "---";
    private static final String TRUE_SYMBOL = "o";
    private static final String FALSE_SYMBOL = "x";

//---  Instance Variables   -------------------------------------------------------------------

    private Map<String, TransitionSystem> fsms;

    private MemoryMeasure lastProcessData;

//---  Constructors   -------------------------------------------------------------------------

    public Manager() {
        fsms = new HashMap<String, TransitionSystem>();
        ReadWrite.assignConstants(SEPARATOR, REGION_SEPARATOR, TRUE_SYMBOL, FALSE_SYMBOL);
        GenerateFSM.assignConstants(SEPARATOR, REGION_SEPARATOR, TRUE_SYMBOL, FALSE_SYMBOL);
        assignAttributeReferences();
    }

//---  Operations   ---------------------------------------------------------------------------

    //-- Assign Attribute Data  -------------------------------

    private void assignAttributeReferences() {
        ProcessDES.assignReferences(this, AttributeList.ATTRIBUTE_INITIAL, AttributeList.ATTRIBUTE_MARKED, AttributeList.ATTRIBUTE_PRIVATE, AttributeList.ATTRIBUTE_OBSERVABLE, AttributeList.ATTRIBUTE_CONTROLLABLE, AttributeList.ATTRIBUTE_BAD, AttributeList.ATTRIBUTE_GOOD);
    }

    public static void assignEndAtFirstCounterexample(boolean in) {
        ProcessDES.assignEndAtFirstCounterexample(in);
    }

    //-- File Meta  -------------------------------------------

    public String generateFSMDot(String ref) {
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return GenerateDot.generateDot(fsms.get(ref));
    }

    public String readInFSM(String fileContents) {
        TransitionSystem in = ReadWrite.readFile(fileContents);
        appendFSM(in.getId(), in, false);
        return in.getId();
    }

    public String readInFSM(List<String> fileLines) {
        TransitionSystem in = ReadWrite.readFile(fileLines);
        appendFSM(in.getId(), in, false);
        return in.getId();
    }

    public String exportFSM(String ref) {
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return ReadWrite.generateFile(fsms.get(ref));
    }

    public List<Map<String, List<Boolean>>> readInAgents(String fileContents) {
        return ReadWrite.readAgentFile(fileContents);
    }

    public List<Map<String, List<Boolean>>> readInAgents(List<String> fileLines) {
        return ReadWrite.readAgentFile(fileLines);
    }

    public String exportAgents(String nom, List<Map<String, List<Boolean>>> agents, List<String> attributes) {
        return ReadWrite.generateAgentFile(nom, agents, attributes);
    }

    public boolean hasFSM(String ref) {
        return fsms.containsKey(ref);
    }

    public String duplicate(String fsm) {
        if(fsm == null || fsms.get(fsm) != null) {
            TransitionSystem out = fsms.get(fsm).copy();
            out.setId(out.getId() + "_copy");
            appendFSM(out.getId(), out, false);
            return out.getId();
        }
        return null;
    }

    public void flushFSMs() {
        Set<String> gather = new HashSet<>(fsms.keySet());
        for(String s : gather) {
            fsms.remove(s);
        }
        fsms = new HashMap<String, TransitionSystem>();
        lastProcessData = null;
    }

    //-- FSM Generation  --------------------------------------

    /**
     * 
     * //TODO: Review overwrite by default condition in there
     * 
     * @param nom
     * @return
     */

    public String generateEmptyFSM(String nom) {
        TransitionSystem use = new TransitionSystem(nom);
        appendFSM(use.getId(), use, true);
        return nom;
    }

    public void assignRandomFSMStateConfiguration(List<String> stateAttr, List<Integer> stateNumb) {
        GenerateFSM.assignStateAttributes(stateAttr, stateNumb);
    }

    public void assignRandomFSMDefaultStateSet(List<String> defaultState) {
        GenerateFSM.assignDefaultStateSet(defaultState);
    }

    public void assignRandomFSMEventConfiguration(List<String> eventAttr, List<Integer> eventNumb) {
        GenerateFSM.assignEventAttributes(eventAttr, eventNumb);
    }

    public void assignRandomFSMDefaultEventSet(List<String> defaultEvent) {
        GenerateFSM.assignDefaultEventSet(defaultEvent);
    }

    public void assignRandomFSMTransitionConfiguration(List<String> transAttr, List<Integer> transNumb) {
        GenerateFSM.assignTransitionAttributes(transAttr, transNumb);
    }

    public String generateRandomFSM(String nom, int numStates, int numEvents, int numTrans, boolean det)  {
        return GenerateFSM.createNewFSM(nom, numStates, numEvents, numTrans, det);
    }

    public String storeProcessHoldSystem() {
        if(lastProcessData == null || lastProcessData.getReserveSystem() == null) {
            return null;
        }
        String nom = lastProcessData.getReserveSystem().getId();
        appendFSM(nom, lastProcessData.getReserveSystem(), false);
        return nom;
    }

    //-- Processes  -------------------------------------------

    public String performProduct(List<String> ref) {
        if(bailMulti(ref)) {
            return null;
        }
        List<TransitionSystem> use = new ArrayList<>();
        for(int i = 0; i < ref.size(); i++) {
            use.add(fsms.get(ref.get(i)));
        }
        TransitionSystem out = ProcessDES.product(use);
        if(out == null) {
            return null;
        }
        appendFSM(out.getId(), out, false);
        return out.getId();
    }

    public String performParallelComposition(List<String> ref) {
        if(bailMulti(ref)) {
            return null;
        }
        List<TransitionSystem> use = new ArrayList<>();
        for(int i = 0; i < ref.size(); i++) {
            use.add(fsms.get(ref.get(i)));
        }
        TransitionSystem out = ProcessDES.parallelComposition(use);
        if(out == null) {
            return null;
        }
        appendFSM(out.getId(), out, false);
        return out.getId();
    }

    public String buildObserver(String ref) {
        if(bail(ref)) {
            return null;
        }
        TransitionSystem out = ProcessDES.buildObserver(fsms.get(ref));
        if(out == null) {
            return null;
        }
        appendFSM(out.getId(), out, false);
        return out.getId();
    }

    //-- Clean  -----------------------------------------------

    public String trim(String ref)  {
        if(bail(ref)) {
            return null;
        }
        TransitionSystem out = ProcessDES.trim(fsms.get(ref));
        if(out == null) {
            return null;
        }
        appendFSM(out.getId(), out, false);
        return out.getId();
    }

    public String makeAccessible(String ref) {
        if(bail(ref)) {
            return null;
        }
        TransitionSystem out = ProcessDES.makeAccessible(fsms.get(ref));
        if(out == null) {
            return null;
        }
        appendFSM(out.getId(), out, false);
        return out.getId();
    }

    public String makeCoAccessible(String ref)  {
        if(bail(ref)) {
            return null;
        }
        TransitionSystem out = ProcessDES.makeCoAccessible(fsms.get(ref));
        if(out == null) {
            return null;
        }
        appendFSM(out.getId(), out, false);
        return out.getId();
    }

    //-- Analysis  --------------------------------------------

    public Boolean stateExists(String ref, String nom) {
        if(bail(ref)) {
            return null;
        }
        return fsms.get(ref).stateExists(nom);
    }

    public Boolean eventExists(String ref, String nom) {
        if(bail(ref)) {
            return null;
        }
        return fsms.get(ref).eventExists(nom);
    }

    public Boolean transitionExists(String ref, String state, String event) {
        if(bail(ref)) {
            return null;
        }
        return fsms.get(ref).hasTransition(state, event);
    }

    public Boolean isBlocking(String ref)  {
        if(bail(ref)) {
            return null;
        }
        return ProcessDES.isBlocking(fsms.get(ref));
    }

    public Boolean isAccessible(String ref)  {
        if(bail(ref)) {
            return null;
        }
        return ProcessDES.isAccessible(fsms.get(ref));
    }

    public Boolean testOpacity(String ref) {
        if(bail(ref)) {
            return null;
        }
        return ProcessDES.testOpacity(fsms.get(ref));
    }

    public List<String> findPrivateStates(String ref){
        if(bail(ref)) {
            return null;
        }
        return ProcessDES.findPrivateStates(fsms.get(ref));
    }

    //-- CoObservability  -------------------------------------

    public String buildUStructure(String ref, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        if(bail(ref)) {
            return null;
        }
        //TODO: Other failure checks to do ahead of time?
        TransitionSystem tS = ProcessDES.buildUStructure(fsms.get(ref), attr, agents);
        if(tS == null) {
            return null;
        }
        appendFSM(tS.getId(), tS, false);
        return tS.getId();
    }

    public String buildUStructure(List<String> plants, List<String> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        if(bail(plants) || bail(specs)) {
            return null;
        }
        List<TransitionSystem> usePl = new ArrayList<>();
        for(String s : plants) {
            usePl.add(fsms.get(s));
        }
        List<TransitionSystem> useSp = new ArrayList<>();
        for(String s : specs) {
            useSp.add(fsms.get(s));
        }
        TransitionSystem tS = ProcessDES.buildUStructure(usePl, useSp, attr, agents);
        if(tS == null) {
            return null;
        }
        appendFSM(tS.getId(), tS, false);
        return tS.getId();
    }

    public List<String> buildUStructureCrush(String ref, List<String> attr, List<Map<String, List<Boolean>>> agents){
        if(bail(ref)) {
            return null;
        }
        //TODO: Other failure checks to do ahead of time?
        List<TransitionSystem> tS = ProcessDES.buildUStructureCrush(fsms.get(ref), attr, agents);
        if(tS == null) {
            return null;
        }
        List<String> out = new ArrayList<>();
        for(TransitionSystem ts : tS) {
            appendFSM(ts.getId(), ts, false);
            out.add(ts.getId());
        }
        return out;
    }

    public Boolean isCoobservableUStruct(String ref, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        if(bail(ref)) {
            return null;
        }
        return ProcessDES.isCoobservableUStruct(fsms.get(ref), attr, agents);
    }

    public Boolean isCoobservableUStruct(List<String> plants, List<String> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        if(bail(plants) || bail(specs)) {
            return null;
        }
        List<TransitionSystem> usePl = new ArrayList<>();
        for(String s : plants) {
            usePl.add(fsms.get(s));
        }
        List<TransitionSystem> useSp = new ArrayList<>();
        for(String s : specs) {
            useSp.add(fsms.get(s));
        }
        return ProcessDES.isCoobservableUStruct(usePl, useSp, attr, agents);
    }

    public Boolean isInferenceCoobservableUStruct(String ref, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        if(bail(ref)) {
            return null;
        }
        return ProcessDES.isInferenceCoobservableUStruct(fsms.get(ref), attr, agents);
    }

    public Boolean isInferenceCoobservableUStruct(List<String> plants, List<String> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        if(bail(plants) || bail(specs)) {
            return null;
        }
        List<TransitionSystem> usePl = new ArrayList<>();
        for(String s : plants) {
            usePl.add(fsms.get(s));
        }
        List<TransitionSystem> useSp = new ArrayList<>();
        for(String s : specs) {
            useSp.add(fsms.get(s));
        }
        return ProcessDES.isInferenceCoobservableUStruct(usePl, useSp, attr, agents);
    }

    public Boolean isSBCoobservableUrvashi(List<String> refPlants, List<String> refSpecs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        if(bail(refPlants) || bail(refSpecs)) {
            return null;
        }
        List<TransitionSystem> plants = new ArrayList<>();
        List<TransitionSystem> specs = new ArrayList<>();
        for(String s : refPlants) {
            plants.add(fsms.get(s));
        }
        for(String s : refSpecs) {
            specs.add(fsms.get(s));
        }
        return ProcessDES.isSBCoobservableUrvashi(plants, specs, attr, agents);
    }

    public Boolean isIncrementalCoobservable(List<String> refPlants, List<String> refSpecs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        if(bail(refPlants) || bail(refSpecs)) {
            return null;
        }
        List<TransitionSystem> plants = new ArrayList<>();
        List<TransitionSystem> specs = new ArrayList<>();
        for(String s : refPlants) {
            plants.add(fsms.get(s));
        }
        for(String s : refSpecs) {
            specs.add(fsms.get(s));
        }
        return ProcessDES.isIncrementalCoobservable(plants, specs, attr, agents);
    }

    public Boolean isIncrementalInferenceCoobservable(List<String> refPlants, List<String> refSpecs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        if(bail(refPlants) || bail(refSpecs)) {
            return null;
        }
        List<TransitionSystem> plants = new ArrayList<>();
        List<TransitionSystem> specs = new ArrayList<>();
        for(String s : refPlants) {
            plants.add(fsms.get(s));
        }
        for(String s : refSpecs) {
            specs.add(fsms.get(s));
        }
        return ProcessDES.isIncrementalInferenceCoobservable(plants, specs, attr, agents);
    }

    public Boolean isIncrementalSBCoobservable(List<String> refPlants, List<String> refSpecs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        if(bail(refPlants) || bail(refSpecs)) {
            return null;
        }
        List<TransitionSystem> plants = new ArrayList<>();
        List<TransitionSystem> specs = new ArrayList<>();
        for(String s : refPlants) {
            plants.add(fsms.get(s));
        }
        for(String s : refSpecs) {
            specs.add(fsms.get(s));
        }
        return ProcessDES.isIncrementalSBCoobservable(plants, specs, attr, agents);
    }

    public String convertSoloPlantSpec(String ref, String newName) {
        if(bail(ref)) {
            return null;
        }
        TransitionSystem t = ProcessDES.convertSoloPlantSpec(fsms.get(ref));
        t.setId(newName);
        appendFSM(t.getId(), t, false);
        return t.getId();
    }

    //-- Manipulate  ------------------------------------------

        //-- FSM  ---------------------------------------------

    public void addFSM(String id, List<String> stateAttr, List<String> eventAttr, List<String> tranAttr) {
        appendFSM(id, new TransitionSystem(id, stateAttr, eventAttr, tranAttr), false);
    }

    public void removeFSM(String id) {
        fsms.remove(id);
    }

    public void renameFSM(String old, String newFSM) {
        if(old != null && fsms.get(old) != null) {
            TransitionSystem oldFS = fsms.get(old).copy();
            oldFS.setId(newFSM);
            fsms.remove(old);
            appendFSM(newFSM, oldFS, true);
        }
    }

    public void assignStateAttributes(String ref, List<String> stateAttr) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).setStateAttributes(stateAttr);
    }

    public void assignEventAttributes(String ref, List<String> eventAttr) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).addEventAttributes(eventAttr);
    }

    public void assignTransitionAttributes(String ref, List<String> tranAttr) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).setTransitionAttributes(tranAttr);
    }

        //-- State  -------------------------------------------

    public void addState(String ref, String stateName) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).addState(stateName);
    }

    public void addStates(String ref, int num) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        String alph = "0123456789";
        int used = 0;
        int curr = 0;
        while(used < num) {
            String nom = StringUtils.EMPTY;
            int cop = curr;
            do {
                nom = alph.charAt(cop % alph.length()) + nom;
                cop /= alph.length();
            }while(cop != 0);
            if(!stateExists(ref, nom)) {
                addState(ref, nom);
                used++;
            }
            curr++;
        }
    }

    public void removeState(String ref, String stateName) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).removeState(stateName);
    }

    public void renameState(String ref, String old, String newNom) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        if(fsms.get(ref) != null) {
            fsms.get(ref).renameState(old, newNom);
        }
    }

    public void setStateAttribute(String ref, String stateName, String attrib, boolean inValue) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).setStateAttribute(stateName, attrib, inValue);
    }

        //-- Event  -------------------------------------------

    public void addEvent(String ref, String eventName) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).addEvent(eventName);
    }

    public void addEvents(String ref, int num) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        String alph = "abcdefghijklmnopqrstuvwxyz";
        int used = 0;
        int curr = 0;
        while(used < num) {
            StringBuilder nom = new StringBuilder();
            int cop = curr;
            do {
                nom.append(alph.charAt(cop % alph.length()));
                cop /= alph.length();
            }while(cop != 0);
            if(!eventExists(ref, nom.toString())) {
                addEvent(ref, nom.toString());
                used++;
            }
            curr++;
        }
    }

    public void removeEvent(String ref, String eventName) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).removeEvent(eventName);
    }

    public void renameEvent(String ref, String old, String newNom) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        if(fsms.get(ref) != null) {
            fsms.get(ref).renameEvent(old, newNom);
        }
    }

    public void setEventAttribute(String ref, String eventName, String attrib, boolean inValue) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).setEventAttribute(eventName, attrib, inValue);
    }

        //-- Transition  --------------------------------------

    public void addTransition(String ref, String star, String even, String targ) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).addTransition(star, even, targ);
    }

    public void removeTransition(String ref, String star, String even, String targ) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).removeTransition(star, even, targ);
    }

    public void setTransitionAttribute(String ref, String star, String even, String attrib, boolean inValue) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).setTransitionAttribute(star, even, attrib, inValue);
    }

//---  Setter Methods   -----------------------------------------------------------------------

    public void setFSMStateAttributes(String ref, List<String> attri) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).setStateAttributes(attri);
    }

    public void setFSMEventAttributes(String ref, List<String> attri) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).overwriteEventAttributes(attri);
    }

    public void setFSMTransitionAttributes(String ref, List<String> attri) {
        if(ref == null || fsms.get(ref) == null) {
            return;
        }
        fsms.get(ref).setTransitionAttributes(attri);
    }

    public void assignMemoryMeasure(MemoryMeasure m) {
        lastProcessData = m;
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public MemoryMeasure getLastProcessData() {
        return lastProcessData;
    }

    //-- States  ----------------------------------------------

    public List<String> getFSMStateList(String ref){
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return fsms.get(ref).getStateNames();
    }

    public List<String> getFSMStateAttributes(String ref){
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return fsms.get(ref).getStateAttributes();
    }

    public Map<String, List<Boolean>> getFSMStateAttributeMap(String ref){
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return fsms.get(ref).getStateAttributeMap();
    }

    //-- Events  ----------------------------------------------

    public List<String> getFSMEventList(String ref){
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return fsms.get(ref).getEventNames();
    }

    public List<String> getFSMEventAttributes(String ref){
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return fsms.get(ref).getEventAttributes();
    }

    public Map<String, List<Boolean>> getFSMEventAttributeMap(String ref){
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return fsms.get(ref).getEventAttributeMap();
    }

    //-- Transitions  -----------------------------------------

    public List<String> getFSMTransitionList(String ref){
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return fsms.get(ref).getTransitionLabels();
    }

    public List<String> getFSMTransitionAttributes(String ref){
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return fsms.get(ref).getTransitionAttributes();
    }

    public Map<String, List<Boolean>> getFSMTransitionAttributeMap(String ref){
        if(ref == null || fsms.get(ref) == null) {
            return null;
        }
        return fsms.get(ref).getTransitionLabelAttributeMap();
    }

    //Need the 'get hashmap<String, ArrayList<boolean>>'

    //-- Meta  ------------------------------------------------

    public List<String> getReferences(){
        List<String> out = new ArrayList<>();
        out.addAll(fsms.keySet());
        return out;
    }

//---  Support Methods   ----------------------------------------------------------------------

    private String appendFSM(String nom, TransitionSystem fsm, boolean overwrite) {
        if(!overwrite && fsms.get(nom) != null) {
            int counter = 1;
            while(fsms.get(nom + " (" + counter + ")") != null) {
                counter++;
            }
            nom = nom + " (" + counter + ")";
        }
        fsms.put(nom, fsm);
        fsm.setId(nom);
        return nom;
    }

    /**
     *
     * Return True on bail means to *not* do the thing
     *
     * @param ref
     * @return
     */

    private boolean bail(String ref) {
        return ref == null || fsms.get(ref) == null;
    }

    private boolean bail(List<String> ref) {
        if(ref != null) {
            for(String s : ref) {
                if(bail(s)) {
                    return true;
                }
            }
        }
        else {
            return true;
        }
        return false;
    }

    private boolean bailMulti(List<String> ref) {
        return bail(ref) || ref.size() < 2;
    }

}
