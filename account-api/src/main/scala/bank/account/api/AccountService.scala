package bank.account.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.Service.pathCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object AccountService  {
  val TOPIC_NAME = "account"
}

/**
  * The Hello service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the HelloService.
  */
trait AccountService extends Service {

  def balance(accountNumber: String): ServiceCall[NotUsed, Double]

  def transactionCount: ServiceCall[NotUsed, Long]

  def deposit(accountNumber: String): ServiceCall[Transaction, Done]

  def withdraw(accountNumber: String): ServiceCall[Transaction, Done]


  override final def descriptor = {
    import Service._
    // @formatter:off
    named("hello")
      .withCalls(
        pathCall("/api/account/:accountNumber/balance", balance _),
        pathCall("/api/account/:accountNumber/deposit", deposit _),
        pathCall("/api/account/:accountNumber/withdraw", withdraw _),
        pathCall("/api/transaction/count", transactionCount _)
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

/**
  * The greeting message class.
  */
case class Transaction(amount: Double)

object Transaction {
  implicit val format: Format[Transaction] = Json.format[Transaction]
}
