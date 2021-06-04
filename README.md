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
