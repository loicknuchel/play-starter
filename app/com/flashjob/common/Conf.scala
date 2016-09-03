package com.flashjob.common

import global.GenericRepository
import com.flashjob.domain.models.{ JobOffer, User }
import play.api.Configuration
import play.api.i18n.DefaultLangs

case class Conf(configuration: Configuration) {
  object App {
    val langs = new DefaultLangs(configuration).availables
  }
  object Repositories {
    val jobOffer = GenericRepository.Collection[JobOffer]("JobOffer")
    val user = GenericRepository.Collection[User]("User")
  }
}
