package com.example.spendsense.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spendsense.data.repository.FirestoreRepository
import com.example.spendsense.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private var currentGroupId: String? = null
    private var isFamilyMode: Boolean = false
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

    var isSharedMode = false
    private var lastAlertShownAt = 0L

    // ================= GROUP =================
    private fun getGroupId(): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return ""
        return currentGroupId ?: uid
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

    fun loadTransactions() {

        val groupId = getGroupId()

        repo.getTransactions(groupId) { list ->

            val incomeList = list.filter { it.type == "INCOME" }
            val expenseList = list.filter { it.type == "EXPENSE" }

            val totalIncome = incomeList.sumOf { it.amount }
            val totalExpense = expenseList.sumOf { it.amount }

            _transactions.value = list
            _income.value = totalIncome
            _expense.value = totalExpense
            _balance.value = totalIncome - totalExpense

            recalculateBudgetAndAlert()
        }
    }

    fun loadBudget() {
        val groupId = getGroupId()

        repo.getBudget(groupId) { value, type ->
            _budget.value = value
            _budgetType.value = BudgetType.valueOf(type)

            recalculateBudgetAndAlert()
        }
    }

    // ================= CRUD =================

    fun add(transaction: Transaction) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val groupId = getGroupId()

        val updated = transaction.copy(
            userId = uid,
            groupId = groupId
        )

        repo.addTransaction(updated) {
            loadAllData()
            recalculateBudgetAndAlert()
        }
    }

    fun update(transaction: Transaction) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val groupId = getGroupId()

        val updated = transaction.copy(
            userId = uid,
            groupId = groupId
        )

        repo.updateTransaction(updated) {
            loadAllData()
            recalculateBudgetAndAlert()
        }
    }

    fun delete(id: String) {

        val groupId = getGroupId()

        repo.deleteTransaction(groupId, id) {
            loadAllData()
            recalculateBudgetAndAlert()
        }
    }

    // ================= BUDGET =================

    fun setBudget(value: Double, type: BudgetType) {

        val groupId = getGroupId()

        _budget.value = value
        _budgetType.value = type

        repo.saveBudget(groupId, value, type.name)

        recalculateBudgetAndAlert()
    }

    // ================= CORE LOGIC =================

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

        val shouldAlert = percent >= 80

        if (shouldAlert) {
            triggerBudgetAlert()
        } else {
            _budgetAlert.value = false
        }
    }

    private fun triggerBudgetAlert() {

        val now = System.currentTimeMillis()

        if (now - lastAlertShownAt > 30000) {
            _budgetAlertEvent.value = true
            lastAlertShownAt = now
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

        val categoryTotals = expenseList
            .groupBy { it.category }
            .mapValues { entry ->
                entry.value.sumOf { it.amount }
            }

        return categoryTotals.maxByOrNull { it.value }?.key ?: "No Data"
    }
    fun setUserSession(uid: String) {

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->

                isFamilyMode = doc.getString("mode") == "FAMILY"

                currentGroupId = if (isFamilyMode) {
                    doc.getString("groupId") ?: uid
                } else {
                    uid
                }

                loadAllData()
            }
    }
}