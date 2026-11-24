package com.example.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.ui.components.LanguagePillBar
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.HijrahArabicStyles
import com.example.ui.theme.HijrahBorderGold
import com.example.ui.theme.HijrahBorderSubtle
import com.example.ui.theme.HijrahBrown
import com.example.ui.theme.HijrahDarkBrown
import com.example.ui.theme.HijrahErrorRust
import com.example.ui.theme.HijrahGold
import com.example.ui.theme.HijrahRadii
import com.example.ui.theme.HijrahRust
import com.example.ui.theme.HijrahShadowWarm
import com.example.ui.theme.HijrahTextSecondary
import com.example.ui.theme.HijrahWhite

@Composable
fun AuthWelcomeScreen(
    onAuthSuccess: () -> Unit,
    isCheckingSession: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val strings = LocalAppStrings.current
    val currentLanguage = LocalAppLanguage.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HijrahWhite)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("auth_welcome_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top editorial mark + Arabic accent + Language switcher
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                LanguagePillBar()
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(HijrahRadii.Chip)
                            .background(HijrahDarkBrown)
                            .border(1.dp, HijrahBorderGold, HijrahRadii.Chip),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "هـ",
                            style = HijrahArabicStyles.KufiAccent,
                            color = HijrahGold
                        )
                    }
                    Text(
                        text = strings.appName,
                        style = MaterialTheme.typography.headlineLarge,
                        color = HijrahDarkBrown
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (currentLanguage == AppLanguage.ARABIC) "Hijrah Netwerk" else "من التوجيه إلى الاندماج",
                    style = HijrahArabicStyles.AmiriSubtitle,
                    color = HijrahBrown
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Architectural Arch Visual Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .shadow(12.dp, HijrahRadii.HeroCard, ambientColor = HijrahShadowWarm, spotColor = HijrahShadowWarm)
                    .clip(HijrahRadii.HeroCard)
                    .border(1.dp, HijrahBorderGold, HijrahRadii.HeroCard)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_welcome_arch),
                    contentDescription = strings.authArchDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xD94F3A23)
                                ),
                                startY = 160f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(22.dp)
                ) {
                    Text(
                        text = strings.authTagline,
                        style = MaterialTheme.typography.labelMedium,
                        color = HijrahGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.appSlogan,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                        color = HijrahWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login & Privacy Card + Google Sign-In CTA
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = HijrahRadii.LargeCard,
                    color = HijrahWhite,
                    border = BorderStroke(1.dp, HijrahBorderSubtle),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.VerifiedUser,
                                contentDescription = null,
                                tint = HijrahBrown,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.authCardTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = HijrahDarkBrown
                            )
                        }
                        Text(
                            text = strings.authCardBody,
                            style = MaterialTheme.typography.bodyMedium,
                            color = HijrahTextSecondary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = HijrahGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = strings.authPrivacyNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = HijrahBrown
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Surface(
                        shape = HijrahRadii.Input,
                        color = HijrahWhite,
                        border = BorderStroke(1.dp, HijrahErrorRust),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_error_banner")
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = HijrahErrorRust,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                val buttonBusy = isLoading || isCheckingSession
                Button(
                    onClick = {
                        errorMessage = null
                        isLoading = true
                        onGoogleSignInClicked(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = {
                                isLoading = false
                                onAuthSuccess()
                            },
                            onAuthError = { msg ->
                                isLoading = false
                                errorMessage = msg
                            },
                            scope = scope,
                            onAuthCancelled = {
                                isLoading = false
                            }
                        )
                    },
                    enabled = !buttonBusy,
                    shape = HijrahRadii.Button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HijrahRust,
                        contentColor = HijrahWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("google_sign_in_button")
                ) {
                    if (buttonBusy) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = HijrahWhite,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isCheckingSession && !isLoading) strings.authCheckingSession else strings.authSigningIn,
                            style = MaterialTheme.typography.labelLarge
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Login,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = strings.authGoogleCta,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Text(
                    text = strings.authFooterNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = HijrahTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
