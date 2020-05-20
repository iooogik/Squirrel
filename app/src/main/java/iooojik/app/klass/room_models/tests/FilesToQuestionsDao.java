package iooojik.app.klass.room_models.tests;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import iooojik.app.klass.AppСonstants;

@Dao
public interface FilesToQuestionsDao {
    @Query("SELECT * FROM " + AppСonstants.TABLE_FILES_TO_QUESTIONS)
    List<FilesToQuestionsEntity> getAll();

    @Query("SELECT * FROM " + AppСonstants.TABLE_FILES_TO_QUESTIONS + " WHERE test_id = :id")
    FilesToQuestionsEntity getById(long id);

    @Insert
    void insert(FilesToQuestionsEntity filesToQuestionsEntity);

    @Update
    void update(FilesToQuestionsEntity filesToQuestionsEntity);

    @Delete
    void delete(FilesToQuestionsEntity filesToQuestionsEntity);

    @Query("DELETE FROM " + AppСonstants.TABLE_FILES_TO_QUESTIONS)
    void deleteAll();
}
