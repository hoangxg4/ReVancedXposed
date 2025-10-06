package io.github.chsbuffer.revancedxposed.youtube.misc.links

import io.github.chsbuffer.revancedxposed.preferences
import io.github.chsbuffer.revancedxposed.youtube.YoutubeHook
import org.luckypray.dexkit.wrap.DexMethod

fun YoutubeHook.openLinksExternallyHook() {
    // Chuỗi action mặc định của YouTube để mở trình duyệt trong ứng dụng (Custom Tabs)
    val customTabsAction = "android.support.customtabs.action.CustomTabsService"
    
    // Chuỗi action của Android để yêu cầu hệ thống mở bằng trình duyệt mặc định
    val externalBrowserAction = "android.intent.action.VIEW"

    // Chúng ta sẽ hook vào hàm khởi tạo (constructor) của lớp Intent
    // cụ thể là hàm nhận một tham số String (chính là action)
    // Signature: public Intent(String action)
    DexMethod("Landroid/content/Intent;-><init>(Ljava/lang/String;)V").hookMethod {
        before { param ->
            // 1. Kiểm tra xem người dùng có bật tùy chọn trong cài đặt không.
            //    Nếu không bật, bỏ qua và không làm gì cả.
            if (!preferences.getBoolean("revanced_external_browser", false)) {
                return@before
            }

            // 2. Lấy tham số action (là tham số đầu tiên, index = 0)
            val action = param.args[0] as? String ?: return@before

            // 3. Nếu action đúng là action để mở Custom Tabs...
            if (action == customTabsAction) {
                // 4. ...thì ta thay thế nó bằng action mở trình duyệt ngoài.
                //    Hàm khởi tạo Intent sẽ nhận giá trị mới này và tạo ra một Intent khác.
                param.args[0] = externalBrowserAction
            }
        }
    }
}
