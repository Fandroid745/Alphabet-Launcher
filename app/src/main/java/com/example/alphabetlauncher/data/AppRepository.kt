package com.example.alphabetlauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository interface for fetching and launching installed applications.
 */
interface AppRepository {
    /**
     * Loads the list of all launchable installed applications on the device.
     */
    suspend fun getInstalledApps(): List<AppInfo>

    /**
     * Loads installed apps grouped by their initial uppercase letter ('A'..'Z').
     */
    suspend fun getAppsByLetter(): Map<Char, List<AppInfo>>

    /**
     * Attempts to launch an installed app by its package name.
     */
    fun launchApp(packageName: String): Boolean
}

class DefaultAppRepository(
    private val context: Context
) : AppRepository {

    private val packageManager: PackageManager = context.packageManager

    override suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
        val currentPackageName = context.packageName

        resolveInfos
            .asSequence()
            .mapNotNull { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                // Do not list our own launcher app
                if (packageName == currentPackageName) return@mapNotNull null

                val label = resolveInfo.loadLabel(packageManager).toString().trim()
                if (label.isEmpty()) return@mapNotNull null

                val icon = resolveInfo.loadIcon(packageManager)
                AppInfo(
                    label = label,
                    packageName = packageName,
                    icon = icon
                )
            }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    override suspend fun getAppsByLetter(): Map<Char, List<AppInfo>> {
        val apps = getInstalledApps()
        return apps.groupBy { it.firstLetter }
    }

    override fun launchApp(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (intent != null) {
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}