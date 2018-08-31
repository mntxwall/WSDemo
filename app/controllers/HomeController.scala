package controllers

import javax.inject._
import models.UserTable
import play.api._
import play.api.data.Forms._
import play.api.data.Form
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */


@Singleton
class HomeController @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>


    Ok(views.html.index())
  }


  def result(param: String) = Action{implicit request =>

    Ok(views.html.result(param))
  }

  def hello() = Action{implicit request =>
    Ok("hello")
  }


  def deal() = Action{ implicit request: Request[AnyContent] =>


    val userForm = Form(
    mapping(
      "example-email-text" ->text,
      "example-passwd-text" -> text(minLength = 5),
      "example-date-text" -> text
    )(UserTable.apply)(UserTable.unapply))

    userForm.bindFromRequest.fold(
  formWithErrors => {
    // binding failure, you retrieve the form containing errors:
    BadRequest(views.html.result(formWithErrors.toString))
  },
      UserTable => {
    /* binding success, you get the actual value. */
    Redirect(routes.HomeController.result(UserTable.password))
  }
)

   // Redirect(routes.HomeController.index)
  }
}
