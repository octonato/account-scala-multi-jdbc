package bank.account

import bank.account.api.AccountService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire._
import play.api.db._

class AccountLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new BankApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new BankApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[AccountService])
}

abstract class BankApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with JdbcPersistenceComponents
    with HikariCPComponents
    with AhcWSComponents {

  lazy val accountRepository = wire[AccountRepository]
  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[AccountService](wire[AccountServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = AccountSerializerRegistry

  // Register the Hello persistent entity
  persistentEntityRegistry.register(wire[AccountEntity])

}
