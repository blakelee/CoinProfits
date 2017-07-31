# CoinProfits
Keep track of your cryptocurrency with this portfolio manager. Made in native Android to offer you the best user experience.

## Technologies used
This project makes use of MVVM architecture. The goal is to make everything reactive in order to make references between objects kept to a minimum. It utilizes Dagger 2 for dependency injection, Retrofit for REST client, Picasso for image loading and caching, Room Persistence Library for storage (SQLite), and uses the new lifecycle activities to make sure there are no leaks during activity lifecycle changes. 
Rxjava is being implemented instead of LiveData in order to stick to industry standards since the Android architecture components aren't yet finalized. 


![Main Screen](/docs/Screenshot_1500668368.png?raw=true "Main Screen")
![Add Currency](/docs/Screenshot_1500668417.png?raw=true "Add Currency")
![Currency Selected](/docs/Screenshot_1500668430.png?raw=true "Currency Selected")
![Merge Replace](/docs/Screenshot_1500668452.png?raw=true "Merge Replace")
![Settings](/docs/Screenshot_1500668373.png?raw=true "Settings Screen")
