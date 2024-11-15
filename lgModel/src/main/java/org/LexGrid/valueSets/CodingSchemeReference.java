/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

package org.LexGrid.valueSets;

/**
 * A reference to all of the entity codes in a given coding scheme.
 * 
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class CodingSchemeReference extends org.mayo.edu.lgModel.LexGridBase 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The local identifier of the coding scheme that the entity
     * codes are drawn from . codingSchemeName must match a local
     * id of a supportedCodingScheme in the mappings section.
     */
    private java.lang.String _codingScheme;


      //----------------/
     //- Constructors -/
    //----------------/

    public CodingSchemeReference() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'codingScheme'. The field
     * 'codingScheme' has the following description: The local
     * identifier of the coding scheme that the entity codes are
     * drawn from . codingSchemeName must match a local id of a
     * supportedCodingScheme in the mappings section.
     * 
     * @return the value of field 'CodingScheme'.
     */
    public java.lang.String getCodingScheme(
    ) {
        return this._codingScheme;
    }

    /**
     * Method isValid.
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid(
    ) {
        try {
            validate();
        } catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    }

    /**
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(
            final java.io.Writer out)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, out);
    }

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(
            final org.xml.sax.ContentHandler handler)
    throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, handler);
    }

    /**
     * Sets the value of field 'codingScheme'. The field
     * 'codingScheme' has the following description: The local
     * identifier of the coding scheme that the entity codes are
     * drawn from . codingSchemeName must match a local id of a
     * supportedCodingScheme in the mappings section.
     * 
     * @param codingScheme the value of field 'codingScheme'.
     */
    public void setCodingScheme(
            final java.lang.String codingScheme) {
        this._codingScheme = codingScheme;
    }

    /**
     * Method unmarshalCodingSchemeReference.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * org.LexGrid.valueSets.CodingSchemeReference
     */
    public static org.LexGrid.valueSets.CodingSchemeReference unmarshalCodingSchemeReference(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (org.LexGrid.valueSets.CodingSchemeReference) org.exolab.castor.xml.Unmarshaller.unmarshal(org.LexGrid.valueSets.CodingSchemeReference.class, reader);
    }

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate(
    )
    throws org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    }

}
