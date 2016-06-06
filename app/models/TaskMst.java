package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * タスクのマスタ.<br>
 * 日々のタスクはこのマスタに登録された内容を雛形に作成される.<br>
 * Created on 2016/05/11.
 */
@Entity
public class TaskMst extends Model {

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
	 * タスク所有チーム.
	 */
	@ManyToOne(cascade= CascadeType.ALL)
	public Team taskTeam;

	/**
	 * 実施頻度タイプ.
	 */
	public String repType;

	/**
	 * 実施頻度.
	 */
	public String repetition;

	/**
	 * 主担当ユーザー.
	 */
	@ManyToOne(cascade= CascadeType.ALL)
	public User mainUser;

	/**
	 * Finder.
	 */
	public static Find<Long, TaskMst> find = new Find<Long, TaskMst>() {};

}
