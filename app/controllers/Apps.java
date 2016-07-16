package controllers;

import constant.Constant;
import dto.user.LoginUserDto;
import play.Logger;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.MsgUtil;
import views.html.index;

/**
 * Created on 2016/05/08.
 */
public class Apps extends Controller {

	/**
	 * エラー発生時のflashメッセージ表示用.
	 * @param call
	 * @param flashKey
	 * @param flashMessage
	 * @return
	 */
	public static Result fail(Call call, String flashKey, String flashMessage) {
		ctx().flash().put(flashKey, flashMessage);
		return redirect(call);
	}


	/**
	 * ルートにアクセスしたときのAction.
	 * 状態により適切な画面へ遷移する.
	 * ①ログイン済みかつチーム選択済み：該当チームのタスクリスト
	 * ②ログイン済みかつチーム未選択：該当ユーザーのチームリスト
	 * ③未ログイン：ログイン画面
	 * @return
	 */
	public Result index() {
		Logger.info("Apps#index");
		if (!getLoginUserName().equals(Constant.USER_TEAM_BLANK)) {
			if (!getSessionTeamName().equals(Constant.USER_TEAM_BLANK)) {
				return redirect(routes.TaskController.displayTaskList(session(Constant.ITEM_TEAM_NAME)));
			} else {
				return redirect(routes.TeamController.displayTeamList());
			}
		} else {
			Form<LoginUserDto> loginForm = Form.form(LoginUserDto.class);
			return ok(index.render(loginForm));
		}
	}

	/**
	 * ログイン試行時のAction.
	 * @return
	 */
	public Result auth() {
		Logger.info("Apps#auth");
		Form<LoginUserDto> loginForm = Form.form(LoginUserDto.class).bindFromRequest();
		if (!loginForm.hasErrors()) {
			session().clear();
			session(Constant.ITEM_USER_NAME, loginForm.get().getUserName());
			flashSuccess(Constant.MSG_I001);
			return redirect(routes.TeamController.displayTeamList());
		} else {
			flashError(Constant.MSG_E001);
			return redirect(routes.Apps.index());
		}

	}

	/**
	 * ログアウト試行時のAction.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result logout() {
		Logger.info("Apps#logout");
		session().clear();
		flashSuccess(Constant.MSG_I002);
		return redirect(routes.Apps.index());
	}

	/**
	 * ログインしているユーザー名を取得する.
	 * 取得できなかった場合、"---"を返却する.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static String getLoginUserName() {
		Logger.info("Apps#getLoginUserName: " + session(Constant.ITEM_USER_NAME));
		return session(Constant.ITEM_USER_NAME) == null ? Constant.USER_TEAM_BLANK : session(Constant.ITEM_USER_NAME);
	}

	/**
	 * セッションにチーム名を保持する.
	 * @param teamName
	 */
	@Security.Authenticated(Secured.class)
	public void setSessionTeamName(String teamName) {
		Logger.info("Apps#setSessionTeamName: " + teamName);
		session(Constant.ITEM_TEAM_NAME, teamName);
	}

	/**
	 * セッションに保持しているチーム名を取得する.
	 * 取得できなかった場合、"---"を返却する.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static String getSessionTeamName() {
		Logger.info("Apps#getSessionTeamName: " + session(Constant.ITEM_TEAM_NAME));
		return session(Constant.ITEM_TEAM_NAME) == null ? Constant.USER_TEAM_BLANK : session(Constant.ITEM_TEAM_NAME);
	}

	/**
	 * flash:successメッセージを表示する。
	 * 引数に渡されたメッセージ定数を元にメッセージを取得し設定する。
	 * @param msgId
	 */
	public static void flashSuccess(String msgId, String... msgParam) {
		flash(Constant.MSG_SUCCESS, MsgUtil.makeMsgStr(msgId, msgParam));
	}

	/**
	 * flash:errorメッセージを表示する。
	 * 引数に渡されたメッセージ定数を元にメッセージを取得し設定する。
	 * @param msgId
	 */
	public static void flashError(String msgId, String... msgParam) {
		flash(Constant.MSG_ERROR, MsgUtil.makeMsgStr(msgId, msgParam));
	}
}

