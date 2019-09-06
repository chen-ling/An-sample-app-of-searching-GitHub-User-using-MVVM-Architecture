# An sample app of searching GitHub User using MVVM Architecture #

This application helps you to search github users easily,

<img src="https://i.imgur.com/34kmU89.png" width=150 height=300/>

### Folder Structure ###

* di - configuration of dependency injection
* extension - extensions used in this app
* model - data model
* service - network service and url request
* ui - components for constructing search result
* utils - several UI related utils now

### Libraries ###
* Android Support Library
* Android Architecture Components
* RxJava2
* Koin
* Retrofit, Okhttp
* Glide
* mockito

### Reminder ###
It's likely to throw API limit error since Github has definied [limited requests](https://developer.github.com/v3/#rate-limiting).
