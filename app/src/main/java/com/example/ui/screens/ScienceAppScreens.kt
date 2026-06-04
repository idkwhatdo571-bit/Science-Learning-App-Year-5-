package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ModuleProgress
import com.example.ui.*
import com.example.ui.theme.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigationWrapper(viewModel: ScienceViewModel) {
    val progressList by viewModel.allProgress.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Subtle ambient moving background stars/particles (animated pulse gradient)
            val infiniteTransition = rememberInfiniteTransition(label = "ambient")
            val glowScale by infiniteTransition.animateFloat(
                initialValue = 0.85f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(DarkPurple, DarkBackground),
                            center = Offset(200f * glowScale, 300f * glowScale),
                            radius = 800f
                        )
                    )
            )

            AnimatedContent(
                targetState = viewModel.currentScreen,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "screens"
            ) { targetScreen ->
                when (targetScreen) {
                    Screen.HOME -> HomeScreen(viewModel, progressList)
                    Screen.ELECTRICITY -> ElectricityScreen(viewModel)
                    Screen.SPACE -> SpaceExplorerScreen(viewModel)
                    Screen.SURVIVAL -> SurvivalScreen(viewModel)
                    Screen.SKELETAL -> SkeletalScreen(viewModel)
                    Screen.QUIZ -> QuizScreen(viewModel)
                    Screen.LEADERBOARD -> LeaderboardScreen(viewModel)
                    Screen.BADGES -> BadgesScreen(viewModel)
                    Screen.SHOP -> ShopScreen(viewModel)
                    Screen.ADMIN -> AdminScreen(viewModel)
                    Screen.PRO_UPGRADE -> ProUpgradeScreen(viewModel)
                }
            }

            // Global Achievement Celebration Overlay
            viewModel.newlyUnlockedBadge?.let { earnedBadge ->
                BadgeCelebrationDialog(
                    badge = earnedBadge,
                    onDismiss = { viewModel.dismissCelebration() }
                )
            }

            // Global Virtual Medal Celebration Overlay
            if (viewModel.showMedalAwarded && viewModel.latestMedalEarned.isNotEmpty()) {
                val earnedMedal = virtualMedalsList.find { it.id == viewModel.latestMedalEarned }
                earnedMedal?.let { medal ->
                    MedalCelebrationDialog(
                        medal = medal,
                        onDismiss = { viewModel.dismissMedalAwarded() }
                    )
                }
            }
        }

        if (viewModel.currentScreen == Screen.HOME || 
            viewModel.currentScreen == Screen.LEADERBOARD || 
            viewModel.currentScreen == Screen.BADGES ||
            viewModel.currentScreen == Screen.SHOP ||
            viewModel.currentScreen == Screen.PRO_UPGRADE) {
            ScienceBottomNavigation(
                currentScreen = viewModel.currentScreen,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    }
}

