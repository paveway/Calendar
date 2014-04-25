package info.paveway.calendar.data;

import info.paveway.log.Logger;

import java.util.HashMap;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * イベントプロバイダークラス
 *
 * @version 1.0 新規作成
 *
 */
public class EventProvider extends AbstractBaseProvider {

    /** ロガー */
    private Logger mLogger = new Logger(EventProvider.class);

    /** テーブル名 */
    private static final String TABLE_NAME = "event";

    /** コンテントプロバイダ識別子 */
    private static final String AUTHORITY = EventProvider.class.getName().toLowerCase();

    /** コンテントURI */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    /** コンテントタイプ */
    public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + TABLE_NAME;

    /** コンテントアイテムタイプ */
    public static final String CONTENT_ITEM_TYPE = CONTENT_ITEM_TYPE_PREFIX + TABLE_NAME;

    /** IDカラム名 */
    public static final String ID = BaseColumns._ID;

    /** タイトルカラム名 */
    public static final String TITLE = "title";

    /** 開始日時 */
    public static final String GD_START_TIME = "gd_start_time";

    /** 終了日時 */
    public static final String GD_END_TIME = "gd_end_time";

    /** 終日フラグ */
    public static final String ALL_DAY = "all_day";

    /** 場所カラム名 */
    public static final String GD_WHERE = "gd_where";

    /** 内容 */
    public static final String CONTENT = "content";

    /** テーブル生成SQL文 */
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    ID            + " INTEGER PRIMARY KEY, "  +
                    TITLE         + " TEXT, "                 +
                    GD_START_TIME + " TEXT, "                 +
                    GD_END_TIME   + " TEXT, "                 +
                    ALL_DAY       + " INTEGER, "              +
                    GD_WHERE      + " TEXT, "                 +
                    CONTENT       + " TEXT"                   +
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
        mProjectionMap.put(ID,            ID);
        mProjectionMap.put(TITLE,         TITLE);
        mProjectionMap.put(GD_START_TIME, GD_START_TIME);
        mProjectionMap.put(GD_END_TIME,   GD_END_TIME);
        mProjectionMap.put(ALL_DAY,       ALL_DAY);
        mProjectionMap.put(GD_WHERE,      GD_WHERE);
        mProjectionMap.put(CONTENT,       CONTENT);

        mLogger.d("OUT(OK)");
    }
}
