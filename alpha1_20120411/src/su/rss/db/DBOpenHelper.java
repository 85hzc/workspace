package su.rss.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "SU_RSS";
	public static final int VERSION = 3;

	public static final String FEED_TABLE = "RSS_FEED";
	public static final String ITEM_TABLE = "RSS_ITEM";
	public static final String PIC_TABLE = "PIC_DATA";
	public static final String COLLECT_TABLE = "COLLECT_DATA";
	public static final String PRIMARY_KEY = "_id";
	public static final String USER_ID = "user_id";
	public static final String ID = "ID";
	public static final String FEED_TITLE = "title";
	public static final String FEED_LINK = "link";
	public static final String FEED_PUBDATE = "pubdate";
	public static final String FEED_IMG_PATH = "image_path";
	public static final String FEED_PROPERTY = "property";
	public static final String FEED_POSITION = "position";
	public static final String ITEM_TITLE = "title";
	public static final String ITEM_DESCRIPTION = "description";
	public static final String ITEM_LINK = "link";
	public static final String ITEM_PUBDATE = "pubdate";
	public static final String ITEM_READ_FLAG = "read_flag";
	public static final String ITEM_IMG_FLAG = "image_flag";
	public static final String ITEM_FEED_ID = "feed_id";
	public static final String ITEM_IMG_PATH = "img_path";
	public static final String PIC_PATH = "path";
	public static final String PIC_RELATE_ID = "relate_id";
	public static final String PIC_WIDTH = "width";
	public static final String PIC_HEIGHT = "height";
	public static final String PIC_LINK = "link_path";
	public static final String COLLECT_ITEM_ID = "item_id";
	public static final String READ_HISTORY_TABLE = "history_table";
	public static final String READ_HISTORY_ITEM_ID = "item_id";
	public static final String READ_HISTORY_DATE = "date";

	private static final String TABLE_CREATE = "CREATE TABLE %s ("
			+ PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,";
	private static final String FEED_TABLE_CRATE = String.format(TABLE_CREATE,
			FEED_TABLE)
			+ ID
			+ " NTEXT,"
			+ FEED_TITLE
			+ " NTEXT,"
			+ FEED_LINK
			+ " NTEXT,"
			+ FEED_PUBDATE
			+ " NTEXT,"
			+ FEED_IMG_PATH
			+ " NTEXT,"
			+ FEED_PROPERTY
			+ " INTEGER,"
			+ FEED_POSITION
			+ " INTEGER,"
			+ USER_ID + " NTEXT)";
	private static final String ITEM_TABLE_CRATE = String.format(TABLE_CREATE,
			ITEM_TABLE)
			+ ID
			+ " NTEXT,"
			+ ITEM_TITLE
			+ " NTEXT,"
			+ ITEM_DESCRIPTION
			+ " NTEXT,"
			+ FEED_PUBDATE
			+ " NTEXT,"
			+ ITEM_LINK
			+ " NTEXT,"
			+ ITEM_READ_FLAG
			+ " INTEGER,"
			+ ITEM_IMG_PATH
			+ " NTEXT,"
			+ ITEM_IMG_FLAG + " INTEGER," + ITEM_FEED_ID + " NTEXT)";
	private static final String COLLECT_TABLE_CRATE = String.format(
			TABLE_CREATE, COLLECT_TABLE)
			+ COLLECT_ITEM_ID
			+ " NTEXT,"
			+ USER_ID + " NTEXT)";
	private static final String PIC_TABLE_CREATE = String.format(TABLE_CREATE,
			PIC_TABLE)
			+ ID
			+ " NTEXT,"
			+ PIC_PATH
			+ " NTEXT,"
			+ PIC_LINK
			+ " NTEXT,"
			+ PIC_RELATE_ID
			+ " NTEXT,"
			+ PIC_WIDTH
			+ " INTEGER,"
			+ PIC_HEIGHT + " INTEGER)";
	private static final String HISTORY_TABLE_CRETE = String.format(
			TABLE_CREATE, READ_HISTORY_TABLE)
			+ READ_HISTORY_ITEM_ID
			+ " NTEXT,"
			+ READ_HISTORY_DATE + " NTEXT," + USER_ID + " NTEXT)";
	
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS %s";

	public DBOpenHelper(Context context) {
		this(context, DB_NAME, null, VERSION);
	}

	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(String.format(DROP_TABLE, FEED_TABLE));
		db.execSQL(String.format(DROP_TABLE, ITEM_TABLE));
		db.execSQL(String.format(DROP_TABLE, COLLECT_TABLE));
		db.execSQL(String.format(DROP_TABLE, PIC_TABLE));
		db.execSQL(String.format(DROP_TABLE, READ_HISTORY_TABLE));
		onCreate(db);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(FEED_TABLE_CRATE);
		db.execSQL(ITEM_TABLE_CRATE);
		db.execSQL(COLLECT_TABLE_CRATE);
		db.execSQL(PIC_TABLE_CREATE);
		db.execSQL(HISTORY_TABLE_CRETE);
	}

}
