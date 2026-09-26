package com.example.alphabetlauncher.ui.components

import android.view.MotionEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.roundToInt

private val ALPHABET = ('A'..'Z').toList()

/**
 * Custom A–Z vertical sidebar with interactive real-time touch drag tracking,
 * Gaussian curve bulge bending math, enlarged letter bubble, and spring physics overshoot.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AlphabetSideBar(
    selectedLetter: Char?,
    onLetterSelected: (letter: Char, touchYRatio: Float) -> Unit,
    onFingerReleased: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Spring animatable for the maximum horizontal curve bulge displacement (dp)
    val maxBulgeAnim = remember { Animatable(0f) }

    BoxWithConstraints(
        modifier = modifier
            .width(60.dp)
            .fillMaxHeight()
    ) {
        val totalHeightPx = with(density) { maxHeight.toPx() }

        // Number of items in sidebar: Star (1) + 26 Letters + Dot (1) = 28 items
        val itemHeightPx = totalHeightPx / (ALPHABET.size + 2)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            val touchY = motionEvent.y.coerceIn(0f, totalHeightPx)
                            val ratio = touchY / totalHeightPx

                            // Determine letter index corresponding to touch Y
                            val letterIndex = ((touchY / itemHeightPx) - 1)
                                .roundToInt()
                                .coerceIn(0, ALPHABET.size - 1)

                            val letter = ALPHABET[letterIndex]
                            onLetterSelected(letter, ratio)

                            // Animate bulge target to max displacement (~110dp)
                            coroutineScope.launch {
                                maxBulgeAnim.animateTo(
                                    targetValue = 110f,
                                    animationSpec = spring(
                                        stiffness = Spring.StiffnessHigh
                                    )
                                )
                            }
                            true
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            onFingerReleased()
                            // Spring overshoot and snap back to straight line
                            coroutineScope.launch {
                                maxBulgeAnim.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                            true
                        }

                        else -> false
                    }
                }
        ) {
            // Render Star + A-Z Column + Dot
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Star Icon
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                // 26 Alphabet Letters
                ALPHABET.forEachIndexed { index, letter ->
                    val letterCenterY = itemHeightPx * (index + 1.5f)
                    val currentTouchY = (selectedLetter?.let {
                        val selIdx = ALPHABET.indexOf(it)
                        if (selIdx != -1) itemHeightPx * (selIdx + 1.5f) else null
                    }) ?: 0f

                    // Gaussian curve displacement formula
                    val distance = kotlin.math.abs(letterCenterY - currentTouchY)
                    val sigma = itemHeightPx * 2.8f
                    val gaussianFactor = exp(-((distance * distance) / (2 * sigma * sigma)))
                    val horizontalShiftDp = -1 * (maxBulgeAnim.value * gaussianFactor)

                    val isSelected = (selectedLetter == letter)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .offset { IntOffset(x = with(density) { horizontalShiftDp.dp.roundToPx() }, y = 0) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.toString(),
                            fontSize = if (isSelected) 14.sp else 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            }
                        )
                    }
                }

                // Bottom Dot Icon
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    )
                }
            }

            // Floating Circular Letter Bubble (Left of finger)
            if (selectedLetter != null && maxBulgeAnim.value > 10f) {
                val currentTouchY = (ALPHABET.indexOf(selectedLetter).takeIf { it != -1 } ?: 0)
                val bubbleCenterY = itemHeightPx * (currentTouchY + 1.5f)

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = with(density) { (-130.dp).roundToPx() },
                                y = (bubbleCenterY - with(density) { 30.dp.roundToPx() }).roundToInt()
                            )
                        }
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedLetter.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
