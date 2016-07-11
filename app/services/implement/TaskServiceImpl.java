package services.implement;

import com.google.inject.Inject;
import constant.Constant;
import dto.task.EditTaskMstDto;
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
	public void createTaskMst(EditTaskMstDto editTaskMstDto) {
		Logger.info("TaskServiceImpl#createTaskMst");

		TaskMst taskMst = new TaskMst();
		List<String> errorMessages = new ArrayList<String>();
		taskMst = editTaskMst(taskMst, editTaskMstDto, errorMessages);
		if (errorMessages.size() > 0) {
			// TODO エラーメッセージの返し方
		} else {
			taskMst.save();
		}
	}

	@Override
	public void updateTaskMst(EditTaskMstDto editTaskMstDto) {
		Logger.info("TaskServiceImpl#updateTaskMst");

		TaskMst taskMst = TaskMst.find.byId(editTaskMstDto.getId());
		List<String> errorMessages = new ArrayList<String>();
		taskMst = editTaskMst(taskMst, editTaskMstDto, errorMessages);
		if (errorMessages.size() > 0) {
			// TODO エラーメッセージの返し方
		} else {
			taskMst.update();
		}
	}

	private TaskMst editTaskMst(TaskMst taskMst, EditTaskMstDto editTaskMstDto, List<String> errorMessages) {
		TeamServiceImpl teamService = new TeamServiceImpl();
		UserServiceImpl userService = new UserServiceImpl();

		taskMst.taskName = editTaskMstDto.getTaskName();
		taskMst.taskInfo = editTaskMstDto.getTaskInfo();
		taskMst.repType = editTaskMstDto.getRepType();
		taskMst.repetition = editTaskMstDto.getRepetition();

		// タスク利用チーム
		String teamName = editTaskMstDto.getTeamName();
		Team team = teamService.findTeamByName(teamName).get(0);
		taskMst.taskTeam = team;

		// 主担当者
		List<User> user = userService.findUserByName(editTaskMstDto.getMainUserName());

		// TODO 存在チェックはDTOのバリデーションに移動する
		// 取得できなかった場合エラー
		if (user.size() == 0) {
			errorMessages.add("ユーザー：" + editTaskMstDto.getMainUserName() + "は存在しません。");
		} else {
			// ユーザー名は重複ない前提
			taskMst.mainUser = user.get(0);
		}

		// 開始日
		taskMst.startDate = editTaskMstDto.getStartDate();

		return taskMst;
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

		TeamServiceImpl teamService = new TeamServiceImpl();

		List<TaskTrn> taskTrnList = new ArrayList<TaskTrn>();
		// 1.チームからタスクマスタの一覧を取得する
		Team team = teamService.findTeamById(teamId);
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
	public List<TaskTrn> createTaskTrnByTeamId(long teamId, String dateStr) {
		Logger.info("TaskServiceImpl#createTaskTrn");

		TeamServiceImpl teamService = new TeamServiceImpl();

		List<TaskTrn> taskTrnList = new ArrayList<TaskTrn>();
		// 1.チームからタスクマスタの一覧を取得する
		Team team = teamService.findTeamById(teamId);
		if (team == null) {
			// TODO エラー
			return null;
		} else {
			try {
				Logger.info("teamName:" + team.teamName);
				List<TaskMst> taskMstList = TaskMst.find.where().eq("taskTeam", team).findList();

				// 2.タスクマスタの一覧を元に、該当日付のタスクトランを作成する
				// ただし、タスクトランを作成する日付がタスクマスタの開始日以降の場合のみとする
				for (TaskMst taskMst : taskMstList) {
					if (taskMst.startDate.compareTo(DateUtil.getDate(dateStr, "yyyyMMdd")) <= 0) {
						taskTrnList.add(createTaskTrn(taskMst, dateStr));
					}
				}
			} catch (ParseException e) {
				// TODO エラーハンドリング
				e.printStackTrace();
			}
		}
		return taskTrnList;
	}

	/**
	 * タスクマスタを元に、指定した日付のタスクトランを作成する.
	 * @param taskMst
	 * @param dateStr
	 * @return
	 */
	@Override
	public TaskTrn createTaskTrn(TaskMst taskMst, String dateStr) throws ParseException {
		TaskTrn taskTrn = new TaskTrn();
		taskTrn.taskMst = taskMst;
		taskTrn.taskDate = DateUtil.getDate(dateStr, "yyyyMMdd");
		taskTrn.operationFlg = getOperationFlg(taskMst.repType, taskMst.repetition, dateStr);

		taskTrn.save();
		return taskTrn;
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

	/**
	 * チーム名からそのチームが所有するタスクマスタを取得する.
	 * @param teamName
	 * @return
	 */
	@Override
	public List<TaskMst> findTaskMstByTeamName(String teamName) throws Exception {
		Logger.info("TaskServiceImpl#findTaskMstByTeamName");
		TeamServiceImpl teamService = new TeamServiceImpl();
		List<Team> teamList = teamService.findTeamByName(teamName);
		if (teamList == null || teamList.size() == 0) {
			// TODO Exceptionの定義
			throw new Exception();
		} else {
			Team team = teamList.get(0);
			return TaskMst.find.where().eq("taskTeam", team).findList();
		}
	}

	/**
	 * タスク名からタスクマスタを取得する.
	 * @param taskName
	 * @return
	 */
	@Override
	public TaskMst findTaskMstByTeamAndTaskName(String teamName, String taskName) {
		Logger.info("TaskServiceImpl#findTaskMstByTeamAndTaskName");

		try {
			List<TaskMst> taskMstList = findTaskMstByTeamName(teamName);
			for (TaskMst taskMst : taskMstList) {
				if (taskMst.taskName.equals((taskName))) {
					return taskMst;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 何もしない
		}
		// 見つからない場合
		return null;
	}

	/**
	 * ユーザーのタスク実施回数を返す.
	 * @param userName
	 * @param teamName
	 * @param taskName
	 * @return
	 */
	public Integer getTaskDoneCount(String userName, String teamName, String taskName) {
		Logger.debug("TaskServiceImpl#getTaskDoneCount");

		TeamServiceImpl teamService = new TeamServiceImpl();
		UserServiceImpl userService = new UserServiceImpl();

		// 元となるタスクマスタ
		Team team = teamService.findTeamByName(teamName).get(0);
		Logger.debug(team.teamName);
		TaskMst taskMst = TaskMst.find.where().eq("taskTeam", team)
				.eq("taskName", taskName)
				.findList().get(0);
		Logger.debug(taskMst.taskName);

		// タスク実施ユーザー
		User user = userService.findUserByName(userName).get(0);
		// タスクトランから該当のタスクのうち特定ユーザーが実施したものを抽出
		List<TaskTrn> taskTrnList = TaskTrn.find.where().eq("taskMst", taskMst)
				.eq("operationUser", user)
				.findList();
		Logger.debug(Integer.toString(taskTrnList.size()));
		return taskTrnList == null ? 0 : taskTrnList.size();
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

}
