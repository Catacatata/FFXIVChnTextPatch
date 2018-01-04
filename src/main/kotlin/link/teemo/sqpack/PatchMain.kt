package link.teemo.sqpack

import com.shenou.fs.core.utils.res.Config
import link.teemo.sqpack.model.SqPackDatFile
import link.teemo.sqpack.model.SqPackIndex
import link.teemo.sqpack.util.FFCRC
import java.io.*

class PatchMain {
    companion object {
        private val ignoreFile = arrayListOf("EXD/Lobby.EXH")
        private val configFilePath = "trans.properties"

        @JvmStatic
        @Throws(Exception::class)
        fun main(arg: Array<String>) {
            // 复制input资源到output文件夹
            val inputFolder = File("input")
            if (inputFolder.isDirectory && inputFolder.list().isNotEmpty()) {
                inputFolder.listFiles()!!.filter { it.isFile }.forEach {
                    if (File("output" + File.separator + it.name).exists()) {
                        File("output" + File.separator + it.name).delete()
                    }
                    it.copyTo(File("output" + File.separator + it.name))
                }
                var exhFile: MutableList<String> = mutableListOf()
                // 拉出root.exl 根据内容插到待List中
                println("Loading Root File...")
                val patchToIndexSE = "input" + File.separator + "0a0000.win32.index"
                val indexSE = SqPackIndex(patchToIndexSE).resloveIndex()
                val filePathCRC = FFCRC.ComputeCRC("exd".toLowerCase().toByteArray())
                val rootFileCRC = FFCRC.ComputeCRC("root.exl".toLowerCase().toByteArray())
                val rootIndexFileSE = indexSE[filePathCRC]!!.files[rootFileCRC]
                val rootFile = extractFile(patchToIndexSE, rootIndexFileSE!!.offset)
                val rootBufferReader = rootFile.inputStream().bufferedReader()
                rootBufferReader.useLines { line ->
                    line.forEach {
                        exhFile.add("EXD/" + it.split(",")[0] + ".EXH")
                    }
                }
                ignoreFile.forEach {
                    exhFile.remove(it)
                }
                Config.setConfigResource(configFilePath)
                // 遍历替换
                ReplaceEXDF("output" + File.separator + "0a0000.win32.index", "resource" + File.separator + "0a0000.win32.index", exhFile, false).replaceSource();
            }
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


