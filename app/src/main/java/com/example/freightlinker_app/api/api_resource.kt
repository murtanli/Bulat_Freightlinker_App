package com.example.freightlinker_app.api

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class api_resource {

    suspend fun log_in(login: String, password: String, role: String): UserloginResponse {
        val apiUrl = "http://95.163.235.212:8100/login/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{\"login\":\"$login\",\"password\":\"$password\",\"role\":\"$role\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), UserloginResponse::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing login data ", e)
                throw e
            }
        }
    }


    suspend fun Sign_in(login:String, password: String, role: String): Registr {
        val apiUrl = "http://95.163.235.212:8100/sign_in/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{\"login\":\"$login\",\"password\":\"$password\",\"role\":\"$role\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), Registr::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing login data ", e)
                throw e
            }
        }
    }


    suspend fun get_all_drivers(): List<DriversInfo> {
        val apiUrl = "http://95.163.235.212:8100/get_all_drivers/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val driversArray = gson.fromJson(response.toString(), Array<DriversInfo>::class.java)
                driversArray.toList()
            } catch (e: Exception) {
                // Обработка ошибок, например, логирование
                throw e
            }
        }
    }

    suspend fun get_all_cargoes(): List<Cargo> {
        val apiUrl = "http://95.163.235.212:8100/get_all_cargoes/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val cargoArray = gson.fromJson(response.toString(), Array<Cargo>::class.java)
                cargoArray.toList()
            } catch (e: Exception) {
                // Обработка ошибок, например, логирование
                throw e
            }
        }
    }


    suspend fun get_profile(profile_id: Int?): ProfileDriver {
        val apiUrl = "http://95.163.235.212:8100/get_all_profile_driver/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{\"profile_id\":\"$profile_id\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), ProfileDriver::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing login data ", e)
                throw e
            }
        }
    }

    suspend fun put_profile(profileData: ProfileData) {
        val apiUrl = "http://95.163.235.212:8100/update_profile_driver/"
        val url = URL(apiUrl)

        withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "PUT"  // Используем PUT для обновления данных
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку из данных профиля
                val gson = Gson()
                val jsonData = gson.toJson(profileData)

                // Отправляем JSON-строку в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonData.toByteArray())
                outputStream.close()

                // Проверяем код ответа сервера
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("PUT Profile", "Profile updated successfully")
                } else {
                    Log.e("PUT Profile", "Error updating profile. Response code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("PUT Profile", "Error updating profile", e)
                throw e
            }
        }
    }

    suspend fun createDriverProfile(profileData: DriversCreate): Int? {
        val apiUrl = "http://95.163.235.212:8100/create_driver_profile/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val jsonInputString = Gson().toJson(profileData)

                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val jsonResponse = JSONObject(response.toString())
                val profileId = jsonResponse.optInt("profile_id")
                profileId
            } catch (e: Exception) {
                Log.e("CreateProfileError", "Error creating driver profile", e)
                null
            }
        }
    }

    suspend fun createTransport(transportData: Transport_create): Int? {
        val apiUrl = "http://95.163.235.212:8100/create_transport/"
        val gson = Gson()
        val requestBody = gson.toJson(transportData)

        return withContext(Dispatchers.IO) {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val outputStream = connection.outputStream
                outputStream.write(requestBody.toByteArray())
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    val jsonResponse = JSONObject(response.toString())
                    return@withContext jsonResponse.getInt("transport_id")
                } else {
                    Log.e("CreateTransport", "Failed to create transport. Response code: $responseCode")
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e("CreateTransport", "Error creating transport", e)
                return@withContext null
            }
        }
    }


    suspend fun get_user(profile_id: Int?): profile_userResponse {
        val apiUrl = "http://95.163.235.212:8100/get_all_profile_user/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                val jsonInputString = "{\"profile_id\":\"$profile_id\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val cargoArray = gson.fromJson(response.toString(), profile_userResponse::class.java)
                cargoArray
            } catch (e: Exception) {
                // Обработка ошибок, например, логирование
                throw e
            }
        }
    }

    suspend fun del_cargo(cargo_id: Int?): message_response {
        val apiUrl = "http://95.163.235.212:8100/delete_cargo/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                val jsonInputString = "{\"cargo_id\":\"$cargo_id\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val cargoArray = gson.fromJson(response.toString(), message_response::class.java)
                cargoArray
            } catch (e: Exception) {
                // Обработка ошибок, например, логирование
                throw e
            }
        }
    }

    suspend fun create_cargo(profile_id: Int?, name_cargo: String, departure_time:String, arrival_time: String, origin: String, destinstion: String): message_response {
        val apiUrl = "http://95.163.235.212:8100/create_cargo/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                val jsonInputString = "{\"profile_id\":\"$profile_id\",\"name_cargo\":\"$name_cargo\",\"departure_time\":\"$departure_time\",\"arrival_time\":\"$arrival_time\",\"origin\":\"$origin\",\"destination\":\"$destinstion\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val cargoArray = gson.fromJson(response.toString(), message_response::class.java)
                cargoArray
            } catch (e: Exception) {
                // Обработка ошибок, например, логирование
                throw e
            }
        }
    }

    suspend fun create_user_profile(fio: String, number_phone: String, user_id: Int?): UserRegisResponse {
        val apiUrl = "http://95.163.235.212:8100/create_user_profile/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                val jsonInputString = "{\"fio\":\"$fio\",\"number_phone\":\"$number_phone\",\"user_id\":\"$user_id\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val cargoArray = gson.fromJson(response.toString(), UserRegisResponse::class.java)
                cargoArray
            } catch (e: Exception) {
                // Обработка ошибок, например, логирование
                throw e
            }
        }



    }

}