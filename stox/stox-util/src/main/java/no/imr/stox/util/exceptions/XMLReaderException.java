package no.imr.stox.util.exceptions;

/**
 * Thrown when unexpected error occurs during
 * parsing of xml files.
 *
 * @author kjetilf
 */
public class XMLReaderException extends Exception {

    /**
     * 
     * @param cause
     */
    public XMLReaderException(Exception cause) {
        super(cause);
    }

}
