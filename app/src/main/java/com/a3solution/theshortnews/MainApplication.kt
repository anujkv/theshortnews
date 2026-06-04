package com.a3solution.theshortnews

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache

/**
 * Main Application class for The Short News.
 *
 * This class is responsible for global configurations. It implements [ImageLoaderFactory]
 * to provide a customized [ImageLoader] for Coil, featuring optimized memory and disk caching.
 */
class MainApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false)
            .apply {
                // BuildConfig is usually available after sync/build
                // logger(DebugLogger())
            }
            .build()
    }
}
