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
 * <p>Java class for TaxaElementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaxaElementType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tsn" type="{http://www.imr.no/formats/nmdreference/v2.0}StringWithAnyAttributes" minOccurs="0"/&gt;
 *         &lt;element name="aphiaid" type="{http://www.imr.no/formats/nmdreference/v2.0}StringWithAnyAttributes" minOccurs="0"/&gt;
 *         &lt;element name="imr" type="{http://www.imr.no/formats/nmdreference/v2.0}BooleanWithAnyAttributes" minOccurs="0"/&gt;
 *         &lt;element name="nodc" type="{http://www.imr.no/formats/nmdreference/v2.0}StringWithAnyAttributes" minOccurs="0"/&gt;
 *         &lt;element name="fao" type="{http://www.imr.no/formats/nmdreference/v2.0}StringWithAnyAttributes" minOccurs="0"/&gt;
 *         &lt;element name="information" type="{http://www.imr.no/formats/nmdreference/v2.0}StringWithAnyAttributes" minOccurs="0"/&gt;
 *         &lt;element name="pgnapes" type="{http://www.imr.no/formats/nmdreference/v2.0}StringWithAnyAttributes" minOccurs="0"/&gt;
 *         &lt;element name="ruCode" type="{http://www.imr.no/formats/nmdreference/v2.0}IntegerWithAnyAttributes" minOccurs="0"/&gt;
 *         &lt;element name="tradefield" type="{http://www.imr.no/formats/nmdreference/v2.0}BooleanWithAnyAttributes" minOccurs="0"/&gt;
 *         &lt;element name="description" type="{http://www.imr.no/formats/nmdreference/v2.0}StringWithAnyAttributes" minOccurs="0"/&gt;
 *         &lt;element name="restrictions" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="restriction" type="{http://www.imr.no/formats/nmdreference/v2.0}RestrictionElementType" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Lists" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="list" type="{http://www.imr.no/formats/nmdreference/v2.0}TaxaListElementType" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="TaxaSynonyms" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="synonym" type="{http://www.imr.no/formats/nmdreference/v2.0}TaxaSynonymElementType" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Stocks" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="stock" type="{http://www.imr.no/formats/nmdreference/v2.0}StockElementType" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://www.imr.no/formats/nmdreference/v2.0}GenericAttrRowGroup"/&gt;
 *       &lt;attGroup ref="{http://www.imr.no/formats/nmdreference/v2.0}DeprecatedAttrGroup"/&gt;
 *       &lt;anyAttribute/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaxaElementType", propOrder = {
    "id",
    "tsn",
    "aphiaid",
    "imr",
    "nodc",
    "fao",
    "information",
    "pgnapes",
    "ruCode",
    "tradefield",
    "description",
    "restrictions",
    "lists",
    "taxaSynonyms",
    "stocks"
})
public class TaxaElementType {

    protected String id;
    protected StringWithAnyAttributes tsn;
    protected StringWithAnyAttributes aphiaid;
    protected BooleanWithAnyAttributes imr;
    protected StringWithAnyAttributes nodc;
    protected StringWithAnyAttributes fao;
    protected StringWithAnyAttributes information;
    protected StringWithAnyAttributes pgnapes;
    protected IntegerWithAnyAttributes ruCode;
    protected BooleanWithAnyAttributes tradefield;
    protected StringWithAnyAttributes description;
    protected TaxaElementType.Restrictions restrictions;
    @XmlElement(name = "Lists")
    protected TaxaElementType.Lists lists;
    @XmlElement(name = "TaxaSynonyms")
    protected TaxaElementType.TaxaSynonyms taxaSynonyms;
    @XmlElement(name = "Stocks")
    protected TaxaElementType.Stocks stocks;
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
     * Gets the value of the tsn property.
     * 
     * @return
     *     possible object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public StringWithAnyAttributes getTsn() {
        return tsn;
    }

    /**
     * Sets the value of the tsn property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public void setTsn(StringWithAnyAttributes value) {
        this.tsn = value;
    }

    /**
     * Gets the value of the aphiaid property.
     * 
     * @return
     *     possible object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public StringWithAnyAttributes getAphiaid() {
        return aphiaid;
    }

    /**
     * Sets the value of the aphiaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public void setAphiaid(StringWithAnyAttributes value) {
        this.aphiaid = value;
    }

    /**
     * Gets the value of the imr property.
     * 
     * @return
     *     possible object is
     *     {@link BooleanWithAnyAttributes }
     *     
     */
    public BooleanWithAnyAttributes getImr() {
        return imr;
    }

    /**
     * Sets the value of the imr property.
     * 
     * @param value
     *     allowed object is
     *     {@link BooleanWithAnyAttributes }
     *     
     */
    public void setImr(BooleanWithAnyAttributes value) {
        this.imr = value;
    }

    /**
     * Gets the value of the nodc property.
     * 
     * @return
     *     possible object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public StringWithAnyAttributes getNodc() {
        return nodc;
    }

    /**
     * Sets the value of the nodc property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public void setNodc(StringWithAnyAttributes value) {
        this.nodc = value;
    }

    /**
     * Gets the value of the fao property.
     * 
     * @return
     *     possible object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public StringWithAnyAttributes getFao() {
        return fao;
    }

    /**
     * Sets the value of the fao property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public void setFao(StringWithAnyAttributes value) {
        this.fao = value;
    }

    /**
     * Gets the value of the information property.
     * 
     * @return
     *     possible object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public StringWithAnyAttributes getInformation() {
        return information;
    }

    /**
     * Sets the value of the information property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public void setInformation(StringWithAnyAttributes value) {
        this.information = value;
    }

    /**
     * Gets the value of the pgnapes property.
     * 
     * @return
     *     possible object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public StringWithAnyAttributes getPgnapes() {
        return pgnapes;
    }

    /**
     * Sets the value of the pgnapes property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public void setPgnapes(StringWithAnyAttributes value) {
        this.pgnapes = value;
    }

    /**
     * Gets the value of the ruCode property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerWithAnyAttributes }
     *     
     */
    public IntegerWithAnyAttributes getRuCode() {
        return ruCode;
    }

