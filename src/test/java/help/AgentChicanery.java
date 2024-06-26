package help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * Notation used is 3D boolean array; first level is for agent data represented as 2D boolean array, second level is each event's
 * observability/controllability data in a length 2 boolean array (so total size is # agents * # events * 2)
 *
 * Each event is {[is_observable], [is_controllable]} in the order of events described by the EventSets.EVENT_LIST_... in the generateAgentSet function call.
 *
 * Easy way to initialize and set exactly what values you want can be seen in generateAgentsDTP where I added some automation to configuring the boolean[][][].
 *
 * @author SirBo
 *
 */

public class AgentChicanery {

    private static final int OBS = 0;
    private static final int CTR = 1;

//---  Operations   ---------------------------------------------------------------------------

    public static List<Map<String, List<Boolean>>> generateAgentsA() {
        boolean[][][] agentInfo = new boolean[][][] {    {    //Agent 1
              {true, false},    //a1
              {true, false},    //a2
              {false, false},    //b1
              {false, false},    //b2
              {true, true}        //c
            },
             {    //Agent 2
              {false, false},
              {false, false},
              {true, false},
              {true, false},
              {true, true}
            }
        };
        return generateAgentSet(agentInfo, EventSets.EVENT_LIST_A);
    }

    public static List<Map<String, List<Boolean>>> generateAgentsB(){
        boolean[][][] agentInfo = new boolean[][][] {    {    //Agent 1
              {true, false},    //a
              {false, false},    //b
              {false, false},    //c
              {true, false},    //d
              {false, true}        //s
            },
             {    //Agent 2
              {false, false},
              {true, false},
              {false, false},
              {true, false},
              {false, true}
            },
             {    //Agent 3
              {false, false},
              {false, false},
              {true, false},
              {true, false},
              {false, true}
            },
        };

        return generateAgentSet(agentInfo, EventSets.EVENT_LIST_B);
    }

    public static List<Map<String, List<Boolean>>> generateAgentsB2(){
        boolean[][][] agentInfo = new boolean[][][] {    {    //Agent 1
              {true, false},    //a
              {false, false},    //b
              {false, false},    //c
              {true, false},    //d
              {false, true}        //s
            },
             {    //Agent 2
              {false, false},
              {true, false},
              {false, false},
              {true, false},
              {false, true}
            },
        };

        return generateAgentSet(agentInfo, EventSets.EVENT_LIST_B);
    }

    public static List<Map<String, List<Boolean>>> generateAgentsC() {
        boolean[][][] agentInfo = new boolean[][][] {    {    //Agent 1
              {true, false},    //a1
              {true, false},    //a2
              {false, false},    //b1
              {false, false},    //b2
              {false, true},        //c
              {false, false}        //d
            },
             {    //Agent 2
              {false, false},
              {false, false},
              {true, false},
              {true, false},
              {false, true},
              {false, false}
            }
        };
        return generateAgentSet(agentInfo, EventSets.EVENT_LIST_C);
    }

    public static List<Map<String, List<Boolean>>> generateAgentsD() {
        boolean[][][] agentInfo = new boolean[][][] {    {    //Agent 1
              {true, false},    //a
              {false, false},    //b
              {false, true},    //c
            },
             {    //Agent 2
              {false, false},
              {true, true},
              {false, true},
            }
        };
        return generateAgentSet(agentInfo, EventSets.EVENT_LIST_D);
    }

    public static List<Map<String, List<Boolean>>> generateAgentsE(){
        boolean[][][] agentInfo = new boolean[][][] {    {    //Agent 1
              {true, false},    //a1
              {true, false},    //a2
              {false, false},    //b1
              {false, false},    //b2
              {false, false},    //c1
              {false, false},    //c2
              {false, false},    //c3
              {true, false},    //d
              {true, true},    //s
            },
             {    //Agent 2
              {false, false},    //a1
              {false, false},    //a2
              {true, false},    //b1
              {true, false},    //b2
              {false, false},    //c1
              {false, false},    //c2
              {false, false},    //c3
              {true, false},    //d
              {true, true},    //s
            },
             {    //Agent 3
              {false, false},    //a1
              {false, false},    //a2
              {false, false},    //b1
              {false, false},    //b2
              {true, false},    //c1
              {true, false},    //c2
              {true, false},    //c3
              {true, false},    //d
              {true, true},    //s
            }
        };
        return generateAgentSet(agentInfo, EventSets.EVENT_LIST_E);
    }

    public static List<Map<String, List<Boolean>>> generateAgentsFinn5(){
        boolean[][][] agentInfo = new boolean[][][] {    {
            //Agent 1
              {true, false},    //a1
              {true, false},    //a2
              {true, false},    //a3
              {true, false},    //a4
              {true, false},    //a5
              {true, false},    //a6
              {false, false},    //b1
              {false, false},    //b2
              {false, false},    //b3
              {false, false},    //b4
              {false, false},   //b5
              {false, false},    //b6
              {false, true}    //s
            },
             {    //Agent 2
                  {false, false},    //a1
                  {false, false},    //a2
                  {false, false},    //a3
                  {false, false},    //a4
                  {false, false},   //a5
                  {false, false},    //a6
                  {true, false},    //b1
                  {true, false},    //b2
                  {true, false},    //b3
                  {true, false},    //b4
                  {true, false},    //b5
                  {true, false},    //b6
                  {false, true}    //s
            },
        };

        return generateAgentSet(agentInfo, EventSets.EVENT_LIST_FINN5);
    }

    public static List<Map<String, List<Boolean>>> generateAgentsLiuOne() {
        boolean[][][] agentInfo = new boolean[][][] {    {    //Agent 1
              {true, true},        //a
              {false, false},    //b
              {false, true},    //g
            },
             {    //Agent 2
              {false, false},    //a
              {true, true},        //b
              {false, true},    //g
            },
        };
        return generateAgentSet(agentInfo, EventSets.EVENT_LIST_LIU_ONE);
    }

    public static List<Map<String, List<Boolean>>> generateAgentsUrvashi(){
        boolean[][][] agentInfo = new boolean[][][] { {
                {true, false},
                {false, false},
                {true, true},
            },
            {
                {false, false},
                {true, false},
                {true, false},
            },
        };
        return generateAgentSet(agentInfo, EventSets.EVENT_LIST_D);
    }

    public static List<Map<String, List<Boolean>>> generateAgentsDTP(){
        String[] events = EventSets.EVENT_LIST_DTP;
        boolean[][][] agentInfo = makeDefaultAgentArray(events, 2);
        assignEventAgentInfo(agentInfo, events, 0, OBS, "getFrame", "send_0", "send_1", "rcvAck_0", "rcvAck_1", "loss");
        assignEventAgentInfo(agentInfo, events, 1, OBS, "sendAck_0", "sendAck_1", "rcv_0", "rcv_1", "passToHost");

        assignEventAgentInfo(agentInfo, events, 0, CTR, "getFrame", "send_0", "send_1");
        assignEventAgentInfo(agentInfo, events, 1, CTR, "sendAck_0", "sendAck_1", "passToHost");

        return generateAgentSet(agentInfo, EventSets.EVENT_LIST_DTP);
    }

    public static List<Map<String, List<Boolean>>> generateAgentsHISC(int num){
        String[] events = new String[EventSets.EVENT_LIST_HISC.length + num * EventSets.EVENT_LIST_HISC_J.length];
        for(int i = 0; i < EventSets.EVENT_LIST_HISC.length; i++) {
            events[i] = EventSets.EVENT_LIST_HISC[i];
        }
        for(int i = 1; i < num + 1; i++) {
            String[] hold = appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_J, i);
            for(int j = 0; j < hold.length; j++) {
                events[EventSets.EVENT_LIST_HISC.length + (i - 1) * EventSets.EVENT_LIST_HISC_J.length + j] = hold[j];
            }
        }
        boolean[][][] agentInfo = makeDefaultAgentArray(events, num + 1);

        assignEventAgentInfo(agentInfo, events, 0, OBS, "take_item", "package", "allow_exit", "new_part", "part_f_obuff", "part_passes",
                                                         "part_fails", "ret_inbuff", "deposit_part", "part_ent-I", "fin_exit-I", "part_ent-II",
                                                         "fin_exit-II", "part_ent-III", "fin_exit-III");

        assignEventAgentInfo(agentInfo, events, 0, CTR, "take_item", "allow_exit", "new_part", "part_f_obuff", "part_passes", "ret_inbuff", "deposit_part",
                                                        "part_ent-I",  "part_ent-II", "part_ent-III");

        for(int i = 1; i < num + 1; i++) {
            assignEventAgentInfo(agentInfo, events, i, OBS, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_J, i));
            assignEventAgentInfo(agentInfo, events, i, CTR, appendControlNumberEventSet(EventSets.EVENT_LIST_HISC_J2, i));
        }

        return generateAgentSet(agentInfo, events);
    }

    private static String[] appendControlNumberEventSet(String[] in, int num) {
        String[] out = new String[in.length];

        for(int i = 0; i < out.length; i++) {
            out[i] = appendControlNumber(in[i], num);
        }
        return out;
    }

    private static String appendControlNumber(String in, int num) {
        return in + StringUtils.repeat('I', num);
    }

