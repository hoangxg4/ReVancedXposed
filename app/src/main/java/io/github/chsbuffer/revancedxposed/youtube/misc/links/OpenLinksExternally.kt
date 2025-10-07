package io.github.chsbuffer.revancedxposed.youtube.misc.links

import app.revanced.extension.youtube.patches.OpenLinksExternallyPatch
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference
import io.github.chsbuffer.revancedxposed.youtube.YoutubeHook
import io.github.chsbuffer.revancedxposed.youtube.misc.settings.PreferenceScreen
import org.luckypray.dexkit.wrap.DexMethod

fun YoutubeHook.openLinksExternallyHook() {
    // Thêm công tắc vào menu. Giờ nó sẽ tự tìm thấy title/summary từ "kho chung".
    PreferenceScreen.MISC.addPreferences(
        SwitchPreference("revanced_external_browser")
    )

    // Hook vào hàm tạo Intent
    DexMethod("Landroid/content/Intent;-><init>(Ljava/lang/String;)V").hookMethod {
        before { param ->
            val originalAction = param.args[0] as? String ?: return@before
            
            // Gọi đến hàm getIntent đã được sửa logic trong file Java
            param.args[0] = OpenLinksExternallyPatch.getIntent(originalAction)
        }
    }
}