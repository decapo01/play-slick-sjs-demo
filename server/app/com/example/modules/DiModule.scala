package com.example.modules

import com.example.palyslicksjsdemo.repo.CrmRepo.{ContactSlickRepo, CustomerSlickRepo}
import com.example.palyslicksjsdemo.repo.UserRepos.{UserRepo, UserSlickRepo}
import com.example.playslicksjsdemo.crm.Crm.{ContactRepo, CustomerRepo}
import com.example.providers.{UUIDProvider, UUIDProviderDefault}
import com.google.inject.AbstractModule
import javax.inject.Inject

class DiModule @Inject() extends AbstractModule{

  override def configure(): Unit = {

    bind(classOf[UserRepo]).to(classOf[UserSlickRepo])
    bind(classOf[UUIDProvider]).to(classOf[UUIDProviderDefault])
    bind(classOf[CustomerRepo]).to(classOf[CustomerSlickRepo])
    bind(classOf[ContactRepo]).to(classOf[ContactSlickRepo])
  }
}
