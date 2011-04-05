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

package org.jboss.lhotse.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.jboss.lhotse.common.dto.Identity;
import org.jboss.lhotse.common.sql.SQLObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract SQLite open helper
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractSQLiteOpenHelper extends SQLiteOpenHelper
{
   private static final String TAG = "AbstractSQLiteOpenHelper";

   private static final Map<Class<?>, SQLiteEntityModel> models = new ConcurrentHashMap<Class<?>, SQLiteEntityModel>();
   private static ThreadLocal<SQLiteDatabase> dbTL = new ThreadLocal<SQLiteDatabase>();

   private volatile boolean initialized;

   public AbstractSQLiteOpenHelper(Context context, String name, int version)
   {
      super(context, name, null, version);
   }

   public void onOpen(SQLiteDatabase db)
   {
      if (initialized == false && db.isReadOnly() == false)
      {
         initialized = true;
         onCreate(db);
      }
   }

   public void onCreate(SQLiteDatabase db)
   {
      for (SQLiteEntityModel em : models.values())
      {
         String table = em.getTable();

         if (tableExists(db, em, true))
            continue;

         StringBuilder builder = new StringBuilder("CREATE TABLE ");
         builder.append(table).append(" (");
         String key = em.getKey();
         builder.append(key).append(" INTEGER PRIMARY KEY, ");

         String[] columns = em.getColumnsDescription();
         if (columns == null || columns.length == 0)
            throw new IllegalArgumentException("Illegale columns: " + em);

         builder.append(columns[0]);
         for (int i = 1; i < columns.length; i++)
         {
            builder.append(", ").append(columns[i]);
         }
         builder.append(")");

         String sql = builder.toString();
         Log.i(TAG, "SQL ==> " + sql);
         db.execSQL(sql);
      }
   }

   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
      for (SQLiteEntityModel em : models.values())
      {
         String sql = "DROP TABLE IF EXISTS " + em.getTable();
         Log.i(TAG, "SQL ==> " + sql);
         db.execSQL(sql);
      }
      onCreate(db);
   }

   protected boolean tableExists(SQLiteDatabase db, SQLiteEntityModel em, boolean alter)
   {
      String table = em.getTable();
      Cursor cursor = db.query("sqlite_master", new String[]{"sql"} , "tbl_name = ?", new String[]{table}, null, null, null);
      if (cursor == null)
         return false;

      try
      {
         int count = cursor.getCount();
         boolean exists = count > 0;
         if (exists && alter)
         {
            cursor.moveToFirst();
            String sql = cursor.getString(0);
            String[] columns = em.getKeyAndColumns();
            String[] desc = em.getColumnsDescription();
            for (int i = 0; i < columns.length; i++)
            {
               if (sql.contains(columns[i]) == false)
               {
                  db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + desc[i - 1]);
               }
            }
         }
         return exists;
      }
      finally
      {
         cursor.close();
      }
   }

   @SuppressWarnings({"unchecked"})
   public static <T> SQLiteEntityModel<T> getEntityModel(Class<T> entityClass)
   {
      SQLiteEntityModel<T> em = models.get(entityClass);
      if (em == null)
      {
         try
         {
            if (SQLObject.class.isAssignableFrom(entityClass))
               em = new SQLObjectSQLiteEntityModel(entityClass);
            else
               em = new ReflectionSQLiteEntityModel<T>(entityClass);

            models.put(entityClass, em);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      return em;
   }

   @SuppressWarnings({"unchecked"})
   public static <T> SQLiteEntityModel<T> putEntityModel(Class<T> clazz, SQLiteEntityModel<T> em)
   {
      if (em == null)
         return models.remove(clazz);
      else
         return models.put(clazz, em);
   }

   public static <T> T getSingleResult(List<T> results)
   {
      return (results == null || results.isEmpty()) ? null : results.get(0);
   }

   public static SQLiteDatabase getDB()
   {
      SQLiteDatabase db = dbTL.get();
      if (db == null)
         throw new IllegalArgumentException("Missing DB, forgot begin()?");
      return db;
   }

   public void begin()
   {
      SQLiteDatabase db = getWritableDatabase();
      dbTL.set(db);
      db.beginTransaction();
   }

   public void commit()
   {
      SQLiteDatabase db = getDB();
      db.setTransactionSuccessful();
   }

   public void rollback()
   {
   }

   public void end()
   {
      SQLiteDatabase db = getDB();
      dbTL.remove();
      db.endTransaction();
   }

   public boolean initialize(int currentVersion)
   {
      SQLiteDatabase db = getWritableDatabase();
      int version = db.getVersion();

      boolean isNew = currentVersion > version;
      if (isNew)
         onUpgrade(db, version, currentVersion);

      return isNew;
   }

   public <T> T load(Class<T> entityClass, long pk)
   {
      return load(getReadableDatabase(), entityClass, pk);
   }

   public static long persist(SQLObject entity)
   {
      if (entity == null)
         return -1;

      SQLiteDatabase db = getDB();
      return insertOrUpdate(db, entity);
   }

   public static int update(SQLObject entity)
   {
      if (entity == null)
         return 0;

      SQLiteDatabase db = getDB();
      return update(db, getEntityModel(entity.getClass()), entity, entity.getPk(), "pk");
   }

   public static int delete(SQLObject entity)
   {
      SQLiteDatabase db = getDB();
      return delete(db, entity, "pk = ?", entity.getPk());
   }

   public <T> List<T> select(Class<T> entityClass, String selection, String[] selectionArgs, String orderBy, String limit)
   {
      SQLiteDatabase db = getReadableDatabase();
      return select(db, entityClass, selection, selectionArgs, orderBy, limit);
   }

   public <T> List select(Class<T> entityClass, List<ColumnMapper> mappers, String selection, String[] selectionArgs, String orderBy, String limit)
   {
      SQLiteDatabase db = getReadableDatabase();
      return select(db, entityClass, mappers, selection, selectionArgs, orderBy, limit);
   }

   public <T> List<Long> pks(Class<T> entityClass, String selection, String[] selectionArgs, String orderBy, String limit)
   {
      SQLiteDatabase db = getReadableDatabase();
      return pks(db, entityClass, selection, selectionArgs, orderBy, limit);
   }

   public int count(Class<?> entityClass, String selection, String[] selectionArgs)
   {
      SQLiteDatabase db = getReadableDatabase();
      return count(db, entityClass, selection, selectionArgs);
   }

   // --- helper methods

   public static <T> T load(SQLiteDatabase db, Class<T> entityClass, Long pk)
   {
      SQLiteEntityModel<T> em = getEntityModel(entityClass);
      return getSingleResult(select(db, em, em.getKey() + " = ?", toSelectionArgs(pk), null, "1"));
   }

   public static <T extends Identity> T loadIdentity(SQLiteDatabase db, Class<T> entityClass, Long id)
   {
      SQLiteEntityModel<T> em = getEntityModel(entityClass);
      return getSingleResult(select(db, em, "id = ?", toSelectionArgs(id), null, "1"));
   }

   public static <T> List<T> select(SQLiteDatabase db, Class<T> entityClass, String selection, Object selectionArg)
   {
      return select(db, entityClass, selection, toSelectionArgs(selectionArg), null, null);
   }

   public static <T> List<T> select(SQLiteDatabase db, Class<T> entityClass, String selection, String[] selectionArgs, String orderBy, String limit)
   {
      SQLiteEntityModel<T> em = getEntityModel(entityClass);
      return select(db, em, selection, selectionArgs, orderBy, limit);
   }

   public static <T> List select(SQLiteDatabase db, Class<T> entityClass, List<ColumnMapper> mappers, String selection, String[] selectionArgs, String orderBy, String limit)
   {
      SQLiteEntityModel<T> em = getEntityModel(entityClass);
      return select(db, em.getTable(), mappers, selection, selectionArgs, orderBy, limit);
   }

   @SuppressWarnings({"unchecked"})
   public static <T> List<Long> pks(SQLiteDatabase db, Class<T> entityClass, String selection, String[] selectionArgs, String orderBy, String limit)
   {
      final SQLiteEntityModel<T> em = getEntityModel(entityClass);
      ColumnMapper idMapper = new ColumnMapper<Long>()
      {
         public String column()
         {
            return em.getKey();
         }

         public Long value(Cursor cursor, int i)
         {
            return cursor.getLong(i);
         }
      };
      return (List<Long>) select(db, em.getTable(), Collections.singletonList(idMapper), selection, selectionArgs, orderBy, limit);
   }

   @SuppressWarnings({"unchecked"})
   private static List select(SQLiteDatabase db, String table, List<ColumnMapper> mappers, String selection, String[] selectionArgs, String orderBy, String limit)
   {
      int n = mappers.size();
      if (n == 0)
         throw new IllegalArgumentException("Empty mappers!");

      String[] columns = new String[n];
      for (int i = 0; i < n; i++)
         columns[i] = mappers.get(i).column();

      Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, orderBy, limit);
      cursor = AbstractSQLiteEntityModel.toFirst(cursor);
      if (cursor == null)
         return Collections.emptyList();

      List result = new ArrayList();
      try
      {
         do
         {
            if (n > 1)
            {
               Object[] array = new Object[n];
               for (int index = 0; index < n; index++)
               {
                  array[index] = mappers.get(index).value(cursor, index);
               }
               result.add(array);
            }
            else
            {
               result.add(mappers.get(0).value(cursor, 0));
            }
         }
         while(cursor.moveToNext());
         
         return result;
      }
      finally
      {
         cursor.close();
      }
   }

   @SuppressWarnings({"unchecked"})
   public static <T> List<T> select(SQLiteDatabase db, SQLiteEntityModel<T> em, String selection, String[] selectionArgs, String orderBy, String limit)
   {
      EntityListener listener = em.onLoad();

      if (listener != null)
         listener.action(db, null, Phase.BEFORE);

      Cursor cursor = db.query(em.getTable(), em.getKeyAndColumns(), selection, selectionArgs, null, null, orderBy, limit);

      List<T> result = em.readCursor(cursor);
      if (listener != null && result.isEmpty() == false)
      {
         for (T entity : result)
            listener.action(db, entity, Phase.AFTER);
      }
      return result;
   }

   public static long insertOrUpdate(SQLiteDatabase db, SQLObject entity)
   {
      return insertOrUpdate(db, entity, entity.getPk(), "pk");
   }

   public static long insertOrUpdate(SQLiteDatabase db, Object entity, Long pk, String key)
   {
      SQLiteEntityModel em = getEntityModel(entity.getClass());

      if (pk == null)
      {
         pk = insert(db, em, entity);
      }
      else
      {
         update(db, em, entity, pk, key);
      }
      return pk != null ? pk : -1;
   }

   @SuppressWarnings({"unchecked"})
   public static Long insert(SQLiteDatabase db, SQLiteEntityModel em, Object entity)
   {
      EntityListener listener = em.onInsert();
      if (listener != null)
         listener.action(db, entity, Phase.BEFORE);

      Long pk = db.insert(em.getTable(), null, em.getContentValues(entity));
      em.setKeyValue(entity, pk);
      if (pk < 0)
         Log.v(TAG, "Error inserting new entity: " + entity);

      if (listener != null && pk != -1)
         listener.action(db, entity, Phase.AFTER);

      return pk;
   }

   @SuppressWarnings({"unchecked"})
   public static int update(SQLiteDatabase db, SQLiteEntityModel em, Object entity, Long pk, String key)
   {
      EntityListener listener = em.onUpdate();

      if (listener != null)
         listener.action(db, entity, Phase.BEFORE);

      int result = db.update(em.getTable(), em.getContentValues(entity), key + " = ?", toSelectionArgs(pk));

      if (result <= 0)
         Log.v(TAG, "No matching entity to update: " + em + ", pk=" + pk);

      if (listener != null && result > 0)
         listener.action(db, entity, Phase.AFTER);

      return result;
   }

   @SuppressWarnings({"unchecked"})
   public static int delete(SQLiteDatabase db, Object entity, String selection, Object selectionArg)
   {
      SQLiteEntityModel em = getEntityModel(entity.getClass());
      EntityListener listener = em.onDelete();

      if (listener != null)
         listener.action(db, entity, Phase.BEFORE);

      int result = db.delete(em.getTable(), selection, toSelectionArgs(selectionArg));

      if (listener != null && result > 0)
         listener.action(db, entity, Phase.AFTER);

      return result;
   }

   @SuppressWarnings({"unchecked"})
   public static int count(SQLiteDatabase db, Class<?> entityClass, String selection, String[] selectionArgs)
   {
      SQLiteEntityModel em = getEntityModel(entityClass);
      Cursor cursor = db.query(em.getTable(), new String[]{em.getKey()}, selection, selectionArgs, null, null, null);
      try
      {
         return (cursor != null) ? cursor.getCount() : 0;
      }
      finally
      {
         if (cursor != null)
            cursor.close();
      }
   }

   public static String toQuery(Iterable arg)
   {
      StringBuilder builder = new StringBuilder();
      //noinspection UnusedDeclaration
      for (Object i : arg)
      {
         if (builder.length() > 0)
            builder.append(",");
         builder.append("?");
      }
      return builder.toString();
   }

   public static String[] toSelectionArgs(Object... args)
   {
      if (args == null)
         return null;

      List<String> results = new ArrayList<String>();
      for (Object arg : args)
      {
         if (arg instanceof Iterable)
         {
            for (Object it : (Iterable) arg)
               results.add(toSelectionArg(it));
         }
         else
         {
            results.add(toSelectionArg(arg));
         }
      }

      return results.toArray(new String[results.size()]);
   }

   private static String toSelectionArg(Object arg)
   {
      if (Boolean.class.isInstance(arg))
      {
         arg = (Boolean.TRUE.equals(arg)) ? 1 : 0;
      }
      else if (Enum.class.isInstance(arg))
      {
         arg = ((Enum) arg).ordinal();
      }
      return (arg == null) ? null : String.valueOf(arg);
   }
}
