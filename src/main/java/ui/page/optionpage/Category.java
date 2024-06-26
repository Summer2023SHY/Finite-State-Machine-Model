package ui.page.optionpage;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ui.page.optionpage.entryset.EntrySet;
import visual.composite.HandlePanel;

public class Category {

//---  Constant Values   ----------------------------------------------------------------------

    private static final Font CATEGORY_FONT = new Font("Serif", Font.BOLD, 14);

//---  Instance Variables   -------------------------------------------------------------------

    private String name;
    private List<EntrySet> sets;
    private boolean open;
    private int catCode;

//---  Constructors   -------------------------------------------------------------------------

    public Category(String nom) {
        name = nom;
        sets = new ArrayList<EntrySet>();
        open = false;
    }

//---  Operations   ---------------------------------------------------------------------------

    public int draw(int y, int heiLine, HandlePanel p) {
        y = drawCategoryHeader(y, heiLine, p);

        if(open) {
            for(EntrySet e : sets) {
                y = e.drawEntrySet(y, heiLine, p);
            }
        }
        else {
            hideContents();
        }
        return y;
    }

    public int drawCategoryHeader(int y, int lineHei, HandlePanel p) {
        int posX = p.getWidth() / 3 / 2;
        int wid =  p.getWidth() * 9/10;
        p.handleText("category_header_" + name + "_header_text", "move", 15, posX, y, wid, lineHei, CATEGORY_FONT, name);
        p.handleButton("category_header_" + name + "_header_butt", "move", 15, posX, y, wid, lineHei, catCode);
        p.handleLine("category_header_" + name + "_header_line", "move", 5, p.getWidth() / 20, y + p.getHeight() / 40, p.getWidth() * 11 / 20, y + p.getHeight() / 40, 3, Color.black);
        return y + lineHei;
    }

    public boolean handleInput(int code, HandlePanel p) {
        EntrySet e = getEntrySet(code);
        if(e != null) {
            return getEntrySet(code).handleInput(code, p);
        }
        return true;
    }

    public void toggleOpen() {
        open = !open;
    }

    public void hideContents() {
        OptionPage.getHandlePanel().moveElementPrefixed(prefix(), -100, -100);
    }

//---  Setter Methods   -----------------------------------------------------------------------

    public void setCode(int in) {
        catCode = in;
    }

    public void addEntrySet(EntrySet in) {
        sets.add(in);
    }

    public void setEntrySetContents(int code, List<String> ref) {
        EntrySet e = getEntrySet(code);
        if(e != null) {
            e.setContents(ref);
        }
    }

    public void setEntrySetContent(int code, int index, String ref) {
        EntrySet e = getEntrySet(code);
        if(e != null) {
            e.setContent(ref, index);
        }
    }

    public void resetEntrySetContent(int code, int index) {
        EntrySet e = getEntrySet(code);
        if(e != null) {
            e.resetContentAt(index);
        }
    }

    public void deleteEntrySetContent(int code, int index) {
        EntrySet e = getEntrySet(code);
        if(e != null) {
            e.deleteContentAt(index);
        }
    }

    public String resetEntrySetContents(int code) {
        EntrySet e = getEntrySet(code);
        if(e != null) {
            return e.resetContent();
        }
        return StringUtils.EMPTY;
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public String prefix() {
        return "category_" + getTitle();
    }

    public String getTitle() {
        return name;
    }

    public boolean isOpen() {
        return open;
    }

    public int getCode() {
        return catCode;
    }

    //-- EntrySet  --------------------------------------------

    protected EntrySet getEntrySet(int code) {
        for(EntrySet e : sets) {
            if(e.containsCode(code)) {
                return e;
            }
        }
        return null;
    }

    public List<String> getContents(int code){
        EntrySet e = getEntrySet(code);
        if(e != null) {
            return e.getContents();
        }
        return null;
    }

    public String getContent(int code, int posit) {
        EntrySet e = getEntrySet(code);
        if(e != null) {
            return e.getContentAt(posit);
        }
        return null;
    }

    public boolean contains(int code) {
        for(EntrySet e : sets) {
            if(e.containsCode(code)) {
                return true;
            }
        }
        return false;
    }

}
