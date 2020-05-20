package iooojik.app.klass.room_models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.room_models.mates.MateEntity;
import iooojik.app.klass.room_models.mates.MatesDao;
import iooojik.app.klass.room_models.notes.NoteEntity;
import iooojik.app.klass.room_models.notes.NotesDao;
import iooojik.app.klass.room_models.profile.ProfileDao;
import iooojik.app.klass.room_models.profile.ProfileEntity;
import iooojik.app.klass.room_models.pupil_groups.GroupPupilDao;
import iooojik.app.klass.room_models.pupil_groups.GroupPupilEntity;
import iooojik.app.klass.room_models.statistic.StatisticDao;
import iooojik.app.klass.room_models.statistic.StatisticEntity;
import iooojik.app.klass.room_models.tests.FilesToQuestionsDao;
import iooojik.app.klass.room_models.tests.FilesToQuestionsEntity;
import iooojik.app.klass.room_models.tests.TestDao;
import iooojik.app.klass.room_models.tests.TestEntity;
import iooojik.app.klass.room_models.tests_results.TestResultDao;
import iooojik.app.klass.room_models.tests_results.TestResultEntity;
import iooojik.app.klass.room_models.todo_list.TodoDao;
import iooojik.app.klass.room_models.todo_list.TodoEntity;

@Database(entities = {StatisticEntity.class, ProfileEntity.class, NoteEntity.class,
        TestEntity.class, TodoEntity.class, FilesToQuestionsEntity.class,
        GroupPupilEntity.class, MateEntity.class, TestResultEntity.class}, version = AppСonstants.DB_VERSION)

public abstract class AppDatabase extends RoomDatabase {
    public abstract StatisticDao statisticDao();
    public abstract ProfileDao profileDao();
    public abstract NotesDao notesDao();
    public abstract TestDao testDao();
    public abstract TodoDao todoDao();
    public abstract FilesToQuestionsDao filesToQuestionsDao();
    public abstract GroupPupilDao groupPupilDao();
    public abstract MatesDao matesDao();
    public abstract TestResultDao testResultDao();
}
