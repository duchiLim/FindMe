package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.SampleModel
import com.greenbee.findme.domain.repository.SampleRepository

class GetSampleUseCase(private val repository: SampleRepository) {
    suspend operator fun invoke(): SampleModel {
        return repository.getSampleData()
    }
}
