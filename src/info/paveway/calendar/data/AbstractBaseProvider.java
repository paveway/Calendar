package info.paveway.calendar.data;

import info.paveway.log.Logger;

import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * コンテントプロバイダー抽象基底クラス
 *
 * @version 1.0 新規作成
 *
 */
public abstract class AbstractBaseProvider extends ContentProvider {

    /** ロガー */
    private Logger mLogger = new Logger(AbstractBaseProvider.class);

    /** コンテントリゾルバー */
    private ContentResolver mResolver;

    /** データヘルパー */
    private DataHelper mDataHelper;

    protected static final String CONTENT_TYPE_PREFIX = "vnd.android.cursor.dir/vnd.neweral.";
    protected static final String CONTENT_ITEM_TYPE_PREFIX = "vnd.android.cursor.item/vnd.newral.";

    /** 全件選択 */
    protected static final int ALL_ROWS = 1;

    /** 個別選択 */
    protected static final int ROW = 2;

    /** URIマッチャー */
    protected UriMatcher mUriMatcher;

    /** プロジェクションマップ */
    protected Map<String, String> mProjectionMap;

    /** テーブル名 */
    protected String mTableName;

    /** コンテントURI */
    protected Uri mContentUri;

    /** コンテントタイプ */
    protected String mContentType;

    /** コンテントアイテムタイプ */
    protected String mContentItemType;

    /** テーブル削除SQL文 */
    private String mDropTableSQL;

    /** テーブル生成SQL文 */
    protected String mCreateTableSQL;

    /**
     * 生成した時に呼び出される。
     *
     * @return 処理結果
     */
    @Override
    public boolean onCreate() {
        mLogger.d("IN");

        // 初期化処理を行う。
        init();
        mDropTableSQL = "DROP TABLE IF EXISTS " + mTableName;

        // コンテントリゾルバーを設定する。
        mResolver = getContext().getContentResolver();

        // データヘルパーを生成する。
        mDataHelper = new DataHelper(getContext());

        mLogger.d("OUT(OK) result=[true]");
        // 正常終了する。
        return true;
    }

    /**
     * 初期化処理を行う。
     */
    abstract protected void init();

