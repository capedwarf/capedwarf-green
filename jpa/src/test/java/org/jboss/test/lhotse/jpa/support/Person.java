package org.jboss.test.lhotse.jpa.support;

import org.jboss.lhotse.jpa.DisableProxy;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@DisableProxy
public class Person extends MockEntity
{
   private String name;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
}
