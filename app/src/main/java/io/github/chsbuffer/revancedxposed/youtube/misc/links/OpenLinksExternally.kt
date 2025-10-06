package io.github.chsbuffer.revancedxposed.youtube.misc.links

// Kotlin có thể gọi trực tiếp phương thức static của Java một cách liền mạch
import app.revanced.extension.youtube.patches.OpenLinksExternallyPatch
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference
import io.github.chsbuffer.revancedxposed.youtube.YoutubeHook
import io.github.chsbuffer.revancedxposed.youtube.misc.settings.PreferenceScreen
import org.luckypray.dexkit.wrap.DexMethod

fun YoutubeHook.openLinksExternallyHook() {
    // Tự động thêm SwitchPreference vào màn hình cài đặt
    PreferenceScreen.MISC.addPreferences(
        SwitchPreference("revanced_external_browser")
    )

    // Hook vào constructor của Intent
    DexMethod("Landroid/content/Intent;-><init>(Ljava/lang/String;)V").hookMethod {
        before { param ->
            val originalAction = param.args[0] as? String ?: return@before
            
            // Gọi đến hàm logic trong file Java đã được refactor
            param.args[0] = OpenLinksExternallyPatch.getIntentAction(originalAction)
        }
    }
}