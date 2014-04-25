package info.paveway.calendar;

import info.paveway.calendar.data.EventProvider;
import info.paveway.log.Logger;
import info.paveway.util.DateUtil;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * 予定編集画面
 *
 * @version 1.0 新規作成
 *
 */
public class EventEditActivity extends AbstractBaseActivity {

    /** ロガー */
    private Logger mLogger = new Logger(EventEditActivity.class);

    /** 戻るボタン */
    private Button mBackButton;

    /** 登録ボタン */
    private Button mRegistButton;

    /** 日付ラベル */
    private TextView mDateLabel;

    /** 件名 */
    private EditText mTitleValue;

    /** 開始日ラベル */
    private TextView mGdStartDateLabel;

    /** 開始時間ラベル */
    private TextView mGdStartTimeLabel;

    /** 終了日ラベル */
    private TextView mGdEndDateLabel;

    /** 終了時間ラベル */
    private TextView mGdEndTimeLabel;

    /** 場所 */
    private EditText mGdWhereValue;

    /** 内容 */
    private EditText mContentValue;

    /** ID */
    private int mId;

    /**
     * 生成した時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLogger.i("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.schedule_edit_activity);

        // インテントを取得する。
        Intent intent = getIntent();

        // インテントを取得できた場合
        if (null != intent) {
            // 各ビューを取得する。
            mBackButton       = (Button)findViewById(R.id.inputBackButton);
            mRegistButton     = (Button)findViewById(R.id.registButton);
            mDateLabel        = (TextView)findViewById(R.id.inputDateLabel);
            mTitleValue       = (EditText)findViewById(R.id.titleValue);
            mGdStartDateLabel = (TextView)findViewById(R.id.gdStartDateLabel);
            mGdStartTimeLabel = (TextView)findViewById(R.id.gdStartTimeLabel);
            mGdEndDateLabel   = (TextView)findViewById(R.id.gdEndDateLabel);
            mGdEndTimeLabel   = (TextView)findViewById(R.id.gdEndTimeLabel);
            mGdWhereValue     = (EditText)findViewById(R.id.gdWhereValue);
            mContentValue     = (EditText)findViewById(R.id.contentValue);

            // 各ビューにリスナーを設定する。
            mBackButton.setOnClickListener(      new OnClickListener() { public void onClick(View v) { backButton();       }});
            mRegistButton.setOnClickListener(    new OnClickListener() { public void onClick(View v) { registButton();     }});
            mGdStartDateLabel.setOnClickListener(new OnClickListener() { public void onClick(View v) { gdStartDateLabel(); }});
            mGdStartTimeLabel.setOnClickListener(new OnClickListener() { public void onClick(View v) { gdStartTimeLabel(); }});
            mGdEndDateLabel.setOnClickListener(  new OnClickListener() { public void onClick(View v) { gdEndDateLabel();   }});
            mGdEndTimeLabel.setOnClickListener(  new OnClickListener() { public void onClick(View v) { gdEndTimeLabel();   }});

            // 付加データを取得する。
            mId          =         intent.getIntExtra(   CommonConstants.EXTRA_KEY_ID, CommonConstants.DEFAULT_VALUE );
            mTitleValue.setText(   intent.getStringExtra(CommonConstants.EXTRA_KEY_TITLE));
            mGdWhereValue.setText( intent.getStringExtra(CommonConstants.EXTRA_KEY_GD_WHERE));
            mContentValue.setText( intent.getStringExtra(CommonConstants.EXTRA_KEY_CONTENT));
            String gdStartTime   = intent.getStringExtra(CommonConstants.EXTRA_KEY_GD_START);
            String gdEndTime     = intent.getStringExtra(CommonConstants.EXTRA_KEY_GD_END);
            mLogger.d("" +
                    "id=["         + mId                                + "] " +
                    "title=["      + mTitleValue.getText().toString()   + "] " +
                    "gdStatTime=[" + gdStartTime                        + "] " +
                    "gdEndTime=["  + gdEndTime                          + "] " +
                    "gdWhere=["    + mGdWhereValue.getText().toString() + "] " +
                    "content=["    + mContentValue.getText().toString() + "]");

            // 日付ラベルを設定する。
            setDateLabel(mDateLabel,        gdStartTime);
            setDateLabel(mGdStartDateLabel, gdStartTime);
            setDateLabel(mGdEndDateLabel,   gdEndTime);

            // 時分ラベルを設定する。
            setTimeLabel(mGdStartTimeLabel, gdStartTime);
            setTimeLabel(mGdEndTimeLabel,   gdEndTime);

        // インテントを取得できない場合
        } else {
            mLogger.w("Intent is null.");

            // エラーメッセージを表示する。
            toast(R.string.error_failed);

            // 終了する。
            finish();
        }

        mLogger.i("OUT(OK)");
    }

    /**
     * 日付ラベルを設定する。
     *
     * @param dateLabel 日付ラベル
     * @param date 日時文字列
     */
    private void setDateLabel(TextView dateLabel, String date) {
        mLogger.d("IN date=[" + date + "]");

        String[] dates = DateUtil.splitDateString(date);
        String dateText =
                dates[0] + getResourceString(R.string.year) +
                dates[1] + getResourceString(R.string.monday) +
                dates[2] + getResourceString(R.string.date);
        mLogger.d("dateText=[" + dateText + "]");
        dateLabel.setText(dateText);

        mLogger.d("OUT(OK)");
    }

