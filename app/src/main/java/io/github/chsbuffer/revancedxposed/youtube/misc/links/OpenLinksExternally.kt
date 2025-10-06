package io.github.chsbuffer.revancedxposed.youtube.misc.links

// Import chính xác đến file logic thực thi bằng Java
import app.revanced.extension.youtube.patches.OpenLinksExternallyPatch
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference
import io.github.chsbuffer.revancedxposed.youtube.YoutubeHook
import io.github.chsbuffer.revancedxposed.youtube.misc.settings.PreferenceScreen
import org.luckypray.dexkit.wrap.DexMethod

fun YoutubeHook.openLinksExternallyHook() {
    PreferenceScreen.MISC.addPreferences(
        SwitchPreference("revanced_external_browser")
    )

    DexMethod("Landroid/content/Intent;-><init>(Ljava/lang/String;)V").hookMethod {
        before { param ->
            val originalAction = param.args[0] as? String ?: return@before
            
            // Gọi đến logic có sẵn từ file Java
            param.args[0] = OpenLinksExternallyPatch.getIntentAction(originalAction)
        }
    }
}
