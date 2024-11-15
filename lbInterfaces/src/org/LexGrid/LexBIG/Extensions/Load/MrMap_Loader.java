
package org.LexGrid.LexBIG.Extensions.Load;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.relations.Relations;

/**
 * Loader class for MrMap and MrSat RRF files resulting in a 
 * mapping coding scheme or schemes depending on the content of these files.
 * @author <a href="mailto:scott.bauer@mayo.edu">Scott Bauer</a>
 *
 */
public interface MrMap_Loader extends Loader {

    public final static String name = "MrMap_Loader";
    
    public final static String description = "This loader loads MRMAP.RRF and MRSAT.RRF" +
    		" files into the LexGrid database as a mapping coding scheme.";



	/**
	 * User designated target Scheme URI. Non-resolving default used if null.
	 * Load content from a candidate resource. This will also result in implicit
	 * generation of standard indices required by the LexBIG runtime.
	 * 
	 * An exception is raised if resources cannot be accessed or another load
	 * operation is already in progress.
	 * 
	 * @param mrMapsource
	 *            String representation of the path to corresponding to the RRF
	 *            file.
	 * @param mrSatSource
	 *            String representation of the path to corresponding to the RRF
	 *            file.
	 * @param nameForMappingScheme
	 *            User designated Mapping Scheme identifier. Default used if
	 *            null.
	 * @param nameForMappingVersion
	 *            User designated Mapping Scheme Version. Default used if null.
	 * @param nameforMappingURI
	 *            User designated Mapping Scheme URI. Non-resolving default used
	 *            if null.
	 * @param sourceScheme
	 *            User designated source scheme identifier which can a local
	 *            scheme to to which mappings resolve. Non resolving default
	 *            created if null.
	 * @param sourceVersion
	 *            User designated source scheme Version. Default used if null.
	 *            Dependent on valid user defined source scheme identifier
	 * @param sourceURI
	 *            User designated source scheme URI. Non-resolving default used
	 *            if null.
	 * @param targetScheme
	 *            User designated target scheme identifier which can a local
	 *            scheme to to which mappings resolve. Non resolving default
	 *            created if null. Dependent on valid user defined source scheme
	 *            identifier.
	 * @param targetVersion
	 *            User designated target scheme Version. Default used if null.
	 * @param targetURI
	 * 
	 * @param source
	 *            String representation of the path to corresponding to the RRF
	 *            file.
	 * @param stopOnErrors
	 *            True means stop if any load error is detected. False means
	 *            attempt to load what can be loaded if recoverable errors are
	 *            encountered.
	 * @param async
	 *            Flag controlling whether load occurs in the calling thread. If
	 *            true, the load will occur in a separate asynchronous process.
	 *            If false, this method blocks until the load operation
	 *            completes or fails. Regardless of setting, the getStatus and
	 *            getLog calls are used to fetch results.
	 * @throws LBException
	 */
	public void load(URI mrMapsource, URI mrSatSource, 
			String nameForMappingScheme,
	        String nameForMappingVersion,
	        String nameforMappingURI,
	        String sourceScheme,
	        String sourceVersion,
	        String sourceURI,
	        String targetScheme,
	        String targetVersion,
	        String targetURI,
	       Map.Entry<String, Relations> relations,
	        boolean stopOnErrors, boolean async) throws LBException;

	/**
	 * Validate content for a candidate resource without performing a load.
	 * Returns without exception if validation succeeds.
	 * 
	 * @param source
	 *            URI corresponding to the XML file.
	 * @param validationLevel
	 *            Supported levels of validation include: 0 = Verify XML is well
	 *            formed. 1 = Verify XML is valid.
	 * @throws LBException
	 */
	public void validate(String source, int validationLevel) throws LBException;


}