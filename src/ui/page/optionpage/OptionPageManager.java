package ui.page.optionpage;

import input.CustomEventReceiver;
import ui.FSMUI;
import ui.page.optionpage.implementation.AdjustFSM;
import ui.page.optionpage.implementation.Operations;
import ui.page.optionpage.implementation.UStructurePage;
import visual.panel.ElementPanel;

public class OptionPageManager {
	
//---  Instance Variables   -------------------------------------------------------------------

	private final static OptionPage[] OPTION_PAGES = new OptionPage[] {
			new AdjustFSM(0, 0, FSMUI.WINDOW_WIDTH/2, (int)(FSMUI.WINDOW_HEIGHT * FSMUI.PANEL_RATIO_VERTICAL)),
			new Operations(0, 0, FSMUI.WINDOW_WIDTH/2, (int)(FSMUI.WINDOW_HEIGHT * FSMUI.PANEL_RATIO_VERTICAL)),
			new UStructurePage(0, 0, FSMUI.WINDOW_WIDTH/2, (int)(FSMUI.WINDOW_HEIGHT * FSMUI.PANEL_RATIO_VERTICAL)),
	};
	private static int currentOptionPageIndex;
	private ElementPanel p;
	private final static int ROTATION_MULTIPLIER = 15;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public OptionPageManager(FSMUI reference) {
		OptionPage.assignFSMUI(reference);
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public ElementPanel generateElementPanel(int x, int y, int width, int height) {
		p = new ElementPanel(x, y, width, height) {
		
			@Override
			public int getMinimumScreenX() {
				return 0;
			}
			
			@Override
			public int getMinimumScreenY() {
				return 0;
			}
			
			@Override
			public int getMaximumScreenY() {
				int max = super.getMaximumScreenY();
				return max + (max > getHeight() ? 15 : 0);
			}
		};
		p.setEventReceiver(new CustomEventReceiver() {
			
			@Override
			public void keyEvent(char code) {
				
			}

			@Override
			public void clickEvent(int code, int x, int y, int mouseType) {
				OPTION_PAGES[currentOptionPageIndex].handleMouseInput(code, x, y);
			}

			
			@Override
			public void mouseWheelEvent(int rotation) {
				if(p.getMaximumScreenY() < p.getHeight()) {
					return;
				}
				p.setOffsetYBounded(p.getOffsetY() - rotation * ROTATION_MULTIPLIER);
			}

		});
		OptionPage.assignElementPanel(p);
		return p;
	}

	public void drawPage() {
		OPTION_PAGES[currentOptionPageIndex].drawPage();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCurrentOptionPageIndex(int in) {
		currentOptionPageIndex = in;
		p.removeElementPrefixed("");
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public int getCurrentOptionPageIndex() {
		return currentOptionPageIndex;
	}
	
	public OptionPage[] getOptionPageList() {
		return OPTION_PAGES;
	}
	
}
