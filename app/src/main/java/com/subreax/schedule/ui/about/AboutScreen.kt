package com.subreax.schedule.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.subreax.schedule.BuildConfig
import com.subreax.schedule.R
import com.subreax.schedule.ui.context
import com.subreax.schedule.ui.theme.ScheduleTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navBack: () -> Unit) {
    val context = context()

    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.go_back))
                    }
                }
            )

            Image(
                painter = painterResource(id = R.drawable.app_icon_round),
                contentDescription = "App icon",
                modifier = Modifier
                    .padding(top = 32.dp)
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp, bottom = 8.dp)
            )


            Text(
                text = stringResource(id = R.string.app_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            AboutItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.github_outline_48),
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = stringResource(R.string.github_repository),
                subtitle = null,
                onClick = { openLink(context, "https://github.com/subreax/Schedule") },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )

            AboutItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.telegram_outline_48),
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = stringResource(R.string.tg_channel),
                subtitle = stringResource(R.string.stay_tuned_for_dev_news),
                onClick = { openLink(context, "https://t.me/subreax_tsu_schedule") },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )

            AboutItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.PrivacyTip,
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = stringResource(R.string.privacy_policy),
                subtitle = null,
                onClick = { openLink(context, "https://schedule-6b060.web.app/") },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                /* TODO: Update on release */
                text = "Версия: ${BuildConfig.VERSION_NAME} (26 авг. 2024, 993c7d)",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

private fun openLink(context: Context, url: String) {
    Intent(Intent.ACTION_VIEW, Uri.parse(url)).also {
        context.startActivity(it)
    }
}

@PreviewLightDark
@Composable
private fun AboutScreenPreview() {
    ScheduleTheme {
        AboutScreen(
            navBack = { }
        )
    }
}
