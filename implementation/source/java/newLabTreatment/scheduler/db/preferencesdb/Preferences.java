package scheduler.db.preferencesdb;

import java.lang.String;

/**
 * This class will contain the data and operations to work with schedule Preferences
 * @author Leland Garofalo
 */
public class Preferences {

    /** The type of the preference */
    int type;

    /** The name of the preference */
    String name;
 
    /** The data contained in the preference */
    String data;

    /** Value to store if preference can be violated */
    int violatable;

    /** Value to store the importance value of the preference */
    int importance;

    /**
     *  Constructor to build a empty Preference
     */
    public Preferences() {
        this.type = 0;
        this.name = "Empty Name";
        this.data = "Empty Data";
        this.violatable = 0;
        this.importance = 0;
    }
    /**
     * Constructor to build a Preference
     *
     * @param name String that holds the name of preference
     * @param data String that holds the data of preference
     * @param type int that holds the type of preference
     * @param importance int that holds the importance value of a preference
     * @param violatable int that holds value of if a preference is violatable
     */
    public Preferences(String name, String data, int type, int importance, int violatable) {
        this.type = type;
        this.name = name;
        this.data = data;
        this.violatable = violatable;
        this.importance = importance;
    }
    /**
     * Returns the name of the preference
     * @return String The name of the preference
     */
    public String getName() {
        return this.name;
    }
    /**
     * Returns the data of a preference
     * @return String The data of the preference
     */
    public String getData() {
        return this.data;
    }

    /**
     * Returns a int representing the type of the preference
     * @return Int type of preference
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference cannot be null
     * p != nil
     *
     * <b><u>Post:</u></b>
     *
     * // The type must be 1,2,3 or 4
     * type == 1 | type == 2 | type == 3 | type == 4
     *
     * &&
     *
     * // The return type must equal the preference type
     * getType() = p.type
     *
     * </pre>
     */
    public int getType() {
        return this.type;
    }
    /**
     * Returns the value of violatable
     * @return int returns violatable value
     */
    public int getViolatable() {
        return this.violatable;
    }
    /**
     * Returns the importance of a preference
     * @return int returns the importance of a preference
     */
    public int getImportance() {
        return this.importance;
    }

    /**
     * Returns a string representation of the Preference
     * which is currently just the name
     * @return String value of Preference
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference.name cannot be null
     * p.name != nil
     *
     * <b><u>Post:</u></b>
     *
     * // The Preference must not have changed
     * p = p'
     *
     * &&
     *
     * // The return value must equal the Preference string
     * toString() = p.name
     *
     * </pre>
     */
    public String toString() {
        return this.name;
    }

    /**
     * Returns a boolean based on if the Preference 
     * has valid data
     * @return Boolean value for validity of string
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference cannot be null
     * p != nil
     *
     * <b><u>Post:</u></b>
     *
     * // The Preference must not have changed
     * p = p'
     *
     *
     * </pre>
     */
    public boolean isValid() {
        if (type >= 0 && type <= 4) {
            if (importance >= 0 && importance <= 10) {
                if (violatable == 0 || violatable == 1) {
                    return true;
                }
            }
        }
        return false;
    }
}

