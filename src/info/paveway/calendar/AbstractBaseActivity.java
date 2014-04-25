package info.paveway.calendar;

import info.paveway.log.Logger;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 画面の基底抽象クラス
 *
 * @version 1.0 新規作成
 *
 */
public class AbstractBaseActivity extends Activity {

    /** ロガー */
    private Logger mLogger = new Logger(AbstractBaseActivity.class);

    /** リソース */
    protected Resources mResources;

    /** コンテントリゾルバー */
    protected ContentResolver mResolver;

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

        // リソースを設定する。
        mResources = getResources();

        // コンテントリゾルバーを設定する。
        mResolver = getContentResolver();

        mLogger.i("OUT(OK)");
    }

    /**
    * リソースから文字列を取得する。
    *
    * @param id 文字列リソースID
    * @return 文字列
    */
    protected String getResourceString(int id) {
        return mResources.getString(id);
    }

    /**
    * トースト表示する。
    *
    * @param id 文字列リソースID
    */
    protected void toast(int id) {
        Toast.makeText(this, getResourceString(id), Toast.LENGTH_SHORT).show();
    }
}
