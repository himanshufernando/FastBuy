package project.ceyloninnovationlabs.fastbuy

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import project.ceyloninnovationlabs.fastbuy.services.listeners.OnBackListener
import project.ceyloninnovationlabs.fastbuy.services.listeners.OnNavigationListener


@HiltAndroidApp
class FastBuy : MultiDexApplication() {

    companion object {
        private var instance: FastBuy? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        private var onBackListener: OnBackListener? = null

        fun setOnBackResponseListener(listener: OnBackListener) { onBackListener = listener }
        fun getOnBackResponseListener(): OnBackListener? { return onBackListener }


        private var onNavigationListener: OnNavigationListener? = null

        fun setOnNavigationListener(listener: OnNavigationListener) { onNavigationListener = listener }
        fun getOnNavigationListener(): OnNavigationListener? { return onNavigationListener }




    }

    init {
        instance = this
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

    }


}
