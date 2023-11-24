package model.process.coobservability.deciding;

import java.util.List;

import model.fsm.TransitionSystem;
import model.process.coobservability.support.Agent;

public class DecideInfCoobs extends DecideCoobs{

    public DecideInfCoobs(List<TransitionSystem> inPlan, List<TransitionSystem> inSpe, List<String> attrIn, List<Agent> agentsIn) {
        super(inPlan, inSpe, attrIn, agentsIn);
    }

    public DecideInfCoobs(List<String> events, TransitionSystem specStart, List<String> attrIn, List<Agent> agentsIn) {
        super(events, specStart, attrIn, agentsIn);
    }

    public DecideInfCoobs(TransitionSystem root, List<String> attr, List<Agent> in) {
        super(root, attr, in);
    }

    public DecideInfCoobs() {

    }

    @Override
    public boolean decideCondition() {
        return super.decideCondition() ? true : ustruct.getFilteredIllegalConfigStates().isEmpty();
    }

    @Override
    public DecideCondition constructDeciderCoobs(List<String> events, TransitionSystem specStart, List<String> attr, List<Agent> agents) {
        return new DecideInfCoobs(events, specStart, attr, agents);
    }

}
