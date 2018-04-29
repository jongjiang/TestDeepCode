import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.chrono.MinguoDate;
import java.time.temporal.Temporal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.iisigroup.ude.pds.annotation.FunctionCode;
import com.iisigroup.ude.testkit.UtilityClassAssertion;
import com.iisigroup.ude.util.lang.UdeRegexUtils;

/**
 * 民國年(YYY)格式工具程式.
 */
@UtilityClassAssertion
public final class YYYUtils {
    //================================================
    //== [Enumeration types] Block Start
    //== [Enumeration types] Block End
    //================================================
    //== [static variables] Block Start
    //== [static variables] Block Stop
    //================================================
    //== [instance variables] Block Start
    //== [instance variables] Block Stop
    //================================================
    //== [static Constructor] Block Start
    //== [static Constructor] Block Stop
    //================================================
    //== [Constructors] Block Start (含init method)

    protected YYYUtils() {
        throw new AssertionError();
    }

    //== [Constructors] Block Stop
    //================================================
    //== [Static Method] Block Start

    //####################################################################
    //## [Method] sub-block : 01 - Get Information
    //####################################################################

    public static int getYyy(final String yyy) {
        innerCheckYear(yyy);
        return NumberUtils.toInt(yyy);
    }

    private static void innerCheckYear(final String yyy) {
        if (!UdeRegexUtils.matches(yyy, "-?\\d+") || UdeRegexUtils.matches(yyy, "0+")) {
            throw new IllegalArgumentException("輸入民國年份格式有誤:" + yyy);
        }
    }

    public static int getTotalDays(final String yyy) {
        final int rocYear = getYyy(yyy);
        final MinguoDate minguoDate = MinguoDate.of(rocYear > 0 ? rocYear : rocYear - 1, 1, 1);
        return minguoDate.lengthOfYear();
    }

    /**
     * 計算含括年數，頭尾都算。
     * 所以年份相同得1.
     *
     * @return
     */
    public static int countYears(final String y1, final String y2) {
        final int years = getYyy(y2) - getYyy(y1);
        return years >= 0 ? years + 1 : years - 1;
    }

    //####################################################################
    //## [Method] sub-block : 03 : JAVA Date 物件轉換
    //####################################################################

    /** 將 Date 轉 YYY 字串 */
    public static String format(final Date date) {
        return format(date, null);
    }

    /** 將 temporal 轉 YYY 字串 */
    public static String format(final Temporal temporal) {
        return format(temporal, null);
    }

    @FunctionCode("JAVA Date 物件轉換")
    public static String format(final Date date, final String overflowText) {
        if (date == null) {
            return "";
        }
        final String format = RocDateUtils.format(date, "twy");
        if (format.length() != 3) {
            if (overflowText != null) {
                return overflowText;
            } else {
                throw new IllegalArgumentException("輸入日期超過民國年月可轉換區間:" + date);
            }
        }
        return format;
    }

    public static String format(final Temporal temporal, final String overflowText) {
        if (temporal == null) {
            return "";
        }
        final String format = RocDateUtils.format(temporal, "twy");
        if (format.length() != 3) {
            if (overflowText != null) {
                return overflowText;
            } else {
                throw new IllegalArgumentException("輸入日期超過民國年月可轉換區間:" + temporal);
            }
        }
        return format;
    }

    public static String format(final int rocYear) {
        if (rocYear >= 1000) {
            throw new IllegalArgumentException("三位表示最多至民國999年");
        } else if (rocYear >= 100) {
            return String.valueOf(rocYear);
        } else if (rocYear >= 10) {
            return "0" + rocYear;
        } else if (rocYear >= 1) {
            return "00" + rocYear;
        } else if (rocYear == 0) {
            throw new IllegalArgumentException("民國無0年");
        } else if (rocYear < -99) {
            throw new IllegalArgumentException("三位表示最多至民前99年");
        } else {
            final NumberFormat nf = new DecimalFormat("00");
            return "-" + nf.format(0 - rocYear);
        }
    }
    //####################################################################
    //## [Static Method] sub-block : 04 日期文字格式轉換 :: AD_FORMAT
    //####################################################################

    /**
     * 轉換為西元年份格式.
     */
    public static String toAdFormat(final String yyy) {
        return InnerUtils.directly(yyy, YYYUtils::innerToAdFormat);
    }

