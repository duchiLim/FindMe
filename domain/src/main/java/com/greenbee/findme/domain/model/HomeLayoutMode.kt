package com.greenbee.findme.domain.model

enum class HomeLayoutMode {
    /** 2열뷰 */
    GRID,

    /** 노멀뷰 */
    LIST,

    /** 카드뷰 */
    COVER,
    ;

    companion object {
        fun fromStored(value: String?): HomeLayoutMode =
            entries.find { it.name == value } ?: GRID
    }
}
