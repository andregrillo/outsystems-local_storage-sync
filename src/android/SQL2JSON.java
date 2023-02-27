package com.outsystems.sql2json;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

/**
 * This class echoes a string called from JavaScript.
 */
public class SQL2JSON extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("select")) {
            String dbName = args.getString(0);
            String table = args.getString(1);
            this.select(dbName, table, callbackContext);
            return true;
        }
        return false;
    }

    @SuppressLint("Range")
    private void select(String dbName, String table, CallbackContext callbackContext) {
        if (table != null && table.length() > 0 && dbName != null && dbName.length() > 0) {

            Context context = this.cordova.getActivity().getApplicationContext();

            File dbPath = context.getDatabasePath(dbName);

            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath.getPath(), null, 0);

            Cursor allRows = db.rawQuery("SELECT Physical_Table_name FROM OSSYS_ENTITY WHERE Name = " + "\"" + table +"\"", null);
            allRows.moveToFirst();
            String physicalTableName = allRows.getString(0);

            Cursor cursor = db.rawQuery("SELECT * FROM " + "\"" + physicalTableName +"\"", null);

            JSONArray resultSet     = new JSONArray();

            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for( int i=0 ;  i< totalColumn ; i++ )
                {
                    if( cursor.getColumnName(i) != null )
                    {
                        try
                        {
                            if( cursor.getString(i) != null )
                            {
                                Log.d("TAG_NAME", cursor.getString(i) );
                                rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                            }
                            else
                            {
                                rowObject.put( cursor.getColumnName(i) ,  "" );
                            }
                        }
                        catch( Exception e )
                        {
                            Log.d("TAG_NAME", e.getMessage()  );
                        }
                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();
            }
            cursor.close();
            Log.d("TAG_NAME", resultSet.toString() );

            callbackContext.success(new Gson().toJson(resultSet));
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
