package org.jboss.test.lhotse.jpa.support;

import org.jboss.lhotse.jpa.DisableProxy;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@DisableProxy
public class Ingredient extends MockEntity
{
   private Long foodId;
   private String description;

   public Long getFoodId()
   {
      return foodId;
   }

   public void setFoodId(Long foodId)
   {
      this.foodId = foodId;
   }

   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }
}
