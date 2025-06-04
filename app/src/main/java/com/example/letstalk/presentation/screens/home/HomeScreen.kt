package com.example.letstalk.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

val LocalNavController =
    staticCompositionLocalOf<NavController> { error("No NavController Provided") }

@Composable
fun HomeScreen(navController: NavController) {

    val homeViewModel= hiltViewModel<HomeViewModel>()
    val loadErrorUiState = homeViewModel.loadErrorUiState.collectAsState()
    val snackBarState = remember { SnackbarHostState() }

//    val homeViewModel: HomeViewModel = hiltViewModel()
//    var logOutAlert by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalNavController provides navController) {
        Scaffold(topBar = {  AppBar() },snackbarHost = { SnackbarHost(snackBarState) })
        { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val (loadingBarRef, errorSnack, usersList) = createRefs()
                val modifier = Modifier.constrainAs(usersList) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                UserListComponent(modifier)
                if(loadErrorUiState.value.loading){
                    LoadingBar(loadingBarRef)
                }
                LaunchedEffect(loadErrorUiState.value.error) {
                    val error=loadErrorUiState.value.error
                    if(error!=null)
                    snackBarState.showSnackbar(error)
                }
            }
        }
    }
}


@Composable
fun UserListComponent(modifier: Modifier){
    val homeViewModel:HomeViewModel= hiltViewModel()
    val userList = homeViewModel.homeUiStateHolder.userListState.collectAsStateWithLifecycle()

//    LaunchedEffect(homeUiState.value) {
//        when(homeUiState.value){
//            is Resource.Loading-> onLoading(true)
//            is Resource.Error->{
//                onLoading(false)
//            }
//        }
//    }
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
        CircularProgressIndicator(color = Color.White)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Please wait", color = Color.White)
    }
}
@Composable
fun AppBar() {
    var logOut by remember { mutableStateOf(false) }
    val homeViewModel:HomeViewModel= hiltViewModel()
//    val signOutState = homeViewModel.signOutState.collectAsState(AuthUiState.Nothing)
    val signOutState=homeViewModel.homeUiStateHolder.signOutState.collectAsState()
    val navController= LocalNavController.current
    HomeAppBar {itemClicked->
        logOut=itemClicked.equals("logout", ignoreCase = true)
    }
    LaunchedEffect(signOutState.value) {
        if(signOutState.value.contains("sign out", ignoreCase = true)){
            navController.navigate("signin"){
                popUpTo("home"){inclusive=true}
            }
        }

    }
    if(logOut){
        AlertDialog {action->
            logOut=false
            action?.let {
                homeViewModel.signOut()
            }
        }
    }

}

@Composable
fun AlertDialog(disMiss: (String?) -> Unit) {
    androidx.compose.material3.AlertDialog(onDismissRequest = { disMiss(null) }, confirmButton = {
        TextButton(onClick = {
            disMiss("logout")
            // logOut
        }) {
            Text(text = "Yes")
        }
    }, dismissButton = {
        TextButton(onClick = { disMiss(null) }) {
            Text(text = "No")
        }
    }, title = { Text(text = "Are you sure ?") })
}

//@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(onItemClick: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val homeViewModel= hiltViewModel<HomeViewModel>()
    val profileState=homeViewModel.homeUiStateHolder.userProfileState.collectAsState(User())
    TopAppBar(modifier = Modifier
        .padding(10.dp), title = {
        Text(
            text = if(profileState.value.username.isNotBlank()) {
                val name=profileState.value.username.replaceFirstChar { it.uppercase()}
                "Welcome $name"
            }else "",
            style = MaterialTheme.typography.headlineMedium
        )
    },
        actions = {

            MyProfile(profileState.value.imageUrl) { expanded = true }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text(text = "Update profile") },
                    onClick = {
                        expanded = false
                        //Update Profile
                        onItemClick("update")
                    }
                )
                DropdownMenuItem(text = { Text(text = "Logout") },
                    onClick = {
                        expanded = false
                        //Sign Out
                        onItemClick("logout")
                    }
                )
            }
        }
    )
}

@Composable
fun MyProfile( imageUrl:String,onClick: () -> Unit) {
    Card(
        shape = CircleShape, modifier = Modifier
            .size(50.dp),
        elevation = CardDefaults.cardElevation(100.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model =imageUrl,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(userList) { user ->
                UserItem(user)
            }
        }
    }

}

@Composable
fun UserItem(user: User) {
    val navController= LocalNavController.current
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .clickable(onClick = {}),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.background)
                .padding(0.dp),
            Alignment.Center
        ) {
            Card(
                shape = CircleShape, modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = user.imageUrl,
                        error = painterResource(id = R.drawable.ic_user_icon),
                        placeholder = painterResource(id = R.drawable.ic_user_icon)
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = "profile_img",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable {
                            navController.navigate("chat/${user.userid}")
                        }
                )
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .offset((-10).dp, (-10).dp)
                    .background(color = if(user.status.equals("online", ignoreCase = true))
                        Color.Green else Color.Gray, shape = CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .align(Alignment.BottomEnd)


            )

        }
        Text(
            text = user.username,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}