//---  Support Methods   ----------------------------------------------------------------------

    protected static List<Map<String, List<Boolean>>> generateAgentSet(boolean[][][] agentInfo, String[] eventList){
        List<Map<String, List<Boolean>>> use = new ArrayList<>();

        for(int i = 0; i < agentInfo.length; i++) {
            Map<String, List<Boolean>> agen = new HashMap<>();
            for(int j = 0; j < eventList.length; j++) {
                String e = eventList[j];
                List<Boolean> att = new ArrayList<>();
                for(int k = 0; k < EventSets.EVENT_ATTR_LIST.length; k++) {
                    att.add(agentInfo[i][j][k]);
                }
                agen.put(e, att);
            }
            use.add(agen);
        }
        return use;
    }

    private static boolean[][][] makeDefaultAgentArray(String[] eventSets, int numAgents){
        boolean[][][] out = new boolean[numAgents][eventSets.length][2];
        for(int i = 0; i < numAgents; i++) {
            for(int j = 0; j < eventSets.length; j++) {
                out[i][j][0] = false;
                out[i][j][1] = false;
            }
        }
        return out;
    }

    /**
     *
     * int pos: 0 = obs, 1 = ctr
     *
     * @param array
     * @param eventSets
     * @param agentPref
     * @param event
     * @param pos
     * @param choice
     */

    private static void assignEventAgentInfo(boolean[][][] array, String[] eventSets, int agentPref, int pos, String ... events) {
        for(String s : events)
            array[agentPref][ArrayUtils.indexOf(eventSets, s)][pos] = true;
    }

}
