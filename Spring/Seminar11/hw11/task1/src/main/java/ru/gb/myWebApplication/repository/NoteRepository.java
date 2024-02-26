package ru.gb.myWebApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.myWebApplication.model.Note;

import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository <Note, Long>{
}
