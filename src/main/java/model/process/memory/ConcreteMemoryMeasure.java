package model.process.memory;

import java.util.ArrayList;
import java.util.List;

import model.fsm.TransitionSystem;

public class ConcreteMemoryMeasure implements MemoryMeasure {

//---  Constants   ----------------------------------------------------------------------------

    public static final Double TEST_RESULT_TRUE = 1.0;
    public static final Double TEST_RESULT_FALSE = 0.0;

//---  Instance Variables   -------------------------------------------------------------------

    private long startingMemory;

    private List<Long> spaceUsage;

    private TransitionSystem hold;

    private boolean testResult;

//---  Constructors   -------------------------------------------------------------------------

    public ConcreteMemoryMeasure() {
        startingMemory = getMemoryUsage();
        spaceUsage = new ArrayList<Long>();
    }

//---  Operations   ---------------------------------------------------------------------------

    public void logMemoryUsage() {
        spaceUsage.add(getMemoryUsage());
    }

    @Override
    public String produceOutputLog() {
        return "\t\t\t\tAverage Memory: " + getAverageMemoryUsage() + " Mb, Max Memory: " + getMaximumMemoryUsage() + " Mb";
    }

    public static ConcreteMemoryMeasure produceBlank() {
        return new ConcreteMemoryMeasure();
    }

    @Override
    public void reserveTransitionSystem(TransitionSystem in) {
        hold = in;
    }

    public void assignTestResult(boolean in) {
        testResult = in;
    }

    public void setStartingMemory(long l) {
        startingMemory = l;
    }

//---  Getter Methods   -----------------------------------------------------------------------

    @Override
    public TransitionSystem getReserveSystem() {
        return hold;
    }

    private long getMemoryUsage() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public double getAverageMemoryUsage() {
        long add = 0;
        for(Long l : spaceUsage) {
            add += l - startingMemory;
        }
        if(spaceUsage.isEmpty()) {
            spaceUsage.add(0L);
        }
        return threeSig(inMB((add /spaceUsage.size())));
    }

    public double getMaximumMemoryUsage() {
        long max = 0;
        for(Long l : spaceUsage) {
            if(l - startingMemory > max) {
                max = l - startingMemory;
            }
        }
        return threeSig(inMB(max));
    }

    public boolean getTestResult() {
        return testResult;
    }

//---  Support Methods   ----------------------------------------------------------------------

    private static double inMB(long in) {
        return ((double)in) / 1000000.0;
    }

    protected static Double threeSig(double in) {
        String use = in+"0000";
        int posit = use.indexOf(".") + 4;
        try {
            return Double.parseDouble(use.substring(0, posit));
        }
        catch(NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public List<String> getOutputGuide() {
        List<String> out = new ArrayList<>();

        out.add("Test Outcome");
        out.add("Average Memory Consumption (Mb)");
        out.add("Maximum Memory Consumption (Mb)");

        return out;
    }

    @Override
    public List<Double> getStoredData() {
        List<Double> out = new ArrayList<>();

        out.add(getTestResult() ? TEST_RESULT_TRUE : TEST_RESULT_FALSE);
        out.add(getAverageMemoryUsage());
        out.add(getMaximumMemoryUsage());

        return out;
    }

}
