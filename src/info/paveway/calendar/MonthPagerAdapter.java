package info.paveway.calendar;

import info.paveway.calendar.oauth2.OAuth2Constants;
import info.paveway.calendar.oauth2.UpdateOAuth2Token;
import info.paveway.calendar.oauth2.UpdateOAuth2TokenListener;
import info.paveway.external.HolidayUtil;
import info.paveway.external.QReki;
import info.paveway.external.Sekki;
import info.paveway.log.Logger;
import info.paveway.util.DateUtil;
import info.paveway.util.ResourcesUtil;
import info.paveway.util.StringUtil;
import info.paveway.util.ViewIdGenerator;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * 月表示ページャーアダプタークラス
 *
 * @version 1.0 新規作成
 *
 */
public class MonthPagerAdapter extends PagerAdapter {

    /** ロガー */
    private final Logger mLogger = new Logger(MonthPagerAdapter.class);

    /** 月数 */
    private static final int NUM_OF_MONTHS = 12;

    /** ページ最大数(未来、過去でそれぞれ100年分(12ヶ月x100年x2) */
    private static final int PAGE_MAX = NUM_OF_MONTHS * 100 * 2;

    /** ページ開始番号 */
    public static final int START_PAGE = PAGE_MAX / 2;

    /** 週の行数 */
    public static final int WEEK_ROW_NUM = 6;

    /** 日付ラベルフォントサイズ */
    private static final float DATE_LABEL_FONT_SIZE = 16.0f;

    /** 六曜ラベルフォントサイズ */
    private static final float ROKUYOU_LABEL_FONT_SIZE = 12.0f;

    /** イベントラベルフォントサイズ */
    public static final float EVENT_LABEL_FONT_SIZE = 12.0f;

    /** 曜日セルID配列 */
    private static SparseIntArray DAYS_OF_WEEK_IDS;
    static {
        DAYS_OF_WEEK_IDS = new SparseIntArray();
        DAYS_OF_WEEK_IDS.put(0,  R.id.daysOfWeekLabel1);
        DAYS_OF_WEEK_IDS.put(1,  R.id.daysOfWeekLabel2);
        DAYS_OF_WEEK_IDS.put(2,  R.id.daysOfWeekLabel3);
        DAYS_OF_WEEK_IDS.put(3,  R.id.daysOfWeekLabel4);
        DAYS_OF_WEEK_IDS.put(4,  R.id.daysOfWeekLabel5);
        DAYS_OF_WEEK_IDS.put(5,  R.id.daysOfWeekLabel6);
        DAYS_OF_WEEK_IDS.put(6,  R.id.daysOfWeekLabel7);
    }

    /** 曜日セル用曜日文字列ID配列(月曜日始まり) */
    private static SparseIntArray START_MON_TEXT_IDS;
    static {
        START_MON_TEXT_IDS = new SparseIntArray();
        START_MON_TEXT_IDS.put(0,  R.string.monday);
        START_MON_TEXT_IDS.put(1,  R.string.tuesday);
        START_MON_TEXT_IDS.put(2,  R.string.wednesday);
        START_MON_TEXT_IDS.put(3,  R.string.thursday);
        START_MON_TEXT_IDS.put(4,  R.string.friday);
        START_MON_TEXT_IDS.put(5,  R.string.saturday);
        START_MON_TEXT_IDS.put(6,  R.string.sunday);
    }

    /** 曜日セル用曜日文字列ID配列(日曜日始まり) */
    private static SparseIntArray START_SUN_TEXT_IDS;
    static {
        START_SUN_TEXT_IDS = new SparseIntArray();
        START_SUN_TEXT_IDS.put(0,  R.string.sunday);
        START_SUN_TEXT_IDS.put(1,  R.string.monday);
        START_SUN_TEXT_IDS.put(2,  R.string.tuesday);
        START_SUN_TEXT_IDS.put(3,  R.string.wednesday);
        START_SUN_TEXT_IDS.put(4,  R.string.thursday);
        START_SUN_TEXT_IDS.put(5,  R.string.friday);
        START_SUN_TEXT_IDS.put(6,  R.string.saturday);
    }

    /** 曜日セル用背景色ID配列(月曜日始まり) */
    private static SparseIntArray START_MON_BACKGROUND_IDS;
    static {
        START_MON_BACKGROUND_IDS = new SparseIntArray();
        START_MON_BACKGROUND_IDS.put(0,  R.drawable.border_ivory);
        START_MON_BACKGROUND_IDS.put(1,  R.drawable.border_ivory);
        START_MON_BACKGROUND_IDS.put(2,  R.drawable.border_ivory);
        START_MON_BACKGROUND_IDS.put(3,  R.drawable.border_ivory);
        START_MON_BACKGROUND_IDS.put(4,  R.drawable.border_ivory);
        START_MON_BACKGROUND_IDS.put(5,  R.drawable.border_lightskyblue);
        START_MON_BACKGROUND_IDS.put(6,  R.drawable.border_mistyrose);
    }

