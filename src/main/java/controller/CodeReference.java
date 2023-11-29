package controller;

public class CodeReference {

//---  Constants   ----------------------------------------------------------------------------

    //-- Display Page  ----------------------------------------

    public static final int CODE_GENERATE_IMAGE = 154;
    public static final int CODE_DISPLAY_CYCLE_VIEW = 155;

    //-- Generate FSM  ----------------------------------------
    public static final int CODE_ACCESS_NUM_STATES = 100;
    public static final int CODE_ACCESS_NUM_EVENTS = 101;
    public static final int CODE_ACCESS_NUM_TRANS = 102;
    public static final int CODE_ACCESS_FSM_NAME = 103;
    public static final int CODE_ACCESS_NON_DETERMINISTIC = 104;
    public static final int CODE_ACCESS_STATE_ATTRIBUTES = 106;
    public static final int CODE_ACCESS_EVENT_ATTRIBUTES = 107;
    public static final int CODE_ACCESS_TRANS_ATTRIBUTES = 108;
    public static final int CODE_ADD_STATE_ATTRIBUTE = 109;
    public static final int CODE_ADD_EVENT_ATTRIBUTE = 110;
    public static final int CODE_ADD_TRANS_ATTRIBUTE = 111;
    public static final int CODE_GENERATE_FSM = 105;
    //-- FSM Properties  --------------------------------------
    public static final int CODE_RENAME_FSM = 135;
    public static final int CODE_FSM_ADD_STATE_ATTRIBUTE = 112;
    public static final int CODE_FSM_ACCESS_ADD_STATE_ATTRIBUTE = 147;
    public static final int CODE_FSM_REMOVE_STATE_ATTRIBUTE = 113;
    public static final int CODE_FSM_ACCESS_REMOVE_STATE_ATTRIBUTE = 148;
    public static final int CODE_FSM_ADD_EVENT_ATTRIBUTE = 114;
    public static final int CODE_FSM_ACCESS_ADD_EVENT_ATTRIBUTE = 149;
    public static final int CODE_FSM_REMOVE_EVENT_ATTRIBUTE = 115;
    public static final int CODE_FSM_ACCESS_REMOVE_EVENT_ATTRIBUTE = 150;
    public static final int CODE_FSM_ADD_TRANS_ATTRIBUTE = 116;
    public static final int CODE_FSM_ACCESS_ADD_TRANS_ATTRIBUTE = 151;
    public static final int CODE_FSM_REMOVE_TRANS_ATTRIBUTE = 117;
    public static final int CODE_FSM_ACCESS_REMOVE_TRANS_ATTRIBUTE = 152;
    //-- States  ----------------------------------------------
    public static final int CODE_ADD_STATE = 118;
    public static final int CODE_REMOVE_STATE = 119;
    public static final int CODE_RENAME_STATE = 120;
    public static final int CODE_ADD_STATES = 121;
    public static final int CODE_EDIT_STATE_ATTRIBUTE = 122;
    public static final int CODE_ADD_EDIT_STATE_ATTRIBUTE = 123;
    public static final int CODE_ACCESS_EDIT_STATE = 124;
    //-- Events  ----------------------------------------------
    public static final int CODE_ADD_EVENT = 140;
    public static final int CODE_REMOVE_EVENT = 141;
    public static final int CODE_RENAME_EVENT = 142;
    public static final int CODE_ADD_EVENTS = 143;
    public static final int CODE_EDIT_EVENT_ATTRIBUTE = 144;
    public static final int CODE_ADD_EDIT_EVENT_ATTRIBUTE = 145;
    public static final int CODE_ACCESS_EDIT_EVENT = 146;
    //-- Transitions  -----------------------------------------
    public static final int CODE_ADD_TRANSITION = 126;
    public static final int CODE_REMOVE_TRANSITION = 127;
    public static final int CODE_EDIT_TRANS_ATTRIBUTE = 153;
    public static final int CODE_ADD_EDIT_TRANS_ATTRIBUTE = 138;
    public static final int CODE_ACCESS_EDIT_TRANS = 139;
    //-- Admin  -----------------------------------------------
    public static final int CODE_SAVE_FSM = 129;
    public static final int CODE_SAVE_IMG = 130;
    public static final int CODE_SAVE_TKZ = 131;
    public static final int CODE_SAVE_SVG = 132;
    public static final int CODE_LOAD_SOURCE = 133;
    public static final int CODE_DELETE_SOURCE = 134;
    public static final int CODE_DUPLICATE_FSM = 136;
    public static final int CODE_CLOSE_FSM = 137;

    //-- Operations  ------------------------------------------

    public static final int CODE_TRIM = 200;
    public static final int CODE_ACCESSIBLE = 201;
    public static final int CODE_CO_ACCESSIBLE = 202;
    public static final int CODE_OBSERVER = 203;
    public static final int CODE_PRODUCT_SELECT = 204;
    public static final int CODE_PRODUCT = 205;
    public static final int CODE_PARALLEL_COMPOSITION_SELECT = 206;
    public static final int CODE_PARALLEL_COMPOSITION = 207;
    public static final int CODE_SUP_CNT_SBL_SELECT = 208;
    public static final int CODE_SUP_CNT_SBL = 209;
    public static final int CODE_UNDER_FSM = 210;
    public static final int CODE_OPT_OPQ_CONTROLLER =111;
    public static final int CODE_OPT_SPVR_SELECT = 212;
    public static final int CODE_OPT_SPVR = 213;
    public static final int CODE_GRT_LWR_BND_SELECT = 214;
    public static final int CODE_GRT_LWR_BND = 215;
    public static final int CODE_PRUNE = 216;
    public static final int CODE_BLOCKING = 217;
    public static final int CODE_STATE_EXISTS = 218;

    //-- UStructure  ------------------------------------------

    public static final int CODE_SELECT_PLANT = 300;
    public static final int CODE_ADD_BAD_TRANS = 301;
    public static final int CODE_BUILD_AGENTS = 302;
    public static final int CODE_BUILD_USTRUCT = 303;
    public static final int CODE_TOGGLE_USTRUCT = 304;
    public static final int CODE_DISPLAY_BAD_TRANS_START = 500;

    /** Private constructor */
    private CodeReference() {
    }

}
