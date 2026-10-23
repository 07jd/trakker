package com.jd.trakker.ui.feed.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jd.trakker.R

@Composable
fun SearchBar(showingSearchResults: Boolean, placeholderText: String, onSearch: (String) -> Unit, onBack: () -> Unit) {
    val focus                   = LocalFocusManager.current
    val state                   = rememberTextFieldState()
    var isFocused               by remember{ mutableStateOf(false) }

    // TODO: Improve this, i couldn't find a viable way of getting if the keyboard is open or not, this is kinda of a hack
    // On back gesture remove focus from text field
    // PS: The first back gesture is captured by the OS keyboard, meaning only after hiding the keyboard
    //     the user can back gesture again to remove focus from text field
    BackHandler(enabled = isFocused) {
        focus.clearFocus()
        isFocused = false
    }

    TextField(
        modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (showingSearchResults) 15.dp else 5.dp, horizontal = 15.dp)
                .onFocusChanged { isFocused = it.isFocused },
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        placeholder = { Text(placeholderText) },
        shape = MaterialTheme.shapes.small,
        leadingIcon = {
            if (showingSearchResults) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.chevron_double_left),
                    contentDescription = stringResource(R.string.selected),
                    modifier = Modifier
                            .size(FilterChipDefaults.IconSize)
                            .clickable {
                                state.clearText()
                                onBack()
                            })
            } else {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.search),
                    contentDescription = stringResource(R.string.search_icon),
                )
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        onKeyboardAction = {
            val userInput = state.text.toString().trim()
            focus.clearFocus()
            if (!userInput.isEmpty()) {
                onSearch(userInput)
            }
        })
}