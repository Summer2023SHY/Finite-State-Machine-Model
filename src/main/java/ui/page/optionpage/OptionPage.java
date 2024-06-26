package ui.page.optionpage;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ui.InputHandler;
import ui.page.optionpage.entryset.EntrySetFactory;
import visual.composite.HandlePanel;

/**
 * 
 * DO NOT USE NEGATIVE CODE VALUES WE HAD TO MAKE A COMPROMISE AND THAT'S THE RESULT
 * 
 * @author Ada Clevinger
 *
 */

public abstract class OptionPage {

//---  Constant Values   ----------------------------------------------------------------------

    private static final Font HELP_FONT = new Font("Serif", Font.BOLD, 18);
    protected static final Font OPTIONS_FONT = new Font("Serif", Font.BOLD, 12);

    private static final File QUESTION_MARK_IMG;
    static {
        try (InputStream is = OptionPage.class.getClassLoader().getResourceAsStream("ui/question_mark.png")) {
            QUESTION_MARK_IMG = File.createTempFile("tmp", ".png");
            Files.copy(is, QUESTION_MARK_IMG.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

//---  Instance Variables   -------------------------------------------------------------------

    private String header;
    private String help;
    private List<Category> categories;

    private static HandlePanel p;
    private boolean showHelp;        //TODO actually implement help pages
    private int helpKey;
    private boolean showSettings; //TODO: Settings menu
    private int settingsKey;

    private static InputHandler inputRef;

    private static int lineHeightFraction;

//---  Constructors   -------------------------------------------------------------------------

    public OptionPage(String head, String inHelp) {
        header = head;
        help = inHelp;
        categories = new ArrayList<Category>();
        lineHeightFraction = 15;
    }

    //---  Operations   ---------------------------------------------------------------------------

    public void drawPage() {
        if(showHelp) {
            drawHelpPage();
        }
        else {
            drawNormalPage();
        }
        addFraming();
    }

    private void drawNormalPage() {
        int wid = p.getWidth();
        int hei = p.getHeight();
        int startY = hei / 20;
        int codeStart = categories.size();
        helpKey = codeStart;
        settingsKey = ++codeStart;
        codeStart++;
        p.handleRectangle("help_butt_rect", "move", 5, wid - wid / 15, wid / 20, wid / 20, wid / 20, Color.gray, Color.black);
        p.handleButton("help_butt_button", "move", 15, wid - wid / 15,  wid / 20, wid / 20, wid / 20, helpKey);
        p.handleImage("help_butt_img", "move", 15, wid - wid / 15, wid / 20, QUESTION_MARK_IMG.getAbsolutePath(), 3);
        for(int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            startY = cat.draw(startY, hei / lineHeightFraction, p);
        }
    }

    private void drawHelpPage() {
        p.handleText("help_text", "no_move", 15, p.getWidth() / 2, p.getHeight() / 2, p.getWidth() * 9 / 10, p.getHeight() * 9 / 10, HELP_FONT, help);
        p.handleRectangle("help_rect", "no_move", 5, p.getWidth() / 2, p.getHeight() / 2, p.getWidth() * 9 / 10, p.getHeight() * 9 / 10, Color.white, Color.black);
    }

    public void handleMouseInput(int code, int x, int y, int mouseType) {
        if(code == helpKey || showHelp == true) {
            showHelp = !showHelp;
            p.removeElementPrefixed(StringUtils.EMPTY);
            drawPage();
            return;
        }
        if(!toggleCategory(code) && handleInput(code)) {
            inputRef.receiveCode(code, mouseType);
        }
    }

    public boolean handleInput(int code) {
        boolean out = false;
        if(code == -1) {
            return out;
        }
        if(getCategoryFromCode(code) != null)
            out = getCategoryFromCode(code).handleInput(code, p);
        drawPage();
        return out;
    }

    private void addFraming() {
        int width = p.getWidth();
        int height = p.getHeight();
        int thick = 3;
        int buf = thick / 2;
        p.addLine("frame_line_3", 15, "no_move", buf, buf, buf, height - buf, thick, Color.BLACK);
        p.addLine("frame_line_4", 15, "no_move",  buf, buf, width - buf, buf, thick, Color.BLACK);
        p.addLine("frame_line_5", 15, "no_move",  width - buf, height - buf, width - buf, buf, thick, Color.BLACK);
        p.addLine("frame_line_6", 15, "no_move",  width - buf, height - buf, buf, height - buf, thick, Color.BLACK);
    }

//---  Setter Methods   -----------------------------------------------------------------------

    public static void assignInputHandler(InputHandler iR) {
        inputRef = iR;
    }

    public static void assignHandlePanel(HandlePanel inP) {
        p = inP;
    }

    public boolean toggleCategory(int code) {
        if(code >= 0 && code < categories.size()) {
            categories.get(code).toggleOpen();
            drawPage();
            return true;
        }
        return false;
    }

    //-- EntrySet  --------------------------------------------

    public void addCategory(String title) {
        Category out = new Category(title);
        out.setCode(categories.size());
        categories.add(out);
    }

    public void resetCodeEntries(int code) {
        for(Category c : categories) {
            if(c.contains(code)) {
                c.getEntrySet(code).reset(p);
            }
        }
        drawPage();
    }

    public void setEntrySetContent(int code, int index, String reference) {
        getCategoryFromCode(code).setEntrySetContent(code, index, reference);
        drawPage();
    }

    public void removeContentsFromCode(int code, int index) {
        getCategoryFromCode(code).resetEntrySetContent(code, index);
        drawPage();
    }

    public void deleteContentsFromCode(int code, int index) {
        getCategoryFromCode(code).deleteEntrySetContent(code, index);
        drawPage();
    }

    public void resetContents(int code) {
        p.removeElementPrefixed(getCategoryFromCode(code).resetEntrySetContents(code));
        drawPage();
    }

        //-- Add Types  ---------------------------------------

    public void addEntryText(String category, String label, boolean button, int code, int size, boolean flex) {
        if(getCategory(category) == null) {
            addCategory(category);
        }
        Category c = getCategory(category);
        c.addEntrySet(EntrySetFactory.generateEntryText(c.prefix(), label, button, code, size, flex));
    }

    public void addEntryTextDisplay(String category, String label, boolean button, int code) {
        if(getCategory(category) == null) {
            addCategory(category);
        }
        Category c = getCategory(category);
        c.addEntrySet(EntrySetFactory.generateEntryTextDisplay(c.prefix(), label, button, code));
    }

    public void addEntryList(String category, String label, boolean button, int code, int newCode) {
        if(getCategory(category) == null) {
            addCategory(category);
        }
        Category c = getCategory(category);
        c.addEntrySet(EntrySetFactory.generateEntryList(c.prefix(), label, button, code, newCode));
    }

    public void addEntryCheckbox(String category, String label, boolean button, int code) {
        if(getCategory(category) == null) {
            addCategory(category);
        }
        Category c = getCategory(category);
        c.addEntrySet(EntrySetFactory.generateEntryCheckbox(c.prefix(), label, button, code));
    }

    public void addEntryEmpty(String category, String label, boolean button, int code) {
        if(getCategory(category) == null) {
            addCategory(category);
        }
        Category c = getCategory(category);
        c.addEntrySet(EntrySetFactory.generateEntryEmpty(c.prefix(), label, button, code));
    }

//---  Getter Methods   -----------------------------------------------------------------------

    //-- Meta  ------------------------------------------------

    public String getHeader() {
        return header;
    }

    public static HandlePanel getHandlePanel() {
        return p;
    }

    public Category getCategory(String title) {
        for(Category c : categories) {
            if(c.getTitle().equals(title)) {
                return c;
            }
        }
        return null;
    }

    public Category getCategoryFromCode(int code) {
        for(Category c : categories) {
            if(c.contains(code)) {
                return c;
            }
        }
        return null;
    }

    //-- Access Contents  -------------------------------------

    public String getTextFromCode(int code, int posit){
        Category c = getCategoryFromCode(code);
        if(c != null)
            return c.getContent(code, posit);
        return null;
    }

    public Integer getIntegerFromCode(int code, int posit) {
        return Integer.parseInt(getTextFromCode(code, posit));
    }

    public Boolean getCheckboxContentsFromCode(int code) {
        return getTextFromCode(code, 0).contentEquals(EntrySetFactory.SIGNIFIER_TRUE);
    }

    public List<String> getContentFromCode(int code) {
        return getCategoryFromCode(code).getContents(code);
    }

}
