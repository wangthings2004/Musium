import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jcxdc.musium.db.Playlist
import com.jcxdc.musium.db.PlaylistDao
import com.jcxdc.musium.db.Song
import com.jcxdc.musium.db.SongDao
import com.jcxdc.musium.db.UserDao
import com.jcxdc.musium.db.UserEntity
import com.jcxdc.musium.utils.Constants.USER_DATABASE

@Database(
    entities = [UserEntity::class, Playlist::class, Song::class],
    version = 2
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun songDao(): SongDao

    companion object {
        @Volatile
        private var instance: UserDatabase? = null
        private val migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add migration logic here. For example:
                // Add a new column to the playlist table
                database.execSQL("ALTER TABLE playlist_table ADD COLUMN description TEXT")
            }
        }

        fun getInstance(context: Context): UserDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    USER_DATABASE
                )
                    .addMigrations(migration_1_2) // Add the migration here
                    .build().also { instance = it }
            }
        }
    }
}
