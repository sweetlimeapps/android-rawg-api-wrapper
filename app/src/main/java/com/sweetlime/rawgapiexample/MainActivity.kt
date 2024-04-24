package com.sweetlime.rawgapiexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sweetlime.rawgapi.network.ApiResponse
import com.sweetlime.rawgapiexample.ui.theme.RAWGAPIExampleTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = com.sweetlime.rawgapi.Service.create("YOUR_API_KEY")

        mainScope.launch {
            runCatching {
                when(val response = service.getListOfGames()){
                    is ApiResponse.Success -> Log.d("RAWG", response.data.toString())
                    is ApiResponse.ApiError -> TODO()
                    is ApiResponse.NetworkError -> TODO()
                    is ApiResponse.UnknownError -> TODO()
                }

            }.onFailure {
                Log.d("RAWG", it.toString())
            }
        }

        setContent {
            RAWGAPIExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RAWGAPIExampleTheme {
        Greeting("Android")
    }
}