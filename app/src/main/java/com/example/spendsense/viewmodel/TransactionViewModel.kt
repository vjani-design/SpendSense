package com.example.spendsense.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.spendsense.data.repository.FirestoreRepository
import com.example.spendsense.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
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
    private var lastSelectedGroupId: String? = null
    private var lastModeShared: Boolean = false
    private var lastAlertShownAt = 0L
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


    // 🔥 FIRESTORE LISTENER
    private var transactionListener: ListenerRegistration? = null

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

        transactionListener?.remove()

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // 🔥 CASE 1: FAMILY MODE WITHOUT GROUP → EMPTY
        if (_isSharedMode.value && currentGroupIdInternal.isNullOrEmpty()) {
            updateTransactionState(emptyList())
            return
        }

        // 🔥 CASE 2: GROUP MODE
        if (_isSharedMode.value) {

            transactionListener =
                repo.listenGroupTransactions(currentGroupIdInternal!!) { list ->
                    updateTransactionState(list)
                }

        } else {

            // 🔥 CASE 3: PERSONAL MODE
            transactionListener =
                repo.listenPersonalTransactions(uid) { list ->
                    updateTransactionState(list)
                }
        }
    }

    private fun updateTransactionState(list: List<Transaction>) {
        val incomeList = list.filter { it.type == "INCOME" }
        val expenseList = list.filter { it.type == "EXPENSE" }

        _transactions.value = list
        _income.value = incomeList.sumOf { it.amount }
        _expense.value = expenseList.sumOf { it.amount }
        _balance.value = _income.value - _expense.value

        recalculateBudgetAndAlert()
    }

    // ================= CRUD =================
    fun add(transaction: Transaction) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (_isSharedMode.value && !currentGroupIdInternal.isNullOrEmpty()) {
            repo.addGroupTransaction(currentGroupIdInternal!!, transaction.copy(userId = uid))
        } else {
            repo.addPersonalTransaction(uid, transaction.copy(userId = uid))
        }
    }

    fun update(transaction: Transaction) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (_isSharedMode.value && !currentGroupIdInternal.isNullOrEmpty()) {
            repo.updateGroupTransaction(currentGroupIdInternal!!, transaction.copy(userId = uid))
        } else {
            repo.updatePersonalTransaction(uid, transaction.copy(userId = uid))
        }
    }

    fun delete(id: String) {
        if (_isSharedMode.value && !currentGroupIdInternal.isNullOrEmpty()) {
            repo.deleteGroupTransaction(currentGroupIdInternal!!, id)
        } else {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            repo.deletePersonalTransaction(uid, id)
        }
    }

    // ================= BUDGET =================
    fun loadBudget() {

        val baseId = if (_isSharedMode.value && !currentGroupIdInternal.isNullOrEmpty())
            currentGroupIdInternal!!
        else
            FirebaseAuth.getInstance().currentUser?.uid ?: return

        repo.getBudget(baseId) { value, type ->
            _budget.value = value
            _budgetType.value = BudgetType.valueOf(type)
            recalculateBudgetAndAlert()
        }
    }

    fun setBudget(value: Double, type: BudgetType) {

        val baseId = if (_isSharedMode.value && !currentGroupIdInternal.isNullOrEmpty())
            currentGroupIdInternal!!
        else
            FirebaseAuth.getInstance().currentUser?.uid ?: return

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

            clearData()
            loadAllData()
            loadGroupInfo(groupId)

            onResult(groupId)
        }
    }

    fun joinGroup(code: String, onResult: (String?) -> Unit) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        repo.joinGroup(uid, code) { groupId ->

            if (!groupId.isNullOrEmpty()) {
                currentGroupIdInternal = groupId
                _currentGroupId.value = groupId
                _isSharedMode.value = true

                clearData()
                loadAllData()
                loadGroupInfo(groupId)
            }

            onResult(groupId)
        }
    }

    private val _userGroups = MutableStateFlow<List<Group>>(emptyList())
    val userGroups: StateFlow<List<Group>> = _userGroups

    fun loadGroupInfo(groupId: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid
        val email = user.email ?: ""

        val safeEmail = email.replace(".", ",")

        repo.getGroup(groupId) { group ->
            if (group != null) {

                _currentGroupName.value = group.name

                val isCreator = group.createdBy == uid
                val isInvited = group.invitedEmails.containsKey(safeEmail)
                val isMember = group.members.containsKey(uid)

                _currentGroupCode.value =
                    if ((isCreator || isInvited) && isMember) group.code else ""
            }
        }
    }

    // ================= MODE SWITCH =================
    fun setSharedMode(enabled: Boolean, groupId: String? = null) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        lastModeShared = enabled   // 🔥 SAVE MODE

        if (enabled) {

            _isSharedMode.value = true

            if (groupId.isNullOrEmpty()) {
                currentGroupIdInternal = null
                _currentGroupId.value = ""
                clearData()
                return
            }

            // 🔥 SAVE GROUP
            lastSelectedGroupId = groupId

            currentGroupIdInternal = groupId
            _currentGroupId.value = groupId

            loadGroupInfo(groupId)

        } else {

            _isSharedMode.value = false
            currentGroupIdInternal = uid
            _currentGroupId.value = uid
        }

        clearData()
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

    fun initAfterLogin() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (lastModeShared) {

            _isSharedMode.value = true

            if (!lastSelectedGroupId.isNullOrEmpty()) {

                currentGroupIdInternal = lastSelectedGroupId
                _currentGroupId.value = lastSelectedGroupId!!

                loadGroupInfo(lastSelectedGroupId!!)

            } else {

                currentGroupIdInternal = null
                _currentGroupId.value = ""
            }

        } else {

            _isSharedMode.value = false
            currentGroupIdInternal = uid
            _currentGroupId.value = uid
        }

        clearData()
        loadAllData()
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

    fun resetBudgetAlert() {
        _budgetAlertEvent.value = false
    }
    private fun recalculateBudgetAndAlert() {

        val budgetValue = _budget.value
        val expenseValue = _expense.value

        if (budgetValue <= 0) {
            _budgetUsedPercent.value = 0.0
            _budgetAlert.value = false
            return
        }

        val percent = (expenseValue / budgetValue) * 100

        _budgetUsedPercent.value = percent

        // 🔥 update alert state
        _budgetAlert.value = percent >= 80

        // 🔥 event trigger (with cooldown)
        if (percent >= 80) {
            val now = System.currentTimeMillis()
            if (now - lastAlertShownAt > 30000) {
                _budgetAlertEvent.value = true
                lastAlertShownAt = now
            }
        }
    }
}