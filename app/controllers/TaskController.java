package controllers;

import com.google.inject.Inject;
import dto.task.CreateTaskMstDto;
import models.TaskTrn;
import models.Team;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import services.TaskService;
import util.DateUtil;
import views.html.index;
import views.html.taskList;
import views.html.taskMst;

import java.util.Date;
import java.util.List;

/**
 * Created on 2016/05/25.
 */
public class TaskController extends Apps {
	@Inject
	TaskService service;

	/**
	 * タスクリスト画面表示（日付指定あり）.
	 * 渡された日付と利用チームを元に、タスクリストを表示する.
	 * @param dateStr
	 * @return
	 */
	public Result displayTaskListWithDate(String teamName, String dateStr) {
		Logger.info("TaskController#displayTaskListWithDate teamName:" + teamName +
					" dateStr:" + dateStr);

		// 利用チームに紐付くタスクリストを表示
		List<Team> teamList = Team.find.where().eq("teamName", teamName).findList();
		if (teamList.size() == 0) {
			// TODO エラーハンドリング
			return ok(index.render("チームが存在しません。"));
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

		return ok(taskList.render(taskTrnList, dateStr, teamName));

	}

	/**
	 * タスクリスト画面表示（日付指定なし）.
	 * 渡された利用チームの実行当日のタスクリストを表示する.
	 * @param teamName
	 * @return
	 */
	public Result displayTaskList(String teamName) {
		Logger.info("TaskController#displayTaskList teamName:" +teamName);

		String dateStr = DateUtil.getDateStr(new Date(), "yyyyMMdd");

		// 本日のタスクリストを表示する.
		return displayTaskListWithDate(teamName, dateStr);

	}

	/**
	 * タスクマスタ新規登録画面表示.
	 * @return
	 */
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

}
