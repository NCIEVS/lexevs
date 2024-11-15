
package org.LexGrid.LexBIG.admin;

import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Extensions.Load.MetaData_Loader;
import org.LexGrid.LexBIG.Extensions.Load.OWL2_Loader;
import org.LexGrid.LexBIG.Extensions.Load.OWL_Loader;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.LexBIGServiceManager;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.annotations.LgAdminFunction;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.kohsuke.args4j.CmdLineParser;
import org.lexevs.system.ResourceManager;
import org.lexgrid.loader.umls.launch.UmlsBatchLoaderLauncher;

import edu.mayo.informatics.resourcereader.core.StringUtils;

/**
 * Loads UMLS content, provided as a collection of RRF files in a
 * single directory.  Files may comprise the entire UMLS distribution
 * or pruned via the MetamorphoSys tool.  A complete list of
 * source vocabularies is available online at
 * http://www.nlm.nih.gov/research/umls/metaa1.html.
 * 
 * <pre>
 * Example: java org.LexGrid.LexBIG.admin.LoadUmlsBatchWithMetadata
 *   -in,--input <uri> URI or path of the directory containing the NLM files. Path string must be preceded by "file:",
 *   -s,--source vocabularies to load.
*    -meta --meatadata input &lt;uri&gt; URI or path specifying location of the metadata source file.  
 *        metadata is applied to the code system and code system version being loaded.
 *   -metav  --validate metadata &lt;int&gt; Perform validation of the metadata source file
 *         without loading data. Supported levels of validation include:
 *         0 = Verify document is valid.
 *         metadata is validated against the code system and code system version being loaded.
 *   -metao, --overwrite If specified, existing metadata for the code system
 *         will be erased. Otherwise, new metadata will be appended to
 *         existing metadata (if present).  
 *   -metaf,--force Force overwrite (no confirmation).
 *   
 * Example: java -Xmx512m -cp lgRuntime.jar
 *  org.LexGrid.LexBIG.admin.LoadUmlsBatchWithMetadata -in "file:///path/to/directory/" -s "PSY"
 * -or-
 *  org.LexGrid.LexBIG.admin.LoadUmlsBatchWithMetadata -in "file:///path/to/directory/" -s "PSY" -meta &quot;file:///path/to/metadata.xml&quot; -metao
 * </pre>
 */
@LgAdminFunction
public class LoadUmlsBatchWithMetadata {

    private static final String EXAMPLE_CALL =  "\n LoadUmlsBatchWithMetadata -in \"file:///path/to/directory/\" -s \"PSY\""
            + "\n LoadUmlsBatchWithMetadata -in \"file:///path/to/directory/\" -s \"PSY\" -meta \"file:///path/to/metadata.xml\" -metav 0 -metao";

    public static void main(String[] args) {
        try {        
            new LoadUmlsBatchWithMetadata().run(args);
        } catch (LBResourceUnavailableException e) {
            Util.displayAndLogError("Resource Unavailable: " + e.getMessage() , e);
        } catch (Exception e) {
            Util.displayAndLogError("REQUEST FAILED !!!", e);
        }
    }

    public LoadUmlsBatchWithMetadata() {
        super();
    }

