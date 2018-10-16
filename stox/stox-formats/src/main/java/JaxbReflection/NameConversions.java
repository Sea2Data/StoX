/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JaxbReflection;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class NameConversions {
    
    public static String getter(String nodename){
        String [] name = nodename.split("_");
        String gettername = "get";
        for(String part: name){
            gettername += part.substring(0, 1).toUpperCase() + part.substring(1);
        }
        return gettername;
    }
}
