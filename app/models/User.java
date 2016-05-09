package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created on 2016/05/07.
 */
@Entity
public class User extends Model {

	/**
	 * DB上のID.
	 */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;

	/**
	 * ユーザー名.
	 */
	public String userName;

	/**
	 * パスワード.
	 */
	public String password;



}
