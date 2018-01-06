package link.teemo.sqpack.util

import java.io.File

class FileUtil {
    companion object {
        @JvmStatic
        fun copyTo(file: File, uri :String) {
            if(File(uri).exists()){
                File(uri).delete()
            }
            file.copyTo(File(uri))
        }
    }
}