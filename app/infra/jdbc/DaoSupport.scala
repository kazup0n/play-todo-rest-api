package infra.jdbc


import play.api.Logger
import scalikejdbc._

import scala.util.Try

trait DaoSupport[E] {

  val versionName: String = "version"

  val mapper: CRUDMapper[E]

  def findAll()(implicit session: DBSession): Try[Seq[E]] = Try {
    mapper.findAll()
  }

  def deleteById(id: String, version: Option[Long])(implicit session: DBSession): Try[Boolean] =
    Try {
      Logger.debug(s"Deleting ${id}")
      mapper.deleteBy(sqls.eq(mapper.primaryKeyField, id)
      ) match {
        case n:Int if n<=0 => {
          Logger.debug(s"Delete failed(id=${id})")
          throw new IllegalStateException("No entity")
        }
        case _ => {
          Logger.debug(s"Deleted(id=${id} )")
          true
        }
      }
    }

  def insertOrUpdate(id: String, record: E, version: Option[Long])(implicit session: DBSession): Try[E] =
    Try {
      Logger.debug(s"insert/update ${record.toString}")
      version match {
        case Some(_) => {
          val where = sqls.eq(mapper.primaryKeyField, id)
          mapper.updateBy(where)
              .withAttributes(mapper.toAttributes(record)
              .filterNot(_._1.name == mapper.primaryKeyFieldName): _*)
          Logger.info(s"updated ${record.toString}")

          findById(id).get
        }
        case None => {
          mapper.createWithAttributes(mapper.toAttributes(record): _*)
          Logger.info(s"inserted ${record.toString}")
          findById(id).get
        }
      }
    }

  def findById(id: String)(implicit session: DBSession): Try[E] =
    Try {
      mapper.findBy(
        sqls
          .eq(mapper.column.c(mapper.primaryKeyFieldName), id)
      ).get
    }


}
