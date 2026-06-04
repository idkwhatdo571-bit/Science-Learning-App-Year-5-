package com.example.ui

import android.media.ToneGenerator
import android.media.AudioManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ModuleProgress
import com.example.data.LeaderboardEntry
import com.example.data.ProgressRepository
import com.example.ui.theme.SolarYellow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    ELECTRICITY,
    SPACE,
    SURVIVAL,
    SKELETAL,
    QUIZ,
    LEADERBOARD,
    BADGES,
    SHOP,
    ADMIN,
    PRO_UPGRADE
}

// Custom data model for digital badges/achievements
data class DigitalBadge(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "Syllabus Explorer", "Electricity", "Solar System", "Animal Survival", etc.
    val iconEmoji: String,
    val criteria: String,
    val fact: String, // KSSR Year 5 facts
    val color: Color
)

// Custom data model for virtual medals awarded on perfect scores
data class VirtualMedal(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val criteria: String,
    val shinyColor: Color,
    val glowColor: Color,
    val module: String
)

val virtualMedalsList = listOf(
    VirtualMedal(
        id = "medal_quiz_perfect",
        title = "Grand Master Laurels",
        description = "Awarded with high honors for answering 100% of KSSR Mega Quiz questions flawlessly!",
        iconEmoji = "🥇",
        criteria = "Score 5/5 on Quiz",
        shinyColor = Color(0xFFFCD116), // Gold Yellow
        glowColor = Color(0xFFFFD700),
        module = "quiz"
    ),
    VirtualMedal(
        id = "medal_skeletal_perfect",
        title = "Skeletal Medallion",
        description = "Awarded with honors for answering all skeletal decryption and function matching riddle questions 100% correctly!",
        iconEmoji = "🏅🦴",
        criteria = "Score 3/3 on Skeletal",
        shinyColor = Color(0xFFFFD700),
        glowColor = Color(0xFFE5C158),
        module = "skeletal"
    ),
    VirtualMedal(
        id = "medal_electricity_perfect",
        title = "Galvanic Masterwork",
        description = "Awarded for assembling a flawless current circuit and glowing the science lamp with standard electrical components!",
        iconEmoji = "🏅⚡",
        criteria = "Complete Current Flow",
        shinyColor = Color(0xFFFFD700),
        glowColor = Color(0xFFFFEB3B),
        module = "electricity"
    ),
    VirtualMedal(
        id = "medal_space_perfect",
        title = "Orbital Alignment",
        description = "Awarded for arranging the orbital distances of standard Year 5 celestial bodies perfectly matching nature's order!",
        iconEmoji = "🏅🪐",
        criteria = "Sequence Solar Planet Order",
        shinyColor = Color(0xFFFFD700),
        glowColor = Color(0xFFE040FB),
        module = "space"
    ),
    VirtualMedal(
        id = "medal_survival_perfect",
        title = "Adaptive Biologist",
        description = "Awarded for researching 100% of animal heat-preservation and drought-protection adaptive behaviors!",
        iconEmoji = "🏅🛡️",
        criteria = "Explore All 5 Animals",
        shinyColor = Color(0xFFFFD700),
        glowColor = Color(0xFF00E676),
        module = "survival"
    )
)

