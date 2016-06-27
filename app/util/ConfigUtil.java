package util;

import play.Play;

import java.util.List;
import java.util.Optional;

/**
 * Created on 16/06/25.
 */
public class ConfigUtil {

	public static Optional<String> get(String key) {
		return Optional.ofNullable(Play.application().configuration().getString(key));
	}

	public static Optional<List<String>> getByList(String key) {
		return Optional.ofNullable(Play.application().configuration().getStringList(key));
	}
}
