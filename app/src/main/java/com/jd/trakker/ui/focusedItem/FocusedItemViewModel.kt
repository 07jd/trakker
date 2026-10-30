package com.jd.trakker.ui.focusedItem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.data.local.Item
import com.jd.trakker.data.repository.TrackedItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusedItemViewModel(
    private val item: Item,
    private val trackedItemRepository: TrackedItemRepository,
    private val goBack: () -> Unit
): ViewModel() {
    private val _state = MutableStateFlow(FocusedItemState(item))
    val uiState = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val trackedItem = trackedItemRepository.getTrackedInfo(item) ?: trackedItemRepository.createDefault(item)
            _state.update { it.copy(loading = false, trackedItem = trackedItem) }
        }
    }

    fun onEvent(event: FocusedItemEvent) {
        viewModelScope.launch {
            when(event) {
                FocusedItemEvent.GoBack -> {
                    trackedItemRepository.update(_state.value.trackedItem!!)
                    goBack()
                }
                is FocusedItemEvent.ChangeState -> {
                    _state.update { it.copy(
                        trackedItem = _state.value.trackedItem!!.copy(
                            status = event.state
                        )
                    ) }
                }
                is FocusedItemEvent.CloseNote   -> {
                    _state.update { it.copy(
                        note = null,
                        noteIndex = null,
                        trackedItem = _state.value.trackedItem?.copy(
                            notes = event.data
                        )
                    ) }

                    trackedItemRepository.update(_state.value.trackedItem!!)
                }
                FocusedItemEvent.Favorite       -> {
                    _state.update { it.copy(
                        trackedItem = _state.value.trackedItem!!.copy(favorite = !_state.value.trackedItem!!.favorite))
                    }

                    trackedItemRepository.update(_state.value.trackedItem!!)
                }
                FocusedItemEvent.NotesScreen    -> {
                    _state.update { it.copy(
                        onNotesScreen = !_state.value.onNotesScreen
                    ) }
                }
                is FocusedItemEvent.OpenNote    -> {
                    val notes = _state.value.trackedItem?.notes?.toMutableList() ?: mutableListOf()
                    val selectedNote = if(event.num == null) {
                        notes.add("" to "")
                        "" to ""
                    } else {
                        notes[event.num]
                    }
                    val index = event.num ?: notes.lastIndex

                    _state.update { it.copy(
                        noteIndex = index,
                        note = selectedNote,
                        trackedItem = _state.value.trackedItem?.copy(notes = notes)
                    ) }

                    trackedItemRepository.update(_state.value.trackedItem!!)
                }
                is FocusedItemEvent.Rate        -> {
                    _state.update { it.copy(
                        trackedItem = _state.value.trackedItem!!.copy(
                            userRating = event.rating
                        )
                    ) }

                    trackedItemRepository.update(_state.value.trackedItem!!)
                }
            }
        }
    }
}