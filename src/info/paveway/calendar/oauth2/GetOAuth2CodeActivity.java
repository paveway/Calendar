package info.paveway.calendar.oauth2;

import info.paveway.calendar.AbstractBaseActivity;
import info.paveway.calendar.R;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ViewSwitcher;

/**
 * 認証コード取得画面
 *
 * @version 1.0 新規作成
 */
public class GetOAuth2CodeActivity extends AbstractBaseActivity {

    /** ロガー */
    private Logger mLogger = new Logger(GetOAuth2CodeActivity.class);

    /** ビュースイッチャー */
    private ViewSwitcher mViewSwitcher;

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
        setContentView(R.layout.get_oauth2code_activity);

        // 各Viewを取得する。
        mViewSwitcher   = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        WebView webView = (WebView)findViewById(R.id.webView);

        // JavaScriptを有効にする。
        webView.getSettings().setJavaScriptEnabled(true);

        // WebViewを設定する。
        // (アドレスバー等の表示防止)
        webView.setWebViewClient(new WebViewClient() {

            /**
             * ページ読み込み完了時に呼び出される。
             *
             * @param webView WebView
             * @param url URL
             */
            @Override
            public void onPageFinished(WebView webView, String url) {
                // ページ読み込み完了時の処理を行う。
                pageFinished(webView, url);
            }
        });

        // 処理結果
        boolean result = false;

        // インテントを取得する。
        Intent intent = getIntent();

        // インテントを取得できた場合
        if (null != intent) {
            // 認証画面URLを取得する。
            String authUrl = intent.getStringExtra(OAuth2Constants.EXTRA_KEY_AUTH_URL);
            mLogger.d("authUrl=[" + authUrl + "]");

            // 認証画面URLを取得できた場合
            if (StringUtil.isNotNullOrEmpty(authUrl)) {
                // 認証画面をロードする。
                webView.loadUrl(authUrl);

                // 処理正常とする。
                result = true;
            }
        }

        // 認証画面がロードできない場合
        if (!result) {
            // エラーとして呼び出し元画面へ遷移し、終了する。
            Intent data = new Intent();
            setResult(Activity.RESULT_CANCELED, data);
            finish();
        }

        mLogger.i("OUT(OK");
    }

    /**
     * ページ読み込み完了時の処理を行う。
     *
     * @param webView Webビュー
     * @param url URL
     */
    private void pageFinished(WebView webView, String url) {
        // ページタイトルを取得する。
        String title = webView.getTitle();
        mLogger.d("title=[" + title + "]");

        if (StringUtil.isNotNullOrEmpty(title)) {
            // ページタイトルから認証コードを取得する。
            String code = getCode(title);
            mLogger.d("code=[" + code + "]");

            // 認証コードが取得できない場合
            if (StringUtil.isNullOrEmpty(code)) {
                // WebViewが表示されていない場合
                if (!(mViewSwitcher.getCurrentView() instanceof WebView)) {
                    // 認証画面を表示する。
                    mViewSwitcher.showNext();
                }

            // 認証コードが取得できた場合
            } else {
                // プログレス画面を表示する。
                mViewSwitcher.showPrevious();

                // プリフェレンスに保存する。
                SharedPreferences.Editor editor = getSharedPreferences(OAuth2Constants.PREF_KEY_AUTH_INFO, MODE_PRIVATE).edit();
                editor.putString(OAuth2Constants.PREF_KEY_CODE, code);
                editor.commit();

                // 呼び出し元画面へ遷移し、終了する。
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        } else {
            // WebViewが表示されていない場合
            if (!(mViewSwitcher.getCurrentView() instanceof WebView)) {
                // 認証画面を表示する。
                mViewSwitcher.showNext();
            }
        }
    }

    /**
     * 認証コードを取得する。
     * 認証成功ページのタイトル「Success code=XXXXX」から「XXXXX」の部分を切り出す。
     *
     * @param title ページタイトル
     * @return 認証コード
     */
    protected String getCode(String title) {
        String code = null;

        // 認証成功ページの場合
        int index = title.indexOf("code=");
        if (index != -1) {
            // 認証コードを切り出す。
            code = title.substring(index + "code=".length());
        }
        return code;
    }
}
