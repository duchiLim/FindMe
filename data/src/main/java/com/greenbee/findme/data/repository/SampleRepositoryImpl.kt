package com.greenbee.findme.data.repository

import com.greenbee.findme.domain.model.SampleModel
import com.greenbee.findme.domain.repository.SampleRepository

class SampleRepositoryImpl : SampleRepository {
    override suspend fun getSampleData(): SampleModel {
        return SampleModel(1, "Clean Architecture Sample")
    }
}
