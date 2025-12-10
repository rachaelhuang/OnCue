package com.ait.oncue.utils

import android.content.Context
import java.io.File

fun createImageFile(context: Context): File {
    return File.createTempFile(
        "captured_image_",
        ".jpg",
        context.externalCacheDir
    )
}