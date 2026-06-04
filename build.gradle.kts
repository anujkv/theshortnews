plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    idea
}

allprojects {
    // Workaround for IDE debugger script incompatibility with newer Gradle versions
    // see https://youtrack.jetbrains.com/issue/IDEA-324376
    val mapPath = object : groovy.lang.Closure<String>(null) {
        @Suppress("unused")
        fun doCall(vararg args: Any): String {
            return args[0].toString()
        }
    }
    (dependencies as org.gradle.api.plugins.ExtensionAware).extensions.add("mapPath", mapPath)
    (this as org.gradle.api.plugins.ExtensionAware).extensions.add("mapPath", mapPath)
}
