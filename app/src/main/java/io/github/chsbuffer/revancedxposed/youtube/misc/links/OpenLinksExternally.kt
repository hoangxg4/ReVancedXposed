package io.github.chsbuffer.revancedxposed.youtube.misc.links

import android.app.Activity
import android.content.Intent
import app.revanced.extension.youtube.patches.OpenLinksExternallyPatch
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference
import io.github.chsbuffer.revancedxposed.youtube.YoutubeHook
import io.github.chsbuffer.revancedxposed.youtube.misc.settings.PreferenceScreen
import org.luckypray.dexkit.wrap.DexMethod

fun YoutubeHook.openLinksExternallyHook() {
    // Phần thêm UI vào menu vẫn giữ nguyên
    PreferenceScreen.MISC.addPreferences(
        SwitchPreference("revanced_external_browser")
    )

    // THAY ĐỔI MỤC TIÊU HOOK:
    // Hook vào android.app.Activity.startActivity(Intent) thay vì constructor của Intent
    DexMethod("Landroid/app/Activity;->startActivity(Landroid/content/Intent;)V").hookMethod {
        before { param ->
            // Lấy đối tượng Intent từ tham số của hàm
            val intent = param.args[0] as? Intent ?: return@before
            
            // Gọi đến hàm xử lý mới trong file Java
            OpenLinksExternallyPatch.handleIntent(intent)
        }
    }
}
