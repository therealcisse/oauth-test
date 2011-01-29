package com.example

import unfiltered.response.Html
import dispatch.oauth.Token

trait Templates {

  def page(body: scala.xml.NodeSeq) = Html(
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title>client d'oauth - aClient</title>
        <link href="/css/app.css" type="text/css" rel="stylesheet"/>
      </head>
      <body>
        <div id="container">
          <h1><a href="/">client d'oauth - aClient</a></h1>
          {body}
        </div>
      </body>
    </html>
  )

  def index = page(<a href={"http://localhost:%s/" format Client.port} >connectez-vous avec votre service provider</a>)

  def apiCall(response: scala.xml.NodeSeq) = page(
    <div><a href="/disconnect">Deconnexion</a></div>
    <div>
      <h2>appel effectue</h2>
      <pre>{response}</pre>
    </div>
  )

  def tokenList(toks: Iterable[ClientToken]) = page(
    <div>
      <p>
        <a href={"http://localhost:%s/connect" format Client.port} class="btn" >connectez-vous avec votre service provider</a>
      </p>
      { if(toks.isEmpty) <p>pas de cles a afficher</p> }
      <ul>{
        toks.map { t => t match {
          case RequestToken(value,_) => <li>Cle non-autorize <strong>{ value }</strong> <a href={"/tokens/delete/%s" format value}>Supprimer</a></li>
          case AccessToken(value,_,_) => <li>Cle d'acces <strong>{ value }</strong> (access) <a href={"/tokens/delete/%s" format value}>Supprimer</a></li>
        } } }
      </ul>
    </div>
  )
}
