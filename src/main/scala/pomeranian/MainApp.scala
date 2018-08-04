package pomeranian

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import pomeranian.utils.AppConfiguration

//#main-class
object MainApp extends App with RoutesHandler {

  // set up ActorSystem and other dependencies here
  //#main-class
  //#server-bootstrapping
  implicit val system: ActorSystem = ActorSystem("HttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  //#server-bootstrapping

  //val userRegistryActor: ActorRef = system.actorOf(UserRegistryActor.props, "userRegistryActor")

  //#main-class
  // from the UserRoutes trait
  //lazy val routes: Route = userRoutes
  //#main-class

  //#http-server
  val host = AppConfiguration.httpHost
  val port = AppConfiguration.httpPort
  Http().bindAndHandle(routes, host, port)
  //  if (AppConfiguration.useHttps) {
  //    val serverContext: HttpsConnectionContext = {
  //      val password = "abc123".toCharArray
  //      val context = SSLContext.getInstance("TLS")
  //      val ks = KeyStore.getInstance("PKCS12")
  //      ks.load(getClass.getClassLoader.getResourceAsStream("server.pfx"), password)
  //      val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
  //      keyManagerFactory.init(ks, password)
  //      context.init(keyManagerFactory.getKeyManagers, null, new SecureRandom)
  //      // start up the web server
  //      ConnectionContext.https(sslContext = context)
  //    }
  //    Http().bindAndHandle(routes, AppConfiguration.httpHost, AppConfiguration.httpsPort, connectionContext = serverContext)
  //  }

  println(s"Server online at http://$host:$port/")

  Await.result(system.whenTerminated, Duration.Inf)
  //#http-server
  //#main-class
}
//#main-class
//#quick-start-server