    /**
     * Sets the value of the ruCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerWithAnyAttributes }
     *     
     */
    public void setRuCode(IntegerWithAnyAttributes value) {
        this.ruCode = value;
    }

    /**
     * Gets the value of the tradefield property.
     * 
     * @return
     *     possible object is
     *     {@link BooleanWithAnyAttributes }
     *     
     */
    public BooleanWithAnyAttributes getTradefield() {
        return tradefield;
    }

    /**
     * Sets the value of the tradefield property.
     * 
     * @param value
     *     allowed object is
     *     {@link BooleanWithAnyAttributes }
     *     
     */
    public void setTradefield(BooleanWithAnyAttributes value) {
        this.tradefield = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public StringWithAnyAttributes getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringWithAnyAttributes }
     *     
     */
    public void setDescription(StringWithAnyAttributes value) {
        this.description = value;
    }

    /**
     * Gets the value of the restrictions property.
     * 
     * @return
     *     possible object is
     *     {@link TaxaElementType.Restrictions }
     *     
     */
    public TaxaElementType.Restrictions getRestrictions() {
        return restrictions;
    }

    /**
     * Sets the value of the restrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxaElementType.Restrictions }
     *     
     */
    public void setRestrictions(TaxaElementType.Restrictions value) {
        this.restrictions = value;
    }

    /**
     * Gets the value of the lists property.
     * 
     * @return
     *     possible object is
     *     {@link TaxaElementType.Lists }
     *     
     */
    public TaxaElementType.Lists getLists() {
        return lists;
    }

    /**
     * Sets the value of the lists property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxaElementType.Lists }
     *     
     */
    public void setLists(TaxaElementType.Lists value) {
        this.lists = value;
    }

    /**
     * Gets the value of the taxaSynonyms property.
     * 
     * @return
     *     possible object is
     *     {@link TaxaElementType.TaxaSynonyms }
     *     
     */
    public TaxaElementType.TaxaSynonyms getTaxaSynonyms() {
        return taxaSynonyms;
    }

    /**
     * Sets the value of the taxaSynonyms property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxaElementType.TaxaSynonyms }
     *     
     */
    public void setTaxaSynonyms(TaxaElementType.TaxaSynonyms value) {
        this.taxaSynonyms = value;
    }

    /**
     * Gets the value of the stocks property.
     * 
     * @return
     *     possible object is
     *     {@link TaxaElementType.Stocks }
     *     
     */
    public TaxaElementType.Stocks getStocks() {
        return stocks;
    }

    /**
     * Sets the value of the stocks property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxaElementType.Stocks }
     *     
     */
    public void setStocks(TaxaElementType.Stocks value) {
        this.stocks = value;
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
     *         &lt;element name="list" type="{http://www.imr.no/formats/nmdreference/v2.0}TaxaListElementType" maxOccurs="unbounded"/&gt;
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
        "list"
    })
    public static class Lists {

        @XmlElement(required = true)
        protected List<TaxaListElementType> list;

        /**
         * Gets the value of the list property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the list property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getList().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TaxaListElementType }
         * 
         * 
         */
        public List<TaxaListElementType> getList() {
            if (list == null) {
                list = new ArrayList<TaxaListElementType>();
            }
            return this.list;
        }

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
     *         &lt;element name="restriction" type="{http://www.imr.no/formats/nmdreference/v2.0}RestrictionElementType" maxOccurs="unbounded"/&gt;
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
        "restriction"
    })
    public static class Restrictions {

        @XmlElement(required = true)
        protected List<RestrictionElementType> restriction;

        /**
         * Gets the value of the restriction property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the restriction property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRestriction().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link RestrictionElementType }
         * 
         * 
         */
        public List<RestrictionElementType> getRestriction() {
            if (restriction == null) {
                restriction = new ArrayList<RestrictionElementType>();
            }
            return this.restriction;
        }

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
     *         &lt;element name="stock" type="{http://www.imr.no/formats/nmdreference/v2.0}StockElementType" maxOccurs="unbounded"/&gt;
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
        "stock"
    })
    public static class Stocks {

        @XmlElement(required = true)
        protected List<StockElementType> stock;

        /**
         * Gets the value of the stock property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the stock property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getStock().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link StockElementType }
         * 
         * 
         */
        public List<StockElementType> getStock() {
            if (stock == null) {
                stock = new ArrayList<StockElementType>();
            }
            return this.stock;
        }

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
     *         &lt;element name="synonym" type="{http://www.imr.no/formats/nmdreference/v2.0}TaxaSynonymElementType" maxOccurs="unbounded"/&gt;
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
        "synonym"
    })
    public static class TaxaSynonyms {

        @XmlElement(required = true)
        protected List<TaxaSynonymElementType> synonym;

        /**
         * Gets the value of the synonym property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the synonym property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSynonym().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TaxaSynonymElementType }
         * 
         * 
         */
        public List<TaxaSynonymElementType> getSynonym() {
            if (synonym == null) {
                synonym = new ArrayList<TaxaSynonymElementType>();
            }
            return this.synonym;
        }

    }

}
