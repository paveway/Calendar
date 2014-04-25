/*
  myastro / 太陽と月に関する位置計算メソッド群
   Created : 2005-10-20
  Modified : 2011-04-03
   by Nozomi Thilogane
      http://www.geocities.jp/thilogane/
  History
   2005-10-20 : Cにて新規作成
   2011-04-03 : Javaに移植
*/

package info.paveway.external;

public class Myastro
{
	/* Constants */

	// Pi
	static double pi = 3.14159265358979323846;

	// Sun's Radius [km]
	static double radius_of_Sun = 695500;

	// Moon's Radius [km]
	static double radius_of_Moon = 1737.4;

	// Earth's Radius [km]
	static double radius_of_Earth = 6378.137;

	// The length of 1 AU [km]
	static double AU_in_km = 149597870;

	// The verocity of light [km/s]
	static double c_lightspeed = 299792.458;

	// The epsilon to calculate solarterm or lunarterm [days]
	static double eps = 1.0e-6;
	static int n_max = 20;

	// The mean length of a solar year [days]
	static double length_solaryear = 365.2422;

	// The mean length of a lunar month [days]
	static double length_lunarmonth = 29.530589;


	// 度数法で角度を指定して sine の値を返す
	public static double dsin(double degree)
	{
		return(Math.sin(degree * pi / 180));
	}

	// 度数法で角度を指定して cosine の値を返す
	public static double dcos(double degree)
	{
		return(Math.cos(degree * pi / 180));
	}

	// arcsine の値を度数法で返す
	public static double dasin(double val)
	{
		return(Math.asin(val) * 180 / pi);
	}

	// 太陽の黄経(正規化されていない)を返す
	public static double sunLambda(double jed)
	{
		double val;
		double T;

		T = (jed - 2451545.0) / 36525.0;

		val  = 36000.7695 * T - 79.5341;
		val += 1.9147 * dcos(  35999.05 * T + 267.52 )
		     + 0.0200 * dcos(  71998.1  * T + 265.1  )
		     - 0.0048 * dcos(  35999.05 * T + 267.52 ) * T
		     + 0.0020 * dcos(  32964 * T + 158 )
		     + 0.0018 * dcos(     19 * T + 159 )
		     + 0.0018 * dcos( 445267 * T + 208 )
		     + 0.0015 * dcos(  45038 * T + 254 )
		     + 0.0013 * dcos(  22519 * T + 352 )
		     + 0.0007 * dcos(  65929 * T +  45 )
		     + 0.0007 * dcos(   3035 * T + 110 )
		     + 0.0007 * dcos(   9038 * T +  64 )
		     + 0.0006 * dcos(  33718 * T + 316 )
		     + 0.0005 * dcos(    155 * T + 118 )
		     + 0.0005 * dcos(   2281 * T + 221 )
		     + 0.0004 * dcos(  29930 * T +  48 )
		     + 0.0004 * dcos(  31557 * T + 161 );

		val += - 0.0057 + 0.0048 * dcos( 1934 * T + 145 );

		return(val);
	}

	// 太陽の黄経を 0-360度 の値で返す
	public static double sunlambda(double jed)
	{
		double val;

		val = fmod(sunLambda(jed), 360.0);

		if (val < 0.0)
			val += 360.0;

		return(val);
	}

	// 太陽の赤経を 0-360度 の値で返す
	public static double sunalpha(double jed)
	{
		double alpha, delta, lambda, beta, epsilon;
		double T;

		T = (jed - 2451545.0) / 36525.0;

		lambda = sunlambda(jed);
		beta = 0.0;
		epsilon = 23.439291 - 0.000130042*T;

		delta = dasin( dsin(beta)*dcos(epsilon) + dcos(beta)*dsin(lambda)*dsin(epsilon));
		alpha = dasin((-dsin(beta)*dsin(epsilon) + dcos(beta)*dsin(lambda)*dcos(epsilon)) / dcos(delta));

		if (90 <= lambda && lambda < 270)
			alpha = 180 - alpha;
		else if (270 <= lambda && lambda < 360)
			alpha = 360 + alpha;

		if (alpha < 0) alpha += 360;
		if (alpha > 360) alpha -= 360;

		return(alpha);
	}