    /**
     * 時分ラベルを設定する。
     *
     * @param timeLabel 時分ラベル
     * @param date 日時文字列
     */
    private void setTimeLabel(TextView timeLabel, String date) {
        mLogger.d("IN date=[" + date + "]");

        String[] dates = DateUtil.splitDateString(date);
        String timeText =
                dates[3] + getResourceString(R.string.hour) +
                dates[4] + getResourceString(R.string.minute);
        mLogger.d("timeText=[" + timeText + "]");

        timeLabel.setText(timeText);

        mLogger.d("OUT(OK)");
    }

    /**
     * 開始日ラベルの処理を行う。
     */
    private void gdStartDateLabel() {
        mLogger.d("IN");

        // 開始日を取得する。
        int[] dates = splitDateLabel(mGdStartDateLabel.getText().toString());

        // 日付ピッカーを生成する。
        DatePicker datePicker = new DatePicker(EventEditActivity.this);

        // APIレベル11以上の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // 月のカレンダーを非表示にする。
            datePicker.setCalendarViewShown(false);
        }

        // 初期値を設定する。
        datePicker.updateDate(dates[0], dates[1] - 1, dates[2]);

        // ダイアログを生成し、設定する。
        AlertDialog.Builder builder = new AlertDialog.Builder(EventEditActivity.this);
        builder.setView(datePicker);
        builder.setTitle(getString(R.string.date_dialog_title));
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton(android.R.string.ok, new GdDateLabelOnClickListener(true, datePicker));
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了日ラベルの処理を行う。
     */
    private void gdEndDateLabel() {
        mLogger.d("IN");

        // 終了日を取得する。
        int[] dates = splitDateLabel(mGdEndDateLabel.getText().toString());

        // 日付ピッカーを生成する。
        DatePicker datePicker = new DatePicker(EventEditActivity.this);

        // APIレベル11以上の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // 月のカレンダーを非表示にする。
            datePicker.setCalendarViewShown(false);
        }
        // 初期値を設定する。
        datePicker.updateDate(dates[0], dates[1] - 1, dates[2]);

        // ダイアログを生成し、設定する。
        AlertDialog.Builder builder = new AlertDialog.Builder(EventEditActivity.this);
        builder.setView(datePicker);
        builder.setTitle(getString(R.string.date_dialog_title));
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton(android.R.string.ok, new GdDateLabelOnClickListener(false, datePicker));
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();

