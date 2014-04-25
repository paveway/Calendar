package info.paveway.calendar;

import info.paveway.calendar.oauth2.OAuth2Constants;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

/**
 * イベントリスト取得タスククラス
 *
 * @version 1.0 新規作成
 */
public class GetEventsListTask extends AsyncTask<String, Void, JSONObject> {

    /** ロガー */
    private Logger mLogger = new Logger(GetEventsListTask.class);

    /** データ受信リスナー */
    private ReceiveDataListener mReceiveDataListener;

    /**
     * コンストラクタ
     *
     * @param receiveDataListener データ受信リスナー
     */
    public GetEventsListTask(ReceiveDataListener receiveDataListener) {
        mReceiveDataListener = receiveDataListener;
    }

    /**
     * イベントリストデータを取得する。
     *
     * @param params パラメータ
     */
    @Override
    protected JSONObject doInBackground(String... params) {
        mLogger.i("IN");

        JSONObject jsonObject = null;

        // イベントリストデータ取得URLを生成する。
        StringBuilder url = new StringBuilder();
        url.append(OAuth2Constants.EVENTS_LIST_URL_PREFIX + params[0]);
        try {
            url.append("&timeMin=" + URLEncoder.encode(params[1], "UTF-8"));
            url.append("&timeMax=" + URLEncoder.encode(params[2], "UTF-8"));
            url.append("&orderBy=startTime&singleEvents=True");
        } catch (UnsupportedEncodingException e) {
            mLogger.e(e);
        }
        mLogger.d("url=[" + url.toString() + "]");

        HttpClient client = new DefaultHttpClient();
        try {
            // HTTP通信を行う。
            HttpResponse httpResponse = client.execute(new HttpGet(url.toString()));

            // HTTP通信結果が正常の場合
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // JSON文字列を取得する。
                String jsonString = EntityUtils.toString(httpResponse.getEntity());
                mLogger.d("jsonString=[" + jsonString + "]");

                // JSONオブジェクトを生成する。
                jsonObject = new JSONObject(jsonString);

            // HTTP通信結果が正常ではない場合
            } else {
                mLogger.w("HTTP response failed. HTTP response=[" + httpResponse.getStatusLine().getStatusCode() + "]");
            }
        } catch (Exception e) {
            mLogger.e(e);

        } finally {
            client.getConnectionManager().shutdown();
        }

        mLogger.i("OUT(OK)");
        return jsonObject;
    }

    /**
     * 後処理を行う。
     */
    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        mLogger.d("IN");

        Map<String, List<EventData>> eventDataMap = new HashMap<String, List<EventData>>();

        // イベントリストデータが取得できた場合
        if (null != jsonObject) {
            try {
                // items配列が取得する。
                JSONArray itemsArray = getJSONArray(jsonObject, "items");

                // items配列が取得できた場合
                if (null != itemsArray) {
                    // item数を取得する。
                    int itemslength = itemsArray.length();

                    // item数分繰り返す。
                    for (int i = 0; i < itemslength; i++) {
                        // itemを取得する。
                        JSONObject item = (JSONObject)itemsArray.get(i);

                        // 開始項目を取得する。
                        JSONObject startObject = getJSONObject(item, "start");

                        // 開始項目がある場合
                        if (null != startObject) {
                            // 開始日時を取得する。
                            String dateTime = getString(startObject, "dateTime");
                            mLogger.d("dateTime=[" + dateTime + "]");

                            if (StringUtil.isNullOrEmpty(dateTime)) {
                                dateTime = getString(startObject, "date");
                            }
                            mLogger.d("dateTime=[" + dateTime + "]");

                            // 開始日時がある場合
                            if (StringUtil.isNotNullOrEmpty(dateTime)) {
                                EventData eventData = new EventData();

                                // 各項目を取得し、イベントデータに設定する。
                                String id = getString(item, "id");
                                String summary = getString(item, "summary");
                                String location = getString(item, "location");
                                String startDateTime = "";
                                if (16 < dateTime.length()) {
                                    startDateTime = dateTime.substring(0, 16);
                                } else {
                                    startDateTime = dateTime;
                                }

                                eventData.setId(id);
                                eventData.setSummary(summary);
                                eventData.setLocation(location);
                                eventData.setStartDateTime(startDateTime);

                                // 検索キーを取得する。
                                String key = startDateTime.substring(0, 10);

                                // イベントデータリストを設定する。
                                List<EventData> eventDataList = null;

                                // イベントデータマップに検索キーが含まれる場合
                                if (eventDataMap.containsKey(key)) {
                                    // イベントデータマップからイベントデータリストを取得する。
                                    eventDataList = eventDataMap.get(key);

                                // イベントデータマップに検索キーが含まれない場合
                                } else {
                                    // イベントデータリストを生成する。
                                    eventDataList = new ArrayList<EventData>();
                                }

                                // イベントデータリストにイベントデータを追加する。
                                eventDataList.add(eventData);

                                // イベントデータマップにイベントデータリストを設定する。
                                eventDataMap.put(key, eventDataList);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                mLogger.e(e);
            }
        }

        // イベントデータがある場合
        if (!eventDataMap.isEmpty()) {
            // データ受信を通知する。
            mReceiveDataListener.onReceiveEventData(eventDataMap);
        }

        mLogger.d("OUT(OK)");
    }

    private JSONArray getJSONArray(JSONObject object, String key) {
        JSONArray array = null;

        try {
            array = object.getJSONArray(key);
        } catch (JSONException e) {
            //mLogger.w(e);
        } catch (Exception e) {
            mLogger.e(e);
        }

        return array;
    }

    private JSONObject getJSONObject(JSONObject parent, String key) {
        JSONObject object = null;

        try {
            object = parent.getJSONObject(key);
        } catch (JSONException e) {
            //mLogger.w(e);
        } catch (Exception e) {
            mLogger.e(e);
        }

        return object;
    }

    private String getString(JSONObject jsonObject, String key) {
        String value = "";

        try {
            value = jsonObject.getString(key);
        } catch (JSONException e) {
            //mLogger.w(e);
        } catch (Exception e) {
            mLogger.e(e);
        }

        return value;
    }
}
