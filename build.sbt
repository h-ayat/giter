scalaVersion := "3.3.6"

name := "giter"

libraryDependencies ++= Seq(
  "io.github.h-ayat" %%% "p752-tiles" % "0.4.4"
)


enablePlugins(ScalaNativePlugin)

nativeConfig ~= {
  _.withIncrementalCompilation(true)
}

scalacOptions ++= Seq("-explain")

