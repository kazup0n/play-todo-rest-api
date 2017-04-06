package infra

import com.google.inject.ImplementedBy
import domain.support.JdbcIOContextProvider

@ImplementedBy(classOf[JdbcIOContextProvider])
trait IOContextProvider {
  def getContext: IOContext
}

trait IOContext