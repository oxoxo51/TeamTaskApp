package dto.task;

import play.data.validation.Constraints;

/**
 * Created on 2016/05/24.
 */
public class CreateTaskMstDto {

	/**
	 * タスク名.
	 */
	@Constraints.Required
	public String taskName;

	/**
	 * タスク内容.
	 */
	public String taskInfo;

	/**
	 * 実施頻度タイプ.
	 */
	@Constraints.Required
	public String repType;

	/**
	 * 実施頻度.
	 */
	@Constraints.Required
	public String repetition;

	/**
	 * 主担当ユーザー名.
	 */
	public String mainUserName;

	/**
	 * タスク所有チーム名.
	 */
	public String teamName;

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
}
