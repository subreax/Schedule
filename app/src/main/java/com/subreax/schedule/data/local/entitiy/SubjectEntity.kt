package com.subreax.schedule.data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.network.model.NetworkGroup

@Entity(tableName = "subject")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val typeId: String,
    val scheduleId: Int,
    val subjectNameId: Int,
    val place: String,
    val teacherNameId: Int,
    val beginTimeMins: Int,
    val endTimeMins: Int,
    val rawGroups: String
) {
    companion object {
        private const val separator = "#"

        fun buildIdV2(ownerId: Int, beginTimeMins: Int, teacherNameId: Int): Long {
            var id: Long = (ownerId.toLong() and 0x3ff)
            id = (id shl 27) or (teacherNameId.toLong() and 0x7ffffff)
            id = (id shl 27) or (beginTimeMins.toLong() and 0x7ffffff)
            return id
        }

        fun buildRawGroups(groups: List<NetworkGroup>): String {
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
