package com.pizzza.pizzzaapp.feature.auth

import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.repository.network.model.LoginRequest
import com.pizzza.pizzzaapp.core.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import com.pizzza.pizzzaapp.model.UserModel
import com.pizzza.pizzzaapp.usecases.DataUseCase
import com.pizzza.pizzzaapp.repository.network.exception.UiTayApiException
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
                nameUser = nameUser.replace(" ", ""),
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

    fun updateAddress(address: String, lat: String, lng: String) {
        _authUiState.update { 
            it.copy(
                address = address,
                latitude = lat,
                longitude = lng
            )
        }
    }

    fun login(onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            val request = LoginRequest(
                nameUser = _authUiState.value.user.trim(),
                password = _authUiState.value.pass.trim()
            )
            val response = io { dataUseCase.login(request) }
            val userValid = response.finalUser
            val userRole = userValid.rol?.uppercase() ?: ""
            if (userRole != "CLIENTE" && userRole != "ADMIN") {
                throw UiTayApiException(
                    code = 401,
                    title = "Usuario no autorizado",
                    messageApi = "El usuario ingresado no está autorizado para esta aplicación"
                )
            }

            val userModel = response.toUserModel()
            io { dataUseCase.saveUserLocal(userModel) }

            appDataOrder.update {
                it.copy(
                    deliveryAddress = userModel.address,
                    latitude = userModel.latitude,
                    longitude = userModel.longitude
                )
            }

            _authUiState.update { 
                it.copy(
                    names = userModel.names,
                    nameUser = userModel.nameUser,
                    lastName = userModel.lastName,
                    isLoginSuccessful = true
                ) 
            }
            onSuccess()
        }
    }

    fun checkExistingUser(onResult: (String?) -> Unit) {
        execute(loading = false, globalUiStateManager = globalUiStateManager) {
            val user = io { dataUseCase.checkSessionAndRefreshToken() }
            if (user != null) {
                _authUiState.update {
                    it.copy(
                        names = user.names,
                        nameUser = user.nameUser,
                        lastName = user.lastName,
                        email = user.email,
                        phone = user.phone,
                        address = user.address
                    )
                }
                appDataOrder.update {
                    it.copy(
                        deliveryAddress = user.address,
                        latitude = user.latitude,
                        longitude = user.longitude
                    )
                }
            }
            onResult(user?.rol)
        }
    }

    fun loadUserLocal() {
        execute(loading = false, globalUiStateManager = globalUiStateManager) {
            val user = io { dataUseCase.getUserLocal() }
            if (user != null) {
                _authUiState.update {
                    it.copy(
                        names = user.names,
                        nameUser = user.nameUser,
                        lastName = user.lastName,
                        email = user.email,
                        phone = user.phone,
                        address = user.address
                    )
                }
            }
        }
    }

    fun register(onSuccess: (String) -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            val state = _authUiState.value
            val request = UserResponse(
                nameUser = state.nameUser.trim(),
                names = state.names.trim(),
                lastName = state.lastName.trim(),
                document = state.document.trim(),
                email = state.email.trim(),
                password = state.pass.trim(),
                phone = "+52${state.phone.trim()}",
                address = state.address.trim(),
                rol = state.rol.trim(),
                area = state.area.trim(),
                longitude = state.longitude.trim(),
                latitude = state.latitude.trim()
            )
            val response = io { dataUseCase.registerUser(request) }
            onSuccess(response)
        }
    }

    fun logout(onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            io { dataUseCase.logout() }
            appDataOrder.update { state ->
                state.copy(
                    cart = emptyList(),
                    deliveryAddress = "",
                    latitude = "",
                    longitude = "",
                    selectedOrder = null,
                    selectedProduct = null,
                    orders = emptyList(),
                    ordersLoaded = false
                )
            }
            onSuccess()
        }
    }

    fun deleteUser(onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            val user = io { dataUseCase.getUserLocal() }
            val userId = user?.uid ?: ""
            if (userId.isNotBlank()) {
                io { dataUseCase.deleteUser(userId) }
            } else {
                io { dataUseCase.logout() }
            }
            appDataOrder.update { state ->
                state.copy(
                    cart = emptyList(),
                    deliveryAddress = "",
                    latitude = "",
                    longitude = "",
                    selectedOrder = null,
                    selectedProduct = null,
                    orders = emptyList(),
                    ordersLoaded = false
                )
            }
            onSuccess()
        }
    }
}
