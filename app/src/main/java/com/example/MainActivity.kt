package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KhanyisaHighTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppContent()
                }
            }
        }
    }
}

// ==========================================
// CENTRAL HERO SLIDER MODEL & VIEWS
// ==========================================
data class HeroSlide(
    val id: String,
    val kind: String, // "notice", "event", "post"
    val title: String,
    val subtitle: String,
    val image: String,
    val screenCode: String // "Home", "Schedule/Calendar", "Hub/Posts", "Alerts/Announcements", "Timetable"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroCarousel(
    slides: List<HeroSlide>,
    onSelectScreen: (String) -> Unit
) {
    if (slides.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { slides.size }
    )

    // Auto-scroll effect after every 4.5 seconds
    LaunchedEffect(pagerState, slides.size) {
        while (true) {
            delay(4500)
            if (slides.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % slides.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
        ) { page ->
            if (page < slides.size) {
                val slide = slides[page]
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onSelectScreen(slide.screenCode) },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(slide.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = slide.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Dark fade gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        // Badge / Badge Icon Column
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                val badgeColor = when (slide.kind) {
                                    "event" -> BrandGold
                                    "post" -> Color(0xFF38BDF8) // sky blue
                                    else -> Color.White
                                }
                                val badgeIcon = when (slide.kind) {
                                    "event" -> Icons.Default.DateRange
                                    "post" -> Icons.Default.ThumbUp
                                    else -> Icons.Default.Notifications
                                }
                                Icon(
                                    imageVector = badgeIcon,
                                    contentDescription = null,
                                    tint = badgeColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = slide.kind.uppercase(Locale.ROOT),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = badgeColor,
                                    letterSpacing = 1.sp
                                )
                            }

                            Text(
                                text = slide.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = slide.subtitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Dot indicator row
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(slides.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) BrandGold else Color.Gray.copy(alpha = 0.4f)
                val width = if (pagerState.currentPage == iteration) 16.dp else 6.dp
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .clip(CircleShape)
                        .background(color)
                        .width(width)
                        .height(6.dp)
                )
            }
        }
    }
}

