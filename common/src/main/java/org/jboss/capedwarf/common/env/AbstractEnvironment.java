/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.capedwarf.common.env;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jboss.capedwarf.common.serialization.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractEnvironment implements Environment
{
   public void touch()
   {
   }

   public void log(String category, Level level, String msg, Throwable t)
   {
      if (level == null)
      {
         verbose(category, msg, t);
         return;
      }

      String name = level.getName();
      if (Level.SEVERE.equals(level))
         name = "ERROR";
      else if (Level.CONFIG.equals(level))
         name = "DEBUG";

      List<Class<?>> types = new ArrayList<Class<?>>();
      types.add(String.class);
      types.add(String.class);
      if (t != null)
         types.add(Throwable.class);

      try
      {
         Method m = getMethod(name, types.toArray(new Class<?>[types.size()]));

         List<Object> args = new ArrayList<Object>();
         args.add(category);
         args.add(msg);
         if (t != null)
            args.add(t);

         m.invoke(null, args.toArray(new Object[args.size()]));
      }
      catch (Exception ignored)
      {
         verbose(category, msg, t);
      }
   }

   /**
    * Get matching log method to prefix.
    *
    * @param name the log level name
    * @param types the arg types
    * @return log method to invoke
    */
   protected abstract Method getMethod(String name, Class... types);

   /**
    * Verbose.
    *
    * @param category the category
    * @param msg the msg
    * @param t the throwable
    */
   protected abstract void verbose(String category, String msg, Throwable t);

   public EnvironmentType envType()
   {
      return EnvironmentType.ANDROID;
   }

   public JSONObject createObject()
   {
      return new HackJSONObject();
   }

   public JSONTokener createTokener(InputStream is) throws IOException
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int ch;
      while ((ch = is.read()) >= 0)
         baos.write(ch);

      String result = new String(baos.toByteArray(), "UTF-8");
      return new JSONTokener(result);
   }

   /**
    * Write the contents of the JSONArray as JSON text to a writer.
    * For compactness, no whitespace is added.
    * <p/>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @param array  the array
    * @param writer the writer
    * @throws org.json.JSONException for any error
    */
   public void writeArray(JSONArray array, Writer writer) throws JSONException
   {
      try
      {
         boolean b = false;
         int len = array.length();

         writer.write('[');

         for (int i = 0; i < len; i += 1)
         {
            if (b)
            {
               writer.write(',');
            }
            Object v = array.get(i);
            if (v instanceof JSONObject)
            {
               writeObject((JSONObject) v, writer);
            }
            else if (v instanceof JSONArray)
            {
               writeArray((JSONArray) v, writer);
            }
            else
            {
               writer.write(valueToString(v));
            }
            b = true;
         }
         writer.write(']');
      }
      catch (JSONException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw JSONUtils.createJSONException(e);
      }
   }

   /**
    * Write the contents of the JSONObject as JSON text to a writer.
    * For compactness, no whitespace is added.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @param object  the object
    * @param writer the writer
    * @throws org.json.JSONException for any error
    */
   @SuppressWarnings({"unchecked"})
   public void writeObject(JSONObject object, Writer writer) throws JSONException
   {
      try
      {
         boolean b = false;
         Iterator<String> keys = object.keys();
         writer.write('{');

         while (keys.hasNext())
         {
            if (b)
            {
               writer.write(',');
            }
            String key = keys.next();
            writer.write(JSONObject.quote(key));
            writer.write(':');
            Object v = object.get(key);
            if (v instanceof JSONObject)
            {
               writeObject((JSONObject) v, writer);
            }
            else if (v instanceof JSONArray)
            {
               writeArray((JSONArray) v,writer);
            }
            else
            {
               writer.write(valueToString(v));
            }
            b = true;
         }
         writer.write('}');
      }
      catch (JSONException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw JSONUtils.createJSONException(e);
      }
   }

   public long getUserId()
   {
      //return Application.getInstance().getPreferences(null).getUserId();
      return 0;
   }

   public String getUserToken()
   {
      //return Application.getInstance().getPreferences(null).getUserToken();
      return "";
   }

   /**
    * Make a JSON text of an Object value. If the object has an
    * value.toJSONString() method, then that method will be used to produce
    * the JSON text. The method is required to produce a strictly
    * conforming text. If the object does not contain a toJSONString
    * method (which is the most common case), then a text will be
    * produced by other means. If the value is an array or Collection,
    * then a JSONArray will be made from it and its toJSONString method
    * will be called. If the value is a MAP, then a JSONObject will be made
    * from it and its toJSONString method will be called. Otherwise, the
    * value's toString method will be called, and the result will be quoted.
    *
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    * @param value The value to be serialized.
    * @return a printable, displayable, transmittable
    *  representation of the object, beginning
    *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
    *  with <code>}</code>&nbsp;<small>(right brace)</small>.
    * @throws JSONException If the value is or contains an invalid number.
    */
   static String valueToString(Object value) throws JSONException
   {
      if (value == null)
      {
         return "null";
      }
      if (value instanceof Number)
      {
         return JSONObject.numberToString((Number) value);
      }
      if (value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray)
      {
         return value.toString();
      }
      if (value instanceof Map)
      {
         return new JSONObject((Map) value).toString();
      }
      if (value instanceof Collection)
      {
         return new JSONArray((Collection) value).toString();
      }
      if (value.getClass().isArray())
      {
         Collection<Object> collection = new ArrayList<Object>();
         for (int i = 0; i < Array.getLength(value); i++)
            collection.add(Array.get(value, i));

         return new JSONArray(collection).toString();
      }
      return JSONObject.quote(value.toString());
   }
}
