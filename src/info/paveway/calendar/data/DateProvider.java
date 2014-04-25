package info.paveway.calendar.data;

import info.paveway.log.Logger;

import java.util.HashMap;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 日付プロバイダークラス
 *
 * @version 1.0 新規作成
 *
 */
public class DateProvider extends AbstractBaseProvider {

    /** ロガー */
    private Logger mLogger = new Logger(DateProvider.class);

    /** テーブル名 */
    private static final String TABLE_NAME = "date";

    /** コンテントプロバイダ識別子 */
    private static final String AUTHORITY = DateProvider.class.getName().toLowerCase();

    /** コンテントURI */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    /** コンテントタイプ */
    private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.paveway." + TABLE_NAME;

    /** コンテントアイテムタイプ */
    private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.paveway." + TABLE_NAME;

    /** IDカラム名 */
    public static final String ID = BaseColumns._ID;

    /** 年カラム名 */
    public static final String YEAR = "year";

    /** 月カラム名 */
    public static final String MONTH = "month";

    /** 日カラム名 */
    public static final String DAY = "day";

    /** 六曜カラム名 */
    public static final String ROKUYOU = "rokuyou";

    /** 祝日カラム名 */
    public static final String HOLIDAY = "holiday";


    /** テーブル生成SQL文 */
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    ID      + " INTEGER PRIMARY KEY, " +
                    YEAR    + " INTEGER, "             +
                    MONTH   + " INTEGER, "             +
                    DAY     + " TEXT, "                +
                    ROKUYOU + " TEXT, "                +
                    HOLIDAY + " TEXT"                  +
             ");";

    /**
     * 初期化処理を行う。
     */
    protected void init() {
        mLogger.d("IN");

        mTableName       = TABLE_NAME;
        mContentUri      = CONTENT_URI;
        mContentType     = CONTENT_TYPE;
        mContentItemType = CONTENT_ITEM_TYPE;
        mCreateTableSQL  = CREATE_TABLE_SQL;

        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME, ALL_ROWS);
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/#", ROW);

        // プロジェクションマップを初期化する。
        mProjectionMap = new HashMap<String, String>();
        mProjectionMap.put(ID,      ID);
        mProjectionMap.put(YEAR,    YEAR);
        mProjectionMap.put(MONTH,   MONTH);
        mProjectionMap.put(DAY,     DAY);
        mProjectionMap.put(ROKUYOU, ROKUYOU);
        mProjectionMap.put(HOLIDAY, HOLIDAY);

        mLogger.d("OUT(OK)");
    }
}
