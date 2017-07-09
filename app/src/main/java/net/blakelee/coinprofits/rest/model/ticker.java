package net.blakelee.coinprofits.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ticker {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("rank")
    @Expose
    private String rank;
    @SerializedName("price_usd")
    @Expose
    private String priceUsd;
    @SerializedName("price_btc")
    @Expose
    private String priceBtc;
    @SerializedName("24h_volume_usd")
    @Expose
    private String _24hVolumeUsd;
    @SerializedName("market_cap_usd")
    @Expose
    private String marketCapUsd;
    @SerializedName("available_supply")
    @Expose
    private String availableSupply;
    @SerializedName("total_supply")
    @Expose
    private String totalSupply;
    @SerializedName("percent_change_1h")
    @Expose
    private String percentChange1h;
    @SerializedName("percent_change_24h")
    @Expose
    private String percentChange24h;
    @SerializedName("percent_change_7d")
    @Expose
    private String percentChange7d;
    @SerializedName("last_updated")
    @Expose
    private String lastUpdated;
    @SerializedName("price_aud")
    @Expose
    private String priceAud;
    @SerializedName("24h_volume_aud")
    @Expose
    private String _24hVolumeAud;
    @SerializedName("market_cap_aud")
    @Expose
    private String marketCapAud;
    @SerializedName("price_brl")
    @Expose
    private String priceBrl;
    @SerializedName("24h_volume_brl")
    @Expose
    private String _24hVolumeBrl;
    @SerializedName("market_cap_brl")
    @Expose
    private String marketCapBrl;
    @SerializedName("price_cad")
    @Expose
    private String priceCad;
    @SerializedName("24h_volume_cad")
    @Expose
    private String _24hVolumeCad;
    @SerializedName("market_cap_cad")
    @Expose
    private String marketCapCad;
    @SerializedName("price_chf")
    @Expose
    private String priceChf;
    @SerializedName("24h_volume_chf")
    @Expose
    private String _24hVolumeChf;
    @SerializedName("market_cap_chf")
    @Expose
    private String marketCapChf;
    @SerializedName("price_cny")
    @Expose
    private String priceCny;
    @SerializedName("24h_volume_cny")
    @Expose
    private String _24hVolumeCny;
    @SerializedName("market_cap_cny")
    @Expose
    private String marketCapCny;
    @SerializedName("price_eur")
    @Expose
    private String priceEur;
    @SerializedName("24h_volume_eur")
    @Expose
    private String _24hVolumeEur;
    @SerializedName("market_cap_eur")
    @Expose
    private String marketCapEur;
    @SerializedName("price_gbt")
    @Expose
    private String priceGbt;
    @SerializedName("24h_volume_gbt")
    @Expose
    private String _24hVolumeGbt;
    @SerializedName("market_cap_gbt")
    @Expose
    private String marketCapGbt;
    @SerializedName("price_hkd")
    @Expose
    private String priceHkd;
    @SerializedName("24h_volume_hkd")
    @Expose
    private String _24hVolumeHkd;
    @SerializedName("market_cap_hkd")
    @Expose
    private String marketCapHkd;
    @SerializedName("price_idr")
    @Expose
    private String priceIdr;
    @SerializedName("24h_volume_idr")
    @Expose
    private String _24hVolumeIdr;
    @SerializedName("market_cap_idr")
    @Expose
    private String marketCapIdr;
    @SerializedName("price_inr")
    @Expose
    private String priceInr;
    @SerializedName("24h_volume_inr")
    @Expose
    private String _24hVolumeInr;
    @SerializedName("market_cap_inr")
    @Expose
    private String marketCapInr;
    @SerializedName("price_jpy")
    @Expose
    private String priceJpy;
    @SerializedName("24h_volume_jpy")
    @Expose
    private String _24hVolumeJpy;
    @SerializedName("market_cap_jpy")
    @Expose
    private String marketCapJpy;
    @SerializedName("price_krw")
    @Expose
    private String priceKrw;
    @SerializedName("24h_volume_krw")
    @Expose
    private String _24hVolumeKrw;
    @SerializedName("market_cap_krw")
    @Expose
    private String marketCapKrw;
    @SerializedName("price_mxn")
    @Expose
    private String priceMxn;
    @SerializedName("24h_volume_mxn")
    @Expose
    private String _24hVolumeMxn;
    @SerializedName("market_cap_mxn")
    @Expose
    private String marketCapMxn;
    @SerializedName("price_rub")
    @Expose
    private String priceRub;
    @SerializedName("24h_volume_rub")
    @Expose
    private String _24hVolumeRub;
    @SerializedName("market_cap_rub")
    @Expose
    private String marketCapRub;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(String priceUsd) {
        this.priceUsd = priceUsd;
    }

    public String getPriceBtc() {
        return priceBtc;
    }

    public void setPriceBtc(String priceBtc) {
        this.priceBtc = priceBtc;
    }

    public String get24hVolumeUsd() {
        return _24hVolumeUsd;
    }

    public void set24hVolumeUsd(String _24hVolumeUsd) {
        this._24hVolumeUsd = _24hVolumeUsd;
    }

    public String getMarketCapUsd() {
        return marketCapUsd;
    }

    public void setMarketCapUsd(String marketCapUsd) {
        this.marketCapUsd = marketCapUsd;
    }

    public String getAvailableSupply() {
        return availableSupply;
    }

    public void setAvailableSupply(String availableSupply) {
        this.availableSupply = availableSupply;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    public String getPercentChange1h() {
        return percentChange1h;
    }

    public void setPercentChange1h(String percentChange1h) {
        this.percentChange1h = percentChange1h;
    }

    public String getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(String percentChange24h) {
        this.percentChange24h = percentChange24h;
    }

    public String getPercentChange7d() {
        return percentChange7d;
    }

    public void setPercentChange7d(String percentChange7d) {
        this.percentChange7d = percentChange7d;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getPriceAud() {
        return priceAud;
    }

    public void setPriceAud(String priceAud) {
        this.priceAud = priceAud;
    }

    public String get24hVolumeAud() {
        return _24hVolumeAud;
    }

    public void set24hVolumeAud(String _24hVolumeAud) {
        this._24hVolumeAud = _24hVolumeAud;
    }

    public String getMarketCapAud() {
        return marketCapAud;
    }

    public void setMarketCapAud(String marketCapAud) {
        this.marketCapAud = marketCapAud;
    }

    public String getPriceBrl() {
        return priceBrl;
    }

    public void setPriceBrl(String priceBrl) {
        this.priceBrl = priceBrl;
    }

    public String get24hVolumeBrl() {
        return _24hVolumeBrl;
    }

    public void set24hVolumeBrl(String _24hVolumeBrl) {
        this._24hVolumeBrl = _24hVolumeBrl;
    }

    public String getMarketCapBrl() {
        return marketCapBrl;
    }

    public void setMarketCapBrl(String marketCapBrl) {
        this.marketCapBrl = marketCapBrl;
    }
    public String getPriceCad() {
        return priceCad;
    }

    public void setPriceCad(String priceCad) {
        this.priceCad = priceCad;
    }

    public String get24hVolumeCad() {
        return _24hVolumeCad;
    }

    public void set24hVolumeCad(String _24hVolumeCad) {
        this._24hVolumeCad = _24hVolumeCad;
    }

    public String getMarketCapCad() {
        return marketCapCad;
    }

    public void setMarketCapCad(String marketCapCad) {
        this.marketCapCad = marketCapCad;
    }
    public String getPriceChf() {
        return priceChf;
    }

    public void setPriceChf(String priceChf) {
        this.priceChf = priceChf;
    }

    public String get24hVolumeChf() {
        return _24hVolumeChf;
    }

    public void set24hVolumeChf(String _24hVolumeChf) {
        this._24hVolumeChf = _24hVolumeChf;
    }

    public String getMarketCapChf() {
        return marketCapChf;
    }

    public void setMarketCapChf(String marketCapChf) {
        this.marketCapChf = marketCapChf;
    }
    public String getPriceCny() {
        return priceCny;
    }

    public void setPriceCny(String priceCny) {
        this.priceCny = priceCny;
    }

    public String get24hVolumeCny() {
        return _24hVolumeCny;
    }

    public void set24hVolumeCny(String _24hVolumeCny) {
        this._24hVolumeCny = _24hVolumeCny;
    }

    public String getMarketCapCny() {
        return marketCapCny;
    }

    public void setMarketCapCny(String marketCapCny) {
        this.marketCapCny = marketCapCny;
    }

    public String getPriceEur() {
        return priceEur;
    }

    public void setPriceEur(String priceEur) {
        this.priceEur = priceEur;
    }

    public String get24hVolumeEur() {
        return _24hVolumeEur;
    }

    public void set24hVolumeEur(String _24hVolumeEur) {
        this._24hVolumeEur = _24hVolumeEur;
    }

    public String getMarketCapEur() {
        return marketCapEur;
    }

    public void setMarketCapEur(String marketCapEur) {
        this.marketCapEur = marketCapEur;
    }

    public String getPriceGbt() {
        return priceGbt;
    }

    public void setPriceGbt(String priceGbt) {
        this.priceGbt = priceGbt;
    }

    public String get24hVolumeGbt() {
        return _24hVolumeGbt;
    }

    public void set24hVolumeGbt(String _24hVolumeGbt) {
        this._24hVolumeGbt = _24hVolumeGbt;
    }

    public String getMarketCapGbt() {
        return marketCapGbt;
    }

    public void setMarketCapGbt(String marketCapGbt) {
        this.marketCapGbt = marketCapGbt;
    }

    public String getPriceHkd() {
        return priceHkd;
    }

    public void setPriceHkd(String priceHkd) {
        this.priceHkd = priceHkd;
    }

    public String get24hVolumeHkd() {
        return _24hVolumeHkd;
    }

    public void set24hVolumeHkd(String _24hVolumeHkd) {
        this._24hVolumeHkd = _24hVolumeHkd;
    }

    public String getMarketCapHkd() {
        return marketCapHkd;
    }

    public void setMarketCapHkd(String marketCapHkd) {
        this.marketCapHkd = marketCapHkd;
    }

    public String getPriceIdr() {
        return priceIdr;
    }

    public void setPriceIdr(String priceIdr) {
        this.priceIdr = priceIdr;
    }

    public String get24hVolumeIdr() {
        return _24hVolumeIdr;
    }

    public void set24hVolumeIdr(String _24hVolumeIdr) {
        this._24hVolumeIdr = _24hVolumeIdr;
    }

    public String getMarketCapIdr() {
        return marketCapIdr;
    }

    public void setMarketCapIdr(String marketCapIdr) {
        this.marketCapIdr = marketCapIdr;
    }

    public String getPriceInr() {
        return priceInr;
    }

    public void setPriceInr(String priceInr) {
        this.priceInr = priceInr;
    }

    public String get24hVolumeInr() {
        return _24hVolumeInr;
    }

    public void set24hVolumeInr(String _24hVolumeInr) {
        this._24hVolumeInr = _24hVolumeInr;
    }

    public String getMarketCapInr() {
        return marketCapInr;
    }

    public void setMarketCapInr(String marketCapInr) {
        this.marketCapInr = marketCapInr;
    }

    public String getPriceJpy() {
        return priceJpy;
    }

    public void setPriceJpy(String priceJpy) {
        this.priceJpy = priceJpy;
    }

    public String get24hVolumeJpy() {
        return _24hVolumeJpy;
    }

    public void set24hVolumeJpy(String _24hVolumeJpy) {
        this._24hVolumeJpy = _24hVolumeJpy;
    }

    public String getMarketCapJpy() {
        return marketCapJpy;
    }

    public void setMarketCapJpy(String marketCapJpy) {
        this.marketCapJpy = marketCapJpy;
    }

    public String getPriceKrw() {
        return priceKrw;
    }

    public void setPriceKrw(String priceKrw) {
        this.priceKrw = priceKrw;
    }

    public String get24hVolumeKrw() {
        return _24hVolumeKrw;
    }

    public void set24hVolumeKrw(String _24hVolumeKrw) {
        this._24hVolumeKrw = _24hVolumeKrw;
    }

    public String getMarketCapKrw() {
        return marketCapKrw;
    }

    public void setMarketCapKrw(String marketCapKrw) {
        this.marketCapKrw = marketCapKrw;
    }

    public String getPriceMxn() {
        return priceMxn;
    }

    public void setPriceMxn(String priceMxn) {
        this.priceMxn = priceMxn;
    }

    public String get24hVolumeMxn() {
        return _24hVolumeMxn;
    }

    public void set24hVolumeMxn(String _24hVolumeMxn) {
        this._24hVolumeMxn = _24hVolumeMxn;
    }

    public String getMarketCapMxn() {
        return marketCapMxn;
    }

    public void setMarketCapMxn(String marketCapMxn) {
        this.marketCapMxn = marketCapMxn;
    }

    public String getPriceRub() {
        return priceRub;
    }

    public void setPriceRub(String priceRub) {
        this.priceRub = priceRub;
    }

    public String get24hVolumeRub() {
        return _24hVolumeRub;
    }

    public void set24hVolumeRub(String _24hVolumeRub) {
        this._24hVolumeRub = _24hVolumeRub;
    }

    public String getMarketCapRub() {
        return marketCapRub;
    }

    public void setMarketCapRub(String marketCapRub) {
        this.marketCapRub = marketCapRub;
    }
}