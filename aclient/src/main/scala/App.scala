package com.example

import unfiltered.request._
import unfiltered.response._
import unfiltered.Cookie

import dispatch._

import dispatch.oauth._
import dispatch.oauth.OAuth._

import dispatch.json._
import dispatch.liftjson.Js._

import net.liftweb.json._
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._

import org.clapper.avsl.Logger

class App(consumer: Consumer) extends Templates with unfiltered.filter.Plan {
  import QParams._

  private val log = Logger(classOf[App])
  private val svc = :/("localhost", 8080)
  private val tmap = scala.collection.mutable.Map.empty[String, ClientToken]

  object AuthorizedToken {
    def unapply[T](r: HttpRequest[T]) = r match {
      case Cookies(cookies) => cookies("token") match {
        case Some(Cookie(_, value, _, _, _, _)) if !value.isEmpty => Some(AccessToken.fromCookieString(value))
        case _ => None
      }
    }
  }

  def intent = {
    // if we have an access token on hand, make an api call
    // if not, render the current list of tokens
    case GET(Path("/") & AuthorizedToken(at)) =>
      try {
        Http(svc / "api" / "user" <@(consumer, at.asDispatchToken, at.verifier) ># { js  =>
          val response = pretty(render(js))
          log.info("made successful api call %s" format response)
          apiCall(<div>{response}</div>)
        })
      } catch { case e =>
        val msg = "there was an error making an api request: %s" format e.getMessage
        log.warn(msg)
        apiCall(<div>Vous devez vous connecter avant d'effectuer un appel</div>)
      }

    // show a list of tokens, if any, and a way to connect
    case GET(Path("/")) => tokenList(tmap.values)

    // kickoff for oauth dance party
    case GET(Path("/connect")) =>
      val token = Http(svc.POST / "oauth" / "request_token" <@(consumer, "http://localhost:8081/authorized") as_token)
      log.info("fetched token unauthorized request token %s" format token.value)
      tmap += (token.value -> RequestToken(token.value, token.secret))
      Redirect("http://localhost:8080/oauth/authorize?oauth_token=%s" format(token.value))

   // clear the current authorized token
   case GET(Path("/disconnect")) =>
     ResponseCookies(Cookie("token", "")) ~> Redirect("/")

    // post user authorization callback uri
    case GET(Path("/authorized") & Params(params)) =>
      val expected = for {
        verifier <- lookup("oauth_verifier") is
          required("verifier is required") is nonempty("verifier can not be blank")
        token <- lookup("oauth_token") is
          required("token is required") is nonempty("token can not be blank")
      } yield {
        log.info("recieved authorization for token %s from verifier %s" format(token.get, verifier.get))
        val access_token = Http(svc.POST / "oauth" /  "access_token" <@(consumer, tmap(token.get).asDispatchToken, verifier.get) as_token)
        log.info("fetched access token %s" format access_token.value)
        tmap -= token.get
        ResponseCookies(Cookie("token", AccessToken(access_token.value, access_token.secret, verifier.get).toCookieString)) ~> Redirect("/")
      }

      expected(params) orFail { fails =>
        BadRequest ~> ResponseString(fails.map { _.error } mkString(". "))
      }

    case GET(Path(Seg("tokens" :: "delete" ::  key :: Nil))) =>
      tmap -= key
      Redirect("/")
  }
}
