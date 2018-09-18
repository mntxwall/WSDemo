package controllers

import java.time.LocalTime

import javax.inject._
import models.UserTable
import play.api._
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.{JsNull, Json}
import play.api.mvc._

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, ExecutionContext, Future, blocking}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


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


  private def doSearch(a: String, b: String):Future[Option[String]] = {

    Future{

      val searchJson = Json.obj("name" -> a, "pet" -> b)

      (searchJson \ "name").asOpt[String]
    }
  }

  def helloSearch(buffer: ListBuffer[String], a: String, b: String, func:(String, String) => Future[Option[String]]): Future[Option[String]] = {

    func(buffer.remove(0), b).map {

      case Some(value) =>
        Logger.info("This is Some " + Some(value).get + " " + LocalTime.now())
        Thread.sleep(5000)
        helloSearch(buffer, a, b, doSearch)
        Some(value)
      case None =>

        Logger.info("This is None " + LocalTime.now())
        Thread.sleep(3000)

        //helloSearch(a, b, doSearch)
        Await.result(helloSearch(buffer, a, b, doSearch), 5.seconds)

    }

    //Thread.sleep(2000)
    //tt.result(5.seconds)
    //Await.result(tt, 5.seconds)
  }

  def test2() = Action{ implicit  request =>

   // val testList:Future[List[String]] = Future{List("A", "B", "C")}

    val testList: ListBuffer[String] = ListBuffer(List("A", "B", "C"): _*)

 //   helloSearch("A", "Hello", doSearch)
    helloSearch(testList, "HelloA", "Hello", doSearch)
/*
    helloSearch(testList, testList.remove(0), "Hello", doSearch).onComplete{
      case Success(value) =>{
        value match {

          case Some(value) => {
            Logger.debug("This is return " + LocalTime.now())
            Thread.sleep(3000)
            helloSearch(testList, testList.remove(0), "Hello", doSearch)
            Some(value)
          }
          case _ => {
            Thread.sleep(4000)
            None
          }

        }

      }
      case Failure(exception) => exception.printStackTrace()

    }*/
    /*
    testList.foreach(
      aaa =>
      Future {
        aaa.foreach{
          testValue  =>
            blocking{


              //Thread.sleep(5000)
            }
            //helloSearch(testValue, "Hello", doSearch).onComplete(xx => Thread.sleep(1000))
            //
            /*for {

              aa <- helloSearch(testValue, "Hello", doSearch)
            }yield (aa)*/


        }
      }
    )*/
   // helloSearch(null, "Hello", doSearch)

    Ok("test2")
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
