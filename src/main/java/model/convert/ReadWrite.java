package model.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.fsm.TransitionSystem;

public class ReadWrite {

//---  Constants   ----------------------------------------------------------------------------

    private static String SEPARATOR;
    private static String REGION_SEPARATOR;
    private static String TRUE_SYMBOL;
    private static String FALSE_SYMBOL;

    /** Private constructor */
    private ReadWrite() {
    }

//---  Operations   ---------------------------------------------------------------------------

    public static void assignConstants(String separator, String regionSeparator, String trueSymbol, String falseSymbol) {
        SEPARATOR = separator;
        REGION_SEPARATOR = regionSeparator;
        TRUE_SYMBOL = trueSymbol;
        FALSE_SYMBOL = falseSymbol;
    }

    public static String generateFile(TransitionSystem in) {
        StringBuilder out = new StringBuilder();

        out.append(in.getId() + "\n");
        out.append(REGION_SEPARATOR + "\n");

        List<String> stateAttr = in.getStateAttributes();
        List<String> eventAttr = in.getEventAttributes();
        List<String> tranAttr = in.getTransitionAttributes();

        attribute(stateAttr, out);
        attribute(eventAttr, out);
        attribute(tranAttr, out);

        out.append(REGION_SEPARATOR + "\n");

        for(String s : in.getStateNames()) {
            StringBuilder build = new StringBuilder(s);
            for(int i = 0; i < stateAttr.size(); i++) {
                build.append(SEPARATOR + (in.getStateAttribute(s, stateAttr.get(i)) ? TRUE_SYMBOL : FALSE_SYMBOL));
            }
            out.append(build.append("\n"));
        }
        out.append(REGION_SEPARATOR + "\n");
        for(String s : in.getEventNames()) {
            StringBuilder build = new StringBuilder(s);
            for(int i = 0; i < eventAttr.size(); i++) {
                build.append(SEPARATOR + (in.getEventAttribute(s, eventAttr.get(i)) ? TRUE_SYMBOL : FALSE_SYMBOL));
            }
            out.append(build.append("\n"));
        }
        out.append(REGION_SEPARATOR + "\n");

        for(String s : in.getStateNames()) {
            for(String e : in.getStateTransitionEvents(s)) {
                for(String t : in.getStateEventTransitionStates(s, e)) {
                    String build = s + SEPARATOR + e + SEPARATOR + t;
                    for(int i = 0; i < tranAttr.size(); i++) {
                        build += SEPARATOR + (in.getTransitionAttribute(s, e, tranAttr.get(i)) ? TRUE_SYMBOL : FALSE_SYMBOL);
                    }
                    out.append(build + "\n");
                }
            }
        }
        return out.toString();
    }

    public static TransitionSystem readFile(String in) {
        return readFile(in.split("\n"));
    }

    public static TransitionSystem readFile(String[] lines) {

        List<String> stateAttr = new ArrayList<>();
        for(String s : lines[2].split(SEPARATOR)) {
            if(!s.isEmpty())
                stateAttr.add(s);
        }
        List<String> eventAttr = new ArrayList<>();
        for(String s : lines[3].split(SEPARATOR)) {
            if(!s.isEmpty())
                eventAttr.add(s);
        }
        List<String> tranAttr = new ArrayList<>();
        for(String s : lines[4].split(SEPARATOR)) {
            if(!s.isEmpty())
                tranAttr.add(s);
        }

        TransitionSystem out = new TransitionSystem(lines[0], stateAttr, eventAttr, tranAttr);

        int index = 6;
        while(index < lines.length && !lines[index].equals(REGION_SEPARATOR)) {
            String[] info = lines[index++].split(SEPARATOR);
            out.addState(info[0]);
            for(int i = 0; i < stateAttr.size(); i++) {
                out.setStateAttribute(info[0], stateAttr.get(i), info[i+1].equals(TRUE_SYMBOL));
            }
        }
        index++;
        while(index < lines.length && !lines[index].equals(REGION_SEPARATOR)) {
            String[] info = lines[index++].split(SEPARATOR);
            out.addEvent(info[0]);
            for(int i = 0; i < eventAttr.size(); i++) {
                out.setEventAttribute(info[0], eventAttr.get(i), info[i+1].equals(TRUE_SYMBOL));
            }
        }
        index++;
        while(index < lines.length && !lines[index].equals(REGION_SEPARATOR)) {
            String[] info = lines[index++].split(SEPARATOR);
            out.addTransition(info[0], info[1], info[2]);
            for(int i = 0; i < tranAttr.size(); i++) {
                out.setTransitionAttribute(info[0], info[1], tranAttr.get(i), info[i+3].equals(TRUE_SYMBOL));
            }
        }
        return out;
    }

    public static TransitionSystem readFile(List<String> lines) {
        return readFile(lines.toArray(new String[0]));
    }

    public static TransitionSystem readDESpotFile(String in) {
        String[] use = in.split("\n");
        int curr = 0;

        while(!use[curr].contains("Header name")) {
            curr++;
        }
        String line = use[curr].substring(use[curr].indexOf('\"'));
        line = line.substring(0, line.indexOf('\"'));
        TransitionSystem out = new TransitionSystem(line);

        //TODO: Finish this converter


        return out;
    }

    public static String generateAgentFile(String nom, List<Map<String, List<Boolean>>> agents, List<String> attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append(nom + "\n");
        sb.append(REGION_SEPARATOR + "\n");
        attribute(attributes, sb);
        sb.append(REGION_SEPARATOR + "\n");
        sb.append(agents.get(0).keySet().size() + "\n");
        sb.append(REGION_SEPARATOR + "\n");

        for(Map<String, List<Boolean>> s : agents) {
            for(String t : s.keySet()) {
                sb.append(t);
                for(Boolean u : s.get(t)) {
                    sb.append(SEPARATOR + (u ? TRUE_SYMBOL : FALSE_SYMBOL));
                }
                sb.append("\n");
            }
            sb.append(REGION_SEPARATOR + "\n");
        }
        return sb.toString();
    }

    public static List<Map<String, List<Boolean>>> readAgentFile(String in) {
        return readAgentFile(in.split("\n"));
    }

    public static List<Map<String, List<Boolean>>> readAgentFile(String[] lines) {
        List<Map<String, List<Boolean>>> out = new ArrayList<Map<String, List<Boolean>>>();
        int index = 4;
        int size = Integer.parseInt(lines[index]);
        index += 2;
        while(index < lines.length) {
            Map<String, List<Boolean>> use = new HashMap<String, List<Boolean>>();
            for(int i = 0; i < size; i++) {
                String[] data = lines[index + i].split(SEPARATOR);
                List<Boolean> need = new ArrayList<Boolean>();
                for(int j = 1; j < data.length; j++) {
                    need.add(data[j].equals(TRUE_SYMBOL));
                }
                use.put(data[0], need);
            }
            out.add(use);
            index += size + 1;
        }
        return out;
    }

    public static List<Map<String, List<Boolean>>> readAgentFile(List<String> lines) {
        return readAgentFile(lines.toArray(new String[0]));
    }

//---  Support Methods   ----------------------------------------------------------------------

    private static StringBuilder attribute(List<String> use, StringBuilder out) {
        for(int i = 0; i < use.size(); i++) {
            out.append(use.get(i) + (i + 1 < use.size() ? SEPARATOR : ""));
        }
        out.append(SEPARATOR + "\n");
        return out;
    }

}
