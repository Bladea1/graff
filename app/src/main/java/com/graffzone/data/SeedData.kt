package com.graffzone.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SeedData {
    suspend fun ensureSeeded(db: AppDatabase) = withContext(Dispatchers.IO) {
        val userDao = db.userDao()
        val postDao = db.postDao()
        val eventDao = db.eventDao()
        val existing = postDao.getById(1L)
        if (existing != null) return@withContext

        val blade = userDao.insert(UserEntity(username = "bladea1", bio = "Oldschool writer"))
        val echo = userDao.insert(UserEntity(username = "echo", bio = "Street photographer"))
        val luna = userDao.insert(UserEntity(username = "luna", bio = "Sketch & throw-ups"))

        postDao.insert(
            PostEntity(
                authorId = blade,
                caption = "Новый скетч. Кто сегодня на споте?",
                tags = "sketch,ru,style",
                mediaType = "NONE",
                lat = 55.7558,
                lon = 37.6173
            )
        )
        postDao.insert(
            PostEntity(
                authorId = echo,
                caption = "Фото стены у жд. Цвета 🔥",
                tags = "photo,wall,train",
                mediaType = "NONE",
                lat = 59.9343,
                lon = 30.3351
            )
        )
        postDao.insert(
            PostEntity(
                authorId = luna,
                caption = "Throw-up на районе.",
                tags = "throwup,night",
                mediaType = "NONE",
                lat = 56.8389,
                lon = 60.6057
            )
        )

        val now = System.currentTimeMillis()
        eventDao.insert(
            EventEntity(
                creatorId = blade,
                title = "Jam (учебный)",
                description = "Только оффлайн-ивент в приложении :)",
                startTime = now + 1000L * 60 * 60 * 24 * 3,
                lat = 55.751244,
                lon = 37.618423
            )
        )
        eventDao.insert(
            EventEntity(
                creatorId = echo,
                title = "Фото-прогулка",
                description = "Собираем кадры, без риска и без вандализма.",
                startTime = now + 1000L * 60 * 60 * 24 * 7,
                lat = 59.939095,
                lon = 30.315868
            )
        )

        // Seed one conversation
        val convId = db.conversationDao().insert(ConversationEntity(title = "bladea1"))
        db.messageDao().insert(MessageEntity(conversationId = convId, sender = "bladea1", text = "Йо! Добро пожаловать в GraffZone."))
        db.messageDao().insert(MessageEntity(conversationId = convId, sender = "you", text = "Привет!"))
    }
}