    /** 曜日セル用背景色ID配列(日曜日始まり) */
    private static SparseIntArray START_SUN_BACKGROUND_IDS;
    static {
        START_SUN_BACKGROUND_IDS = new SparseIntArray();
        START_SUN_BACKGROUND_IDS.put(0,  R.drawable.border_mistyrose);
        START_SUN_BACKGROUND_IDS.put(1,  R.drawable.border_ivory);
        START_SUN_BACKGROUND_IDS.put(2,  R.drawable.border_ivory);
        START_SUN_BACKGROUND_IDS.put(3,  R.drawable.border_ivory);
        START_SUN_BACKGROUND_IDS.put(4,  R.drawable.border_ivory);
        START_SUN_BACKGROUND_IDS.put(5,  R.drawable.border_ivory);
        START_SUN_BACKGROUND_IDS.put(6,  R.drawable.border_lightskyblue);
    }

    /** 曜日セル用文字色ID配列(月曜日始まり) */
    private static SparseIntArray START_MON_TEXT_COLOR_IDS;
    static {
        START_MON_TEXT_COLOR_IDS = new SparseIntArray();
        START_MON_TEXT_COLOR_IDS.put(0,  R.color.black);
        START_MON_TEXT_COLOR_IDS.put(1,  R.color.black);
        START_MON_TEXT_COLOR_IDS.put(2,  R.color.black);
        START_MON_TEXT_COLOR_IDS.put(3,  R.color.black);
        START_MON_TEXT_COLOR_IDS.put(4,  R.color.black);
        START_MON_TEXT_COLOR_IDS.put(5,  R.color.blue);
        START_MON_TEXT_COLOR_IDS.put(6,  R.color.red);
    }

    /** 曜日セル用文字色ID配列(月曜日始まり) */
    private static SparseIntArray START_SUN_TEXT_COLOR_IDS;
    static {
        START_SUN_TEXT_COLOR_IDS = new SparseIntArray();
        START_SUN_TEXT_COLOR_IDS.put(0,  R.color.red);
        START_SUN_TEXT_COLOR_IDS.put(1,  R.color.black);
        START_SUN_TEXT_COLOR_IDS.put(2,  R.color.black);
        START_SUN_TEXT_COLOR_IDS.put(3,  R.color.black);
        START_SUN_TEXT_COLOR_IDS.put(4,  R.color.black);
        START_SUN_TEXT_COLOR_IDS.put(5,  R.color.black);
        START_SUN_TEXT_COLOR_IDS.put(6,  R.color.blue);
    }

	/** 日付セル行ID配列 */
    public static SparseIntArray DATE_CELL_ROW_IDS;
    static {
        DATE_CELL_ROW_IDS = new SparseIntArray();
        for (int i = 0; i < WEEK_ROW_NUM; i++) {
            DATE_CELL_ROW_IDS.put(i, ViewIdGenerator.generateViewId());
        }
    }

    /** 日付セルID配列 */
    private static SparseIntArray DATE_CELL_IDS;
    static {
        DATE_CELL_IDS = new SparseIntArray();
        int dateCellNum = CommonConstants.DAYS_OF_WEEK * WEEK_ROW_NUM;
        for (int i = 0; i < dateCellNum; i++) {
            DATE_CELL_IDS.put(i,  ViewIdGenerator.generateViewId());
        }
    }

    /** 日付ラベルID */
    private static final int DATE_LABEL_ID = ViewIdGenerator.generateViewId();

    /** 六曜ラベルID */
    private static final int ROKUYOU_LABEL_ID = ViewIdGenerator.generateViewId();

    /** イベント最大数 */
    private static final int EVENT_MAX_NUM = 4;

    /** イベントラベルID配列 */
    private static SparseIntArray EVENT_LABEL_IDS;
    static {
        EVENT_LABEL_IDS = new SparseIntArray();
        for (int i = 0; i < EVENT_MAX_NUM; i++) {
            EVENT_LABEL_IDS.put(i,  ViewIdGenerator.generateViewId());
        }
    }

    /** コンテキスト */
    private final Context mContext;

    /** ページ番号リスナー */
    private final PageNoListener mPageNoListener;

    /** レイアウトインフレーター */
    private final LayoutInflater mLayoutInflater;

    /** リソースユーティリティ */
    private final ResourcesUtil mResourcesUtil;

    /** カレンダー */
    private final Calendar mCalendar;

    /** 曜日開始タイプ */
    private int mStartType;

    private boolean mRokuyouFlg;
    
