package services.implement;

import dto.user.CreateUserDto;
import models.User;
import services.UserService;

/**
 * Created on 2016/05/08.
 */
public class UserServiceImpl implements UserService {

	@Override
	public void create(CreateUserDto createUserDto) {
		User user = new User();
		user.userName = createUserDto.userName;
		user.password = createUserDto.password;
		user.save();
	}
}
