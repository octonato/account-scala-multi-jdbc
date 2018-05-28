package bank.account

import akka.Done
import akka.persistence.ReplyToStrategy
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonMigration, JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{JsObject, JsString, Json}
import slick.ast.Aggregate

import scala.collection.immutable.Seq

class AccountEntity extends PersistentEntity {

  override type Command = AccountCommand[_]
  override type Event = AccountEvent
  override type State = Account

  override def initialState = Account(0.0)

  override def behavior = {
    case Account(balance) =>
      Actions()
        .onCommand[Deposit, Done] {

          case (Deposit(amount), ctx, state) =>
            ctx.thenPersist(Deposited(amount)) { _ => ctx.reply(Done)}
        }
        .onCommand[Withdraw, Done] {

          case (Withdraw(amount), ctx, state) =>
            if (balance - amount >= 0) {
              ctx.thenPersist(Withdrawn(amount)) { _ => ctx.reply(Done)}
            } else {
              ctx.invalidCommand(s"Insufficient balance. Can't withdraw $amount")
              ctx.done
            }
        }
        .onReadOnlyCommand[GetBalance.type , Double] {
          case (GetBalance, ctx, state) => ctx.reply(state.balance)
        }
        .onEvent {
          case (Deposited(amount), state) => state.copy(balance + amount)
          case (Withdrawn(amount), state) => state.copy(balance - amount)
        }
  }
}

case class Account(balance: Double)

sealed trait AccountCommand[R] extends ReplyType[R]

case object GetBalance extends AccountCommand[Double]

case class Deposit(amount: Double) extends AccountCommand[Done]
object Deposit {
  implicit val format = Json.format[Deposit]
}

case class Withdraw(amount: Double) extends AccountCommand[Done]
object Withdraw {
  implicit val format = Json.format[Withdraw]
}

sealed trait AccountEvent
  extends AggregateEvent[AccountEvent] {
  override def aggregateTag = AccountEvent.tag
}

object AccountEvent {
  val tag: AggregateEventTag[AccountEvent] = AggregateEventTag("account")
}

case class Deposited(amount: Double) extends AccountEvent

object Deposited {
  implicit val format = Json.format[Deposited]
}
case class Withdrawn(amount: Double) extends AccountEvent

object Withdrawn {
  implicit val format = Json.format[Withdrawn]
}


object AccountSerializerRegistry extends JsonSerializerRegistry {

  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[Deposit],
    JsonSerializer[Withdraw],
    JsonSerializer[Deposited],
    JsonSerializer[Withdrawn]
  )
}
