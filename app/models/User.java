package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * このサービスを利用するユーザー.<br>
 * 初回利用時に新規登録する.<br>
 * Created on 2016/05/07.
 */
@Entity
public class User extends Model {

	/**
	 * DB上のID.
	 */
	@Id
	public long id;

	/**
	 * ユーザー名.
	 */
	public String userName;

	/**
	 * パスワード.
	 */
	public String password;

	/**
	 * 所属するチーム.
	 */
	@ManyToMany
	public List<Team> teams;

	/**
	 * Finder.
	 */
	public static Find<Long, User> find = new Find<Long, User>() {};

}
