package com.develhack.gradle.plugins.ide.eclipse
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile

import com.esotericsoftware.minlog.Log.Logger;

class EclipsePlugin implements Plugin<Project> {

	private Project project;

	void apply(Project project) {

		this.project = project;

		project.afterEvaluate {

			project.configure(project) {

				if(!project.plugins.hasPlugin('eclipse')) {
					project.apply plugin: 'eclipse'
				}

				if(!project.plugins.hasPlugin('java')) {
					println "the project has no java plugin. do not configure."
					return;
				}

				project.eclipse {
					classpath {
						file {
							whenMerged { classpath ->
								classpath.entries.findAll {
									it.path.contains('org.eclipse.jdt.launching.JRE_CONTAINER')
								}.each { it.path = "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-${project.sourceCompatibility}" }
								classpath.entries.unique()
							}
						}
					}
					jdt {
						file {
							withProperties { properties ->

								def prefs = new Properties()
								prefs.load(this.getClass().getResourceAsStream('org.eclipse.jdt.core.prefs'))
								prefs.entrySet().each {
									properties.put(it.key, it.value)
								}

								properties.put('org.eclipse.jdt.core.compiler.codegen.targetPlatform', project.sourceCompatibility as String)
								properties.put('org.eclipse.jdt.core.compiler.compliance', project.sourceCompatibility as String)
								properties.put('org.eclipse.jdt.core.compiler.source', project.sourceCompatibility as String)

							}
						}
					}
				}

				project.tasks.eclipseProject.doLast {
					mergePreferences(project, 'org.eclipse.core.resources.prefs')
					mergePreferences(project, 'org.eclipse.core.runtime.prefs')
					mergePreferences(project, 'org.eclipse.jdt.ui.prefs')
				}

				project.tasks.withType(AbstractCompile) { task ->
					task.options.encoding = 'UTF-8'
				}
			}
		}
	}

	private void mergePreferences(Project project, String name) {
		File file = project.file('.settings/' + name )
		Properties properties = new Properties()
		if (file.exists()) {
			file.withInputStream { stream ->
				properties.load(stream)
			}
		}
		this.getClass().getResourceAsStream(name).withStream { stream ->
			properties.load(stream)
		}
		file.withOutputStream { stream ->
			properties.store(stream, null)
		}
	}
}
