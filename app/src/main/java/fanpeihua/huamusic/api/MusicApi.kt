package fanpeihua.huamusic.api

import fanpeihua.huamusic.bean.Music
import fanpeihua.huamusic.common.Constants
import io.reactivex.Observable

object MusicApi {
    private val TAG = "MusicApi"

    fun getLyricInfo(music: Music): Observable<String>? {
        return when (music.type) {
            Constants.BAIDU -> {
                if (music.lyric != null) {
                    
                }
            }
        }
    }
}