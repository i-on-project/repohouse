package isel.ps.classcode.presentation.bootUp

import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.TAG
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.presentation.login.LoginActivity
import isel.ps.classcode.presentation.menu.MenuActivity
import isel.ps.classcode.ui.theme.ClasscodeTheme
private const val BIOMETRIC_TAG = "BIOMETRIC"
class BootUpActivity : ComponentActivity() {

    private val sessionStore: SessionStore by lazy { (application as DependenciesContainer).sessionStore }
    private var cancellationSignal: CancellationSignal? = null
    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, BootUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val vm by viewModels<BootUpViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BootUpViewModel(sessionStore = sessionStore) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.checkIfTokenExists()
        setContent {
            ClasscodeTheme {
                BootUpScreen(
                    actionHandler = {
                        if (vm.tokensExists) {
                            launchBiometricPrompt()
                        } else {
                            LoginActivity.navigate(origin = this)
                        }
                    }
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
                Log.d(BIOMETRIC_TAG, "Authentication was successful")
                Log.d(BIOMETRIC_TAG, "result crypto object: ${result.cryptoObject}")
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.d(BIOMETRIC_TAG, "Fingerprint authentication permission is not enabled")
            return false
        }
        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }
}