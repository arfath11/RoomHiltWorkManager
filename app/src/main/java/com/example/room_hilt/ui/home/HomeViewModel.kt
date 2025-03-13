package com.example.room_hilt.ui.home
import com.example.room_hilt.data.MyItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.room_hilt.domain.usecase.AddItemUseCase
import com.example.room_hilt.domain.usecase.DeleteItemUseCase
import com.example.room_hilt.domain.usecase.GetItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.room_hilt.domain.model.Result

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val addItemUseCase: AddItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemUiState>(ItemUiState.Loading)
    val uiState: StateFlow<ItemUiState> = _uiState

    init {
        getItems()
    }

    private fun getItems() {
        viewModelScope.launch {
            getItemsUseCase().collect { result ->
                _uiState.value = when (result) {
                    is Result.Success -> ItemUiState.Success(result.data)
                    is Result.Loading -> ItemUiState.Loading
                    is Result.Error -> ItemUiState.Error(
                        result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun addItem(item: MyItem) {
        viewModelScope.launch {

            when (val result = addItemUseCase(item)) {
                is Result.Success -> "success"
                is Result.Loading -> ItemUiState.Loading
                is Result.Error -> _uiState.value =
                    ItemUiState.Error(result.exception.message ?: "Failed to add item")
            }
        }
    }

    fun deleteAllItem() {
        viewModelScope.launch {
            when (val result = deleteItemUseCase()) {
                is Result.Success -> "success"
                is Result.Loading -> ItemUiState.Loading
                is Result.Error -> _uiState.value =
                    ItemUiState.Error(result.exception.message ?: "Failed to delete item")
            }
        }
    }
}
