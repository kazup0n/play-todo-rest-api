package infra.jdbc

import skinny.orm.{SkinnyNoIdCRUDMapper}

trait CRUDMapper[E] extends SkinnyNoIdCRUDMapper[E] {

  override def primaryKeyFieldName: String = "id"

  def toAttributes(record: E): Seq[(Symbol, Any)]

}
