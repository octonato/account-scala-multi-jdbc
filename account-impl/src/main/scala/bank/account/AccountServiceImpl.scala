package bank.account

import bank.account.api.AccountService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class AccountServiceImpl (persistentEntityRegistry: PersistentEntityRegistry,
                          accountRepository: AccountRepository) extends AccountService {

  val logger = LoggerFactory.getLogger(getClass)

  override def balance(accountNumber: String) = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[AccountEntity](accountNumber)
    logger.info(s"get balance for $accountNumber")
    ref.ask(GetBalance)
  }

  override def deposit(accountNumber: String) = ServiceCall { req =>
    val ref = persistentEntityRegistry.refFor[AccountEntity](accountNumber)
    logger.info(s"make a deposit of ${req.amount} on $accountNumber")
    ref.ask(Deposit(req.amount))
  }

  override def withdraw(accountNumber: String) = ServiceCall { req =>
    val ref = persistentEntityRegistry.refFor[AccountEntity](accountNumber)
    logger.info(s"make a withdraw of ${req.amount} on $accountNumber")
    ref.ask(Withdraw(req.amount))
  }

  override def transactionCount = ServiceCall { _ =>
    Future.successful(accountRepository.getCount)
  }
}
