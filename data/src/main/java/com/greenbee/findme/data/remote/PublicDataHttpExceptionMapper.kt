package com.greenbee.findme.data.remote

import retrofit2.HttpException

internal object PublicDataHttpExceptionMapper {

    fun toPublicDataException(exception: HttpException): Throwable {
        val code = exception.code()
        val body = exception.response()?.errorBody()?.string().orEmpty().trim()

        val message = when {
            code == 500 && body.equals("Unexpected errors", ignoreCase = true) ->
                "공공데이터 API 게이트웨이 오류입니다. data.go.kr에서 " +
                    "'국가동물보호정보시스템 구조동물 조회' 활용신청 승인과 인증키를 확인해 주세요. " +
                    "발급·재발급 직후에는 최대 1시간까지 동기화 지연이 있을 수 있습니다."

            code == 401 || code == 403 ->
                "공공데이터 API 인증에 실패했습니다. 마이페이지의 인증키(디코딩)를 확인해 주세요."

            body.isNotBlank() ->
                "HTTP $code: $body"

            else ->
                "HTTP $code: ${exception.message()}"
        }

        return IllegalStateException(message, exception)
    }
}
