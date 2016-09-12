package controllers;

import constant.Constant;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created on 2016/06/06.
 */
public class Secured extends Security.Authenticator {

	@Override
	public String getUsername(Http.Context ctx) {
		Apps.logClassAndMethodName();
		return ctx.session().get(Constant.ITEM_USER_NAME);
	}

	@Override
	public Result onUnauthorized(Http.Context ctx) {
		Apps.logClassAndMethodName();
		Apps.flashError(Constant.MSG_E002);
		return redirect(routes.Apps.index());
	}


}
