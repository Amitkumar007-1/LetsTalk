package com.example.letstalk.presentation.screens.sign_up

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.letstalk.R
import com.example.letstalk.presentation.screens.sign_up.viewmodel.SignUpViewModel
import com.example.letstalk.common.utils.AuthUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val snackBarState = remember { SnackbarHostState() }
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    val signUpUiState = signUpViewModel.signUpUiState.collectAsState(AuthUiState.Nothing)


    LaunchedEffect(signUpUiState.value) {
        when (val state = signUpUiState.value) {
            is AuthUiState.Success -> {
                navController.navigate("home") {
                    popUpTo("signin") { inclusive = true }
                }
            }

            is AuthUiState.Error -> {
                snackBarState.showSnackbar(state.error!!)
            }

            else -> Unit
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarState) },
        containerColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .windowInsetsPadding(WindowInsets.ime),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(id = R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(0.75f),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                shape = RoundedCornerShape(5.dp),
                maxLines = 1,
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Person, contentDescription = null)
                },
                label = {
                    Text(
                        text = "Name", fontSize = 15.sp,
                        textDecoration = TextDecoration.None
                    )
                }, colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,


                    )
            )

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(
                value = email,
                modifier = Modifier.fillMaxWidth(0.75f),
                onValueChange = { email = it },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                shape = RoundedCornerShape(5.dp),
                maxLines = 1,
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.MailOutline, contentDescription = null)
                },
                label = {
                    Text(
                        text = "Email", fontSize = 15.sp,
                        textDecoration = TextDecoration.None
                    )
                }, colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,


                    )
            )

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(
                value = password,
                modifier = Modifier.fillMaxWidth(0.75f),
                onValueChange = { password = it },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                shape = RoundedCornerShape(5.dp),
                maxLines = 1,
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
                },
                label = {
                    Text(
                        text = "Password",
                        fontSize = 15.sp,
                        textDecoration = TextDecoration.None
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,


                    )

            )

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(
                value = confirmPassword,
                modifier = Modifier.fillMaxWidth(0.75f),
                onValueChange = { confirmPassword = it },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                shape = RoundedCornerShape(5.dp),
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                supportingText = {
                    if (confirmPassword.isNotBlank() && !password.equals(
                            confirmPassword,
                            ignoreCase = false
                        )
                    ) Text(
                        text = "Password is not matched"
                    )
                },
                enabled = password.isNotBlank(),
                isError = confirmPassword.isNotBlank() && !password.equals(
                    confirmPassword,
                    ignoreCase = false
                ),
                label = {
                    Text(
                        text = "Confirm Password", fontSize = 15.sp,
                        textDecoration = TextDecoration.None
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    errorLeadingIconColor = Color.Red,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,


                    )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    signUpViewModel.signUp(email.trim(), password.trim(), name.trim())
                },
                modifier = Modifier.fillMaxWidth(0.75f),
                enabled = email.isNotEmpty()
                        && name.isNotEmpty()
                        && password.isNotEmpty()
                        && confirmPassword.equals(password, ignoreCase = false)

            ) {
                if (signUpUiState.value is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 1.5.dp
                    )
                } else {
                    Text("Sign Up")
                }
            }
            Spacer(modifier = Modifier.height(5.dp))

            TextButton(onClick = {
                navController.popBackStack()
            }) {
                Text(text = "Already have account ?")
            }
        }
    }
}