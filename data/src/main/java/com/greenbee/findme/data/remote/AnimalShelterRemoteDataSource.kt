package com.greenbee.findme.data.remote

import android.util.Log
import com.greenbee.findme.data.di.PublicDataServiceKey
import com.greenbee.findme.data.remote.dto.PublicDataContentDto
import com.greenbee.findme.data.remote.dto.PublicDataResponseDto
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.HttpException

@Singleton
class AnimalShelterRemoteDataSource @Inject constructor(
    private val api: AnimalShelterApi,
    @PublicDataServiceKey private val serviceKey: String,
) {

    suspend fun <T> fetch(
        block: suspend (String) -> PublicDataResponseDto<T>,
    ): PublicDataContentDto<T> {
        val key = serviceKey.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("PUBLIC_DATA_SERVICE_KEY가 설정되지 않았습니다.")

        val response = try {
            block(key)
        } catch (exception: HttpException) {
            throw PublicDataHttpExceptionMapper.toPublicDataException(exception)
        }
        val header = response.response.header
        if (header.resultCode != "00") {
            Log.e(
                TAG,
                "Shelter info API error resultCode=${header.resultCode}, resultMsg=${header.resultMsg}",
            )
        }
        require(header.resultCode == "00") {
            mapApiResultMessage(header.resultMsg)
        }
        return response.response.body
    }

    private fun mapApiResultMessage(resultMsg: String): String =
        when {
            resultMsg.contains("SERVICE KEY IS NOT REGISTERED", ignoreCase = true) ->
                "등록되지 않은 인증키입니다. data.go.kr 마이페이지에서 '디코딩' 인증키를 확인하거나, " +
                    "활용신청 승인 후 최대 1시간 뒤 다시 시도해 주세요."

            else -> resultMsg
        }

    private companion object {
        const val TAG = "FindMe-Network"
    }
}
