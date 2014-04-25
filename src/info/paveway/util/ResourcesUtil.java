package info.paveway.util;

import info.paveway.calendar.CommonConstants;
import info.paveway.calendar.R;
import android.content.res.Resources;

/**
 * リソースユーティリティクラス
 *
 * @version 1.0 新規作成
 *
 */
public class ResourcesUtil {

    private static final int SUN_POSITION_START_MON = 6;
    private static final int SAT_POSITION_START_MON = 5;
    private static final int SUN_POSITION_START_SUN = 0;
    private static final int SAT_POSITION_START_SUN = 6;

    /** リソース */
    private final Resources mResources;

    /** 日曜日位置 */
    private int mSunPosition;

    /** 土曜日位置 */
    private int mSatPosition;

    /**
     * コンストラクタ
     *
     * @param resources リソース
     */
    public ResourcesUtil(Resources resources) {
        // スーパークラスのコンストラクタを呼び出す。
        super();

        // 引数のデータを設定する。
        mResources = resources;
    }

    private void setStartPosition(int startType) {
        if (0 == startType) {
            mSunPosition = SUN_POSITION_START_MON;
            mSatPosition = SAT_POSITION_START_MON;

        } else {
            mSunPosition = SUN_POSITION_START_SUN;
            mSatPosition = SAT_POSITION_START_SUN;
        }
    }

    /**
     * 曜日ごとの背景リソースを返却する。
     *
     * @param position 日付セル位置
     * @param currentMonth 対象月
     * @return 曜日ごとの枠線ID
     */
    public int getBackgroundResource(int position, boolean currentMonth, int startType) {
        // デフォルトの背景リソース
        int backgroundResource = R.drawable.border_ivory;

        setStartPosition(startType);

        // 対象月の場合
        if (currentMonth) {
            // 曜日を求める。
            int daysOfWeek = position % CommonConstants.DAYS_OF_WEEK;

            // 日曜日の場合
            if (mSunPosition == daysOfWeek) {
                backgroundResource = R.drawable.border_mistyrose;

            // 土曜日の場合
            } else if (mSatPosition == daysOfWeek) {
                backgroundResource = R.drawable.border_lightskyblue;
            }

        // 対象月ではない場合
        } else {
            backgroundResource = R.drawable.border_whitesmoke;
        }

        return backgroundResource;
    }

    /**
     * 曜日ごとの日付のカラーを返却する。
     *
     * @param dateCellNo 日付セル番号
     * @param currentMonth 対象月
     * @return 曜日ごとのカラーID
     */
    public int getDateColor(int dateCellNo, boolean currentMonth, int startType) {
        int daysOfWeek = dateCellNo % CommonConstants.DAYS_OF_WEEK;

        // デフォルトのカラーID
        int colorId = R.color.black;

        setStartPosition(startType);

        // 対象月の場合
        if (currentMonth) {
            // 日曜日の場合
            if (mSunPosition == daysOfWeek) {
                colorId = R.color.red;

            // 土曜日の場合
            } else if (mSatPosition == daysOfWeek) {
                colorId = R.color.blue;
            }

        // 対象月ではない場合
        } else {
            colorId = R.color.gray;
        }

        return mResources.getColor(colorId);
    }

    /**
     * 六曜のカラーを取得する。
     *
     * @param rokuyou 六曜文字列
     * @param currentMonth 対象月
     * @return 六曜のカラー
     */
    public int getRokuyouColor(String rokuyou, boolean currentMonth) {
        int colorId = R.color.black;

        // 対象月ではない場合
        if (!currentMonth) {
            colorId = R.color.gray;

        } else {
            if ("大安".equals(rokuyou)) {
                colorId = R.color.red;

            } else if ("仏滅".equals(rokuyou)) {
                colorId = R.color.blue;
            }
        }

        return mResources.getColor(colorId);
    }

    /**
     * リソース文字列を取得する。
     *
     * @param id 文字列リソースID
     * @return 文字列
     */
    public String getResourceString(int id) {
        return mResources.getString(id);
    }

    /**
     * 色リソース番号を取得する。
     *
     * @param id 色リソースID
     * @return 色リソース番号
     */
    public int getResourceColor(int id) {
        return mResources.getColor(id);
    }
}
