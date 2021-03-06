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

	/* message_I007 */
	public static final String MSG_I007 = CONF_MSG_PREFIX + "I007";

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

	/* message_E006 */
	public static final String MSG_E006 = CONF_MSG_PREFIX + "E006";

	/* message_E007 */
	public static final String MSG_E007 = CONF_MSG_PREFIX + "E007";

	/* message_E008 */
	public static final String MSG_E008 = CONF_MSG_PREFIX + "E008";

	/* message_E009 */
	public static final String MSG_E009 = CONF_MSG_PREFIX + "E009";

	/* message_E010 */
	public static final String MSG_E010 = CONF_MSG_PREFIX + "E010";

	/* message_E011 */
	public static final String MSG_E011 = CONF_MSG_PREFIX + "E011";

	/* message_E012 */
	public static final String MSG_E012 = CONF_MSG_PREFIX + "E012";

	/* message_E013 */
	public static final String MSG_E013 = CONF_MSG_PREFIX + "E013";

	/* message_E014 */
	public static final String MSG_E014 = CONF_MSG_PREFIX + "E014";

	/* メッセージのタイプ：成功 */
	public static final String MSG_SUCCESS = "success";

	/* メッセージのタイプ：エラー */
	public static final String MSG_ERROR = "error";

	/* 項目：userName */
	public static final String ITEM_USER_NAME = "userName";

	/* 項目：teamName */
	public static final String ITEM_TEAM_NAME = "teamName";

	/* 項目：URL */
	public static final String ITEM_URL = "url";

	/* 項目：taskName */
	public static final String ITEM_TASK_NAME = "taskName";

	/* 項目：repType */
	public static final String ITEM_REP_TYPE = "repType";

	/* 項目：repetition */
	public static final String ITEM_REPETITION = "repetition";

	/* 項目：memberListStr */
	public static final String ITEM_MEMBER_LIST_STR = "memberListStr";

	/* 項目：password */
	public static final String ITEM_PASSWORD = "password";

	/* 項目：passwordAsIs */
	public static final String ITEM_PASSWORD_AS_IS = "passwordAsIs";

	/* 項目：passwordToBe */
	public static final String ITEM_PASSWORD_TO_BE = "passwordToBe";

	/* 項目：mainUserName */
	public static final String ITEM_MAIN_USER_NAME = "mainUserName";

	/* 項目：startDate */
	public static final String ITEM_START_DATE = "startDate";

	/* 項目名：ユーザー名 */
	public static final String ITEM_NAME_USER_NAME = "ユーザー名";

	/* 項目名：タスク名 */
	public static final String ITEM_NAME_TASK_NAME = "タスク名";

	/* 項目名：チーム名 */
	public static final String ITEM_NAME_TEAM_NAME = "チーム名";

	/* 項目名：パスワード */
	public static final String ITEM_NAME_PASSWORD = "パスワード";

	/* 項目名：パスワード(確認用) */
	public static final String ITEM_NAME_PASS_CONF = "パスワード(確認)";

	/* 項目名：新パスワード */
	public static final String ITEM_NAME_PASS_TO_BE = "新パスワード";

	/* 項目名：新パスワード(確認用) */
	public static final String ITEM_NAME_PASS_TO_BE_CONF = "新パスワード(確認)";

	/* ユーザー／チームのプランク表示 */
	public static final String USER_TEAM_BLANK = "---";

	/* フォーマット：yyyyMMdd */
	public static final String DATE_FORMAT_yMd = "yyyyMMdd";

	/* フォーマット：yyyy/MM/dd */
	public static final String DATE_FORMAT_yMd_SLASH = "yyyy/MM/dd";

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

	/* 実施タイプ：1回のみ */
	public static final String REPTYPE_TEMP = "T";

	/* 実施タイプ文字列：日次 */
	public static final String REPTYPE_STR_DAYLY = "日次";

	/* 実施タイプ文字列：週次 */
	public static final String REPTYPE_STR_WEEKLY = "週次";

	/* 実施タイプ文字列：月次 */
	public static final String REPTYPE_STR_MONTHLY = "月次";

	/* 実施タイプ文字列：1回のみ */
	public static final String REPTYPE_STR_TEMP = "1回のみ";

	/* 実施頻度：週次：エラーメッセージ用 */
	public static final String REP_WEEKLY_MSG_STR = "曜日(Sun~Sat)";

	/* 実施頻度：月次：エラーメッセージ用 */
	public static final String REP_MONTHLY_MSG_STR = "日付(1~31)";

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
