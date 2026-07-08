package com.greenbee.findme.domain.model

data class AbandonedAnimal(
    val desertionNo: String,
    val happenPlace: String,
    val kindName: String,
    val color: String,
    val age: String,
    val weight: String,
    val imageUrls: List<String>,
    val processState: String,
    val shelterName: String,
    val shelterTel: String,
    val specialMark: String,
    val sex: String,
    val neuterYn: String,
    val happenDate: String = "",
    val noticeStartDate: String = "",
    val noticeEndDate: String = "",
)
