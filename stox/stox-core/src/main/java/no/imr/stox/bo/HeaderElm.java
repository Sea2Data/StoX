/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import java.util.Objects;

/**
 *
 * @author aasmunds
 */
public class HeaderElm {

    String level;
    String name;

    public HeaderElm(String level, String name) {
        this.level = level;
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.level);
        hash = 67 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HeaderElm other = (HeaderElm) obj;
        if (!Objects.equals(this.level, other.level)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return level + "." + name;
    }
    

}
