package com.tayler.pizzzaapp.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tayler.pizzzaapp.DispatcherProvider
import com.tayler.pizzzaapp.repository.network.model.UserResponse
import com.tayler.pizzzaapp.repository.network.model.LoginRequest
import com.tayler.pizzzaapp.repository.db.entity.UserEntity
import com.tayler.pizzzaapp.ui.base.BaseViewModel
import com.tayler.pizzzaapp.usecases.DataUseCase
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val dataUseCase: DataUseCase,
    private val dispatchers: DispatcherProvider
) : BaseViewModel(dispatchers) {

    var authUiState by mutableStateOf(AuthUiState())
        private set

    fun onUserChange(newUser: String) {
        authUiState = authUiState.copy(user = newUser)
    }

    fun onPassChange(newPass: String) {
        authUiState = authUiState.copy(pass = newPass)
    }

    // Registration handlers
    fun onRegisterFieldChange(
        nameUser: String = authUiState.nameUser,
        names: String = authUiState.names,
        lastName: String = authUiState.lastName,
        document: String = authUiState.document,
        email: String = authUiState.email,
        phone: String = authUiState.phone,
        address: String = authUiState.address,
        area: String = authUiState.area,
        longitude: String = authUiState.longitude,
        latitude: String = authUiState.latitude,
        pass: String = authUiState.pass
    ) {
        authUiState = authUiState.copy(
            nameUser = nameUser,
            names = names,
            lastName = lastName,
            document = document,
            email = email,
            phone = phone,
            address = address,
            area = area,
            longitude = longitude,
            latitude = latitude,
            pass = pass
        )
    }

    fun login(onSuccess: () -> Unit) {
        execute {
            try {
                val request = LoginRequest(
                    nameUser = authUiState.user,
                    password = authUiState.pass
                )
                val response = dataUseCase.login(request)
                
                // Persistir usuario localmente
                val userValid = response.userValid
                dataUseCase.saveUserLocal(
                    UserEntity(
                        uid = userValid.uid ?: "",
                        nameUser = userValid.nameUser ?: "",
                        names = userValid.names ?: "",
                        lastName = userValid.lastName ?: "",
                        document = userValid.document ?: "",
                        email = userValid.email ?: "",
                        phone = userValid.phone ?: "",
                        address = userValid.address ?: "",
                        rol = userValid.rol ?: "CLIENTE",
                        area = userValid.area ?: "1",
                        longitude = userValid.longitude ?: "",
                        latitude = userValid.latitude ?: "",
                        token = response.token
                    )
                )

                withContext(dispatchers.main) {
                    authUiState = authUiState.copy(isLoginSuccessful = true)
                    onSuccess()
                }
            } catch (e: Exception) {
                authUiState = authUiState.copy(error = e.message)
                throw e
            }
        }
    }

    fun checkExistingUser(onResult: (String?) -> Unit) {
        execute(loading = false) {
            val localUser = dataUseCase.getUserLocal()
            withContext(dispatchers.main) {
                onResult(localUser?.rol)
            }
        }
    }

    fun register(onSuccess: (String) -> Unit) {
        execute {
            try {
                val request = UserResponse(
                    nameUser = authUiState.nameUser,
                    names = authUiState.names,
                    lastName = authUiState.lastName,
                    document = authUiState.document,
                    email = authUiState.email,
                    password = authUiState.pass,
                    phone = authUiState.phone,
                    address = authUiState.address,
                    rol = authUiState.rol,
                    area = authUiState.area,
                    longitude = authUiState.longitude,
                    latitude = authUiState.latitude
                )
                val response = dataUseCase.registerUser(request)
                withContext(dispatchers.main) {
                    onSuccess(response)
                }
            } catch (e: Exception) {
                authUiState = authUiState.copy(error = e.message)
                throw e
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        execute {
            try {
                dataUseCase.logout()
                withContext(dispatchers.main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                authUiState = authUiState.copy(error = e.message)
                throw e
            }
        }
    }
}
