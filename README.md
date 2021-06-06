# Android Architecture Components
![image](https://user-images.githubusercontent.com/50735594/120812898-61162f00-c588-11eb-8338-a6142771129f.png)

# RoomDatabase
## Entity 
데이터베이스 테이블에 들어갈 엔티티를 표현하는 데이터 클래스
클래스의 멤버는 엔티티의 속성(attribute)를 표현한다.
### data / Record.kt
```
@Entity(tableName = "record_table") // Record는 "record_table" 테이블을 구성하는 엔티티 
data class Record (
    val speech: String, // speech 속성
    val created: Long = System.currentTimeMillis(), // created 속성
    @PrimaryKey(autoGenerate = true) val id: Int = 0) { // id 속성, primary key
    val createdDateFormatted : String // createdDatedFormatted 속성
        get() = DateFormat.getDateTimeInstance().format(created) // - created를 포맷하여 반환
}
```
- Record 인스턴스의 createdDateFormatted로 접근하여 포맷된 생성날짜와 시간을 얻을 수 있다.
## DAO (Data Access Objects)
복잡한 쿼리를 간단한 메소드 호출로 수행할 수 있도록 하는 객체이다.
인터페이스로 작성하면 Room이 컴파일 시간에 이를 구현한다.

Insert, Update, Delete는 어노테이션만으로 쿼리가 생성되며,
직접 Query 문을 작성할 수도 있다.

### data / RecordDao.kt
```
@Dao
interface RecordDao {

    @Query("SELECT * FROM record_table") // "record_table"에서 모든 컬럼을 조회한다.
    fun getRecords() : LiveData<List<Record>> // getRecords()를 호출하면 된다.

    @Insert(onConflict = OnConflictStrategy.REPLACE) // "record_table"에 컬럼을 추가한다.
    suspend fun insert(record : Record) // insert(record)를 호출하면 된다.
}
```
## Database
abstract database holder class : 데이터베이스를 담게 된다.
Room이 이 추상 클래스를 구현한다.
데이터베이스 인스턴스를 생성하거나 / 이미 생성된 경우 이를 반환하는 메소드를 가진다. -> getInstance()
앱 전체에서 하나의 Room database 인스턴스만 갖도록 하기 위해 싱글톤으로 만든다.
### data / RecordDatabase.kt
```
@Database(entities = [Record::class], version = 1)
abstract class RecordDatabase : RoomDatabase() {
    abstract val recordDao : RecordDao // DAO 클래스를 내부에 가진다.

    companion object{ // 추상 메소드 또는 추상 프로퍼티를 정의한다.
        @Volatile // 캐시되지 않음 -> 항상 최신 상태로 유지된다.
        private var INSTANCE : RecordDatabase? = null // 데이터베이스 참조를 저장한다.

        fun getInstance(context: Context) : RecordDatabase {
            synchronized(this){
                var instance = INSTANCE 

                if(instance == null){ 
                    // 데이터베이스 참조를 가져온다.
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecordDatabase::class.java,
                        "speech_record_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
```
