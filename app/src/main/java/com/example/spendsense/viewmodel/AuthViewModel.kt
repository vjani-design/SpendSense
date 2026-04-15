package com.example.spendsense.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsense.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    // =========================
    // AUTH STATE (LOGIN STATUS)
    // =========================
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // =========================
    // LOADING STATE
    // =========================
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // =========================
    // ERROR STATE
    // =========================
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // =========================
    // INIT CHECK (AUTO LOGIN CHECK)
    // =========================
    init {
        _isLoggedIn.value = repo.isLoggedIn()
    }

    // =========================
    // LOGIN
    // =========================
    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        repo.login(email, password) { success, error ->
            _isLoading.value = false

            if (success) {
                _isLoggedIn.value = true
            } else {
                _errorMessage.value = error ?: "Login failed"
            }
        }
    }

    // =========================
    // REGISTER
    // =========================
    fun register(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        repo.register(email, password) { success, error ->
            _isLoading.value = false

            if (success) {
                _isLoggedIn.value = true
            } else {
                _errorMessage.value = error ?: "Registration failed"
            }
        }
    }

    // =========================
    // LOGOUT
    // =========================
    fun logout() {
        repo.logout()
        _isLoggedIn.value = false
    }

    // =========================
    // GET CURRENT USER ID
    // =========================
    fun getUserId(): String? {
        return repo.getUserId()
    }

    // =========================
    // CLEAR ERROR (FOR UI SNACKBAR)
    // =========================
    fun clearError() {
        _errorMessage.value = null
    }
}