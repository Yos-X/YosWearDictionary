package yos.wear.dictionary.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.curvedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import yos.wear.dictionary.R

@Composable
fun TranslateResult(query: String) {
    var translation by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    var loading by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (translation != "") {
            return@LaunchedEffect
        }
        loading = true
        val result = translate(query)
        if (result == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.tips_request_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@LaunchedEffect
        }
        translation = result
        loading = false
    }

    Column(Modifier.fillMaxSize()) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 5.dp,
                indicatorColor = MaterialTheme.colors.primary,
                trackColor = MaterialTheme.colors.primary.copy(alpha = 0.4f)
            )
        } else {
            val listState = rememberScalingLazyListState(initialCenterItemIndex = 1)
            Scaffold(
                positionIndicator = {
                    PositionIndicator(
                        scalingLazyListState = listState,
                        modifier = Modifier
                    )
                },
                timeText = {
                    val leadingTextStyle =
                        TimeTextDefaults.timeTextStyle(color = MaterialTheme.colors.primary)
                    val text = stringResource(id = R.string.nav_translate_result)

                    TimeText(
                        startLinearContent = {
                            Text(
                                text = text,
                                style = leadingTextStyle
                            )
                        },
                        startCurvedContent = {

                            curvedText(
                                text = text,
                                style = CurvedTextStyle(leadingTextStyle)
                            )
                        },
                    )
                },
                content = {
                    ScalingLazyColumn(
                        contentPadding = PaddingValues(
                            start = 22.dp,
                            end = 22.dp,
                            top = 25.dp,
                            bottom = 25.dp
                        ),
                        state = listState,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item("翻译源") {
                            Text(
                                text = query, fontSize = 12.sp, modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 2.dp)
                                    .alpha(0.8f),
                                style = MaterialTheme.typography.body2.copy(fontSize = 12.sp)
                            )
                        }
                        item("翻译") {
                            StartPadding {
                                Text(
                                    text = translation,
                                    style = MaterialTheme.typography.title2.copy(fontSize = 20.sp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        item("提示") {
                            Text(
                                text = stringResource(id = R.string.tips_translate_source),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(0.7f)
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}

suspend fun translate(query: String): String? = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val eng = if (query.matches(Regex("^\\p{ASCII}*$"))) "1" else "0"
    val requestBody = FormBody.Builder()
        .add("eng", eng)
        .add("validate", "")
        .add("ignore_trans", "0")
        .add("query", query)
        .build()
    val request = Request.Builder()
        .url("https://fanyi.so.com/index/search")
        .post(requestBody)
        .addHeader("pro", "fanyi")
        .addHeader("user-agent", "YosWearDic@Yos-X")
        .build()

    var result: String?
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            result = null
            return@use
        }

        val jsonData = response.body!!.string()
        val jsonObject = JSONObject(jsonData)
        result = jsonObject.getJSONObject("data").getString("fanyi")
    }

    result
}

