package com.flashjob.common

import com.flashjob.domain.models.{ User, JobOffer }
import global.GenericRepository
import play.api.Configuration

case class Conf(configuration: Configuration) {
  object Repositories {
    val jobOffer = GenericRepository.Collection[JobOffer]("JobOffer")
    val user = GenericRepository.Collection[User]("User")
  }
}
