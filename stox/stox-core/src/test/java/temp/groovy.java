/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import javax.script.Invocable;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.junit.Ignore;

/**
 *
 * @author aasmunds
 */
@Ignore
public class groovy {

    @Test
    public void foo() {
        JexlEngine eng = new JexlEngine();
        eng.setLenient(false);
        eng.setSilent(false);
        Expression expr = eng.createExpression("a == '9' and a =~ ['7','9']");
        System.out.println(expr.dump());
        JexlContext ctx = new MapContext();
        ctx.set("a", "9");
        Boolean result = (Boolean) expr.evaluate(ctx);
        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("jython");

            engine.put("first", 9);
            result = (Boolean) engine.eval("first not in [7,9]");
            result = (Boolean) engine.eval("first in ['7','9']");
            assertEquals(true, result);
            //This next example illustrates calling an invokable function:

            String fact = "def factorial(n) { n == 1 ? 1 : n * factorial(n - 1) }";
            engine.eval(fact);
            Invocable inv = (Invocable) engine;
            Object[] params = {5};
            Object oresult = inv.invokeFunction("factorial", params);
            assertEquals(120, oresult);
        } catch (ScriptException | NoSuchMethodException ex) {
            Logger.getLogger(groovy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