    public static String toAdFormatOrElse(final String yyy, final String alterText) {
        return InnerUtils.orElse(yyy, YYYUtils::innerToAdFormat, alterText);
    }

    public static String toAdFormatOrOrigin(final String yyy) {
        return InnerUtils.orElse(yyy, YYYUtils::innerToAdFormat);
    }

    /** 內部函式. */
    private static String innerToAdFormat(final String yyy) {
        final int rocYear = getYyy(yyy);
        final int adYear = toAdYear(rocYear);
        return String.valueOf(adYear);
    }

    //####################################################################
    //## [Method] sub-block : 05 : 與現在比較年度
    //####################################################################

    /**
     * 計算西元年度.
     *
     * @throws IllegalArgumentException 民國0年為未定義值。
     */
    public static int toAdYear(final int rocYear) {
        if (rocYear == 0) {
            throw new IllegalArgumentException("輸入民國年定義有誤:0");
        }
        return rocYear > 0 ? rocYear + 1911 : rocYear + 1912;
    }

    public static boolean isCurrentYear(final String rocYear) {
        innerCheckYear(rocYear);
        return isCurrentYear(NumberUtils.toInt(rocYear));
    }

    public static boolean isCurrentYear(final int rocYear) {
        return toAdYear(rocYear) == Now.localDate().getYear();
    }

    //####################################################################
    //## [Method] sub-block : 05 : Before
    //####################################################################

    /**
     * @param rocYear
     * @return true 過去年度 false 本年度 / 未來年度
     */
    public static boolean isBeforeCurrentYear(final String rocYear) {
        innerCheckYear(rocYear);
        return isBeforeCurrentYear(false, NumberUtils.toInt(rocYear));
    }

    public static boolean isBeforeCurrentYear(final boolean incldue, final String rocYear) {
        innerCheckYear(rocYear);
        return isBeforeCurrentYear(incldue, NumberUtils.toInt(rocYear));
    }

    public static boolean isBeforeCurrentYear(final boolean incldue, final int rocYear) {
        if (incldue) {
            return toAdYear(rocYear) <= Now.localDate().getYear();
        } else {
            return toAdYear(rocYear) < Now.localDate().getYear();
        }
    }

    //####################################################################
    //## [Method] sub-block : 05 : After
    //####################################################################

    public static boolean isAfterCurrentYear(final String rocYear) {
        innerCheckYear(rocYear);
        return isAfterCurrentYear(false, NumberUtils.toInt(rocYear));
    }

    public static boolean isAfterCurrentYear(final boolean incldue, final String rocYear) {
        innerCheckYear(rocYear);
        return isAfterCurrentYear(incldue, NumberUtils.toInt(rocYear));
    }

    public static boolean isAfterCurrentYear(final boolean incldue, final int rocYear) {
        if (incldue) {
            return toAdYear(rocYear) >= Now.localDate().getYear();
        } else {
            return toAdYear(rocYear) > Now.localDate().getYear();
        }
    }

    /**
     * 是否傳入的第一個年份比第二個年份早.
     *
     * @return true, if is beforeAnotherMonth
     */
    public static boolean isOrderly(final boolean allowOverlap, final String aYear, final String bYear) {
        if (StringUtils.isBlank(aYear) || StringUtils.isBlank(bYear)) {
            throw new IllegalArgumentException("輸入比較年月不應含空值(" + aYear + "," + bYear + ")");
        }
        final int d1 = getYyy(aYear);
        final int d2 = getYyy(bYear);
        if (d1 == d2) {
            if (allowOverlap) {
                return true;
            } else {
                return false;
            }
        }
        return d1 < d2;
    }

    //####################################################################
    //## [Static Method] sub-block : 以 YYY 格式取得關連日期資料 (String). 現在
    //####################################################################

    /** 取得現在時間的YYYMM格式字串. */
    public static String now() {
        return format(Now.localDateTime());
    }

    //== [Static Method] Block Stop
    //================================================
    //== [Accessor] Block Start
    //== [Accessor] Block Stop
    //================================================
    //== [Overrided Method] Block Start (Ex. toString/equals+hashCode)
    //== [Overrided Method] Block Stop
    //================================================
    //== [Method] Block Start
    //####################################################################
    //## [Method] sub-block :
    //####################################################################
    //== [Method] Block Stop
    //================================================

}
