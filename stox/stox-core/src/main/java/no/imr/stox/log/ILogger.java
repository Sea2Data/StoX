package no.imr.stox.log;

/**
 * TODO: hva er funksjonen til dette interfacet?
 *
 * @author aasmunds
 */
public interface ILogger {

    void error(String error, Exception e);

    void log(String msg);
}
