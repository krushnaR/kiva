package com.sapience.kiva.kivafinal;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DbHelper extends SQLiteOpenHelper {

	public static final String DATABSE_NAME = "codes";
	public static final int DATABASE_VERSION = 1;
	private Context context;
	private int i;

	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + DbFeed.TABLE_NAME + " (" + DbFeed.COLUMN_ID
			+ " INTEGER PRIMARY KEY," + DbFeed.COLUMN_QR_CONTENT + " CHAR[20] NOT NULL," + DbFeed.COLUMN_IS_CORNER
			+ " INT NOT NULL," + DbFeed.COLUMN_FORWARD_TURN + " CHAR[20] NOT NULL," + DbFeed.COLUMN_REVERSE_TURN
			+ " CHAR[20] NOT NULL)";

	public DbHelper(Context context) {
		super(context, DATABSE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
		insert(db);
	}

	private void insert(SQLiteDatabase db) {

		// Default values insertion
		ContentValues values = new ContentValues();

		// row 0 insert
		values.put(DbFeed.COLUMN_QR_CONTENT, DbFeed.ROW0_CONTENT);
		values.put(DbFeed.COLUMN_IS_CORNER, DbFeed.ROW0_IS_CORNER);
		values.put(DbFeed.COLUMN_FORWARD_TURN, DbFeed.ROW0_FORWARD_TURN);
		values.put(DbFeed.COLUMN_REVERSE_TURN, DbFeed.ROW0_REVERSE_TURN);

		db.insert(DbFeed.TABLE_NAME, null, values);

		values.clear();

		// row 1 insert
		values.put(DbFeed.COLUMN_QR_CONTENT, DbFeed.ROW1_CONTENT);
		values.put(DbFeed.COLUMN_IS_CORNER, DbFeed.ROW1_IS_CORNER);
		values.put(DbFeed.COLUMN_FORWARD_TURN, DbFeed.ROW1_FORWARD_TURN);
		values.put(DbFeed.COLUMN_REVERSE_TURN, DbFeed.ROW1_REVERSE_TURN);

		db.insert(DbFeed.TABLE_NAME, null, values);

		values.clear();

		// row 2 insert
		values.put(DbFeed.COLUMN_QR_CONTENT, DbFeed.ROW2_CONTENT);
		values.put(DbFeed.COLUMN_IS_CORNER, DbFeed.ROW2_IS_CORNER);
		values.put(DbFeed.COLUMN_FORWARD_TURN, DbFeed.ROW2_FORWARD_TURN);
		values.put(DbFeed.COLUMN_REVERSE_TURN, DbFeed.ROW2_REVERSE_TURN);

		db.insert(DbFeed.TABLE_NAME, null, values);

		values.clear();

		// row 3 insert
		values.put(DbFeed.COLUMN_QR_CONTENT, DbFeed.ROW3_CONTENT);
		values.put(DbFeed.COLUMN_IS_CORNER, DbFeed.ROW3_IS_CORNER);
		values.put(DbFeed.COLUMN_FORWARD_TURN, DbFeed.ROW3_FORWARD_TURN);
		values.put(DbFeed.COLUMN_REVERSE_TURN, DbFeed.ROW3_REVERSE_TURN);

		db.insert(DbFeed.TABLE_NAME, null, values);

		values.clear();

		// row 4 insert
		values.put(DbFeed.COLUMN_QR_CONTENT, DbFeed.ROW4_CONTENT);
		values.put(DbFeed.COLUMN_IS_CORNER, DbFeed.ROW4_IS_CORNER);
		values.put(DbFeed.COLUMN_FORWARD_TURN, DbFeed.ROW4_FORWARD_TURN);
		values.put(DbFeed.COLUMN_REVERSE_TURN, DbFeed.ROW4_REVERSE_TURN);

		db.insert(DbFeed.TABLE_NAME, null, values);

		values.clear();

		// row 5 insert
		values.put(DbFeed.COLUMN_QR_CONTENT, DbFeed.ROW5_CONTENT);
		values.put(DbFeed.COLUMN_IS_CORNER, DbFeed.ROW5_IS_CORNER);
		values.put(DbFeed.COLUMN_FORWARD_TURN, DbFeed.ROW5_FORWARD_TURN);
		values.put(DbFeed.COLUMN_REVERSE_TURN, DbFeed.ROW5_REVERSE_TURN);

		db.insert(DbFeed.TABLE_NAME, null, values);

		values.clear();

		// row 6 insert
		values.put(DbFeed.COLUMN_QR_CONTENT, DbFeed.ROW6_CONTENT);
		values.put(DbFeed.COLUMN_IS_CORNER, DbFeed.ROW6_IS_CORNER);
		values.put(DbFeed.COLUMN_FORWARD_TURN, DbFeed.ROW6_FORWARD_TURN);
		values.put(DbFeed.COLUMN_REVERSE_TURN, DbFeed.ROW6_REVERSE_TURN);

		db.insert(DbFeed.TABLE_NAME, null, values);

		values.clear();

		// row 7 insert
		values.put(DbFeed.COLUMN_QR_CONTENT, DbFeed.ROW7_CONTENT);
		values.put(DbFeed.COLUMN_IS_CORNER, DbFeed.ROW7_IS_CORNER);
		values.put(DbFeed.COLUMN_FORWARD_TURN, DbFeed.ROW7_FORWARD_TURN);
		values.put(DbFeed.COLUMN_REVERSE_TURN, DbFeed.ROW7_REVERSE_TURN);

		db.insert(DbFeed.TABLE_NAME, null, values);

		values.clear();

		// row 8 insert
		values.put(DbFeed.COLUMN_QR_CONTENT, DbFeed.ROW8_CONTENT);
		values.put(DbFeed.COLUMN_IS_CORNER, DbFeed.ROW8_IS_CORNER);
		values.put(DbFeed.COLUMN_FORWARD_TURN, DbFeed.ROW8_FORWARD_TURN);
		values.put(DbFeed.COLUMN_REVERSE_TURN, DbFeed.ROW8_REVERSE_TURN);

		db.insert(DbFeed.TABLE_NAME, null, values);

		values.clear();

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		onCreate(db);
	}

	public Bundle getContent(String content) {
		Bundle bundle = new Bundle();
		

		SQLiteDatabase db = getReadableDatabase();
		
		String[] columns = { DbFeed.COLUMN_ID, DbFeed.COLUMN_QR_CONTENT, DbFeed.COLUMN_IS_CORNER,
				DbFeed.COLUMN_FORWARD_TURN, DbFeed.COLUMN_REVERSE_TURN };

		Cursor c = db.rawQuery("SELECT * FROM " + DbFeed.TABLE_NAME + " WHERE content='" + content + "'", null);

		ArrayList<String> values = new ArrayList<String>();

		if (c != null) {
			while (c.moveToNext()) {
				String value0 = c.getString(0);
				String value1 = c.getString(1);
				String value2 = c.getString(2);
				String value3 = c.getString(3);
				String value4 = c.getString(4);
				
				boolean value2B = false;
				if (value2.equals("1")) {
					value2B = true;
				}

				int value0I = Integer.parseInt(value0);

				bundle.putInt(DbFeed.COLUMN_ID, value0I);
				bundle.putString(DbFeed.COLUMN_QR_CONTENT, value1);
				bundle.putBoolean(DbFeed.COLUMN_IS_CORNER, value2B);
				bundle.putString(DbFeed.COLUMN_FORWARD_TURN, value3);
				bundle.putString(DbFeed.COLUMN_REVERSE_TURN, value4);
			
			}
		}
		db.close();
		return bundle;
	}

}
