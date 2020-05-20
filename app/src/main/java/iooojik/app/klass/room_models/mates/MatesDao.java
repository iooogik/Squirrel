package iooojik.app.klass.room_models.mates;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import iooojik.app.klass.AppСonstants;

@Dao
public interface MatesDao {
    @Query("SELECT * FROM " + AppСonstants.TABLE_MATES_LIST)
    List<MateEntity> getAll();

    @Query("SELECT * FROM " + AppСonstants.TABLE_MATES_LIST + " WHERE mate_group_id = :mate_group_id")
    List<MateEntity> getAllByGroupId(long mate_group_id);

    @Query("SELECT * FROM " + AppСonstants.TABLE_MATES_LIST + " WHERE mate_id = :mate_id")
    MateEntity getById(long mate_id);

    @Insert
    void insert(MateEntity mateEntity);

    @Update
    void update(MateEntity mateEntity);

    @Delete
    void delete(MateEntity mateEntity);

    @Query("DELETE FROM " + AppСonstants.TABLE_MATES_LIST)
    void deleteAll();
}
