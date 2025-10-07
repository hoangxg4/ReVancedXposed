package io.github.chsbuffer.revancedxposed.youtube.misc.links

import android.content.Context
import android.net.Uri
import app.revanced.extension.youtube.patches.OpenLinksExternallyPatch
import de.robv.android.xposed.XC_MethodHook
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference
import io.github.chsbuffer.revancedxposed.youtube.YoutubeHook
import io.github.chsbuffer.revancedxposed.youtube.misc.settings.PreferenceScreen
import org.luckypray.dexkit.wrap.DexMethod

fun YoutubeHook.openLinksExternallyHook() {
    // Thêm UI vào menu, nó sẽ tự tìm thấy title/summary từ "kho chung"
    PreferenceScreen.MISC.addPreferences(
        SwitchPreference("revanced_external_browser")
    )

    // Hook vào mục tiêu chính xác: CustomTabsIntent.launchUrl
    val launchUrlMethod = DexMethod("Landroidx/browser/customtabs/CustomTabsIntent;->launchUrl(Landroid/content/Context;Landroid/net/Uri;)V")

    launchUrlMethod.hookMethod(object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            val context = param.args[0] as Context
            val uri = param.args[1] as Uri

            // Gọi hàm helper bên Java để thử mở link ra ngoài.
            val handled = OpenLinksExternallyPatch.launchExternal(context, uri)

            // Nếu hàm helper báo lại là đã xử lý xong (handled = true)...
            if (handled) {
                // ...thì chúng ta ngăn không cho hàm launchUrl gốc được chạy nữa.
                param.result = null 
            }
        }
    })
}
