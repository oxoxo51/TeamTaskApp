package dto.task;

import constant.Constant;
import models.TaskMst;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import services.implement.TaskServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 2016/05/24.
 */
public class CreateTaskMstDto {

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
	private String repetition;

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

	public CreateTaskMstDto() {
		errors = new ArrayList<ValidationError>();
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

	public String getRepetition() {
		return repetition;
	}

	public void setRepetition(String repetition) {
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
		return errors.isEmpty() ? null : errors;
	}

	private void checkDuplicateTaskName() {
		TaskServiceImpl service = new TaskServiceImpl();
		// 登録済みのタスクマスタ一覧
		try {
			List<TaskMst> taskMstList = service.findTaskMstByTeamName(teamName);
			for (TaskMst taskMst : taskMstList) {
				// 入力のタスク名称と登録済みの名称が重複したらエラー
				if (taskMst.taskName.equals(taskName)) {
					errors.add(new ValidationError("taskName", "タスク：" + taskName + " は既に登録されています。"));
					break;
				}
			}
		} catch (Exception e) {
			// TODO エラー：チーム見つからないエラーを想定。Exceptionの定義
			e.printStackTrace();
			// 発生しない想定のエラー
			errors.add(new ValidationError("teamName", "チームが見つかりません。"));
		}
	}

	/**
	 * 実施頻度タイプの区分値チェック.
	 * repTypeがD,W,M以外の場合エラーとする.
	 * @return
	 */
	private boolean checkValueRepType() {
		if ((!Constant.REPTYPE_DAYLY.equals(repType))
			&& (!Constant.REPTYPE_WEEKLY.equals(repType))
			&& (!Constant.REPTYPE_MONTHLY.equals(repType))) {
			errors.add(new ValidationError("repType", "実施頻度タイプは D,W,M のいずれかを指定してください。"));
			return false;
		}
		return true;
	}

	/**
	 * 実施頻度タイプと実施頻度の相関チェック.
	 * タイプ：D→実施頻度は指定不可
	 * タイプ：W→曜日の定数と一致しない場合エラー
	 * タイプ：M→１〜３１の数値出ない場合エラー
	 */
	private void checkRepTypeAndRepetition() {
		switch (repType) {
			case Constant.REPTYPE_DAYLY:
				if (!(repetition == null || "".equals(repetition))) {
					errors.add(new ValidationError("repetition", "実施頻度タイプが D の場合、実施頻度は指定できません。"));
				}
				break;
			case Constant.REPTYPE_WEEKLY:
				if (repetition == null || "".equals(repetition)) {
					errors.add(new ValidationError("repetition", "実施頻度を入力してください。"));
				} else {
					String[] repStrAry = repetition.split(",");
					for (String repStr : repStrAry) {
						if (!Constant.SET_REPETITION.contains(repStr)) {
							errors.add(new ValidationError("repetition", "実施頻度タイプが W の場合、実施頻度："
										+ repStr + " は指定できません。"));
						}
					}
				}
				break;
			case Constant.REPTYPE_MONTHLY:
				if (repetition == null || "".equals(repetition)) {
					errors.add(new ValidationError("repetition", "実施頻度を入力してください。"));
				} else {
					String[] repStrAry = repetition.split(",");
					for (String repStr : repStrAry) {
						try {
							int date = Integer.parseInt(repStr);
							if (date < 1 || date > 31) {
								errors.add(new ValidationError("repetition",
										"実施頻度タイプが M の場合、実施頻度は1~31を指定してください。"));
							}
						} catch (Exception e) {
							errors.add(new ValidationError("repetition",
									"実施頻度タイプが M の場合、実施頻度は1~31を指定してください。"));
						}
					}
				}
		}
	}


}
