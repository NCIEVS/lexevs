
package org.LexGrid.LexBIG.Impl.function.codednodeset;

import java.util.Arrays;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.IncludeForDistributedTests;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.healthmarketscience.sqlbuilder.Query;

@Category(IncludeForDistributedTests.class)
public class MultipeRestrictionsTest extends BaseCodedNodeSetTest {

    @Override
    protected String getTestID() {
        return "MultipeRestrictionsTest Tests";
    }
    @Test
    public void testRestrictDesignationAndHasPropertyType() throws LBException{
        cns = cns.restrictToMatchingDesignations("automobile", SearchDesignationOption.ALL, "contains", null);
        cns = cns.restrictToProperties(
        		null, 
        		new PropertyType[]{PropertyType.DEFINITION}, 
        		null, 
        		null, 
        		null);
        
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, -1);

        assertTrue("Count: " + rcrl.getResolvedConceptReferenceCount(),
        		rcrl.getResolvedConceptReferenceCount() == 1);
        
        ResolvedConceptReference ref = rcrl.getResolvedConceptReference(0);
        
        assertTrue(ref.getCode().equals("A0001"));
    }
    
    @Test
    public void testRestrictDesignationAndDefinition() throws LBException{
        cns = cns.restrictToMatchingDesignations("automobile", SearchDesignationOption.ALL, "exactMatch", null);
        cns = cns.restrictToMatchingProperties(null, 
        		new PropertyType[]{PropertyType.DEFINITION}, 
        		"An automobile", 
        		"exactMatch", 
        		null);
        
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, -1);

        assertTrue("Count: " + rcrl.getResolvedConceptReferenceCount(),
        		rcrl.getResolvedConceptReferenceCount() == 1);
        
        ResolvedConceptReference ref = rcrl.getResolvedConceptReference(0);
        
        assertTrue(ref.getCode().equals("A0001"));
    }
    
    @Test
    public void testRestrictDesignationAndWrongDefinition() throws LBException{
        cns = cns.restrictToMatchingDesignations("automobile", SearchDesignationOption.ALL, "contains", null);
        cns = cns.restrictToMatchingProperties(null, 
        		new PropertyType[]{PropertyType.DEFINITION}, 
        		"An automobileWRONG", 
        		"exactMatch", 
        		null);
        
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, -1);

        assertTrue("Count: " + rcrl.getResolvedConceptReferenceCount(),
        		rcrl.getResolvedConceptReferenceCount() == 0); 
    }
    
    @Test
    public void testRestrictDesignationAndSource() throws LBException{
        cns = cns.restrictToMatchingDesignations("Domestic Auto Makers", SearchDesignationOption.ALL, "contains", null);
        cns = cns.restrictToProperties(
        		null, 
        		new PropertyType[]{PropertyType.PRESENTATION}, 
        		Constructors.createLocalNameList("lexgrid.org"), 
        		null, 
        		null);
        
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, -1);
        
        assertTrue("Count: " + rcrl.getResolvedConceptReferenceCount(),
        		rcrl.getResolvedConceptReferenceCount() == 1);
        
        ResolvedConceptReference ref = rcrl.getResolvedConceptReference(0);
        
        assertTrue(ref.getCode().equals("005"));
    }
     
    @Test
    public void testRestrictDesignationAndSourceAndActive() throws LBException{
        cns = cns.restrictToMatchingDesignations("Domestic Auto Makers", SearchDesignationOption.ALL, "contains", null);
        cns = cns.restrictToProperties(
        		null, 
        		new PropertyType[]{PropertyType.PRESENTATION}, 
        		Constructors.createLocalNameList("lexgrid.org"), 
        		null, 
        		null);
        cns = cns.restrictToStatus(ActiveOption.ACTIVE_ONLY, null);
        
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, -1);
        
        assertTrue("Count: " + rcrl.getResolvedConceptReferenceCount(),
        		rcrl.getResolvedConceptReferenceCount() == 1);
        
        ResolvedConceptReference ref = rcrl.getResolvedConceptReference(0);
        
        assertTrue(ref.getCode().equals("005"));
    }
    
    @Test
    public void testRestrictDesignationAndSourceAndActiveAndWrongPresentation() throws LBException{
        cns = cns.restrictToMatchingDesignations("Domestic Auto Makers", SearchDesignationOption.ALL, "contains", null);
        cns = cns.restrictToProperties(
        		null, 
        		new PropertyType[]{PropertyType.PRESENTATION}, 
        		Constructors.createLocalNameList("lexgrid.org"), 
        		null, 
        		null);
        cns = cns.restrictToStatus(ActiveOption.ACTIVE_ONLY, null);
        cns = cns.restrictToMatchingDesignations("WRONG__DONT_MATCH_ANYTHING", SearchDesignationOption.ALL, "contains", null);
        
        
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, -1);
        
        assertTrue("Count: " + rcrl.getResolvedConceptReferenceCount(),
        		rcrl.getResolvedConceptReferenceCount() == 0);      
    }
    
    @Test
    public void testRestrictDesignationAndSourceAndActiveAndCode() throws LBException{
        cns = cns.restrictToMatchingDesignations("Domestic Auto Makers", SearchDesignationOption.ALL, "contains", null);
        cns = cns.restrictToProperties(
        		null, 
        		new PropertyType[]{PropertyType.PRESENTATION}, 
        		Constructors.createLocalNameList("lexgrid.org"), 
        		null, 
        		null);
        cns = cns.restrictToStatus(ActiveOption.ACTIVE_ONLY, null);
        
        cns = cns.restrictToCodes(Constructors.createConceptReferenceList("005"));
        
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, -1);
        
        assertTrue("Count: " + rcrl.getResolvedConceptReferenceCount(),
        		rcrl.getResolvedConceptReferenceCount() == 1);
        
        ResolvedConceptReference ref = rcrl.getResolvedConceptReference(0);
        
        assertTrue(ref.getCode().equals("005"));
    }
    
    @Test
    public void testRestrictDesignationAndSourceAndActiveAndWrongCode() throws LBException{
        cns = cns.restrictToMatchingDesignations("Domestic Auto Makers", SearchDesignationOption.ALL, "contains", null);
        cns = cns.restrictToProperties(
        		null, 
        		new PropertyType[]{PropertyType.PRESENTATION}, 
        		Constructors.createLocalNameList("lexgrid.org"), 
        		null, 
        		null);
        cns = cns.restrictToStatus(ActiveOption.ACTIVE_ONLY, null);
        
        cns = cns.restrictToCodes(Constructors.createConceptReferenceList("WRONG_CODE"));
        
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, -1);
        
        assertTrue("Count: " + rcrl.getResolvedConceptReferenceCount(),
        		rcrl.getResolvedConceptReferenceCount() == 0);
    }
    
    @Test
    public void testRestrictToPropertySourceMiddleOfList() throws LBException, ParseException{
        cns = cns.restrictToProperties(
        		null, 
        		new PropertyType[]{PropertyType.PRESENTATION}, 
        		Constructors.createLocalNameList("lexgrid.org.org"), 
        		null, 
        		null);
        
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, -1);
        assertTrue(rcrl != null);
        assertTrue(rcrl.getResolvedConceptReferenceCount() > 0);
        assertTrue(Arrays.asList(rcrl.getResolvedConceptReference()).stream().anyMatch(x -> x.getEntityDescription().getContent().equals("Domestic Auto Makers")));
    }
    
    @Test
    public void testRestrictToPropertySourceWithDash() throws LBException, ParseException{
        cns = cns.restrictToProperties(
        		null, 
        		new PropertyType[]{PropertyType.PRESENTATION}, 
        		Constructors.createLocalNameList("lexgrid-org"), 
        		null, 
        		null);
        
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, -1);
        assertTrue(rcrl != null);
        assertTrue(rcrl.getResolvedConceptReferenceCount() > 0);
        assertTrue(Arrays.asList(rcrl.getResolvedConceptReference()).stream().anyMatch(x -> x.getEntityDescription().getContent().equals("Domestic Auto Makers")));
    }
}