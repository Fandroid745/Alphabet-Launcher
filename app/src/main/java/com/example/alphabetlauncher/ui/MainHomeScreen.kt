package com.example.alphabetlauncher.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alphabetlauncher.data.AppInfo
import com.example.alphabetlauncher.ui.components.AlphabetSideBar
import com.example.alphabetlauncher.ui.components.AppItem



@Composable
fun MainHomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedLetter by viewModel.selectedLetter.collectAsStateWithLifecycle()
    val appsByLetter by viewModel.appsByLetter.collectAsStateWithLifecycle()
    val favouriteApps by viewModel.favouriteApps.collectAsStateWithLifecycle()
    val currentTime by viewModel.currentTimeString.collectAsStateWithLifecycle()
    val currentDate by viewModel.currentDateString.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            // Main content area, leaving room on the right edge for the bar.
            Crossfade(
                targetState = selectedLetter,
                animationSpec = tween(durationMillis = 180),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 40.dp), // reserve space for AlphabetSideBar
                label = "home_content_crossfade"
            ) { letter ->
                if (letter == null) {
                    ClockAndFavouritesView(
                        currentTime = currentTime,
                        currentDate = currentDate,
                        favouriteApps = favouriteApps,
                        onLaunchApp = viewModel::launchApp
                    )
                } else {
                    FilteredAppListView(
                        letter = letter,
                        apps = appsByLetter[letter].orEmpty(),
                        onLaunchApp = viewModel::launchApp
                    )
                }
            }

            AlphabetSideBar(
                selectedLetter = selectedLetter,
                onLetterSelected = viewModel::onLetterTouched,
                onFingerReleased = viewModel::onFingerReleased,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(60.dp)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun ClockAndFavouritesView(
    currentTime: String,
    currentDate: String,
    favouriteApps: List<AppInfo>,
    onLaunchApp: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 64.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentTime,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Light ,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = currentDate,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (favouriteApps.isNotEmpty()) {
            Text(
                text = "FAVOURITES",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 48.dp, bottom = 12.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favouriteApps, key = { it.packageName }) { app ->
                    AppItem(app = app, onLaunchApp = onLaunchApp)
                }
            }
        }
    }
}

@Composable
private fun FilteredAppListView(
    letter: Char,
    apps: List<AppInfo>,
    onLaunchApp: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, top = 32.dp, bottom = 8.dp)
        )

        if (apps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "No apps",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(apps, key = { it.packageName }) { app ->
                    AppItem(app = app, onLaunchApp = onLaunchApp)
                }
            }
        }
    }
}