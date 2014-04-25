package info.paveway.calendar.oauth2;

import java.net.URLEncoder;

/**
 * 共通定数クラス
 *
 * @version 1.0 新規作成
 */
public class OAuth2Constants {

    /**
     * コンストラクタ
     * インスタンス化させない。
     */
    private OAuth2Constants() {
        // 何もしない。
    }

    /** クライアントID */
    public static final String CLIENT_ID =
            "590901334180-0l9qe091ehtb2mjumvt80fqekvnbnss9.apps.googleusercontent.com";

    /** クライアントシークレット */
    public static final String CLIENT_SECRET = "";

    /** スコープ:Google Calendar API V2 */
    public static final String SCOPE_CALENDAR_API_V2 = URLEncoder.encode("https://www.google.com/calendar/feeds/");

    /** スコープ:Google Calendar API V3 */
    public static final String SCOPE_CALENDAR_API_V3 = URLEncoder.encode("https://www.googleapis.com/auth/calendar");

    /** カレンダー情報取得URI */
    public static final String CALENDAR_URI = "https://www.googleapis.com/calendar/v3/calendars/primary";

    /** カレンダーリストURI */
    public static final String CALENDAR_LIST_URI = "https://www.googleapis.com/calendar/v3/users/me/calendarList";

    /** イベントリスト取得URI */
    public static final String EVENTS_LIST_URI = "https://www.googleapis.com/calendar/v3/calendars/primary/events";

    /** 認証コード取得画面URI */
    public static final String AUTH_URI = "https://accounts.google.com/o/oauth2/auth";

    /** 認証トークン取得画面URI */
    public static final String TOKEN_URI = "https://accounts.google.com/o/oauth2/token";

    /** 認証コード取得画面URL */
    public static final String AUTH_URL =
        AUTH_URI +
        "?client_id=" + CLIENT_ID +
        "&response_type=code" +
        "&redirect_uri=urn:ietf:wg:oauth:2.0:oob" +
        "&scope=" + SCOPE_CALENDAR_API_V3;

    /** カレンダー情報取得URL接頭語 */
    public static final String CALENDAR_URL_PREFIX = CALENDAR_URI + "?access_token=";

    /** カレンダーリスト情報取得URL接頭語 */
    public static final String CALENDAR_LIST_URL_PREFIX = CALENDAR_LIST_URI + "?access_token=";

    /** イベントリスト取得URL接頭語 */
    public static final String EVENTS_LIST_URL_PREFIX = EVENTS_LIST_URI + "?access_token=";

    /** URLパラメータキー名:クライアントID */
    public static final String ENTITY_KEY_CLIENT_ID = "client_id";

    /** URLパラメータキー名:クライアントシークレット */
    public static final String ENTITY_KEY_CLIENT_SECRET = "client_secret";

    /** URLパラメータキー名:権限タイプ */
    public static final String ENTITY_KEY_GRANT_TYPE = "grant_type";

    /** URLパラメータキー名:認証コード */
    public static final String ENTITY_KEY_CODE = "code";

    /** URLパラメータキー名:リダイレクトURI */
    public static final String ENTITY_KEY_REDIRECT_URI = "redirect_uri";

    /** URLパラメータキー名:リフレッシュトークン */
    public static final String ENTITY_KEY_REFRESH_TOKEN = "refresh_token";

    /** URLパラメータ値:認証コード用権限タイプ */
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    /** URLパラメータ値:リフレッシュトークン用権限タイプ */
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    /** URLパラメータ値:リダイレクトURI */
    public static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    /** 設定ファイルキー名:認証情報ファイル名 */
    public static final String PREF_KEY_AUTH_INFO = "auth_info";

    /** 設定ファイルキー名:アクセストークン */
    public static final String PREF_KEY_ACCESS_TOKEN = "access_token";

    /** 設定ファイルキー名:リフレッシュトークン */
    public static final String PREF_KEY_REFRESH_TOKEN = "refresh_token";

    /** 設定ファイルキー名:有効期限 */
    public static final String PREF_KEY_EXPIRES_IN = "expires_in";

    /** 設定ファイルキー名:認証コード */
    public static final String PREF_KEY_CODE = "code";

    /** 画面間データキー名:認証画面URL */
    public static final String EXTRA_KEY_AUTH_URL = "auth_url";

    /** 画面間データキー名:アクセストークン */
    public static final String EXTRA_KEY_ACCESS_TOKEN = "access_token";

    /** 画面間データキー名:アクセストークン */
    public static final String EXTRA_KEY_REFRESH_TOKEN = "refresh_token";

    /** 画面間データキー名:有効期限 */
    public static final String EXTRA_KEY_EXPIRES_IN = "expires_in";

    /** 画面間データキー名:認証コード */
    public static final String EXTRA_KEY_CODE = "code";

    /** 画面間データキー名:結果 */
    public static final String EXTRA_KEY_RESULT = "result";

    /** JSONデータキー名:エラー */
    public static final String JSON_KEY_ERROR = "error";

    /** JSONデータキー名:アクセストークン */
    public static final String JSON_KEY_ACCESS_TOKEN = "access_token";

    /** JSONデータキー名:リフレッシュトークン */
    public static final String JSON_KEY_REFRESH_TOKEN = "refresh_token";

    /** JSONデータキー名:有効期限 */
    public static final String JSON_KEY_EXPIRES_IN = "expires_in";

    /** エンコーディング:UTF-8 */
    public static final String ENCODING_UTF_8 = "UTF-8";

    /** 要求コード:認証コード取得 */
    public static final int REQUEST_CODE_GET_OAUTH2_CODE = 0;

    /** 要求コード:認証トークン取得 */
    public static final int REQUEST_CODE_GET_OAUTH2_TOKEN = 1;
}
