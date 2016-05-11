package models;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * タスクのマスタ.<br>
 * 日々のタスクはこのマスタに登録された内容を雛形に作成される.<br>
 * Created on 2016/05/11.
 */
@Entity
public class TaskMst {

	/**
	 * DB上のID.
	 */
	@Id
	public long id;

	/**
	 * タスク名.
	 */
	public String taskName;

	/**
	 * タスク内容.
	 */
	public String taskInfo;

	/**
	 * 実施頻度タイプ.
	 */
	public String repType;

	/**
	 * 実施頻度.
	 */
	public String repetition;

}
