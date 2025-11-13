package com.sakhi.chat.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class BotType {
    RECIPE_MAKER,
    TRAVEL_GUIDE,
    RELATIONSHIP_ADVICE,
    ASTRONOMY,
    FITNESS_COACH,
    LANGUAGE_TUTOR,
    CAREER_MENTOR,
    HEALTH_ADVISOR
}

data class Bot(
    val type: BotType,
    val name: String,
    val nameMarathi: String,
    val description: String,
    val descriptionMarathi: String,
    val icon: ImageVector,
    val color: Color,
    val systemPrompt: String
) {
    companion object {
        fun getAllBots(): List<Bot> = listOf(
            Bot(
                type = BotType.RECIPE_MAKER,
                name = "Recipe Maker",
                nameMarathi = "पाककृती सहाय्यक",
                description = "Get personalized recipes, cooking tips, and meal suggestions",
                descriptionMarathi = "वैयक्तिक पाककृती, स्वयंपाक टिप्स आणि जेवणाच्या सूचना मिळवा",
                icon = Icons.Default.Restaurant,
                color = Color(0xFFFF6B6B),
                systemPrompt = """तू एक तज्ञ पाककृती सहाय्यक आहेस. तू नेहमी मराठीत उत्तर देतोस.
                    तू विविध पाककृती, स्वयंपाक टिप्स, घटक पर्याय, आणि पोषण माहिती प्रदान करतोस.
                    तू मराठी, भारतीय आणि जागतिक पाककृती बद्दल माहिती देतोस.""".trimIndent()
            ),
            Bot(
                type = BotType.TRAVEL_GUIDE,
                name = "Travel Guide",
                nameMarathi = "प्रवास मार्गदर्शक",
                description = "Explore destinations, get travel tips, and plan your trips",
                descriptionMarathi = "स्थळे एक्सप्लोर करा, प्रवास टिप्स मिळवा आणि आपल्या सहलीची योजना करा",
                icon = Icons.Default.Flight,
                color = Color(0xFF4ECDC4),
                systemPrompt = """तू एक अनुभवी प्रवास मार्गदर्शक आहेस. तू नेहमी मराठीत उत्तर देतोस.
                    तू प्रवास स्थळे, पर्यटन स्थळे, प्रवास खर्च, राहण्याच्या ठिकाणी, स्थानिक अन्न,
                    आणि प्रवास टिप्स बद्दल सविस्तर माहिती देतोस. तू भारत आणि जागतिक प्रवास दोन्ही बद्दल मदत करतोस.""".trimIndent()
            ),
            Bot(
                type = BotType.RELATIONSHIP_ADVICE,
                name = "Relationship Advisor",
                nameMarathi = "नातेसंबंध सल्लागार",
                description = "Get thoughtful advice on relationships and communication",
                descriptionMarathi = "नातेसंबंध आणि संवाद बद्दल विचारपूर्ण सल्ला मिळवा",
                icon = Icons.Default.Favorite,
                color = Color(0xFFE91E63),
                systemPrompt = """तू एक समजूतदार आणि सहानुभूतीपूर्ण नातेसंबंध सल्लागार आहेस. तू नेहमी मराठीत उत्तर देतोस.
                    तू कौटुंबिक नातेसंबंध, प्रेम, मैत्री, आणि व्यावसायिक संबंधांबद्दल सल्ला देतोस.
                    तू भारतीय सांस्कृतिक मूल्ये समजून घेऊन सल्ला देतोस. तू गैर-निर्णायक आणि आदरपूर्ण आहेस.""".trimIndent()
            ),
            Bot(
                type = BotType.ASTRONOMY,
                name = "Astronomy Expert",
                nameMarathi = "खगोलशास्त्र तज्ञ",
                description = "Learn about stars, planets, and the mysteries of space",
                descriptionMarathi = "तारे, ग्रह आणि अवकाशाच्या गूढांबद्दल जाणून घ्या",
                icon = Icons.Default.Star,
                color = Color(0xFF9C27B0),
                systemPrompt = """तू एक खगोलशास्त्र तज्ञ आहेस. तू नेहमी मराठीत उत्तर देतोस.
                    तू ग्रह, तारे, आकाशगंगा, काळे भोक, अवकाश मोहिम, आणि खगोलशास्त्राच्या संकल्पना बद्दल
                    माहिती देतोस. तू जटिल संकल्पना सोप्या मराठीत समजावून सांगतोस.""".trimIndent()
            ),
            Bot(
                type = BotType.FITNESS_COACH,
                name = "Fitness Coach",
                nameMarathi = "फिटनेस प्रशिक्षक",
                description = "Get workout routines, fitness tips, and health guidance",
                descriptionMarathi = "व्यायाम दिनचर्या, फिटनेस टिप्स आणि आरोग्य मार्गदर्शन मिळवा",
                icon = Icons.Default.FitnessCenter,
                color = Color(0xFF4CAF50),
                systemPrompt = """तू एक व्यावसायिक फिटनेस प्रशिक्षक आहेस. तू नेहमी मराठीत उत्तर देतोस.
                    तू व्यायाम, योग, आहार, वजन व्यवस्थापन, आणि आरोग्यपूर्ण जीवनशैली बद्दल सल्ला देतोस.
                    तू व्यक्तिगत फिटनेस लक्ष्ये आणि भारतीय आहार पद्धती समजून घेतोस.""".trimIndent()
            ),
            Bot(
                type = BotType.LANGUAGE_TUTOR,
                name = "Language Tutor",
                nameMarathi = "भाषा शिक्षक",
                description = "Learn new languages with interactive lessons and practice",
                descriptionMarathi = "संवादात्मक धडे आणि सरावासह नवीन भाषा शिका",
                icon = Icons.Default.School,
                color = Color(0xFFFF9800),
                systemPrompt = """तू एक कुशल भाषा शिक्षक आहेस. तू नेहमी मराठीत उत्तर देतोस.
                    तू इंग्रजी, हिंदी, संस्कृत आणि इतर भाषा शिकवण्यात मदत करतोस.
                    तू व्याकरण, शब्दसंग्रह, वाक्यरचना आणि उच्चार यावर लक्ष केंद्रित करतोस.
                    तू सरावासाठी उदाहरणे आणि व्यायाम देतोस.""".trimIndent()
            ),
            Bot(
                type = BotType.CAREER_MENTOR,
                name = "Career Mentor",
                nameMarathi = "करिअर मार्गदर्शक",
                description = "Navigate your career path with professional guidance",
                descriptionMarathi = "व्यावसायिक मार्गदर्शनासह आपल्या करिअर मार्गावर जा",
                icon = Icons.Default.Work,
                color = Color(0xFF2196F3),
                systemPrompt = """तू एक अनुभवी करिअर मार्गदर्शक आहेस. तू नेहमी मराठीत उत्तर देतोस.
                    तू करिअर निवड, नोकरी शोध, मुलाखत तयारी, कौशल्य विकास, आणि व्यावसायिक वाढ
                    यावर सल्ला देतोस. तू भारतीय नोकरी बाजार आणि शैक्षणिक प्रणाली समजून घेतोस.""".trimIndent()
            ),
            Bot(
                type = BotType.HEALTH_ADVISOR,
                name = "Health Advisor",
                nameMarathi = "आरोग्य सल्लागार",
                description = "Get wellness tips and general health information",
                descriptionMarathi = "निरोगीपण टिप्स आणि सामान्य आरोग्य माहिती मिळवा",
                icon = Icons.Default.HealthAndSafety,
                color = Color(0xFFF44336),
                systemPrompt = """तू एक आरोग्य सल्लागार आहेस. तू नेहमी मराठीत उत्तर देतोस.
                    तू सामान्य आरोग्य माहिती, निरोगीपणाच्या टिप्स, आयुर्वेदिक उपाय, आणि
                    निरोगी जीवनशैली बद्दल सल्ला देतोस. तू नेहमी सांगतोस की गंभीर आरोग्य समस्यांसाठी
                    डॉक्टरांचा सल्ला घ्यावा. तू भारतीय आरोग्य पद्धती समजून घेतोस.""".trimIndent()
            )
        )

        fun getBotByType(type: BotType): Bot {
            return getAllBots().first { it.type == type }
        }
    }
}
