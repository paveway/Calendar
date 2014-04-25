package info.paveway.external;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Sekki {

    private static final String[] SEKKI = {
        "小寒", "大寒", "立春", "雨水", "啓蟄", "春分",
        "清明", "穀雨", "立夏", "小満", "芒種", "夏至",
        "小暑", "大暑", "立秋", "処暑", "白露", "秋分",
        "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"
    };

    private Sekki() {
    }

    public static String getSekki(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        String src = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

        // 24回分計算
        for (int i = 0; i < 24; i++) {
            // 春分点からの経過年数
            double y = year + (i - 5) / 24.0;

            // 経過年数を力学時のユリウス通日 JED に変換
            double jed = Myastro.solarterm(y);

            // ΔTをもちいて世界時に変換
            double jd = jed - Myastro.deltaT(Myastro.jepoch(jed)) / (24 * 60 * 60);

            // 通算秒に直す
            long t = Mycal.jdtotime(jd);

            // 時刻を文字列に直す
            String dst = timetostr1(t);

            if (src.equals(dst)) {
                return SEKKI[i];
            }
        }

        return "";
    }

    /*
    timetostr1
    通算ミリ秒を文字列に変換
    */
    private static String timetostr1(long t)
    {
        // グレゴリオ暦の宣言
        GregorianCalendar cal = new GregorianCalendar();

        // グレゴリオ暦を全期間にわたって使う
        cal.setGregorianChange(new Date(Long.MIN_VALUE));

        cal.setTimeInMillis(t);

        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day   = cal.get(Calendar.DATE);

        String str = String.format("%04d%02d%02d", year, month, day);
        return str;
    }
}
