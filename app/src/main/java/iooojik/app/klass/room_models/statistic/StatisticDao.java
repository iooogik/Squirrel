package iooojik.app.klass.room_models.statistic;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import iooojik.app.klass.AppСonstants;

@Dao
public interface StatisticDao {

    @Query("SELECT * FROM " + AppСonstants.TABLE_STATISTIC)
    List<StatisticEntity> getAll();

    @Query("SELECT * FROM " + AppСonstants.TABLE_STATISTIC + " WHERE _id = :id")
    StatisticEntity getById(long id);

    @Insert
    void insert(StatisticEntity statisticEntity);

    @Update
    void update(StatisticEntity statisticEntity);

    @Delete
    void delete(StatisticEntity statisticEntity);
}
