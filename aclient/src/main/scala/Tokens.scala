package com.example

trait ClientToken {
  val value: String
  val secret: String
  def asDispatchToken = dispatch.oauth.Token(value, secret)
}

case class RequestToken(value: String, secret: String) extends ClientToken

object AccessToken {
  def fromCookieString(str: String) = str.split("!!!") match {
    case Array(vl, sec, ver) => AccessToken(vl, sec, ver)
    case _ => error("invalid cookie value %s" format str)
  }
}
case class AccessToken(value: String, secret: String, verifier: String) extends ClientToken {
  def toCookieString = "%s!!!%s!!!%s!!!" format(value, secret, verifier)
}
