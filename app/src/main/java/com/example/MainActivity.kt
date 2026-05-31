package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
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
                                    .data("file:///android_asset/supabase/av_1.jpg")
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
                                        .data("file:///android_asset/supabase/av_1.jpg")
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
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 6.dp, y = (-6).dp)
                                            .size(16.dp)
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
    val progressInfo = remember { SchoolCalendarHelper.getTerm2Progress() }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "SOUTH AFRICA SCHOOL TERMS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = BrandGold,
                    letterSpacing = 1.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = progressInfo.label,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Current Date: 31 May 2026",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Text(
                        text = "${(progressInfo.progressPercentage * 100).toInt()}%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                LinearProgressIndicator(
                    progress = progressInfo.progressPercentage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = BrandGold,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )

                Text(
                    text = "School Term 2 ends on Friday, 26 June 2026. Keep doing your best!",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Normal
                )
            }
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
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        h2Decoration("Campus Hub Feed")
        Text(
            text = "Stay interconnected with recent matric developments, welfare food drives, and pupil initiatives.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        if (posts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No campus news posts in the current stream.",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(posts.size) { idx ->
                    val post = posts[idx]
                    var commentText by remember { mutableStateOf("") }
                    val comments by viewModel.getComments(post.id).collectAsState(initial = emptyList())
                    val likes by viewModel.getLikes(post.id).collectAsState(initial = emptyList())

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Category Badge Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = post.category.uppercase(Locale.ROOT),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrandGoldDark,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Text(
                                    text = "31 May 2026",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Text(
                                text = post.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            // News Feed Picture Card Slider
                            val imgs = post.imageUrls.split(",").map { it.trim() }.filter { it.isNotEmpty() }
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
                                            contentDescription = "Post Photo",
                                            modifier = Modifier
                                                .width(260.dp)
                                                .height(140.dp)
                                                .clip(RoundedCornerShape(16.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }

                            Text(
                                text = post.content,
                                fontSize = 13.sp,
                                color = Color(0xFF334155),
                                lineHeight = 19.sp
                            )

                            // Reactions Bar
                            Divider(color = Color(0xFFF1F5F9))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val clientLiked = likes.any { it.clientId == viewModel.clientId }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .clickable { viewModel.toggleLike(post.id) }
                                ) {
                                    Icon(
                                        imageVector = if (clientLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUp,
                                        contentDescription = "Like Post",
                                        tint = if (clientLiked) BrandGold else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "${likes.size} Likes",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (clientLiked) BrandGoldDark else Color.DarkGray
                                    )
                                }

                                Text(
                                    text = "${comments.size} Comments",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                            }

                            // Inline comment list
                            if (comments.isNotEmpty()) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                                        .padding(10.dp)
                                ) {
                                    comments.forEach { comment ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = comment.author,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color(0xFF1E293B)
                                                )
                                                Text(
                                                    text = comment.content,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF334155)
                                                )
                                            }

                                            // Quick delete comment for safety
                                            IconButton(
                                                onClick = { viewModel.deleteComment(comment) },
                                                modifier = Modifier.size(16.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Delete Comment",
                                                    tint = Color.Red.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Write inline Comment Form
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextField(
                                    value = commentText,
                                    onValueChange = { commentText = it },
                                    placeholder = { Text("Write a comment...", fontSize = 11.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF1F5F9),
                                        unfocusedContainerColor = Color(0xFFF1F5F9),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true
                                )

                                IconButton(
                                    onClick = {
                                        if (commentText.trim().isNotEmpty()) {
                                            viewModel.addComment(post.id, "Learner Representative", commentText.trim())
                                            commentText = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F172A))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
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
    var adminTab by remember { mutableStateOf("Notices") } // "Notices", "Events", "News", "Schedules"

    // Form inputs
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var text3 by remember { mutableStateOf("") }
    var text4 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        h2Decoration("Admin Control Deck")
        Text(
            text = "Create new timetables, events, bulletin notices or news blocks locally.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        // Subtabs
        val tabs = listOf("Notices", "Events", "News", "Schedules")
        TabRow(
            selectedTabIndex = tabs.indexOf(adminTab),
            containerColor = Color.White,
            contentColor = BrandGoldDark
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = adminTab == tab,
                    onClick = {
                        adminTab = tab
                        text1 = ""; text2 = ""; text3 = ""; text4 = ""
                    },
                    text = { Text(tab, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                )
            }
        }

        // Action input fields depending on active sub-tab
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Add new $adminTab Entry",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )

                when (adminTab) {
                    "Notices" -> {
                        OutlinedTextField(
                            value = text1, onValueChange = { text1 = it },
                            label = { Text("Notice Title") }, modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = text2, onValueChange = { text2 = it },
                            label = { Text("Content/Details") }, modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = text3, onValueChange = { text3 = it },
                            label = { Text("Category (Community, Academics, Sports)") }, modifier = Modifier.fillMaxWidth()
                        )
                    }
                    "Events" -> {
                        OutlinedTextField(
                            value = text1, onValueChange = { text1 = it },
                            label = { Text("Event Name") }, modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = text2, onValueChange = { text2 = it },
                            label = { Text("Description") }, modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = text3, onValueChange = { text3 = it },
                            label = { Text("Date (e.g., 2026-06-12 09:00)") }, modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = text4, onValueChange = { text4 = it },
                            label = { Text("Venue Location") }, modifier = Modifier.fillMaxWidth()
                        )
                    }
                    "News" -> {
                        OutlinedTextField(
                            value = text1, onValueChange = { text1 = it },
                            label = { Text("News Title") }, modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = text2, onValueChange = { text2 = it },
                            label = { Text("Content Body") }, modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = text3, onValueChange = { text3 = it },
                            label = { Text("Category (Social, Community, Academic)") }, modifier = Modifier.fillMaxWidth()
                        )
                    }
                    "Schedules" -> {
                        OutlinedTextField(
                            value = text1, onValueChange = { text1 = it },
                            label = { Text("Schedule Title") }, modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = text2, onValueChange = { text2 = it },
                            label = { Text("Class Grade (e.g., 12A, 11B, 8A)") }, modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = text3, onValueChange = { text3 = it },
                            label = { Text("Type (weekly or exam)") }, modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Button(
                    onClick = {
                        if (text1.blankValid() || text2.blankValid()) {
                            Toast.makeText(context, "Please satisfy required inputs", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        when (adminTab) {
                            "Notices" -> {
                                viewModel.addAnnouncement(text1, text2, text3.ifEmpty { "general" })
                            }
                            "Events" -> {
                                viewModel.addEvent(
                                    text1, text2, text3.ifEmpty { "2026-06-15 10:00" }, text4.ifEmpty { "School Hall" },
                                    "file:///android_asset/supabase/6.jpg"
                                )
                            }
                            "News" -> {
                                viewModel.addPost(
                                    text1, text2, text3.ifEmpty { "Social" },
                                    "file:///android_asset/supabase/images.jpeg"
                                )
                            }
                            "Schedules" -> {
                                viewModel.addTimetable(
                                    text1, text3.ifEmpty { "weekly" }, text2.ifEmpty { "12A" },
                                    "file:///android_asset/supabase/2.jpg"
                                )
                            }
                        }
                        Toast.makeText(context, "Added successfully to Local Database", Toast.LENGTH_SHORT).show()
                        text1 = ""; text2 = ""; text3 = ""; text4 = ""
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Local Entry", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Small inventory list with delete option
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        Text("Active Inventory Logs (Tap to Delete)", fontWeight = FontWeight.Bold, fontSize = 12.sp)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            when (adminTab) {
                "Notices" -> {
                    items(announcements.size) { idx ->
                        val item = announcements[idx]
                        InventoryRow(title = item.title, subtitle = item.category) {
                            viewModel.deleteAnnouncement(item)
                            Toast.makeText(context, "Removed Notice", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                "Events" -> {
                    items(events.size) { idx ->
                        val item = events[idx]
                        InventoryRow(title = item.title, subtitle = item.eventDate) {
                            viewModel.deleteEvent(item)
                            Toast.makeText(context, "Removed Event", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                "News" -> {
                    items(posts.size) { idx ->
                        val item = posts[idx]
                        InventoryRow(title = item.title, subtitle = item.category) {
                            viewModel.deletePost(item)
                            Toast.makeText(context, "Removed News Post", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                "Schedules" -> {
                    items(timetables.size) { idx ->
                        val item = timetables[idx]
                        InventoryRow(title = item.title, subtitle = "${item.grade} - ${item.type}") {
                            viewModel.deleteTimetable(item)
                            Toast.makeText(context, "Removed Timetable", Toast.LENGTH_SHORT).show()
                        }
                    }
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

@Composable
fun InventoryRow(
    title: String,
    subtitle: String,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onDelete() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
            Text(subtitle, fontSize = 10.sp, color = Color.Gray)
        }
        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.5f))
    }
}

fun String.blankValid() = this.trim().isEmpty()
