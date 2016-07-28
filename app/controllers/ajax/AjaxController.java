package controllers.ajax;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import constant.Constant;
import controllers.Apps;
import models.TaskTrn;
import models.Team;
import play.libs.Json;
import play.mvc.Result;
import services.TaskService;
import services.TeamService;
import util.MsgUtil;

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

	/**
	 * jsonのタスクトランIDを元にタスク状態を更新し、更新後のタスクリストを返却する.
	 * 返却する情報は（タスクトランID、タスク名、担当者、実施者、実施要否…画面表示する項目のみ）.
	 * jsonの構文：
	 * taskTrn: [
	 *     {
	 *         taskTrnId: xxx
	 *         taskName: "XXX"
	 *         mainUserName: "XXX"
	 *         operationUserName: "XXX"
	 *         operationFlg: true
	 *     }
	 * ]
	 * TODO 【仮】日付やチームでの抽出はタスクトランIDを元にサーバー側で実施する.
	 * @return
	 */
	public Result updTaskStatAndGetTaskList() {
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



}
