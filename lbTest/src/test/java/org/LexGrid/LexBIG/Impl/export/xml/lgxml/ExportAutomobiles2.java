
package org.LexGrid.LexBIG.Impl.export.xml.lgxml;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Impl.export.common.util.AbstractOutputDir;
import org.LexGrid.LexBIG.Impl.export.common.util.CngFactory;
import org.LexGrid.LexBIG.Impl.export.common.util.CnsFactory;
import org.LexGrid.LexBIG.Impl.export.common.util.CodingSchemeChecker;
import org.LexGrid.LexBIG.Impl.export.common.util.ExportDataVerifier;
import org.LexGrid.LexBIG.Impl.export.common.util.Logger;
import org.LexGrid.LexBIG.Impl.export.common.util.TestCleaner;
import org.LexGrid.LexBIG.Impl.export.xml.lgxml.util.ExportHelper;
import org.LexGrid.LexBIG.Impl.export.xml.lgxml.util.ImportHelper;
import org.LexGrid.LexBIG.Impl.export.xml.lgxml.util.XmlExporterTestOutputDir;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;

public class ExportAutomobiles2 extends TestCase {

    private final String INPUT_FILE_NAME_AUTO2 = "resources/testData/lgXmlExport/Automobiles2.xml";    
    private final String CS_AUTO2_URI = "urn:oid:11.11.0.1";
    private final String CS_AUTO2_VERSION = "1.1";
    
    private AbstractOutputDir outputDir;
    
    private final String[] assoc1 = {"<lgRel:source",
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
                "xsi:schemaLocation=\"http://LexGrid.org/schema/2010/01/LexGrid/codingSchemes  ../master/codingSchemes.xsd\"",
                "sourceEntityCodeNamespace=\"Automobiles\" sourceEntityCode=\"A0001\">",
                "<lgRel:target targetEntityCode=\"Tires\" targetEntityCodeNamespace=\"ExpendableParts\"/>",
            "</lgRel:source>"};
    
    private final String[] entity1 = {    
    		"<lgCon:entity",
    		"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
    		"xsi:schemaLocation=\"http://LexGrid.org/schema/2010/01/LexGrid/codingSchemes  ../master/codingSchemes.xsd\"",
    		"isActive=\"true\" status=\"asfd\" entityCode=\"A0001\"",
    		"entityCodeNamespace=\"Automobiles\" isDefined=\"false\">",
    		"<lgCommon:entityDescription>Automobile</lgCommon:entityDescription>",
    		"<lgCon:entityType>concept</lgCon:entityType>",
    		"<lgCon:presentation propertyName=\"textualPresentation\"",
    		"propertyId=\"t1\" propertyType=\"presentation\"", 
    		"language=\"en\" isPreferred=\"true\" matchIfNoContext=\"true\">",
    		"<lgCommon:source>A0001</lgCommon:source>",
    		"<lgCommon:value dataType=\"textplain\">Automobile</lgCommon:value>",
    		"</lgCon:presentation>",
    		"<lgCon:definition propertyName=\"definition\" propertyId=\"p1\"",
    		"propertyType=\"definition\" language=\"en\" isPreferred=\"true\">",
    		"<lgCommon:source>A0001</lgCommon:source>",
    		"<lgCommon:value dataType=\"textplain\">An automobile</lgCommon:value>",
    		"</lgCon:definition>",
    		"</lgCon:entity>"
    };
    
    private final String[] entity2 = {
    		"<lgCon:associationEntity", 
    		"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
    		"xsi:schemaLocation=\"http://LexGrid.org/schema/2010/01/LexGrid/codingSchemes  ../master/codingSchemes.xsd\"",
    		"isActive=\"true\" entityCode=\"uses\"",
    		"entityCodeNamespace=\"Automobiles\" isDefined=\"false\"",
    		"forwardName=\"uses\" isNavigable=\"true\">",
    		"<lgCommon:entityDescription>uses</lgCommon:entityDescription>",
    		"<lgCon:entityType>association</lgCon:entityType>",
    		"</lgCon:associationEntity>"
    };


    
    public void testLexGridExportAutomibiles2() {
    	Logger.log("ExportAutomobiles2: testLexGridExportAutomibiles2: entry");
    	boolean rv = false;
		this.init();
		try {
			
			// check if coding scheme already exists
			rv = CodingSchemeChecker.exists(this.CS_AUTO2_URI, this.CS_AUTO2_VERSION);
			assertFalse("coding scheme " + this.CS_AUTO2_URI + "/" + this.CS_AUTO2_VERSION + " should not exist", rv);
			
			// import coding scheme
			rv = ImportHelper.importLgXml(this.INPUT_FILE_NAME_AUTO2);
			Assert.assertTrue("loaded LexGrid data from " + this.INPUT_FILE_NAME_AUTO2, rv);
			
			// create CNS and CNG objects
			CodedNodeGraph cng = CngFactory.createCngExportAll(this.CS_AUTO2_URI, this.CS_AUTO2_VERSION);
			CodedNodeSet cns = CnsFactory.createCnsExportAll(this.CS_AUTO2_URI, this.CS_AUTO2_VERSION);
			
			// export 
			rv = ExportHelper.export(
					this.CS_AUTO2_URI, 
					this.CS_AUTO2_VERSION, 
					this.outputDir.getOutputDirAsString(), 
					true, 
					cns, 
					cng);
			Assert.assertTrue("exported data to " + this.outputDir.getOutputDirAsString(), rv);
			
			//-----------------------------------------------------------------
			// verify verify verify verify verify verify verify verify
			//-----------------------------------------------------------------
			String fullyQualifiedOutputFile = ExportHelper.getExportedFileName(this.outputDir.getOutputDirAsString());
			
//			rv = ExportDataVerifier.verifyOutFileHasContent(fullyQualifiedOutputFile, this.SEARCH_STRING_ENTITY_CODE_AUTOMOBILE);
//			Assert.assertTrue("search string " + this.SEARCH_STRING_ENTITY_CODE_AUTOMOBILE + " should exist in file", rv);
			
			rv = ExportDataVerifier.verifyOutFileHasContent(fullyQualifiedOutputFile, this.entity1);
			Assert.assertTrue("search string entity1 should exist in file", rv);

			
			rv = ExportDataVerifier.verifyOutFileHasContent(fullyQualifiedOutputFile, this.entity2);
			Assert.assertTrue("search string entity2 should exist in file", rv);
			
			rv = ExportDataVerifier.verifyOutFileHasContent(fullyQualifiedOutputFile, this.assoc1);
			Assert.assertTrue("search string assoc1 should exist in file", rv);
			
			// cleanup 
			TestCleaner.cleanUp(this.outputDir.getOutputDirAsString(), this.CS_AUTO2_URI, this.CS_AUTO2_VERSION);
			this.outputDir.deleteOutputDir();
			
		} catch (LBException e) {
			e.printStackTrace();
			Assert.fail("ExportAutomobiles2: testLexGridExportAutomibiles2: caught exception: " + e.getMessage());
		}
		Logger.log("ExportAutomobiles2: testLexGridExportAutomibiles2: exit");
    }
        
    public void init() {
    	Logger.log("ExportAutomobiles2: init: entry");
    	outputDir = new XmlExporterTestOutputDir();
		// cleanup 
    	Logger.log("ExportAutomobiles2: init: clean up any failed test artifacts");
		TestCleaner.cleanUp(this.outputDir.getOutputDirAsString(), this.CS_AUTO2_URI, this.CS_AUTO2_VERSION);
    	
    	Logger.log("ExportAutomobiles2: init: exit");
    }    
}