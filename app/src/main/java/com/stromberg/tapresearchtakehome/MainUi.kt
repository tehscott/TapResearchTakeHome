package com.stromberg.tapresearchtakehome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.stromberg.tapresearchtakehome.ui.theme.TapResearchTakeHomeTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainUi(
    isSdkInitialized: Boolean = false,
    openPlacement: ((placementTag: String, onContentDismissed: (() -> Unit)?) -> Unit)? = null,
    onSetUserIdentifier: ((identifier: String) -> Unit)? = null,
    buttonOptions: List<String> = emptyList(),
    buttonNames: List<String> = emptyList(),
) {
    TapResearchTakeHomeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        ) {
            val showBanner = remember { mutableStateOf(true) }

            if (isSdkInitialized) {
                if (showBanner.value) {
                    openPlacement?.invoke(stringResource(id = R.string.app_launch_placement)) {
                        showBanner.value = false
                    }
                } else {
                    buttonOptions.forEachIndexed { index, option ->
                        Button(
                            onClick = { openPlacement?.invoke(option, null) },
                            modifier = Modifier.padding(5.dp),
                        ) {
                            Text(text = buttonNames[index])
                        }
                    }

                    val userIdentifier = remember { mutableStateOf("theinterviewer-1") }
                    val keyboardController = LocalSoftwareKeyboardController.current
                    val focusManager = LocalFocusManager.current

                    TextField(
                        value = userIdentifier.value,
                        onValueChange = { userIdentifier.value = it },
                        label = { Text("User Identifier") },
                        placeholder = { Text("Enter your user identifier") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onSetUserIdentifier?.invoke(userIdentifier.value)
                            },
                        ),
                        singleLine = true,
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = "SDK initializing..."
                    )
                }
            }
        }
    }
}