package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * タスク管理を行うためのチーム.<br>
 * チームにはユーザーが所属する.<br>
 * タスクはチームに紐つけて登録する.<br>
 * Created on 2016/05/10.
 */
@Entity
public class Team extends Model{

	/**
	 * DB上のID.
	 */
	@Id
	public long id;

	/**
	 * チーム名.
	 */
	public String teamName;

	/**
	 * 作成ユーザー.
	 */
	@ManyToOne(cascade= CascadeType.ALL)
	public User createUser;

	/**
	 * 所属しているユーザー.
	 */
	@ManyToMany(mappedBy = "teams", cascade = CascadeType.ALL)
	public List<User> members;


}
