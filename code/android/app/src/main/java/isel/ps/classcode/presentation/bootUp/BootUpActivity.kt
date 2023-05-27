package isel.ps.classcode.presentation.bootUp

import android.app.KeyguardManager
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.presentation.bootUp.services.BootUpServices
import isel.ps.classcode.presentation.login.LoginActivity
import isel.ps.classcode.presentation.menu.MenuActivity
import isel.ps.classcode.ui.theme.ClasscodeTheme

private const val BIOMETRIC_TAG = "BIOMETRIC"
class BootUpActivity : ComponentActivity() {

    private val bootUpServices: BootUpServices by lazy { (application as DependenciesContainer).bootUpServices }

    private val sessionStore: SessionStore by lazy { (application as DependenciesContainer).sessionStore }
    private var cancellationSignal: CancellationSignal? = null

    @Suppress("UNCHECKED_CAST")
    private val vm by viewModels<BootUpViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BootUpViewModel(sessionStore = sessionStore, bootUpServices = bootUpServices) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.getHome()
        vm.checkIfTokenExists()
        setContent {
            ClasscodeTheme {
                BootUpScreen(
                    strongBiometric = {
                        if (vm.tokensExists) {
                            launchBiometricPrompt()
                        } else {
                            LoginActivity.navigate(origin = this)
                        }
                    },
                )
            }
        }
    }

    private val authenticationCallback: AuthenticationCallback
        get() = object : AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(BIOMETRIC_TAG, "$errorCode :: $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(BIOMETRIC_TAG, "Authentication failed for an unknown reason")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                MenuActivity.navigate(origin = this@BootUpActivity)
            }
        }

    private fun launchBiometricPrompt() {
        if (checkBiometricSupport()) {
            val biometricPrompt = BiometricPrompt
                .Builder(this)
                .setTitle("Biometric Authentication")
                .setSubtitle("Login using fingerprint authentication")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .build()
            biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
        }
    }
    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            Log.d(BIOMETRIC_TAG, "Cancelled via signal")
        }
        return cancellationSignal as CancellationSignal
    }
    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isDeviceSecure) {
            Log.d(BIOMETRIC_TAG, "Fingerprint authentication has not been enabled in settings")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            Log.d(BIOMETRIC_TAG, "Fingerprint authentication permission is not enabled")
            return false
        }
        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }
}
