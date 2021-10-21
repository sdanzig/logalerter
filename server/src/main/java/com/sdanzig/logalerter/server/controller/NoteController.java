package com.sdanzig.logalerter.server.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sdanzig.logalerter.server.entities.NoteEntity;
import com.sdanzig.logalerter.server.exception.ResourceNotFoundException;
import com.sdanzig.logalerter.server.repository.NoteRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class NoteController {
	Logger logger = LoggerFactory.getLogger(NoteController.class);

	private final NoteRepository noteRepository;

	@Autowired
	public NoteController(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}

	@GetMapping("/alerts/{alertId}/notes")
	public List<NoteEntity> getNotesForAlert(@PathVariable Long alertId) {
		NoteEntity note = new NoteEntity();
		note.setAlertId(alertId);
		return noteRepository.findByAlertId(alertId);
	}

	@PostMapping("/alerts/{alertId}/notes")
	public NoteEntity addNote(@PathVariable Long alertId,
			@RequestParam("email") String email,
			@RequestParam("info") String info) {
		NoteEntity note = new NoteEntity();
		note.setEmail(email);
		note.setInfo(info);
		note.setAlertId(alertId);
		return noteRepository.save(note);
	}

	@DeleteMapping("/alerts/{alertId}/notes/{noteId}")
	public ResponseEntity<?> deleteAlert(@PathVariable Long alertId,
			@PathVariable Long noteId) {
		return noteRepository.findById(noteId)
				.map(note -> {
					if(note.getAlertId() != alertId) {
						logger.warn("When deleting note {}, alertId provided, {}, does not match note's alertId, {}",
								noteId, alertId, note.getAlertId());
					}
					noteRepository.delete(note);
					return ResponseEntity.ok().build();
				}).orElseThrow(() -> new ResourceNotFoundException(
						"NoteEntity not found with id " + noteId + " for alert with id " + alertId));
	}
}