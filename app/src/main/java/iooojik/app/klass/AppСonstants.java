package iooojik.app.klass;

import android.annotation.SuppressLint;
import android.hardware.Camera;
//класс с константами
public class AppСonstants {

    // идентификатор уведомления
    static int NOTIFY_ID = 101;
    // название настроек
    public static final String APP_PREFERENCES = "Settings";
    // тема приложения
    public static final String APP_PREFERENCES_THEME = "Theme";
    // показывать ли доп. материалы в заметках
    public static final String APP_PREFERENCES_SHOW_BOOK_MATERIALS = "Show Book Materials";
    // имя файла с бд
    static String DB_NAME = "data.db";
    // путь к бд
    static String DB_PATH;
    // версия бд
    static int DB_VERSION = 3;
    // код запроса для намерения для обработки обновления play services, если это необходимо
    public static final int RC_HANDLE_GMS = 9001;
    // код разрешения
    public static final int RC_HANDLE_CAMERA_PERM = 2;
    //автофокус
    public static final String AutoFocus = "AutoFocus";
    //фонарик
    public static final String UseFlash = "UseFlash";
    //сам QR-объект
    public static final String BarcodeObject = "Barcode";
    @SuppressLint("InlinedApi")
    public static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    @SuppressLint("InlinedApi")
    public static final int CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
    public static final String TAG = "OpenCameraSource";
    public static final int DUMMY_TEXTURE_NAME = 100;
    public static final float ASPECT_RATIO_TOLERANCE = 0.01f;

    //базовый url, чтобы получать данные с сервера
    public static final String BASE_URL = "http://195.19.44.146/service/";
    //url, чтобы получать пользовательские картинки
    public static final String IMAGE_URL = "http://195.19.44.146/service/uploads/user/";
    //ключ апи
    public static final String X_API_KEY = "33CD9CAE7D5D15F7E435A26BBFF81A4E";
    //названия настроек
    //пользовательский токен
    public static String AUTH_SAVED_TOKEN = "AUTH_TOKEN";
    //админский токен
    public static String STANDART_TOKEN = "STANDART_TOKEN";
    //пользовательский id
    public static String USER_ID = "USER_ID";
    //пользовательский пароль
    public static String USER_PASSWORD = "USER_PASSWORD";
    //логин пользователя
    public static String USER_LOGIN = "USER_LOGIN";
    //email пользователя
    public static String USER_EMAIL = "USER_EMAIL";
    //аватар
    public static String USER_AVATAR = "USER_AVATAR";
    //тип аккаунта
    public static String USER_ROLE = "USER_ROLE";
    //полное имя пользователя
    public static String USER_FULL_NAME = "USER_FULL_NAME";
    //количество койнов пользователя
    public static String USER_COINS = "USER_COINS";
    //id "достижений" пользователя
    public static String ACHIEVEMENTS_ID = "ACHIEVEMENTS_ID" ;
    //админский email
    static final String adminEmail = "test@test.com";
    //админский пароль
    static final String adminPassword = "123456";
    //специальный символ для тестов
    public static final String testDivider = "{#$#}";
    //переменная, индентифицируящая выбранную пользователем картинку
    public static final int PICK_IMAGE_AVATAR = 9;
    //табоица с тестами
    public static final String TABLE_TESTS = "Tests";
    //название колонок в табоице Tests////////////////////////////////////////////////////
    public static final String TABLE_ID = "_id";
    public static final String TABLE_TESTS_NAME = "name";
    public static final String TABLE_TESTS_DESCRIPTION = "description";
    public static final String TABLE_TESTS_IS_PASSED = "isPassed";
    public static final String TABLE_TESTS_QUESTIONS = "questions";
    public static final String TABLE_TESTS_ANSWERS = "answers";
    public static final String TABLE_TESTS_TEXT_ANSWERS = "textAnswers";
    public static final String TABLE_TESTS_TIME = "time";
    public static final String TABLE_TESTS_TOTAL_SCORE = "totalScore";
    public static final String TABLE_TESTS_USER_SCORE = "userScore";
    public static final String TABLE_TESTS_SCORE_QUEST = "scoreForQuest";
    public static final String TABLE_TESTS_GROUP_ID = "group_id";
    ///////////////////////////////////////////////////////////////////////////////////
    //id группы, в которой находится пользователь
    public static String USER_CURR_GROUP_ID = "USER_CURR_GROUP_ID";
    //названия колонок в таблце TodoList
    public static  final String TABLE_TODO_NAME = "todo_list";
    public static  final String TABLE_TODO_TEXT = "text";
    public static  final String TABLE_TODO_CHECKED = "checked";
}
