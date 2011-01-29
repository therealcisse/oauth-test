package com.example

trait Templates {
  import unfiltered.response._
  import unfiltered.oauth.{Consumer, Token, UserLike}
  
  def page(body: scala.xml.NodeSeq) = Html(
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title>oauth provider</title>
        <link href="/css/app.css" type="text/css" rel="stylesheet" />
      </head>
      <body>
        <div id="container">
          <h1><a href="/">service provider</a></h1>
          {body}
        </div>
      </body>
    </html>
  )
  
  def index(urlBase: String, currentUser: Option[UserLike]) = page(
    <div>{
        currentUser match {
          case Some(user) => <div>
            <p>Bienvenue {user.id}</p>
            <p> Afficher vos connections <a href="/connections">vos connections</a></p>
          </div>
          case _ => <p>vous n'etes pas logged in</p>
        }
      }
      <p>Vos endpoints</p>
      <ul id="oauth-endpoints">
        <li>{urlBase}oauth/request_token</li>
        <li>{urlBase}oauth/authorize</li>
        <li>{urlBase}oauth/access_token</li>
      </ul>
      <a href="http://localhost:8081/">Demarrer le processus &rarr;</a>
    </div>
  )
  
  def authorizationForm(consumerName: String, token: String, approve: String, deny: String) = page(
    <div>
      <form action="/oauth/authorize" method="POST">
        <p>
          Une application tierce partie appele <strong>{consumerName}</strong> veut avoir acces a tes donnees.
        </p>
        <input type="hidden" name="oauth_token" value={token} />
        <div id="oauth-opts">
          <input type="submit" name="submit" value={approve} />
          <input type="submit" name="submit" value={deny} />
        </div>
      </form>
    </div>
  )
  
  def deniedNotice(consumerName: String) = page(
    <div>Vous avez refuser l'acces a <strong>{consumerName}</strong></div>
  )
  
  def oobNotice(verifier: String) = page(
    <p>Entrez le code suivant a votre client: <strong>{verifier}</strong></p>
  )
  
  def loginForm(token: String) = page(
    <div>
      <form action="/authenticate" method="POST">
        <p>Connectez-vous. Quelqu'un veut votre autorization pour acceder a tes donnees</p>
        <input type="hidden" name="oauth_token" value={token}/>
        <dl>
          <dt><label for="username">Login</label></dt>
          <dd><input type="text" name="username" value="jim"/></dd>
          <dt><label for="password">Mot de passe</label></dt>
          <dd><input type="password" name="password" value="jim"/></dd>
          <dt></dt>
          <dd><input type="submit" value="Login" /></dd>
        </dl>
      </form>
    </div>
  )
  
  def connections(consumers: Seq[(Token, Consumer)]) = page(
    <div>
      <h2>Connections</h2> {
        if(consumers.isEmpty) <li>Vous n'avez pas des connections</li>
        else {
          <p>Vous avez des connections avec les applications suivant</p>
        }
      } {
        consumers.map { (_: (Token, Consumer)) match {
          case (t, ExampleConsumer(key,_,name)) => 
            <li>
              <strong>{name}</strong>
              <a href={"/connections/disconnect/%s" format(t.key) }>Deconnectez-vous</a>
            </li>
          case _ => <li>?</li>
        } }
      }
    </div>
  )
}
