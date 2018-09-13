package controllers

import javax.inject._
import models.UserTable
import play.api._
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.DurationInt


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */


@Singleton
class HomeController @Inject()(cc: ControllerComponents, config: Configuration)(implicit assetsFinder: AssetsFinder,
                                                         ec: ExecutionContext) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>


    Ok(views.html.index(UserTable("","","")))
  }


  def result(param: String) = Action{implicit request =>

    Ok(views.html.result(param))
  }

  def hello() = Action{implicit request =>
    Ok("hello")
  }

  def hellojob(email: String, passwd: String, userDate: String) = Action{implicit request =>

    Ok(views.html.index(UserTable(email, passwd, userDate)))
  }
  def addTwo(n:Int):Future[Int] = Future { n + 2 }

  def addTwonoFuture(n: Int):Int = {n*2}

  def addTwoAndDouble(n:Int):Future[Int] =
    addTwo(n).map { x:Int =>
      addTwonoFuture(x)
    }

  def addTwoAddTwo(num: Int, n:Int):Future[Int] = {
    //Thread.sleep(3000)
    addTwo(n).flatMap { n2 => addTwo(n2).map(_ + num) }
  }




  def test() = Action.async{ implicit request =>

    //val a1 = addTwoAndDouble(4)

    val aa1 = addTwoAndDouble(4)


    aa1.flatMap{r1 =>


      println("r1 is " + r1)
      //Await.result(aa1, 3.seconds)

      addTwoAddTwo(r1, 4).map{r2 =>
          Thread.sleep(3000)
          println("r2 is " + r2)

      }
    }


    Await.result(aa1, 3.seconds)

/*
    for{
      a1 <- addTwoAndDouble(4) //12
      a2 <- addTwoAddTwo(a1, 4) //18
    }yield {

      println("a1 is " + a1);
      println("a2 is " + a2);
    }
    */
    //val a2 = addTwoAddTwo(4)

    val b: Future[Int] = Future{
      12
    }

    //Await.result(a2, 2.seconds)

    //a1.map(println)
    //a2.foreach(println)


    println(config.get[Int]("wei.test"))

    b.map(bb =>{
      Ok(Json.obj("name" -> bb))
    })

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
