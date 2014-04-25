package info.paveway.calendar.data;

/**
 * イベントデータクラス
 *
 * @version 1.0 新規作成
 *
 */
public class EventData extends AbstractBaseData {

    /** 件名 */
    private String mTitle;

    /** 開始時間 */
    private String mGdStartTime;

    /** 終了時間 */
    private String mGdEndTime;

    /** 終日フラグ */
    private boolean mAllDay;

    /** 場所 */
    private String mGdWhere;

    /** 内容 */
    private String mContent;

    /**
     * コンストラクタ
     */
    public EventData() {
        super();
    }

    /**
     * 件名を設定する。
     *
     * @param title 件名
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * 件名を返却する。
     *
     * @return 件名
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * 開始時間を設定する。
     *
     * @param gdStartTime 開始時間
     */
    public void setGdStartTime(String gdStartTime) {
        mGdStartTime = gdStartTime;
    }

    /**
     * 開始時間を返却する。
     *
     * @return 開始時間
     */
    public String getGdStartTime() {
        return mGdStartTime;
    }

    /**
     * 終了時間を設定する。
     *
     * @param gdEndTime 終了時間
     */
    public void setGdEndTime(String gdEndTime) {
        mGdEndTime = gdEndTime;
    }

    /**
     * 終了時間を返却する。
     *
     * @return 終了時間
     */
    public String getGdEndTime() {
        return mGdEndTime;
    }

    /**
     * 終日フラグを設定する。
     *
     * @param allDay 終日フラグ(0:false/0以外:true)
     */
    public void setAllDay(int allDay) {
    	if (0 == allDay) {
    		mAllDay = false;
    	} else {
    		mAllDay = true;
    	}
    }

    /**
     * 終日フラグを設定する。
     *
     * @param allDay 終日フラグ
     */
    public void setAllDay(boolean allDay) {
    	mAllDay = allDay;
    }

    /**
     * 終日フラグを返却する。
     *
     * @return 終日フラグ
     */
    public boolean getAllDay() {
    	return mAllDay;
    }

    /**
     * 終日フラグを返却する。
     *
     * @return 終日フラグ(0:終日ではない/1:終日)
     */
    public int getAllDayInt() {
    	return mAllDay ? 1 : 0;
    }

    /**
     * 場所を設定する。
     *
     * @param gdWhere 場所
     */
    public void setGdWhere(String gdWhere) {
        mGdWhere = gdWhere;
    }

    /**
     * 場所を返却する。
     *
     * @return 場所
     */
    public String getGdWhere() {
        return mGdWhere;
    }

    /**
     * 内容を設定する。
     *
     * @param content 内容
     */
    public void setContent(String content) {
        mContent = content;
    }

    /**
     * 内容を返却する。
     *
     * @return 内容
     */
    public String getContent() {
        return mContent;
    }
}
