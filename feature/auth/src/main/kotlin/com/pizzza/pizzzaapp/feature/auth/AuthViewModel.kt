package com.pizzza.pizzzaapp.feature.auth

import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.repository.network.model.LoginRequest
import com.pizzza.pizzzaapp.repository.db.entity.UserEntity
import com.pizzza.pizzzaapp.core.ui.BaseViewModel
import com.pizzza.pizzzaapp.core.ui.GlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.AppDataOrder
import com.pizzza.pizzzaapp.usecases.DataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager,
    private val appDataOrder: AppDataOrder
) : BaseViewModel() {

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    fun onUserChange(newUser: String) {
        _authUiState.update { it.copy(user = newUser) }
    }

    fun onPassChange(newPass: String) {
        _authUiState.update { it.copy(pass = newPass) }
    }

    // Registration handlers
    fun onRegisterFieldChange(
        nameUser: String = _authUiState.value.nameUser,
        names: String = _authUiState.value.names,
        lastName: String = _authUiState.value.lastName,
        document: String = _authUiState.value.document,
        email: String = _authUiState.value.email,
        phone: String = _authUiState.value.phone,
        address: String = _authUiState.value.address,
        area: String = _authUiState.value.area,
        longitude: String = _authUiState.value.longitude,
        latitude: String = _authUiState.value.latitude,
        pass: String = _authUiState.value.pass
    ) {
        _authUiState.update { 
            it.copy(
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
    }

    fun login(onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            val request = LoginRequest(
                nameUser = _authUiState.value.user,
                password = _authUiState.value.pass
            )
            val response = io { dataUseCase.login(request) }

            val userValid = response.userValid
            val userEntity = UserEntity(
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

            io { dataUseCase.saveUserLocal(userEntity) }

            // Update global data
            appDataOrder.update {
                it.copy(
                    deliveryAddress = userEntity.address,
                    latitude = userEntity.latitude,
                    longitude = userEntity.longitude
                )
            }

            _authUiState.update { it.copy(isLoginSuccessful = true) }
            onSuccess()
        }
    }

    fun resetState() {
        _authUiState.update { AuthUiState() }
    }

    fun checkExistingUser(onResult: (String?) -> Unit) {
        execute(loading = false, globalUiStateManager = globalUiStateManager) {
            val localUser = io { dataUseCase.getUserLocal() }
            if (localUser != null) {
                appDataOrder.update {
                    it.copy(
                        deliveryAddress = localUser.address,
                        latitude = localUser.latitude,
                        longitude = localUser.longitude
                    )
                }
            }
            onResult(localUser?.rol)
        }
    }

    fun register(onSuccess: (String) -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            val state = _authUiState.value
            val request = UserResponse(
                nameUser = state.nameUser,
                names = state.names,
                lastName = state.lastName,
                document = state.document,
                email = state.email,
                password = state.pass,
                phone = "+52${state.phone}",
                address = state.address,
                rol = state.rol,
                area = state.area,
                longitude = state.longitude,
                latitude = state.latitude
            )
            val response = io { dataUseCase.registerUser(request) }
            onSuccess(response)
        }
    }

    fun logout(onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            io { dataUseCase.logout() }
            appDataOrder.reset()
            onSuccess()
        }
    }
}
