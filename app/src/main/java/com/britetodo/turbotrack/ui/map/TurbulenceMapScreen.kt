package com.britetodo.turbotrack.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.britetodo.turbotrack.data.model.PIREPReport
import com.britetodo.turbotrack.theme.SeverityExtreme
import com.britetodo.turbotrack.theme.SeverityLight
import com.britetodo.turbotrack.theme.SeverityModerate
import com.britetodo.turbotrack.theme.SeveritySevere
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.cos
import kotlin.math.sin

// CartoDB Dark Matter tile source — free, no key required
private val CARTO_DARK = object : OnlineTileSourceBase(
    "CartoDark", 0, 19, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/dark_all/",
        "https://b.basemaps.cartocdn.com/dark_all/",
        "https://c.basemaps.cartocdn.com/dark_all/"
    )
) {
    override fun getTileURLString(pMapTileIndex: Long): String =
        baseUrl +
            MapTileIndex.getZoom(pMapTileIndex) + "/" +
            MapTileIndex.getX(pMapTileIndex) + "/" +
            MapTileIndex.getY(pMapTileIndex) +
            mImageFilenameEnding
}

private fun circlePoints(center: GeoPoint, radiusMeters: Double, n: Int = 32): ArrayList<GeoPoint> {
    val pts = ArrayList<GeoPoint>(n)
    val latRad = Math.toRadians(center.latitude)
    for (i in 0 until n) {
        val angle = Math.toRadians(i * 360.0 / n)
        val dLat = (radiusMeters / 111_320.0) * cos(angle)
        val dLon = (radiusMeters / (111_320.0 * cos(latRad))) * sin(angle)
        pts.add(GeoPoint(center.latitude + dLat, center.longitude + dLon))
    }
    return pts
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurbulenceMapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    settingsViewModel: com.britetodo.turbotrack.ui.settings.SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isPremium by settingsViewModel.isPremium.collectAsState()
    val hasSuperPro by settingsViewModel.hasSuperPro.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val sheetState = rememberModalBottomSheetState()

    var locationGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
        )
    }
    val locationPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> locationGranted = granted }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        if (!locationGranted) locationPermLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(CARTO_DARK)
            setMultiTouchControls(true)
            controller.setZoom(4.5)
            controller.setCenter(GeoPoint(37.0, -95.0))
            isTilesScaledToDpi = true
        }
    }

    // Pause/resume map with lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE  -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val filteredPireps = viewModel.filteredPireps()

    Box(modifier = modifier.fillMaxSize()) {

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { mv ->
                mv.overlays.clear()

                // My location overlay
                if (locationGranted) {
                    val myLocation = MyLocationNewOverlay(GpsMyLocationProvider(context), mv)
                    myLocation.enableMyLocation()
                    mv.overlays.add(myLocation)
                }

                // SIGMET polygons
                state.sigmets.forEach { sigmet ->
                    val coords = sigmet.coords ?: return@forEach
                    if (coords.size < 3) return@forEach
                    val poly = Polygon(mv).apply {
                        points = coords.map { GeoPoint(it.lat, it.lon) }
                        fillPaint.color = android.graphics.Color.argb(60, 255, 107, 0)
                        outlinePaint.color = android.graphics.Color.argb(180, 255, 107, 0)
                        outlinePaint.strokeWidth = 3f
                    }
                    mv.overlays.add(poly)
                }

                // PIREP circles
                filteredPireps.forEach { pirep ->
                    val lat = pirep.lat ?: return@forEach
                    val lon = pirep.lon ?: return@forEach
                    val center = GeoPoint(lat, lon)
                    val color = pirep.severity.color
                    val androidColor = color.toArgb()
                    val fillAlpha = (0.35f * 255).toInt()
                    val strokeAlpha = (0.85f * 255).toInt()

                    val circle = Polygon(mv).apply {
                        points = circlePoints(center, 30_000.0)
                        fillPaint.color = android.graphics.Color.argb(
                            fillAlpha,
                            android.graphics.Color.red(androidColor),
                            android.graphics.Color.green(androidColor),
                            android.graphics.Color.blue(androidColor)
                        )
                        outlinePaint.color = android.graphics.Color.argb(
                            strokeAlpha,
                            android.graphics.Color.red(androidColor),
                            android.graphics.Color.green(androidColor),
                            android.graphics.Color.blue(androidColor)
                        )
                        outlinePaint.strokeWidth = 2f
                        setOnClickListener { _, _, _ ->
                            viewModel.selectPirep(pirep)
                            true
                        }
                    }
                    mv.overlays.add(circle)
                }

                mv.invalidate()
            }
        )

        // Altitude filter chips
        LazyRow(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(viewModel.altitudeFilters) { alt ->
                FilterChip(
                    selected = state.selectedAltitude == alt,
                    onClick = { viewModel.setAltitudeFilter(alt) },
                    label = { Text(alt, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TurboBlue,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.92f),
                        labelColor = TextSecondary
                    )
                )
            }
        }

        // Legend bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                "Light" to SeverityLight,
                "Moderate" to SeverityModerate,
                "Severe" to SeveritySevere,
                "Extreme" to SeverityExtreme
            ).forEach { (label, color) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(color, RoundedCornerShape(3.dp)))
                    Spacer(Modifier.width(4.dp))
                    Text(label, fontSize = 10.sp, color = TextSecondary)
                }
            }
        }

        // Loading
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TurboBlue)
            }
        }

        // Error
        state.error?.let { error ->
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)) {
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.loadData() }) {
                            Text("Retry", color = TurboBlue)
                        }
                    }
                ) { Text(error) }
            }
        }

        // Refresh button
        IconButton(
            onClick = { viewModel.loadData() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 60.dp, end = 8.dp)
                .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(8.dp))
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TurboBlue)
        }

        // Super Pro banner
        if (isPremium && !hasSuperPro) {
            com.britetodo.turbotrack.ui.components.SuperProBanner(
                source = "map",
                onTap = { source ->
                    settingsViewModel.analytics.logUpsellBannerClicked(source)
                    settingsViewModel.triggerUpsell()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 72.dp)
            )
        }
    }

    // PIREP bottom sheet
    state.selectedPirep?.let { pirep ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectPirep(null) },
            sheetState = sheetState,
            containerColor = TurboCard,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFE5E5EA)) }
        ) {
            PIREPDetailSheet(pirep = pirep)
        }
    }
}

@Composable
private fun PIREPDetailSheet(pirep: PIREPReport) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(pirep.displayAircraftType, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
                Text(pirep.displayFlightLevel, color = TextSecondary, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .background(pirep.severity.color.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(pirep.severity.displayName, color = pirep.severity.color, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(16.dp))
        DetailRow("Time", pirep.timeAgo)
        pirep.turbulenceType?.let { DetailRow("Type", it) }
        pirep.turbulenceFreq?.let { DetailRow("Frequency", it) }
        pirep.windSpeed?.let { DetailRow("Wind Speed", "$it kt") }
        pirep.windDirection?.let { DetailRow("Wind Direction", "${it}°") }
        pirep.temperature?.let { DetailRow("Temperature", "${it}°C") }
        pirep.rawObservation?.let { raw ->
            Spacer(Modifier.height(12.dp))
            Text("Raw Report", color = TextMuted, fontSize = 12.sp)
            Text(raw, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMuted, fontSize = 14.sp)
        Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
