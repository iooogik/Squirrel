package iooojik.app.klass.room_models.todo_list;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import iooojik.app.klass.AppСonstants;

@Dao
public interface TodoDao {
    @Query("SELECT * FROM " + AppСonstants.TABLE_TODO_NAME)
    List<TodoEntity> getAll();

    @Query("SELECT * FROM " + AppСonstants.TABLE_TODO_NAME + " WHERE _id = :id")
    TodoEntity getById(long id);

    @Insert
    void insert(TodoEntity todoEntity);

    @Update
    void update(TodoEntity todoEntity);

    @Delete
    void delete(TodoEntity todoEntity);

    @Query("DELETE FROM " + AppСonstants.TABLE_TESTS)
    void deleteAll();
}
