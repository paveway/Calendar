/*
  mycal / 日付に関するメソッド群
   Created : 2011-03-09
  Modified : 2011-04-03
   by Nozomi Thilogane
      http://www.geocities.jp/thilogane/
  History
   2011-03-09 : Java版を新規作成
*/

package info.paveway.external;

public class Mycal
{
	/*
	 timetojd
	 1970年1月1日0時UTCからの通算ミリ秒数(UNIX time)をユリウス日に変換
	 */
	public static double timetojd(long t){
		// 1970年1月1日0時UTCのユリウス日は 2440587.5
		// ミリ秒単位から日単位へ変換して 2440587.5 を加える
		double jd = t/(24.0*60*60*1000) + 2440587.5;

		// 結果を返す
		return(jd);
	}

	/*
	 jdtotime
	 ユリウス日を1970年1月1日0時UTCからの通算ミリ秒数(UNIX time)に変換
	 */
	public static long jdtotime(double jd){
		// 1970年1月1日0時UTCのユリウス日は 2440587.5
		// ユリウス日から 2440587.5 を減じた日単位からミリ秒単位へ変換した後 long 型に変換
		long t = (long)((jd - 2440587.5)*(24.0*60*60*1000));

		// 結果を返す
		return(t);
	}

	/*
	 ymdtojdn
	 年月日をユリウス通日に変換
	 月は 0(January)～11(December) の整数
	 */
	 public static long ymdtojdn(int year, int month, int day)
	 {
		long jdn;

		// 月は 0(January)～11(December) の整数
		month += 1;

		// 年月を月が3から14のあいだにもっていくように調整
		int y = year + (int)Math.floor((month-3)/12.0);
		int m = month - (int)Math.floor((month-3)/12.0)*12;

		// JDNを求める
		jdn =  y*365L + (long)Math.floor(y/4.0) - (long)Math.floor(y/100.0) + (long)Math.floor(y/400.0);
		jdn += (long)Math.floor(30.6*(m-3)+0.5) + day + 1721119;

		return(jdn);
	}

	/*
	 jdntoymd
	 ユリウス通日(JDN)から年月日に変換
	 算法は 長谷川一郎『天文計算入門』恒星社、1978年、62-63頁 より
	 */
	public static int[] jdntoymd(long jdn){
		int[] ymd = new int[3];
		long da, db, dc, de, df, dg, dd, dh, dm, dy;

		da = (long)Math.floor(jdn + 68569.5);
		db = (long)Math.floor(da/36524.25);
		dc = da - (long)Math.floor(36524.25*db + 0.75);
		de = (long)Math.floor((dc+1)/365.25025);
		df = dc - (long)Math.floor(365.25*de) + 31;
		dg = (long)Math.floor(df/30.59);
		dd = df - (long)Math.floor(30.59*dg);
		dh = (long)Math.floor(dg/11.0);
		dm = dg - 12*dh + 2;
		dy = 100*(db-49) + de + dh;

		if (dm == 12 && dd == 32){
			dy++; dm = 1; dd = 1;
		}

		ymd[0] = (int)dy;
		ymd[1] = (int)dm - 1; // 月は 0(January)～11(December) の整数
		ymd[2] = (int)dd;

		return(ymd);
	}
}
