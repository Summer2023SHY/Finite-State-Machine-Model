package ui.page.optionpage;

import java.util.ArrayList;

import controller.InputReceiver;
import input.CustomEventReceiver;
import ui.page.optionpage.implementation.AdjustFSM;
import ui.page.optionpage.implementation.Operations;
import ui.page.optionpage.implementation.UStructurePage;
import visual.composite.HandlePanel;

public class OptionPageManager {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static int ROTATION_MULTIPLIER = 15;
	
//---  Instance Variables   -------------------------------------------------------------------

	private OptionPage[] optionPages;
	private static int currentOptionPageIndex;
	private HandlePanel p;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public OptionPageManager(InputReceiver reference, int xIn, int yIn, int wid, int hei) {
		OptionPage.assignInputReceiver(reference);
		generateHandlePanel(xIn, yIn, wid, hei);
		OptionPage.assignHandlePanel(p);
		optionPages = new OptionPage[] {
				new AdjustFSM(),
				new Operations(),
				new UStructurePage(),
		};
	}

//---  Operations   ---------------------------------------------------------------------------
	
	private void generateHandlePanel(int x, int y, int width, int height) {
		p = new HandlePanel(x, y, width, height) {
		
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
				optionPages[currentOptionPageIndex].handleMouseInput(code, x, y, mouseType);
			}

			
			@Override
			public void mouseWheelEvent(int rotation) {
				if(p.getMaximumScreenY() < p.getHeight()) {
					return;
				}
				p.setOffsetYBounded(p.getOffsetY() - rotation * ROTATION_MULTIPLIER);
			}

		});
	}

	public void drawPage() {
		optionPages[currentOptionPageIndex].drawPage();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCurrentOptionPageIndex(int in) {
		currentOptionPageIndex = in;
		p.removeElementPrefixed("");
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<String> getOptionPageNames(){
		ArrayList<String> out = new ArrayList<String>();
		for(OptionPage p : optionPages) {
			out.add(p.getHeader());
		}
		return out;
	}
	
	public HandlePanel getPanel() {
		return p;
	}
	
	public int getCurrentOptionPageIndex() {
		return currentOptionPageIndex;
	}
	
	public OptionPage[] getOptionPageList() {
		return optionPages;
	}
	
}
