package controllers

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WSDemoController @Inject() (ws:WSClient, cc: ControllerComponents) (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>


    val complexRequest: WSRequest =
      ws.url("https://jsonplaceholder.typicode.com/todos/1")

    val futureResponse: Future[WSResponse] = complexRequest.get()

      futureResponse.map{
        response => {
          Logger.debug("The title is " + response.json("title").toString())
          //Logger.debug(response.json.toString)
        }

    }

   // val nameReads: Reads[String] = (JsPath \ "name").read[String]


    /*response =>
       (response.json \ "userId" \ "id" \ "title" \ "complete").as[String]*/
    //futureResponse.map(i => Logger.debug("hello " + i))
   // Logger.debug(futureResponse.toString)

    Ok(views.html.ws())


  }



}
