package io.farzadmoradkani.auval.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.farzadmoradkani.auval.R
import io.farzadmoradkani.auval.util.extentions.separate3Digits

@Composable
fun SmallTitle(text: String){
    Text(
        text,
        fontSize = 12.sp
    )
}
@Composable
fun LargeTitle(text:String){
    Text(
        modifier = Modifier.padding(start = 20.dp),
        fontWeight = FontWeight.SemiBold,
        text = text,
        fontSize = 25.sp
    )
}



