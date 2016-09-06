package controllers;

import com.google.inject.Inject;
import constant.Constant;
import dto.user.LoginUserDto;
import models.TaskMst;
import models.TaskTrn;
import models.Team;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.TaskService;
import services.TeamService;
import services.UserService;
import services.implement.UserServiceImpl;
import util.DateUtil;
import util.MsgUtil;
import views.html.index;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created on 2016/05/08.
 */
public class Apps extends Controller {

	@Inject
	public UserService uService;
	@Inject
	public TeamService teService;
	@Inject
	public TaskService taService;


	/**
	 * エラー発生時のflashメッセージ表示用.
	 *
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
	 * ①、②は遷移先で分岐
	 *
	 * @return
	 */
	public Result index() {
		Logger.info("Apps#index");
		if (!getLoginUserName().equals(Constant.USER_TEAM_BLANK)) {
			return redirect(routes.TaskController.displayTaskList());
		} else {
			Form<LoginUserDto> loginForm = Form.form(LoginUserDto.class);
			return ok(index.render(loginForm));
		}
	}

	/**
	 * ログイン試行時のAction.
	 *
	 * @return
	 */
	public Result auth() {
		Logger.info("Apps#auth");
		Form<LoginUserDto> loginForm = Form.form(LoginUserDto.class).bindFromRequest();
		if (!loginForm.hasErrors()) {
			login(
					uService.findUserByName(
							loginForm.get().getUserName()
					).get(0)
			);
			flashSuccess(Constant.MSG_I001);
		} else {
			flashError(Constant.MSG_E001);
		}
		return redirect(routes.Apps.index());
	}

	/**
	 * ログアウト試行時のAction.
	 *
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
	 * セッションに保持しているチーム名をクリアする.
	 *
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result clearSessionTeamName() {
		Logger.info("Apps#clearSessionTeamName: " + session(Constant.ITEM_TEAM_NAME));
		session().remove(Constant.ITEM_TEAM_NAME);
		return redirect(routes.Apps.index());
	}

	/**
	 * ログインに伴うデータセットを行う.
	 */
	protected void login(User user) {
		Logger.info("Apps#login");
		session().clear();
		session(Constant.ITEM_USER_NAME, user.userName);

		// 最終ログインから当日までのタスクトランを作成する
		chkAndCreateTaskTrn(user);

		// 最終ログイン日付更新
		uService.updateLastLoginDate(user);

	}

	/**
	 * ログインしているユーザー名を取得する.
	 * 取得できなかった場合、"---"を返却する.
	 *
	 * @return
	 */
	public static String getLoginUserName() {
		Logger.info("Apps#getLoginUserName: " + session(Constant.ITEM_USER_NAME));
		return session(Constant.ITEM_USER_NAME) == null ? Constant.USER_TEAM_BLANK : session(Constant.ITEM_USER_NAME);
	}

	/**
	 * ログインしているユーザーを取得する.
	 * @return
	 */
	protected static User getLoginUser() {
		Logger.info("Apps#getLoginUser");

		UserServiceImpl userService = new UserServiceImpl();
		return userService.findUserByName(getLoginUserName()).get(0);
	}

	/**
	 * セッションにチーム名を保持する.
	 *
	 * @param teamName
	 */
	protected void setSessionTeamName(String teamName) {
		Logger.info("Apps#setSessionTeamName: " + teamName);
		session(Constant.ITEM_TEAM_NAME, teamName);
	}

	/**
	 * セッションに保持しているチーム名を取得する.
	 * 取得できなかった場合、"---"を返却する.
	 *
	 * @return
	 */
	public static String getSessionTeamName() {
		Logger.info("Apps#getSessionTeamName: " + session(Constant.ITEM_TEAM_NAME));
		return session(Constant.ITEM_TEAM_NAME) == null ? Constant.USER_TEAM_BLANK : session(Constant.ITEM_TEAM_NAME);
	}

