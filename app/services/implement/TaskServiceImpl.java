package services.implement;

import com.google.inject.Inject;
import constant.Constant;
import dto.task.CreateTaskMstDto;
import models.TaskMst;
import models.TaskTrn;
import models.Team;
import models.User;
import play.Logger;
import play.mvc.Http;
import services.TaskService;
import services.UserService;
import util.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created on 2016/05/24.
 */
public class TaskServiceImpl implements TaskService {

	@Inject
	UserService userService;

	@Override
	public void createTaskMst(CreateTaskMstDto createTaskMstDto) {
		Logger.info("TaskServiceImpl#createTaskMst");

		TaskMst taskMst = new TaskMst();
		taskMst.taskName = createTaskMstDto.getTaskName();
		taskMst.taskInfo = createTaskMstDto.getTaskInfo();
		taskMst.repType = createTaskMstDto.getRepType();
		taskMst.repetition = createTaskMstDto.getRepetition();

		// タスク利用チーム
		String teamName = createTaskMstDto.getTeamName();
		List<Team> teamList = Team.find.where().eq("teamName", teamName).findList();
		if (teamList.size() == 0) {
			// TODO エラー
			Logger.error("team not found");

			return;
		} else {
			taskMst.taskTeam = teamList.get(0);
		}

		// 主担当者
		List<User> user = User.find.where().eq(
				"userName", createTaskMstDto.getMainUserName())
				.findList();
		List<String> errorMessages = new ArrayList<String>();

		// 取得できなかった場合エラー
		if (user.size() == 0) {
			errorMessages.add("ユーザー：" + createTaskMstDto.getMainUserName() + "は存在しません。");
		} else {
			// ユーザー名は重複ない前提
			taskMst.mainUser = user.get(0);
		}

		if (errorMessages.size() > 0) {
			// TODO エラーメッセージの返し方
		} else {
			taskMst.save();
		}
	}

	/**
	 * チームと日付からタスクリストを検索する.
	 * @param teamId
	 * @param dateStr
	 * @return
	 */
	@Override
	public List<TaskTrn> findTaskList(long teamId, String dateStr) {
		Logger.info("TaskServiceImpl#findTaskList");
		List<TaskTrn> taskTrnList = new ArrayList<TaskTrn>();
		// 1.チームからタスクマスタの一覧を取得する
		Team team = Team.find.byId(teamId);
		if (team == null) {
			// TODO エラー
			return null;
		} else {
			try {
				List<TaskMst> taskMstList = TaskMst.find.where().eq("taskTeam", team).findList();

				// 2.タスクマスタの一覧から、紐付く該当日付のタスクトランを取得する
				// タスクマスタと日付からは1件のみ取得される想定
				for (TaskMst taskMst : taskMstList) {
					Logger.debug("taskMst.taskName: " + taskMst.taskName);
					List<TaskTrn> taskTrn = TaskTrn.find.where().eq("taskMst", taskMst)
							.where().eq("taskDate", DateUtil.getDate(dateStr, "yyyyMMdd")).findList();
					for (TaskTrn task : taskTrn) {
						Logger.debug("taskTrn.taskName, date: "
								+ task.taskMst.taskName + ", " + task.taskDate);
					}
					taskTrnList.addAll(taskTrn);
				}

			} catch (ParseException e) {
				// TODO エラーハンドリング
				e.printStackTrace();
			}
		}
		return taskTrnList;
	}

