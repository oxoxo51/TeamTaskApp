package services.implement;

import dto.user.CreateUserDto;
import models.User;
import play.Logger;
import services.UserService;

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
}
