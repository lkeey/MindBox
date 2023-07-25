package com.example.mindbox

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.mindbox.api.ChatGptApi
import com.example.mindbox.models.ImageGenerateRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : ComponentActivity() {

    private val myLive = MutableLiveData<String>()

    companion object {
        private const val TAG = "ActivityMain"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var userInput by remember {
                mutableStateOf("")
            }
            val img: String? by myLive.observeAsState()
            var isExpanded by remember {
                mutableStateOf(false)
            }
            var size by remember {
                mutableStateOf(getString(R.string.choose_size))
            }

            Column {

                Box(modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally),
                ) {
                    GeneratedImage()
                }
                
                Spacer(modifier = Modifier.height(30.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    TextField(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        value = userInput,
                        textStyle = TextStyle.Default.copy(
                            fontSize = 24.sp
                        ),
                        label = {
                            Text(
                                text = "Write something to find",
                                fontSize = 16.sp
                            )
                        },
                        onValueChange = {
                            userInput = it
                        }

                    )
                    
                    Spacer(modifier = Modifier.width(20.dp))

                    Button(
                        modifier = Modifier
                            .wrapContentSize(Alignment.Center),
                        onClick = {

                            if (userInput.isNotEmpty() && size != getString(R.string.choose_size)) {

                                sendRequest(
                                    userInput,
                                    size
                                )

                                Toast.makeText(applicationContext, "Searching $userInput...", Toast.LENGTH_SHORT).show()

                            } else if (userInput.isEmpty()){
                                Toast.makeText(applicationContext, "Please, write something )", Toast.LENGTH_SHORT).show()
                            } else if (size == getString(R.string.choose_size)) {
                                Toast.makeText(applicationContext, "Please, choose size )", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = Icons.Filled.Search,
                            contentDescription = "search button",
                            tint = Color.White
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ExposedDropdownMenuBox(
                        expanded = isExpanded,
                        onExpandedChange = { isExpanded = it }
                    ) {
                        TextField(
                            value = size,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "256x256"
                                    )
                                },
                                onClick = {
                                    size = "256x256"
                                    isExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "512x512"
                                    )
                                },
                                onClick = {
                                    size = "512x512"
                                    isExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "1024x1024"
                                    )
                                },
                                onClick = {
                                    size = "1024x1024"
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = img,
                        contentDescription = "generated image"
                    )
                }
            }
        }
    }

    private fun sendRequest(
        text: String,
        size: String
    ) {
        val apiInterface = ChatGptApi.getApi()

        val requestBody = Gson().toJson(
            ImageGenerateRequest(
                text,
                1,
                size
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        lifecycleScope.launch(Dispatchers.IO) {
            try {

                val response = apiInterface.generateImage(
                    "application/json",
                    "Bearer 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",
                    requestBody
                )

                val textResponse = response.data.first().url

                myLive.postValue(textResponse)

                Log.i(TAG, textResponse)

            } catch (ex: Exception) {
                Log.i(TAG, "error - ${ex.message}")
            }
        }
    }
}

@Composable
fun GeneratedImage() {
    Image(painter = painterResource(id = R.drawable.logo), contentDescription = "img")
}