	/**
	 * チームと日付でタスクトランを作成する.
	 * @param teamId
	 * @param dateStr
	 */
	@Override
	public List<TaskTrn> createTaskTrn(long teamId, String dateStr) {
		Logger.info("TaskServiceImpl#createTaskTrn");
		List<TaskTrn> taskTrnList = new ArrayList<TaskTrn>();
		// 1.チームからタスクマスタの一覧を取得する
		Team team = Team.find.byId(teamId);
		if (team == null) {
			// TODO エラー
			return null;
		} else {
			try {
				Logger.info("teamName:" + team.teamName);
				List<TaskMst> taskMstList = TaskMst.find.where().eq("taskTeam", team).findList();

				// 2.タスクマスタの一覧を元に、該当日付のタスクトランを作成する
				for (TaskMst taskMst : taskMstList) {
					TaskTrn taskTrn = new TaskTrn();
					taskTrn.taskMst = taskMst;
					taskTrn.taskDate = DateUtil.getDate(dateStr, "yyyyMMdd");
					taskTrn.operationFlg = getOperationFlg(taskMst.repType, taskMst.repetition, dateStr);

					taskTrn.save();
					taskTrnList.add(taskTrn);
				}

			} catch (ParseException e) {
				// TODO エラーハンドリング
				e.printStackTrace();
			}
		}
		return taskTrnList;
	}

	/**
	 * 実施頻度タイプ、実施頻度、タスクトランの日付を元に実施対象フラグをセットする.
	 * @param repType
	 * @param repetition
	 * @param dateStr
	 * @return 実施対象：1、実施対象外：0
	 */
	private String getOperationFlg(String repType, String repetition, String dateStr) {
		switch(repType) {
			case Constant.REPTYPE_DAYLY:
				// 日次の場合は毎日実施対象
				return Constant.FLAG_ON;
			case Constant.REPTYPE_WEEKLY:
				// dateStrの曜日を取得する
				Locale.setDefault(Locale.US);
				try {
					String dow = DateUtil.getDateStrFromStr(dateStr, "yyyyMMdd", "E");
					for (String rep : repetition.split(",")) {
						if (rep.equals(dow)) {
							return Constant.FLAG_ON;
						}
					}
					// 実施頻度の中に一致する曜日が無かった場合はOFF
					return Constant.FLAG_OFF;
				} catch (Exception e) {
					e.printStackTrace();
					// TODO エラーの扱い
					return Constant.FLAG_OFF;
				}
			case Constant.REPTYPE_MONTHLY:
				try {
					// dateStrの日付部分を取得する
					String d = DateUtil.getDateStrFromStr(dateStr, "yyyyMMdd", "d");
					for (String rep : repetition.split(",")) {
						if (rep.equals(d)) {
							return Constant.FLAG_ON;
						}
					}
					// 実施頻度の中に一致する日付が無かった場合はOFF
					return Constant.FLAG_OFF;
				} catch (Exception e) {
					e.printStackTrace();
					// TODO エラーの扱い
					return Constant.FLAG_OFF;
				}
			default:
				// ありえない
				return Constant.FLAG_OFF;
		}
	}

	/**
	 * IDからタスク名称を取得する.
	 * @param mstId
	 * @return
	 */
	@Override
	public String getTaskMstName(long mstId) {
		TaskMst mst = TaskMst.find.byId(mstId);
		return mst.taskName;
	}

	/**
	 * 指定されたタスクトランを実施済み←→未実施にステータス変更する.
	 * エラー有無をintで返却する.
	 * 1:未実施→実施済
	 * 0:実施済→未実施
	 * -1:エラー
	 * @param taskTrnId
	 * @return
	 */
	@Override
	public int updateTaskTrnStatus(long taskTrnId) {
		TaskTrn trn = TaskTrn.find.byId(taskTrnId);
		if (trn == null) {
			return -1;
		} else {
			if (trn.operationUser == null) {
				// 未実施→実施済
				// 実施ユーザーの取得
				List<User> userList = userService.findUserByName(Http.Context.current().session().get("userName"));
				if (userList == null || userList.get(0) == null) {
					// TODO エラー
					return -1;
				} else {
					trn.operationUser = userList.get(0);
					trn.update();
					return 1;
				}
			} else {
				// 実施済→未実施
				trn.operationUser = null;
				trn.update();
				return 0;
			}
		}
	}

}
