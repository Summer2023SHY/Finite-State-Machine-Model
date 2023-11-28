package model.process.memory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class UStructMemoryMeasure extends ConcreteMemoryMeasure{

    private int stateSize;

    private int transSize;

    private List<Integer> numberAgentGroups;

    private List<Integer> sizeStateGroups;

    public void assignStateSize(int in) {
        stateSize = in;
    }

    public void assignTransitionSize(int in) {
        transSize = in;
    }

    public void logAgentGroupSize(int in) {
        if(numberAgentGroups == null) {
            numberAgentGroups = new ArrayList<Integer>();
        }
        numberAgentGroups.add(in);
    }

    public void logStateGroupSize(int in) {
        if(sizeStateGroups == null) {
            sizeStateGroups = new ArrayList<Integer>();
        }
        sizeStateGroups.add(in);
    }

    private double getAverageAgents() {
        return threeSig(averageList(numberAgentGroups));
    }

    private double getAverageStates() {
        return threeSig(averageList(sizeStateGroups));
    }

    private double averageList(List<Integer> in) {
        int total = 0;
        for(int i : in) {
            total += i;
        }
        return (double)total / in.size();
    }

    @Override
    public String produceOutputLog() {
        String out = super.produceOutputLog();
        out += "\n\t\t\t\tState Size: " + stateSize + ", Transition Size: " + transSize;
        if(numberAgentGroups != null) {
            out += "\n\t\t\t\tAverage Number of State Groups per Agent: " + getAverageAgents() + StringUtils.LF;
            out += "\t\t\t\tAverage Number of States per State Group: " + getAverageStates() + StringUtils.LF;
            out += "\t\t\t\tMaximum Number of States in a State Group: " + getMaximumStates();
        }
        return out;
    }

    private int getMaximumStates() {
        int out = -1;
        for(int i : sizeStateGroups) {
            if(out == -1 || i > out) {
                out = i;
            }
        }
        return out;
    }

    @Override
    public List<String> getOutputGuide(){
        List<String> out = super.getOutputGuide();
        out.add("Number of States in UStructure");
        out.add("Number of Transitions in UStructure");
        if(numberAgentGroups != null) {
            out.add("Average Number of State Groups per Agent");
            out.add("Average Number of States per State Group");
            out.add("Maximum Number of States in a State Group");
        }
        return out;
    }

    @Override
    public List<Double> getStoredData(){
        List<Double> out = super.getStoredData();
        out.add((double)stateSize);
        out.add((double)transSize);
        if(numberAgentGroups != null) {
            out.add(getAverageAgents());
            out.add(getAverageStates());
            out.add((double)getMaximumStates());
        }
        return out;
    }

    public static ConcreteMemoryMeasure produceBlank() {
        UStructMemoryMeasure out = new UStructMemoryMeasure();
        out.assignStateSize(0);
        out.assignTransitionSize(0);
        out.assignTestResult(true);
        return out;
    }

}