    /**
     * クエリーを行う。
     *
     * @param uri クエリーを行うテーブルのURI
     * @param projection セレクトする項目名の配列
     * @param selection 選択条件
     * @param selectionArgs 選択条件の値の配列
     * @param sortOrder ソート順
     * @return 検索結果のカーソル
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mLogger.d("IN uri=[" + uri + "] selection=[" + selection + "] sortOrder=[" + sortOrder + "]");

        // クエリービルダーを生成する。
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // テーブルを設定する。
        qb.setTables(mTableName);

        // URIにより処理を判別する。
        switch (mUriMatcher.match(uri)) {
        // 全行選択の場合
        case ALL_ROWS:
            mLogger.d("ALL_ROWS");
            // プロジェクションマップを設定する。
            qb.setProjectionMap(mProjectionMap);
            break;

        // 個別行選択の場合
        case ROW:
            mLogger.d("ROW");
            // プロジェクションマップを設定する。
            qb.setProjectionMap(mProjectionMap);

            // 検索条件を設定する。
            qb.appendWhere(BaseColumns._ID + "=" + getId(uri));
            break;

        // 上記以外
        default:
            String msg = "Unknown URI uri=[" + uri + "]";
            mLogger.e(msg);
            // エラーで終了する。
            throw new IllegalArgumentException(msg);
        }

        // 読み出し用データベースを取得する。
        SQLiteDatabase db = mDataHelper.getReadableDatabase();

        // クエリーを実行する。
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // クエリーの実行を通知する。
        c.setNotificationUri(mResolver, uri);

        // クエリー結果のカーソルを返却する。
        mLogger.d("OUT(OK)");
        return c;
    }

    /**
     * データを新規登録する。
     *
     * @param uri 新規登録するテーブルのURI
     * @param values 新規登録するデータ
     * @return 新規登録したデータのURI
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mLogger.d("IN uri=[" + uri + "]");

        // 全行選択ではない場合
        if (mUriMatcher.match(uri) != ALL_ROWS) {
            // エラーで終了する。
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // 書き込み用データベースを取得する。
        SQLiteDatabase db = mDataHelper.getWritableDatabase();

        // データを新規登録する。
        long rowId = db.insert(mTableName, null, values);
        mLogger.d("rowId=[" + rowId + "]");

        // データを新規登録できた場合
        if (rowId > 0) {
            // 新規登録したデータのURIを取得する。
            Uri returnUri = ContentUris.withAppendedId(mContentUri, rowId);

            // データの新規登録を通知する。
            mResolver.notifyChange(returnUri, null);
            mLogger.d("OUT(OK) result=[" + returnUri + "]");
            return returnUri;
        }

        // データを新規登録できなかった場合、エラーで終了する。
        throw new SQLException("Failed to insert row into " + uri);
    }

    /**
     * データを更新する。
     *
     * @param uri 更新するテーブルのURI
     * @param values 更新するデータ
     * @param selection 選択条件
     * @param selectionArgs 選択条件の値の配列
     * @return 更新したデータ数
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // 書き込み用データベースを取得する。
        SQLiteDatabase db = mDataHelper.getWritableDatabase();

        // 更新したデータ数
        int count;

        // URIにより処理を判別する。
        switch (mUriMatcher.match(uri)) {
        // 全行選択の場合
        case ALL_ROWS:
            count = db.update(mTableName, values, selection, selectionArgs);
            break;

        // 個別行選択の場合
        case ROW:
            count = db.update(mTableName, values, createSelection(uri, selection), selectionArgs);
            break;

        // 上記以外
        default:
            // エラーで終了する。
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // データの更新を通知する。
        mResolver.notifyChange(uri, null);

        // 更新したデータ数を返却する。
        return count;
    }

    /**
     * データを削除する。
     *
     * @param uri 削除するテーブルのURI
     * @param selection 選択条件
     * @param selectionArgs 選択条件の値の配列
     * @return 削除したデータ数
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // 書き込み用データベースを取得する。
        SQLiteDatabase db = mDataHelper.getWritableDatabase();
        int count;

        // URIにより処理を判別する。
        switch (mUriMatcher.match(uri)) {
        // 全行選択の場合
        case ALL_ROWS:
            count = db.delete(mTableName, selection, selectionArgs);
            break;

        // 個別行選択の場合
        case ROW:
            count = db.delete(mTableName, createSelection(uri, selection), selectionArgs);
            break;

        // 上記以外
        default:
            // エラーで終了する。
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        mResolver.notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // URIにより処理を判別する。
        switch (mUriMatcher.match(uri)) {
        // 全行選択の場合
        case ALL_ROWS:
            return mContentType;

        // 個別行選択の場合
        case ROW:
            return mContentItemType;

        // 上記以外
        default:
            // エラーで終了する。
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /**
     * IDを返却する。
     *
     * @param uri URI
     * @return ID
     */
    private String getId(Uri uri) {
        mLogger.d("IN uri=[" + uri + "]");

        String id = uri.getPathSegments().get(1);

        mLogger.d("OUT(OK) result=[" + id + "]");
        return id;
    }

    /**
     * 選択条件のSQL文字列を生成する。
     *
     * @param uri URI
     * @param 選択条件
     * @return 選択条件のSQL文字列
     */
    private String createSelection(Uri uri, String selection) {
        mLogger.d("IN uri=[" + uri + "] selection=[" + selection + "]");

        String id = getId(uri);
        String result = BaseColumns._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");

        mLogger.d("OUT(OK) result=[" + result + "]");
        return result;
    }

    /**
     * データヘルパークラス
     *
     */
    private class DataHelper extends SQLiteOpenHelper {

        /** ロガー */
        private Logger mLogger = new Logger(DataHelper.class);

        /** データベースバージョン */
        private static final int DB_VERSION = 1;

        /**
         * コンストラクタ
         * スーパークラスのコンストラクタを呼び出す。
         */
        public DataHelper(Context context) {
            super(context, null, null, DB_VERSION);
            mLogger.d("IN");
            mLogger.d("OUT(OK)");
        }

        /**
         * 生成した時に呼び出される。
         *
         * @param db データベース
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            mLogger.d("IN");

            // テーブル削除SQLを実行する。
            db.execSQL(mDropTableSQL);

            // テーブル生成用SQLを実行する。
            db.execSQL(mCreateTableSQL);

            mLogger.d("OUT(OK)");
        }

        /**
         * アップグレードした時に呼び出される。
         *
         * @param db データベース
         * @param oldVersion 旧バージョン
         * @param newVersion 新バージョン
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mLogger.d("IN");

            // 生成した時と同様の処理を行う。
            onCreate(db);

            mLogger.d("OUT(OK)");
        }
    }
}
