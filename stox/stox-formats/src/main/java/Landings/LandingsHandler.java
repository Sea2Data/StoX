/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Landings;

import LandingsTypes.v2.LandingsdataType;
import XMLHandling.NamespaceVersionHandler;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class LandingsHandler extends NamespaceVersionHandler<LandingsdataType>{
    
    
       public LandingsHandler() {
        this.latestNamespace = "http://www.imr.no/formats/landinger/v2";
        this.latestBioticClass = LandingsdataType.class;
        this.compatibleNamespaces = null;
    }
}
