package model.process.memory;

import java.util.List;

import model.fsm.TransitionSystem;

public interface MemoryMeasure {

    public double getAverageMemoryUsage();

    public double getMaximumMemoryUsage();

    public boolean getTestResult();

    public String produceOutputLog();

    public List<String> getOutputGuide();

    public List<Double> getStoredData();

    public TransitionSystem getReserveSystem();

    public void reserveTransitionSystem(TransitionSystem in);

}
