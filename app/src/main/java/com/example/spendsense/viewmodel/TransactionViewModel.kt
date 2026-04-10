package com.example.spendsense.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsense.model.Transaction
import com.example.spendsense.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {

    private val repo = TransactionRepository()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _budget = MutableStateFlow(0.0)
    val budget: StateFlow<Double> = _budget

    init {
        load()
    }

    fun load() {
        repo.listenTransactions {
            _transactions.value = it
        }
    }

    fun add(transaction: Transaction) {
        viewModelScope.launch {
            repo.addTransaction(transaction)
        }
    }

    fun setBudget(value: Double) {
        _budget.value = value
    }

    fun getIncome(): Double =
        _transactions.value.filter { it.type == "income" }.sumOf { it.amount }

    fun getExpense(): Double =
        _transactions.value.filter { it.type == "expense" }.sumOf { it.amount }

    fun getBalance(): Double =
        getIncome() - getExpense()
}