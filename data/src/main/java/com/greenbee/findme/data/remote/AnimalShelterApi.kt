package com.greenbee.findme.data.remote

import com.greenbee.findme.data.remote.dto.PublicDataResponseDto
import com.greenbee.findme.data.remote.dto.ShelterInfoItemsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimalShelterApi {

    @GET("shelterInfo_v2")
    suspend fun getShelterInfo(
        @Query("serviceKey") serviceKey: String,
        @Query("care_reg_no") careRegNo: String? = null,
        @Query("care_nm") careName: String? = null,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("_type") responseType: String = "json",
    ): PublicDataResponseDto<ShelterInfoItemsDto>
}
