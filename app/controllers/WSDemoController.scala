package controllers

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.{JsPath, Json, Reads}
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

  val mashapeApiKey = "FbkOCUKf8hMopHO5I8u2pGBUli2Dp14ZSMIjsnh0ky1T5tmpK1"

  private def getAvailableDomains(phrase: String): Future[List[String]] = {
    val domainSearchUrl = "https://domainsearch.p.mashape.com/index.php?name="
    val futureDomains = ws.url(domainSearchUrl + phrase)
      .withHttpHeaders("X-Mashape-Key" -> mashapeApiKey,
        "Accept" -> "application/json").get
    futureDomains map { domainNames =>
      //val domains: Map[String, String] =
        domainNames.json.as[List[String]]

    }
  }



  private def getNounPhrases(text: String): Future[List[String]] = {
    val textAnalysisUrl = "https://textanalysis.p.mashape.com/textblob-noun-phrase-extraction"
    val futureKeyPhrases = ws.url(textAnalysisUrl)
      .withHttpHeaders("X-Mashape-Key" -> mashapeApiKey,
        "Content-Type" -> "application/x-www-form-urlencoded",
        "Accept" -> "application/json")
      .post(Map("text" -> Seq(text)) )
    futureKeyPhrases map { keyPhrases =>
      (keyPhrases.json \ "noun_phrases").as[List[String]].map(phrase => phrase.replace(" ", ""))
    }
  }

  private def getAvailableDomains(phrases: List[String]): Future[List[String]] = {
    val a: List[Future[List[String]]] = phrases.map(phrase => getAvailableDomains(phrase))
    Future.sequence(a).map(_.flatten)
    //Future.sequence(phrases.flatMap(phrase => getAvailableDomains(phrase)))
  }


  def getDomainNames(text: String) = Action.async { implicit request =>

    getNounPhrases(text).flatMap { phrases => getAvailableDomains(phrases).map(domains => Ok(Json.obj("available_domains" -> domains.mkString(";")))) };
  }

    /*
    for {
      phrases <- getNounPhrases(text)
      domains <- getAvailableDomains(phrases)
    } yield {
      Ok(Json.obj("available_domains" -> domains.mkString(";")))
    }
  }*/




}
