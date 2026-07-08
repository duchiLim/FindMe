package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.HomeDateRange
import com.greenbee.findme.domain.model.HomeRegionFilter
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.domain.repository.UserSettingsRepository
import javax.inject.Inject

class GetHomeRegionFilterUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    suspend operator fun invoke(): HomeRegionFilter = repository.getHomeRegionFilter()
}

class SetHomeRegionFilterUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    suspend operator fun invoke(filter: HomeRegionFilter) {
        repository.setHomeRegionFilter(filter)
    }
}

class GetHomeUpKindFilterUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    suspend operator fun invoke(): UpKind? = repository.getHomeUpKindFilter()
}

class SetHomeUpKindFilterUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    suspend operator fun invoke(upKind: UpKind?) {
        repository.setHomeUpKindFilter(upKind)
    }
}

class GetHomeDateRangeFilterUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    suspend operator fun invoke(): HomeDateRange = repository.getHomeDateRangeFilter()
}

class SetHomeDateRangeFilterUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    suspend operator fun invoke(dateRange: HomeDateRange) {
        repository.setHomeDateRangeFilter(dateRange)
    }
}
