package com.onourem.android.activity

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp
import com.onourem.android.activity.di.DaggerApplicationComponent
import com.onourem.android.activity.di.module.ApplicationModule
import contacts.core.Contacts
import contacts.core.entities.custom.CustomDataRegistry
import contacts.core.log.AndroidLogger
import contacts.entities.custom.gender.GenderRegistration
import contacts.entities.custom.googlecontacts.GoogleContactsRegistration
import contacts.entities.custom.handlename.HandleNameRegistration
import contacts.entities.custom.pokemon.PokemonRegistration
import contacts.entities.custom.rpg.RpgRegistration
import dagger.android.support.DaggerApplication

class OnouremApp : DaggerApplication() {

    val contacts by lazy(LazyThreadSafetyMode.NONE) {
        Contacts(
            this,
            customDataRegistry = CustomDataRegistry().register(
                GenderRegistration(),
                GoogleContactsRegistration(),
                HandleNameRegistration(),
                PokemonRegistration(),
                RpgRegistration()
            ),
            logger = AndroidLogger(redactMessages = !BuildConfig.DEBUG)
        )
    }

    private val applicationInjector = DaggerApplicationComponent
        .builder()
        .application(this)
        .applicationModule(ApplicationModule(this))
        .build()

    override fun applicationInjector() = applicationInjector

//    override fun applicationInjector(): AndroidInjector<DaggerApplication> {
//        return DaggerApplicationComponent
//            .builder()
//            .applicationModule(ApplicationModule(this))
//            .build()
//    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        //FacebookSdk.sdkInitialize(applicationContext)
//        AppEventsLogger.activateApp(this);
        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(this);
    }
}