// ============================================================================
// SCREEN: HOME / WELCOME DASHBOARD
// ============================================================================
@Composable
fun HomeScreen(viewModel: ScienceViewModel, progressList: List<ModuleProgress>) {
    val totalModules = 4
    val completedCount = progressList.count { it.completed }
    val starsEarned = progressList.count { it.earnedStarBadge }
    val progressPercentage = if (totalModules > 0) completedCount.toFloat() / totalModules else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. App Header Title block (Vibrant Palette style)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // S5 Gradient Icon with subtle glow
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(CosmicPurple, NeonBlue),
                                    start = Offset.Zero,
                                    end = Offset.Infinite
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "S5",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Column {
                        Text(
                            text = "Science Year 5",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            lineHeight = 24.sp
                        )
                        Text(
                            text = "KSSR SEMAKAN",
                            color = NeonBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Header active stars badge & Admin settings icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.ADMIN) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(CardSurface, CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            .testTag("btn_admin_gear")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Admin Panel",
                            tint = NeonBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardSurface)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(LimeGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "★",
                                    color = DarkBackground,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = if (starsEarned > 0) "${starsEarned * 300 + 100}" else "100",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Announcement Banner managed dry-run via Admin Control Panel
        if (viewModel.announcementActive && viewModel.announcementText.isNotEmpty() && !viewModel.userDismissedAnnouncement) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .border(
                            BorderStroke(2.dp, Brush.linearGradient(listOf(CosmicPurple, NeonBlue))),
                            RoundedCornerShape(20.dp)
                        )
                        .testTag("announcement_banner"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📢", fontSize = 20.sp)
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SYSTEM ANNOUNCEMENT",
                                    color = NeonBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )

                                IconButton(
                                    onClick = { 
                                        viewModel.dismissAnnouncement()
                                        SoundPlayer.playCorrect()
                                    },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("btn_dismiss_announcement")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss Announcement",
                                        tint = MutedSlate,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = viewModel.announcementText,
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 2. Beautiful Progress Tracker Dashboard Card (Vibrant Palette style with bottom border and linear bar)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardSurface)
                    .drawBehind {
                        val strokeWidth = 5.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = CosmicPurple,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .padding(top = 20.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "COURSE PROGRESS",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (progressPercentage == 1f) "Splendid Success!" else if (progressPercentage > 0.5f) "Excellent Work!" else "Keep Exploring!",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Text(
                            text = "${(progressPercentage * 100).toInt()}%",
                            color = LimeGreen,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Streamlined linear progress bar following the CSS design
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(DarkBackground)
                    ) {
                        if (progressPercentage > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progressPercentage)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(CosmicPurple, NeonBlue)
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Horizontal tags list inside dashboard
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (starsEarned > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LimeGreen)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "3D BADGE EARNED",
                                    color = DarkBackground,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "UNIT 4 UNLOCKED",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (completedCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "PASSED: $completedCount",
                                    color = NeonBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // KSSR Digital Achievements summary card
        item {
            val leaderboardList by viewModel.leaderboardList.collectAsStateWithLifecycle()
            val unlockedBadges = getUnlockedBadgeIds(progressList, leaderboardList, viewModel.profileRegisteredName)
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = BorderStroke(1.dp, NeonBlue.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(Screen.BADGES) }
                    .testTag("home_achievements_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🏅 KSSR DIGITAL HONOURS",
                            color = NeonBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${unlockedBadges.size} of ${kssrBadges.size} Badges Earned",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to view your medals, locked requirements & trivia!",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(CosmicPurple.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, CosmicPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎖️", fontSize = 24.sp)
                    }
                }
            }
        }

        // 3. 3D Stars Badge Showcase Collection
        if (starsEarned > 0) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "⭐ YOUR EARNED 3D BADGES!",
                        color = SolarYellow,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        progressList.filter { it.earnedStarBadge }.forEach { p ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(CardSurface, RoundedCornerShape(16.dp))
                                    .border(1.5.dp, LimeGreen, RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    // Bouncing glowing Star badge simulation
                                    val infiniteStarVal = rememberInfiniteTransition("badgeBounce")
                                    val badgeScale by infiniteStarVal.animateFloat(
                                        initialValue = 1f,
                                        targetValue = 1.15f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(2000, easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "bounce"
                                    )

                                    Text(
                                        text = "⭐",
                                        fontSize = 32.sp,
                                        modifier = Modifier.scale(badgeScale)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = p.moduleName,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "KSSR Winner",
                                        color = LimeGreen,
                                        fontSize = 9.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Science Pro Upgrade Banner
        item {
            val isPro = viewModel.isProActive
            if (isPro) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF13102C)),
                    border = BorderStroke(1.5.dp, CosmicPurple.copy(alpha = 0.8f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pro_active_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(CosmicPurple.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, CosmicPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🛡️", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SCIENCE PRO MEMBER",
                                color = CosmicPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (viewModel.isTrialActive) "7-Day Free Trial Activated!" else "Premium Plan Active!",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Enjoy unlimited lessons, no advertisements, and 100% core access.",
                                color = MutedSlate,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF13102C)),
                    border = BorderStroke(1.5.dp, CosmicPurple.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.PRO_UPGRADE) }
                        .testTag("home_upgrade_callout")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(CosmicPurple.copy(alpha = 0.15f), CircleShape)
                                .border(1.2.dp, CosmicPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "UPGRADE TO SCIENCE PRO",
                                    color = CosmicPurple,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(LimeGreen, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("7-DAY TRIAL", color = DarkBackground, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Unlock the universe of Year 5 Science",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Get unlimited access, zero ads, priority simulations, and download revision sets.",
                                color = MutedSlate,
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Navigate to upgrade",
                            tint = CosmicPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // 4. List of 3 Gamified Learning Units + Mega Quiz
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "SELECT AN ADVENTURE MODULE",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )

                // Layout our 4 modules beautifully
                val electricityProg = progressList.find { it.moduleId == "electricity" }
                val spaceProg = progressList.find { it.moduleId == "space" }
                val survivalProg = progressList.find { it.moduleId == "survival" }
                val skeletalProg = progressList.find { it.moduleId == "skeletal" }
                val quizProg = progressList.find { it.moduleId == "quiz" }

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // MODULE 1 CARD
                    ModuleLaunchCard(
                        moduleId = "electricity",
                        title = "MODULE 1: The Power Station",
                        subtitle = "Build interactive circuits & light up bulbs!",
                        topic = "Electricity Unit",
                        emoji = "⚡🔌💡",
                        progress = electricityProg,
                        tintColor = NeonBlue,
                        testTag = "btn_module_electricity",
                        onClick = { viewModel.navigateTo(Screen.ELECTRICITY) }
                    )

                    // MODULE 2 CARD
                    ModuleLaunchCard(
                        moduleId = "space",
                        title = "MODULE 2: Space Explorer",
                        subtitle = "Reorder solar system planets from Sun!",
                        topic = "Solar System Unit",
                        emoji = "🪐🌍☄️",
                        progress = spaceProg,
                        tintColor = CosmicPurple,
                        testTag = "btn_module_space",
                        onClick = { viewModel.navigateTo(Screen.SPACE) }
                    )

                    // MODULE 3 CARD
                    ModuleLaunchCard(
                        moduleId = "survival",
                        title = "MODULE 3: Survival Quest",
                        subtitle = "Learn amazing animal adaptation abilities!",
                        topic = "Animal & Plant Adaptations",
                        emoji = "🐻🐪🦔",
                        progress = survivalProg,
                        tintColor = LimeGreen,
                        testTag = "btn_module_survival",
                        onClick = { viewModel.navigateTo(Screen.SURVIVAL) }
                    )

                    // MODULE 4 CARD
                    ModuleLaunchCard(
                        moduleId = "skeletal",
                        title = "MODULE 4: Human Skeletal Lab",
                        subtitle = "Identify major bone structures & match their vital KSSR functions!",
                        topic = "Human Skeletal System",
                        emoji = "💀🫁🦴",
                        progress = skeletalProg,
                        tintColor = EmberOrange,
                        testTag = "btn_module_skeletal",
                        onClick = { viewModel.navigateTo(Screen.SKELETAL) }
                    )

                    // MEGA QUIZ CARD
                    ModuleLaunchCard(
                        moduleId = "quiz",
                        title = "MEGA QUIZ: Curriculum Challenge",
                        subtitle = "Answer 5 KSSR questions to win Master Star!",
                        topic = "Full Year 5 Science Syllabus",
                        emoji = "🏆📚🎯",
                        progress = quizProg,
                        tintColor = SolarYellow,
                        testTag = "btn_module_quiz",
                        onClick = { viewModel.navigateTo(Screen.QUIZ) }
                    )
                }
            }
        }

        // 5. Reset progress button
        item {
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = { viewModel.resetAllProgressDB() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FF5722)),
                border = BorderStroke(1.5.dp, EmberOrange),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("reset_progress_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Achievements",
                    tint = EmberOrange,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset All Lab Achievements",
                    color = ErrorStateText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

val ErrorStateText = Color(0xFFFF8B66)

@Composable
fun ModuleLaunchCard(
    moduleId: String,
    title: String,
    subtitle: String,
    topic: String,
    emoji: String,
    progress: ModuleProgress?,
    tintColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    val isCompleted = progress?.completed == true

    when (moduleId) {
        "electricity" -> {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NeonBlue, Color(0xFF00A3B5))
                        )
                    )
                    .clickable { onClick() }
                    .testTag(testTag)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = 30.dp, y = 10.dp)
                            .background(LimeGreen.copy(alpha = 0.25f), CircleShape)
                    )

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "MODULE 1",
                                    color = DarkBackground,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (isCompleted) {
                                Text(
                                    text = "⭐ Passed",
                                    color = DarkBackground,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = title.substringAfter("MODULE 1: "),
                            color = DarkBackground,
                            fontWeight = FontWeight.Black,
                            fontSize = 21.sp
                        )

                        Text(
                            text = subtitle,
                            color = DarkBackground.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkBackground)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "GO!",
                                    color = LimeGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(LimeGreen, CircleShape)
                                )
                                Text(
                                    text = "SWITCH: ON",
                                    color = DarkBackground,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        "space" -> {
            val borderClr = Color(0xFF5000CC)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .drawBehind {
                        val strokeWidth = 5.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = borderClr,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
                    .clickable { onClick() }
                    .testTag(testTag)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "MODULE 2",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (isCompleted) {
                            Text(
                                text = "⭐ Passed",
                                color = LimeGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = title.substringAfter("MODULE 2: "),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(14.dp).background(Color(0xFFFFA726), CircleShape))
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFFEF5350), CircleShape))
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFF42A5F5), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Text(
                            text = "PLAY",
                            color = DarkBackground,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        "survival" -> {
            val borderClr = Color(0xFF8CAF00)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LimeGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .drawBehind {
                        val strokeWidth = 5.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = borderClr,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
                    .clickable { onClick() }
                    .testTag(testTag)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "MODULE 3",
                                color = DarkBackground,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (isCompleted) {
                            Text(
                                text = "⭐ Explorer Passed",
                                color = DarkBackground,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = title.substringAfter("MODULE 3: "),
                        color = DarkBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(DarkBackground)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(if (isCompleted) 1f else 0.3f)
                                .background(NeonBlue)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = subtitle,
                        color = DarkBackground.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Text(
                            text = "EXPLORE",
                            color = LimeGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        "skeletal" -> {
            val borderClr = Color(0xFFD84315)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4D1C0E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .drawBehind {
                        val strokeWidth = 5.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = borderClr,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
                    .clickable { onClick() }
                    .testTag(testTag)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "MODULE 4",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (isCompleted) {
                            Text(
                                text = "⭐ Expert Passed",
                                color = LimeGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = title.substringAfter("MODULE 4: "),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💀", fontSize = 14.sp)
                        Text(text = "🫁", fontSize = 14.sp)
                        Text(text = "🦴", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmberOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Text(
                            text = "START LAB ARCHEOLOGY",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        else -> {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, if (isCompleted) LimeGreen else SolarYellow.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                    .clickable { onClick() }
                    .testTag(testTag)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(SolarYellow.copy(alpha = 0.15f), CircleShape)
                            .border(2.dp, SolarYellow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji.take(2), fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = topic.uppercase(),
                            color = SolarYellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                        Text(
                            text = subtitle,
                            color = MutedSlate,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isCompleted) {
                            Text(text = "⭐ Passed", color = LimeGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Quiz",
                                tint = SolarYellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// ============================================================================
// MODULE 1: THE POWER STATION (Circuit builder)
// ============================================================================
@Composable
fun ElectricityScreen(viewModel: ScienceViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App header inside screen
        GameScreenHeader(
            title = "THE POWER STATION",
            subtitle = "KSSR Year 5 • Electricity Circuits",
            colorAccent = NeonBlue,
            onBack = { viewModel.navigateTo(Screen.HOME) }
        )

        // Game Instructions and explanation
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, NeonBlue.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚙️ INSTRUCTIONS:",
                    color = NeonBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Tap components below to place and wire them. Then click the POWER SWITCH to close the circuit! Can you turn on the glowing lightbulb?",
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Live Custom Circuit Canvas
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .border(2.dp, if (viewModel.isLightGlowing) LimeGreen else CosmicPurple.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Background Custom Circuit Path Canvas
                val closedCircuitColor = LimeGreen
                val openCircuitColor = MutedSlate
                val wireColor = if (viewModel.isLightGlowing) closedCircuitColor else openCircuitColor

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val w = size.width
                    val h = size.height

                    // Drawing rectangle wiring path with path effect dots
                    val pathEffect = if (viewModel.isLightGlowing) {
                        PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    } else {
                        PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    }

                    // Loop drawing
                    drawRect(
                        color = wireColor,
                        topLeft = Offset(w * 0.15f, h * 0.15f),
                        size = androidx.compose.ui.geometry.Size(w * 0.7f, h * 0.7f),
                        style = Stroke(
                            width = 6f,
                            pathEffect = pathEffect,
                            cap = StrokeCap.Round
                        )
                    )
                }

                // Interactive Overlaid positions represented inside circuit loop
                // 1. BULB position (Top Middle)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                ) {
                    CircuitComponentAnchor(
                        emoji = if (viewModel.isLightGlowing) "💡🌟" else "💡",
                        label = "Lightbulb",
                        hasPlaced = viewModel.hasBulb,
                        isGlowing = viewModel.isLightGlowing,
                        onClick = { viewModel.toggleComponent("bulb") },
                        testTag = "btn_place_bulb"
                    )
                }

                // 2. BATTERY position (Bottom Middle)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                ) {
                    CircuitComponentAnchor(
                        emoji = "🔋",
                        label = "Battery",
                        hasPlaced = viewModel.hasBattery,
                        isGlowing = viewModel.isLightGlowing,
                        onClick = { viewModel.toggleComponent("battery") },
                        testTag = "btn_place_battery"
                    )
                }

                // 3. WIRES position (Left Middle)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                ) {
                    CircuitComponentAnchor(
                        emoji = "🔌",
                        label = "Wires",
                        hasPlaced = viewModel.hasWires,
                        isGlowing = viewModel.isLightGlowing,
                        onClick = { viewModel.toggleComponent("wires") },
                        testTag = "btn_place_wires"
                    )
                }

                // 4. SWITCH position (Right Middle)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                ) {
                    CircuitComponentAnchor(
                        emoji = if (viewModel.switchOn) "🛡️" else "🔘",
                        label = "Switch: " + (if (viewModel.switchOn) "ON" else "OFF"),
                        hasPlaced = viewModel.hasSwitch,
                        isGlowing = viewModel.isLightGlowing,
                        onClick = { viewModel.toggleComponent("switch") },
                        testTag = "btn_place_switch"
                    )
                }

                // Center explanation avatar / warning
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .widthIn(max = 160.dp)
                        .background(Color(0xE0060314), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (viewModel.isLightGlowing) {
                            Text(text = "⚡ ELECTRICAL FLUX ⚡", color = LimeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text(text = "Current Flows!", color = Color.White, fontSize = 11.sp, textAlign = TextAlign.Center)
                        } else {
                            Text(text = "⚠️ CIRCUIT BLOCKED", color = ErrorStateText, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text(text = "Unfinished Loop", color = MutedSlate, fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        // Action Status bar and Toggles
        AnimatedVisibility(
            visible = viewModel.isLightGlowing,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LimeGreen.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, LimeGreen, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🌟 EXCELLENT LAB SUCCESS! 🌟",
                        color = LimeGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "All components are connected, switch is toggled ON, and the circuit is CLOSED. Electric energy converts to light! KSSR Year 5 Milestone complete.",
                        color = Color.White,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Interactive actual Switch clicker
            Button(
                onClick = { viewModel.toggleSwitch() },
                enabled = viewModel.isCircuitComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.switchOn) LimeGreen else CosmicPurple,
                    disabledContainerColor = Color(0xFF261D5C)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1.5f)
                    .height(52.dp)
                    .testTag("toggle_circuit_switch")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Switch status",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (!viewModel.isCircuitComplete) "Connect circuit first!" else if (viewModel.switchOn) "SWITCH: TURN OFF" else "SWITCH: TURN ON",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            // RESET button
            Button(
                onClick = { viewModel.resetElectricityGame() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FF5722)),
                border = BorderStroke(1.5.dp, EmberOrange),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("btn_reset_electricity")
            ) {
                Text(text = "Reset", color = ErrorStateText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CircuitComponentAnchor(
    emoji: String,
    label: String,
    hasPlaced: Boolean,
    isGlowing: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val scaleFactor = run {
        if (isGlowing && label.contains("Bulb")) {
            val infinitePulse = rememberInfiniteTransition("bulbPulse")
            val pScale by infinitePulse.animateFloat(
                initialValue = 1f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )
            pScale
        } else {
            1f
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scaleFactor)
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    if (hasPlaced) {
                        if (isGlowing) LimeGreen.copy(alpha = 0.25f) else NeonBlue.copy(alpha = 0.2f)
                    } else {
                        Color(0xFF140E2C)
                    },
                    CircleShape
                )
                .border(
                    2.dp,
                    if (hasPlaced) {
                        if (isGlowing) LimeGreen else NeonBlue
                    } else {
                        MutedSlate.copy(alpha = 0.4f)
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (hasPlaced) {
                Text(text = emoji, fontSize = 34.sp)
            } else {
                Text(text = "+", color = MutedSlate, fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (hasPlaced) Color.White else MutedSlate,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


// ============================================================================
// MODULE 2: SPACE EXPLORER (Planet sorting game)
// ============================================================================
@Composable
fun SpaceExplorerScreen(viewModel: ScienceViewModel) {
    // Make sure we shuffle if starting empty
    LaunchedEffect(Unit) {
        if (viewModel.currentPlanetsList.isEmpty()) {
            viewModel.resetSpaceGame()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App header inside screen
        GameScreenHeader(
            title = "SPACE EXPLORER",
            subtitle = "KSSR Year 5 • Solar System Order",
            colorAccent = CosmicPurple,
            onBack = { viewModel.navigateTo(Screen.HOME) }
        )

        // Instructions card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, CosmicPurple.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🌌 MISSION DIRECTIVE:",
                    color = CosmicPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Arrange the space objects below in correct sequential order from the Sun (closest to farthest). Tap a planet, then choose another to swap positions!",
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Planets Ordering Visual Slot Board
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .border(2.dp, if (viewModel.spaceGameSuccess) LimeGreen else CosmicPurple.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(viewModel.currentPlanetsList.size) { index ->
                    val planet = viewModel.currentPlanetsList[index]
                    val isSelected = viewModel.selectedPlanetIndex == index

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) CosmicPurple.copy(alpha = 0.35f) else Color(0xFF191244),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                2.dp,
                                if (isSelected) NeonBlue else if (planet.index == index && viewModel.spaceGameSuccess) LimeGreen else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectPlanetToSwap(index) }
                            .padding(12.dp)
                            .testTag("planet_slot_$index"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Slot Label index tag
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFF0C0728), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = if (planet.index == index && viewModel.spaceGameSuccess) LimeGreen else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Render beautiful colored circle avatar of the planet
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(planet.color, CircleShape)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (planet.id) {
                                    "sun" -> "☀️"
                                    "earth" -> "🌍"
                                    "mars" -> "🔴"
                                    "jupiter" -> "🪐"
                                    "saturn" -> "🪐"
                                    else -> "☄️"
                                },
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = planet.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                if (viewModel.spaceGameSuccess && planet.index == index) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "✔ Correct",
                                        color = LimeGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = planet.info,
                                color = MutedSlate,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }

                        if (isSelected) {
                            Text(text = "📌 SWAP", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Success dialog panel
        AnimatedVisibility(
            visible = viewModel.spaceGameSuccess,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LimeGreen.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, LimeGreen, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 EXPLORER MISSION EXCELLENT! 🎉",
                        color = LimeGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Perfect configuration! Order is correct: Sun, Earth (3rd from Sun), Mars (4th), Jupiter (5th), Saturn (6th). You earned a Star Badge! 🌟🚀",
                        color = Color.White,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // SHUFFLE / RESET button
            Button(
                onClick = { viewModel.resetSpaceGame() },
                colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1.5f)
                    .height(52.dp)
                    .testTag("btn_reset_space")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Shuffle planets",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "SHUFFLE & RETRY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Button(
                onClick = { viewModel.navigateTo(Screen.HOME) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF261D5C)),
                border = BorderStroke(1.dp, NeonBlue.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(text = "Go Back", color = Color.White)
            }
        }
    }
}


// ============================================================================
// MODULE 3: SURVIVAL QUEST (Adaptations flashcard game)
// ============================================================================
@Composable
fun SurvivalScreen(viewModel: ScienceViewModel) {
    val totalAnimals = viewModel.standardAnimals.size
    val readCount = viewModel.exploredAnimals.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App header inside screen
        GameScreenHeader(
            title = "SURVIVAL QUEST",
            subtitle = "KSSR Year 5 • Animal Special Adaptations",
            colorAccent = LimeGreen,
            onBack = { viewModel.navigateTo(Screen.HOME) }
        )

        // Instructions and count of cards read
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, LimeGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🐾 KSSR WILDLIFE OBSERVER:",
                        color = LimeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Tap on all 5 animals below to explore their unique morphological adaptation tricks. Explore all animal files to complete the unit!",
                        color = Color.White,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Score block
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFF0F0C20), CircleShape)
                        .border(1.5.dp, if (readCount == totalAnimals) LimeGreen else NeonBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$readCount/$totalAnimals", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Files", color = MutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Animal Cards Grid Component
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.standardAnimals) { animal ->
                val hasRead = viewModel.exploredAnimals.contains(animal.id)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clickable { viewModel.exploreAnimal(animal) }
                        .border(
                            2.dp,
                            if (hasRead) LimeGreen else CosmicPurple.copy(alpha = 0.4f),
                            RoundedCornerShape(16.dp)
                        )
                        .testTag("animal_card_${animal.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = animal.imageUrl, fontSize = 34.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = animal.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (hasRead) LimeGreen else NeonBlue, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasRead) "Read 📖" else "Unread 🎯",
                                color = if (hasRead) LimeGreen else NeonBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Completion indicator
        AnimatedVisibility(
            visible = readCount == totalAnimals,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LimeGreen.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, LimeGreen, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆 ANIMAL FILES EXPLORED SUCCESSFULLY! 🏆",
                        color = LimeGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "You have discovered how all 5 wildlife species employ structural and behavioral adaptations under KSSR Year 5 Science syllabus! Badge earned! 🌟🏕️",
                        color = Color.White,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Action Row with Reset button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.resetSurvivalGame() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FF5722)),
                border = BorderStroke(1.5.dp, EmberOrange),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("btn_reset_survival")
            ) {
                Text(text = "Reset Progress", color = ErrorStateText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.navigateTo(Screen.HOME) },
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(text = "FINISH EXPLODING", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }

    // Modal popup card details
    val activePopup = viewModel.activeAnimalPopup
    if (activePopup != null) {
        Dialog(onDismissRequest = { viewModel.closeAnimalPopup() }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, LimeGreen, RoundedCornerShape(24.dp))
                    .testTag("animal_pupup"),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(LimeGreen.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = activePopup.imageUrl, fontSize = 40.sp)
                    }

                    Text(
                        text = activePopup.name.uppercase(),
                        fontWeight = FontWeight.Black,
                        color = LimeGreen,
                        fontSize = 22.sp
                    )

                    HorizontalDivider(color = MutedSlate.copy(alpha = 0.3f), thickness = 1.dp)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row {
                            Text(text = "🧬 Adaptation Model: ", color = NeonBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = activePopup.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }

                        Row {
                            Text(text = "⚙️ Mode / Trait: ", color = CosmicPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = activePopup.trait, color = Color.White, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "📖 KSSR SYLLABUS SCIENCE FACT:",
                            color = SolarYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Text(
                            text = activePopup.kssrFact,
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.closeAnimalPopup() },
                        colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_close_pupup")
                    ) {
                        Text(text = "Understood! 👍", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ============================================================================
// COMPONENT: MEGA CURRICULUM QUIZ
// ============================================================================
@Composable
fun QuizScreen(viewModel: ScienceViewModel) {
    val totalQs = viewModel.quizQuestions.size
    val activeInd = viewModel.currentQuestionIndex
    val activeQ = viewModel.quizQuestions[activeInd]

    // Track eliminated options using local state that resets per question index
    var eliminatedOptions by remember(activeInd) { mutableStateOf(emptySet<Int>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App header inside screen
        GameScreenHeader(
            title = "MEGA KSSR QUIZ",
            subtitle = "Syllabus Assessment • Win Master Badge",
            colorAccent = SolarYellow,
            onBack = { viewModel.navigateTo(Screen.HOME) }
        )

        if (!viewModel.isQuizCompleted) {
            // Step meter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUESTION ${activeInd + 1} OF $totalQs",
                    color = SolarYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
                Text(
                    text = "Score: ${viewModel.quizScore}/$totalQs",
                    color = LimeGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Progress Bar index indicator
            LinearProgressIndicator(
                progress = { (activeInd + 1).toFloat() / totalQs },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = SolarYellow,
                trackColor = Color(0xFF1E1552)
            )

            // Current Question display
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, CosmicPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = activeQ.question,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(18.dp)
                )
            }

            // Power-up Resources Use Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Use Hint
                val canUseHint = viewModel.hintsOwned > 0 && !viewModel.isAnswerSubmitted && eliminatedOptions.isEmpty()
                Button(
                    onClick = {
                        if (canUseHint) {
                            val correct = activeQ.correctIndex
                            val wrongIndices = activeQ.options.indices.filter { it != correct }
                            if (wrongIndices.isNotEmpty() && viewModel.useHint()) {
                                eliminatedOptions = wrongIndices.shuffled().take(2).toSet()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canUseHint) CosmicPurple else Color.White.copy(alpha = 0.04f),
                        disabledContainerColor = Color.White.copy(alpha = 0.04f)
                    ),
                    enabled = canUseHint,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("quiz_btn_use_hint")
                ) {
                    Text(
                        text = "💡 Take Hint (${viewModel.hintsOwned})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canUseHint) Color.White else Color.White.copy(alpha = 0.3f)
                    )
                }

                // Use Boost
                val canUseBoost = viewModel.boostsOwned > 0 && !viewModel.isAnswerSubmitted
                Button(
                    onClick = {
                        if (canUseBoost && viewModel.useBoost()) {
                            viewModel.submitAnswer(activeQ.correctIndex)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canUseBoost) SolarYellow else Color.White.copy(alpha = 0.04f),
                        disabledContainerColor = Color.White.copy(alpha = 0.04f)
                    ),
                    enabled = canUseBoost,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("quiz_btn_use_boost")
                ) {
                    Text(
                        text = "⚡ Auto Boost (${viewModel.boostsOwned})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canUseBoost) Color.Black else Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            // Options Selection
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                activeQ.options.forEachIndexed { optInd, optionText ->
                    val isSelected = viewModel.selectedAnswerIndex == optInd
                    val isEliminated = eliminatedOptions.contains(optInd)
                    val isSubmitted = viewModel.isAnswerSubmitted
                    val isCorrectOpt = optInd == activeQ.correctIndex

                    val cardBorderColor = when {
                        isEliminated -> Color.White.copy(alpha = 0.05f)
                        isSubmitted && isCorrectOpt -> LimeGreen
                        isSubmitted && isSelected -> EmberOrange
                        isSelected -> SolarYellow
                        else -> Color.Transparent
                    }

                    val cardBgColor = when {
                        isEliminated -> Color(0xFF110C2E).copy(alpha = 0.5f)
                        isSubmitted && isCorrectOpt -> LimeGreen.copy(alpha = 0.15f)
                        isSubmitted && isSelected -> EmberOrange.copy(alpha = 0.15f)
                        isSelected -> SolarYellow.copy(alpha = 0.12f)
                        else -> Color(0xFF191244)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isEliminated && !isSubmitted) { viewModel.submitAnswer(optInd) }
                            .border(2.dp, cardBorderColor, RoundedCornerShape(14.dp))
                            .testTag("quiz_option_$optInd"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = optionText,
                                color = if (isEliminated) Color.White.copy(alpha = 0.25f) else Color.White,
                                fontWeight = if (isSelected || (isSubmitted && isCorrectOpt)) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )

                            if (isSubmitted) {
                                if (isCorrectOpt) {
                                    Text(text = "✔ Correct", color = LimeGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                } else if (isSelected) {
                                    Text(text = "❌ Wrong", color = EmberOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            } else if (isEliminated) {
                                Text(text = "🚫 Eliminated", color = Color.White.copy(alpha = 0.25f), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Explanation & Next Button
            AnimatedVisibility(
                visible = viewModel.isAnswerSubmitted,
                enter = fadeIn() + expandVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x3300FAFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeonBlue, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "🧠 TEACHER EXPLANATION:", color = NeonBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(
                                text = activeQ.explanation,
                                color = Color.White,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.nextQuestion() },
                        colors = ButtonDefaults.buttonColors(containerColor = SolarYellow),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("quiz_next_button")
                    ) {
                        Text(
                            text = if (activeInd == totalQs - 1) "SEE FINAL RESULTS 🏅" else "NEXT QUESTION ➡️",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

        } else {
            // Quiz completed view slide with 3D star awards logic if perfect score
            val isPerfect = viewModel.quizScore == totalQs

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(2.dp, if (isPerfect) SolarYellow else CosmicPurple, RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Star rotating logic
                    val infiniteTransition = rememberInfiniteTransition("badgeRotation")
                    val starRotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(8000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "rotate"
                    )

                    if (isPerfect) {
                        Text(
                            text = "⭐🌟⭐",
                            fontSize = 44.sp,
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .rotate(starRotation)
                        )
                        Text(
                            text = "3D MASTER STAR AWARDED!",
                            color = SolarYellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Outstanding Achievement! You scored a perfect 5/5 on the Year 5 Science Final. You have officially mastered KSSR Electric Systems, Solar order, and Animal Defense Adaptations!",
                            color = Color.White,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    } else {
                        Text(text = "🎖️👍", fontSize = 48.sp, modifier = Modifier.padding(bottom = 12.dp))
                        Text(
                            text = "GREAT ATTEMPT!",
                            color = NeonBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "You scored ${viewModel.quizScore} out of 5 correct! Retake the assessment to earn a perfect score and unlock your shimmering 3D Star Badge trophy!",
                            color = Color.White,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.resetQuizGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(text = "RETAKE QUIZ", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(Screen.HOME) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isPerfect) SolarYellow else Color(0xFF261D5C)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "BACK TO HOME",
                                color = if (isPerfect) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


// ============================================================================
// REUSABLE SUB-COMPONENTS
// ============================================================================
@Composable
fun GameScreenHeader(
    title: String,
    subtitle: String,
    colorAccent: Color,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // High-contrast back button
        Button(
            onClick = { onBack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1642)),
            border = BorderStroke(1.5.dp, colorAccent),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .size(46.dp)
                .testTag("btn_back_home")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Return home",
                tint = colorAccent,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                color = colorAccent,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = subtitle,
                color = MutedSlate,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============================================================================
// COMPONENT: SCIENCE BOTTOM NAVIGATION BAR
// ============================================================================
@Composable
fun ScienceBottomNavigation(currentScreen: Screen, onNavigate: (Screen) -> Unit) {
    Surface(
        color = CardSurface,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("science_bottom_navigation")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Home
            val isHome = currentScreen == Screen.HOME
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onNavigate(Screen.HOME) }
                    .padding(4.dp)
                    .testTag("nav_tab_home")
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isHome) CosmicPurple else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏠", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Home",
                    color = if (isHome) Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tab 2: Honours / Badges
            val isBadges = currentScreen == Screen.BADGES
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onNavigate(Screen.BADGES) }
                    .padding(4.dp)
                    .testTag("nav_tab_badges")
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isBadges) CosmicPurple else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎖️", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Honours",
                    color = if (isBadges) Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tab 3: Shop / Superstore
            val isShop = currentScreen == Screen.SHOP
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onNavigate(Screen.SHOP) }
                    .padding(4.dp)
                    .testTag("nav_tab_shop")
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isShop) CosmicPurple else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛒", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Shop",
                    color = if (isShop) Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tab 4: Leaderboard
            val isLeaderboard = currentScreen == Screen.LEADERBOARD
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onNavigate(Screen.LEADERBOARD) }
                    .padding(4.dp)
                    .testTag("nav_tab_leaderboard")
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isLeaderboard) CosmicPurple else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏆", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Leaderboard",
                    color = if (isLeaderboard) Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tab 5: Pro Upgrade
            val isPro = currentScreen == Screen.PRO_UPGRADE
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onNavigate(Screen.PRO_UPGRADE) }
                    .padding(4.dp)
                    .testTag("nav_tab_pro")
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isPro) CosmicPurple else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pro",
                    color = if (isPro) Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ============================================================================
// SCREEN: LEADERBOARD PLATFORM
// ============================================================================
@Composable
fun LeaderboardScreen(viewModel: ScienceViewModel) {
    val leaderboardList by viewModel.leaderboardList.collectAsStateWithLifecycle()
    val progressList by viewModel.allProgress.collectAsStateWithLifecycle()

    val completedCount = progressList.count { it.completed }
    val starsEarned = progressList.count { it.earnedStarBadge }
    val userScore = starsEarned * 300 + completedCount * 100

    var nameInput by remember { mutableStateOf("") }
    var scoreSubmittedName by remember { mutableStateOf("") }
    var showSubmissionSuccess by remember { mutableStateOf(false) }
    var isDoubleActive by remember { mutableStateOf(false) }
    val finalScore = if (isDoubleActive) userScore * 2 else userScore

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Outer title banner with back navigation button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier
                        .background(CardSurface, CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .size(46.dp)
                        .testTag("btn_leaderboard_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return home",
                        tint = LimeGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "LEADERBOARD",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Top Achievers of Science Year 5",
                        color = NeonBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Leaderboard Registration Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardSurface)
                    .border(1.5.dp, if (userScore > 0) CosmicPurple else Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "YOUR CURRENT SCORE",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "$userScore PTS",
                                color = LimeGreen,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Sparking Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CosmicPurple)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "★ $starsEarned STARS",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

                    if (userScore == 0) {
                        Text(
                            text = "Play modules to earn stars and unlock your score on the leaderboard! Complete any Science challenge to start.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    } else if (showSubmissionSuccess) {
                        AnimatedVisibility(
                            visible = showSubmissionSuccess,
                            enter = fadeIn() + expandVertically()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "🎉 Score Submitted Successfully!",
                                    color = LimeGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "You are now on the board as \"$scoreSubmittedName\". Play more of the syllabus or retake quizzes to rank higher!",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Submit Your Score to the Board:",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Enter Your Name / School") },
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("field_leaderboard_name"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LimeGreen,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedLabelColor = LimeGreen,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.4f)
                                )
                            )

                            // 2X Score Multiplier Activation Slot
                            if (viewModel.doublePointsOwned > 0 || isDoubleActive) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDoubleActive) LimeGreen.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.03f))
                                        .border(
                                            1.5.dp,
                                            if (isDoubleActive) LimeGreen else CosmicPurple.copy(alpha = 0.4f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable(enabled = !isDoubleActive) {
                                            if (viewModel.useDoublePoints()) {
                                                isDoubleActive = true
                                            }
                                        }
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = if (isDoubleActive) "💎 2X MULTIPLIER ACTIVE! ($finalScore PTS)" 
                                               else "💎 Tap to Use 2x Multiplier (Owned: ${viewModel.doublePointsOwned})",
                                        color = if (isDoubleActive) LimeGreen else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isDoubleActive) {
                                        Text(text = "ACTIVE 🔥", color = LimeGreen, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    } else {
                                        Text(text = "USE 🚀", color = CosmicPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    if (nameInput.isNotBlank()) {
                                        viewModel.addLeaderboardScore(nameInput.trim(), finalScore)
                                        scoreSubmittedName = nameInput.trim()
                                        showSubmissionSuccess = true
                                        nameInput = ""
                                        isDoubleActive = false
                                    }
                                },
                                enabled = nameInput.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("btn_leaderboard_submit")
                            ) {
                                Text(
                                    text = "Submit Score ⚡",
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // podium rows header
        if (leaderboardList.isNotEmpty()) {
            item {
                Text(
                    text = "🏆 CHAMPION STANDINGS",
                    color = SolarYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 8.dp)
                )
            }

            // Podiums
            val sortedList = leaderboardList.sortedByDescending { it.score }
            
            // Top 3 Podium Cards side by side
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Rank 2 (Silver)
                    val secondPlace = sortedList.getOrNull(1)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardSurface)
                            .border(1.5.dp, Color(0xFFC0C0C0), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🥈", fontSize = 24.sp)
                            Text(
                                text = secondPlace?.name ?: "-",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (secondPlace != null) "${secondPlace.score} pts" else "0 pts",
                                color = Color(0xFFC0C0C0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "2nd Place",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Rank 1 (Gold) - Elevated size
                    val firstPlace = sortedList.getOrNull(0)
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardSurface)
                            .border(2.dp, SolarYellow, RoundedCornerShape(16.dp))
                            .padding(vertical = 20.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "👑 🥇", fontSize = 28.sp)
                            Text(
                                text = firstPlace?.name ?: "-",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (firstPlace != null) "${firstPlace.score} pts" else "0 pts",
                                color = SolarYellow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Grand Champion",
                                color = LimeGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Rank 3 (Bronze)
                    val thirdPlace = sortedList.getOrNull(2)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardSurface)
                            .border(1.5.dp, Color(0xFFCD7F32), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🥉", fontSize = 24.sp)
                            Text(
                                text = thirdPlace?.name ?: "-",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (thirdPlace != null) "${thirdPlace.score} pts" else "0 pts",
                                color = Color(0xFFCD7F32),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "3rd Place",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            // Scrollable remaining entries list
            if (sortedList.size > 3) {
                items(sortedList.drop(3)) { entry ->
                    val rankIndex = sortedList.indexOf(entry) + 1
                    val isSelf = entry.name == scoreSubmittedName && entry.score == userScore

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelf) CosmicPurple.copy(alpha = 0.4f) else CardSurface)
                            .border(
                                width = 1.dp,
                                color = if (isSelf) LimeGreen else Color.White.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#$rankIndex",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column {
                                Text(
                                    text = entry.name,
                                    color = if (isSelf) LimeGreen else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = entry.dateString,
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Text(
                            text = "${entry.score} PTS",
                            color = if (isSelf) LimeGreen else NeonBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No competitors found.",
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }

        // Bottom space padding
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// ============================================================================
// SCREEN: KSSR DIGITAL REWARDS & HONOURS GALLERY
// ============================================================================
@Composable
fun BadgesScreen(viewModel: ScienceViewModel) {
    val progressList by viewModel.allProgress.collectAsStateWithLifecycle()
    val leaderboardList by viewModel.leaderboardList.collectAsStateWithLifecycle()
    val profileName = viewModel.profileRegisteredName
    
    val unlockedBadgeIds = getUnlockedBadgeIds(progressList, leaderboardList, profileName)
    var selectedCategoryTab by remember { mutableStateOf(0) } // 0 = Badges, 1 = Medals
    var selectedShowcaseBadge by remember { mutableStateOf<DigitalBadge?>(null) }
    var selectedShowcaseMedal by remember { mutableStateOf<VirtualMedal?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Title
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(CardSurface, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .testTag("btn_badges_back")
                ) {
                    Text(text = "←", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "My Achievements",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "KSSR Year 5 Science Honours",
                        color = NeonBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // 2. Summary Progress Bar
        item {
            val total = kssrBadges.size
            val earned = unlockedBadgeIds.size
            val ratio = if (total > 0) earned.toFloat() / total else 0f
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardSurface)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "HONOURS PROGRESS",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (earned == total) "KSSR Grandmaster Mastermind! 👑" 
                                       else if (earned >= 5) "Elite Science Superstar! 🌟" 
                                       else if (earned >= 1) "Budding Researcher! 🔬" 
                                       else "Embark on Your Voyage! 🚀",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        
                        Text(
                            text = "$earned / $total earned",
                            color = LimeGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(DarkBackground)
                    ) {
                        if (ratio > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ratio)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(NeonBlue, LimeGreen)
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }

        // 2.5. Category Selector Pill Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF15103E))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedCategoryTab == 0) CosmicPurple else Color.Transparent)
                        .clickable { selectedCategoryTab = 0 }
                        .padding(vertical = 10.dp)
                        .testTag("tab_badges"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏅 KSSR BADGES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (selectedCategoryTab == 0) Color.White else Color.White.copy(alpha = 0.5f),
                        letterSpacing = 0.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedCategoryTab == 1) CosmicPurple else Color.Transparent)
                        .clickable { selectedCategoryTab = 1 }
                        .padding(vertical = 10.dp)
                        .testTag("tab_medals"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🥇 VIRTUAL MEDALS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (selectedCategoryTab == 1) Color.White else Color.White.copy(alpha = 0.5f),
                            letterSpacing = 0.5.sp
                        )
                        val unlockedCount = viewModel.unlockedMedals.size
                        if (unlockedCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SolarYellow)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$unlockedCount/5",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Grid of achievements manually displayed via chunked rows (avoids nested scrolling)
        if (selectedCategoryTab == 0) {
            val chunkedBadges = kssrBadges.chunked(2)
        chunkedBadges.forEach { pair ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    pair.forEach { badge ->
                        val isUnlocked = unlockedBadgeIds.contains(badge.id)
                        BadgeCard(
                            badge = badge,
                            isUnlocked = isUnlocked,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedShowcaseBadge = badge }
                        )
                    }
                    if (pair.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        } else {
            // RENDER THE CABINET OF VIRTUAL MEDALS!
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF15103E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Brush.linearGradient(listOf(SolarYellow.copy(alpha = 0.3f), Color.Transparent)), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "👑 THE ROYAL SCIENCE CABINET 👑",
                            color = SolarYellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Earned by achieving perfect 100% scores on Mega Quizzes and Challenge Assessments.",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            val chunkedMedals = virtualMedalsList.chunked(2)
            chunkedMedals.forEach { pair ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        pair.forEach { medal ->
                            val isUnlocked = viewModel.unlockedMedals.contains(medal.id)
                            MedalCard(
                                medal = medal,
                                isUnlocked = isUnlocked,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedShowcaseMedal = medal }
                            )
                        }
                        if (pair.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(110.dp)) // Safe scrolling padding to clear bottom bar
        }
    }

    // Detail Showcase Dialog
    selectedShowcaseBadge?.let { badge ->
        val isUnlocked = unlockedBadgeIds.contains(badge.id)
        BadgeShowcaseDialog(
            badge = badge,
            isUnlocked = isUnlocked,
            onDismiss = { selectedShowcaseBadge = null },
            onAction = {
                selectedShowcaseBadge = null
                val destination = when (badge.id) {
                    "circuit_master" -> Screen.ELECTRICITY
                    "galactic_explorer" -> Screen.SPACE
                    "animal_guardian" -> Screen.SURVIVAL
                    "skeletal_expert" -> Screen.SKELETAL
                    "trivia_cadet", "perfect_genius" -> Screen.QUIZ
                    "social_scholar", "top_three_legend" -> Screen.LEADERBOARD
                    else -> Screen.HOME
                }
                viewModel.navigateTo(destination)
            }
        )
    }

    // Medal Showcase Dialog
    selectedShowcaseMedal?.let { medal ->
        val isUnlocked = viewModel.unlockedMedals.contains(medal.id)
        MedalShowcaseDialog(
            medal = medal,
            isUnlocked = isUnlocked,
            onDismiss = { selectedShowcaseMedal = null },
            onAction = {
                selectedShowcaseMedal = null
                if (!isUnlocked) {
                    val destination = when (medal.module) {
                        "electricity" -> Screen.ELECTRICITY
                        "space" -> Screen.SPACE
                        "survival" -> Screen.SURVIVAL
                        "skeletal" -> Screen.SKELETAL
                        "quiz" -> Screen.QUIZ
                        else -> Screen.HOME
                    }
                    viewModel.navigateTo(destination)
                }
            }
        )
    }
}

@Composable
fun BadgeCard(
    badge: DigitalBadge,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isUnlocked) CardSurface else CardSurface.copy(alpha = 0.5f))
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isUnlocked) badge.color.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("badge_card_${badge.id}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (isUnlocked) badge.color.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(text = badge.iconEmoji, fontSize = 28.sp)
                } else {
                    Text(text = "🔒", fontSize = 20.sp, modifier = Modifier.scale(0.85f))
                }
            }

            Text(
                text = badge.title,
                color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = badge.category,
                color = if (isUnlocked) badge.color else Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun BadgeShowcaseDialog(
    badge: DigitalBadge,
    isUnlocked: Boolean,
    onDismiss: () -> Unit,
    onAction: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CardSurface,
            border = BorderStroke(1.5.dp, if (isUnlocked) badge.color else Color.White.copy(alpha = 0.1f)),
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .padding(4.dp)
                .testTag("dialog_badge_showcase")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            color = if (isUnlocked) badge.color.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isUnlocked) badge.iconEmoji else "🔒",
                        fontSize = 44.sp
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = badge.title,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = badge.category.uppercase(),
                        color = if (isUnlocked) badge.color else Color.White.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBackground.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "CRITERIA REQUIREMENTS",
                        color = NeonBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = badge.criteria,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = badge.description,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                if (isUnlocked) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(badge.color.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .border(1.dp, badge.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "💡 DID YOU KNOW? (KSSR FACTS)",
                            color = badge.color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = badge.fact,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Dismiss", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onAction,
                            colors = ButtonDefaults.buttonColors(containerColor = badge.color),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(
                                "Revise Topic 📘", 
                                color = DarkBackground, 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Dismiss", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onAction,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(
                                "Let's Earn It! ⚡", 
                                color = DarkBackground, 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeCelebrationDialog(badge: DigitalBadge, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = CardSurface,
            border = BorderStroke(2.dp, badge.color),
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .padding(4.dp)
                .testTag("dialog_badge_celebration")
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "celebration_glow")
            val rotateAnim by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(8000, easing = LinearEasing)
                ),
                label = "rotate"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🏆 ACHIEVEMENT UNLOCKED! 🌟",
                    color = SolarYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(badge.color.copy(alpha = 0.1f), badge.color, badge.color.copy(alpha = 0.1f))
                                ),
                                style = Stroke(
                                    width = 4.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), rotateAnim)
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = badge.iconEmoji, fontSize = 56.sp)
                }

                Text(
                    text = badge.title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .background(badge.color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge.category.uppercase(),
                        color = badge.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = badge.description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "💡 KSSR YEAR 5 FACT",
                        color = NeonBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = badge.fact,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = badge.color),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_celebration_claim")
                ) {
                    Text(
                        text = "Claim & Continue ⚡",
                        color = DarkBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MedalCard(
    medal: VirtualMedal,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition("medalGlow")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val bgModifier = if (isUnlocked) {
        Modifier.background(Brush.verticalGradient(listOf(CardSurface, Color(0xFF191244))))
    } else {
        Modifier.background(CardSurface.copy(alpha = 0.4f))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .then(bgModifier)
            .border(
                border = BorderStroke(
                    width = if (isUnlocked) 2.dp else 1.dp,
                    color = if (isUnlocked) {
                        medal.glowColor.copy(alpha = borderAlpha)
                    } else {
                        Color.White.copy(alpha = 0.08f)
                    }
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("medal_card_${medal.id}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = if (isUnlocked) medal.shinyColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f),
                        shape = CircleShape
                    )
                    .border(
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = if (isUnlocked) medal.shinyColor else Color.White.copy(alpha = 0.1f)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(
                        text = medal.iconEmoji,
                        fontSize = 32.sp,
                        modifier = Modifier.scale(if (medal.iconEmoji.contains("🦴") || medal.iconEmoji.contains("⚡") || medal.iconEmoji.contains("🪐") || medal.iconEmoji.contains("🛡️")) 0.9f else 1.0f)
                    )
                } else {
                    Text(text = "🔒", fontSize = 20.sp, color = Color.White.copy(alpha = 0.3f))
                }
            }

            Text(
                text = medal.title,
                color = if (isUnlocked) medal.shinyColor else Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = if (isUnlocked) "🏆 PERFECT MASTER" else "UNEARNED",
                color = if (isUnlocked) LimeGreen else Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun MedalShowcaseDialog(
    medal: VirtualMedal,
    isUnlocked: Boolean,
    onDismiss: () -> Unit,
    onAction: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = CardSurface,
            border = BorderStroke(2.dp, if (isUnlocked) medal.shinyColor else Color.White.copy(alpha = 0.1f)),
            modifier = Modifier
                .width(320.dp)
                .testTag("dialog_medal_showcase")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(if (isUnlocked) medal.shinyColor.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f))
                        .border(1.5.dp, if (isUnlocked) medal.shinyColor else Color.White.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isUnlocked) medal.iconEmoji else "🔒",
                        fontSize = 44.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = medal.title,
                    color = if (isUnlocked) medal.shinyColor else Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "QUIZ CRITERIA",
                    color = NeonBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = medal.criteria,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = medal.description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(medal.shinyColor.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .border(1.dp, medal.shinyColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "KSSR SCHOLAR REVELATION",
                        color = medal.shinyColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Students who earn perfect marks in science quizzes reinforce their long-term cognitive retention of vital syllabus topics (like electrical currents or orbital distances) by over 80%!",
                        color = Color.White,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isUnlocked) medal.shinyColor else CosmicPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = if (isUnlocked) "DONE" else "RETAKE MODULE FOR MEDAL 🚀",
                        color = if (isUnlocked) Color.Black else Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MedalCelebrationDialog(medal: VirtualMedal, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = CardSurface,
            border = BorderStroke(3.dp, medal.shinyColor),
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .padding(4.dp)
                .testTag("dialog_medal_celebration")
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "medal_celebration_glow")
            val rotateAnim by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(8000, easing = LinearEasing)
                ),
                label = "rotate"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🥇 ROYAL MEDAL EARNED! 🥇",
                    color = SolarYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(medal.shinyColor.copy(alpha = 0.1f), medal.shinyColor, medal.shinyColor.copy(alpha = 0.1f))
                                ),
                                style = Stroke(
                                    width = 5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), rotateAnim)
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = medal.iconEmoji, fontSize = 64.sp)
                }

                Text(
                    text = medal.title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .background(medal.shinyColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "100% PERFECT SCORE REWARD",
                        color = medal.shinyColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = medal.description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "💡 +100 COINS BONUS REWARD!",
                        color = LimeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "As part of our KSSR Year 5 Science encouragement program, you have been awarded 100 bonus science coins for this flawless score!",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                }

                Button(
                    onClick = {
                        onDismiss()
                        SoundPlayer.playCorrect()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = medal.shinyColor),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_medal_celebration_claim")
                ) {
                    Text(
                        text = "Claim Medal & Shimmer 👑",
                        color = DarkBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ============================================================================
// SCREEN: SCIENCE UPGRADES STORE (SHOP)
// ============================================================================
@Composable
fun ShopScreen(viewModel: ScienceViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Outer title banner with back button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier
                        .background(CardSurface, CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .size(46.dp)
                        .testTag("btn_shop_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return home",
                        tint = SolarYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "SCIENCE STORE",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "KSSR Year 5 Upgrade Power-Ups",
                        color = NeonBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Currency balance display card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, CosmicPurple, RoundedCornerShape(22.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "YOUR BALANCE",
                            color = Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${viewModel.userCoins} COINS",
                                color = SolarYellow,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Removed coin generator button to promote authentic learning progress
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CosmicPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🧪", fontSize = 18.sp)
                    }
                }
            }
        }

        // Premium Core Banner In Shop
        item {
            val isPro = viewModel.isProActive
            if (isPro) {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF17123A)),
                    border = BorderStroke(1.5.dp, CosmicPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shop_pro_active_banner")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👑", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SCIENCE PRO IS ACTIVE",
                                color = LimeGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "You have unlocked the full potential of Year 5 Science! Ad-free learning and unlimited quizzes are now permanently enabled on this device.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF17123A)),
                    border = BorderStroke(1.5.dp, CosmicPurple.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.PRO_UPGRADE) }
                        .testTag("shop_upgrade_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👑", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "UPGRADE TO SCIENCE PRO",
                                    color = CosmicPurple,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Unlock the universe of Year 5 Science — no limits, no distractions.",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "TAP TO START 7-DAY FREE TRIAL",
                                color = NeonBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Navigate to upgrade details",
                            tint = CosmicPurple,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Store listing items
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Hint (💡)
                ShopItemCard(
                    title = "Interactive Hint",
                    itemCount = viewModel.hintsOwned,
                    cost = 100,
                    iconSymbol = "💡",
                    accentColor = CosmicPurple,
                    description = "Eliminates and grays out two incorrect answers in the mega Year 5 multiple-choice assessment.",
                    userCoins = viewModel.userCoins,
                    onBuy = { viewModel.buyItem("hint", 100) },
                    testTagPrefix = "hint"
                )

                // Course Auto-Boost (⚡)
                ShopItemCard(
                    title = "Course Auto-Boost",
                    itemCount = viewModel.boostsOwned,
                    cost = 200,
                    iconSymbol = "⚡",
                    accentColor = SolarYellow,
                    description = "Instantly clears any single tough question in the quiz and credits you the correct score automatically.",
                    userCoins = viewModel.userCoins,
                    onBuy = { viewModel.buyItem("boost", 200) },
                    testTagPrefix = "boost"
                )

                // 2X Score Multiplier (💎)
                ShopItemCard(
                    title = "2X Points Multiplier",
                    itemCount = viewModel.doublePointsOwned,
                    cost = 300,
                    iconSymbol = "💎",
                    accentColor = LimeGreen,
                    description = "Doubles your score submission on the Leaderboard so you claim the regional champion rank!",
                    userCoins = viewModel.userCoins,
                    onBuy = { viewModel.buyItem("double_points", 300) },
                    testTagPrefix = "multiplier"
                )
            }
        }
    }
}

@Composable
fun ShopItemCard(
    title: String,
    itemCount: Int,
    cost: Int,
    iconSymbol: String,
    accentColor: Color,
    description: String,
    userCoins: Int,
    onBuy: () -> Unit,
    testTagPrefix: String
) {
    val canAfford = userCoins >= cost

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, if (canAfford) accentColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .testTag("shop_card_$testTagPrefix")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoticon Sphere
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconSymbol, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "OWNED: $itemCount",
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onBuy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) accentColor else Color.White.copy(alpha = 0.05f),
                        disabledContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    enabled = canAfford,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("btn_buy_$testTagPrefix")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Buy for ",
                            color = if (canAfford) (if (accentColor == SolarYellow) Color.Black else Color.White) else Color.White.copy(alpha = 0.3f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "🪙 $cost Coins",
                            color = if (canAfford) (if (accentColor == SolarYellow) Color.Black else SolarYellow) else Color.White.copy(alpha = 0.3f),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// SCREEN: ADMIN COMMAND PORTAL
// ============================================================================
@Composable
fun AdminScreen(viewModel: ScienceViewModel) {
    val progressList by viewModel.allProgress.collectAsStateWithLifecycle()
    val leaderboardList by viewModel.leaderboardList.collectAsStateWithLifecycle()

    if (!viewModel.adminAccessGranted) {
        var passcodeText by remember { mutableStateOf("") }
        var passcodeVisible by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp)
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier
                        .background(CardSurface, CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .size(46.dp)
                        .align(Alignment.CenterStart)
                        .testTag("btn_admin_gate_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return home",
                        tint = NeonBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFE53935).copy(alpha = 0.1f), CircleShape)
                    .border(2.dp, Color(0xFFEF5350), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted Core",
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ENCRYPTED SYSTEM CORE",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Authorized access only. Enter administrative passcode to unlock system override protocols.",
                color = MutedSlate,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = passcodeText,
                onValueChange = { 
                    passcodeText = it
                    errorMessage = null 
                },
                label = { Text("Enter Passcode", color = MutedSlate) },
                singleLine = true,
                visualTransformation = if (passcodeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val success = viewModel.attemptAdminUnlock(passcodeText)
                        if (!success) {
                            errorMessage = "ACCESS DENIED: INVALID DECRYPTION PASSKEY"
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = NeonBlue,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = CardSurface,
                    unfocusedContainerColor = CardSurface
                ),
                trailingIcon = {
                    TextButton(
                        onClick = { passcodeVisible = !passcodeVisible },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = if (passcodeVisible) "HIDE" else "SHOW",
                            color = NeonBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_password_input")
            )

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFEF5350),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val success = viewModel.attemptAdminUnlock(passcodeText)
                    if (!success) {
                        errorMessage = "ACCESS DENIED: INVALID DECRYPTION PASSKEY"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_password_submit")
            ) {
                Text(
                    text = "DECRYPT SYSTEM OVERRIDES",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with custom back button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier
                        .background(CardSurface, CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .size(46.dp)
                        .testTag("btn_admin_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return home",
                        tint = NeonBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "ADMIN COMMAND PORTAL",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Authorized overrides & system diagnostics",
                        color = MutedSlate,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // 2. Diagnostics Overview Panel
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "DIAGNOSTICS MONITOR",
                        color = NeonBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "🪙 Current Coins", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            Text(text = "${viewModel.userCoins}", color = SolarYellow, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                        Column {
                            Text(text = "🎒 Completed Modules", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            val completedNum = progressList.count { it.completed }
                            Text(text = "$completedNum / 4", color = LimeGreen, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                        Column {
                            Text(text = "🏆 Leaderboard Entries", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            Text(text = "${leaderboardList.size}", color = Color(0xFFFF8A65), fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // 3. Currency Adjustment Terminal
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "CURRENCY CONTROLS",
                        color = SolarYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Directly adjust the user's science coin reserves for store testing.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.adminSetCoins(viewModel.userCoins + 100) },
                            colors = ButtonDefaults.buttonColors(containerColor = CardSurface.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, SolarYellow.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .testTag("btn_admin_add_100")
                        ) {
                            Text("+100 🪙", color = SolarYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.adminSetCoins(viewModel.userCoins + 1000) },
                            colors = ButtonDefaults.buttonColors(containerColor = CardSurface.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, SolarYellow.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .testTag("btn_admin_add_1000")
                        ) {
                            Text("+1000 🪙", color = SolarYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.adminSetCoins(350) },
                            colors = ButtonDefaults.buttonColors(containerColor = CardSurface.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .testTag("btn_admin_reset_coins")
                        ) {
                            Text("Reset (350)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3.5. System Announcement Broadcast Console
        item {
            var localAnnouncementText by remember { mutableStateOf(viewModel.announcementText) }
            var localAnnouncementActive by remember { mutableStateOf(viewModel.announcementActive) }
            var showSavedFeedback by remember { mutableStateOf(false) }

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
                    .testTag("admin_announcement_controls_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "📢 SYSTEM BROADCAST CONSOLE",
                        color = NeonBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Edit, activate, or clear system-wide announcements shown at the top of the student Home screen.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // TextField for Announcement Text
                    OutlinedTextField(
                        value = localAnnouncementText,
                        onValueChange = {
                            localAnnouncementText = it
                            showSavedFeedback = false
                        },
                        label = { Text("Announcement Message", color = MutedSlate) },
                        placeholder = { Text("🚀 Welcome to Science Year 5! Unleash your curiosity in the new Space Exploration simulator!", color = MutedSlate.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground
                        ),
                        singleLine = false,
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_admin_announcement_text")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .clickable { 
                                localAnnouncementActive = !localAnnouncementActive 
                                showSavedFeedback = false
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = if (localAnnouncementActive) "🟢" else "🔴", fontSize = 14.sp)
                            Column {
                                Text(
                                    text = "Broadcast Status",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (localAnnouncementActive) "Visible to all students" else "Hidden / Draft status",
                                    color = MutedSlate,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Switch(
                            checked = localAnnouncementActive,
                            onCheckedChange = { 
                                localAnnouncementActive = it 
                                showSavedFeedback = false
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LimeGreen,
                                checkedTrackColor = LimeGreen.copy(alpha = 0.3f),
                                uncheckedThumbColor = MutedSlate,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.scale(0.85f).testTag("switch_admin_announcement_active")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Clear/Reset Button
                        Button(
                            onClick = {
                                localAnnouncementText = ""
                                localAnnouncementActive = false
                                viewModel.adminUpdateAnnouncement("", false)
                                showSavedFeedback = true
                                SoundPlayer.playCorrect()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_admin_clear_announcement")
                        ) {
                            Text("Clear Broadcast", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Save/Publish Button
                        Button(
                            onClick = {
                                viewModel.adminUpdateAnnouncement(localAnnouncementText, localAnnouncementActive)
                                showSavedFeedback = true
                                SoundPlayer.playCorrect()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(44.dp)
                                .testTag("btn_admin_save_announcement")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Publish Live 🚀",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // Success Feedback message
                    if (showSavedFeedback) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(LimeGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "✓", color = LimeGreen, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                Text(
                                    text = "System overrides configured & saved successfully!",
                                    color = LimeGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Live preview section showing how it will render to the student
                    if (localAnnouncementText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "LIVE DESKTOP PREVIEW",
                            color = SolarYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Render preview of the alert
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    BorderStroke(1.5.dp, if (localAnnouncementActive) TechGreen else Color.White.copy(alpha = 0.15f)),
                                    RoundedCornerShape(14.dp)
                                ),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkBackground)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeonBlue.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "📢", fontSize = 16.sp)
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "SYSTEM ANNOUNCEMENT",
                                            color = NeonBlue,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                        if (!localAnnouncementActive) {
                                            Text(
                                                text = "DRAFT - HIDDEN",
                                                color = EmberOrange,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier
                                                    .background(EmberOrange.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = localAnnouncementText,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Module Progression Overrides
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "MODULE STATUS OVERRIDES",
                        color = CosmicPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val modules = listOf(
                        "electricity" to "Electricity (The Power Station)",
                        "space" to "Space Explorer (Planets)",
                        "survival" to "Wildlife Survival (Adaptations)",
                        "quiz" to "Mega Science Quiz"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        modules.forEach { (modId, name) ->
                            val prog = progressList.find { it.moduleId == modId }
                            val isCompleted = prog?.completed == true

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkBackground.copy(alpha = 0.3f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (isCompleted) "Status: COMPLETED" else "Status: IN PROGRESS",
                                        color = if (isCompleted) LimeGreen else Color.White.copy(alpha = 0.4f),
                                        fontSize = 11.sp
                                    )
                                }

                                Button(
                                    onClick = { viewModel.adminToggleModuleCompleted(modId, !isCompleted) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCompleted) Color.White.copy(alpha = 0.05f) else CosmicPurple
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .testTag("btn_admin_toggle_$modId")
                                ) {
                                    Text(
                                        text = if (isCompleted) "Reset" else "Complete",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Leaderboard Entries Manager
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "LEADERBOARD RECORDS",
                        color = Color(0xFFFF8A65),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Observe live entries and delete custom test names or spam records easily.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (leaderboardList.isEmpty()) {
                        Text(
                            text = "No leaderboard records found in database.",
                            color = Color.White.copy(alpha = 0.3f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            leaderboardList.sortedByDescending { it.score }.forEachIndexed { idx, entry ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkBackground.copy(alpha = 0.3f))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "#${idx + 1}", color = Color(0xFFFF8A65), fontWeight = FontWeight.Black, fontSize = 12.sp)
                                        Column {
                                            Text(text = entry.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "Score: ${entry.score} pts • ${entry.dateString}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.adminDeleteLeaderboardEntry(entry) },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .testTag("btn_admin_delete_entry_${entry.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete entry",
                                            tint = Color(0xFFEF5350),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Global System Operations (Destructive DB Reset)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEF5350).copy(alpha = 0.2f), RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "SYSTEM CONTROLS",
                        color = Color(0xFFEF5350),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.resetAllProgressDB() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_admin_hard_reset")
                    ) {
                        Text(
                            text = "☢️ HARD RESET DATABASE",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SkeletalScreen(viewModel: ScienceViewModel) {
    val exploredBones = viewModel.exploredBones
    val activeBone = viewModel.activeBonePopup

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App header inside screen
        GameScreenHeader(
            title = "HUMAN SKELETAL LAB",
            subtitle = "KSSR Year 5 Science Unit 2",
            colorAccent = EmberOrange,
            onBack = { 
                viewModel.resetSkeletalGame()
                viewModel.navigateTo(Screen.HOME) 
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        when (viewModel.activeSkeletalStage) {
            0 -> {
                // STAGE 0: EXPLORATION
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, EmberOrange.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🔬 SKELETAL ANATOMY STATION",
                            color = EmberOrange,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap on the 4 major bone structures of the human body below to inspect their KSSR defensive and protective functions.",
                            color = MutedSlate,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive Bone Model Selector Stack
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            viewModel.standardBones.forEach { bone ->
                                val isExplored = exploredBones.contains(bone.id)
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isExplored) Color(0xFF2E1A11) else Color(0xAA1C1642)
                                    ),
                                    border = BorderStroke(
                                        1.5.dp,
                                        if (isExplored) LimeGreen else Color.White.copy(alpha = 0.15f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.exploreBone(bone.id) }
                                        .testTag("bone_selection_${bone.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(
                                                    if (isExplored) LimeGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = bone.emoji, fontSize = 20.sp)
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = bone.name,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = if (isExplored) "✓ Explored & Analyzed" else "⏳ Click to dissect function",
                                                color = if (isExplored) LimeGreen else MutedSlate,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        if (isExplored) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Analyzed",
                                                tint = LimeGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Exploration progress indicators
                        Text(
                            text = "Lab Dissection Progress: ${exploredBones.size} / 4",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(exploredBones.size / 4f)
                                    .background(EmberOrange)
                            )
                        }

                        if (exploredBones.size == 4) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.startSkeletalChallenge() },
                                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_start_skeletal_challenge")
                            ) {
                                Text(
                                    text = "🧠 START DECRYPTION CHALLENGE",
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // STAGE 1: RIDDLES / QUIZ MATCH-UP
                val currentRiddle = viewModel.skeletalRiddles[viewModel.skeletalRiddleIndex]
                val answered = viewModel.isSkeletalRiddleSubmitted
                val selectedIdx = viewModel.skeletalRiddleAnswerIndex

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, CosmicPurple.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DECRYPTION CHALLENGE",
                                color = CosmicPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Question ${viewModel.skeletalRiddleIndex + 1} of 4",
                                color = MutedSlate,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = currentRiddle.question,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        // Riddle choices
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            currentRiddle.options.forEachIndexed { optIdx, optValue ->
                                val isSelected = selectedIdx == optIdx
                                val isCorrectOption = optIdx == currentRiddle.correctIndex
                                val buttonColor = when {
                                    !answered -> if (isSelected) CosmicPurple else CardSurface
                                    isCorrectOption -> LimeGreen.copy(alpha = 0.15f)
                                    isSelected -> Color.Red.copy(alpha = 0.12f)
                                    else -> CardSurface
                                }
                                val borderColor = when {
                                    !answered -> if (isSelected) CosmicPurple else Color.White.copy(alpha = 0.15f)
                                    isCorrectOption -> LimeGreen
                                    isSelected -> Color.Red
                                    else -> Color.White.copy(alpha = 0.08f)
                                }

                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = buttonColor),
                                    border = BorderStroke(1.5.dp, borderColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = !answered) { viewModel.submitSkeletalRiddle(optIdx) }
                                        .testTag("skeletal_option_$optIdx")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(
                                                    if (isSelected) CosmicPurple else Color.White.copy(alpha = 0.05f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = ('A'.code + optIdx).toChar().toString(),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Text(
                                            text = optValue,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (answered) {
                                            if (isCorrectOption) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Correct",
                                                    tint = LimeGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            } else if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Incorrect",
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (answered) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedIdx == currentRiddle.correctIndex) Color(0x118BC34A) else Color(0x11FF5722)
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (selectedIdx == currentRiddle.correctIndex) LimeGreen.copy(alpha = 0.4f) else EmberOrange.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = currentRiddle.explanation,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { viewModel.nextSkeletalRiddle() },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("btn_next_skeletal_riddle")
                            ) {
                                Text(
                                    text = if (viewModel.skeletalRiddleIndex < 3) "CONTINUE ARCHEOLOGY" else "SUBMIT SPECIALIST CERTIFICATION",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // STAGE 2: ACTION RECONSTRUCTION SUCCESS VIEW!
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, LimeGreen.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💀 MODULE COMPLETED 💀",
                            color = LimeGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(LimeGreen.copy(alpha = 0.15f), CircleShape)
                                .border(2.dp, LimeGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 42.sp)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "OSTEOLOGY SPECIALIST",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Magnificent work! You have explored the primary blueprint components of the human frame and completed all diagnostic bone matching challenges perfectly.",
                            color = MutedSlate,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "MODULE STATUS", color = MutedSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "PASSED", color = LimeGreen, fontSize = 14.sp, fontWeight = FontWeight.Black)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "BONE SCORE", color = MutedSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${viewModel.skeletalScore} / 4 CORRECT", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { 
                                viewModel.resetSkeletalGame()
                                viewModel.navigateTo(Screen.HOME) 
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_skeletal_return")
                        ) {
                            Text(
                                text = "RETURN TO HEADQUARTERS",
                                color = DarkBackground,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Interactive Popups for Bone exploration Info
    activeBone?.let { bone ->
        Dialog(onDismissRequest = { viewModel.closeBonePopup() }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = BorderStroke(2.dp, EmberOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(EmberOrange.copy(alpha = 0.15f), CircleShape)
                            .border(1.5.dp, EmberOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = bone.emoji, fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = bone.name.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmberOrange.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "KSSR PRIMARY FUNCTION",
                            color = EmberOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = bone.function,
                        color = LimeGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = bone.detail,
                        color = MutedSlate,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.closeBonePopup() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmberOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_close_bone_popup")
                    ) {
                        Text(
                            text = "CLOSE DISSECTION DISK",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// SCREEN: PREMIUM UPGRADE INTERFACE (SCIENCE PRO)
// ============================================================================
@Composable
fun ProUpgradeScreen(viewModel: ScienceViewModel) {
    val isPro = viewModel.isProActive
    val plan = viewModel.subscriptionPlan
    var showTrialSuccess by remember { mutableStateOf(false) }
    var showSubscribeDialog by remember { mutableStateOf(false) }
    var showSubscribeSuccess by remember { mutableStateOf(false) }
    var selectedPlanForSub by remember { mutableStateOf("Yearly Plan") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App header inside screen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.HOME) },
                modifier = Modifier
                    .background(CardSurface, CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    .size(46.dp)
                    .testTag("btn_pro_upgrade_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return home",
                    tint = CosmicPurple,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "UPGRADE TO SCIENCE PRO",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "KSSR Year 5 Science Edition",
                    color = CosmicPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        if (isPro) {
            // PRO ACTIVE DASHBOARD VIEW
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF140D33)),
                border = BorderStroke(2.dp, CosmicPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .testTag("pro_active_dashboard")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(CosmicPurple.copy(alpha = 0.2f), CircleShape)
                            .border(2.dp, CosmicPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛡️", fontSize = 42.sp)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "SCIENCE PRO IS ACTIVE!",
                        color = LimeGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Plan: $plan",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Thank you for upgrading! You now have total, uninterrupted access to all of KSSR Year 5 Science simulations, interactive labs, 100% ad-free quizzes, priority speed and downloadable flashcards.",
                        color = MutedSlate,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.cancelProActivation() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_pro_cancel")
                    ) {
                        Text(
                            text = "DEMO: RESET TO FREE MEMBERSHIP",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            // PRO UPGRADE PURCHASE VIEW
            // Headings
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Upgrade to Science Pro",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("pro_upgrade_header")
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Unlock the universe of Year 5 Science — no limits, no distractions.",
                    color = CosmicPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // GORGEOUS HERO PRO CARD VISUAL
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .drawBehind {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF320070), Color(0xFF0D031F)),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            )
                        )
                    }
                    .border(2.dp, CosmicPurple, RoundedCornerShape(24.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.TopStart)) {
                        Text(
                            text = "OFFICIAL SCIENCE PASS",
                            color = CosmicPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "KSSR PRO SCIENTIST",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "S5",
                        color = Color.White.copy(alpha = 0.08f),
                        fontSize = 100.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.align(Alignment.BottomEnd).offset(x = 10.dp, y = 30.dp)
                    )
                    Text(
                        text = "YEAR 5 SPECIALIST",
                        color = LimeGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // LIST OF PERKS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProPerkCard(
                    title = "Unlimited Science Access",
                    function = "Take as many quizzes and lessons as you like. Full access until your trial ends or you buy a month/year subscription.",
                    emoji = "♾️"
                )
                ProPerkCard(
                    title = "No Ads",
                    function = "100% ad-free learning. Total focus on your scientific journey across the whole app.",
                    emoji = "🚫"
                )
                ProPerkCard(
                    title = "Priority Experiment Speed",
                    function = "Access simulations and video lessons first with accelerated loading times.",
                    emoji = "⚡"
                )
                ProPerkCard(
                    title = "Bulk Revision Download",
                    function = "Download large batches of revision notes, flashcards, or practice sheets instantly.",
                    emoji = "📥"
                )
                ProPerkCard(
                    title = "Extra Curricular Content",
                    function = "Unlock deeper, more advanced topics beyond the core Year 5 syllabus.",
                    emoji = "📚"
                )
                ProPerkCard(
                    title = "Junior Scientist Community",
                    function = "Exclusive 'Science Pro' badge and access to premium discussion channels and homework help.",
                    emoji = "👥"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // REVELATION CALL TO ACTIONS stacked
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // BUTTON 1: Trial
                Button(
                    onClick = {
                        viewModel.activateProTrial()
                        showTrialSuccess = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_pro_start_trial")
                ) {
                    Text(
                        text = "Start 7-Day Free Trial",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                // BUTTON 2: Subscriptions options
                Button(
                    onClick = { showSubscribeDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.5.dp, CosmicPurple),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_pro_or_subscribe")
                ) {
                    Text(
                        text = "Or Subscribe (Month or Year)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Trial Success Dialog
    if (showTrialSuccess) {
        Dialog(onDismissRequest = { showTrialSuccess = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = BorderStroke(2.dp, CosmicPurple),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎉⚡👑", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "TRIAL UNLOCKED!",
                        color = LimeGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Congratulations! Your 7-Day Free Trial to Science Pro is now active. Enjoy complete lesson access and zero advertisements!",
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { showTrialSuccess = false },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text("LET'S STUDY SCIENCE", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Subscribe Dialog selection
    if (showSubscribeDialog) {
        Dialog(onDismissRequest = { showSubscribeDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = BorderStroke(2.dp, CosmicPurple),
                modifier = Modifier.padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "CHOOSE YOUR SPECIALIST PLAN",
                        color = CosmicPurple,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Monthly Subscription option
                    val isMonthlySelected = selectedPlanForSub == "Monthly Plan"
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMonthlySelected) CosmicPurple.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f)
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            if (isMonthlySelected) CosmicPurple else Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlanForSub = "Monthly Plan" }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Monthly Membership", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Cancel anytime. 100% core access.", color = MutedSlate, fontSize = 11.sp)
                            }
                            Text("$4.99 / mo", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }

                    // Yearly Subscription option
                    val isYearlySelected = selectedPlanForSub == "Yearly Plan"
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isYearlySelected) CosmicPurple.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f)
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            if (isYearlySelected) CosmicPurple else Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlanForSub = "Yearly Plan" }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Yearly Membership", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(LimeGreen, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("SAVE 50%", color = DarkBackground, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("Billed annually. Best value option.", color = MutedSlate, fontSize = 11.sp)
                            }
                            Text("$29.99 / yr", color = LimeGreen, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showSubscribeDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Text("CANCEL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                showSubscribeDialog = false
                                viewModel.activateProSubscription(selectedPlanForSub)
                                showSubscribeSuccess = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                            modifier = Modifier.weight(1.5f).height(44.dp)
                        ) {
                            Text("COMPLETE CHECKOUT", color = DarkBackground, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Subscribe Success Dialog
    if (showSubscribeSuccess) {
        Dialog(onDismissRequest = { showSubscribeSuccess = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = BorderStroke(2.dp, CosmicPurple),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "👑🌟🚀", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "SUBSCRIBED TO SCIENCE PRO!",
                        color = LimeGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Wonderful! Your subscription to $plan is active. You have full, unlimited, and priority access to all materials — completely ad-free!",
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { showSubscribeSuccess = false },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text("ENTER SCIENCE EXTRAORDINARY", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProPerkCard(title: String, function: String, emoji: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13162F)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CosmicPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = function,
                    color = MutedSlate,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
