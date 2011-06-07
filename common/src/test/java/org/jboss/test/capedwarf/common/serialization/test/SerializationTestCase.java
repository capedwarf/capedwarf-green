package org.jboss.test.capedwarf.common.serialization.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.jboss.capedwarf.common.serialization.*;
import org.jboss.test.capedwarf.common.serialization.support.TestData;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test serialization.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings({"unchecked"})
public class SerializationTestCase
{
   @SuppressWarnings({"deprecation"})
   private void testSerialization(Serializator serializator) throws Exception
   {
      TestData ping = new TestData();
      ping.setId(123l);
      ping.setTopic("RadarApp");
      ping.setX(1.0);
      ping.setExtra("");
      testSerialization(serializator, ping, TestData.class);

      /*
      EventInfo ei = new EventInfo();
      ei.setId(0l);
      ei.setTitle("");
      ei.setTopicId(1l);
      byte[] photo = "foobar".getBytes();
      ei.setPhoto(photo);
      testSerialization(serializator, ei, EventInfo.class);

      CommentInfo ci = new CommentInfo();
      ci.setComment(":;\":;!@#$%^&**()_+-{}=\"!@#$%^&*())_::\"{}\"{}\"");
      ci.setId(0l);
      testSerialization(serializator, ci, CommentInfo.class);
      */
   }

   private <T> void testSerialization(Serializator serializator, T instance, Class<T> clazz) throws Exception
   {
      byte[] bytes = serializator.serialize(instance);
      InputStream is = new ByteArrayInputStream(bytes);
      T copy = serializator.deserialize(is, clazz);
      Assert.assertNotNull(copy);
      Assert.assertEquals(instance, copy);
   }

   @Test
   public void testDefault() throws Exception
   {
      testSerialization(DefaultSerializator.INSTANCE);
   }

   @Test
   public void testJSON() throws Exception
   {
      testSerialization(JSONSerializator.INSTANCE);
   }

   @Test
   public void testGzip() throws Exception
   {
      testSerialization(JSONSerializator.GZIPPED);
      testSerialization(DefaultSerializator.GZIPPED);
   }

   @Test
   public void testOptional() throws Exception
   {
      GzipOptionalSerializator.disableGzip();
      try
      {
         testSerialization(JSONSerializator.OPTIONAL_GZIP);
      }
      finally
      {
         GzipOptionalSerializator.enableGzip();
      }
   }

   @Test
   public void testJSONCollection() throws Exception
   {
      TestData ping = new TestData();
      ping.setId(123l);
      ping.setTopic("RadarApp");
      ping.setX(1.0);

      Serializator serializator = new JSONCollectionSerializator(TestData.class);
      Collection<TestData> collection = new ArrayList<TestData>();
      collection.add(ping);

      byte[] bytes = serializator.serialize(collection);
      InputStream is = new ByteArrayInputStream(bytes);
      Collection copy = serializator.deserialize(is, ArrayList.class);
      Assert.assertNotNull(copy);
      Assert.assertEquals(collection, copy);
   }

   @Test
   public void testMultiJSONCollection() throws Exception
   {
      TestData ping = new TestData();
      ping.setId(123l);
      ping.setTopic("RadarApp");
      ping.setX(1.0);

      /*
      EventInfo ei = new EventInfo();
      ei.setId(0l);
      ei.setTitle("");
      ei.setTopicId(1l);
      byte[] photo = "foobar".getBytes();
      ei.setPhoto(photo);
      */

      final Class[] array = new Class[]{TestData.class}; //, EventInfo.class};
      JSONAwareInstanceProvider jaip = new ReflectionJSONAwareInstanceProvider(new ArrayElementTypeProvider(array));

      Serializator serializator = new MultiJSONCollectionSerializator(jaip);
      Collection collection = new ArrayList();
      collection.add(ping);
      // collection.add(ei);

      byte[] bytes = serializator.serialize(collection);
      InputStream is = new ByteArrayInputStream(bytes);
      Collection copy = serializator.deserialize(is, ArrayList.class);
      Assert.assertNotNull(copy);
      Assert.assertEquals(collection, copy);
   }
}