    private boolean mHolidayFlg;
    
    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param viewPager ビューページャー
     */
    public MonthPagerAdapter(Context context, ViewPager viewPager, PageNoListener pageNoListener) {
        // スーパークラスのコンストラクタを呼び出す。
        super();

        mLogger.i("IN");

        // 引数のデータを設定する。
        mContext = context;
        mPageNoListener = pageNoListener;

        // レイアウトインフレーターを設定する。
        mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // リソースを生成する。
        mResourcesUtil = new ResourcesUtil(context.getResources());

        // カレンダーを生成する。
        mCalendar = new GregorianCalendar();

        mLogger.i("OUT(OK)");
    }

    /**
     * カウント数を返却する。
     *
     * @return カウント数
     */
    @Override
    public int getCount() {
        return PAGE_MAX;
    }

    /**
     * ページを生成する。
     * position番目のViewを生成し返却する。
     *
     * @param container 表示するViewのコンテナ
     * @param position インスタンス生成位置
     * @return ページを格納しているコンテナを返却すること。
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mLogger.i("IN position=[" + position + "]");

        // ページビューのオフセット位置を取得する。
        int offset = position - START_PAGE ;
        mLogger.d("offset=[" + offset + "]");

        // レイアウトを取得する。
        LinearLayout parent = (LinearLayout)mLayoutInflater.inflate(R.layout.month, null);

        // 曜日開始タイプを取得する。
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mStartType = Integer.parseInt(sharedPreferences.getString(CommonConstants.PREF_KEY_START_TYPE_LIST_KEY, "0"));

        // 曜日数分繰り返す。
        for (int i = 0; i < CommonConstants.DAYS_OF_WEEK; i++) {
            // 曜日ラベルを取得する。
            TextView daysOfWeekLabel = (TextView)parent.findViewById(DAYS_OF_WEEK_IDS.get(i));

            // 曜日テキスト、背景、テキスト色のIDを判別する。
            SparseIntArray textIds;
            SparseIntArray backgroundIds;
            SparseIntArray textColorIds;

            // 曜日開始タイプが月曜日始まりの場合
            if (0 == mStartType) {
                textIds       = START_MON_TEXT_IDS;
                backgroundIds = START_MON_BACKGROUND_IDS;
                textColorIds  = START_MON_TEXT_COLOR_IDS;

            // 曜日開始タイプが日曜日始まりの場合
            } else {
                textIds       = START_SUN_TEXT_IDS;
                backgroundIds = START_SUN_BACKGROUND_IDS;
                textColorIds  = START_SUN_TEXT_COLOR_IDS;
            }

            // 曜日ラベルにテキスト、背景、テキスト色を設定する。
            daysOfWeekLabel.setText(mResourcesUtil.getResourceString(textIds.get(i)));
            daysOfWeekLabel.setBackgroundResource(backgroundIds.get(i));
            daysOfWeekLabel.setTextColor(mResourcesUtil.getResourceColor(textColorIds.get(i)));
        }

        // 年月ラベルを設定する。
        setYearMonthLabel(parent, offset);

        // 日付セルテーブルを取得する。
        TableLayout dateCellTable = (TableLayout)parent.findViewById(R.id.dateCellTable);

        // 週テーブル行数分繰り返す。
        for (int i = 0; i < WEEK_ROW_NUM; i++) {
            // 週テーブル行を生成する。
            TableRow dateCellRow = new TableRow(mContext);
            dateCellRow.setId(DATE_CELL_ROW_IDS.get(i));

            // 週テーブル行のレイアウトパラメータを生成し、設定する。
            TableLayout.LayoutParams params =
                    new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, 0, 1.0f);
            dateCellRow.setLayoutParams(params);

            // 日付セルを設定する。
            setDateCell(dateCellRow, i, offset);

            // 日付セルテーブルに週テーブル行を設定する。
            dateCellTable.addView(dateCellRow, i);
        }

        // ビューページャーへ親ビューを設定する。
        ((ViewPager)container).addView(parent);

        // 認証トークンを更新し、イベントデータを設定する。
        new SetEventsThread(mContext, mCalendar, parent, offset, mStartType).run();

        mLogger.i("OUT(OK)");
        return parent;
    }

    /**
     * ページ位置のビューを削除する。
     *
     * @param container コンテナ
     * @param position ページ位置
     * @param object コンテナが返却したオブジェクト
     */
    @Override
    public void destroyItem(View container, int position, Object view) {
        mLogger.i("IN position=[" + position + "]");

        ((ViewPager)container).removeView((View)view);

        mLogger.i("OUT(OK)");
    }

    /**
     * ビューがコンテナに含まれているか判定する。
     *
     * @param view 対象のビュー
     * @param object コンテナが返却したオブジェクト
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        mLogger.i("IN");

        boolean result = (view == ((View)object));

        mLogger.i("OUT(OK) result=[" + result + "]");
        return result;
    }

    /**
     * アイテムの位置を取得する。
     * PagerAdapterを更新するために必要
     *
     * @param object アイテム
     * @return アイテムの位置
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * カレンダーを取得する。
     *
     * @return カレンダー
     */
    public Calendar getCalendar() {
        return mCalendar;
    }

