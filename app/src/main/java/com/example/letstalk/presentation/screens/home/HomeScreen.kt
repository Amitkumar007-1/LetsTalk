package com.example.letstalk.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.letstalk.R
import com.example.letstalk.data.model.User
import com.example.letstalk.presentation.screens.home.viewmodel.HomeViewModel
import com.example.letstalk.utils.Resource


@Composable
fun HomeScreen(userClicked:(String)->Unit) {

    val homeViewModel:HomeViewModel= hiltViewModel()
    val homeUiState= homeViewModel.userUiState.collectAsStateWithLifecycle()


    Scaffold() { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
           when(val state =homeUiState.value){
               is Resource.Loading->{
                   CircularProgressIndicator(color = Color.Black)
               }
               is Resource.Error->{
                   Log.d("Error",state.message?:"Something went wrong ")
               }
               is Resource.Success->{
                   UserList(state.data,userClicked)
               }
           }
        }
    }
}

@Composable
fun UserList(userList: List<User>, userClicked: (String) -> Unit) {

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(userList) { user ->
                UserItem(user,userClicked)
            }
        }
    }

}

@Composable
fun UserItem(user: User, userClicked: (String) -> Unit) {
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight().clickable(onClick = {userClicked(user.userid)}),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color = Color.White)
                .padding(3.dp),
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
                        .border(2.dp, color = Color.Red, shape = CircleShape)
                )
            }
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .offset((-5).dp, (-5).dp)
                    .background(color = Color.Green, shape = CircleShape)
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