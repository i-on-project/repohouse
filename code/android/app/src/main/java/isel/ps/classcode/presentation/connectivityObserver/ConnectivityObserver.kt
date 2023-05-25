package isel.ps.classcode.presentation.connectivityObserver

import kotlinx.coroutines.flow.Flow

/**
 * Contract for the connectivity observer. This will permit the app to be notified when the connectivity status changes.
 */
interface ConnectivityObserver {
    fun observer(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}