    /**
     * 認証トークン更新スレッドクラス
     */
    private class UpdateOAuth2TokenThread extends Thread {

        /** ロガー */
        private final Logger mLogger = new Logger(UpdateOAuth2TokenThread.class);

        /** 親ビュー */
        private final LinearLayout mParent;

        /** ページオフセット位置 */
        private final int mOffset;

        /**
         * コンストラクタ
         *
         * @param parent 親ビュー
         * @param offset ページオフセット位置
         */
        public UpdateOAuth2TokenThread(LinearLayout parent, int offset) {
            mLogger.d("IN offset=[" + offset + "]");

            mParent = parent;
            mOffset = offset;

            mLogger.d("OUT(OK)");
        }

        /**
         * 認証トークンを更新する。
         */
        @Override
        public void run() {
            mLogger.d("IN");

            // 認証トークン更新クラスを生成する。
            UpdateOAuth2Token updateOAuth2Token =
                new UpdateOAuth2Token(mContext, new UpdateOAuth2TokenListenerImpl(mParent, mOffset));

            // 認証トークンを更新する。
            updateOAuth2Token.getOAuth2Token();

            mLogger.d("OUT(OK)");
        }
    }

    /**
     * 認証トークン更新リスナークラス
     */
    private class UpdateOAuth2TokenListenerImpl implements UpdateOAuth2TokenListener {

        /** ロガー */
        private final Logger mLogger = new Logger(UpdateOAuth2TokenListenerImpl.class);

        /** 親ビュー */
        private final LinearLayout mParent;

        /** ページオフセット位置 */
        private final int mOffset;

        /**
         * コンストラクタ
         *
         * @param parents 親ビュー
         * @param offset ページオフセット位置
         */
        public UpdateOAuth2TokenListenerImpl(LinearLayout parents, int offset) {
            mLogger.d("IN offset=[" + offset + "]");

            mParent = parents;
            mOffset = offset;

            mLogger.d("OUT(OK)");
        }

        /**
         * 認証トークンが更新された時に呼び出される。
         */
        @Override
        public void onUpdate() {
            mLogger.d("IN");

            // プリフェレンスからアクセストークンを取得する。
            SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(OAuth2Constants.PREF_KEY_AUTH_INFO, Context.MODE_PRIVATE);
            String accessToken =
                sharedPreferences.getString(OAuth2Constants.PREF_KEY_ACCESS_TOKEN, null);

            // アクセストークンが取得できた場合
            if (StringUtil.isNotNullOrEmpty(accessToken)) {
                // イベントリストを取得する。
                Calendar calendar =
                        DateUtil.getPageCalendar(
                                mCalendar, mOffset);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01'T'00:00:00'Z'", Locale.JAPAN);
                String startDate = sdf.format(calendar.getTime());
                calendar.add(Calendar.MONTH, 1);
                String endDate = sdf.format(calendar.getTime());

                // イベントリスト取得タスクのパラメータを生成する。
                String[] params = {accessToken, startDate, endDate};

                // イベントリスト取得タスクを実行する。
                new GetEventsListTask(new ReceiveEventDataListener(mParent, mOffset)).execute(params);

                mLogger.d("OUT(OK)");
            } else {
                mLogger.w("OUT(NG)");
            }
        }
    }

    /**
     * イベントデータ受信リスナークラス
     */
    public class ReceiveEventDataListener implements ReceiveDataListener {

        /** ロガー */
        private final Logger mLogger = new Logger(ReceiveEventDataListener.class);

        /** 親ビュー */
        private final LinearLayout mParent;

        /** ページオフセット位置 */
        private final int mOffset;

        /**
         * コンストラクタ
         *
         * @param parent 親ビュー
         * @param offset ページオフセット位置
         */
        public ReceiveEventDataListener(LinearLayout parent, int offset) {
            mLogger.d("IN offset=[" + offset + "]");

            mParent = parent;
            mOffset = offset;

            mLogger.d("OUT(OK)");
        }

        /**
         * イベントデータを受信した時に呼び出される。
         *
         * @param eventDataMap イベントデータマップ
         */
        @Override
        public void onReceiveEventData(Map<String, List<EventData>> eventDataMap) {
            mLogger.d("IN");

            // イベントデータマップを設定する。
            setEventDataMap(eventDataMap, mParent, mOffset);

            mLogger.d("OUT(OK)");
        }

        /**
         * イベントデータマップを設定する。
         *
         * @param eventDataList イベントデータリスト
         * @param parent 親ビュー
         * @param offset ページオフセット位置
         */
        public void setEventDataMap(Map<String, List<EventData>> eventDataMap, LinearLayout parent, int offset) {
            mLogger.d("IN");

            // 日付セルテーブルを取得する。
            TableLayout dateCellTable = (TableLayout)parent.findViewById(R.id.dateCellTable);

            // 検索キーのフォーマットを生成する。
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN);

