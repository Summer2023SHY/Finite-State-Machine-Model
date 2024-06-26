package model.process.memory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class IncrementalMemoryMeasure extends ConcreteMemoryMeasure{

    private List<MemoryMeasure> endStates;
    private List<Integer> endNumComponents;
    private List<List<String>> componentNames;
    private int[] heuristics;

    public void logHeuristics(int[] in) {
        heuristics = in;
    }

    public void logFinishedProcess(MemoryMeasure in) {
        if(endStates == null) {
            endStates = new ArrayList<MemoryMeasure>();
        }
        endStates.add(in);
    }

    public void logFinishedComponents(int in) {
        if(endNumComponents == null) {
            endNumComponents = new ArrayList<Integer>();
        }
        endNumComponents.add(in);
    }

    public void logComponentNames(List<String> in) {
        if(componentNames == null) {
            componentNames = new ArrayList<List<String>>();
        }
        componentNames.add(in);
    }

    @Override
    public String produceOutputLog() {
        StringBuilder out = new StringBuilder(super.produceOutputLog());

        if(heuristics != null && heuristics.length > 2)
            out.append("\n\t\t\t\tUsing Heuristics: " + heuristics[0] + ", " + heuristics[1] + ", " + heuristics[2]);

        if(endNumComponents != null) {

            out.append("\n\t\t\t\tPerformance Log Within Incremental Loop:");

            for(int i = 0; i < endStates.size(); i++) {
                out.append("\n\t\t\t" + ((i+1) + ": " + endNumComponents.get(i) + " Components: " + componentNames.get(i) + StringUtils.LF) + (endStates.get(i)).produceOutputLog());
            }
        }

        return out.toString();
    }

    @Override
    public List<String> getOutputGuide(){
        List<String> out = endStates != null  ? endStates.get(0).getOutputGuide() : super.getOutputGuide();

        out.add("Num Components");

        if(heuristics != null && heuristics.length > 2) {
            out.add("Heuristic A");
            out.add("Heuristic B");
            out.add("Heuristic C");
        }

        return out;
    }

    @Override
    public List<Double> getStoredData(){
        List<Double> out = super.getStoredData();

        boolean printOnce = true;

        if(endStates != null) {
            for(int i = 0; i < endStates.size(); i++) {
                out.add(null);
                out.addAll(endStates.get(i).getStoredData());
                out.add((double)(endNumComponents.get(i)));
                if(printOnce && heuristics != null && heuristics.length > 2) {
                    out.add((double)(heuristics[0]));
                    out.add((double)(heuristics[1]));
                    out.add((double)(heuristics[2]));
                    printOnce = false;
                }
            }
        }

        return out;
    }

}
