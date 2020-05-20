package iooojik.app.klass.room_models.tests_results;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import iooojik.app.klass.AppСonstants;

@Dao
public interface TestResultDao {
    @Query("SELECT * FROM " + AppСonstants.TABLE_TESTS_RESULTS)
    List<TestResultEntity> getAll();

    @Query("SELECT * FROM " + AppСonstants.TABLE_TESTS_RESULTS + " WHERE _id = :id")
    TestResultEntity getById(long id);

    @Insert
    void insert(TestResultEntity testResultEntity);

    @Update
    void update(TestResultEntity testResultEntity);

    @Delete
    void delete(TestResultEntity testResultEntity);

    @Query("DELETE FROM " + AppСonstants.TABLE_TESTS_RESULTS)
    void deleteAll();
}
