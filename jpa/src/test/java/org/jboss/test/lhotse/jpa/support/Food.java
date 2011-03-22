package org.jboss.test.lhotse.jpa.support;

import javax.persistence.Entity;

import java.util.Set;

import org.jboss.lhotse.jpa.ManyToOne;
import org.jboss.lhotse.jpa.OneToMany;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Entity
public class Food extends MockEntity
{
   private Long personId;
   private Person person;
   private String description;

   public Long getPersonId()
   {
      return personId;
   }

   public void setPersonId(Long personId)
   {
      this.personId = personId;
   }

   @ManyToOne
   public Person getPerson()
   {
      return person;
   }

   @ManyToOne
   public void setPerson(Person person)
   {
      this.person = person;
   }

   @OneToMany
   public Set<Ingredient> getIngredients()
   {
      return null;
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
