package model.process.coobservability.deciding;

import java.util.List;
import java.util.Set;

import model.fsm.TransitionSystem;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.IllegalConfig;
import model.process.memory.MemoryMeasure;

public interface DecideCondition {

    public boolean decideCondition() ;

    public DecideCondition constructDeciderCoobs(List<String> events, TransitionSystem specStart, List<String> attr, List<Agent> agents);

    public Set<IllegalConfig> getCounterExamples();

    public void addComponent(TransitionSystem next, boolean plant);

    public MemoryMeasure produceMemoryMeasure();

}
