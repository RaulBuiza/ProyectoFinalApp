package com.rbr.proyectofinal.screens.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _loading = MutableLiveData(false)

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Google logueado!!!!")
                            home()
                        } else {
                            Log.d(
                                "MyLogin", "signInWithGoogle: ${task.result.toString()}"
                            )
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "Error al loguear con Google: ${ex.message}")
            }
        }

    fun signInWithEmailAndPassword(
        email: String, password: String, onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Inicio de sesión exitoso")
                            onResult(true)
                        } else {
                            Log.d("MyLogin", "Error al iniciar sesión: ${task.exception?.message}")
                            onResult(false)
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "Excepción al iniciar sesión: ${ex.message}")
                onResult(false)
            }
        }
    }

    fun createUserWithEmailAndPassword(
        name: String, email: String, password: String, sexo: String, edad: Float, home: () -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    createUser(name, email, password, sexo, edad)
                    home()
                } else {
                    Log.d("MyLogin", "Error Usuario no creado: ${task.exception?.message}")
                }
                _loading.value = false
            }
        }
    }

    private fun createUser(name: String, email: String, password: String, sexo: String, edad: Float) {
        val userId = auth.currentUser?.uid
        val user = mapOf(
            "name" to name,
            "email" to email,
            "password" to password,
            "sexo" to sexo,
            "edad" to edad.toInt()
        )

        firestore.collection("users").document(userId ?: "").set(user).addOnSuccessListener {
            Log.d("MyLogin", "Usuario guardado exitosamente en Firestore")
        }.addOnFailureListener { e ->
            Log.d("MyLogin", "Error al guardar usuario en Firestore: $e")
        }
    }
}


