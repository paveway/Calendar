package info.paveway.calendar.oauth2;

import info.paveway.calendar.AbstractBaseActivity;
import info.paveway.calendar.R;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 認証トークン取得画面
 *
 * @version 1.0 新規作成
 */
public class GetOAuth2TokenActivity extends AbstractBaseActivity {

    /** メッセージラベル */
    private TextView mMessageLabel;

    /** 認証コード */
    private String mCode;

    /** アクセストークン */
    private String mAccessToken;

    /** リフレッシュトークン */
    private String mRefreshToken;

    /** アクセストークン有効期限 */
    private long mExpiresIn;

    /**
     * 生成した時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.get_oauth2token_activity);

        // メッセージラベルを取得する。
        mMessageLabel = (TextView)findViewById(R.id.messageLabel);

        // 認証トークンを取得する。
        getOAuth2Token();
    }

    /**
     * 認証コード取得画面の呼び出しから戻ってきた時に呼び出される。
     *
     * @param requestCode 要求コード
     * @param resultCode 結果コード
     * @param data 引継ぎデータ
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // スーパークラスのメソッドを呼び出す。
        super.onActivityResult(requestCode, resultCode, data);

        // 処理結果
        boolean result = false;

        // 要求コードが認証コード取得の場合
        if (OAuth2Constants.REQUEST_CODE_GET_OAUTH2_CODE == requestCode) {
            // 結果コードが正常の場合
            if (RESULT_OK == resultCode) {
                // 認証トークンを取得する。
                getTokens();

                // 処理結果を成功に設定する。
                result = true;
            }
        }

        // 処理結果が成功ではない場合
        // 成功な場合は認証トークン取得後、呼び出し元画面へ遷移する。
        if (!result) {
            // エラーとして呼び出し元画面へ遷移し、終了する。
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    /**
     * 認証トークンを取得する。
     */
    public void getOAuth2Token() {
        // プリフェレンスから各設定値を取得する。
        SharedPreferences sharedPreferences = getSharedPreferences(OAuth2Constants.PREF_KEY_AUTH_INFO, MODE_PRIVATE);
        mCode         = sharedPreferences.getString(OAuth2Constants.PREF_KEY_CODE,          null);
        mAccessToken  = sharedPreferences.getString(OAuth2Constants.PREF_KEY_ACCESS_TOKEN,  null);
        mRefreshToken = sharedPreferences.getString(OAuth2Constants.PREF_KEY_REFRESH_TOKEN, null);
        mExpiresIn    = sharedPreferences.getLong(  OAuth2Constants.PREF_KEY_EXPIRES_IN,    -1);

        // リフレッシュトークンがない場合
        if (StringUtil.isNullOrEmpty(mRefreshToken)) {
            // 認証コードがない場合
            if (StringUtil.isNullOrEmpty(mCode)) {
                // 認証コード取得画面へ遷移する。
                startGetOAuth2CodeActivity();

            // 認証コードがある場合
            } else {
                // 認証トークンを取得する。
                getTokens();
            }

        // 認証トークンが期限切れの場合
        } else if (Calendar.getInstance().getTimeInMillis() > mExpiresIn) {
            // 認証トークンを更新する。
            updateTokens();
        }
    }

    /**
     * 認証コード取得画面を開始する。
     */
    private void startGetOAuth2CodeActivity() {
        Intent intent = new Intent(this, GetOAuth2CodeActivity.class);
        intent.putExtra(OAuth2Constants.EXTRA_KEY_AUTH_URL, OAuth2Constants.AUTH_URL);
        startActivityForResult(intent, OAuth2Constants.REQUEST_CODE_GET_OAUTH2_CODE);
    }

    /**
     * 認証トークンを取得する。
     *
     * @return 取得結果 true:成功 / false:失敗
     */
    @SuppressWarnings("unchecked")
    private void getTokens() {
        // HTTP通信のパラメータリストを生成する。
        List<NameValuePair> entityList = new ArrayList<NameValuePair>();
        entityList.add(new BasicNameValuePair(OAuth2Constants.ENTITY_KEY_CLIENT_ID,     OAuth2Constants.CLIENT_ID));
        entityList.add(new BasicNameValuePair(OAuth2Constants.ENTITY_KEY_GRANT_TYPE,    OAuth2Constants.GRANT_TYPE_AUTHORIZATION_CODE));
        entityList.add(new BasicNameValuePair(OAuth2Constants.ENTITY_KEY_CODE,          mCode));
        entityList.add(new BasicNameValuePair(OAuth2Constants.ENTITY_KEY_REDIRECT_URI,  OAuth2Constants.REDIRECT_URI));

        // トークン取得タスクを実行する。
        new GetTokenTask().execute(entityList);
    }

    /**
     * 認証トークンを更新する。
     *
     * @return 更新結果 true:成功 / false:失敗
     */
    @SuppressWarnings("unchecked")
    private void updateTokens() {
        // HTTP通信のパラメータリストを生成する。
        List<NameValuePair> entityList = new ArrayList<NameValuePair>();
        entityList.add(new BasicNameValuePair(OAuth2Constants.ENTITY_KEY_CLIENT_ID,     OAuth2Constants.CLIENT_ID));
        entityList.add(new BasicNameValuePair(OAuth2Constants.ENTITY_KEY_GRANT_TYPE,    OAuth2Constants.GRANT_TYPE_REFRESH_TOKEN));
        entityList.add(new BasicNameValuePair(OAuth2Constants.ENTITY_KEY_REFRESH_TOKEN, mRefreshToken));

        // トークン取得タスクを実行する。
        new GetTokenTask().execute(entityList);
    }

