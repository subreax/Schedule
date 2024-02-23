package com.subreax.schedule

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.subreax.schedule.data.local.MIGRATION_1_2
import com.subreax.schedule.data.local.ScheduleDatabase
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ScheduleDatabase::class.java.canonicalName!!,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        val subjectTable = "subject"
        var db = helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO `$subjectTable` (id, typeId, ownerId, subjectNameId, place, teacherNameId, beginTimeMins, endTimeMins, rawGroups) VALUES(1, 1, 1, 1, 'place1', 100, 28478124, 0, '220431#')")
            execSQL("INSERT INTO `$subjectTable` (id, typeId, ownerId, subjectNameId, place, teacherNameId, beginTimeMins, endTimeMins, rawGroups) VALUES(2, 1, 1, 1, 'place2', 105, 28478127, 0, '220431#')")
            execSQL("INSERT INTO `$subjectTable` (id, typeId, ownerId, subjectNameId, place, teacherNameId, beginTimeMins, endTimeMins, rawGroups) VALUES(3, 1, 2, 1, 'place3', 110, 28478130, 0, '220431#')")
            execSQL("INSERT INTO `$subjectTable` (id, typeId, ownerId, subjectNameId, place, teacherNameId, beginTimeMins, endTimeMins, rawGroups) VALUES(4, 1, 2, 1, 'place4', 115, 28478139, 0, '220431#')")
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)
        val expectedIds = listOf(
            18014411959732908L,
            18014412630821551L,
            36028811811392178L,
            36028812482480827L
        )
        val actualIds = mutableListOf<Long>()
        val cursor = db.query("SELECT `id` FROM `$subjectTable`")
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow("id"))
                actualIds.add(id)
            }
        }

        Assert.assertEquals(expectedIds, actualIds)
    }
}