package services.implement;

import dto.user.CreateUserDto;
import models.User;
import play.Logger;
import services.UserService;

import java.util.List;

/**
 * Created on 2016/05/08.
 */
public class UserServiceImpl implements UserService {

	@Override
	public void create(CreateUserDto createUserDto) {
		Logger.info("UserServiceImpl#create");
		User user = new User();
		user.userName = createUserDto.userName;
		user.password = createUserDto.password;
		user.save();
	}

	@Override
	public List<User> findUser(String userName, String password) {
		Logger.info("UserServiceImpl#findUser");
		return User.find.where().eq("userName", userName).eq("password", password).findList();
	}

	@Override
	public List<User> findUserByName(String userName) {
		Logger.info("UserServiceImpl#findUserByName");
		return User.find.where().eq("userName", userName).findList();
	}

}
