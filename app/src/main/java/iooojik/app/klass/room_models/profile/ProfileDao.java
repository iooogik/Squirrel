package iooojik.app.klass.room_models.profile;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import iooojik.app.klass.AppСonstants;

@Dao
public interface ProfileDao {
    @Query("SELECT * FROM " + AppСonstants.TABLE_PROFILE)
    List<ProfileEntity> getAll();

    @Query("SELECT * FROM " + AppСonstants.TABLE_PROFILE + " WHERE _id = :id")
    ProfileEntity getById(long id);

    @Insert
    void insert(ProfileEntity profileEntity);

    @Update
    void update(ProfileEntity profileEntity);

    @Delete
    void delete(ProfileEntity profileEntity);
}
