package yos.wear.dictionary.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import yos.wear.dictionary.R
import java.net.URLEncoder


@Composable
fun YoudaoDicResult(query: String, navController: NavHostController) {
    var jsonString by rememberSaveable { mutableStateOf("") }
    val listState = rememberScalingLazyListState(initialCenterItemIndex = 1)
    val context = LocalContext.current
    var loading by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (jsonString != "") {
            return@LaunchedEffect
        }
        loading = true
        val result = getDic(query)
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
        jsonString = result
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
                    val text = stringResource(id = R.string.nav_dic_result)

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
                        item {
                            Text(
                                text = query,
                                style = MaterialTheme.typography.title2.copy(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 6.dp).fillMaxWidth()
                            )
                        }

                        try {
                            val jsonObject = JSONObject(jsonString)
                            val webTrans = jsonObject.getJSONObject("web_trans")
                            val webTranslation = webTrans.getJSONArray("web-translation")

                            if (webTranslation.length() > 0) {
                                item {
                                    ListTitle(text = "相关词汇")
                                }
                            }

                            for (i in 0 until webTranslation.length()) {
                                val item = webTranslation.getJSONObject(i)
                                val key = item.getString("key")
                                val trans = item.getJSONArray("trans").getJSONObject(0)
                                val value = trans.getString("value")

                                item {
                                    Column(
                                        Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate("dic/$value")
                                            }
                                            .padding(start = 15.dp)
                                            .padding(vertical = 3.dp)) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.title3.copy(fontSize = 15.sp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = value,
                                            modifier = Modifier.alpha(0.6f),
                                            style = MaterialTheme.typography.body2
                                        )
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        try {
                            val jsonObject = JSONObject(jsonString)
                            val blngSentsPart = jsonObject.getJSONObject("blng_sents_part")
                            val sentencePair = blngSentsPart.getJSONArray("sentence-pair")

                            if (sentencePair.length() > 0) {
                                item {
                                    ListTitle(text = "例句")
                                }
                            }

                            for (i in 0 until sentencePair.length()) {
                                val item = sentencePair.getJSONObject(i)
                                val sentence = item.getString("sentence")
                                val sentenceTranslation = item.getString("sentence-translation")

                                item {
                                    Column(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(start = 15.dp)
                                            .padding(vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = sentence,
                                            style = MaterialTheme.typography.title3.copy(fontSize = 15.sp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = sentenceTranslation,
                                            modifier = Modifier.alpha(0.6f),
                                            style = MaterialTheme.typography.body2
                                        )
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        /*try {
                            val jsonObject = JSONObject(jsonString)
                            val ce = jsonObject.getJSONObject("ce")
                            val word = ce.getJSONArray("word")

                            if (word.length() > 0) {
                                item {
                                    Text(text = "术语")
                                }
                            }

                            for (i in 0 until word.length()) {
                                val item = word.getJSONObject(i)
                                val returnPhrase = item.getJSONObject("return-phrase")
                                val iText = returnPhrase.getJSONArray("i")
                                var tran = ""
                                for (j in 0 until iText.length()) {
                                    val textItem = iText.getJSONObject(j)
                                    if (textItem.has("#text")) {
                                        tran += textItem.getString("#text") + " "
                                    }
                                }

                                item {
                                    Column {
                                        Text(
                                            text = iText.toString(),
                                            style = MaterialTheme.typography.title2
                                        )
                                        Text(text = tran, modifier = Modifier.alpha(0.8f), style = MaterialTheme.typography.body1)
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }*/

                        try {
                            val jsonObject = JSONObject(jsonString)
                            val baike = jsonObject.getJSONObject("baike")
                            val summarys = baike.getJSONArray("summarys")
                            val source = baike.getJSONObject("source")
                            val name = source.getString("name")

                            if (summarys.length() > 0) {
                                item {
                                    ListTitle(text = "百科")
                                }
                            }

                            for (i in 0 until summarys.length()) {
                                val item = summarys.getJSONObject(i)
                                val summary = item.getString("summary")

                                item {
                                    Column(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(start = 15.dp)
                                            .padding(vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = summary,
                                            style = MaterialTheme.typography.title3.copy(fontSize = 15.sp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = name,
                                            modifier = Modifier.alpha(0.6f),
                                            style = MaterialTheme.typography.body2
                                        )
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        item("提示") {
                            Text(
                                text = stringResource(id = R.string.tips_dic_source),
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(0.7f)
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                })
        }
    }

}

suspend fun getDic(query: String): String? = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(
            "https://dict.youdao.com/jsonapi?xmlVersion=5.1&client=&q=${
                URLEncoder.encode(
                    query,
                    "utf-8"
                )
            }&dicts=&keyfrom=&model=&mid=&imei=&vendor=&screen=&ssid=&network=5g&abtest=&jsonversion=2"
        )
        .get()
        .addHeader("user-agent", "YosWearDic@Yos-X")
        .build()

    var result: String?
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            result = null
            return@use
        }
        result = response.body!!.string()
    }

    result
}