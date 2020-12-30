package model.fsm.component;

import java.util.LinkedList;

public class Attribute {

	private String id;
	
	private boolean value;
	
	private boolean mergeRule;
	
	private Attribute wrap;
	
	public Attribute(String inId, boolean inRule) {
		id = inId;
		mergeRule = inRule;
		value = false;
	}
	
	public void setValue(String ref, boolean in) {
		if(id.equals(ref)) {
			value = in;
		}
		if(wrap != null) {
			wrap.setValue(ref, in);
		}
	}
	
	public Boolean getValue(String ref) {
		if(id.equals(ref)) {
			return value;
		}
		if(wrap != null) {
			return wrap.getValue(ref);
		}
		return null;
	}
	
	public boolean getMergeRule() {
		return mergeRule;
	}
	
	public void setAttributes(LinkedList<String> in, LinkedList<Boolean> rule) {
		if(in != null && in.size() != 0) {
			wrap = new Attribute(in.poll(), rule.poll());
			wrap.setAttributes(in, rule);
		}
	}
	
	public LinkedList<String> getAttributes(){
		LinkedList<String> out = new LinkedList<String>();
		out.add(id);
		if(wrap != null) {
			out.addAll(wrap.getAttributes());
		}
		return out;
	}
	
	public void addWrapper(String ref, boolean rule) {
		if(id.equals(ref)) {
			return;
		}
		if(wrap != null) {
			wrap.addWrapper(ref, rule);
		}
		else {
			wrap = new Attribute(ref, rule);
		}
	}
	
}