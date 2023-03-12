package model.process.memory;

import java.util.ArrayList;

public class IncrementalMemoryMeasure extends ConcreteMemoryMeasure{

	private ArrayList<MemoryMeasure> endStates;
	private ArrayList<Integer> endNumComponents;
	private ArrayList<ArrayList<String>> componentNames;
	
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
	
	public void logComponentNames(ArrayList<String> in) {
		if(componentNames == null) {
			componentNames = new ArrayList<ArrayList<String>>();
		}
		componentNames.add(in);
	}
	
	@Override
	public String produceOutputLog() {
		String out = super.produceOutputLog();
		
		if(endNumComponents != null) {
		
			out += "\n\t\t\t\tPerformance Log Within Incremental Loop:";
			
			for(int i = 0; i < endStates.size(); i++) {
				out += "\n\t\t\t" + ((i+1) + ": " + endNumComponents.get(i) + " Components: " + componentNames.get(i) + "\n") + (endStates.get(i)).produceOutputLog();
			}
		}
		
		return out;
	}
	
}