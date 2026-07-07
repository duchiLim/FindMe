package com.greenbee.findme.data.repository

import com.greenbee.findme.data.di.PublicDataServiceKey
import com.greenbee.findme.data.remote.AbandonmentPublicApi
import com.greenbee.findme.domain.model.SampleModel
import com.greenbee.findme.domain.repository.SampleRepository
import javax.inject.Inject

class SampleRepositoryImpl @Inject constructor(
    private val abandonmentPublicApi: AbandonmentPublicApi,
    @PublicDataServiceKey private val serviceKey: String,
) : SampleRepository {

    override suspend fun getSampleData(): SampleModel {
        if (serviceKey.isBlank()) {
            return SampleModel(1, "API 키를 local.properties에 설정해 주세요")
        }

        return runCatching {
            val response = abandonmentPublicApi.getSidoList(
                serviceKey = serviceKey,
                numOfRows = 10,
                pageNo = 1,
            )
            val header = response.response.header
            require(header.resultCode == "00") { header.resultMsg }

            val totalCount = response.response.body.totalCount ?: 0
            SampleModel(1, "Retrofit 연동 완료 (시도 ${totalCount}개)")
        }.getOrElse {
            SampleModel(1, "Clean Architecture Sample")
        }
    }
}
