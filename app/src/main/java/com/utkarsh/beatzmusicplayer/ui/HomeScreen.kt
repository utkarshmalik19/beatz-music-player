package com.utkarsh.beatzmusicplayer.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.utkarsh.beatzmusicplayer.R
import com.utkarsh.beatzmusicplayer.model.AudioFile
import com.utkarsh.beatzmusicplayer.utils.SetSystemBarsDarkTheme
import com.utkarsh.beatzmusicplayer.utils.getAlbumArt
import com.utkarsh.beatzmusicplayer.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavHostController) {
    SetSystemBarsDarkTheme()
    val songs by viewModel.audioFiles.collectAsState()
    val filteredSongs by viewModel.filteredSongs.collectAsState()
    val likedSongs by viewModel.likedSongs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSong by viewModel.selectedSongForPlaylist.collectAsState()
    val playlists by viewModel.playlists.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    Scaffold(

            topBar = {
                Column {
                TopAppBar(title = { Text("Beatz Music Player", fontWeight = FontWeight.Bold) })
                SongSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onClear = { viewModel.updateSearchQuery("") }
                )
            }
        }
    ) { padding ->
        if (selectedSong != null) {
            AddToPlaylistSheet(
                playlists = playlists,
                onCreatePlaylist = { viewModel.createPlaylist(it) },
                onAdd = { playlistName ->
                    viewModel.addSongToPlaylist(playlistName, selectedSong!!)
                },
                onDismiss = { viewModel.closeAddToPlaylistDialog() }
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
            ) {
                items(filteredSongs) { audio ->
                    SongItem(
                        song = audio,
                        isLiked = likedSongs.contains(audio),
                        onClick = { viewModel.playSong(audio) },
                        onToggleLike = { viewModel.toggleLikeSong(it) },
                        onAddToPlaylist = { viewModel.openAddToPlaylistDialog(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun SongItem(
    song: AudioFile,
    isLiked: Boolean,
    onClick: () -> Unit,
    onToggleLike: (AudioFile) -> Unit,
    onAddToPlaylist: (AudioFile) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) Color(0xFF1F1F1F) else Color(0xFFF5F5F5)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(
                onClick = onClick,
            ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            AlbumArtFromMetadata(song.data)

            Spacer(modifier = Modifier.width(12.dp))

            // Song info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = song.artist,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3-dot menu
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.Gray
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },

                    ) {
                    DropdownMenuItem(
                        text = { Text(if (isLiked) "Remove from Favourites" else "Add to Favourites") },
                        onClick = {
                            onToggleLike(song)
                            menuExpanded = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like Icon",
                                tint = if (isLiked) Color.Red else Color.Gray
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to Playlist") },
                        onClick = {
                            onAddToPlaylist(song)
                            menuExpanded = false
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.LibraryAdd, contentDescription = "Add to Playlist")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Song") },
                        onClick = { menuExpanded = false },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Add to Playlist icon",
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Share Song") },
                        onClick = { menuExpanded = false },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Add to Playlist icon",
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun AlbumArtFromMetadata(
    path: String,
    modifier: Modifier = Modifier
) {
    // Load album art off the main thread
    val bitmapState = produceState<Bitmap?>(initialValue = null, path) {
        value = getAlbumArt(path)
    }

    AsyncImage(
        model = bitmapState.value ?: R.drawable.album_placeholder,
        contentDescription = "Album Art",
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    )
}