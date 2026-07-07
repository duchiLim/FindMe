package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.greenbee.findme.presentation.home.model.ProcessStateType
import com.greenbee.findme.presentation.ui.theme.FindMeOnPrimary

@Composable
fun AnimalKindTitleRow(
    kindName: String,
    processState: String,
    statusType: ProcessStateType,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    attachStatusToKindName: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = kindName,
            modifier = when {
                attachStatusToKindName -> Modifier.weight(1f, fill = false)
                else -> Modifier.weight(1f)
            },
            style = titleStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        ProcessStateChip(
            processState = processState,
            statusType = statusType,
        )
    }
}

@Composable
private fun ProcessStateChip(
    processState: String,
    statusType: ProcessStateType,
) {
    Surface(
        color = statusType.color,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = processState,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = FindMeOnPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
