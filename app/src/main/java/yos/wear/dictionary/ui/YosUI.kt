package yos.wear.dictionary.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun StartPadding(content: @Composable () -> Unit) {
    Row(
        Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(10.dp)
                .padding(end = 5.dp)
                .padding(top = 3.5.dp, bottom = 2.dp)
                .background(MaterialTheme.colors.primary)
        )
        content()
    }
}

@Composable
fun ListTitle(text: String) = StartPadding {
    Text(
        text = text,
        style = MaterialTheme.typography.title2.copy(fontSize = 20.sp),
        fontWeight = FontWeight.Bold
    )
}