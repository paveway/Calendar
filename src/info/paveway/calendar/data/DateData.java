package info.paveway.calendar.data;

/**
 * 日付データクラス
 *
 * @version 1.0 新規作成
 *
 */
public class DateData extends AbstractBaseData {

    /** 年 */
    private int mYear;

    /** 月 */
    private int mMonth;

    /** 日 */
    private int mDay;

    /** 六曜 */
    private String mRokuyou;

    /** 祝日 */
    private String mHoliday;

    /**
     * コンストラクタ
     */
    public DateData() {
        super();
    }

    /**
     * 年を設定する。
     *
     * @param year 年
     */
    public void setYear(int year) {
        mYear = year;
    }

    /**
     * 年を返却する。
     *
     * @return 年
     */
    public int getYear() {
        return mYear;
    }

    /**
     * 月を設定する。
     *
     * @param month 月
     */
    public void setMonth(int month) {
        mMonth = month;
    }

    /**
     * 月を返却する。
     *
     * @return 月
     */
    public int getMonth() {
        return mMonth;
    }

    /**
     * 日を設定する。
     *
     * @param day 日
     */
    public void setDay(int day) {
        mDay = day;
    }

    /**
     * 日を返却する。
     *
     * @return 日
     */
    public int getDay() {
        return mDay;
    }

    /**
     * 六曜を設定する。
     *
     * @param rokuyou 六曜
     */
    public void setRokuyou(String rokuyou) {
        mRokuyou = rokuyou;
    }

    /**
     * 六曜を返却する。
     *
     * @return 六曜
     */
    public String getRokuyou() {
        return mRokuyou;
    }

    /**
     * 祝日を設定する。
     *
     * @param holiday 祝日
     */
    public void setHoliday(String holiday) {
        mHoliday = holiday;
    }

    /**
     * 祝日を返却する。
     *
     * @return 祝日
     */
    public String getHoliday() {
        return mHoliday;
    }
}
