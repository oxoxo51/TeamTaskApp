package controllers;

import constant.Constant;
import dto.task.EditTaskMstDto;
import models.TaskMst;
import models.Team;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import services.implement.TaskServiceImpl;
import services.implement.TeamServiceImpl;
import util.DateUtil;
import views.html.taskList;
import views.html.taskMst;
import views.html.taskMstList;
import views.html.taskRefer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 2016/05/25.
 */
public class TaskController extends Apps {

	/**
	 * タスクマスタ新規登録画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayCreateTaskMst() {
		Logger.info("TaskController#displayCreateTaskMst");
		setSessionUrl(routes.TaskController.displayCreateTaskMst().url());

		EditTaskMstDto dto = new EditTaskMstDto();

		String teamName = getSessionTeamName();
		if (Constant.USER_TEAM_BLANK.equals(teamName)) {
			dto.setTeamName("");
		} else {
			dto.setTeamName(teamName);
		}
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

		TaskMst taskMst = taService.findTaskMstByTeamAndTaskName(teamName, taskName);

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

		Form<EditTaskMstDto> editTaskMstDtoForm = Form.form(EditTaskMstDto.class);
		return ok(views.html.taskMst.render(Constant.MODE_UPDATE, editTaskMstDtoForm.fill(dto)));
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
					taService.createTaskMst(dto);
					flashSuccess(Constant.MSG_I003);
					break;
				case Constant.MODE_UPDATE :
					taService.updateTaskMst(dto);
					flashSuccess(Constant.MSG_I004);
					break;
			}
			// タスクリストに遷移
			return redirect(routes.TaskController.displayTaskList());
		} else {
			flashError(Constant.MSG_E003);
			return badRequest(taskMst.render(mode, editTaskMstDtoForm));
		}
	}

	/**
	 * タスクリスト画面表示（日付指定あり）.
	 * 渡された日付と利用チームを元に、タスクリストを表示する.
	 * @param dateStr
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayTaskListWithDate(String dateStr) {
		Logger.info("TaskController#displayTaskListWithDate"
				+ " dateStr:" + dateStr);
		setSessionUrl(routes.TaskController.displayTaskListWithDate(dateStr).url());

		// チーム未選択の場合、全件表示にリダイレクトする
		if (Constant.USER_TEAM_BLANK.equals(getSessionTeamName())) {
			return redirect(routes.TaskController.displayTaskList());
		}
		// セッションのチームを取得する
		Team team = teService.findTeamByName(getSessionTeamName()).get(0);

		// 未作成のタスクトラン作成
		super.createTaskTrn(team, dateStr);

		return ok(taskList.render(dateStr, team.teamName));
	}

	/**
	 * タスクリスト画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayTaskList() {
		Logger.info("TaskController#displayTaskList");
		setSessionUrl(routes.TaskController.displayTaskList().url());
		if (Constant.USER_TEAM_BLANK.equals(getSessionTeamName())) {
			super.chkAndCreateTaskTrn(super.getLoginUser());
			return ok(taskList.render("", ""));
		} else {
			return displayTaskListWithDate(
							DateUtil.getDateStr(new Date(), Constant.DATE_FORMAT_yMd));
		}
	}

	/**
	 * タスクマスタ一覧画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayTaskMstList() {
		Logger.info("TaskController#displayTaskMstList");

		String userName = session("userName");
		String teamName = (session("teamName") == null ? "" : session("teamName"));

		return ok(taskMstList.render(userName, teamName));
	}

	/**
	 * チーム名・タスク名からタスクマスタを取得する.
	 * @param teamName
	 * @param taskName
	 * @return
	 */
	public static TaskMst findTaskMstByTeamAndTaskName(String teamName, String taskName) {
		TaskServiceImpl service = new TaskServiceImpl();
		return service.findTaskMstByTeamAndTaskName(teamName, taskName);
	}


	/**
	 * タスク参照画面に表示するユーザーごとのタスク実施回数をHTMLにする.
	 * @param teamName
	 * @param taskMst
	 * @return
	 */
	public static String editTaskDoneCountHtml(String teamName, TaskMst taskMst) {
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
			if ((count = service.getTaskDoneCount(
					user, taskMst)) > 0) {
				html += htmlFormat.replace("[userName]", user.userName)
						.replace("NNN", count.toString());
			}
		}
		return html;
	}

	/**
	 * タスク頻度を参照画面で表示する文言にして返却する.
	 * @param taskMst
	 * @return
	 */
	public static String getTaskRepetition(TaskMst taskMst) {

		String repTypeStr = getRepTypeStr(taskMst.repType);

		return repTypeStr
				+ ("".equals(taskMst.repetition) ? "" : "/" + taskMst.repetition);
	}

	/**
	 * 頻度タイプの文言を返却する.
	 * 例：日次/週次/月次
	 * @param repType
	 * @return
	 */
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

	public static List<String> getTaskTeamName() {
		List<Team> teamList = getLoginUser().teams;
		List<String> teamNameList = new ArrayList<>();
		for (Team team : teamList) {
			teamNameList.add(team.teamName);
		}
		return teamNameList;
	}

}
