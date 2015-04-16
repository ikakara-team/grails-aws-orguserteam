grails.project.work.dir = 'target'
grails.project.target.level = 1.7
grails.project.source.level = 1.7

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {

  inherits "global"
  log "warn"

  repositories {
    grailsCentral()
    mavenLocal()
    mavenCentral()
  }

  dependencies {
    compile ('com.amazonaws:aws-java-sdk:1.9.30') { // http://aws.amazon.com/releasenotes/Java?browse=1
      export = false
    }
  }

  plugins {
    // needed for testing
    build (":tomcat:8.0.21") {
      export = false
    }

    // needed for config management
    compile ':plugin-config:0.2.0'

    compile ':simple-marshaller:0.1.2'
    compile (':aws-instance:0.5.3') {
      export = false
    }

    build(":release:3.1.1", ":rest-client-builder:2.1.1") {
      export = false
    }
  }
}
