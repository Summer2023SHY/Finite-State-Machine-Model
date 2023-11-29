package help;

import model.AttributeList;

public class EventSets {

//---  Constants   ----------------------------------------------------------------------------

    public static final String[] EVENT_ATTR_LIST = {AttributeList.ATTRIBUTE_OBSERVABLE, AttributeList.ATTRIBUTE_CONTROLLABLE};

    public static final String[] EVENT_LIST_A = {"a_{1}", "a_{2}", "b_{1}", "b_{2}", "c"};

    public static final String[] EVENT_LIST_B = {"a", "b", "c", "d", "s"};

    public static final String[] EVENT_LIST_C = {"a_{1}", "a_{2}", "b_{1}", "b_{2}", "c", "d"};

    public static final String[] EVENT_LIST_D = {"a", "b", "c"};

    public static final String[] EVENT_LIST_E = {"a_{1}", "a_{2}", "b_{1}", "b_{2}", "c1", "c2", "c3", "d", "s"};

    public static final String[] EVENT_LIST_FINN5 = {"a_{1}", "a_{2}", "a_{3}","a_{4}","a_{5}","a_{6}","b_{1}", "b_{2}", "b_{3}","b_{4}","b_{5}","b_{6}", "s"};

    public static final String[] EVENT_LIST_LIU_ONE = {"a", "b", "g"};

    public static final String[] EVENT_LIST_DTP = {"getFrame", "send_0", "send_1", "rcvAck_0", "rcvAck_1", "loss", "sendAck_0", "sendAck_1", "rcv_0", "rcv_1", "passToHost"};

    public static final String[] EVENT_LIST_DTP_SENDER = {"getFrame", "send_0", "send_1", "rcvAck_0", "rcvAck_1", "loss"};

    public static final String[] EVENT_LIST_DTP_RECEIVER = {"rcv_0", "rcv_1", "passToHost", "sendAck_0", "sendAck_1"};

    public static final String[] EVENT_LIST_DTP_CHANNEL = {"rcv_0", "rcv_1", "send_0", "send_1", "loss", "sendAck_0", "sendAck_1", "rcvAck_0", "rcvAck_1"};

    public static final String[] EVENT_LIST_DTP_SPEC_ONE = EVENT_LIST_DTP;

    public static final String[] EVENT_LIST_DTP_SPEC_TWO = {"getFrame", "loss", "send_0", "send_1", "rcvAck_0", "rcvAck_1"};

    public static final String[] EVENT_LIST_DTP_SPEC_THREE = {"rcv_0", "rcv_1", "sendAck_0", "sendAck_1", "passToHost"};

    public static final String[] EVENT_LIST_SPEC_PRIME = {"a", "b", "g"};



    public static final String[] EVENT_LIST_HISC = {"take_item", "package", "allow_exit", "new_part", "part_f_obuff", "part_passes",
                                                    "part_fails", "ret_inbuff", "deposit_part", "part_ent-I", "part_ent-II", "part_ent-III",
                                                    "fin_exit-I", "fin_exit-II", "fin_exit-III"};

    public static final String[] EVENT_LIST_HISC_J = {"part_arr1-", "start_pol-", "compl_pol-", "part_lv1-",
                                                      "part_arr2-", "partLvExit-", "recog_A-", "recog_B-", "attch_ptA-", "attch_ptB-",
                                                      "finA_attch-", "finB_attch-", "part_lv2-", "part_arr3-", "start_case-", "compl_case-",
                                                      "part_lv3-", "str_exit-", "take_pt-", "str_ptA-", "str_ptB-", "cmpl_A-", "cmpl_B-",
                                                      "ret_pt-", "dip_acid-", "polish-", "str_rlse-", "attch_case-"};

    public static final String[] EVENT_LIST_HISC_J2 = {"start_pol-", "attch_ptA-", "attch_ptB-", "start_case-", "finA_attch-", "finB_attch-", "part_lv1-",
                                                       "partLvExit-", "str_exit-", "part_lv2-", "part_lv3-", "take_pt-", "str_ptA-", "str_ptB-",
                                                       "dip_acid-", "polish-", "str_rlse-", "part_ent-"};

    public static final String[] EVENT_LIST_HISC_PACK_SYS = {"take_item", "package", "allow_exit"};

    public static final String[] EVENT_LIST_HISC_SOURCE = {"new_part"};

    public static final String[] EVENT_LIST_HISC_SINK = {"allow_exit"};

    public static final String[] EVENT_LIST_HISC_TEST = {"part_f_obuff", "part_passes", "part_fails", "ret_inbuff", "deposit_part"};


    public static final String[] EVENT_LIST_HISC_PATH = {"part_ent-", "part_arr1-", "part_lv1-", "str_exit-", "fin_exit-", "partLvExit-",
                                                         "part_arr2-", "recog_A-", "recog_B-", "part_lv2-", "part_arr3-", "part_lv3-"};

    public static final String[] EVENT_LIST_HISC_DEFINE = {"attch_ptA-", "attch_ptB-", "finA_attch-", "finB_attch-"};

    public static final String[] EVENT_LIST_HISC_ATTACH_PART = {"take_pt-", "str_ptA-", "cmpl_A-", "str_ptB-", "cmpl_B-", "ret_pt-"};

    public static final String[] EVENT_LIST_HISC_POLISH_PART = {"start_pol-", "dip_acid-", "polish-", "str_rlse-", "compl_pol-"};

    public static final String[] EVENT_LIST_HISC_ATTACH_CASE = {"start_case-", "attch_case-", "compl_case-"};

    public static final String[] EVENT_LIST_HISC_INBUFF = {"ret_inbuff", "new_part", "part_ent-I", "part_ent-II", "part_ent-III"};

    public static final String[] EVENT_LIST_HISC_OUTBUFF = {"part_f_obuff", "part_ent-I", "part_ent-II", "part_ent-III", "fin_exit-I", "fin_exit-II", "fin_exit-III"};

    public static final String[] EVENT_LIST_HISC_PACKBUFF = {"deposit_part", "take_item"};

    public static final String[] EVENT_LIST_HISC_ENSURE = {"new_part", "part_passes"};

    public static final String[] EVENT_LIST_HISC_MOVE = {"part_ent-", "fin_exit-"};

    public static final String[] EVENT_LIST_HISC_POLISH_SEQUENCE = {"start_pol-", "dip_acid-", "polish-", "str_rlse-"};

    public static final String[] EVENT_LIST_HISC_SEQUENCE = {"fin_exit-", "part_ent-", "part_arr1-", "start_pol-", "compl_pol-", "part_lv1-",
                                                             "part_arr2-", "partLvExit-", "recog_A-", "recog_B-", "attch_ptA-", "attch_ptB-",
                                                             "finA_attch-", "finB_attch-", "part_lv2-", "part_arr3-", "start_case-", "compl_case-",
                                                             "part_lv3-", "str_exit-"};

    public static final String[] EVENT_LIST_HISC_AFFIX = {"str_ptA-", "attch_ptB-", "take_pt-", "str_ptB-", "cmpl_B-", "ret_pt-", "finB_attch-", "attch_ptA-",
                                                          "cmpl_A-", "finA_attch-"};

    public static final String[] EVENT_LIST_HISC_G = {"start_pol-", "compl_pol-", "attch_ptA-", "finA_attch-", "attch_ptB-", "finB_attch-", "start_case-", "compl_case-"};
}
