package info.paveway.calendar;

import info.paveway.calendar.data.EventData;
import info.paveway.calendar.data.EventProvider;
import info.paveway.external.QReki;
import info.paveway.external.Sekki;
import info.paveway.log.Logger;
import info.paveway.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 予定一覧画面
 *
 * @version 1.0 新規作成
 *
 */
public class EventListActivity extends AbstractBaseActivity {

    /** ロガー */
    private Logger mLogger = new Logger(EventListActivity.class);

    /** カレンダー */
    private Calendar mCalendar;

    /** 日付ラベル */
    private TextView mDateLabel;

    /** 予定リスト */
    private ListView mScheduleList;

    /** データリスト */
    private List<EventData> mDataList = new ArrayList<EventData>();

    /**
     * 生成する時に呼び出される。
     *
     * @param savedInstanceState 保存時のインスタンスの状態
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.schedule_list_activity);

        // インテントを取得する。
        Intent intent = getIntent();

        // インテントが取得できた場合
        if (null != intent) {
            // 付加データを取得する。
            String[] dates = DateUtil.splitDateString(intent.getStringExtra(CommonConstants.EXTRA_KEY_GD_START));

            // 付加データが取得できた場合
            if (null != dates) {
                // 各ビューを設定する。
                mDateLabel    = (TextView)findViewById(R.id.listDateLabel);
                mScheduleList = (ListView)findViewById(R.id.scheduleList);

                // カレンダーを生成する。
                mCalendar = new GregorianCalendar();
                mCalendar.set(Calendar.YEAR,  Integer.parseInt(dates[0]));
                mCalendar.set(Calendar.MONTH, Integer.parseInt(dates[1]) - 1);
                mCalendar.set(Calendar.DATE,  Integer.parseInt(dates[2]));

                // 日付ラベルを設定する。
                setDateLabel(0);

                // データリストを取得する。
                getDataList(dates);

                // 予定リストを設定する。
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getTitles());
                mScheduleList.setAdapter(adapter);

                // イベントリスナーを設定する。
                ((Button)findViewById(R.id.listBackButton)).setOnClickListener(
                        new OnClickListener() { public void onClick(View v) { backButton();        }});
                ((Button)findViewById(R.id.infoButton)).setOnClickListener(
                        new OnClickListener() { public void onClick(View v) { infoButton();        }});
                ((Button)findViewById(R.id.prevDateButton)).setOnClickListener(
                        new OnClickListener() { public void onClick(View v) { prevDateButton();    }});
                ((Button)findViewById(R.id.nextDateButton)).setOnClickListener(
                        new OnClickListener() { public void onClick(View v) { nextDateButton();    }});
                ((Button)findViewById(R.id.newScheduleButton)).setOnClickListener(
                        new OnClickListener() { public void onClick(View v) { newScheduleButton(); }});
                mScheduleList.setOnItemClickListener(
                        new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                startScheduleDetailActivity(position); }});

            // 付加データが取得できない場合
            } else {
                // エラーを表示する。
                toast(R.string.error_failed);

                // 終了する。
                finish();
            }

        // インテントが取得できない場合
        } else {
            // エラーを表示する。
            toast(R.string.error_failed);

            // 終了する。
            finish();
        }
    }

    /**
     * 日付ラベルを設定する。
     *
     * @param offset オフセット
     */
    private void setDateLabel(int offset) {
        mCalendar.set(Calendar.DATE, mCalendar.get(Calendar.DATE) + offset);
        mDateLabel.setText(
                String.valueOf(           mCalendar.get(Calendar.YEAR))  + getResourceString(R.string.year)  +
                DateUtil.paddingZeroMonth(mCalendar.get(Calendar.MONTH)) + getResourceString(R.string.month) +
                DateUtil.paddingZero(     mCalendar.get(Calendar.DATE))  + getResourceString(R.string.date));
    }

    /**
     * データリストを取得する。
     *
     * @param year 年
     * @param month 月
     * @param date 日
     * @return データリスト
     */
    private void getDataList(String[] dates) {
        // データリストをクリアする。
        mDataList.clear();

        // カーソルを取得する。
        String selection = EventProvider.GD_START_TIME + " LIKE ?";
        String[] selectionArgs =
                new String[]{dates[0] + dates[1] + dates[2] + "%"};
        Cursor c =
                getContentResolver().query(
                        EventProvider.CONTENT_URI, null, selection, selectionArgs, null);
        try {
            // カーソルが取得できた場合
            if (null != c) {
                // カーソルを先頭に移動できた場合
                if (c.moveToFirst()) {
                    // データ数分繰り返す。
                    do {
                        // データを設定する。
                        EventData data = new EventData();

                        data.setId(         c.getInt(   c.getColumnIndex(EventProvider.ID)));
                        data.setTitle(      c.getString(c.getColumnIndex(EventProvider.TITLE)));
                        data.setGdStartTime(c.getString(c.getColumnIndex(EventProvider.GD_START_TIME)));
                        data.setGdEndTime(  c.getString(c.getColumnIndex(EventProvider.GD_END_TIME)));
                        data.setContent(    c.getString(c.getColumnIndex(EventProvider.CONTENT)));
                        data.setGdWhere(    c.getString(c.getColumnIndex(EventProvider.GD_WHERE)));

                        mDataList.add(data);
                    } while (c.moveToNext());
                }
            }
        } finally {
            if (null != c) {
                c.close();
            }
        }
    }

