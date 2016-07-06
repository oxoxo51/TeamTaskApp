package util;

import play.data.validation.ValidationError;

/**
 * Created on 16/07/06.
 */
public class MsgUtil {

	/**
	 * 引数を元に、項目に対してValidationErrorを作成し返却する.
	 * @param itemName 項目名
	 * @param msgId メッセージID
	 * @param msgParam メッセージ変数(0...N)
	 * @return
	 */
	public static ValidationError getValidationError(String itemName, String msgId, String... msgParam) {
		return new ValidationError(itemName, makeMsgStr(itemName, msgId));
	}

	/**
	 * メッセージIDとメッセージ変数を元にメッセージを作成し返却する.
	 * @param msgId
	 * @param msgParam
	 * @return
	 */
	public static String makeMsgStr(String msgId, String... msgParam) {
		String msgStr = ConfigUtil.get(msgId).orElse("");
		int i = 1;
		for (String paramStr : msgParam) {
			msgStr.replace("#" + i, paramStr);
			i++;
		}
		return msgStr;
	}
}
