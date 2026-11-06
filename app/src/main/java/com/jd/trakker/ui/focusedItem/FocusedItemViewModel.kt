package com.jd.trakker.ui.focusedItem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.data.local.item.Item
import com.jd.trakker.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusedItemViewModel(
    private val item: Item,
    private val itemRepository: ItemRepository,
    private val goBack: () -> Unit
): ViewModel() {
    private val _state = MutableStateFlow(FocusedItemState(item))
    val uiState = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val trackedItem = itemRepository.getTracked(item)
            _state.update { it.copy(loading = false, trackedItem = trackedItem) }
        }
    }

    fun onEvent(event: FocusedItemEvent) {
        viewModelScope.launch {
            when(event) {
                FocusedItemEvent.GoBack -> {
                    itemRepository.upsert(_state.value.trackedItem!!)
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

                    itemRepository.upsert(_state.value.trackedItem!!)
                }
                FocusedItemEvent.Favorite       -> {
                    _state.update { it.copy(
                        trackedItem = _state.value.trackedItem!!.copy(favorite = !_state.value.trackedItem!!.favorite))
                    }

                    itemRepository.upsert(_state.value.trackedItem!!)
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

                    itemRepository.upsert(_state.value.trackedItem!!)
                }
                is FocusedItemEvent.Rate        -> {
                    _state.update { it.copy(
                        trackedItem = _state.value.trackedItem!!.copy(
                            rating = event.rating
                        )
                    ) }

                    itemRepository.upsert(_state.value.trackedItem!!)
                }
            }
        }
    }
}