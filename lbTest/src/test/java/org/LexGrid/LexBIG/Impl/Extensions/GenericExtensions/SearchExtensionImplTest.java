
package org.LexGrid.LexBIG.Impl.Extensions.GenericExtensions;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension.MatchAlgorithm;
import org.LexGrid.LexBIG.Impl.function.LexBIGServiceTestCase;
import org.LexGrid.LexBIG.Impl.testUtility.ServiceHolder;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.RemoveFromDistributedTests;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(BlockJUnit4ClassRunner.class)
public class SearchExtensionImplTest extends LexBIGServiceTestCase {
    final static String testID = "SearchExtensionImplTest";

	@Override
	protected String getTestID() {
		return testID;
	}
	
	@Test
	public void testIsExtensionAvailable() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		assertNotNull(searchExtension);
	}
	
	@Test
    @Category(RemoveFromDistributedTests.class)
	public void testCodeSearchSpecialCharacters() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("C0011(5564)", MatchAlgorithm.CODE_EXACT);
		assertTrue(itr.hasNext());
		assertEquals("C0011(5564)", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testExactPresentationSearchSpecialCharacters() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("a^s sp*cial co{nce]pt you (know/don't know)", MatchAlgorithm.PRESENTATION_EXACT);
		assertTrue(itr.hasNext());
		assertEquals("SpecialCharactersConcept", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
    @Category(RemoveFromDistributedTests.class)
	public void testContainsPresentationSearchSpecialCharacters() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("x-ray", MatchAlgorithm.PRESENTATION_CONTAINS);
		assertTrue(itr.hasNext());
		assertEquals("C0034571", itr.next().getCode());
	}
	
	@Test
	public void testContainsPresentationSearchTwoWords() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("General Motors", MatchAlgorithm.PRESENTATION_CONTAINS);
		assertTrue(itr.hasNext());
		assertEquals("GM", itr.next().getCode());
	}
	
	@Test
	public void testExactPresentationMissingSearchSpecialCharacters() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("a^s spcial co{nce]pt", MatchAlgorithm.PRESENTATION_EXACT);
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testExactPresentationNonPreferred() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("Chevrolet", MatchAlgorithm.PRESENTATION_EXACT);
		assertTrue(itr.hasNext());
		assertEquals("Chevy", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testCodeSearchWrongSpecialCharacters() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("C0011\\)5564\\)", MatchAlgorithm.CODE_EXACT);
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearch() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("Mauthner", MatchAlgorithm.LUCENE);
		assertTrue(itr.hasNext());
		assertEquals("CL:0000246", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchContains() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("Mauth", MatchAlgorithm.PRESENTATION_CONTAINS);
		assertTrue(itr.hasNext());
		assertEquals("CL:0000246", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSearchAll() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search(null, MatchAlgorithm.LUCENE);
		assertTrue(itr.hasNext());
	}
	
	public void testSearchAllEmptyIncludes() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search(null, null, MatchAlgorithm.LUCENE);
		assertTrue(itr.hasNext());
		assertTrue(itr.numberRemaining() > 10);
	}
	
	@Test
    @Category(RemoveFromDistributedTests.class)
	public void testSearchInactive() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		AbsoluteCodingSchemeVersionReference ref = new AbsoluteCodingSchemeVersionReference();
		ref.setCodingSchemeURN(AUTO_URN);
		ref.setCodingSchemeVersion(AUTO_VERSION);
		
		try {
			lbs.getServiceManager(null).deactivateCodingSchemeVersion(ref, null);
			
			ResolvedConceptReferencesIterator itr = searchExtension.search("code:C0001", MatchAlgorithm.CODE_EXACT);
			assertFalse(itr.hasNext());
		} finally {
			lbs.getServiceManager(null).activateCodingSchemeVersion(ref);
		}
	}

	@Test
	 @Category(RemoveFromDistributedTests.class)
	public void testSimpleSearchCorrectFields() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("Jaguar", MatchAlgorithm.PRESENTATION_CONTAINS);
		assertTrue(itr.hasNext());
		
		ResolvedConceptReference ref = itr.next();
		assertEquals("Jaguar", ref.getCode());
		assertEquals("Automobiles", ref.getCodeNamespace());
		assertEquals("Automobiles", ref.getCodingSchemeName());
		assertEquals("urn:oid:11.11.0.1", ref.getCodingSchemeURI());
		assertEquals(1, ref.getEntityType().length);
		assertEquals("concept", ref.getEntityType()[0]);
		assertEquals("Jaguar", ref.getEntityDescription().getContent());
		
		assertFalse(itr.hasNext());
	}
	
	@Test
    @Category(RemoveFromDistributedTests.class)
	@SuppressWarnings("serial")
	public void testSimpleSearchLimitedToCodingScheme() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		final CodingSchemeReference ref = new CodingSchemeReference();
		ref.setCodingScheme(AUTO_SCHEME);
		
		Set<CodingSchemeReference> includes = 
			new HashSet<CodingSchemeReference>() {{ add(ref); }};
			
		ResolvedConceptReferencesIterator itr = searchExtension.search("Jaguar", includes, MatchAlgorithm.PRESENTATION_CONTAINS);
		assertTrue(itr.hasNext());
		assertEquals("Jaguar", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
    @Category(RemoveFromDistributedTests.class)
	@SuppressWarnings("serial")
	public void testSimpleSearchLimitedToDifferentCodingScheme() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		final CodingSchemeReference ref = new CodingSchemeReference();
		ref.setCodingScheme(PARTS_SCHEME);
		
		Set<CodingSchemeReference> includes = 
			new HashSet<CodingSchemeReference>() {{ add(ref); }};
			
		ResolvedConceptReferencesIterator itr = searchExtension.search("Jaguar", includes, MatchAlgorithm.LUCENE);
		assertFalse(itr.hasNext());
	}
	
	@Test
    @Category(RemoveFromDistributedTests.class)
	@SuppressWarnings("serial")
	public void testSimpleSearchExcludeCodingScheme() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		final CodingSchemeReference ref = new CodingSchemeReference();
		ref.setCodingScheme(PARTS_SCHEME);
		
		Set<CodingSchemeReference> excludes = 
			new HashSet<CodingSchemeReference>() {{ add(ref); }};
			
		ResolvedConceptReferencesIterator itr = searchExtension.search("Jaguar", null, excludes, MatchAlgorithm.LUCENE);
		assertTrue(itr.hasNext());
	}
	
	@Test
    @Category(RemoveFromDistributedTests.class)
	@SuppressWarnings("serial")
	public void testSimpleSearchExcludeCodingSchemeNoneReturned() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		final CodingSchemeReference ref = new CodingSchemeReference();
		ref.setCodingScheme(AUTO_SCHEME);
		
		Set<CodingSchemeReference> excludes = 
			new HashSet<CodingSchemeReference>() {{ add(ref); }};
			
		ResolvedConceptReferencesIterator itr = searchExtension.search("Jaguar", null, excludes, MatchAlgorithm.LUCENE);
		assertFalse(itr.hasNext());
	}
	
	@Test
    @Category(RemoveFromDistributedTests.class)
	@SuppressWarnings("serial")
	public void testSimpleSearchLimitedWithIncludeAndExclude() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		final CodingSchemeReference auto = new CodingSchemeReference();
		auto.setCodingScheme(AUTO_SCHEME);
		
		final CodingSchemeReference parts = new CodingSchemeReference();
		parts.setCodingScheme(PARTS_SCHEME);
		
		Set<CodingSchemeReference> includes = 
			new HashSet<CodingSchemeReference>() {{ add(auto); add(parts); }};
			
		Set<CodingSchemeReference> excludes = 
					new HashSet<CodingSchemeReference>() {{ add(auto); }};
			
		ResolvedConceptReferencesIterator itr = searchExtension.search("Tires", includes, excludes, MatchAlgorithm.LUCENE);
		assertTrue(itr.hasNext());
	}
	
	@Test
    @Category(RemoveFromDistributedTests.class)
	@SuppressWarnings("serial")
	public void testSimpleSearchLimitedWithIncludeAndExcludeAllExcluded() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		final CodingSchemeReference auto = new CodingSchemeReference();
		auto.setCodingScheme(AUTO_SCHEME);
		
		final CodingSchemeReference parts = new CodingSchemeReference();
		parts.setCodingScheme(PARTS_SCHEME);
		
		Set<CodingSchemeReference> includes = 
			new HashSet<CodingSchemeReference>() {{ add(auto); add(parts); }};
			
		Set<CodingSchemeReference> excludes = 
					new HashSet<CodingSchemeReference>() {{ add(parts); }};
			
		ResolvedConceptReferencesIterator itr = searchExtension.search("tire", includes, excludes, MatchAlgorithm.LUCENE);
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchWithAnonymousInclude() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
		
		ResolvedConceptReferencesIterator itr = searchExtension.search("Anonymous-mobile", null, null, MatchAlgorithm.CODE_EXACT, true);
		assertTrue(itr.hasNext());
		assertEquals("Anonymous-mobile", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchWithAnonymousExclude() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
		
		ResolvedConceptReferencesIterator itr = searchExtension.search("Anonymous-mobile", null, null, MatchAlgorithm.CODE_EXACT, false);
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchDefault1() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
		
		// by default, we should get back active and inactive entities.
		ResolvedConceptReferencesIterator itr = searchExtension.search("73", null, MatchAlgorithm.CODE_EXACT);
		assertTrue(itr.hasNext());
		assertEquals("73", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchDefault2() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
		
		// by default, we should get back active and inactive entities.
		ResolvedConceptReferencesIterator itr = searchExtension.search("73", null, null, MatchAlgorithm.CODE_EXACT);
		assertTrue(itr.hasNext());
		assertEquals("73", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchWithInactiveInclude() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
		
		ResolvedConceptReferencesIterator itr = searchExtension.search("73", null, null, MatchAlgorithm.CODE_EXACT, false, true);
		assertTrue(itr.hasNext());
		assertEquals("73", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchWithInactiveExclude() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
		
		ResolvedConceptReferencesIterator itr = searchExtension.search("73", null, null, MatchAlgorithm.CODE_EXACT, false, false);
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchCaseInsensitive() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("mauthner", MatchAlgorithm.LUCENE);
		assertTrue(itr.hasNext());
		assertEquals("CL:0000246", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchCaseInsensitiveAndWildcard() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("maut*", MatchAlgorithm.LUCENE);
		assertTrue(itr.hasNext());
		assertEquals("CL:0000246", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchNone() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("____NONE____", MatchAlgorithm.PRESENTATION_CONTAINS);
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchExactCode() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("C0001", MatchAlgorithm.CODE_EXACT);
		assertTrue(itr.hasNext());
		assertEquals("C0001", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void tesExactCodeWrongCase() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("c0001", MatchAlgorithm.CODE_EXACT);
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchExactCodeAndNamespace() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("code:C0001 AND entityCodeNamespace:Automobiles", MatchAlgorithm.LUCENE);
		assertTrue(itr.hasNext());
		assertEquals("C0001", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchExactCodeAndWrongNamespace() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("code:C0001 AND entityCodeNamespace:AutomobilesINVALID", MatchAlgorithm.LUCENE);
		assertFalse(itr.hasNext());
	}
	
	@SuppressWarnings("serial")
	@Test
    @Category(RemoveFromDistributedTests.class)
	public void testSimpleSearchForNonParentDocWithoutAnonymous() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
		
		final CodingSchemeReference auto = new CodingSchemeReference();
		auto.setCodingScheme(AUTO_SCHEME);
		
		final CodingSchemeReference parts = new CodingSchemeReference();
		parts.setCodingScheme(PARTS_SCHEME);
		
		Set<CodingSchemeReference> includes = 
			new HashSet<CodingSchemeReference>() {{ add(auto); add(parts); }};
	
		ResolvedConceptReferencesIterator itr = searchExtension.search(
				"+(+(-active:false ((code:C0001 (+code:C0001 +namespace:Automobiles)) (code:A0001 (+code:A0001 +namespace:automobiles)) (code:T0001 (+code:T0001 +namespace:germanmadeparts)))))", includes, null, MatchAlgorithm.LUCENE, false);
		assertTrue(itr.hasNext());
	}
	
	@Test
	public void testSimpleSearchFuzzyAndNegationWithGrouping() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");

		ResolvedConceptReferencesIterator itr = searchExtension.search("(kar~ -Trailer) AND Car", MatchAlgorithm.LUCENE);
		
		assertTrue(itr.hasNext());
		
		while(itr.hasNext()){
			assertFalse("C0011(5564)".equals(itr.next().getCode()));
		}
	}

	@Test
	public void testNegativeSearch() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");

		ResolvedConceptReferencesIterator itr = searchExtension.search("*:* and -car", MatchAlgorithm.LUCENE);

		assertTrue(itr.hasNext());
	}

	@Test
	public void testExactPresentation() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("Car", MatchAlgorithm.PRESENTATION_EXACT);
		assertTrue(itr.hasNext());
		assertEquals("C0001", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testExactPresentationSpecialCharacters() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("a^s sp*cial co{nce]pt you (know/don't know)", MatchAlgorithm.PRESENTATION_EXACT);
		assertTrue(itr.hasNext());
		assertEquals("SpecialCharactersConcept", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testExactPresentationCaseInSensitive() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("car", MatchAlgorithm.PRESENTATION_EXACT);
		assertTrue(itr.hasNext());
		assertEquals("C0001", itr.next().getCode());
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testGrouping() throws LBException {
		LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
		SearchExtension searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
	
		ResolvedConceptReferencesIterator itr = searchExtension.search("((car AND -Trailer) OR (General AND Motors))", MatchAlgorithm.LUCENE);
		Set<String> codes = new HashSet<String>();
		while(itr.hasNext()){
			codes.add(itr.next().getCode());
		}

		assertTrue(codes.contains("C0001"));
		assertTrue(codes.contains("GM"));
	}

}