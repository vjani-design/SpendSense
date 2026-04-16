package com.example.spendsense.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spendsense.data.repository.FirestoreRepository
import com.example.spendsense.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class BudgetType {
    WEEKLY,
    MONTHLY
}

class TransactionViewModel : ViewModel() {

    private val repo = FirestoreRepository()

    // ================= STATES =================
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _income = MutableStateFlow(0.0)
    val income: StateFlow<Double> = _income

    private val _expense = MutableStateFlow(0.0)
    val expense: StateFlow<Double> = _expense

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance

    private val _budget = MutableStateFlow(0.0)
    val budget: StateFlow<Double> = _budget

    private val _budgetType = MutableStateFlow(BudgetType.MONTHLY)
    val budgetType: StateFlow<BudgetType> = _budgetType

    private val _budgetUsedPercent = MutableStateFlow(0.0)
    val budgetUsedPercent: StateFlow<Double> = _budgetUsedPercent

    private val _budgetAlert = MutableStateFlow(false)
    val budgetAlert: StateFlow<Boolean> = _budgetAlert

    private val _budgetAlertEvent = MutableStateFlow(false)
    val budgetAlertEvent: StateFlow<Boolean> = _budgetAlertEvent

    // ================= MODE =================
    private val _isSharedMode = MutableStateFlow(false)
    val isSharedMode: StateFlow<Boolean> = _isSharedMode

    private var currentGroupId: String? = null

    private var lastAlertShownAt = 0L

    // ================= BASE ID (IMPORTANT FIX) =================
    private fun getBaseId(): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        return when {
            _isSharedMode.value && !currentGroupId.isNullOrEmpty() -> currentGroupId!!
            uid.isNotEmpty() -> uid
            else -> ""
        }
    }

    // ================= SESSION =================
    fun setUserSession(uid: String) {
        _isSharedMode.value = false
        currentGroupId = uid
        loadAllData()
    }

    fun setPersonalMode() {
        _isSharedMode.value = false
        currentGroupId = FirebaseAuth.getInstance().currentUser?.uid
        loadAllData()
    }

    fun setSharedMode(enabled: Boolean, groupId: String? = null) {

        _isSharedMode.value = enabled

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        currentGroupId = if (enabled) {
            groupId ?: currentGroupId ?: uid
        } else {
            uid
        }

        loadAllData()
    }

    // ================= LOAD =================
    fun loadAllData() {
        loadTransactions()
        loadBudget()
    }

    fun clearData() {
        _transactions.value = emptyList()
        _income.value = 0.0
        _expense.value = 0.0
        _balance.value = 0.0
        _budget.value = 0.0
        _budgetUsedPercent.value = 0.0
        _budgetAlert.value = false
    }

    // ================= TRANSACTIONS =================
    fun loadTransactions() {

        val baseId = getBaseId()
        if (baseId.isEmpty()) return

        repo.getTransactions(baseId) { list ->

            val incomeList = list.filter { it.type == "INCOME" }
            val expenseList = list.filter { it.type == "EXPENSE" }

            _transactions.value = list
            _income.value = incomeList.sumOf { it.amount }
            _expense.value = expenseList.sumOf { it.amount }
            _balance.value = _income.value - _expense.value

            recalculateBudgetAndAlert()
        }
    }

    fun add(transaction: Transaction) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val baseId = getBaseId()

        repo.addTransaction(
            transaction.copy(userId = uid, groupId = baseId)
        ) {
            loadAllData()
        }
    }

    fun update(transaction: Transaction) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val baseId = getBaseId()

        repo.updateTransaction(
            transaction.copy(userId = uid, groupId = baseId)
        ) {
            loadAllData()
        }
    }

    fun delete(id: String) {

        val baseId = getBaseId()

        repo.deleteTransaction(baseId, id) {
            loadAllData()
        }
    }

    // ================= BUDGET =================
    fun loadBudget() {

        val baseId = getBaseId()
        if (baseId.isEmpty()) return

        repo.getBudget(baseId) { value, type ->
            _budget.value = value
            _budgetType.value = BudgetType.valueOf(type)
            recalculateBudgetAndAlert()
        }
    }

    fun setBudget(value: Double, type: BudgetType) {

        val baseId = getBaseId()

        _budget.value = value
        _budgetType.value = type

        repo.saveBudget(baseId, value, type.name)

        recalculateBudgetAndAlert()
    }

    // ================= GROUP =================
    fun createGroup(groupName: String, onResult: (String) -> Unit = {}) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        repo.createGroup(uid, groupName) { groupId ->
            currentGroupId = groupId
            _isSharedMode.value = true
            loadAllData()

            onResult(groupId) // 👈 ADD THIS
        }
    }

    fun joinGroup(code: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        repo.joinGroup(uid, code) { groupId ->

            if (!groupId.isNullOrEmpty()) {
                currentGroupId = groupId
                _isSharedMode.value = true
                loadAllData()
            }
        }
    }

    // ================= ALERT =================
    private fun recalculateBudgetAndAlert() {

        val expense = _expense.value
        val budgetValue = _budget.value

        if (budgetValue <= 0) {
            _budgetUsedPercent.value = 0.0
            _budgetAlert.value = false
            return
        }

        val percent = (expense / budgetValue) * 100
        _budgetUsedPercent.value = percent

        if (percent >= 80) {
            val now = System.currentTimeMillis()

            if (now - lastAlertShownAt > 30000) {
                _budgetAlertEvent.value = true
                lastAlertShownAt = now
            }
        } else {
            _budgetAlert.value = false
        }
    }

    fun resetBudgetAlert() {
        _budgetAlertEvent.value = false
    }

    // ================= ANALYTICS =================
    fun getMostSpentCategory(): String {

        val expenseList = _transactions.value.filter {
            it.type == "EXPENSE"
        }

        if (expenseList.isEmpty()) return "No Data"

        return expenseList
            .groupBy { it.category }
            .maxByOrNull { it.value.sumOf { t -> t.amount } }
            ?.key ?: "No Data"
    }
    fun enableSharedMode(groupId: String) {
        _isSharedMode.value = true
        currentGroupId = groupId
        loadAllData()
    }
    fun getCurrentGroupId(): String? {
        return currentGroupId
    }
}