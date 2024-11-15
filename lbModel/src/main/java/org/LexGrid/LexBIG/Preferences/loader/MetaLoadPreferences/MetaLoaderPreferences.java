/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

package org.LexGrid.LexBIG.Preferences.loader.MetaLoadPreferences;

/**
 * Class MetaLoaderPreferences.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class MetaLoaderPreferences extends org.LexGrid.LexBIG.Preferences.loader.LoadPreferences.LoaderPreferences 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The Metathesaurus distribution contains a compilation of
     * several individual sources
     *  (e.g. ICD9, MeSH). Each source represents, in essence, a
     * unique code system
     *  However, the LexBIG Meta loader is designed to load all
     * Metathesaurus codes to a
     *  single LexGrid coding scheme for convenient load and query.
     * Since there is only
     *  one coding scheme, metadata for individual sources must be
     * stored separately.
     *  The loader solves this issue by storing the information to
     * auxiliary metadata associated
     *  with the primary coding scheme. The auxiliary metadata
     * complies with the LexGrid
     *  CodingSchemes model definition, with each Metathesaurus
     * source represented by
     *  an individual CodingScheme and using standard LexGrid
     * properties to describe the
     *  additional meta-information provided by NIH.
     *  As a convenience to the administrator, the auxiliary
     * metadata generated by the loader
     *  can optionally be written to a specified file in the local
     * file system. This preference is
     *  used to specify the location of the reference file. If this
     * preference is absent, auxiliary
     *  metadata is still generated and assigned to the primary
     * coding scheme but a temp file is
     *  used If specified and the file already exists, it is
     * overwritten. If specified and the file
     *  does not exist, it is created by the loader.
     */
    private java.lang.String _XMLMetadataFilePath;


      //----------------/
     //- Constructors -/
    //----------------/

    public MetaLoaderPreferences() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'XMLMetadataFilePath'. The field
     * 'XMLMetadataFilePath' has the following description: The
     * Metathesaurus distribution contains a compilation of several
     * individual sources
     *  (e.g. ICD9, MeSH). Each source represents, in essence, a
     * unique code system
     *  However, the LexBIG Meta loader is designed to load all
     * Metathesaurus codes to a
     *  single LexGrid coding scheme for convenient load and query.
     * Since there is only
     *  one coding scheme, metadata for individual sources must be
     * stored separately.
     *  The loader solves this issue by storing the information to
     * auxiliary metadata associated
     *  with the primary coding scheme. The auxiliary metadata
     * complies with the LexGrid
     *  CodingSchemes model definition, with each Metathesaurus
     * source represented by
     *  an individual CodingScheme and using standard LexGrid
     * properties to describe the
     *  additional meta-information provided by NIH.
     *  As a convenience to the administrator, the auxiliary
     * metadata generated by the loader
     *  can optionally be written to a specified file in the local
     * file system. This preference is
     *  used to specify the location of the reference file. If this
     * preference is absent, auxiliary
     *  metadata is still generated and assigned to the primary
     * coding scheme but a temp file is
     *  used If specified and the file already exists, it is
     * overwritten. If specified and the file
     *  does not exist, it is created by the loader.
     * 
     * @return the value of field 'XMLMetadataFilePath'.
     */
    public java.lang.String getXMLMetadataFilePath(
    ) {
        return this._XMLMetadataFilePath;
    }

    /**
     * Sets the value of field 'XMLMetadataFilePath'. The field
     * 'XMLMetadataFilePath' has the following description: The
     * Metathesaurus distribution contains a compilation of several
     * individual sources
     *  (e.g. ICD9, MeSH). Each source represents, in essence, a
     * unique code system
     *  However, the LexBIG Meta loader is designed to load all
     * Metathesaurus codes to a
     *  single LexGrid coding scheme for convenient load and query.
     * Since there is only
     *  one coding scheme, metadata for individual sources must be
     * stored separately.
     *  The loader solves this issue by storing the information to
     * auxiliary metadata associated
     *  with the primary coding scheme. The auxiliary metadata
     * complies with the LexGrid
     *  CodingSchemes model definition, with each Metathesaurus
     * source represented by
     *  an individual CodingScheme and using standard LexGrid
     * properties to describe the
     *  additional meta-information provided by NIH.
     *  As a convenience to the administrator, the auxiliary
     * metadata generated by the loader
     *  can optionally be written to a specified file in the local
     * file system. This preference is
     *  used to specify the location of the reference file. If this
     * preference is absent, auxiliary
     *  metadata is still generated and assigned to the primary
     * coding scheme but a temp file is
     *  used If specified and the file already exists, it is
     * overwritten. If specified and the file
     *  does not exist, it is created by the loader.
     * 
     * @param XMLMetadataFilePath the value of field
     * 'XMLMetadataFilePath'.
     */
    public void setXMLMetadataFilePath(
            final java.lang.String XMLMetadataFilePath) {
        this._XMLMetadataFilePath = XMLMetadataFilePath;
    }

}
