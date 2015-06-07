# gradle-eclipse-plugin
Gradle plugin for eclipse that contains the common settings of Develhack.com.

## About
This plugin is wrapper around the standard eclipse plugin of gralde.

If the target project has `java` plugin, this plugin applies standard eclipse plugin and the following standard settings of Develhack.com.

* Java cord formatter
* Encoding of resources (org.eclipse.core.resources.prefs)
* Line separator of resources (org.eclipse.core.runtime.prefs)
* Java compiler options (org.eclipse.jdt.core.prefs)
* Java editor options (org.eclipse.jdt.ui.prefs)

## License
* [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)