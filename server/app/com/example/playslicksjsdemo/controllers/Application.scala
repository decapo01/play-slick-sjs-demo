package com.example.playslicksjsdemo.controllers

import java.util.UUID

import com.example.palyslicksjsdemo.repo.UserRepos
import com.example.palyslicksjsdemo.repo.UserRepos.UserRepo
import com.example.playslicksjsdemo.accounts.Accounts
import com.example.playslicksjsdemo.accounts.Accounts.{EmailEq, User, UserId, UserRole}
import javax.inject._
import com.example.playslicksjsdemo.shared.SharedMessages
import com.example.providers.UUIDProvider
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.I18nSupport
import views.html.registerFormView
import views.html.loginView

import scala.concurrent.{ExecutionContext, Future}

final case class LoginData(

  email    : String,
  password : String
)


class Application @Inject()(userRepo: UserRepos.UserRepo,
                            uuidProvider: UUIDProvider,
                            cc: ControllerComponents) (
                            implicit ec: ExecutionContext)extends AbstractController(cc)
                                                          with I18nSupport {


  val registerForm = Form[User](
    mapping(
      "id" -> mapping(
        "value" -> uuid
      )(UserId.apply)(UserId.unapply),
      "email" -> email,
      "password" -> nonEmptyText(minLength = 8,maxLength = 18)
    )(User.apply)(User.unapply)
  )

  val loginForm = Form[LoginData](
    mapping(
      "email"    -> email,
      "password" -> nonEmptyText
    )(LoginData.apply)(LoginData.unapply)
  )

  def index = Action {

    val x = null  // compiles :-(

    Ok(views.html.index(SharedMessages.itWorks))
  }

  def getRegister = Action { implicit req: Request[_] =>

    val userId: UserId = UserId(uuidProvider.randomUUID)

    val user: User = User(userId,"","")

    Ok(registerFormView(registerForm.fill(user)))
  }

  def postRegister = Action.async { implicit req =>


    registerForm.bindFromRequest().fold(

      formWithErrors => {
        Future.successful{

          BadRequest(registerFormView(formWithErrors))
        }
      },
      user => {

        for {
          maybeUser <- userRepo.findByCriteria(Seq(Accounts.EmailEq(user.email)))
          res <-
            maybeUser match {
              case Some(u) => Future.successful(
                BadRequest(registerFormView(registerForm.fill(user)))
              )
              case None    => userRepo.insert(user,UserRole).map { _ =>

                Redirect(routes.Application.index()).flashing(("msg","User Created"))
              }
            }
        }
        yield {

          res
        }
      }
    )
  }

  def getLogin = Action { implicit req =>

    def defaultLogin = LoginData("","")

    Ok(loginView(loginForm.fill(defaultLogin)))
  }

  def postLogin = Action.async { implicit req =>

    loginForm.bindFromRequest().fold(

      formWithErrors => {
        Future.successful{
          BadRequest(loginView(formWithErrors))
        }
      },
      loginData => {

        for {
          userOpt <- userRepo.findByCriteria(Seq(EmailEq(loginData.email)))
          result  <-
            userOpt match {
              case None => {

                val _form = loginForm.fill(loginData).withGlobalError("User Not Found")

                Future.successful(BadRequest(loginView(_form)))
              }
              case Some((u,r)) => {

                if (u.password == loginData.password) {

                  Future.successful(Redirect(routes.Application.index()).withSession(("user",u.email)))
                }
                else {

                  val _form = loginForm.fill(loginData).withGlobalError("Passwords dont match")

                  Future.successful(BadRequest(loginView(_form)))
                }
              }
            }
        }
        yield {
          result
        }
      }
    )
  }
}
