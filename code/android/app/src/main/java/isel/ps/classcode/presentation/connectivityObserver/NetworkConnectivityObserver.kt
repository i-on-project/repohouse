package isel.ps.classcode.presentation.connectivityObserver

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Class that implements the connectivity observer contract. This will permit the app to be notified when the connectivity status changes, based on flows
 */
class NetworkConnectivityObserver(
    private val context: Context
): ConnectivityObserver {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    override fun observer(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(ConnectivityObserver.Status.Available) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(ConnectivityObserver.Status.Losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(ConnectivityObserver.Status.Lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(ConnectivityObserver.Status.Unavailable) }
                }
            }
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
        }.distinctUntilChanged() // Só da trigger quando muda
    }
}