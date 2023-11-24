package ui.page.displaypage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import visual.composite.HandlePanel;
import visual.composite.ImageDisplay;

public class FSMImage {

//---  Constants   ----------------------------------------------------------------------------

    private static final File DEFAULT_IMAGE;
    static {
        try (InputStream is = FSMImage.class.getClassLoader().getResourceAsStream("default.png")) {
            DEFAULT_IMAGE = File.createTempFile("tmp", ".png");
            Files.copy(is, DEFAULT_IMAGE.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final String EVENT_RECEIVER_NAME = "Image Display Event Receiver";

//---  Instance Variables   -------------------------------------------------------------------

    private String reference;

    private String path;

    private double zoom;

    private int x;

    private int y;

    private static ImageDisplay iD;

//---  Constructors   -------------------------------------------------------------------------

    public FSMImage (String inRef, String inPath) {
        reference = inRef;
        setImage(inPath);
        zoom = 1;
        x = 0;
        y = 0;
    }

//---  Operations   ---------------------------------------------------------------------------

    public static void attachPanel(HandlePanel p) {
        if(iD == null) {
            iD = new ImageDisplay(DEFAULT_IMAGE.getAbsolutePath(), p);
            iD.toggleUI();
            iD.toggleDisableToggleUI();
        }
        p.addEventReceiver(EVENT_RECEIVER_NAME, iD.generateEventReceiver());
        iD.autofitImage();
    }

    public static void dettachPanel(HandlePanel p) {
        if(iD == null)
            iD = new ImageDisplay(DEFAULT_IMAGE.getAbsolutePath(), p);
        p.removeEventReceiver(EVENT_RECEIVER_NAME);
    }

    public void drawPage() {
        iD.drawPage();
    }

//---  Setter Methods   -----------------------------------------------------------------------

    public void setZoom(double in) {
        zoom = in;
    }

    public void setX(int in) {
        x = in;
    }

    public void setY(int in) {
        y = in;
    }

    public void setReferenceName(String in) {
        reference = in;
    }

    public void setImage(String in) {
        path = in;
        if(in != null) {
            iD.setImage(in);
            iD.autofitImage();
        }
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public double getZoom() {
        return zoom;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getReferenceName() {
        return reference;
    }

    public String getImage() {
        return path;
    }

}
