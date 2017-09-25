package thread;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author aasmunds
 */
public class Runner extends SwingWorker<Void, Void> {

    private Boolean finished = true;
    private Boolean pause = false;
    private Integer func;
    private Object parameter = null;

    @Override
    protected Void doInBackground() {
        while (!finished) {
            if (pause) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                if (functionNeedsParametersFromUsers(func)) {
                    pause = true;
                    getPropertyChangeSupport().firePropertyChange("get-param-by-user", null, func);
                } else {
                    // function has enough parameters to run.
                    runFunction(func);
                    getPropertyChangeSupport().firePropertyChange("function-end", null, func);
                    finished = func == 3;
                    func++;
                }
            }
        }
        return null;
    }

    public void runFunction(Integer func) {
        try {
            // Do something
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean functionNeedsParametersFromUsers(Integer func) {
        return func == 2 && parameter == null;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Boolean getPause() {
        return pause;
    }

    public void setPause(Boolean pause) {
        this.pause = pause;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    public Boolean isRunning() {
        return !(getFinished() || getPause());
    }

    public Integer getFunc() {
        return func;
    }

    public void setFunc(Integer func) {
        this.func = func;
    }
}
