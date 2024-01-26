package com.subreax.schedule.data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.subreax.schedule.data.model.Group

@Entity(tableName = "subject")
data class LocalSubject(
    @PrimaryKey
    val id: Long,
    val typeId: String,
    val ownerId: Int,
    val subjectNameId: Int,
    val place: String, // todo: should be fk
    val teacherNameId: Int,
    val beginTimeMins: Int,
    val endTimeMins: Int,
    val rawGroups: String
) {
    companion object {
        private const val separator = "#"

        fun buildId(ownerId: Int, beginTimeMins: Int, subjectNameId: Int, teacherNameId: Int): Long {
            var id = (beginTimeMins and 0x3ffffff).toLong() // 26 bits
            id = id or ((subjectNameId and 0x3fff).toLong() shl 26) // 14 bits
            id = id or ((teacherNameId and 0x1fff).toLong() shl 40) // 13 bits
            id = id or ((ownerId and 0x3ff).toLong() shl 53) // 10 bits
            // free: 1 sign bit
            return id
        }

        fun buildRawGroups(groups: List<Group>): String {
            return groups.joinToString(separator = separator) {
                "${it.id}$separator${it.note}"
            }
        }

        fun parseGroups(raw: String): List<Group> {
            val v = raw.split(separator)
            val groups = mutableListOf<Group>()
            val iter = v.iterator()
            while (iter.hasNext()) {
                val id = iter.next()
                val note = iter.next()
                groups.add(Group(id, note))
            }
            return groups
        }
    }
}
