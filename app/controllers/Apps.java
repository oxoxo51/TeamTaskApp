package controllers;

import dto.user.LoginUserDto;
import play.Logger;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
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
		if (session("userName") != null) {
			if (session("teamName") != null) {
				return redirect(routes.TaskController.displayTaskList(session("teamName")));
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
			session("userName", loginForm.get().userName);
			flash("success", "ログインしました。userName:" + loginForm.get().userName);
			return redirect(routes.TeamController.displayTeamList());
		} else {
			flash("error", "ユーザー・パスワードが正しくありません。");
			return badRequest(index.render(loginForm));
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
		flash("success", "ログアウトしました。");
		return redirect(routes.Apps.index());
	}

	/**
	 * ログインしているユーザー名を取得する.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static String getLoginUserName() {
		if (session("userName") != null) {
			return session("userName");
		} else {
			return "---";
		}
	}

	/**
	 * セッションにチーム名を保持する.
	 * @param teamName
	 */
	@Security.Authenticated(Secured.class)
	public void setSessionTeamName(String teamName) {
		Logger.info("Apps#setSessionTeamName: " + teamName);
		session("teamName", teamName);
	}

	/**
	 * セッションに保持しているチーム名を取得する.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static String getSessionTeamName() {
		Logger.info("Apps#getSessionTeamName: " + session("teamName"));
		if (session("teamName") != null) {
			return session("teamName");
		} else {
			return "---";
		}
	}



}

