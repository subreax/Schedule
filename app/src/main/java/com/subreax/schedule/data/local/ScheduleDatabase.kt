package com.subreax.schedule.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.subreax.schedule.data.local.dao.BookmarkDao
import com.subreax.schedule.data.local.dao.ScheduleIdDao
import com.subreax.schedule.data.local.dao.SubjectDao
import com.subreax.schedule.data.local.dao.SubjectNameDao
import com.subreax.schedule.data.local.dao.TeacherNameDao
import com.subreax.schedule.data.local.entitiy.ScheduleIdEntity
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
        ScheduleIdEntity::class
    ],
    version = 6,
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
    abstract val scheduleIdDao: ScheduleIdDao
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
        owner_renameToBookmark(db)
        bookmark_config(db)
        scheduleId_createTable(db)
        subject_renameOwnerIdToScheduleId(db)
    }

    private fun owner_renameToBookmark(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE owner RENAME TO $bookmarks")
    }

    private fun bookmark_config(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE $bookmarks RENAME COLUMN localId TO id")
        db.execSQL("ALTER TABLE $bookmarks RENAME COLUMN networkId TO scheduleId")
        db.execSQL("ALTER TABLE $bookmarks DROP COLUMN scheduleLastUpdate")

        val cursor = db.query("SELECT id, type FROM $bookmarks")
        with (cursor) {
            val idIdx = getColumnIndexOrThrow("id")
            val typeIdx = getColumnIndexOrThrow("type")
            while (moveToNext()) {
                val id = getInt(idIdx)
                val type = getInt(typeIdx)
                db.execSQL("UPDATE $bookmarks SET type = ${type + 1} WHERE id = $id")
            }
        }
    }

    private fun scheduleId_createTable(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `schedule_id` (`localId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `remoteId` TEXT NOT NULL, `type` INTEGER NOT NULL, `syncTime` INTEGER NOT NULL)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_schedule_id_localId` ON `schedule_id` (`localId`)")
    }

    private fun subject_renameOwnerIdToScheduleId(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE subject RENAME COLUMN ownerId TO scheduleId")
    }
}