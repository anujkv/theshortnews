package com.a3solution.theshortnews.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a3solution.theshortnews.ui.theme.TheShortNewsTheme

@Composable
fun AppIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Newspaper,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (showIcon) {
            AppIcon(size = 32.dp)
            Spacer(modifier = Modifier.width(12.dp))
        }
        
        Column {
            Text(
                text = "The Short",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraLight,
                lineHeight = 18.sp
            )
            Text(
                text = "News",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                lineHeight = 22.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppIconPreview() {
    TheShortNewsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AppIcon()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppLogoPreview() {
    TheShortNewsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AppLogo()
        }
    }
}
