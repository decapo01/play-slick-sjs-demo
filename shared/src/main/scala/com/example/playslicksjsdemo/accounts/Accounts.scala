package com.example.playslicksjsdemo.accounts

import java.util.UUID

import com.example.playslicksjsdemo.common.Repo._

object Accounts {

  final case class UserId(value: UUID) extends Id[UUID]

  final case class User(

    id       : UserId,
    email    : String,
    password : String

  ) extends Entity[UserId]


  sealed trait UserCriteria[A] extends Criteria[A]

  final case class IdEq(value: UserId)    extends UserCriteria[UserId]
  final case class IdNotEq(value: UserId) extends UserCriteria[UserId]
  final case class EmailEq(value: String) extends UserCriteria[String]

  sealed trait UserSort extends Sort

  case object IdAsc  extends UserSort
  case object IdDesc extends UserSort

  //trait UserRepo extends Repo[UserId,User,UserCriteria[_],UserSort]


  sealed trait Role
  case object UserRole  extends Role
  case object GuestRole extends Role
}
