package com.example.alphabetlauncher.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.alphabetlauncher.data.AppInfo

/**
 * Single row representing one installed app.
 * Tapping launches the app via [onLaunchApp]; on failure shows a toast
 * rather than crashing the launcher.
 */
@Composable
fun AppItem(
    app: AppInfo,
    onLaunchApp: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Drawable -> ImageBitmap conversion is cheap enough to do here (icons
    // are small, ~48dp), but we still remember it keyed on the package name
    // so recomposition from unrelated state changes doesn't redo the
    // bitmap conversion.
    val imageBitmap = remember(app.packageName) {
        app.icon.toBitmap(width = 96, height = 96).asImageBitmap()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                val launched = onLaunchApp(app.packageName)
                if (!launched) {
                    Toast.makeText(
                        context,
                        "Couldn't open ${app.label}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = app.label,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}