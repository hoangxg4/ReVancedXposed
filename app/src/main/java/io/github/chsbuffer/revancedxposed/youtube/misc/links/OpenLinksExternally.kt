package io.github.chsbuffer.revancedxposed.youtube.misc.links

import de.robv.android.xposed.XSharedPreferences
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference
import io.github.chsbuffer.revancedxposed.youtube.YoutubeHook
import io.github.chsbuffer.revancedxposed.youtube.misc.settings.PreferenceScreen
import org.luckypray.dexkit.wrap.DexMethod

fun YoutubeHook.openLinksExternallyHook() {
    // --- Định nghĩa các hằng số để dễ quản lý ---
    val PACKAGE_NAME = "io.github.chsbuffer.revancedxposed"
    val PREFERENCE_KEY = "revanced_external_browser"
    val CUSTOM_TABS_ACTION = "android.support.customtabs.action.CustomTabsService"
    val EXTERNAL_BROWSER_ACTION = "android.intent.action.VIEW"

    // --- Logic Phần 1: Thêm tùy chọn vào giao diện cài đặt ---
    PreferenceScreen.MISC.addPreferences(
        SwitchPreference(PREFERENCE_KEY)
    )

    // --- Logic Phần 2: Hook vào ứng dụng và xử lý ---
    DexMethod("Landroid/content/Intent;-><init>(Ljava/lang/String;)V").hookMethod {
        before { param ->
            // --- Logic Phần 3: Đọc cài đặt và quyết định ---
            // Toàn bộ logic được thực thi trực tiếp tại đây
            val prefs = XSharedPreferences(PACKAGE_NAME)
            prefs.reload()

            val shouldOpenExternally = prefs.getBoolean(PREFERENCE_KEY, false)
            val originalAction = param.args[0] as? String ?: return@before

            if (shouldOpenExternally && originalAction == CUSTOM_TABS_ACTION) {
                // Nếu tùy chọn được bật và đúng là action mở trình duyệt trong app,
                // ta sẽ thay thế nó bằng action mở trình duyệt ngoài.
                param.args[0] = EXTERNAL_BROWSER_ACTION
            }
        }
    }
}
