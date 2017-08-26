package net.blakelee.coinprofits.tools

import android.arch.persistence.room.TypeConverter
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import net.blakelee.coinprofits.models.Transaction


class TransactionConverter {
    @TypeConverter
    fun toString(transaction: List<Transaction>) = Gson().toJson(transaction)

    @TypeConverter
    fun toTransaction(transaction: String) = Gson().fromJson<List<Transaction>>(transaction)
}