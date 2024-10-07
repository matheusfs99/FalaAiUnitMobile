import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.falaaiunit.MainActivity

object NavigationUtils {
    fun backToMenu(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    fun getToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            "prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    fun getUserId(context: Context): Int? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            "prefs", Context.MODE_PRIVATE)
        return if (sharedPreferences.contains("user_id")) {
            sharedPreferences.getInt("user_id", -1)
        } else {
            null
        }
    }
}