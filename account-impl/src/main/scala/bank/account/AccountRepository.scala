package bank.account

import slick.lifted.TableQuery

class AccountRepository {

  private var count: Long = 0

  def increase() = count = count + 1
  def getCount: Long = count
}
