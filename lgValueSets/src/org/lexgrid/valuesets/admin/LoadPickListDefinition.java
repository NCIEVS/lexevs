
package org.lexgrid.valuesets.admin;
import java.net.URI;

import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.admin.Util;
import org.LexGrid.annotations.LgAdminFunction;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.lexevs.system.service.LexEvsResourceManagingService;
import org.lexgrid.valuesets.LexEVSPickListDefinitionServices;
import org.lexgrid.valuesets.impl.LexEVSPickListDefinitionServicesImpl;

/**
 * Imports the source file, provided in LexGrid canonical format, to the LexBIG
 * repository.
 * 
 * <pre>
 * Example: java org.lexgrid.valuesets.admin.LoadPickList
 *   -in,--input &lt;uri&gt; URI or path specifying location of the source file
 *   -v, --validate &lt;int&gt; Perform validation of the candidate
 *         resource without loading data.  
 *         Supported levels of validation include:
 *         0 = Verify document is well-formed
 *         1 = Verify document is valid
 * 
 * Example: java -Xmx512m -cp lgRuntime.jar
 *  org.lexgrid.valuesets.admin.LoadPickList -in &quot;file:///path/to/file.xml&quot; -v 0
 * </pre>
 * 
 * @author <A HREF="mailto:dwarkanath.sridhar@mayo.edu">Sridhar Dwarkanath</A>
 */
@LgAdminFunction
public class LoadPickListDefinition {

	private LexEvsResourceManagingService service = new LexEvsResourceManagingService();
	
    public static void main(String[] args) {
    	try {
            new LoadPickListDefinition().run(args);
        } catch (LBResourceUnavailableException e) {
        	Util.displayAndLogError("Resource not available: " + e.getMessage() , e);
        } catch (Exception e) {
            Util.displayAndLogError("REQUEST FAILED !!!", e);
        }
    }

    public LoadPickListDefinition() {
        super();
    }

    /**
     * Primary entry point for the program.
     * 
     * @throws Exception
     */
    public void run(String[] args) throws Exception {
        synchronized (service) {

            // Parse the command line ...
            CommandLine cl = null;
            Options options = getCommandOptions();
            int vl = -1;
            try {
                cl = new BasicParser().parse(options, args);
                if (cl.hasOption("v"))
                    vl = Integer.parseInt(cl.getOptionValue("v"));
            } catch (Exception e) {
            	Util.displayAndLogError("Parsing of command line options failed: " + e.getMessage() , e);
                Util
                        .displayCommandOptions(
                                "LoadPickList",
                                options,
                                "\n LoadPickList -in \"file:///path/to/file.xml\""
                                        + "\n LoadPickList -in \"file:///path/to/file.xml\" -v 0" + Util.getURIHelp(), e);
                return;
            }

            // Interpret provided values ...
            URI source = Util.string2FileURI(cl.getOptionValue("in"));
            if (vl >= 0) {
                Util.displayAndLogMessage("VALIDATION SOURCE URI: " + source.toString());
            } else {
                Util.displayAndLogMessage("LOADING FROM URI: " + source.toString());
            }

            LexEVSPickListDefinitionServices pls = LexEVSPickListDefinitionServicesImpl.defaultInstance();
            
            // Perform the requested load or validate action ...
            if (vl >= 0) {
                pls.validate(source, vl);
                Util.displayAndLogMessage("VALIDATION SUCCESSFUL");
            } else {
            	pls.loadPickList(source.toString(), false);
            }            
        }
    }

    /**
     * Return supported command options.
     * 
     * @return org.apache.commons.cli.Options
     */
    private Options getCommandOptions() {
        Options options = new Options();
        Option o;

        o = new Option("in", "input", true, "URI or path specifying location of the source file.");
        o.setArgName("uri");
        o.setRequired(true);
        options.addOption(o);

        o = new Option("v", "validate", true, "Validation only; no load. "
                + "0 to verify well-formed xml; 1 to check validity.");
        o.setArgName("int");
        o.setRequired(false);
        options.addOption(o);
        
        return options;
    }

}