val kssrBadges = listOf(
    DigitalBadge(
        id = "module_pioneer",
        title = "Pioneer Adventurer",
        description = "Unlocked by completing any science module to begin your KSSR Year 5 voyage!",
        category = "Syllabus Explorer",
        iconEmoji = "🎒",
        criteria = "Complete at least 1 module",
        fact = "Science Year 5 introduces amazing concepts about energy, space, and life processes. Exploring is the first step to discovery!",
        color = Color(0xFF64B5F6) // Light Blue
    ),
    DigitalBadge(
        id = "circuit_master",
        title = "Circuit Surgeon",
        description = "Unlocked by successfully lighting up the lightbulb in 'The Power Station' electricity module.",
        category = "Electricity",
        iconEmoji = "⚡",
        criteria = "Complete Electricity Module",
        fact = "Did you know? Electric current can only flow in a complete circuit with a source, wires, a load (bulb), and a closed switch!",
        color = Color(0xFFFFD54F) // Amber Yellow
    ),
    DigitalBadge(
        id = "galactic_explorer",
        title = "Cosmic Cartographer",
        description = "Unlocked by arranging the solar system planets in their correct orbital sequence from the Sun.",
        category = "Solar System",
        iconEmoji = "🪐",
        criteria = "Complete Space Explorer Module",
        fact = "The 8 planets orbit the Sun at different distances. Year 5 students study how the orbital radius affects temperature and orbital periods!",
        color = Color(0xFFCE93D8) // Soft Purple
    ),
    DigitalBadge(
        id = "animal_guardian",
        title = "Wildlife Protector",
        description = "Unlocked by discovering all 5 unique animal adaptation secrets in the 'Survival Quest' module.",
        category = "Animal Survival",
        iconEmoji = "🛡️",
        criteria = "Complete Survival Quest Module",
        fact = "Animals have physical and behavioral adaptations to survive extreme cold or dry environments, such as polar bear blubber and camel humps!",
        color = Color(0xFF81C784) // Green
    ),
    DigitalBadge(
        id = "trivia_cadet",
        title = "Trivia Cadet",
        description = "Unlocked by completing the quiz with a passing score of 3 or more correct answers.",
        category = "Quiz Mastery",
        iconEmoji = "🧠",
        criteria = "Score 3+ pts on Quiz",
        fact = "Active testing and quiz-taking is proven to boost science memory retention and syllabus understanding by up to 50%!",
        color = Color(0xFF4DD0E1) // Cyan
    ),
    DigitalBadge(
        id = "perfect_genius",
        title = "Einstein Genius",
        description = "Earned for achieving a flawless 100% perfect score (5 out of 5) on the Mega Quiz Adventure!",
        category = "Quiz Perfection",
        iconEmoji = "👑",
        criteria = "Score 5/5 on Mega Quiz",
        fact = "Earning a perfect score means you've completely mastered the Year 5 KSSR concepts of Circuitry, Planetary Motion, and Animal Protection!",
        color = Color(0xFFFF8A65) // Deep Orange
    ),
    DigitalBadge(
        id = "social_scholar",
        title = "Leaderboard Challenger",
        description = "Earned by submitting your score and school name to the Year 5 Science global leaderboard.",
        category = "Global Arena",
        iconEmoji = "🏅",
        criteria = "Submit name on Leaderboard",
        fact = "Sharing achievements with peers fosters interactive learning and helps students progress together to achieve higher grades!",
        color = Color(0xFFFFB74D) // Bright Orange
    ),
    DigitalBadge(
        id = "top_three_legend",
        title = "Supreme Astral Legend",
        description = "The ultimate honor! Unlocked by securing a spot in the Top 3 ranks of the Year 5 Science Leaderboard.",
        category = "Elite Hall of Fame",
        iconEmoji = "🏆",
        criteria = "Reach Top 3 ranks on Leaderboard",
        fact = "Only the most diligent Year 5 science scholars reach the top 3! Keep revising to preserve your position!",
        color = Color(0xFFF06292) // Hot Pink
    ),
    DigitalBadge(
        id = "skeletal_expert",
        title = "Certified Osteologist",
        description = "Unlocked by completing the Human Skeletal System module and matching all bone organs with their functions.",
        category = "Human Skeletal System",
        iconEmoji = "🦴",
        criteria = "Complete Skeletal System Module",
        fact = "Our bones provide structural support, allow body movement, and act as standard protective armor for vital organs such as the brain, heart, and lungs!",
        color = Color(0xFFA1887F) // Wooden brown / Bone color
    )
)

fun getUnlockedBadgeIds(
    progressList: List<ModuleProgress>,
    leaderboardList: List<LeaderboardEntry>,
    submittedName: String
): Set<String> {
    val unlocked = mutableSetOf<String>()
    val completedCount = progressList.count { it.completed }
    
    // 1. Module Pioneer: any 1 module completed
    if (completedCount >= 1) {
        unlocked.add("module_pioneer")
    }
    
    // 2. Circuit Master: electricity completed
    val electricityCompleted = progressList.any { it.moduleId == "electricity" && it.completed }
    if (electricityCompleted) {
        unlocked.add("circuit_master")
    }
    
    // 3. Galactic Explorer: space completed
    val spaceCompleted = progressList.any { it.moduleId == "space" && it.completed }
    if (spaceCompleted) {
        unlocked.add("galactic_explorer")
    }
    
    // 4. Animal Guardian: survival completed
    val survivalCompleted = progressList.any { it.moduleId == "survival" && it.completed }
    if (survivalCompleted) {
        unlocked.add("animal_guardian")
    }

    // 4b. Bone Specialist: skeletal completed
    val skeletalCompleted = progressList.any { it.moduleId == "skeletal" && it.completed }
    if (skeletalCompleted) {
        unlocked.add("skeletal_expert")
    }
    
    // 5. Trivia Cadet: quiz completed with score >= 3
    val quizProgress = progressList.find { it.moduleId == "quiz" }
    if (quizProgress != null && quizProgress.completed && quizProgress.score >= 3) {
        unlocked.add("trivia_cadet")
    }
    
    // 6. Perfect Genius: quiz completed with score == 5
    if (quizProgress != null && quizProgress.completed && quizProgress.score == 5) {
        unlocked.add("perfect_genius")
    }
    
    // 7. Social Scholar: submitted score
    val hasSubmitted = submittedName.isNotBlank() && leaderboardList.any { it.name.trim() == submittedName.trim() }
    if (hasSubmitted) {
        unlocked.add("social_scholar")
    }
    
    // 8. Top 3 Legend: user is in the sorted Top 3 list on leaderboard
    val sortedLeaderboard = leaderboardList.sortedByDescending { it.score }
    val top3Names = sortedLeaderboard.take(3).map { it.name.trim() }
    if (submittedName.isNotBlank() && top3Names.contains(submittedName.trim())) {
        unlocked.add("top_three_legend")
    }
    
    return unlocked
}

// Custom data model for Planet Ordering game
data class SpaceObject(
    val id: String,
    val name: String,
    val color: Color,
    val info: String,
    val index: Int // Correct order from Sun (0: Sun, 1: Earth, 2: Mars, 3: Jupiter, 4: Saturn)
)

