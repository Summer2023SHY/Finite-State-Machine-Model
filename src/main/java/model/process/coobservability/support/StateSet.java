package model.process.coobservability.support;

import java.util.Arrays;

public class StateSet {

//---  Instance Variables   -------------------------------------------------------------------

    /** Number of plants*/
    private static int sizePlants;
    /** Number of specifications*/
    private static int sizeSpecs;
    /** List of our plants*/
    private String[] plant;
    /** List of our specifications*/
    private String[] spec;

//---  Constructors   -------------------------------------------------------------------------

    /**
     *
     * After statically assigning the size of our plant and specification lists, take an input array
     * of Strings representing State names mapping to each of our plant and specifications. Uses the
     * number of plants and specifications to know when the Strings stop representing plant States
     * and start representing specification States.
     * 
     * @param in - String[] containing the names of States in plants and specifications
     */

    public StateSet(String[] in) {
        plant = new String[sizePlants];
        spec = new String[sizeSpecs];
        System.arraycopy(in, 0, plant, 0, Math.min(in.length, sizePlants));
        if (sizePlants < in.length)
            System.arraycopy(in, sizePlants, spec, 0, in.length - sizePlants);
    }

//---  Setter Methods   -----------------------------------------------------------------------

    /**
     * 
     * Static assignment function to inform the StateSet class of objects how many plants and
     * specifications there are to expect State names from when representing a set of States.
     * 
     * @param sizePl
     * @param sizeSp
     */

    public static void assignSizes(int sizePl, int sizeSp) {
        sizePlants = sizePl;
        sizeSpecs = sizeSp;
    }

//---  Getter Methods   -----------------------------------------------------------------------

    public String[] getStates() {
        String[] out = new String[sizePlants + sizeSpecs];
        System.arraycopy(plant, 0, out, 0, sizePlants);
        System.arraycopy(spec, 0, out, sizePlants, sizeSpecs);
        return out;
    }

    public String[] getPlantStates() {
        return plant;
    }

    public String getPlantState(int index) {
        return plant[index];
    }

    public String[] getSpecStates() {
        return spec;
    }

    public String getSpecState(int index) {
        return spec[index];
    }

    public String getState(int index) {
        boolean isPlant = index < sizePlants;
        return isPlant ? plant[index] : spec[index - sizePlants];
    }

    public String getPairName() {
        String out = "(";
        for(String s : plant) {
            out += s + ", ";
        }
        for(String s : spec) {
            out += s + ", ";
        }
        out = out.substring(0, out.length() - 2);
        return out + ")";
    }

//---  Mechanics   ----------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return getPairName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        else if (o instanceof StateSet) {
            StateSet ot = (StateSet)o;
            return getPairName().equals(ot.getPairName());
        } else
            return false;
    }

    @Override
    public String toString() {
        return Arrays.toString(plant) + " " + Arrays.toString(spec);
    }

}