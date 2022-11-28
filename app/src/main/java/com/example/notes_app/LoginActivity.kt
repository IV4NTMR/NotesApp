package com.example.notes_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: PromptInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Handler().postDelayed({
            biometricInit()
            biometricPrompt!!.authenticate(promptInfo!!)
        }, 4000)

        val button = findViewById<ImageButton>(R.id.imageButton)
        button.setOnClickListener{
            biometricInit()
            biometricPrompt!!.authenticate(promptInfo!!)
        }

    }

    fun biometricInit() {
        //Biometrica

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this@LoginActivity,
            executor!!, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    ).show()
                    val intent =  Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        promptInfo = PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
    }

}