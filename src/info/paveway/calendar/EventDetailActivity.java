package info.paveway.calendar;

import java.util.ArrayList;
import java.util.List;

import info.paveway.calendar.data.EventProvider;
import info.paveway.log.Logger;
import info.paveway.util.DateUtil;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 予定詳細画面
 *
 * @version 1.0 新規作成
 *
 */
public class EventDetailActivity extends AbstractBaseActivity {

    /** ロガー */
    private Logger mLogger = new Logger(EventDetailActivity.class);

    /** 件名値 */
    private TextView mTitleValue;

    /** 開始日時値 */
    private TextView mGdStartTimeValue;

    /** 終了日時値 */
    private TextView mGdEndTimeValue;

    /** 場所値 */
    private TextView mGdWhereValue;

    /** 内容 */
    private TextView mContentValue;

    /** ID */
    private int mId;

    /** 件名 */
    private String mTitle;

    /** 開始日時 */
    private String mGdStartTime;

    /** 終了日時 */
    private String mGdEndTime;

    /** 場所 */
    private String mGdWhere;

    /** 内容 */
    private String mContent;

    /**
    * 生成した時に呼び出される。
    *
    * @param savedInstanceState 保存した時のインスタンスの状態
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.i("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.schedule_detail_activity);

        // インテントを取得する。
        Intent intent = getIntent();

        // インテントが取得できた場合
        if (null != intent) {
	        // 各ビューを取得する。
	        mTitleValue       = (TextView)findViewById(R.id.detailTitleValue);
	        mGdStartTimeValue = (TextView)findViewById(R.id.detailGdStartTimeValue);
	        mGdEndTimeValue   = (TextView)findViewById(R.id.detailGdEndTimeValue);
	        mGdWhereValue     = (TextView)findViewById(R.id.detailGdWhereValue);
	        mContentValue     = (TextView)findViewById(R.id.detailContentValue);

	        // 各ボタンにリスナーを設定する。
	        ((Button)findViewById(R.id.detailBackButton)).setOnClickListener(  new OnClickListener() { public void onClick(View v) { backButton();   }});
	        ((Button)findViewById(R.id.detailDeleteButton)).setOnClickListener(new OnClickListener() { public void onClick(View v) { deleteButton(); }});
	        ((Button)findViewById(R.id.detailEditButton)).setOnClickListener(  new OnClickListener() { public void onClick(View v) { editButton();   }});

            // 付加データを取得する。
            mId          = intent.getIntExtra(   CommonConstants.EXTRA_KEY_ID, CommonConstants.DEFAULT_VALUE);
            mTitle       = intent.getStringExtra(CommonConstants.EXTRA_KEY_TITLE);
            mGdStartTime = intent.getStringExtra(CommonConstants.EXTRA_KEY_GD_START);
            mGdEndTime   = intent.getStringExtra(CommonConstants.EXTRA_KEY_GD_END);
            mGdWhere     = intent.getStringExtra(CommonConstants.EXTRA_KEY_GD_WHERE);
            mContent     = intent.getStringExtra(CommonConstants.EXTRA_KEY_CONTENT);
            mLogger.d("" +
                    "id=["         + mId          + "] " +
                    "title=["      + mTitle       + "] " +
                    "gdStatTime=[" + mGdStartTime + "] " +
                    "gdEndTime=["  + mGdEndTime   + "] " +
                    "gdWhere=["    + mGdWhere     + "] " +
                    "content=["    + mContent     + "]");

            // 開始日を日付ラベルに表示する。
            String[] dates = DateUtil.splitDateString(mGdStartTime);
            ((TextView)findViewById(R.id.detailDateLabel)).setText(
                    dates[0] + getResourceString(R.string.year) +
                    dates[1] + getResourceString(R.string.month) +
                    dates[2] + getResourceString(R.string.date));

            // 日時に付加する文字列のリストを生成する。
            List<String> suffixList = new ArrayList<String>();
            suffixList.add(mResources.getString(R.string.year));
            suffixList.add(mResources.getString(R.string.month));
            suffixList.add(mResources.getString(R.string.date));
            suffixList.add(mResources.getString(R.string.hour));
            suffixList.add(mResources.getString(R.string.minute));

            // 各ビューに表示値を設定する。
            mTitleValue.setText(      mTitle);
            mGdStartTimeValue.setText(DateUtil.format(mGdStartTime, suffixList));
            mGdEndTimeValue.setText(  DateUtil.format(mGdEndTime, suffixList));
            mGdWhereValue.setText(    mGdWhere);
            mContentValue.setText(    mContent);

        // インテントが取得できない場合
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
     * 戻るボタンの処理を行う。
     */
    private void backButton() {
        mLogger.d("IN");

        // 終了する。
        finish();

        mLogger.d("OUT(OK)");
    }

    /**
     * 削除ボタンの処理を行う。
     */
    private void deleteButton() {
        mLogger.d("IN");

        // 予定を削除する。
        String where = EventProvider.ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mId)};
        int result = mResolver.delete(EventProvider.CONTENT_URI, where, selectionArgs);

        // 削除できない場合
        if (1 != result) {
            mLogger.w("Delete error. id=[" + mId + "]");

            // エラーメッセージを表示する。
        	toast(R.string.error_delete);
        }

        // 予定一覧画面を起動する。
        Intent intent = new Intent(EventDetailActivity.this, EventListActivity.class);
        intent.putExtra(CommonConstants.EXTRA_KEY_GD_START, mGdStartTime);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        // 終了する。
        finish();

        mLogger.d("OUT(OK)");
    }

    /**
     * 編集ボタンの処理を行う。
     */
    private void editButton() {
        mLogger.d("IN");

        // 予定編集画面を起動する。
        Intent intent = new Intent(EventDetailActivity.this, EventEditActivity.class);
        intent.putExtra(CommonConstants.EXTRA_KEY_ID,       mId);
        intent.putExtra(CommonConstants.EXTRA_KEY_TITLE,    mTitle);
        intent.putExtra(CommonConstants.EXTRA_KEY_GD_START, mGdStartTime);
        intent.putExtra(CommonConstants.EXTRA_KEY_GD_END,   mGdEndTime);
        intent.putExtra(CommonConstants.EXTRA_KEY_GD_WHERE, mGdWhere);
        intent.putExtra(CommonConstants.EXTRA_KEY_CONTENT,  mContent);
        startActivity(intent);

        mLogger.d("OUT(OK)");
    }
}