            // 週テーブル行数分繰り返す。
            for (int i = 0; i < WEEK_ROW_NUM; i++) {
                // 週テーブル行を取得する。
                TableRow dateCellRow = (TableRow)dateCellTable.findViewById(DATE_CELL_ROW_IDS.get(i));

                // 曜日数分繰り返す。
                for (int j = 0; j < CommonConstants.DAYS_OF_WEEK; j++) {
                    // 日付セル番号を取得する。
                    int dateCellNo = i * CommonConstants.DAYS_OF_WEEK + j;

                    // 対象のカレンダーを取得する。
                    Calendar cal = DateUtil.getDateCellCalendar(mCalendar, offset, dateCellNo, mStartType);

                    // 年月日文字列を取得する。
                    String dateString = sdf.format(cal.getTime());

                    // イベントデータマップに対象のイベントデータが含まれる場合
                    if (eventDataMap.containsKey(dateString)) {
                        // 各曜日のセルのベースレイアウトを取得する。
                        RelativeLayout cellLayout = (RelativeLayout)dateCellRow.getChildAt(j);

                        // 日付ラベルを取得する。
                        TextView dateLabel = (TextView)cellLayout.getChildAt(0);
                        int prevViewId = dateLabel.getId();

                        // イベントデータを取得する。
                        List<EventData> eventDataList = eventDataMap.get(dateString);
                        for (int k = 0; k < eventDataList.size(); k++) {
                            // イベントデータを取得する。
                            EventData eventData = eventDataList.get(k);

                            // イベントラベルを生成する。
                            TextView eventLabel = (TextView)cellLayout.getChildAt(k + 2);

                            // テキストを設定する。
                            eventLabel.setText(eventData.getSummary());

                            // テキストサイズを設定する。
                            eventLabel.setTextSize(EVENT_LABEL_FONT_SIZE);

                            // 行数を設定する。
                            eventLabel.setMaxLines(1);

                            // はみ出した行末を省略するように設定する。
                            eventLabel.setEllipsize(TextUtils.TruncateAt.END);

                            // レイアウトパラメータを生成し、設定する。
                            RelativeLayout.LayoutParams eventParams =
                                    new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                            // 日付ラベルの下に配置する。
                            eventParams.addRule(RelativeLayout.BELOW, prevViewId);

                            // 前のビューのIDを保存する。
                            prevViewId = eventLabel.getId();
                        }
                    }
                }
            }

