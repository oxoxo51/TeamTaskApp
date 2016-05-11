package models;

import javax.persistence.*;
import java.util.Date;

/**
 * 実施日毎のタスク.<br>
 * 日付ごとの画面表示の際に未作成の場合、タスクマスタから新規作成される.<br>
 * 週ごと、月ごとのタスクも毎日トラン作成され、実施日の場合に実施対象フラグを立てる.<br>
 * 複合キーは面倒そうなのでトラン独自でIDを採番する.<br>
 * Created on 2016/05/11.
 */
@Entity
public class TaskTrn {

	/**
	 * DB上のID.
	 */
	@Id
	public long id;

	/**
	 * 元となるマスタ.
	 */
	@ManyToOne(cascade= CascadeType.ALL)
	public TaskMst taskMst;

	/**
	 * 日付.
	 */
	public Date taskDate;

	/**
	 * 実施対象フラグ.
	 */
	public String operationFlg;

	/**
	 * 実施ユーザーID.
	 */
	@OneToOne
	public User operationUser;
}
