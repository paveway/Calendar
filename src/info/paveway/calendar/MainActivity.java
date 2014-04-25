package info.paveway.calendar;

import info.paveway.calendar.oauth2.GetOAuth2TokenActivity;
import info.paveway.calendar.oauth2.OAuth2Constants;
import info.paveway.log.Logger;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;

/**
 * カレンダーメイン画面
 *
 * @version 1.0 新規作成
 *
 */
public class MainActivity extends AbstractBaseActivity {

    /** ロガー */
    private final Logger mLogger = new Logger(MainActivity.class);

    /** 月表示ページアダプター */
    private MonthPagerAdapter mMonthPagerAdapter;

    /** ビューページャー */
    private ViewPager mViewPager;

    /** 現在のページ */
    private int mCurrentPage = MonthPagerAdapter.START_PAGE;

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
        setContentView(R.layout.activity_main);

        // ビューページャーを取得する。
        mViewPager = (ViewPager)findViewById(R.id.viewpager);

        // ビューページャーにページ変更リスナーを設定する。
        mViewPager.setOnPageChangeListener(new ViewPagerOnPageChangeListener());

        // ページアダプターを生成する。
        mMonthPagerAdapter = new MonthPagerAdapter(MainActivity.this, mViewPager, new PageNoListener() {

            /**
            * 更新した時に呼び出される。
            *
            * @param pageNo ページ番号
            */
            @Override
            public void onUpdate(int pageNo) {
                mLogger.i("IN");

                // ページ位置を更新する。
                mViewPager.setCurrentItem(pageNo);

                mLogger.i("OUT(OK)");
            }
        });

            // ビューページャーにページアダプターを設定する。
            mViewPager.setAdapter(mMonthPagerAdapter);

        mLogger.i("OUT(OK)");
    }

    /**
     * 復帰する時に呼び出される。
     */
    @Override
    protected void onResume() {
        mLogger.i("IN");

        // 親クラスのメソッドを呼び出す。
        super.onResume();

        // ページ位置を設定する。
        mViewPager.setCurrentItem(mCurrentPage);

        mLogger.i("OUT(OK)");
    }

    /**
     * メニューを生成した時に呼び出される。
     *
     * @param menu メニュー
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * メニュー項目が選択された時に呼び出される。
     *
     * @param item メニュー項目
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;

        // メニュー項目IDにより処理を判別する。
        switch (item.getItemId()) {
        // 認証情報取得メニューの場合
        case R.id.get_auth_info_menu:
            // 認証トークン取得画面を呼び出す。
            startOAuth2TokenActivity();
            break;

        // 認証情報クリアメニューの場合
        case R.id.clear_auth_info_menu:
            clearAuthInfo();
            break;

        // 設定メニューの場合
        case R.id.settings_menu:
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, CommonConstants.REQUEST_CODE_SETTINGS);
            break;

        // バージョン情報メニューの場合
        case R.id.version_menu:
            ret = true;
            showVersionDialog();
            break;

        // 上記以外
        default:
            // スーパークラスのメソッドを呼び出す。
            ret = super.onOptionsItemSelected(item);
            break;
        }

        return ret;
    }

    /**
     * 認証トークン取得画面の呼び出しから戻った時に呼び出される。
     *
     * @param requestCode 要求コード
     * @param resultCode 結果コード
     * @param data 引継ぎデータ
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLogger.i("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onActivityResult(requestCode, resultCode, data);

        // 要求コードにより処理を判別する。
        switch (requestCode) {
        // 認証トークン取得要求の場合
        case OAuth2Constants.REQUEST_CODE_GET_OAUTH2_TOKEN:
            // 認証トークン取得結果の処理を行う。
            getOAuth2TokenResult(resultCode);
            break;

        // 設定画面の場合
        case CommonConstants.REQUEST_CODE_SETTINGS:
            // 月表示ページアダプターを更新する。
            mMonthPagerAdapter.notifyDataSetChanged();
            break;
        }

        mLogger.i("OUT(OK)");
    }

    /**
     * ビューページャーページ変更リスナークラス
     * ページが変更された時のページ番号を保存する。
     */
    private class ViewPagerOnPageChangeListener implements OnPageChangeListener {

        /** ロガー */
        private final Logger mLogger = new Logger(ViewPagerOnPageChangeListener.class);

        /**
         * ページが選択された時に呼び出される。
         *
         * @param position ページ位置
         */
        @Override
        public void onPageSelected(int position) {
            // 何もしない。
        }

        /**
         * ページがスクロールした時に呼び出される。
         *
         * @param position ページ位置
         * @param positionOffset ページオフセット
         * @param positionOffSetPixels ページオフセット(ピクセル)
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // 何もしない。
        }

        /**
         * ページスクロール状態が変更した時に呼び出される。
         *
         * @param state ステータス
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            mLogger.i("IN");

            // セッティング状態の場合
            if (state == ViewPager.SCROLL_STATE_SETTLING) {
                // ページ位置を取得する。
                mCurrentPage = mViewPager.getCurrentItem();
                mLogger.i("mCurrentPage=[" + mCurrentPage + "]");
            }

            mLogger.i("OUT(OK)");
        }
    }

    /**
     * 認証トークン取得画面を呼び出す。
     */
    private void startOAuth2TokenActivity() {
        // 認証トークン取得画面を呼び出す。
        Intent intent = new Intent(MainActivity.this, GetOAuth2TokenActivity.class);
        startActivityForResult(intent, OAuth2Constants.REQUEST_CODE_GET_OAUTH2_TOKEN);
    }

    /**
     * 認証情報をクリアする。
     */
    private void clearAuthInfo() {
        // プリフェレンスをクリアする。
        SharedPreferences sharedPreferences =
            getSharedPreferences(OAuth2Constants.PREF_KEY_AUTH_INFO, MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 認証トークン取得結果の処理を行う。
     *
     * @param resultCode 結果コード
     */
    private void getOAuth2TokenResult(int resultCode) {
        // 正常終了ではない場合
        if (RESULT_OK != resultCode) {
            // エラーメッセージを表示する。
            toast(R.string.error_get_auth_info);
        }
    }

    /**
     * バージョンダイアログを表示する。
     */
    private void showVersionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // ダイアログの各種設定を行う。
        builder.setTitle(getString(R.string.version_dialog_title));
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setMessage(
            getString(R.string.version_pre_message) +
            getVersionName() +
            getString(R.string.version_post_message));
        builder.setPositiveButton(getString(R.string.dialog_positive_button), null);

        // ダイアログを生成する。
        AlertDialog dialog = builder.create();

        // ダイアログ画面外のタップで終了しない設定を行う。
        dialog.setCanceledOnTouchOutside(false);

        // ダイアログを表示する。
        dialog.show();
    }

    /**
     * バージョン名を取得する。
     *
     * @return バージョン名
     */
    private String getVersionName() {
        String versionName = "";
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo =
                packageManager.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            versionName = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            mLogger.e(e);
        }

        return versionName;
    }
}
