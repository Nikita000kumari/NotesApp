package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.Delete

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.tooling.preview.Preview

import androidx.room.Room

import com.example.notesapp.ui.theme.NotesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            "notes_db"
        ).build()

        enableEdgeToEdge()

        setContent {
            NotesAppTheme {
                NotesScreen(dao = db.noteDao())
            }
        }
    }
}

@Composable
fun NotesScreen(
    dao: NoteDao,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }

    val viewModel: NotesViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel(
            factory = NotesViewModelFactory(dao)
        )

    var noteText by remember { mutableStateOf("") }
    var showInput by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        topBar = {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "My Notes",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

            }
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { showInput = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        )  {
            // 🔍 Search
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search notes...") },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Tap a note to edit",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            // ➕ Add input
            if (showInput) {

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("Enter your note...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            viewModel.addNote(noteText)
                            noteText = ""
                            showInput = false
                        }
                    }
                ) {
                    Text("Save")
                }
            }

            val filteredNotes = viewModel.notes.filter {
                it.text.contains(searchText, ignoreCase = true)
            }

            if (filteredNotes.isEmpty()) {
                Text(
                    text = "No notes yet.\nTap + to add one!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp), // space for FAB
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(filteredNotes) { _, note ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(10.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            if (editingNote == note) {

                                var editText by remember { mutableStateOf(note.text) }

                                OutlinedTextField(
                                    value = editText,
                                    onValueChange = { editText = it },
                                    modifier = Modifier.weight(1f)
                                )

                                Button(
                                    onClick = {
                                        viewModel.updateNote(note.copy(text = editText))
                                        editingNote = null
                                    }
                                ) {
                                    Text("Save")
                                }

                            } else {

                                Text(
                                    text = note.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 2,
                                    modifier = Modifier
                                        .weight(1f)   // 🔥 VERY IMPORTANT
                                        .clickable {
                                            editingNote = note
                                        }
                                )

                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.clickable {
                                        viewModel.deleteNote(note)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewNotesScreen() {
    NotesAppTheme {
        Text("Preview not connected to DB (expected)")
    }
}