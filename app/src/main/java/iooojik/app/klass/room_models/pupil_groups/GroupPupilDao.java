package iooojik.app.klass.room_models.pupil_groups;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import iooojik.app.klass.AppСonstants;


@Dao
public interface GroupPupilDao {
    @Query("SELECT * FROM " + AppСonstants.TABLE_PUPIL_GROUPS)
    List<GroupPupilEntity> getAll();

    @Query("SELECT * FROM " + AppСonstants.TABLE_PUPIL_GROUPS + " WHERE group_id = :test_id")
    GroupPupilEntity getById(long test_id);

    @Insert
    void insert(GroupPupilEntity groupPupilEntity);

    @Update
    void update(GroupPupilEntity groupPupilEntity);

    @Delete
    void delete(GroupPupilEntity groupPupilEntity);

    @Query("DELETE FROM " + AppСonstants.TABLE_PUPIL_GROUPS)
    void deleteAll();
}
