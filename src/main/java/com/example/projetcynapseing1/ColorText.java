package com.example.projetcynapseing1;

/**
 * Enum representing ANSI codes for text colors and styles formatting in the console.
 * Each constant corresponds to a specific ANSI code that changes the text color,
 * background color, or style (bold, italic, underline, etc.).
 * <p>
 * Usage example:
 * <pre>
 *     System.out.println(ColorText.RED + "Text in red" + ColorText.RESET);
 * </pre>
 * 
 * @author Jonathan
 */
public enum ColorText {
    /** ANSI code to reset text color and style */
    RESET("\u001B[0m"),

    /** ANSI code for red text */
    RED("\u001B[31m"),

    /** ANSI code for red background */
    REDBACK("\u001B[48;5;196m"),

    /** ANSI code for green text */
    GREEN("\u001b[38;5;46m"),

    /** ANSI code for green background */
    GREENBACK("\u001b[48;5;46m"),

    /** ANSI code for yellow text */
    YELLOW("\u001B[33m"),

    /** ANSI code for blue text */
    BLUE("\u001B[34m"),

    /** ANSI code for cyan text */
    CYAN("\u001B[36m"),

    /** ANSI code for magenta text */
    MAGENTA("\u001B[35m"),

    /** ANSI code for bold text */
    BOLD("\u001B[1m"),

    /** ANSI code for italic text */
    ITALIC("\u001B[3m"),

    /** ANSI code for underline text */
    UNDERLINE("\u001B[4m"),

    /** ANSI code for gray text */
    GRAY("\u001b[38;5;244m");

    /** The ANSI code corresponding to the color or style */
    private final String codeColor;

    /**
     * Constructor for the ColorText enum.
     *
     * @param codeColor The ANSI code representing the color or style.
     */
    ColorText(String codeColor) {
        this.codeColor = codeColor;
    }

    /**
     * Returns the ANSI code associated with the color or style.
     *
     * @return The ANSI code as a String.
     */
    public String getCode() {
        return codeColor;
    }

    /**
     * Returns the ANSI code as a String.
     * This allows using the enum directly in string concatenations.
     *
     * @return The ANSI code as a String.
     */
    @Override
    public String toString() {
        return codeColor;
    }
    
}
