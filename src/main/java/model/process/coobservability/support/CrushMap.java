package model.process.coobservability.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class CrushMap {

//---  Instance Variables   -------------------------------------------------------------------

    // Maps the State name to a list of group IDs they belong to
    private Map<String, Set<Integer>> crushMapping;

//---  Constructors   -------------------------------------------------------------------------

    public CrushMap() {
        crushMapping = new HashMap<String, Set<Integer>>();
    }

//---  Operations   ---------------------------------------------------------------------------

    public void assignStateGroup(String state, int group) {
        if(crushMapping.get(state) == null) {
            crushMapping.put(state, new HashSet<Integer>());
        }
        crushMapping.get(state).add(group);
    }

    public String getOutput(List<String> importantStates) {
        StringBuilder sb = new StringBuilder();

        Map<Integer, Set<String>> mapCrush = new HashMap<Integer, Set<String>>();

        for(String s : crushMapping.keySet()) {
            for(int i : crushMapping.get(s)) {
                if(mapCrush.get(i) == null) {
                    mapCrush.put(i, new HashSet<String>());
                }
                mapCrush.get(i).add(s);
            }
        }

        for(int i : mapCrush.keySet()) {
            sb.append("\t" + i +": ");
            for(String s : mapCrush.get(i)) {
                sb.append(s + ",");
            }
            sb.append(StringUtils.LF);
        }


        if(importantStates != null && !importantStates.isEmpty()) {
            sb.append("By request, in particular:\n");
            for(String s : importantStates) {
                sb.append("\t" + s + ": ");
                for(int i : crushMapping.get(s)) {
                    sb.append(i + ", ");
                }
                sb.append(StringUtils.LF);
            }
        }

        return sb.toString();
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public List<Integer> getStateMemberships(String stateName){
        if(!crushMapping.containsKey(stateName)) {
            return null;
        }
        return new ArrayList<>(crushMapping.get(stateName));
    }

    public boolean hasStateMembership(String stateName, int group) {
        if(crushMapping.get(stateName) != null) {
            return crushMapping.get(stateName).contains(group);
        }
        return false;
    }

}
