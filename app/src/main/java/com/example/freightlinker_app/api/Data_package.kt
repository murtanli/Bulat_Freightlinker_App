package com.example.freightlinker_app.api


data class UserRegisResponse(
    val profile_id: Int
)


data class UserloginResponse(
    val user_id: Int,
    val profile_id: Int,
    val message: String
)

data class Registr(
    val user_id: Int,
    val message: String
)

data class Transport_create(
    val brand: String,
    val max_weight: Int?,
    val transport_type: String
)

data class Transport(
    val id: Int,
    val brand: String,
    val max_weight: Int,
    val transport_type: String
)

data class DriversCreate(
    val origin: String,
    val destination: String,
    val fio: String,
    val number_phone: String,
    val status: String,
    val about_me: String,
    val transport: Int?,
    val user_id: Int?
)


data class DriversInfo(
    val id: Int,
    val origin: String,
    val destination: String,
    val fio: String,
    val number_phone: String,
    val status: String,
    val about_me: String,
    val transport: Transport,
)

data class DriversResponse (
    val driver: List<DriversInfo>
)

data class ProfileDriver(
    val profile_inf: DriversInfo
)

data class profile_inf(
    val fio: String,
    val number_phone: String
)

data class Cargo(
    val pk:Int,
    val profile_id: Int,
    val name_cargo: String,
    val departure_time: String,
    val arrival_time: String,
    val origin: String,
    val destination: String,
    val profile_info: profile_inf
)

data class CargoResponse (
    val cargo: List<Cargo>
)

data class ProfileData (
    val profile_id: String?,
    val origin: String,
    val destination: String,
    val status: String
)


data class cargo_inf(
    val pk: Int,
    val profile_id: Int,
    val name_cargo: String,
    val departure_time: String,
    val arrival_time: String,
    val origin: String,
    val destination: String
)

data class profile_userResponse(
    val cargo_inf: List<cargo_inf>,
    val profile_inf: profile_inf
)

data class message_response(
    val message: String
)
