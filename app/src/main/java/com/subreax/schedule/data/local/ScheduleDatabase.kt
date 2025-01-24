package com.subreax.schedule.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.subreax.schedule.data.local.dao.BookmarkDao
import com.subreax.schedule.data.local.dao.ScheduleInfoDao
import com.subreax.schedule.data.local.dao.SubjectDao
import com.subreax.schedule.data.local.dao.SubjectNameDao
import com.subreax.schedule.data.local.dao.TeacherNameDao
import com.subreax.schedule.data.local.entitiy.ScheduleInfoEntity
import com.subreax.schedule.data.local.entitiy.BookmarkEntity
import com.subreax.schedule.data.local.entitiy.SubjectEntity
import com.subreax.schedule.data.local.entitiy.SubjectNameEntity
import com.subreax.schedule.data.local.entitiy.TeacherNameEntity

@Database(
    entities = [
        SubjectEntity::class,
        SubjectNameEntity::class,
        TeacherNameEntity::class,
        BookmarkEntity::class,
        ScheduleInfoEntity::class
    ],
    version = 7,
    autoMigrations = [
        AutoMigration(from = 3, to = 4)
    ]
)
@TypeConverters(RoomConverters::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract val subjectDao: SubjectDao
    abstract val subjectNameDao: SubjectNameDao
    abstract val teacherNameDao: TeacherNameDao
    abstract val bookmarkDao: BookmarkDao
    abstract val scheduleIdDao: ScheduleInfoDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    val idColumn = "id"
    val ownerIdColumn = "ownerId"
    val teacherNameIdColumn = "teacherNameId"
    val beginTimeMinsColumn = "beginTimeMins"

    override fun migrate(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT `$idColumn`, `$ownerIdColumn`, `$teacherNameIdColumn`, `$beginTimeMinsColumn` FROM `subject`")
        try {
            with(cursor) {
                val idIdx = getColumnIndexOrThrow(idColumn)
                val ownerIdIdx = getColumnIndexOrThrow(ownerIdColumn)
                val teacherNameIdIdx = getColumnIndexOrThrow(teacherNameIdColumn)
                val beginTimeMinsIdx = getColumnIndexOrThrow(beginTimeMinsColumn)

                while (moveToNext()) {
                    val oldId = getLong(idIdx)
                    val ownerId = getInt(ownerIdIdx)
                    val teacherNameId = getInt(teacherNameIdIdx)
                    val beginTimeMins = getInt(beginTimeMinsIdx)

                    val newId = SubjectEntity.buildIdV2(ownerId, beginTimeMins, teacherNameId)
                    db.execSQL("UPDATE subject SET $idColumn=$newId WHERE $idColumn=$oldId")
                }
            }
        } catch (ex: Exception) {
            db.execSQL("DELETE FROM subject")
        }
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DELETE FROM subject")
    }
}

val MIGRATION_1_3 = object : Migration(1, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DELETE FROM subject")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE subject_name SET alias = ''")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    private val bookmarks = "bookmarks"

    override fun migrate(db: SupportSQLiteDatabase) {
        bookmark_createTable(db)

        try {
            bookmark_copyFromOwnerTable(db)
        } catch (ex: Exception) {
            try {
                Firebase.crashlytics.recordException(ex)
            } catch (ignored: Exception) {}
        }

        owner_dropTable(db)
        scheduleInfo_createTable(db)
    }

    private fun bookmark_createTable(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS '$bookmarks' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'scheduleId' TEXT NOT NULL, 'type' INTEGER NOT NULL, 'name' TEXT NOT NULL)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS 'index_bookmarks_scheduleId' ON '$bookmarks' ('scheduleId')")
    }

    private fun bookmark_copyFromOwnerTable(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT networkId, type, name FROM owner")
        with (cursor) {
            val networkIdIdx = getColumnIndexOrThrow("networkId")
            val typeIdx = getColumnIndexOrThrow("type")
            val nameIdx = getColumnIndexOrThrow("name")
            while (moveToNext()) {
                val scheduleId = getString(networkIdIdx)
                val oldType = getInt(typeIdx)
                val type = oldType + 1
                val name = getString(nameIdx)
                db.execSQL("INSERT INTO $bookmarks (scheduleId, type, name) VALUES ('$scheduleId', '$type', '$name')")
            }
        }
    }

    private fun owner_dropTable(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE owner")
    }

    private fun scheduleInfo_createTable(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS 'schedule_info' ('localId' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'remoteId' TEXT NOT NULL, 'type' INTEGER NOT NULL, 'syncTime' INTEGER NOT NULL)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS 'index_schedule_info_localId' ON 'schedule_info' ('localId')")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE 'bookmarks' ADD COLUMN 'position' INTEGER DEFAULT 0 NOT NULL")

        val cursor = db.query("SELECT id FROM bookmarks")
        with (cursor) {
            val idIdx = getColumnIndexOrThrow("id")
            var i = 0
            while (moveToNext()) {
                val id = getInt(idIdx)
                db.execSQL("UPDATE bookmarks SET position = $i WHERE id = $id")
                i++
            }
        }
    }
}