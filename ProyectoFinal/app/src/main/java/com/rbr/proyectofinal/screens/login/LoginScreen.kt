package com.rbr.proyectofinal.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rbr.proyectofinal.R
import com.rbr.proyectofinal.ui.navigation.Screens

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current

    val errorMessage = remember { mutableStateOf("") }

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    if (currentUser != null) {
        LaunchedEffect(Unit) {
            navController.navigate(Screens.HomeScreen.name) {
                popUpTo(Screens.LoginScreen.name) {
                    inclusive = true
                }
            }
        }
    }

    val showLoginForm = rememberSaveable { mutableStateOf(true) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            viewModel.signInWithGoogleCredential(credential) {
                navController.navigate(Screens.HomeScreen.name)
            }
        } catch (ex: Exception) {
            Log.d("My Login", "GoogleSignIn falló: ${ex.message}")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "PhotoQuest", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(75.dp)
                    .padding(bottom = 20.dp)
            )


            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showLoginForm.value) {
                    Text(text = "Inicia Sesión", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    UserLoginForm { email, password ->
                        viewModel.signInWithEmailAndPassword(email, password) { success ->
                            if (success) {
                                navController.navigate(Screens.HomeScreen.name)
                            } else {
                                errorMessage.value = "Usuario o contraseña incorrectos"
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Crear una Cuenta", fontWeight = FontWeight.Bold, fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    UserSignUpForm { name, email, password, sexo, edad ->
                        viewModel.createUserWithEmailAndPassword(name, email, password, sexo, edad) {
                            navController.navigate(Screens.HomeScreen.name)
                        }
                    }
                }
            }


            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val text1 =
                    if (showLoginForm.value) "¿No tienes cuenta?" else "¿Ya tienes cuenta?"
                val text2 = if (showLoginForm.value) "Crear cuenta" else "Iniciar sesión"
                Text(
                    text = text1, fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = text2,
                    modifier = Modifier
                        .clickable { showLoginForm.value = !showLoginForm.value }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        val options = GoogleSignInOptions
                            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken("847750422956-632g78ee6899akkf1ns5u9ps5gng5ac3.apps.googleusercontent.com")
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, options)
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Login con GOOGLE",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp)
                )

                Text(
                    text = "Login con Google", fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun UserLoginForm(onDone: (String, String) -> Unit) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        EmailInput(emailState = email)
        PasswordInput(
            passwordState = password,
            passwordVisible = rememberSaveable { mutableStateOf(false) })
        Spacer(modifier = Modifier.height(10.dp))
        SubmitButton(
            textId = "Iniciar Sesión",
            inputValido = email.value.isNotBlank() && password.value.isNotBlank()
        ) {
            onDone(email.value, password.value)
        }
    }
}

@Composable
fun UserSignUpForm(onDone: (String, String, String, String, Float) -> Unit) {
    val name = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val selectedSexo = rememberSaveable { mutableStateOf("") } // Estado para el sexo seleccionado
    val showDialog = remember { mutableStateOf(false) } // Estado para mostrar el diálogo
    val edad = rememberSaveable { mutableStateOf(18f) } // Estado para la edad seleccionada

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        InputField(valuestate = name, labelId = "Nombre", keyboardType = KeyboardType.Text)
        EmailInput(emailState = email)
        PasswordInput(
            passwordState = password,
            passwordVisible = rememberSaveable { mutableStateOf(false) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // RadioGroup para seleccionar el sexo
        Text(text = "Selecciona tu sexo:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedSexo.value == "Masculino",
                onClick = { selectedSexo.value = "Masculino" }
            )
            Text(text = "Masculino", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = selectedSexo.value == "Femenino",
                onClick = { selectedSexo.value = "Femenino" }
            )
            Text(text = "Femenino", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = selectedSexo.value == "Otro",
                onClick = { selectedSexo.value = "Otro" }
            )
            Text(text = "Otro")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Selecciona tu edad: ${edad.value.toInt()} años", fontSize = 16.sp)
        Slider(
            value = edad.value,
            onValueChange = { edad.value = it },
            valueRange = 0f..100f,
            steps = 100,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        SubmitButton(
            textId = "Crear Cuenta",
            inputValido = name.value.isNotBlank() &&
                    email.value.isNotBlank() &&
                    password.value.isNotBlank() &&
                    selectedSexo.value.isNotBlank()
        ) {
            showDialog.value = true
            onDone(name.value, email.value, password.value, selectedSexo.value, edad.value)
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Aceptar")
                }
            },
            title = { Text("Cuenta Creada") },
            text = { Text("Tu cuenta ha sido creada con éxito.") }
        )
    }
}


@Composable
fun SubmitButton(
    textId: String, inputValido: Boolean, onClic: () -> Unit
) {
    Button(
        onClick = onClic,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        enabled = inputValido,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF000000), contentColor = Color.White
        )
    ) {
        Text(text = textId, modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun EmailInput(emailState: MutableState<String>) {
    InputField(valuestate = emailState,
        labelId = "Email",
        keyboardType = KeyboardType.Email,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Email, contentDescription = null)
        })
}

@Composable
fun PasswordInput(
    passwordState: MutableState<String>, passwordVisible: MutableState<Boolean>
) {
    OutlinedTextField(value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = "Contraseña") },
        singleLine = true,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                val image =
                    if (passwordVisible.value) Icons.Default.VisibilityOff else Icons.Default.Visibility
                Icon(imageVector = image, contentDescription = null)
            }
        })
}

@Composable
fun InputField(
    valuestate: MutableState<String>,
    labelId: String,
    keyboardType: KeyboardType,
    isSingleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(value = valuestate.value,
        onValueChange = { valuestate.value = it },
        label = { Text(text = labelId) },
        singleLine = isSingleLine,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = leadingIcon
    )
}


