package com.masrofy.screens.mainScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masrofy.data.entity.toTransactionGroup
import com.masrofy.data.relation.toTransactions
import com.masrofy.data.relation.transactionsToBalance
import com.masrofy.repository.AccountRepository
import com.masrofy.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) :
    ViewModel() {

    private val _transactionGroup = MutableStateFlow<MainScreenState>(MainScreenState())
    val transactionGroup = _transactionGroup.asStateFlow()

    init {
        loadData()
    }

    fun updateDate(month:Long,dateEvent: DateEvent){
        _transactionGroup.update {
            val  currentDate = when(dateEvent){
                DateEvent.PLUS -> it.currentDate.plusMonths(month)
                DateEvent.MIN -> it.currentDate.minusMonths(month)
            }
            it.copy(
                currentDate = currentDate
            )
        }
        loadData()
    }

    private fun loadData() {
        val getAccountWithTransactions = accountRepository.getAccountsWithTransactions()
        viewModelScope.launch(Dispatchers.IO) {
            getAccountWithTransactions
                .collect {accountWithTransaction->
                    val filter = accountWithTransaction.toTransactions().filter {
                        it.createdAt.monthValue == _transactionGroup.value.currentDate.monthValue
                    }
                    Log.d("MainViewModel", "loadData: called collect")
                    Log.d("MainViewModel", "${_transactionGroup.value.currentDate.monthValue}")
                _transactionGroup.update {
                    it.copy(
                        balance = filter.transactionsToBalance(),
                        transactions = filter.toTransactionGroup()
                    )
                }
            }
        }
    }
}
enum class DateEvent{
    PLUS,MIN
}