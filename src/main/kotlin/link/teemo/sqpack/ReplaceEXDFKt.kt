package link.teemo.sqpack

import com.shenou.fs.core.utils.res.Config
import link.teemo.sqpack.model.SqPackDatFile
import link.teemo.sqpack.model.SqPackIndex
import link.teemo.sqpack.swing.TextPatchPanel
import link.teemo.sqpack.util.FFCRC
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class ReplaceEXDFKt {

    companion object {
        private val ignoreFile = arrayListOf("EXD/Lobby.EXH")

        @JvmStatic
        @Throws(Exception::class)
        fun replace(pathToIndex: String, resourceFolder: String, panel : TextPatchPanel) {
            var exhFile: MutableList<String> = mutableListOf()
            // 拉出root.exl 根据内容插到待List中
            println("Loading Root File...")
            val indexSE = SqPackIndex(pathToIndex).resloveIndex()
            val filePathCRC = FFCRC.ComputeCRC("exd".toLowerCase().toByteArray())
            val rootFileCRC = FFCRC.ComputeCRC("root.exl".toLowerCase().toByteArray())
            val rootIndexFileSE = indexSE[filePathCRC]!!.files[rootFileCRC]
            val rootFile = extractFile(pathToIndex, rootIndexFileSE!!.offset)
            val rootBufferReader = rootFile.inputStream().bufferedReader()
            rootBufferReader.useLines { line ->
                line.forEach {
                    exhFile.add("EXD/" + it.split(",")[0] + ".EXH")
                }
            }
            ignoreFile.forEach {
                exhFile.remove(it)
            }
            Config.setConfigResource("transtable", "conf" + File.separator + "transtable.properties")
            Config.setConfigResource("transtring", "conf" + File.separator + "transtring.properties")
            // 遍历替换
            ReplaceEXDF(pathToIndex, resourceFolder + File.separator + "0a0000.win32.index", exhFile, false, panel).replaceSource();
        }

        @Throws(IOException::class, FileNotFoundException::class)
        fun extractFile(pathToIndex: String, dataOffset: Long): ByteArray {
            var dataOffset = dataOffset
            var pathToOpen = pathToIndex
            val datNum = ((dataOffset and 0xF) / 2L).toInt()
            dataOffset -= dataOffset and 0xF
            pathToOpen = pathToOpen.replace("index2", "dat" + datNum)
            pathToOpen = pathToOpen.replace("index", "dat" + datNum)
            val datFile = SqPackDatFile(pathToOpen)
            val data = datFile.extractFile(dataOffset * 8L, false)
            datFile.close()
            return data
        }
    }
}