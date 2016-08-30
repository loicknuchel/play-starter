package com.flashjob.domain.repositories

import com.flashjob.domain.models.{ JobOfferNoId, JobOffer }
import global.GenericRepository

trait JobOfferRepository extends GenericRepository[JobOffer, JobOffer.Id, JobOfferNoId] {
}