	// 太陽の赤緯の値で返す
	public static double sundelta(double jed)
	{
	{
		double delta, lambda, beta, epsilon;
		double T;

		T = (jed - 2451545.0) / 36525.0;

		lambda = sunlambda(jed);
		beta = 0.0;
		epsilon = 23.439291 - 0.000130042*T;

		delta = dasin( dsin(beta)*dcos(epsilon) + dcos(beta)*dsin(lambda)*dsin(epsilon));

		return(delta);
	}

	}

	// 春分点を起点とした太陽年を返す
	public static double solaryear(double jed)
	{
		return(sunLambda(jed) / 360.0 + 2000.0);
	}

		// 太陽の地心距離を AU(天文単位) で返す
	public static double sundist(double jed)
	{
		double val;
		double T;

		T = (jed - 2451545.0) / 36525.0;

		val  = 1.000140
		     + 0.016706 * dcos(  35999.05 * T + 177.53 )
		     + 0.000139 * dcos(  71998    * T + 175 )
		     - 0.000042 * dcos(  35999.05 * T + 177.53 ) * T
		     + 0.000031 * dcos( 445267 * T + 298 )
		     + 0.000016 * dcos(  32964 * T +  68 )
		     + 0.000016 * dcos(  45038 * T + 164 )
		     + 0.000005 * dcos(  22519 * T + 233 )
		     + 0.000005 * dcos(  33718 * T + 226 );

		return(val);
	}

	// 太陽の視差を 度 の単位で返す
	public static double sunparallax(double jed)
	{
		return(dasin(1 / (sundist(jed) * AU_in_km / radius_of_Earth)));
	}

	// 太陽の視直径を 度 の単位で返す
	public static double sunsubtends(double jed)
	{
		return(2 * dasin( radius_of_Sun / (sundist(jed) * AU_in_km )));
	}