            mLogger.d("OUT(OK)");
        }
    }

    /**
     * 年月ラベルを設定する。
     *
     * @param parent 親ビュー
     * @param position ページ位置
     */
    private void setYearMonthLabel(LinearLayout parent, int position) {
        mLogger.d("IN position=[" + position + "]");

        // 年月ラベルを取得する。
        TextView yearMonthLabel = (TextView)parent.findViewById(R.id.yearMonthLabel);

        // 年月ラベルを設定する。
        yearMonthLabel.setText(createYearMonthText(position));

        // リスナーを設定する。
        yearMonthLabel.setOnClickListener(new YearMonthLabelOnClickListener());

        mLogger.d("OUT(OK)");
    }

    /**
     * 年月テキストを生成する。
     *
     * @param position ページ位置
     * @return 年月テキスト
     */
    private String createYearMonthText(int position) {
        mLogger.d("IN position=[" + position + "]");

        // ページ位置のカレンダーを取得する。
        Calendar cal = DateUtil.getPageCalendar(mCalendar, position);

        String result =
                String.valueOf(cal.get(Calendar.YEAR))      + mContext.getString(R.string.year) +
                String.valueOf(cal.get(Calendar.MONTH) + 1) + mContext.getString(R.string.month);
        mLogger.d("OUT(OK) result=[" + result + "]");
        return result;

    }

    /**
     * 日付セルを設定する。
     *
     * @param parent 親のビュー
     * @param rowNo 行番号
     * @param position セルの位置
     */
    private void setDateCell(TableRow parent, int rowNo, int position) {
        mLogger.v("IN rowNo=[" + rowNo + "] position=[" + position + "]");

        // 週の日数分繰り返す。
        for (int i = 0; i < CommonConstants.DAYS_OF_WEEK; i++) {
            // 日付セル番号を取得する。
            int dateCellNo = rowNo * CommonConstants.DAYS_OF_WEEK + i;

            // 対象のカレンダーを取得する。
            Calendar cal = DateUtil.getDateCellCalendar(mCalendar, position, dateCellNo, mStartType);

            // 日付位置の月が表示月か判定する。
            boolean currentMonth = (cal.get(Calendar.MONTH) == DateUtil.getTargetMonth(mCalendar, position));

            // 日付セルのベースレイアウトを設定する。
            RelativeLayout baseLayout = setBaseLayout(dateCellNo, position, i, currentMonth);

            // 日付ラベルを設定する。
            TextView dateLabel = setDateLabel(cal, baseLayout, currentMonth, dateCellNo);

            // 六曜ラベルを設定する。
            setRokuyouLabel(cal, baseLayout, currentMonth);

            // イベントラベルを設定する
            setEventLabels(cal, baseLayout, dateLabel, currentMonth);

            // 日付テーブル行に日付ベースレイアウトを追加する。
            parent.addView(baseLayout, i);
        }

        mLogger.v("OUT(OK)");
    }

    /**
     * ベースレイアウトを設定する。
     *
     * @param dateCellNo 日付セル番号
     * @param position 位置
     * @return ベースレイアウト
     */
    private RelativeLayout setBaseLayout(int dateCellNo, int position, int daysOfWeek, boolean currentMonth) {
        // 日付セルのベースレイアウトを生成する。
        RelativeLayout baseLayout = new RelativeLayout(mContext);

        // クリックした時に日付セルを識別するためのIDを設定する。
        baseLayout.setId(DATE_CELL_IDS.get(dateCellNo));

        // 背景を設定する。
        baseLayout.setBackgroundResource(mResourcesUtil.getBackgroundResource(daysOfWeek, currentMonth, mStartType));

        // クリックリスナーを設定する。
        baseLayout.setOnClickListener(new DateCellOnClickListener(position));

        // レイアウトパラメータを生成し、設定する。
        TableRow.LayoutParams baseParams =
                new TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        baseLayout.setLayoutParams(baseParams);

        return baseLayout;
    }

    /**
     * 日付ラベルを設定する。
     *
     * @param cal カレンダー
     * @param baseLayout ベースレイアウト
     * @param currentMonth 現在月フラグ
     * @param dateCellNo 日付セル番号
     * @return 日付ラベル
     */
    private TextView setDateLabel(Calendar cal, RelativeLayout baseLayout, boolean currentMonth, int dateCellNo) {
        // 日付ラベルを生成する。
        TextView dateLabel = new TextView(mContext);

        // 祝日ラベルを相対配置するためIDを設定する。
        dateLabel.setId(DATE_LABEL_ID);

        // 日付文字を設定する。
        dateLabel.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

        // 文字サイズを設定する。
        dateLabel.setTextSize(DATE_LABEL_FONT_SIZE);

        // 文字色を設定する。
        dateLabel.setTextColor(mResourcesUtil.getDateColor(dateCellNo, currentMonth, mStartType));

        // レイアウトパラメータを生成し、設定する。
        RelativeLayout.LayoutParams dateParams =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        // ベースレイアウトの左上に設定する。
        dateParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        dateParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        // ベースレイアウトに配置する。
        baseLayout.addView(dateLabel, dateParams);
        return dateLabel;
    }

    /**
     * 六曜ラベルを設定する。
     *
     * @param cal カレンダー
     * @param baseLayout ベースレイアウト
     * @param currentMonth 現在月フラグ
     */
    private void setRokuyouLabel(Calendar cal, RelativeLayout baseLayout, boolean currentMonth) {
        // 六曜ラベルを生成する。
        TextView rokuyouLabel = new TextView(mContext);

        // IDを設定する。
        rokuyouLabel.setId(ROKUYOU_LABEL_ID);

        // 六曜文字列を取得する。
        String rokuyou = QReki.getRokuYo(cal);

        // 六曜文字列を設定する。
        rokuyouLabel.setText(rokuyou);

        // 文字サイズを設定する。
        rokuyouLabel.setTextSize(ROKUYOU_LABEL_FONT_SIZE);

        // 文字色を設定する。
        rokuyouLabel.setTextColor(mResourcesUtil.getRokuyouColor(rokuyou, currentMonth));

        // レイアウトパラメータを生成する。
        RelativeLayout.LayoutParams rokuyouParams =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        // ベースレイアウトの右上に配置する。
        rokuyouParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rokuyouParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        // ベースレイアウトに設定する。
        baseLayout.addView(rokuyouLabel, rokuyouParams);
    }

    /**
     * イベントラベルを設定する。
     *
     * @param cal カレンダー
     * @param baseLayout ベースレイアウト
     * @param dateLabel 日付ラベル
     * @param currentMonth 現在月フラグ
     */
    private void setEventLabels(Calendar cal, RelativeLayout baseLayout, TextView dateLabel, boolean currentMonth) {
        // 祝日フラグ
        boolean holidayFlg = false;

        // イベントリストを生成する。
        List<String> eventList = new ArrayList<String>();

        // 対象月の場合
        if (currentMonth) {
            // 祝日名を取得する。
            String holidayName = HolidayUtil.getHolidayName(cal);

            // 祝日名を取得できた場合
            if (!"".equals(holidayName)) {
                // 祝日フラグを設定する。
                holidayFlg = true;

                // 祝日名を予定リストに追加する。
                eventList.add(holidayName);

                // 祝日用の背景を設定する。
                baseLayout.setBackgroundResource(R.drawable.border_mistyrose);

                // 日付の文字を赤色に設定する。
                dateLabel.setTextColor(mContext.getResources().getColor(R.color.red));
            }
        }

        // イベントリストを取得する。
        getEventList(cal, eventList);

        // イベント数を設定する。
        int eventNum = EVENT_MAX_NUM;

        // 取得したイベント数が表示数より少ない場合
        if (eventNum > eventList.size()) {
            // イベント数を再設定する。
            eventNum = eventList.size();
        }

        // 前のビューのIDを設定する。
        int preViewId = dateLabel.getId();

        // イベント数分繰り返す。
        for (int i = 0; i < eventNum; i++) {
            // イベントラベルを設定する。
            preViewId = setEventLabel(baseLayout, preViewId, i, eventList.get(i), holidayFlg);

            // 祝日フラグをクリアする(祝日はイベントの1件目のみ)
            holidayFlg = false;
        }
    }

    /**
     * イベントリストを取得する。
     *
     * @param cal カレンダー
     * @param eventList イベントリスト
     */
    private void getEventList(Calendar cal, List<String> eventList) {
        // イベントの取得回数を算出する。
        int loop = EVENT_MAX_NUM - eventList.size();

        // イベント数分繰り返す。
        for (int i = 0; i < loop; i++) {
            // ダミーで空文字列を設定する。
            eventList.add("");
        }
    }

    /**
     * イベントラベルを設定する。
     *
     * @param baseLayout ベースレイアウト
     * @param preViewId 前のイベントのID
     * @param event イベント
     * @param holidayFlg 祝日フラグ
     * @return ビューのID
     */
    private int setEventLabel(RelativeLayout baseLayout, int preViewId, int index, String event, boolean holidayFlg) {
        // イベントラベルを生成する。
        TextView label = new TextView(mContext);

        int viewId = EVENT_LABEL_IDS.get(index);
        label.setId(viewId);

        // 予定を設定する。
        label.setText(event);

        // 文字サイズを設定する。
        label.setTextSize(EVENT_LABEL_FONT_SIZE);

        // 行数を設定する。
        label.setMaxLines(1);

        // はみ出した行末を省略するように設定する。
        label.setEllipsize(TextUtils.TruncateAt.END);

        // 祝日の場合
        if (holidayFlg) {
            // 文字色を変更する。
            label.setTextColor(mContext.getResources().getColor(R.color.red));
        }

        // レイアウトパラメータを生成する。
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        // 日付ラベルの下に配置する。
        params.addRule(RelativeLayout.BELOW, preViewId);

        // ベースレイアウトに追加する。
        baseLayout.addView(label, params);

        return viewId;
    }

    /**
     * 年月ラベルクリックリスナークラス
     *
     */
    private class YearMonthLabelOnClickListener implements OnClickListener {

        /** ロガー */
        private final Logger mLogger = new Logger(YearMonthLabelOnClickListener.class);

        /** 現在表示されている年 */
        private int mYear;

        /** 現在表示されている月 */
        private int mMonth;

        /**
        * クリックされた時に呼び出される。
        *
        * @param v 年月ラベル
        */
        @Override
        public void onClick(View v) {
            // 年月選択ダイアログを表示する。
            showYearMonthDialog((TextView)v);
        }

        /**
        * 年月選択ダイアログを表示する。
        *
        * @param yearMonthLabel 年月ラベル
        */
        private void showYearMonthDialog(TextView yearMonthLabel) {
            // 現在表示されている年月の数値を取得する。
            getYearMonth(yearMonthLabel);

            // 年月ピッカーを生成する。
            DatePicker yearMonthPicker = createYearMonthPicker();

            // ダイアログを生成し、設定する。
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(yearMonthPicker);
            builder.setTitle(mContext.getString(R.string.yearmonth_dialog_title));
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setPositiveButton(android.R.string.ok, new YearMonthOnClickListener(yearMonthPicker));
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        }

        /**
        * 現在表示されている年月の数値を取得する。
        *
        * @param yearMonthLabel 年月ラベル
        */
        private void getYearMonth(TextView yearMonthLabel) {
            // 年月ラベルの文字列を取得する。
            String yearMonth = yearMonthLabel.getText().toString();

            // 年の切り出し位置を取得する。
            int yearStrPos = yearMonth.indexOf(mContext.getString(R.string.year));

            // 月の切り出し位置を取得する。
            int monthStrPos = yearMonth.indexOf(mContext.getString(R.string.month));

            // 年、月の数値を取得する。
            mYear = Integer.parseInt(yearMonth.substring(0, yearStrPos));
            mMonth = Integer.parseInt(yearMonth.substring(yearStrPos + 1, monthStrPos));
        }

        /**
        * 年月ピッカーを生成する。
        *
        * @return 年月ピッカー
        */
        private DatePicker createYearMonthPicker() {
            // 年月日ピッカーを生成する。
            DatePicker datePicker = new DatePicker(mContext);

            // 初期値を設定する。
            datePicker.updateDate(mYear, mMonth - 1, 1);

            // 年月ピッカーのIDを取得する。
            int dayId = Resources.getSystem().getIdentifier("day", "id", "android");

            // 年月ピッカーの日を非表示にする。
            datePicker.findViewById(dayId).setVisibility(View.GONE);

            // APIレベル11以上の場合
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // 月のカレンダーを非表示にする。
                try {
                    Method method = datePicker.getClass().getMethod("setCalendarViewShown", new Class[]{boolean.class});
                    if (null != method) {
                        method.invoke(datePicker, new Object[]{Boolean.valueOf(false)});
                    }
                } catch (Exception e) {
                    mLogger.e(e);
                }
            }

            return datePicker;
        }

        /**
        * 年月ラベルクリックリスナークラス
        *
        */
        private class YearMonthOnClickListener implements DialogInterface.OnClickListener {

            /** 年月ピッカー */
            private final DatePicker mYearMonthPicker;

            /**
            * コンストラクタ
            *
            * @param yearMonthPicker 年月ピッカー
            */
            public YearMonthOnClickListener(DatePicker yearMonthPicker) {
                mYearMonthPicker = yearMonthPicker;
            }

            /**
            * クリックした時に呼び出される。
            *
            * @param dialog ダイアログ
            * @param which クリックされたアイテム
            */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 年月ピッカーの年、月を取得する。
                int year = mYearMonthPicker.getYear();
                int month = mYearMonthPicker.getMonth();

                // 設定すべき年月のページ番号を取得する。
                Calendar cal = (Calendar)mCalendar.clone();
                int pageNo = (year - cal.get(Calendar.YEAR)) * MonthPagerAdapter.NUM_OF_MONTHS + (month - cal.get(Calendar.MONTH));

                // ビューページャーのページを更新する。
                mPageNoListener.onUpdate(START_PAGE + pageNo);
            }
        }
    }

    /**
    * 日付セルクリックリスナークラス
    *
    */
    private class DateCellOnClickListener implements OnClickListener {

        /** ページ位置 */
        private final int mPagePosition;

        /**
        * コンストラクタ
        *
        * @param pagePosition ページ位置
        */
        public DateCellOnClickListener(int pagePosition) {
            mPagePosition = pagePosition;
        }

        /**
        * クリックした時に呼び出される。
        *
        * @param v クリックされたビュー
        */
        @Override
        public void onClick(View v) {
            // ビューのIDからセル番号を取得する。
            int cellNo = DATE_CELL_IDS.indexOfValue(v.getId());

            // 日付セル用カレンダーを取得する。
            Calendar cal = DateUtil.getDateCellCalendar(mCalendar, mPagePosition, cellNo, mStartType);

            showInfoDialog(cal);
        }

        /**
         * 情報ダイアログを表示する。
         */
        private void showInfoDialog(Calendar cal) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            // ダイアログの各種設定を行う。
            builder.setTitle(getResourceString(R.string.info_dialog_title));
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage(createMessage(cal));
            builder.setPositiveButton(getResourceString(R.string.info_dialog_positive_button), null);

            // ダイアログを生成する。
            AlertDialog dialog = builder.create();
            // ダイアログ画面外のタップで終了しない設定を行う。
            dialog.setCanceledOnTouchOutside(false);
            // ダイアログを表示する。
            dialog.show();
        }

        /**
         * メッセージを生成する。
         *
         * @return メッセージ
         */
        private String createMessage(Calendar cal) {
            // 各情報を取得する。
            String seireki  = getResourceString(R.string.seireki_prefix) +
                    new SimpleDateFormat(getResourceString(R.string.seireki_format)).format(cal.getTime());
            String wareki   = getResourceString(R.string.wareki_prefix)  + DateUtil.getWareki(cal);
            String kyureki  = getResourceString(R.string.kyureki_prefix) + QReki.getKyureki(cal);
            String nissu    = getResourceString(R.string.nissu_prefix)   + mCalendar.get(Calendar.DAY_OF_YEAR);
            String sekki    = Sekki.getSekki(mCalendar);
            sekki = (!"".equals(sekki) ? (getResourceString(R.string.sekki_prefix) + sekki) : "");

            return  seireki + "\n" + wareki + "\n" + kyureki + "\n" + nissu + "\n" + sekki;
        }

        /**
         * リソースから文字列を取得する。
         *
         * @param id 文字列リソースID
         * @return 文字列
         */
         protected String getResourceString(int id) {
             return mContext.getResources().getString(id);
         }

    }

}
