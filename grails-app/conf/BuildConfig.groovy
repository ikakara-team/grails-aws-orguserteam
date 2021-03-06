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
    compile ('com.amazonaws:aws-java-sdk:1.10.7') { // http://aws.amazon.com/releasenotes/Java?browse=1
      export = false // allow user to use another version
    }
  }

  plugins {
    // needed for testing
    build (":tomcat:8.0.22") {
      export = false
    }

    // needed for config management
    compile ':plugin-config:0.2.1'

    compile ':simple-marshaller:0.1.5'
    compile (':aws-instance:0.7.0') {
      export = false // allow user to use another version
    }

    build(":release:3.1.1", ":rest-client-builder:2.1.1") {
      export = false
    }
  }
}
