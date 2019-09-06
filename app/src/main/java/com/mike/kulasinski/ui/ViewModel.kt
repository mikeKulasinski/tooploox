package com.mike.kulasinski.ui

sealed class ViewModel {
    object Loading : ViewModel()
    object Error : ViewModel()
    object SoftError : ViewModel()
    data class PostsArrived(val posts: List<SongModel>) : ViewModel()
}