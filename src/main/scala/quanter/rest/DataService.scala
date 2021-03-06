/**
  *
  */
package quanter.rest


import quanter.actors.data.{DataManagerActor, RequestBarData, RequestIndicatorData, RequestTickData}
import spray.routing.HttpService

/**
  * 提供一些非推送服务。
  */
trait DataService extends HttpService {
  val dataManager = actorRefFactory.actorSelection("/user/" + DataManagerActor.path)

  val dataServiceRoute = {
    get {
      path("data" / "symbols") {
        complete("获取可交易的代码表")
      }~
      path("data" /"symbols" / "equity") {
        complete("股票列表")
      }~
      path("data" /"symbols" / "future") {
        complete("")
      }~
      path("data" /"symbols" / "option") {
        complete("")
      } ~
      path("data" /"symbols" / "spif") {
        complete("")
      } ~
      path("data" / "finance") {
        complete("")
      }~
      path("data" / "finance"/"pe_ratio"/ "GT" /DoubleNumber) { // 静态市盈率 > DOUBLE
        ratio =>
          complete("")
      }~
      path("data" / "finance"/"pe_ratio"/ "LT" /DoubleNumber) { // 静态市盈率 < DOUBLE
        ratio =>
          complete("")
      }~
      path("data" / "market") {
        complete("")
      }
    }~
    post {
      path("data"/ Rest) { topic =>
        requestInstance {
          request =>
            complete {
              _subscribe(topic, request.entity.data.asString)
            }
        }
      }
    }
  }

//  private def _subscribe(topic: String, subscription: String): String = {
//    dataManager ! RequestIndicatorData(subscription, topic)
//    """{"code":0}"""
//  }

  private def _subscribe(topic: String, subscription: String): String = {
    try {
      val arr = subscription.split(",")
      val symbol = arr(0)
      val _type = arr(1)

      _type match {
        case "TICK" => dataManager ! RequestTickData(topic, subscription)
        case "BAR" =>dataManager ! RequestBarData(topic, subscription)
        case _ =>dataManager ! RequestIndicatorData(subscription, subscription)
      }
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

}
