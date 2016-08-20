package services.implement;

import dto.user.ChangePwdDto;
import dto.user.CreateUserDto;
import models.User;
import play.Logger;
import services.UserService;

import java.util.Date;
import java.util.List;

/**
 * Created on 2016/05/08.
 */
public class UserServiceImpl implements UserService {

	@Override
	public User create(CreateUserDto createUserDto) {
		Logger.info("UserServiceImpl#edit");
		User user = new User();
		user.userName = createUserDto.getUserName();
		user.password = createUserDto.getPassword();
		user.save();
		// 再取得して返却する(id含めたmodelを渡すため)
		return User.find.where().eq("userName", createUserDto.getUserName()).findList().get(0);
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

	@Override
	public void changePwd(ChangePwdDto changePwdDto) {
		Logger.info("UserServiceImpl#changePwd");
		User user = findUserByName(changePwdDto.getUserName()).get(0);
		user.password = changePwdDto.getPasswordToBe();
		user.update();
	}

	@Override
	public User findUserById(Long id) {
		return User.find.byId(id);
	}

	@Override
	public void updateLastLoginDate(User user) {
		user.lastLoginDate = new Date();
		user.update();
	}

}
