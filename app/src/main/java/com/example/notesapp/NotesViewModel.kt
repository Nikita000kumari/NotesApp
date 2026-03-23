package com.example.notesapp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf

class NotesViewModel(private val dao: NoteDao) : ViewModel() {

    var notes = mutableStateListOf<Note>()
        private set

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            val savedNotes = dao.getAllNotes()
            notes.clear()
            notes.addAll(savedNotes)
        }
    }


    fun addNote(text: String) {
        viewModelScope.launch {
            dao.insert(Note(text = text))
            loadNotes()
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            dao.update(note)
            loadNotes()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            dao.delete(note)
            loadNotes()
        }
    }
}