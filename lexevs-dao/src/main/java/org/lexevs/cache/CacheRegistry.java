
package org.lexevs.cache;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
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
	
	@Autowired
	private CacheConfigLocationFactory cacheConfig;
	
	public CacheConfigLocationFactory getCacheConfig() {
		return cacheConfig;
	}

	public void setCacheConfig(CacheConfigLocationFactory cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

	private StatisticsService statisticsService;
	private XmlConfiguration xmlConfig;
	Map<String, CacheConfiguration<?, ?>> cacheConfigs;

/** The caches. */
 private Map<String,Cache<String,Object>> caches = new HashMap<String,Cache<String,Object>>();


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
			Cache cache = this.cacheManager.getCache(cacheName, String.class, Object.class);
			
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
		if(cacheManager != null) {return;} else {
		URL myUrl = null;
		try {
			myUrl = cacheConfig.getObject().getURL();
		} catch (Exception e) {

			e.printStackTrace();
		} 
		xmlConfig = new XmlConfiguration(myUrl); 
		cacheConfigs = xmlConfig.getCacheConfigurations();
		this.statisticsService = new DefaultStatisticsService();
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder().using(statisticsService).newCacheManager(xmlConfig);
		cacheManager.init();
		cacheConfigs.keySet().forEach(x -> this.caches.put(x,this.cacheManager.getCache(x, String.class, Object.class)));
		}
	}

	public void clearAll() {
		List<String> caches = cacheManager.getRuntimeConfiguration().getCacheConfigurations().keySet().stream().collect(Collectors.toList());
		caches.stream().forEach(x -> cacheManager.getCache(x, String.class, Object.class).clear());

	}
	
	public Map<String, Cache<String, Object>> getCaches() {
		return Collections.unmodifiableMap(this.caches);
	}

	public Cache<String, Object> getCache(String cacheName, boolean createIfNotPresent) {
//		synchronized(caches) {
//			if(! (cacheManager.getCache(cacheName, String.class, Object.class) == null)) {
//				if(!createIfNotPresent){
//					throw new RuntimeException("\n\n\n" +
//							"=============================================\n" +
//							"                Cache Error\n" +
//							" Cache: " + cacheName + " not found.\n" +
//					"=============================================\n\n");
////				} else {
		if(cacheManager.getCache(cacheName, String.class, Object.class) != null) {
			cacheManager.getCache(cacheName, String.class, Object.class).;
					return cacheManager.getCache(cacheName, String.class, Object.class);
		}
		else {
//					}
//						CacheWrapper<String,Object> cacheWrapper = new EhCacheWrapper<String,Object>(cacheName,this.cacheManager);
//						this.caches.put(cacheName,cacheWrapper);
//						return this.cacheManager.getCache(cacheName, null, null)
//					} else {
						LoggerFactory.getLogger().debug("Using default cache for Cache Name: " + cacheName);
						
						CacheConfigurationBuilder<String, Object> configuration =
							    CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Object.class, ResourcePoolsBuilder
							        .heap(100))
							        .withExpiry(new ExpiryPolicy<String, Object>() {    
							          @Override
							          public java.time.Duration getExpiryForCreation(String key, Object value) {
							            return getTimeToLiveDuration(key, value);   
							          }

							          private java.time.Duration getTimeToLiveDuration(String key,
											Object value) {
										// TODO Auto-generated method stub
										return null;
									}

									@Override
							          public java.time.Duration getExpiryForAccess(String key, Supplier<? extends Object> value) {
							            return null;  // Keeping the existing expiry
							          }

							          @Override
							          public java.time.Duration getExpiryForUpdate(String key, Supplier<? extends Object> oldValue, Object newValue) {
							            return null;  // Keeping the existing expiry
							          }
							        });

						Cache<String,Object> cache = this.cacheManager.createCache(cacheName, configuration.build());
//
//						CacheWrapper<String,Object> cacheWrapper = 
//							new EhCacheWrapper<String,Object>(cacheName,this.cacheManager);
//						cacheManager.put(cacheName,value);

						return cache;
					}
	}
//				}
//			}
//			return this.caches.get(cacheName);
//		}
//	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

//	public interface CacheWrapper<K, V> 
//	{
//		public void put(K key, V value);
//
//		public V get(K key);
//		
//		public void clear();
//		
//		public int size();
//		
//		public List<V> values();
//	}
//	
//
//	public class EhCacheWrapper<K extends Serializable, V> implements CacheWrapper<K, V> {
//		private final String cacheName;
//		private final CacheManager cacheManager;
//
//		public EhCacheWrapper(final String cacheName, final CacheManager cacheManager){
//			this.cacheName = cacheName;
//			this.cacheManager = cacheManager;
//		}
//
//		public void put(final K key, final V value){
//			if(cacheManager.getCache(cacheName, key.getClass(), ((Object)value).getClass()) == null)
//			{
//				cacheManager.createCache(cacheName, configureDefaultCache(cacheName, key.getClass(),((Object)value).getClass()));
//			}
//			getCache().put(cacheName, (String)key, (Object)value);
//		}
//
//		@SuppressWarnings("unchecked")
//		public V get(final K key, V value){
//			Object results = getCache(key));
//			if (results != null) {
//				
//					return (V) results;
//			}
//			return null;
//		}
//		
//		public int size() {
//			AtomicInteger count = new AtomicInteger(0);
//			getCache().forEach(item -> count.incrementAndGet());
//			return count.get();
//		}
//		
//
//		
//		public void clear(){
//			getCache().clear();
//		}
//		
//		@SuppressWarnings("unchecked")
//		public List<V> values(){
//			List<V> returnList = new ArrayList<V>();
//			
//			getCache().forEach(x -> returnList.add((V) x));
//
//			return returnList;
//		}
//
//		public  Cache<K,V> getCache(){
//			return (Cache<K, V>) cacheManager.getCache(cacheName, String.class, Object.class);
//		}
//		
//		private CacheConfiguration<? extends Serializable, ? extends Object> configureDefaultCache(String cacheName, Class<? extends Serializable> class1, Class<? extends Object> class2){
//			ResourcePools pools = ResourcePoolsBuilder.newResourcePoolsBuilder().build();
//			return CacheConfigurationBuilder.newCacheConfigurationBuilder(class1, class2, pools).build();
//		}
//	}
	
	public Boolean getInThreadCacheClearingState(){
		return this.inCacheClearingState.get();
	}
	
	public void setInThreadCacheClearingState(boolean inThreadClearingState){
		this.inCacheClearingState.set(inThreadClearingState);
	}
	

}