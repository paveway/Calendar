package info.paveway.calendar;

import java.util.List;
import java.util.Map;

/**
 * 受信データリスナーインターフェース
 *
 * @version 1.0 新規作成
 */
public interface ReceiveDataListener {

    /**
     * イベントデータを受信した時に呼び出される。
     *
     * @param eventDataMap イベントデータマップ
     */
    void onReceiveEventData(Map<String, List<EventData>> eventDataMap);
}
