package dto.task;

import constant.Constant;
import models.TaskMst;
import models.User;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import services.implement.TaskServiceImpl;
import services.implement.UserServiceImpl;
import util.DateUtil;
import util.MsgUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 2016/05/24.
 */
public class EditTaskMstDto {

	/**
	 * id.
	 */
	private Long id;

	/**
	 * タスク名.
	 */
	@Constraints.Required
	private String taskName;

	/**
	 * タスク内容.
	 */
	private String taskInfo;

	/**
	 * 実施頻度タイプ.
	 */
	@Constraints.Required
	private String repType;

	/**
	 * 実施頻度.
	 */
	private List<String> repetition;

	/**
	 * 主担当ユーザー名.
	 */
	@Constraints.Required
	private String mainUserName;

	/**
	 * タスク所有チーム名.
	 */
	@Constraints.Required
	private String teamName;

	/**
	 * 開始日.
	 */
	@Constraints.Required
	private Date startDate;


	private List<ValidationError> errors;

	public EditTaskMstDto() {
		errors = new ArrayList<ValidationError>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(String taskInfo) {
		this.taskInfo = taskInfo;
	}

	public String getRepType() {
		return repType;
	}

	public void setRepType(String repType) {
		this.repType = repType;
	}

	public List<String> getRepetition() {
		return repetition;
	}

	public void setRepetition(List<String> repetition) {
		this.repetition = repetition;
	}

	public String getMainUserName() {
		return mainUserName;
	}

	public void setMainUserName(String mainUserName) {
		this.mainUserName = mainUserName;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	/**
	 * バリデーション.
	 * @return
	 */
	public List<ValidationError> validate() {

		// タスク名称の重複チェック
		checkDuplicateTaskName();

		// 実施頻度タイプの区分値チェック
		if (checkValueRepType()) {
			// 実施頻度タイプと実施頻度の相関チェック
			checkRepTypeAndRepetition();
		}

		// 主担当ユーザーの存在チェック
		checkRegistryMainUSer();

		// 開始日の過去日チェック
		checkStartDate();

		return errors.isEmpty() ? null : errors;
	}

	private void checkDuplicateTaskName() {
		TaskServiceImpl service = new TaskServiceImpl();
		// 登録済みのタスクマスタ一覧
		try {
			List<TaskMst> taskMstList = service.findTaskMstByTeamName(teamName);
			for (TaskMst taskMst : taskMstList) {
				// 入力のタスク名称と登録済み別タスクの名称が重複したらエラー
				if (taskMst.taskName.equals(taskName)
						&& (id == null || taskMst.id != id)) {
					errors.add(MsgUtil.getValidationError(
							Constant.ITEM_TASK_NAME,
							Constant.MSG_E006,
							Constant.ITEM_NAME_TASK_NAME,
							taskName));
					break;
				}
			}
		} catch (Exception e) {
			// TODO エラー：チーム見つからないエラーを想定。Exceptionの定義
			e.printStackTrace();
			// 発生しない想定のエラー
			errors.add(MsgUtil.getValidationError(Constant.ITEM_TEAM_NAME, Constant.MSG_E007));
		}
	}

	/**
	 * 実施頻度タイプの区分値チェック.
	 * repTypeがD,W,M,T以外の場合エラーとする.
	 * @return
	 */
	private boolean checkValueRepType() {
		if ((!Constant.REPTYPE_DAYLY.equals(repType))
			&& (!Constant.REPTYPE_WEEKLY.equals(repType))
			&& (!Constant.REPTYPE_MONTHLY.equals(repType))
			&& (!Constant.REPTYPE_TEMP.equals(repType)) ) {
			errors.add(MsgUtil.getValidationError(Constant.ITEM_REP_TYPE, Constant.MSG_E008));
			return false;
		}
		return true;
	}

	/**
	 * 実施頻度タイプと実施頻度の相関チェック.
	 * タイプ：D→実施頻度は指定不可(画面表示しないため発生しない想定)
	 * タイプ：W→曜日の定数と一致しない場合エラー
	 * タイプ：M→１〜３１の数値出ない場合エラー
	 * タイプ：T→実施頻度は指定不可(画面表示しないため発生しない想定)
	 */
	private void checkRepTypeAndRepetition() {
		switch (repType) {
			case Constant.REPTYPE_DAYLY:
				if (!(repetition == null || repetition.size() == 0)) {
					errors.add(MsgUtil.getValidationError(
							Constant.ITEM_REPETITION,
							Constant.MSG_E009,
							Constant.REPTYPE_STR_DAYLY));
				}
				break;
			case Constant.REPTYPE_WEEKLY:
				if (repetition == null || repetition.size() == 0) {
					errors.add(MsgUtil.getValidationError(Constant.ITEM_REPETITION, Constant.MSG_E010));
				} else {
					for (String repStr : repetition) {
						if (!Constant.SET_REPETITION.contains(repStr)) {
							errors.add(MsgUtil.
									getValidationError(Constant.ITEM_REPETITION,
													   Constant.MSG_E011,
													   Constant.REPTYPE_WEEKLY,
													   Constant.REP_WEEKLY_MSG_STR));
							// エラーの重複を防ぐため1つ目でfor文を抜ける
							break;
						}
					}
				}
				break;
			case Constant.REPTYPE_MONTHLY:
				if (repetition == null || repetition.size() == 0) {
					errors.add(MsgUtil.getValidationError(Constant.ITEM_REPETITION, Constant.MSG_E010));
				} else {
					try {
						for (String repStr : repetition) {
							int date = Integer.parseInt(repStr);
							if (date < 1 || date > 31) {
								errors.add(MsgUtil.
										getValidationError(Constant.ITEM_REPETITION,
														   Constant.MSG_E011,
														   Constant.REPTYPE_MONTHLY,
														   Constant.REP_MONTHLY_MSG_STR));
								// エラーの重複を防ぐため1つ目でfor文を抜ける
								break;
							}
						}
					} catch (Exception e) {
						errors.add(MsgUtil.
								getValidationError(Constant.ITEM_REPETITION,
										Constant.MSG_E011,
										Constant.REPTYPE_MONTHLY,
										Constant.REP_MONTHLY_MSG_STR));
					}
				}
				break;
			case Constant.REPTYPE_TEMP:
				if (!(repetition == null || repetition.size() == 0)) {
					errors.add(MsgUtil.getValidationError(
							Constant.ITEM_REPETITION,
							Constant.MSG_E009,
							Constant.REPTYPE_STR_TEMP));
				}

		}
	}

	/**
	 * 主担当ユーザーの存在チェック.
	 */
	private void checkRegistryMainUSer() {
		UserServiceImpl userService = new UserServiceImpl();

		List<User> userList = userService.findUserByName(mainUserName);
		if (userList == null || userList.size() == 0) {
			errors.add(MsgUtil.getValidationError(
					Constant.ITEM_MAIN_USER_NAME,
					Constant.MSG_E005,
					mainUserName
			));
		}
	}

	/**
	 * 開始日の過去日付チェック.
	 */
	private void checkStartDate() {
		try {
			// 新規の場合のみ、開始日が過去日の場合エラーとする
			if (id == null
				&& startDate.compareTo(DateUtil.getDateWithoutTime(new Date())) < 0) {
				errors.add(MsgUtil.getValidationError(
						Constant.ITEM_START_DATE,
						Constant.MSG_E014
				));

			}

		} catch (ParseException e) {
			// TODO エラーハンドリング
			e.printStackTrace();
		}
	}
}
