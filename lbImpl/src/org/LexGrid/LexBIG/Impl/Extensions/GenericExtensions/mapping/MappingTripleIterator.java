package org.LexGrid.LexBIG.Impl.Extensions.GenericExtensions.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.QualifierSortOption;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ServiceUtility;
import org.LexGrid.relations.Relations;
import org.lexevs.dao.database.access.DaoManager;
import org.lexevs.dao.database.service.daocallback.DaoCallbackService.DaoCallback;
import org.lexevs.locator.LexEvsServiceLocator;
import org.lexevs.paging.AbstractPageableIterator;

public class MappingTripleIterator extends AbstractPageableIterator<ResolvedConceptReference> {

    private static final long serialVersionUID = 5709428653655124881L;

    private Iterator<String> tripleUidIterator;
    
    private MappingAbsoluteCodingSchemeVersionReferences refs;
    private String uri;
    private String version;
    private String relationsContainerName;
    
    protected class MappingAbsoluteCodingSchemeVersionReferences {
        private AbsoluteCodingSchemeVersionReference sourceCodingScheme;
        private AbsoluteCodingSchemeVersionReference targetCodingScheme;
        protected AbsoluteCodingSchemeVersionReference getSourceCodingScheme() {
            return sourceCodingScheme;
        }
        protected void setSourceCodingScheme(AbsoluteCodingSchemeVersionReference sourceCodingScheme) {
            this.sourceCodingScheme = sourceCodingScheme;
        }
        protected AbsoluteCodingSchemeVersionReference getTargetCodingScheme() {
            return targetCodingScheme;
        }
        protected void setTargetCodingScheme(AbsoluteCodingSchemeVersionReference targetCodingScheme) {
            this.targetCodingScheme = targetCodingScheme;
        }  
    }
    
    public MappingTripleIterator(
            String uri, 
            String version,
            String relationsContainerName,
            List<MappingSortOption> sortOptionList, 
            QualifierSortOption qualifierSortOption) throws LBParameterException {
       this.refs = this.getMappingAbsoluteCodingSchemeVersionReference(uri, version, relationsContainerName);
       
       this.uri = uri;
       this.version = version;
       this.relationsContainerName = relationsContainerName;
       
       this.tripleUidIterator = new MappingTripleUidIterator(
               uri, 
               version, 
               relationsContainerName,
               refs, 
               sortOptionList, 
               qualifierSortOption);
    }
    
    @Override
    protected List<? extends ResolvedConceptReference> doPage(int currentPosition, int pageSize) {
        List<String> tripleUidList = new ArrayList<String>();
        for(int i=0;i<pageSize && tripleUidIterator.hasNext();i++) {
            tripleUidList.add(this.tripleUidIterator.next());
        }
        
        return this.buildList(tripleUidList);
    }
    
    private List<? extends ResolvedConceptReference> buildList(List<String> tripleUids){
        List<? extends ResolvedConceptReference> list = 
            LexEvsServiceLocator.getInstance().
                getDatabaseServiceManager().
                    getCodedNodeGraphService().
                        getMappingTriples(
                                uri, 
                                version, 
                                refs.getSourceCodingScheme(), 
                                refs.getTargetCodingScheme(), 
                                relationsContainerName, 
                                tripleUids);
        
        return list;
    }
    
    private MappingAbsoluteCodingSchemeVersionReferences getMappingAbsoluteCodingSchemeVersionReference(
            final String uri, 
            final String version,
            final String relationsContainerName) throws LBParameterException {
        MappingAbsoluteCodingSchemeVersionReferences refs = new MappingAbsoluteCodingSchemeVersionReferences();
        
        Relations relations = 
            LexEvsServiceLocator.getInstance().
                getDatabaseServiceManager().
                    getDaoCallbackService().
                        executeInDaoLayer(new DaoCallback<Relations>() {

            @Override
            public Relations execute(DaoManager daoManager) {
                String codingSchemeUid = daoManager.getCodingSchemeDao(uri, version).getCodingSchemeUIdByUriAndVersion(uri, version);
                
                String relationsUid = daoManager.getAssociationDao(uri, version).getRelationUId(codingSchemeUid, relationsContainerName);
                
                return daoManager.getAssociationDao(uri, version).getRelationsByUId(
                        codingSchemeUid, 
                        relationsUid, 
                        false);
            }
        });
        
        if(! relations.getIsMapping()) {
            throw new LBParameterException("RelationsContainer: " + relations.getContainerName() + " is not a Mapping Relations Container.");
        }
        
        AbsoluteCodingSchemeVersionReference source = ServiceUtility.getAbsoluteCodingSchemeVersionReference(
                relations.getSourceCodingScheme(), 
                Constructors.createCodingSchemeVersionOrTagFromVersion(relations.getSourceCodingSchemeVersion()));
        
        AbsoluteCodingSchemeVersionReference target = ServiceUtility.getAbsoluteCodingSchemeVersionReference(
                relations.getTargetCodingScheme(), 
                Constructors.createCodingSchemeVersionOrTagFromVersion(relations.getTargetCodingSchemeVersion()));
        
        refs.setSourceCodingScheme(source);
        refs.setTargetCodingScheme(target);
        
        return refs;
    }
}