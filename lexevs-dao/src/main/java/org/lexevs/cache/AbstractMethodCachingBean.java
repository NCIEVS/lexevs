
package org.lexevs.cache;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.Boolean;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.LexGrid.LexBIG.Utility.logging.LgLoggerIF;
import org.apache.commons.lang3.ClassUtils;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.internal.statistics.DefaultStatisticsService;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import com.google.common.primitives.Primitives;
import org.lexevs.cache.annotation.CacheMethod;
import org.lexevs.cache.annotation.Cacheable;
import org.lexevs.cache.annotation.ClearCache;
import org.lexevs.cache.annotation.ParameterKey;
import org.lexevs.dao.database.utility.DaoUtility;
import org.lexevs.logging.LoggerFactory;
import org.lexevs.system.constants.SystemVariables;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * The Class MethodCachingProxy.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractMethodCachingBean<T> implements InitializingBean, DisposableBean{
	
	private static CacheManager cacheManager;

	private static final boolean DEFAULT_ISOLATE_CACHES_ON_CLEAR = false;

	/** The logger. */
	private LgLoggerIF logger;

	private SystemVariables systemVariables;
	
	private static String NULL_VALUE_KEY = "null";
	
	private static String NULL_VALUE_CACHE_PLACEHOLDER = "NULL_VALUE_CACHE_PLACEHOLDER";
	
	private static final String CLEAR_CACHE_PRESENT = "CLEAR_CASE_ANNOTATION_PRESENT";

	//private CacheRegistry cacheRegistry;
	
	private boolean isolateCachesOnClear = DEFAULT_ISOLATE_CACHES_ON_CLEAR;
	
	private StatisticsService statisticsService;
	
	Map<String, CacheConfiguration<?, ?>> cacheConfigs;
	
	private static Map<String,Cache<String,Object>> caches = new ConcurrentHashMap<String,Cache<String,Object>>();
	
	
	public void afterPropertiesSet() throws Exception {
		initializeCache();
	}
	
	public Map<String,Cache<String,Object>> getCaches(){
		return caches;
	}
	
	protected void initializeCache() {
		this.statisticsService = new DefaultStatisticsService();
		cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder()
				.using(statisticsService)
				.build();
		cacheManager.init();
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

	public void clearCacheByName(String name) {
		this.getCacheFromName(name).clear();
	}
	

	/**
	 * Clear cache.
	 * 
	 * @param pjp the pjp
	 * 
	 * @return the object
	 * 
	 * @throws Throwable the throwable
	 */
	protected Object clearCache(T joinPoint, Method method) throws Throwable {
		try {
			logger.debug("Clearing cache.");

//			if(isolateCachesOnClear){
//				this.cacheRegistry.setInThreadCacheClearingState(true);
//			}

			Object target = this.getTarget(joinPoint);

	//		Cacheable cacheableAnnotation = AnnotationUtils.findAnnotation(target.getClass(), Cacheable.class);

			ClearCache clearCacheAnnotation = method.getAnnotation(ClearCache.class);

			Object returnObj = this.proceed(joinPoint);
			
			clearCache(clearCacheAnnotation, joinPoint);

			return returnObj;
		} finally {
//			if(isolateCachesOnClear){
//				this.cacheRegistry.setInThreadCacheClearingState(false);
//			}
		}
	}

	private void clearCache(
			ClearCache clearCacheAnnotation, T joinPoint) {
		if(clearCacheAnnotation.clearAll()) {
			clearAll();
		} else {

			//for(String cacheName : clearCacheAnnotation.clearCaches()){
				Cache<String,Object> cache = this.getCacheFromName(getMethodKey(joinPoint));
						//, false, joinPoint);
				if(cache != null) {
				cache.clear();
			//	}
			}
			
//			Cache<String,Object> cache = this.getCacheFromName(cacheableAnnotation.cacheName());
//					//, false,joinPoint);
//	
//			cache.clear();
		}
	}
	
	public void clearAll() {
		List<String> caches = cacheManager
				.getRuntimeConfiguration()
				.getCacheConfigurations()
				.keySet()
				.stream()
				.collect(Collectors.toList());
		
		caches
		.stream()
		.forEach(x -> 
		this.clearCacheByName(x));

	}
	
	/**
	 * Cache method.
	 * 
	 * @param pjp the pjp
	 * 
	 * @return the object
	 * 
	 * @throws Throwable the throwable
	 */
	protected Object doCacheMethod(T joinPoint) throws Throwable {
	
//      I don't see any evidence this caching status is ever set to true.  Not used.  		
//		if(!CacheSessionManager.getCachingStatus()) {
//			return this.proceed(joinPoint);
//		}
		
		String key = this.getMethodKey(joinPoint);
		if(key.equals(CLEAR_CACHE_PRESENT)) {
			return this.clearCache(joinPoint, this.getMethod(joinPoint));
		}

		Method method = this.getMethod(joinPoint);

//		if(method.isAnnotationPresent(CacheMethod.class)
//				&&
//					method.isAnnotationPresent(ClearCache.class)){
//			throw new RuntimeException("Cannot both Cache method results and clear the Cache in " +
//					"the same method. Please only use @CacheMethod OR @ClearCache -- not both. " +
//					" This occured on method: " + method.toString());
//		}

		//Cacheable cacheableAnnotation = AnnotationUtils.findAnnotation(target.getClass(), Cacheable.class);
		CacheMethod cacheMethodAnnotation = AnnotationUtils.findAnnotation(method, CacheMethod.class);
	
		Cache<String,Object> cache = this.getCacheFromName(key);
				//key, true, joinPoint);
		if(cache == null) {
			cache = this.getCache(key, true, joinPoint);
		}
//		caches.put(key, cache);

		
//		Seems to be dead code -- there is an exception thrown earlier if this case exists
		
//		if(method.isAnnotationPresent(ClearCache.class)) {
//			return this.clearCache(joinPoint, method);
//		}

		Object value = cache.get(key);
		if(value != null) {
			this.logger.debug("Cache hit on: " + key);
			if(value.equals(NULL_VALUE_CACHE_PLACEHOLDER)) {
				return null;
			} else {
				return returnResult(value, cacheMethodAnnotation);
			}
		} else {
			this.logger.debug("Caching miss on: " + key);
		}

		Object result = this.proceed(joinPoint);

		if(result != null) {
			cache.put(key, result);
		} 
//		else {
//			cache.put(key, null);
//		}
		
	//		caches.put(key, cache);
			return returnResult(result, cacheMethodAnnotation);
	}
	
	public Object getPrimitiveWrapperValue(Object wrapper) {
		if(Primitives.isWrapperType(wrapper.getClass())) {

		String name = wrapper.getClass().getSimpleName();
		
		 switch (name) {
         case "Boolean":
         		  return ((Boolean)wrapper).booleanValue();
                 
         case "Byte": 
         		  return ((Byte)wrapper).byteValue();
                 
         case "Character":
         		  return ((Character)wrapper).charValue();
                 
         case "Double":
         		  return ((Double)wrapper).doubleValue();
                 
         case "Float":
         		  return ((Float)wrapper).floatValue();
                 
         case "Integer":
           		  return ((Integer)wrapper).intValue();
                 
         case "Long":
         		  return ((Long)wrapper).longValue();
                 
         case "Short":
         		  return ((Short)wrapper).shortValue();
         default: throw new RuntimeException("Attempting to return primitive from "
         		+ "wrapper on unqualified class.  This should never happen"); 
     }
		}
		return null;
	}
	
	public String getMethodKey(T joinPoint) {
		Method method = this.getMethod(joinPoint);

		if(method.isAnnotationPresent(ClearCache.class)){
			return CLEAR_CACHE_PRESENT; }
		Object target = this.getTarget(joinPoint);
		
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		
		return this.getKeyFromMethod(target.getClass().getName(),
					method.getName(),
					this.getArguments(joinPoint), 
					parameterAnnotations);
	}
	
	private Object returnResult(Object result, CacheMethod cacheMethodAnnotation) {
		if(result != null && 
				cacheMethodAnnotation.cloneResult() && 
				ClassUtils.isAssignable(result.getClass(), Serializable.class)) {
			return DaoUtility.deepClone((Serializable)result);
		} else {
			return result;
		}
	}

	protected abstract Method getMethod(T joinPoint);

	protected abstract Object getTarget(T joinPoint);

	protected abstract Object proceed(T joinPoint) throws Throwable;

	protected abstract Object[] getArguments(T joinPoint);
	
	protected abstract Class<Object> getReturnType(T jointPoint);
	
	public Cache<String,Object> getCacheFromName(String cacheName){
		Map<String, CacheConfiguration<?,?>> configs = cacheManager.getRuntimeConfiguration().getCacheConfigurations();
		Class<Object> valueType = null;
		if(configs != null && configs.size() > 0) {
		CacheConfiguration config = configs.get(cacheName);
		if(config != null) {
		valueType = config.getValueType();
		}
		}
		
		return cacheManager.getCache(cacheName, String.class, valueType);
	}
	

	/**
	 * Gets the key from method.
	 * 
	 * @param className the class name
	 * @param signature the signature
	 * @param arguments the arguments
	 * @param parameterAnnotations 
	 * 
	 * @return the key from method
	 */
	protected String getKeyFromMethod(
			String className, 
			String signature, 
			Object[] arguments, 
			Annotation[][] parameterAnnotations){
		StringBuffer sb = new StringBuffer();
		sb.append(className);
		sb.append(signature);
		for(int i=0;i<arguments.length;i++){
			Object arg = arguments[i];
			
			Annotation[] annotations = parameterAnnotations[i];
			
			ParameterKey parameterKey = getParameterKeyAnnotation(annotations);
			
			sb.append(getArgumentKey(arg, parameterKey));
		}
		return sb.toString();
	}
	
	private ParameterKey getParameterKeyAnnotation(Annotation[] annotations) {
		List<ParameterKey> returnList = new ArrayList<ParameterKey>();
		
		for(Annotation annotation : annotations) {
			if(annotation instanceof ParameterKey) {
				returnList.add((ParameterKey)annotation);
			}
		}
		if(returnList.size() > 1) {
			throw new RuntimeException("Only one ParameterKey annotation allowed per Parameter.");
		}
		
		if(returnList.size() == 1) {
			return returnList.get(0);
		} else {
			return null;
		}
	}
	
	protected String getArgumentKey(Object argument, ParameterKey key) {
		if(key != null) {
			StringBuffer sb = new StringBuffer();
			String[] fields = key.field();
			for(String fieldName : fields) {
				Field field = 
					ReflectionUtils.findField(argument.getClass(), fieldName);
				field.setAccessible(true);
				
				Object fieldArg = ReflectionUtils.getField(field, argument);
				sb.append(getArgumentKey(fieldArg));
			}
			return sb.toString();
		} else {
			return getArgumentKey(argument);
		}
	}
	
	protected String getArgumentKey(Object argument) {
		StringBuffer sb = new StringBuffer();
		if(argument == null) {
			sb.append(NULL_VALUE_KEY);
		} else {
			sb.append(argument.hashCode());
		}
		return sb.toString();
	}
	
//	protected Map<String, Cache<String, Object>> getCaches(){
//		return this.cacheRegistry.getCaches();
//	}
	
	/**
	 * Gets the logger.
	 * 
	 * @return the logger
	 */
	public LgLoggerIF getLogger() {
		return logger;
	}

	/**
	 * Sets the logger.
	 * 
	 * @param logger the new logger
	 */
	public void setLogger(LgLoggerIF logger) {
		this.logger = logger;
	}

	public SystemVariables getSystemVariables() {
		return systemVariables;
	}

	public void setSystemVariables(SystemVariables systemVariables) {
		this.systemVariables = systemVariables;
	}


		
	public CacheConfigurationBuilder getDefaultCacheConfiguration(T joinPoint) {
		
			
			return CacheConfigurationBuilder
					.newCacheConfigurationBuilder(String.class, getReturnType(joinPoint), 
							ResourcePoolsBuilder
							.heap(100))
					.withExpiry(
							ExpiryPolicyBuilder
							.timeToLiveExpiration(
									Duration
									.ofSeconds(600)));
		}
		
	public Cache getCache(String cacheName, boolean createIfNotPresent, T joinPoint) {
		
		Cache cache = cacheManager.getCache(cacheName, String.class, getReturnType(joinPoint));
			if(cache != null){
				return cache;
			}
			else {
				return cacheManager.createCache(cacheName, getDefaultCacheConfiguration(joinPoint));
			}
		}
	
	@Override
	public void destroy() throws Exception {
		LoggerFactory.getLogger().debug(
				getCacheStatisticsStringRepresentation());
		cacheManager.close();
	}

}