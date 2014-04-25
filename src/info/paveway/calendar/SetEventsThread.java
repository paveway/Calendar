package info.paveway.calendar;

import info.paveway.calendar.oauth2.OAuth2Constants;
import info.paveway.calendar.oauth2.UpdateOAuth2Token;
import info.paveway.calendar.oauth2.UpdateOAuth2TokenListener;
import info.paveway.log.Logger;
import info.paveway.util.DateUtil;
import info.paveway.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * イベント設定スレッドクラス
 *
 * @version 1.0 新規作成
 *
 */
public class SetEventsThread extends Thread {

    /** ロガー */
    private final Logger mLogger = new Logger(SetEventsThread.class);

    /** コンテキスト */
    private final Context mContext;

    /** カレンダー */
    private final Calendar mCalendar;

    /** 親ビュー */
    private final LinearLayout mParent;

    /** ページオフセット位置 */
    private final int mOffset;

    /** 曜日始まりタイプ */
    private final int mStartType;

    /**
     * コンテキスト
     *
     * @param context コンテキスト
     *
     * @param calendar カレンダー
     * @param parent 親ビュー
     * @param offset オフセット
     * @param startType 曜日始まりタイプ
     */
    public SetEventsThread(Context context, Calendar calendar, LinearLayout parent, int offset, int startType) {
        mContext = context;
        mCalendar = calendar;
        mParent = parent;
        mOffset = offset;
        mStartType = startType;
    }

    /**
     * 認証トークンを更新する。
     */
    @Override
    public void run() {
        mLogger.d("IN");

        // 認証トークン更新クラスを生成する。
        UpdateOAuth2Token updateOAuth2Token =
            new UpdateOAuth2Token(mContext, new UpdateOAuth2TokenListenerImpl());

        // 認証トークンを更新する。
        updateOAuth2Token.getOAuth2Token();

        mLogger.d("OUT(OK)");
    }

    /**
     * 認証トークン更新リスナークラス
     */
    private class UpdateOAuth2TokenListenerImpl implements UpdateOAuth2TokenListener {

        /** ロガー */
        private final Logger mLogger = new Logger(UpdateOAuth2TokenListenerImpl.class);

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
                new GetEventsListTask(new ReceiveEventDataListener()).execute(params);

                mLogger.d("OUT(OK)");
            } else {
                mLogger.w("OUT(NG)");
            }
        }
    }

    /**
     * イベントデータ受信リスナークラス
     */
    private class ReceiveEventDataListener implements ReceiveDataListener {

        /** ロガー */
        private final Logger mLogger = new Logger(ReceiveEventDataListener.class);

        /**
         * イベントデータを受信した時に呼び出される。
         *
         * @param eventDataMap イベントデータマップ
         */
        @Override
        public void onReceiveEventData(Map<String, List<EventData>> eventDataMap) {
            mLogger.d("IN");

            // イベントデータを設定する。
            setEvents(eventDataMap);

            mLogger.d("OUT(OK)");
        }

        /**
         * イベントデータを設定する。
         *
         * @param eventDataList イベントデータリスト
         */
        public void setEvents(Map<String, List<EventData>> eventDataMap) {
            mLogger.d("IN");

            // 日付セルテーブルを取得する。
            TableLayout dateCellTable = (TableLayout)mParent.findViewById(R.id.dateCellTable);

            // 検索キーのフォーマットを生成する。
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN);

            // 週テーブル行数分繰り返す。
            for (int i = 0; i < MonthPagerAdapter.WEEK_ROW_NUM; i++) {
                // 週テーブル行を取得する。
                TableRow dateCellRow = (TableRow)dateCellTable.findViewById(MonthPagerAdapter.DATE_CELL_ROW_IDS.get(i));

                // 曜日数分繰り返す。
                for (int j = 0; j < CommonConstants.DAYS_OF_WEEK; j++) {
                    // 日付セル番号を取得する。
                    int dateCellNo = i * CommonConstants.DAYS_OF_WEEK + j;

                    // 対象のカレンダーを取得する。
                    Calendar cal = DateUtil.getDateCellCalendar(mCalendar, mOffset, dateCellNo, mStartType);

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
                            eventLabel.setTextSize(MonthPagerAdapter.EVENT_LABEL_FONT_SIZE);

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
}
