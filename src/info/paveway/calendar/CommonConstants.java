package info.paveway.calendar;

/**
 * 共通定数クラス
 *
 * @version 1.0 新規作成
 */
public class CommonConstants {

    /**
     * コンストラクタ
     * インスタンス化させない。
     */
    private CommonConstants() {
        // 何もしない。
    }

    /** 付加データキー:ID */
    public static final String EXTRA_KEY_ID = "id";

    /** 付加データキー:件名 */
    public static final String EXTRA_KEY_TITLE = "title";

    /** 付加データキー:開始年 */
    public static final String EXTRA_KEY_GD_START = "gdStart";

    /** 付加データキー:終了年 */
    public static final String EXTRA_KEY_GD_END = "gdEnd";

    /** 付加データキー:場所 */
    public static final String EXTRA_KEY_GD_WHERE = "gdWhere";

    /** 付加データキー:内容 */
    public static final String EXTRA_KEY_CONTENT = "content";

    /** デフォルト値 */
    public static final int DEFAULT_VALUE = -1;

    /** 単位時間(5分) */
    public static final int UNIT_TIME = 5;

    /** デフォルト時間 */
    public static final String DEFAULT_TIME = "0000";

    /** 月数 */
    public static final int NUM_OF_MONTHS = 12;

    /** 週の日数 */
    public static final int DAYS_OF_WEEK = 7;

    /** 週の行数 */
    public static final int WEEK_ROW_NUM = 6;

    /** 設定値キー:曜日始まりキー */
    public static final String PREF_KEY_START_TYPE_LIST_KEY = "start_type_list_key";

    /** 要求コード:設定画面 */
    public static final int REQUEST_CODE_SETTINGS = 2;
}
