import link.teemo.sqpack.ReplaceEXDF
import java.io.File

@Throws(Exception::class)
fun main(arg: Array<String>) {
    // 复制input资源到output文件夹
    val inputFolder = File("input")
    if (inputFolder.isDirectory && inputFolder.list().size > 0) {
        inputFolder.listFiles()!!.filter { it.isFile }.forEach {
            if (File("output" + File.separator + it.name).exists()) {
                File("output" + File.separator + it.name).delete()
            }
            it.copyTo(File("output" + File.separator + it.name))
        }
        // 汉化
        var exhFile: MutableList<String> = mutableListOf()
        exhFile.add("exd/addon.exh")
        exhFile.add("exd/action.exh")
        exhFile.add("exd/actioncategory.exh")
        exhFile.add("exd/actioncomboroute.exh")
        exhFile.add("exd/actioncomboroutetransient.exh")
        exhFile.add("exd/actiontransient.exh")
        exhFile.add("exd/status.exh")
        exhFile.add("exd/item.exh")
        exhFile.add("exd/quest.exh")
        exhFile.add("exd/bnpcname.exh")
        exhFile.add("exd/eobjname.exh")
        exhFile.add("exd/configkey.exh")
        exhFile.add("exd/mount.exh")
        exhFile.add("exd/npcyell.exh")
        exhFile.add("exd/maincommand.exh")
        ReplaceEXDF("output" + File.separator + "0a0000.win32.index", "resource" + File.separator + "0a0000.win32.index", exhFile, false).replaceSource();
    }
}