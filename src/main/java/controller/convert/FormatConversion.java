package controller.convert;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

/**
 * Handles the transfer of an object from the {@link model.fsm fsm} package to
 * a {@code .dot} format so that it may be visually represented as a graph via
 * graphviz, producing a {@code .jpg} image at a hard-coded location using a
 * name given at the time of creation.
 * 
 * <p>In addition, it also serves to create an .svg file type as well as
 * convert that file into a {@code .tikz} file for use in LaTeX or other
 * programs that read through that.
 * 
 * <p>TODO: Config File is referenced straight from the package, definite
 * location, do not need file naming.
 * 
 * <p>This class is a part of the graphviz package.
 * 
 * @author Ada Clevinger
 * @author Graeme Zinck
 *
 */

public class FormatConversion {

//---  Instance Variables   -------------------------------------------------------------------

    private static String WORKING_PATH = System.getProperty("user.dir");

//---  Initialization   -----------------------------------------------------------------------

    /** Private constructor. */
    private FormatConversion() {
    }

    public static void assignPaths(String workingPath) {
        WORKING_PATH = workingPath;
    }

//---  Operations   ---------------------------------------------------------------------------

    /**
     * This method takes any object extending the TransitionSystem class and converts it to the
     * dot-String-format, using that to generates a .jpg image from it using the GraphViz library.
     *
     * @param fsm - A generic TransitionSystem object that will be converted into a .jpg image.
     * @param name - A String object denoting the name to which the file should be saved, including its name.
     */

    public static String createImgFromFSM(String fsm, String name) throws IOException {
        return generateDotFile(fsm, name, Format.PNG).getAbsolutePath();
    }

    /**
     * This method creates a file representing the provided TransitionSystem object in the .svg format,
     * saving it to a location as defined by the caller. (.svg format is a graphical view of the graph,
     * but is composed of a series of instructions on how to draw the graph to be interpreted by another
     * program. This permits its conversion in another method in this class.)
     *
     * @param fsm - A TransitionSystem extending object that will be converted into .svg format.
     * @param name - A String object denoting the name to which the file should be saved, including its name.
     */

    public static String createSVGFromFSM(String fsm, String name) throws IOException {
        return generateDotFile(fsm, name, Format.SVG).getAbsolutePath();
    }

    /**
     * This method converts a provided file in the .svg format into a file in the .tikz
     * format for use with LaTEX programs. It calls a support class that can be used disjoint
     * from this project to perform the same feat.
     *
     * @param svgFile - A File object containing a TransitionSystem described in the .svg format.
     * @param name - A String object representing the file name to save the new file to.
     */

    public static String createTikZFromSVG(String fsm, String name) throws IOException {
        return SVGtoTikZ.convertSVGToTikZ(generateDotFile(fsm, name, Format.SVG), WORKING_PATH + File.separator + name).getAbsolutePath();
    }

    /**
     * This method takes in a TransitionSystem object and converts it into a representative file
     * of the .tikz format; it firts converts the object to .svg from which it is converted to .tikz
     * via the {@link #createTikZFromSVG(String, String)} method included in this class. (Deleting the interim file.)
     *
     * @param fsm - A TransitionSystem extending object that will be converted into .tikz format.
     * @param name - A String object denoting the name to which the file should be saved, including its name.
     */

    public static String createTikZFromFSM(String fsm, String name) throws IOException {
        File out = generateDotFile(fsm, "DEMOLISH", Format.SVG);
        String ret = SVGtoTikZ.convertSVGToTikZ(out, WORKING_PATH + File.separator + name).getAbsolutePath();
        return ret;
    }

//---  Support Functions   --------------------------------------------------------------------

    public static File generateDotFile(String fsm, String name, Format type) throws IOException {
        Graphviz gv = Graphviz.fromString(fsm);
        File out = new File(WORKING_PATH, name + FilenameUtils.EXTENSION_SEPARATOR + type.fileExtension);
        gv.render(type).toFile(out);
        return out;
    }

}