	// 月の黄経(正規化されていない)を返す
	public static double moonLambda(double jed)
	{
		double val;
		double T;

		T = (jed - 2451545.0) / 36525.0;

		val  = 481267.8809 * T + 218.3162;

		val += 6.2888 * dcos( 477198.868 * T +  44.963 )
		     + 1.2740 * dcos( 413335.35  * T +  10.74  )
		     + 0.6583 * dcos( 890534.22  * T + 145.7   )
		     + 0.2136 * dcos( 954397.74  * T + 179.93  )
		     + 0.1851 * dcos(  35999.05  * T +  87.53  )
		     + 0.1144 * dcos(  966404   * T + 276.5 )
		     + 0.0588 * dcos(   63863.5 * T + 124.2 )
		     + 0.0571 * dcos(  377336.3 * T +  13.2 )
		     + 0.0533 * dcos( 1367733.1 * T + 280.7 )
		     + 0.0458 * dcos(  854535.2 * T + 148.2 )
		     + 0.0409 * dcos(  441199.8 * T +  47.4 )
		     + 0.0347 * dcos(  445267.1 * T +  27.9 )
		     + 0.0304 * dcos(  513197.9 * T + 222.5 )
		     + 0.0154 * dcos(   75870 * T +  41 )
		     + 0.0125 * dcos( 1443603 * T +  52 )
		     + 0.0110 * dcos(  489205 * T + 142 )
		     + 0.0107 * dcos( 1303870 * T + 246 )
		     + 0.0100 * dcos( 1431597 * T + 315 )
		     + 0.0085 * dcos(  826671 * T + 111 )
		     + 0.0079 * dcos(  449334 * T + 188 )
		     + 0.0068 * dcos(  926533 * T + 323 )
		     + 0.0052 * dcos(   31932 * T + 107 )
		     + 0.0050 * dcos(  481266 * T + 205 )
		     + 0.0040 * dcos( 1331734 * T + 283 )
		     + 0.0040 * dcos( 1844932 * T +  56 )
		     + 0.0040 * dcos(     133 * T +  29 )
		     + 0.0038 * dcos( 1781068 * T +  21 )
		     + 0.0037 * dcos(  541062 * T + 259 )
		     + 0.0028 * dcos(    1934 * T + 145 )
		     + 0.0027 * dcos(  918399 * T + 182 )
		     + 0.0026 * dcos( 1379739 * T +  17 )
		     + 0.0024 * dcos(   99863 * T + 122 )
		     + 0.0023 * dcos(  922466 * T + 163 )
		     + 0.0022 * dcos(  818536 * T + 151 )
		     + 0.0021 * dcos(  990397 * T + 357 )
		     + 0.0021 * dcos(   71998 * T +  85 )
		     + 0.0021 * dcos(  341337 * T +  16 )
		     + 0.0018 * dcos(  401329 * T + 274 )
		     + 0.0016 * dcos( 1856938 * T + 152 )
		     + 0.0012 * dcos( 1267871 * T + 249 )
		     + 0.0011 * dcos( 1920802 * T + 186 )
		     + 0.0009 * dcos(  858602 * T + 129 )
		     + 0.0008 * dcos( 1403732 * T +  98 )
		     + 0.0007 * dcos(  790672 * T + 114 )
		     + 0.0007 * dcos(  405201 * T +  50 )
		     + 0.0007 * dcos(  485333 * T + 186 )
		     + 0.0007 * dcos(   27864 * T + 127 )
		     + 0.0006 * dcos(  111869 * T +  38 )
		     + 0.0006 * dcos( 2258267 * T + 156 )
		     + 0.0005 * dcos( 1908795 * T +  90 )
		     + 0.0005 * dcos( 1745069 * T +  24 )
		     + 0.0005 * dcos(  509131 * T + 242 )
		     + 0.0004 * dcos(   39871 * T + 223 )
		     + 0.0004 * dcos(   12006 * T + 187 )
		     + 0.0003 * dcos(  958465 * T + 340 )
		     + 0.0003 * dcos(  381404 * T + 354 )
		     + 0.0003 * dcos(  349472 * T + 337 )
		     + 0.0003 * dcos( 1808933 * T +  58 )
		     + 0.0003 * dcos(  549197 * T + 220 )
		     + 0.0003 * dcos(    4067 * T +  70 )
		     + 0.0003 * dcos( 2322131 * T + 191 );

		return(val);
	}

	// 月の黄経を 0-360度 の値で返す
	public static double moonlambda(double jed)
	{
		double val;

		val = fmod(moonLambda(jed), 360.0);

		if (val < 0.0)
			val += 360.0;

		return(val);
	}

	// 月の朔を基準とした Lunation の値を返す
	public static double lunation(double jed)
	{
		return((moonLambda(jed) - sunLambda(jed)) / 360.0 + 952);
	}

	// 月の離角(位相)を 0-360度 の値で返す
	public static double moonphase(double jed)
	{
		return((lunation(jed) - Math.floor(lunation(jed))) * 360.0);
	}

