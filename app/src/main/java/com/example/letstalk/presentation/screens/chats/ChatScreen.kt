package com.example.letstalk.presentation.screens.chats

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.letstalk.R
import com.example.letstalk.data.model.Message
import com.example.letstalk.presentation.screens.chats.viewmodel.ChatViewModel
import com.example.letstalk.utils.Resource
import com.example.letstalk.utils.UserFormatter
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatScreen() {

    var message by remember { mutableStateOf("") }
    val chatViewModel: ChatViewModel = hiltViewModel()
    val loadErrorUiState=chatViewModel.loadErrorUiState.collectAsState()
    val sentMsgUiState = chatViewModel.chatUiDataHolder.sentMessage.collectAsState(Resource.Loading)
    val chatMessages = chatViewModel.chatUiDataHolder.chatMessages.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loadErrorUiState.value.error) {
        val error= loadErrorUiState.value.error
        if(error!=null)
            snackBarHostState.showSnackbar(error)
    }

    Scaffold( containerColor = MaterialTheme.colorScheme.background
        , topBar = { AppBar() }, snackbarHost = { SnackbarHost(snackBarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if(loadErrorUiState.value.loading){
                Box(modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Please wait...", color = MaterialTheme.colorScheme.onBackground,
                        style = TextStyle(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier,
                        fontSize = 20.sp
                    )
                }
            }else{
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    reverseLayout = true,
                ) {
                    items(chatMessages.value) { message ->
                        ChatBubble(message)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .background(MaterialTheme.colorScheme.background),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .clip(shape = RoundedCornerShape(20)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor =MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.outline,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledLeadingIconColor = Color.Transparent
                    ),
                    value = message,
                    trailingIcon = {
                        IconButton(

                            onClick = {
                                chatViewModel.sendMessage(message)
                                message = ""
                            },
                            modifier = Modifier.size(10.dp).padding(horizontal = 2.dp),
                            enabled = message.isNotEmpty()
                        ) {
                        }
                        Icon(Icons.AutoMirrored.TwoTone.Send, contentDescription = null)
                    },
                    onValueChange = { message = it },
                    placeholder = { Text("Type a message") })
            }
        }
    }
}
@SuppressLint("SuspiciousIndentation")
@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser =
    message.senderId.equals(FirebaseAuth.getInstance().currentUser?.uid, ignoreCase = false)
    val backGroundColor = if(isCurrentUser) MaterialTheme.colorScheme.secondary
    else MaterialTheme.colorScheme.surface

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .widthIn(max = 250.dp, min = 100.dp),
                shape = RoundedCornerShape(
                    topStart = 10.dp, topEnd = 10.dp,
                    bottomEnd = if (isCurrentUser) 0.dp else 10.dp,
                    bottomStart = if (isCurrentUser) 10.dp else 0.dp
                )
            ) {
                ConstraintLayout(modifier = Modifier
                    .background(backGroundColor)
                    .padding(horizontal = 15.dp, vertical = 8.dp)
                ) {
                    val (messageRef,time)=createRefs()
                    Text(
                        text =message.message.trim(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        lineHeight = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .constrainAs(messageRef){
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                            }
                    )
                    Text(
//                        text = "3:15 pm ",
                        text=message.dateTime,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .constrainAs(time){
                                top.linkTo(messageRef.bottom,margin = 4.dp)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            }
                    )
                }
            }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(){
    val chatViewModel= hiltViewModel<ChatViewModel>()
    val friendDetails=chatViewModel.chatUiDataHolder.friendDetail.collectAsStateWithLifecycle()

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = if(isSystemInDarkTheme()) MaterialTheme.colorScheme.background
        else MaterialTheme.colorScheme.surface),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyProfile(friendDetails.value.imageData.imageUrl)
                Spacer(
                    modifier = Modifier
                        .width(10.dp)
                )
               Column(
                   verticalArrangement = Arrangement.Center) {
                   Text(
                       text = run{
                           UserFormatter.formatUserName(friendDetails.value.username)
                       },
                       modifier = Modifier,
                       fontSize = 17.sp,
                       fontWeight = FontWeight.W500,
                       color = MaterialTheme.colorScheme.onBackground
                   )
                   Text(
                       text =  friendDetails.value.status,
                       modifier = Modifier,
                       fontSize = 12.sp,
                       fontWeight = FontWeight.W300,
                       color = run{
                           if(friendDetails.value.status.equals("online", ignoreCase = true))
                               MaterialTheme.colorScheme.primary
                           else Color.Red
                       }
                   )
               }
            }
        }
    )
}
@Composable
fun MyProfile(imageUrl: String) {
    Card(
        shape = CircleShape, modifier = Modifier
            .size(50.dp),
        elevation = CardDefaults.cardElevation(25.dp)
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
                .clip(CircleShape)
        )
    }
}