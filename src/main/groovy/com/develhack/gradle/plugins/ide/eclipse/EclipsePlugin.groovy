package com.develhack.gradle.plugins.ide.eclipse
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile

class EclipsePlugin implements Plugin<Project> {

	private Project project;

	void apply(Project project) {

		this.project = project;

		project.apply plugin: 'eclipse'

		project.eclipse {
			classpath {
				file {
					whenMerged { classpath ->
						classpath.entries.findAll { it.path.contains('org.eclipse.jdt.launching.JRE_CONTAINER') }.each { it.path = "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-${project.sourceCompatibility}" }
						classpath.entries.unique()
					}
				}
			}
			jdt {
				file {
					withProperties { properties ->

						def formatter = new XmlSlurper().parse(this.getClass().getResource('formatter.xml').toURI().toString())

						formatter.profile.setting.each {
							if ((it.@id as String).startsWith('org.eclipse.jdt.core.formatter.')) {
								properties.put(it.@id as String, it.@value as String)
							}
						}
					}
				}
			}
		}

		project.eclipseJdt {
			inputFile = new File(this.getClass().getResource('org.eclipse.jdt.core.prefs').file)
			transformer.addAction { properties ->
				properties.put('org.eclipse.jdt.core.compiler.codegen.targetPlatform', project.sourceCompatibility as String)
				properties.put('org.eclipse.jdt.core.compiler.compliance', project.sourceCompatibility as String)
				properties.put('org.eclipse.jdt.core.compiler.source', project.sourceCompatibility as String)
			}
		}

		project.tasks.eclipseProject.doLast {
			mergePreferences('org.eclipse.core.resources.prefs')
			mergePreferences('org.eclipse.core.runtime.prefs')
			mergePreferences('org.eclipse.jdt.ui.prefs')
		}

		project.tasks.withType(AbstractCompile) { task ->
			task.options.encoding = 'UTF-8'
		}
	}

	private void mergePreferences(String name) {
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
