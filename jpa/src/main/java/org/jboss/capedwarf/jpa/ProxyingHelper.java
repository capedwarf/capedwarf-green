/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.capedwarf.jpa;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

/**
 * Proxy wrapping helper.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
abstract class ProxyingHelper implements ProxyingFactory
{
   public <T extends Entity> T createProxy(Class<T> entityClass) throws Exception
   {
      if (entityClass == null)
         throw new IllegalArgumentException("Null entity class");

      T instance = entityClass.newInstance();
      return wrap(instance);
   }

   public boolean isProxy(Entity entity)
   {
      if (entity == null)
         throw new IllegalArgumentException("Null entity");

      Class<?> ec = entity.getClass();
      return ProxyFactory.isProxyClass(ec); 
   }

   /**
    * Get entity manager provider.
    *
    * @return the entity manager provider
    */
   protected abstract EntityManagerProvider getProvider();

   /**
    * Get real entity from proxy.
    *
    * @param obj the proxy
    * @return real entity
    */
   @SuppressWarnings({"unchecked"})
   protected <T> T getEntity(T obj)
   {
      if (obj == null)
         throw new IllegalArgumentException("Null entity");

      Class<?> ec = obj.getClass();

      if (ProxyFactory.isProxyClass(ec) == false)
         return obj;

      if (obj instanceof ProxyEntity == false)
         throw new IllegalArgumentException("Object is not a ProxyEntity: " + obj);

      ProxyEntity<T> pe = (ProxyEntity<T>) obj;
      return pe.getRealEntity();
   }

   /**
    * Safe wrap real entity into proxy.
    * e.g. null is allowed as entity parameter
    *
    * @param entity the real entity
    * @return proxy entity or null ir entity is null
    */
   @SuppressWarnings({"unchecked"})
   protected <T> T safeWrap(final T entity)
   {
      return (entity != null) ? wrap(entity) : null;
   }

   /**
    * Wrap real entity into proxy.
    *
    * @param entity the real entity
    * @return proxy entity
    */
   @SuppressWarnings({"unchecked"})
   protected <T> T wrap(final T entity)
   {
      if (entity == null)
         throw new IllegalArgumentException("Null entity");

      Class<?> ec = entity.getClass();

      // do not wrap if disabled or existing proxies or disabled proxy entities or not an persistent entity
      if (ProxyingUtils.isDisabled() || ProxyFactory.isProxyClass(ec) || ec.isAnnotationPresent(DisableProxy.class) || ec.isAnnotationPresent(javax.persistence.Entity.class) == false)
         return entity;

      ProxyFactory factory = new ProxyFactory();
      factory.setFilter(FINALIZE_FILTER);
      factory.setSuperclass(ec); // expose our entity's class
      factory.setInterfaces(new Class[]{ProxyEntity.class}); // expose ProxyEntity
      Class<?> proxyClass = getProxyClass(factory);
      ProxyObject proxy;
      try
      {
         proxy = (ProxyObject) proxyClass.newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      proxy.setHandler(new MethodHandler()
      {
         public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Throwable
         {
            // Is this ProxyEntity::getRealEntity() invocation
            if ((args == null || args.length == 0) && "getRealEntity".equals(method.getName()))
            {
               return entity;
            }

            ManyToOne mto = method.getAnnotation(ManyToOne.class);
            if (mto != null)
            {
               return Relationships.handleManyToOne(entity, method, args, mto, getProvider());
            }

            OneToMany otm = method.getAnnotation(OneToMany.class);
            if (otm != null)
            {
               return Relationships.handleOneToMany(entity, method, otm, ProxyingHelper.this);
            }

            return method.invoke(entity, args);
         }
      });
      return (T) proxy;
   }

   protected static Class<?> getProxyClass(ProxyFactory factory)
   {
      SecurityManager sm = System.getSecurityManager();
      if (sm == null)
         return factory.createClass();
      else
         return AccessController.doPrivileged(new ClassCreator(factory));
   }

   /**
    * Privileged class creator.
    */
   protected static class ClassCreator implements PrivilegedAction<Class<?>>
   {
      private ProxyFactory factory;

      public ClassCreator(ProxyFactory factory)
      {
         this.factory = factory;
      }

      public Class<?> run()
      {
         return factory.createClass();
      }
   }

   private static final MethodFilter FINALIZE_FILTER = new MethodFilter()
   {
      public boolean isHandled(Method m)
      {
         // skip finalize methods
         return !("finalize".equals(m.getName()) && m.getParameterTypes().length == 0);
      }
   };
}
