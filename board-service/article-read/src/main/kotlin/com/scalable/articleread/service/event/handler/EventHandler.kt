package com.scalable.articleread.service.event.handler

import com.common.event.Event
import com.common.event.payload.EventPayload

interface EventHandler<T : EventPayload> {
    fun handle(event: Event<T>)
    fun supports(event: Event<T>): Boolean
}