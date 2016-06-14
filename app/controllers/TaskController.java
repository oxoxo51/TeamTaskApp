package controllers;

import com.google.inject.Inject;
import dto.task.CreateTaskMstDto;
import models.TaskTrn;
import models.Team;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import services.TaskService;
import services.TeamService;
import util.DateUtil;
import views.html.taskList;
import views.html.taskMst;

import java.util.*;

/**
 * Created on 2016/05/25.
 */
public class TaskController extends Apps {
	@Inject
	TaskService service;
	@Inject
	TeamService teamService;

	/**
	 * タスクリスト画面表示（日付指定なし）.
	 * 渡された利用チームの実行当日のタスクリストを表示する.
	 * @param teamName
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayTaskList(String teamName) {
		Logger.info("TaskController#displayTaskList teamName:" +teamName);

		String dateStr = DateUtil.getDateStr(new Date(), "yyyyMMdd");

		// 本日のタスクリストを表示する.
		return displayTaskListWithDate(teamName, dateStr);
	}

	/**
	 * タスクリスト画面表示（日付指定あり）.
	 * 渡された日付と利用チームを元に、タスクリストを表示する.
	 * @param teamName
	 * @param dateStr
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayTaskListWithDate(String teamName, String dateStr) {
		Logger.info("TaskController#displayTaskListWithDate teamName:" + teamName +
					" dateStr:" + dateStr);

		// セッションにチーム名を保持する
		this.setSessionTeamName(teamName);

		// 利用チームに紐付くタスクリストを表示
		List<Team> teamList = Team.find.where().eq("teamName", teamName).findList();
		if (teamList.size() == 0) {
			// TODO エラーハンドリング
			List<Team> teams = teamService.findTeamListByUserName(session("userName"));
			return ok(views.html.teamList.render(session("userName"), teams));
		}
		// チーム名は重複しない想定
		Team team = teamList.get(0);
		// 該当日付のタスクトランを取得し、0件の場合新規作成する
		List<TaskTrn> taskTrnList = service.findTaskList(team.id, dateStr);
		if (taskTrnList.size() == 0) {
			taskTrnList = service.createTaskTrn(team.id, dateStr);
		}
		for (TaskTrn task : taskTrnList) {
			Logger.info("taskName:" + task.taskMst.taskName
						+ "taskMstId:" + task.taskMst.id);
		}

		String html = this.editTaskListHtml(taskTrnList, dateStr);

		return ok(taskList.render(html, dateStr, teamName));

	}

	/**
	 * タスクリスト画面に表示するHTMLを作成する.
	 * @param taskTrnList
	 * @param dateStr
	 * @return
	 */
	private String editTaskListHtml(List<TaskTrn> taskTrnList, String dateStr) {
		String html = "";
		// タスクリストをキーとタスクトランのマップに置き換える
		// キー：実施状態(未実施：0,実施済：1,実施対象外：-1)
		// 		担当者or実施者のユーザーID
		// キーはカンマ区切りのStringとする
		Map<String, TaskTrn> taskMap = new HashMap<String, TaskTrn>();
		for (TaskTrn task : taskTrnList) {
			String key = "";
			// 実施状態
			// TODO 実施対象かどうかの判定を追加する必要あり:条件分岐の先頭に追加
			if (task.operationUser != null) {
				// 実施済
				key = "1," + task.operationUser.id + "," + task.id;
			} else {
				// TODO 実施対象化どうかの判定を追加する
				key = "0," + task.taskMst.mainUser.id+ "," + task.id;
			}
			taskMap.put(key, task);
		}

		// 実施済、未実施それぞれString配列のMAPでHTMLを作成する
		Map<String, String> finishMap = new HashMap<String, String>(); // 実施済
		Map<String, String> notyetMap = new HashMap<String, String>(); // 未実施

		// キーの状態毎にHTMLを作成する.
		for (Iterator i = taskMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry)i.next();
			String[] keyArg = ((String)entry.getKey()).split(",");
			TaskTrn task = (TaskTrn)entry.getValue();
			String taskUpdateurl = routes.TaskController.updateTaskTrnStatus(task.id,dateStr).absoluteURL(request());
			String htmlStr = "";
			if ("1".equals(keyArg[0])) {
				// 実施済
				if (!finishMap.containsKey(keyArg[1])) {
					// 実施者毎のヘッダ作成
					htmlStr = "<tr><th colspan=\"2\">実施者："
							+ (User.find.byId(Long.parseLong(keyArg[1]))).userName
							+ "</th></tr>";
					// 一度MAPに入れる
					finishMap.put(keyArg[1], htmlStr);
				} else {
					htmlStr = finishMap.get(keyArg[1]);
				}
				htmlStr += "<tr><td>" +
						"<a href=" + taskUpdateurl + ">戻す</a>" +
						"</td><td>" +
						task.taskMst.taskName +
						"</td></tr>";
				finishMap.replace(keyArg[1], htmlStr);
			} else {
				// 未実施
				if (!notyetMap.containsKey(keyArg[1])) {
					// 主担当者毎のヘッダ作成
					htmlStr = "<tr><th colspan=\"2\">主担当者："
							+ (User.find.byId(Long.parseLong(keyArg[1]))).userName
							+ "</th></tr>";
					// 一度MAPに入れる
					notyetMap.put(keyArg[1], htmlStr);
				} else {
					htmlStr = notyetMap.get(keyArg[1]);
				}
				htmlStr += "<tr><td>" +
						"<a href=" + taskUpdateurl + ">実施</a>" +
						"</td><td>" +
						task.taskMst.taskName +
						"</td></tr>";
				notyetMap.replace(keyArg[1], htmlStr);
			}
			Logger.debug("key:" + keyArg[0] + "," + keyArg[1] + " html:" + htmlStr);
		}
		// 未実施
		html = "<tr class=\"table-info\"><th colspan=\"2\">未実施</th></tr>";
		for (Iterator i = notyetMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry)i.next();
			html += (String)entry.getValue();
			Logger.debug("html:" + html);
		}
		// 実施済
		html += "<tr class=\"table-success\"><th colspan=\"2\">実施済</th></tr>";
		for (Iterator i = finishMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry)i.next();
			html += (String)entry.getValue();
			Logger.debug("html:" + html);
		}

		return html;
	}

	/**
	 * タスクマスタ新規登録画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayCreateTaskMst(String teamName) {
		Logger.info("TaskController#displayCreateTaskMst");

		CreateTaskMstDto dto = new CreateTaskMstDto();
		dto.setTeamName(teamName);
		Form<CreateTaskMstDto> createTaskMstDtoForm = Form.form(CreateTaskMstDto.class);
		return ok(taskMst.render("CREATE", createTaskMstDtoForm.fill(dto)));
	}

	/**
	 * タスクマスタ新規登録.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result create() {
		Logger.info("TaskController#create");

		Form<CreateTaskMstDto> createTaskMstDtoForm = Form.form(CreateTaskMstDto.class).bindFromRequest();
		if (!createTaskMstDtoForm.hasErrors()) {
			CreateTaskMstDto dto = createTaskMstDtoForm.get();
			service.createTaskMst(dto);
			String msg = "登録しました。";
			msg += "taskName: " + dto.taskName;
			flash("success", msg);

			// タスクリストに遷移
			return displayTaskList(dto.teamName);

		} else {
			// TODO
			flash("error", "登録できません。");
			return badRequest(taskMst.render("CREATE", createTaskMstDtoForm));
		}

	}

	/**
	 * 指定されたタスクトランを実施済み←→未実施にステータス変更する
	 * @param taskTrnId
	 * @param dateStr
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result updateTaskTrnStatus(long taskTrnId, String dateStr) {
		int status = 0;
		if ((status = service.updateTaskTrnStatus(taskTrnId)) != -1) {
			// タスクリストを再表示
			if (status == 1) {
				flash("success", "実施済にしました。");
			} else {
				flash("success", "未実施に戻しました。");
			}
			return redirect(routes.TaskController.displayTaskListWithDate(session("teamName"), dateStr));
		} else {
			// TODO
			flash("error", "更新できません。");
			return redirect(routes.TaskController.displayTaskListWithDate(session("teamName"), dateStr));
		}
	}

}
