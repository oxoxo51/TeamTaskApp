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

	/* message定数のPREFIX */
	public static final String CONF_MSG_PREFIX = "message.";

	/* message_I001 */
	public static final String MSG_I001 = CONF_MSG_PREFIX + "I001";

	/* message_I002 */
	public static final String MSG_I002 = CONF_MSG_PREFIX + "I002";

	/* message_I003 */
	public static final String MSG_I003 = CONF_MSG_PREFIX + "I003";

	/* message_I004 */
	public static final String MSG_I004 = CONF_MSG_PREFIX + "I004";

	/* message_I005 */
	public static final String MSG_I005 = CONF_MSG_PREFIX + "I005";

	/* message_I006 */
	public static final String MSG_I006 = CONF_MSG_PREFIX + "I006";

	/* message_E001 */
	public static final String MSG_E001 = CONF_MSG_PREFIX + "E001";

	/* message_E002 */
	public static final String MSG_E002 = CONF_MSG_PREFIX + "E002";

	/* message_E003 */
	public static final String MSG_E003 = CONF_MSG_PREFIX + "E003";

	/* message_E004 */
	public static final String MSG_E004 = CONF_MSG_PREFIX + "E004";

	/* message_E005 */
	public static final String MSG_E005 = CONF_MSG_PREFIX + "E005";

	/* メッセージのタイプ：成功 */
	public static final String MSG_SUCCESS = "success";

	/* メッセージのタイプ：エラー */
	public static final String MSG_ERROR = "error";

	/* セッション保存項目：userName */
	public static final String SESS_USER_NAME = "userName";

	/* セッション保存項目：teamName */
	public static final String SESS_TEAM_NAME = "teamName";

	/* ユーザー／チームのプランク表示 */
	public static final String USER_TEAM_BLANK = "---";

	/* mode:CREATE */
	public static final String MODE_CREATE = "CREATE";

	/* mode:UPDATE */
	public static final String MODE_UPDATE = "UPDATE";

	/* flag:ON */
	public static final String FLAG_ON = "1";

	/* flag:OFF */
	public static final String FLAG_OFF = "0";

	/* タスク実施状態：未実施 */
	public static final String TASK_NOT_YET = "0";

	/* タスク実施状態：実施済 */
	public static final String TASK_FINISHED = "1";

	/* タスク実施状態：対象外 */
	public static final String TASK_OTHER = "-1";

	/* タスク更新：→未実施 */
	public static final int TASK_UPD_NOT_YET = 0;

	/* タスク更新：→実施済 */
	public static final int TASK_UPD_FINISHED = 1;

	/* タスク更新：エラー */
	public static final int TASK_UPD_ERROR = -1;

	/* カンマ */
	public static final String COMMA = ",";

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