    /**
     * 件名配列を取得する。
     *
     * @return 件名配列
     */
    private String[] getTitles() {
        int size = mDataList.size();
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = mDataList.get(i).getTitle();
        }
        return names;
    }

    /**
     * 戻るボタンの処理を行う。
     */
    private void backButton() {
        // メイン画面を起動する。
        Intent intent = new Intent(EventListActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 情報ボタンの処理を行う。
     */
    private void infoButton() {
        // 情報ダイアログを表示する。
        showInfoDialog();
    }

    /**
     * 前日ボタンの処理を行う。
     */
    private void prevDateButton() {
        // 日付ラベルを再設定する。
        setDateLabel(-1);

        String[] dates = new String[3];
        dates[0] = String.valueOf(mCalendar.get(Calendar.YEAR));
        dates[1] = DateUtil.paddingZeroMonth(mCalendar.get(Calendar.MONTH));
        dates[2] = DateUtil.paddingZero(mCalendar.get(Calendar.DATE));

        // データリストを取得する。
        getDataList(dates);

        // スケジュールリストを設定する。
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(EventListActivity.this, android.R.layout.simple_list_item_1, getTitles());
        mScheduleList.setAdapter(adapter);
    }

    /**
     * 翌日ボタンの処理を行う。
     */
    private void nextDateButton() {
        // 日付ラベルを再設定する。
        setDateLabel(1);

        String[] dates = new String[3];
        dates[0] = String.valueOf(mCalendar.get(Calendar.YEAR));
        dates[1] = DateUtil.paddingZeroMonth(mCalendar.get(Calendar.MONTH));
        dates[2] = DateUtil.paddingZero(mCalendar.get(Calendar.DATE));

        // データリストを取得する。
        getDataList(dates);

        // スケジュールリストを設定する。
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(EventListActivity.this, android.R.layout.simple_list_item_1, getTitles());
        mScheduleList.setAdapter(adapter);
    }

    /**
     * 新規登録ボタンの処理を行う。
     */
    private void newScheduleButton() {
        Intent intent = new Intent(EventListActivity.this, EventEditActivity.class);

        intent.putExtra(CommonConstants.EXTRA_KEY_ID,       CommonConstants.DEFAULT_VALUE);
        intent.putExtra(CommonConstants.EXTRA_KEY_GD_START, DateUtil.createDateString(mCalendar));
        mLogger.d("gdStart=[" + DateUtil.createDateString(mCalendar) + "]");

        Calendar cal = (Calendar)mCalendar.clone();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        intent.putExtra(CommonConstants.EXTRA_KEY_GD_END,   DateUtil.createDateString(cal));

        startActivity(intent);
    }

    /**
     * 情報ダイアログを表示する。
     */
    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EventListActivity.this);

        // ダイアログの各種設定を行う。
        builder.setTitle(getResourceString(R.string.info_dialog_title));
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setMessage(createMessage());
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
    private String createMessage() {
        // 各情報を取得する。
        String seireki  = getResourceString(R.string.seireki_prefix) +
                new SimpleDateFormat(getResourceString(R.string.seireki_format)).format(mCalendar.getTime());
        String wareki   = getResourceString(R.string.wareki_prefix)  + DateUtil.getWareki(mCalendar);
        String kyureki  = getResourceString(R.string.kyureki_prefix) + QReki.getKyureki(mCalendar);
        String nissu    = getResourceString(R.string.nissu_prefix)   + mCalendar.get(Calendar.DAY_OF_YEAR);
        String sekki    = Sekki.getSekki(mCalendar);
        sekki = (!"".equals(sekki) ? (getResourceString(R.string.sekki_prefix) + sekki) : "");

        return  seireki + "\n" + wareki + "\n" + kyureki + "\n" + nissu + "\n" + sekki;
    }

    /**
     * 予定詳細画面を開始する。
     *
     * @param position リスト位置
     */
    private void startScheduleDetailActivity(int position) {
        // リスト位置のデータを取得する。
        EventData data = mDataList.get(position);

        // 予定詳細画面を起動する。
        Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);

        intent.putExtra(CommonConstants.EXTRA_KEY_ID,       data.getId());
        intent.putExtra(CommonConstants.EXTRA_KEY_TITLE,    data.getTitle());
        intent.putExtra(CommonConstants.EXTRA_KEY_GD_START, data.getGdStartTime());
        intent.putExtra(CommonConstants.EXTRA_KEY_GD_END,   data.getGdEndTime());
        intent.putExtra(CommonConstants.EXTRA_KEY_GD_WHERE, data.getGdWhere());
        intent.putExtra(CommonConstants.EXTRA_KEY_CONTENT,  data.getContent());

        startActivity(intent);
    }
}