// Custom data model for Animal Adaptations
data class AnimalCard(
    val id: String,
    val name: String,
    val imageUrl: String, // Decorative emoji avatar
    val title: String,
    val trait: String,
    val kssrFact: String
)

class ScienceViewModel(
    private val repository: ProgressRepository,
    private val sharedPreferences: android.content.SharedPreferences? = null
) : ViewModel() {

    // --- Navigation ---
    var currentScreen by mutableStateOf(Screen.HOME)
        private set

    fun navigateTo(screen: Screen) {
        currentScreen = screen
        // Initialize or reset specific screen states when entering
        if (screen == Screen.ELECTRICITY) resetElectricityGame()
        if (screen == Screen.SPACE) resetSpaceGame()
        if (screen == Screen.SURVIVAL) resetSurvivalGame()
        if (screen == Screen.QUIZ) resetQuizGame()
    }

    // --- Database Progress ---
    val allProgress: StateFlow<List<ModuleProgress>> = repository.allProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Leaderboard Flow ---
    val leaderboardList: StateFlow<List<LeaderboardEntry>> = repository.allLeaderboard
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Profile & Badge Logic ---
    var profileRegisteredName by mutableStateOf(sharedPreferences?.getString("profile_name", "") ?: "")
        private set

    // --- Shop System Currency & Inventory ---
    var userCoins by mutableStateOf(sharedPreferences?.getInt("user_coins", 350) ?: 350)
        private set

    var hintsOwned by mutableStateOf(sharedPreferences?.getInt("hints_owned", 0) ?: 0)
        private set

    var boostsOwned by mutableStateOf(sharedPreferences?.getInt("boosts_owned", 0) ?: 0)
        private set

    var doublePointsOwned by mutableStateOf(sharedPreferences?.getInt("double_points_owned", 0) ?: 0)
        private set

    var isProActive by mutableStateOf(sharedPreferences?.getBoolean("is_pro_active", false) ?: false)
        private set

    var isTrialActive by mutableStateOf(sharedPreferences?.getBoolean("is_trial_active", false) ?: false)
        private set

    var subscriptionPlan by mutableStateOf(sharedPreferences?.getString("subscription_plan", "") ?: "")
        private set

    var announcementText by mutableStateOf(sharedPreferences?.getString("announcement_text", "") ?: "")
        private set

    var announcementActive by mutableStateOf(sharedPreferences?.getBoolean("announcement_active", false) ?: false)
        private set

    var userDismissedAnnouncement by mutableStateOf(false)

    var unlockedMedals by mutableStateOf(sharedPreferences?.getStringSet("unlocked_medals", emptySet()) ?: emptySet())
        private set

    var showMedalAwarded by mutableStateOf(false)
    var latestMedalEarned by mutableStateOf("")

    fun unlockMedal(medalId: String) {
        val updated = unlockedMedals.toMutableSet()
        if (updated.add(medalId)) {
            unlockedMedals = updated
            sharedPreferences?.edit()?.putStringSet("unlocked_medals", updated)?.apply()
            earnCoins(100) // Reward student with extra 100 coins for earning a virtual medal!
            latestMedalEarned = medalId
            showMedalAwarded = true
        }
    }

    fun dismissMedalAwarded() {
        showMedalAwarded = false
    }

    fun activateProTrial() {
        isProActive = true
        isTrialActive = true
        subscriptionPlan = "7-Day Free Trial"
        sharedPreferences?.edit()?.apply {
            putBoolean("is_pro_active", true)
            putBoolean("is_trial_active", true)
            putString("subscription_plan", "7-Day Free Trial")
            apply()
        }
        SoundPlayer.playCorrect()
    }

    fun activateProSubscription(plan: String) {
        isProActive = true
        isTrialActive = false
        subscriptionPlan = plan
        sharedPreferences?.edit()?.apply {
            putBoolean("is_pro_active", true)
            putBoolean("is_trial_active", false)
            putString("subscription_plan", plan)
            apply()
        }
        SoundPlayer.playCorrect()
    }

    fun cancelProActivation() {
        isProActive = false
        isTrialActive = false
        subscriptionPlan = ""
        sharedPreferences?.edit()?.apply {
            putBoolean("is_pro_active", false)
            putBoolean("is_trial_active", false)
            putString("subscription_plan", "")
            apply()
        }
    }

    var newlyUnlockedBadge by mutableStateOf<DigitalBadge?>(null)
    private var previouslyUnlockedBadgeIds = mutableSetOf<String>()

    init {
        // Collect progress and leaderboard flows to reactively watch badge unlocks
        viewModelScope.launch {
            allProgress.collect {
                checkBadgeUnlocks()
            }
        }
        viewModelScope.launch {
            leaderboardList.collect {
                checkBadgeUnlocks()
            }
        }

        // Initialize default modules if database is empty
        viewModelScope.launch {
            repository.allProgress.collect { list ->
                if (list.isEmpty()) {
                    val defaults = listOf(
                        ModuleProgress("electricity", "The Power Station", completed = false, score = 0, maxScore = 1, earnedStarBadge = false),
                        ModuleProgress("space", "Space Explorer", completed = false, score = 0, maxScore = 1, earnedStarBadge = false),
                        ModuleProgress("survival", "Survival Quest", completed = false, score = 0, maxScore = 1, earnedStarBadge = false),
                        ModuleProgress("skeletal", "Human Skeletal System", completed = false, score = 0, maxScore = 1, earnedStarBadge = false),
                        ModuleProgress("quiz", "Mega Quiz Adventure", completed = false, score = 0, maxScore = 5, earnedStarBadge = false)
                    )
                    for (mod in defaults) {
                        repository.saveProgress(mod)
                    }
                }
            }
        }

        // Initialize default KSSR leaderboard if database table is empty
        viewModelScope.launch {
            repository.allLeaderboard.collect { list ->
                if (list.isEmpty()) {
                    val defaultEntries = listOf(
                        LeaderboardEntry(name = "Siti Aminah (SK Permata)", score = 1500, dateString = "04/06/2026"),
                        LeaderboardEntry(name = "Ahmad Faiz (SJKC Sentul)", score = 1200, dateString = "04/06/2026"),
                        LeaderboardEntry(name = "Muthu Raj (SK Damansara)", score = 1000, dateString = "03/06/2026"),
                        LeaderboardEntry(name = "Zoe Tan (SK Cyberjaya)", score = 700, dateString = "02/06/2026"),
                        LeaderboardEntry(name = "Sarah Lin (SK Kiara)", score = 400, dateString = "01/06/2026")
                    )
                    for (entry in defaultEntries) {
                        repository.saveLeaderboardEntry(entry)
                    }
                }
            }
        }
    }

    fun saveProfileName(name: String) {
        profileRegisteredName = name
        sharedPreferences?.edit()?.putString("profile_name", name)?.apply()
        checkBadgeUnlocks()
    }

    fun checkBadgeUnlocks() {
        val progress = allProgress.value
        val leaderboard = leaderboardList.value
        val name = profileRegisteredName

        val currentUnlocked = getUnlockedBadgeIds(progress, leaderboard, name)
        val newUnlocks = currentUnlocked - previouslyUnlockedBadgeIds
        if (newUnlocks.isNotEmpty()) {
            // Find the first newly unlocked badge
            val firstNewId = newUnlocks.first()
            val badge = kssrBadges.find { it.id == firstNewId }
            if (badge != null && previouslyUnlockedBadgeIds.isNotEmpty()) {
                newlyUnlockedBadge = badge
            }
            previouslyUnlockedBadgeIds.addAll(currentUnlocked)
        }
    }

    fun dismissCelebration() {
        newlyUnlockedBadge = null
    }

    fun earnCoins(amount: Int) {
        userCoins += amount
        sharedPreferences?.edit()?.putInt("user_coins", userCoins)?.apply()
    }

    fun spendCoins(amount: Int): Boolean {
        return if (userCoins >= amount) {
            userCoins -= amount
            sharedPreferences?.edit()?.putInt("user_coins", userCoins)?.apply()
            true
        } else {
            false
        }
    }

    fun buyItem(itemId: String, cost: Int): Boolean {
        if (spendCoins(cost)) {
            when (itemId) {
                "hint" -> {
                    hintsOwned++
                    sharedPreferences?.edit()?.putInt("hints_owned", hintsOwned)?.apply()
                }
                "boost" -> {
                    boostsOwned++
                    sharedPreferences?.edit()?.putInt("boosts_owned", boostsOwned)?.apply()
                }
                "double_points" -> {
                    doublePointsOwned++
                    sharedPreferences?.edit()?.putInt("double_points_owned", doublePointsOwned)?.apply()
                }
            }
            return true
        }
        return false
    }

    fun useHint(): Boolean {
        if (hintsOwned > 0) {
            hintsOwned--
            sharedPreferences?.edit()?.putInt("hints_owned", hintsOwned)?.apply()
            return true
        }
        return false
    }

    fun useBoost(): Boolean {
        if (boostsOwned > 0) {
            boostsOwned--
            sharedPreferences?.edit()?.putInt("boosts_owned", boostsOwned)?.apply()
            return true
        }
        return false
    }

    fun useDoublePoints(): Boolean {
        if (doublePointsOwned > 0) {
            doublePointsOwned--
            sharedPreferences?.edit()?.putInt("double_points_owned", doublePointsOwned)?.apply()
            return true
        }
        return false
    }

    fun completeModule(moduleId: String) {
        viewModelScope.launch {
            // Check if already completed to avoid repetitive coin farming
            val progress = repository.allProgress.stateIn(viewModelScope).value
            val isAlreadyCompleted = progress.any { it.moduleId == moduleId && it.completed }
            
            repository.updateProgress(
                moduleId = moduleId,
                completed = true,
                score = 1,
                maxScore = 1,
                earnedStarBadge = true // Standard modules get a badge on completion!
            )
            
            if (!isAlreadyCompleted) {
                earnCoins(150) // Reward 150 Science Coins for a new milestone!
            } else {
                earnCoins(50) // Reward 50 coins for revisions!
            }
        }
    }

    fun completeQuiz(score: Int, maxScore: Int) {
        viewModelScope.launch {
            val perfect = score == maxScore
            repository.updateProgress(
                moduleId = "quiz",
                completed = true,
                score = score,
                maxScore = maxScore,
                earnedStarBadge = perfect // Rewards 3D star badge on perfect score
            )
            // Earn coins: 40 coins per correct answer!
            earnCoins(score * 40)

            if (perfect) {
                unlockMedal("medal_quiz_perfect")
            }
        }
    }

    fun addLeaderboardScore(name: String, score: Int) {
        viewModelScope.launch {
            repository.saveLeaderboardEntry(
                LeaderboardEntry(
                    name = name,
                    score = score,
                    dateString = "04/06/2026"
                )
            )
            saveProfileName(name)
        }
    }

    fun resetAllProgressDB() {
        viewModelScope.launch {
            saveProfileName("")
            previouslyUnlockedBadgeIds.clear()
            
            // Reset shop variables
            userCoins = 350
            hintsOwned = 0
            boostsOwned = 0
            doublePointsOwned = 0
            unlockedMedals = emptySet()
            sharedPreferences?.edit()?.apply {
                putInt("user_coins", 350)
                putInt("hints_owned", 0)
                putInt("boosts_owned", 0)
                putInt("double_points_owned", 0)
                putStringSet("unlocked_medals", emptySet())
                apply()
            }
            
            repository.resetProgress()
            val defaults = listOf(
                ModuleProgress("electricity", "The Power Station", completed = false, score = 0, maxScore = 1, earnedStarBadge = false),
                ModuleProgress("space", "Space Explorer", completed = false, score = 0, maxScore = 1, earnedStarBadge = false),
                ModuleProgress("survival", "Survival Quest", completed = false, score = 0, maxScore = 1, earnedStarBadge = false),
                ModuleProgress("skeletal", "Human Skeletal System", completed = false, score = 0, maxScore = 1, earnedStarBadge = false),
                ModuleProgress("quiz", "Mega Quiz Adventure", completed = false, score = 0, maxScore = 5, earnedStarBadge = false)
            )
            for (mod in defaults) {
                repository.saveProgress(mod)
            }
            
            // Reinitialize the default leaderboard entries as well
            repository.clearLeaderboard()
            val defaultEntries = listOf(
                LeaderboardEntry(name = "Siti Aminah (SK Permata)", score = 1500, dateString = "04/06/2026"),
                LeaderboardEntry(name = "Ahmad Faiz (SJKC Sentul)", score = 1200, dateString = "04/06/2026"),
                LeaderboardEntry(name = "Muthu Raj (SK Damansara)", score = 1000, dateString = "03/06/2026"),
                LeaderboardEntry(name = "Zoe Tan (SK Cyberjaya)", score = 700, dateString = "02/06/2026"),
                LeaderboardEntry(name = "Sarah Lin (SK Kiara)", score = 400, dateString = "01/06/2026")
            )
            for (entry in defaultEntries) {
                repository.saveLeaderboardEntry(entry)
            }
        }
    }

    // ==========================================
    // MODULE 1: THE POWER STATION (Electricity)
    // ==========================================
    var hasBattery by mutableStateOf(false)
    var hasWires by mutableStateOf(false)
    var hasSwitch by mutableStateOf(false)
    var hasBulb by mutableStateOf(false)
    var switchOn by mutableStateOf(false)

    val isCircuitComplete: Boolean
        get() = hasBattery && hasWires && hasSwitch && hasBulb

    val isLightGlowing: Boolean
        get() = isCircuitComplete && switchOn

    fun toggleComponent(component: String) {
        when (component) {
            "battery" -> hasBattery = !hasBattery
            "wires" -> hasWires = !hasWires
            "switch" -> hasSwitch = !hasSwitch
            "bulb" -> hasBulb = !hasBulb
        }
        checkElectricityCompletion()
    }

    fun toggleSwitch() {
        if (isCircuitComplete) {
            switchOn = !switchOn
        } else {
            switchOn = false
        }
        checkElectricityCompletion()
    }

    private fun checkElectricityCompletion() {
        if (isLightGlowing) {
            completeModule("electricity")
            unlockMedal("medal_electricity_perfect")
        }
    }

    fun resetElectricityGame() {
        hasBattery = false
        hasWires = false
        hasSwitch = false
        hasBulb = false
        switchOn = false
    }

    // ==========================================
    // MODULE 2: SPACE EXPLORER (Solar System)
    // ==========================================
    private val standardPlanets = listOf(
        SpaceObject("sun", "Sun", SolarYellow, "The massive center star of our Solar System.", 0),
        SpaceObject("earth", "Earth", Color(0xFF1E88E5), "Our home! The 3rd planet, with oxygen & liquid water.", 1),
        SpaceObject("mars", "Mars", Color(0xFFE53935), "The Red Planet! Covered in iron-rich rust dust.", 2),
        SpaceObject("jupiter", "Jupiter", Color(0xFFD84315), "The Gas Giant! Largest planet in the solar system.", 3),
        SpaceObject("saturn", "Saturn", Color(0xFFFFB300), "Dazzled by beautiful rings made of ice & rocks.", 4)
    )

    var currentPlanetsList by mutableStateOf<List<SpaceObject>>(emptyList())
        private set

    var selectedPlanetIndex by mutableStateOf<Int?>(null)
    var spaceGameSuccess by mutableStateOf(false)

    fun resetSpaceGame() {
        // Shuffle the planets, ensuring they don't accidentally start in the correct order
        var shuffled = standardPlanets.shuffled()
        while (isCorrectSpaceOrder(shuffled)) {
            shuffled = standardPlanets.shuffled()
        }
        currentPlanetsList = shuffled
        selectedPlanetIndex = null
        spaceGameSuccess = false
    }

    fun selectPlanetToSwap(index: Int) {
        if (spaceGameSuccess) return
        val currentSelected = selectedPlanetIndex
        if (currentSelected == null) {
            selectedPlanetIndex = index
        } else {
            // Swap them!
            val newList = currentPlanetsList.toMutableList()
            val temp = newList[currentSelected]
            newList[currentSelected] = newList[index]
            newList[index] = temp
            currentPlanetsList = newList
            selectedPlanetIndex = null

            // Check if correct
            if (isCorrectSpaceOrder(newList)) {
                spaceGameSuccess = true
                completeModule("space")
                unlockMedal("medal_space_perfect")
            }
        }
    }

    private fun isCorrectSpaceOrder(list: List<SpaceObject>): Boolean {
        if (list.size != standardPlanets.size) return false
        for (i in list.indices) {
            if (list[i].index != i) return false
        }
        return true
    }

    // ==========================================
    // MODULE 3: SURVIVAL QUEST (Adaptations)
    // ==========================================
    val standardAnimals = listOf(
        AnimalCard(
            id = "polar_bear",
            name = "Polar Bear",
            imageUrl = "🐻❄️",
            title = "Thick Fur & Blubber Layer",
            trait = "Structural (Keeps Warm & Camouflages)",
            kssrFact = "Lives in freezing Arctic. It has very thick translucent white fur that traps body heat, and black skin underneath to absorb sunlight. A thick layer of fat (blubber) keeps it warm!"
        ),
        AnimalCard(
            id = "camel",
            name = "Camel",
            imageUrl = "🐪🌵",
            title = "Fat-Storing Hump & Long Lashes",
            trait = "Structural & Physiological (Survives Desert Drought)",
            kssrFact = "Lives in hot, dry deserts. Its hump stores fat (not liquid water), which provides energy when food is scarce. It can drink 100 liters of water at once and goes weeks without drinking!"
        ),
        AnimalCard(
            id = "porcupine",
            name = "Porcupine",
            imageUrl = "🦔🛡️",
            title = "Sharp Defensive Quills",
            trait = "Structural (Deters Predators)",
            kssrFact = "When threatened, it raises its sharp, barbed quills and runs backward toward the threat! The quills detach easily, piercing the predator's skin and causing terrible pain."
        ),
        AnimalCard(
            id = "chameleon",
            name = "Chameleon",
            imageUrl = "🦎🎨",
            title = "Camouflage Skin & Sticky Tongue",
            trait = "Behavioral & Physiological (Blends and Hunts)",
            kssrFact = "Changes its skin color to mimic surrounding foliage to hide from larger birds and snakes. It has eyes that move independently and a super-fast tongue 2x its body length to catch insects."
        ),
        AnimalCard(
            id = "pangolin",
            name = "Pangolin",
            imageUrl = "🐾🛡️",
            title = "Hard Keratin Scales",
            trait = "Structural (Rolls into a Ball)",
            kssrFact = "Known as the scaly anteater. It has dense overlapping scales made of keratin (like fingernails). When a tiger or leopard attacks, the pangolin rolls into an impenetrable scale-ball!"
        )
    )

    var exploredAnimals by mutableStateOf<Set<String>>(emptySet())
        private set

    var activeAnimalPopup by mutableStateOf<AnimalCard?>(null)

    fun exploreAnimal(animal: AnimalCard) {
        val newSet = exploredAnimals.toMutableSet()
        newSet.add(animal.id)
        exploredAnimals = newSet
        activeAnimalPopup = animal

        // If child has read all 5 animal cards, complete the module!
        if (newSet.size == 5) {
            completeModule("survival")
            unlockMedal("medal_survival_perfect")
        }
    }

    fun closeAnimalPopup() {
        activeAnimalPopup = null
    }

    fun resetSurvivalGame() {
        exploredAnimals = emptySet()
        activeAnimalPopup = null
    }

    // ==========================================
    // MODULE 4: HUMAN SKELETAL SYSTEM
    // ==========================================
    data class BoneItem(
        val id: String,
        val name: String,
        val emoji: String,
        val function: String,
        val detail: String
    )

    val standardBones = listOf(
        BoneItem(
            id = "skull",
            name = "Skull (Tengkorak)",
            emoji = "💀",
            function = "Protects the brain from injuries.",
            detail = "The skull is a hard bone structure that acts as a natural helmet for our delicate brain, protecting it from external impacts and severe injuries."
        ),
        BoneItem(
            id = "ribcage",
            name = "Ribcage (Tulang rusuk)",
            emoji = "🫁",
            function = "Protects our internal organs.",
            detail = "The ribcage forms a protective cage around vital internal organs such as the heart and lungs, buffering them from accidental chest impacts."
        ),
        BoneItem(
            id = "backbone",
            name = "Backbone / Spine (Tulang belakang)",
            emoji = "🧗",
            function = "Provides main support and posture.",
            detail = "The backbone consists of multiple small vertebrae. It sustains the entire body structure, allowing us to stand vertically, bend, twist, and maintain proper posture."
        ),
        BoneItem(
            id = "limbs",
            name = "Hand & Leg Bones (Tulang kaki dan tangan)",
            emoji = "🦵",
            function = "Enables movement and supports body weight.",
            detail = "These strong bones work together with joints and skeletal muscles to enable running, grabbing, lifting, and support our total bodily weight."
        )
    )

    // Shuffled riddle sequence for the game
    val skeletalRiddles = listOf(
        QuizQuestion(
            question = "Which bone structure acts like a natural helmet to protect your soft, delicate brain?",
            options = listOf("Backbone / Spine", "Ribcage", "Skull", "Hand & Leg Bones"),
            correctIndex = 2,
            explanation = "Excellent! The Skull (Tengkorak) shields the brain from injuries."
        ),
        QuizQuestion(
            question = "Which bones protect your vital internal organs, such as the heart and lungs?",
            options = listOf("Ribcage", "Backbone", "Limbs", "Skull"),
            correctIndex = 0,
            explanation = "Spot on! The Ribcage forms a cage protecting the lungs and heart."
        ),
        QuizQuestion(
            question = "Which bone structure allows us to stand upright, bend, and twist our body?",
            options = listOf("Limbs", "Skull", "Backbone / Spine", "Ribcage"),
            correctIndex = 2,
            explanation = "Great job! The Backbone (Tulang belakang) sustains posture and supports the body."
        ),
        QuizQuestion(
            question = "Which bones work with joints and muscles to allow running, walking, and grabbing?",
            options = listOf("Skull", "Hand & Leg Bones (Limbs)", "Ribcage", "Backbone"),
            correctIndex = 1,
            explanation = "Fantastic! Hand & Leg Bones allow limbs to support weight and move."
        )
    )

    var exploredBones by mutableStateOf<Set<String>>(emptySet())
        private set

    var activeBonePopup by mutableStateOf<BoneItem?>(null)
    
    var activeSkeletalStage by mutableStateOf(0) // 0: Study, 1: Quiz Challenge, 2: Success Celebration
    var skeletalRiddleIndex by mutableStateOf(0)
    var skeletalRiddleAnswerIndex by mutableStateOf<Int?>(null)
    var isSkeletalRiddleSubmitted by mutableStateOf(false)
    var skeletalScore by mutableStateOf(0)

    fun exploreBone(boneId: String) {
        val newSet = exploredBones.toMutableSet()
        newSet.add(boneId)
        exploredBones = newSet
        activeBonePopup = standardBones.find { it.id == boneId }
        SoundPlayer.playCorrect() // Fun chime on exploring
    }

    fun closeBonePopup() {
        activeBonePopup = null
    }

    fun submitSkeletalRiddle(optionIndex: Int) {
        if (isSkeletalRiddleSubmitted) return
        skeletalRiddleAnswerIndex = optionIndex
        isSkeletalRiddleSubmitted = true
        val correctIdx = skeletalRiddles[skeletalRiddleIndex].correctIndex
        if (optionIndex == correctIdx) {
            skeletalScore++
            SoundPlayer.playCorrect()
        } else {
            SoundPlayer.playIncorrect()
        }
    }

    fun nextSkeletalRiddle() {
        skeletalRiddleAnswerIndex = null
        isSkeletalRiddleSubmitted = false
        if (skeletalRiddleIndex < skeletalRiddles.size - 1) {
            skeletalRiddleIndex++
        } else {
            activeSkeletalStage = 2
            completeModule("skeletal")
            if (skeletalScore == skeletalRiddles.size) {
                unlockMedal("medal_skeletal_perfect")
            }
        }
    }

    fun startSkeletalChallenge() {
        activeSkeletalStage = 1
        skeletalRiddleIndex = 0
        skeletalRiddleAnswerIndex = null
        isSkeletalRiddleSubmitted = false
        skeletalScore = 0
    }

    fun resetSkeletalGame() {
        exploredBones = emptySet()
        activeBonePopup = null
        activeSkeletalStage = 0
        skeletalRiddleIndex = 0
        skeletalRiddleAnswerIndex = null
        isSkeletalRiddleSubmitted = false
        skeletalScore = 0
    }

    // ==========================================
    // SECURE ADMIN AUTHORIZATION
    // ==========================================
    var adminAccessGranted by mutableStateOf(false)

    fun attemptAdminUnlock(password: String): Boolean {
        return if (password == "YBB6113") {
            adminAccessGranted = true
            SoundPlayer.playCorrect()
            true
        } else {
            SoundPlayer.playIncorrect()
            false
        }
    }

    fun lockAdminAccess() {
        adminAccessGranted = false
    }

    // ==========================================
    // INTERACTIVE QUIZ: MEGA QUIZ ADVENTURE
    // ==========================================
    data class QuizQuestion(
        val question: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String
    )

    val quizQuestions = listOf(
        QuizQuestion(
            question = "Which component is essential to provide electrical energy in a complete circuit?",
            options = listOf("A) Electric Switch", "B) Wires", "C) Dry Cell (Battery)", "D) Lightbulb"),
            correctIndex = 2,
            explanation = "Excellent! The dry cell (battery) is the source of electrical energy in a circuit."
        ),
        QuizQuestion(
            question = "If a switch in a complete circuit is 'OFF' (open), what happens to the electric current?",
            options = listOf("A) Current continues to flow", "B) Current stops flowing", "C) Bulb shines brighter", "D) Current flows backward"),
            correctIndex = 1,
            explanation = "Correct! An 'OFF' switch opens the circuit, breaking the path and preventing electric current from flowing."
        ),
        QuizQuestion(
            question = "Arrange these planets in the correct sequential order starting from the Sun:",
            options = listOf("A) Sun -> Mars -> Earth -> Saturn -> Jupiter", "B) Sun -> Earth -> Mars -> Jupiter -> Saturn", "C) Sun -> Earth -> Jupiter -> Mars -> Saturn", "D) Sun -> Earth -> Mars -> Saturn -> Jupiter"),
            correctIndex = 1,
            explanation = "Spot on! The correct sequential order is Sun -> Earth -> Mars -> Jupiter -> Saturn."
        ),
        QuizQuestion(
            question = "How does a polar bear adapt itself to stay alive in freezing temperatures?",
            options = listOf("A) It has camouflage skin", "B) It has water-storing humps", "C) It has sharp quills", "D) It has thick fur and a blubber layer"),
            correctIndex = 3,
            explanation = "Great job! A polar bear's thick fur traps warmth, and its thick fat layer (blubber) insulates it from Arctic cold."
        ),
        QuizQuestion(
            question = "What is the unique adaptation behavior of a Pangolin when it faces danger?",
            options = listOf("A) It sprays bad-smelling fluid", "B) It rolls into a tight sphere using its hard scales", "C) It ejects quills into the predator's eyes", "D) It blends with trees using dynamic camouflage"),
            correctIndex = 1,
            explanation = "Spot on! The pangolin rolls itself into an impenetrable ball of hard keratin scales, shielding its soft body."
        )
    )

    var currentQuestionIndex by mutableStateOf(0)
    var selectedAnswerIndex by mutableStateOf<Int?>(null)
    var isAnswerSubmitted by mutableStateOf(false)
    var quizScore by mutableStateOf(0)
    var isQuizCompleted by mutableStateOf(false)

    fun submitAnswer(optionIndex: Int) {
        if (isAnswerSubmitted) return
        selectedAnswerIndex = optionIndex
        isAnswerSubmitted = true
        if (optionIndex == quizQuestions[currentQuestionIndex].correctIndex) {
            quizScore++
            SoundPlayer.playCorrect()
        } else {
            SoundPlayer.playIncorrect()
        }
    }

    fun nextQuestion() {
        selectedAnswerIndex = null
        isAnswerSubmitted = false
        if (currentQuestionIndex < quizQuestions.size - 1) {
            currentQuestionIndex++
        } else {
            isQuizCompleted = true
            completeQuiz(quizScore, quizQuestions.size)
        }
    }

    fun resetQuizGame() {
        currentQuestionIndex = 0
        selectedAnswerIndex = null
        isAnswerSubmitted = false
        quizScore = 0
        isQuizCompleted = false
    }

    // --- Admin Control Panel Methods ---
    fun adminSetCoins(amount: Int) {
        userCoins = amount
        sharedPreferences?.edit()?.putInt("user_coins", userCoins)?.apply()
    }

    fun adminToggleModuleCompleted(moduleId: String, completed: Boolean) {
        viewModelScope.launch {
            val maxS = if (moduleId == "quiz") 5 else 1
            repository.updateProgress(
                moduleId = moduleId,
                completed = completed,
                score = if (completed) maxS else 0,
                maxScore = maxS,
                earnedStarBadge = completed
            )
            // Recalculate badge unlocks
            checkBadgeUnlocks()
        }
    }

    fun adminDeleteLeaderboardEntry(entry: LeaderboardEntry) {
        viewModelScope.launch {
            repository.deleteLeaderboardEntry(entry)
        }
    }

    fun adminUpdateAnnouncement(text: String, active: Boolean) {
        announcementText = text
        announcementActive = active
        userDismissedAnnouncement = false
        sharedPreferences?.edit()?.apply {
            putString("announcement_text", text)
            putBoolean("announcement_active", active)
        }?.apply()
    }

    fun dismissAnnouncement() {
        userDismissedAnnouncement = true
    }
}

// Singleton helper to play MIDI-synthesized buzzer tones for instant, direct correct/incorrect audio feedback on any device
object SoundPlayer {
    private var toneGen: ToneGenerator? = null

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 90)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playCorrect() {
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playIncorrect() {
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_NACK, 200)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// factory to instantiate ScienceViewModel
class ScienceViewModelFactory(
    private val repository: ProgressRepository,
    private val context: android.content.Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScienceViewModel::class.java)) {
            val prefs = context.getSharedPreferences("science_year5_prefs", android.content.Context.MODE_PRIVATE)
            @Suppress("UNCHECKED_CAST")
            return ScienceViewModel(repository, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
