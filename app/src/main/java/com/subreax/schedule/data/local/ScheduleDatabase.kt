package com.subreax.schedule.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.subreax.schedule.data.local.dao.OwnerDao
import com.subreax.schedule.data.local.dao.SubjectDao
import com.subreax.schedule.data.local.dao.SubjectNameDao
import com.subreax.schedule.data.local.dao.TeacherNameDao
import com.subreax.schedule.data.local.entitiy.LocalOwner
import com.subreax.schedule.data.local.entitiy.LocalSubject
import com.subreax.schedule.data.local.entitiy.LocalSubjectName
import com.subreax.schedule.data.local.entitiy.LocalTeacherName

@Database(
    entities = [
        LocalSubject::class,
        LocalSubjectName::class,
        LocalTeacherName::class,
        LocalOwner::class
    ],
    version = 3
)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract val subjectDao: SubjectDao
    abstract val subjectNameDao: SubjectNameDao
    abstract val teacherNameDao: TeacherNameDao
    abstract val ownerDao: OwnerDao
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

                    val newId = LocalSubject.buildIdV2(ownerId, beginTimeMins, teacherNameId)
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