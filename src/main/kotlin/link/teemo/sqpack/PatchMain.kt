package link.teemo.sqpack

import com.shenou.fs.core.utils.res.Config
import link.teemo.sqpack.model.SqPackDatFile
import link.teemo.sqpack.model.SqPackIndex
import link.teemo.sqpack.swing.ConfigApplicationPanel
import link.teemo.sqpack.swing.TextPathPanel
import link.teemo.sqpack.util.FFCRC
import java.io.*

class PatchMain {
    companion object {

        @JvmStatic
        @Throws(Exception::class)
        fun main(arg: Array<String>) {
            Config.setConfigResource("conf" + File.separator + "global.properties")
            val path = Config.getProperty("GamePath")
            if(isFFXIVFloder(path)){
                TextPathPanel()
            }else{
                ConfigApplicationPanel()
            }
        }

        private fun isFFXIVFloder(path: String?): Boolean {
            if (path == null)
                return false
            return File(path + File.separator + "game" + File.separator + "ffxiv.exe").exists()
        }
    }
}


