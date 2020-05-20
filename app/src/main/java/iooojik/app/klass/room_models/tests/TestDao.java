package iooojik.app.klass.room_models.tests;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import iooojik.app.klass.AppСonstants;

@Dao
public interface TestDao {
    @Query("SELECT * FROM " + AppСonstants.TABLE_TESTS)
    List<TestEntity> getAll();

    @Query("SELECT * FROM " + AppСonstants.TABLE_TESTS + " WHERE _id = :id")
    TestEntity getById(long id);

    @Insert
    void insert(TestEntity testEntity);

    @Update
    void update(TestEntity testEntity);

    @Delete
    void delete(TestEntity testEntity);

    @Query("DELETE FROM " + AppСonstants.TABLE_TESTS)
    void deleteAll();
}
