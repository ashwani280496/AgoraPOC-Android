package io.agora.openduo.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File

object FileUtil {
    private const val LOG_FOLDER_NAME = "log"
    private const val RTC_LOG_FILE_NAME = "agora-rtc.log"
    private const val RTM_LOG_FILE_NAME = "agora-rtm.log"
    fun rtcLogFile(context: Context): String {
        return logFilePath(context, RTC_LOG_FILE_NAME)
    }

    @JvmStatic
    fun rtmLogFile(context: Context): String {
        return logFilePath(context, RTM_LOG_FILE_NAME)
    }

    private fun logFilePath(context: Context, name: String): String {
        var folder: File?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            folder = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), LOG_FOLDER_NAME)
        } else {
            val path = Environment.getExternalStorageDirectory()
                    .absolutePath + File.separator +
                    context.packageName + File.separator +
                    name
            folder = File(path)
            if (!folder.exists() && !folder.mkdir()) folder = null
        }
        return if (folder != null && !folder.exists() && !folder.mkdir()) "" else File(folder, RTC_LOG_FILE_NAME).absolutePath
    }
}