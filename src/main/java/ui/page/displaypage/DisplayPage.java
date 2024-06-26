package ui.page.displaypage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import controller.CodeReference;
import input.CustomEventReceiver;
import ui.InputHandler;
import visual.composite.HandlePanel;

/**
 * TODO: Search Entries
 * TODO: Wire up Scrollwheel properly - backend is having issues with the click regions; multiple threads may be causing race conditions
 * (adding an element to an List shouldn't be done at the wrong index)
 * 
 * @author Borinor
 *
 */

public class DisplayPage {

//---  Constants   ----------------------------------------------------------------------------

    private static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 22);
    private static final Font TEXT_FONT = new Font("Serif", Font.BOLD, 16);
    private static final Font ATTRIBUTE_FONT = new Font("Serif", Font.BOLD, 14);
    private static final int STAND_IN_CODE = -1;
    private static final int DEFAULT_ENTRY_SIZE = 25;

    private static final String TITLE_STATES = "States";
    private static final String TITLE_EVENTS = "Events";
    private static final String TITLE_TRANS = "Transitions";

    private static final double VERT_PROP = 7 / (double)12;

    private static final File CYCLE_IMG;
    static {
        try (InputStream is = DisplayPage.class.getClassLoader().getResourceAsStream("ui/cycle.png")) {
            CYCLE_IMG = File.createTempFile("tmp", ".png");
            Files.copy(is, CYCLE_IMG.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

//---  Instance Variables   -------------------------------------------------------------------

    private FSMInfo info;

    private FSMImage imageDisplay;

    private boolean displayImageMode;

    private InputHandler reference;

    private HandlePanel p;

//---  Constructors   -------------------------------------------------------------------------

    public DisplayPage(InputHandler give, HandlePanel panel) {
        reference = give;
        p = panel;
    }

//---  Operations   ---------------------------------------------------------------------------

    public void draw() {
        if(info == null) {
            drawDefault();
        }
        else if(displayImageMode) {
            drawImage();
        }
        else {
            drawInfo();
        }
        addFraming();
    }

    public void updateFSMInfo(FSMInfo in) {
        info = in;
        p.removeAllElements();
    }

    public void drawInfo() {
        int vertP = (int)(p.getHeight() * VERT_PROP);

        drawDisplayThing(0, 0, p.getWidth() / 2, vertP, TITLE_STATES, info.getStateAttributes(), info.getStates(), info.getStateDetails());

        drawDisplayThing(p.getWidth() / 2, 0, p.getWidth() / 2, vertP, TITLE_EVENTS, info.getEventAttributes(), info.getEvents(), info.getEventDetails());

        drawDisplayThing(0, vertP, p.getWidth(), p.getHeight() - vertP, TITLE_TRANS, info.getTransitionAttributes(), info.getTransitions(), info.getTransitionDetails());

        drawCycleImageButton();

        //TODO: Set up buttons for adding/removing/renaming events/states/transitions and toggling qualities thereof
    }

    private void drawDisplayThing(int x, int y, int wid, int hei, String title, List<String> attributes, List<String> entries, Map<String, List<Boolean>> factors) {
        int sideBuffer = getSideBuffer(p);
        int vertBuffer = getVertBuffer(p);

        int currY = y + vertBuffer;

        p.handleText(title + "_list_title_", "move", 50, x + wid / 8, currY, wid / 3, hei / 4, TITLE_FONT, title);

        currY += vertBuffer;

        p.handleThickRectangle(title + "_list_rect_", "move", 35, x + sideBuffer, currY, x + wid - sideBuffer, y + hei - vertBuffer, Color.BLACK, 3);

        int boxHei = hei - 3 * vertBuffer;

        p.handleLine(title + "_list_line_ver_", "move", 35, x + wid / 2, currY, x + wid / 2, currY + boxHei, 2, Color.black);

        int entryHei = vertBuffer;

        int runX = x + wid / 2;
        int blockWid = sideBuffer;
        int attrBlockHeight = vertBuffer * 3 / 4;

        for(int i = 0; i < attributes.size(); i++) {
            int desX = runX + blockWid / 2 + blockWid * i;
            if(desX + blockWid < runX || desX > x + wid - sideBuffer) {
                continue;
            }
            p.handleLine(title + "_list_attr_line_ver_" + "_" + i, "move_horiz", 30, desX + blockWid / 2, currY, desX + blockWid / 2, currY + boxHei, 1, Color.black);
            p.handleText(title + "_list_attr_text_" + "_" + i, "move_horiz", 30, desX, currY + attrBlockHeight / 2, blockWid, attrBlockHeight, ATTRIBUTE_FONT, attributes.get(i));
        }

        Color back = p.getPanel().getBackground();

        p.handleRectangle(title + "_rect_cover_" + "_" + 1, "move", 25, x + wid / 2, currY + boxHei + entryHei / 2, wid, entryHei, back, back);

        p.handleRectangle(title + "_rect_cover_" + "_" + 2, "move", 25, x + wid / 2, currY + attrBlockHeight / 2, wid - sideBuffer * 2, attrBlockHeight, back, back);

        p.handleRectangle(title + "_rect_cover_" + "_" + 3, "move", 25, x + wid / 2, y + vertBuffer, wid, 2 * vertBuffer, back, back);

        currY += attrBlockHeight;

        p.handleLine(title + "_list_attr_line_bott_", "move", 30, x + sideBuffer, currY, x + wid - sideBuffer, currY, 2, Color.black);

        for(int i = 0; i < entries.size(); i++) {
            int desY = currY + entryHei * i;

            p.handleThickRectangle(title + "_list_entry_" + i + "_rect", title, 15, x + sideBuffer, desY, x + wid - sideBuffer, desY + entryHei, Color.BLACK, 1);
            p.handleText(title + "_list_entry_" + i + "_text", title, 15, x + sideBuffer * 2, desY + entryHei / 2, wid / 4, entryHei, TEXT_FONT, entries.get(i));

            List<Boolean> att = factors.get(entries.get(i));

            for(int j = 0; j < att.size(); j++) {
                int desX = runX + (j * blockWid);
                if(desX + blockWid < runX || desX > x + wid - sideBuffer) {
                    continue;
                }
                desX += blockWid / 2;
                p.handleRectangle(title + "_list_entry_" + i + "_attribute_signifier_rect_" + "_" + j, title, 10, desX, desY + entryHei / 2, entryHei / 2, entryHei / 2, att.get(j) ? Color.green : Color.red, Color.black);
                p.handleButton(title + "_list_entry_" + i + "_attribute_signifier_button_" + "_" + j, title, 10, desX, desY + entryHei / 2, entryHei / 2, entryHei / 2, STAND_IN_CODE);
                //TODO : Replace STAND_IN_CODE with actual value to communicate with model
            }
        }
        p.addScrollbar(title + "_scrollbar_vert", 45, "no_move", x + wid - sideBuffer, y + 2 * vertBuffer, sideBuffer / 3, boxHei, currY, boxHei - attrBlockHeight, title, true);
        p.setGroupDrawOutsideWindow(title, false);
    }

    public void drawImage() {
        if(imageDisplay == null || imageDisplay.getImage() == null) {
            reference.receiveCode(CodeReference.CODE_GENERATE_IMAGE, 0);
        }
        imageDisplay.drawPage();
        drawCycleImageButton();
    }

    private void drawCycleImageButton() {
        int size = getVertBuffer(p);
        p.handleRectangle("cycle_display_rect", "no_move", 45, p.getWidth() - size, size, size, size, Color.white, Color.black);
        p.handleImageButton("cycle_display", "no_move", 50, p.getWidth() - size, size, size, size, CYCLE_IMG.getAbsolutePath(), CodeReference.CODE_DISPLAY_CYCLE_VIEW);
    }

    public void drawDefault() {
        int vertP = (int)(p.getHeight() * VERT_PROP);
        p.handleLine("vertline", "move", 35, p.getWidth() / 2, 0, p.getWidth() / 2, vertP, 3, Color.black);

        p.handleLine("horizline", "move", 35, 0, vertP, p.getWidth(), vertP, 3, Color.black);

        List<String> attr = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            attr.add("Attr" + i);
        }
        Map<String, List<Boolean>> fact = new HashMap<>();

        List<String> stat = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < DEFAULT_ENTRY_SIZE; i++) {
            String nom = "Entry " + i;
            stat.add(nom);
            List<Boolean> bol = new ArrayList<>();
            for(int j = 0; j < 3; j++) {
                bol.add(rand.nextInt(2) == 0 ? true : false);
            }
            fact.put(nom, bol);
        }

        drawDisplayThing(0, 0, p.getWidth() / 2, vertP, TITLE_STATES, attr, stat, fact);

        drawDisplayThing(p.getWidth() / 2, 0, p.getWidth() / 2, vertP, TITLE_EVENTS, attr, stat, fact);

        drawDisplayThing(0, vertP, p.getWidth(), p.getHeight() - vertP, TITLE_TRANS, attr, stat, fact);


        addFraming();
    }

    private void addFraming() {
        int width = p.getWidth();
        int height = p.getHeight();
        int thick = 3;
        int buf = thick / 2;
        p.addLine("frame_line_3", 25, "no_move", buf, buf, buf, height - buf, thick, Color.BLACK);
        p.addLine("frame_line_4", 25, "no_move",  buf, buf, width - buf, buf, thick, Color.BLACK);
        p.addLine("frame_line_5", 25, "no_move",  width - buf, height - buf, width - buf, buf, thick, Color.BLACK);
        p.addLine("frame_line_6", 25, "no_move",  width - buf, height - buf, buf, height - buf, thick, Color.BLACK);
    }

    public void toggleDisplayMode() {
        displayImageMode = !displayImageMode;
        if(displayImageMode) {
            FSMImage.attachPanel(p);
        }
        else {
            FSMImage.dettachPanel(p);
        }
    }

    public void updateImage() {
        if(imageDisplay == null) {
            imageDisplay = new FSMImage(info.getName(), info.getImage());
        }
        else {
            imageDisplay.setImage(info.getImage());
        }
    }

    public void adjustOffsets() {
        p.adjustGroupOffset(TITLE_STATES, 0, 0);
        p.adjustGroupOffset(TITLE_EVENTS, 0, 0);
        p.adjustGroupOffset(TITLE_TRANS, 0, 0);
    }

//---  Getter Methods   -----------------------------------------------------------------------

    private int getVertBuffer(HandlePanel p) {
        return p.getHeight() / 25;
    }

    private int getSideBuffer(HandlePanel p) {
        return p.getWidth() / 16;
    }

    public boolean hasDisplay() {
        return imageDisplay != null;
    }

}
