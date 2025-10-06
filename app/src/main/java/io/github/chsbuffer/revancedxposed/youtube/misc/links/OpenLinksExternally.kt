package io.github.chsbuffer.revancedxposed.youtube.misc.links

import de.robv.android.xposed.XSharedPreferences
import io.github.chsbuffer.revancedxposed.R // <-- QUAN TRỌNG: Đảm bảo có dòng import này
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference
import io.github.chsbuffer.revancedxposed.youtube.YoutubeHook
import io.github.chsbuffer.revancedxposed.youtube.misc.settings.PreferenceScreen
import org.luckypray.dexkit.wrap.DexMethod

fun YoutubeHook.openLinksExternallyHook() {
    val PACKAGE_NAME = "io.github.chsbuffer.revancedxposed"
    val PREFERENCE_KEY = "revanced_external_browser"
    val CUSTOM_TABS_ACTION = "android.support.customtabs.action.CustomTabsService"
    val EXTERNAL_BROWSER_ACTION = "android.intent.action.VIEW"

    // Thêm tùy chọn vào giao diện cài đặt với ĐẦY ĐỦ thông tin
    PreferenceScreen.MISC.addPreferences(
        SwitchPreference(
            key = PREFERENCE_KEY,
            titleKey = "revanced_external_browser_title",     // <-- Trỏ đến string title đã tạo
            summaryKey = "revanced_external_browser_summary", // <-- Trỏ đến string summary đã tạo
            defaultValue = false                              // <-- Cung cấp giá trị mặc định
        )
    )

    // Hook vào ứng dụng và xử lý
    DexMethod("Landroid/content/Intent;-><init>(Ljava/lang/String;)V").hookMethod {
        before { param ->
            val prefs = XSharedPreferences(PACKAGE_NAME)
            prefs.reload()

            val shouldOpenExternally = prefs.getBoolean(PREFERENCE_KEY, false)
            val originalAction = param.args[0] as? String ?: return@before

            if (shouldOpenExternally && originalAction == CUSTOM_TABS_ACTION) {
                param.args[0] = EXTERNAL_BROWSER_ACTION
            }
        }
    }
}