// ==========================================
// CORE APP SCAFFOLDING WITH SIDEBAR & NAV
// ==========================================
@Composable
fun MainAppContent() {
    val app = LocalContext.current.applicationContext as SchoolApplication
    val factory = remember { SchoolViewModelFactory(app.repository) }
    val viewModel: SchoolViewModel = viewModel(factory = factory)

    val announcements by viewModel.announcements.collectAsState()
    val events by viewModel.events.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val timetables by viewModel.timetables.collectAsState()

    // Side Drawer & Tab States
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("Home") } // "Home", "Calendar", "Hub", "Alerts", "Timetables", "Admin"

    // Calculate unread notice count for badge logic isRead == false
    val unreadNoticesCount = remember(announcements) {
        announcements.count { !it.isRead }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(310.dp),
                drawerContainerColor = Color(0xFF0F172A),
                drawerContentColor = Color.White
            ) {
                // Header (Old Logo & Style)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A))
                                .border(2.dp, BrandGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(R.drawable.school_logo)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "School Logo",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Text(
                            text = "Downtown Lobby",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        )
                        Text(
                            text = "LET YOUR LIGHT SHINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BrandGold,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Item Links in Side Drawer
                val menuItems = listOf(
                    Triple("Home", "Lobby Portal", Icons.Default.Home),
                    Triple("Calendar", "Events Showcase", Icons.Default.DateRange),
                    Triple("Hub", "Campus News & Hub", Icons.Default.List),
                    Triple("Alerts", "School Bulletins", Icons.Default.Notifications),
                    Triple("Timetables", "Class Timetables", Icons.Default.Edit),
                    Triple("Admin", "Admin Control Deck", Icons.Default.Lock)
                )

                menuItems.forEach { (code, label, icon) ->
                    val isSelected = currentScreen == code
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF0F172A) else Color.White.copy(alpha = 0.7f)
                            )
                        },
                        label = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = if (isSelected) Color(0xFF0F172A) else Color.White
                                )

                                if (code == "Alerts" && unreadNoticesCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BrandGold)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = unreadNoticesCount.toString(),
                                            color = Color(0xFF0F172A),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        },
                        selected = isSelected,
                        onClick = {
                            currentScreen = code
                            coroutineScope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = BrandGold,
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .height(50.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                
                // Footer
                Text(
                    text = "Khanyisa High School • v1.0.0",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                // HIGH CONTRAST DOWNTOWN LOBBY TOP NAVIGATION BAR
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0F172A), // Majestic Slate Navy Primary
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .statusBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Drawer trigger hamburger button
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                },
                                modifier = Modifier.size(40.dp).testTag("drawer_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Drawer menu",
                                    tint = Color.White
                                )
                            }

                            // Logo Next to "Downtown Lobby"
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, BrandGold, CircleShape)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(R.drawable.school_logo)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Downtown Lobby logo",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Column {
                                Text(
                                    text = "Downtown Lobby",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "LET YOUR LIGHT SHINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BrandGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 8.sp,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }

                        // Notification bell icon with App read/unread Badge
                        IconButton(
                            onClick = {
                                currentScreen = "Alerts"
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .testTag("notification_bell_button")
                        ) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications Board",
                                    tint = BrandGold,
                                    modifier = Modifier.size(22.dp)
                                )

                                if (unreadNoticesCount > 0) {
                                    val pulseTransition = rememberInfiniteTransition(label = "pulse")
                                    val pulseScale by pulseTransition.animateFloat(
                                        initialValue = 0.95f,
                                        targetValue = 1.25f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1200, easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "scale"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 6.dp, y = (-6).dp)
                                            .size(16.dp)
                                            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(BrandGold, BrandGoldDark)
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (unreadNoticesCount > 9) "9+" else unreadNoticesCount.toString(),
                                            color = Color(0xFF0F172A),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    val tabs = listOf(
                        Triple("Home", "Lobby", Icons.Default.Home),
                        Triple("Calendar", "Events", Icons.Default.DateRange),
                        Triple("Hub", "News Hub", Icons.Default.List),
                        Triple("Alerts", "Notices", Icons.Default.Notifications)
                    )

                    tabs.forEach { (tabCode, tabLabel, tabIcon) ->
                        val isSelected = currentScreen == tabCode
                        NavigationBarItem(
                            selected = isSelected,
                            label = {
                                Text(
                                    text = tabLabel,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    color = if (isSelected) BrandGold else Color.White.copy(alpha = 0.6f)
                                )
                            },
                            icon = {
                                Box {
                                    Icon(
                                        imageVector = tabIcon,
                                        contentDescription = tabLabel,
                                        tint = if (isSelected) BrandGold else Color.White.copy(alpha = 0.6f)
                                    )
                                    if (tabCode == "Alerts" && unreadNoticesCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = 8.dp, y = (-4).dp)
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(BrandGold)
                                        )
                                    }
                                }
                            },
                            onClick = { currentScreen = tabCode },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // React-style page navigation animations
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "Core_tab_switch_anim"
                ) { screen ->
                    when (screen) {
                        "Home" -> HomeScreen(
                            viewModel = viewModel,
                            announcements = announcements,
                            events = events,
                            onSwitchPage = { currentScreen = it }
                        )
                        "Calendar" -> CalendarScreen(
                            events = events,
                            viewModel = viewModel
                        )
                        "Hub" -> HubScreen(
                            posts = posts,
                            viewModel = viewModel
                        )
                        "Alerts" -> AlertsScreen(
                            announcements = announcements,
                            viewModel = viewModel
                        )
                        "Timetables" -> TimetablesScreen(
                            timetables = timetables,
                            viewModel = viewModel
                        )
                        "Admin" -> AdminScreen(
                            viewModel = viewModel,
                            announcements = announcements,
                            events = events,
                            posts = posts,
                            timetables = timetables
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 1. HOME SCREEN (LOBBY PORTAL WITH SA TERM PROGRESS & HERO CAROUSEL)
// ==========================================
@Composable
fun HomeScreen(
    viewModel: SchoolViewModel,
    announcements: List<Announcement>,
    events: List<Event>,
    onSwitchPage: (String) -> Unit
) {
    val posts by viewModel.posts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Welcome student greeting card below the top bar
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)) {
                h2Decoration("Hello Student! 👋")
                Text(
                    text = "Welcome back to Khanyisa High • Let Your Light Shine",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // South African school term progress bar block
        item {
            SATermProgressCard()
        }

        // Auto-Sliding Hero Carousel
        item {
            val slides = remember(announcements, events, posts) {
                val list = mutableListOf<HeroSlide>()

                // Show announcements
                announcements.take(2).forEach {
                    list.add(
                        HeroSlide(
                            id = "ann-${it.id}",
                            kind = "notice",
                            title = it.title,
                            subtitle = it.content,
                            image = "file:///android_asset/supabase/unnamed.jpg",
                            screenCode = "Alerts"
                        )
                    )
                }

                // Show upcoming events
                events.take(3).forEach { ev ->
                    val imgs = ev.imageUrls.split(",")
                    val displayImage = imgs.firstOrNull()?.trim() ?: "file:///android_asset/supabase/6.jpg"

                    list.add(
                        HeroSlide(
                            id = "ev-${ev.id}",
                            kind = "event",
                            title = ev.title,
                            subtitle = ev.description,
                            image = displayImage,
                            screenCode = "Calendar"
                        )
                    )
                }

                // Show posts
                posts.take(3).forEach { po ->
                    val imgs = po.imageUrls.split(",")
                    val displayImage = imgs.firstOrNull()?.trim() ?: "file:///android_asset/supabase/images.jpeg"

                    list.add(
                        HeroSlide(
                            id = "po-${po.id}",
                            kind = "post",
                            title = po.title,
                            subtitle = po.content,
                            image = displayImage,
                            screenCode = "Hub"
                        )
                    )
                }

                if (list.isEmpty()) {
                    list.add(
                        HeroSlide(
                            id = "fallback",
                            kind = "notice",
                            title = "Welcome to Downtown Lobby",
                            subtitle = "Keep shining and tracking your school progress every day.",
                            image = "file:///android_asset/supabase/unnamed_1.jpg",
                            screenCode = "Home"
                        )
                    )
                }

                list.take(8)
            }

            HeroCarousel(slides = slides, onSelectScreen = onSwitchPage)
        }

        // THREE QUICK ACCESS GRID-ROW BUTTONS (EXACTLY AS RECONSTRUCTED FROM React `Index.tsx`)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "QUICK ACCESS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Black.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.8.sp
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Button 1: Events
                    QuickAccessCard(
                        title = "Events",
                        icon = Icons.Default.DateRange,
                        isPrimaryColor = false,
                        modifier = Modifier.weight(1f),
                        onClick = { onSwitchPage("Calendar") }
                    )

                    // Button 2: News & Posts (Gold Highlight)
                    QuickAccessCard(
                        title = "News & Posts",
                        icon = Icons.Default.List,
                        isPrimaryColor = true,
                        modifier = Modifier.weight(1f),
                        onClick = { onSwitchPage("Hub") }
                    )

                    // Button 3: Timetable
                    QuickAccessCard(
                        title = "Timetable",
                        icon = Icons.Default.Edit,
                        isPrimaryColor = false,
                        modifier = Modifier.weight(1f),
                        onClick = { onSwitchPage("Timetables") }
                    )
                }
            }
        }

        // DETAILED UPCOMING EVENTS BRIEF LIST
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UPCOMING EVENTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Black.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.8.sp
                        )
                    )

                    TextButton(onClick = { onSwitchPage("Calendar") }) {
                        Text(
                            text = "View All",
                            color = BrandGoldDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (events.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No events scheduled for the current term.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        events.take(3).forEach { event ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .clickable { onSwitchPage("Calendar") }
                                    .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Date Badge Component
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(BrandGold, BrandGoldDark)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        val day = try {
                                            event.eventDate.split(" ")[0].split("-")[2]
                                        } catch (e: Exception) {
                                            "12"
                                        }
                                        val month = try {
                                            val parts = event.eventDate.split(" ")[0].split("-")
                                            val cal = Calendar.getInstance()
                                            cal.set(Calendar.MONTH, parts[1].toInt() - 1)
                                            SimpleDateFormat("MMM", Locale.US).format(cal.time).uppercase(Locale.ROOT)
                                        } catch (e: Exception) {
                                            "JUN"
                                        }

                                        Text(
                                            text = month,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = day,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF0F172A),
                                            lineHeight = 20.sp
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = event.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = event.description,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Gray,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (event.location.isNotEmpty()) {
                                        Text(
                                            text = "📍 ${event.location}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandGoldDark,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// REUSABLE QUICK ACCESS CARD COMPONENT
// ==========================================
@Composable
fun QuickAccessCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPrimaryColor: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() }
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Background
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isPrimaryColor) {
                            Brush.linearGradient(colors = listOf(BrandGold, BrandGoldDark))
                        } else {
                            Brush.linearGradient(colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPrimaryColor) Color(0xFF0F172A) else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 11.dp.value.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = Color(0xFF1E293B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==========================================
// SOUTH AFRICA TERM PROGRESS CARD
// ==========================================
@Composable
fun SATermProgressCard() {
    var isAutomatic by remember { mutableStateOf(true) }
    
    // Live automatic calculations with year-correction fallback for 2026 term tables
    val today = remember {
        val actualToday = Date()
        val calendar = java.util.Calendar.getInstance()
        calendar.time = actualToday
        if (calendar.get(java.util.Calendar.YEAR) != 2026) {
            calendar.set(java.util.Calendar.YEAR, 2026)
            calendar.time
        } else {
            actualToday
        }
    }
    val autoTerm = remember(today) { SchoolCalendarHelper.getActiveTermOrUpcoming(today) }
    val autoProgressInfo = remember(autoTerm, today) { SchoolCalendarHelper.calculateProgressForDate(autoTerm, today) }

    // Manual simulator state
    var selectedManualTermIndex by remember { mutableStateOf(1) } // Default to Term 2 (index 1)
    var manualProgressPercent by remember { mutableStateOf(0.63f) } 

    val selectedManualTerm = SchoolCalendarHelper.SA_TERMS_2026[selectedManualTermIndex]
    
    // Calculate simulated date based on term start, end, and slider percentage
    val simulatedProgressInfo = remember(selectedManualTerm, manualProgressPercent) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val start = sdf.parse(selectedManualTerm.startDate) ?: Date()
        val end = sdf.parse(selectedManualTerm.endDate) ?: Date()
        val duration = end.time - start.time
        val simulatedTime = start.time + (duration * manualProgressPercent).toLong()
        val simulatedDate = Date(simulatedTime)
        SchoolCalendarHelper.calculateProgressForDate(selectedManualTerm, simulatedDate)
    }

    val finalProgressInfo = if (isAutomatic) autoProgressInfo else simulatedProgressInfo

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .testTag("sa_term_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with mode switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SOUTH AFRICA SCHOOL TERMS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = BrandGold,
                    letterSpacing = 1.5.sp
                )
                
                // Mode selector buttons
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isAutomatic) BrandGold else Color.Transparent)
                            .clickable { isAutomatic = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Auto Sync",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAutomatic) Color(0xFF0F172A) else Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isAutomatic) BrandGold else Color.Transparent)
                            .clickable { isAutomatic = false }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Manual",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isAutomatic) Color(0xFF0F172A) else Color.White
                        )
                    }
                }
            }

            // Core display: Title, Date & Progress Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = finalProgressInfo.label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        // Live indicator badge
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isAutomatic) Color(0xFF10B981) else Color(0xFFF59E0B))
                        )
                        Text(
                            text = if (isAutomatic) "Live Date: ${finalProgressInfo.displayDate}" else "Simulated Date: ${finalProgressInfo.displayDate}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = "${(finalProgressInfo.progressPercentage * 100).toInt()}%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            // Premium thicker tactile progress indicator with glow and gold horizontal gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = finalProgressInfo.progressPercentage)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    BrandGoldDark,
                                    BrandGold,
                                    BrandGoldLight
                                )
                            )
                        )
                )
            }

            // Dynamic simulator controls
            AnimatedVisibility(
                visible = !isAutomatic,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🛠️ Calendar Simulator Panel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGoldLight
                    )
                    
                    // Term Selection Filter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SchoolCalendarHelper.SA_TERMS_2026.forEachIndexed { index, term ->
                            val isSelected = selectedManualTermIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BrandGold else Color.White.copy(alpha = 0.1f))
                                    .clickable { selectedManualTermIndex = index }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Term ${term.termNumber}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) Color(0xFF0F172A) else Color.White
                                )
                            }
                        }
                    }

                    // Progress Slider controls
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Elapsed: ${finalProgressInfo.daysElapsed} of ${finalProgressInfo.totalDays} academic days", fontSize = 10.sp, color = Color.LightGray)
                            Text("Slipped: ${(manualProgressPercent * 100).toInt()}%", fontSize = 10.sp, color = BrandGold)
                        }
                        Slider(
                            value = manualProgressPercent,
                            onValueChange = { manualProgressPercent = it },
                            colors = SliderDefaults.colors(
                                thumbColor = BrandGold,
                                activeTrackColor = BrandGold,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.height(28.dp).testTag("term_progress_slider")
                        )
                    }
                }
            }

            // Footer info label showing end dates
            Text(
                text = "School Term ${finalProgressInfo.termNumber} ends on ${finalProgressInfo.endDateFormatted}. Keep doing your best!",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

// ==========================================
// 2. CALENDAR SCREEN (RECONSTRUCTED WITH POPUP & SLIDERS)
// ==========================================
@Composable
fun CalendarScreen(
    events: List<Event>,
    viewModel: SchoolViewModel
) {
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            h2Decoration("Events Calendar")
            Text(
                text = "Discover sports matches, tech exhibits, and music nights taking place in Khanyisa.",
                fontSize = 12.sp,
                color = Color.Gray
            )

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming events listed at this time.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(events.size) { index ->
                        val event = events[index]
                        val imgs = event.imageUrls.split(",")
                        val displayImage = imgs.firstOrNull()?.trim() ?: "file:///android_asset/supabase/6.jpg"

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEvent = event }
                                .shadow(2.dp, RoundedCornerShape(20.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(displayImage)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = event.title,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = event.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "📅 ${event.eventDate}",
                                        fontSize = 11.sp,
                                        color = BrandGoldDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = event.description,
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Details dialog popup modal
        selectedEvent?.let { event ->
            AlertDialog(
                onDismissRequest = { selectedEvent = null },
                confirmButton = {
                    TextButton(onClick = { selectedEvent = null }) {
                        Text("Close", fontWeight = FontWeight.Bold, color = BrandGoldDark)
                    }
                },
                title = {
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        // Multi-image slider showing inside the details popup
                        val imgs = event.imageUrls.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        if (imgs.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                imgs.forEach { img ->
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(img)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Event Snapshot",
                                        modifier = Modifier
                                            .width(220.dp)
                                            .height(130.dp)
                                            .clip(RoundedCornerShape(14.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        Text(
                            text = "📅 Date & Time: ${event.eventDate}",
                            fontWeight = FontWeight.Bold,
                            color = BrandGoldDark,
                            fontSize = 12.sp
                        )

                        if (event.location.isNotEmpty()) {
                            Text(
                                text = "📍 Venue: ${event.location}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = event.description,
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            lineHeight = 18.sp
                        )
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White
            )
        }
    }
}

// ==========================================
// 3. NEWS FEED HUB SCREEN (RECONSTRUCTED WITH LIKES & COMMENTS)
// ==========================================
@Composable
fun HubScreen(
    posts: List<Post>,
    viewModel: SchoolViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        h2Decoration("Campus Hub Feed")
        Text(
            text = "Stay interconnected with recent matric developments, welfare food drives, and pupil initiatives.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (posts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.LightGray
                    )
                    Text(
                        text = "No campus news posts in the current stream.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(posts.size) { idx ->
                    val post = posts[idx]
                    val likes by viewModel.getLikes(post.id).collectAsState(initial = emptyList())

                    val likeCount = likes.count { it.reactionType == "like" }
                    val heartCount = likes.count { it.reactionType == "heart" }
                    val hasLiked = likes.any { it.clientId == viewModel.clientId && it.reactionType == "like" }
                    val hasHearted = likes.any { it.clientId == viewModel.clientId && it.reactionType == "heart" }

                    Card(
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(22.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0F172A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = BrandGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = when (post.category.lowercase(Locale.ROOT)) {
                                                "community" -> "Khanyisa Community Welfare"
                                                "social" -> "Student Governance Council"
                                                "academic" -> "Administrative Secretariat"
                                                else -> "Khanyisa High Campus News"
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "Verified Publisher • 2026",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFFFAF0))
                                        .border(1.dp, BrandGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = post.category.uppercase(Locale.ROOT),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrandGoldDark,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Text(
                                text = post.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A),
                                lineHeight = 22.sp
                            )

                            val imgs = post.imageUrls.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            if (imgs.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    imgs.forEach { img ->
                                        Card(
                                            shape = RoundedCornerShape(16.dp),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            modifier = Modifier.width(300.dp).height(160.dp)
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(img)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Post Photo",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = post.content,
                                fontSize = 13.sp,
                                color = Color(0xFF334155),
                                lineHeight = 19.sp
                            )

                            Divider(color = Color(0xFFEDF2F7), thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (hasLiked) Color(0xFFE3F2FD) else Color(0xFFF1F5F9))
                                        .clickable { viewModel.toggleReaction(post.id, "like") }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Like Post",
                                        tint = if (hasLiked) Color(0xFF1976D2) else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (likeCount > 0) "$likeCount Likes" else "Like",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasLiked) Color(0xFF1565C0) else Color.DarkGray
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (hasHearted) Color(0xFFFFEBEE) else Color(0xFFF1F5F9))
                                        .clickable { viewModel.toggleReaction(post.id, "heart") }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Love Post",
                                        tint = if (hasHearted) Color(0xFFE91E63) else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (heartCount > 0) "$heartCount Hearts" else "Love",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasHearted) Color(0xFFC2185B) else Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. NOTICE BOARD (ALERTS SCREEN WITH BADGES)
// ==========================================
@Composable
fun AlertsScreen(
    announcements: List<Announcement>,
    viewModel: SchoolViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        h2Decoration("Notice Board")
        Text(
            text = "Official administrative circulars, welfare dress policy adjustments, and announcements.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        // Mark all as read action button
        if (announcements.any { !it.isRead }) {
            Button(
                onClick = {
                    announcements.forEach {
                        viewModel.addAnnouncement(it.title, it.content, it.category) // replaces with updated read status
                        viewModel.deleteAnnouncement(it)
                    }
                    Toast.makeText(context, "All alerts marked as read locally", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Mark all as Read", color = Color(0xFF0F172A), fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
        }

        if (announcements.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Bulletins or notices listed right now.",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(announcements.size) { idx ->
                    val alert = announcements[idx]
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (alert.isRead) Color.White else Color(0xFFFEF3C7).copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF0F172A))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = alert.category.uppercase(Locale.ROOT),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandGold
                                        )
                                    }

                                    if (!alert.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color.Red)
                                        )
                                    }
                                }

                                Text(
                                    text = alert.createdAt,
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Text(
                                text = alert.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            Text(
                                text = alert.content,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. TIMETABLES SCREEN (RECONSTRUCTED GRADE PICKER STYLE)
// ==========================================
@Composable
fun TimetablesScreen(
    timetables: List<Timetable>,
    viewModel: SchoolViewModel
) {
    val grades = listOf("8A", "8B", "8C", "11B", "12A")
    var selectedGrade by remember { mutableStateOf<String?>("12A") }
    var selectedType by remember { mutableStateOf("weekly") } // "weekly" or "exam"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        h2Decoration("Class Timetables")
        Text(
            text = "Pick your registered class grade to visualize class schedules or upcoming midterm exam logs.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        // Grade slider picker row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            grades.forEach { grade ->
                val isSelected = selectedGrade == grade
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) BrandGold else Color.White)
                        .clickable { selectedGrade = grade }
                        .border(1.dp, if (isSelected) BrandGoldDark else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = grade,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF0F172A) else Color(0xFF475569)
                    )
                }
            }
        }

        // Toggle Buttons Weekly vs Exam
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val weeklySelected = selectedType == "weekly"
            Button(
                onClick = { selectedType = "weekly" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (weeklySelected) Color(0xFF0F172A) else Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text(
                    text = "Weekly Schedule",
                    color = if (weeklySelected) Color.White else Color(0xFF475569),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = { selectedType = "exam" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!weeklySelected) Color(0xFF0F172A) else Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text(
                    text = "Exam Timetable",
                    color = if (!weeklySelected) Color.White else Color(0xFF475569),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Filter and display matching schedules
        val filtered = timetables.filter {
            it.type == selectedType && (selectedGrade == null || it.grade == selectedGrade)
        }

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No $selectedType timetables uploaded for Class $selectedGrade yet.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filtered.size) { idx ->
                    val tt = filtered[idx]
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            // Info Banner
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CLASS ${tt.grade} • ${tt.type.uppercase(Locale.ROOT)}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrandGoldDark
                                )

                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share schedule",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = tt.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )

                                // Actual Schedule Image asset
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(tt.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = tt.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. ADMIN SCREEN (ADMIN CONTROLLER PANEL WITH DATABASE POPULATE FORMS)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: SchoolViewModel,
    announcements: List<Announcement>,
    events: List<Event>,
    posts: List<Post>,
    timetables: List<Timetable>
) {
    val context = LocalContext.current
    
    // Auth state helper (persistent during session / until lock)
    var isAuth by remember { mutableStateOf(false) }
    var pinCodeInput by remember { mutableStateOf("") }
    val correctPin = "1234"
    
    if (!isAuth) {
        // GORGEOUS PASSCODE SCREEN FOR SECURE STAFF PASS
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF7ED)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = BrandGoldDark,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Text(
                        text = "Khanyisa Staff Desk",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A)
                        )
                    )
                    
                    Text(
                        text = "Enter secure admin PIN to unlock content moderation and schedule publishing controls.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    
                    OutlinedTextField(
                        value = pinCodeInput,
                        onValueChange = { 
                            if (it.length <= 4) pinCodeInput = it
                            if (it == correctPin) {
                                isAuth = true
                                Toast.makeText(context, "Welcome, Administrator!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        label = { Text("Enter 4-Digit PIN") },
                        placeholder = { Text("e.g. 1234") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("pin_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGoldDark,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = BrandGoldDark)
                        }
                    )
                    
                    Text(
                        text = "Hint: Default staff access code is 1234",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGoldDark,
                        textAlign = TextAlign.Center
                    )
                    
                    // Simple interactive PIN dialer keypad
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        val keys = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("C", "0", "Unlock")
                        )
                        keys.forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                row.forEach { key ->
                                    Button(
                                        onClick = {
                                            when (key) {
                                                "C" -> pinCodeInput = ""
                                                "Unlock" -> {
                                                    if (pinCodeInput == correctPin) {
                                                        isAuth = true
                                                        Toast.makeText(context, "Welcome, Administrator!", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "Invalid PIN code. Try again.", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                else -> {
                                                    if (pinCodeInput.length < 4) pinCodeInput += key
                                                    if (pinCodeInput == correctPin) {
                                                        isAuth = true
                                                        Toast.makeText(context, "Welcome, Administrator!", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (key == "Unlock") Color(0xFF0F172A) else Color(0xFFF1F5F9),
                                            contentColor = if (key == "Unlock") Color.White else Color(0xFF1E293B)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(key, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // MAIN ADMIN CONTROLS SCREEN
        var adminTab by remember { mutableStateOf("Notices") } // "Notices", "Events", "News", "Schedules"

        // Editing state holders
        var editingAnnouncement by remember { mutableStateOf<Announcement?>(null) }
        var editingPost by remember { mutableStateOf<Post?>(null) }
        var editingTimetable by remember { mutableStateOf<Timetable?>(null) }
        var editingEvent by remember { mutableStateOf<Event?>(null) }

        // Form inputs
        var text1 by remember { mutableStateOf("") } // title
        var text2 by remember { mutableStateOf("") } // description / body
        var text3 by remember { mutableStateOf("general") } // category / type
        var text4 by remember { mutableStateOf("12A") } // venue / grade
        
        // Multiple attachment state
        var attachImageUrlInput by remember { mutableStateOf("") }
        var attachedImagesList by remember { mutableStateOf<List<String>>(emptyList()) }

        // Synchronize fields when starting an edit
        LaunchedEffect(editingAnnouncement, editingPost, editingTimetable, editingEvent, adminTab) {
            when (adminTab) {
                "Notices" -> {
                    editingAnnouncement?.let {
                        text1 = it.title
                        text2 = it.content
                        text3 = it.category
                    } ?: run {
                        text1 = ""; text2 = ""; text3 = "general"; text4 = ""
                    }
                }
                "Events" -> {
                    editingEvent?.let {
                        text1 = it.title
                        text2 = it.description
                        text3 = it.eventDate
                        text4 = it.location
                        attachedImagesList = it.imageUrls.split(",").map { img -> img.trim() }.filter { img -> img.isNotEmpty() }
                    } ?: run {
                        text1 = ""; text2 = ""; text3 = "2026-06-15 10:00"; text4 = "School Hall"
                        attachedImagesList = emptyList()
                    }
                }
                "News" -> {
                    editingPost?.let {
                        text1 = it.title
                        text2 = it.content
                        text3 = it.category
                        attachedImagesList = it.imageUrls.split(",").map { img -> img.trim() }.filter { img -> img.isNotEmpty() }
                    } ?: run {
                        text1 = ""; text2 = ""; text3 = "Social"; text4 = ""
                        attachedImagesList = emptyList()
                    }
                }
                "Schedules" -> {
                    editingTimetable?.let {
                        text1 = it.title
                        text3 = it.type // "weekly" or "exam"
                        text4 = it.grade // "12A", etc
                    } ?: run {
                        text1 = ""; text2 = ""; text3 = "exam"; text4 = "12A"
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F9)),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    h2Decoration("Staff Desk")
                    Button(
                        onClick = { isAuth = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", tint = Color.White, modifier = Modifier.size(14.dp))
                            Text("Lock Keypad", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text(
                    text = "Publish notices, manage community news with multiple image grids, and update real-time exam schedules.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Subtabs row
                val tabs = listOf("Notices", "Events", "News", "Schedules")
                TabRow(
                    selectedTabIndex = tabs.indexOf(adminTab),
                    containerColor = Color.White,
                    contentColor = BrandGoldDark,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp)).border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                ) {
                    tabs.forEach { tab ->
                        Tab(
                            selected = adminTab == tab,
                            onClick = {
                                adminTab = tab
                                // Reset editing states on tab switch
                                editingAnnouncement = null
                                editingPost = null
                                editingTimetable = null
                                editingEvent = null
                                text1 = ""; text2 = ""; text3 = ""; text4 = ""
                                attachedImagesList = emptyList()
                            },
                            text = { Text(tab, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Input module
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        val isEditingMode = (editingAnnouncement != null || editingPost != null || editingTimetable != null || editingEvent != null)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEditingMode) "✏️ Edit $adminTab Entry" else "➕ Add New $adminTab",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            if (isEditingMode) {
                                TextButton(
                                    onClick = {
                                        editingAnnouncement = null
                                        editingPost = null
                                        editingTimetable = null
                                        editingEvent = null
                                        text1 = ""; text2 = ""; text3 = ""; text4 = ""
                                        attachedImagesList = emptyList()
                                    }
                                ) {
                                    Text("Cancel Edit", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        when (adminTab) {
                            "Notices" -> {
                                OutlinedTextField(
                                    value = text1, onValueChange = { text1 = it },
                                    label = { Text("Notice Title") }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = text2, onValueChange = { text2 = it },
                                    label = { Text("Detailed Bulletin Message") }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    minLines = 3
                                )
                                
                                Text("Choose Bulletin Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val cats = listOf("general", "academics", "sports", "community")
                                    cats.forEach { cat ->
                                        val isSelected = text3.lowercase() == cat
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { text3 = cat },
                                            label = { Text(cat.replaceFirstChar { it.uppercase() }) }
                                        )
                                    }
                                }
                            }
                            "Events" -> {
                                OutlinedTextField(
                                    value = text1, onValueChange = { text1 = it },
                                    label = { Text("Event Name") }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = text2, onValueChange = { text2 = it },
                                    label = { Text("Event Description") }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    minLines = 2
                                )
                                OutlinedTextField(
                                    value = text3, onValueChange = { text3 = it },
                                    label = { Text("Date (e.g., 2026-06-15 10:00)") }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = text4, onValueChange = { text4 = it },
                                    label = { Text("Venue Location") }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                MediaAttachmentManagerSection(
                                    attachedImagesList = attachedImagesList,
                                    onImagesListChange = { attachedImagesList = it },
                                    attachImageUrlInput = attachImageUrlInput,
                                    onUrlInputChange = { attachImageUrlInput = it }
                                )
                            }
                            "News" -> {
                                OutlinedTextField(
                                    value = text1, onValueChange = { text1 = it },
                                    label = { Text("News Headline") }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = text2, onValueChange = { text2 = it },
                                    label = { Text("Full News Article Content") }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    minLines = 3
                                )
                                
                                Text("Choose Feed Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val cats = listOf("Social", "Community", "Academic")
                                    cats.forEach { cat ->
                                        val isSelected = text3 == cat
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { text3 = cat },
                                            label = { Text(cat) }
                                        )
                                    }
                                }
                                MediaAttachmentManagerSection(
                                    attachedImagesList = attachedImagesList,
                                    onImagesListChange = { attachedImagesList = it },
                                    attachImageUrlInput = attachImageUrlInput,
                                    onUrlInputChange = { attachImageUrlInput = it }
                                )
                            }
                            "Schedules" -> {
                                OutlinedTextField(
                                    value = text1, onValueChange = { text1 = it },
                                    label = { Text("Schedule Action Title") }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                
                                Text("Class Grade target:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val grades = listOf("12A", "11B", "8A", "8B", "8C")
                                    grades.forEach { grade ->
                                        val isSelected = text4 == grade
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { text4 = grade },
                                            label = { Text(grade) }
                                        )
                                    }
                                }

                                Text("Schedule Type (weekly or exam):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val tps = listOf("weekly", "exam")
                                    tps.forEach { tp ->
                                        val isSelected = text3.lowercase() == tp
                                        Button(
                                            onClick = { text3 = tp },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                                                contentColor = if (isSelected) Color.White else Color(0xFF1E293B)
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(tp.uppercase(Locale.ROOT), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (text1.blankValid()) {
                                    Toast.makeText(context, "Please enter a valid title/headline", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                when (adminTab) {
                                    "Notices" -> {
                                        viewModel.addAnnouncement(
                                            title = text1,
                                            content = text2.ifEmpty { "Important memo for Khanyisa High pupils." },
                                            category = text3,
                                            id = editingAnnouncement?.id ?: 0L
                                        )
                                        Toast.makeText(context, if (editingAnnouncement != null) "Notice updated!" else "Notice added!", Toast.LENGTH_SHORT).show()
                                        editingAnnouncement = null
                                    }
                                    "Events" -> {
                                        val combinedImages = if (attachedImagesList.isEmpty()) {
                                            "file:///android_asset/supabase/6.jpg"
                                        } else {
                                            attachedImagesList.joinToString(",")
                                        }
                                        viewModel.addEvent(
                                            title = text1,
                                            description = text2.ifEmpty { "School Community event." },
                                            date = text3.ifEmpty { "2026-06-15 10:00" },
                                            location = text4.ifEmpty { "School Campus Grounds" },
                                            imageUrls = combinedImages,
                                            id = editingEvent?.id ?: 0L
                                        )
                                        Toast.makeText(context, if (editingEvent != null) "Event updated successfully!" else "Event added successfully!", Toast.LENGTH_SHORT).show()
                                        editingEvent = null
                                        attachedImagesList = emptyList()
                                    }
                                    "News" -> {
                                        val combinedImages = if (attachedImagesList.isEmpty()) {
                                            "file:///android_asset/supabase/images.jpeg"
                                        } else {
                                            attachedImagesList.joinToString(",")
                                        }
                                        viewModel.addPost(
                                            title = text1,
                                            content = text2.ifEmpty { "Campus news developments." },
                                            category = text3,
                                            imageUrls = combinedImages,
                                            id = editingPost?.id ?: 0L
                                        )
                                        Toast.makeText(context, if (editingPost != null) "News Post updated!" else "News Post added!", Toast.LENGTH_SHORT).show()
                                        editingPost = null
                                        attachedImagesList = emptyList()
                                    }
                                    "Schedules" -> {
                                        val filePresets = mapOf(
                                            "12A" to "file:///android_asset/supabase/2.jpg",
                                            "11B" to "file:///android_asset/supabase/4.jpg",
                                            "8A" to "file:///android_asset/supabase/3.jpg",
                                            "8B" to "file:///android_asset/supabase/5.jpg"
                                        )
                                        val chosenImage = filePresets[text4] ?: "file:///android_asset/supabase/2.jpg"

                                        viewModel.addTimetable(
                                            title = text1,
                                            type = text3,
                                            grade = text4,
                                            imageUrl = chosenImage,
                                            id = editingTimetable?.id ?: 0L
                                        )
                                        Toast.makeText(context, if (editingTimetable != null) "Schedules entry updated!" else "Schedules entry added!", Toast.LENGTH_SHORT).show()
                                        editingTimetable = null
                                    }
                                }
                                text1 = ""; text2 = ""; text3 = ""; text4 = ""
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_admin_entry")
                        ) {
                            Text(
                                text = if (isEditingMode) "✏️ Save Modified Changes" else "💾 Add Local Database Entry",
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // INVENTORY CHANNELS LOGS
            item {
                Divider(color = Color(0xFFCBD5E1).copy(alpha = 0.5f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active $adminTab Logs (${
                            when(adminTab) {
                                "Notices" -> announcements.size
                                "Events" -> events.size
                                "News" -> posts.size
                                "Schedules" -> timetables.size
                                else -> 0
                            }
                        })", 
                        fontWeight = FontWeight.Black, 
                        fontSize = 13.sp, 
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Swipe to moderation deck",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            when (adminTab) {
                "Notices" -> {
                    if (announcements.isEmpty()) {
                        item { EmptyInventoryPlaceholder("Notices") }
                    } else {
                        items(announcements.size) { idx ->
                            val item = announcements[idx]
                            InventoryActionRow(
                                title = item.title,
                                subtitle = "Category: ${item.category.uppercase(Locale.ROOT)} • Published in SQLite",
                                onEdit = { editingAnnouncement = item },
                                onDelete = { viewModel.deleteAnnouncement(item) }
                            )
                        }
                    }
                }
                "Events" -> {
                    if (events.isEmpty()) {
                        item { EmptyInventoryPlaceholder("Events") }
                    } else {
                        items(events.size) { idx ->
                            val item = events[idx]
                            InventoryActionRow(
                                title = item.title,
                                subtitle = "Date: ${item.eventDate} • Venue: ${item.location}",
                                onEdit = { editingEvent = item },
                                onDelete = { viewModel.deleteEvent(item) }
                            )
                        }
                    }
                }
                "News" -> {
                    if (posts.isEmpty()) {
                        item { EmptyInventoryPlaceholder("News Posts") }
                    } else {
                        items(posts.size) { idx ->
                            val item = posts[idx]
                            val numImages = item.imageUrls.split(",").filter { it.trim().isNotEmpty() }.size
                            InventoryActionRow(
                                title = item.title,
                                subtitle = "Category: ${item.category} • Attached Images: $numImages",
                                onEdit = { editingPost = item },
                                onDelete = { viewModel.deletePost(item) }
                            )
                        }
                    }
                }
                "Schedules" -> {
                    if (timetables.isEmpty()) {
                        item { EmptyInventoryPlaceholder("Timetables") }
                    } else {
                        items(timetables.size) { idx ->
                            val item = timetables[idx]
                            InventoryActionRow(
                                title = item.title,
                                subtitle = "Grade: ${item.grade} • Target Type: ${item.type.uppercase(Locale.ROOT)}",
                                onEdit = { editingTimetable = item },
                                onDelete = { viewModel.deleteTimetable(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkeletonLoaderCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = alpha),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0))
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(11.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFE2E8F0))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.35f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFF1F5F9))
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyInventoryPlaceholder(name: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color(0xFFEDF2F7))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = BrandGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Live $name database stream cached. Ready to add logs...",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        repeat(2) {
            SkeletonLoaderCard()
        }
    }
}

@Composable
fun InventoryActionRow(
    title: String,
    subtitle: String,
    onEdit: (() -> Unit)?,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onEdit != null) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFFFF7ED), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit entry",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFFFEF2F2), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// SELECTION OF REUSABLE HELPER UI FUNCTIONS
// ==========================================
@Composable
fun h2Decoration(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium.copy(
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
    )
}

fun String.blankValid() = this.trim().isEmpty()

@Composable
fun ProfileRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontSize = 12.sp, color = Color.LightGray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun MediaAttachmentManagerSection(
    attachedImagesList: List<String>,
    onImagesListChange: (List<String>) -> Unit,
    attachImageUrlInput: String,
    onUrlInputChange: (String) -> Unit
) {
    val context = LocalContext.current
    
    Divider(color = Color(0xFFEDF2F7))
    Text("💡 Media Attachment Manager (Multiple Images)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
    
    if (attachedImagesList.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            attachedImagesList.forEachIndexed { index, imgUrl ->
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imgUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Post image $index",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .clickable {
                                onImagesListChange(attachedImagesList.filterIndexed { i, _ -> i != index })
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove image",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }
    } else {
        Text("No images attached yet. Paste a URL or use campus presets below:", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
    }

    Row(
        modifier = Modifier.fillMaxWidth().testTag("add_image_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = attachImageUrlInput,
            onValueChange = onUrlInputChange,
            label = { Text("Paste Image URL") },
            placeholder = { Text("https://...") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).testTag("image_url_input"),
            singleLine = true
        )
        Button(
            onClick = {
                if (attachImageUrlInput.trim().isNotEmpty()) {
                    onImagesListChange(attachedImagesList + attachImageUrlInput.trim())
                    onUrlInputChange("")
                    Toast.makeText(context, "Successfully attached custom image URL!", Toast.LENGTH_SHORT).show()
                } else {
                    // Intelligent action: If line is blank, auto-attach a high-quality campus preset!
                    val defaultPresets = listOf(
                        "file:///android_asset/supabase/1.jpg",
                        "file:///android_asset/supabase/2.jpg",
                        "file:///android_asset/supabase/3.jpg",
                        "file:///android_asset/supabase/4.jpg",
                        "file:///android_asset/supabase/5.jpg",
                        "file:///android_asset/supabase/6.jpg"
                    )
                    val remaining = defaultPresets.filter { !attachedImagesList.contains(it) }
                    if (remaining.isNotEmpty()) {
                        val randomPick = remaining.random()
                        onImagesListChange(attachedImagesList + randomPick)
                        Toast.makeText(context, "Grabbed a premium default campus photo.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "All campus presets are already loaded!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(52.dp).testTag("plus_add_image_button")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add image", tint = Color.White)
        }
    }

    // Quick tap interactive preset grid cards
    Text("Or Tap preset cards below to attach/detach campus pictures:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val presetsList = listOf(
            "file:///android_asset/supabase/1.jpg" to "Classroom",
            "file:///android_asset/supabase/2.jpg" to "Sports Field",
            "file:///android_asset/supabase/3.jpg" to "School Hall",
            "file:///android_asset/supabase/4.jpg" to "Tech Lab",
            "file:///android_asset/supabase/5.jpg" to "Library",
            "file:///android_asset/supabase/6.jpg" to "Academics"
        )
        presetsList.forEach { (url, label) ->
            val isSelected = attachedImagesList.contains(url)
            Card(
                shape = RoundedCornerShape(12.dp),
                border = if (isSelected) BorderStroke(2.dp, BrandGold) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFF8FAFC) else Color.White),
                modifier = Modifier
                    .width(110.dp)
                    .clickable {
                        if (isSelected) {
                            onImagesListChange(attachedImagesList.filter { it != url })
                            Toast.makeText(context, "Removed $label", Toast.LENGTH_SHORT).show()
                        } else {
                            onImagesListChange(attachedImagesList + url)
                            Toast.makeText(context, "Attached $label!", Toast.LENGTH_SHORT).show()
                        }
                    }
            ) {
                Box(modifier = Modifier.height(85.dp).fillMaxWidth()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)
                            .build(),
                        contentDescription = label,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Dynamic visual indicator icon (plus/checkmark status badge)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFF10B981) else Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = "Attach status",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
