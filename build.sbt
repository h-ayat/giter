scalaVersion := "3.3.3"

name := "tim"

libraryDependencies ++= Seq(
  "io.github.h-ayat" %%% "p752-tiles" % "0.4.2",
  "org.scala-native" % "nativelib_native0.5_3" % "0.5.5"
)


enablePlugins(ScalaNativePlugin)

nativeConfig ~= {
  _.withIncrementalCompilation(true)
}

scalacOptions ++= Seq("-explain")