	// 月の黄緯を 度 の単位で返す
	public static double moonbeta(double jed)
	{
		double val;
		double T;

		T = (jed - 2451545.0) / 36525.0;

		val  = 5.1281 * dcos(  483202.019 * T +   3.273 )
		     + 0.2806 * dcos(  960400.89 * T + 138.24 )
		     + 0.2777 * dcos(    6003.15 * T +  48.31 )
		     + 0.1733 * dcos(  407332.2 * T +  52.43 )
		     + 0.0554 * dcos(  896537.4 * T + 104  )
		     + 0.0463 * dcos(   69866.7 * T + 82.5 )
		     + 0.0326 * dcos( 1373736.2 * T + 239  )
		     + 0.0172 * dcos( 1437599.8 * T + 273.2 )
		     + 0.0093 * dcos(  884531 * T + 187 )
		     + 0.0088 * dcos(  471196 * T +  87 )
		     + 0.0082 * dcos(  371333 * T +  55 )
		     + 0.0043 * dcos(  547066 * T + 217 )
		     + 0.0042 * dcos( 1850935 * T +  14 )
		     + 0.0034 * dcos(  443331 * T + 230 )
		     + 0.0025 * dcos(  860538 * T + 106 )
		     + 0.0022 * dcos(  481268 * T + 308 )
		     + 0.0022 * dcos( 1337737 * T + 241 )
		     + 0.0021 * dcos(  105866 * T +  80 )
		     + 0.0019 * dcos(  924402 * T + 141 )
		     + 0.0018 * dcos(  820668 * T + 153 )
		     + 0.0018 * dcos(  519201 * T + 181 )
		     + 0.0018 * dcos( 1449606 * T +  10 )
		     + 0.0015 * dcos(   42002 * T +  46 )
		     + 0.0015 * dcos(  928469 * T + 121 )
		     + 0.0015 * dcos(  996400 * T + 316 )
		     + 0.0014 * dcos(   29996 * T + 129 )
		     + 0.0013 * dcos(  447203 * T +   6 )
		     + 0.0013 * dcos(   37935 * T +  65 )
		     + 0.0011 * dcos( 1914799 * T +  48 )
		     + 0.0010 * dcos( 1297866 * T + 288 )
		     + 0.0009 * dcos( 1787072 * T + 340 )
		     + 0.0008 * dcos(  972407 * T + 235 )
		     + 0.0007 * dcos( 1309873 * T + 205 )
		     + 0.0006 * dcos(  559072 * T + 134 )
		     + 0.0006 * dcos( 1361730 * T + 322 )
		     + 0.0005 * dcos(  848352 * T + 190 )
		     + 0.0005 * dcos(  419339 * T + 149 )
		     + 0.0005 * dcos(  948395 * T + 222 )
		     + 0.0004 * dcos( 2328134 * T + 149 )
		     + 0.0004 * dcos( 1024264 * T + 352 )
		     + 0.0003 * dcos(  932536 * T + 282 )
		     + 0.0003 * dcos( 1409735 * T +  57 )
		     + 0.0003 * dcos( 2264270 * T + 115 )
		     + 0.0003 * dcos( 1814936 * T +  16 )
		     + 0.0003 * dcos(  335334 * T +  57 );

		return(val);
	}

	// 月の地心距離を 地球の半径を単位 として返す
	public static double moondist(double jed)
	{
		return(1 / dsin(moonparallax(jed)));
	}

	// 月の視差を 度 の単位で返す
	public static double moonparallax(double jed)
	{
		double val;
		double T;

		T = (jed - 2451545.0) / 36525.0;

		val  = 0.950725
		     + 0.051820 * dcos( 477198.868 * T + 134.963 )
		     + 0.009530 * dcos( 413335.35 * T + 100.74 )
		     + 0.007842 * dcos( 890534.22 * T + 235.7  )
		     + 0.002824 * dcos( 954397.74 * T + 269.93 )
		     + 0.000858 * dcos( 1367733.1 * T +  10.7 )
		     + 0.000531 * dcos(  854535.2 * T + 238.2 )
		     + 0.000400 * dcos(  377336.3 * T + 103.2 )
		     + 0.000319 * dcos(  441199.8 * T + 137.4 )
		     + 0.000271 * dcos(  445267 * T + 118 )
		     + 0.000263 * dcos(  513198 * T + 312 )
		     + 0.000197 * dcos(  489205 * T + 232 )
		     + 0.000173 * dcos( 1431597 * T +  45 )
		     + 0.000167 * dcos( 1303870 * T + 336 )
		     + 0.000111 * dcos(   35999 * T + 178 )
		     + 0.000103 * dcos(  826671 * T + 201 )
		     + 0.000084 * dcos(   63864 * T + 214 )
		     + 0.000083 * dcos(  926533 * T +  53 )
		     + 0.000078 * dcos( 1844932 * T + 146 )
		     + 0.000073 * dcos( 1781068 * T + 111 )
		     + 0.000064 * dcos( 1331734 * T +  13 )
		     + 0.000063 * dcos(  449334 * T + 278 )
		     + 0.000041 * dcos(  481266 * T + 295 )
		     + 0.000034 * dcos(  918399 * T + 272 )
		     + 0.000033 * dcos(  541062 * T + 349 )
		     + 0.000031 * dcos(  922466 * T + 253 )
		     + 0.000030 * dcos(   75870 * T + 131 )
		     + 0.000029 * dcos(  990397 * T +  87 )
		     + 0.000026 * dcos(  818536 * T + 241 )
		     + 0.000023 * dcos(  553069 * T + 266 )
		     + 0.000019 * dcos( 1267871 * T + 339 )
		     + 0.000013 * dcos( 1403732 * T + 188 )
		     + 0.000013 * dcos(  341337 * T + 106 )
		     + 0.000013 * dcos(  401329 * T +   4 )
		     + 0.000012 * dcos( 2258267 * T + 246 )
		     + 0.000011 * dcos( 1908795 * T + 180 )
		     + 0.000011 * dcos(  858602 * T + 219 )
		     + 0.000010 * dcos( 1745069 * T + 114 )
		     + 0.000009 * dcos(  790672 * T + 204 )
		     + 0.000007 * dcos( 2322131 * T + 281 )
		     + 0.000007 * dcos( 1808933 * T + 148 )
		     + 0.000006 * dcos(  485333 * T + 276 )
		     + 0.000006 * dcos(   99863 * T + 212 )
		     + 0.000005 * dcos(  405201 * T + 140 );

		return(val);
	}

