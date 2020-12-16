package io.agora.openduo.DataLayer.Models

data class Staff(
    val id: Int,
    val rtc_token: String,
    val rtm_token: String,
    val user_id: String
)