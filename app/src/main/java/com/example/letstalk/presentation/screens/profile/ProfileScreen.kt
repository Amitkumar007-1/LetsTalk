package com.example.letstalk.presentation.screens.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.letstalk.R
import com.example.letstalk.data.model.User
import com.example.letstalk.presentation.screens.profile.viewmodel.ProfileViewModel
import java.io.File


val LocalUser = compositionLocalOf<User> { error("User not present") }

//@Preview(showBackground = true)
@Composable
fun ProfileScreen() {
    val snackBarState = remember { SnackbarHostState() }
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val loadErrorUiState = profileViewModel.loadErrorUiState.collectAsState()
    val profileUiState =
        profileViewModel.profileUiStateHolder.profileState.collectAsStateWithLifecycle()

//    LaunchedEffect(loadErrorUiState.value.error) {
//        val error = loadErrorUiState.value.error
//        if (error != null)
//            snackBarState.showSnackbar(error)
//    }

    CompositionLocalProvider(LocalUser provides profileUiState.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        ) {
            ConstraintLayout {
                val (picRef, nameRef, loadingRef, addUpdatePicRef) = createRefs()

                ProfilePicComponent(modifier = Modifier.constrainAs(picRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
                if (loadErrorUiState.value.loading) {
                    println("loaadingggg")
                    LoadingBar(loadingRef = loadingRef)
                }

                UpdateNameComponent(modifier = Modifier.constrainAs(nameRef) {
                    top.linkTo(picRef.bottom)
                })

                if (profileUiState.value.imageData.imageUrl.isEmpty() && !loadErrorUiState.value.loading) {
                    AddPicComponent(modifier = Modifier.constrainAs(addUpdatePicRef) {
                        top.linkTo(nameRef.bottom)
                    })
                } else if (profileUiState.value.imageData.imageUrl.isNotEmpty() && !loadErrorUiState.value.loading) {
                    UpdateDeletePicComponent(modifier = Modifier.constrainAs(addUpdatePicRef) {
                        top.linkTo(nameRef.bottom)
                    })
                }
            }
        }
    }
}

@Composable
fun rememberActivityLauncher(context: Context, onImageSelected: (file: File) -> Unit): () -> Unit {
    val imagePicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            if (uri != null && imageFile(context, uri)) {
                val file = convertToFile(context, uri)
                onImageSelected(file)
            } else if (uri != null && !imageFile(context, uri)) {
                Toast.makeText(context, "Please select valid image", Toast.LENGTH_SHORT).show()
            }
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.RequestPermission()
    ) { grant ->
        if (grant) {
            imagePicker.launch("images/*")
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    return {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val hasPermission =
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            imagePicker.launch("image/*")
        } else {
            permissionLauncher.launch(permission)
        }
    }
}

@Composable
fun UpdateDeletePicComponent(modifier: Modifier) {
    val user = LocalUser.current
    val context = LocalContext.current
    var alertDialog by remember { mutableStateOf(false) }
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val activityResult = rememberActivityLauncher(context) { file ->
        profileViewModel.updateProfilePic(user.imageData.publicId,file)
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clickable { activityResult() }
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Update profile photo",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clickable {
                    alertDialog=true
                }
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                tint = Color.Red
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Delete profile photo",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if(alertDialog){
            AlertDialogBox({alertDialog=false}) {
                profileViewModel.deletePic(user.imageData.publicId)
                alertDialog=false
            }
        }
    }
}


@Composable
fun AddPicComponent(modifier: Modifier) {
    val context = LocalContext.current
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val activityResult = rememberActivityLauncher(context) { file ->
        profileViewModel.uploadProfilePic(file)
    }

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { activityResult() }
            .padding(10.dp)
            .then(modifier)
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Filled.AddCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Add Profile Photo",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun UpdateNameComponent(modifier: Modifier) {


    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { }
            .padding(10.dp)
            .then(modifier)
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
//            tint = MaterialTheme.colorScheme.onBackground
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = "Name", color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Amit",
                color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
                fontSize = 12.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.W400
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogBox( dismiss: () -> Unit, confirm: () -> Unit) {
    AlertDialog(onDismissRequest = {}, containerColor = MaterialTheme.colorScheme.surface, confirmButton = {
        TextButton(onClick = {
            confirm()
        }) {
            Text(text = "Yes")
        }
    }, dismissButton = {
        TextButton(onClick = {
            dismiss()
        }) {
            Text(text = "No")
        }
    }, title = { Text("Are you sure ?") })
}

@Composable
fun ProfilePicComponent(modifier: Modifier) {
    val user = LocalUser.current
    Card(
        shape = CircleShape, modifier = Modifier
            .padding(top = 50.dp, bottom = 50.dp)
            .size(100.dp)
            .then(modifier),
        elevation = CardDefaults.cardElevation(100.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = user.imageData.imageUrl,
                error = painterResource(id = R.drawable.ic_user_icon),
                placeholder = painterResource(id = R.drawable.ic_user_icon)
            ),
            contentScale = ContentScale.Crop,
            contentDescription = "profile_img",
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun ConstraintLayoutScope.LoadingBar(loadingRef: ConstrainedLayoutReference) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .constrainAs(loadingRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Please wait", color = MaterialTheme.colorScheme.tertiary)
    }
}

fun convertToFile(context: Context, uri: Uri): File {
    val inputUri = context.contentResolver.openInputStream(uri)
    val file = File.createTempFile("file", ".jpg")
    file.outputStream().use { outputStream ->
        inputUri?.copyTo(outputStream)
    }
    return file
}

fun imageFile(context: Context, uri: Uri): Boolean {
    val type = context.contentResolver.getType(uri)
    return type?.startsWith("image/") == true
}
