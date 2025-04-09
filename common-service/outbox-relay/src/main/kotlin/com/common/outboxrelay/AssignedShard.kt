package com.common.outboxrelay

data class AssignedShard(
    val shards: List<Long>,
) {
    companion object {
        fun of(
            appId: String,
            appIds: List<String>,
            shardCount: Long,
        ): AssignedShard {
            return AssignedShard(
                shards = assign(
                    appId = appId,
                    appIds = appIds,
                    shardCount = shardCount,
                )
            )
        }

        private fun assign(
            appId: String,
            appIds: List<String>,
            shardCount: Long,
        ): List<Long> {
            val index = findAppIndex(appId, appIds)
            if (index == -1) return emptyList()

            val start = index * shardCount / appIds.size
            val end = (index + 1) * shardCount / appIds.size

            return (start..end).toList()
        }

        private fun findAppIndex(
            appId: String,
            appIds: List<String>,
        ): Int {
            return appIds.indexOf(appId)
        }
    }
}
