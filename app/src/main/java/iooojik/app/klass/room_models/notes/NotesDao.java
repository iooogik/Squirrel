package iooojik.app.klass.room_models.notes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import iooojik.app.klass.AppСonstants;

@Dao
public interface NotesDao {
    @Query("SELECT * FROM " + AppСonstants.TABLE_NOTES)
    List<NoteEntity> getAll();

    @Query("SELECT * FROM " + AppСonstants.TABLE_NOTES + " WHERE _id = :id")
    NoteEntity getById(long id);

    @Insert
    void insert(NoteEntity noteEntity);

    @Update
    void update(NoteEntity noteEntity);

    @Delete
    void delete(NoteEntity noteEntity);

    @Query("DELETE FROM " + AppСonstants.TABLE_TESTS)
    void deleteAll();
}
