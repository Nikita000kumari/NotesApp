package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.*
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.navigation.compose.*
import androidx.compose.foundation.clickable
import android.net.Uri


import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
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

                val navController = rememberNavController()

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("add")
                            }
                        ) {
                            Text("+")
                        }
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "list",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable("list") {
                            NotesScreen(
                                dao = db.noteDao(),
                                navController = navController
                            )
                        }

                        composable("add") {
                            AddNoteScreen(
                                navController = navController,
                                dao = db.noteDao()
                            )
                        }

                        composable("edit/{id}/{text}") { backStackEntry ->

                            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
                            val text = backStackEntry.arguments?.getString("text") ?: ""

                            AddNoteScreen(
                                navController = navController,
                                dao = db.noteDao(),
                                noteId = id,
                                existingText = text
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
    fun NotesScreen(
        dao: NoteDao,
        navController: NavController,
        modifier: Modifier = Modifier
    ) {
    var noteText by remember { mutableStateOf("") }

    val viewModel: NotesViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel(
            factory = NotesViewModelFactory(dao)
        )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "My Notes",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = { Text("Enter note here") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (noteText.isNotBlank()) {
                    viewModel.addNote(noteText)
                    noteText = ""
                }
            }
        ) {
            Text("Add Note")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            itemsIndexed(viewModel.notes) { index, note ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "${index + 1}. ${note.text}",
                        modifier = Modifier.clickable {
                            navController.navigate(
                                "edit/${note.id}/${Uri.encode(note.text)}"
                            )
                        }
                    )

                    Button(
                        onClick = {
                            viewModel.deleteNote(note)
                        }
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
@Composable
fun AddNoteScreen(
    navController: NavController,
    dao: NoteDao,
    noteId: Int = 0,
    existingText: String = ""
) {
    var noteText by remember { mutableStateOf(existingText) }
        val viewModel: NotesViewModel =
            androidx.lifecycle.viewmodel.compose.viewModel(
                factory = NotesViewModelFactory(dao)
            )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text("Add Note", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = noteText,
                onValueChange = { noteText = it },
                placeholder = { Text("Enter note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (noteId == 0) {
                        viewModel.addNote(noteText)
                    } else {
                        viewModel.updateNote(
                            Note(id = noteId, text = noteText)
                        )
                    }
                    navController.popBackStack()
                }
            ) {
                Text("Save")
            }
        }
    }
@Preview(showBackground = true)
@Composable
fun PreviewNotesScreen() {
    NotesAppTheme {
        Text("Preview not available with DB")
    }
}