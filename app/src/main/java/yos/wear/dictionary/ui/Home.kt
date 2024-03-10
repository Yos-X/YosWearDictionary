package yos.wear.dictionary.ui

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import yos.wear.dictionary.R


@ExperimentalWearMaterialApi
@Composable
fun Home(navController: NavController) {
    val context = LocalContext.current

    val listState = rememberScalingLazyListState(initialCenterItemIndex = 1)

    var query by remember { mutableStateOf("") }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (matches != null && matches.size > 0) {
                    query = matches[0]
                }
            }
        }

    Scaffold(
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = listState,
                modifier = Modifier
            )
        },
        timeText = {
            TimeText()
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
                modifier = Modifier.fillMaxSize()
            ) {
                item("标题") {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.title2
                    )
                }

                item("输入框") {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text(stringResource(id = R.string.tips_input_keywords)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = MaterialTheme.colors.secondary,
                            cursorColor = MaterialTheme.colors.primary
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                                    intent.putExtra(
                                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                    )
                                    launcher.launch(intent)
                                }
                            ) {
                                Icon(
                                    Icons.Default.KeyboardVoice,
                                    null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )
                }

                item("查询方式")
                {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                if (query.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        context.resources.getString(R.string.tips_not_allowed_empty),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                navController.navigate("translate/$query")
                            },
                            modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ButtonDefaults.LargeIconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (query.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        context.resources.getString(R.string.tips_not_allowed_empty),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                navController.navigate("dic/$query")
                            },
                            modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Abc,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ButtonDefaults.LargeIconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                        }
                    }
                }
            }
        }
    )
}
