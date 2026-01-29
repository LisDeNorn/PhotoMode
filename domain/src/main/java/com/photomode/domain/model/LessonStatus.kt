package com.photomode.domain.model

/**
 * Статус урока для отображения на главном экране
 */
enum class LessonStatus {
    /**
     * Непройденный урок, необходимый для выполнения текущей миссии
     */
    REQUIRED_FOR_MISSION,
    
    /**
     * Обычный непройденный урок
     */
    NOT_STARTED,
    
    /**
     * Пройденный урок
     */
    COMPLETED
}





