package dev.taekwondo.academy.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dev.taekwondo.academy.R
import dev.taekwondo.academy.data.Poomsae
import dev.taekwondo.academy.data.TaekwondoData
import dev.taekwondo.academy.data.Technique
import dev.taekwondo.academy.data.TrainingProgram
import java.util.Calendar

val BgLight = Color(0xFFF7F7F8)
val Charcoal = Color(0xFF17171C)
val AccentRed = Color(0xFFE30613)
val CardWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFF5A5A64)

data class TabDef(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaekwondoApp() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme {
            var tab by remember { mutableIntStateOf(0) }
            var selectedPoomsae by remember { mutableStateOf<Poomsae?>(null) }
            var selectedTechnique by remember { mutableStateOf<Technique?>(null) }
            val tabs = listOf(
                TabDef("خانه", Icons.Filled.Home),
                TabDef("فرم‌ها", Icons.AutoMirrored.Filled.List),
                TabDef("تکنیک‌ها", Icons.Filled.Star),
                TabDef("برنامه", Icons.Filled.DateRange),
                TabDef("کلاس آنلاین", Icons.Filled.PlayArrow)
            )
            val detailTitle = when {
                selectedPoomsae != null -> selectedPoomsae!!.faName
                selectedTechnique != null -> selectedTechnique!!.faName
                else -> null
            }
            Scaffold(
                containerColor = BgLight,
                topBar = {
                    TopAppBar(
                        title = { Text(detailTitle ?: "آموزش تکواندو", fontWeight = FontWeight.Bold, color = Color.White) },
                        navigationIcon = {
                            if (detailTitle != null) {
                                IconButton(onClick = { selectedPoomsae = null; selectedTechnique = null }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Charcoal)
                    )
                },
                bottomBar = {
                    NavigationBar(containerColor = Charcoal, contentColor = Color.White) {
                        tabs.forEachIndexed { i, t ->
                            NavigationBarItem(
                                selected = tab == i,
                                onClick = { tab = i; selectedPoomsae = null; selectedTechnique = null },
                                icon = { Icon(t.icon, contentDescription = t.title) },
                                label = { Text(t.title, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color.White,
                                    unselectedIconColor = Color(0xFFB9B9C2),
                                    unselectedTextColor = Color(0xFFB9B9C2),
                                    indicatorColor = AccentRed
                                )
                            )
                        }
                    }
                }
            ) { pad ->
                Box(Modifier.fillMaxSize().padding(pad).background(BgLight)) {
                    when {
                        selectedPoomsae != null -> PoomsaeDetail(selectedPoomsae!!)
                        selectedTechnique != null -> TechniqueDetail(selectedTechnique!!)
                        tab == 0 -> HomeScreen(
                            onStartTraining = { tab = 3 },
                            onOpenForms = { tab = 1 },
                            onOpenOnline = { tab = 4 }
                        )
                        tab == 1 -> PoomsaeList { selectedPoomsae = it }
                        tab == 2 -> TechniqueList { selectedTechnique = it }
                        tab == 3 -> ProgramsScreen()
                        else -> OnlineTab()
                    }
                }
            }
        }
    }
}

@Composable
fun RedButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = AccentRed, contentColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) { Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Charcoal,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
}

@Composable
fun HomeScreen(onStartTraining: () -> Unit, onOpenForms: () -> Unit, onOpenOnline: () -> Unit) {
    val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7 // 0=Sat-ish mapping for display only
    val program = TaekwondoData.programs[0]
    val today = program.schedule[todayIdx % program.schedule.size]
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            // Hero — charcoal
            Card(colors = CardDefaults.cardColors(containerColor = Charcoal), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("🥋", fontSize = 36.sp)
                    Text("تکواندو را از پایه، درست یاد بگیر", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("فرم‌های تگوک کوکی‌وان مرحله‌به‌مرحله، کتابخانه تکنیک با ویدیو، برنامه تمرین و کلاس آنلاین زنده.", color = Color(0xFFD6D6DD), fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    RedButton("شروع تمرین", Modifier.fillMaxWidth()) { onStartTraining() }
                }
            }
        }
        item { SectionTitle("تمرین امروز") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("${today.day} — ${program.title}", fontWeight = FontWeight.Bold, color = Charcoal, fontSize = 17.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(today.focus, color = TextGray, fontSize = 14.sp)
                    Text("مدت: ${today.duration}", color = TextGray, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    RedButton("شروع تمرین امروز", Modifier.fillMaxWidth()) { onStartTraining() }
                }
            }
        }
        item { SectionTitle("در یک نگاه") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("۸" to "فرم تگوک", "۱۶" to "تکنیک", "۳" to "برنامه تمرینی").forEach { (n, label) ->
                    Card(colors = CardDefaults.cardColors(containerColor = CardWhite),
                        shape = RoundedCornerShape(14.dp), modifier = Modifier.weight(1f)) {
                        Column(Modifier.padding(14.dp)) {
                            Text(n, color = AccentRed, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text(label, color = Charcoal, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        item { SectionTitle("مسیر یادگیری") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(16.dp), modifier = Modifier.clickable { onOpenForms() }) {
                Column(Modifier.padding(16.dp)) {
                    Text("از تگوک ۱ (آسمان) تا تگوک ۸ (زمین)", fontWeight = FontWeight.Bold, color = Charcoal)
                    Text("هر فرم با فلسفه، نماد و مراحل گام‌به‌گام — دقیقاً به ترتیب آزمون کمربند کوکی‌وان.", color = TextGray, fontSize = 13.sp)
                }
            }
        }
        item { SectionTitle("اشتراک و کلاس آنلاین") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(16.dp), modifier = Modifier.clickable { onOpenOnline() }) {
                Column(Modifier.padding(16.dp)) {
                    Text("کلاس‌های زنده با اشتراک آکادمی", fontWeight = FontWeight.Bold, color = Charcoal)
                    Text("با کد اشتراکی که مربی در اختیارتان می‌گذارد (ماهانه، سه‌ماهه یا سالانه) به کلاس‌ها و بخش آنلاین دسترسی پیدا می‌کنید.", color = TextGray, fontSize = 13.sp)
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
fun PoomsaeList(onSelect: (Poomsae) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("فرم‌های تگوک (پومسه)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Charcoal) }
        item { Text("۸ فرم رسمی کوکی‌وان، از گئوپ ۸ تا گئوپ ۱", color = TextGray, fontSize = 13.sp) }
        items(TaekwondoData.poomsaeList) { p ->
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().clickable { onSelect(p) }) {
                Row(Modifier.padding(16.dp)) {
                    Text(p.trigram, fontSize = 30.sp, color = Charcoal)
                    Spacer(Modifier.padding(horizontal = 6.dp))
                    Column {
                        Text(p.faName, fontWeight = FontWeight.Bold, color = Charcoal, fontSize = 16.sp)
                        Text("${p.enName} • ${p.meaning}", color = TextGray, fontSize = 12.sp)
                        Text("${p.geup} • ${p.movements} حرکت", color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Flow clips live in the repo's /videos folder and are served by the
// jsDelivr CDN: direct 200 responses with a proper video/mp4 type and no
// redirects (GitHub's release-asset host 302-redirects, which Android's
// media player cannot handle).
const val VIDEO_BASE = "https://cdn.jsdelivr.net/gh/jinjemorasi/Taekwondo@main/videos"

fun techniqueVideoUrl(enName: String): String? = when (enName) {
    "Front Kick" -> "$VIDEO_BASE/ap-chagi.mp4"
    "Roundhouse Kick" -> "$VIDEO_BASE/dollyo-chagi.mp4"
    "Side Kick" -> "$VIDEO_BASE/yeop-chagi.mp4"
    "Back Kick" -> "$VIDEO_BASE/dwi-chagi.mp4"
    "Axe Kick" -> "$VIDEO_BASE/naeryeo-chagi.mp4"
    "Hook Kick" -> "$VIDEO_BASE/huryeo-chagi.mp4"
    else -> null
}

fun formVideoUrl(id: Int): String? =
    if (id in 1..8) "$VIDEO_BASE/taeguk-$id.mp4" else null

@Composable
fun RemoteVideoPlayer(url: String) {
    var resolved by remember(url) { mutableStateOf<String?>(null) }
    var failed by remember(url) { mutableStateOf(false) }
    var attempt by remember(url) { mutableIntStateOf(0) }
    // GitHub release links answer with a redirect that Android's media
    // player cannot follow — resolve the final file URL first, then play.
    val startedFor = remember(url) { mutableIntStateOf(-1) }
    if (startedFor.value != attempt) {
        startedFor.value = attempt
        Thread {
            try {
                val conn = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                conn.instanceFollowRedirects = true
                conn.connectTimeout = 12000
                conn.readTimeout = 12000
                conn.setRequestProperty("Range", "bytes=0-0")
                conn.connect()
                val code = conn.responseCode
                val finalUrl = conn.url.toString()
                conn.disconnect()
                if (code in 200..299) resolved = finalUrl else failed = true
            } catch (e: Exception) {
                failed = true
            }
        }.start()
    }
    Box(
        Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Charcoal, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        when {
            resolved != null -> AndroidView(
                factory = {
                    VideoView(it).apply {
                        val mc = MediaController(it)
                        mc.setAnchorView(this)
                        setMediaController(mc)
                        setVideoURI(Uri.parse(resolved))
                        setOnErrorListener { _, _, _ -> failed = true; true }
                    }
                },
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
            )
            failed -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("خطا در بارگذاری ویدیو", color = Color.White, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { failed = false; resolved = null; attempt++ },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp)) { Text("تلاش دوباره", fontSize = 13.sp) }
            }
            else -> CircularProgressIndicator(color = Color.White)
        }
    }
}

@Composable
fun PoomsaeDetail(p: Poomsae) {
    val ctx = LocalContext.current
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Charcoal), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("${p.trigram}  ${p.meaning}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${p.enName} • ${p.koName}", color = Color(0xFFD6D6DD), fontSize = 13.sp)
                    Text("${p.geup} • ${p.movements} حرکت", color = Color.White, fontSize = 13.sp)
                }
            }
            SectionTitle("فلسفه فرم")
            Text(p.philosophy, color = TextGray, fontSize = 14.sp)
            SectionTitle("تکنیک‌های جدید این فرم")
            Text(p.newTechniques.joinToString("، "), color = Charcoal, fontSize = 14.sp)
            val fUrl = formVideoUrl(p.id)
            if (fUrl != null) {
                SectionTitle("ویدیوی آموزشی فرم (Google Flow)")
                RemoteVideoPlayer(fUrl)
            }
            SectionTitle("مراحل گام‌به‌گام")
        }
        items(p.steps) { s ->
            val n = p.steps.indexOf(s) + 1
            Row(Modifier.padding(vertical = 5.dp)) {
                Text("$n.", color = AccentRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(s, color = Charcoal, fontSize = 14.sp)
            }
        }
        item {
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun TechniqueList(onSelect: (Technique) -> Unit) {
    var filter by remember { mutableStateOf("همه") }
    val cats = listOf("همه", "ضربات پا", "دفاع‌ها", "ضربات دست", "ایستادن‌ها")
    val list = if (filter == "همه") TaekwondoData.techniques else TaekwondoData.techniques.filter { it.category == filter }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("کتابخانه تکنیک‌ها", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Charcoal) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                cats.forEach { c ->
                    val sel = filter == c
                    Button(onClick = { filter = c },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (sel) AccentRed else Charcoal,
                            contentColor = Color.White),
                        shape = RoundedCornerShape(20.dp)) { Text(c, fontSize = 12.sp) }
                }
            }
        }
        items(list) { t ->
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().clickable { onSelect(t) }) {
                Column(Modifier.padding(16.dp)) {
                    Text(t.faName, fontWeight = FontWeight.Bold, color = Charcoal, fontSize = 16.sp)
                    Text("${t.enName} • ${t.koName}", color = TextGray, fontSize = 12.sp)
                    Text("${t.category} • سطح: ${t.difficulty}", color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TechniqueDetail(t: Technique) {
    val ctx = LocalContext.current
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(t.koName, color = TextGray, fontSize = 13.sp)
            Text(t.description, color = Charcoal, fontSize = 15.sp)
            val vUrl = techniqueVideoUrl(t.enName)
            if (vUrl != null) {
                SectionTitle("ویدیوی آموزشی (Google Flow)")
                RemoteVideoPlayer(vUrl)
            }
            SectionTitle("مراحل اجرا")
        }
        items(t.steps) { s ->
            Row(Modifier.padding(vertical = 4.dp)) {
                Text("•", color = AccentRed, fontWeight = FontWeight.Bold)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(s, color = Charcoal, fontSize = 14.sp)
            }
        }
        item {
            SectionTitle("خطاهای رایج")
            t.mistakes.forEach { m -> Text("✗ $m", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 2.dp)) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProgramsScreen() {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("برنامه‌های تمرینی", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Charcoal) }
        items(TaekwondoData.programs) { prog -> ProgramCard(prog) }
    }
}

@Composable
fun ProgramCard(prog: TrainingProgram) {
    Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(prog.title, fontWeight = FontWeight.Bold, color = Charcoal, fontSize = 18.sp)
            Text("${prog.level} • ${prog.durationWeeks}", color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(prog.description, color = TextGray, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
            prog.schedule.forEach { d ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(d.day, fontWeight = FontWeight.Bold, color = Charcoal, fontSize = 13.sp)
                        Text(d.focus, color = TextGray, fontSize = 12.sp)
                    }
                    Text(d.duration, color = Charcoal, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun OnlineTab() {
    val ctx = LocalContext.current
    var active by remember { mutableStateOf(LicenseStore.isActive(ctx)) }
    var showRenew by remember { mutableStateOf(false) }
    when {
        active && !showRenew -> OnlineClassScreen(onRenew = { showRenew = true })
        else -> SubscriptionScreen(onActivated = {
            active = LicenseStore.isActive(ctx)
            showRenew = false
        })
    }
}

@Composable
fun SubscriptionScreen(onActivated: () -> Unit) {
    val ctx = LocalContext.current
    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val cached = LicenseStore.loadCachedPool(ctx)
    var pool by remember {
        mutableStateOf(if (cached.isNotEmpty()) cached else LicenseStore.loadBundledPool(ctx))
    }
    val fetchStarted = remember { mutableStateOf(false) }
    if (!fetchStarted.value) {
        fetchStarted.value = true
        Thread { LicenseStore.fetchPool(ctx)?.let { pool = it } }.start()
    }
    val renewing = LicenseStore.isActive(ctx)

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Charcoal), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("اشتراک آکادمی تکواندو", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("کلاس‌های آنلاین و بخش کلاس‌ها مخصوص هنرجویان دارای اشتراک فعال است. کد اشتراک را از مربی دریافت کنید و همین‌جا فعالش کنید.", color = Color(0xFFD6D6DD), fontSize = 13.sp)
                    if (renewing) {
                        Spacer(Modifier.height(8.dp))
                        Text("اشتراک فعلی شما فعال است — ${LicenseStore.daysLeft(ctx)} روز باقی مانده. با وارد کردن کد جدید، مدت اشتراک تمدید می‌شود.",
                            color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            SectionTitle("پلن‌های اشتراک")
        }
        items(PLANS) { plan ->
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(plan.faTitle, fontWeight = FontWeight.Bold, color = Charcoal, fontSize = 15.sp)
                    Text(plan.faDuration, color = AccentRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            SectionTitle("فعال‌سازی با کد اشتراک")
            OutlinedTextField(code, { code = it; error = null }, label = { Text("کد اشتراک (مثلاً TKD-XXXXXX-XXXXXX)") },
                modifier = Modifier.fillMaxWidth())
            if (error != null) {
                Spacer(Modifier.height(6.dp))
                Text(error!!, color = AccentRed, fontSize = 13.sp)
            }
            Spacer(Modifier.height(12.dp))
            RedButton("فعال‌سازی اشتراک", Modifier.fillMaxWidth()) {
                val plan = LicenseStore.findPlan(code, pool)
                if (plan != null) {
                    LicenseStore.activate(ctx, plan)
                    onActivated()
                } else {
                    error = "این کد معتبر نیست. املای کد را بررسی کنید؛ اگر تازه خریده‌اید، چند لحظه صبر کنید و دوباره تلاش کنید."
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("قیمت و خرید کد اشتراک را مربی باشگاه اعلام می‌کند؛ پس از پرداخت، کد را از مربی بگیرید و همین‌جا وارد کنید. هر کد پس از فعال‌سازی به مدت پلن انتخابی معتبر است.",
                color = TextGray, fontSize = 12.sp)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun OnlineClassScreen(onRenew: () -> Unit = {}) {
    val ctx = LocalContext.current
    var room by remember { mutableStateOf("TaekwondoAcademyLive") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var classType by remember { mutableStateOf("گروهی آنلاین") }
    var savedName by remember { mutableStateOf<String?>(null) }

    // Load previous registration
    val prefs = remember { ctx.getSharedPreferences("class_prefs", Context.MODE_PRIVATE) }
    if (savedName == null && prefs.getString("saved_name_marker", null) != null) {
        savedName = prefs.getString("name", "")
    }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Charcoal), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("کلاس آنلاین زنده", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("کلاس‌ها روی Jitsi Meet برگزار می‌شود — کاملاً رایگان و متن‌باز، بدون نیاز به ساخت حساب و بدون محدودیت زمانی. کافی است نام اتاق را وارد کنید و وارد شوید؛ دوربین و میکروفون را مرورگر یا اپ Jitsi مدیریت می‌کند.", color = Color(0xFFD6D6DD), fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("✓ اشتراک فعال: ${LicenseStore.activePlan(ctx)?.faTitle ?: ""} — ${LicenseStore.daysLeft(ctx)} روز باقی مانده",
                        color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onRenew() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)) { Text("تمدید اشتراک", fontSize = 13.sp) }
                }
            }
            SectionTitle("ورود به کلاس")
            OutlinedTextField(room, { room = it }, label = { Text("نام اتاق کلاس") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            RedButton("ورود به کلاس آنلاین", Modifier.fillMaxWidth()) {
                val r = room.trim().ifEmpty { "TaekwondoAcademyLive" }
                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://meet.jit.si/$r")))
            }
            SectionTitle("ثبت‌نام کلاس")
            if (savedName != null) {
                Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(12.dp)) {
                    Text("✓ ثبت‌نام شما ثبت شد، $savedName عزیز. اطلاعات کلاس برایتان ارسال می‌شود.",
                        color = Charcoal, modifier = Modifier.padding(14.dp), fontSize = 14.sp)
                }
                Spacer(Modifier.height(10.dp))
            }
            OutlinedTextField(name, { name = it }, label = { Text("نام و نام خانوادگی") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(phone, { phone = it }, label = { Text("شماره موبایل") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(level, { level = it }, label = { Text("سطح / کمربند (مثلاً زرد، گئوپ ۸)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("گروهی آنلاین", "خصوصی آنلاین", "حضوری").forEach { t ->
                    Button(onClick = { classType = t },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (classType == t) AccentRed else Charcoal,
                            contentColor = Color.White),
                        shape = RoundedCornerShape(20.dp)) { Text(t, fontSize = 12.sp) }
                }
            }
            Spacer(Modifier.height(12.dp))
            RedButton("ثبت‌نام کلاس", Modifier.fillMaxWidth()) {
                prefs.edit()
                    .putString("name", name).putString("phone", phone)
                    .putString("level", level).putString("class_type", classType)
                    .putString("saved_name_marker", "1").apply()
                savedName = name
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
