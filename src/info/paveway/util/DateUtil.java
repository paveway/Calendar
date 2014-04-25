package info.paveway.util;

import info.paveway.calendar.CommonConstants;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 日付ユーティリティークラス
 *
 * @version 1.0 新規作成
 *
 */
public class DateUtil {

    /** 元号テーブル */
    private static final String GENGO_TABLE[][] = {
        {"18680908","19120729", "明治"},   //明治
        {"19120730","19261224", "大正"},   //大正
        {"19261225","19890107", "昭和"},   //昭和
        {"19890108","99991231", "平成"}};  //平成

    /**
     * コンストラクタ
     * インスタンス化させない。
     */
    private DateUtil() {
        // 何もしない。
    }

    /**
     * 和暦を返却する。
     *
     * @param cal カレンダー
     * @return 和暦
     */
    public static String getWareki(Calendar cal) {

        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day   = cal.get(Calendar.DATE);

        //1900年以前をエラーとする場合は、コメントをはずす。
        if (year < 1900) {
            return null;
        }

        //YYYYMMDDの形式に変換する
        NumberFormat frm = new DecimalFormat("00");
        String ymd = "" + year + frm.format(month) + frm.format(day);
        //暦変換テーブルをサーチする
        int i = 0;
        for (i=0; i<GENGO_TABLE.length; i++) {
            //当該西暦が開始年以上で最終年以下ならbreak
            if (ymd.compareTo(GENGO_TABLE[i][0]) >= 0 && ymd.compareTo(GENGO_TABLE[i][1]) <= 0) {
                break;
            }
        }
        //暦テーブルに該当レコードがあれば、和暦年を計算する
        if (i < GENGO_TABLE.length) {
            int jpYear = year - Integer.parseInt(GENGO_TABLE[i][0].substring(0,4)) + 1;
            //和号+和暦年を返す
            return GENGO_TABLE[i][2] + jpYear + "年" + month + "月" + day + "日";
        } else {
            return null;
        }
    }

    /**
     * 分の単位時間を取得する。
     *
     * @param minute 分
     * @return 分の単位時間
     */
    public static int getUnitTimeOfMinutes(int minute) {
        return (minute / CommonConstants.UNIT_TIME) * CommonConstants.UNIT_TIME;
    }

    /**
     * 日付文字列を分割する。
     *
     * @param date 日付文字列
     * @return 年月日時分に分割した文字配列
     */
    public static String[] splitDateString(String date) {
        if ((null != date) && !"".equals(date) && ("YYYYMMDDhhmm".length() == date.length())) {
            String[] dates = new String[5];
            dates[0] = date.substring(0, 4);
            dates[1] = DateUtil.paddingZeroMonth(Integer.parseInt(date.substring(4, 6)) - 1);
            dates[2] = DateUtil.paddingZero(Integer.parseInt(date.substring(6, 8)));
            dates[3] = DateUtil.paddingZero(Integer.parseInt(date.substring(8, 10)));
            dates[4] = DateUtil.paddingZero(DateUtil.getUnitTimeOfMinutes(Integer.parseInt(date.substring(10, 12))));
            return dates;
        } else {
            return null;
        }
    }

    public static String format(String date, List<String> suffixList) {
        StringBuilder sb = new StringBuilder();
        String[] dates = splitDateString(date);
        sb.append(dates[0]);
        sb.append(suffixList.get(0));
        sb.append(dates[1]);
        sb.append(suffixList.get(1));
        sb.append(dates[2]);
        sb.append(suffixList.get(2));
        sb.append(dates[3]);
        sb.append(suffixList.get(3));
        sb.append(dates[4]);
        sb.append(suffixList.get(4));
        return sb.toString();
    }

    /**
     * 日付文字列を生成する。
     *
     * @param cal カレンダー
     * @return 日付文字列
     */
    public static String createDateString(Calendar cal) {
        String year   = String.valueOf(cal.get(Calendar.YEAR));
        String month  = paddingZeroMonth(cal.get(Calendar.MONTH));
        String date   = paddingZero(cal.get(Calendar.DATE));
        String hour   = paddingZero(cal.get(Calendar.HOUR_OF_DAY));
        String minute = paddingZero(DateUtil.getUnitTimeOfMinutes(cal.get(Calendar.MINUTE)));
        return year + month + date + hour + minute;
    }

    /**
     * 0パディングを行う。
     *
     * @param src 元の文字列
     * @return 0パディングされた文字列
     */
    public static String paddingZero(int src) {
        String dst = String.valueOf(src);
        if (10 > src) {
            dst = "0" + dst;
        }
        return dst;
    }

    /**
     * 月の0パディングを行う。
     *
     * @param month 月
     * @return 0パディングされた文字列
     */
    public static String paddingZeroMonth(int month) {
        String monthStr = String.valueOf(month + 1);
        if (10 > (month + 1)) {
            monthStr = "0" + monthStr;
        }
        return monthStr;
    }

    /**
     * ページ位置のカレンダーを返却する。
     *
     * @param position ページ位置
     * @return ページ位置のカレンダー
     */
    public static Calendar getPageCalendar(Calendar calendar, int position) {
        Calendar cal = (Calendar)calendar.clone();

        // 現在の月とビューの位置から月位置を取得する。
        int monthPosition = cal.get(Calendar.MONTH) + position;

        // 月位置から年、月のオフセットを取得する。
        int yearOffset = monthPosition / CommonConstants.NUM_OF_MONTHS;
        int monthOffset = monthPosition % CommonConstants.NUM_OF_MONTHS;

        // カレンダーに年、月のオフセットを設定する。
        cal.add(Calendar.YEAR,  yearOffset);
        cal.set(Calendar.MONTH, monthOffset);

        // 月の最初(1日)に設定する。
        cal.set(Calendar.DATE, 1);

        return cal;
    }

    /**
     * 対象の月を取得する。
     *
     * @param 日付の位置
     * @return 対象の月
     */
    public static int getTargetMonth(Calendar calendar, int position) {
        Calendar cal = getPageCalendar(calendar, position);
        return cal.get(Calendar.MONTH);
    }

    /**
    * 日付セル用カレンダーを返却する。
    *
    * @param calendar カレンダー
    * @param position ページ位置
    * @param cellNo セル番号
    * @param startType 曜日開始タイプ
    * @return 日付セル用カレンダー
    */
    public static Calendar getDateCellCalendar(Calendar calendar, int position, int cellNo, int startType) {
        int offset;
        if (1 == startType) {
            offset = 1;

        } else {
            offset = 2;
        }
        // 対象の日付を計算する。
        Calendar cal = (Calendar)calendar.clone();

        cal.add(Calendar.MONTH, position);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, cellNo - cal.get(Calendar.DAY_OF_WEEK) + offset);

        return cal;
    }
}
