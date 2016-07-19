package controllers;

import com.google.inject.Inject;
import constant.Constant;
import dto.task.EditTaskMstDto;
import models.TaskMst;
import models.TaskTrn;
import models.Team;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import services.TaskService;
import services.TeamService;
import services.UserService;
import services.implement.TaskServiceImpl;
import services.implement.TeamServiceImpl;
import util.DateUtil;
import views.html.taskList;
import views.html.taskMst;
import views.html.taskRefer;

import java.util.*;

/**
 * Created on 2016/05/25.
 */
public class TaskController extends Apps {
	@Inject
	TaskService service;
	@Inject
	TeamService teamService;
	@Inject
	UserService userService;

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
		Team team = teamService.findTeamByName(teamName).get(0);
		// 該当日付のタスクトランを取得し、0件の場合新規作成する
		List<TaskTrn> taskTrnList = service.findTaskList(team.id, dateStr);
		if (taskTrnList.size() == 0) {
			taskTrnList = service.createTaskTrnByTeamId(team.id, dateStr);
		} else {
			// タスクマスタにあってタスクトラン未作成の場合個別にトランを作成する
			try {
				List<TaskMst> taskMstList = service.findTaskMstByTeamName(team.teamName); // throws Exception
				for (TaskMst taskMst : taskMstList) {
					// トラン未作成フラグ
					boolean noTrnFlg = true;
					for (TaskTrn taskTrn : taskTrnList) {
						if (taskMst.id == taskTrn.taskMst.id) {
							// 作成済みならfor文を抜ける
							noTrnFlg = false;
							break;
						}
					}
					if (noTrnFlg) {
						taskTrnList.add(service.createTaskTrn(taskMst, dateStr)); // throws ParseException
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				// TODO エラーの扱い
			}

		}

		String html = this.editTaskListHtml(taskTrnList, dateStr);

		return ok(taskList.render(html, dateStr, teamName));

	}

	/**
	 * タスクマスタ新規登録画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayCreateTaskMst(String teamName) {
		Logger.info("TaskController#displayCreateTaskMst");

		EditTaskMstDto dto = new EditTaskMstDto();
		dto.setTeamName(teamName);
		dto.setStartDate(new Date());
		Form<EditTaskMstDto> editTaskMstDtoForm = Form.form(EditTaskMstDto.class);
		return ok(taskMst.render(Constant.MODE_CREATE, editTaskMstDtoForm.fill(dto)));
	}

	/**
	 * タスクマスタ更新画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayUpdateTaskMst(String teamName, String taskName) {
		Logger.info("TaskController#displayUpdateTaskMst");

		EditTaskMstDto dto = new EditTaskMstDto();

		TaskMst taskMst = service.findTaskMstByTeamAndTaskName(teamName, taskName);

		dto.setId(taskMst.id);
		dto.setMainUserName(taskMst.mainUser.userName);
		List<String> repetition = new ArrayList<String>();
		for (String repStr : taskMst.repetition.split(",")) {
			repetition.add(repStr);
		}
		dto.setRepetition(repetition);
		dto.setRepType(taskMst.repType);
		dto.setStartDate(taskMst.startDate);
		dto.setTaskInfo(taskMst.taskInfo);
		dto.setTaskName(taskMst.taskName);
		dto.setTeamName(taskMst.taskTeam.teamName);

/*		Logger.info("*** taskMst.repetition:" + taskMst.repetition);
		Logger.info("*** taskMst.repetition.split:" + Arrays.toString(taskMst.repetition.split(",")));
		Logger.info("*** dto.getRepetition:" + Arrays.toString(dto.getRepetition()));
		for(String str : dto.getRepetition()) {
			Logger.info(str);
		}
*/
		Form<EditTaskMstDto> editTaskMstDtoForm = Form.form(EditTaskMstDto.class);
		return ok(views.html.taskMst.render(Constant.MODE_UPDATE, editTaskMstDtoForm.fill(dto)));
	}
	/**
	 * タスクマスタ登録/更新.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result edit(String mode) {
		Logger.info("TaskController#edit MODE:" + mode);

		Form<EditTaskMstDto> editTaskMstDtoForm = Form.form(EditTaskMstDto.class).bindFromRequest();
		if (!editTaskMstDtoForm.hasErrors()) {
			String msg = "";
			EditTaskMstDto dto = editTaskMstDtoForm.get();
			switch (mode) {
				case Constant.MODE_CREATE :
					service.createTaskMst(dto);
					flashSuccess(Constant.MSG_I003);
					break;
				case Constant.MODE_UPDATE :
					service.updateTaskMst(dto);
					flashSuccess(Constant.MSG_I004);
					break;
			}

			// タスクリストに遷移
			return redirect(routes.TaskController.displayTaskList(dto.getTeamName()));

		} else {
			flashError(Constant.MSG_E003);
			return badRequest(taskMst.render(mode, editTaskMstDtoForm));
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
		int status = Constant.TASK_UPD_NOT_YET;
		if ((status = service.updateTaskTrnStatus(taskTrnId)) != -1) {
			// タスクリストを再表示
			if (status == Constant.TASK_UPD_FINISHED) {
				flashSuccess(Constant.MSG_I005, service.getTaskMstByTaskTrnId(taskTrnId).taskName);
			} else {
				flashSuccess(Constant.MSG_I006, service.getTaskMstByTaskTrnId(taskTrnId).taskName);
			}
			return redirect(routes.TaskController.displayTaskListWithDate(getSessionTeamName(), dateStr));
		} else {
			// エラー
			flashError(Constant.MSG_E004);
			return redirect(routes.TaskController.displayTaskListWithDate(getSessionTeamName(), dateStr));
		}
	}

	/**
	 * タスク参照画面を表示する.
	 * @param teamName
	 * @param taskName
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result referTask(String teamName, String taskName) {
		return ok(taskRefer.render(teamName, taskName));
	}

	public static TaskMst findTaskMstByTeamAndTaskName(String teamName, String taskName) {
		TaskServiceImpl service = new TaskServiceImpl();
		return service.findTaskMstByTeamAndTaskName(teamName, taskName);
	}

	/**
	 * タスク参照画面に表示するユーザーごとのタスク実施回数をHTMLにする.
	 * @param teamName
	 * @param taskName
	 * @return
	 */
	public static String editTaskDoneCountHtml(String teamName, String taskName) {
		Logger.info("TaskController#editTaskDoneCountHtml");

		TaskServiceImpl service = new TaskServiceImpl();
		TeamServiceImpl teamService = new TeamServiceImpl();

		String html = "";

		// フォーマット：[ユーザー名] : N回
		String htmlFormat = "<p>[userName] : NNN 回</p>";

		// チームに所属するユーザー
		List<User> userList = teamService.findUserByTeamName(teamName);

		for (User user : userList) {
			Integer count = 0;
			String userName = user.userName;
			if ((count = service.getTaskDoneCount(
					userName, teamName, taskName)) > 0) {
				String addHtml = htmlFormat;
				addHtml = addHtml.replace("[userName]", userName)
								 .replace("NNN", count.toString());
				html += addHtml;
			}

		}
		return html;
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
			if (task.operationUser != null) {
				// 実施済:実施ユーザーあり
				key = Constant.TASK_FINISHED + Constant.COMMA + task.operationUser.id
						+ Constant.COMMA + task.id;
			} else if (Constant.FLAG_ON.equals(task.operationFlg)
					&& (task.operationUser == null)) {
				// 未実施:実施対象ON、かつ、実施ユーザーなし
				key = Constant.TASK_NOT_YET + Constant.COMMA + task.taskMst.mainUser.id
						+ Constant.COMMA + task.id;
			} else {
				// 実施対象外:実施対象OFF、かつ、実施ユーザーなし
				key = Constant.TASK_OTHER + Constant.COMMA + task.id;
			}
			taskMap.put(key, task);
		}

