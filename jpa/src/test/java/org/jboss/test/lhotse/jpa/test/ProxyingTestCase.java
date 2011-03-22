package org.jboss.test.lhotse.jpa.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.Set;

import org.jboss.lhotse.jpa.ProxyingEntityManagerFactory;
import org.jboss.lhotse.jpa.ProxyingFactory;
import org.jboss.lhotse.jpa.ProxyingUtils;
import org.jboss.test.lhotse.jpa.support.Food;
import org.jboss.test.lhotse.jpa.support.Ingredient;
import org.jboss.test.lhotse.jpa.support.MockEMF;
import org.jboss.test.lhotse.jpa.support.Person;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test simple proxying.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ProxyingTestCase
{
   @Test
   public void testGet() throws Exception
   {
      EntityManagerFactory emf = new ProxyingEntityManagerFactory(new MockEMF());
      EntityManager em = emf.createEntityManager();
      Person person = new Person();
      person.setId(1l);
      person.setName("Ales");
      em.persist(person);
      Food food = new Food();
      food.setPersonId(1l);
      food.setDescription("Pizza");
      food = em.merge(food);
      Person lazy = food.getPerson();
      Assert.assertSame(person, lazy);
   }

   @Test
   public void testSet() throws Exception
   {
      EntityManagerFactory emf = new ProxyingEntityManagerFactory(new MockEMF());
      EntityManager em = emf.createEntityManager();
      Person person = new Person();
      person.setId(1l);
      Food food = new Food();
      food.setDescription("Pizza");
      food = em.merge(food);
      food.setPerson(person);
      Assert.assertSame(1l, food.getPersonId());
   }

   @Test
   public void testDisabled() throws Exception
   {
      EntityManagerFactory emf = new ProxyingEntityManagerFactory(new MockEMF());
      EntityManager em = emf.createEntityManager();
      Person person = new Person();
      Assert.assertSame(person, em.merge(person));
   }

   @Test
   public void testCollections() throws Exception
   {
      EntityManagerFactory emf = new ProxyingEntityManagerFactory(new MockEMF());
      EntityManager em = emf.createEntityManager();
      Food food = new Food();
      food.setId(1l);
      food.setDescription("Pizza");
      food = em.merge(food);
      Ingredient i1 = new Ingredient();
      i1.setId(1l);
      i1.setFoodId(1l);
      i1.setDescription("Cheese");
      em.persist(i1);
      Ingredient i2 = new Ingredient();
      i2.setId(2l);
      i2.setFoodId(1l);
      i2.setDescription("Tomato");
      em.persist(i2);
      Ingredient i3 = new Ingredient();
      i3.setId(3l);
      i3.setFoodId(1l);
      i3.setDescription("Mushrooms");
      em.persist(i3);
      Set<Ingredient> ingredients = food.getIngredients();
      Assert.assertEquals(3, ingredients.size());
   }

   @Test
   public void testProxyingFactory() throws Exception
   {
      ProxyingFactory factory = new ProxyingEntityManagerFactory(new MockEMF());
      Person person = new Person();
      person.setId(1l);
      Food food = factory.createProxy(Food.class);
      food.setDescription("Pizza");
      food.setPerson(person);
      Assert.assertSame(1l, food.getPersonId());
   }

   @Test
   public void testProxyingUtils() throws Exception
   {
      ProxyingFactory factory = new ProxyingEntityManagerFactory(new MockEMF());
      ProxyingUtils.disable();
      try
      {
         Food food = factory.createProxy(Food.class);
         Assert.assertFalse(factory.isProxy(food));
      }
      finally
      {
         ProxyingUtils.enable();
      }
   }
}