	// 月の赤経を 0-360度 の値で返す
	public static double moonalpha(double jed)
	{
		double alpha, delta, lambda, beta, epsilon;
		double T;

		T = (jed - 2451545.0) / 36525.0;

		lambda = moonlambda(jed);
		beta = moonbeta(jed);
		epsilon = 23.439291 - 0.000130042*T;

		delta = dasin( dsin(beta)*dcos(epsilon) + dcos(beta)*dsin(lambda)*dsin(epsilon));
		alpha = dasin((-dsin(beta)*dsin(epsilon) + dcos(beta)*dsin(lambda)*dcos(epsilon)) / dcos(delta));

		if (90 <= lambda && lambda < 270)
			alpha = 180 - alpha;
		else if (270 <= lambda && lambda < 360)
			alpha = 360 + alpha;

		if (alpha < 0) alpha += 360;
		if (alpha > 360) alpha -= 360;

		return(alpha);
	}

	// 月の赤緯の値で返す
	public static double moondelta(double jed)
	{
		double delta, lambda, beta, epsilon;
		double T;

		T = (jed - 2451545.0) / 36525.0;

		lambda = moonlambda(jed);
		beta = moonbeta(jed);
		epsilon = 23.439291 - 0.000130042*T;

		delta = dasin(dsin(beta)*dcos(epsilon) + dcos(beta)*dsin(lambda)*dsin(epsilon));

		return(delta);
	}

	// 月の視直径を 度 の単位で返す
	public static double moonsubtends(double jed)
	{
		return(2 * dasin(radius_of_Moon / (moondist(jed) * radius_of_Earth)));
	}

	// 世界時 0時 の グリニッジ恒星時を返す(日の小数で) ΔT補正前の値を使う
	public static double gmst_m(double jd)
	{
		double T, val;

		T = (jd - 2451545.0) / 36525;

		val = 67310.54181 + 8640184.812866*T + 0.093104*T*T - 0.0000062*T*T*T;
		val = val / 86400;
		val = fmod(val, 1.0);

		if (val < 0.0)
			val += 1.0;

		return(val);
	}

	// グリニッジ恒星時を返す(日の小数で) ΔT補正前の値を使う
	public static double gmst(double jd)
	{
//		double T, val;
		double val;

//		T = (jd - 2451545.0) / 36525;
		val = fmod(fmod(jd+0.5, 1.0) + gmst_m(jd) + 0.5, 1);

		if (val < 0.0)
			val += 1.0;

		return(val);
	}

	// 東経 lont 度の場所の地方恒星時を返す(日の小数で) ΔT補正前の値を使う
	public static double lmst(double jd, double lont)
	{
//		double T, val;
		double val;

//		T = (jd - 2451545.0) / 36525;
		val = fmod(gmst(jd) + lont/360.0, 1.0);

		if (val < 0.0)
			val += 1.0;

		return(val);
	}

