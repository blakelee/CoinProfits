## Coin Profits Project Overview

This project is an Android application called Coin Profits, which is designed to help users track their cryptocurrency portfolio. It uses a variety of modern Android technologies and architectural patterns, including MVVM, Dagger 2, Retrofit, Picasso, Room Persistence Library, and RxJava.

### Inputs

The application takes several inputs:

*   **Cryptocurrency Data:** Real-time and historical cryptocurrency data is fetched from external APIs like CoinMarketCap and Ethplorer.
*   **User Transactions:** Users input their cryptocurrency transactions, including the currency, amount, buy-in price, and optionally, their public key for ETH/ERC20 tokens. 
*   **User Preferences:**  Users can customize settings like auto-refresh on startup and currency conversion preferences.

### Outputs

The application provides various outputs:

*   **Portfolio Overview:** A summarized view of the user's total portfolio value, 24-hour change, overall margins, 7-day change, and total buy-in amount.
*   **Detailed Holdings List:** A list of each cryptocurrency the user holds, with detailed information such as:
    *   Balance in fiat currency, cryptocurrency, Bitcoin, and Ethereum.
    *   Average buy-in price and total.
    *   Fiat and percentage margins.
    *   Interactive price charts with historical data for each currency. 
*   **Transaction Tracking:** Users can add, edit, and delete transactions to keep their portfolio up-to-date.
*   **Public Key Balance Tracking:** For ETH/ERC20 tokens, users can input their public key to automatically track their token balances.

### Usage

Coin Profits is designed to be user-friendly and intuitive. Users can easily add their cryptocurrency holdings, input their transactions, and track their portfolio performance. The application provides both a summarized portfolio overview and detailed information for each currency holding. The interactive price charts allow users to visualize the price history of their investments.  