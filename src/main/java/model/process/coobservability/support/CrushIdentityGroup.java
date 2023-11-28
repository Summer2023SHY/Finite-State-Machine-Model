package model.process.coobservability.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class CrushIdentityGroup implements Comparable<CrushIdentityGroup> {

    private CrushIdentityGroup parentGroup;

    private String event;

    private Set<String> thisGroup;

    public CrushIdentityGroup(CrushIdentityGroup parent, String ev, Set<String> group) {
        parentGroup = parent;
        event = StringUtils.defaultString(ev);
        thisGroup = parent != null && parent.search(group) ? null : group;
    }

    public Set<String> getGroup(){
        return thisGroup;
    }

    public int getSize() {
        return thisGroup.size();
    }

    private boolean search(Set<String> check) {
        return thisGroup.equals(check) ? true : parentGroup != null ? parentGroup.search(check) : false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        if(thisGroup == null) {
            return parentGroup.toString();
        }
        ArrayList<String> b = new ArrayList<String>();
        b.addAll(thisGroup);
        Collections.sort(b);
        return (parentGroup == null ? "Null" : parentGroup.toString()) + "\n by " + event + " to \n" + b.toString();
    }

    @Override
    public int compareTo(CrushIdentityGroup o) {
        return this.toString().compareTo(o.toString());
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }

}
