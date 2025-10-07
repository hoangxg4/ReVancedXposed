package io.github.chsbuffer.revancedxposed.youtube.misc.links

import android.content.Context
import android.content.Intent
import android.net.Uri
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference
import io.github.chsbuffer.revancedxposed.youtube.YoutubeHook
import io.github.chsbuffer.revancedxposed.youtube.misc.settings.PreferenceScreen
import org.luckypray.dexkit.wrap.DexMethod
import de.robv.android.xposed.XSharedPreferences

fun YoutubeHook.openLinksExternallyHook() {
    val PACKAGE_NAME = "io.github.chsbuffer.revancedxposed"
    val PREFERENCE_KEY = "revanced_external_browser"

    // Phần UI: Thêm công tắc vào menu, nó sẽ tự lấy title/summary từ tài nguyên chung
    PreferenceScreen.MISC.addPreferences(
        SwitchPreference(PREFERENCE_KEY)
    )

    // Hook vào mục tiêu mới và chính xác nhất: CustomTabsIntent.launchUrl
    val launchUrlMethod = DexMethod("Landroidx/browser/customtabs/CustomTabsIntent;->launchUrl(Landroid/content/Context;Landroid/net/Uri;)V")
    
    launchUrlMethod.hookMethod(object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam) {
            val context = param.args[0] as Context
            val uri = param.args[1] as Uri
            
            val prefs = XSharedPreferences(PACKAGE_NAME)
            prefs.reload()
            val shouldOpenExternally = prefs.getBoolean(PREFERENCE_KEY, false)

            if (shouldOpenExternally) {
                // Nếu cài đặt được bật:
                // 1. Tạo một Intent ACTION_VIEW hoàn toàn mới để mở trình duyệt ngoài.
                val newIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    // Thêm flag này để đảm bảo nó có thể được gọi từ một context không phải là Activity
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) 
                }
                // 2. Tự tay gọi startActivity, hoàn toàn bỏ qua logic của YouTube.
                context.startActivity(newIntent)
            } else {
                // Nếu cài đặt bị tắt:
                // Giữ nguyên hành vi gốc, gọi lại hàm launchUrl ban đầu.
                XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args)
            }
        }
    })
}
