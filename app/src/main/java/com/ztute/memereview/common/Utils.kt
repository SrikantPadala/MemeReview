package com.ztute.memereview.common

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import android.net.NetworkRequest
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.ztute.memereview.database.DatabaseMeme
import com.ztute.memereview.domain.model.Meme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber


fun <T> Fragment.collectStateFlowWithLifecycle(
    flow: Flow<T>,
    collector: suspend (T) -> Unit
) {
    lifecycleScope.launchWhenStarted {
        flow.collectLatest(collector)
    }
}

fun hasInternetConnection(application: Application): Boolean {
    val connectivityManager = application.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork)
                ?: return false
        return when {
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.activeNetworkInfo?.run {
            return when (type) {
                TYPE_WIFI -> true
                TYPE_MOBILE -> true
                TYPE_ETHERNET -> true
                else -> false
            }
        }
    }
    return false
}

val networkRequest = NetworkRequest.Builder()
    .addCapability(NET_CAPABILITY_INTERNET)
    .addTransportType(TRANSPORT_WIFI)
    .addTransportType(TRANSPORT_CELLULAR)
    .build()

fun getNetworkCallback(status: (Boolean) -> Unit): NetworkCallback {
    return object : NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            status(true)
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unmetered = networkCapabilities.hasCapability(
                NET_CAPABILITY_NOT_METERED
            )
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            status(false)
        }
    }
}


fun List<Meme>.asDatabaseModel(): List<DatabaseMeme> {
    return map {
        DatabaseMeme(
            id = it.id,
            boxCount = it.boxCount,
            height = it.height,
            name = it.name,
            url = it.url,
            width = it.width
        )
    }
}

//https://nezspencer.medium.com/navigation-components-a-fix-for-navigation-action-cannot-be-found-in-the-current-destination-95b63e16152e
fun NavController.safeNavigate(direction: NavDirections) {
    Timber.d("Click happened")
    currentDestination?.getAction(direction.actionId)?.run {
        Timber.d("Click Propagated")
        navigate(direction)
    }
}