    /**
     * Primary entry point for the program.
     * 
     * @throws Exception
     */
    public void run(String[] args) throws Exception {
        synchronized (ResourceManager.instance()) {
            
            // Parse the command line ...
            CommandLine cl = null;
            Options options = getCommandOptions();
            int v1 = -1;
    
            try {
                cl = new BasicParser().parse(options, args);
                if (cl.hasOption("metav")){
                    v1 = Integer.parseInt(cl.getOptionValue("metav"));
                }
            } catch (ParseException e) {
                Util.displayCommandOptions("LoadUmlsBatchWithMetadata",options,
                        EXAMPLE_CALL + Util.getURIHelp(), e);
                return;
            }
            
            // metatdata - input file (optional)
            String metaUriStr = cl.getOptionValue("meta");
            URI metaUri = null;
            if (!StringUtils.isNull(metaUriStr)){
               metaUri = Util.string2FileURI(metaUriStr);
            }
              
            // metatdata - validate input file (optional)
            if (v1 >= 0) {
                Util.displayAndLogMessage("VALIDATING METADATA SOURCE URI: " + metaUri.toString());
            } 
           
            // metadata force
            boolean force = cl.hasOption("metaf");
            // metadata overwrite
            boolean overwrite = cl.hasOption("metao");
            
            // launch the UMLS loader
            UmlsBatchLoaderLauncher launcher = new UmlsBatchLoaderLauncher();
            CmdLineParser parser = new CmdLineParser(launcher);
            
            String[] umlsArgs = cleanseArgs(args);
            
            parser.parseArgument(umlsArgs);    
            launcher.load();
            
            LexBIGService lbs = LexBIGServiceImpl.defaultInstance();
            LexBIGServiceManager lbsm = lbs.getServiceManager(null);
                    
            // If there is a metadata URI passed in, then load it.
            if (metaUri != null) {
                CodingSchemeSummary css = null;
                
                // Find the registered extension handling this type of load ...               
                MetaData_Loader metadataLoader = (MetaData_Loader) lbsm.getLoader("MetaDataLoader");
                                           
                Enumeration<? extends CodingSchemeRendering> schemes = lbs.getSupportedCodingSchemes()
                        .enumerateCodingSchemeRendering();
                while (schemes.hasMoreElements() && css == null) {
                    CodingSchemeSummary summary = schemes.nextElement().getCodingSchemeSummary();
                                                                     
                    AbsoluteCodingSchemeVersionReference[] refs = launcher.getCodingSchemeRefs();
                    for (int i = 0; i < refs.length; i++) {
                        AbsoluteCodingSchemeVersionReference ref = refs[i];
                                                
                        if (ref.getCodingSchemeURN().equalsIgnoreCase(summary.getCodingSchemeURI())
                                && ref.getCodingSchemeVersion().equalsIgnoreCase(summary.getRepresentsVersion())){
                            css = summary;
                            break;
                        }
                    }
                }
                
                if (css == null){
                    Util.displayAndLogMessage("Unable to apply metadata");
                    return;
                }
                             
                if (v1 >=0 ){
                    Util.displayAndLogMessage("Validating Metadata");
                    metadataLoader.validateAuxiliaryData(metaUri, Constructors.createAbsoluteCodingSchemeVersionReference(css), v1);
                    Util.displayAndLogMessage("METADATA VALIDATION SUCCESSFUL");
                }
                else{
                    boolean confirmed = true;
                    if (overwrite && !force) {
                        Util.displayMessage("OVERWRITE EXISTING METADATA? ('Y' to confirm, any other key to cancel)");
                        char choice = Util.getConsoleCharacter();
                        confirmed = choice == 'Y' || choice == 'y';
                    }
                    if (confirmed) {
                        Util.displayAndLogMessage("Loading Metadata");
                        metadataLoader.loadAuxiliaryData(metaUri, Constructors.createAbsoluteCodingSchemeVersionReference(css),
                                overwrite, false, true);
                        Util.displayLoaderStatus(metadataLoader);
                    }
                }

            }
        }
    }

    private String[] cleanseArgs(String[] args) {
        String[] umlsArgs = null;
        ArrayList<String> argList = new ArrayList<>();
        
        for (int i = 0; i < args.length; i++) {
            // don't add -meta options
            if (!args[i].startsWith("-meta")){
                argList.add(args[i]);
            }
            else{
                i++;
                // if -meta was found above, don't add its value, if there is one.
                while (i < args.length && args[i].startsWith("-meta")){
                    i++;
                }
                if (i < args.length && args[i].startsWith("-")){
                    argList.add(args[i]);
                }
            }
        }
        umlsArgs = (String[])argList.toArray(new String[argList.size()]);
        return umlsArgs;
    }

    /**
     * Return supported command options.
     * 
     * @return org.apache.commons.cli.Options
     */
    private Options getCommandOptions() {
        Options options = new Options();
        Option o;

        o = new Option("in", "input", true, "URI or path of the directory containing the NLM files. Path string must be preceded by \"file:\"");
        o.setArgName("uri");
        o.setRequired(true);
        options.addOption(o);

        o = new Option("s", "source", true, "Source vocabularies to load.");
        o.setArgName("uri");
        o.setRequired(true);
        options.addOption(o);
        
        o = new Option("meta", "metadata", true, "URI or path specifying location of the metadata file.");
        o.setArgName("uri");
        o.setRequired(false);
        options.addOption(o);
        
        o = new Option("metav", "metadata validate", true, "Validation metadata only; no load. 0 to verify well-formed xml.");
        o.setArgName("int");
        o.setRequired(false);
        options.addOption(o);
        
        o = new Option("metao", "overwrite", false, "If specified, existing metadata for the code system "
                + "will be erased. Otherwise, new metadata will be appended " + "to existing metadata (if present). ");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("metaf", "force", false, "Force overwrite (no confirmation).");
        o.setRequired(false);
        options.addOption(o);

        return options;
    }

}