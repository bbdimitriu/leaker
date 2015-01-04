package org.leaker

import java.io.{IOException, InputStream}
import java.util.jar.{JarEntry, JarFile}

import org.apache.commons.io.IOUtils._
import org.slf4j.LoggerFactory

class LeakerClassLoader(parent: ClassLoader, priorityJarFile: String) extends ClassLoader(parent) {

  val log = LoggerFactory.getLogger(getClass)

  override def loadClass(name: String, resolve: Boolean): Class[_] = {
    getClassLoadingLock(name).synchronized {
      // can't mess with the java.* package
      if (name.startsWith("java.") || name.startsWith("javax.")) {
        return super.loadClass(name, resolve)
      } else {
        // is class already loaded?
        var classToLoad: Class[_] = findLoadedClass(name)
        if (classToLoad == null) {
          var in: InputStream = null
          try {
            val classFileName: String = name.replaceAll("\\.", "/") + ".class"
            in = findResourceInPriorityJar(classFileName, priorityJarFile)
            if (in == null) {
              return super.loadClass(name, resolve)
            }
            val cBytes: Array[Byte] = toByteArray(in)
            classToLoad = defineClass(name, cBytes, 0, cBytes.length)
          } catch {
            case e: IOException => {
              throw new ClassNotFoundException("Could not load " + name, e)
            }
          } finally {
            closeQuietly(in)
          }
        }
        if (resolve) {
          resolveClass(classToLoad)
        }
        classToLoad
      }
    }
  }

  private def findResourceInPriorityJar(classFileName: String, priorityJar: String): InputStream = {
    var stream: InputStream = null
    try {
      val jarFile: JarFile = new JarFile(priorityJar)
      val jarEntry: JarEntry = jarFile.getJarEntry(classFileName)
      if (jarEntry != null) {
        stream = jarFile.getInputStream(jarEntry)
      }
    } catch {
      case e: IOException => {
        log.error(s"Could not get an InputStream of the class '$classFileName' in priority JAR '$priorityJar'", e)
      }
    }
    stream
  }
}
