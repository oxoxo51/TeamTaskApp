package controllers;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created on 2016/06/06.
 */
public class Secured extends Security.Authenticator {

	@Override
	public String getUsername(Http.Context ctx) {
		Logger.info("Secured#getUsername userName:" + ctx.session().get("userName"));
		return ctx.session().get("userName");
	}

	@Override
	public Result onUnauthorized(Http.Context ctx) {
		Logger.info("Secured#onUnauthorized");
		Controller.flash("error", "ログインして下さい。");
		return redirect(routes.Apps.index());
	}


}
