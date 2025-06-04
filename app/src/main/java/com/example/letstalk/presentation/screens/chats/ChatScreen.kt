package com.example.letstalk.presentation.screens.chats

import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Send
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.letstalk.data.model.Message
import com.example.letstalk.presentation.screens.chats.viewmodel.ChatViewModel
import com.example.letstalk.ui.theme.receiverBubble
import com.example.letstalk.ui.theme.senderBubble
import com.example.letstalk.utils.Resource
import com.google.firebase.auth.FirebaseAuth

//@Preview(showSystemUi = true)
@Composable
fun ChatScreen() {

    var message by remember { mutableStateOf("") }
    val chatViewModel: ChatViewModel = hiltViewModel()
    val sentMsgUiState = chatViewModel.sentMsgUiState.collectAsState(Resource.Loading)
    val chatMessages = chatViewModel.chatMessages.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }


    LaunchedEffect(sentMsgUiState.value) {
        when (val state = sentMsgUiState.value) {
            is Resource.Error -> {
                snackBarHostState.showSnackbar(state.message!!)
            }

            is Resource.Success -> {}
            else -> Unit
        }
    }
    LaunchedEffect(chatMessages.value) {
        when (val chatState = chatMessages.value) {
            is Resource.Error -> {
                snackBarHostState.showSnackbar(chatState.message!!)
            }

            is Resource.Success -> {

            }

            else -> Unit
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0C0C0C))
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val chatState = chatMessages.value) {
                is Resource.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        reverseLayout = true,
                    ) {
                        items(chatState.data) { message ->
                            ChatBubble(message)
                        }
                    }
                }

                is Resource.Loading -> {
                    Text(
                        text = "Please wait...", color = MaterialTheme.colorScheme.inverseOnSurface,
                        style = TextStyle(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier
                            .size(10.dp)
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                else -> Unit
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(Color(0xFF0C0C0C)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .clip(shape = RoundedCornerShape(20)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF344955),
                        unfocusedContainerColor = Color(0xFF344955),
                        disabledContainerColor = Color(0xFF344955),
                        focusedTrailingIconColor = Color(0xFF78A083),
                        unfocusedTrailingIconColor = Color(0xFF78A083),
                        disabledTrailingIconColor = Color(0xFF78A083),
                        focusedPlaceholderColor = Color.White,
                        unfocusedPlaceholderColor = Color.White,
                        cursorColor = Color(0xFF78A083),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
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
                            modifier = Modifier.padding(horizontal = 2.dp),
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
//@Preview(showBackground = true)
@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser =
    message.senderId.equals(FirebaseAuth.getInstance().currentUser?.uid, ignoreCase = false)
    val backGroundColor = if (isCurrentUser) senderBubble else receiverBubble

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
                        text =message.message.trim()  ,
//                        message.message.trim(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        lineHeight = 0.5.sp,
                        color = Color.White,
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
                        color = Color.LightGray,
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

//
//@Composable
//fun Preview() {
//
//    val isCurrentUser = true
////        message.senderId.equals(FirebaseAuth.getInstance().currentUser?.uid, ignoreCase = false)
//    val backGroundColor =
//        if (isCurrentUser) senderBubble else receiverBubble
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(5.dp),
//        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
//    ) {
//        Surface(
//            color = backGroundColor,
//            shape = RoundedCornerShape(
//                topStart = 10.dp, topEnd = 10.dp,
//                bottomEnd = if (isCurrentUser) 0.dp else 10.dp,
//                bottomStart = if (isCurrentUser) 10.dp else 0.dp
//            )
//
//        ) {
//
//            ConstraintLayout() {
//                val (messageRef, timeRef) = createRefs()
//                Text(
//                    text = "jwndjnwdjn",
//                    fontSize = 18.sp,
//                    color = Color.White,
//                    modifier = Modifier
//                        .padding(40.dp)
//                        .constrainAs(messageRef) {
//                            start.linkTo(parent.start)
//                            end.linkTo(parent.end)
//                            width = Dimension.fillToConstraints
//                        }
//
//
//                )
//                Text(
//                    modifier = Modifier
//                        .padding(bottom = 4.dp),
//                    fontSize = 14.sp,
//                    text = "time",
//                    color = Color.Gray
//                )
//            }
//
//
//        }
//    }
//}
//
//@Composable
//fun Test() {
////  Column(modifier=Modifier.fillMaxSize()) {
////      Text(text = "Hello Moto", modifier = Modifier
////          .background(Color.Red)
////          .padding(10.dp)
////          .padding(10.dp)
////          .background(Color.Green)
////          .padding(20.dp)
////          .background(Color.Yellow)
////          .fillMaxWidth()
////
////      )
////  }
//
//    val isCurrentUser = false
////        message.senderId.equals(FirebaseAuth.getInstance().currentUser?.uid, ignoreCase = false)
//    val backGroundColor =
//        if (isCurrentUser) senderBubble else receiverBubble
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .background(Color.Gray)
//                .padding(10.dp),
//            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Surface(
//                modifier = Modifier
//                    .wrapContentHeight()
//                    .wrapContentWidth()
//                    .widthIn(max = 250.dp, min = 100.dp),
//                shape = RoundedCornerShape(
//                    topStart = 10.dp, topEnd = 10.dp,
//                    bottomEnd = if (isCurrentUser) 0.dp else 10.dp,
//                    bottomStart = if (isCurrentUser) 10.dp else 0.dp
//                )
//            ) {
//                Column(
//                    modifier = Modifier
//                        .background(backGroundColor)
//                        .padding(8.dp)
//                        .wrapContentHeight(),
//                    verticalArrangement = Arrangement.spacedBy(10.dp)
//                ) {
//                    Text(
//                        text = "Hecjy",
////                        message.message.trim()
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.W400,
//                        lineHeight = 1.5.em,
//                        color = Color.White
//                    )
//
//                    Spacer(modifier = Modifier.height(2.5.dp))
//
//                    Row(
//                        horizontalArrangement = Arrangement.End, modifier = Modifier
//                            .wrapContentHeight()
//                    ) {
//                        Text(
//                            text = "3:15 pm ",
//                            fontSize = 14.sp,
//                            color = Color.LightGray,
//                            fontWeight = FontWeight.W400,
//                        )
//
//                    }
//                }
//            }
//        }
//    }
//}
//
////@Preview(showBackground = true)
//@Composable
//fun Test2() {
//
//}