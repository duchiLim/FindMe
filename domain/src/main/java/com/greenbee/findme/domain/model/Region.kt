package com.greenbee.findme.domain.model

data class Region(
    val code: String,
    val name: String,
    val parentCode: String? = null,
)