	/**
	 * セッションにURLを保持する.
	 *
	 * @param url
	 */
	protected void setSessionUrl(String url) {
		Logger.info("Apps#setSessionUrl: " + url);
		session().remove(Constant.ITEM_URL);
		session(Constant.ITEM_URL, url);
	}

	/**
	 * flash:successメッセージを表示する。
	 * 引数に渡されたメッセージ定数を元にメッセージを取得し設定する。
	 *
	 * @param msgId
	 */
	public static void flashSuccess(String msgId, String... msgParam) {
		flash(Constant.MSG_SUCCESS, MsgUtil.makeMsgStr(msgId, msgParam));
	}

	/**
	 * flash:errorメッセージを表示する。
	 * 引数に渡されたメッセージ定数を元にメッセージを取得し設定する。
	 *
	 * @param msgId
	 */
	public static void flashError(String msgId, String... msgParam) {
		flash(Constant.MSG_ERROR, MsgUtil.makeMsgStr(msgId, msgParam));
	}

	/**
	 * ログインユーザの最終ログイン日付を確認し、最終ログインから当日までの
	 * タスクトランを作成する.
	 * @param user
	 */
	protected void chkAndCreateTaskTrn(User user) {
		Logger.info("Apps#chkAndCreateTaskTrn " + user.userName);

		// ログインユーザの所属チーム、最終ログイン日付を取得
		List<Team> teamList = teService.findTeamListByUser(user);
		Date lastLoginDate = user.lastLoginDate;
		try {
			Date today = DateUtil.getDateWithoutTime(new Date());
			if (lastLoginDate != null
					&& today != DateUtil.getDateWithoutTime(lastLoginDate)) {
				// タスクトラン作成日付:最終ログイン日付の翌日から
				Date taskDate = DateUtil.getDateWithoutTime(
						DateUtil.getThatDate(lastLoginDate, 1));
				// 当日分まで繰り返し作成
				while (taskDate.compareTo(today) < 1) {
					for (Team team : teamList) {
						this.createTaskTrn(
								team,
								DateUtil.getDateStr(taskDate, Constant.DATE_FORMAT_yMd));
					}
					taskDate = DateUtil.getThatDate(taskDate, 1);
				}
			}
		} catch (ParseException e) {
			// TODO エラーハンドリング
			e.printStackTrace();
		}

	}

	/**
	 * 指定されたチーム・日付のタスクトランをタスクマスタを元に作成する.
	 * @param team
	 * @param dateStr
	 */
	protected void createTaskTrn(Team team, String dateStr) {
		Logger.info("Apps#createTaskTrn " + team.teamName + dateStr);

		 // タスクマスタにあってタスクトラン未作成の場合個別にトランを作成する
		try {
			List<TaskMst> taskMstList = taService.findTaskMstByTeamName(team.teamName); // throws Exception
			for (TaskMst taskMst : taskMstList) {
				// 開始日が未来日の場合作成しない
				if (taskMst.startDate.compareTo(DateUtil.getDate(dateStr, Constant.DATE_FORMAT_yMd)) > 0) {
					continue;
				}

				// 実施タイプが1回のみの場合、開始日が当日以外の場合トランを作成しない（余計なトラン作成を防ぐ）
				if (Constant.REPTYPE_TEMP.equals(taskMst.repType)
					&& !dateStr.equals(DateUtil.getDateStr(taskMst.startDate, Constant.DATE_FORMAT_yMd))) {
					continue;
				}
				// トラン未作成フラグ
				boolean noTrnFlg = true;

				List<TaskTrn> taskTrnList = taService.findTaskList(team, dateStr);
				for (TaskTrn taskTrn : taskTrnList) {
					if (taskMst.id == taskTrn.taskMst.id) {
						// 作成済みならfor文を抜ける
						noTrnFlg = false;
						break;
					}
				}
				if (noTrnFlg) {
					taService.createTaskTrn(taskMst, dateStr); // throws ParseException
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO エラーの扱い
		}
	}
}