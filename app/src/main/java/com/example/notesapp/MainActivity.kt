package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

import com.example.notesapp.ui.theme.NotesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   NotesScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun NotesScreen(modifier:Modifier = Modifier) {

    var noteText by remember { mutableStateOf("") }
    val notes = remember { mutableStateListOf<String>() }
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
                    notes.add(noteText)
                    noteText = ""
                }
            }
        ) {
            Text("Add Note")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // list display code
        LazyColumn {
            itemsIndexed(notes) { index, note ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "${index + 1}. $note",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = {
                            notes.removeAt(index)
                        }
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesAppTheme {
        NotesScreen()
    }
}