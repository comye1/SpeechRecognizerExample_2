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
# UI Controller & ViewModel
## MainActivity.kt 
NavHostFragment가 네비게이션 그래프(화면의 이동을 정의) 대로 프래그먼트를 보여준다.
액션바를 설정하고 Up 버튼 ( <- 모양) 이 네비게이션에 따라 동작하도록 한다.

## MainFragment.kt
제일 먼저 보이는 프래그먼트이다. 
UI의 업데이트를 담당하여, 클릭 등의 이벤트를 수신한다.
뷰모델의 LiveData를 observe하여 데이터가 변경될 때 바로 필요한 UI 동작을 수행한다.
오디오 권한 확인과 SpeechRecognizer의 생성, 실행을 담당한다.

## MainViewModel.kt
뷰(UI)와 관련된 데이터를 관리한다. 프래그먼트에서 변화를 즉각적으로 알 수 있도록 LiveData<T> 타입의 값을 사용한다.
- 음성 인식 결과로 텍스트뷰에 표시되는 speechText (LiveData<String>)
- ListFragment로 이동할 지 여부를 나타내는 navigateToList (LiveData<Boolean>)
- 기록이 저장되면 토스트 메시지를 보여주기 위한 onRecordSaved (LiveData<Boolean>)
이 있고, 이 LiveData 값을 바꾸는 함수들이 있다.
    
    
**데이터베이스 연산을 뷰모델에서 호출한다.**
- 뷰모델 생성 시 생성자로 dao를 전달받아 viewModelScope에서 dao의 함수를 호출한다.

## ListFragment.kt
## ListViewModel.kt
