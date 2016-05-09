package controllers;

import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

/**
 * Created on 2016/05/08.
 */
public class Apps extends Controller {

	/**
	 * エラー発生時のflashメッセージ表示用.
	 * @param call
	 * @param flashKey
	 * @param flashMessage
	 * @return
	 */
	public static Result fail(Call call, String flashKey, String flashMessage) {
		ctx().flash().put(flashKey, flashMessage);
		return redirect(call);
	}


	/**
	 * ルートにアクセスしたときのAction
	 * @return
	 */
	public Result index() {
		// TODO ログイン済みの場合はチーム一覧画面へリダイレクトする
		return ok(index.render("Welcome!"));
	}
}

