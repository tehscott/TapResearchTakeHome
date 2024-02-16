package com.stromberg.tapresearchtakehome

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.stromberg.tapresearchtakehome.ui.theme.TapResearchTakeHomeTheme
import com.tapresearch.tapsdk.TapResearch
import com.tapresearch.tapsdk.callback.TRContentCallback
import com.tapresearch.tapsdk.models.TRError
import com.tapresearch.tapsdk.models.TRReward

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TapResearchTakeHomeTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainUi()
                }
            }
        }

        initTapResearchSdk()
    }

    private fun initTapResearchSdk() {
        TapResearch.initialize(
            apiToken = getString(R.string.api_token),
            userIdentifier = getString(R.string.test_user),
            activity = this@MainActivity,
            rewardCallback = { rewards -> showRewardToast(rewards) },
            errorCallback = { trError -> showErrorToast(trError) },
            sdkReadyCallback = {
                Log.d(LOG_TAG, "SDK is ready")
                doSetContent()
            },
        )
    }

    private fun showRewardToast(rewards: MutableList<TRReward>) {
        var rewardCount = 0f
        for (reward: TRReward in rewards) {
            reward.rewardAmount?.let { rewardCount += it }
        }

        val currencyName = rewards.first().currencyName
        Toast.makeText(
            this@MainActivity,
            "Congrats! You've earned $rewardCount $currencyName!",
            Toast.LENGTH_LONG,
        ).show()
    }

    private fun showErrorToast(trError: TRError) {
        Toast.makeText(
            this@MainActivity,
            "Error: ${trError.description}",
            Toast.LENGTH_LONG,
        ).show()
    }

    private fun doSetContent() {
        val buttonOptions = resources.getStringArray(R.array.placement_tags)
        val buttonNames = resources.getStringArray(R.array.placement_tag_names)

        // Swap these in to test without any options
        // val buttonOptions = emptyList<String>()
        // val buttonNames = emptyList<String>()

        setContent {
            TapResearchTakeHomeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainUi(
                        isSdkInitialized = true,
                        buttonOptions = buttonOptions.toList(),
                        buttonNames = buttonNames.toList(),
                        openPlacement = { placementTag, onContentDismissed ->
                            openPlacement(placementTag, onContentDismissed)
                        },
                        onSetUserIdentifier = { id ->
                            TapResearch.setUserIdentifier(id)
                        }
                    )
                }
            }
        }
    }

    private fun openPlacement(
        placementTag: String,
        onContentDismissed: (() -> Unit)? = null,
    ) {
        if (TapResearch.canShowContentForPlacement(
                placementTag,
            ) { trError -> trError.description?.let { Log.e(LOG_TAG, it) } }
        ) {
            val customParameters: HashMap<String, Any> = HashMap()
            customParameters["age"] = 25
            customParameters["VIP"] = "true"
            customParameters["name"] = "John Doe"

            TapResearch.showContentForPlacement(
                tag = placementTag,
                application = application,
                contentCallback = object : TRContentCallback {
                    override fun onTapResearchContentDismissed(placementTag: String) {
                        onContentDismissed?.invoke()
                    }

                    override fun onTapResearchContentShown(placementTag: String) {}
                },
                customParameters = customParameters,
            ) { trError -> showErrorToast(trError) }
        }
    }

    companion object {
        const val LOG_TAG = "TRTakeHome"
    }
}