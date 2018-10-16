//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.10.16 at 02:52:48 PM CEST 
//


package no.imr.formats.nmdreference.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * <p>Java class for FAOSeaAreaElementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FAOSeaAreaElementType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="norwegian_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="english_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="wmsurl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="imageurl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="regulation_norwegian" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="regulation_english" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="subareas" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="subarea" type="{http://www.imr.no/formats/nmdreference/v2.0}FAOSeaAreaElementType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://www.imr.no/formats/nmdreference/v2.0}DeprecatedAttrGroup"/&gt;
 *       &lt;attGroup ref="{http://www.imr.no/formats/nmdreference/v2.0}GenericAttrRowGroup"/&gt;
 *       &lt;anyAttribute/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FAOSeaAreaElementType", propOrder = {
    "id",
    "code",
    "norwegianName",
    "englishName",
    "wmsurl",
    "imageurl",
    "regulationNorwegian",
    "regulationEnglish",
    "description",
    "subareas"
})
public class FAOSeaAreaElementType {

    protected String id;
    protected String code;
    @XmlElement(name = "norwegian_name")
    protected String norwegianName;
    @XmlElement(name = "english_name")
    protected String englishName;
    protected String wmsurl;
    protected String imageurl;
    @XmlElement(name = "regulation_norwegian")
    protected String regulationNorwegian;
    @XmlElement(name = "regulation_english")
    protected String regulationEnglish;
    protected String description;
    protected FAOSeaAreaElementType.Subareas subareas;
    @XmlAttribute(name = "deprecated")
    protected Boolean deprecated;
    @XmlAttribute(name = "newKey")
    protected String newKey;
    @XmlAttribute(name = "validFrom")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar validFrom;
    @XmlAttribute(name = "validTo")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar validTo;
    @XmlAttribute(name = "updatedTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar updatedTime;
    @XmlAttribute(name = "updatedBy")
    protected String updatedBy;
    @XmlAttribute(name = "insertedTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar insertedTime;
    @XmlAttribute(name = "insertedBy")
    protected String insertedBy;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the norwegianName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNorwegianName() {
        return norwegianName;
    }

    /**
     * Sets the value of the norwegianName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNorwegianName(String value) {
        this.norwegianName = value;
    }

    /**
     * Gets the value of the englishName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * Sets the value of the englishName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnglishName(String value) {
        this.englishName = value;
    }

    /**
     * Gets the value of the wmsurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWmsurl() {
        return wmsurl;
    }

    /**
     * Sets the value of the wmsurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWmsurl(String value) {
        this.wmsurl = value;
    }

    /**
     * Gets the value of the imageurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageurl() {
        return imageurl;
    }

    /**
     * Sets the value of the imageurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageurl(String value) {
        this.imageurl = value;
    }

    /**
     * Gets the value of the regulationNorwegian property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegulationNorwegian() {
        return regulationNorwegian;
    }

    /**
     * Sets the value of the regulationNorwegian property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegulationNorwegian(String value) {
        this.regulationNorwegian = value;
    }

    /**
     * Gets the value of the regulationEnglish property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegulationEnglish() {
        return regulationEnglish;
    }

    /**
     * Sets the value of the regulationEnglish property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegulationEnglish(String value) {
        this.regulationEnglish = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the subareas property.
     * 
     * @return
     *     possible object is
     *     {@link FAOSeaAreaElementType.Subareas }
     *     
     */
    public FAOSeaAreaElementType.Subareas getSubareas() {
        return subareas;
    }

    /**
     * Sets the value of the subareas property.
     * 
     * @param value
     *     allowed object is
     *     {@link FAOSeaAreaElementType.Subareas }
     *     
     */
    public void setSubareas(FAOSeaAreaElementType.Subareas value) {
        this.subareas = value;
    }

    /**
     * Gets the value of the deprecated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeprecated() {
        return deprecated;
    }

    /**
     * Sets the value of the deprecated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeprecated(Boolean value) {
        this.deprecated = value;
    }

    /**
     * Gets the value of the newKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewKey() {
        return newKey;
    }

    /**
     * Sets the value of the newKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewKey(String value) {
        this.newKey = value;
    }

    /**
     * Gets the value of the validFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the value of the validFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setValidFrom(XMLGregorianCalendar value) {
        this.validFrom = value;
    }

    /**
     * Gets the value of the validTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getValidTo() {
        return validTo;
    }

    /**
     * Sets the value of the validTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setValidTo(XMLGregorianCalendar value) {
        this.validTo = value;
    }

    /**
     * Gets the value of the updatedTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUpdatedTime() {
        return updatedTime;
    }

    /**
     * Sets the value of the updatedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUpdatedTime(XMLGregorianCalendar value) {
        this.updatedTime = value;
    }

    /**
     * Gets the value of the updatedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the value of the updatedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedBy(String value) {
        this.updatedBy = value;
    }

    /**
     * Gets the value of the insertedTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInsertedTime() {
        return insertedTime;
    }

    /**
     * Sets the value of the insertedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInsertedTime(XMLGregorianCalendar value) {
        this.insertedTime = value;
    }

    /**
     * Gets the value of the insertedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsertedBy() {
        return insertedBy;
    }

    /**
     * Sets the value of the insertedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsertedBy(String value) {
        this.insertedBy = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="subarea" type="{http://www.imr.no/formats/nmdreference/v2.0}FAOSeaAreaElementType" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "subarea"
    })
    public static class Subareas {

        protected List<FAOSeaAreaElementType> subarea;

        /**
         * Gets the value of the subarea property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the subarea property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSubarea().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FAOSeaAreaElementType }
         * 
         * 
         */
        public List<FAOSeaAreaElementType> getSubarea() {
            if (subarea == null) {
                subarea = new ArrayList<FAOSeaAreaElementType>();
            }
            return this.subarea;
        }

    }

}
