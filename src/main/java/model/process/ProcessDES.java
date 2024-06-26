package model.process;

import java.util.List;
import java.util.Map;

import model.fsm.TransitionSystem;
import model.process.memory.ReceiveMemoryMeasure;

public class ProcessDES {

//---  Static Assignments   -------------------------------------------------------------------

    public static void assignReferences(ReceiveMemoryMeasure rmm, String init, String mark, String priv, String obs, String cont, String bad, String good) {
        ProcessAnalysis.assignAttributeReferences(priv, init);
        ProcessOperation.assignAttributeReferences(init, obs);
        ProcessClean.assignAttributeReferences(init, mark);
        ProcessCoobservability.assignReferences(rmm, cont, obs, init, bad, good);
    }

    public static void assignEndAtFirstCounterexample(boolean in) {
        ProcessCoobservability.assignEndAtFirstCounterexample(in);
    }

//---  Operations   ---------------------------------------------------------------------------

    //-- Operation  -------------------------------------------

    public static TransitionSystem buildObserver(TransitionSystem in) {
        return ProcessOperation.buildObserver(in);
    }

    public static TransitionSystem product(List<TransitionSystem> fsms) {
        return ProcessOperation.product(fsms);
    }

    public static TransitionSystem parallelComposition(List<TransitionSystem> fsms) {
        return ProcessOperation.parallelComposition(fsms);
    }

    public static TransitionSystem permissiveUnion(List<TransitionSystem> fsms) {
        return ProcessOperation.permissiveUnion(fsms);
    }

    public static TransitionSystem convertSoloPlantSpec(TransitionSystem in) {
        return ProcessCoobservability.convertSoloPlantSpec(in);
    }

    //-- Clean  -----------------------------------------------

    public static TransitionSystem trim(TransitionSystem in)  {
        return ProcessClean.trim(in);
    }

    public static TransitionSystem makeAccessible(TransitionSystem in) {
        return ProcessClean.makeAccessible(in);
    }

    public static TransitionSystem makeCoAccessible(TransitionSystem in)  {
        return ProcessClean.makeCoAccessible(in);
    }

    //-- Analysis  --------------------------------------------

    public static Boolean isBlocking(TransitionSystem in)  {
        return ProcessAnalysis.isBlocking(in);
    }

    public static Boolean isAccessible(TransitionSystem in)  {
        return ProcessAnalysis.isAccessible(in);
    }

    public static List<String> findPrivateStates(TransitionSystem in){
        return ProcessAnalysis.findPrivateStates(in);
    }

    public static Boolean testOpacity(TransitionSystem in) {
        return ProcessAnalysis.testOpacity(in);
    }

    public static Boolean isCoobservableUStruct(TransitionSystem plant, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.isCoobservableUStruct(plant, attr, agents);
    }

    public static Boolean isInferenceCoobservableUStruct(TransitionSystem plant, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.isInferenceCoobservableUStruct(plant, attr, agents);
    }

    public static Boolean isCoobservableUStruct(List<TransitionSystem> plant, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.isCoobservableUStruct(plant, specs, attr, agents);
    }

    public static Boolean isInferenceCoobservableUStruct(List<TransitionSystem> plant, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.isInferenceCoobservableUStruct(plant, specs, attr, agents);
    }

    public static Boolean isSBCoobservableUrvashi(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.isSBCoobservable(plants, specs, attr, agents);
    }

    public static Boolean isIncrementalCoobservable(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.isIncrementalCoobservable(plants, specs, attr, agents);
    }

    public static Boolean isIncrementalInferenceCoobservable(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.isIncrementalInferenceCoobservable(plants, specs, attr, agents);
    }

    public static Boolean isIncrementalSBCoobservable(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.isIncrementalSBCoobservable(plants, specs, attr, agents);
    }

    //-- UStructure  ------------------------------------------

    public static TransitionSystem buildUStructure(TransitionSystem plant, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.constructUStruct(plant, attr, agents).getUStructure();
    }

    public static TransitionSystem buildUStructure(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.constructUStruct(plants, specs, attr, agents).getUStructure();
    }

    public static List<TransitionSystem> buildUStructureCrush(TransitionSystem plant, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.constructUStruct(plant, attr, agents).getCrushUStructures();
    }

    public static List<TransitionSystem> buildUStructureCrush(List<TransitionSystem> plants, List<TransitionSystem> specs, List<String> attr, List<Map<String, List<Boolean>>> agents) {
        return ProcessCoobservability.constructUStruct(plants, specs, attr, agents).getCrushUStructures();
    }

}
