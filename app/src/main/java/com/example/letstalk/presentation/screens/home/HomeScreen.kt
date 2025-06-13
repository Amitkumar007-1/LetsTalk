package com.example.letstalk.presentation.screens.home

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.sharp.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.letstalk.R
import com.example.letstalk.data.model.User
import com.example.letstalk.presentation.screens.home.viewmodel.HomeViewModel
import com.example.letstalk.ui.theme.Accent
import com.example.letstalk.ui.theme.DarkGray
import com.example.letstalk.ui.theme.LightReceiver
import com.example.letstalk.utils.UserFormatter

val LocalNavController =
    staticCompositionLocalOf<NavController> { error("No NavController Provided") }

@Composable
fun HomeScreen(navController: NavController) {

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val loadErrorUiState = homeViewModel.loadErrorUiState.collectAsState()
    val snackBarState = remember { SnackbarHostState() }


    CompositionLocalProvider(LocalNavController provides navController) {
        Scaffold(topBar = { AppBar() }, snackbarHost = { SnackbarHost(snackBarState) })
        { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                val (loadingBarRef, errorSnack, usersList) = createRefs()
                val modifier = Modifier.constrainAs(usersList) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                UserListComponent(modifier)
                if (loadErrorUiState.value.loading) {
                    LoadingBar(loadingBarRef)
                }
                LaunchedEffect(loadErrorUiState.value.error) {
                    val error = loadErrorUiState.value.error
                    if (error != null)
                        snackBarState.showSnackbar(error)
                }
            }
        }
    }
}


@Composable
fun UserListComponent(modifier: Modifier) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val userList = homeViewModel.homeUiStateHolder.userListState.collectAsStateWithLifecycle()
    UserList(userList.value, modifier)
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

@Composable
fun AppBar() {
    var logOut by remember { mutableStateOf(false) }
    val homeViewModel: HomeViewModel = hiltViewModel()
    val signOutState = homeViewModel.homeUiStateHolder.signOutState.collectAsState()
    val navController = LocalNavController.current
//    HomeAppBar { itemClicked ->
//        if(itemClicked.equals("logout", ignoreCase = true)){
//            logOut=true
//        }else if(itemClicked.equals("update", ignoreCase = true)){
//            navController.navigate("profile")
//        }
//    }
    HomeAppBar(signOut = {
        logOut=true
    }){userId->
        navController.navigate("profile/$userId")
    }
    LaunchedEffect(signOutState.value) {
        if (signOutState.value.contains("sign out", ignoreCase = true)) {
            navController.navigate("signin") {
                popUpTo("home") { inclusive = true }
            }
        }

    }
    if (logOut) {
        AlertDialog { action ->
            logOut = false
            action?.let {
                homeViewModel.signOut()
            }
        }
    }

}

@Composable
fun AlertDialog(disMiss: (String?) -> Unit) {
    androidx.compose.material3.AlertDialog(
        iconContentColor = Color.Yellow,
        icon = {
            Icon(imageVector = Icons.Sharp.Warning, contentDescription = null)
        },
        containerColor = MaterialTheme.colorScheme.surface,

        onDismissRequest = { disMiss(null) },
        confirmButton = {
            TextButton(onClick = {
                disMiss("logout")
                // logOut
            }) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = { disMiss(null) }) {
                Text(text = "No")
            }
        }, title = { Text(text = "Are you sure ?") })
}

//@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(signOut:()->Unit,update:(String)->Unit) {
    var expanded by remember { mutableStateOf(false) }
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val profileState = homeViewModel.homeUiStateHolder.userProfileState.collectAsState(User())

    val infiniteTransition = rememberInfiniteTransition(label = "waving_hand_animation")
    val rotateAngle = if (expanded) {
        infiniteTransition.animateFloat(
            initialValue = -15f,
            targetValue = 15f,
            animationSpec = InfiniteRepeatableSpec(
                animation = tween(durationMillis = 600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "waving_hand_animation"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background
        else MaterialTheme.colorScheme.surface
    ), modifier = Modifier,
        title = {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "LetsTalk",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily(Font(R.font.headline))
                )
            }
        },
        actions = {

            MyProfile(profileState.value.imageData.imageUrl) { expanded = true }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(5.dp)
            ) {
                DropdownMenuItem(trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(rotateAngle.value),
                        painter = painterResource(id = R.drawable.waving_hand),
                        contentDescription = null,
                        tint = Color(0xFFff8000)
                    )
                }, text = {
                    Text(
                        text = run {
                            val name = profileState.value.username
                            "Hi ${UserFormatter.formatUserName(name)}"
                        },
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily(Font(R.font.headline_font))
                    )
                },
                    onClick = {}, enabled = false
                )
                DropdownMenuItem(text = { Text(text = "Update profile") },
                    onClick = {
                        expanded = false
                        //Update Profile
                        update(profileState.value.userid)
                    }
                )
                DropdownMenuItem(trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Accent
                    )
                },
                    text = { Text(text = "Logout") },
                    onClick = {
                        expanded = false
                        //Sign Out
                        signOut()
                    }
                )
            }
        }
    )
}

@Composable
fun MyProfile(imageUrl: String, onClick: () -> Unit) {
    Card(
        shape = CircleShape, modifier = Modifier
            .size(50.dp),
        elevation = CardDefaults.cardElevation(100.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUrl,
                error = painterResource(id = R.drawable.ic_user_icon),
                placeholder = painterResource(id = R.drawable.ic_user_icon)
            ),
            contentScale = ContentScale.Crop,
            contentDescription = "profile_img",
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
        )
    }
}

@Composable
fun UserList(userList: List<User>, modifier: Modifier) {

    Card(
        shape = RoundedCornerShape(7.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp)
            .then(modifier)
    ) {
        LazyRow(
            modifier = Modifier
                .background(if (isSystemInDarkTheme()) DarkGray else LightReceiver)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(userList) { user ->
                UserItem(user)
            }
        }
    }

}

@Composable
fun UserItem(user: User) {
    val navController = LocalNavController.current
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = CircleShape, modifier = Modifier
                    .fillMaxSize()
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
                        .clickable {
                            navController.navigate("chat/${user.userid}")
                        }
                )
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .offset((-10).dp, (-10).dp)
                    .background(
                        color = if (user.status.equals("online", ignoreCase = true))
                            Color.Green else Color.Gray, shape = CircleShape
                    )
                    .border(1.5.dp, Color.White, CircleShape)
                    .align(Alignment.BottomEnd)


            )

        }
        Text(
            text = run {
                val name = user.username.split(Regex("[ _]"))[0]
                name.replaceFirstChar { it.uppercase() }
            },
            maxLines = 3,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.W300,
            fontStyle = FontStyle.Normal
        )
    }
}