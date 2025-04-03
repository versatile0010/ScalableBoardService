package com.scalable.hotarticle.service.eventhandler

import com.common.event.Event
import com.common.event.payload.EventPayload

interface EventHandler<T : EventPayload> {
    fun handle(event: Event<T>)
    fun supports(event: Event<T>): Boolean
    fun findArticleId(event: Event<T>): Long
}
