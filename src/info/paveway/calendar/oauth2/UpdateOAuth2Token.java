package info.paveway.calendar.oauth2;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

/**
 * 認証トークン更新クラス
 */
public class UpdateOAuth2Token {

    /** ロガー */
    private Logger mLogger = new Logger(UpdateOAuth2Token.class);

    /** コンテキスト */
    private Context mContext;

    /** アクセストークン */
    private String mAccessToken;

    /** リフレッシュトークン */
    private String mRefreshToken;

    /** アクセストークン有効期限 */
    private long mExpiresIn;

    private UpdateOAuth2TokenListener mListener;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public UpdateOAuth2Token(Context context, UpdateOAuth2TokenListener listener) {
        mContext = context;
        mListener = listener;
    }

    /**
     * 認証トークンを取得する。
     */
    public void getOAuth2Token() {
        mLogger.d("IN");

        // プリフェレンスから各設定値を取得する。
        SharedPreferences sharedPreferences =
            mContext.getSharedPreferences(OAuth2Constants.PREF_KEY_AUTH_INFO, Context.MODE_PRIVATE);
        mAccessToken  = sharedPreferences.getString(OAuth2Constants.PREF_KEY_ACCESS_TOKEN,  null);
        mRefreshToken = sharedPreferences.getString(OAuth2Constants.PREF_KEY_REFRESH_TOKEN, null);
        mExpiresIn    = sharedPreferences.getLong(  OAuth2Constants.PREF_KEY_EXPIRES_IN,    -1);
        mLogger.d("mAccessToken=[" + mAccessToken + "] mRefreshToken=[" + mRefreshToken + "] mExpiresIn=[" + mExpiresIn + "]");

        // リフレッシュトークンがある場合
        if (StringUtil.isNotNullOrEmpty(mRefreshToken)) {
            // 認証トークンが期限切れの場合
            if (Calendar.getInstance().getTimeInMillis() > mExpiresIn) {
                // 認証トークンを更新する。
                updateTokens();

            // 認証トークンが期限切れではない場合
            } else {
                // 更新する。
                mListener.onUpdate();
            }
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 認証トークンを更新する。
     *
     * @return 更新結果 true:成功 / false:失敗
     */
    @SuppressWarnings("unchecked")
    private void updateTokens() {
        mLogger.d("IN");

        // HTTP通信のパラメータリストを生成する。
        List<NameValuePair> entityList = new ArrayList<NameValuePair>();
        entityList.add(new BasicNameValuePair(OAuth2Constants.ENTITY_KEY_CLIENT_ID,     OAuth2Constants.CLIENT_ID));
        entityList.add(new BasicNameValuePair(OAuth2Constants.ENTITY_KEY_GRANT_TYPE,    OAuth2Constants.GRANT_TYPE_REFRESH_TOKEN));
        entityList.add(new BasicNameValuePair(OAuth2Constants.ENTITY_KEY_REFRESH_TOKEN, mRefreshToken));

        // トークン取得タスクを実行する。
        new GetTokenTask().execute(entityList);

        mLogger.d("OUT(OK)");
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
            mLogger.d("IN");

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
                            SharedPreferences.Editor editor = mContext.getSharedPreferences(OAuth2Constants.PREF_KEY_AUTH_INFO, Context.MODE_PRIVATE).edit();
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

            mLogger.d("OUT(OK)");
            return result;
        }

        /**
         * 後処理を行う。
         * 呼び出し元画面へ遷移する。
         */
        @Override
        protected void onPostExecute(Boolean result) {
            mLogger.d("IN result=[" + result + "]");

            // アクセストークンが取得できた場合
            if (result) {
                // 更新する。
                mListener.onUpdate();
            }

            mLogger.d("OUT(OK)");
        }
    }
}