    /**
     * 認証トークン取得タスククラス
     */
    protected class GetTokenTask extends AsyncTask<List<NameValuePair>, Void, Boolean> {

        /** ロガー */
        private Logger mLogger = new Logger(GetTokenTask.class);

        /**
         * 前処理を行う。
         * 処理中のメッセージを表示する。
         */
        @Override
        protected void onPreExecute() {
            mMessageLabel.setText("認証情報を取得しています。");
        }

        /**
         * バックグラウンド処理を行う。
         * 認証トークンの取得処理を行う。
         * ここでUIは直接の操作はできない。
         *
         * @param entityList HTTP通信のパラメータリスト
         */
        @Override
        protected Boolean doInBackground(List<NameValuePair>... entityList) {
            boolean result = false;
            HttpClient httpClient = new DefaultHttpClient();
            try {
                // 認証トークンを取得する。
                // 認証トークンはパラメータがあるためPOSTメソッドで取得する。
                HttpPost httpPost = new HttpPost(OAuth2Constants.TOKEN_URI);
                httpPost.setEntity(new UrlEncodedFormEntity(entityList[0], OAuth2Constants.ENCODING_UTF_8));
                HttpResponse httpResponse = httpClient.execute(httpPost);

                // HTTP通信結果が正常の場合
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    // 返却されたデータ文字列を取得する。
                    String httpEntityString = EntityUtils.toString(httpResponse.getEntity());
                    mLogger.d("httpEntityString=[" + httpEntityString + "]");

                    // データ文字列のJSONオブジェクトを生成する。
                    JSONObject json = new JSONObject(httpEntityString);

                    // エラー文字列が含まれる場合
                    if (json.has(OAuth2Constants.JSON_KEY_ERROR)) {
                        // 何もしない。
                        mLogger.w("error=[" + json.getString(OAuth2Constants.JSON_KEY_ERROR) + "]");

                    // エラー文字列が含まれない場合
                    } else {
                        // アクセストークンが含まれる場合
                        if (json.has(OAuth2Constants.JSON_KEY_ACCESS_TOKEN)) {
                            // アクセストークンを取得する。
                            mAccessToken = json.getString(OAuth2Constants.JSON_KEY_ACCESS_TOKEN);

                        // アクセストークンが含まれない場合
                        } else {
                            mAccessToken = null;
                        }

                        // リフレッシュトークンが含まれる場合
                        // 認証トークン更新の場合、JSON文字列にリフレッシュトークンは含まれないため、mRefreshTokenはクリアしない。
                        if (json.has(OAuth2Constants.JSON_KEY_REFRESH_TOKEN)) {
                            // リフレッシュトークンを取得する。
                            mRefreshToken = json.getString(OAuth2Constants.JSON_KEY_REFRESH_TOKEN);
                        }

                        // アクセストークン有効期限が含まれる場合
                        if (json.has(OAuth2Constants.JSON_KEY_EXPIRES_IN)) {
                            // アクセストークン有効期限を取得する。
                            String expiresIn = json.getString(OAuth2Constants.JSON_KEY_EXPIRES_IN);

                            // アクセストークン有効期限を取得できた場合
                            if (StringUtil.isNotNullOrEmpty(expiresIn)) {
                                // アクセストークン有効期限を取得する。
                                mExpiresIn = Calendar.getInstance().getTimeInMillis() + Long.parseLong(expiresIn);

                            // アクセストークン有効期限が取得できない場合
                            } else {
                                mExpiresIn = -1;
                            }

                        // アクセストークン有効期限が含まれない場合
                        } else {
                            mExpiresIn = -1;
                        }

                        // 認証情報が全て取得できた場合
                        if (StringUtil.isNotNullOrEmpty(mAccessToken) &&
                            StringUtil.isNotNullOrEmpty(mRefreshToken) &&
                            (-1 != mExpiresIn)) {
                            // プリフェレンスに保存する。
                            SharedPreferences.Editor editor = getSharedPreferences(OAuth2Constants.PREF_KEY_AUTH_INFO, MODE_PRIVATE).edit();
                            editor.putString(OAuth2Constants.PREF_KEY_ACCESS_TOKEN,  mAccessToken);
                            editor.putString(OAuth2Constants.PREF_KEY_REFRESH_TOKEN, mRefreshToken);
                            editor.putLong(OAuth2Constants.PREF_KEY_EXPIRES_IN,      mExpiresIn);
                            editor.commit();

                            // 処理成功とする。
                            result = true;
                        }
                    }

                // HTTP通信結果が正常ではない場合
                } else {
                    mLogger.w("HTTP response failed. HTTP response=[" + httpResponse.getStatusLine().getStatusCode() + "]");
                }
            } catch (Exception e) {
                mLogger.e(e);

            } finally {
                // リソースを開放する。
                httpClient.getConnectionManager().shutdown();
            }
            return result;
        }

        /**
         * 後処理を行う。
         * 呼び出し元画面へ遷移する。
         */
        @Override
        protected void onPostExecute(Boolean result) {
            // 呼び出し元画面へ遷移し、終了する。
            Intent data = new Intent();

            // 成功の場合
            if (result) {
                setResult(RESULT_OK, data);

            // エラーの場合
            } else {
                setResult(RESULT_CANCELED, data);
            }

            // 終了する。
            finish();
        }
    }
}
