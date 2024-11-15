
package edu.mayo.informatics.lexgrid.convert.exporters.xml.lgxml.impl;

/*
 * code based on example from: http://www.hsqldb.org/doc/guide/apb.html
 */

import java.util.List;

import org.LexGrid.relations.AssociationEntity;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;

import edu.mayo.informatics.lexgrid.convert.exporters.xml.lgxml.interfaces.AssociationEntityCache;

public class AssociationEntityCacheEhcacheImpl implements AssociationEntityCache {

    private Cache theCache;
    private CacheManager theCacheManager;
    
    public AssociationEntityCacheEhcacheImpl() {
        //Create a CacheManager using defaults
        this.theCacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();

        //Create a Cache specifying its configuration.
        int maxElementsInMemory = 100;
//        this.theCache = new Cache(
//          new CacheConfigurationBuilder("AssociationEntityCacheEhcacheImpl")
//            .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
//            .overflowToDisk(true)
//            .eternal(true)
//            .diskPersistent(false)
//            //.diskStorePath(diskStorePath)
//            .diskExpiryThreadIntervalSeconds(0).build);
//        try {
//            this.theCacheManager .addCache(this.theCache);
//        } catch (net.sf.ehcache.ObjectExistsException e) {
//            
//            System.out.println("AssociationEntityCacheEhcacheImpl: " + e.getMessage());
//            System.out.println("AssociationEntityCacheEhcacheImpl: remove and re-add the cache");
//            this.theCacheManager .removalAll();
//            this.theCacheManager .addCache(this.theCache);
//        }
//        System.out.println("AssociationEntityCacheEhcacheImpl: DiskStorePath=" + this.theCache.getCacheConfiguration().getDiskStorePath());
//        System.out.println("AssociationEntityCacheEhcacheImpl: debug version 2");
         
        
    }
    
    private String makeKey(AssociationEntity associationEntity) {
        String key = associationEntity.getEntityCode() + associationEntity.getEntityCodeNamespace();
        return key;
    }
    
    @Override
    public void put(AssociationEntity associationEntity) {
        String key = this.makeKey(associationEntity);
//        Element element = new Element(key, associationEntity);
//        this.theCache.put(element);
    }

    @Override
    public void clear() {
//        this.theCache.removeAll();
    }

    @Override
    public void dumpCacheContentsToStdOut() {
        System.out.println("AssociationEntityCacheEhcacheImpl: dumpCacheContentsToStdOut:");
//        List<String> keyList = this.theCache.getKeys();
//        for(int i=0; i<keyList.size(); ++i) {
//            Element e = this.theCache.get(keyList.get(i));
//            System.out.println("  element[" + i + "] key:" + e.getKey() + " value:" + e.getValue());
//        }
//        
    }
    
    @Override
    public AssociationEntity get(String key) {
//        Element e = this.theCache.get(key);
//        if(e == null) {
//            return null;
//        }
//        AssociationEntity rv = (AssociationEntity)e.getValue();
//        return rv;
        return null;
    }

    @Override
    public List<String> getKeys() {
//        List<String> keyList = this.theCache.getKeys();
//        return keyList;
        return null;
    }

    @Override
    public void destroy() {
   //     this.theCacheManager.shutdown();
    }

}