package com.example.spendsense.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.spendsense.data.repository.FirestoreRepository
import com.example.spendsense.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.spendsense.data.model.Group
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

    // ================= GROUP =================
    private val _currentGroupCode = MutableStateFlow("")
    val currentGroupCode: StateFlow<String> = _currentGroupCode

    private val _currentGroupName = MutableStateFlow("")
    val currentGroupName: StateFlow<String> = _currentGroupName

    private val _currentGroupId = mutableStateOf("")
    val currentGroupId: String get() = _currentGroupId.value

    private var currentGroupIdInternal: String? = null

    private var lastAlertShownAt = 0L

    // ================= BASE ID =================
    private fun getBaseId(): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        return if (_isSharedMode.value && !currentGroupIdInternal.isNullOrEmpty()) {
            currentGroupIdInternal!!
        } else {
            uid
        }
    }

    // ================= SESSION =================
    fun setUserSession(uid: String) {
        _isSharedMode.value = false
        currentGroupIdInternal = uid
        _currentGroupId.value = uid
        loadAllData()
    }

    fun setPersonalMode() {
        _isSharedMode.value = false
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        currentGroupIdInternal = uid
        _currentGroupId.value = uid
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

        repo.addTransaction(transaction.copy(userId = uid, groupId = baseId)) {
            loadAllData()
        }
    }

    fun update(transaction: Transaction) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val baseId = getBaseId()

        repo.updateTransaction(transaction.copy(userId = uid, groupId = baseId)) {
            loadAllData()
        }
    }

    fun delete(id: String) {
        val baseId = getBaseId()
        repo.deleteTransaction(baseId, id) { loadAllData() }
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
            currentGroupIdInternal = groupId
            _currentGroupId.value = groupId
            _isSharedMode.value = true

            loadAllData()
            loadGroupInfo(groupId)

            onResult(groupId)
        }
    }

    fun joinGroup(code: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        repo.joinGroup(uid, code) { groupId ->
            if (!groupId.isNullOrEmpty()) {
                currentGroupIdInternal = groupId
                _currentGroupId.value = groupId
                _isSharedMode.value = true

                loadAllData()
                loadGroupInfo(groupId)
            }
        }
    }
    private val _userGroups = MutableStateFlow<List<Group>>(emptyList())
    val userGroups: StateFlow<List<Group>> = _userGroups


    // ================= GROUP INFO =================
    fun loadGroupInfo(groupId: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid
        val email = user.email ?: ""

        val safeEmail = email.replace(".", ",") // ✅ FIX

        repo.getGroup(groupId) { group ->
            if (group != null) {

                _currentGroupName.value = group.name

                val isCreator = group.createdBy == uid
                val isInvited = group.invitedEmails.containsKey(safeEmail) // ✅ FIX
                val isMember = group.members.containsKey(uid)

                _currentGroupCode.value =
                    if ((isCreator || isInvited) && isMember) {
                        group.code
                    } else {
                        ""
                    }
            }
        }
    }

    // ================= ALERT =================
    private fun recalculateBudgetAndAlert() {
        val budgetValue = _budget.value
        val expense = _expense.value

        if (budgetValue <= 0) return

        val percent = (expense / budgetValue) * 100
        _budgetUsedPercent.value = percent

        if (percent >= 80) {
            val now = System.currentTimeMillis()
            if (now - lastAlertShownAt > 30000) {
                _budgetAlertEvent.value = true
                lastAlertShownAt = now
            }
        }
    }

    fun resetBudgetAlert() {
        _budgetAlertEvent.value = false
    }

    // ================= HELPERS =================
    fun getMostSpentCategory(): String {
        val expenseList = _transactions.value.filter { it.type == "EXPENSE" }

        if (expenseList.isEmpty()) return "No Data"

        return expenseList
            .groupBy { it.category }
            .maxByOrNull { it.value.sumOf { t -> t.amount } }
            ?.key ?: "No Data"
    }

    fun setSharedMode(enabled: Boolean, groupId: String? = null) {
        _isSharedMode.value = enabled

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        currentGroupIdInternal = if (enabled) {
            groupId ?: currentGroupIdInternal ?: uid
        } else {
            uid
        }

        _currentGroupId.value = currentGroupIdInternal ?: ""

        if (enabled && currentGroupIdInternal != null) {
            loadGroupInfo(currentGroupIdInternal!!)
        }

        loadAllData()
    }

    fun inviteUser(email: String) {
        val groupId = currentGroupIdInternal ?: return
        repo.inviteUser(groupId, email) {}
    }
    fun loadUserGroups() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        repo.getAllGroups { list ->
            _userGroups.value = list.filter {
                it.createdBy == uid || it.members.containsKey(uid)
            }
        }
    }
}