		// 実施済、未実施、対象外それぞれString配列のMAPでHTMLを作成する
		Map<String, String> finishMap = new HashMap<String, String>(); // 実施済
		Map<String, String> notyetMap = new HashMap<String, String>(); // 未実施
		Map<String, String> otherMap = new HashMap<String, String>(); // 対象外

		// キーの状態毎にHTMLを作成する.
		for (Iterator i = taskMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry)i.next();
			String[] keyArg = ((String)entry.getKey()).split(",");
			TaskTrn task = (TaskTrn)entry.getValue();
			String taskUpdateUrl = routes.TaskController.updateTaskTrnStatus(task.id,dateStr)
					.absoluteURL(request());
			String taskReferUrl = routes.TaskController.referTask(getSessionTeamName(), task.taskMst.taskName)
					.absoluteURL(request());
			String htmlStr = "";
			if (Constant.TASK_FINISHED.equals(keyArg[0])) {
				// 実施済
				if (!finishMap.containsKey(keyArg[1])) {
					// 実施者毎のヘッダ作成
					htmlStr = "<tr class=\"warning\"><th colspan=\"2\">実施者："
							+ (userService.findUserById(Long.parseLong(keyArg[1]))).userName
							+ "</th></tr>";
					// 一度MAPに入れる
					finishMap.put(keyArg[1], htmlStr);
				} else {
					htmlStr = finishMap.get(keyArg[1]);
				}
				htmlStr += getTaskHtmlLine(taskUpdateUrl, taskReferUrl, task.taskMst.taskName, Constant.TASK_UPD_NOT_YET);
				finishMap.replace(keyArg[1], htmlStr);
			} else if (Constant.TASK_NOT_YET.equals(keyArg[0])) {
				// 未実施
				if (!notyetMap.containsKey(keyArg[1])) {
					// 主担当者毎のヘッダ作成
					htmlStr = "<tr class=\"warning\"><th colspan=\"2\">主担当者："
							+ (userService.findUserById(Long.parseLong(keyArg[1]))).userName
							+ "</th></tr>";
					// 一度MAPに入れる
					notyetMap.put(keyArg[1], htmlStr);
				} else {
					htmlStr = notyetMap.get(keyArg[1]);
				}
				htmlStr += getTaskHtmlLine(taskUpdateUrl, taskReferUrl, task.taskMst.taskName, Constant.TASK_UPD_FINISHED);
				notyetMap.replace(keyArg[1], htmlStr);
			} else {
				// 対象外
				htmlStr += getTaskHtmlLine(taskUpdateUrl, taskReferUrl, task.taskMst.taskName, Constant.TASK_UPD_FINISHED);
				//ユーザー毎にまとめる必要なし
				otherMap.put(keyArg[1], htmlStr);
			}
		}
		// 未実施
		html = "<thead><tr class=\"danger\"><th colspan=\"2\">未実施</th></tr></thead><tbody>";
		for (Iterator i = notyetMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry)i.next();
			html += (String)entry.getValue();
		}
		html += "</tbody>";
		// 対象外
		html += "<thead><tr class=\"active\"><th colspan=\"2\">実施不要</th></tr></thead><tbody>";
		for (Iterator i = otherMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry)i.next();
			html += (String)entry.getValue();
		}
		html += "</tbody>";
		// 実施済
		html += "<thead><tr class=\"success\"><th colspan=\"2\">実施済</th></tr></thead><tbody>";
		for (Iterator i = finishMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			html += (String) entry.getValue();
		}
		html += "</tbody>";

		return html;
	}

	/**
	 * タスク行のHTMLを返す.
	 * @param taskUpdateUrl
	 * @param taskReferUrl
	 * @param taskName
	 * @param taskUpdType
	 * @return
	 */
	private String getTaskHtmlLine(String taskUpdateUrl,
								   String taskReferUrl,
								   String taskName,
								   int taskUpdType) {
		String taskUpdStr = "";
		switch (taskUpdType) {
			// 実施済→未実施
			case Constant.TASK_UPD_NOT_YET :
				taskUpdStr = "戻す";
				break;
			// 未実施→実施済
			case Constant.TASK_UPD_FINISHED :
				taskUpdStr = "実施";
		}
		return "<tr><td>" +
				"<a href=" + taskUpdateUrl + ">" + taskUpdStr + "</a>" +
				"</td><td>" +
				"<a href=" + taskReferUrl + ">" + taskName + "</a>" +
				"</td></tr>";
	}

	public static String getTaskRepetition(String teamName, String taskName) {
		TaskServiceImpl service = new TaskServiceImpl();

		TaskMst taskMst = service.findTaskMstByTeamAndTaskName(teamName, taskName);
		String repTypeStr = getRepTypeStr(taskMst.repType);

		return repTypeStr
				+ ("".equals(taskMst.repetition) ? "" : "/" + taskMst.repetition);


	}

	private static String getRepTypeStr(String repType) {
		switch (repType) {
			case Constant.REPTYPE_DAYLY :
				return Constant.REPTYPE_STR_DAYLY;
			case Constant.REPTYPE_WEEKLY :
				return Constant.REPTYPE_STR_WEEKLY;
			case Constant.REPTYPE_MONTHLY :
				return Constant.REPTYPE_MONTHLY;
			default :
				return null;
		}
	}

}
