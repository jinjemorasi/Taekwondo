package dev.taekwondo.academy.ui

import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/** Subscription plans sold by the coach as license codes. */
data class Plan(val id: String, val faTitle: String, val days: Int, val faDuration: String)

val PLANS = listOf(
    Plan("monthly", "اشتراک ماهانه", 30, "۳۰ روز"),
    Plan("quarterly", "اشتراک سه‌ماهه", 90, "۹۰ روز"),
    Plan("yearly", "اشتراک سالانه", 365, "۳۶۵ روز")
)

const val LICENSE_POOL_URL =
    "https://github.com/jinjemorasi/Taekwondo/releases/download/licenses-v1/licenses.json"

/**
 * Offline license verification: the app only stores SHA-256 hashes of valid
 * codes (hosted on GitHub, bundled copy in assets as fallback). Plain codes
 * exist only in the coach's list, so the public repo reveals nothing usable.
 */
object LicenseStore {
    private const val PREFS = "license_prefs"

    fun normalize(code: String): String =
        code.uppercase().replace("-", "").replace(" ", "").trim()

    fun sha256Hex(s: String): String =
        MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    /** Parse pool JSON into planId -> set of code hashes. */
    fun parsePool(json: String): Map<String, Set<String>> = try {
        val obj = JSONObject(json)
        PLANS.associate { plan ->
            val arr = obj.optJSONArray(plan.id)
            val set = mutableSetOf<String>()
            if (arr != null) for (i in 0 until arr.length()) set.add(arr.getString(i))
            plan.id to set
        }
    } catch (e: Exception) {
        emptyMap()
    }

    fun loadBundledPool(ctx: Context): Map<String, Set<String>> = try {
        parsePool(ctx.assets.open("licenses.json").bufferedReader().readText())
    } catch (e: Exception) {
        emptyMap()
    }

    fun loadCachedPool(ctx: Context): Map<String, Set<String>> {
        val cached = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString("pool_cache", null)
        return if (cached != null) parsePool(cached) else emptyMap()
    }

    fun fetchPool(ctx: Context): Map<String, Set<String>>? = try {
        val conn = URL(LICENSE_POOL_URL).openConnection() as HttpURLConnection
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        val text = conn.inputStream.bufferedReader().readText()
        conn.disconnect()
        val parsed = parsePool(text)
        if (parsed.values.any { it.isNotEmpty() }) {
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString("pool_cache", text).apply()
            parsed
        } else null
    } catch (e: Exception) {
        null
    }

    fun findPlan(code: String, pool: Map<String, Set<String>>): Plan? {
        val hash = sha256Hex(normalize(code))
        return PLANS.firstOrNull { pool[it.id]?.contains(hash) == true }
    }

    fun expiry(ctx: Context): Long =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong("expires_at", 0L)

    fun activePlan(ctx: Context): Plan? {
        val id = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("plan_id", null)
        return PLANS.firstOrNull { it.id == id }
    }

    fun isActive(ctx: Context): Boolean =
        System.currentTimeMillis() < expiry(ctx)

    fun daysLeft(ctx: Context): Long {
        val left = expiry(ctx) - System.currentTimeMillis()
        return if (left > 0) (left + 86_399_999L) / 86_400_000L else 0L
    }

    /** Activate (or renew): expiry extends from the later of now / current expiry. */
    fun activate(ctx: Context, plan: Plan) {
        val now = System.currentTimeMillis()
        val base = maxOf(now, expiry(ctx))
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("plan_id", plan.id)
            .putLong("activated_at", now)
            .putLong("expires_at", base + plan.days * 86_400_000L)
            .apply()
    }
}
