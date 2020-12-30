package ui.page.optionpage.implementation;

import java.io.File;
import java.util.Random;

import support.meta.GenerateFSM;
import ui.FSMUI;
import ui.page.optionpage.OptionPage;
import visual.composite.popout.PopoutAlert;
import visual.composite.popout.PopoutSelectList;

/**
 * TODO: SVG is not displaying correctly, despite the produced TikZ functioning correctly
 * TODO: Allow editing a transition to become a Must transition, other types?
 * TODO: Crashes if Make FSM and Make Complicated FSM are open at the same time
 * TODO: Correction: It freezes if those two are open, naming collision? Infinite loop?
 * @author Reithger
 *
 */

public class AdjustFSM extends OptionPage{
	
//---  Constant Values   ----------------------------------------------------------------------
	

	// Generate FSM  
	private final static int CODE_ACCESS_NUM_STATES = 100;
	private final static int CODE_ACCESS_NUM_EVENTS = 101;
	private final static int CODE_ACCESS_NUM_TRANS = 102;
	private final static int CODE_ACCESS_FSM_NAME = 103;
	private final static int CODE_ACCESS_NON_DETERMINISTIC = 104;
	private final static int CODE_GENERATE_FSM = 105;
	// Generate Complex FSM  
	private final static int CODE_ACCESS_COMPLEX_NUM_STATES = 106;
	private final static int CODE_ACCESS_COMPLEX_NUM_EVENTS = 107;
	private final static int CODE_ACCESS_COMPLEX_NUM_TRANS = 108;
	private final static int CODE_ACCESS_COMPLEX_INITIAL = 109;
	private final static int CODE_ACCESS_COMPLEX_MARKED = 110;
	private final static int CODE_ACCESS_COMPLEX_SECRET = 111;
	private final static int CODE_ACCESS_COMPLEX_CONTROLLED = 112;
	private final static int CODE_ACCESS_COMPLEX_UNOBSERVED = 113;
	private final static int CODE_ACCESS_COMPLEX_ATTACKER = 114;
	private final static int CODE_ACCESS_COMPLEX_NON_DETERMINISTIC = 115;
	private final static int CODE_ACCESS_COMPLEX_FSM_NAME = 116;
	private final static int CODE_GENERATE_COMPLEX_FSM = 117;
	//
	private final static int CODE_ADD_STATE = 118;
	private final static int CODE_REMOVE_STATE = 119;	
	private final static int CODE_RENAME_STATE = 120;
	private final static int CODE_ADD_STATES = 121;
	private final static int CODE_INITIAL_STATE = 122;
	private final static int CODE_MARKED_STATE = 123;
	private final static int CODE_SECRET_STATE = 124;
	private final static int CODE_BAD_STATE = 125;
	//--
	private final static int CODE_ADD_TRANSITION = 126;
	private final static int CODE_TOGGLE_MUST = 127;
	private final static int CODE_REMOVE_TRANSITION = 128;	
	//--
	private final static int CODE_SAVE_FSM = 129;
	private final static int CODE_SAVE_IMG = 130;
	private final static int CODE_SAVE_TKZ = 131;
	private final static int CODE_SAVE_SVG = 132;
	private final static int CODE_LOAD_SOURCE = 133;
	private final static int CODE_DELETE_SOURCE = 134;
	private final static int CODE_RENAME_FSM = 135;
	private final static int CODE_DUPLICATE_FSM = 136;
	private final static int CODE_CLOSE_FSM = 137;
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "Adjust FSM";
	private final static String[] CATEGORIES = new String[] {"Generate FSM Simple", "Generate FSM Complicated", "Edit FSM States", "Edit FSM Transitions", "Admin"};
	private final static Object[][][] DATA = new Object[][][] {
		{
			{"Number of States", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_NUM_STATES, false},
			{"Number of Events", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_NUM_EVENTS, false},
			{"Number of Transitions", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_NUM_TRANS, false},
			{"Non-Deterministic", OptionPage.ENTRY_CHECKBOX, CODE_ACCESS_NON_DETERMINISTIC, false},
			{"Name", OptionPage.ENTRY_TEXT_LONG, CODE_ACCESS_FSM_NAME, false},
			{"Generate", OptionPage.ENTRY_EMPTY,  CODE_GENERATE_FSM, true},
		},
		{
			{"Number of States", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_COMPLEX_NUM_STATES, false},
			{"Number of Events", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_COMPLEX_NUM_EVENTS, false},
			{"Number of Transitions", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_COMPLEX_NUM_TRANS, false},
			
			{"Number of Initial States", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_COMPLEX_INITIAL, false},
			{"Number of Marked States", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_COMPLEX_MARKED, false},
			{"Number of Secret States", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_COMPLEX_SECRET, false},
			{"Number of Controlled Events", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_COMPLEX_CONTROLLED, false},
			{"Number of Unobservable Events", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_COMPLEX_UNOBSERVED, false},
			{"Number of Attacker Invisible Events", OptionPage.ENTRY_TEXT_SINGLE, CODE_ACCESS_COMPLEX_ATTACKER, false},
			
			{"Non-Deterministic", OptionPage.ENTRY_CHECKBOX, CODE_ACCESS_COMPLEX_NON_DETERMINISTIC, false},
			{"Name", OptionPage.ENTRY_TEXT_LONG, CODE_ACCESS_COMPLEX_FSM_NAME, false},
			{"Generate", OptionPage.ENTRY_EMPTY, CODE_GENERATE_COMPLEX_FSM, true},
		},
		{
			{"Add State", OptionPage.ENTRY_TEXT_SINGLE, CODE_ADD_STATE, true},
			{"Add Many States", OptionPage.ENTRY_TEXT_SINGLE, CODE_ADD_STATES, true},
			{"Remove State", OptionPage.ENTRY_TEXT_SINGLE, CODE_REMOVE_STATE, true},
			{"Rename State", OptionPage.ENTRY_TEXT_DOUBLE, CODE_RENAME_STATE, true},
			{"Add Initial State", OptionPage.ENTRY_TEXT_SINGLE, CODE_INITIAL_STATE, true},
			{"Set State Marked", OptionPage.ENTRY_TEXT_SINGLE, CODE_MARKED_STATE, true},
			{"Set State Secret", OptionPage.ENTRY_TEXT_SINGLE, CODE_SECRET_STATE, true},
			{"Set State Bad", OptionPage.ENTRY_TEXT_SINGLE, CODE_BAD_STATE, true},
		},
		{
			{"Add Transition", OptionPage.ENTRY_TEXT_TRIPLE, CODE_ADD_TRANSITION, true},
			//{"Mark Must Transition", OptionPage.ENTRY_CHECKBOX, CODE_TOGGLE_MUST, false},
			{"Remove Transition", OptionPage.ENTRY_TEXT_TRIPLE, CODE_REMOVE_TRANSITION, true},
		},
		{
			{"Save Source", OptionPage.ENTRY_EMPTY, CODE_SAVE_FSM, true},
			{"Save Image", OptionPage.ENTRY_EMPTY, CODE_SAVE_IMG, true},
			{"Generate Tikz", OptionPage.ENTRY_EMPTY, CODE_SAVE_TKZ, true},
			{"Generate SVG", OptionPage.ENTRY_EMPTY, CODE_SAVE_SVG, true},
			{"Load Source", OptionPage.ENTRY_EMPTY, CODE_LOAD_SOURCE, true},
			{"Rename FSM", OptionPage.ENTRY_TEXT_LONG, CODE_RENAME_FSM, true},
			{"Generate Copy", OptionPage.ENTRY_TEXT_LONG, CODE_DUPLICATE_FSM, true},
			{"Close FSM", OptionPage.ENTRY_EMPTY, CODE_CLOSE_FSM, true},
			{"Delete Source", OptionPage.ENTRY_EMPTY, CODE_DELETE_SOURCE, true},
		},
	};
	private final static String HELP = 
			"Some line\n"
			+ "Another line\n"
			+ "This will be the help page";
	private final static String LANGUAGE = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private final static int RANDOM_NAME_LENGTH = 8;

	
//---  Constructors   -------------------------------------------------------------------------

