package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.SampleModel

interface SampleRepository {
    suspend fun getSampleData(): SampleModel
}