        mLogger.d("OUT(OK)");
    }

    /**
     * 開始時間ラベルの処理を行う。
     */
    private void gdStartTimeLabel() {
        mLogger.d("IN");

        // 開始時間を取得する。
        int[] times = splitTimeLabel(mGdStartTimeLabel.getText().toString());

        // 時間ピッカーを生成する。
        TimePicker timePicker = new TimePicker(EventEditActivity.this);
        timePicker.setIs24HourView(true);

        // 初期値を設定する。
        timePicker.setCurrentHour(times[0]);
        timePicker.setCurrentMinute(times[1]);

        // ダイアログを生成し、設定する。
        AlertDialog.Builder builder = new AlertDialog.Builder(EventEditActivity.this);
        builder.setView(timePicker);
        builder.setTitle(getString(R.string.time_dialog_title));
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton(android.R.string.ok, new GdTimeLabelOnClickListener(true, timePicker));
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了時間ラベルの処理を行う。
     */
    private void gdEndTimeLabel() {
        mLogger.d("IN");

        // 終了時間を取得する。
        int[] times = splitTimeLabel(mGdEndTimeLabel.getText().toString());

        // 時間ピッカーを生成する。
        TimePicker timePicker = new TimePicker(EventEditActivity.this);
        timePicker.setIs24HourView(true);

        // 初期値を設定する。
        timePicker.setCurrentHour(times[0]);
        timePicker.setCurrentMinute(times[1]);

        // ダイアログを生成し、設定する。
        AlertDialog.Builder builder = new AlertDialog.Builder(EventEditActivity.this);
        builder.setView(timePicker);
        builder.setTitle(getString(R.string.time_dialog_title));
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton(android.R.string.ok, new GdTimeLabelOnClickListener(false, timePicker));
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();

        mLogger.d("OUT(OK)");
    }

    /**
     * 戻るボタンの処理を行う。
     */
    private void backButton() {
        finish();
    }

    /**
     * 登録ボタンの処理を行う。
     */
    private void registButton() {
        mLogger.d("IN");

        // 開始日、開始時間、終了日、終了時間を取得する。
        String startDate = mGdStartDateLabel.getText().toString();
        String startTime = mGdStartTimeLabel.getText().toString();
        String endDate   = mGdEndDateLabel.getText().toString();
        String endTime   = mGdEndTimeLabel.getText().toString();
        mLogger.d("startDate=[" + startDate + "] startTime=[" + startTime + "] endDate=[" + endDate + "] endTIme=[" + endTime + "]");

        int[] gdStartDates = splitDateLabel(startDate);
        int[] gdStartTimes = splitTimeLabel(startTime);
        int[] gdEndDates   = splitDateLabel(endDate);
        int[] gdEndTimes   = splitTimeLabel(endTime);

        // 開始日時と終了日時のチェックを行う。
        boolean check = false;

        // 年月日分繰り返す。
        int datesLopp = gdStartDates.length;
        for (int i = 0; i < datesLopp; i++) {
            // 開始が終了より大きい場合
            if (gdStartDates[i] > gdEndDates[i]) {
                // エラーメッセージを表示する。
                toast(R.string.error_start_time);

                // 終了する。
                return;

            // 開始が終了より小さい場合
            } else if (gdStartDates[i] < gdEndDates[i]) {
                // チェックOKを設定し、ループを終了する。
                check = true;
                break;
            }
        }

        // チェックNGの場合
        if (!check) {
            // 時分分繰り返す。
            int timesLoop = gdStartTimes.length;
            for (int i = 0; i < timesLoop; i++) {
                // 開始が終了より大きい場合
                if (gdStartTimes[i] > gdEndTimes[i]) {
                    // エラーメッセージを表示する。
                    toast(R.string.error_start_time);

                    // 終了する。
                    return;

                // 開始が終了より小さい場合
                } else    if (gdStartTimes[i] < gdEndTimes[i]) {
                    // チェックOKを設定し、ループを終了する。
                    check = true;
                    break;
                }
            }
        }

        // チェックNGの場合
        if (!check) {
            // エラーメッセージを表示する。
            toast(R.string.error_start_time);

            // 終了する。
            return;
        }

        ContentValues values = new ContentValues();
        values.put(EventProvider.TITLE,         mTitleValue.getText().toString());
        values.put(EventProvider.CONTENT,       mContentValue.getText().toString());
        values.put(EventProvider.GD_WHERE,      mGdWhereValue.getText().toString());
        values.put(EventProvider.GD_START_TIME, createDateString(gdStartDates, gdStartTimes));
        values.put(EventProvider.GD_END_TIME,   createDateString(gdEndDates,   gdEndTimes));

        // 新規登録の場合
        if (CommonConstants.DEFAULT_VALUE == mId) {
            // データを登録する。
            Uri uri = getContentResolver().insert(EventProvider.CONTENT_URI, values);

            // 登録できない場合
            if (null == uri) {
                // エラーメッセージを表示する。
                toast(R.string.error_insert);
            }

        // 更新の場合
        } else {
            // データを更新する。
            Uri uri = ContentUris.withAppendedId(EventProvider.CONTENT_URI, mId);
            int result = getContentResolver().update(uri, values, null, null);

            // 更新できない場合
            if (1 > result) {
                // エラーメッセージを表示する。
                toast(R.string.error_update);
            }
        }

        // メイン画面を起動する。
        Intent intent = new Intent(EventEditActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        mLogger.d("OUT(OK)");
    }

    /**
     * 日付ラベルを分割する。
     *
     * @param dateLabel 日付ラベル
     * @return 年月日に分割したint配列
     */
    private int[] splitDateLabel(String dateLabel) {
        mLogger.d("IN dateLabel=[" + dateLabel + "]");

        int[] dates = new int[3];
        dates[0] = Integer.parseInt(dateLabel.substring(0, 4));
        dates[1] = Integer.parseInt(dateLabel.substring(5, 7));
        dates[2] = Integer.parseInt(dateLabel.substring(8, 10));

        mLogger.d("OUT(OK)");
        return dates;
    }

    /**
     * 時間ラベルを分割する。
     *
     * @param timeLabel 時間ラベル
     * @return 時分に分割したint配列
     */
    private int[] splitTimeLabel(String timeLabel) {
        mLogger.d("IN dateLabel=[" + timeLabel + "]");

        int[] times = new int[2];
        times[0] = Integer.parseInt(timeLabel.substring(0, 2));
        times[1] = DateUtil.getUnitTimeOfMinutes(Integer.parseInt(timeLabel.substring(3, 5)));

        mLogger.d("OUT(OK)");
        return times;
    }

    /**
     * 日付文字列を生成する。
     *
     * @param dates 年月日のint配列
     * @param times 時分のint配列
     * @return 日付文字列
     */
    private String createDateString(int[] dates, int[] times) {
        mLogger.d("IN");

        String dateString =
                String.valueOf(           dates[0])     +
                DateUtil.paddingZeroMonth(dates[1] - 1) +
                DateUtil.paddingZero(     dates[2])     +
                DateUtil.paddingZero(     times[0])     +
                DateUtil.paddingZero(     times[1]);

        mLogger.d("OUT(OK) result=[" + dateString + "]");
        return dateString;
    }

    /**
     * 日付ラベルクリックリスナークラス
     *
     */
    private class GdDateLabelOnClickListener implements DialogInterface.OnClickListener {

        /** タイミング(true:開始/false:終了) */
        private boolean mTiming;

        /** 日付ピッカー */
        private DatePicker mDatePicker;

        /**
        * コンストラクタ
        *
        * @param timing タイミング
        */
        public GdDateLabelOnClickListener(boolean timing, DatePicker datePicker) {
            mTiming = timing;
            mDatePicker = datePicker;
        }

        /**
         * クリックした時に呼び出される。
         *
         * @param dialog ダイアログ
         * @param which クリックされたアイテム
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 開始の場合
            if (mTiming) {
                // 開始日ラベルを設定する。
                setDateLabel(mGdStartDateLabel, mDatePicker);

            // 終了の場合
            } else {
                // 終了日ラベルを設定する。
                setDateLabel(mGdEndDateLabel, mDatePicker);
            }
        }
    }

    /**
     * 時間ラベルクリックリスナークラス
     *
     */
    private class GdTimeLabelOnClickListener implements DialogInterface.OnClickListener {

        /** タイミング(true:開始/false:終了) */
        private boolean mTiming;

        /** 時間ピッカー */
        private TimePicker mTimePicker;

        /**
        * コンストラクタ
        *
        * @param timing タイミング
        * @param timePicker 時間ピッカー
        */
        public GdTimeLabelOnClickListener(boolean timing, TimePicker timePicker) {
            mTiming = timing;
            mTimePicker = timePicker;
        }

        /**
         * クリックした時に呼び出される。
         *
         * @param dialog ダイアログ
         * @param which クリックされたアイテム
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 開始の場合
            if (mTiming) {
                // 開始時ラベルを設定する。
                setTimeLabel(mGdStartTimeLabel, mTimePicker);

            // 終了の場合
            } else {
                // 終了時ラベルを設定する。
                setTimeLabel(mGdEndTimeLabel, mTimePicker);
            }
        }
    }

    /**
     * 日付ラベルを設定する。
     *
     * @param dateLabel 日付ラベル
     * @param datePicker 日付ピッカー
     */
    private void setDateLabel(TextView dateLabel, DatePicker datePicker) {
        mLogger.d("IN dateLabel=[" + dateLabel.getText().toString() + "]");

        String dateText =
                String.valueOf(datePicker.getYear())             + getResourceString(R.string.year)  +
                DateUtil.paddingZeroMonth(datePicker.getMonth()) + getResourceString(R.string.month) +
                DateUtil.paddingZero(datePicker.getDayOfMonth()) + getResourceString(R.string.date);
        mLogger.d("dateText=[" + dateText + "]");
        dateLabel.setText(dateText);

        mLogger.d("OUT(OK)");
    }

    /**
     * 時間ラベルを設定する。
     *
     * @param timeLabel 時間ラベル
     * @param timePicker 時間ピッカー
     */
    private void setTimeLabel(TextView timeLabel, TimePicker timePicker) {
        mLogger.d("IN timeLabel=[" + timeLabel.getText().toString() + "]");

        String timeText =
                DateUtil.paddingZero(timePicker.getCurrentHour()) +
                getResourceString(R.string.hour) +
                DateUtil.paddingZero(DateUtil.getUnitTimeOfMinutes(timePicker.getCurrentMinute())) +
                getResourceString(R.string.minute);
        mLogger.d("timeText=[" + timeText + "]");

        timeLabel.setText(timeText);

        mLogger.d("OUT(OK)");
    }
}
