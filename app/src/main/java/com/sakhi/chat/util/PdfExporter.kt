package com.sakhi.chat.util

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.sakhi.chat.model.Bot
import com.sakhi.chat.model.BotType
import com.sakhi.chat.model.Message
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PdfExporter(private val context: Context) {

    fun exportConversationToPdf(
        messages: List<Message>,
        botType: BotType,
        conversationTitle: String
    ): Result<File> {
        return try {
            val bot = Bot.getBotByType(botType)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Sakhi_${bot.name}_$timestamp.pdf"

            // Use app-specific directory (doesn't require storage permissions on Android 10+)
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(downloadsDir, fileName)

            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            // Title
            val title = Paragraph("सखी - ${bot.nameMarathi}")
                .setFontSize(24f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10f)
            document.add(title)

            // Conversation title
            if (conversationTitle.isNotEmpty()) {
                val subtitle = Paragraph(conversationTitle)
                    .setFontSize(16f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20f)
                document.add(subtitle)
            }

            // Date
            val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("mr", "IN"))
            val dateText = Paragraph("तारीख: ${dateFormat.format(Date())}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20f)
            document.add(dateText)

            // Messages
            messages.forEach { message ->
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val timeString = timeFormat.format(Date(message.timestamp))

                val sender = if (message.isFromUser) "तुम्ही" else bot.nameMarathi
                val senderText = Text("$sender ($timeString):\n")
                    .setBold()
                    .setFontColor(if (message.isFromUser) ColorConstants.BLUE else ColorConstants.GREEN)

                val messageText = Text(message.text + "\n\n")

                val paragraph = Paragraph()
                    .add(senderText)
                    .add(messageText)
                    .setMarginBottom(10f)

                document.add(paragraph)
            }

            // Footer
            val footer = Paragraph("\n\nसखी AI द्वारे व्युत्पन्न • ${messages.size} संदेश")
                .setFontSize(8f)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY)
            document.add(footer)

            document.close()
            Result.success(file)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
