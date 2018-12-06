/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Landings;

import HierarchicalData.IO;
import LandingsTypes.v2.LandingsdataType;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Example of stream-filters for landings. These save considerable parsing time and memory usage,
 * compared to in-memory processing for large data sets.
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class FilterLandings {

    /**
     * Filter for skipping certain species
     */
    class SkipSpecies extends XMLFilterImpl {

        private Set<String> species_codes_retained;
        private boolean reading = true;
        private final String seddellinje = "Seddellinje";
        private final String art = "Art_kode";

        public SkipSpecies(Set<String> species_codes_retained) {
            this.species_codes_retained = species_codes_retained;
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {

            if (this.reading) {
                super.endElement(uri, localName, qName);
            } //ensures reading_line is on whenever seddellinje is finished;
            else if (localName.equals(seddellinje)) {
                this.reading = true;
            }

        }

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes atts) throws SAXException {

            //turns reading_line off for this seddellinje if it is not in species_codes_retained
            if (localName.equals(seddellinje)) {
                if (this.species_codes_retained.contains(atts.getValue(art))) {
                    this.reading = true;
                } else {
                    this.reading = false;
                }
            }

            if (this.reading) {
                super.startElement(uri, localName, qName, atts);
            }
        }
    }

        /**
     * Filter for skipping certain species
     */
    class SkipSpeciesAndElements extends XMLFilterImpl {

        private Set<String> species_codes_retained;
        private Set<String> elements_skipped;
        private boolean reading_line = true;
        private boolean reading_element = true;
        private final String seddellinje = "Seddellinje";
        private final String art = "Art_kode";

        public SkipSpeciesAndElements(Set<String> species_codes_retained, Set<String> elements_skipped) {
            this.species_codes_retained = species_codes_retained;
            this.elements_skipped = elements_skipped;
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {

            if (this.reading_line && this.reading_element) {
                super.endElement(uri, localName, qName);
            } //ensures reading_line is on whenever seddellinje is finished;
            
            if (!this.reading_line && localName.equals(seddellinje)) {
                this.reading_line = true;
            }
            
            if (!this.reading_element && this.elements_skipped.contains(localName)){
                this.reading_element = true;
            }

        }

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes atts) throws SAXException {

            //turns reading_line off for this seddellinje if it is not in species_codes_retained
            if (localName.equals(seddellinje)) {
                if (this.species_codes_retained.contains(atts.getValue(art))) {
                    this.reading_line = true;
                } else {
                    this.reading_line = false;
                }
            }
            
            if (this.reading_element && this.elements_skipped.contains(localName)){
                this.reading_element=false;
            }

            if (this.reading_line && this.reading_element) {
                super.startElement(uri, localName, qName, atts);
            }
        }
    }
    
    /**
     * Filter for skipping certain elements
     */
    class SkipElements extends XMLFilterImpl {

        private Set<String> elements_skipped;
        private int depth = 0;

        public SkipElements(Set<String> elements_skipped) {
            this.elements_skipped = elements_skipped;
        }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (this.depth==0) {
            super.endElement(uri, localName, qName);
        } 
        if (this.depth>0 && elements_skipped.contains(localName)) {
            this.depth--;
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        
        if (elements_skipped.contains(localName)){
                this.depth++;
        }

        if (this.depth==0) {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.depth==0) {
            super.characters(ch, start, length);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (this.depth==0) {
            super.ignorableWhitespace(ch, start, length);
        }
    }
    }

    /**
     *
     * @param species_codes_retained
     * @return
     */
    public LandingsdataType readLandingsSpecies(Set<String> species_codes_retained, InputStream xml) throws JAXBException, XMLStreamException, ParserConfigurationException, SAXException, IOException {
        Set<String> skipelements = new HashSet<>();
        skipelements.add("Salgslagdata");
        skipelements.add("Mottaker");
        skipelements.add("Fisker");
        skipelements.add("Fartøy");
        skipelements.add("Mottakendefartøy");
        skipelements.add("Kvote");
        skipelements.add("Dellanding");
        return IO.parse(xml, LandingsdataType.class, new SkipSpeciesAndElements(species_codes_retained, skipelements));
    }

    /**
     * Read only selected tags
     */
    public LandingsdataType readBasic(InputStream xml) throws JAXBException, XMLStreamException, ParserConfigurationException, SAXException, IOException {
        Set<String> skipelements = new HashSet<>();
        skipelements.add("Salgslagdata");
        skipelements.add("Mottaker");
        skipelements.add("Produksjon");
        skipelements.add("Fisker");
        skipelements.add("Fartøy");
        skipelements.add("Mottakendefartøy");
        skipelements.add("Kvote");
        skipelements.add("Dellanding");
    
        return IO.parse(xml, LandingsdataType.class, new SkipElements(skipelements));
    }

}
