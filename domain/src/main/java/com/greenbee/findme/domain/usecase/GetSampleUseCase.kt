package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.SampleModel
import com.greenbee.findme.domain.repository.SampleRepository
import javax.inject.Inject

class GetSampleUseCase @Inject constructor(
    private val repository: SampleRepository,
) {
    suspend operator fun invoke(): SampleModel {
        return repository.getSampleData()
    }
}