	public AdjustFSM(int x, int y, int width, int height) {
		super(HEADER, HELP, CATEGORIES, DATA);
	}
	
//---  Operations   ---------------------------------------------------------------------------

	@Override
	public void applyCode(int code) {
		switch(code) {
			case CODE_ADD_STATES:
				int id = 1;
				int added = 0;
				int toAdd = this.getIntegerFromCode(CODE_ADD_STATES, 0);
				while(added != toAdd) {
					while(getFSMUI().getActiveFSM().stateExists(""+id)) {
						id++;
					}
					getFSMUI().getActiveFSM().addState(""+id++);
					added++;
				}
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CODE_RENAME_STATE:
				String oldName = getTextFromCode(CODE_RENAME_STATE, 0);
				String newName = getTextFromCode(CODE_RENAME_STATE, 1);
				getFSMUI().getActiveFSM().getStateMap().renameState(oldName, newName);
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CODE_SAVE_SVG:
				new PopoutAlert(250, 150, "Saved at: " + getFSMUI().saveActiveSVG());
				break;
			case CODE_SAVE_TKZ:
				new PopoutAlert(250, 150, "Saved at: " + getFSMUI().saveActiveTikZ());
				break;
			case CODE_SAVE_FSM:
				getFSMUI().saveActiveFSMSource();
				break;
			case CODE_SAVE_IMG:
				getFSMUI().saveActiveFSMImage();
				break;
			case CODE_DUPLICATE_FSM:
				String tex = getTextFromCode(CODE_DUPLICATE_FSM, 0); 
				tex = tex.equals("") || tex == null ? getFSMUI().getActiveFSM().getId() + "(copy)" : tex;
				getFSMUI().allotTransitionSystem(getFSMUI().getActiveFSM().copy(), tex);
				break;
			case CODE_RENAME_FSM:
				getFSMUI().renameActiveFSM(getTextFromCode(CODE_RENAME_FSM, 0));
				resetCodeEntries(code);
				break;
			case CODE_LOAD_SOURCE:
				File f = new File(FSMUI.ADDRESS_SOURCES);
				PopoutSelectList fs = new PopoutSelectList(300, 600, f.list(), true);
				String outcome = fs.getSelected();
				getFSMUI().allotTransitionSystem(FSMUI.ADDRESS_SOURCES + outcome, outcome.replaceAll(".fsm", ""));
				getFSMUI().refreshActiveImage();
				fs.dispose();
				resetCodeEntries(code);
				break;
			case CODE_DELETE_SOURCE:
				getFSMUI().deleteActiveFSM();
				break;
			case CODE_ADD_STATE: 
				getFSMUI().getActiveFSM().addState(this.getTextFromCode(CODE_ADD_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CODE_REMOVE_STATE: 
				getFSMUI().getActiveFSM().removeState(this.getTextFromCode(CODE_REMOVE_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CODE_ADD_TRANSITION: 	//TODO: Integrate Must transition boolean at CODE_TOGGLE_MUST
				getFSMUI().getActiveFSM().addTransition(this.getTextFromCode(CODE_ADD_TRANSITION, 0), this.getTextFromCode(CODE_ADD_TRANSITION, 1), this.getTextFromCode(CODE_ADD_TRANSITION, 2));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);	//TODO: Let user decide whether or not to clear entries, in settings
				break;
			case CODE_REMOVE_TRANSITION: 
				getFSMUI().getActiveFSM().removeTransition(this.getTextFromCode(CODE_REMOVE_TRANSITION, 0), this.getTextFromCode(CODE_REMOVE_TRANSITION, 1), this.getTextFromCode(CODE_REMOVE_TRANSITION, 2));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CODE_SECRET_STATE:
				getFSMUI().getActiveFSM().toggleSecretState(this.getTextFromCode(CODE_SECRET_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CODE_INITIAL_STATE:
				getFSMUI().getActiveFSM().addInitialState(this.getTextFromCode(CODE_INITIAL_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CODE_MARKED_STATE:
				getFSMUI().getActiveFSM().toggleMarkedState(this.getTextFromCode(CODE_MARKED_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CODE_BAD_STATE:
				getFSMUI().getActiveFSM().toggleBadState(this.getTextFromCode(CODE_BAD_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CODE_CLOSE_FSM:
				getFSMUI().removeActiveTransitionSystem();
				getFSMUI().refreshActiveImage();
				break;
			case CODE_GENERATE_FSM: 
				try {
					int numState = getIntegerFromCode(CODE_ACCESS_NUM_STATES, 0);
					int numEvent = getIntegerFromCode(CODE_ACCESS_NUM_EVENTS, 0);
					int numTrans = getIntegerFromCode(CODE_ACCESS_NUM_TRANS, 0);
					boolean nonDet = getCheckboxContentsFromCode(CODE_ACCESS_NON_DETERMINISTIC);
					String name = getTextFromCode(CODE_ACCESS_FSM_NAME, 0);
					if(name == null) {
						name = "";
						Random rand = new Random();
						for(int i = 0; i < RANDOM_NAME_LENGTH; i++) {
							name += LANGUAGE.charAt(rand.nextInt(LANGUAGE.length()));
						}
					}
					String path = GenerateFSM.createNewFSM(numState, 0, numEvent, numTrans, 1, 0, 0, 0, 0, nonDet, name, FSMUI.ADDRESS_SOURCES);
					getFSMUI().allotTransitionSystem(path, name);
					resetCodeEntries(code);
				}
				catch(Exception e) {
					e.printStackTrace();
					new PopoutAlert(250, 250, "Failed to generate FSM, please check your input again.");
				}
				break;
			case CODE_GENERATE_COMPLEX_FSM:
				try {
					int numStateC = getIntegerFromCode(CODE_ACCESS_COMPLEX_NUM_STATES, 0);
					int numEventC = getIntegerFromCode(CODE_ACCESS_COMPLEX_NUM_EVENTS, 0);
					int numTransC = getIntegerFromCode(CODE_ACCESS_COMPLEX_NUM_TRANS, 0);
					int numInitC = getIntegerFromCode(CODE_ACCESS_COMPLEX_INITIAL, 0);
					int numMarkedC = getIntegerFromCode(CODE_ACCESS_COMPLEX_MARKED, 0);
					int numSecretC = getIntegerFromCode(CODE_ACCESS_COMPLEX_SECRET, 0);
					int numControlledC = getIntegerFromCode(CODE_ACCESS_COMPLEX_CONTROLLED, 0);
					int numUnobservedC = getIntegerFromCode(CODE_ACCESS_COMPLEX_UNOBSERVED, 0);
					int numAttackerC = getIntegerFromCode(CODE_ACCESS_COMPLEX_ATTACKER, 0);
					boolean nonDetC = getCheckboxContentsFromCode(CODE_ACCESS_NON_DETERMINISTIC);
					String nameC = getTextFromCode(CODE_ACCESS_COMPLEX_FSM_NAME, 0);
					if(nameC == null) {
						nameC = "";
						Random rand = new Random();
						for(int i = 0; i < RANDOM_NAME_LENGTH; i++) {
							nameC += LANGUAGE.charAt(rand.nextInt(LANGUAGE.length()));
						}
					}
					String pathC = GenerateFSM.createNewFSM(numStateC, numMarkedC, numEventC, numTransC, numInitC, numSecretC, numUnobservedC, numAttackerC, numControlledC, nonDetC, nameC, FSMUI.ADDRESS_SOURCES);
					getFSMUI().allotTransitionSystem(pathC, nameC);
					resetCodeEntries(code);
				}
				catch(Exception e) {
					e.printStackTrace();
					new PopoutAlert(250, 250, "Failed to generate FSM, please check your input again.");
				}
				break;
			default:
				break;
			}
	}
	
}