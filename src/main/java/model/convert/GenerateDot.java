package model.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import model.AttributeList;
import model.fsm.TransitionSystem;

public class GenerateDot {

//---  Constants   ----------------------------------------------------------------------------

    private static final String INITIAL_STATE_MARKER = "ArbitraryUnusedNameNoWorriesJustGrooving";
    //TODO: Make sure there are a lot of potential colors here, preferably prime, to avoid reuse
    private static final ColorPack[] backgroundColorCycle = new ColorPack[] {new ColorPack(0x4f, 0x4f, 0x4f),
                                                                             new ColorPack(0x96, 0x32, 0x32),
                                                                             new ColorPack(0x00, 0x80, 0x00),
                                                                             new ColorPack(0x5b, 0x32, 0x92),
                                                                             new ColorPack(0xff, 0xa5, 0x00),
                                                                             new ColorPack(0x7f, 0xff, 0x00),
                                                                             new ColorPack(0x00, 0xff, 0xff),
                                                                             new ColorPack(0x00, 0x00, 0xff),
                                                                             new ColorPack(0xff, 0x00, 0xff),
                                                                             new ColorPack(0xee, 0xe8, 0xaa),
                                                                             new ColorPack(0x64, 0x95, 0xed),
                                                                             new ColorPack(0xff, 0x69, 0xb4),
                                                                             new ColorPack(0x8b, 0x45, 0x13),
                                                                             };

    private static final String SUB_START = "_{";
    private static final String SUP_START = "^{";
    private static final String SCRIPT_END = "}";

    private static final String SUB_CONVERT_START = "<sub>";
    private static final String SUB_CONVERT_END = "</sub>";
    private static final String SUP_CONVERT_START = "<SUP>";
    private static final String SUP_CONVERT_END = "</SUP>";

//---  Operations   ---------------------------------------------------------------------------

    public static String generateDot(TransitionSystem in) {
        HashMap<String, String> nameMap = new HashMap<String, String>();

        int counter = 0;

        ArrayList<String> states = new ArrayList<String>();
        ArrayList<String> transitions = new ArrayList<String>();

        for(String e : in.getStateNames()) {
            nameMap.put(e, "n" + counter++);
            String line = "\"" + nameMap.get(e) + "\"[label= <" + processObjectNameScripts(e) + "> shape=" + generateStateDot(in, e);
            states.add(line);
            Boolean init = in.getStateAttribute(e, AttributeList.ATTRIBUTE_INITIAL);
            if(init != null && init) {
                String use = (INITIAL_STATE_MARKER + counter);
                states.add("\"" + use + "\"[fontSize=1 shape=point];");
                transitions.add("{\"" + use + "\"}->{\"" + nameMap.get(e) + "\"};");
            }
        }
        for(String s : in.getStateNames()) {
            for(String e : in.getStateTransitionEvents(s)) {
                for(String t : in.getStateEventTransitionStates(s, e)) {
                    String trans = "{\"" + nameMap.get(s) + "\"}->{\"" + nameMap.get(t) + "\"}";
                    trans += generateTransitionDot(in, s, e);
                    transitions.add(trans);
                }
            }
        }
        StringBuilder out = new StringBuilder("digraph G {\n");
        for(String s : states) {
            out.append(s + StringUtils.LF);
        }
        for(String s : transitions) {
            out.append(s + StringUtils.LF);
        }

        return out.append('}').toString();
    }

//---  Getter Methods   -----------------------------------------------------------------------

//---  Support Methods   ----------------------------------------------------------------------

    private static String generateStateDot(TransitionSystem in, String ref) {
        Boolean bad = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_BAD);
        Boolean good = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_GOOD);
        Boolean mark = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_MARKED);
        Boolean priv = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_PRIVATE);
        bad = bad == null ? false : bad;
        good = good == null ? false : good;
        mark = mark == null ? false : mark;
        priv = priv == null ? false : priv;
        StringBuilder line = new StringBuilder(mark || bad || good ? "doublecircle" : "circle");
        line.append(" color=\"");
        if(priv) {
            line.append(bad ? "purple" : "orange");
        }
        else {
            line.append(bad ? "red" : good ? "green" : "black");
        }
        line.append("\" style=wedged fillcolor=\"");

        int count = 0;        // how do we actually record multiple colors onto one node oh god oh no
        boolean first = true;
        boolean second = false;
        while(count < 100) {
            if(in.getStateAttribute(ref, Integer.toString(count)) != null) {
                line.append((first ? StringUtils.EMPTY : ":") + backgroundColorCycle[count % backgroundColorCycle.length].cycleColor(count / backgroundColorCycle.length));
                if(!first) {
                    second = true;
                }
                first = false;
            }
            count++;
        }

        if(second) {
            line.append("\" style=wedged");
        }
        else {
            if(first) {
                line.append("white");
            }
            line.append("\" style=filled");
        }
        line.append(" fontsize=\"28\"");
        return line.append("];").toString();
    }

    /*

        line += "\" style=filled fillcolor=red];";
        return line;
     *
     */

    private static String generateTransitionDot(TransitionSystem in, String state, String event) {
        StringBuilder trans = new StringBuilder("[label = <" + processObjectNameScripts(event) + "> color=\"");
        Boolean obs = in.getEventAttribute(event, AttributeList.ATTRIBUTE_OBSERVABLE);
        Boolean atkObs = in.getEventAttribute(event, AttributeList.ATTRIBUTE_ATTACKER_OBSERVABLE);
        Boolean cont = in.getEventAttribute(event, AttributeList.ATTRIBUTE_CONTROLLABLE);
        Boolean bad = in.getTransitionAttribute(state, event, AttributeList.ATTRIBUTE_BAD);
        trans.append(obs == null || obs ? "black" : "red");
        trans.append("\" arrowhead=\"normal");
        trans.append(atkObs != null && atkObs ? "odot" : StringUtils.EMPTY);
        trans.append(cont != null && cont ? "diamond" : StringUtils.EMPTY);
        trans.append("\" style=\"");
        trans.append(bad != null && bad ? "dashed" : StringUtils.EMPTY);
        trans.append("\" fontsize=\"28");
        trans.append("\"];");
        return trans.toString();
    }

    /**
     *
     * TODO: Needs to recursively search these out in case of nested occurences
     *
     * @param in
     * @return
     */

    private static String processObjectNameScripts(String in) {
        String out = new String(in);
        out = out.replace("<", "&lt;");
        out = out.replace(">", "&gt;");
        while(out.contains(SUB_START)) {
            out = out.substring(0, out.indexOf(SUB_START)) + out.substring(out.indexOf(SUB_START)).replaceFirst(SCRIPT_END, SUB_CONVERT_END);
            out = out.replaceFirst(Pattern.quote(SUB_START), SUB_CONVERT_START);
        }
        while(out.contains(SUP_START)) {
            out = out.substring(0, out.indexOf(SUP_START)) + out.substring(out.indexOf(SUP_START)).replaceFirst(SCRIPT_END, SUP_CONVERT_END);
            out = out.replaceFirst(Pattern.quote(SUP_START), SUP_CONVERT_START);
        }
        return out;
    }

}
