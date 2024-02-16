package com.stromberg.tapresearchtakehome

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.tapresearch.tapsdk.TapResearch
import com.tapresearch.tapsdk.callback.TRContentCallback
import com.tapresearch.tapsdk.models.TRError
import com.tapresearch.tapsdk.models.TRReward

class MainViewModel : ViewModel() {
    fun initTapResearchSdk(
        activity: Activity,
        rewardCallback: (List<TRReward>) -> Unit,
        errorCallback: (TRError) -> Unit,
        sdkReadyCallback: () -> Unit,
    ) {
        TapResearch.initialize(
            apiToken = activity.getString(R.string.api_token),
            userIdentifier = activity.getString(R.string.test_user),
            activity = activity,
            rewardCallback = { rewards -> rewardCallback(rewards) },
            errorCallback = { trError -> errorCallback(trError) },
            sdkReadyCallback = { sdkReadyCallback() },
        )
    }

    fun openPlacement(
        application: Application,
        placementTag: String,
        onContentDismissed: (() -> Unit)? = null,
        errorCallback: ((TRError) -> Unit)? = null,
    ) {
        if (TapResearch.canShowContentForPlacement(
                placementTag,
            ) { trError -> trError.description?.let { Log.e(MainActivity.LOG_TAG, it) } }
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
            ) { trError -> errorCallback?.invoke(trError) }
        }
    }

    fun onSetUserIdentifier(id: String) {
        TapResearch.setUserIdentifier(id)
    }
}