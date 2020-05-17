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
    static int DB_VERSION = 9;
    // код запроса для намерения для обработки обновления play services, если это необходимо
    public static final int RC_HANDLE_GMS = 9001;
    // код разрешения
    public static final int RC_HANDLE_CAMERA_PERM = 2;
    //автофокус
    public static final String AutoFocus = "AutoFocus";
    //фонарик
    public static final String UseFlash = "UseFlash";
    @SuppressLint("InlinedApi")
    public static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    @SuppressLint("InlinedApi")
    public static final int CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
    public static final String TAG = "OpenCameraSource";
    public static final int DUMMY_TEXTURE_NAME = 100;
    public static final float ASPECT_RATIO_TOLERANCE = 0.01f;

    //базовый url, чтобы получать данные с сервера
    public static final String BASE_URL = "http://195.19.44.146:89/";
    //url, чтобы получать пользовательские картинки
    public static final String IMAGE_URL = "http://195.19.44.146:89/uploads/user/";
    public static final String NEW_API_URL = "http://188.127.254.149:8008/klass/";
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
    public static final String adminEmail = "test@test.com";
    //админский пароль
    public static final String adminPassword = "123456";
    //специальный символ для тестов
    public static final String testDivider = "{#$#}";
    //переменная, индентифицируящая выбранную пользователем картинку
    public static final int PICK_IMAGE_AVATAR = 9;
    //переменная, индентифицируящий выбранный пользователем файл
    public static final int PICK_FILE = 10;
    //таблицы
    public static final String TABLE_TESTS = "Tests";
    public static final String TABLE_NOTES = "Notes";
    public static final String TABLE_FILES_TO_QUESTIONS = "files_to_questions";
    //название колонок ////////////////////////////////////////////////////
    public static final String TABLE_ID = "_id";
    public static final String TABLE_NAME = "name";
    public static final String TABLE_DESCRIPTION = "description";
    public static final String TABLE_IS_PASSED = "isPassed";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_ANSWERS = "answers";
    public static final String TABLE_TEXT_ANSWERS = "textAnswers";
    public static final String TABLE_TIME = "time";
    public static final String TABLE_TOTAL_SCORE = "totalScore";
    public static final String TABLE_USER_SCORE = "userScore";
    public static final String TABLE_SCORE_QUEST = "scoreForQuest";
    public static final String TABLE_GROUP_ID = "group_id";
    public static final String TABLE_IMAGE = "image";
    public static final String TABLE_PERM_TO_SYNC = "permToSync";
    public static final String TABLE_IS_NOTIF_SET = "isNotifSet";
    //public static final String TABLE_SHORT_NOTE = "shortNote";
    public static final String TABLE_SHORT_NAME = "shortName";
    public static final String TABLE_TYPE = "type";
    public static final String TABLE_DB_TYPE_SHOP = "shop";
    public static final String TABLE_DB_TYPE_STNDRT = "standart";
    public static final String TABLE_IS_CHECKED = "isChecked";
    public static final String TABLE_POINTS = "points";
    public static final String TABLE_IS_COMPLETED = "isCompleted";
    public static final String TABLE_DECODE_QR = "decodeQR";
    public static final String TABLE_TYPEFACE = "typeface";
    public static final String TABLE_FONT_SIZE = "fontSize";
    public static final String TABLE_RESULT = "result";
    public static final String TABLE_SCORES = "scores";
    public static final String TABLE_MAX_SCORE = "max_score";
    public static final String TABLE_TEST_ID = "test_id";
    public static final String TABLE_QUESTION_NUM = "question_num";
    public static final String TABLE_FILE_URL = "file_url";
    ///////////////////////////////////////////////////////////////////////////////////
    //id группы, в которой находится пользователь
    public static String USER_CURR_GROUP_ID = "USER_CURR_GROUP_ID";
    //названия колонок в таблце TodoList
    public static  final String TABLE_TODO_NAME = "todo_list";
    public static  final String TABLE_TODO_CHECKED = "checked";
    public static  final String TABLE_TEXT = "text";
    public static  final String SHOW_GROUP_ID = "SHOW_GROUP_ID";
    //weather api key
    public static final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String WEATHER_API_KEY = "d98807cde5d96afd3382d7d8ef923ea6";
    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String USER_LAT = "USER_LAT";
    public static final String USER_LON = "USER_LON";
    public static final String SHOW_WEATHER_NOTIF = "SHOW_WEATHER_NOTIF";
    public static final String YANDEX_TRANSLATE_API_KEY = "trnsl.1.1.20200504T150254Z.de2d9abf855314c5.22a15b3c1fad2d9bdb3e676aa3abf9d3ec67dbc0";
    public static final String YANDEX_TRANSLATE_BASE_URL = "https://translate.yandex.net/";
    public static final String IOOOJIK_BASE_URL = "http://www.iooojik.ru/";

    public static String CASES = "CASES";

    //online-db fields
    public static final String USER_EMAIL_FIELD = "user_email";
    public static final String ID_FIELD = "_id";
    public static final String COINS_FIELD = "coins";
    public static final String LAT_FIELD = "latitude";
    public static final String LOT_FIELD = "longitude";
    public static final String COUNT_FIELD = "count";
    public static final String DATE_FIELD = "date";
    public static final String ITEM_ID_FIELD = "item_id";
    public static final String LOG_FIELD = "log";
    public static final String ACTIVATED_FIELD = "activated";
    public static final String PROMO_FIELD = "promo";
    public static final String GROUP_ID_FIELD = "group_id";
    public static final String EMAIL_FIELD = "email";
    public static final String AUTHOR_EMAIL_FIELD = "author_email";
    public static final String USER_ID_FIELD = "user_id";
    public static final String FULL_NAME_FIELD = "full_name";
    public static final String GROUP_NAME_FIELD = "group_name";
    public static final String AVATAR_FIELD = "avatar";
    public static final String MESSAGE_FIELD = "message";
    public static final String TEST_NAME_FIELD = "test_name";
    public static final String DIFFICULTIES_FIELD = "difficultiesCount";
    public static final String FILE_URL_FIELD = "file_url";

}