	// 均時差を返す(日の小数で) ΔT補正前の値を使う
	public static double mean_theta(double jd)
	{
		double val;

		val = gmst_m(jd) - sunalpha(jd)/360.0;

		if (val > 0.5) val -= 1.0;
		if (val < -0.5) val += 1.0;

		return(val);
	}

	// Julian Epoch が year の時の ΔT の値を返す
	public static double deltaT(double year)
	{
		double dt, f, t;
		int i;

		double deltaTtab[] = {
			121, 112, 103, 95, 88, 82, 77, 72, 68, 63, 60, 56, 53, 51, 48, 46,
			44, 42, 40, 38, 35, 33, 31, 29, 26, 24, 22, 20, 18, 16, 14, 12,
			11, 10, 9, 8, 7, 7, 7, 7, 7, 7, 8, 8, 9, 9, 9, 9, 9, 10, 10, 10,
			10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 12, 12, 12, 12, 13, 13,
			13, 14, 14, 14, 14, 15, 15, 15, 15, 15, 16, 16, 16, 16, 16, 16,
			16, 16, 15, 15, 14, 13, 13.1, 12.5, 12.2, 12, 12, 12, 12, 12, 12,
			11.9, 11.6, 11, 10.2, 9.2, 8.2, 7.1, 6.2, 5.6, 5.4, 5.3, 5.4, 5.6,
			5.9, 6.2, 6.5, 6.8, 7.1, 7.3, 7.5, 7.6, 7.7, 7.3, 6.2, 5.2, 2.7,
			1.4, -1.2, -2.8, -3.8, -4.8, -5.5, -5.3, -5.6, -5.7, -5.9, -6,
			-6.3, -6.5, -6.2, -4.7, -2.8, -0.1, 2.6, 5.3, 7.7, 10.4, 13.3, 16,
			18.2, 20.2, 21.1, 22.4, 23.5, 23.8, 24.3, 24, 23.9, 23.9, 23.7,
			24, 24.3, 25.3, 26.2, 27.3, 28.2, 29.1, 30, 30.7, 31.4, 32.2,
			33.1, 34, 35, 36.5, 38.3, 40.2, 42.2, 44.5, 46.5, 48.5, 50.5,
			52.2, 53.8, 54.9, 55.8, 56.9, 58.3, 60, 61.6, 63, 65, 66.6 };

		if ((year >= 1620) && (year <= 2000)) {
			i = (int)Math.floor((year - 1620) / 2.0);
			f = ((year - 1620) / 2) - i;
			dt = deltaTtab[i] + ((deltaTtab[i + 1] - deltaTtab[i]) * f);
		} else {
			t = (year - 2000) / 100;
			if (year < 948) {
				dt = 2177 + (497 * t) + (44.1 * t * t);
			} else {
				dt = 102 + (102 * t) + (25.3 * t * t);
				if ((year > 2000) && (year < 2100)) {
					dt += 0.37 * (year - 2100);
				}
			}
		}

	    return(dt);
	}

	// solaryear が指定された値になる時刻を JD で返す
	public static double solarterm(double sol)
	{
		double jed;
		double delta;
		int n;

		// Guess the value.
		jed = 2451625.0 + length_solaryear * (sol - 2000);
		n = 0;

		do {
			delta = (sol - solaryear(jed)) * length_solaryear;

			jed += delta; n++;
		}
		while (Math.abs(delta) > eps && n < n_max);

		return(jed);
	}

	// lunation が指定された値になる時刻を JD で返す
	public static double lunarterm(double luna)
	{
		double jed;
		double delta;
		int n;

		// Guess the value.
		jed = 2423407.0 + length_lunarmonth * luna;
		n = 0;

		do {
			delta = (luna - lunation(jed)) * length_lunarmonth;

			jed += delta; n++;
		}
		while (Math.abs(delta) > eps && n < n_max);

		return(jed);
	}

	// JD での Julian Epoch の値を返す
	public static double jepoch(double jed)
	{
		double t;

		t = (jed - 2451545.0) / 365.25 + 2000;

		return(t);
	}

	// 実数の剰余を返す
	public static double fmod(double a, double b)
	{
		return(a - Math.floor(a / b) * b);
	}
}
