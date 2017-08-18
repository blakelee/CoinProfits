package net.blakelee.coinprofits.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Error {
    @SerializedName("code") @Expose var code: Int? = null
    @SerializedName("message") @Expose var message: String? = null
}

class ETH {
    @SerializedName("balance") @Expose var balance: Int? = null
    @SerializedName("totalIn") @Expose var totalIn: Int? = null
    @SerializedName("totalOut") @Expose var totalOut: Int? = null
}

class Price {
    @SerializedName("rate") @Expose var rate: Double? = null
    @SerializedName("diff") @Expose var diff: Double? = null
    @SerializedName("ts") @Expose var ts: Int? = null
    @SerializedName("currency") @Expose var currency: String? = null
}

class Token {
    @SerializedName("tokenInfo") @Expose var tokenInfo: TokenInfo? = null
    @SerializedName("balance") @Expose var balance: Double? = null
    @SerializedName("totalIn") @Expose var totalIn: Double? = null
    @SerializedName("totalOut") @Expose var totalOut: Int? = null
    @SerializedName("lastUpdated") @Expose var lastUpdated: Int? = null
}

class TokenInfo {
    @SerializedName("address") @Expose var address: String? = null
    @SerializedName("name") @Expose var name: String? = null
    @SerializedName("decimals") @Expose var decimals: String? = null
    @SerializedName("symbol") @Expose var symbol: String? = null
    @SerializedName("totalSupply") @Expose var totalSupply: String? = null
    @SerializedName("owner") @Expose var owner: String? = null
    @SerializedName("totalIn") @Expose var totalIn: Double? = null
    @SerializedName("totalOut") @Expose var totalOut: Double? = null
    @SerializedName("createdAt") @Expose var createdAt: Int? = null
    @SerializedName("createdTx") @Expose var createdTx: String? = null
    @SerializedName("last_updated") @Expose var lastUpdated: Int? = null
    @SerializedName("issuancesCount") @Expose var issuancesCount: Int? = null
    @SerializedName("holdersCount") @Expose var holdersCount: Int? = null
    @SerializedName("price") @Expose var price: Price? = null
}

class ERC20 {
    @SerializedName("address") @Expose var address: String? = null
    @SerializedName("ETH") @Expose var eTH: ETH? = null
    @SerializedName("countTxs") @Expose var countTxs: Int? = null
    @SerializedName("tokens") @Expose var tokens: List<Token>? = null
    @SerializedName("error") @Expose var error: Error? = null
}