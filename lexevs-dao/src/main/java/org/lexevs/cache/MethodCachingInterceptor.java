
package org.lexevs.cache;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

public class MethodCachingInterceptor extends AbstractMethodCachingBean<MethodInvocation> implements MethodInterceptor{

	@Override
	protected Object[] getArguments(MethodInvocation joinPoint) {
		return joinPoint.getArguments();
	}

	@Override
	protected Method getMethod(MethodInvocation joinPoint) {
		return joinPoint.getMethod();
	}

	@Override
	protected Object getTarget(MethodInvocation joinPoint) {
		return joinPoint.getThis();
	}

	@Override
	protected Object proceed(MethodInvocation joinPoint) throws Throwable {
		return joinPoint.proceed();
	}

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		return this.doCacheMethod(methodInvocation);
	}

	@Override
	protected Class<Object> getReturnType(MethodInvocation jointPoint) {
		return (Class<Object>) jointPoint.getMethod().getReturnType();
	}
}