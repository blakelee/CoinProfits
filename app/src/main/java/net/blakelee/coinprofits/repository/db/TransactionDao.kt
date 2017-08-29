package net.blakelee.coinprofits.repository.db

import android.arch.persistence.room.*
import io.reactivex.Flowable
import net.blakelee.coinprofits.models.Transaction

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactions(transactions: List<Transaction>)

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionsById(id: String): Flowable<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE publicKey = :publicKey")
    fun getTransactionsByAddress(publicKey: String): Flowable<List<Transaction>>

    @Delete
    fun deleteTransaction(transaction: Transaction)
}