
package org.lexevs.cache;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.internal.statistics.DefaultStatisticsService;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.xml.XmlConfiguration;


import org.lexevs.logging.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.NodeList;

public class CacheRegistry implements InitializingBean, DisposableBean {
	
	private static CacheManager cacheManager;
	
//	@Autowired
//	private CacheConfigLocationFactory cacheConfig;
//	
//	public CacheConfigLocationFactory getCacheConfig() {
//		return cacheConfig;
//	}
//
//	public void setCacheConfig(CacheConfigLocationFactory cacheConfig) {
//		this.cacheConfig = cacheConfig;
//	}

	private StatisticsService statisticsService;
//	private XmlConfiguration xmlConfig;
	Map<String, CacheConfiguration<?, ?>> cacheConfigs;

/** The caches. */
// private Map<String,Cache<String,Object>> caches = new HashMap<String,Cache<String,Object>>();


	private final ThreadLocal<Boolean> inCacheClearingState =
		new ThreadLocal<Boolean>();
	
	public void afterPropertiesSet() throws Exception {
		initializeCache();
	}
	
	@Override
	public void destroy() throws Exception {
		LoggerFactory.getLogger().debug(
				getCacheStatisticsStringRepresentation());
		cacheManager.close();
	}
	
	protected String getCacheStatisticsStringRepresentation() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n===============================");
		sb.append("\n         Cache Statistics      \n");
		
		long hits = 0;
		float misses = 0;
		float offheapmemoryUsage = 0;
		float onheapmemoryUsage = 0;
        String[] keys = (String[]) cacheConfigs.keySet().toArray();
		for(int i=0; i < keys.length; i++) {
		 String cacheName = keys[i];
			//Cache cache = cacheManager.getCache(cacheName, String.class, Object.class);
			
			CacheStatistics ehCacheStat = statisticsService.getCacheStatistics(cacheName);
			hits += ehCacheStat.getCacheHits();
			misses += ehCacheStat.getCacheMisses();
			
			sb.append("\n" + statisticsService.getCacheStatistics(cacheName).getTierStatistics().values().toString());
			float offheapcacheMemory = getMegaBytesFromBytes(statisticsService.getCacheStatistics(cacheName).getTierStatistics().get("offHeap").getAllocatedByteSize());
			offheapmemoryUsage += offheapcacheMemory;
			float onHeapcacheMemory = getMegaBytesFromBytes(statisticsService.getCacheStatistics(cacheName).getTierStatistics().get("OnHeap").getAllocatedByteSize());
			onheapmemoryUsage += onHeapcacheMemory;
			sb.append("\n - Off Heap in Memory Size (MB): " + offheapcacheMemory);
			sb.append("\n - On Heap in Memory Size (MB): " + onHeapcacheMemory);

		
		sb.append("\n\n");
		sb.append("\nTOTAL STATS:");
		sb.append("\n - Total Cache Requests: " + (hits + misses));
		sb.append("\n - Hits: " + hits);
		sb.append("\n - Misses: " + misses);
		sb.append("\n - Total Off Heap Memory Usage (MB): " + offheapmemoryUsage);
		sb.append("\n - Total On Heap Memory Usage (MB): " + onheapmemoryUsage);
		sb.append("\n - Cache Efficiency: " + (hits/(misses+hits)));
		
		sb.append("\n===============================\n");
		}
		return sb.toString();
	}
	
	private float getMegaBytesFromBytes(float bytes) {
		//             to KB  to MB
		return bytes / 1024 / 1024;
	}
	
	protected void initializeCache() {
		this.statisticsService = new DefaultStatisticsService();
		cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder()
				.using(statisticsService)
				.build();
		cacheManager.init();
	}

	public void clearAll() {
		List<String> caches = cacheManager.getRuntimeConfiguration().getCacheConfigurations().keySet().stream().collect(Collectors.toList());
		caches.stream().forEach(x -> cacheManager.getCache(x, String.class, Object.class).clear());

	}
	
//	public Map<String, Cache<String, Object>> getCaches() {
//		return Collections.unmodifiableMap(this.caches);
//	}

//	public Cache<String, Object> getCache(String cacheName, boolean createIfNotPresent, Class clazz) {
//			EhcacheFactory<clazz> factory = new org.lexevs.cache.CacheRegistry.EhcacheFactory<clazz>();
//			return (Cache<String, Object>) factory.getCache(cacheName, String.class, clazz);
//
//	}
	
	class EhcacheFactory<T>{
		
		protected CacheConfigurationBuilder<String, T> getDefaultCacheConfiguration(T type) {
			Class<T> clazz = (Class<T>) type.getClass();
			return CacheConfigurationBuilder
					.newCacheConfigurationBuilder(String.class, clazz, 
							ResourcePoolsBuilder
							.heap(100))
					.withExpiry(
							ExpiryPolicyBuilder
							.timeToLiveExpiration(
									Duration
									.ofSeconds(600)));
		}
		
		protected Cache<String, T> getCache(String cacheName, boolean createIfNotPresent, T type) {
			Cache<String, T> cache = (Cache<String, T>) cacheManager.getCache(cacheName, String.class, type.getClass());
			if(cache != null){
				return cache;
			}
			else {
				return cacheManager.createCache(cacheName, getDefaultCacheConfiguration(type));
			}
		}
		
	}


	public void setCacheManager(CacheManager cManager) {
		cacheManager = cManager;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}
	
	public Boolean getInThreadCacheClearingState(){
		return this.inCacheClearingState.get();
	}
	
	public void setInThreadCacheClearingState(boolean inThreadClearingState){
		this.inCacheClearingState.set(inThreadClearingState);
	}
	

}