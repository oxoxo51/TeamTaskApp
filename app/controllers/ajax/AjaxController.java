package controllers.ajax;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import constant.Constant;
import controllers.Apps;
import models.TaskMst;
import models.TaskTrn;
import models.Team;
import models.User;
import play.libs.Json;
import play.mvc.Result;
import services.TaskService;
import services.TeamService;
import services.UserService;
import util.DateUtil;
import util.MsgUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * サーバーからのリクエストを受けるフロント部分.
 * 扱うデータごとにサーバー側処理を各コントローラに振り分ける.
 * Created on 16/07/22.
 */
public class AjaxController extends Apps {
	@Inject
	TaskService taskService;
	@Inject
	TeamService teamService;
	@Inject
	UserService userService;

	/**
	 * jsonのタスクトランIDを元にタスク状態を更新し、更新後のタスクリストを返却する.
	 * 返却する情報は（タスクトランID、タスク名、担当者、実施者、実施要否…画面表示する項目のみ）.
	 * ※取得するタスクリストは紐付くチーム・日付のもののみ.
	 *  (チームAの4月1日のタスクを更新した際、チームAの4月1日のタスクリストを返却する)
	 jsonの構文：
	 * {
	 *     taskTrn: [
	 *         {
	 *             taskTrnId: xxx
	 *             taskName: "XXX"
	 *             mainUserName: "XXX"
	 *             operationUserName: "XXX"
	 *             operationFlg: true
	 *         }
	 *     ]
	 *     dateStr: "XXX"
	 *     msg: "XXX"
	 *     msgStatus: "XXX"
	 * }
	 * @return
	 */
	public Result updTaskStatAndGetTaskListByTeamAndDate() {
		Long id = Long.parseLong(request().body().asFormUrlEncoded().get("id")[0]);
		String dateStr = request().body().asFormUrlEncoded().get("dateStr")[0];

		ObjectNode result = Json.newObject();
		ArrayNode arr = result.arrayNode();

		String msg = "";
		String msgStatus = Constant.MSG_SUCCESS;

		if (id != -1) {
			int status;
			// 初期表示時以外
			if ((status = taskService.updateTaskTrnStatus(id)) == Constant.TASK_UPD_FINISHED) {
				// →実施済
				msg = MsgUtil.makeMsgStr(Constant.MSG_I005, taskService.getTaskMstByTaskTrnId(id).taskName);
			} else if (status == Constant.TASK_UPD_NOT_YET){
				msg = MsgUtil.makeMsgStr(Constant.MSG_I006, taskService.getTaskMstByTaskTrnId(id).taskName);
			} else {
				msg = MsgUtil.makeMsgStr(Constant.MSG_E004);
				msgStatus = Constant.MSG_ERROR;
			}
		}

		Team team = teamService.findTeamByName(session(Constant.ITEM_TEAM_NAME)).get(0);

		// タスクリスト抽出（レスポンスのjson作成）
		List<TaskTrn> taskTrnList = taskService.findTaskList(team.id, dateStr);
		// ArrayNodeに変換
		for (TaskTrn task : taskTrnList) {
			ObjectNode element = Json.newObject();
			element.put("taskTrnId", task.id);
			element.put("taskName", task.taskMst.taskName);
			element.put("mainUserName", task.taskMst.mainUser.userName);
			element.put("operationUserName", task.operationUser == null ? "" : task.operationUser.userName);
			element.put("operationFlg", task.operationFlg);
			arr.add(element);
		}
		result.putArray("taskTrn").addAll(arr);
		result.put("dateStr", dateStr);
		result.put("msg", msg);
		result.put("msgStatus", msgStatus);

		return ok(result);
	}

	/**
	 * jsonのタスクトランIDを元にタスク状態を更新し、更新後のタスクリストを返却する.
	 * 返却する情報は（タスクトランID、タスク名、チーム名、日付、担当者…画面表示する項目のみ）.
	 * ※取得するタスクリストは紐付くチーム・日付のもののみ.
	 *  (チームAの4月1日のタスクを更新した際、チームAの4月1日のタスクリストを返却する)
	 jsonの構文：
	 * {
	 *     taskTrn: [
	 *         {
	 *             taskTrnId: xxx
	 *             taskName: "XXX"
	 *             teamName: "XXX"
	 *             dateStr: "XXX" // YYYY/mm/DD形式：そのまま表示するため
	 *             mainUserName: "XXX"
	 *         }
	 *     ]
	 *     msg: "XXX"
	 *     msgStatus: "XXX"
	 * }
	 * @return
	 */
	public Result updTaskStatAndGetTaskListByUser() {
		Long id = Long.parseLong(request().body().asFormUrlEncoded().get("id")[0]);
		User user = userService.findUserByName(session("userName")).get(0);

		ObjectNode result = Json.newObject();
		ArrayNode arr = result.arrayNode();

		String msg = "";
		String msgStatus = Constant.MSG_SUCCESS;

		// 表示対象のタスクトランを詰めるリスト
		List<TaskTrn> taskTrnList = new ArrayList<>();

		if (id != -1) {
			int status;
			// 初期表示時以外
			if ((status = taskService.updateTaskTrnStatus(id)) == Constant.TASK_UPD_FINISHED) {
				// →実施済
				msg = MsgUtil.makeMsgStr(Constant.MSG_I005, taskService.getTaskMstByTaskTrnId(id).taskName);
			} else if (status == Constant.TASK_UPD_NOT_YET){
				msg = MsgUtil.makeMsgStr(Constant.MSG_I006, taskService.getTaskMstByTaskTrnId(id).taskName);
			} else {
				msg = MsgUtil.makeMsgStr(Constant.MSG_E004);
				msgStatus = Constant.MSG_ERROR;
			}
		}

		// TODO 一旦Modelから直接取得
		List<Team> teamList = Team.find.where().eq("members", user).setOrderBy("teamName").findList();

		for (Team team : teamList) {
			try {
				// TODO try~catchちゃんとやる
				List<TaskMst> taskMstList = taskService.findTaskMstByTeamName(team.teamName);
				for (TaskMst taskMst : taskMstList) {
					// TODO 一旦Modelから直接取得
					// タスクトランを抽出してリストに詰める:日付でソートし、今日以前で最も古い1件ずつを取得
					// 抽出条件：未実施かつ実施対象
					List<TaskTrn> ttList =
							TaskTrn.find.where()
								.eq("taskMst", taskMst) // タスクマスタ
								.isNull("operationUser") // 未実施
								.eq("operationFlg", Constant.FLAG_ON) // 実施対象
								.lt("taskDate", new Date()) // 今日以前
								.orderBy("taskDate") // 古いものから
								.findList();
					if (ttList.size() != 0) {
						taskTrnList.add(ttList.get(0)); // 最も古い1件
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// TODO ソート(1.日付, 2.チーム名, 3.タスク名)

		for (TaskTrn t : taskTrnList) {
			ObjectNode element = Json.newObject();
			element.put("taskTrnId", t.id);
			element.put("taskName", t.taskMst.taskName);
			element.put("teamName", t.taskMst.taskTeam.teamName);
			element.put("dateStr", DateUtil.getDateStr(t.taskDate, Constant.DATE_FORMAT_yMd_SLASH));
			element.put("mainUserName", t.taskMst.mainUser.userName);
			arr.add(element);
		}
		result.putArray("taskTrn").addAll(arr);
		result.put("msg", msg);
		result.put("msgStatus", msgStatus);

		return ok(result);
	}

}
