package constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created on 16/06/15.
 */
public class Constant {

	// privateコンストラクタでインスタンス生成を抑止する
	private Constant() {}

	/* mode:CREATE */
	public static final String MODE_CREATE = "CREATE";

	/* mode:UPDATE */
	public static final String MODE_UPDATE = "UPDATE";

	/* flag:ON */
	public static final String FLAG_ON = "1";

	/* flag:OFF */
	public static final String FLAG_OFF = "0";

	/* 実施タイプ：日次 */
	public static final String REPTYPE_DAYLY = "D";

	/* 実施タイプ：週次 */
	public static final String REPTYPE_WEEKLY = "W";

	/* 実施タイプ：月次 */
	public static final String REPTYPE_MONTHLY = "M";

	/* 実施頻度：日曜 */
	public static final String REPETITION_SUN = "Sun";

	/* 実施頻度：月曜 */
	public static final String REPETITION_MON = "Mon";

	/* 実施頻度：火曜 */
	public static final String REPETITION_TUE = "Tue";

	/* 実施頻度：水曜 */
	public static final String REPETITION_WED = "Wed";

	/* 実施頻度：木曜 */
	public static final String REPETITION_THU = "Thu";

	/* 実施頻度：金曜 */
	public static final String REPETITION_FRI= "Fri";

	/* 実施頻度：土曜 */
	public static final String REPETITION_SAT = "Sat";

	/* 実施頻度セット */
	public static final Set<String> SET_REPETITION
			= new HashSet<>(Arrays.asList(
			Constant.REPETITION_SUN,
			Constant.REPETITION_MON,
			Constant.REPETITION_TUE,
			Constant.REPETITION_WED,
			Constant.REPETITION_THU,
			Constant.REPETITION_FRI,
			Constant.REPETITION_SAT));

}
